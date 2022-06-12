package com.rogermiranda1000.watchwolf.server;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server extends JavaPlugin implements ServerPetition {
    private ServerConnector connector;

    @Override
    public void onEnable() {
        getLogger().info("Loading socket data...");

        // read data
        FileConfiguration config = this.getConfig();
        String ip = config.getString("target-ip");
        int port = config.getInt("use-port");
        String []replyIP = config.getString("reply").split(":");

        try {
            getLogger().info("Hosting on " + port + " (for " + ip + ")");
            this.connector = new ServerConnector(ip, port, new Socket(replyIP[0], Integer.parseInt(replyIP[1])), config.getString("key"), this);
            this.connector.onServerStart();
            new Thread(this.connector).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO events?
    }

    @Override
    public void onDisable() {
        this.connector.close();
    }

    private static boolean isUsername(String nick) {
        Pattern pattern = Pattern.compile("^\\w{3,16}$"); // https://help.minecraft.net/hc/en-us/articles/4408950195341-Minecraft-Java-Edition-Username-VS-Gamertag-FAQ
        Matcher m = pattern.matcher(nick);
        return m.matches();
    }

    @Override
    public void opPlayer(String nick) {
        if (!Server.isUsername(nick)) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "op " + nick);
    }

    @Override
    public void whitelistPlayer(String nick) {
        if (!Server.isUsername(nick)) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + nick);
    }

    @Override
    public void stopServer(ServerStopNotifier onServerStop) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
    }
}
