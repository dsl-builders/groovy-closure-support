package space.jasan.support.groovy.closure;

public interface OwnerPropagator {

    static Object getPropagatedOwner(Object object) {
        if (object instanceof OwnerPropagator) {
            return ((OwnerPropagator) object).getOwner();
        }
        return object;
    }

    Object getOwner();
}
