package dev.watchwolf.utils;

import dev.watchwolf.entities.Position;
import dev.watchwolf.entities.blocks.*;
import dev.watchwolf.entities.blocks.transformer.AgeableTransformer;
import dev.watchwolf.entities.blocks.transformer.DirectionableTransformer;
import dev.watchwolf.entities.blocks.transformer.OrientableTransformer;
import dev.watchwolf.entities.entities.DroppedItem;
import dev.watchwolf.entities.entities.Entity;
import dev.watchwolf.entities.items.Item;
import dev.watchwolf.entities.items.ItemType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpigotToWatchWolfTranslator {
    public static Block getBlock(org.bukkit.block.Block block) {
        return SpigotToWatchWolfTranslator.getBlock(block.getBlockData());
    }

    public static Block getBlock(Material material) {
        return Blocks.getBlock(material.name());
    }

    /*   --- BLOCK DATA TO BLOCK ---   */

    public static Block getBlock(BlockData blockData) {
        Block block = SpigotToWatchWolfTranslator.getBlock(blockData.getMaterial());
        Map<String,String> arguments = getArgumentsAndProperty(blockData);

        // TODO do with reflection and a loop
        if (block instanceof Directionable) block = (Block) DirectionableTransformer.getInstance().applyPropertiesToBlock((Directionable) block, arguments);
        if (block instanceof Orientable) block = (Block) OrientableTransformer.getInstance().applyPropertiesToBlock((Orientable) block, arguments);
        if (block instanceof Ageable) block = (Block) AgeableTransformer.getInstance().applyPropertiesToBlock((Ageable) block, arguments);
        // TODO others

        return block;
    }

    public static Set<String> getArguments(BlockData blockData) {
        return SpigotToWatchWolfTranslator.getArgumentsAndProperty(blockData).keySet();
    }

    /*   --- INTERNAL USE ONLY ---   */

    private static final Pattern blockDataData = Pattern.compile("minecraft:([^\\[]+)\\[(.+)\\]");
    public static Map<String,String> getArgumentsAndProperty(BlockData blockData) {
        Map<String,String> arguments = new HashMap<>();

        Matcher m = SpigotToWatchWolfTranslator.blockDataData.matcher(blockData.getAsString());
        if (!m.find()) return arguments; // just a basic block

        String matName = m.group(1);
        String[] args = m.group(2).split(",");
        for (String arg : args) {
            String[] data = arg.split("=");
            // non-useful data
            if (data[0].startsWith("has_bottle_")
                    || data[0].startsWith("has_record")) continue;                          // inv dependant
            if (data[0].equals("enabled") && matName.equals("HOPPER")
                    || data[0].equals("triggered")) continue;                               // adjacent redstone dependant
            if (data[0].equals("instrument") && matName.equals("NOTE_BLOCK")) continue;  // bottom-block dependant
            if (data[0].equals("occupied")) continue;                                       // entity dependant
            if (data[0].equals("persistent")
                    || data[0].equals("unstable")
                    || data[0].equals("bloom")) continue;                                   // admin block
            if (data[0].equals("distance")) continue;                                       // block dependant (leaves relative to wood)
            if (data[0].equals("stage")) continue;                                          // same block type (aging saplings)
            if (data[0].equals("attached") || data[0].equals("disarmed")) continue;         // block dependant
            if (data[0].equals("power")) continue;                                          // block/event dependant
            if (data[0].equals("tilt")) continue;                                           // entity dependant (over the leaf)
            if (data[0].equals("can_summon") || data[0].equals("shrieking")) continue;      // admin/entity dependant (sculk shrieker)
            if (data[0].equals("bottom") && matName.equals("SCAFFOLDING")) continue;     // bottom-block dependant
            if (data[0].equals("has_book")) continue;                                       // inv dependant
            if (data[0].equals("sculk_sensor_phase")) continue;                             // admin block
            if (data[0].equals("signal_fire")) continue;                                    // bottom-block dependant
            if (data[0].equals("hatch")) continue;                                          // unable to concatenate
            if (data[0].equals("up") && matName.endsWith("_WALL")) continue;             // adjacent-block dependant
            if (data[0].equals("thickness")) continue;                                      // block dependant
            if (data[0].equals("snowy")) continue;                                          // block dependant
            if (data[0].equals("in_wall")) continue;                                        // same block
            // any block is tall by default
            //if (data[1].equals("tall")) continue;                                           // top-block dependant

            if (data[0].equals("lit") && (matName.equals("SMOKER")
                    || matName.equals("FURNACE"))) continue;                             // inv dependant
            if (data[0].equals("powered") && Arrays.asList("ACACIA_DOOR",
                    "ACACIA_FENCE_GATE", "ACACIA_TRAPDOOR", "ACTIVATOR_RAIL",
                    "BELL", "BIRCH_DOOR", "BIRCH_FENCE_GATE", "BIRCH_TRAPDOOR",
                    "CRIMSON_DOOR", "CRIMSON_FENCE_GATE", "CRIMSON_TRAPDOOR",
                    "DARK_OAK_DOOR", "DARK_OAK_FENCE_GATE", "DARK_OAK_TRAPDOOR",
                    "IRON_DOOR", "IRON_TRAPDOOR", "JUNGLE_DOOR", "JUNGLE_FENCE_GATE",
                    "JUNGLE_TRAPDOOR", "LECTERN", "MANGROVE_DOOR",
                    "MANGROVE_FENCE_GATE", "MANGROVE_TRAPDOOR", "NOTE_BLOCK",
                    "OAK_DOOR", "OAK_FENCE_GATE", "OAK_TRAPDOOR", "POWERED_RAIL",
                    "SPRUCE_DOOR", "SPRUCE_FENCE_GATE", "SPRUCE_TRAPDOOR",
                    "TRIPWIRE", "WARPED_DOOR", "WARPED_FENCE_GATE",
                    "WARPED_TRAPDOOR").contains(matName)) continue;
            if (data[0].equals("age") && Arrays.asList("CAVE_VINES", "CACTUS", "FIRE",
                    "KELP", "SUGAR_CANE", "MANGROVE_PROPAGULE", "TWISTING_VINES",
                    "WEEPING_VINES").contains(matName)) continue;

            arguments.put(data[0], data[1]);
        }

        return arguments;
    }

    public static Item getItem(ItemStack item) throws IllegalArgumentException {
        if (item == null) return null;
        return new Item(ItemType.valueOf(item.getType().name()), (byte)item.getAmount());
    }

    public static Position getPosition(Location loc) {
        return new Position((loc.getWorld() == null) ? "" : loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
    }

    public static Entity getEntity(org.bukkit.entity.Entity e) {
        if (e.getType().equals(EntityType.DROPPED_ITEM)) return new DroppedItem(e.getUniqueId().toString(), SpigotToWatchWolfTranslator.getPosition(e.getLocation()),
                SpigotToWatchWolfTranslator.getItem(((org.bukkit.entity.Item)e).getItemStack()));
        return null; // unknown entity; TODO
    }
}
