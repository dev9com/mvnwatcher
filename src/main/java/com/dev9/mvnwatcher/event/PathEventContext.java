package com.dev9.mvnwatcher.event;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/20/12
 * Time: 10:00 PM
 */

public interface PathEventContext {

    boolean isValid();

    Path getWatchedDirectory();

    List<PathEvent> getEvents();

}
