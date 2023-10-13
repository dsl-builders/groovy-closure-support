package dsl.builders.support.groovy.closure;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.function.Predicate;

public class PredicateWithDelegate<T> implements Predicate<T> {

    public static <T> Predicate<T> create(Closure<?> c, Object owner, int strategy) {
        return new PredicateWithDelegate<>(c, strategy, owner);
    }

    public static <T> Predicate<T> create(Closure<?> c, Object owner) {
        return create(c, owner, Closure.DELEGATE_FIRST);
    }

    public static <T> Predicate<T> create(Closure<?> c, int strategy) {
        return create(c, GroovyClosure.getPropagatedOwner(c.getOwner()), strategy);
    }

    public static <T> Predicate<T> create(Closure<?> c) {
        return create(c, Closure.DELEGATE_FIRST);
    }

    private final int strategy;
    private final Object owner;
    private final Closure<?> closure;

    private PredicateWithDelegate(Closure<?> closure, int strategy, Object owner) {
        this.strategy = strategy;
        this.owner = owner;
        this.closure = closure;
    }

    @Override
    public boolean test(T t) {
        Closure<?> closure = this.closure.rehydrate(t, owner, this.closure.getThisObject());
        closure.setResolveStrategy(strategy);
        Object result = closure.call(t);
        if (result == null) {
            return false;
        }
        return DefaultGroovyMethods.asType(result, Boolean.class);
    }
}
