package com.kahzerx.kahzerxmod.extensions.memberExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.util.Objects;

public class MemberExtension extends GenericExtension implements Extensions {
    private Connection conn;

    public MemberExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        if (!this.getSettings().isEnabled()) {
            return;
        }
        this.conn = conn;
        try {
            String createBackDatabase = "CREATE TABLE IF NOT EXISTS `ever_joined` (" +
                    "`uuid` VARCHAR(50) PRIMARY KEY NOT NULL," +
                    "`joined` NUMERIC(1) DEFAULT NULL," +
                    "FOREIGN KEY(uuid) REFERENCES player(uuid));";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createBackDatabase);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        if (!this.getSettings().isEnabled()) {
            return;
        }
        try {
            boolean isNew = false;
            String query = "SELECT joined FROM ever_joined WHERE uuid = ?";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, player.getUuidAsString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                String q = "INSERT INTO ever_joined (uuid, joined) VALUES (?, ?)";
                PreparedStatement p = this.conn.prepareStatement(q);
                p.setString(1, player.getUuidAsString());
                p.setInt(2, 1);
                p.executeUpdate();
                p.close();
                isNew = true;
            }
            rs.close();
            ps.close();
            if (isNew) {
                for (Team t : Objects.requireNonNull(player.getServer()).getScoreboard().getTeams()) {
                    if (t.getName().equals("MIEMBRO")) {
                        player.getServer().getScoreboard().addPlayerToTeam(player.getName().getString(), t);
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
