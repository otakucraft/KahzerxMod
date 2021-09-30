package com.kahzerx.kahzerxmod.extensions.modTPExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class ModTPExtension extends GenericExtension implements Extensions {
    private final PermsExtension permsExtension;
    public ModTPExtension(ExtensionSettings settings, PermsExtension perms) {
        super(settings);
        this.permsExtension = perms;
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!this.getSettings().isEnabled()) {
            return;
        }
        if (!permsExtension.getSettings().isEnabled()) {
            return;
        }
        new ModTPCommand().register(dispatcher, this);
    }

    public int tp(ServerCommandSource source, String playerName) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(playerName);
        if (player == null) {
            source.sendFeedback(new LiteralText("No está conectado XD."), false);
            return 1;
        }
        if (permsExtension.getPlayerPerms().containsKey(player.getUuidAsString())) {
            if (permsExtension.getPlayerPerms().get(player.getUuidAsString()).getId() == PermsLevels.MEMBER.getId()) {
                source.sendFeedback(new LiteralText("No tienes permisos para ejecutar este comando."), false);
                return 1;
            }
        } else {
            source.sendFeedback(new LiteralText("Error al ejecutar, intenta reconectarte o contacta con un admin."), false);
            return 1;
        }
        ServerPlayerEntity sourcePlayer = source.getPlayer();
        sourcePlayer.teleport(player.getServerWorld(), player.getX(), player.getY(), player.getZ(), sourcePlayer.getYaw(), sourcePlayer.getPitch());
        return 1;
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
}
