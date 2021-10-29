package com.kahzerx.kahzerxmod.utils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Objects;

public class DimUtils {
    public static ServerWorld getWorld(String dim, ServerPlayerEntity player) {
        return switch (dim) {
            case "Overworld" -> Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
            case "Nether" -> Objects.requireNonNull(player.getServer()).getWorld(World.NETHER);
            default -> Objects.requireNonNull(player.getServer()).getWorld(World.END);
        };
    }

    public static ServerWorld getWorld(int dimID, MinecraftServer server) {
        return switch (dimID) {
            case 0 -> server.getWorld(World.OVERWORLD);
            case 1 -> server.getWorld(World.NETHER);
            default -> server.getWorld(World.END);
        };
    }

    public static int getWorldID(String dim) {
        return switch (dim) {
            case "Overworld" -> 0;
            case "Nether" -> 1;
            default -> 2;
        };
    }

    public static String getWorldString(int dimID) {
        return switch (dimID) {
            case 0 -> "Overworld";
            case 1 -> "Nether";
            default -> "End";
        };
    }

    public static String getDim(World world) {
        Identifier dimensionType = world.getRegistryKey().getValue();
        String msg = world.getDimension().toString();
        if (dimensionType.equals(World.OVERWORLD.getValue())) {
            msg = "Overworld";
        } else if (dimensionType.equals(World.NETHER.getValue())) {
            msg = "Nether";
        } else if (dimensionType.equals(World.END.getValue())) {
            msg = "End";
        }
        return msg;
    }

    public static String getDimensionWithColor(World world) {
        Identifier dimensionType = world.getRegistryKey().getValue();
        String msg = world.getDimension().toString();
        return getDim(dimensionType, msg);
    }

    public static String getDimensionWithColor(final Identifier dimensionType) {
        String msg = dimensionType.toString();
        return getDim(dimensionType, msg);
    }

    private static String getDim(Identifier dimensionType, String msg) {
        if (dimensionType.equals(World.OVERWORLD.getValue())) {
            msg = Formatting.GREEN + "[Overworld]";
        } else if (dimensionType.equals(World.NETHER.getValue())) {
            msg = Formatting.RED + "[Nether]";
        } else if (dimensionType.equals(World.END.getValue())) {
            msg = Formatting.DARK_PURPLE + "[End]";
        }
        return msg;
    }

    public static String formatCoords(double x, double y, double z) {
        return Formatting.WHITE + String.format("[x: %d, y: %d, z: %d]", (int) x, (int) y, (int) z);
    }
}
