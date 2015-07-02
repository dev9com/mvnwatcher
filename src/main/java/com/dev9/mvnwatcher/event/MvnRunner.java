package com.dev9.mvnwatcher.event;

import com.dev9.mvnwatcher.RunnerErrorHandler;
import com.dev9.mvnwatcher.RunnerOutputHandler;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class MvnRunner {

    Path projectPath;

    public MvnRunner(Path projectPath)
    {
        this.projectPath = projectPath;
    }


    public void stopBuild()
    {

    }


    public void startBuildWithProcessBuilder()
    {

        List<String> params = java.util.Arrays.asList("mvn", "spring-boot:run");
        ProcessBuilder b = new ProcessBuilder(params);
        b.directory(projectPath.toFile());

        Path log = Paths.get("", "target", "mvnrunner.log");

        b.redirectErrorStream(true);
        b.redirectOutput(ProcessBuilder.Redirect.appendTo(log.toFile()));
        try {
            Process p = b.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startBuildWithInvoker()
    {

        InvocationRequest request = new DefaultInvocationRequest();

        request.setBaseDirectory(projectPath.toFile());
        request.setPomFile(new File(projectPath.toFile(), "pom.xml"));
        request.setGoals(Collections.singletonList("spring-boot:run"));
        request.setErrorHandler(new RunnerErrorHandler());
        request.setInteractive(false);
        //request.setOffline(true);
        request.setOutputHandler(new RunnerOutputHandler());
        request.setRecursive(false);
        request.setUpdateSnapshots(false);

        Invoker invoker = new DefaultInvoker();

        InvocationResult result = null;
        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }

        if (result != null)
            if (result.getExitCode() != 0) {
                throw new IllegalStateException("Build failed.");
            }

    }

}
