package com.kahzerx.kahzerxmod.extensions.badgeExtension;

public class BadgeInstance {
    private final String badge;
    private final int colorIndex;
    private final String description;
    public BadgeInstance(String badge, int colorIndex, String description) {
        this.badge = badge;
        this.colorIndex = colorIndex;
        this.description = description;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public String getBadge() {
        return badge;
    }

    public String getDescription() {
        return description;
    }
}
