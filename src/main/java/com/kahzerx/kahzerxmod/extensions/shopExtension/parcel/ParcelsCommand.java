package com.kahzerx.kahzerxmod.extensions.shopExtension.parcel;

import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.math.BlockPos;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.kahzerx.kahzerxmod.extensions.shopExtension.parcel.Parcel.draw;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ParcelsCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ShopExtension extension) {
        dispatcher.register(literal("parcels").
                requires(server -> extension.extensionSettings().isEnabled() && (server.hasPermissionLevel(2) || (extension.getPermsExtension().extensionSettings().isEnabled() && extension.getPermsExtension().getDBPlayerPerms(server.getPlayer().getUuidAsString()).getId() >= PermsLevels.HELPER.getId()))).
                then(literal("create").
                        then(argument("corner1", BlockPosArgumentType.blockPos()).
                                then(argument("corner2", BlockPosArgumentType.blockPos()).
                                        executes(context -> {
                                            BlockPos corner1 = BlockPosArgumentType.getBlockPos(context, "corner1");
                                            BlockPos corner2 = BlockPosArgumentType.getBlockPos(context, "corner2");
                                            if (corner1.getX() > corner2.getX()) {
                                                int x1 = corner1.getX();
                                                int x2 = corner2.getX();
                                                corner1 = new BlockPos(x2, corner1.getY(), corner1.getZ());
                                                corner2 = new BlockPos(x1, corner2.getY(), corner2.getZ());
                                            }
                                            if (corner1.getY() > corner2.getY()) {
                                                int y1 = corner1.getY();
                                                int y2 = corner2.getY();
                                                corner1 = new BlockPos(corner1.getX(), y2, corner1.getZ());
                                                corner2 = new BlockPos(corner2.getX(), y1, corner2.getZ());
                                            }
                                            if (corner1.getZ() > corner2.getZ()) {
                                                int z1 = corner1.getZ();
                                                int z2 = corner2.getZ();
                                                corner1 = new BlockPos(corner1.getX(), corner1.getY(), z2);
                                                corner2 = new BlockPos(corner2.getX(), corner2.getY(), z1);
                                            }
                                            corner2 = new BlockPos(corner2.getX() + 1, corner2.getY() + 1, corner2.getZ() + 1);
                                            Parcel parcel = new Parcel(null, corner1, corner2, null, 0, DimUtils.getWorldID(DimUtils.getDim(context.getSource().getPlayer().getWorld())), null);
                                            extension.getDB().getQuery().registerParcel(parcel);
                                            extension.getParcels().addParcel(parcel);
                                            context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Parcela registrada!"), false);
                                            return 1;
                                        })))).
                then(literal("list").
                        executes(context -> {
                            if (extension.getParcels().getParcels().size() == 0) {
                                context.getSource().sendFeedback(MarkEnum.INFO.appendMessage("No hay parcelas!"), false);
                            }
                            for (Parcel p : extension.getParcels().getParcels()) {
                                context.getSource().sendFeedback(p.formatted(extension), false);
                            }
                            return 1;
                        })).
                then(literal("set").
                        then(literal("price").
                                then(argument("price", IntegerArgumentType.integer(1)).
                                        executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            if (player != null) {
                                                int dim = DimUtils.getWorldID(DimUtils.getDim(player.getWorld()));
                                                BlockPos playerPos = player.getBlockPos();
                                                if (extension.getParcels().isPosInParcel(dim, playerPos)) {
                                                    int price = IntegerArgumentType.getInteger(context, "price");
                                                    Parcel actualParcel = extension.getParcels().getParcel(dim, playerPos);
                                                    actualParcel.setPrice(price);
                                                    extension.getDB().getQuery().updatePrice(actualParcel, price);
                                                    context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Esta parcela ahora cuesta " + price), false);
                                                } else {
                                                    context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Debes estar en una parcela!"), false);
                                                }
                                            }
                                            return 1;
                                        })))).
                then(literal("give").
                        then(argument("player", StringArgumentType.string()).
                                suggests((c, b) -> suggestMatching(extension.getDB().getQuery().getPlayers(), b)).
                                executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    String playerUUID = extension.getDB().getQuery().getPlayerUUID(playerName);
                                    if (playerUUID == null) {
                                        context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Este jugador no existe!"), false);
                                        return 1;
                                    }
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        int dim = DimUtils.getWorldID(DimUtils.getDim(player.getWorld()));
                                        BlockPos playerPos = player.getBlockPos();
                                        if (extension.getParcels().isPosInParcel(dim, playerPos)) {
                                            Parcel actualParcel = extension.getParcels().getParcel(dim, playerPos);
                                            if (actualParcel.hasOwner()) {
                                                context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Esta parcela ya pertenece a " + extension.getDB().getQuery().getPlayerName(actualParcel.getOwnerUUID())), false);
                                            } else {
                                                Timestamp t = Timestamp.valueOf(LocalDateTime.now().plusMonths(1));
                                                actualParcel.setOwnerUUID(playerUUID);
                                                actualParcel.setNextPayout(t);
                                                extension.getDB().getQuery().giveParcel(actualParcel, playerUUID, t);
                                                context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Parcela comprada por " + playerName), false);
                                                context.getSource().sendFeedback(MarkEnum.INFO.appendMessage("§8/parcel info§r para más información").styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/parcel info"))), false);
                                            }
                                        } else {
                                            context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Debes estar en una parcela!"), false);
                                        }
                                    }
                                    return 1;
                                }))).
                then(literal("info").
                        executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                int dim = DimUtils.getWorldID(DimUtils.getDim(player.getWorld()));
                                BlockPos playerPos = player.getBlockPos();
                                if (extension.getParcels().isPosInParcel(dim, playerPos)) {
                                    Parcel actualParcel = extension.getParcels().getParcel(dim, playerPos);
                                    context.getSource().sendFeedback(actualParcel.formatted(extension), false);
                                } else {
                                    context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Debes estar en una parcela!"), false);
                                }
                            }
                            return 1;
                        })).
                then(literal("remove").
                        executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                int dim = DimUtils.getWorldID(DimUtils.getDim(player.getWorld()));
                                BlockPos playerPos = player.getBlockPos();
                                if (extension.getParcels().isPosInParcel(dim, playerPos)) {
                                    Parcel actualParcel = extension.getParcels().getParcel(dim, playerPos);
                                    extension.getParcels().remove(actualParcel);
                                    extension.getDB().getQuery().removeParcel(actualParcel);
                                    context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Parcela eliminada!"), false);
                                } else {
                                    context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Debes estar en una parcela!"), false);
                                }
                            }
                            return 1;
                        })).
                then(literal("kick").
                        executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                int dim = DimUtils.getWorldID(DimUtils.getDim(player.getWorld()));
                                BlockPos playerPos = player.getBlockPos();
                                if (extension.getParcels().isPosInParcel(dim, playerPos)) {
                                    Parcel actualParcel = extension.getParcels().getParcel(dim, playerPos);
                                    actualParcel.setOwnerUUID(null);
                                    actualParcel.setNextPayout(null);
                                    actualParcel.setName(null);
                                    extension.getDB().getQuery().giveParcel(actualParcel, null, null);
                                    context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Dueño eliminado!"), false);
                                } else {
                                    context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Debes estar en una parcela!"), false);
                                }
                            }
                            return 1;
                        })));
    }
}
