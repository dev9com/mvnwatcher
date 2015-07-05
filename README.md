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

(Eventual) Usage
================

Well, as of this writing it's a working PoC, but it's filled with hard-coded values.  Eventually, you'll just add this to the pom.xml...

    <build>
        <plugins>
            <plugin>
                <groupId>com.dev9</groupId>
                <artifactId>watcher-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
            </plugin>
        </plugins>
    </build>

...and then type this at the console window...

    mvn watcher:watch
    
...and the plugin will launch, and start monitoring your project.  Changes to files or directories inside the sources
folder will cause the plugin to stop and then restart the build.

Eventually, there will be more configuration options, etc.

Thanks!
=======

File watching basic implementation based on Guava EventBus per...

http://codingjunkie.net/eventbus-watchservice/

To Do
=====

* Add newly added directories to watch
* Add System tray UI notifications
* Add pom.xml configuration options
* Ignore file changes that start with a .
