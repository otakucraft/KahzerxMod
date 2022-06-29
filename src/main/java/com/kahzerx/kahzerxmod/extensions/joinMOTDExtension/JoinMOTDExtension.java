package com.kahzerx.kahzerxmod.extensions.joinMOTDExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsExtension;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class JoinMOTDExtension extends GenericExtension implements Extensions {
    public final PermsExtension permsExtension;
    public JoinMOTDExtension(JoinMOTDSettings settings, PermsExtension permsExtension) {
        super(settings);
        this.permsExtension = permsExtension;
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new JoinMOTDCommand().register(dispatcher, this);
    }

    public void updateMessage(ServerCommandSource source, String message) {
        this.extensionSettings().setMessage(message);
        source.sendFeedback(MarkEnum.TICK.appendMessage("New join message configured!"), false);
        source.sendFeedback(getFormatted(message), false);
    }

    @Override
    public void onPlayerConnected(ServerPlayerEntity player) {
        if (this.extensionSettings().isEnabled() && !this.extensionSettings().getMessage().equals("")) {
            player.sendMessage(getFormatted(this.extensionSettings().getMessage()));
        }
    }

    public MutableText getFormatted(String message) {
        // /joinMOTD set awdawd [%1awdawd1]{click=/say a} \n[awdawd test]{display=test} a [%dawdawd1]{display=test2,click=/say a} a
        message = message.replace('%', '\u00a7');
        message = message.replace("\\n", "\n");
        List<StyleData> styled = new ArrayList<>();
        List<StyleData> styles = new ArrayList<>();
        int posAtStyleOpen = -1;
        int posAtStyledOpen = -1;

        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '[') {
                posAtStyledOpen = i;
            }
            if (message.charAt(i) == ']') {
                if (posAtStyledOpen != -1) {
                    styled.add(new StyleData(posAtStyledOpen, i));
                    posAtStyledOpen = -1;
                }
            }

            if (message.charAt(i) == '{') {
                posAtStyleOpen = i;
            }
            if (message.charAt(i) == '}') {
                if (posAtStyleOpen != -1) {
                    styles.add(new StyleData(posAtStyleOpen, i));
                    posAtStyleOpen = -1;
                }
            }
        }

        List<StyleData> correctStyled = new ArrayList<>();
        List<StyleData> correctStyles = new ArrayList<>();
        for (StyleData style_t : styled) {
            for (StyleData style_t2 : styles) {
                if (style_t.ends() + 1 == style_t2.starts()) {
                    correctStyled.add(style_t);
                    correctStyles.add(style_t2);
                }
            }
        }

        MutableText text = Text.literal("");
        int actualStartPos = 0;
        if (correctStyled.size() == 0) {
            text.append(message);
        }
        for (int i = 0; i < correctStyled.size(); i++) {
            if (correctStyled.get(i).starts() == actualStartPos) {
                String custom = message.substring(correctStyled.get(i).starts() + 1, correctStyled.get(i).ends());
                String styleFormat = message.substring(correctStyles.get(i).starts(), correctStyles.get(i).ends() + 1);
                text.append(format(custom, styleFormat));
            } else {
                text.append(Text.literal(message.substring(actualStartPos, correctStyled.get(i).starts())));
                String custom = message.substring(correctStyled.get(i).starts() + 1, correctStyled.get(i).ends());
                String styleFormat = message.substring(correctStyles.get(i).starts(), correctStyles.get(i).ends() + 1);
                text.append(format(custom, styleFormat));
            }
            actualStartPos = correctStyles.get(i).ends() + 1;
        }
        text.append(message.substring(actualStartPos));
        return text;
    }

    public MutableText format(String text, String formats) {
        MutableText msg = Text.literal(text);
        String[] configs = formats.substring(1, formats.length() - 1).split(",");
        for (String config : configs) {
            config = config.strip();
            if (config.contains("=")) {
                String[] kv = config.split("=");
                if (kv.length != 2) {
                    continue;
                }
                if (kv[0].strip().equals("display")) {
                    String hoverText = kv[1].strip();
                    msg.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hoverText))));
                }
                if (kv[0].strip().equals("click")) {
                    String click = kv[1].strip();
                    msg.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, click)));
                }
            }
        }
        return msg;
    }

    @Override
    public JoinMOTDSettings extensionSettings() {
        return (JoinMOTDSettings) this.getSettings();
    }

    public PermsExtension getPermsExtension() {
        return permsExtension;
    }

    record StyleData(int starts, int ends) { }
}
