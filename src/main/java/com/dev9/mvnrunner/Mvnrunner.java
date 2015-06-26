package com.dev9.mvnrunner;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;

public class Mvnrunner {

    public static void build() {

        InvocationRequest request = new DefaultInvocationRequest();

        File base = new File("./src/test/resources/sample-project/");
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

    public static void main(String[] args) {
        build();
        build();
    }

}
