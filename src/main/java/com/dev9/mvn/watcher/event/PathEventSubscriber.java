package com.dev9.mvn.watcher.event;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/20/12
 * Time: 12:44 PM
 */


public interface PathEventSubscriber {

    void handlePathEvents(PathEventContext pathEventContext);
}
