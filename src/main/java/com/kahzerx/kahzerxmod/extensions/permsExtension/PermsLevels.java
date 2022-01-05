package com.kahzerx.kahzerxmod.extensions.permsExtension;

import java.util.Locale;

public enum PermsLevels {
    MEMBER(1),
    HELPER(2),
    MOD(3);

    private final int id;

    PermsLevels(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static PermsLevels getValue(int l) {
        return switch (l) {
            case 2 -> HELPER;
            case 3 -> MOD;
            default -> MEMBER;
        };
    }

    public static String[] permNames() {
        return new String[]{"MEMBER", "HELPER", "MOD"};
    }

    public static int getLevel(String name) {
        String upName = name.toUpperCase(Locale.ROOT);
        return switch (upName) {
            case "MEMBER" -> 1;
            case "HELPER" -> 2;
            case "MOD" -> 3;
            default -> -1;
        };
    }
}
