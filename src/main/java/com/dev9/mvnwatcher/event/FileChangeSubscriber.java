package com.dev9.mvnwatcher.event;

import com.dev9.mvnwatcher.RunnerErrorHandler;
import com.dev9.mvnwatcher.RunnerOutputHandler;
import com.google.common.eventbus.Subscribe;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;


public class FileChangeSubscriber implements PathEventSubscriber {

    DirectoryEventWatcher dirWatcher;
    Path projectPath;

    public FileChangeSubscriber(DirectoryEventWatcher dirWatcher, Path projectPath) {
        this.dirWatcher = dirWatcher;
        this.projectPath = projectPath;
    }

    @Subscribe
    @Override
    public void handlePathEvents(PathEventContext pathEventContext) {

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

        for (PathEvent evt : pathEventContext.getEvents()) {
            System.out.println("@" + evt.getType().name() + ">" + evt.getEventTarget().getFileName());

            if (evt.getType().name().compareTo("ENTRY_CREATE") == 0) {
                if (Files.isDirectory(evt.getEventTarget()))
                    try {
                        dirWatcher.stop();
                        dirWatcher.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

}
