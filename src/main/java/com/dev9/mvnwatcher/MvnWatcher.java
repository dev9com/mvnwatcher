package com.dev9.mvnwatcher;

import com.dev9.mvnwatcher.event.DirectoryEventWatcherImpl;
import com.dev9.mvnwatcher.event.FileChangeSubscriber;
import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * Responsible for the overall lifecycle of a monitored project
 */
public class MvnWatcher {

    private final Path sourcePath;
    private final Path projectPath;
    private final Path targetPath;
    private final List<Task> tasks;

    private MvnRunner runner;

    /**
     * Set to false to terminate
     */
    public boolean terminate = false;

    public MvnWatcher(Path sourcePath, Path projectPath, Path targetPath, List<Task> tasks) {
        this.sourcePath = Objects.requireNonNull(sourcePath);
        this.projectPath = Objects.requireNonNull(projectPath);
        this.targetPath = Objects.requireNonNull(targetPath);
        this.tasks = Objects.requireNonNull(tasks);
    }

    /**
     * Configure and start watching the project
     */
    public void startUpWatcher() throws IOException {
        EventBus eventBus;
        DirectoryEventWatcherImpl dirWatcher;
        FileChangeSubscriber subscriber;

        eventBus = new EventBus();
        dirWatcher = new DirectoryEventWatcherImpl(eventBus, sourcePath);
        dirWatcher.start();
        runner = new MvnRunner(projectPath, targetPath, tasks);
        subscriber = new FileChangeSubscriber(dirWatcher, runner);
        eventBus.register(subscriber);
        runner.start();
    }

    /**
     * Start a sleeping loop waiting for a cancel.
     */
    public void waitForCancel() {

        while (!terminate) {
            try {
                Thread.sleep(1000);

                if (!runner.running())
                    terminate = true;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
