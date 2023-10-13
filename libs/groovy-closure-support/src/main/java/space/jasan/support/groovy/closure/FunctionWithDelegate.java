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

import java.util.function.Function;

public class FunctionWithDelegate<T, R> implements Function<T, R> {

    public static <T, R> Function<T, R> create(Closure<R> c, Object owner, int strategy) {
        return new FunctionWithDelegate<>(c, strategy, owner);
    }

    public static <T, R> Function<T, R> create(Closure<R> c, Object owner) {
        return create(c, owner, Closure.DELEGATE_FIRST);
    }

    public static <T, R> Function<T, R> create(Closure<R> c, int strategy) {
        return create(c, GroovyClosure.getPropagatedOwner(c.getOwner()), strategy);
    }

    public static <T, R> Function<T, R> create(Closure<R> c) {
        return create(c, Closure.DELEGATE_FIRST);
    }

    private final int strategy;
    private final Object owner;
    private final Closure<R> closure;

    private FunctionWithDelegate(Closure<R> closure, int strategy, Object owner) {
        this.strategy = strategy;
        this.owner = owner;
        this.closure = closure;
    }

    @Override
    public R apply(T t) {
        Closure<R> closure = this.closure.rehydrate(t, owner, this.closure.getThisObject());
        closure.setResolveStrategy(strategy);
        return closure.call(t);
    }
}
