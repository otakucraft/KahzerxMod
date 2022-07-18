package com.kahzerx.kahzerxmod.extensions.shopExtension.parcel;

import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ParcelCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ShopExtension extension) {
        dispatcher.register(literal("parcel").
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
                                context.getSource().sendFeedback(formatted(p, extension), false);
                            }
                            return 1;
                        })).
                then(literal("visualize").
                        then(argument("corner1", BlockPosArgumentType.blockPos()).
                                then(argument("corner2", BlockPosArgumentType.blockPos()).
                                        executes(context -> {
                                            BlockPos corner1 = BlockPosArgumentType.getBlockPos(context, "corner1");
                                            BlockPos corner2 = BlockPosArgumentType.getBlockPos(context, "corner2");
                                            draw(corner1, corner2, context.getSource().getWorld());
                                            return 1;
                                        })))).
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
                                        }))).
                        then(literal("name").
                                then(argument("name", StringArgumentType.string()).
                                        executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            if (player != null) {
                                                int dim = DimUtils.getWorldID(DimUtils.getDim(player.getWorld()));
                                                BlockPos playerPos = player.getBlockPos();
                                                if (extension.getParcels().isPosInParcel(dim, playerPos)) {
                                                    String name = StringArgumentType.getString(context, "name");
                                                    Parcel actualParcel = extension.getParcels().getParcel(dim, playerPos);
                                                    actualParcel.setName(name);
                                                    extension.getDB().getQuery().updateName(actualParcel, name);
                                                    context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Esta parcela ahora se llama " + name), false);
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
                                                context.getSource().sendFeedback(MarkEnum.INFO.appendMessage("§8/parcel info§r para más información"), false);
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
                                    context.getSource().sendFeedback(formatted(actualParcel, extension), false);
                                } else {
                                    context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Debes estar en una parcela!"), false);
                                }
                            }
                            return 1;
                        })));
    }

    private void draw(BlockPos corner1, BlockPos corner2, ServerWorld world) {
        if (corner1.getX() > corner2.getX()) {
            int x1 = corner1.getX();
            int x2 = corner2.getX();
            corner1 = new BlockPos(x2, corner1.getY(), corner1.getZ());
            corner2 = new BlockPos(x1, corner2.getY(), corner2.getZ());
        }
        if (corner1.getY() > corner2.getY()) {
            int y1 = corner1.getX();
            int y2 = corner2.getX();
            corner1 = new BlockPos(corner1.getX(), y2, corner1.getZ());
            corner2 = new BlockPos(corner2.getX(), y1, corner2.getZ());
        }
        if (corner1.getZ() > corner2.getZ()) {
            int z1 = corner1.getX();
            int z2 = corner2.getX();
            corner1 = new BlockPos(corner1.getX(), corner1.getY(), z2);
            corner2 = new BlockPos(corner2.getX(), corner2.getY(), z1);
        }
        List<BlockPos> structure = new ArrayList<>();
        for (int x = corner1.getX(); x <= corner2.getX(); x++) {
            structure.add(new BlockPos(x, corner1.getY(), corner1.getZ()));
            structure.add(new BlockPos(x, corner1.getY(), corner2.getZ()));
            structure.add(new BlockPos(x, corner2.getY(), corner1.getZ()));
            structure.add(new BlockPos(x, corner2.getY(), corner2.getZ()));
        }
        for (int y = corner1.getY(); y <= corner2.getY(); y++) {
            structure.add(new BlockPos(corner1.getX(), y, corner1.getZ()));
            structure.add(new BlockPos(corner2.getX(), y, corner1.getZ()));
            structure.add(new BlockPos(corner1.getX(), y, corner2.getZ()));
            structure.add(new BlockPos(corner2.getX(), y, corner2.getZ()));
        }
        for (int z = corner1.getZ(); z <= corner2.getZ(); z++) {
            structure.add(new BlockPos(corner1.getX(), corner1.getY(), z));
            structure.add(new BlockPos(corner2.getX(), corner1.getY(), z));
            structure.add(new BlockPos(corner1.getX(), corner2.getY(), z));
            structure.add(new BlockPos(corner2.getX(), corner2.getY(), z));
        }
        Collections.shuffle(structure);

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                for (BlockPos p : structure) {
                    try {
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    spawnDustParticle(world, p);
                }
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void spawnDustParticle(ServerWorld world, BlockPos pos) {
        world.spawnParticles(
                new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(new Color(255, 220, 0).getRGB())), 1.0F),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                50,
                0,
                0,
                0,
                1
        );
    }

    public MutableText formatted(Parcel p, ShopExtension extension) {
        int centerX = (p.getCorner1().getX() + p.getCorner2().getX()) / 2;
        int centerZ = (p.getCorner1().getZ() + p.getCorner2().getZ()) / 2;
        MutableText t = MarkEnum.INFO.appendMessage(String.format("coords: §7%d %d§r owner: §8%s§r", centerX, centerZ, p.getOwnerUUID() != null ? extension.getDB().getQuery().getPlayerName(p.getOwnerUUID()) : "None"));
        MutableText hover = Text.literal(String.format("Dimension: %s\nName: %s\nPrice: %d\nPayout: %s\nCorner1: %d %d %d\nCorner2: %d %d %d\nClick to visualize!", DimUtils.getWorldString(p.getDim()), p.getName() != null ? p.getName() : "None", p.getPrice(), p.getNextPayout() != null ? p.getNextPayout().toLocalDateTime(): "None", p.getCorner1().getX(), p.getCorner1().getY(), p.getCorner1().getZ(), p.getCorner2().getX(), p.getCorner2().getY(), p.getCorner2().getZ()));
        t.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/parcel visualize %d %d %d %d %d %d", p.getCorner1().getX(), p.getCorner1().getY(), p.getCorner1().getZ(), p.getCorner2().getX(), p.getCorner2().getY(), p.getCorner2().getZ()))));
        return t;
    }
}
