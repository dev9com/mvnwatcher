package com.dev9.mvnwatcher;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class MvnwatcherMojoTest {

    @Test
    public void simpleMojoBootTest() throws IOException, MojoExecutionException {

        String cwd = Paths.get("").toAbsolutePath().toString();

        WatcherMojo mojo = new WatcherMojo();

        mojo.basedir = Paths.get(cwd, "target/test-classes/sample-project/").toFile();
        mojo.sourceDirectory = Paths.get(cwd, "target/test-classes/sample-project/src/main/java").toFile();
        mojo.terminate = true;

        mojo.execute();

    }

}
