package com.dev9.mvnwatcher.event;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Based on code written originally by bbejeck
 */

public class DirectoryEventWatcherImpl implements DirectoryEventWatcher {

    private FutureTask<Integer> watchTask;
    private EventBus eventBus;
    private WatchService watchService;
    private volatile boolean keepWatching = true;
    private Path startPath;


    public DirectoryEventWatcherImpl(EventBus eventBus, Path startPath) {
        this.eventBus = Objects.requireNonNull(eventBus);
        this.startPath = Objects.requireNonNull(startPath);
    }

    @Override
    public void start() throws IOException {
        initWatchService();
        registerDirectories();
        createWatchTask();
        startWatching();
    }

    @Override
    public boolean isRunning() {
        return watchTask != null && !watchTask.isDone();
    }

    @Override
    public void stop() {
        keepWatching = false;
    }

    @VisibleForTesting
    public Integer getEventCount() {
        try {
            return watchTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private void createWatchTask() {
        watchTask = new FutureTask<>(new Callable<Integer>() {
            private int totalEventCount;

            @Override
            public Integer call() throws Exception {

                // Loop to keep watching until shutdown
                while (keepWatching) {
                    WatchKey watchKey = watchService.poll(500, TimeUnit.MILLISECONDS);
                    if (watchKey != null) {
                        List<WatchEvent<?>> events = watchKey.pollEvents();
                        Path watched = (Path) watchKey.watchable();
                        PathEvents pathEvents = new PathEvents(watchKey.isValid(), watched);
                        for (WatchEvent event : events) {
                            pathEvents.add(new PathEvent((Path) event.context(), event.kind()));
                            totalEventCount++;
                        }
                        watchKey.reset();
                        eventBus.post(pathEvents);
                    }
                }

                // Fell out of the watch loop, shut down the service.
                watchService.close();

                return totalEventCount;
            }
        });
    }

    private void startWatching() {
        new Thread(watchTask).start();
    }

    private void registerDirectories() throws IOException {
        Files.walkFileTree(startPath, new WatchServiceRegisteringVisitor());
    }

    private WatchService initWatchService() throws IOException {
        if (watchService == null) {
            watchService = FileSystems.getDefault().newWatchService();
        }
        return watchService;
    }

    public void addPath(Path path) throws IOException {
        System.out.println("Watching: " + path.toAbsolutePath());
        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    private class WatchServiceRegisteringVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            addPath(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
