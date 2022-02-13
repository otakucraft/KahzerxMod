package com.kahzerx.kahzerxmod.extensions.discordExtension.commands;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordPermission;
import com.kahzerx.kahzerxmod.klone.KlonePlayerEntity;
import com.kahzerx.kahzerxmod.extensions.discordExtension.utils.DiscordChatUtils;
import com.kahzerx.kahzerxmod.extensions.discordExtension.utils.DiscordUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Objects;

public class OnlineCommand extends GenericCommand {
    public OnlineCommand(String prefix) {
        super("online", DiscordPermission.ALLOWED_CHAT, prefix + "online");
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server, String serverPrefix, List<Long> allowedChats) {
        if (!DiscordUtils.isAllowed(event.getChannel().getIdLong(), allowedChats)) {
            return;
        }
        StringBuilder msg = new StringBuilder();
        int n = server.getPlayerManager().getPlayerList().size();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            boolean isBot = player.getClass() == KlonePlayerEntity.class;
            msg.append(player.getName().getString().replace("_", "\\_")).append(isBot ? " [Bot]" : "").append("\n");
        }
        event.getChannel().sendMessageEmbeds(Objects.requireNonNull(DiscordChatUtils.generateEmbed(msg, n, serverPrefix)).build()).queue();
    }
}
