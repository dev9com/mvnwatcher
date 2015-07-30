package com.dev9.mvnwatcher;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildRestartTest {

    private MvnRunner runner;

    @Test(timeout = 30000)
    public void simpleBootTest() throws IOException, InterruptedException {
        String cwd = Paths.get("").toAbsolutePath().toString();

        Path projectPath = Paths.get(cwd, "target", "test-classes", "sample-project");

        assertThat(projectPath).exists();

        Path targetPath = Paths.get(cwd, "target", "test-classes", "sample-project", "target");

        assertThat(targetPath).exists();

        runner = new MvnRunner(projectPath, targetPath, new WatcherMojo().getDefaultTasks(projectPath));

        runner.start();

        while (runner.status().compareToIgnoreCase("Ready:OK") != 0)
            Thread.sleep(500);
    }

    @After
    public void shutdown() {
        if (runner != null)
            runner.stop();
    }


}
