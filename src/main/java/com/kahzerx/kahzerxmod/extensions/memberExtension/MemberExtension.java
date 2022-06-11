package com.kahzerx.kahzerxmod.extensions.memberExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class MemberExtension extends GenericExtension implements Extensions {
    public MemberExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        if (!this.getSettings().isEnabled()) {
            return;
        }
        MinecraftServer server = player.getServer();
        assert server != null;
        Collection<String> teamNames = server.getScoreboard().getTeamNames();
        if (!teamNames.contains("ADMIN")) {
            Team team = server.getScoreboard().addTeam("ADMIN");
            team.setPrefix(Text.literal("[ADMIN] ").styled(style -> style.withBold(true).withColor(Formatting.GOLD)));
            team.setShowFriendlyInvisibles(false);
        }
        if (!teamNames.contains("MOD")) {
            Team team = server.getScoreboard().addTeam("MOD");
            team.setPrefix(Text.literal("[MOD] ").styled(style -> style.withBold(true).withColor(Formatting.DARK_PURPLE)));
            team.setShowFriendlyInvisibles(false);
        }
        if (!teamNames.contains("HELPER")) {
            Team team = server.getScoreboard().addTeam("HELPER");
            team.setPrefix(Text.literal("[HELPER] ").styled(style -> style.withBold(true).withColor(Formatting.AQUA)));
            team.setShowFriendlyInvisibles(false);
        }
        if (!teamNames.contains("MIEMBRO")) {
            Team team = server.getScoreboard().addTeam("MIEMBRO");
            team.setPrefix(Text.literal("[MIEMBRO] ").styled(style -> style.withBold(true).withColor(Formatting.GREEN)));
            team.setShowFriendlyInvisibles(false);
        }
        if (teamNames.contains("MIEMBRO")) {
            Team playerTeam = server.getScoreboard().getPlayerTeam(player.getName().getString());
            if (playerTeam != null) {
                return;
            }
            Team team = server.getScoreboard().getTeam("MIEMBRO");
            server.getScoreboard().addPlayerToTeam(player.getName().getString(), team);
        }
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onExtensionEnabled() { }

    @Override
    public void onExtensionDisabled() { }
}
