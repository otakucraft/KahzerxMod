package com.kahzerx.kahzerxmod.extensions.shopExtension.gui.panels;

import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.GuiBase;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.MapGui;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.back.SolidBack;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.buttons.TextButton;
import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.helpers.TextMapper;

import java.awt.*;

import static com.kahzerx.kahzerxmod.extensions.shopExtension.gui.colors.ColorList.*;

public class GuiMain extends GuiBase {
    private SolidBack backTest;
    private TextButton closeButton;
    public GuiMain() {
        backTest = new SolidBack(LIGHT_GRAY.getCode());
        closeButton = new TextButton(new TextMapper("Close", new Font("Times New Roman", Font.PLAIN, 30)), RED.getCode(), DARK_RED.getCode(), (byte) 84, (byte) 87);
        closeButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.closePanel());
        addComponent(backTest);
        addComponent(closeButton);
    }

    @Override
    public void render(GuiPlayer guiPlayer) {
        closeButton.setDimensions(guiPlayer.getPanelPixelWidth() - 100 - 10, 10, 100, 50);
        backTest.setDimensions(0, 80, guiPlayer.getPanelPixelWidth(), guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        super.render(guiPlayer);
    }

    @Override
    public void onMouseChange(GuiPlayer guiPlayer, int newX, int newY, int oldX, int oldY) {
        super.onMouseChange(guiPlayer, newX, newY, oldX, oldY);
    }
}
