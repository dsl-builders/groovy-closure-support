# Groovy Closure Support for Java

[![Build Status](https://travis-ci.org/jasanspace/groovy-closure-support.svg?branch=master)](https://travis-ci.org/jasanspace/groovy-closure-support)

Groovy Closure Support for Java enables using closure delegation shortcuts on coerced closures.

Support setting delegate of closures coerced to single abstract method interfaces (Groovy 2.0 and later) as well
as for closures coerced to single abstract method abstract classes (Groovy 2.2 and later only).

Imagine you have simple builder class which you want to use in Groovy-builder style to create nested
structures.

```java
class PointBuilder {
    PointBuilder x(int x);
    PointBuilder y(int y);
}
```

```java
import java.util.function.Consumer;

class PointSource {
    Point add(Consumer<PointBuilder> pointConsumer) {
        PointBuilder point = new MyPointBuilder();
        // sets the delegate if Groovy Closure wrapper
        GroovyClosure.setDelegate(pointConsumer, point);
        pointConsumer.accept(point);
    }
}
```

In Java code you can get as close as to use for example `java.util.function.Consumer` to
which you pass the builder for the inner element and than call the methods on the builder object.

```java
class JavaUsage {
    void addPointToSource(PointSource source) {
        source.add(point -> {
            point.x(0);
            point.y(100);
        });
        source.add(point -> point.x(2).y(3));
    }
}
```

On the other hand when Groovy is on the classpath and instead of Java object a Groovy closure is passed
to the method then `GroovyClosure.setDelegate(pointConsumer, point)` will recognize it and sets
the closure delegate to object passed as second argument and also sets the resolve strategy to `DELEGATE_FIRST`
so you don't have to prefix the method calls with the builder object any longer.

```groovy
class GroovyUsage {
    void addPointToSource(PointSource source) {
        source.add {
            x 0
            y 100
        }
        source.add { x 2 y 3 }
    }
}
```

Another approach is to create a Groovy extension which will itself accepts `Closure` and use `ConsumerWithDelegate`,
`FunctionWithDelegate` or `PredicateWithDelegate` to wrap closure as `Consumer`, `Function` or `Predicate`.
This approach is preferred as it gives more options for static compilation hints.

```java
class PointExtensions {

    public static Point add(
        PointSource self,
        @DelegatesTo(value = PointBuilder.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.example.PointBuilder")
        Closure builder
    ) {
        return self.add(ConsumerWithDelegate.create(builder));
    }

}
```

## Setup

The binaries are hosted on Maven Central

### Gradle

```
repositories {
    mavenCentral()
}

dependencies {
    compile 'space.jasan:groovy-closure-support:0.6.2'
}
```

### Maven
```
<dependency>
  <groupId>space.jasan</groupId>
  <artifactId>groovy-closure-support</artifactId>
  <version>0.6.2</version>
  <type>pom</type>
</dependency>
```

## Version Compatibility
This utility is tested against following JVMs
  * OracleJDK 8

 and following Groovy versions
  * `2.0.8`
  * `2.1.9`
  * `2.2.2`
  * `2.3.11`
  * `2.4.6`
  * `2.5.4`
