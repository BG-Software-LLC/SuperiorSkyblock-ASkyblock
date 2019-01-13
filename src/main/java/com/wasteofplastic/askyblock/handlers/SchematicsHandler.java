package com.wasteofplastic.askyblock.handlers;

import com.ome_r.superiorskyblock.legacy.LegacyBiome;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.schematics.ISchematic;
import com.wasteofplastic.askyblock.schematics.Schematic;
import com.wasteofplastic.askyblock.util.VaultHelper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SchematicsHandler {

    private HashMap<String, ISchematic> schematics = new HashMap<>();
    private ASkyBlock plugin;

    public SchematicsHandler(ASkyBlock plugin){
        this.plugin = plugin;
        loadSchematics();
    }

    public ISchematic getSchematic(String schematic){
        return schematics.get(schematic);
    }

    public boolean isSchematic(String schematic){
        return schematics.containsKey(schematic);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadSchematics() {
        // Check if there is a schematic folder and make it if it does not exist
        File schematicFolder = new File(plugin.getDataFolder(), "schematics");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdir();
        }
        // Clear the schematic list that is kept in memory
        schematics.clear();
        // Load the default schematic if it exists
        // Set up the default schematic
        File schematicFile = new File(schematicFolder, "island.schematic");
        File netherFile = new File(schematicFolder, "nether.schematic");
        if (!schematicFile.exists()) {
            // Only copy if the default exists
            if (plugin.getResource("schematics/island.schematic") != null) {
                plugin.getLogger().info("Default schematic does not exist, saving it...");
                plugin.saveResource("schematics/island.schematic", false);
                // Add it to schematics
                try {
                    schematics.put("default",new Schematic(plugin, schematicFile));
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not load default schematic!");
                    e.printStackTrace();
                }
                // If this is repeated later due to the schematic config, fine, it will only add info
            } else {
                // No islands.schematic in the jar, so just make the default using built-in island generation
                schematics.put("default",new Schematic(plugin));
            }
            plugin.getLogger().info("Loaded default nether schematic");
        } else {
            // It exists, so load it
            try {
                schematics.put("default",new Schematic(plugin, schematicFile));
                plugin.getLogger().info("Loaded default island schematic.");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not load default schematic!");
                e.printStackTrace();
            }
        }
        // Add the nether default too
        if (!netherFile.exists()) {
            if (plugin.getResource("schematics/nether.schematic") != null) {
                plugin.saveResource("schematics/nether.schematic", false);

                // Add it to schematics
                try {
                    Schematic netherIsland = new Schematic(plugin, netherFile);
                    netherIsland.setVisible(false);
                    schematics.put("nether", netherIsland);
                    plugin.getLogger().info("Loaded default nether schematic.");
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not load default nether schematic!");
                    e.printStackTrace();
                }
            } else {
                plugin.getLogger().severe("Could not find default nether schematic!");
            }
        } else {
            // It exists, so load it
            try {
                Schematic netherIsland = new Schematic(plugin, netherFile);
                netherIsland.setVisible(false);
                schematics.put("nether", netherIsland);
                plugin.getLogger().info("Loaded default nether schematic.");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not load default nether schematic!");
                e.printStackTrace();
            }
        }
        // Set up some basic settings just in case the schematics section is missing
        if (schematics.containsKey("default")) {
            schematics.get("default").setIcon(Material.GRASS);
            schematics.get("default").setOrder(1);
            schematics.get("default").setName("The Original");
            schematics.get("default").setDescription("");
            schematics.get("default").setPartnerName("nether");
            schematics.get("default").setBiome(Settings.defaultBiome);
            schematics.get("default").setIcon(Material.GRASS);
            if (Settings.chestItems.length == 0) {
                schematics.get("default").setUseDefaultChest(false);
            } else {
                schematics.get("default").setUseDefaultChest(true);
            }
        }
        if (schematics.containsKey("nether")) {
            schematics.get("nether").setName("NetherBlock Island");
            schematics.get("nether").setDescription("Nether Island");
            schematics.get("nether").setPartnerName("default");
            schematics.get("nether").setBiome(LegacyBiome.NETHER);
            schematics.get("nether").setIcon(Material.NETHERRACK);
            schematics.get("nether").setVisible(false);
            schematics.get("nether").setPasteEntities(true);
            if (Settings.chestItems.length == 0) {
                schematics.get("nether").setUseDefaultChest(false);
            }
        }

        // Load the schematics from config.yml
        ConfigurationSection schemSection = plugin.getConfig().getConfigurationSection("schematicsection");
        if (plugin.getConfig().contains("schematicsection")) {
            Settings.useSchematicPanel = schemSection.getBoolean("useschematicspanel", false);
            Settings.chooseIslandRandomly = schemSection.getBoolean("chooseislandrandomly", false);
            ConfigurationSection schematicsSection = schemSection.getConfigurationSection("schematics");
            // Section exists, so go through the various sections
            for (String key : schematicsSection.getKeys(false)) {
                try {
                    ISchematic newSchem = null;
                    // Check the file exists
                    //plugin.getLogger().info("DEBUG: schematics." + key + ".filename" );
                    String filename = schemSection.getString("schematics." + key + ".filename","");
                    if (!filename.isEmpty()) {
                        //plugin.getLogger().info("DEBUG: filename = " + filename);
                        // Check if this file exists or if it is in the jar
                        schematicFile = new File(schematicFolder, filename);
                        // See if the file exists
                        if (schematicFile.exists()) {
                            newSchem = new Schematic(plugin, schematicFile);
                        } else if (plugin.getResource("schematics/"+filename) != null) {
                            plugin.saveResource("schematics/"+filename, false);
                            newSchem = new Schematic(plugin, schematicFile);
                        }
                    } else {
                        //plugin.getLogger().info("DEBUG: filename is empty");
                        if (key.equalsIgnoreCase("default")) {
                            //Øplugin.getLogger().info("DEBUG: key is default, so use this one");
                            newSchem = schematics.get("default");
                        } else {
                            plugin.getLogger().severe("Schematic " + key + " does not have a filename. Skipping!");
                        }
                    }
                    if (newSchem != null) {
                        // Set the heading
                        newSchem.setHeading(key);
                        // Order
                        newSchem.setOrder(schemSection.getInt("schematics." + key + ".order", 0));
                        // Load the rest of the settings
                        // Icon
                        try {

                            Material icon;
                            String iconString = schemSection.getString("schematics." + key + ".icon","MAP").toUpperCase();
                            // Support damage values
                            String[] split = iconString.split(":");
                            if (StringUtils.isNumeric(split[0])) {
                                if(Bukkit.getVersion().contains("1.13")){
                                    throw new UnsupportedOperationException("Please do not use material ids!");
                                }
                                icon = Material.getMaterial(Integer.parseInt(split[0]));
                                if (icon == null) {
                                    icon = Material.MAP;
                                    plugin.getLogger().severe("Schematic's icon could not be found. Try using quotes like '17:2'");
                                }
                            } else {
                                icon = Material.valueOf(split[0]);
                            }
                            int damage = 0;
                            if (split.length == 2) {
                                if (StringUtils.isNumeric(split[1])) {
                                    damage = Integer.parseInt(split[1]);
                                }
                            }
                            newSchem.setIcon(icon, damage);
                        } catch (Exception e) {
                            //e.printStackTrace();
                            newSchem.setIcon(Material.MAP);
                        }
                        // Friendly name
                        String name = ChatColor.translateAlternateColorCodes('&', schemSection.getString("schematics." + key + ".name",""));
                        newSchem.setName(name);
                        // Rating - Rating is not used right now
                        int rating = schemSection.getInt("schematics." + key + ".rating",50);
                        if (rating <1) {
                            rating = 1;
                        } else if (rating > 100) {
                            rating = 100;
                        }
                        newSchem.setRating(rating);
                        // Cost
                        double cost = schemSection.getDouble("schematics." + key + ".cost", 0D);
                        if (cost < 0) {
                            cost = 0;
                        }
                        newSchem.setCost(cost);
                        // Description
                        String description = ChatColor.translateAlternateColorCodes('&', schemSection.getString("schematics." + key + ".description",""));
                        description = description.replace("[rating]",String.valueOf(rating));
                        if (Settings.useEconomy) {
                            description = description.replace("[cost]", String.valueOf(cost));
                        }
                        newSchem.setDescription(description);
                        // Permission
                        String perm = schemSection.getString("schematics." + key + ".permission","");
                        newSchem.setPerm(perm);
                        // Use default chest
                        newSchem.setUseDefaultChest(schemSection.getBoolean("schematics." + key + ".useDefaultChest", true));
                        // Biomes - overrides default if it exists
                        String biomeString = schemSection.getString("schematics." + key + ".biome",Settings.defaultBiome.toString());
                        Biome biome = null;
                        try {
                            biome = Biome.valueOf(biomeString);
                            newSchem.setBiome(biome);
                        } catch (Exception e) {
                            plugin.getLogger().severe("Could not parse biome " + biomeString + " using default instead.");
                        }
                        // Use physics - overrides default if it exists
                        newSchem.setUsePhysics(schemSection.getBoolean("schematics." + key + ".usephysics",Settings.usePhysics));
                        // Paste Entities or not
                        newSchem.setPasteEntities(schemSection.getBoolean("schematics." + key + ".pasteentities",false));
                        // Paste air or not.
                        newSchem.setPasteAir(schemSection.getBoolean("schematics." + key + ".pasteair",true));
                        // Visible in GUI or not
                        newSchem.setVisible(schemSection.getBoolean("schematics." + key + ".show",true));
                        // Partner schematic
                        if (biome != null && biome.equals(LegacyBiome.NETHER)) {
                            // Default for nether biomes is the default overworld island
                            newSchem.setPartnerName(schemSection.getString("schematics." + key + ".partnerSchematic","default"));
                        } else {
                            // Default for overworld biomes is nether island
                            newSchem.setPartnerName(schemSection.getString("schematics." + key + ".partnerSchematic","nether"));
                        }
                        // Island companion
                        List<String> companion = schemSection.getStringList("schematics." + key + ".companion");
                        List<EntityType> companionTypes = new ArrayList<EntityType>();
                        if (!companion.isEmpty()) {
                            for (String companionType : companion) {
                                companionType = companionType.toUpperCase();
                                if (companionType.equalsIgnoreCase("NOTHING")) {
                                    companionTypes.add(null);
                                } else {
                                    try {
                                        EntityType type = EntityType.valueOf(companionType);
                                        companionTypes.add(type);
                                    } catch (Exception e) {
                                        plugin.getLogger()
                                                .warning(
                                                        "Island companion is not recognized in schematic '" + name + "'.");
                                    }
                                }
                            }
                            newSchem.setIslandCompanion(companionTypes);
                        }
                        // Companion names
                        List<String> companionNames = schemSection.getStringList("schematics." + key + ".companionnames");
                        if (!companionNames.isEmpty()) {
                            List<String> names = new ArrayList<String>();
                            for (String companionName : companionNames) {
                                names.add(ChatColor.translateAlternateColorCodes('&', companionName));
                            }
                            newSchem.setCompanionNames(names);
                        }
                        // Get chest items
                        final List<String> chestItems = schemSection.getStringList("schematics." + key + ".chestItems");
                        if (!chestItems.isEmpty()) {
                            ItemStack[] tempChest = new ItemStack[chestItems.size()];
                            int i = 0;
                            for (String chestItemString : chestItems) {
                                //plugin.getLogger().info("DEBUG: chest item = " + chestItemString);
                                try {
                                    String[] amountdata = chestItemString.split(":");
                                    if (amountdata[0].equals("POTION")) {
                                        if (amountdata.length == 3) {
                                            Potion chestPotion = new Potion(PotionType.valueOf(amountdata[1]));
                                            tempChest[i++] = chestPotion.toItemStack(Integer.parseInt(amountdata[2]));
                                        } else if (amountdata.length == 4) {
                                            // Extended or splash potions
                                            if (amountdata[2].equals("EXTENDED")) {
                                                Potion chestPotion = new Potion(PotionType.valueOf(amountdata[1])).extend();
                                                tempChest[i++] = chestPotion.toItemStack(Integer.parseInt(amountdata[3]));
                                            } else if (amountdata[2].equals("SPLASH")) {
                                                Potion chestPotion = new Potion(PotionType.valueOf(amountdata[1])).splash();
                                                tempChest[i++] = chestPotion.toItemStack(Integer.parseInt(amountdata[3]));
                                            } else if (amountdata[2].equals("EXTENDEDSPLASH")) {
                                                Potion chestPotion = new Potion(PotionType.valueOf(amountdata[1])).extend().splash();
                                                tempChest[i++] = chestPotion.toItemStack(Integer.parseInt(amountdata[3]));
                                            }
                                        }
                                    } else {
                                        Material mat;
                                        if (StringUtils.isNumeric(amountdata[0])) {
                                            if(Bukkit.getVersion().contains("1.13")){
                                                throw new UnsupportedOperationException("Please do not use material ids!");
                                            }
                                            mat = Material.getMaterial(Integer.parseInt(amountdata[0]));
                                        } else {
                                            mat = Material.getMaterial(amountdata[0].toUpperCase());
                                        }
                                        if (amountdata.length == 2) {
                                            tempChest[i++] = new ItemStack(mat, Integer.parseInt(amountdata[1]));
                                        } else if (amountdata.length == 3) {
                                            tempChest[i++] = new ItemStack(mat, Integer.parseInt(amountdata[2]), Short.parseShort(amountdata[1]));
                                        }
                                    }
                                } catch (java.lang.IllegalArgumentException ex) {
                                    plugin.getLogger().severe("Problem loading chest item for schematic '" + name + "' so skipping it: " + chestItemString);
                                    plugin.getLogger().severe("Error is : " + ex.getMessage());
                                    plugin.getLogger().info("Potential potion types are: ");
                                    for (PotionType c : PotionType.values())
                                        plugin.getLogger().info(c.name());
                                } catch (Exception e) {
                                    plugin.getLogger().severe("Problem loading chest item for schematic '" + name + "' so skipping it: " + chestItemString);
                                    plugin.getLogger().info("Potential material types are: ");
                                    for (Material c : Material.values())
                                        plugin.getLogger().info(c.name());
                                    // e.printStackTrace();
                                }
                            }

                            // Store it
                            newSchem.setDefaultChestItems(tempChest);
                        }
                        // Player spawn block
                        String spawnBlock = schemSection.getString("schematics." + key + ".spawnblock");
                        if (spawnBlock != null) {
                            // Check to see if this block is a valid material
                            try {
                                Material playerSpawnBlock;
                                if (StringUtils.isNumeric(spawnBlock)) {
                                    playerSpawnBlock = Material.getMaterial(Integer.parseInt(spawnBlock));
                                } else {
                                    playerSpawnBlock = Material.valueOf(spawnBlock.toUpperCase());
                                }
                                if (newSchem.setPlayerSpawnBlock(playerSpawnBlock)) {
                                    plugin.getLogger().info("Player will spawn at the " + playerSpawnBlock.toString());
                                } else {
                                    plugin.getLogger().severe("Problem with schematic '" + name + "'. Spawn block '" + spawnBlock + "' not found in schematic or there is more than one. Skipping...");
                                }
                            } catch (Exception e) {
                                plugin.getLogger().severe("Problem with schematic '" + name + "'. Spawn block '" + spawnBlock + "' is unknown. Skipping...");
                            }
                        } else {
                            // plugin.getLogger().info("No spawn block found");
                        }
                        // Level handicap
                        newSchem.setLevelHandicap(schemSection.getInt("schematics." + key + ".levelHandicap", 0));

                        // Store it
                        schematics.put(key, newSchem);
                        if (perm.isEmpty()) {
                            perm = "all players";
                        } else {
                            perm = "player with " + perm + " permission";
                        }
                        plugin.getLogger().info("Loading schematic " + ChatColor.stripColor(name) + " (" + filename + ") for " + perm + ", order " + newSchem.getOrder());
                    } else {
                        plugin.getLogger().warning("Could not find " + filename + " in the schematics folder! Skipping...");
                    }
                } catch (IOException e) {
                    plugin.getLogger().info("Error loading schematic in section " + key + ". Skipping...");
                }
            }
            if (schematics.isEmpty()) {
                tip();
            }
        }
    }

    private void tip() {
        // There is no section in config.yml. Save the default schematic anyway
        plugin.getLogger().warning("***************************************************************");
        plugin.getLogger().warning("* 'schematics' section in config.yml has been deprecated.     *");
        plugin.getLogger().warning("* See 'schematicsection' in config.new.yml for replacement.   *");
        plugin.getLogger().warning("***************************************************************");
    }

    public List<ISchematic> getSchematics(Player player, boolean ignoreNoPermission) {
        List<ISchematic> result = new ArrayList<>();
        // Find out what schematics this player can choose from
        for (ISchematic schematic : schematics.values()) {
            if ((!ignoreNoPermission && schematic.getPerm().isEmpty()) || VaultHelper.checkPerm(player, schematic.getPerm())) {
                // Only add if it's visible
                if (schematic.isVisible()) {
                    // Check if it's a nether island, but the nether is not enables
                    if (schematic.getBiome().equals(LegacyBiome.NETHER)) {
                        if (Settings.createNether && Settings.newNether && ASkyBlock.getNetherWorld() != null) {
                            result.add(schematic);
                        }
                    } else {
                        result.add(schematic);
                    }
                }

            }
        }
        // Sort according to order
        result.sort((o1, o2) -> ((o2.getOrder() < o1.getOrder()) ? 1 : -1));
        return result;
    }
}
