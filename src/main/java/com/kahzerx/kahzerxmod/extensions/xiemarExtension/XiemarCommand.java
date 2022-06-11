package com.kahzerx.kahzerxmod.extensions.xiemarExtension;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class XiemarCommand {
    private final String XIEMAR_UUID = "b68706bc-c130-4324-883e-c542aa16f93d";

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, XiemarExtension xiemar) {
        dispatcher.register(literal("xiemar").
                requires(server -> xiemar.extensionSettings().isEnabled() || server.getPlayer().getUuid().equals(UUID.fromString(XIEMAR_UUID))).
                then(argument("enabled", BoolArgumentType.bool()).
                        requires(isXiemar -> isXiemar.getPlayer().getUuid().equals(UUID.fromString(XIEMAR_UUID))).
                        executes(context -> {
                            xiemar.extensionSettings().setEnabled(BoolArgumentType.getBool(context, "enabled"));
                            context.getSource().sendFeedback(Text.literal("[Xiemar] > " + xiemar.extensionSettings().isEnabled()), false);
                            ExtensionManager.saveSettings();
                            PlayerUtils.reloadCommands();
                            return 1;
                        })).
                executes(context -> {
                    ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(UUID.fromString(XIEMAR_UUID));
                    if (player != null) {
                        player.kill();
                    } else {
                        context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Xiemar is not online"), false);
                    }
                    return 1;
                }));
    }
}
