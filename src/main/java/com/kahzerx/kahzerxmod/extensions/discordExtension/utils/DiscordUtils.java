package com.kahzerx.kahzerxmod.extensions.discordExtension.utils;

import java.util.List;

public class DiscordUtils {
    public static boolean isAllowed(long chatID, List<Long> validChannels) {
        return validChannels.contains(chatID);
    }
}
