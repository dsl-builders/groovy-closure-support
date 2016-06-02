# Groovy Closure Support for Java

Groovy Closure Support for Java enables using closure delegation shortcuts on coerced closures.

Currently supports setting delegate of closures coerced to single abstract method interfaces only.

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

## Setup

The binaries are hosted on BinTray: https://bintray.com/jasan/space/groovy-closure-support

### Gradle

```
repositories {
    maven {
        url  'http://dl.bintray.com/jasan/space'
    }
}

dependencies {
    compile 'space.jasan:groovy-closure-support:0.1.0'
}
```

### Maven

```
<?xml version='1.0' encoding='UTF-8'?>
<settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd' xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
<profiles>
	<profile>
		<repositories>
			<repository>
				<snapshots>
					<enabled>false</enabled>
				</snapshots>
				<id>bintray-jasan-space</id>
				<name>bintray</name>
				<url>http://dl.bintray.com/jasan/space</url>
			</repository>
		</repositories>
		<pluginRepositories>
			<pluginRepository>
				<snapshots>
					<enabled>false</enabled>
				</snapshots>
				<id>bintray-jasan-space</id>
				<name>bintray-plugins</name>
				<url>http://dl.bintray.com/jasan/space</url>
			</pluginRepository>
		</pluginRepositories>
		<id>bintray</id>
	</profile>
</profiles>
<activeProfiles>
	<activeProfile>bintray</activeProfile>
</activeProfiles>
</settings>
```

```
<dependency>
  <groupId>space.jasan</groupId>
  <artifactId>groovy-closure-support</artifactId>
  <version>0.1.0</version>
  <type>pom</type>
</dependency>
```

