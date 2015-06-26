package com.dev9.mvnrunner;

import com.dev9.mvnrunner.event.DirectoryEventWatcherImpl;
import com.dev9.mvnrunner.event.FileChangeSubscriber;
import com.google.common.eventbus.EventBus;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class Mvnrunner {

    public static void build() {

        InvocationRequest request = new DefaultInvocationRequest();

        File base = new File("/Users/wiverson/src/mvnrunnner/src/test/resources/sample-project/");
        request.setBaseDirectory(base);
        request.setPomFile(new File("pom.xml"));
        request.setGoals(Collections.singletonList("compiler:compile jar:jar spring-boot:run"));
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

    private EventBus eventBus;
    private DirectoryEventWatcherImpl dirWatcher;
    private CountDownLatch doneSignal;
    private FileChangeSubscriber subscriber;

    static Mvnrunner runner;

    public void startUpWatcher(Path watchPath) throws IOException {
        eventBus = new EventBus();
        dirWatcher = new DirectoryEventWatcherImpl(eventBus, watchPath);
        dirWatcher.start();
        subscriber = new FileChangeSubscriber();
        eventBus.register(subscriber);

    }

    public static void main(String[] args) {
        //build();

        File base = new File("/Users/wiverson/src/mvnrunnner/src/test/resources/sample-project/");

        try {
            System.out.println("Starting watcher...");
            runner = new Mvnrunner();
            runner.startUpWatcher(base.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Thread.sleep(1000);
                if (runner.dirWatcher.isRunning())
                    System.out.print(".");
                else
                    System.out.print("x");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
