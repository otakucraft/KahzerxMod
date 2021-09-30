package com.kahzerx.kahzerxmod.extensions.permsExtension;

public enum PermsLevels {
    MEMBER(1),
    MOD(2);

    private final int id;

    PermsLevels(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static PermsLevels getValue(int l) {
        if (l == 2) {
            return MOD;
        }
        return MEMBER;
    }
}
