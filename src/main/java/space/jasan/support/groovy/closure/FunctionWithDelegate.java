package space.jasan.support.groovy.closure;

import groovy.lang.Closure;

import java.util.function.Function;

public class FunctionWithDelegate<T, R> implements Function<T, R> {

    public static <T, R> Function<T, R> create(Closure<R> c, Object owner, int strategy) {
        return new FunctionWithDelegate<>(c, strategy, owner);
    }

    public static <T, R> Function<T, R> create(Closure<R> c, Object owner) {
        return create(c, owner, Closure.DELEGATE_FIRST);
    }

    public static <T, R> Function<T, R> create(Closure<R> c, int strategy) {
        return create(c, c.getOwner(), strategy);
    }

    public static <T, R> Function<T, R> create(Closure<R> c) {
        return create(c, Closure.DELEGATE_FIRST);
    }

    private final int strategy;
    private final Object owner;
    private final Closure<R> closure;

    private FunctionWithDelegate(Closure<R> closure, int strategy, Object owner) {
        this.strategy = strategy;
        this.owner = OwnerPropagator.getPropagatedOwner(owner);
        this.closure = closure;
    }

    @Override
    public R apply(T t) {
        Closure<R> closure = this.closure.rehydrate(t, owner, this.closure.getThisObject());
        closure.setResolveStrategy(strategy);
        return closure.call(t);
    }
}
