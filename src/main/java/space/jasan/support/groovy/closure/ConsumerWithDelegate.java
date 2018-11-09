package space.jasan.support.groovy.closure;

import groovy.lang.Closure;

import java.util.function.Consumer;

public class ConsumerWithDelegate<T> implements Consumer<T> {

    public static <T> Consumer<T> create(Closure c, Object owner, int strategy) {
        return new ConsumerWithDelegate<>(c, strategy, owner);
    }

    public static <T> Consumer<T> create(Closure c, Object owner) {
        return create(c, owner, Closure.DELEGATE_FIRST);
    }

    public static <T> Consumer<T> create(Closure c, int strategy) {
        return create(c, c.getOwner(), strategy);
    }

    public static <T> Consumer<T> create(Closure c) {
        return create(c, Closure.DELEGATE_FIRST);
    }

    private final int strategy;
    private final Object owner;
    private final Closure closure;

    private ConsumerWithDelegate(Closure closure, int strategy, Object owner) {
        this.strategy = strategy;
        this.owner = OwnerPropagator.getPropagatedOwner(owner);
        this.closure = closure;
    }

    @Override
    public void accept(T t) {
        Closure closure = this.closure.rehydrate(t, owner, this.closure.getThisObject());
        closure.setResolveStrategy(strategy);
        closure.call(t);
    }
}
