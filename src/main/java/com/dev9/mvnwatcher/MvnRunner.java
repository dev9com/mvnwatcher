package com.dev9.mvnwatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for managing the lifecycle of the mvn execution.
 * <p>
 * Monitor started = set dirty flag
 * Directory changed = set dirty flag
 * If dirty flag set, wait 1 second, then stop build if running, and then start build.
 */
public class MvnRunner {

    Path projectPath;

    public MvnRunner(Path projectPath) {
        this.projectPath = projectPath;
    }

    private MvnMonitor monitor;

    public void start() {

        List<String> params = java.util.Arrays.asList("mvn", "spring-boot:run");
        ProcessBuilder b = new ProcessBuilder(params);
        b.directory(projectPath.toFile());

        Path log = Paths.get("", "target", "mvnrunner.log");

        b.redirectErrorStream(true);
        b.redirectOutput(ProcessBuilder.Redirect.appendTo(log.toFile()));

        monitor = new MvnMonitor(b);

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
            System.out.println("Running hook...");
            monitor.kill();
        }
    }

    private class MvnMonitor implements Runnable {

        private ProcessBuilder config;

        private Process watchedProcess;

        public MvnMonitor(ProcessBuilder config) {
            this.config = config;
        }

        Exception lastError = null;

        public boolean dirty = true;

        public boolean shutdown = false;

        public int statusCode = 0;

        @Override
        public void run() {

            Exception lastNotifiedException = null;
            if (lastError != lastNotifiedException) {
                lastError = lastNotifiedException;
                if (lastError != null)
                    System.err.println(lastError.getMessage());
            }

            while (!shutdown) {

                try {
                    dirty = false;

                    if (watchedProcess == null) {
                        watchedProcess = config.start();
                        System.out.println("Started process (a) " + watchedProcess.toString());
                    } else if (!watchedProcess.isAlive()) {
                        watchedProcess = config.start();
                        System.out.println("Started process (b) " + watchedProcess.toString());
                    }

                } catch (IOException e) {
                    lastError = e;
                }

                while (dirty == false && watchedProcess.isAlive()) {
                    try {
                        if (watchedProcess.isAlive())
                            watchedProcess.waitFor(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        lastError = e;
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


//    public void startBuildWithInvoker() {
//
//        InvocationRequest request = new DefaultInvocationRequest();
//
//        request.setBaseDirectory(projectPath.toFile());
//        request.setPomFile(new File(projectPath.toFile(), "pom.xml"));
//        request.setGoals(Collections.singletonList("spring-boot:run"));
//        request.setErrorHandler(new RunnerErrorHandler());
//        request.setInteractive(false);
//        //request.setOffline(true);
//        request.setOutputHandler(new RunnerOutputHandler());
//        request.setRecursive(false);
//        request.setUpdateSnapshots(false);
//
//        Invoker invoker = new DefaultInvoker();
//
//        InvocationResult result = null;
//        try {
//            invoker.execute(request);
//        } catch (MavenInvocationException e) {
//            e.printStackTrace();
//        }
//
//        if (result != null)
//            if (result.getExitCode() != 0) {
//                throw new IllegalStateException("Build failed.");
//            }
//
//    }

}
