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
    private final TextButton backPageButton;
    private final TextButton nextPageButton;
    private final TextLabel pagination;
    private final TextLabel title;
    private final SolidBack strip1;
    private final SolidBack strip2;
    private final int page;
    public TransfersGui(int page) {
        this.page = page;
        mainBack = new SolidBack(LIGHT_YELLOW.getCode());
        closeButton = new TextButton(new TextMapper("Close", new Font("Times New Roman", Font.PLAIN, 30)), RED.getCode(), DARK_RED.getCode(), (byte) 84, (byte) 87);
        closeButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.closePanel());
        backButton = new TextButton(new TextMapper("Back", new Font("Times New Roman", Font.PLAIN, 30)), LIGHT_GRAY.getCode(), GRAY.getCode(), (byte) 84, (byte) 87);
        backButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.openGui(new MainGui()));
        title = new TextLabel(new TextMapper(" TUS TRANSFERENCIAS ", ShopResources.US), DARK_GRAY.getCode());
        backPageButton = new TextButton(new TextMapper("<", new Font("Times New Roman", Font.PLAIN, 30)), LIGHT_GRAY.getCode(), GRAY.getCode(), (byte) 84, (byte) 87);
        backPageButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.openGui(new TransfersGui(page - 1)));
        nextPageButton = new TextButton(new TextMapper(">", new Font("Times New Roman", Font.PLAIN, 30)), LIGHT_GRAY.getCode(), GRAY.getCode(), (byte) 84, (byte) 87);
        nextPageButton.setClickCallback((boolean isKey, GuiPlayer p) -> p.openGui(new TransfersGui(page + 1)));
        pagination = new TextLabel(new TextMapper(String.valueOf(page), new Font("Times New Roman", Font.BOLD, 35)), WHITE.getCode());

        strip1 = new SolidBack(ORANGE.getCode());
        strip2 = new SolidBack(ORANGE.getCode());

        addComponent(mainBack);
        addComponent(closeButton, backButton);
        addComponent(title);
        addComponent(strip1, strip2);
        addComponent(nextPageButton, backPageButton);
        addComponent(pagination);
    }

    @Override
    public void render(GuiPlayer guiPlayer) {
        String uuid = guiPlayer.isBank() ? "00000000-0000-0000-0000-000000000000" : guiPlayer.getPlayer().getUuidAsString();

        mainBack.setDimensions(0, 80, guiPlayer.getPanelPixelWidth(), guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        closeButton.setDimensions(guiPlayer.getPanelPixelWidth() - 100 - 10, 10, 100, 50);
        backButton.setDimensions(10, 10, 100, 50);

        title.setDimensions((guiPlayer.getPanelPixelWidth() / 2) - (title.getWidth() / 2), 80 + 40, title.getWidth(), title.getHeight());

        strip1.setDimensions(guiPlayer.getPanelPixelWidth() - 45, 80, 15, guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);
        strip2.setDimensions(guiPlayer.getPanelPixelWidth() - 15, 80, 15, guiPlayer.getPanelPixelHeight() - 80 - MapGui.MAP_HEIGHT);

        List<BankInstance.Transfer> transfers = guiPlayer.getShopExtension().getDB().getQuery().getTransfers(uuid, this.page).getTransfers();
        List<BankInstance.Transfer> transfers2 = guiPlayer.getShopExtension().getDB().getQuery().getTransfers(uuid, this.page + 1).getTransfers();

        if (this.page > 1) {
            backPageButton.setDimensions(10, guiPlayer.getPanelPixelHeight() - 100 - 10, 70, 50);
        } else {
            removeComponent(backPageButton);
        }
        if (this.page < 30 && transfers.size() == 8 && transfers2.size() != 0) {
            nextPageButton.setDimensions(guiPlayer.getPanelPixelWidth() - 70 - 10, guiPlayer.getPanelPixelHeight() - 100 - 10, 70, 50);
        } else {
            removeComponent(nextPageButton);
        }

        pagination.setDimensions((guiPlayer.getPanelPixelWidth() / 2) - (pagination.getWidth() / 2), guiPlayer.getPanelPixelHeight() - 75 - 10, pagination.getWidth(), pagination.getHeight());

        super.render(guiPlayer);

        int gap = 0;
        for (int i = transfers.size() - 1; i >= 0; i--) {
            TextLabel label;
            if (transfers.get(i).isReceived()) {
                label = new TextLabel(new TextMapper(String.format("- %s te ha transferido %d", transfers.get(i).getDestName(), transfers.get(i).getAmount()), ShopResources.US_SMALLEST), DARK_GRAY.getCode());
            } else {
                label = new TextLabel(new TextMapper(String.format("- Has transferido %d a %s", transfers.get(i).getAmount(), transfers.get(i).getDestName()), ShopResources.US_SMALLEST), DARK_GRAY.getCode());
            }
            label.setDimensions(title.getX() - (title.getText().getWidth() / 2) + 20, title.getY() + 55 + gap, label.getText().getWidth(), label.getText().getHeight());
            label.render(guiPlayer);
            label.setReRender(true);
            gap += 35;
        }
    }
}
