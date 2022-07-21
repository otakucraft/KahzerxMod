package com.kahzerx.kahzerxmod.extensions.shopExtension.parcel;

import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.awt.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Parcel {
    private String ownerUUID;
    private final BlockPos corner1;
    private final BlockPos corner2;
    private String name;
    private int price;
    private final int dim;
    private Timestamp nextPayout;

    public Parcel(String ownerUUID, BlockPos corner1, BlockPos corner2, String name, int price, int dim, Timestamp nextPayout) {
        this.ownerUUID = ownerUUID;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.name = name;
        this.price = price;
        this.dim = dim;
        this.nextPayout = nextPayout;
    }

    public BlockPos getCorner1() {
        return corner1;
    }

    public BlockPos getCorner2() {
        return corner2;
    }

    public int getDim() {
        return dim;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public Timestamp getNextPayout() {
        return nextPayout;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasOwner() {
        return this.ownerUUID != null;
    }

    public void setNextPayout(Timestamp nextPayout) {
        this.nextPayout = nextPayout;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public MutableText formatted(ShopExtension extension) {
        int centerX = (corner1.getX() + corner2.getX()) / 2;
        int centerZ = (corner1.getZ() + corner2.getZ()) / 2;
        MutableText t = MarkEnum.INFO.appendMessage(String.format("coords: §7%d %d§r owner: §7%s§r", centerX, centerZ, ownerUUID != null ? extension.getDB().getQuery().getPlayerName(ownerUUID) : "None"));
        MutableText hover = Text.literal(String.format("Dimension: %s\nPrice: %d\nPayout: %s\nCorner1: %d %d %d\nCorner2: %d %d %d\nClick to visualize!", DimUtils.getWorldString(dim), price, nextPayout != null ? nextPayout.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "None", corner1.getX(), corner1.getY(), corner1.getZ(), corner2.getX(), corner2.getY(), corner2.getZ()));
        t.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/parcel draw %d %d %d %d %d %d", corner1.getX(), corner1.getY(), corner1.getZ(), corner2.getX(), corner2.getY(), corner2.getZ()))));
        return t;
    }

    public static void draw(BlockPos corner1, BlockPos corner2, ServerWorld world) {
        if (corner1.getX() > corner2.getX()) {
            int x1 = corner1.getX();
            int x2 = corner2.getX();
            corner1 = new BlockPos(x2, corner1.getY(), corner1.getZ());
            corner2 = new BlockPos(x1, corner2.getY(), corner2.getZ());
        }
        if (corner1.getY() > corner2.getY()) {
            int y1 = corner1.getX();
            int y2 = corner2.getX();
            corner1 = new BlockPos(corner1.getX(), y2, corner1.getZ());
            corner2 = new BlockPos(corner2.getX(), y1, corner2.getZ());
        }
        if (corner1.getZ() > corner2.getZ()) {
            int z1 = corner1.getX();
            int z2 = corner2.getX();
            corner1 = new BlockPos(corner1.getX(), corner1.getY(), z2);
            corner2 = new BlockPos(corner2.getX(), corner2.getY(), z1);
        }
        List<BlockPos> structure = new ArrayList<>();
        for (int x = corner1.getX(); x <= corner2.getX(); x++) {
            structure.add(new BlockPos(x, corner1.getY(), corner1.getZ()));
            structure.add(new BlockPos(x, corner1.getY(), corner2.getZ()));
            structure.add(new BlockPos(x, corner2.getY(), corner1.getZ()));
            structure.add(new BlockPos(x, corner2.getY(), corner2.getZ()));
        }
        for (int y = corner1.getY(); y <= corner2.getY(); y++) {
            structure.add(new BlockPos(corner1.getX(), y, corner1.getZ()));
            structure.add(new BlockPos(corner2.getX(), y, corner1.getZ()));
            structure.add(new BlockPos(corner1.getX(), y, corner2.getZ()));
            structure.add(new BlockPos(corner2.getX(), y, corner2.getZ()));
        }
        for (int z = corner1.getZ(); z <= corner2.getZ(); z++) {
            structure.add(new BlockPos(corner1.getX(), corner1.getY(), z));
            structure.add(new BlockPos(corner2.getX(), corner1.getY(), z));
            structure.add(new BlockPos(corner1.getX(), corner2.getY(), z));
            structure.add(new BlockPos(corner2.getX(), corner2.getY(), z));
        }
        Collections.shuffle(structure);

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                for (BlockPos p : structure) {
                    try {
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    spawnDustParticle(world, p);
                }
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void spawnDustParticle(ServerWorld world, BlockPos pos) {
        world.spawnParticles(
                new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(new Color(255, 220, 0).getRGB())), 1.0F),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                50,
                0,
                0,
                0,
                1
        );
    }
}
