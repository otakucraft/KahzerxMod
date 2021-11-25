package com.kahzerx.kahzerxmod.extensions.cameraExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;

public class CameraExtension extends GenericExtension implements Extensions {
    public final PermsExtension permsExtension;

    public CameraExtension(ExtensionSettings settings, PermsExtension permsExtension) {
        super(settings);
        this.permsExtension = permsExtension;
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new CameraCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    public int setCameraMode(ServerCommandSource src) throws CommandSyntaxException {
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) {
            return 1;
        }
        if (permsExtension.getPlayerPerms().containsKey(player.getUuidAsString())) {
            if (permsExtension.getPlayerPerms().get(player.getUuidAsString()).getId() == PermsLevels.MEMBER.getId()) {
                player.sendMessage(
                        new LiteralText("You don't have permission to run this command."),
                        false
                );
                return 1;
            }
        } else {
            player.sendMessage(
                    new LiteralText("Error."),
                    false
            );
            return 1;
        }
        player.changeGameMode(GameMode.SPECTATOR);
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.NIGHT_VISION,
                999999,
                0,
                false,
                false
        ));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.CONDUIT_POWER,
                999999,
                0,
                false,
                false
        ));
        return 1;
    }
}
