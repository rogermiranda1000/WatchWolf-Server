package dev.watchwolf.server.versionController.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MinecraftBlock {
    private static final Pattern blockDataData = Pattern.compile("minecraft:([^\\[]+)(?:\\[(.+)\\])?");

    protected String blockData;

    public MinecraftBlock(String blockData) {
        this.blockData = blockData;
    }

    public MinecraftBlock(Block block) {
        this.blockData = this.blockToBlockData(block);
    }

    /**
     * Converts the block data into a Minecraft material
     * @return The material. Null if none
     * @throws IllegalArgumentException Block data not following the standard
     */
    public Material getMaterial() throws IllegalArgumentException {
        Matcher match = blockDataData.matcher(this.blockData);
        if (!match.find()) throw new IllegalArgumentException("Block data " + this.blockData + " don't match the expected!");
        return Material.getMaterial(match.group(1).toUpperCase());
    }

    public String getBlockData() {
        return this.blockData;
    }

    /**
     * Change the block's type
     * @param block Block to change
     */
    public abstract void setType(Block block);

    /**
     * Get the block data string given a MC block
     * @param block Block to get the data
     * @return Block data
     */
    public abstract String blockToBlockData(Block block);
}
