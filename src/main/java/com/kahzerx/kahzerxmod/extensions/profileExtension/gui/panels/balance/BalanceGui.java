package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.balance;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiBase;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.MapGui;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.back.ImageBack;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.back.SolidBack;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.buttons.TextButton;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.helpers.TextMapper;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.labels.TextLabel;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.main.MainGui;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.resources.ShopResources;

import java.awt.*;

import static com.kahzerx.kahzerxmod.extensions.profileExtension.gui.colors.ColorList.*;

public class BalanceGui extends GuiBase {
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
    private ImageBack scrapImage;
    private ImageBack diaImage;
    private TextLabel netheriteBlockLabel;
    private TextLabel debrisLabel;
    private TextLabel diaBlockLabel;
    private TextLabel netheriteIngotLabel;
    private TextLabel scrapLabel;
    private TextLabel diaLabel;
    public BalanceGui() {
        mainBack = new SolidBack(LIGHT_YELLOW.getCode());
        closeButton = new TextButton(new TextMapper("Close", new Font("Times New Roman", Font.PLAIN, 30)), RED.getCode(), DARK_RED.getCode(), (byte) 84, (byte) 87);
        closeButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.closePanel());
        backButton = new TextButton(new TextMapper("Back", new Font("Times New Roman", Font.PLAIN, 30)), LIGHT_GRAY.getCode(), GRAY.getCode(), (byte) 84, (byte) 87);
        backButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.openGui(new MainGui()));
        title = new TextLabel(new TextMapper(" TU BALANCE ", ShopResources.US), DARK_GRAY.getCode());
        balance = new TextLabel(new TextMapper("", ShopResources.US), DARK_GRAY.getCode());
        coinImage = new ImageBack(ShopResources.COIN);
        strip1 = new SolidBack(ORANGE.getCode());
        strip2 = new SolidBack(ORANGE.getCode());
        strip3 = new SolidBack(ORANGE.getCode());
        strip4 = new SolidBack(ORANGE.getCode());
        exchanges = new TextLabel(new TextMapper("ITEMS INGRESADOS:", ShopResources.US_SMALL), DARK_GRAY.getCode());
        netheriteBlockImage = new ImageBack(ShopResources.NETH_BLOCK);
        debrisImage = new ImageBack(ShopResources.DEBRIS);
        diaBlockImage = new ImageBack(ShopResources.DIA_BLOCK);
        netheriteIngotImage = new ImageBack(ShopResources.NETH_INGOT);
        scrapImage = new ImageBack(ShopResources.NETH_SCRAP);
        diaImage = new ImageBack(ShopResources.DIA);
        netheriteBlockLabel = new TextLabel(new TextMapper("", ShopResources.US_SMALL), DARK_GRAY.getCode());
        debrisLabel = new TextLabel(new TextMapper("", ShopResources.US_SMALL), DARK_GRAY.getCode());
        diaBlockLabel = new TextLabel(new TextMapper("", ShopResources.US_SMALL), DARK_GRAY.getCode());
        netheriteIngotLabel = new TextLabel(new TextMapper("", ShopResources.US_SMALL), DARK_GRAY.getCode());
        scrapLabel = new TextLabel(new TextMapper("", ShopResources.US_SMALL), DARK_GRAY.getCode());
        diaLabel = new TextLabel(new TextMapper("", ShopResources.US_SMALL), DARK_GRAY.getCode());

        addComponent(mainBack);
        addComponent(closeButton, backButton);
        addComponent(title);
        addComponent(coinImage);
        addComponent(balance);
        addComponent(strip1, strip2, strip3, strip4);
        addComponent(exchanges);
        addComponent(netheriteBlockImage, debrisImage, diaBlockImage, netheriteIngotImage, scrapImage, diaImage);
        addComponent(netheriteBlockLabel, debrisLabel, diaBlockLabel, netheriteIngotLabel, scrapLabel, diaLabel);
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
        scrapImage.setDimensions(guiPlayer.getPanelPixelWidth() / 3 * 2 - (guiPlayer.getPanelPixelWidth() / 3 - 30), 80 + 40 + title.getHeight() + 60 + 270, scrapImage.getImage().getWidth(), scrapImage.getHeight());
        diaImage.setDimensions(guiPlayer.getPanelPixelWidth() - (guiPlayer.getPanelPixelWidth() / 3 + 10), 80 + 40 + title.getHeight() + 60 + 270, diaImage.getImage().getWidth(), diaImage.getHeight());

        updateBalance(guiPlayer);
    }

    @Override
    public void onMouseChange(GuiPlayer guiPlayer, int newX, int newY, int oldX, int oldY) {
        updateBalance(guiPlayer);
        super.onMouseChange(guiPlayer, newX, newY, oldX, oldY);
    }

    private void updateBalance(GuiPlayer guiPlayer) {
        balance.setText(new TextMapper(String.valueOf(guiPlayer.getShopExtension().getAccounts().get(guiPlayer.getPlayer()).getCoins()), ShopResources.US));
        balance.setDimensions(guiPlayer.getPanelPixelWidth() / 2 - balance.getText().getWidth() / 2, 80 + 40 + title.getHeight() + 60, balance.getText().getWidth(), balance.getText().getHeight());
        coinImage.setDimensions((guiPlayer.getPanelPixelWidth() / 2 - balance.getText().getWidth() / 2) - coinImage.getImage().getWidth(), 80 + 40 + title.getHeight() + 40, coinImage.getImage().getWidth(), coinImage.getImage().getHeight());

        int nethBlockCount = guiPlayer.getShopExtension().getAccounts().get(guiPlayer.getPlayer()).getExchanges().getNetheriteBlock();
        setLabel(nethBlockCount, netheriteBlockLabel, netheriteBlockImage);
        int debrisCount = guiPlayer.getShopExtension().getAccounts().get(guiPlayer.getPlayer()).getExchanges().getDebris();
        setLabel(debrisCount, debrisLabel, debrisImage);
        int diaBlockCount = guiPlayer.getShopExtension().getAccounts().get(guiPlayer.getPlayer()).getExchanges().getDiamondBlock();
        setLabel(diaBlockCount, diaBlockLabel, diaBlockImage);
        int netheriteIngotCount = guiPlayer.getShopExtension().getAccounts().get(guiPlayer.getPlayer()).getExchanges().getNetheriteIngot();
        setLabel(netheriteIngotCount, netheriteIngotLabel, netheriteIngotImage);
        int scrapCount = guiPlayer.getShopExtension().getAccounts().get(guiPlayer.getPlayer()).getExchanges().getNetheriteScrap();
        setLabel(scrapCount, scrapLabel, scrapImage);
        int diaCount = guiPlayer.getShopExtension().getAccounts().get(guiPlayer.getPlayer()).getExchanges().getDiamond();
        setLabel(diaCount, diaLabel, diaImage);

        super.render(guiPlayer);
    }

    private void setLabel(int count, TextLabel label, ImageBack image) {
        label.setText(new TextMapper(String.valueOf(count == -1 ? 0 : count), ShopResources.US_SMALL));
        label.setDimensions(image.getX() + image.getWidth() + 5, image.getY() - (image.getImage().getHeight() / 4), label.getText().getWidth(), label.getText().getHeight());
    }
}
