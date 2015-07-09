package com.dev9.mvnwatcher;

import com.dev9.mvnwatcher.WatcherMojo;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildRestartTest {

    MvnRunner runner;

    @Test
    public void simpleBootTest() throws IOException, InterruptedException {
        String cwd = Paths.get("").toAbsolutePath().toString();

        Path projectPath = Paths.get(cwd, "target", "test-classes", "sample-project");
        Path targetPath = Paths.get(cwd, "target", "test-classes", "sample-project", "target");

        MvnRunner runner = new MvnRunner(projectPath, targetPath, new WatcherMojo().getDefaultTasks());

        runner.start();

        Thread.sleep(5000);
    }

    @After
    public void shutdown() {
        if (runner != null)
            runner.stop();
    }


}
