package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.main;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiBase;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.MapGui;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.back.ImageBack;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.back.SolidBack;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.buttons.ImageButton;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.buttons.TextButton;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.helpers.TextMapper;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.labels.TextLabel;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.balance.GuiBalance;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.awt.*;

import static com.kahzerx.kahzerxmod.extensions.profileExtension.gui.colors.ColorList.*;

public class GuiMain extends GuiBase {
    private SolidBack mainBack;
    private TextButton closeButton;
    private ImageBack cornerBR;
    private ImageBack cornerBL;
    private ImageBack cornerTR;
    private ImageBack cornerTL;
    private ImageBack boxTL;
    private ImageBack boxTC;
    private ImageBack boxTR;
    private ImageBack boxBL;
    private ImageBack boxBC;
    private ImageBack boxBR;
    private ImageButton coinImage;
    private TextLabel coinText;
    private ImageButton shopsImage;
    private TextLabel shopsText;
    private ImageButton eventsImage;
    private TextLabel eventsText;
    private ImageButton transfersImage;
    private TextLabel transfersText;
    private ImageButton waypointsImage;
    private TextLabel waypointsText;
    private ImageButton renderImage;
    private TextLabel renderText;
    public GuiMain() {
        mainBack = new SolidBack(LIGHT_GRAY.getCode());
        closeButton = new TextButton(new TextMapper("Close", new Font("Times New Roman", Font.PLAIN, 30)), RED.getCode(), DARK_RED.getCode(), (byte) 84, (byte) 87);
        closeButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.closePanel());
        cornerBR = new ImageBack(MainResources.CORNER_BR);
        cornerBL = new ImageBack(MainResources.CORNER_BL);
        cornerTR = new ImageBack(MainResources.CORNER_TR);
        cornerTL = new ImageBack(MainResources.CORNER_TL);
        boxTL = new ImageBack(MainResources.BOX);
        boxTC = new ImageBack(MainResources.BOX);
        boxTR = new ImageBack(MainResources.BOX);
        boxBL = new ImageBack(MainResources.BOX);
        boxBC = new ImageBack(MainResources.BOX);
        boxBR = new ImageBack(MainResources.BOX);
        coinImage = new ImageButton(MainResources.COIN);
        coinImage.setClickCallback((boolean isKey, GuiPlayer p) -> {
            if (p.getShopExtension().extensionSettings().isEnabled()) {
                p.openGui(new GuiBalance());
            } else {
                p.getPlayer().networkHandler.sendPacket(new OverlayMessageS2CPacket(new LiteralText("Shops extension is not enabled!")));
            }
        });
        coinText = new TextLabel(new TextMapper("Balance", new Font("Times New Roman", Font.BOLD, 20)), BLACK.getCode());
        shopsImage = new ImageButton(MainResources.SHOPS);
        shopsImage.setClickCallback((boolean isKey, GuiPlayer p) -> System.out.println("openShopsGui"));
        shopsText = new TextLabel(new TextMapper("Tiendas", new Font("Times New Roman", Font.BOLD, 20)), BLACK.getCode());
        eventsImage = new ImageButton(MainResources.EVENTS);
        eventsImage.setClickCallback((boolean isKey, GuiPlayer p) -> System.out.println("openEventsGui"));
        eventsText = new TextLabel(new TextMapper("Eventos", new Font("Times New Roman", Font.BOLD, 20)), BLACK.getCode());
        transfersImage = new ImageButton(MainResources.TRANSFERS);
        transfersImage.setClickCallback((boolean isKey, GuiPlayer p) -> System.out.println("openTransfersGui"));
        transfersText = new TextLabel(new TextMapper("Transferencias", new Font("Times New Roman", Font.BOLD, 20)), BLACK.getCode());
        waypointsImage = new ImageButton(MainResources.WAYPOINTS);
        waypointsImage.setClickCallback((boolean isKey, GuiPlayer p) -> System.out.println("openWaypointsGui"));
        waypointsText = new TextLabel(new TextMapper("Waypoints", new Font("Times New Roman", Font.BOLD, 20)), BLACK.getCode());
        renderImage = new ImageButton(MainResources.RENDER);
        renderImage.setClickCallback((boolean isKey, GuiPlayer p) -> p.getPlayer().sendMessage(MarkEnum.INFO.appendMessage("Puedes encontrar el render del mundo aquí: ").append(new LiteralText("https://maps.kahzerx.com/otakucraft/otakucraft3/").styled(style -> style.withColor(Formatting.GRAY).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://maps.kahzerx.com/otakucraft/otakucraft3/")))), false));
        renderText = new TextLabel(new TextMapper("Render", new Font("Times New Roman", Font.BOLD, 20)), BLACK.getCode());

        addComponent(mainBack);
        addComponent(closeButton);
        addComponent(cornerBR);
        addComponent(cornerBL);
        addComponent(cornerTR);
        addComponent(cornerTL);
        addComponent(boxTL);
        addComponent(boxTC);
        addComponent(boxTR);
        addComponent(boxBL);
        addComponent(boxBC);
        addComponent(boxBR);
        addComponent(coinImage);
        addComponent(coinText);
        addComponent(shopsImage);
        addComponent(shopsText);
        addComponent(eventsImage);
        addComponent(eventsText);
        addComponent(transfersImage);
        addComponent(transfersText);
        addComponent(waypointsImage);
        addComponent(waypointsText);
        addComponent(renderImage);
        addComponent(renderText);
    }

    @Override
    public void render(GuiPlayer guiPlayer) {
        mainBack.setDimensions(0, 80, guiPlayer.getPanelPixelWidth(), guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        closeButton.setDimensions(guiPlayer.getPanelPixelWidth() - 100 - 10, 10, 100, 50);

        cornerBR.setDimensions(guiPlayer.getPanelPixelWidth() - MainResources.CORNER_BR.getWidth(), guiPlayer.getPanelPixelHeight() - MapGui.MAP_HEIGHT - MainResources.CORNER_BR.getHeight() + 1, MainResources.CORNER_BR.getWidth(), MainResources.CORNER_BR.getHeight());
        cornerBL.setDimensions(0, guiPlayer.getPanelPixelHeight() - MapGui.MAP_HEIGHT - MainResources.CORNER_BR.getHeight() + 1, MainResources.CORNER_BR.getWidth(), MainResources.CORNER_BR.getHeight());
        cornerTR.setDimensions(guiPlayer.getPanelPixelWidth() - MainResources.CORNER_BR.getWidth(), 80 - 1, MainResources.CORNER_BR.getWidth(), MainResources.CORNER_BR.getHeight());
        cornerTL.setDimensions(0, 80 - 1, MainResources.CORNER_BR.getWidth(), MainResources.CORNER_BR.getHeight());

        boxTC.setDimensions((guiPlayer.getPanelPixelWidth() / 2) - (MainResources.BOX.getWidth() / 2), ((guiPlayer.getPanelPixelHeight() - MapGui.MAP_HEIGHT - 80) / 2) - ((MainResources.BOX.getHeight() * 2 + 15) / 2) + 80, MainResources.BOX.getWidth(), MainResources.BOX.getHeight());
        boxTL.setDimensions(boxTC.getX() - (MainResources.BOX.getWidth() + 15), boxTC.getY(), boxTC.getWidth(), boxTC.getHeight());
        boxTR.setDimensions(boxTC.getX() + (MainResources.BOX.getWidth() + 15), boxTC.getY(), boxTC.getWidth(), boxTC.getHeight());
        boxBL.setDimensions(boxTL.getX(), boxTL.getY() + MainResources.BOX.getHeight() + 15, boxTC.getWidth(), boxTC.getHeight());
        boxBC.setDimensions(boxTC.getX(), boxBL.getY(), boxTC.getWidth(), boxTC.getHeight());
        boxBR.setDimensions(boxTR.getX(), boxBL.getY(), boxTC.getWidth(), boxTC.getHeight());

        coinImage.setDimensions(boxTL.getX() + (boxTL.getWidth() / 2) - (MainResources.COIN.getWidth() / 2), boxTL.getY() + (boxTL.getHeight() / 2) - (MainResources.COIN.getHeight() / 2), MainResources.COIN.getWidth(), MainResources.COIN.getHeight());
        coinText.setDimensions(boxTL.getX() + (boxTL.getWidth() / 2) - (coinText.getWidth() / 2), boxTL.getY() + boxTL.getHeight(), coinText.getWidth(), coinText.getHeight());

        shopsImage.setDimensions(boxTC.getX() + (boxTC.getWidth() / 2) - (MainResources.SHOPS.getWidth() / 2), boxTC.getY() + (boxTC.getHeight() / 2) - (MainResources.SHOPS.getHeight() / 2), MainResources.SHOPS.getWidth(), MainResources.SHOPS.getHeight());
        shopsText.setDimensions(boxTC.getX() + (boxTC.getWidth() / 2) - (shopsText.getWidth() / 2), boxTC.getY() + boxTC.getHeight(), shopsText.getWidth(), shopsText.getHeight());

        eventsImage.setDimensions(boxTR.getX() + (boxTR.getWidth() / 2) - (MainResources.EVENTS.getWidth() / 2), boxTR.getY() + (boxTR.getHeight() / 2) - (MainResources.EVENTS.getHeight() / 2), MainResources.EVENTS.getWidth(), MainResources.EVENTS.getHeight());
        eventsText.setDimensions(boxTR.getX() + (boxTR.getWidth() / 2) - (eventsText.getWidth() / 2), boxTR.getY() + boxTR.getHeight(), eventsText.getWidth(), eventsText.getHeight());

        transfersImage.setDimensions(boxBL.getX() + (boxBL.getWidth() / 2) - (MainResources.TRANSFERS.getWidth() / 2), boxBL.getY() + (boxBL.getHeight() / 2) - (MainResources.TRANSFERS.getHeight() / 2), MainResources.TRANSFERS.getWidth(), MainResources.TRANSFERS.getHeight());
        transfersText.setDimensions(boxBL.getX() + (boxBL.getWidth() / 2) - (transfersText.getWidth() / 2), boxBL.getY() + boxBL.getHeight(), transfersText.getWidth(), transfersText.getHeight());

        waypointsImage.setDimensions(boxBC.getX() + (boxBC.getWidth() / 2) - (MainResources.WAYPOINTS.getWidth() / 2), boxBC.getY() + (boxBC.getHeight() / 2) - (MainResources.WAYPOINTS.getHeight() / 2), MainResources.WAYPOINTS.getWidth(), MainResources.WAYPOINTS.getHeight());
        waypointsText.setDimensions(boxBC.getX() + (boxBC.getWidth() / 2) - (waypointsText.getWidth() / 2), boxBC.getY() + boxBC.getHeight(), waypointsText.getWidth(), waypointsText.getHeight());

        renderImage.setDimensions(boxBR.getX() + (boxBR.getWidth() / 2) - (MainResources.RENDER.getWidth() / 2), boxBR.getY() + (boxBR.getHeight() / 2) - (MainResources.RENDER.getHeight() / 2), MainResources.RENDER.getWidth(), MainResources.RENDER.getHeight());
        renderText.setDimensions(boxBR.getX() + (boxBR.getWidth() / 2) - (renderText.getWidth() / 2), boxBR.getY() + boxBR.getHeight(), renderText.getWidth(), renderText.getHeight());

        super.render(guiPlayer);
    }

    @Override
    public void onMouseChange(GuiPlayer guiPlayer, int newX, int newY, int oldX, int oldY) {
        super.onMouseChange(guiPlayer, newX, newY, oldX, oldY);
    }
}
