package com.dev9.mvnwatcher;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MvnwatcherTest {

    MvnWatcher runner;

    @Test
    public void simpleBootTest() throws IOException {
        String cwd = Paths.get("").toAbsolutePath().toString();

        Path projectPath = Paths.get(cwd, "target/test-classes/sample-project/");
        Path sourcePath = Paths.get(cwd, "target/test-classes/sample-project/src/main/java");

        runner = new MvnWatcher(sourcePath, projectPath);

        runner.startUpWatcher();
    }

    @After
    public void shutdown() {
        if (runner != null)
            runner.terminate = true;
    }


}
