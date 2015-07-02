package com.dev9.mvnwatcher.event;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/15/12
 * Time: 10:29 PM
 */

public interface DirectoryEventWatcher {
    void start() throws IOException;

    boolean isRunning();

    void stop();

}
