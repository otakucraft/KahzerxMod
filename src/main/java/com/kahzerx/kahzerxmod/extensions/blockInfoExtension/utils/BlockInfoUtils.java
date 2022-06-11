package com.kahzerx.kahzerxmod.extensions.blockInfoExtension.utils;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BlockInfoUtils {
    public static boolean shouldRegisterBlock(Block block, ServerPlayerEntity player) {
        return !player.isInSneakingPose() && (block instanceof BlockWithEntity
                || block instanceof DoorBlock || block instanceof FenceGateBlock
                || block instanceof TrapdoorBlock || block instanceof LeverBlock
                || block instanceof AbstractButtonBlock || block instanceof NoteBlock);
    }

    public static boolean shouldRegisterItem(ServerPlayerEntity player, ItemStack itemStack) {
        return !player.isInSneakingPose() && (itemStack.getItem() == Items.LAVA_BUCKET
                || itemStack.getItem() == Items.WATER_BUCKET);
    }

    public static MutableText buildLine(final ResultSet rs) throws SQLException {
        LocalDateTime date = LocalDateTime.parse(rs.getString("date"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        String playerName = rs.getString("playerName");
        String block = rs.getString("block");
        int amount = rs.getInt("amount");
        block = block.split("\\.")[block.split("\\.").length - 1];
        MutableText fDate = getDateWithHover(date);
        return switch (rs.getInt("action")) {
            case 0 -> fDate.append(String.format(" %s %s %s.",
                    Formatting.WHITE + playerName,
                    Formatting.RED + "broke",
                    Formatting.YELLOW + block + Formatting.WHITE));
            case 1 -> fDate.append(String.format(" %s %s %s.",
                    Formatting.WHITE + playerName,
                    Formatting.GREEN + "placed",
                    Formatting.YELLOW + block + Formatting.WHITE));
            case 2 -> fDate.append(String.format(" %s %s %s.",
                    Formatting.WHITE + playerName,
                    Formatting.DARK_AQUA + "used",
                    Formatting.YELLOW + block + Formatting.WHITE));
            case 3 -> fDate.append(String.format(" %s %s %s %s.",
                    Formatting.WHITE + playerName,
                    Formatting.AQUA + "added",
                    Formatting.YELLOW + String.valueOf(amount),
                    block + Formatting.WHITE));
            default -> fDate.append(String.format(" %s %s %s %s.",
                    Formatting.WHITE + playerName,
                    Formatting.DARK_RED + "removed",
                    Formatting.YELLOW + String.valueOf(amount),
                    block + Formatting.WHITE));
        };
    }

    private static MutableText getDateWithHover(LocalDateTime date) {
        return Text.literal("[" + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "]").styled(style -> style.
                withColor(Formatting.GRAY).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))))));
    }

    public static MutableText getPages(int page, int nLine) {
        return Text.literal(String.format("%d/%d", page, nLine)).styled((style -> style.withColor(Formatting.WHITE)));
    }

    public static MutableText getNext(int x, int y, int z, int page) {
        return Text.literal(" >>>").styled(style -> style.withColor(Formatting.GOLD).
                withClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        String.format("/blockInfo %d %d %d %d", x, y, z, page + 1))).
                withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, Text.literal("Next"))));
    }

    public static MutableText getPrev(int x, int y, int z, int page) {
        return Text.literal("<<< ").styled(style -> style.withColor(Formatting.GOLD).
                withClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        String.format("/blockInfo %d %d %d %d", x, y, z, page - 1))).
                withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, Text.literal("Prev"))));
    }

    public static MutableText getHelp(int x, int y, int z) {
        return Text.literal(
                String.format(". Page with /blockInfo %d %d %d <page>.", x, y, z))
                .styled(style -> style.withColor(Formatting.WHITE));
    }
}
