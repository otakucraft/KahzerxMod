package com.kahzerx.kahzerxmod.extensions.scoreboardExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Block;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.network.MessageType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.world.level.ServerWorldProperties;
import org.apache.logging.log4j.core.jmx.Server;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class ScoreboardExtension extends GenericExtension implements Extensions {
    public ScoreboardExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new ScoreboardCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    public int hideSidebar(ServerCommandSource source) {
        Scoreboard scoreboard = source.getServer().getScoreboard();
        Entity entity = source.getEntity();
        if (scoreboard.getObjectiveForSlot(1) == null) {
            source.sendFeedback(new LiteralText("No hay ninguna scoreboard."), false);
            return 1;
        } else {
            scoreboard.setObjectiveSlot(1, null);
            assert entity != null;
            source.getServer().getPlayerManager().broadcastChatMessage(new LiteralText(entity.getEntityName() + " ha eliminado la scoreboard."), MessageType.CHAT, Util.NIL_UUID);
        }
        return 1;
    }

    public int startThreadedShowSideBar(ServerCommandSource source, ItemStackArgument item, String type) {
        new Thread(() -> showSideBar(source, item, type)).start();
        return 1;
    }

    public void showSideBar(ServerCommandSource source, ItemStackArgument item, String type) {
        Scoreboard scoreboard = source.getServer().getScoreboard();
        Item minecraftItem = item.getItem();
        String objectiveName = type + "." + Item.getRawId(minecraftItem);
        ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(objectiveName);

        Entity entity = source.getEntity();
        Text text;

        if (scoreboardObjective != null) {
            if (scoreboard.getObjectiveForSlot(1) == scoreboardObjective) {
                text = new LiteralText("Ya se está mostrando.");
            } else {
                assert entity != null;
                text = new LiteralText(entity.getEntityName() + " ha seleccionado el scoreboard " + Formatting.GOLD + "[" + scoreboardObjective.getDisplayName().asString() + "]");
                scoreboard.setObjectiveSlot(1, scoreboardObjective);
            }
        } else {
            String criteriaName = "minecraft." + type + ":minecraft." + item.getItem().toString();
            String capitalize = type.substring(0, 1).toUpperCase() + type.substring(1);
            String displayName = capitalize + " " + minecraftItem.toString().replaceAll("_", " ");
            Optional<ScoreboardCriterion> opCriteria = ScoreboardCriterion.getOrCreateStatCriterion(criteriaName);
            if (opCriteria.isEmpty()) {
                return;
            }
            ScoreboardCriterion criteria = opCriteria.get();

            scoreboard.addObjective(objectiveName, criteria, new LiteralText(displayName).formatted(Formatting.GOLD), criteria.getDefaultRenderType());

            ScoreboardObjective newScoreboardObjective = scoreboardObjective = scoreboard.getNullableObjective(objectiveName);
            try {
                initScoreboard(source, newScoreboardObjective, minecraftItem, type);
            } catch (Exception e) {
                scoreboard.removeObjective(newScoreboardObjective);
                text = new LiteralText("Ha ocurrido un error al momento de seleccionar un scoreboard, inténtelo de nuevo.").formatted(Formatting.RED);
                assert entity != null;
                source.getServer().getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, Util.NIL_UUID);

                return;

            }
            scoreboard.setObjectiveSlot(1, newScoreboardObjective);
            assert entity != null;
            assert scoreboardObjective != null;
            text = new LiteralText(entity.getEntityName() + " ha seleccionado el scoreboard " + Formatting.GOLD + "[" + scoreboardObjective.getDisplayName().asString() + "]");
        }
        assert entity != null;
        source.getServer().getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, Util.NIL_UUID);
    }

    public void initScoreboard(ServerCommandSource source, ScoreboardObjective scoreboardObjective, Item item, String type) {
        Scoreboard scoreboard = source.getServer().getScoreboard();
        MinecraftServer server = source.getServer();
        File file = new File(((ServerWorldProperties) server.getOverworld().getLevelProperties()).getLevelName(), "stats");
        File[] stats = file.listFiles();
        assert stats != null;
        for (File stat : stats) {
            String fileName = stat.getName();
            String uuidString = fileName.substring(0, fileName.lastIndexOf(".json"));
            UUID uuid = UUID.fromString(uuidString);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            Stat<?> finalStat = null;
            if (type.equalsIgnoreCase("broken")) {
                finalStat = Stats.BROKEN.getOrCreateStat(item);
            } else if (type.equalsIgnoreCase("crafted")) {
                finalStat = Stats.CRAFTED.getOrCreateStat(item);
            } else if (type.equalsIgnoreCase("mined")) {
                finalStat = Stats.MINED.getOrCreateStat(Block.getBlockFromItem(item));
            } else if (type.equalsIgnoreCase("used")) {
                finalStat = Stats.USED.getOrCreateStat(item);
            }
            int value;
            String playerName;
            if (player != null) {
                value = player.getStatHandler().getStat(finalStat);
                playerName = player.getEntityName();
            } else {
                ServerStatHandler serverStatHandler = new ServerStatHandler(server, stat);
                value = serverStatHandler.getStat(finalStat);
                Optional<GameProfile> gameProfile = server.getUserCache().getByUuid(uuid);

                if (gameProfile.isEmpty()) {
                    continue;
                }
                playerName = gameProfile.get().getName();
            }
            if (value == 0) {
                continue;
            }
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(playerName, scoreboardObjective);
            scoreboardPlayerScore.setScore(value);
        }
    }
}
