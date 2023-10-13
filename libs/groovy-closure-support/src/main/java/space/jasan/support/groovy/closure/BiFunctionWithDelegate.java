package space.jasan.support.groovy.closure;

import groovy.lang.Closure;

import java.util.function.BiFunction;

/**
 * {@link BiFunction} wrapper for closure.
 * @param <D> the delegate to be (the first argument)
 * @param <T> the parameter to be (the second argument)
 */
public class BiFunctionWithDelegate<D, T, R> implements BiFunction<D, T, R> {

    public static <D, T, R> BiFunction<D, T, R> create(Closure<R> c, Object owner, int strategy) {
        return new BiFunctionWithDelegate<>(c, strategy, owner);
    }

    public static <D, T, R> BiFunction<D, T, R> create(Closure<R> c, Object owner) {
        return create(c, owner, Closure.DELEGATE_FIRST);
    }

    public static <D, T, R> BiFunction<D, T, R> create(Closure<R> c, int strategy) {
        return create(c, GroovyClosure.getPropagatedOwner(c.getOwner()), strategy);
    }

    public static <D, T, R> BiFunction<D, T, R> create(Closure<R> c) {
        return create(c, Closure.DELEGATE_FIRST);
    }

    private final int strategy;
    private final Object owner;
    private final Closure<R> closure;

    private BiFunctionWithDelegate(Closure<R> closure, int strategy, Object owner) {
        this.strategy = strategy;
        this.owner = owner;
        this.closure = closure;
    }

    @Override
    public R apply(D d, T t) {
        Closure<R> closure = this.closure.rehydrate(d, owner, this.closure.getThisObject());
        closure.setResolveStrategy(strategy);
        return closure.call(t);
    }
}
