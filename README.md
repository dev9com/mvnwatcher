# mvnwatcher

Maven Plugin Build Watcher
==========================

Maven Plugin for watching and restarting a build based on monitoring a file directory.  

When used with a microservice project, gives you something that feels like a dynamic reload capability, 
especially if running on a fast machine.  

Usage
=====

Add the following to your pom.xml:

````xml
    <profiles>
        <profile>
            <id>watch</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.dev9</groupId>
                        <artifactId>watcher-maven-plugin</artifactId>
                        <version>1.6</version>
                        <executions>
                            <execution>
                                <id>watch</id>
                                <goals>
                                    <goal>watch</goal>
                                </goals>
                                <phase>verify</phase>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
````

And then execute the command:

    mvn clean verify -Pwatch

This will automatically start the project in watch mode, assuming that the project builds correctly.  You can
stop watching by either stopping from the system menu, or by pressing ctl-C or otherwise terminating the build.

Development
===========

Here is all you need to start hacking:

````bash
     git clone https://github.com/dev9com/mvnwatcher.git
     cd mvnwatcher
     mvn install
````

That will build a snapshot of the plugin in your local repository.  Then, add the following (or create) 
your  [settings.xml](https://maven.apache.org/settings.html).  

````xml
    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          http://maven.apache.org/xsd/settings-1.0.0.xsd">
        <pluginGroups>
            <pluginGroup>com.dev9</pluginGroup>
        </pluginGroups>
    </settings>
````

...and then run to run plugin, either from inside your IDE or a terminal just type...

````bash
    mvn clean verify watcher:watch
````    
    
The plugin should launch and start monitoring your project.  Changes to files or directories inside the sources
folder will cause the plugin to stop and then restart the build.  You can easily see the status of the build from
the system tray icon (green happy = ready, blue gear = restarting build, red exclaimation = build failing for some 
reason).

As described in Configuration, the default configuration assumes you are running a Spring Boot project.  If
you are just trying this out for the first time, the easiest thing to do is go to the 
[Spring Boot project wizard site](http://start.spring.io/), leave the project defaults, except check "WS" (for web
services).  Download the demo project, and do a `mvn clean install watcher:watch`.  You should see an icon appear in 
your system tray indicating the current status of the build.

Configuration
=============

The configuration options include:

**sourceDirectory** default-value="${project.build.sourceDirectory}"

**basedir** default-value="${project.basedir}"

**directory** default-value="${project.build.directory}"

**terminate** defaults to false, set this to true for the plugin to self-terminate (useful for testing)

**tasks**
  * executable
  * arguments
  * outputFile
  * executableDirectory
    
The defaults for tasks are:

````java
    Task mvnBuild = new Task(
                "mvn",
                java.util.Arrays.asList("resources:resources", 
                     "compiler:compile", "jar:jar", "spring-boot:repackage"),
                Paths.get(basedir.getAbsolutePath(), 
                     "target", "mvnrunner.log").toFile(),
                basedir.toPath());

        Task javaBuild = new Task(
                "java",
                java.util.Arrays.asList("-jar", "demo-0.0.1-SNAPSHOT.jar"),
                Paths.get(directory.getAbsolutePath(), "mvnrunner-app.log").toFile(),
                directory.toPath()
        );
````

Thanks & Inspiration
====================

* [File watching implementation based on Guava EventBus](http://codingjunkie.net/eventbus-watchservice/)
* [Gradle](https://docs.gradle.org/current/release-notes#continuous-build)
* [Grunt](https://github.com/gruntjs/grunt-contrib-watch)
* [Gulp](https://www.npmjs.com/package/gulp-watch)
* [Play](https://www.playframework.com/)

Spring Boot already has a class-reload framework, which does something a bit different - it swaps in byte-cde at runtime
when it detects a code change.  It can also be configured to swap in different templates.  It's well worth checking out
the standard [Spring Boot hot reload features](http://docs.spring.io/spring-boot/docs/current/reference/html/howto-hotswapping.html),
although it's more complex to set up.

[JRebel](http://zeroturnaround.com/software/jrebel/) does something similar.

Mvnwatcher is different - it just flat restarts the server when it notices changes.

You may want to start with mvnwatcher, and then if you start getting impatient with the turnaround time, investigate
the Spring Boot hot reload features instead if you feel that rebooting the whole Spring Boot app is too slow.  Of course,
you might be getting away from a "microservice" then...  ;)
