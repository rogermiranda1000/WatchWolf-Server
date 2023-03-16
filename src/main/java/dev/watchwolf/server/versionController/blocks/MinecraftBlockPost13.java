package dev.watchwolf.server.versionController.blocks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import java.util.regex.Pattern;

/**
 * @author <a href="https://github.com/rogermiranda1000/Spigot-VersionController">https://github.com/rogermiranda1000/Spigot-VersionController</a>
 */
public class MinecraftBlockPost13 extends MinecraftBlock {
    private static final Pattern blockDataData = Pattern.compile("minecraft:[^\\[]+(\\[(.+)\\])?");
    private final BlockData data;

    public MinecraftBlockPost13(String blockData) throws IllegalArgumentException {
        super(blockData);

        BlockData data;
        try {
            data = Bukkit.createBlockData(this.blockData);
        } catch (IllegalArgumentException ex) {
            Material mat = Material.getMaterial(this.blockData);
            if (mat == null) throw new IllegalArgumentException(this.blockData + " is not a Material, nor BlockData");
            data = mat.createBlockData();
        }
        this.data = data;
    }

    public MinecraftBlockPost13(Block block) throws IllegalArgumentException {
        this(MinecraftBlockPost13.staticBlockToBlockData(block));
    }

    public MinecraftBlockPost13(Material material) throws IllegalArgumentException {
        this(material.createBlockData().getAsString());
    }

    @Override
    public void setType(Block block) {
        block.setBlockData(this.data);
    }

    @Override
    public String blockToBlockData(Block block) {
        return MinecraftBlockPost13.staticBlockToBlockData(block);
    }

    public static String staticBlockToBlockData(Block block) {
        return block.getBlockData().getAsString();
    }
}