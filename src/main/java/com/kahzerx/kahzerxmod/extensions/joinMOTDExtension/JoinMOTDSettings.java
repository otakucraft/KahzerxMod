package com.kahzerx.kahzerxmod.extensions.joinMOTDExtension;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

public class JoinMOTDSettings extends ExtensionSettings {
    private String message;
    public JoinMOTDSettings(String name, boolean enabled, String description, String message) {
        super(name, enabled, description);
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
