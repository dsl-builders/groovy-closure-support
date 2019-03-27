package space.jasan.support.groovy.closure

import spock.lang.Specification

import java.util.concurrent.atomic.AtomicReference
import java.util.function.BiConsumer

/**
 * Tests for ConsumerWithDelegate
 */
@SuppressWarnings(['Indentation'])
class BiConsumerWithDelegateSpec extends Specification {

    private static final String SOMETHING = 'smtg'

    void 'use as consumer'() {
        given:
            BiConsumer<BiConsumerFoo, BiConsumerFoo> consumer = BiConsumerWithDelegate.create {
                foo = 'FOO'
            }
        expect:
            AcceptsBiConsumer.testMe(consumer).foo == 'FOO'
    }

    void 'owner is set from propagator'() {
        when:
            Object o = null
            BiConsumerWithDelegate.create {
                BiConsumerWithDelegate.create {
                    o = foo
                }.accept(it, it)
            }.accept(new BiConsumerFoo(), new BiConsumerFoo(), )
        then:
            o == 'foo'
    }

    void 'owner is set'() {
        when:
            AtomicReference<String> reference = new AtomicReference<>()
            BiConsumerWithDelegate.create({
                reference.set(foo)
            }, new FunctionFoo()).accept(SOMETHING, SOMETHING)
        then:
            reference.get() == 'foo'
    }

}

class BiConsumerFoo {
    String foo = 'foo'
    String bar = 'bar'
}

class AcceptsBiConsumer {

    static BiConsumerFoo testMe(BiConsumer<BiConsumerFoo, BiConsumerFoo> consumer) {
        BiConsumerFoo foo = new BiConsumerFoo()
        consumer.accept(foo, foo)
        foo
    }

}
