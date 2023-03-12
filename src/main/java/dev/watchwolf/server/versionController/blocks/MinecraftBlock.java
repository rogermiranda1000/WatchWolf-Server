package dev.watchwolf.server.versionController.blocks;

import org.bukkit.block.Block;

public abstract class MinecraftBlock {
    protected String blockData;

    public MinecraftBlock(String blockData) {
        this.blockData = blockData;
    }

    public MinecraftBlock(Block block) {
        this.blockData = this.blockToBlockData(block);
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
