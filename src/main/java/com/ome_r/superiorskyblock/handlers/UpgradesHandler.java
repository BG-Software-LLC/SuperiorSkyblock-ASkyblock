package com.ome_r.superiorskyblock.handlers;

import com.ome_r.superiorskyblock.hooks.PlaceholderHook;
import com.ome_r.superiorskyblock.upgrades.CommandUpgrade;
import com.ome_r.superiorskyblock.upgrades.MultiplierUpgrade;
import com.ome_r.superiorskyblock.upgrades.Upgrade;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpgradesHandler {

    private ASkyBlock plugin;
    private SettingsHandler settings;

    public UpgradesHandler(ASkyBlock plugin){
        this.plugin = plugin;
        this.settings = plugin.getSuperiorSkyblock().getSettings();
    }

    public Inventory getUpgradeGUI(Player player){
        boolean replacePlaceholder = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        Inventory inventory = Bukkit.createInventory(null, settings.upgradesSize * 9, settings.upgradesTitle);
        Island playerIsland = plugin.getGrid().getIsland(player.getUniqueId());

        for(Map.Entry<ItemStack, Integer[]> entry : settings.upgradesFillItems.entrySet()){
            ItemStack itemStack = entry.getKey();

            if(replacePlaceholder)
                itemStack = PlaceholderHook.format(player, itemStack);

            for(int slot : entry.getValue())
                inventory.setItem(slot, itemStack);
        }

        for(Upgrade upgrade : settings.upgrades){
            int level = playerIsland.getUpgradeLevel(upgrade.getName()) + 1;
            ItemStack itemStack = level > upgrade.getMaximumLevel() ? upgrade.getMaxLevelItem() : upgrade.getNextLevelItem();
            String placeHolder = upgrade instanceof MultiplierUpgrade ?
                    String.valueOf(((MultiplierUpgrade) upgrade).getMultiplier(level)) : ((CommandUpgrade) upgrade).getPlaceholder(level);
            inventory.setItem(upgrade.getItemSlot(), getFormattedItem(player, itemStack, placeHolder, upgrade.getPrice(level), level));
        }


        return inventory;
    }

    public Upgrade getUpgrade(String upgradeName){
        for(Upgrade upgrade : plugin.getSuperiorSkyblock().getSettings().upgrades){
            if(upgrade.getName().equalsIgnoreCase(upgradeName))
                return upgrade;
        }

        return null;
    }

    private ItemStack getFormattedItem(Player player, ItemStack itemStack, int multiplier, double price, int level){
        return getFormattedItem(player, itemStack, String.valueOf(multiplier), price, level);
    }

    private ItemStack getFormattedItem(Player player, ItemStack itemStack, String multiplierHolder, double price, int level){
        ItemStack formattedItem = itemStack.clone();
        ItemMeta itemMeta = formattedItem.getItemMeta();

        try {
            if (itemMeta.hasDisplayName()) {
                itemMeta.setDisplayName(replace(itemMeta.getDisplayName(), level, multiplierHolder, getFormattedNumber(price)));
                //itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{0}", level + ""));
            }

            if (itemMeta.hasLore()) {
                List<String> lore = new ArrayList<>();

                itemMeta.getLore().forEach(line -> {
                    //lore.add(line.replace("{1}", multipliers.get(level - 1) + "").replace("{2}", getFormattedNumber(prices.get(level - 1))));
                    lore.add(replace(line, level, multiplierHolder, getFormattedNumber(price)));
                });
                itemMeta.setLore(lore);
            }
        }catch(Exception ex){
            //Do nothing
        }

        formattedItem.setItemMeta(itemMeta);

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            formattedItem = PlaceholderHook.format(player, formattedItem);

        return formattedItem;
    }

    private String replace(String string, Object... args){
        String formattedString = string;

        for(int i = 0; i < args.length; i++) {
            formattedString = formattedString.replace("{" + i + "}", args[i].toString());
        }

        return formattedString;
    }

    private String getFormattedNumber(double number){
        DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
        String formattedNumber = decimalFormat.format(number);
        if(formattedNumber.endsWith(".00"))
            formattedNumber = formattedNumber.substring(0, formattedNumber.length() - 3);
        return formattedNumber;
    }

}
