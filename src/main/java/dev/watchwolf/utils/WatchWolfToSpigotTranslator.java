package dev.watchwolf.utils;

import dev.watchwolf.entities.Position;
import dev.watchwolf.entities.blocks.Block;
import dev.watchwolf.entities.blocks.transformer.Transformers;
import dev.watchwolf.entities.entities.DroppedItem;
import dev.watchwolf.entities.items.Item;
import dev.watchwolf.server.versionController.VersionController;
import dev.watchwolf.server.versionController.blocks.MinecraftBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * We need to convert WatchWolf blocks into Spigot's block
 */
public class WatchWolfToSpigotTranslator {
    public static MinecraftBlock getBlockData(Block watchWolfBlock) {
        Material spigotMaterial = Material.getMaterial(watchWolfBlock.getName());
        if (spigotMaterial == null) throw new IllegalArgumentException("Couldn't find Spigot material " + watchWolfBlock.getName());
        String spigotBlock = spigotMaterial.createBlockData().getAsString();

        String blockData = Transformers.getBlockData(watchWolfBlock, spigotBlock);
        return VersionController.get().getMaterial(blockData);
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

    public static EntityType getType(dev.watchwolf.entities.entities.EntityType type) {
        return EntityType.valueOf(type.name());
    }

    public static String spawnEntity(dev.watchwolf.entities.entities.Entity entity) throws IllegalArgumentException {
        World w = Bukkit.getWorld(entity.getPosition().getWorld());
        if (w == null) throw new IllegalArgumentException("World '" + entity.getPosition().getWorld() + "' not found");

        Location location = WatchWolfToSpigotTranslator.getLocation(entity.getPosition());
        EntityType entityType = WatchWolfToSpigotTranslator.getType(entity.getType());
        Entity spawned;
        switch (entityType) {
            case DROPPED_ITEM:
                spawned = w.dropItem(location, WatchWolfToSpigotTranslator.getItem(((DroppedItem)entity).getItem()));

                // TODO other special cases

            default:
                spawned = w.spawnEntity(location, entityType);
                // TODO apply other properties
        }

        return spawned.getUniqueId().toString();
    }
}
