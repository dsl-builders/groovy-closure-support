package space.jasan.support.groovy.closure

import space.jasan.support.groovy.closure.FunctionWithDelegate
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicReference
import java.util.function.Function

/**
 * Tests for FunctionWithDelegate
 */
@CompileDynamic
@SuppressWarnings(['Indentation', 'SpaceAroundMapEntryColon'])
class FunctionWithDelegateSpec extends Specification {

    private static final String SOMETHING = 'smtg'

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
            FunctionWithDelegate.create {
                FunctionWithDelegate.create {
                    o = foo
                }.apply(it)
            }.apply(new FunctionFoo())
        then:
            o == 'foo'
    }

    void 'owner is set'() {
        when:
            AtomicReference<String> reference = new AtomicReference<>()
            FunctionWithDelegate.create({
                reference.set(foo)
            }, new FunctionFoo()).apply(SOMETHING)
        then:
            reference.get() == 'foo'
    }

}

@CompileStatic
class FunctionFoo {

    String foo = 'foo'
    String bar = 'bar'

}

@CompileStatic
class AcceptsFunction {

    static FunctionFoo testMe(Function<FunctionFoo, FunctionFoo> function) {
        FunctionFoo foo = new FunctionFoo()
        return function.apply(foo)
    }

}
