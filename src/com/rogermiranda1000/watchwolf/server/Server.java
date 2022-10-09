package com.rogermiranda1000.watchwolf.server;

import com.rogermiranda1000.watchwolf.entities.Position;
import com.rogermiranda1000.watchwolf.entities.blocks.Block;
import com.rogermiranda1000.watchwolf.utils.SpigotToWatchWolfTranslator;
import com.rogermiranda1000.watchwolf.utils.WatchWolfToSpigotTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server extends JavaPlugin implements ServerPetition, SequentialExecutor {
    private ServerConnector connector;

    private final Queue<SequentialExecutor.ThrowableRunnable> futureExecute = new LinkedList<>();
    private int futureExecuteTaskID;

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

    private static boolean isUsername(String nick) {
        Pattern pattern = Pattern.compile("^\\w{3,16}$"); // https://help.minecraft.net/hc/en-us/articles/4408950195341-Minecraft-Java-Edition-Username-VS-Gamertag-FAQ
        Matcher m = pattern.matcher(nick);
        return m.matches();
    }

    @Override
    public void opPlayer(String nick) {
        if (!Server.isUsername(nick)) return;
        getLogger().info("OP player (" + nick + ") request");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "op " + nick);
    }

    @Override
    public void whitelistPlayer(String nick) {
        if (!Server.isUsername(nick)) return;
        getLogger().info("Whitelist player (" + nick + ") request");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + nick);
    }

    @Override
    public void stopServer(ServerStopNotifier onServerStop) {
        getLogger().info("Stop server request");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
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
}
