package com.kahzerx.kahzerxmod.extensions.joinMOTDExtension;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class JoinMOTDCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, JoinMOTDExtension joinMOTD) {
        dispatcher.register(literal("joinMOTD").
                requires(server -> {
                    if (joinMOTD.extensionSettings().isEnabled() && joinMOTD.getPermsExtension().extensionSettings().isEnabled()) {
                        return joinMOTD.getPermsExtension().getDBPlayerPerms(server.getPlayer().getUuidAsString()).getId() >= PermsLevels.HELPER.getId();
                    }
                    return false;
                }).
                then(literal("set").
                        then(argument("message", MessageArgumentType.message()).
                                executes(context -> {
                                    joinMOTD.updateMessage(context.getSource(), MessageArgumentType.getMessage(context, "message").getString());
                                    ExtensionManager.saveSettings();
                                    return 1;
                                }))).
                then(literal("clear").
                        executes(context -> {
                            joinMOTD.extensionSettings().setMessage("");
                            ExtensionManager.saveSettings();
                            return 1;
                        })).
                executes(context -> {
                    context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Actual MOTD!"), false);
                    context.getSource().sendFeedback(joinMOTD.getFormatted(joinMOTD.extensionSettings().getMessage()), false);
                    return 1;
                }));
    }
}
