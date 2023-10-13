package space.jasan.support.groovy.closure

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicReference
import java.util.function.BiFunction

/**
 * Tests for ConsumerWithDelegate
 */
@CompileDynamic
@SuppressWarnings(['Indentation'])
class BiFunctionWithDelegateSpec extends Specification {

    private static final String SOMETHING = 'smtg'

    void 'use as consumer'() {
        given:
            BiFunction<BiFunctionFoo, BiFunctionFoo, BiFunctionFoo> consumer = BiFunctionWithDelegate.create {
                foo = 'FOO'
                it
            }
        expect:
            AcceptsBiFunction.testMe(consumer).foo == 'FOO'
    }

    void 'owner is set from propagator'() {
        when:
            Object o = null
            BiFunctionWithDelegate.create {
                BiFunctionWithDelegate.create {
                    o = foo
                    it
                }.apply(it, it)
            }.apply(new BiFunctionFoo(), new BiFunctionFoo())
        then:
            o == 'foo'
    }

    void 'owner is set'() {
        when:
            AtomicReference<String> reference = new AtomicReference<>()
            BiFunctionWithDelegate.create({
                reference.set(foo)
                it
            }, new BiFunctionFoo()).apply(SOMETHING, SOMETHING)
        then:
            reference.get() == 'foo'
    }

}

@CompileStatic
class BiFunctionFoo {

    String foo = 'foo'
    String bar = 'bar'

}

@CompileStatic
class AcceptsBiFunction {

    static BiFunctionFoo testMe(BiFunction<BiFunctionFoo, BiFunctionFoo, BiFunctionFoo> consumer) {
        BiFunctionFoo foo = new BiFunctionFoo()
        consumer.apply(foo, foo)
        return foo
    }

}
