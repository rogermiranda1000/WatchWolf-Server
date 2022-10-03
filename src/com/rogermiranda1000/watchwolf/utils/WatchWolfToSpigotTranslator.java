package com.rogermiranda1000.watchwolf.utils;

import com.rogermiranda1000.watchwolf.entities.Position;
import com.rogermiranda1000.watchwolf.entities.blocks.Block;
import com.rogermiranda1000.watchwolf.entities.blocks.Orientable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * We need to convert WatchWolf blocks into Spigot's block
 */
public class WatchWolfToSpigotTranslator {
    public static BlockData getBlockData(Block watchWolfBlock) {
        Material spigotMaterial = Material.getMaterial(watchWolfBlock.getName());
        if (spigotMaterial == null) throw new IllegalArgumentException("Couldn't find Spigot material " + watchWolfBlock.getName());
        String spigotBlock = spigotMaterial.createBlockData().getAsString();
        if (watchWolfBlock instanceof Orientable) {
            boolean doubleType = false;
            try {
                Orientable orientable = (Orientable)watchWolfBlock;
                if (orientable.isSet(Orientable.Orientation.U)) {
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "up", "true");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "face", "ceiling");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "half", "top");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "half", "upper");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "facing", "up");
                    try {
                        doubleType = orientable.isSet(Orientable.Orientation.D); // both top and bottom
                    } catch (IllegalArgumentException ignore) {}
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "type", doubleType ? "double" : "top");
                    // TODO orientation
                    // TODO shape
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "hanging", "true");
                }
                else spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "up", "false");
            } catch (IllegalArgumentException ignore) {}
            // TODO down, north, south, east, west
        }
        // TODO others

        return Bukkit.createBlockData(spigotBlock);
    }

    private static String setBlockDataProperty(String blockData, String property, String value) {
        return blockData.replaceAll("(?=[,\\[])" + property + "=[^,\\]]+", property + "=" + value);
    }

    public static Location getLocation(Position pos) {
        return new Location(Bukkit.getWorld(pos.getWorld()), pos.getX(), pos.getY(), pos.getZ());
    }
}
