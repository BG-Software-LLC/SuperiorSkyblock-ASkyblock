package com.ome_r.superiorskyblock.handlers;

import com.ome_r.superiorskyblock.SuperiorSkyblock;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class DataHandler {

    private static ASkyBlock plugin = ASkyBlock.getPlugin();

    public static void loadAllOldData(){
        loadOldStackedBlocksData();
        loadOldUpgradesData();
    }

    public static void loadOldUpgradesData(){
        File file = new File(plugin.getDataFolder(), "upgrades.yml");

        if(!file.exists())
            return;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        if(!cfg.contains("inventory.upgrades"))
            return;

        ConfigurationSection section = cfg.getConfigurationSection("inventory.upgrades");

        for(String sectionName : section.getKeys(false)){
            if(section.contains(sectionName + ".islands")){
                List<String> islands = section.getStringList(sectionName + ".islands");

                for(String islandUUID : islands) {
                    Island island;
                    UUID uuid = UUID.fromString(islandUUID.split(":")[0]);
                    if ((island = plugin.getGrid().getIsland(uuid)) != null) {
                        int level = Integer.parseInt(islandUUID.split(":")[1]);
                        switch (sectionName.toUpperCase()) {
                            case "DIAMOND_HOE":
                                island.setCropGrowthLevel(level + 1);
                                break;
                            case "MOB_SPAWNER":
                                island.setSpawnRatesLevel(level + 1);
                                break;
                            case "ROTTEN_FLESH":
                                island.setMobDropsLevel(level + 1);
                                break;
                            case "HOPPER":
                                island.setHoppersLevel(level + 1);
                                break;
                            default:
                                SuperiorSkyblock.log("Couldn't understand which upgrade is: " + sectionName.toUpperCase());
                                break;
                        }
                    }
                }
            }
        }

        file.delete();
    }

    public static void loadOldStackedBlocksData(){
        File file = new File(plugin.getDataFolder(), "holograms.yml");

        if(!file.exists())
            return;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = cfg.getConfigurationSection("");

        for(String hologramName : section.getKeys(false)){
            World world = Bukkit.getWorld(section.getString(hologramName + ".world"));
            double x = section.getDouble(hologramName + ".x"), y = section.getDouble(hologramName + ".y"), z = section.getDouble(hologramName + ".z");
            Location blockLocation = new Location(world, x, y - 2, z);
            int amount = -1;
            for(String line : section.getStringList(hologramName + ".lines")){
                String[] sections;
                if((sections = line.split(": ")).length > 0){
                    amount = Integer.valueOf(sections[1]);
                    break;
                }
            }

            if(amount != -1){
                Island island = plugin.getGrid().getIslandAt(blockLocation);
                island.setStackedBlock(blockLocation, amount);
            }
        }

        file.delete();
    }

}
