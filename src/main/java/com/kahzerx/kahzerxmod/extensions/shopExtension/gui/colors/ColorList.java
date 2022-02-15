package com.kahzerx.kahzerxmod.extensions.shopExtension.gui.colors;

public enum ColorList {
    RED(ColorMatcher.getBestColor(220, 0, 0, 255, true)),
    DARK_RED(ColorMatcher.getBestColor(190, 0, 0, 255, true)),
    LIGHT_GRAY(ColorMatcher.getBestColor(128, 128, 128, 255, true)),
    WHITE(ColorMatcher.getBestColor(255, 255, 255, 255, true)),
    BLACK(ColorMatcher.getBestColor(0, 0, 0, 255, true));

    public byte code;
    ColorList(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
