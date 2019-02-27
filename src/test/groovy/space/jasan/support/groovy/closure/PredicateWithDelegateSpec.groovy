package space.jasan.support.groovy.closure

import spock.lang.Specification

import java.util.concurrent.atomic.AtomicReference
import java.util.function.Predicate

/**
 * Tests for PredicateWithDelegate
 */
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

class PredicateFoo {
    String foo = 'foo'
    String bar = 'bar'
}

class AcceptsPredicate {

    static boolean testMe(Predicate<PredicateFoo> predicate) {
        PredicateFoo foo = new PredicateFoo()
        predicate.test(foo)
    }

}
