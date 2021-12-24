package com.kahzerx.kahzerxmod.extensions.prankExtension;

import net.minecraft.util.Formatting;

public enum PrankLevel {
    LEVEL0("", Formatting.RESET, 0),
    LEVEL1("①", Formatting.GREEN, 1),
    LEVEL2("②", Formatting.YELLOW, 2),
    LEVEL3("③", Formatting.GOLD, 3),
    LEVEL4("④", Formatting.RED, 4),
    LEVEL5("⑤", Formatting.DARK_RED, 5);

    private final String identifier;
    private final Formatting formatting;
    private final int ID;

    PrankLevel(String identifier, Formatting formatting, int ID) {
        this.identifier = identifier;
        this.formatting = formatting;
        this.ID = ID;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getID() {
        return ID;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    public static PrankLevel idToLevel(int i) {
        return switch (i) {
            case 1 -> LEVEL1;
            case 2 -> LEVEL2;
            case 3 -> LEVEL3;
            case 4 -> LEVEL4;
            case 5 -> LEVEL5;
            default -> LEVEL0;
        };
    }
}
