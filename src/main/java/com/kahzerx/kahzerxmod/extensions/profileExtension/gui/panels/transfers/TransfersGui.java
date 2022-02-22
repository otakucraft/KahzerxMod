package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.transfers;

import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiBase;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.MapGui;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.back.SolidBack;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.buttons.TextButton;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.helpers.TextMapper;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.components.labels.TextLabel;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.main.MainGui;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.resources.ShopResources;
import com.kahzerx.kahzerxmod.extensions.shopExtension.BankInstance;

import java.awt.*;
import java.util.List;

import static com.kahzerx.kahzerxmod.extensions.profileExtension.gui.colors.ColorList.*;

public class TransfersGui extends GuiBase {
    private final SolidBack mainBack;
    private final TextButton closeButton;
    private final TextButton backButton;
    private final TextLabel title;
    private final SolidBack strip1;
    private final SolidBack strip2;
    public TransfersGui() {
        mainBack = new SolidBack(LIGHT_YELLOW.getCode());
        closeButton = new TextButton(new TextMapper("Close", new Font("Times New Roman", Font.PLAIN, 30)), RED.getCode(), DARK_RED.getCode(), (byte) 84, (byte) 87);
        closeButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.closePanel());
        backButton = new TextButton(new TextMapper("Back", new Font("Times New Roman", Font.PLAIN, 30)), LIGHT_GRAY.getCode(), GRAY.getCode(), (byte) 84, (byte) 87);
        backButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.openGui(new MainGui()));
        title = new TextLabel(new TextMapper(" TUS TRANSFERENCIAS ", ShopResources.US), DARK_GRAY.getCode());

        strip1 = new SolidBack(ORANGE.getCode());
        strip2 = new SolidBack(ORANGE.getCode());

        addComponent(mainBack);
        addComponent(closeButton, backButton);
        addComponent(title);
        addComponent(strip1, strip2);
    }

    @Override
    public void render(GuiPlayer guiPlayer) {
        mainBack.setDimensions(0, 80, guiPlayer.getPanelPixelWidth(), guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        closeButton.setDimensions(guiPlayer.getPanelPixelWidth() - 100 - 10, 10, 100, 50);
        backButton.setDimensions(10, 10, 100, 50);

        title.setDimensions((guiPlayer.getPanelPixelWidth() / 2) - (title.getWidth() / 2), 80 + 40, title.getWidth(), title.getHeight());

        strip1.setDimensions(guiPlayer.getPanelPixelWidth() - 45, 80, 15, guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        strip2.setDimensions(guiPlayer.getPanelPixelWidth() - 15, 80, 15, guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);

        super.render(guiPlayer);

        List<BankInstance.Transfer> transfers = guiPlayer.getShopExtension().getTransfers(guiPlayer.getPlayer()).getTransfers();

        int gap = 0;
        for (int i = transfers.size() - 1; i >=0; i--) {
            TextLabel label = new TextLabel(new TextMapper(String.format("- Has transferido %d a %s", transfers.get(i).getAmount(), transfers.get(i).getDestName()), ShopResources.US_SMALLEST), DARK_GRAY.getCode());
            label.setDimensions(title.getX() - (title.getText().getWidth() / 2) + 20, title.getY() + 55 + gap, label.getText().getWidth(), label.getText().getHeight());
            label.render(guiPlayer);
            label.setReRender(true);
            gap += 35;
        }
    }

    @Override
    public void onMouseChange(GuiPlayer guiPlayer, int newX, int newY, int oldX, int oldY) {
        this.setReRender(true);
    }
}
