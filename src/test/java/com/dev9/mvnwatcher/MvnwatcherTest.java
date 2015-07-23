package com.dev9.mvnwatcher;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MvnwatcherTest {

    MvnWatcher runner;

    @Test
    public void simpleBootTest() throws IOException {
        String cwd = Paths.get("").toAbsolutePath().toString();

        Path projectPath = Paths.get(cwd, "target", "test-classes", "sample-project");
        Path targetPath = Paths.get(cwd, "target", "test-classes", "sample-project", "target");
        Path sourcePath = Paths.get(cwd, "target", "test-classes", "sample-project", "src", "main", "java");

        List<Path> watchPath = new ArrayList<>();
        watchPath.add(sourcePath);

        runner = new MvnWatcher(watchPath, projectPath, targetPath, new WatcherMojo().getDefaultTasks(projectPath));

        runner.startUpWatcher();
    }

    @After
    public void shutdown() {
        if (runner != null)
            runner.terminate = true;
    }


}
