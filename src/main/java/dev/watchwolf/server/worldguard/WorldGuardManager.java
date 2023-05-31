package dev.watchwolf.server.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.watchwolf.entities.Position;
import dev.watchwolf.server.ExtendedPetitionManager;
import dev.watchwolf.server.WorldGuardServerPetition;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class WorldGuardManager extends ExtendedPetitionManager implements WorldGuardServerPetition {
    public WorldGuardManager(JavaPlugin watchwolf, Plugin plugin) {
        super(watchwolf, plugin);
    }

    @Override
    public void createRegion(String s, Position position, Position position1) throws IOException {
        if (!position.getWorld().equals(position1.getWorld())) throw new IllegalArgumentException("A region can only be in one world!");

        World positionWorld = Bukkit.getWorld(position.getWorld());
        if (positionWorld == null) throw new IllegalArgumentException("The world specified doesn't exists.");

        try {
            Class<?> bukkitAdapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Method adaptBukkitWorldToWorldGuards = bukkitAdapter.getDeclaredMethod("adapt", World.class);
            Class<?> wgWorld = Class.forName("com.sk89q.worldedit.world.World");
            Class<?> vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
            Method vectorBuilder = vectorClass.getDeclaredMethod("at", double.class, double.class, double.class);
            Class<?> protectedRegionClass = Class.forName("com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion");
            Constructor<?> protectedRegionBuilder = protectedRegionClass.getConstructor(String.class, vectorClass, vectorClass);

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            Method getContainerRegion = container.getClass().getDeclaredMethod("get", wgWorld);
            RegionManager worldManager = (RegionManager)getContainerRegion.invoke(container, wgWorld.cast(adaptBukkitWorldToWorldGuards.invoke(null, positionWorld)));

            ProtectedCuboidRegion region = (ProtectedCuboidRegion)protectedRegionBuilder.newInstance(s,
                            vectorClass.cast(vectorBuilder.invoke(null, position.getX(), position.getY(), position.getZ())),
                            vectorClass.cast(vectorBuilder.invoke(null, position1.getX(), position1.getY(), position1.getZ())));
            worldManager.addRegion(region);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String[] getRegions() throws IOException {
        ArrayList<String> regions = new ArrayList<>();

        try {
            Class<?> bukkitAdapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Method adaptBukkitWorldToWorldGuards = bukkitAdapter.getDeclaredMethod("adapt", World.class);
            Class<?> wgWorld = Class.forName("com.sk89q.worldedit.world.World");

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            Method getContainerRegion = container.getClass().getDeclaredMethod("get", wgWorld);
            for (World world : Bukkit.getWorlds()) {
                RegionManager worldManager = (RegionManager)getContainerRegion.invoke(container, wgWorld.cast(adaptBukkitWorldToWorldGuards.invoke(null, world)));
                regions.addAll(worldManager.getRegions().keySet());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return regions.toArray(new String[0]);
    }

    @Override
    public String[] getRegions(Position position) throws IOException {
        World positionWorld = Bukkit.getWorld(position.getWorld());
        if (positionWorld == null) throw new IllegalArgumentException("The world specified doesn't exists.");

        try {
            Class<?> bukkitAdapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Method adaptBukkitWorldToWorldGuards = bukkitAdapter.getDeclaredMethod("adapt", World.class);
            Class<?> wgWorld = Class.forName("com.sk89q.worldedit.world.World");
            Class<?> vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
            Method vectorBuilder = vectorClass.getDeclaredMethod("at", double.class, double.class, double.class);

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            Method getContainerRegion = container.getClass().getDeclaredMethod("get", wgWorld);
            RegionManager worldManager = (RegionManager)getContainerRegion.invoke(container, wgWorld.cast(adaptBukkitWorldToWorldGuards.invoke(null, positionWorld)));
            Method getApplicableRegions = worldManager.getClass().getDeclaredMethod("getApplicableRegions", vectorClass);
            Object positionVector = vectorBuilder.invoke(null, position.getX(), position.getY(), position.getZ());
            return ((ApplicableRegionSet)getApplicableRegions.invoke(worldManager, vectorClass.cast(positionVector))).getRegions().stream().map(r -> r.getId())
                    .collect(Collectors.toList()).toArray(new String[0]);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
