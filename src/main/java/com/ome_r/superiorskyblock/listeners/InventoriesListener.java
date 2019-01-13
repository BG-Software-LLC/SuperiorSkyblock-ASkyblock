package com.ome_r.superiorskyblock.listeners;

import com.ome_r.superiorskyblock.Locale;
import com.ome_r.superiorskyblock.upgrades.CommandUpgrade;
import com.ome_r.superiorskyblock.upgrades.Upgrade;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.util.VaultHelper;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class InventoriesListener implements Listener {

    private ASkyBlock plugin;

    public InventoriesListener(ASkyBlock plugin){
        this.plugin = plugin;
    }

    private Set<UUID> playersInsideCountedBlocksGUI = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTopGUIClick(InventoryClickEvent e){
        if(e.getInventory() == null || !e.getInventory().getTitle().equals(plugin.getSuperiorSkyblock().getSettings().topTitle))
            return;

        e.setCancelled(true);

        if(plugin.getSuperiorSkyblock().getSettings().islandSlots.contains(e.getRawSlot())){
            List<Island> topIslands = plugin.getSuperiorSkyblock().getTopHandler().getTopIslands();
            int islandIndex = plugin.getSuperiorSkyblock().getSettings().islandSlots.indexOf(e.getRawSlot());
            if(islandIndex < topIslands.size()) {
                Island island = topIslands.get(islandIndex);

                if(e.getClick().name().contains("RIGHT")) {
                    Bukkit.dispatchCommand(e.getWhoClicked(), "is warp " + Bukkit.getOfflinePlayer(island.getOwner()).getName());
                    e.getWhoClicked().closeInventory();
                }else if(e.getClick().name().contains("LEFT") && island.getOwner() != null) {
                    e.getWhoClicked().openInventory(plugin.getSuperiorSkyblock().getTopHandler().getCountedBlocksGUI(island));
                    playersInsideCountedBlocksGUI.add(e.getWhoClicked().getUniqueId());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCountedBlockGUIClick(InventoryClickEvent e){
        if(e.getInventory() == null || !playersInsideCountedBlocksGUI.contains(e.getWhoClicked().getUniqueId()))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(playersInsideCountedBlocksGUI.contains(e.getPlayer().getUniqueId())){
            playersInsideCountedBlocksGUI.remove(e.getPlayer().getUniqueId());
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    e.getPlayer().openInventory(plugin.getSuperiorSkyblock().getTopHandler().getTopGUI((Player) e.getPlayer())), 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onUpgadeGUIClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player) || e.getInventory() == null || !e.getInventory().getTitle().equals(plugin.getSuperiorSkyblock().getSettings().upgradesTitle))
            return;

        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();
        Island island = plugin.getGrid().getIsland(player.getUniqueId());

        if(island == null)
            return;

        double price = -1;

        try {
            for(Upgrade upgrade : plugin.getSuperiorSkyblock().getSettings().upgrades){
                if(e.getRawSlot() == upgrade.getItemSlot()){
                    if(upgrade.getMaximumLevel() <= island.getUpgradeLevel(upgrade.getName()))
                        return;
                    price = upgrade.getPrice(island.getUpgradeLevel(upgrade.getName()) + 1);
                    break;
                }
            }

            if(price < 0)
                return;
        }catch(Exception ex){
            return;
        }

        player.closeInventory();

        EconomyResponse economyResponse = VaultHelper.econ.withdrawPlayer(player, price);

        if (!economyResponse.transactionSuccess()) {
            Locale.UPGRADE_FAILURE.send(player, e.getCurrentItem().getItemMeta().getDisplayName());
            return;
        }

        for(Upgrade upgrade : plugin.getSuperiorSkyblock().getSettings().upgrades){
            if(e.getRawSlot() == upgrade.getItemSlot()){
                int level = island.getUpgradeLevel(upgrade.getName());
                island.setUpgradeLevel(upgrade.getName(), level + 1);
                if(upgrade instanceof CommandUpgrade){
                    ((CommandUpgrade) upgrade).getCommands(level + 1).forEach(command ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName())));
                }
                break;
            }
        }

        Locale.UPGRADE_PURCHASE.send(player, e.getCurrentItem().getItemMeta().getDisplayName());
    }


}
