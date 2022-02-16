package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.balance;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.helpers.BitMapImage;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.main.MainResources;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class BalanceResources {
    public static Font US;
    public static BitMapImage COIN;

    static {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            US = Font.createFont(Font.TRUETYPE_FONT, BalanceResources.class.getClassLoader().getResource("assets/kahzerx/gui/balance/us.ttf").openStream());
            US = US.deriveFont(Font.BOLD, 50);
            ge.registerFont(US);
            COIN = new BitMapImage(ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/coin.png"))).setMatchSlow(true).scaledDimensions(100, 100).clearColors(34, 57, 58, -110).bake();
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void noop() { }
}
