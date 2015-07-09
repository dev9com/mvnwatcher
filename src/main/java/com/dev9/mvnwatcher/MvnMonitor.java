package com.dev9.mvnwatcher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

class MvnMonitor implements Runnable {

    private List<ProcessBuilder> config;

    private Process watchedProcess;

    public boolean dirty = true;

    public boolean shutdown = false;

    private MvnSystemNotifications notifier;

    public MvnMonitor(List<ProcessBuilder> config) {
        this.config = config;
    }

    /**
     * Returns true if sync builds are successful
     */
    private boolean execSyncBuilds() {


        for (int i = 0; i < config.size() - 1; i++) {

            ProcessBuilder pb = config.get(i);

            notifier.update("Directory for build step " + i + " : " + pb.directory().getAbsolutePath() + "...",
                    MvnSystemNotifications.Status.WORKING);

            notifier.update("Starting " + pb.command().get(0) + "...",
                    MvnSystemNotifications.Status.WORKING);

            Process p;
            try {
                p = pb.start();
            } catch (IOException e1) {
                notifier.update(e1.getLocalizedMessage(), MvnSystemNotifications.Status.FAIL, e1);
                return false;
            }

            try {
                // This waits for UP TO 10 seconds.
                p.waitFor(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // For now, swallow interruption exceptions..?
            } finally {
                if (p.isAlive()) {
                    notifier.update("Destroying Forcibly " + pb.command().get(0), MvnSystemNotifications.Status.FAIL);
                    p.destroyForcibly();
                    return false;
                }
            }

            // Doh, failed build!
            if (p.exitValue() != 0) {
                notifier.update("Prep build failed with exit code " + p.exitValue(), MvnSystemNotifications.Status.FAIL);
                return false;
            }
        }

        return true;
    }

    private void execMainBuild() {

        ProcessBuilder finalConfig = config.get((config.size() - 1));

        notifier.update("Directory for monitored build : " + finalConfig.directory().getAbsolutePath() + "...",
                MvnSystemNotifications.Status.WORKING);

        try {

            if (watchedProcess == null) {
                notifier.update("Starting process (a) " + finalConfig.command().get(0) + "...",
                        MvnSystemNotifications.Status.WORKING);
                watchedProcess = finalConfig.start();
                notifier.update("Ready",
                        MvnSystemNotifications.Status.OK);

            } else if (!watchedProcess.isAlive()) {
                notifier.update("Starting process (b) " + finalConfig.command().get(0) + "...",
                        MvnSystemNotifications.Status.WORKING);
                watchedProcess = finalConfig.start();
                notifier.update("Ready",
                        MvnSystemNotifications.Status.OK);
            }
        } catch (IOException e1) {
            notifier.update(e1.getLocalizedMessage(),
                    MvnSystemNotifications.Status.FAIL, e1);
        }
    }

    @Override
    public void run() {

        notifier = new MvnSystemNotifications(this);
        notifier.init(true);

        while (!shutdown) {

            if (dirty) {
                dirty = false;

                kill();

                if (execSyncBuilds()) {
                    execMainBuild();
                }
            }

            if (watchedProcess != null)
                if (watchedProcess.isAlive()) {
                    {
                        try {
                            watchedProcess.waitFor(500, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            // Swallowing interruption exceptions for now.
                        }


                    }
                } else {
                    if (watchedProcess.exitValue() != 0)
                        notifier.update("Monitored build no longer running.", MvnSystemNotifications.Status.FAIL);
                }

        }

        kill();
    }


    public void kill() {


        if (watchedProcess != null)
            if (watchedProcess.isAlive()) {
                notifier.update("Terminating existing build...",
                        MvnSystemNotifications.Status.WORKING);
                watchedProcess.destroyForcibly();
                System.out.println("Killed " + watchedProcess.toString());
            }

    }

    public String status() {
        return notifier.status();
    }
}
