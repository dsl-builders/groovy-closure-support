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

import java.util.function.BiFunction;

/**
 * {@link BiFunction} wrapper for closure.
 * @param <D> the delegate to be (the first argument)
 * @param <T> the parameter to be (the second argument)
 */
public class BiFunctionWithDelegate<D, T, R> implements BiFunction<D, T, R> {

    public static <D, T, R> BiFunction<D, T, R> create(Closure<R> c, Object owner, int strategy) {
        return new BiFunctionWithDelegate<>(c, strategy, owner);
    }

    public static <D, T, R> BiFunction<D, T, R> create(Closure<R> c, Object owner) {
        return create(c, owner, Closure.DELEGATE_FIRST);
    }

    public static <D, T, R> BiFunction<D, T, R> create(Closure<R> c, int strategy) {
        return create(c, GroovyClosure.getPropagatedOwner(c.getOwner()), strategy);
    }

    public static <D, T, R> BiFunction<D, T, R> create(Closure<R> c) {
        return create(c, Closure.DELEGATE_FIRST);
    }

    private final int strategy;
    private final Object owner;
    private final Closure<R> closure;

    private BiFunctionWithDelegate(Closure<R> closure, int strategy, Object owner) {
        this.strategy = strategy;
        this.owner = owner;
        this.closure = closure;
    }

    @Override
    public R apply(D d, T t) {
        Closure<R> closure = this.closure.rehydrate(d, owner, this.closure.getThisObject());
        closure.setResolveStrategy(strategy);
        return closure.call(t);
    }
}
