package com.ome_r.superiorskyblock.legacy;

import org.bukkit.Art;
import org.bukkit.Bukkit;

public class LegacyArt{

    private static boolean isLegacy = !Bukkit.getVersion().contains("1.13");

    public static Art DONKEY_KONG = Art.valueOf(isLegacy ? "DONKEYKONG" : "DONKEY_KONG");
    public static Art BURNING_SKULL = Art.valueOf(isLegacy ? "BURNINGSKULL" : "BURNING_SKULL");

}
