package com.dev9.mvnwatcher;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildRestartTest {

    MvnRunner runner;

    @Test
    public void simpleBootTest() throws IOException, InterruptedException {
        String cwd = Paths.get("").toAbsolutePath().toString();

        Path projectPath = Paths.get(cwd, "target", "test-classes", "sample-project");

        assertThat(projectPath).exists();

        Path targetPath = Paths.get(cwd, "target", "test-classes", "sample-project", "target");

        assertThat(targetPath).exists();

        MvnRunner runner = new MvnRunner(projectPath, targetPath, new WatcherMojo().getDefaultTasks(projectPath));

        runner.start();

        Thread.sleep(5000);

        assertThat(runner.status()).isEqualTo("Ready:OK");
    }

    @After
    public void shutdown() {
        if (runner != null)
            runner.stop();
    }


}
