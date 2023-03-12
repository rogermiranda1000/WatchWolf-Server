package dev.watchwolf.server.versionController.blocks;

import org.bukkit.block.Block;

public abstract class MinecraftBlock {
    protected String blockData;

    public MinecraftBlock(String blockData) {
        this.blockData = blockData;
    }

    /**
     * Change the block's type
     * @param block Block to change
     */
    public abstract void setType(Block block);
}
