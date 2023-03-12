package dev.watchwolf.server.versionController.blocks;

/**
 * BlockManager for version >= 1.13
 * @author <a href="https://github.com/rogermiranda1000/Spigot-VersionController">https://github.com/rogermiranda1000/Spigot-VersionController</a>
 */
public class BlockPost13 implements BlockManager {
    @Override
    public MinecraftBlock getMaterial(String blockData) {
        try {
            return new MinecraftBlockPost13(blockData);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
