# Groovy Closure Support for Java

Groovy Closure Support for Java enables using closure delegation shortcuts on coerced closures.

Currently supports setting delegate of closures coerced to functional interface only

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

```java
class JavaUsage {
    void addPointToSource(PointSource source) {
        source.add(point -> {
            point.x(0);
            point.y(100);
        });
    }
}
```

```groovy
class GroovyUsage {
    void addPointToSource(PointSource source) {
        source.add {
            x 0
            y 100
        }
    }
}
```
