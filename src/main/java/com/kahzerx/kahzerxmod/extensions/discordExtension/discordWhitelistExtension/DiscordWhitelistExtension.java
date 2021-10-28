package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.database.ServerQuery;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordCommandsExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordListener;
import com.kahzerx.kahzerxmod.extensions.discordExtension.commands.AddCommand;
import com.kahzerx.kahzerxmod.extensions.discordExtension.commands.ListCommand;
import com.kahzerx.kahzerxmod.extensions.discordExtension.commands.RemoveCommand;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordExtension;
import com.kahzerx.kahzerxmod.mixin.discordWhitelistExtension.PlayerManagerWLMixin;
import com.kahzerx.kahzerxmod.utils.DiscordChatUtils;
import com.kahzerx.kahzerxmod.utils.DiscordUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.minecraft.server.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DiscordWhitelistExtension extends GenericExtension implements Extensions, DiscordCommandsExtension {
    private final DiscordExtension discordExtension;
    private Connection conn;

    public static boolean isExtensionEnabled = false;

    private final AddCommand addCommand = new AddCommand(DiscordListener.commandPrefix);
    private final RemoveCommand removeCommand = new RemoveCommand(DiscordListener.commandPrefix);
    private final ListCommand listCommand = new ListCommand(DiscordListener.commandPrefix);

    public DiscordWhitelistExtension(DiscordWhitelistSettings settings, DiscordExtension discordExtension) {
        super(settings);
        this.discordExtension = discordExtension;
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        this.conn = conn;
        if (!this.getSettings().isEnabled()) {
            return;
        }
        if (!discordExtension.getSettings().isEnabled()) {
            return;
        }
        try {
            String createDiscordDatabase = "CREATE TABLE IF NOT EXISTS `discord`(" +
                    "`uuid` VARCHAR(50) UNIQUE NOT NULL," +
                    "`discordID` NUMERIC NOT NULL," +
                    "FOREIGN KEY(uuid) REFERENCES player(uuid)," +
                    "PRIMARY KEY (uuid, discordID));";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createDiscordDatabase);
            stmt.close();

            String createBannedDiscordDatabase = "CREATE TABLE IF NOT EXISTS `discord_banned`(" +
                    "`discordID` NUMERIC PRIMARY KEY NOT NULL," +
                    "FOREIGN KEY(discordID) REFERENCES discord(discordID));";
            Statement stmt_ = conn.createStatement();
            stmt_.executeUpdate(createBannedDiscordDatabase);
            stmt_.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        if (!this.getSettings().isEnabled()) {
            return;
        }
        if (!discordExtension.getSettings().isEnabled()) {
            return;
        }
        DiscordListener.discordExtensions.add(this);
        isExtensionEnabled = true;
    }

    public boolean isDiscordBanned(long discordID) {
        boolean banned = false;
        try {
            String getBan = "SELECT discordID FROM discord_banned WHERE discordID = ?;";
            PreparedStatement ps = conn.prepareStatement(getBan);
            ps.setLong(1, discordID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                banned = true;
            }
            rs.close();
            ps.close();
            return banned;
        } catch (SQLException e) {
            e.printStackTrace();
            return banned;
        }
    }

    public boolean canRemove(long discordID, String uuid) {
        boolean remove = false;
        try {
            String getPlayers = "SELECT discordID FROM discord WHERE uuid = ? AND discordID = ?;";
            PreparedStatement ps = conn.prepareStatement(getPlayers);
            ps.setString(1, uuid);
            ps.setLong(2, discordID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                remove = true;
            }
            rs.close();
            ps.close();
            return remove;
        } catch (SQLException e) {
            e.printStackTrace();
            return remove;
        }
    }

    public boolean alreadyAddedBySomeone(String uuid) {
        boolean added = false;
        try {
            String already = "SELECT * FROM discord WHERE uuid = ?;";
            PreparedStatement ps = conn.prepareStatement(already);
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                added = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return added;
    }

    public boolean alreadyAddedBySomeone(UUID uuid) {
        return alreadyAddedBySomeone(uuid.toString());
    }

    public boolean userReachedMaxPlayers(long discordID) {
        boolean canAdd = false;
        try {
            String getPlayers = "SELECT COUNT(*) AS rows FROM discord WHERE discordID = ?;";
            PreparedStatement ps = conn.prepareStatement(getPlayers);
            ps.setLong(1, discordID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("rows") < this.extensionSettings().getNPlayers()) {
                    canAdd = true;
                }
            } else {
                canAdd = true;
            }
            rs.close();
            ps.close();
            return canAdd;
        } catch (SQLException e) {
            e.printStackTrace();
            return canAdd;
        }
    }

    public boolean isPlayerBanned(String uuid) {
        try {
            boolean isBanned = false;
            String getPlayers = "SELECT discordID FROM discord WHERE uuid = ?;";
            PreparedStatement ps = conn.prepareStatement(getPlayers);
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long discordID = rs.getLong("discordID");
                isBanned = isDiscordBanned(discordID);
            }
            rs.close();
            ps.close();
            return isBanned;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public long getDiscordID(String uuid) {
        long id = 0L;
        try {
            String getPlayers = "SELECT discordID FROM discord WHERE uuid = ?;";
            PreparedStatement ps = conn.prepareStatement(getPlayers);
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getLong("discordID");
            }
            rs.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return id;
        }
    }

    public void banDiscord(long discordID) {
        try {
            String insertPlayer = "INSERT OR IGNORE INTO discord_banned (discordID) VALUES (?);";
            PreparedStatement ps = conn.prepareStatement(insertPlayer);
            ps.setLong(1, discordID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void pardonDiscord(long discordID) {
        try {
            String insertPlayer = "DELETE FROM discord_banned WHERE discordID = ?;";
            PreparedStatement ps = conn.prepareStatement(insertPlayer);
            ps.setLong(1, discordID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getWhitelistedPlayers(long discordID) {
        ArrayList<String> players = new ArrayList<>();
        try {
            String q = "SELECT uuid FROM discord WHERE discordID = ?;";
            PreparedStatement ps = this.conn.prepareStatement(q);
            ps.setLong(1, discordID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                players.add(rs.getString("uuid"));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return players;
    }

    public ArrayList<Long> getDiscordIDs() {
        ArrayList<Long> IDs = new ArrayList<>();
        try {
            String q = "SELECT discordID FROM discord;";
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(q);
            while (rs.next()) {
                long newID = rs.getLong("discordID");
                if (!IDs.contains(newID)) {
                    IDs.add(newID);
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return IDs;
    }

    public void deletePlayer(long discordID, String uuid) {
        try {
            String delete = "DELETE FROM discord WHERE uuid = ? AND discordID = ?;";
            PreparedStatement ps = conn.prepareStatement(delete);
            ps.setString(1, uuid);
            ps.setLong(2, discordID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayer(long discordID, String uuid, String playerName) {
        try {
            ServerQuery q = new ServerQuery(conn);
            q.insertPlayerUUID(uuid, playerName);

            String insertPlayer = "INSERT OR IGNORE INTO discord (uuid, discordID) VALUES (?, ?);";
            PreparedStatement ps = conn.prepareStatement(insertPlayer);
            ps.setString(1, uuid);
            ps.setLong(2, discordID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void tryVanillaBan(BannedPlayerList banList, GameProfile profile, MinecraftServer server) {
        if (!banList.contains(profile)) {
            BannedPlayerEntry playerEntry = new BannedPlayerEntry(profile, null, "DiscordBan", null, null);
            banList.add(playerEntry);
            ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(profile.getId());
            if (serverPlayerEntity != null) {
                serverPlayerEntity.networkHandler.disconnect(new TranslatableText("Te han baneado :)"));
            }
        }
    }

    public void tryVanillaPardon(BannedPlayerList banList, GameProfile profile) {
        if (banList.contains(profile)) {
            banList.remove(profile);
        }
    }

    public void tryVanillaWhitelistRemove(Whitelist whitelist, GameProfile profile, MinecraftServer server) {
        if (whitelist.isAllowed(profile)) {
            WhitelistEntry whitelistEntry = new WhitelistEntry(profile);
            whitelist.remove(whitelistEntry);
            ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(profile.getId());
            if (serverPlayerEntity != null) {
                serverPlayerEntity.networkHandler.disconnect(new TranslatableText("Byee~"));
            }
        }
    }

    @Override
    public DiscordWhitelistSettings extensionSettings() {
        return (DiscordWhitelistSettings) this.getSettings();
    }

    @Override
    public void onExtensionEnabled() {
        if (!DiscordListener.discordExtensions.contains(this)) {
            DiscordListener.discordExtensions.add(this);
        }
        this.onCreateDatabase(this.conn);
        isExtensionEnabled = true;
    }

    @Override
    public void onExtensionDisabled() {
        DiscordListener.discordExtensions.remove(this);
        isExtensionEnabled = false;
    }

    @Override
    public boolean processCommands(MessageReceivedEvent event, String message, MinecraftServer server) {
        if (!this.getSettings().isEnabled()) {
            return false;
        }
        if (!discordExtension.getSettings().isEnabled()) {
            return false;
        }
        if (!DiscordUtils.isAllowed(event.getChannel().getIdLong(), this.extensionSettings().getWhitelistChats())) {
            if (message.startsWith(DiscordListener.commandPrefix + addCommand.getBody())
                    || message.startsWith(DiscordListener.commandPrefix + removeCommand.getBody())
                    || message.startsWith(DiscordListener.commandPrefix + listCommand.getBody())) {
                event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
                EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**Usa bien los canales!!! >:(**"}, discordExtension.extensionSettings().getPrefix(), true, Color.RED, true);
                assert embed != null;
                MessageAction embedSent = event.getChannel().sendMessageEmbeds(embed.build());
                embedSent.queue(m -> m.delete().queueAfter(2, TimeUnit.SECONDS));
                return true;
            }
        }
        if (message.startsWith(DiscordListener.commandPrefix + addCommand.getBody() + " ")) {
            addCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), this);
            return true;
        } else if (message.startsWith(DiscordListener.commandPrefix + removeCommand.getBody() + " ")) {
            removeCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), this);
            return true;
        } else if (message.equals(DiscordListener.commandPrefix + listCommand.getBody())) {
            listCommand.execute(event, server, discordExtension.extensionSettings().getPrefix());
            return true;
        }
        return false;
    }

    @Override
    public void settingsCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.
                then(literal("discordRoleID").
                        then(argument("discordRole", LongArgumentType.longArg()).
                                executes(context -> {
                                    extensionSettings().setDiscordRoleID(LongArgumentType.getLong(context, "discordRoleID"));
                                    context.getSource().sendFeedback(new LiteralText("[discordRole] > " + extensionSettings().getDiscordRole() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Role que se da a toda la gente que se añade a la whitelist.";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            context.getSource().sendFeedback(new LiteralText("[discordRole] > " + extensionSettings().getDiscordRole() + "."), false);
                            return 1;
                        })).
                then(literal("nPlayers").
                        then(argument("nPlayers", IntegerArgumentType.integer()).
                                executes(context -> {
                                    extensionSettings().setNPlayers(IntegerArgumentType.getInteger(context, "nPlayers"));
                                    context.getSource().sendFeedback(new LiteralText("[players] > " + extensionSettings().getNPlayers() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Numero de Players que un user de discord puede añadir a la whitelist.";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            context.getSource().sendFeedback(new LiteralText("[players] > " + extensionSettings().getNPlayers() + "."), false);
                            return 1;
                        })).
                then(literal("whitelistChats").
                        then(literal("add").
                                then(argument("chatID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getWhitelistChats().contains(LongArgumentType.getLong(context, "chatID"))) {
                                                context.getSource().sendFeedback(new LiteralText("Este ID ya está añadido."), false);
                                            } else {
                                                extensionSettings().addWhitelistChatID(LongArgumentType.getLong(context, "chatID"));
                                                context.getSource().sendFeedback(new LiteralText("ID añadido."), false);
                                                ExtensionManager.saveSettings();
                                            }
                                            return 1;
                                        }))).
                        then(literal("remove").
                                then(argument("chatID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getWhitelistChats().contains(LongArgumentType.getLong(context, "chatID"))) {
                                                extensionSettings().removeWhitelistChatID(LongArgumentType.getLong(context, "chatID"));
                                                context.getSource().sendFeedback(new LiteralText("ID eliminado."), false);
                                                ExtensionManager.saveSettings();
                                            } else {
                                                context.getSource().sendFeedback(new LiteralText("Este ID no estaba añadido."), false);
                                            }
                                            return 1;
                                        }))).
                        then(literal("list").
                                executes(context -> {
                                    context.getSource().sendFeedback(new LiteralText(extensionSettings().getWhitelistChats().toString()), false);
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Lista de ChatIDs en los que el comando !add, !remove y !list funcionan.";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            return 1;
                        }));
    }
}
