package space.jasan.support.groovy.closure

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Specification for GroovyClosure helper.
 */
@Unroll class GroovyClosureSpec extends Specification {

    void "test delegate #method"() {
        Object token = new Object()
        Closure tester = { }

        when:
        "$method"(tester, token)

        then:
        tester.resolveStrategy == Closure.DELEGATE_FIRST
        tester.delegate == token

        where:
        method << testMethods
    }

    static void setDelegateInterfaceTest(Runnable consumer, Object token) {
        GroovyClosure.setDelegate(consumer, token)
    }

    static void setDelegateSAMTest(SAM consumer, Object token) {
        GroovyClosure.setDelegate(consumer, token)
    }

    static void setClosureTest(Closure consumer, Object token) {
        GroovyClosure.setDelegate(consumer, token)
    }

    private static List<String> getTestMethods() {
        List<String> ret = ['setDelegateInterfaceTest', 'setClosureTest']
        if (!(GroovySystem.version ==~ /2\.[01]\..*/)) {
            ret << 'setDelegateSAMTest'
        }
        ret
    }

}

abstract class SAM {
    abstract void testIt()
}
