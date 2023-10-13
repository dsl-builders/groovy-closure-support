package space.jasan.support.groovy.closure

import dsl.builders.support.groovy.closure.ConsumerWithDelegate
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
