package com.kahzerx.kahzerxmod.utils;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum MarkEnum {
    TICK("✔", Formatting.GREEN),
    CROSS("✘", Formatting.DARK_RED),
    INFO("ⓘ", Formatting.GOLD),
    RIP("☠", Formatting.DARK_RED),
    SUN("☀", Formatting.GOLD);

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
        return new LiteralText(identifier).styled(style -> style.withColor(formatting));
    }

    public MutableText getFormattedIdentifierBold() {
        return new LiteralText(identifier).styled(style -> style.withColor(formatting).withBold(true));
    }

    public MutableText appendMessage(String message, Formatting color) {
        return getFormattedIdentifier().append(new LiteralText(" " + message).styled(style -> style.withColor(color)));
    }

    public MutableText appendMessage(String message) {
        return appendMessage(message, Formatting.WHITE);
    }

    public MutableText appendText(Text t) {
        return getFormattedIdentifier().append(new LiteralText(" ").append(t));
    }

    public MutableText boldAppendMessage(String message, Formatting color) {
        return getFormattedIdentifierBold().append(new LiteralText(" " + message).styled(style -> style.withColor(color).withBold(false)));
    }

    public MutableText boldAppendMessage(String message) {
        return boldAppendMessage(message, Formatting.WHITE);
    }
}
