package com.kahzerx.kahzerxmod.extensions.shopExtension.parcel;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordListener;
import com.kahzerx.kahzerxmod.extensions.discordExtension.utils.DiscordChatUtils;
import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.TimerTask;
import java.util.UUID;

public class ParcelPayoutCheckThread extends TimerTask {
    private static final Logger LOGGER = LogManager.getLogger();
    private ShopExtension extension;
    private MinecraftServer server;

    public ParcelPayoutCheckThread(ShopExtension extension, MinecraftServer server) {
        this.extension = extension;
        this.server = server;
    }

    @Override
    public void run() {
        if (!extension.extensionSettings().isEnabled()) {
            return;
        }
        JDA jda = DiscordListener.jda;
        Guild guild = null;
        if (jda != null) {
            guild = DiscordListener.jda.getGuildById("607849606532956161");
        }
        TextChannel channel = null;
        if (guild != null) {
            channel = guild.getTextChannelById("943660665296535572");
        }
        for (Parcel parcel : extension.getParcels().getOwnedParcels()) {
            int centerX = (parcel.getCorner1().getX() + parcel.getCorner2().getX()) / 2;
            int centerZ = (parcel.getCorner1().getZ() + parcel.getCorner2().getZ()) / 2;
            String uuid = parcel.getOwnerUUID();
            String playerName = extension.getDB().getQuery().getPlayerName(uuid);
            if (playerName.equals("") || uuid.equals("")) {
                String message = String.format("Unable to find a valid owner for parcel %d %d on dim %s, removing owner reference...", centerX, centerZ, DimUtils.getWorldString(parcel.getDim()));
                LOGGER.info(message);
                parcel.setOwnerUUID(null);
                parcel.setNextPayout(null);
                parcel.setName(null);
                extension.getDB().getQuery().giveParcel(parcel, null, null);
                continue;
            }
            Timestamp timestamp = parcel.getNextPayout();
            if (timestamp.before(Timestamp.valueOf(LocalDateTime.now()))) {
                int price = parcel.getPrice();
                int playerBalance = extension.getDB().getQuery().getBalance(uuid);
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(UUID.fromString(uuid));
                if (playerBalance < price) {
                    String message = String.format("%s no tiene balance suficiente para pagar la tienda en %d %d dim %s", playerName, centerX, centerZ, DimUtils.getWorldString(parcel.getDim()));
                    parcel.setOwnerUUID(null);
                    parcel.setNextPayout(null);
                    parcel.setName(null);
                    extension.getDB().getQuery().giveParcel(parcel, null, null);
                    if (player != null) {
                        player.sendMessage(MarkEnum.CROSS.appendMessage(String.format("No tienes balance suficiente para pagar tu parcela en %d %d dim %s, avisa a helpers para recuperarla", centerX, centerZ, DimUtils.getWorldString(parcel.getDim()))));
                    }
                    LOGGER.info(message);
                    EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{message}, "", true, Color.RED, true, true);
                    if (channel != null && embed != null) {
                        channel.sendMessageEmbeds(embed.build()).queue();
                    }
                } else {
                    Timestamp nextTimestamp = Timestamp.valueOf(LocalDateTime.now().plusMonths(1));
                    String bankUUID = "00000000-0000-0000-0000-000000000000";
                    extension.getDB().getQuery().updateFounds(bankUUID, price, extension.getAccounts());
                    extension.getDB().getQuery().updateFounds(uuid, price * -1, extension.getAccounts());
                    extension.getDB().getQuery().logTransfer(uuid, bankUUID, playerName, price, extension.getAccounts());
                    parcel.setNextPayout(nextTimestamp);
                    extension.getDB().getQuery().giveParcel(parcel, uuid, nextTimestamp);
                    if (player != null) {
                        player.sendMessage(MarkEnum.INFO.appendMessage(String.format("Has pagado tus %d mensuales de tu parcela en %d %d dim %s", price, centerX, centerZ, DimUtils.getWorldString(parcel.getDim()))));
                    }
                    String message = String.format("%s ha pagado %d por su parcela en %d %d dim %s", playerName, price, centerX, centerZ, DimUtils.getWorldString(parcel.getDim()));
                    LOGGER.info(message);
                    EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{message}, "", true, Color.GREEN, true, true);
                    if (channel != null && embed != null) {
                        channel.sendMessageEmbeds(embed.build()).queue();
                    }
                }
            }
        }
    }
}
