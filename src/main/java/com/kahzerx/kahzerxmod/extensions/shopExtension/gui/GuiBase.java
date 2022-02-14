package com.kahzerx.kahzerxmod.extensions.shopExtension.gui;

import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.Component;

import java.util.ArrayList;

public abstract class GuiBase {
    private boolean reRender = true;
    protected ArrayList<Component> components = new ArrayList<>();

    public void onMouseChange(GuiPlayer guiPlayer, int newX, int newY, int oldX, int oldY) {
        for (Component component : components) {
            component.onMouseChange(guiPlayer, newX, newY, oldX, oldY);
        }
    }

    public void addComponent(Component c) {
        components.add(c);
    }

    public void onClose(GuiPlayer guiPlayer) { }

    public void onOpen(GuiPlayer guiPlayer) {
        this.setReRender(true);
    }

    public void onClick(boolean isKey, GuiPlayer p) {
        for (Component c : components) {
            if (c.isMouseOver()) {
                c.onClick(isKey, p);
            }
        }
    }

    public void setReRender(boolean reRender) {
        this.reRender = reRender;
    }

    public boolean shouldRender(GuiPlayer guiPlayer) {
        for (Component c : components) {
            if (c.shouldRender(guiPlayer)) {
                return true;
            }
        }
        return reRender;
    }

    public void render(GuiPlayer guiPlayer) {
        for (Component c : components) {
            c.render(guiPlayer);
            c.setReRender(false);
        }
    }
}
