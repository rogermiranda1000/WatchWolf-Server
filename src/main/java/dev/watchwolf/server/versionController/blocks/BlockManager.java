package dev.watchwolf.server.versionController.blocks;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author <a href="https://github.com/rogermiranda1000/Spigot-VersionController">https://github.com/rogermiranda1000/Spigot-VersionController</a>
 */
public interface BlockManager {
    /**
     * String (representing the block data) to material
     * @param blockData Material's block data (e.g. WHITE_WOOL)
     * @return Material (null if IllegalArgumentException)
     */
    MinecraftBlock getMaterial(String blockData);


    /**
     * Block to material
     * @param block Block
     * @return Material (null if IllegalArgumentException)
     */
    MinecraftBlock getMaterial(Block block);

    /**
     * Block material to material
     * @param material Block material
     * @return Material (null if IllegalArgumentException)
     */
    MinecraftBlock getMaterial(Material material);
}
