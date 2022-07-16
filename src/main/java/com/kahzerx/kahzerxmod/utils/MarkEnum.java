package com.kahzerx.kahzerxmod.utils;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum MarkEnum {
    TICK("✔", Formatting.GREEN),
    CROSS("✘", Formatting.DARK_RED),
    INFO("ⓘ", Formatting.GOLD),
    WARNING("⚠", Formatting.GOLD),
    RIP("☠", Formatting.DARK_RED),
    SUN("☀", Formatting.GOLD),
    QUESTION("?", Formatting.YELLOW),
    OTAKU_COIN("\uD83D\uDD25", Formatting.RED),
    SLEEP("zZz", Formatting.BLUE),
    DIAMOND_LIKE("✧", Formatting.AQUA),
    NETHERITE_LIKE("✦", Formatting.GRAY);

    private final String identifier;
    private final Formatting formatting;

    MarkEnum(String identifier, Formatting formatting) {
        this.identifier = identifier;
        this.formatting = formatting;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    public String getIdentifier() {
        return identifier;
    }

    public MutableText getFormattedIdentifier() {
        return Text.literal(identifier).styled(style -> style.withColor(formatting).withBold(false).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, identifier)));
    }

    public MutableText getFormattedIdentifierBold() {
        return Text.literal(identifier).styled(style -> style.withColor(formatting).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, identifier)));
    }

    public MutableText appendMessage(String message, Formatting color) {
        return getFormattedIdentifier().append(Text.literal(" " + message).styled(style -> style.withColor(color)));
    }

    public MutableText appendMessage(String message) {
        return appendMessage(message, Formatting.WHITE);
    }

    public MutableText appendText(MutableText t, Formatting color) {
        return getFormattedIdentifier().append(Text.literal(" ").append(t.styled(style -> style.withColor(color))));
    }

    public MutableText appendText(MutableText t) {
        return appendText(t, Formatting.WHITE);
    }

    public MutableText boldAppendMessage(String message, Formatting color) {
        return getFormattedIdentifierBold().append(Text.literal(" " + message).styled(style -> style.withColor(color).withBold(false)));
    }

    public MutableText appendSuffix(String message, Formatting color) {
        return Text.literal(message + " ").styled(style -> style.withColor(color).withBold(false)).append(getFormattedIdentifier());
    }

    public MutableText appendSuffix(String message) {
        return appendSuffix(message, Formatting.WHITE);
    }

    public MutableText boldAppendMessage(String message) {
        return boldAppendMessage(message, Formatting.WHITE);
    }
}
