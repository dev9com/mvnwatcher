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

    public DirectoryEventWatcherImpl(EventBus eventBus) throws IOException {
        this.eventBus = Objects.requireNonNull(eventBus);
        if (watchService == null) {
            watchService = FileSystems.getDefault().newWatchService();
        }
    }

    @Override
    public void start() throws IOException {
        createWatchTask();
        startWatching();
    }

    public void add(Path p) throws IOException {
        Objects.requireNonNull(p);
        if (!p.toFile().exists())
            throw new IllegalArgumentException("Path " + p.toFile().getAbsolutePath() + " does not actually exist.");

        Files.walkFileTree(p, new WatchServiceRegisteringVisitor());
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

    private class WatchServiceRegisteringVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Objects.requireNonNull(dir);
            Objects.requireNonNull(watchService);

            dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            return FileVisitResult.CONTINUE;
        }
    }
}
