package com.dev9.mvnwatcher.event;

import com.google.common.eventbus.Subscribe;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;


public class FileChangeSubscriber implements PathEventSubscriber {

    DirectoryEventWatcher dirWatcher;
    MvnRunner runner;

    public FileChangeSubscriber(DirectoryEventWatcher dirWatcher, MvnRunner runner) {
        this.dirWatcher = Objects.requireNonNull(dirWatcher);
        this.runner = Objects.requireNonNull(runner);
    }

    @Subscribe
    @Override
    public void handlePathEvents(PathEventContext pathEventContext) {

        runner.startBuildWithInvoker();

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
