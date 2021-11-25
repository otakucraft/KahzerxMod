package com.kahzerx.kahzerxmod.extensions.permsExtension;

import java.util.Locale;

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

    public static String[] permNames() {
        return new String[]{"MEMBER", "MOD"};
    }

    public static int getLevel(String name) {
        String upName = name.toUpperCase(Locale.ROOT);
        return switch (upName) {
            case "MEMBER" -> 1;
            case "MOD" -> 2;
            default -> -1;
        };
    }
}
