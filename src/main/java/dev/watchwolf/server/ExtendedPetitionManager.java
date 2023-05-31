package dev.watchwolf.server;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ExtendedPetitionManager {
    private JavaPlugin watchwolf;
    private Plugin plugin;

    public ExtendedPetitionManager(JavaPlugin watchwolf, Plugin plugin) {
        this.watchwolf = watchwolf;
        this.plugin = plugin;
    }

    public JavaPlugin getWatchWolf() {
        return watchwolf;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
