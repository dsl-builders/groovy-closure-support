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
