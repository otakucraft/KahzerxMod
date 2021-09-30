package com.kahzerx.kahzerxmod.extensions.cameraExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class CameraCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CameraExtension camera) {
        dispatcher.register(literal("c").
                executes(context -> camera.setCameraMode(context.getSource())));
    }
}
