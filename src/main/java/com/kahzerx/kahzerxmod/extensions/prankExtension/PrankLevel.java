package com.kahzerx.kahzerxmod.extensions.prankExtension;

public enum PrankLevel {
    LEVEL0(""),
    LEVEL1("①"),
    LEVEL2("②"),
    LEVEL3("③"),
    LEVEL4("④"),
    LEVEL5("⑤");

    private final String identifier;

    PrankLevel(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
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

    public static int levelToID(PrankLevel level) {
        return switch (level) {
            case LEVEL0 -> 0;
            case LEVEL1 -> 1;
            case LEVEL2 -> 2;
            case LEVEL3 -> 3;
            case LEVEL4 -> 4;
            case LEVEL5 -> 5;
        };
    }
}
