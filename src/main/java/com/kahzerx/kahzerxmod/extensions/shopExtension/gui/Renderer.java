package com.kahzerx.kahzerxmod.extensions.shopExtension.gui;

import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.helpers.TextMapper;

import java.util.ArrayList;

public class Renderer {
    public static void clear(GuiPlayer guiPlayer) {
        fill(guiPlayer, (byte) 0);
    }

    public static void fill(GuiPlayer guiPlayer, byte color) {
        fill(guiPlayer, 0, 0, guiPlayer.getPanelPixelWidth(), guiPlayer.getPanelPixelHeight(), color);
    }

    public static void fill(GuiPlayer guiPlayer, int x, int y, int width, int height, byte color) {
        for (int x2 = x; x2 < x + width; x2++) {
            for (int y2 = y; y2 < y + height; y2++) {
                guiPlayer.setPixel(x2, y2, color);
            }
        }
    }

    public static void drawText(GuiPlayer guiPlayer, TextMapper text, int x, int y, byte color) {
        ArrayList<Integer> m = text.getBitmask();
        for (int i : m) {
            int mx = i % text.getWidth();
            int my = i / text.getWidth();
            guiPlayer.setPixel(x + mx, y + my, color);
        }
    }
}
