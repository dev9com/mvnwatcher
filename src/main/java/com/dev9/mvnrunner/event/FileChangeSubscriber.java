package com.dev9.mvnrunner.event;

import com.google.common.eventbus.Subscribe;

/**
 * Created by wiverson on 6/26/15.
 */
public class FileChangeSubscriber implements PathEventSubscriber {


    @Subscribe
    @Override
    public void handlePathEvents(PathEventContext pathEventContext) {

        System.out.println("+");

        for (PathEvent evt : pathEventContext.getEvents()) {
            System.out.println("@>" + evt.getEventTarget().getFileName());
        }
    }
}
