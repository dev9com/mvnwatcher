package com.dev9.mvnwatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private String executable;
    private List<String> arguments;
    private File outputFile;

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public List<String> toArgList()
    {
        List<String> result = new ArrayList<>();

        result.add(executable);
        result.addAll(arguments);

        return result;
    }

}
