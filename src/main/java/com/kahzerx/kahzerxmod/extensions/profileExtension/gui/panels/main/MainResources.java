package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.main;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.helpers.BitMapImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.kahzerx.kahzerxmod.extensions.profileExtension.gui.colors.ColorList.BLACK;

public class MainResources {
    public static BitMapImage COIN;
    public static BitMapImage SHOPS;
    public static BitMapImage EVENTS;
    public static BitMapImage TRANSFERS;
    public static BitMapImage WAYPOINTS;
    public static BitMapImage RENDER;
    public static BitMapImage CORNER_BR;
    public static BitMapImage CORNER_BL;
    public static BitMapImage CORNER_TR;
    public static BitMapImage CORNER_TL;
    public static BitMapImage BOX;

    static {
        try {
            BufferedImage cornerBR = ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/corner.png"));
            BufferedImage cornerBL = new BufferedImage(cornerBR.getWidth(), cornerBR.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < cornerBR.getHeight(); y++) {
                for (int lx = 0, rx = cornerBR.getWidth() - 1; lx < cornerBR.getWidth(); lx++, rx--) {
                    cornerBL.setRGB(rx, y, cornerBR.getRGB(lx, y));
                }
            }
            BufferedImage cornerTR = new BufferedImage(cornerBR.getWidth(), cornerBR.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < cornerBR.getHeight(); y++) {
                for (int lx = 0, rx = cornerBR.getWidth() - 1; lx < cornerBR.getWidth(); lx++, rx--) {
                    cornerTR.setRGB(lx, cornerBR.getHeight() - 1 - y, cornerBR.getRGB(lx, y));
                }
            }
            BufferedImage cornerTL = new BufferedImage(cornerBL.getWidth(), cornerBL.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < cornerBL.getHeight(); y++) {
                for (int lx = 0, rx = cornerBL.getWidth() - 1; lx < cornerBL.getWidth(); lx++, rx--) {
                    cornerTL.setRGB(lx, cornerBL.getHeight() - 1 - y, cornerBL.getRGB(lx, y));
                }
            }
            CORNER_BR = new BitMapImage(cornerBR).scaledDimensions(190, 190).forceColor(BLACK.getCode()).bake();
            CORNER_BL = new BitMapImage(cornerBL).scaledDimensions(190, 190).forceColor(BLACK.getCode()).bake();
            CORNER_TR = new BitMapImage(cornerTR).scaledDimensions(190, 190).forceColor(BLACK.getCode()).bake();
            CORNER_TL = new BitMapImage(cornerTL).scaledDimensions(190, 190).forceColor(BLACK.getCode()).bake();
            BOX = new BitMapImage(ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/box.png"))).scaledDimensions(170, 170).forceColor(BLACK.getCode()).bake();
            COIN = new BitMapImage(ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/coin.png"))).setMatchSlow(true).scaledDimensions(140, 140).clearColors(34, 57, 58, -110).bake();
            SHOPS = new BitMapImage(ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/shops.png"))).scaledDimensions(100, 100).bake();
            EVENTS = new BitMapImage(ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/events.png"))).scaledDimensions(100, 100).bake();
            TRANSFERS = new BitMapImage(ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/transfers.png"))).scaledDimensions(100, 100).bake();
            WAYPOINTS = new BitMapImage(ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/waypoints.png"))).scaledDimensions(100, 100).bake();
            RENDER = new BitMapImage(ImageIO.read(MainResources.class.getClassLoader().getResource("assets/kahzerx/gui/mainMenu/render.png"))).scaledDimensions(110, 110).bake();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void noop() { }
}
