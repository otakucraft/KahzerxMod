package com.kahzerx.kahzerxmod.extensions.blockInfoExtension.helpers;

public enum ActionTypes {
    BREAK(0),
    PLACE(1),
    INTERACT(2),
    ADDED(3),
    REMOVED(4);

    private int id;
    ActionTypes(int actionType) {
        this.id = actionType;
    }

    public int getId() {
        return id;
    }
}
