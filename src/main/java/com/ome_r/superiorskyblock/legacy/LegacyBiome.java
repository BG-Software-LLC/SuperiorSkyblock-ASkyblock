package com.ome_r.superiorskyblock.legacy;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;

public class LegacyBiome{

    private static boolean isLegacy = !Bukkit.getVersion().contains("1.13");

    public static Biome NETHER = Biome.valueOf(isLegacy ? "HELL" : "NETHER");

}
