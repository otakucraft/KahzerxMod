package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.balance;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiBase;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.MapGui;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.back.ImageBack;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.back.SolidBack;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.buttons.TextButton;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.helpers.TextMapper;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.labels.TextLabel;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.main.GuiMain;
import net.minecraft.item.Items;

import java.awt.*;

import static com.kahzerx.kahzerxmod.extensions.profileExtension.gui.colors.ColorList.*;

public class GuiBalance extends GuiBase {
    private SolidBack mainBack;
    private TextButton closeButton;
    private TextButton backButton;
    private TextLabel title;
    private TextLabel balance;
    private ImageBack coinImage;
    private SolidBack strip1;
    private SolidBack strip2;
    private SolidBack strip3;
    private SolidBack strip4;
    private TextLabel exchanges;
    private ImageBack netheriteBlockImage;
    private ImageBack debrisImage;
    private ImageBack diaBlockImage;
    private ImageBack netheriteIngotImage;
    private ImageBack netheriteScrapImage;
    private ImageBack diaImage;
    private TextLabel netheriteBlockLabel;
    private TextLabel debrisLabel;
    private TextLabel diaBlockLabel;
    private TextLabel netheriteIngotLabel;
    private TextLabel scrapLabel;
    private TextLabel diaLabel;
    public GuiBalance() {
        mainBack = new SolidBack(LIGHT_YELLOW.getCode());
        closeButton = new TextButton(new TextMapper("Close", new Font("Times New Roman", Font.PLAIN, 30)), RED.getCode(), DARK_RED.getCode(), (byte) 84, (byte) 87);
        closeButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.closePanel());
        backButton = new TextButton(new TextMapper("Back", new Font("Times New Roman", Font.PLAIN, 30)), LIGHT_GRAY.getCode(), GRAY.getCode(), (byte) 84, (byte) 87);
        backButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.openGui(new GuiMain()));
        title = new TextLabel(new TextMapper(" TU BALANCE ", BalanceResources.US), DARK_GRAY.getCode());
        balance = new TextLabel(new TextMapper("", BalanceResources.US), DARK_GRAY.getCode());
        coinImage = new ImageBack(BalanceResources.COIN);
        strip1 = new SolidBack(ORANGE.getCode());
        strip2 = new SolidBack(ORANGE.getCode());
        strip3 = new SolidBack(ORANGE.getCode());
        strip4 = new SolidBack(ORANGE.getCode());
        exchanges = new TextLabel(new TextMapper("ITEMS INGRESADOS:", BalanceResources.US_SMALL), DARK_GRAY.getCode());
        netheriteBlockImage = new ImageBack(BalanceResources.NETH_BLOCK);
        debrisImage = new ImageBack(BalanceResources.DEBRIS);
        diaBlockImage = new ImageBack(BalanceResources.DIA_BLOCK);
        netheriteIngotImage = new ImageBack(BalanceResources.NETH_INGOT);
        netheriteScrapImage = new ImageBack(BalanceResources.NETH_SCRAP);
        diaImage = new ImageBack(BalanceResources.DIA);
        netheriteBlockLabel = new TextLabel(new TextMapper("", BalanceResources.US_SMALL), DARK_GRAY.getCode());
        debrisLabel = new TextLabel(new TextMapper("", BalanceResources.US_SMALL), DARK_GRAY.getCode());
        diaBlockLabel = new TextLabel(new TextMapper("", BalanceResources.US_SMALL), DARK_GRAY.getCode());
        netheriteIngotLabel = new TextLabel(new TextMapper("", BalanceResources.US_SMALL), DARK_GRAY.getCode());
        scrapLabel = new TextLabel(new TextMapper("", BalanceResources.US_SMALL), DARK_GRAY.getCode());
        diaLabel = new TextLabel(new TextMapper("", BalanceResources.US_SMALL), DARK_GRAY.getCode());

        addComponent(mainBack);
        addComponent(closeButton);
        addComponent(backButton);
        addComponent(title);
        addComponent(coinImage);
        addComponent(balance);
        addComponent(strip1);
        addComponent(strip2);
        addComponent(strip3);
        addComponent(strip4);
        addComponent(exchanges);
        addComponent(netheriteBlockImage);
        addComponent(debrisImage);
        addComponent(diaBlockImage);
        addComponent(netheriteIngotImage);
        addComponent(netheriteScrapImage);
        addComponent(diaImage);
        addComponent(netheriteBlockLabel);
        addComponent(debrisLabel);
        addComponent(diaBlockLabel);
        addComponent(netheriteIngotLabel);
        addComponent(scrapLabel);
        addComponent(diaLabel);
    }

    @Override
    public void render(GuiPlayer guiPlayer) {
        mainBack.setDimensions(0, 80, guiPlayer.getPanelPixelWidth(), guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        closeButton.setDimensions(guiPlayer.getPanelPixelWidth() - 100 - 10, 10, 100, 50);
        backButton.setDimensions(10, 10, 100, 50);

        title.setDimensions((guiPlayer.getPanelPixelWidth() / 2) - (title.getWidth() / 2), 80 + 40, title.getWidth(), title.getHeight());

        strip1.setDimensions(0, 80, 15, guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        strip2.setDimensions(30, 80, 15, guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        strip3.setDimensions(guiPlayer.getPanelPixelWidth() - 45, 80, 15, guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        strip4.setDimensions(guiPlayer.getPanelPixelWidth() - 15, 80, 15, guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);

        exchanges.setDimensions((guiPlayer.getPanelPixelWidth() / 2) - (exchanges.getWidth() / 2), 80 + 40 + title.getWidth() + 60 + 110, exchanges.getWidth(), exchanges.getHeight());

        netheriteBlockImage.setDimensions(guiPlayer.getPanelPixelWidth() / 3 - (guiPlayer.getPanelPixelWidth() / 3 - 60), 80 + 40 + title.getHeight() + 60 + 180, netheriteBlockImage.getImage().getWidth(), netheriteBlockImage.getHeight());
        debrisImage.setDimensions(guiPlayer.getPanelPixelWidth() / 3 * 2 - (guiPlayer.getPanelPixelWidth() / 3 - 30), 80 + 40 + title.getHeight() + 60 + 180, debrisImage.getImage().getWidth(), debrisImage.getHeight());
        diaBlockImage.setDimensions( guiPlayer.getPanelPixelWidth() - (guiPlayer.getPanelPixelWidth() / 3 + 10), 80 + 40 + title.getHeight() + 60 + 180, diaBlockImage.getImage().getWidth(), diaBlockImage.getHeight());
        netheriteIngotImage.setDimensions(guiPlayer.getPanelPixelWidth() / 3 - (guiPlayer.getPanelPixelWidth() / 3 - 60), 80 + 40 + title.getHeight() + 60 + 270, netheriteIngotImage.getImage().getWidth(), netheriteIngotImage.getHeight());
        netheriteScrapImage.setDimensions(guiPlayer.getPanelPixelWidth() / 3 * 2 - (guiPlayer.getPanelPixelWidth() / 3 - 30), 80 + 40 + title.getHeight() + 60 + 270, netheriteScrapImage.getImage().getWidth(), netheriteScrapImage.getHeight());
        diaImage.setDimensions(guiPlayer.getPanelPixelWidth() - (guiPlayer.getPanelPixelWidth() / 3 + 10), 80 + 40 + title.getHeight() + 60 + 270, diaImage.getImage().getWidth(), diaImage.getHeight());

        updateBalance(guiPlayer);
    }

    @Override
    public void onMouseChange(GuiPlayer guiPlayer, int newX, int newY, int oldX, int oldY) {
        updateBalance(guiPlayer);
        super.onMouseChange(guiPlayer, newX, newY, oldX, oldY);
    }

    private void updateBalance(GuiPlayer guiPlayer) {
        balance.setText(new TextMapper(String.valueOf(guiPlayer.getShopExtension().getBalance(guiPlayer.getPlayer())), BalanceResources.US));
        balance.setDimensions(guiPlayer.getPanelPixelWidth() / 2 - balance.getText().getWidth() / 2, 80 + 40 + title.getHeight() + 60, balance.getText().getWidth(), balance.getText().getHeight());
        coinImage.setDimensions((guiPlayer.getPanelPixelWidth() / 2 - balance.getText().getWidth() / 2) - coinImage.getImage().getWidth(), 80 + 40 + title.getHeight() + 40, coinImage.getImage().getWidth(), coinImage.getImage().getHeight());

        int nethBlockCount = guiPlayer.getShopExtension().getAlreadyExchangedItem(guiPlayer.getPlayer(), Items.NETHERITE_BLOCK);
        setLabel(nethBlockCount, netheriteBlockLabel, netheriteBlockImage);
        int debrisCount = guiPlayer.getShopExtension().getAlreadyExchangedItem(guiPlayer.getPlayer(), Items.ANCIENT_DEBRIS);
        setLabel(debrisCount, debrisLabel, debrisImage);
        int diaBlockCount = guiPlayer.getShopExtension().getAlreadyExchangedItem(guiPlayer.getPlayer(), Items.DIAMOND_BLOCK);
        setLabel(diaBlockCount, diaBlockLabel, diaBlockImage);
        int netheriteIngotCount = guiPlayer.getShopExtension().getAlreadyExchangedItem(guiPlayer.getPlayer(), Items.NETHERITE_INGOT);
        setLabel(netheriteIngotCount, netheriteIngotLabel, netheriteIngotImage);
        int scrapCount = guiPlayer.getShopExtension().getAlreadyExchangedItem(guiPlayer.getPlayer(), Items.NETHERITE_SCRAP);
        setLabel(scrapCount, scrapLabel, netheriteScrapImage);
        int diaCount = guiPlayer.getShopExtension().getAlreadyExchangedItem(guiPlayer.getPlayer(), Items.DIAMOND);
        setLabel(diaCount, diaLabel, diaImage);

        super.render(guiPlayer);
    }

    private void setLabel(int count, TextLabel label, ImageBack image) {
        label.setText(new TextMapper(String.valueOf(count == -1 ? 0 : count), BalanceResources.US_SMALL));
        label.setDimensions(image.getX() + image.getWidth() + 5, image.getY() - (image.getImage().getHeight() / 4), label.getText().getWidth(), label.getText().getHeight());
    }
}
