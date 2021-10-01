package com.kahzerx.kahzerxmod.extensions.hereExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class HereExtension extends GenericExtension implements Extensions {
    public HereExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (this.getSettings().isEnabled()) {
            new HereCommand().register(dispatcher, this);
        }
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onExtensionEnabled() {

    }

    @Override
    public void onExtensionDisabled() {

    }

    public int sendLocation(ServerCommandSource src) throws CommandSyntaxException {
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) {
            return 1;
        }
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        if (player.world.getRegistryKey().getValue().equals(World.OVERWORLD.getValue())) {
            src.getServer().getPlayerManager().broadcastChatMessage(
                    new LiteralText(String.format(
                            "%s %s %s -> %s %s",
                            PlayerUtils.getPlayerWithColor(player),
                            DimUtils.getDimensionWithColor(player.world),
                            DimUtils.formatCoords(x, y, z),
                            DimUtils.getDimensionWithColor(World.NETHER.getValue()),
                            DimUtils.formatCoords(x / 8, y, z / 8)
                    )),
                    MessageType.CHAT,
                    Util.NIL_UUID
            );
        } else if (player.world.getRegistryKey().getValue().equals(World.NETHER.getValue())) {
            src.getServer().getPlayerManager().broadcastChatMessage(
                    new LiteralText(String.format(
                            "%s %s %s -> %s %s",
                            PlayerUtils.getPlayerWithColor(player),
                            DimUtils.getDimensionWithColor(player.world),
                            DimUtils.formatCoords(x, y, z),
                            DimUtils.getDimensionWithColor(World.OVERWORLD.getValue()),
                            DimUtils.formatCoords(x *  8, y, z * 8)
                    )),
                    MessageType.CHAT,
                    Util.NIL_UUID
            );
        } else if (player.world.getRegistryKey().getValue().equals(World.END.getValue())) {
            src.getServer().getPlayerManager().broadcastChatMessage(
                    new LiteralText(String.format(
                            "%s %s %s",
                            PlayerUtils.getPlayerWithColor(player),
                            DimUtils.getDimensionWithColor(player.world),
                            DimUtils.formatCoords(x, y, z)
                    )),
                    MessageType.CHAT,
                    Util.NIL_UUID
            );
        }
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0, false, false));
        return 1;
    }
}
