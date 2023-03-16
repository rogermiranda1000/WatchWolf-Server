package dev.watchwolf.server.versionController.blocks;
import org.bukkit.Material;
import org.bukkit.block.Block;

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

    @Override
    public MinecraftBlock getMaterial(Block block) {
        try {
            return new MinecraftBlockPost13(block);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public MinecraftBlock getMaterial(Material material) {
        try {
            return new MinecraftBlockPost13(material);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
