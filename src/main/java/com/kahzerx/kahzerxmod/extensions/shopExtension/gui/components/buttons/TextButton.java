package com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.buttons;

import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.Renderer;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.Component;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.helpers.TextMapper;

public class TextButton extends Component {
    private TextMapper text;
    private byte fillColor;
    private byte hoverColor;
    private byte textColor;
    private byte hoverTextColor;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean textHidden = false;
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
        if (!textHidden) {
            Renderer.drawText(guiPlayer, text, x + width / 2 - text.getWidth() / 2, y + height / 2 - text.getHeight() / 2, color);
        }
    }

    @Override
    public void onMouseOver(GuiPlayer guiPlayer, int x, int y) {
        setReRender(true);
    }

    @Override
    public void onMouseOut(GuiPlayer guiPlayer, int x, int y) {
        setReRender(true);
    }
}
