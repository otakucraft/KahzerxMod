package com.kahzerx.kahzerxmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kahzerx.kahzerxmod.config.KSettings;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.achusExtension.AchusExtension;
import com.kahzerx.kahzerxmod.extensions.afkExtension.AFKExtension;
import com.kahzerx.kahzerxmod.extensions.backExtension.BackExtension;
import com.kahzerx.kahzerxmod.extensions.badgeExtension.BadgeExtension;
import com.kahzerx.kahzerxmod.extensions.bedTimeExtension.BedTimeExtension;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.BlockInfoExtension;
import com.kahzerx.kahzerxmod.extensions.bocaExtension.BocaExtension;
import com.kahzerx.kahzerxmod.extensions.cameraExtension.CameraExtension;
import com.kahzerx.kahzerxmod.extensions.deathMsgExtension.DeathMsgExtension;
import com.kahzerx.kahzerxmod.extensions.deepslateInstaMineExtension.DeepslateInstaMineExtension;
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
import com.kahzerx.kahzerxmod.extensions.endermanNoGriefExtension.EndermanNoGriefExtension;
import com.kahzerx.kahzerxmod.extensions.farmlandMyceliumExtension.FarmlandMyceliumExtension;
import com.kahzerx.kahzerxmod.extensions.fckPrivacyExtension.FckPrivacyExtension;
import com.kahzerx.kahzerxmod.extensions.hatExtension.HatExtension;
import com.kahzerx.kahzerxmod.extensions.helperKickExtension.HelperKickExtension;
import com.kahzerx.kahzerxmod.extensions.hereExtension.HereExtension;
import com.kahzerx.kahzerxmod.extensions.homeExtension.HomeExtension;
import com.kahzerx.kahzerxmod.extensions.itemFormattedExtension.ItemFormattedExtension;
import com.kahzerx.kahzerxmod.extensions.kloneExtension.KloneExtension;
import com.kahzerx.kahzerxmod.extensions.maintenanceExtension.MaintenanceExtension;
import com.kahzerx.kahzerxmod.extensions.memberExtension.MemberExtension;
import com.kahzerx.kahzerxmod.extensions.modTPExtension.ModTPExtension;
import com.kahzerx.kahzerxmod.extensions.opOnWhitelistExtension.OpOnWhitelistExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsExtension;
import com.kahzerx.kahzerxmod.extensions.pitoExtension.PitoExtension;
import com.kahzerx.kahzerxmod.extensions.playerDropsSkullExtension.PlayerDropsSkullExtension;
import com.kahzerx.kahzerxmod.extensions.prankExtension.PrankExtension;
import com.kahzerx.kahzerxmod.extensions.profileExtension.ProfileExtension;
import com.kahzerx.kahzerxmod.extensions.randomTPExtension.RandomTPExtension;
import com.kahzerx.kahzerxmod.extensions.renewableElytraExtension.RenewableElytraExtension;
import com.kahzerx.kahzerxmod.extensions.scoreboardExtension.ScoreboardExtension;
import com.kahzerx.kahzerxmod.extensions.seedExtension.SeedExtension;
import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.kahzerx.kahzerxmod.extensions.sitExtension.SitExtension;
import com.kahzerx.kahzerxmod.extensions.skullExtension.SkullExtension;
import com.kahzerx.kahzerxmod.extensions.slabExtension.SlabExtension;
import com.kahzerx.kahzerxmod.extensions.solExtension.SolExtension;
import com.kahzerx.kahzerxmod.extensions.spawnExtension.SpawnExtension;
import com.kahzerx.kahzerxmod.extensions.spoofExtension.SpoofExtension;
import com.kahzerx.kahzerxmod.extensions.survivalExtension.SurvivalExtension;
import com.kahzerx.kahzerxmod.extensions.totopoExtension.TotopoExtension;
import com.kahzerx.kahzerxmod.extensions.villagersFollowEmeraldExtension.VillagersFollowEmeraldExtension;
import com.kahzerx.kahzerxmod.extensions.whereExtension.WhereExtension;
import com.kahzerx.kahzerxmod.extensions.xiemarExtension.XiemarExtension;
import com.kahzerx.kahzerxmod.utils.FileUtils;
import net.minecraft.util.WorldSavePath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExtensionManager {
    public static void saveSettings() {
        List<ExtensionSettings> settingsArray = new ArrayList<>();
        for (Extensions ex : KahzerxServer.extensions) {
            settingsArray.add(ex.extensionSettings());
        }
        KSettings settings = new KSettings(settingsArray);
        FileUtils.createConfig(KahzerxServer.minecraftServer.getSavePath(WorldSavePath.ROOT).toString(), settings);
    }

    private static boolean isEnabled(HashMap<String, Boolean> found, String extension) {
        return found.get(extension) != null ? found.get(extension) : false;
    }

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

        MemberExtension memberExtension = new MemberExtension(new ExtensionSettings("member", isEnabled(found, "member"), "Gives member role on player first joined, it also creates member, mod and admin teams if not exist."));
        PermsExtension permsExtension = new PermsExtension(new ExtensionSettings("perms", isEnabled(found, "perms"), "Permission levels for other commands like /back, /c or /modTP. Enables /kPerms command."), memberExtension);

        KahzerxServer.extensions.add(memberExtension);
        KahzerxServer.extensions.add(permsExtension);
        KahzerxServer.extensions.add(new HomeExtension(new ExtensionSettings("home", isEnabled(found, "home"), "/home and /setHome commands.")));
        KahzerxServer.extensions.add(new BackExtension(new ExtensionSettings("back", isEnabled(found, "back"), "/back command to tp to the last death position."), permsExtension));
        KahzerxServer.extensions.add(new CameraExtension(new ExtensionSettings("camera", isEnabled(found, "camera"), "/c, spectator + night vision + conduit (stolen from carpet)."), permsExtension));
        KahzerxServer.extensions.add(new ModTPExtension(new ExtensionSettings("modTP", isEnabled(found, "modTP"), "Enables /modTP that allows players with mod perms to tp to other players."), permsExtension));
        KahzerxServer.extensions.add(new HelperKickExtension(new ExtensionSettings("helperKick", isEnabled(found, "helperKick"), "Allows helpers and above to run /kick"), permsExtension));
        KahzerxServer.extensions.add(new SurvivalExtension(new ExtensionSettings("survival", isEnabled(found, "survival"), "/s, survival - night vision - conduit (stolen from carpet).")));
        KahzerxServer.extensions.add(new PitoExtension(new ExtensionSettings("pito", isEnabled(found, "pito"), "/pito ¯\\_(ツ)_/¯")));
        KahzerxServer.extensions.add(new HereExtension(new ExtensionSettings("here", isEnabled(found, "here"), "/here, print current location + glowing 5 seconds.")));
        KahzerxServer.extensions.add(new DeathMsgExtension(new ExtensionSettings("deathMessage", isEnabled(found, "deathMessage"), "Print death position when player dies.")));
        KahzerxServer.extensions.add(new AFKExtension(new ExtensionSettings("afk", isEnabled(found, "afk"), "/afk idk everyone keeps asking for this thing, it literally kicks you from the server.")));
        KahzerxServer.extensions.add(new RandomTPExtension(new ExtensionSettings("randomTP", isEnabled(found, "randomTP"), "randomTP in a 10k block radius.")));
        KahzerxServer.extensions.add(new BlockInfoExtension(new ExtensionSettings("blockInfo", isEnabled(found, "blockInfo"), "Player action logging and /blockInfo command.")));
        KahzerxServer.extensions.add(new SeedExtension(new ExtensionSettings("seed", isEnabled(found, "seed"), "Enables seed command for everyone in the server.")));
        KahzerxServer.extensions.add(new FckPrivacyExtension(new ExtensionSettings("fckPrivacy", isEnabled(found, "fckPrivacy"), "Saves every executed command including private messages in the logs file, like /msg name hello.")));
        KahzerxServer.extensions.add(new SpoofExtension(new ExtensionSettings("spoof", isEnabled(found, "spoof"), "Enables /spoof command that allows OP players to see other connected players enderchest and inventories, player inventory may not work correctly so unless you know what you are doing is not recommended to move items from the slots.")));
        KahzerxServer.extensions.add(new ScoreboardExtension(new ExtensionSettings("scoreboard", isEnabled(found, "scoreboard"), "Enables /sb command.")));
        KahzerxServer.extensions.add(new SpawnExtension(new ExtensionSettings("spawn", isEnabled(found, "spawn"), "Enables /spawn.")));
        KahzerxServer.extensions.add(new WhereExtension(new ExtensionSettings("where", isEnabled(found, "where"), "Enables /where.")));
        KahzerxServer.extensions.add(new BocaExtension(new ExtensionSettings("boca", isEnabled(found, "boca"), "Enables /boca & /boquita command.")));
        KahzerxServer.extensions.add(new TotopoExtension(new ExtensionSettings("totopo", isEnabled(found, "totopo"), "Enables /totopo command.")));
        KahzerxServer.extensions.add(new HatExtension(new ExtensionSettings("hat", isEnabled(found, "hat"), "Puts whatever item you have in the main hand on your head.")));
        KahzerxServer.extensions.add(new EndermanNoGriefExtension(new ExtensionSettings("endermanNoGrief", isEnabled(found, "endermanNoGrief"), "Prevents endermans to pickup or place blocks (this will break enderman based farms).")));
        KahzerxServer.extensions.add(new DeepslateInstaMineExtension(new ExtensionSettings("deepslateInstaMine", isEnabled(found, "deepslateInstaMine"), "Deepslate instamine as if it was stone.")));
        KahzerxServer.extensions.add(new RenewableElytraExtension(new ExtensionSettings("renewableElytra", isEnabled(found, "renewableElytra"), "Phantoms killed by shulker have 25% chance of dropping elytras.")));
        KahzerxServer.extensions.add(new VillagersFollowEmeraldExtension(new ExtensionSettings("villagersFollowEmeralds", isEnabled(found, "villagersFollowEmeralds"), "Villagers will follow any player holding emerald blocks.")));
        KahzerxServer.extensions.add(new XiemarExtension(new ExtensionSettings("xiemar", isEnabled(found, "xiemar"), "Literally kills xiemar if connected.")));
        KahzerxServer.extensions.add(new SolExtension(new ExtensionSettings("sol", isEnabled(found, "sol"), "Waifu!")));
        KahzerxServer.extensions.add(new KloneExtension(new ExtensionSettings("klone", isEnabled(found, "klone"), "Clones your player to afk (will kick you); the bot will leave once you rejoin.")));
        KahzerxServer.extensions.add(new MaintenanceExtension(new ExtensionSettings("maintenance", isEnabled(found, "maintenance"), "Sets your server in maintenance mode so only op players can join.")));
        KahzerxServer.extensions.add(new PrankExtension(new ExtensionSettings("pranks", isEnabled(found, "pranks"), "Sets a prank level on your name.")));
        KahzerxServer.extensions.add(new SkullExtension(new ExtensionSettings("skull", isEnabled(found, "skull"), "Gives player heads.")));
        KahzerxServer.extensions.add(new PlayerDropsSkullExtension(new ExtensionSettings("playerDropsSkull", isEnabled(found, "playerDropsSkull"), "Players have a 12% chance of dropping skull on death by trident lightning and a 30% by natural lightning.")));
        KahzerxServer.extensions.add(new BadgeExtension(new ExtensionSettings("badge", isEnabled(found, "badge"), "Badge system, helpers can add badges to players that will display on chat(only last 3), and on chat hover."), permsExtension));
        KahzerxServer.extensions.add(new ItemFormattedExtension(new ExtensionSettings("formattedItems",isEnabled(found, "formattedItems"), "Items renamed on anvils can set format if set on the usual mc formatting replacing § with %.")));
        KahzerxServer.extensions.add(new SlabExtension(new ExtensionSettings("slab", isEnabled(found, "slab"), "Enchants the slab on your main hand with the /slab command so you can always place the upper slab.")));
        KahzerxServer.extensions.add(new AchusExtension(new ExtensionSettings("achus", isEnabled(found, "achus"), "Achus!!")));
        KahzerxServer.extensions.add(new SitExtension(new ExtensionSettings("sit", isEnabled(found, "sit"), "To sit anywhere.")));
        KahzerxServer.extensions.add(new FarmlandMyceliumExtension(new ExtensionSettings("farmlandMycelium", isEnabled(found, "farmlandMycelium"), "Hoe can be used to farm mycelium.")));
        ShopExtension shopExtension = new ShopExtension(new ExtensionSettings("shop", isEnabled(found, "shop"), "Enables currency system along with shop commands and helpers."));
        KahzerxServer.extensions.add(shopExtension);

        ProfileExtension profileExtension = new ProfileExtension(
                new ExtensionSettings("profile", isEnabled(found, "profile"), "Enables the /profile command."),
                shopExtension);
        KahzerxServer.extensions.add(profileExtension);

        KahzerxServer.extensions.add(new OpOnWhitelistExtension(new ExtensionSettings("opOnWhitelist", isEnabled(found, "opOnWhitelist"), "Auto ops and deops on whitelist add and remove.")));
        KahzerxServer.extensions.add(new BedTimeExtension(new ExtensionSettings("bedTime", isEnabled(found, "bedTime"), "Notifies when a player goes to sleep")));

        String token = "";
        boolean crossServerChat = false;
        String prefix = "";
        boolean isRunning = false;
        long chatChannelID = 0L;
        boolean shouldFeedback = true;
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
                    shouldFeedback = ds.isShouldFeedback();
                    break;
                }
            }
        }
        DiscordExtension discordExtension = new DiscordExtension(
                new DiscordSettings(
                        "discord",
                        isEnabled(found, "discord"),
                        "Connects minecraft chat + some events with a discord chat (chatbridge). Prefix is necessary if you want crossServerChat to work properly and not having duplicated messages.",
                        token,
                        crossServerChat,
                        prefix,
                        isRunning,
                        chatChannelID,
                        allowedChats,
                        shouldFeedback
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
                        isEnabled(found, "discordWhitelist") && (discordExtension.extensionSettings().isEnabled()),
                        "Enables !list, !add and !remove commands along with nPlayers that specifies how many minecraft players a discord user can add; There is also an optional discordRole that will be given to the discord user on !add and deleted on !remove.",
                        whitelistChats,
                        discordRoleID,
                        nPlayers
                ),
                discordExtension);
        KahzerxServer.extensions.add(discordWhitelistExtension);

        List<Long> adminChats = new ArrayList<>();
        boolean feedback = true;
        DiscordAdminToolsJsonSettings datjs = gson.fromJson(settings, DiscordAdminToolsJsonSettings.class);
        if (dwjs != null) {
            for (DiscordAdminToolsSettings dats : datjs.getSettings()) {
                if (dats == null) {
                    continue;
                }
                if (dats.getName().equals("discordAdminTools")) {
                    adminChats = dats.getAdminChats() != null ? dats.getAdminChats() : new ArrayList<>();
                    feedback = dats.isShouldFeedback();
                    break;
                }
            }
        }
        KahzerxServer.extensions.add(new DiscordAdminToolsExtension(
                new DiscordAdminToolsSettings(
                        "discordAdminTools",
                        isEnabled(found, "discordAdminTools") && (discordExtension.extensionSettings().isEnabled()) && (discordWhitelistExtension.extensionSettings().isEnabled()),
                        "Enables !ban, !pardon, !exadd, !exremove on discord AdminChats.",
                        adminChats,
                        feedback
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
                        isEnabled(found, "discordWhitelistSync") && (discordExtension.extensionSettings().isEnabled()) && (discordWhitelistExtension.extensionSettings().isEnabled()),
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
