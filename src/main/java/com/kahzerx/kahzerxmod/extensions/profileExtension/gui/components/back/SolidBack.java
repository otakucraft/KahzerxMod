package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.back;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.Renderer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.Component;

public class SolidBack extends Component {
    private final byte fillColor;
    private int x;
    private int y;
    private int width;
    private int height;
    public SolidBack(byte fillColor) {
        this.fillColor = fillColor;
    }

    public void setDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setBounds(x, y, x + width, y + height);
    }

    @Override
    public void render(GuiPlayer guiPlayer) {
        Renderer.fill(guiPlayer, x, y, width, height, fillColor);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
