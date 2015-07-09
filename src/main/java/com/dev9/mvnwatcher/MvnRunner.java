package com.dev9.mvnwatcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Responsible for managing the lifecycle of the mvn execution.
 * <p/>
 * Monitor started = set dirty flag
 * Directory changed = set dirty flag
 * If dirty flag set, wait 1 second, then stop build if running, and then start build.
 */
public class MvnRunner {

    Path projectPath;
    Path targetDirectory;

    public MvnRunner(Path projectPath, Path targetDirectory, List<Task> tasks) {
        this.projectPath = Objects.requireNonNull(projectPath);
        this.targetDirectory = Objects.requireNonNull(targetDirectory);
        this.tasks = Objects.requireNonNull(tasks);
    }

    private MvnMonitor monitor;

    private List<Task> tasks;

    public boolean running() {
        return !monitor.shutdown;
    }

    public String status() {
        return monitor.status();
    }

    public void start() {

        List<ProcessBuilder> configs = new ArrayList<>();

        for (Task task : tasks) {
            ProcessBuilder pb = task.toProcessBuilder();
            configs.add(pb);
        }

        monitor = new MvnMonitor(configs);

        new Thread(monitor, "mvnmonitor").start();

        Thread reaper = new Thread(new MvnTerminate(monitor), "mvnmonitor-reaper");

        Runtime.getRuntime().addShutdownHook(reaper);
    }

    public void changeEvent() {
        monitor.dirty = true;
    }

    public void stop() {
        monitor.shutdown = true;
    }

    private class MvnTerminate implements Runnable {

        private MvnMonitor monitor;

        MvnTerminate(MvnMonitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            monitor.kill();
        }
    }


}
