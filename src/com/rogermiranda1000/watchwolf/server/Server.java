package com.rogermiranda1000.watchwolf.server;

import org.bukkit.plugin.java.JavaPlugin;

public class Server extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Loading socket data...");
        // TODO load
        // TODO send packet
        // TODO event?
    }

    @Override
    public void onDisable() {
        // TODO send packet
    }
}
