package com.ome_r.superiorskyblock.utils;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtils {

    private static ASkyBlock plugin = ASkyBlock.getPlugin();

    public static List<Island> loadAllIslands(){
        List<Island> islands = new ArrayList<>();
        File folder = new File(plugin.getDataFolder(), "islands-" + Settings.worldName);

        if(!folder.exists())
            return islands;

        for(File islandFile : folder.listFiles()){
            islands.add(loadIsland(islandFile));
        }

        return islands;
    }

    public static Island loadIsland(File file){
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        plugin.getLogger().info("Loading " + file.getName() + "...");

        if(cfg.contains("island")){
            return (Island) cfg.get("island");
        }

        else{
            return new Island(cfg);
        }
    }

    public static void deleteIsland(UUID oldOwner){
        File file = new File(plugin.getDataFolder(), "islands-" + Settings.worldName + "/" + oldOwner + ".yml");
        file.delete();
    }

    public static void saveIsland(Island island){
        File file = new File(plugin.getDataFolder(), "islands-" + Settings.worldName + "/" + island.getOwner() + ".yml");

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            if(cfg.contains("island"))
                cfg.set("island", null);

            cfg.set("center.x", island.getCenter().getBlockX());
            cfg.set("center.y", island.getCenter().getBlockY());
            cfg.set("center.z", island.getCenter().getBlockZ());
            cfg.set("protection-range", island.getProtectionSize());
            cfg.set("island-distance", island.getIslandDistance());
            cfg.set("owner", island.isSpawn() ? "spawn" : island.getOwner() == null ? "null" : island.getOwner().toString());
            cfg.set("locked", island.isLocked());
            cfg.set("purge-protected", island.isPurgeProtected());
            if(island.getSpawnPoint() != null)
                cfg.set("spawn-point", Util.getStringLocation(island.getSpawnPoint()));
            cfg.set("settings", island.getSettings());
            cfg.set("biome", island.getBiome().toString());
            cfg.set("level", island.getLevelHandicap());
            cfg.set("hoppers-amount", island.getHoppersAmount());
            cfg.set("upgrades", island.getUpgradesList());
            cfg.set("counted-blocks", island.getCountedBlocksList());
            cfg.set("stacked-blocks", island.getStackedBlocks());
            cfg.set("blocks-calculation", island.getWorth());
            cfg.set("paypal", island.getPaypal());
            cfg.set("discord", island.getDiscord());

            cfg.save(file);
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

}
