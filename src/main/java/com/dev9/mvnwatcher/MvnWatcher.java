package com.dev9.mvnwatcher;

import com.dev9.mvnwatcher.event.DirectoryEventWatcherImpl;
import com.dev9.mvnwatcher.event.FileChangeSubscriber;
import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * Responsible for the overall lifecycle of a monitored project
 */
public class MvnWatcher {

    private final Path sourcePath;
    private final Path projectPath;

    private MvnRunner runner;

    public MvnWatcher(Path sourcePath, Path projectPath) {
        this.sourcePath = Objects.requireNonNull(sourcePath);
        this.projectPath = Objects.requireNonNull(projectPath);
    }

    /**
     * Configure and start watching the project
     */
    public void startUpWatcher() throws IOException {
        EventBus eventBus;
        DirectoryEventWatcherImpl dirWatcher;
        CountDownLatch doneSignal;
        FileChangeSubscriber subscriber;

        eventBus = new EventBus();
        dirWatcher = new DirectoryEventWatcherImpl(eventBus, sourcePath);
        dirWatcher.start();
        runner = new MvnRunner(projectPath);
        subscriber = new FileChangeSubscriber(dirWatcher, runner);
        eventBus.register(subscriber);
    }

    /**
     * Set to false to terminate
     */
    public boolean terminate = false;

    /**
     * Start a sleeping loop waiting for a cancel.
     */
    public void waitForCancel() {

        while (!terminate) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        runner.stop();
    }

}
