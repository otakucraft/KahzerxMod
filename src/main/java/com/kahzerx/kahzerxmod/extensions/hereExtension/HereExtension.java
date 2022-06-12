package com.kahzerx.kahzerxmod.extensions.hereExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class HereExtension extends GenericExtension implements Extensions {
    public HereExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new HereCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    public int sendLocation(ServerCommandSource src) throws CommandSyntaxException {
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) {
            return 1;
        }
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        if (player.getWorld().getRegistryKey().getValue().equals(World.OVERWORLD.getValue())) {
            src.getServer().getPlayerManager().broadcast(
                    Text.literal(String.format(
                            "%s %s %s -> %s %s",
                            PlayerUtils.getPlayerWithColor(player),
                            DimUtils.getDimensionWithColor(player.getWorld()),
                            DimUtils.formatCoords(x, y, z),
                            DimUtils.getDimensionWithColor(World.NETHER.getValue()),
                            DimUtils.formatCoords(x / 8, y, z / 8)
                    )),
                    MessageType.SYSTEM
            );
        } else if (player.getWorld().getRegistryKey().getValue().equals(World.NETHER.getValue())) {
            src.getServer().getPlayerManager().broadcast(
                    Text.literal(String.format(
                            "%s %s %s -> %s %s",
                            PlayerUtils.getPlayerWithColor(player),
                            DimUtils.getDimensionWithColor(player.getWorld()),
                            DimUtils.formatCoords(x, y, z),
                            DimUtils.getDimensionWithColor(World.OVERWORLD.getValue()),
                            DimUtils.formatCoords(x *  8, y, z * 8)
                    )),
                    MessageType.SYSTEM
            );
        } else if (player.getWorld().getRegistryKey().getValue().equals(World.END.getValue())) {
            src.getServer().getPlayerManager().broadcast(
                    Text.literal(String.format(
                            "%s %s %s",
                            PlayerUtils.getPlayerWithColor(player),
                            DimUtils.getDimensionWithColor(player.getWorld()),
                            DimUtils.formatCoords(x, y, z)
                    )),
                    MessageType.SYSTEM
            );
        }
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0, false, false));
        player.getWorld().playSound(null, player.getBlockPos(), Instrument.HARP.getSound(), SoundCategory.RECORDS, 3.0f, (float)Math.pow(2.0, (double)(24 - 12) / 12.0));
        return 1;
    }
}
