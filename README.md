# Jasmine Junit Runner
[![Build Status](https://api.travis-ci.org/akiellor/jasmine-junit-runner.png)](http://travis-ci.org/akiellor/jasmine-junit-runner)

## What's this?

Jasmine Junit Runner allows <a href="https://github.com/pivotal/jasmine" target="_blank">Jasmine</a> specs to be executed
anywhere a JUnit test can be executed. Be it an IDE (Eclipse/IntelliJ) or build tool (Gradle/Maven/Ant).

## Getting Started
This getting started assumes you are using Gradle, but the project doesn't require Gradle. There are currently working examples for the following:

* Gradle [https://github.com/akiellor/jasmine-gradle](https://github.com/akiellor/jasmine-gradle)
* Maven [https://github.com/akiellor/jasmine-maven](https://github.com/akiellor/jasmine-maven)

### Get Gradle
Gradle is a language agnostic build tool with a groovy buildscript DSL, you can get it from [Gradle.org](http://gradle.org).

### Setting Up Your New Project

To get started with gradle, we need a build.gradle. This file contains all of our build logic and dependency declarations.

Create a new build.gradle in a new directory with the following content:

```groovy

apply plugin: 'java'
apply plugin: 'idea'
apply plugin: 'eclipse'

repositories {
  maven { url 'https://raw.github.com/akiellor/jasmine-junit-runner/mvn-repo' }
  mavenCentral()
}

sourceSets {
  main {
    resources {
      srcDir 'src/main/javascript'
    }
  }
  test {
    resources {
      srcDir 'src/test/javascript'
    }
  }
}

dependencies {
  testCompile 'jasmine:junit-runner:0.8'
}

```

So what does this do?

* The plugins at the top declare that this project is a 'java' project and that we are going to use gradles 'idea' (IntelliJ) and 'eclipse' support. Typically you would use either eclipse or idea, feel free to remove the one you don't care about.
* The repositories section declares that the artifact repositories we are going to use. In this case mavenCentral and the jasmine-junit-runner repository.
* The sourceSets section declares where in the project Gradle can find the javascript sources and specs.
* The dependencies section includes the one and only top level dependency jasmine:junit-runner.

To setup you IDE project files you should be able to run:

#### For Eclipse

```
gradle eclipse
```

#### For IntelliJ

```
gradle idea
```

You should be able to open the project in you IDE of choice.

### Lets write some specs!

Jasmine JUnit Runner uses a standard JUnit test to run the Jasmine specs. Create a new empty JUnit test class in **src/test/java** and annotate it with:

```java
@RunWith(JasmineTestRunner.class)
```

If you run the test, it should fail with something like **"&lt;yourTestClass&gt;Spec.js could not be found"**. Go ahead and create it in **src/test/javascript**.

If you run the test again, and again it fails as no specs are present. Lets fix that and add a spec to that file:

```javascript
describe("Calculator", function() {
	it("should add two numbers together", function() {
		expect(1 + 2).toBe(3);
	});
});
```

Running the test again should give you a single passing Spec and some output like (from IntelliJ):

![Junit IntelliJ](http://oi45.tinypic.com/20j0g15.jpg)


