package space.jasan.support.groovy.closure;

import groovy.lang.Closure;

import java.util.function.Function;

public class GroovyClosureFunction<T, R> implements Function<T, R> {

    public static <T, R> Function<T, R> create(Closure<R> c, int strategy, Object owner) {
        return new GroovyClosureFunction<>(c, strategy, owner);
    }

    public static <T, R> Function<T, R> create(Closure<R> c, int strategy) {
        return create(c, strategy, c.getOwner());
    }

    public static <T, R> Function<T, R> create(Closure<R> c) {
        return create(c, Closure.DELEGATE_FIRST);
    }

    private final int strategy;
    private final Object owner;
    private final Closure<R> closure;

    private GroovyClosureFunction(Closure<R> closure, int strategy, Object owner) {
        this.strategy = strategy;
        this.owner = owner;
        this.closure = closure;
    }

    @Override
    public R apply(T t) {
        Closure<R> closure = this.closure.rehydrate(t, owner, this.closure.getThisObject());
        closure.setResolveStrategy(strategy);
        return closure.call(t);
    }
}
