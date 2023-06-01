package dev.watchwolf.utils;

import org.bukkit.Bukkit;

public class ServerTypeGetter {
    public static boolean isPaper() {
        return Bukkit.getName().equals("Paper");
    }
}
