package space.jasan.support.groovy.closure

import spock.lang.Specification

import java.util.function.Consumer

class GroovyClosureConsumerSpec extends Specification {

    void 'use as consumer'() {
        expect:
            AcceptsConsumer.testMe(GroovyClosureConsumer.create { foo = 'FOO' }).foo == 'FOO'
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
        return foo
    }

}
