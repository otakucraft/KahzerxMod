package com.kahzerx.kahzerxmod.utils;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
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
        block = block.split("\\.")[block.split("\\.").length - 1];
        MutableText fDate = getDateWithHover(date);
        return switch (rs.getInt("action")) {
            case 0 -> fDate.append(String.format(" <%s> ha roto '%s'.",
                    Formatting.WHITE + playerName,
                    Formatting.DARK_PURPLE + block + Formatting.WHITE));
            case 1 -> fDate.append(String.format(" <%s> ha puesto '%s'.",
                    Formatting.WHITE + playerName,
                    Formatting.DARK_PURPLE + block + Formatting.WHITE));
            default -> fDate.append(String.format(" <%s> ha usado '%s'.",
                    Formatting.WHITE + playerName,
                    Formatting.DARK_PURPLE + block + Formatting.WHITE));
        };
    }

    private static MutableText getDateWithHover(LocalDateTime date) {
        return new LiteralText("[" + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "]").styled(style -> style.
                withColor(Formatting.GRAY).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))))));
    }

    public static MutableText getPages(int page, int nLine) {
        return new LiteralText(String.format("%d/%d", page, nLine)).styled((style -> style.withColor(Formatting.WHITE)));
    }

    public static MutableText getNext(int x, int y, int z, int page) {
        return new LiteralText(" >>>").styled(style -> style.withColor(Formatting.GOLD).
                withClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        String.format("/blockInfo %d %d %d %d", x, y, z, page + 1))).
                withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, new LiteralText("Página siguiente"))));
    }

    public static MutableText getPrev(int x, int y, int z, int page) {
        return new LiteralText("<<< ").styled(style -> style.withColor(Formatting.GOLD).
                withClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        String.format("/blockInfo %d %d %d %d", x, y, z, page - 1))).
                withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, new LiteralText("Página anterior"))));
    }

    public static MutableText getHelp(int x, int y, int z) {
        return new LiteralText(
                String.format(". Página con /blockInfo %d %d %d <página>.", x, y, z))
                .styled(style -> style.withColor(Formatting.WHITE));
    }
}
