package com.kahzerx.kahzerxmod.extensions.cameraExtension;

import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class CameraCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CameraExtension camera) {
        dispatcher.register(literal("c").
                requires(serverCommandSource -> {
                    try {
                        if (camera.extensionSettings().isEnabled() && camera.permsExtension.extensionSettings().isEnabled()) {
                            return camera.permsExtension.getDBPlayerPerms(serverCommandSource.getPlayer().getUuidAsString()).getId() > PermsLevels.MEMBER.getId();
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).
                executes(context -> camera.setCameraMode(context.getSource())));
    }
}
