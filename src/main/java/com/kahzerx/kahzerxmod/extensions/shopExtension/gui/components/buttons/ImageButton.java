package com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.buttons;

import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.Renderer;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.Component;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.helpers.BitMapImage;

public class ImageButton extends Component {
    private BitMapImage image;
    private int x;
    private int y;
    private int width;
    private int height;
    public ImageButton(BitMapImage image) {
        this.image = image;
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
        Renderer.drawImage(guiPlayer, image, x + width / 2 - image.getWidth() / 2, y + height / 2 - image.getHeight() / 2);
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
