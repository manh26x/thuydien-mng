package com.codetreatise.thuydienapp.event;


import java.util.Observable;

public class EventTrigger extends Observable{

    private EventTrigger() {

    }

    private static EventTrigger instance;

    public static EventTrigger getInstance() {
        if(instance == null) {
            instance = new EventTrigger();
        }
        return instance;
    }

    public void setChange() {
        super.setChanged();
    }
}
