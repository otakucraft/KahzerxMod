package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.back;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.Renderer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.Component;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.helpers.BitMapImage;

public class ImageBack extends Component {
    private final BitMapImage image;
    private int x;
    private int y;
    private int width;
    private int height;
    public ImageBack(BitMapImage image) {
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

    public BitMapImage getImage() {
        return image;
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
