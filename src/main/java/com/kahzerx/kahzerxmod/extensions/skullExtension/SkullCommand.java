package com.kahzerx.kahzerxmod.extensions.skullExtension;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ItemScatterer;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

public class SkullCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, SkullExtension skull) {
        dispatcher.register(literal("skull").
                requires(isEnabled -> skull.extensionSettings().isEnabled()).
                then(CommandManager.argument("playerName", StringArgumentType.word()).
                        executes(context -> {
                            Optional<GameProfile> profile = context.getSource().getServer().getUserCache().findByName(StringArgumentType.getString(context, "playerName"));
                            if (profile.isEmpty()) {
                                context.getSource().sendFeedback(new LiteralText("Not premium!"), false);
                                return 1;
                            }
                            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
                            NbtCompound compound = stack.getOrCreateNbt();
                            compound.putString("SkullOwner", profile.get().getName());
                            stack.writeNbt(compound);
                            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
                            if (sourcePlayer.giveItemStack(stack)) {
                                ItemScatterer.spawn(sourcePlayer.getWorld(), sourcePlayer.getX(), sourcePlayer.getY(), sourcePlayer.getZ(), stack);
                            }
                            return 1;
                        })));
    }
}
