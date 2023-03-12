package dev.watchwolf.server.versionController.blocks;
import org.bukkit.block.Block;

/**
 * BlockManager for version < 1.13
 */
public class BlockPre13 implements BlockManager {
    @Override
    public MinecraftBlock getMaterial(String blockData) {
        try {
            return new MinecraftBlockPre13(blockData);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public MinecraftBlock getMaterial(Block block) {
        try {
            return new MinecraftBlockPre13(block);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
