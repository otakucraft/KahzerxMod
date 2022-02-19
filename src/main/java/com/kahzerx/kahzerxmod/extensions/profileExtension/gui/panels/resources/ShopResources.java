package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.resources;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.helpers.BitMapImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class ShopResources {
    public static Font US;
    public static Font US_SMALL;
    public static BitMapImage COIN;
    public static BitMapImage DEBRIS;
    public static BitMapImage DIA_BLOCK;
    public static BitMapImage DIA;
    public static BitMapImage NETH_BLOCK;
    public static BitMapImage NETH_INGOT;
    public static BitMapImage NETH_SCRAP;

    static {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            US = Font.createFont(Font.TRUETYPE_FONT, ShopResources.class.getClassLoader().getResource("assets/kahzerx/gui/balance/us.ttf").openStream());
            US = US.deriveFont(Font.BOLD, 45);
            ge.registerFont(US);
            US_SMALL = US.deriveFont(Font.PLAIN, 30);
            ge.registerFont(US_SMALL);
            COIN = new BitMapImage(ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/coin.png"))).setMatchSlow(true).scaledDimensions(100, 100).clearColors(34, 57, 58, -110).bake();
            DEBRIS = new BitMapImage(ImageIO.read(ShopResources.class.getClassLoader().getResource("assets/kahzerx/gui/balance/debris.png"))).setMatchSlow(true).scaledDimensions(70, 70).bake();
            DIA_BLOCK = new BitMapImage(ImageIO.read(ShopResources.class.getClassLoader().getResource("assets/kahzerx/gui/balance/diaBlock.png"))).setMatchSlow(true).scaledDimensions(70, 70).bake();
            DIA = new BitMapImage(ImageIO.read(ShopResources.class.getClassLoader().getResource("assets/kahzerx/gui/balance/dia.png"))).setMatchSlow(true).scaledDimensions(70, 70).bake();
            NETH_BLOCK = new BitMapImage(ImageIO.read(ShopResources.class.getClassLoader().getResource("assets/kahzerx/gui/balance/nethBlock.png"))).setMatchSlow(true).scaledDimensions(70, 70).bake();
            NETH_INGOT = new BitMapImage(ImageIO.read(ShopResources.class.getClassLoader().getResource("assets/kahzerx/gui/balance/nethIngot.png"))).setMatchSlow(true).scaledDimensions(70, 70).bake();
            NETH_SCRAP = new BitMapImage(ImageIO.read(ShopResources.class.getClassLoader().getResource("assets/kahzerx/gui/balance/nethScrap.png"))).setMatchSlow(true).scaledDimensions(70, 70).bake();
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void noop() { }
}
