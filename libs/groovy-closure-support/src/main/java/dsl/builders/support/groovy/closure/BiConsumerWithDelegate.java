package dsl.builders.support.groovy.closure;

import groovy.lang.Closure;

import java.util.function.BiConsumer;

/**
 * {@link BiConsumer} wrapper for closure.
 * @param <D> the delegate to be (the first argument)
 * @param <T> the parameter to be (the second argument)
 */
public class BiConsumerWithDelegate<D, T> implements BiConsumer<D, T> {

    public static <D, T> BiConsumer<D, T> create(Closure c, Object owner, int strategy) {
        return new BiConsumerWithDelegate<>(c, strategy, owner);
    }

    public static <D, T> BiConsumer<D, T> create(Closure c, Object owner) {
        return create(c, owner, Closure.DELEGATE_FIRST);
    }

    public static <D, T> BiConsumer<D, T> create(Closure c, int strategy) {
        return create(c, GroovyClosure.getPropagatedOwner(c.getOwner()), strategy);
    }

    public static <D, T> BiConsumer<D, T> create(Closure c) {
        return create(c, Closure.DELEGATE_FIRST);
    }

    private final int strategy;
    private final Object owner;
    private final Closure closure;

    private BiConsumerWithDelegate(Closure closure, int strategy, Object owner) {
        this.strategy = strategy;
        this.owner = owner;
        this.closure = closure;
    }

    @Override
    public void accept(D d, T t) {
        Closure closure = this.closure.rehydrate(d, owner, this.closure.getThisObject());
        closure.setResolveStrategy(strategy);
        closure.call(t);
    }
}
