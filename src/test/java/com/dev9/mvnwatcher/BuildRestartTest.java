package com.dev9.mvnwatcher;

import com.dev9.mvnwatcher.event.MvnRunner;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildRestartTest {

    @Test
    public void simpleBootTest() throws IOException, InterruptedException {
        String cwd = Paths.get("").toAbsolutePath().toString();

        Path projectPath = Paths.get(cwd, "target/test-classes/sample-project/");
        Path sourcePath = Paths.get(cwd, "target/test-classes/sample-project/src/main/java");

        MvnRunner runner = new MvnRunner(projectPath);

        runner.startBuildWithProcessBuilder();

        Thread.sleep(3000);

    }


}
