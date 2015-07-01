package com.dev9.mvn.watcher;


import org.apache.maven.shared.invoker.InvocationOutputHandler;


public class RunnerOutputHandler implements InvocationOutputHandler {
    public void consumeLine(String line) {

        System.out.println(">" + line);
    }

}
