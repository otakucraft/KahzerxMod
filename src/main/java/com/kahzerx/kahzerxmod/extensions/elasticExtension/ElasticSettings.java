package com.kahzerx.kahzerxmod.extensions.elasticExtension;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

public class ElasticSettings extends ExtensionSettings {
    private String host;
    private String user;
    private String password;
    private int port;
    public ElasticSettings(String name, boolean enabled, String description, String host, String user, String password, int port) {
        super(name, enabled, description);
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
