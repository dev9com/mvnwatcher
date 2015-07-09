package com.dev9.mvnwatcher;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private String executable;
    private List<String> arguments;
    private File outputFile;
    private Path executableDirectory;

    public Task(String executable, List<String> arguments, File outputFile, Path executableDirectory) {
        this.executable = executable;
        this.arguments = arguments;
        this.outputFile = outputFile;
        this.executableDirectory = executableDirectory;



        if (!Files.exists(Paths.get(outputFile.getParent())))
            throw new IllegalArgumentException("Can't find " + outputFile.getParent());

        if (!executableDirectory.toFile().exists())
            throw new IllegalArgumentException("Can't find " + executableDirectory.toAbsolutePath());
    }

    public File getOutputFile() {
        return outputFile;
    }


    public String getExecutable() {
        return executable;
    }


    public List<String> getArguments() {
        return arguments;
    }

    public List<String> toArgList() {
        List<String> result = new ArrayList<>();

        result.add(executable);
        result.addAll(arguments);

        return result;
    }

    public Path getExecutableDirectory() {
        return executableDirectory;
    }


    public ProcessBuilder toProcessBuilder() {
        ProcessBuilder b = new ProcessBuilder(toArgList());
        b.directory(executableDirectory.toFile());

        b.redirectErrorStream(true);
        b.redirectOutput(ProcessBuilder.Redirect.appendTo(outputFile));

        return b;
    }

}
