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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Only goal: watch a directory for changes.
 *
 * @goal watch
 */
@Mojo(name = "Watcher")
public class WatcherMojo extends AbstractMojo {
    /**
     * Location of the Java sources.
     *
     * @parameter default-value="${project.build.sourceDirectory}"
     * @required
     */
    public File sourceDirectory;

    /**
     * Location of the resources.
     *
     * @parameter default-value="${project.build.resources[0].directory}"
     * @required
     */
    public File resourcesDirectory;

    /**
     * This is typically the root folder for the project, holding the pom.xml file.
     *
     * @parameter default-value="${project.basedir}"
     * @readonly
     */
    public File basedir;

    /**
     * Typically, this is the target directory.
     *
     * @parameter default-value="${project.build.directory}"
     * @readonly
     */
    public File directory;

    /**
     * Typically, this is the target directory.
     *
     * @parameter default-value="${project.build.directory}/${project.build.finalName}.${project.packaging}"
     * @readonly
     */

    public File finalName;

    /**
     * Defaults to false.  Set to true for WatcherMojo to self-terminate (useful for testing)
     */
    @VisibleForTesting
    public boolean terminate = false;

    /**
     * @parameter
     */
    public List<Task> tasks;


    public void createTargetDirectoryIfNotExists() {

        if (!basedir.exists())
            throw new IllegalArgumentException("Can't create target directory, no basedir defined.");

        if (directory == null) {
            directory = Paths.get(basedir.getAbsolutePath(), "target").toFile();
        }

        if (!directory.exists()) {
            directory.mkdirs();
        }

    }

    public List<Task> getDefaultTasks(Path base) {
        if (basedir == null)
            basedir = base.toFile();

        if (sourceDirectory == null) {
            sourceDirectory = Paths.get(base.toFile().getAbsolutePath(), "src", "main", "java").toFile();
        }

        if (resourcesDirectory == null) {
            resourcesDirectory = Paths.get(base.toFile().getAbsolutePath(), "src", "main", "resources").toFile();
        }

        if (finalName == null)
            finalName = Paths.get(base.toFile().getAbsolutePath(), "target", "demo-0.0.1-SNAPSHOT.jar").toFile();

        createTargetDirectoryIfNotExists();

        System.out.println("Using default spring-boot configuration.");

        List<Task> result = new ArrayList<>();

        Task mvnBuild = new Task(
                "mvn",
                java.util.Arrays.asList("resources:resources", "compiler:compile", "jar:jar", "spring-boot:repackage"),
                Paths.get(basedir.getAbsolutePath(), "target", "mvnrunner-build.log").toFile(),
                basedir.toPath());

        Task javaBuild = new Task(
                "java",
                java.util.Arrays.asList("-jar", finalName.getAbsolutePath()),
                Paths.get(directory.getAbsolutePath(), "mvnrunner-app.log").toFile(),
                directory.toPath()
        );

        result.add(mvnBuild);
        result.add(javaBuild);

        return result;
    }

    public void execute() throws MojoExecutionException {

        Log log = getLog();

        if (!basedir.exists()) {
            throw new MojoExecutionException("Can't find project directory " + basedir);
        }

        if (!sourceDirectory.exists()) {
            throw new MojoExecutionException("Can't find source directory " + sourceDirectory);
        } else {
            log.info("Found: " + sourceDirectory.getAbsolutePath());
        }

        if (resourcesDirectory != null) {
            if (!resourcesDirectory.exists()) {
                log.warn("Can't find resources directory " + resourcesDirectory);
            } else {
                log.info("Found: " + resourcesDirectory.getAbsolutePath());
            }
        } else {
            log.warn("No resources directory specified.");
        }

        createTargetDirectoryIfNotExists();

        MvnWatcher runner = null;

        try {

            if (tasks == null)
                tasks = getDefaultTasks(basedir.toPath());

            List<Path> directoriesToWatch = new ArrayList<>();
            directoriesToWatch.add(sourceDirectory.toPath());
            directoriesToWatch.add(resourcesDirectory.toPath());

            runner = new MvnWatcher(directoriesToWatch, basedir.toPath(), directory.toPath(), tasks);
            runner.startUpWatcher();
            runner.terminate = terminate;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (runner != null)
            runner.waitForCancel();

    }
}
