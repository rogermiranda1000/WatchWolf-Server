package dev.watchwolf.utils;

import dev.watchwolf.entities.Position;
import dev.watchwolf.entities.blocks.Block;
import dev.watchwolf.entities.blocks.Directionable;
import dev.watchwolf.entities.blocks.Orientable;
import dev.watchwolf.entities.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

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
            Orientable orientable = (Orientable)watchWolfBlock;
            try {
                if (orientable.isOrientationSet(Orientable.Orientation.U)) {
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "up", "true");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "face", "ceiling");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "attachment", "ceiling");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "half", "top");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "half", "upper");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "facing", "up");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "vertical-direction", "up");
                    try {
                        doubleType = orientable.isOrientationSet(Orientable.Orientation.D); // both top and bottom
                    } catch (IllegalArgumentException ignore) {}
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "type", doubleType ? "double" : "top");
                    // TODO orientation
                    // TODO shape
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "hanging", "true");
                }
                else spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "up", "false");
            } catch (IllegalArgumentException ignore) {}

            try {
                if (orientable.isOrientationSet(Orientable.Orientation.D)) {
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "down", "true");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "face", "floor");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "attachment", "floor");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "half", "bottom");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "half", "lower");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "facing", "down");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "vertical-direction", "down");
                    // double already done on top
                    // TODO orientation
                }
                else spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "down", "false");
            } catch (IllegalArgumentException ignore) {}

            try {
                if (orientable.isOrientationSet(Orientable.Orientation.N)) {
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "north", "true");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "facing", "north");
                    // TODO orientation
                    // TODO shape
                }
                else spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "north", "false");
            } catch (IllegalArgumentException ignore) {}

            try {
                if (orientable.isOrientationSet(Orientable.Orientation.S)) {
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "south", "true");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "facing", "south");
                    // TODO orientation
                    // TODO shape
                }
                else spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "south", "false");
            } catch (IllegalArgumentException ignore) {}

            try {
                if (orientable.isOrientationSet(Orientable.Orientation.E)) {
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "east", "true");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "facing", "east");
                    // TODO orientation
                    // TODO shape
                }
                else spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "east", "false");
            } catch (IllegalArgumentException ignore) {}

            try {
                if (orientable.isOrientationSet(Orientable.Orientation.W)) {
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "west", "true");
                    spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "facing", "west");
                    // TODO orientation
                    // TODO shape
                }
                else spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "west", "false");
            } catch (IllegalArgumentException ignore) {}
        }
        if (watchWolfBlock instanceof Directionable) {
            Directionable directionable = (Directionable)watchWolfBlock;
            Directionable.Direction current = directionable.getFacingDirection();
            if (current == Directionable.Direction.X) spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "axis", "x");
            else if (current == Directionable.Direction.Y) spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "axis", "y");
            else if (current == Directionable.Direction.Z) spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "axis", "z");
            else if (current == Directionable.Direction.DOUBLE_WALL) spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "attachment", "double_wall");
            else if (current == Directionable.Direction.SINGLE_WALL) spigotBlock = WatchWolfToSpigotTranslator.setBlockDataProperty(spigotBlock, "attachment", "single_wall");
        }
        // TODO others

        return Bukkit.createBlockData(spigotBlock);
    }

    private static String setBlockDataProperty(String blockData, String property, String value) {
        return blockData.replaceAll("(?<=[,\\[])" + property + "=[^,\\]]+", property + "=" + value);
    }

    public static Location getLocation(Position pos) {
        return new Location(Bukkit.getWorld(pos.getWorld()), pos.getX(), pos.getY(), pos.getZ());
    }

    public static ItemStack getItem(Item item) {
        return new ItemStack(Material.valueOf(item.getType().name()), item.getAmount());
    }
}
