package dev.watchwolf.server.versionController.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/rogermiranda1000/Spigot-VersionController">https://github.com/rogermiranda1000/Spigot-VersionController</a>
 */
public class MinecraftBlockPre13 extends MinecraftBlock {
    private final ItemStack type;

    private static final Method setTypeMethod = MinecraftBlockPre13.getSetTypeMethod();

    private static Method getSetTypeMethod() {
        try {
            return Block.class.getMethod("setTypeIdAndData", int.class, byte.class, boolean.class);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public MinecraftBlockPre13(String blockData) throws IllegalArgumentException {
        super(blockData);

        this.type = new ItemStack(Material.valueOf(data[0]), 1, Short.parseShort(data[1]));
    }

    @Override
    public void setType(Block block) {
        try {
            MinecraftBlockPre13.setTypeMethod.invoke(block, this.type.getType().getId(), this.type.getData().getData(), true); // TODO gravity
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException | NullPointerException ignored) {}
    }
}
