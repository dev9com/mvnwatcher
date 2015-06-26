package com.dev9.mvnrunner;


import org.apache.maven.shared.invoker.InvocationOutputHandler;


public class RunnerOutputHandler implements InvocationOutputHandler {
    public void consumeLine(String line) {

        System.out.println(">" + line);
    }

}
