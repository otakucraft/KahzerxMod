package com.kahzerx.kahzerxmod.extensions.shopExtension.parcel;

import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import static com.kahzerx.kahzerxmod.extensions.shopExtension.parcel.Parcel.draw;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ParcelCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ShopExtension extension) {
        dispatcher.register(literal("parcel").
                requires(server -> extension.extensionSettings().isEnabled()).
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
                then(literal("draw").
                        then(argument("corner1", BlockPosArgumentType.blockPos()).
                                then(argument("corner2", BlockPosArgumentType.blockPos()).
                                        executes(context -> {
                                            BlockPos corner1 = BlockPosArgumentType.getBlockPos(context, "corner1");
                                            BlockPos corner2 = BlockPosArgumentType.getBlockPos(context, "corner2");
                                            draw(corner1, corner2, context.getSource().getWorld());
                                            return 1;
                                        })))));
    }
}
