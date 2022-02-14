package com.kahzerx.kahzerxmod.extensions.shopExtension.gui;

import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class GuiPlayer {
    private ArrayList<MapGui> maps = new ArrayList<>();
    private ServerPlayerEntity player;
    private boolean isOpen;
    private int scrolled = 0;
    private int oldX = -1;
    private int oldY = -1;
    private GuiBase gui;
    private int panelSize;
    private BlockPos panelOpenPos;
    private Direction panelFacingSide;
    private ServerWorld panelWorld;
    private int panelWidth;
    private int panelHeight;
    private int panelPixelWidth;
    private int panelPixelHeight;
    private BlockPos panelCorner1;
    private BlockPos panelCorner2;
    private Box panelBox;

    public GuiPlayer(ServerPlayerEntity player) {
        this.player = player;
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean shouldRemove() {
        return !player.isAlive();
    }

    public void tick() {
        if (isOpen && shouldClose()) {
            closePanel();
        }
        if (scrolled > 0) {
            scrolled--;
        }
        if (isOpen) {
            if (gui != null && gui.shouldRender(this)) {
                gui.render(this);
                gui.setReRender(false);
            }
            BlockHitResult hit = Box.raycast(List.of(panelBox), player.getCameraPosVec(1f), player.getCameraPosVec(1f).add(player.getRotationVec(1f).multiply(20)), new BlockPos(0, 0, 0));
            int newX = -1;
            int newY = -1;
            if (hit != null && hit.getSide() == panelFacingSide) {
                double dx = panelCorner1.getX() - hit.getPos().getX();
                double dy = panelCorner1.getY() - hit.getPos().getY() + 1;
                double dz = panelCorner1.getZ() - hit.getPos().getZ();
                if (panelFacingSide != Direction.NORTH) {
                    dx = -dx;
                } else {
                    dx += 1;
                }
                if (panelFacingSide != Direction.EAST) {
                    dz = -dz;
                } else {
                    dz += 1;
                }

                newX = (int) ((panelFacingSide.getAxis() == Direction.Axis.Z ? dx : dz) * MapGui.MAP_WIDTH);
                newY = (int) (dy * MapGui.MAP_HEIGHT);
                if (newX != oldX || newY != oldY) {
                    onMouseChange(newX, newY, oldX, oldY);
                    oldX = newX;
                    oldY = newY;
                }
                for (int i = 0; i < panelSize; i++) {
                    MapGui g = maps.get(i);
                    if (g.shouldUpdate()) {
                        g.sendData();
                    }
                }
            }
        }
    }

    public boolean shouldClose() {
        return shouldRemove() || panelWorld != player.getWorld() || !player.getBlockPos().isWithinDistance(panelOpenPos, 10);
    }

    public void openGui(GuiBase gui) {
        if (gui != null) {
            closeGui();
        }
        Renderer.clear(this);
        this.gui = gui;
        this.gui.setReRender(true);
        this.gui.onOpen(this);
    }

    public void closeGui() {
        if (gui == null) {
            return;
        }
        gui.onClose(this);
        gui = null;
    }

    public void openPanel(BlockPos pos, Direction dir, int width, int height) {
        if (isOpen) {
            closePanel();
        }
        if (gui != null) {
            gui.setReRender(true);
        }
        isOpen = true;
        panelWorld = player.getWorld();
        panelOpenPos = pos;
        panelFacingSide = dir;
        panelWidth = width;
        panelHeight = height;
        panelPixelWidth = width * MapGui.MAP_WIDTH;
        panelPixelHeight = height * MapGui.MAP_HEIGHT;
        panelSize = width * height;
        int offsetX = 0;
        int offsetZ = 0;
        if (dir.getAxis() == Direction.Axis.Z) {
            offsetX = dir != Direction.NORTH ? (-width / 2) : -(-width / 2);
        } else if (dir.getAxis() == Direction.Axis.X) {
            offsetZ = dir != Direction.EAST ? (-width / 2) : -(-width / 2);
        }
        panelCorner1 = new BlockPos(offsetX + pos.getX(), pos.getY() + height - 1, offsetZ + pos.getZ()).offset(dir.getOpposite(), 1);
        offsetX = 0;
        offsetZ = 0;
        if (dir.getAxis() == Direction.Axis.Z) {
            offsetX = dir != Direction.NORTH ? (width - width / 2 - 1) : -(width - width / 2 - 1);
        } else if (dir.getAxis() == Direction.Axis.X) {
            offsetZ = dir != Direction.EAST ? (width - width / 2 - 1) : -(width - width / 2 - 1);
        }
        panelCorner2 = new BlockPos(offsetX + pos.getX(), pos.getY(), offsetZ + pos.getZ()).offset(dir.getOpposite(), 1);
        int minX = Math.min(panelCorner1.getX(), panelCorner2.getX());
        int minY = Math.min(panelCorner1.getY(), panelCorner2.getY());
        int minZ = Math.min(panelCorner1.getZ(), panelCorner2.getZ());
        int maxX = Math.max(panelCorner1.getX(), panelCorner2.getX()) + 1;
        int maxY = Math.max(panelCorner1.getY(), panelCorner2.getY()) + 1;
        int maxZ = Math.max(panelCorner1.getZ(), panelCorner2.getZ()) + 1;
        panelBox = new Box(minX, minY, minZ, maxX, maxY, maxZ);

        int index = 0;

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                offsetX = 0;
                offsetZ = 0;
                if (dir.getAxis() == Direction.Axis.Z) {
                    offsetX = dir != Direction.NORTH ? (x - width / 2) : -(x - width / 2);
                } else if (dir.getAxis() == Direction.Axis.X) {
                    offsetZ = dir != Direction.EAST ? (x - width / 2) : -(x - width / 2);
                }
                BlockPos pos1 = new BlockPos(offsetX + pos.getX(), pos.getY() + y, offsetZ + pos.getZ());

                MapGui map;
                if (index >= maps.size()) {
                    map = new MapGui(1000000000 + index, this, player);
                    maps.add(map);
                } else {
                    map = maps.get(index);
                }

                map.updateFrame(pos1, dir, x, y);
                map.showFrame();
                index++;
            }
        }
        force();
    }

    private void force() {
        for (MapGui g : maps) {
            g.force();
        }
        if (gui != null) {
            gui.setReRender(true);
        }
    }

    public void closePanel() {
        if (!isOpen) {
            return;
        }
        if (oldX != -1 || oldY != -1) {
            this.onMouseChange(-1, -1, oldX, oldY);
        }
        oldX = -1;
        oldY = -1;
        int i = Math.min(this.panelSize, Integer.MAX_VALUE);
        int[] is = new int[i];
        for (int j = 0; j < i; j++) {
            is[j] = maps.get(j).getEntityID();
        }
        if (player != null) {
            player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(is));
        }
        isOpen = false;
        panelWorld = null;
    }

    public boolean isTracking() {
        return isOpen && oldX != -1 && oldY != -1;
    }

    private void onMouseChange(int newX, int newY, int oldX, int oldY) {
        if (gui == null) {
            return;
        }
        gui.onMouseChange(this, newX, newY, oldX, oldY);
    }

    public int getPanelPixelWidth() {
        return panelPixelWidth;
    }

    public int getPanelPixelHeight() {
        return panelPixelHeight;
    }

    public boolean setPixel(int x, int y, byte color) {
        if (x < 0 || y < 0)
            return false;
        if (x >= panelPixelWidth || y >= panelPixelHeight)
            return false;

        int mapX = x / MapGui.MAP_WIDTH;
        int mapY = y / MapGui.MAP_HEIGHT;

        int index = mapX + mapY * panelWidth;

        return maps.get(index).setPixel(x - mapX * MapGui.MAP_WIDTH, y - mapY * MapGui.MAP_HEIGHT, color);
    }

    public void onClick() {
        if (gui == null) {
            return;
        }
        gui.onClick(false, this);
    }
}
