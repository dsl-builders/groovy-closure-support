package space.jasan.support.groovy.closure

import spock.lang.Specification

import java.util.function.Consumer

/**
 * Tests for ConsumerWithDelegate
 */
@SuppressWarnings(['Indentation'])
class ConsumerWithDelegateSpec extends Specification {

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
            ConsumerWithDelegate.create({
                o = foo
            }, new ConsumerWithDifferentOwner()).accept 'foobar'
        then:
            o == 'foo'
    }

}

class ConsumerFoo {
    String foo = 'foo'
    String bar = 'bar'
}

class ConsumerWithDifferentOwner implements OwnerPropagator {
    final Object owner = new ConsumerFoo()
}

class AcceptsConsumer {

    static ConsumerFoo testMe(Consumer<ConsumerFoo> consumer) {
        ConsumerFoo foo = new ConsumerFoo()
        consumer.accept(foo)
        foo
    }

}
