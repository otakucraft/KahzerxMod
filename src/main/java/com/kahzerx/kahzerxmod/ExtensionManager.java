package com.kahzerx.kahzerxmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kahzerx.kahzerxmod.config.KSettings;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.afkExtension.AFKExtension;
import com.kahzerx.kahzerxmod.extensions.backExtension.BackExtension;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.BlockInfoExtension;
import com.kahzerx.kahzerxmod.extensions.cameraExtension.CameraExtension;
import com.kahzerx.kahzerxmod.extensions.deathMsgExtension.DeathMsgExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordAdminToolsExtension.DiscordAdminToolsExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordAdminToolsExtension.DiscordAdminToolsJsonSettings;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordAdminToolsExtension.DiscordAdminToolsSettings;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordJsonSettings;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordSettings;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistJsonSettings;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistSettings;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension.DiscordWhitelistSyncExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension.DiscordWhitelistSyncJsonSettings;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension.DiscordWhitelistSyncSettings;
import com.kahzerx.kahzerxmod.extensions.fckPrivacyExtension.FckPrivacyExtension;
import com.kahzerx.kahzerxmod.extensions.hereExtension.HereExtension;
import com.kahzerx.kahzerxmod.extensions.homeExtension.HomeExtension;
import com.kahzerx.kahzerxmod.extensions.memberExtension.MemberExtension;
import com.kahzerx.kahzerxmod.extensions.modTPExtension.ModTPExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsExtension;
import com.kahzerx.kahzerxmod.extensions.pitoExtension.PitoExtension;
import com.kahzerx.kahzerxmod.extensions.randomTPExtension.RandomTPExtension;
import com.kahzerx.kahzerxmod.extensions.scoreboardExtension.ScoreboardExtension;
import com.kahzerx.kahzerxmod.extensions.seedExtension.SeedExtension;
import com.kahzerx.kahzerxmod.extensions.spoofExtension.SpoofExtension;
import com.kahzerx.kahzerxmod.extensions.survivalExtension.SurvivalExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExtensionManager {
    public static void manageExtensions(String settings) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        KSettings ks = gson.fromJson(settings, KSettings.class);
        HashMap<String, Boolean> found = new HashMap<>();

        if (ks != null) {
            for (ExtensionSettings es : ks.getSettings()) {
                if (es == null) {
                    continue;
                }
                found.put(es.getName(), es.isEnabled());
            }
        }

        PermsExtension permsExtension = new PermsExtension(
                new ExtensionSettings(
                        "perms",
                        found.get("perms") != null ? found.get("perms") : true,
                        "Permission levels for other commands like /back, /c or /modTP. Enables /perms command."));
        KahzerxServer.extensions.add(permsExtension);
        KahzerxServer.extensions.add(new HomeExtension(
                new ExtensionSettings(
                        "home",
                        found.get("home") != null ? found.get("home") : true,
                        "/home and /setHome commands.")));
        KahzerxServer.extensions.add(new BackExtension(
                new ExtensionSettings(
                        "back",
                        found.get("back") != null ? found.get("back") : true,
                        "/back command to tp to the last death position."),
                permsExtension));
        KahzerxServer.extensions.add(new CameraExtension(
                new ExtensionSettings(
                        "camera",
                        found.get("camera") != null ? found.get("camera") : true,
                        "/c, spectator + night vision + conduit (stolen from carpet)."),
                permsExtension));
        KahzerxServer.extensions.add(new ModTPExtension(
                new ExtensionSettings(
                        "modTP",
                        found.get("modTP") != null ? found.get("modTP") : true,
                        "Enables /modTP that allows players with mod perms to tp to other players."
                ),
                permsExtension));
        KahzerxServer.extensions.add(new SurvivalExtension(
                new ExtensionSettings(
                        "survival",
                        found.get("survival") != null ? found.get("survival") : true,
                        "/s, survival - night vision - conduit (stolen from carpet).")));
        KahzerxServer.extensions.add(new PitoExtension(
                new ExtensionSettings(
                        "pito",
                        found.get("pito") != null ? found.get("pito") : true,
                        "/pito ¯\\_(ツ)_/¯")));
        KahzerxServer.extensions.add(new HereExtension(
                new ExtensionSettings(
                        "here",
                        found.get("here") != null ? found.get("here") : true,
                        "/here, print current location + glowing 5 seconds.")));
        KahzerxServer.extensions.add(new DeathMsgExtension(
                new ExtensionSettings(
                        "deathMessage",
                        found.get("deathMessage") != null ? found.get("deathMessage") : true,
                        "Print death position when player dies.")));
        KahzerxServer.extensions.add(new AFKExtension(
                new ExtensionSettings(
                        "afk",
                        found.get("afk") != null ? found.get("afk") : true,
                        "/afk idk everyone keeps asking for this thing, it literally kicks you from the server.")));
        KahzerxServer.extensions.add(new RandomTPExtension(
                new ExtensionSettings(
                        "randomTP",
                        found.get("randomTP") != null ? found.get("randomTP") : true,
                        "randomTP in a 10k block radius.")));
        KahzerxServer.extensions.add(new BlockInfoExtension(
                new ExtensionSettings(
                        "blockInfo",
                        found.get("blockInfo") != null ? found.get("blockInfo") : true,
                        "Player action logging and /blockInfo command.")));
        KahzerxServer.extensions.add(new MemberExtension(
                new ExtensionSettings(
                        "member",
                        found.get("member") != null ? found.get("member") : true,
                        "Gives member role on player first joined.")));
        KahzerxServer.extensions.add(new SeedExtension(
                new ExtensionSettings(
                        "seed",
                        found.get("seed") != null ? found.get("seed") : true,
                        "Enables seed command for everyone in the server.")));
        KahzerxServer.extensions.add(new FckPrivacyExtension(
                new ExtensionSettings(
                        "fckPrivacy",
                        found.get("fckPrivacy") != null ? found.get("fckPrivacy") : true,
                        "Saves every executed command including private messages in the logs file, like /msg name hello.")));
        KahzerxServer.extensions.add(new SpoofExtension(
                new ExtensionSettings(
                        "spoof",
                        found.get("spoof") != null ? found.get("spoof") : true,
                        "Enables /spoof command that allows OP players to see other connected players enderchest and inventories, player inventory may not work correctly so unless you know what you are doing is not recommended to move items from the slots.")));
        KahzerxServer.extensions.add(new ScoreboardExtension(
                new ExtensionSettings(
                        "scoreboard",
                        found.get("scoreboard") != null ? found.get("scoreboard") : true,
                        "Enables /sb command.")));

        String token = "";
        boolean crossServerChat = false;
        String prefix = "";
        boolean isRunning = false;
        long chatChannelID = 0L;
        List<Long> allowedChats = new ArrayList<>();
        DiscordJsonSettings djs = gson.fromJson(settings, DiscordJsonSettings.class);
        if (djs != null) {
            for (DiscordSettings ds : djs.getSettings()) {
                if (ds == null) {
                    continue;
                }
                if (ds.getName().equals("discord")) {
                    token = ds.getToken() != null ? ds.getToken() : "";
                    crossServerChat = ds.isCrossServerChat();
                    prefix = ds.getPrefix() != null ? ds.getPrefix().replaceAll(" ", "_") : "";
                    isRunning = ds.isRunning();
                    chatChannelID = ds.getChatChannelID();
                    allowedChats = ds.getAllowedChats() != null ? ds.getAllowedChats() : new ArrayList<>();
                    break;
                }
            }
        }
        DiscordExtension discordExtension = new DiscordExtension(
                new DiscordSettings(
                        "discord",
                        found.get("discord") != null ? found.get("discord") : false,
                        "Connects minecraft chat + some events with a discord chat (chatbridge). Prefix is necessary if you want crossServerChat to work properly and not having duplicated messages.",
                        token,
                        crossServerChat,
                        prefix,
                        isRunning,
                        chatChannelID,
                        allowedChats
                ));
        KahzerxServer.extensions.add(discordExtension);

        long discordRoleID = 0L;
        List<Long> whitelistChats = new ArrayList<>();
        int nPlayers = 1;
        DiscordWhitelistJsonSettings dwjs = gson.fromJson(settings, DiscordWhitelistJsonSettings.class);
        if (dwjs != null) {
            for (DiscordWhitelistSettings dws : dwjs.getSettings()) {
                if (dws == null) {
                    continue;
                }
                if (dws.getName().equals("discordWhitelist")) {
                    discordRoleID = dws.getDiscordRole();
                    whitelistChats = dws.getWhitelistChats() != null ? dws.getWhitelistChats() : new ArrayList<>();
                    nPlayers = dws.getNPlayers() != 0 ? dws.getNPlayers() : 1;
                    break;
                }
            }
        }
        DiscordWhitelistExtension discordWhitelistExtension = new DiscordWhitelistExtension(
                new DiscordWhitelistSettings(
                        "discordWhitelist",
                        (found.get("discordWhitelist") != null ? found.get("discordWhitelist") : true) && (discordExtension.extensionSettings().isEnabled()),
                        "Enables !list, !add and !remove commands along with nPlayers that specifies how many minecraft players a discord user can add; There is also an optional discordRole that will be given to the discord user on !add and deleted on !remove.",
                        whitelistChats,
                        discordRoleID,
                        nPlayers
                ),
                discordExtension);
        KahzerxServer.extensions.add(discordWhitelistExtension);

        List<Long> adminChats = new ArrayList<>();
        DiscordAdminToolsJsonSettings datjs = gson.fromJson(settings, DiscordAdminToolsJsonSettings.class);
        if (dwjs != null) {
            for (DiscordAdminToolsSettings dats : datjs.getSettings()) {
                if (dats == null) {
                    continue;
                }
                if (dats.getName().equals("discordAdminTools")) {
                    adminChats = dats.getAdminChats() != null ? dats.getAdminChats() : new ArrayList<>();
                    break;
                }
            }
        }
        KahzerxServer.extensions.add(new DiscordAdminToolsExtension(
                new DiscordAdminToolsSettings(
                        "discordAdminTools",
                        (found.get("discordWhitelist") != null ? found.get("discordWhitelist") : true) && (discordExtension.extensionSettings().isEnabled()) && (discordWhitelistExtension.extensionSettings().isEnabled()),
                        "Enables !ban, !pardon, !exadd, !exremove on discord AdminChats.",
                        adminChats
                ),
                discordExtension,
                discordWhitelistExtension));

        List<Long> validRoles = new ArrayList<>();
        long notifyChannelID = 0L;
        long groupID = 0L;
        boolean aggressive = false;
        DiscordWhitelistSyncJsonSettings dwsjs = gson.fromJson(settings, DiscordWhitelistSyncJsonSettings.class);
        if (dwsjs != null) {
            for (DiscordWhitelistSyncSettings dwss : dwsjs.getSettings()) {
                if (dwss == null) {
                    continue;
                }
                if (dwss.getName().equals("discordWhitelistSync")) {
                    validRoles = dwss.getValidRoles() != null ? dwss.getValidRoles() : new ArrayList<>();
                    notifyChannelID = dwss.getNotifyChannelID();
                    groupID = dwss.getGroupID();
                    aggressive = dwss.isAggressive();
                }
            }
        }
        KahzerxServer.extensions.add(new DiscordWhitelistSyncExtension(
                new DiscordWhitelistSyncSettings(
                        "discordWhitelistSync",
                        (found.get("discordWhitelistSync") != null ? found.get("discordWhitelistSync") : true) && (discordExtension.extensionSettings().isEnabled()) && (discordWhitelistExtension.extensionSettings().isEnabled()),
                        "Check if people that did !add have a given discord role, if not they will get automatically removed from whitelist, useful for sub twitch role. The groupID is the ID of the discord server/guild. The aggressive mode will force whitelist and discord database have the same users so any player added with /whitelist add will get removed on autosave.",
                        notifyChannelID,
                        validRoles,
                        groupID,
                        aggressive
                ),
                discordExtension,
                discordWhitelistExtension));
    }
}
