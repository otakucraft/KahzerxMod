package com.kahzerx.kahzerxmod.extensions.joinMOTDExtension;

import java.util.List;

public class JoinMOTDJsonSettings {
    private List<JoinMOTDSettings> settings;

    public JoinMOTDJsonSettings(List<JoinMOTDSettings> settings) {
        this.settings = settings;
    }

    public List<JoinMOTDSettings> getSettings() {
        return settings;
    }
}
