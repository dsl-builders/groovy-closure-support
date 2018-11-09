package space.jasan.support.groovy.closure;

import groovy.lang.Closure;

public interface OwnerPropagator {

    static Object getPropagatedOwner(Object object) {
        if (object instanceof OwnerPropagator) {
            return ((OwnerPropagator) object).getOwner();
        }
        if (object instanceof Closure) {
            return ((Closure) object).getOwner();
        }
        return object;
    }

    Object getOwner();
}
