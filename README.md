# mvnwatcher

Maven Plugin Build Watcher
==========================

Maven Plugin for watching and restarting a build based on monitoring a file directory.  

When used with a microservice project, gives you something that feels like a dynamic reload capability, 
especially if running on a fast machine.  

Usage
=====

Until this plugin is available from Maven central, you'll want to download and install it to your local repository
the old fashioned way - build it from source.

     git clone https://github.com/dev9com/mvnwatcher.git
     cd mvnwatcher
     mvn install

That will build a snapshot of the plugin in your local repository.  Then, add the following (or create) 
your  [settings.xml](https://maven.apache.org/settings.html).  

    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          http://maven.apache.org/xsd/settings-1.0.0.xsd">
        <pluginGroups>
            <pluginGroup>com.dev9</pluginGroup>
        </pluginGroups>
    </settings>

...and then run to run plugin, either from inside your IDE or a terminal just type...

    mvn watcher:watch
    
The plugin should launch and start monitoring your project.  Changes to files or directories inside the sources
folder will cause the plugin to stop and then restart the build.

As described in Configuration, below, the default configuration assumes you are running a Spring Boot project.  If
you are just trying this out for the first time, the easiest thing to do is go to the 
[Spring Boot project wizard site](http://start.spring.io/), leave the project defaults, except check "WS" (for web
services).  Download the demo project, and do a `mvn clean install` to verify things.  Then, do a 
`mvn watcher:watch` to start the watcher service.  You should see an icon appear in your system tray indicating the
current status of the build.

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

To Do
=====

* Add newly added directories to watch (directories added after the watcher is launched are not monitored)
* Support complex directory configuration (file patterns)

Thanks & Inspiration
====================

[File watching basic implementation based on Guava EventBus.](http://codingjunkie.net/eventbus-watchservice/)

[Gradle](https://docs.gradle.org/current/release-notes#continuous-build)
[Grunt](https://github.com/gruntjs/grunt-contrib-watch)
[Gulp](https://www.npmjs.com/package/gulp-watch)
[Play](https://www.playframework.com/)
