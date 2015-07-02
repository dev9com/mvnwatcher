# mvnwatcher

Persistent Maven Build Watcher
==============================

Maven Plugin for watching and restarting a build based on monitoring a file directory.  

When used with a microservice project, gives you something that feels like a dynamic reload capability, 
especially if running on a fast machine.  

Usage
=====

Well, as of this writing it's a not-quite-working-yet PoC.  Eventually, you'll just add this to the pom.xml...

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
* Start goal when first launched
* Shut down & restart goal when change detected
* Add System tray UI notifications
* Add pom.xml configuration options
* Ignore file changes that start with a .
