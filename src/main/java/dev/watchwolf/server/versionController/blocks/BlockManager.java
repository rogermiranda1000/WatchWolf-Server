package dev.watchwolf.server.versionController.blocks;

/**
 * @author <a href="https://github.com/rogermiranda1000/Spigot-VersionController">https://github.com/rogermiranda1000/Spigot-VersionController</a>
 */
public interface BlockManager {
    /**
     * String to material
     * @param blockData Material's block data (e.g. WHITE_WOOL)
     * @return Material (null if IllegalArgumentException)
     */
    MinecraftBlock getMaterial(String blockData);
}
