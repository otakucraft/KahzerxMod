package com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components;

import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.GuiBase;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.callbacks.ClickCallback;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.shapes.SimpleRect;

public class Component extends GuiBase {
    private SimpleRect bounds = new SimpleRect(0, 0, 0, 0);
    private boolean isMouseOver = false;
    private ClickCallback clickCallback = null;

    public void setBounds(int minX, int minY, int maxX, int maxY) {
        bounds.set(minX, minY, maxX, maxY);
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public boolean isMouseOnComponent(int x, int y) {
        return bounds.has(x, y);
    }

    @Override
    public void onMouseChange(GuiPlayer guiPlayer, int newX, int newY, int oldX, int oldY) {
        if (isMouseOnComponent(newX, newY)) {
            if (!isMouseOver) {
                isMouseOver = true;
                onMouseOver(guiPlayer, newX, newY);
            }
        } else {
            if (isMouseOver) {
                isMouseOver = false;
                onMouseOut(guiPlayer, newX, newY);
            }
        }
        super.onMouseChange(guiPlayer, newX, newY, oldX, oldY);
    }

    public void onMouseEnter(GuiPlayer guiPlayer, int x, int y) { }

    public void onMouseExit(GuiPlayer guiPlayer, int x, int y) { }

    public void onMouseOver(GuiPlayer guiPlayer, int x, int y) { }

    public void onMouseOut(GuiPlayer guiPlayer, int x, int y) { }

    public void onClick(boolean isKey, GuiPlayer p) {
        if (clickCallback != null) {
            clickCallback.run(isKey, p);
        }
        super.onClick(isKey, p);
    }

    public boolean isMouseOver() {
        return isMouseOver;
    }
}
