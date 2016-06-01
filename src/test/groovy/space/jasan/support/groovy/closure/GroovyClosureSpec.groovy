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
        method << ['setDelegateInterfaceTest' /* , 'setDelegateSAMTest' */]

    }

    static setDelegateInterfaceTest(Runnable consumer, Object token) {
        GroovyClosure.setDelegate(consumer, token)
    }

    static setDelegateSAMTest(SAM consumer, Object token) {
        GroovyClosure.setDelegate(consumer, token)
    }

}

abstract class SAM {
    abstract void testIt()
}
