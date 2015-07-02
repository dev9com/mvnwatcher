package com.dev9.mvnwatcher;


import org.apache.maven.shared.invoker.InvocationOutputHandler;


public class RunnerErrorHandler implements InvocationOutputHandler {
    public void consumeLine(String line) {
        System.err.println("!>" + line);
    }
}
