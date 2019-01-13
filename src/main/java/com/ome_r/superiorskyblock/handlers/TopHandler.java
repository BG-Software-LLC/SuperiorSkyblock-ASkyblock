package com.ome_r.superiorskyblock.handlers;

import com.ome_r.superiorskyblock.hooks.PlaceholderHook;
import com.ome_r.superiorskyblock.legacy.LegacyMaterial;
import com.ome_r.superiorskyblock.utils.HeadUtils;
import com.ome_r.superiorskyblock.utils.ListUtils;
import com.ome_r.superiorskyblock.utils.StringUtils;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TopHandler {

    private ASkyBlock plugin;
    private SettingsHandler settings;

    private List<UUID> topIslands;

    public TopHandler(ASkyBlock plugin){
        this.plugin = plugin;
        this.settings = plugin.getSuperiorSkyblock().getSettings();
        this.topIslands = new ArrayList<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> updateTopList(), 0L, 1200L);
    }

    public List<Island> getTopIslands(){
        List<Island> islands = new ArrayList<>();

        for(UUID uuid : topIslands){
            Island island = plugin.getGrid().getIsland(uuid);
            if(island != null && !islands.contains(island))
                islands.add(island);
        }

        return islands;
    }

    public Inventory getTopGUI(Player player){
        boolean replacePlaceholder = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        Inventory inventory = Bukkit.createInventory(null, settings.topSize * 9, settings.topTitle);

        for(Map.Entry<ItemStack, Integer[]> entry : settings.topFillItems.entrySet()){
            ItemStack itemStack = entry.getKey();

            if(replacePlaceholder)
                itemStack = PlaceholderHook.format(player, itemStack);

            for(int slot : entry.getValue())
                inventory.setItem(slot, itemStack);
        }

        List<Island> topIslands = getTopIslands();

        for(int i = 0; i < settings.islandSlots.size(); i++){
            UUID playerUUID = i < topIslands.size() ? topIslands.get(i).getOwner() : UUID.fromString("606e2ff0-ed77-4842-9d6c-e1d3321c7838");
            ItemStack itemStack = getPlayerHead(playerUUID, i + 1);

            if(replacePlaceholder)
                itemStack = PlaceholderHook.format(player, itemStack);

            inventory.setItem(settings.islandSlots.get(i), itemStack);
        }

        return inventory;
    }

    public Inventory getCountedBlocksGUI(Island island){
        String playerName = Bukkit.getOfflinePlayer(island.getOwner()).getName();

        Inventory inventory = Bukkit.createInventory(null, settings.countedBlockSize * 9,
                StringUtils.formatString(settings.countedBlockTitle, playerName, StringUtils.getFormattedNumber(island.getWorth())));

        for(Map.Entry<ItemStack, Integer[]> entry : settings.countedBlocksFillItems.entrySet()){
            Arrays.stream(entry.getValue()).forEach(slot -> inventory.setItem(slot, entry.getKey()));
        }

        for(String string : settings.countedBlockSlots.keySet()){
            String[] sections = string.split(":");
            ItemStack itemStack = new ItemStack(Material.valueOf(sections[0]));
            int slot = settings.countedBlockSlots.get(string);

            String typeName = StringUtils.getFormattedType(sections[0]);
            int amount = island.getBlockCount(itemStack);

            if(sections.length == 2) {
                if(itemStack.getType() == LegacyMaterial.SPAWNER) {
                    EntityType entityType = EntityType.valueOf(sections[1]);
                    amount = island.getBlockCount(entityType);
                    itemStack = HeadUtils.getEntityHead(entityType);
                    typeName = StringUtils.getFormattedType(sections[1]) + " Spawner";
                }
                else {
                    itemStack.setDurability(Short.valueOf(sections[1]));
                    amount = island.getBlockCount(itemStack);
                }
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(StringUtils.formatString(settings.countedBlockItemName, typeName, amount));
            itemMeta.setLore(ListUtils.formatList(settings.countedBlockItemLore, typeName, amount));
            itemStack.setItemMeta(itemMeta);

            if(amount == 0)
                amount = 1;
            else if(amount > 64)
                amount = 64;

            itemStack.setAmount(amount);

            inventory.setItem(slot, itemStack);
        }

        return inventory;
    }

    public void updateTopList(){
        List<UUID> topIslands = new ArrayList<>();
        int size = settings.islandSlots.size();

        List<Island> islands = plugin.getGrid().getOwnedIslands().values().stream()
                .filter(island -> island.getOwner() != null && Bukkit.getOfflinePlayer(island.getOwner()).getName() != null)
                .sorted(((Comparator<Island>) (o1, o2) -> {
                    long firstIslandLevel = plugin.getPlayers().getIslandLevel(o1.getOwner());
                    long secondIslandLevel = plugin.getPlayers().getIslandLevel(o2.getOwner());
                    if (firstIslandLevel > secondIslandLevel)
                        return 1;
                    else if (firstIslandLevel < secondIslandLevel)
                        return -1;

                    return Bukkit.getOfflinePlayer(o1.getOwner()).getName().compareTo(Bukkit.getOfflinePlayer(o2.getOwner()).getName());
                }).reversed()).collect(Collectors.toList());

        for(int i = 0; i < size && i < islands.size(); i++){
            topIslands.add(islands.get(i).getOwner());
        }

        this.topIslands = new ArrayList<>(topIslands);
    }

    private ItemStack getPlayerHead(UUID playerUUID, int level){
        ItemStack playerHead = settings.islandItem.clone();
        String name = playerUUID == null || Bukkit.getOfflinePlayer(playerUUID) == null ? null : Bukkit.getOfflinePlayer(playerUUID).getName();

        ItemMeta itemMeta = playerHead.getItemMeta();

        if(name == null || name.isEmpty()) {
            ItemStack noIslandItem = settings.noIslandItem.clone();
            if(noIslandItem.getType() == LegacyMaterial.getPlayerHead(1).getType())
                playerHead = HeadUtils.getPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19");
            itemMeta = playerHead.getItemMeta();

            if(noIslandItem.hasItemMeta()) {
                if(noIslandItem.getItemMeta().hasDisplayName())
                    itemMeta.setDisplayName(noIslandItem.getItemMeta().getDisplayName());
                if(noIslandItem.getItemMeta().hasLore())
                    itemMeta.setLore(noIslandItem.getItemMeta().getLore());
            }
        }

        else{
            Island island = plugin.getGrid().getIsland(playerUUID);
            if(itemMeta.hasDisplayName()){
                itemMeta.setDisplayName(StringUtils.formatString(itemMeta.getDisplayName(),
                        plugin.getGrid().getIslandName(playerUUID), level, island.getIslandLevel(), StringUtils.getFormattedNumber(island.getWorth())));
            }
            if(itemMeta.hasLore()){
                List<String> lore = new ArrayList<>();

                for(String line : itemMeta.getLore()){
                    if(line.contains("{4}")){
                        String memberFormat = line.split("\\{4}:")[1];
                        if(island.getMembers().size() == 1){
                            lore.add(memberFormat.replace("{}", "None"));
                        }
                        else {
                            for (UUID memberUUID : island.getMembers()) {
                                if(!memberUUID.equals(playerUUID))
                                    lore.add(memberFormat.replace("{}", Bukkit.getOfflinePlayer(memberUUID).getName()));
                            }
                        }
                    }else{
                        lore.add(StringUtils.formatString(line,
                                plugin.getGrid().getIslandName(playerUUID), level, island.getIslandLevel(), StringUtils.getFormattedNumber(island.getWorth())));
                    }
                }

                itemMeta.setLore(lore);
            }

            if(itemMeta instanceof SkullMeta){
                ((SkullMeta) itemMeta).setOwner(name);
            }
        }

        playerHead.setItemMeta(itemMeta);

        return playerHead;
    }

}
