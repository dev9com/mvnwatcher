package com.dev9.mvnwatcher;

import com.dev9.mvnwatcher.event.DirectoryEventWatcherImpl;
import com.dev9.mvnwatcher.event.FileChangeSubscriber;
import com.dev9.mvnwatcher.event.MvnRunner;
import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class MvnWatcher {

    private final Path sourcePath;
    private final Path projectPath;

    public MvnWatcher(Path sourcePath, Path projectPath) {
        this.sourcePath = Objects.requireNonNull(sourcePath);
        this.projectPath = Objects.requireNonNull(projectPath);
    }

    private EventBus eventBus;
    private DirectoryEventWatcherImpl dirWatcher;
    private CountDownLatch doneSignal;
    private FileChangeSubscriber subscriber;

    public void startUpWatcher() throws IOException {
        eventBus = new EventBus();
        dirWatcher = new DirectoryEventWatcherImpl(eventBus, sourcePath);
        dirWatcher.start();
        MvnRunner runner = new MvnRunner(projectPath);
        subscriber = new FileChangeSubscriber(dirWatcher, runner);
        eventBus.register(subscriber);
    }

    /**
     * Set to false to terminate
     */
    public boolean terminate = false;

    public void waitForCancel() {

        while (!terminate) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
