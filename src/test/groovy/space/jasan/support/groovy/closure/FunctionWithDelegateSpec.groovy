package space.jasan.support.groovy.closure

import spock.lang.Specification

import java.util.function.Function

/**
 * Tests for FunctionWithDelegate
 */
@SuppressWarnings(['Indentation', 'SpaceAroundMapEntryColon'])
class FunctionWithDelegateSpec extends Specification {

    void 'use as function'() {
        given:
            Function<FunctionFoo, FunctionFoo> closureFunction = FunctionWithDelegate.create {
                new FunctionFoo(foo: bar, bar: foo)
            }
        expect:
            AcceptsFunction.testMe(closureFunction).foo == 'bar'
    }

    void 'owner is set from propagator'() {
        when:
            Object o = null
            FunctionWithDelegate.create({
                o = foo
            }, new FunctionWithDifferentOwner()).apply 'foobar'
        then:
            o == 'foo'
    }

}

class FunctionFoo {
    String foo = 'foo'
    String bar = 'bar'
}

class FunctionWithDifferentOwner implements OwnerPropagator {
    final Object owner = new FunctionFoo()
}

class AcceptsFunction {

    static FunctionFoo testMe(Function<FunctionFoo, FunctionFoo> function) {
        FunctionFoo foo = new FunctionFoo()
        function.apply(foo)
    }

}
