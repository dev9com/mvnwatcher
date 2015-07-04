package com.dev9.mvnwatcher.event;

import com.dev9.mvnwatcher.MvnRunner;
import com.google.common.eventbus.Subscribe;

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

        runner.changeEvent();

        for (PathEvent evt : pathEventContext.getEvents()) {
            System.out.println("@" + evt.getType().name() + ">" + evt.getEventTarget().getFileName());

            //TODO add newly created directories to monitoring
            //TODO remove deleted directories from monitoring
        }
    }

}
