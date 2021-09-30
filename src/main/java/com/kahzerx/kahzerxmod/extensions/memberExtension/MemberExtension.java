package com.kahzerx.kahzerxmod.extensions.memberExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
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
        if (!teamNames.contains("MIEMBRO")) {
            Team team = server.getScoreboard().addTeam("MIEMBRO");
            team.setPrefix(new LiteralText("[MIEMBRO] ").styled(style -> style.withBold(true).withColor(Formatting.GREEN)));
        }
        if (teamNames.contains("MIEMBRO")) {
            Team playerTeam = server.getScoreboard().getPlayerTeam(player.getName().getString());
            System.out.println(playerTeam);
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
    public void onExtensionEnabled() {

    }

    @Override
    public void onExtensionDisabled() {

    }
}
