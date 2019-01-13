package com.ome_r.superiorskyblock.listeners;

import com.ome_r.superiorskyblock.Locale;
import com.ome_r.superiorskyblock.hooks.WildStackerHook;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UpgradesListener implements Listener {

    private ASkyBlock plugin;
    private Map<String, Byte> maxGrowthData = new HashMap<>();

    public UpgradesListener(ASkyBlock instance) {
        this.plugin = instance;
        maxGrowthData.put("BEETROOT_BLOCK", (byte) 3);
        maxGrowthData.put("NETHER_WARTS", (byte) 3);
    }

    /*
     * Crop Growth
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGrow(BlockGrowEvent e) {
        if(!plugin.getSuperiorSkyblock().getSettings().cropGrowthEnabled)
            return;

        Island island = plugin.getGrid().getIslandAt(e.getBlock().getLocation());

        if(island == null)
            return;

        if(island.hasCropGrowthMultiplier()){
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                byte newData = (byte) (e.getBlock().getData() + island.getCropGrowthMultiplier());
                if(newData > maxGrowthData.getOrDefault(e.getBlock().getType().name(), (byte) 7))
                    newData = maxGrowthData.getOrDefault(e.getBlock().getType().name(), (byte) 7);
                e.getBlock().setData(newData);
                e.getBlock().getState().update();
            }, 2L);
        }
    }

    /*
     * Spawner Rates
     */

    private Set<UUID> alreadySet = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawn(SpawnerSpawnEvent e) {
        if(!plugin.getSuperiorSkyblock().getSettings().spawnerRatesEnabled)
            return;

        Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

        if(island == null)
            return;

        if(island.hasSpawnRatesMultiplier()){
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(!alreadySet.contains(island.getOwner())) {
                    alreadySet.add(island.getOwner());
                    e.getSpawner().setDelay((int) (
                            plugin.getSuperiorSkyblock().getNMSAbstraction().getSpawnerDelay(e.getSpawner()) / island.getSpawnRatesMultiplier()));
                    e.getSpawner().update();
                    Bukkit.getScheduler().runTaskLater(plugin, () -> alreadySet.remove(island.getOwner()), 10L);
                }
            }, 1L);
        }
    }

    /*
     * Mob Drops
     */

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        if(!plugin.getSuperiorSkyblock().getSettings().mobDropsEnabled || e.getEntity() instanceof Player)
            return;

        Island island = plugin.getGrid().getIslandAt(e.getEntity().getLocation());

        if(island == null)
            return;

        if(island.hasMobDropsMultiplier()){
            List<ItemStack> naturalDrops = new ArrayList<>(e.getDrops());
            e.getDrops().clear();

            //I know it's messy af, but idc as the entire plugin will be recoded.

            //Getting drops async
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                List<ItemStack> drops = plugin.getSuperiorSkyblock().getDropsHook().getDrops(e.getEntityType(), naturalDrops);

                if(Bukkit.getPluginManager().isPluginEnabled("WildStacker")){
                    List<ItemStack> wildDrops = WildStackerHook.getStackedEntityDrops(e.getEntity());
                    if(!wildDrops.isEmpty()) {
                        drops.clear();
                        drops.addAll(wildDrops);
                        WildStackerHook.setDrops(e.getEntity(), new ArrayList<>());
                    }
                }

                final double multiplier = !drops.equals(naturalDrops) ? island.getMobDropsMultiplier() - 1 : island.getMobDropsMultiplier();

                multiplyItems(drops, multiplier);

                //Spawning them synced
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for(ItemStack itemStack : drops){
                        int amount = itemStack.getAmount();

                        while(amount > 64){
                            itemStack.setAmount(64);
                            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), itemStack);
                            amount -= 64;
                        }

                        itemStack.setAmount(amount);
                        e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), itemStack);
                    }
                });
            });
        }

    }

    private void multiplyItems(List<ItemStack> drops, double multiplier){
        for(ItemStack itemStack : drops){
            itemStack.setAmount((int) (itemStack.getAmount() * multiplier));
        }
    }

    /*
     * Hopper Limits
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHopperPlaceMonitor(BlockPlaceEvent e){
        if(e.getBlockPlaced().getType() != Material.HOPPER)
            return;

        Island island = plugin.getGrid().getIslandAt(e.getBlockPlaced().getLocation());

        if(island == null)
            return;

        island.handleHopperPlacement();
    }

    private Set<UUID> noRightClickTwice = new HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHopperCartPlaceMonitor(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK || noRightClickTwice.contains(e.getPlayer().getUniqueId()) ||
                !e.getClickedBlock().getType().name().contains("RAIL") || e.getItem() == null || e.getItem().getType() != Material.HOPPER_MINECART)
            return;

        Island island = plugin.getGrid().getIslandAt(e.getClickedBlock().getLocation());

        if(island == null)
            return;

        noRightClickTwice.add(e.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin,() -> noRightClickTwice.remove(e.getPlayer().getUniqueId()), 2L);

        island.handleHopperPlacement();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHopperBreakMonitor(BlockBreakEvent e){
        if(e.getBlock().getType() != Material.HOPPER)
            return;

        Island island = plugin.getGrid().getIslandAt(e.getBlock().getLocation());

        if(island == null)
            return;

        island.handleHopperBreakment();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHopperCartBreakMonitor(VehicleDestroyEvent e){
        if(!(e.getVehicle() instanceof HopperMinecart))
            return;

        Island island = plugin.getGrid().getIslandAt(e.getVehicle().getLocation());

        if(island == null)
            return;

        island.handleHopperBreakment();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHopperPlace(BlockPlaceEvent e){
        if(!plugin.getSuperiorSkyblock().getSettings().hoppersLimitEnabled ||
                e.getBlockPlaced().getType() != Material.HOPPER && e.getBlockPlaced().getType() != Material.HOPPER_MINECART)
            return;

        Island island = plugin.getGrid().getIslandAt(e.getBlockPlaced().getLocation());

        if(island == null)
            return;

        if(island.getHoppersLimit() != -1 && island.getHoppersAmount() >= island.getHoppersLimit()){
            e.setCancelled(true);
            Locale.MAX_HOPPERS.send(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHopperCartPlace(PlayerInteractEvent e) {
        if(!plugin.getSuperiorSkyblock().getSettings().hoppersLimitEnabled || e.getAction() != Action.RIGHT_CLICK_BLOCK ||
                noRightClickTwice.contains(e.getPlayer().getUniqueId()) || !e.getClickedBlock().getType().name().contains("RAIL") ||
                e.getItem() == null || e.getItem().getType() != Material.HOPPER_MINECART)
            return;

        Island island = plugin.getGrid().getIslandAt(e.getClickedBlock().getLocation());

        if(island == null)
            return;

        if(island.getHoppersLimit() != -1 && island.getHoppersAmount() >= island.getHoppersLimit()){
            e.setCancelled(true);
            //e.getPlayer().sendMessage(plugin.getCore().getConfigUtils().getSuperiorConfig().getString("messages.max-hoppers").replaceAll("&", "§"));
            Locale.MAX_HOPPERS.send(e.getPlayer());
        }
    }

}
