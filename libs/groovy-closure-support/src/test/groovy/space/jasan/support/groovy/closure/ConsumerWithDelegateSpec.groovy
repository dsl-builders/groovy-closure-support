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
import java.util.function.Consumer

/**
 * Tests for ConsumerWithDelegate
 */
@CompileDynamic
@SuppressWarnings(['Indentation'])
class ConsumerWithDelegateSpec extends Specification {

    private static final String SOMETHING = 'smtg'

    void 'use as consumer'() {
        given:
            Consumer<ConsumerFoo> consumer = ConsumerWithDelegate.create {
                foo = 'FOO'
            }
        expect:
            AcceptsConsumer.testMe(consumer).foo == 'FOO'
    }

    void 'owner is set from propagator'() {
        when:
            Object o = null
            ConsumerWithDelegate.create {
                ConsumerWithDelegate.create {
                    o = foo
                }.accept(it)
            }.accept(new ConsumerFoo())
        then:
            o == 'foo'
    }

    void 'owner is set'() {
        when:
            AtomicReference<String> reference = new AtomicReference<>()
            ConsumerWithDelegate.create({
                reference.set(foo)
            }, new FunctionFoo()).accept(SOMETHING)
        then:
            reference.get() == 'foo'
    }

}

@CompileStatic
class ConsumerFoo {

    String foo = 'foo'
    String bar = 'bar'

}

@CompileStatic
class AcceptsConsumer {

    static ConsumerFoo testMe(Consumer<ConsumerFoo> consumer) {
        ConsumerFoo foo = new ConsumerFoo()
        consumer.accept(foo)
        return foo
    }

}
