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
package space.jasan.support.groovy.closure

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicReference
import java.util.function.Function

/**
 * Tests for FunctionWithDelegate
 */
@CompileDynamic
@SuppressWarnings(['Indentation', 'SpaceAroundMapEntryColon'])
class FunctionWithDelegateSpec extends Specification {

    private static final String SOMETHING = 'smtg'

    void 'use as function'() {
        given:
            Function<FunctionFoo, FunctionFoo> closureFunction = FunctionWithDelegate.create {
                new FunctionFoo(foo: bar, bar: foo)
            }
        expect:
            AcceptsFunction.testMe(closureFunction).foo == 'bar'
    }

    void 'owner is set from propagator'() {
        when:
            Object o = null
            FunctionWithDelegate.create {
                FunctionWithDelegate.create {
                    o = foo
                }.apply(it)
            }.apply(new FunctionFoo())
        then:
            o == 'foo'
    }

    void 'owner is set'() {
        when:
            AtomicReference<String> reference = new AtomicReference<>()
            FunctionWithDelegate.create({
                reference.set(foo)
            }, new FunctionFoo()).apply(SOMETHING)
        then:
            reference.get() == 'foo'
    }

}

@CompileStatic
class FunctionFoo {

    String foo = 'foo'
    String bar = 'bar'

}

@CompileStatic
class AcceptsFunction {

    static FunctionFoo testMe(Function<FunctionFoo, FunctionFoo> function) {
        FunctionFoo foo = new FunctionFoo()
        return function.apply(foo)
    }

}
