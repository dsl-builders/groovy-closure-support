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

}

class ConsumerFoo {
    String foo = 'foo'
    String bar = 'bar'
}

class AcceptsConsumer {

    static ConsumerFoo testMe(Consumer<ConsumerFoo> consumer) {
        ConsumerFoo foo = new ConsumerFoo()
        consumer.accept(foo)
        foo
    }

}
