package dev.watchwolf.utils;

import dev.watchwolf.entities.Position;
import dev.watchwolf.entities.blocks.Ageable;
import dev.watchwolf.entities.blocks.Block;
import dev.watchwolf.entities.blocks.Directionable;
import dev.watchwolf.entities.blocks.Orientable;
import dev.watchwolf.entities.blocks.transformer.AgeableTransformer;
import dev.watchwolf.entities.blocks.transformer.DirectionableTransformer;
import dev.watchwolf.entities.blocks.transformer.OrientableTransformer;
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

        // TODO do with reflection and a loop
        if (watchWolfBlock instanceof Orientable) spigotBlock = OrientableTransformer.getInstance().modifyBlockData((Orientable) watchWolfBlock, spigotBlock);
        if (watchWolfBlock instanceof Directionable) spigotBlock = DirectionableTransformer.getInstance().modifyBlockData((Directionable) watchWolfBlock, spigotBlock);
        if (watchWolfBlock instanceof Ageable) spigotBlock = AgeableTransformer.getInstance().modifyBlockData((Ageable) watchWolfBlock, spigotBlock);
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
