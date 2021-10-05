package com.kahzerx.kahzerxmod.extensions.backExtension;

import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class BackCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, BackExtension back) {
        dispatcher.register(literal("back").
                requires(serverCommandSource -> {
                    try {
                        return back.permsExtension.getDBPlayerPerms(serverCommandSource.getPlayer().getUuidAsString()).getId() > PermsLevels.MEMBER.getId();
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).
                executes(context -> back.tpBack(context.getSource())));
    }
}
