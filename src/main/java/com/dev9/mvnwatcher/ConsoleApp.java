package com.dev9.mvnwatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConsoleApp {

    public static void main(String[] args) {

        String cwd = Paths.get("").toAbsolutePath().toString();

        Path projectPath = Paths.get(cwd, "src/test/resources/sample-project/");
        Path sourcePath = Paths.get(cwd, "src/test/resources/sample-project/src/main/java");

        MvnWatcher runner = null;

        try {
            runner = new MvnWatcher(sourcePath, projectPath);
            runner.startUpWatcher();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (runner != null)
            runner.waitForCancel();

    }

}
