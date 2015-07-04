package com.dev9.mvnwatcher;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MvnwatcherTest {

    @Test
    public void simpleBootTest() throws IOException {
        String cwd = Paths.get("").toAbsolutePath().toString();

        Path projectPath = Paths.get(cwd, "target/test-classes/sample-project/");
        Path sourcePath = Paths.get(cwd, "target/test-classes/sample-project/src/main/java");

        MvnWatcher runner = null;

        runner = new MvnWatcher(sourcePath, projectPath);

        runner.startUpWatcher();
    }


}
