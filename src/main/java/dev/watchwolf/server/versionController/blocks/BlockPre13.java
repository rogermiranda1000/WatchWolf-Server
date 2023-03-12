package dev.watchwolf.server.versionController.blocks;

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
}
