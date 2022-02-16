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
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.main.MainResources;

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
    public GuiBalance() {
        mainBack = new SolidBack(LIGHT_YELLOW.getCode());
        closeButton = new TextButton(new TextMapper("Close", new Font("Times New Roman", Font.PLAIN, 30)), RED.getCode(), DARK_RED.getCode(), (byte) 84, (byte) 87);
        closeButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.closePanel());
        backButton = new TextButton(new TextMapper("Back", new Font("Times New Roman", Font.PLAIN, 30)), LIGHT_GRAY.getCode(), GRAY.getCode(), (byte) 84, (byte) 87);
        backButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.openGui(new GuiMain()));
        title = new TextLabel(new TextMapper(" TU BALANCE ", BalanceResources.US), BLACK.getCode());
        balance = new TextLabel(new TextMapper("", BalanceResources.US), BLACK.getCode());
        coinImage = new ImageBack(BalanceResources.COIN);
        strip1 = new SolidBack(ORANGE.getCode());
        strip2 = new SolidBack(ORANGE.getCode());
        strip3 = new SolidBack(ORANGE.getCode());
        strip4 = new SolidBack(ORANGE.getCode());

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
        super.render(guiPlayer);
    }
}
