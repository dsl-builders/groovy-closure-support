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
import java.util.function.Predicate

/**
 * Tests for PredicateWithDelegate
 */
@CompileDynamic
@SuppressWarnings(['Indentation', 'SpaceAroundMapEntryColon'])
class PredicateWithDelegateSpec extends Specification {

    void 'use as predicate'() {
        given:
            Predicate<PredicateFoo> closurePredicate = PredicateWithDelegate.create {
                it.foo == 'foo'
            }
        expect:
            AcceptsPredicate.testMe(closurePredicate)
    }

    void 'owner is set from propagator'() {
        when:
            Object o = null
            PredicateWithDelegate.create {
                PredicateWithDelegate.create {
                    o = foo
                    'non empty string is true'
                } test(it)
            } test(new FunctionFoo())
        then:
            o == 'foo'
    }

    void 'owner is set'() {
        when:
            AtomicReference<String> reference = new AtomicReference<>()
            PredicateWithDelegate.create({
                reference.set(new String(bytes))
            }, 'owner').test(new PredicateFoo(foo: 'bar', bar: 'foo'))
        then:
            reference.get() == 'owner'
    }

}

@CompileStatic
class PredicateFoo {

    String foo = 'foo'
    String bar = 'bar'

}

@CompileStatic
class AcceptsPredicate {

    static boolean testMe(Predicate<PredicateFoo> predicate) {
        PredicateFoo foo = new PredicateFoo()
        return predicate.test(foo)
    }

}
