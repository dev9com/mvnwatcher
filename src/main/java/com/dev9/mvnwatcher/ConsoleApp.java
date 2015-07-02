package com.dev9.mvnwatcher;

import com.dev9.mvnwatcher.event.DirectoryEventWatcherImpl;
import com.dev9.mvnwatcher.event.FileChangeSubscriber;
import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class ConsoleApp {

    private final Path sourcePath;
    private final Path projectPath;

    public ConsoleApp(Path sourcePath, Path projectPath) {
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
        subscriber = new FileChangeSubscriber(dirWatcher, projectPath);
        eventBus.register(subscriber);
    }

    /** Set to false to terminate */
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

    public static void main(String[] args) {

        String cwd = Paths.get("").toAbsolutePath().toString();

        Path projectPath = Paths.get(cwd, "src/test/resources/sample-project/");
        Path sourcePath = Paths.get(cwd,"src/test/resources/sample-project/src/main/java");

        ConsoleApp runner = null;

        try {
            runner = new ConsoleApp(sourcePath, projectPath);
            runner.startUpWatcher();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (runner != null)
            runner.waitForCancel();

    }

}
