package com.ome_r.superiorskyblock.hooks;

import com.ome_r.superiorskyblock.upgrades.CommandUpgrade;
import com.ome_r.superiorskyblock.upgrades.MultiplierUpgrade;
import com.ome_r.superiorskyblock.upgrades.Upgrade;
import com.ome_r.superiorskyblock.utils.StringUtils;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderHook extends EZPlaceholderHook {

    private ASkyBlock plugin;

    private PlaceholderHook(ASkyBlock plugin){
        super(plugin, "superiorskyblock");
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if(player == null)
            return "";

        Matcher matcher;

        if((matcher = Pattern.compile("upgrades_(.+)_(.+)").matcher(params)).matches()){
            Island island = plugin.getGrid().getIsland(player.getUniqueId());

            if(island == null)
                return "";

            String subPlaceholder = matcher.group(1);
            String upgradeName = matcher.group(2);
            Upgrade upgrade = plugin.getSuperiorSkyblock().getUpgrades().getUpgrade(upgradeName);
            int upgradeLevel = island.getUpgradeLevel(upgradeName);

            if(upgrade == null)
                return "";

            if(subPlaceholder.equalsIgnoreCase("level")){
                return String.valueOf(upgradeLevel);
            }

            else if(subPlaceholder.equalsIgnoreCase("multiplier")){
                if(upgrade instanceof MultiplierUpgrade)
                    return String.valueOf(((MultiplierUpgrade) upgrade).getMultiplier(upgradeLevel));
                else return "";
            }

            else if(subPlaceholder.equalsIgnoreCase("placeholder")){
                if(upgrade instanceof CommandUpgrade)
                    return String.valueOf(((CommandUpgrade) upgrade).getPlaceholder(upgradeLevel));
                else return "";
            }
        }

        else if((matcher = Pattern.compile("island_block_(.*)").matcher(params)).matches()){
            Island island = plugin.getGrid().getIsland(player.getUniqueId());

            if(island == null)
                return "";

            return String.valueOf(island.getBlockCount(matcher.group(1).toUpperCase()));
        }

        else if((matcher = Pattern.compile("island_top_(.*)").matcher(params)).matches()){
            List<Island> topIslands = plugin.getSuperiorSkyblock().getTopHandler().getTopIslands();
            String islandOwner = "None";
            int topIndex = -1;

            try{
                topIndex = Integer.valueOf(matcher.group(1)) - 1;
            }catch(IllegalArgumentException ignored){}

            if(topIndex != -1){
                try{
                    islandOwner = Bukkit.getOfflinePlayer(topIslands.get(topIndex).getOwner()).getName();
                }catch(Exception ignoerd){}
            }

            return islandOwner;
        }

        else if((matcher = Pattern.compile("island_(.+)").matcher(params)).matches()){
            Island island = plugin.getGrid().getIsland(player.getUniqueId());

            if(island == null)
                return "";

            String subPlaceholder = matcher.group(1);

            switch (subPlaceholder.toLowerCase()){
                case "size":
                    return String.valueOf(island.getProtectionSize());
                case "center":
                    return StringUtils.fancyLocation(island.getCenter());
                case "owner":
                    return Bukkit.getOfflinePlayer(island.getOwner()).getName();
                case "create_date":
                    return String.valueOf(island.getCreatedDate());
                case "updated_date":
                    return String.valueOf(island.getUpdatedDate());
                case "password":
                    return String.valueOf(island.getPassword());
                case "distance":
                    return String.valueOf(island.getIslandDistance());
                case "is_locked":
                    return String.valueOf(island.isLocked());
                case "is_spawn":
                    return String.valueOf(island.isSpawn());
                case "purge_protection":
                    return String.valueOf(island.isPurgeProtected());
                case "spawnpoint":
                    return island.getSpawnPoint() == null ? "null" : StringUtils.fancyLocation(island.getSpawnPoint());
                case "biome":
                    return island.getBiome().name();
                case "hoppers_amount":
                    return String.valueOf(island.getHoppersAmount());
                case "worth":
                    return StringUtils.getFormattedNumber(island.getWorth());
                case "location":
                    return StringUtils.fancyLocation(plugin.getGrid().getSafeHomeLocation(player.getUniqueId(), 1));
            }

        }

        return null;
    }

    public static void register(){
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new PlaceholderHook(ASkyBlock.getPlugin()).hook();
        }
    }

    public static String format(Player player, String original){
        return PlaceholderAPI.setPlaceholders(player, original);
    }

    public static ItemStack format(Player player, ItemStack original){
        ItemStack itemStack = original.clone();
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta.hasDisplayName()){
            itemMeta.setDisplayName(format(player, itemMeta.getDisplayName()));
        }

        if(itemMeta.hasLore()){
            List<String> lore = new ArrayList<>();

            for(String line : itemMeta.getLore()){
                lore.add(PlaceholderHook.format(player, line));
            }

            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
