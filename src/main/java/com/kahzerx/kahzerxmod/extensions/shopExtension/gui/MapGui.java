package com.kahzerx.kahzerxmod.extensions.shopExtension.gui;

import com.kahzerx.kahzerxmod.extensions.shopExtension.gui.components.shapes.SimpleRect;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MapGui {
    private int code;
    private ServerPlayerEntity player;

    private ItemFrameEntity mapEntity = null;
    private ItemStack mapItem;
    private NbtCompound mapItemTag;

    public static int MAP_WIDTH = 128;
    public static int MAP_HEIGHT = 128;
    private byte[] colors;
    private SimpleRect newBounds = new SimpleRect(0, 0, 0, 0);
    private byte[] prevColors;

    public MapGui(int code, GuiPlayer guiPlayer, ServerPlayerEntity player) {
        this.code = code;
        this.player = player;
        this.colors = new byte[MAP_WIDTH * MAP_HEIGHT];
        this.prevColors = new byte[MAP_WIDTH * MAP_HEIGHT];

        this.mapItem = new ItemStack(Items.FILLED_MAP);
        this.mapItemTag = this.mapItem.getOrCreateNbt();
        this.mapItemTag.putInt("map", code);
        this.mapItem.setCount(1);
    }

    void force() {
        MapState.UpdateData data = new MapState.UpdateData(0, 0, MAP_WIDTH, MAP_HEIGHT, colors);
        player.networkHandler.sendPacket(new MapUpdateS2CPacket(code, (byte) 0, false, null, data));
        newBounds.set(0, 0, 0, 0);
    }

    public void updateFrame(BlockPos pos, Direction dir, int x, int y) {
        mapEntity = new GlowItemFrameEntity(player.getWorld(), pos, dir);
        mapEntity.setInvisible(true);
        mapEntity.setHeldItemStack(mapItem, false);
    }

    public void sendData() {
        int minX = newBounds.getMinX();
        int maxX = newBounds.getMaxX();
        int minY = newBounds.getMinY();
        int maxY = newBounds.getMaxY();
        newBounds.set(0, 0, 0, 0);
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {

                int index = y * MAP_WIDTH + x;
                if (prevColors[index] != colors[index]) {
                    newBounds.include(x, y);
                }
                prevColors[index] = colors[index];
            }
        }
        int size = newBounds.getSize();
        if (size == 0) {
            return;
        }
        byte[] colorsToSend = new byte[size];
        for (int x = 0; x < newBounds.getWidth(); x++) {
            for (int y = 0; y < newBounds.getHeight(); y++) {
                colorsToSend[x + y * newBounds.getWidth()] = colors[x + minX + (y + minY) * MAP_WIDTH];
            }
        }
        MapState.UpdateData data = new MapState.UpdateData(newBounds.getMinX(), newBounds.getMinY(), newBounds.getWidth(), newBounds.getHeight(), colorsToSend);
        player.networkHandler.sendPacket(new MapUpdateS2CPacket(code, (byte) 0, false, null, data));
        newBounds.set(0, 0, 0, 0);
    }

    public void showFrame() {
        player.networkHandler.sendPacket(mapEntity.createSpawnPacket());
        if (!mapEntity.getDataTracker().isEmpty()) {
            player.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(mapEntity.getId(), mapEntity.getDataTracker(), true));
        }
    }

    public boolean shouldUpdate() {
        return newBounds.getSize() > 0;
    }

    public boolean setPixel(int i, int j, byte color) {
        if (i < 0 || i >= MAP_WIDTH || j < 0 || j >= MAP_HEIGHT) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }
        int index = i + j * MAP_WIDTH;
        if (this.colors[index] != color)
            newBounds.include(i, j);
        this.colors[index] = color;
        return true;
    }

    public int getEntityID() {
        return mapEntity.getId();
    }
}
