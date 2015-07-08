package com.dev9.mvnwatcher;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal which watches the default sourceDirectory
 * <p/>
 * //            ProcessBuilder pb1 = getPB(java.util.Arrays.asList(
 * //                    "mvn", "resources:resources", "compiler:compile", "jar:jar", "spring-boot:repackage"), projectPath, "mvnrunner.log");
 * //            ProcessBuilder pb2 = getPB(java.util.Arrays.asList("java", "-jar", "demo-0.0.1-SNAPSHOT.jar"), targetDir, "app.log");
 *
 * @goal watch
 */
@Mojo(name = "Watcher")
public class WatcherMojo
        extends AbstractMojo {
    /**
     * Location of the file.
     *
     * @parameter property="project.build.sourceDirectory" default-value="${project.build.sourceDirectory}"
     * @required
     */
    public File sourceDirectory;

    /**
     * @parameter default-value="${project.basedir}"
     * @readonly
     */
    public File basedir;

    /**
     * Defaults to false.  Set to true for WatcherMojo to self-terminate (useful for testing and...?)
     */
    @VisibleForTesting
    public boolean terminate = false;

    /**
     * @parameter
     */
    public List<Task> tasks;

    public List<Task> getDefaultTasks()
    {
        if(basedir == null)
            basedir = Paths.get("").toFile();

        if(sourceDirectory == null)
            sourceDirectory = Paths.get("", "src", "main", "java").toFile();

        List<Task> result = new ArrayList<>();

        Task mvnBuild = new Task();
        mvnBuild.setExecutable("mvn");
        mvnBuild.setArguments(java.util.Arrays.asList("resources:resources", "compiler:compile", "jar:jar", "spring-boot:repackage"));
        mvnBuild.setOutputFile(Paths.get(basedir.getAbsolutePath(), "target", "mvnrunner.log").toFile());;

        Task javaBuild = new Task();
        javaBuild.setExecutable("java");
        javaBuild.setArguments(java.util.Arrays.asList("-jar", "demo-0.0.1-SNAPSHOT.jar"));
        javaBuild.setOutputFile(Paths.get(basedir.getAbsolutePath(), "target", "mvnrunner-app.log").toFile());;

        result.add(mvnBuild);
        result.add(javaBuild);

        return result;
    }

    public void execute()
            throws MojoExecutionException {

        Log log = getLog();

        if (!sourceDirectory.exists()) {
            throw new MojoExecutionException("Can't find directory " + sourceDirectory);
        } else {
            log.info("Found: " + sourceDirectory.getAbsolutePath());
        }

        MvnWatcher runner = null;

        try {

            if(tasks == null)
                tasks = getDefaultTasks();

            runner = new MvnWatcher(sourceDirectory.toPath(), basedir.toPath(), tasks);
            runner.startUpWatcher();
            runner.terminate = terminate;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (runner != null)
            runner.waitForCancel();

    }
}
