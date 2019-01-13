package com.ome_r.superiorskyblock.handlers;

import com.ome_r.superiorskyblock.SuperiorSkyblock;
import com.ome_r.superiorskyblock.upgrades.CommandUpgrade;
import com.ome_r.superiorskyblock.upgrades.MultiplierUpgrade;
import com.ome_r.superiorskyblock.upgrades.Upgrade;
import com.ome_r.superiorskyblock.utils.ListUtils;
import com.wasteofplastic.askyblock.ASkyBlock;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsHandler {

    private ASkyBlock plugin;

    public final boolean worldBordersEnabled;

    //Stacked blocks section
    public final boolean stackedBlocksEnabled;
    public final String stackedBlockName;
    public final List<String> stackedBlockMaterials, stackedBlocksDisabledWorlds;

    //Upgrades section
    public final List<Upgrade> upgrades;
    public final int upgradesSize;
    public final String upgradesTitle;
    public final Map<ItemStack, Integer[]> upgradesFillItems;

    // Plugin's upgrades section
    public final boolean hoppersLimitEnabled;
    public final boolean cropGrowthEnabled;
    public final boolean spawnerRatesEnabled;
    public final boolean mobDropsEnabled;

    //Top gui section
    public final long topCheckInterval;
    public final int topSize;
    public final String topTitle;
    public final Map<ItemStack, Integer[]> topFillItems;
    public final ItemStack islandItem, noIslandItem;
    public final List<Integer> islandSlots;

    //Counted blocks section
    public final boolean countedBlocksEnabled;
    public final Map<ItemStack, Integer[]> countedBlocksFillItems;
    public final String countedBlockItemName;
    public final List<String> countedBlockItemLore;
    public final Map<String, Integer> countedBlockSlots;
    public final int countedBlockSize;
    public final String countedBlockTitle;

    //Custom names section
    public final Map<String, String> customNames;


    public SettingsHandler(ASkyBlock plugin){
        this.plugin = plugin;

        File file = new File(plugin.getDataFolder(), "superior.yml");

        if(!file.exists())
            plugin.saveResource("superior.yml", false);

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        worldBordersEnabled = cfg.getBoolean("world-borders", true);

        stackedBlocksEnabled = cfg.getBoolean("stacked-blocks.enabled", true);
        stackedBlockName = ChatColor.translateAlternateColorCodes('&', cfg.getString("stacked-blocks.custom-name", "&bx{0} &7{1}"));
        stackedBlocksDisabledWorlds = cfg.getStringList("stacked-blocks.disabled-worlds");
        stackedBlockMaterials = cfg.getStringList("stacked-blocks.materials");

        upgrades = new ArrayList<>();
        upgradesSize = cfg.getInt("upgrades.size", 4);
        upgradesTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("upgrades.title", "&lIsland Upgrades"));
        upgradesFillItems = new HashMap<>();
        if(cfg.contains("upgrades.fill-items")) {
            for (String item : cfg.getConfigurationSection("upgrades.fill-items").getKeys(false)) {
                List<Integer> slots = new ArrayList<>();
                Arrays.stream(cfg.getString("upgrades.fill-items." + item + ".slots").split(",")).forEach(slot -> slots.add(Integer.parseInt(slot)));
                upgradesFillItems.put(getItemStack(cfg, "upgrades.fill-items." + item), slots.toArray(new Integer[0]));
            }
        }

        if((hoppersLimitEnabled = cfg.contains("upgrades.hoppers-limit")))
            initializeUpgrade(cfg.getConfigurationSection("upgrades.hoppers-limit"), "hoppers-limit");
        if((cropGrowthEnabled = cfg.contains("upgrades.crop-growth")))
            initializeUpgrade(cfg.getConfigurationSection("upgrades.crop-growth"), "crop-growth");
        if((spawnerRatesEnabled = cfg.contains("upgrades.spawner-rates")))
            initializeUpgrade(cfg.getConfigurationSection("upgrades.spawner-rates"), "spawner-rates");
        if((mobDropsEnabled = cfg.contains("upgrades.mob-drops")))
            initializeUpgrade(cfg.getConfigurationSection("upgrades.mob-drops"), "mob-drops");

        if(cfg.contains("upgrades.custom-upgrades")){
            for(String customUpgrade : cfg.getConfigurationSection("upgrades.custom-upgrades").getKeys(false)){
                initializeUpgrade(cfg.getConfigurationSection("upgrades.custom-upgrades." + customUpgrade), customUpgrade);
            }
        }

        topCheckInterval = cfg.getLong("top-gui.check-interval", 6000);
        topSize = cfg.getInt("top-gui.size", 3);
        topTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("top-gui.title", "&lTop Islands"));
        topFillItems = new HashMap<>();
        if(cfg.contains("top-gui.fill-items")) {
            for (String item : cfg.getConfigurationSection("top-gui.fill-items").getKeys(false)) {
                List<Integer> slots = new ArrayList<>();
                Arrays.stream(cfg.getString("top-gui.fill-items." + item + ".slots").split(",")).forEach(slot -> slots.add(Integer.parseInt(slot)));
                topFillItems.put(getItemStack(cfg, "top-gui.fill-items." + item), slots.toArray(new Integer[0]));
            }
        }
        islandItem = getItemStack(cfg, "top-gui.island-item");
        noIslandItem = getItemStack(cfg, "top-gui.no-island-item");
        islandSlots = new ArrayList<>();
        for(String slot : cfg.getString("top-gui.slots", "4,12,14,19,20,21,22,23,24,25").split(","))
            islandSlots.add(Integer.parseInt(slot));

        countedBlocksEnabled = cfg.getBoolean("top-gui.counted-blocks.enabled");
        countedBlocksFillItems = new HashMap<>();
        if(cfg.contains("top-gui.counted-blocks.fill-items")) {
            for (String item : cfg.getConfigurationSection("top-gui.counted-blocks.fill-items").getKeys(false)) {
                List<Integer> slots = new ArrayList<>();
                Arrays.stream(cfg.getString("top-gui.counted-blocks.fill-items." + item + ".slots").split(",")).forEach(slot -> slots.add(Integer.parseInt(slot)));
                countedBlocksFillItems.put(getItemStack(cfg, "top-gui.counted-blocks.fill-items." + item), slots.toArray(new Integer[0]));
            }
        }
        countedBlockItemName = ChatColor.translateAlternateColorCodes('&', cfg.getString("top-gui.counted-blocks.block-item.name"));
        countedBlockItemLore = ListUtils.translateAlternateColorCodes('&', cfg.getStringList("top-gui.counted-blocks.block-item.lore"));
        countedBlockSlots = new HashMap<>();
        for(String materialName : cfg.getStringList("top-gui.counted-blocks.materials")){
            String[] sections = materialName.split(":");
            if(sections.length == 2){
                countedBlockSlots.put(sections[0], Integer.valueOf(sections[1]));
            }else{
                countedBlockSlots.put(sections[0] + ":" + sections[1], Integer.valueOf(sections[2]));
            }
        }
        countedBlockSize = cfg.getInt("top-gui.counted-blocks.size");
        countedBlockTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("top-gui.counted-blocks.title"));

        customNames = new HashMap<>();
        if(cfg.contains("custom-names")){
            for(String name : cfg.getConfigurationSection("custom-names").getKeys(false)){
                customNames.put(name, ChatColor.translateAlternateColorCodes('&', cfg.getString("custom-names." + name)));
            }
        }
    }

    private void initializeUpgrade(ConfigurationSection section, String name){
        int level = 1;
        //Checks if it's not a custom upgrade
        if(!section.getCurrentPath().contains("custom-upgrades")){
            MultiplierUpgrade upgrade = new MultiplierUpgrade(name);
            while(section.contains("levels." + String.valueOf(level))){
                upgrade.setMultiplier(level, section.getDouble("levels." + level + ".multiplier"));
                upgrade.setPrice(level, section.getDouble("levels." + level + ".price"));
                level++;
            }
            upgrade.setMultiplier(0, section.getDouble("levels.default.multiplier"));

            upgrade.setItemSlot(section.getInt("gui-item.slot"));
            upgrade.setMaxLevelItem(getItemStack(section, "gui-item.max-level"));
            upgrade.setNextLevelItem(getItemStack(section, "gui-item.next-level"));

            upgrades.add(upgrade);
        }
        //This is a command upgrade
        else{
            CommandUpgrade upgrade = new CommandUpgrade(name);
            while(section.contains("levels." + String.valueOf(level))){
                upgrade.setPlaceholder(level, section.getString("levels." + level + ".placeholder"));
                upgrade.setPrice(level, section.getDouble("levels." + level + ".price"));
                upgrade.setCommands(level, section.getStringList("levels." + level + ".commands"));
                level++;
            }

            upgrade.setItemSlot(section.getInt("gui-item.slot"));
            upgrade.setMaxLevelItem(getItemStack(section, "gui-item.max-level"));
            upgrade.setNextLevelItem(getItemStack(section, "gui-item.next-level"));

            upgrades.add(upgrade);
        }
    }

    private ItemStack getItemStack(ConfigurationSection section, String path){
        Material type;

        if(!section.contains(path + ".type")){
            throw new NullPointerException("Couldn't find a type for path: " + path + ". Please re-generate your config.");
        }

        try{
            type = Material.valueOf(section.getString(path + ".type"));
        }catch(IllegalArgumentException ex){
            throw new IllegalArgumentException("Couldn't find a valid material type for " + path + ".type");
        }

        short damage = 0;
        if(section.contains(path + ".data"))
            damage = (short) section.getInt(path + ".data");

        ItemStack itemStack = new ItemStack(type, 1, damage);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(section.contains(path + ".name"))
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString(path + ".name")));

        if(section.contains(path + ".lore")) {
            List<String> lore = new ArrayList<>();
            section.getStringList(path + ".lore").forEach(line -> lore.add(ChatColor.translateAlternateColorCodes('&', line)));
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void reload(){
        try{
            ASkyBlock plugin = ASkyBlock.getPlugin();
            Field field = SuperiorSkyblock.class.getDeclaredField("settingsHandler");
            field.setAccessible(true);
            field.set(plugin.getSuperiorSkyblock(), new SettingsHandler(plugin));
        }catch(Exception ex){

        }
    }

}
