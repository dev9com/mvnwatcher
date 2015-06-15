package com.dev9.mvnrunner;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;

public class Mvnrunner {

    public static void build() {

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("pom.xml"));
        request.setGoals(Collections.singletonList("install"));

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
