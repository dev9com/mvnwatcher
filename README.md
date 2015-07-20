# mvnwatcher

Persistent Maven Build Watcher
==============================

Maven Plugin for watching and restarting a build based on monitoring a file directory.  

When used with a microservice project, gives you something that feels like a dynamic reload capability, 
especially if running on a fast machine.  


Current Working State
=====================

Ok, so this is a work in progress, but the concept basically works.  To try it out, do the following:

Check out and install the project.  You'll want to do this:

   mvn clean install
   
...to install the plugin.  Then, once you have build the project, cd into the sample project like this:

   cd target/test-classes/sample-project

...and start the watcher:

   mvn watcher:watch
   
You should see the watcher start watching the demo project immediately.  You can open your browser to:

http://localhost:8080

...and you should see Cool World and the date.

Now, open up this file in your favorite IDE:

   /mvnwatcher/target/test-classes/sample-project/src/main/java/demo/Example.java
   
...and play around with the output of the file.  For example, instead of Cool World change the text to Hello World.
Save the file, and watch the output of the watcher terminal.  After a second or two, you should see the watcher notice
the changes, shut down, rebuild, and relaunch your project!

You'll also see an icon in your system tray showing the status of the build.  Select Quit from the system tray menu
to exit the watcher.

(Eventual) Usage
================

Once the plugin is installed, you can run it from your build by adding the following to your project pom.xml...

    <build>
        <plugins>
            <plugin>
                <groupId>com.dev9</groupId>
                <artifactId>watcher-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
            </plugin>
        </plugins>
    </build>

...and then run the plugin, either from inside your IDE or a terminal...

    mvn watcher:watch
    
The plugin should launch and start monitoring your project.  Changes to files or directories inside the sources
folder will cause the plugin to stop and then restart the build.

Configuration
=============

The configuration options include:

    sourceDirectory - default-value="${project.build.sourceDirectory}"

    basedir - default-value="${project.basedir}"

    directory - default-value="${project.build.directory}"

    terminate - defaults to false, set this to true for the plugin to self-terminate (useful for testing)

    tasks
            executable
            arguments
            outputFile
            executableDirectory
    
The defaults for tasks are:

    Task mvnBuild = new Task(
                "mvn",
                java.util.Arrays.asList("resources:resources", "compiler:compile", "jar:jar", "spring-boot:repackage"),
                Paths.get(basedir.getAbsolutePath(), "target", "mvnrunner.log").toFile(),
                basedir.toPath());

        Task javaBuild = new Task(
                "java",
                java.util.Arrays.asList("-jar", "demo-0.0.1-SNAPSHOT.jar"),
                Paths.get(directory.getAbsolutePath(), "mvnrunner-app.log").toFile(),
                directory.toPath()
        );

Thanks!
=======

File watching basic implementation based on Guava EventBus based on:

http://codingjunkie.net/eventbus-watchservice/

To Do
=====

* Add newly added directories to watch (directories added after the watcher is launched are not monitored)
* Support complex directory configuration (file patterns)

Inspiration
===========

Gradle: https://docs.gradle.org/current/release-notes#continuous-build
Grunt: https://github.com/gruntjs/grunt-contrib-watch
Gulp: https://www.npmjs.com/package/gulp-watch
Play: https://www.playframework.com/
