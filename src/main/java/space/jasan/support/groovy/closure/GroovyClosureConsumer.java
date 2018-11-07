package space.jasan.support.groovy.closure;

import groovy.lang.Closure;

import java.util.function.Consumer;

public class GroovyClosureConsumer<T> implements Consumer<T> {

    public static <T> Consumer<T> create(Closure c, int strategy, Object owner) {
        return new GroovyClosureConsumer<>(c, strategy, owner);
    }

    public static <T> Consumer<T> create(Closure c, int strategy) {
        return create(c, strategy, c.getOwner());
    }

    public static <T> Consumer<T> create(Closure c) {
        return create(c, Closure.DELEGATE_FIRST);
    }

    private final int strategy;
    private final Object owner;
    private final Closure closure;

    private GroovyClosureConsumer(Closure closure, int strategy, Object owner) {
        this.strategy = strategy;
        this.owner = owner;
        this.closure = closure;
    }

    @Override
    public void accept(T t) {
        Closure closure = this.closure.rehydrate(t, owner, this.closure.getThisObject());
        closure.setResolveStrategy(strategy);
        closure.call(t);
    }
}
