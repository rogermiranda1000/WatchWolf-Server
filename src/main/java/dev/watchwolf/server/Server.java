package dev.watchwolf.server;

import dev.watchwolf.entities.Container;
import dev.watchwolf.entities.Position;
import dev.watchwolf.entities.blocks.Block;
import dev.watchwolf.entities.entities.Entity;
import dev.watchwolf.entities.items.Item;
import dev.watchwolf.utils.SpigotToWatchWolfTranslator;
import dev.watchwolf.utils.WatchWolfToSpigotTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
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
        final FileConfiguration config = this.getConfig();
        final String ip = config.getString("target-ip");
        final int port = config.getInt("use-port");
        final String []replyIP = config.getString("reply").split(":");

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

        // notify back once the server has fully started
        this.run(()->{
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
        });

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
        getLogger().info("Get " + username + "'s position request");
        Player p = Bukkit.getPlayer(username);
        if (p == null) return null;
        Location loc = p.getLocation();
        return new Position(p.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public float getPlayerPitch(String username) throws IOException {
        getLogger().info("Get " + username + "'s pitch request");
        Player p = Bukkit.getPlayer(username);
        if (p == null) return 0.0f;
        return p.getLocation().getPitch();
    }

    @Override
    public float getPlayerYaw(String username) throws IOException {
        getLogger().info("Get " + username + "'s yaw request");
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
        getLogger().info("Tp " + username + " to " + position + " request");
        Player p = Bukkit.getPlayer(username);
        if (p == null) return;
        p.teleport(WatchWolfToSpigotTranslator.getLocation(position));
    }

    @Override
    public Container getInventory(String username) throws IOException {
        getLogger().info("Get " + username + "'s inventory request");
        Player p = Bukkit.getPlayer(username);
        if (p == null) return null;
        ItemStack []r = p.getInventory().getContents();
        Item []items = new Item[r.length];
        for (int n = 0; n < items.length; n++) items[n] = SpigotToWatchWolfTranslator.getItem(r[n]);
        return new Container(items);
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
        getLogger().info("Set block " + block.toString() + " at " + position.toString() + " request");
        WatchWolfToSpigotTranslator.setBlockData(block, WatchWolfToSpigotTranslator.getLocation(position).getBlock());
    }

    @Override
    public Block getBlock(Position position) throws IOException {
        Location loc = WatchWolfToSpigotTranslator.getLocation(position);
        Block found = SpigotToWatchWolfTranslator.getBlock(loc.getBlock());
        getLogger().info("Get block at " + position.toString() + " request; Found " + found.toString());
        return found;
    }

    @Override
    public void runCommand(String cmd) throws IOException {
        getLogger().info("Run " + cmd + " request");
        this.runSpigotCommand(cmd);
    }

    @Override
    public Entity[] getEntities(Position position, double radius) throws IOException {
        getLogger().info("Get entities request");
        return this.getEntitiesByRadius(position, radius).stream().map(e -> SpigotToWatchWolfTranslator.getEntity(e))
                .filter(Objects::nonNull).toArray(Entity[]::new);
    }

    @Override
    public String spawnEntity(Entity entity) throws IOException {
        getLogger().info("Spawn " + entity.toString() + " request");
        try {
            return WatchWolfToSpigotTranslator.spawnEntity(entity);
        } catch (Exception ex) {
            getLogger().warning(ex.getMessage());
        }
        return "";
    }

    public List<org.bukkit.entity.Entity> getEntitiesByRadius(Position position, double radius) {
        ArrayList<org.bukkit.entity.Entity> r = new ArrayList<>();

        World world = Bukkit.getWorld(position.getWorld());
        if (world == null) return r; // no entities

        for (org.bukkit.entity.Entity e : world.getEntities()) {
            if (Math.abs(e.getLocation().getX() - position.getX()) <= radius
                    && Math.abs(e.getLocation().getZ() - position.getZ()) <= radius) r.add(e); // inside the radius
        }

        return r;
    }

    @Override
    public void synchronize() throws IOException {}
}
