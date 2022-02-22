package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.buttons;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.Renderer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.Component;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.helpers.TextMapper;

public class TextButton extends Component {
    private final TextMapper text;
    private final byte fillColor;
    private final byte hoverColor;
    private final byte textColor;
    private final byte hoverTextColor;
    private int x;
    private int y;
    private int width;
    private int height;
    public TextButton(TextMapper text, byte fillColor, byte hoverColor, byte textColor, byte hoverTextColor) {
        this.text = text;
        this.fillColor = fillColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        this.hoverTextColor = hoverTextColor;
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
        byte backColor = isMouseOver() ? hoverColor : fillColor;
        byte color = isMouseOver() ? hoverTextColor : textColor;
        Renderer.fill(guiPlayer, x, y, width, height, backColor);
        Renderer.drawText(guiPlayer, text, x + width / 2 - text.getWidth() / 2, y + height / 2 - text.getHeight() / 2, color);
    }

    @Override
    public void onMouseOver(GuiPlayer guiPlayer, int x, int y) {
        setReRender(true);
    }

    @Override
    public void onMouseOut(GuiPlayer guiPlayer, int x, int y) {
        setReRender(true);
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
