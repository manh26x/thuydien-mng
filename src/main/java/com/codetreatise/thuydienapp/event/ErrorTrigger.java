package com.codetreatise.thuydienapp.event;

import java.util.Observable;

public class ErrorTrigger extends Observable {
    private ErrorTrigger () {
    }
    private static ErrorTrigger instance;

    public static ErrorTrigger getInstance() {
        if(instance == null) {
            instance = new ErrorTrigger();
        }
        return instance;
    }

    public void setChange() {
        super.setChanged();
    }

}
