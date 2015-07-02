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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;

/**
 * Goal which watches the default sourceDirectory
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
    private File sourceDirectory;

    /**
     * @parameter default-value="${project.basedir}"
     * @readonly
     */
    private File basedir;


    public void execute()
            throws MojoExecutionException {

        Log log = getLog();

        if (!sourceDirectory.exists()) {
            throw new MojoExecutionException("Can't find directory " + sourceDirectory);
        } else {
            log.info("Found: " + sourceDirectory.getAbsolutePath());
        }

        ConsoleApp runner = null;

        try {
            runner = new ConsoleApp(sourceDirectory.toPath(), basedir.toPath());
            runner.startUpWatcher();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (runner != null)
            runner.waitForCancel();

    }
}
