package dev.watchwolf.server.versionController;

import dev.watchwolf.server.versionController.blocks.BlockManager;
import dev.watchwolf.server.versionController.blocks.BlockPost13;
import dev.watchwolf.server.versionController.blocks.BlockPre13;
import dev.watchwolf.server.versionController.blocks.MinecraftBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

/**
 * Singleton object for cross-version compatibility
 * @author <a href="https://github.com/rogermiranda1000/Spigot-VersionController">https://github.com/rogermiranda1000/Spigot-VersionController</a>
 */
public class VersionController implements BlockManager {
    private static VersionController versionController = null;

    public static final Version version = VersionController.getVersion();
    public static final boolean isPaper = VersionController.getMCPaper();

    private final BlockManager blockManager;

    /**
     * Get the current minecraft version
     * @return version (1.XX)
     */
    private static Version getVersion() {
        // TODO get the full version
        return new Version(1, Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]), 0);
    }

    /**
     * Get if Paper is running (or, by cons, Spigot)
     * https://www.spigotmc.org/threads/how-do-i-detect-if-a-server-is-running-paper.499064/
     * @author Gadse
     * @return Paper (true), Spigot (false)
     */
    private static boolean getMCPaper() {
        return Bukkit.getName().equals("Paper");
    }

    public static VersionController get() {
        if (VersionController.versionController == null) VersionController.versionController = new VersionController();
        return VersionController.versionController;
    }

    private VersionController() {
        this.blockManager = (VersionController.version.compareTo(Version.MC_1_13) < 0) ? new BlockPre13() : new BlockPost13();
    }

    @Override
    public MinecraftBlock getMaterial(String blockData) {
        return this.blockManager.getMaterial(blockData);
    }

    @Override
    public MinecraftBlock getMaterial(Block block) {
        return this.blockManager.getMaterial(block);
    }
}
