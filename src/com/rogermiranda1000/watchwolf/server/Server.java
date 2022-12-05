package com.rogermiranda1000.watchwolf.server;

import com.rogermiranda1000.watchwolf.entities.Position;
import com.rogermiranda1000.watchwolf.entities.blocks.Block;
import com.rogermiranda1000.watchwolf.entities.items.Item;
import com.rogermiranda1000.watchwolf.utils.SpigotToWatchWolfTranslator;
import com.rogermiranda1000.watchwolf.utils.WatchWolfToSpigotTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server extends JavaPlugin implements ServerPetition, SequentialExecutor {
    private ServerWhitelistResolver whitelistResolver;

    private ServerConnector connector;

    private final Queue<SequentialExecutor.ThrowableRunnable> futureExecute = new LinkedList<>();
    private int futureExecuteTaskID;

    @Override
    public void onEnable() {
        this.whitelistResolver = new OfflineSpigotWhitelistResolver(this);

        getLogger().info("Loading socket data...");

        // read data
        FileConfiguration config = this.getConfig();
        String ip = config.getString("target-ip");
        int port = config.getInt("use-port");
        String []replyIP = config.getString("reply").split(":");

        try {
            getLogger().info("Hosting on " + port + " (for " + ip + ")");
            getLogger().info("Reply to " + replyIP[0] + ":" + replyIP[1]);
            this.connector = new ServerConnector(ip, port, new Socket(replyIP[0], Integer.parseInt(replyIP[1])), config.getString("key"), this, this);

            this.connector.onServerStart();
            getLogger().info("Server started notified.");

            new Thread(this.connector).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // execute sequentially the orders one by one (and letting the server update)
        this.futureExecuteTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            synchronized (this.futureExecute) {
                if (this.futureExecute.isEmpty()) return;

                SequentialExecutor.ThrowableRunnable run = this.futureExecute.remove();
                try {
                    run.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 0L, 1);

        // TODO events?
    }

    @Override
    public void onDisable() {
        this.connector.close();
        Bukkit.getScheduler().cancelTask(this.futureExecuteTaskID);
    }

    @Override
    public void run(SequentialExecutor.ThrowableRunnable run) {
        synchronized (this.futureExecute) {
            this.futureExecute.add(run);
        }
    }

    private void runSpigotCommand(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    private static boolean isUsername(String nick) {
        Pattern pattern = Pattern.compile("^\\w{3,16}$"); // https://help.minecraft.net/hc/en-us/articles/4408950195341-Minecraft-Java-Edition-Username-VS-Gamertag-FAQ
        Matcher m = pattern.matcher(nick);
        return m.matches();
    }

    @Override
    public void opPlayer(String nick) {
        if (!Server.isUsername(nick)) return;
        getLogger().info("OP player (" + nick + ") request");
        this.runSpigotCommand("op " + nick);
    }

    @Override
    public void whitelistPlayer(String nick) {
        if (!Server.isUsername(nick)) return;
        getLogger().info("Whitelist player (" + nick + ") request");
        this.whitelistResolver.addToWhitelist(nick);
    }

    @Override
    public Position getPlayerPosition(String username) throws IOException {
        Player p = Bukkit.getPlayer(username);
        if (p == null) return null;
        Location loc = p.getLocation();
        return new Position(p.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public float getPlayerPitch(String username) throws IOException {
        Player p = Bukkit.getPlayer(username);
        if (p == null) return 0.0f;
        return p.getLocation().getPitch();
    }

    @Override
    public float getPlayerYaw(String username) throws IOException {
        Player p = Bukkit.getPlayer(username);
        if (p == null) return 0.0f;
        return p.getLocation().getYaw();
    }

    @Override
    public void giveItem(String s, Item item) throws IOException {
        getLogger().info("Give " + item.toString() + " to " + s + " request");
        Player p = Bukkit.getPlayer(s);
        if (p == null) {
            getLogger().info("Player " + s + " not found");
            return;
        }
        p.getInventory().addItem(WatchWolfToSpigotTranslator.getItem(item));
    }

    @Override
    public void tp(String username, Position position) throws IOException {
        Player p = Bukkit.getPlayer(username);
        if (p == null) return;
        p.teleport(WatchWolfToSpigotTranslator.getLocation(position));
    }

    @Override
    public String[] getPlayers() throws IOException {
        getLogger().info("Get players request");
        return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toArray(String[]::new);
    }

    @Override
    public void stopServer(ServerStopNotifier onServerStop) {
        getLogger().info("Stop server request");
        this.runSpigotCommand("stop");
    }

    @Override
    public void setBlock(Position position, Block block) throws IOException {
        BlockData bd = WatchWolfToSpigotTranslator.getBlockData(block);
        getLogger().info("Set block " + bd.getAsString() + " at " + position.toString() + " request");
        WatchWolfToSpigotTranslator.getLocation(position)
                .getBlock().setBlockData(bd);
    }

    @Override
    public Block getBlock(Position position) throws IOException {
        Location loc = WatchWolfToSpigotTranslator.getLocation(position);
        Block found = SpigotToWatchWolfTranslator.getBlock(loc.getBlock());
        getLogger().info("Get block at " + position.toString() + " request; Found " + loc.getBlock().getBlockData());
        return found;
    }

    @Override
    public void runCommand(String cmd) throws IOException {
        getLogger().info("Run " + cmd + " request");
        this.runSpigotCommand(cmd);
    }

    @Override
    public void synchronize() throws IOException {}
}
