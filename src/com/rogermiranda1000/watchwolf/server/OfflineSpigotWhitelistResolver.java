package com.rogermiranda1000.watchwolf.server;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * Spigot handles the whitelist by checking the user UUID to the authentication Mojang servers.
 * By setting offline mode to false (so bots can join) the UUID is randomly generated, so it
 * doesn't match by the official server UUID.
 * This class will disable the Spigot whitelist and will check itself the "whitelisted" users.
 */
public class OfflineSpigotWhitelistResolver implements ServerWhitelistResolver, Listener {
    public ArrayList<String> whitelistedUsers;

    public OfflineSpigotWhitelistResolver(JavaPlugin plugin) {
        this.whitelistedUsers = new ArrayList<>();

        org.bukkit.Server server = plugin.getServer();
        server.setWhitelist(false); // the whitelist will be handled internally
        server.getPluginManager().registerEvents(this, plugin); // call onJoin event
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!this.whitelistedUsers.contains(p.getName())) p.kickPlayer("You're not whitelisted!");
    }

    @Override
    public void addToWhitelist(String name) {
        this.whitelistedUsers.add(name);
    }
}
