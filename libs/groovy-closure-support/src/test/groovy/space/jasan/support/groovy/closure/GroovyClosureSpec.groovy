package space.jasan.support.groovy.closure

import space.jasan.support.groovy.closure.GroovyClosure
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Specification for GroovyClosure helper.
 */
@Unroll
@CompileDynamic
@SuppressWarnings('StaticMethodsBeforeInstanceMethods')
class GroovyClosureSpec extends Specification {

    void "check delegate #method"() {
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

    @SuppressWarnings(['Indentation', 'Instanceof'])
    void 'clone with top level owner - manual delegate'() {
        when:
            Level1 level1 = new Level1()
            level1.level2ManualDelegate {
                level3 { 'hello' }
            }
        then:
            level1.level2.closure.owner instanceof GroovyClosureSpec
    }

    @SuppressWarnings(['Indentation', 'Instanceof'])
    void 'clone with top level owner - method with delegate'() {
        when:
            Level1 level1 = new Level1()
            level1.level2MehodWithDelegate {
                level3 { 'hello' }
            }
        then:
            level1.level2.closure.owner instanceof GroovyClosureSpec
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
        return ret
    }

}

@CompileStatic
abstract class SAM {

    abstract void testIt()

}

@CompileStatic
class Level1 {

    Level2 level2 = new Level2()

    void level2ManualDelegate(Closure closure) {
        Closure c = GroovyClosure.cloneWithTopLevelOwner closure
        c.delegate = level2
        c.call(level2)
    }

    void level2MehodWithDelegate(Closure closure) {
        Closure c = GroovyClosure.cloneWithTopLevelOwner closure, level2
        c.call(level2)
    }

}

@CompileStatic
class Level2 {

    Closure closure

    void level3(Closure closure) {
        this.closure = GroovyClosure.cloneWithTopLevelOwner closure
    }

}
