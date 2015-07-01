package com.dev9.mvn.watcher;

import com.dev9.mvn.watcher.event.DirectoryEventWatcherImpl;
import com.dev9.mvn.watcher.event.FileChangeSubscriber;
import com.google.common.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

public class Mvnrunner {

    private final Path sourcePath;
    private final Path projectPath;

    public Mvnrunner(Path sourcePath, Path projectPath) {
        this.sourcePath = sourcePath;
        this.projectPath = projectPath;
    }

    private EventBus eventBus;
    private DirectoryEventWatcherImpl dirWatcher;
    private CountDownLatch doneSignal;
    private FileChangeSubscriber subscriber;

    public void startUpWatcher() throws IOException {
        eventBus = new EventBus();
        dirWatcher = new DirectoryEventWatcherImpl(eventBus, sourcePath);
        dirWatcher.start();
        subscriber = new FileChangeSubscriber(dirWatcher, projectPath);
        eventBus.register(subscriber);
    }

    public void waitForCancel() {
        boolean running = true;

        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        File projectPath = new File("src/mvnrunnner/src/test/resources/sample-project/");
        File sourcePath = new File("src/mvnrunnner/src/test/resources/sample-project/src/main/java");

        Mvnrunner runner = null;

        try {
            runner = new Mvnrunner(sourcePath.toPath(), projectPath.toPath());
            runner.startUpWatcher();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (runner != null)
            runner.waitForCancel();

    }

}
