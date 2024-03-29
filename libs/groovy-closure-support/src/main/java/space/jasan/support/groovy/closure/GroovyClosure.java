/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2024 Vladimir Orany.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package space.jasan.support.groovy.closure;

import groovy.lang.Closure;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Makes Java API Groovy Closure friendly.
 */
public class GroovyClosure {

    private static final int DELEGATE_FIRST = 1;
    private static final String CLOSURE_CLASS_NAME = "groovy.lang.Closure";
    private static final Class<?> CLOSURE_CLASS;
    private static final String CONVERSION_HANDLER_CLASS_NAME = "org.codehaus.groovy.runtime.ConversionHandler";
    private static final Class<?> CONVERSION_HANDLER_CLASS;
    private static final String SET_DELEGATE_NAME = "setDelegate";
    private static final Method SET_DELEGATE_METHOD;
    private static final String SET_RESOLVE_STRATEGY_NAME = "setResolveStrategy";
    private static final Method SET_RESOLVE_STRATEGY_METHOD;
    private static final String GET_DELEGATE_NAME = "getDelegate";
    private static final Method GET_DELEGATE_METHOD;
    private static final String SAM_PROXY_SUFFIX = "_groovyProxy";
    private static final String SAM_PROXY_MAP_FIELD = "$closures$delegate$map";

    static {
        CLOSURE_CLASS = getClassIfAvailable(CLOSURE_CLASS_NAME);
        CONVERSION_HANDLER_CLASS = getClassIfAvailable(CONVERSION_HANDLER_CLASS_NAME);

        Method setDelegateMethod = null;
        Method setResolveStrategyMethod = null;

        if (CLOSURE_CLASS != null) {
            try {
                setDelegateMethod = CLOSURE_CLASS.getMethod(SET_DELEGATE_NAME, Object.class);
                setResolveStrategyMethod = CLOSURE_CLASS.getMethod(SET_RESOLVE_STRATEGY_NAME, int.class);
            } catch (NoSuchMethodException nsme) {
                throw new IllegalStateException("Closure class does not contain expected methods", nsme);
            }
        }
        SET_DELEGATE_METHOD = setDelegateMethod;
        SET_RESOLVE_STRATEGY_METHOD = setResolveStrategyMethod;

        Method getDelegateMethod = null;

        if (CONVERSION_HANDLER_CLASS != null) {
            try {
                getDelegateMethod = CONVERSION_HANDLER_CLASS.getMethod(GET_DELEGATE_NAME);
            } catch (NoSuchMethodException nsme) {
                throw new IllegalStateException("Converted closure class does not contain expected methods", nsme);
            }
        }
        GET_DELEGATE_METHOD = getDelegateMethod;
    }

    private static Class<?> getClassIfAvailable(String name) {
        Class<?> closureClass;
        try {
            closureClass = Class.forName(name, false, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException ignored) {
            closureClass = null;
        }
        return closureClass;
    }

    /**
     * Attempts to set delegate of object which might be a Closure.
     * <p>
     * This usually happens when the object is functional interface or single abstract method class. Only the first
     * use case is supported at the moment.
     * <p>
     * The possibility of delegate being set should be documented with @DelegatesTo annotation (use provided scope to
     * groovy dependency to retain the annotation to Groovy enabled projects).
     *
     * @param potentialClosure object which might be backed by Groovy closure
     * @param delegateWanted   delegate to be set if the object is backed by Groovy closure
     * @param <T>              the type of the object which might be backed by Groovy closure
     * @return always the original potentialClosure parameter which might have the delegate set if possible
     */
    public static <T> T setDelegate(T potentialClosure, Object delegateWanted) {
        if (potentialClosure == null || CLOSURE_CLASS == null || SET_RESOLVE_STRATEGY_METHOD == null || SET_DELEGATE_METHOD == null) {
            return potentialClosure;
        }

        if (Proxy.isProxyClass(potentialClosure.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(potentialClosure);
            if (CONVERSION_HANDLER_CLASS.isInstance(handler)) {
                try {
                    Object shouldBeClosure = GET_DELEGATE_METHOD.invoke(handler);
                    setDelegate(shouldBeClosure, delegateWanted);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get closure delegate from " + handler, e);
                }
            }
        } else if (CLOSURE_CLASS.isInstance(potentialClosure)) {
            try {
                SET_RESOLVE_STRATEGY_METHOD.invoke(potentialClosure, DELEGATE_FIRST);
                SET_DELEGATE_METHOD.invoke(potentialClosure, delegateWanted);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set delegate " + delegateWanted + " to " + potentialClosure, e);
            }
        } else if (potentialClosure.getClass().getSimpleName().endsWith(SAM_PROXY_SUFFIX)) {
            try {
                Class<?> proxyClass = potentialClosure.getClass();
                Field field = proxyClass.getDeclaredField(SAM_PROXY_MAP_FIELD);
                field.setAccessible(true);
                Object delegateMapValue = field.get(potentialClosure);
                if (!(delegateMapValue instanceof Map)) {
                    throw new IllegalStateException("Map field is not a map: " + delegateMapValue);
                }
                Map delegateMap = (Map) delegateMapValue;
                if (delegateMap.size() != 1) {
                    throw new IllegalStateException("Map field contains unexpected number of items: " + delegateMap.size());
                }
                setDelegate(delegateMap.values().iterator().next(), delegateWanted);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set closure delegate to " + potentialClosure, e);
            }
        }

        return potentialClosure;
    }

    /**
     * Returns closure's owner if the object is closure or the object self otherwise
     * @param object maybe a closure
     * @return closure's owner if the object is closure or the object self otherwise
     */
    public static Object getPropagatedOwner(Object object) {
        if (object instanceof Closure) {
            return ((Closure) object).getOwner();
        }
        return object;
    }

    public static <T> Closure<T> cloneWithTopLevelOwner(Closure<T> closure) {
        return cloneWithTopLevelOwner(closure, closure.getDelegate());
    }

    public static <T> Closure<T> cloneWithTopLevelOwner(Closure<T> closure, Object delegate) {
        return cloneWithTopLevelOwner(closure, delegate, Closure.DELEGATE_FIRST);
    }

    public static <T> Closure<T> cloneWithTopLevelOwner(Closure<T> closure, Object delegate, int strategy) {
        Closure<T> clone = closure.rehydrate(delegate, getPropagatedOwner(closure.getOwner()), closure.getThisObject());
        clone.setResolveStrategy(strategy);
        return clone;
    }

}
