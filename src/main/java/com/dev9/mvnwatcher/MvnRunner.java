package com.dev9.mvnwatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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


    private ProcessBuilder getPB(List<String> params, File logFile) {
        ProcessBuilder b = new ProcessBuilder(params);
        b.directory(targetDirectory.toFile());

        b.redirectErrorStream(true);
        b.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        return b;
    }

    public void start() {

        List<ProcessBuilder> configs = new ArrayList<>();

        for (Task task : tasks) {
            ProcessBuilder pb = getPB(task.toArgList(), task.getOutputFile());
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

    private class MvnMonitor implements Runnable {

        private List<ProcessBuilder> config;

        private Process watchedProcess;

        public MvnMonitor(List<ProcessBuilder> config) {
            this.config = config;
        }

        Exception lastError = null;

        public boolean dirty = true;

        public boolean shutdown = false;

        public int statusCode = 0;

        @Override
        public void run() {

            Exception lastNotifiedException = null;

            while (!shutdown) {
                if (lastError != lastNotifiedException) {
                    lastError = lastNotifiedException;
                    if (lastError != null)
                        System.err.println(lastError.getMessage());
                }

                if(dirty == true) {
                    try {
                        dirty = false;

                        for (int i = 0; i < config.size() - 1; i++) {

                            ProcessBuilder pb = config.get(i);

                            System.out.print("Starting " + pb.command().get(0) + "...");

                            Process p = pb.start();

                            System.out.println("done. ");
                            try {
                                p.waitFor(10, TimeUnit.SECONDS);
                            } catch (InterruptedException e) {
                                lastNotifiedException = e;
                                e.printStackTrace();
                            } finally {
                                if (p.isAlive()) {
                                    System.out.println("Destroy Forcibly " + pb.command().get(0));
                                    p.destroyForcibly();
                                }
                            }
                        }

                        ProcessBuilder finalConfig = config.get((config.size() - 1));

                        if (watchedProcess == null) {
                            System.out.print("Starting process (a) " + finalConfig.command().get(0) + "...");
                            watchedProcess = finalConfig.start();
                            System.out.println("ready. " + watchedProcess.toString());

                        } else if (!watchedProcess.isAlive()) {
                            System.out.print("Starting process (b) " + finalConfig.command().get(0) + "...");
                            watchedProcess = finalConfig.start();
                            System.out.println("ready. " + watchedProcess.toString());
                        }

                    } catch (IOException e) {
                        lastError = e;
                        e.printStackTrace();
                    }
                }

                while (dirty == false && watchedProcess.isAlive()) {
                    try {
                        if (watchedProcess.isAlive())
                            watchedProcess.waitFor(500, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        lastError = e;
                        e.printStackTrace();
                    }
                }

                kill();

                try {
                    statusCode = watchedProcess.exitValue();
                } catch (IllegalThreadStateException e) {
                    lastError = e;
                }
            }
            kill();
        }

        private void kill() {

            if (watchedProcess != null)
                if (watchedProcess.isAlive()) {
                    watchedProcess.destroyForcibly();
                    System.out.println("Killed " + watchedProcess.toString());
                }

        }

    }

}
