package com.ome_r.superiorskyblock.listeners;

import com.ome_r.superiorskyblock.utils.FileUtils;
import com.ome_r.superiorskyblock.utils.HologramUtils;
import com.songoda.epicspawners.EpicSpawnersPlugin;
import com.songoda.epicspawners.api.spawner.Spawner;
import com.songoda.epicspawners.spawners.spawner.ESpawnerManager;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.events.IslandChangeOwnerEvent;
import com.wasteofplastic.askyblock.events.IslandDeleteEvent;
import com.wasteofplastic.askyblock.events.IslandEnterEvent;
import com.wasteofplastic.askyblock.events.IslandNewEvent;
import com.wasteofplastic.askyblock.events.IslandPreDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class IslandsListener implements Listener {

    private ASkyBlock plugin;

    public IslandsListener(ASkyBlock plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIslandEnter(IslandEnterEvent e){
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(e.getIsland() != null && e.getPlayer() != null)
                plugin.getSuperiorSkyblock().getNMSAbstraction().setWorldBorder(Bukkit.getPlayer(e.getPlayer()), e.getIsland());
        }, 10L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e){
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Island island = plugin.getGrid().getIslandAt(e.getTo());

            if(island != null && e.getPlayer() != null)
                plugin.getSuperiorSkyblock().getNMSAbstraction().setWorldBorder(e.getPlayer(), island);
        }, 5L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent e){
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Island island = plugin.getGrid().getIslandAt(e.getTo());

            if(island != null && e.getPlayer() != null)
                plugin.getSuperiorSkyblock().getNMSAbstraction().setWorldBorder(e.getPlayer(), island);
        }, 5L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e){
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Island island = plugin.getGrid().getIslandAt(e.getPlayer().getLocation());

            if(island != null && e.getPlayer() != null)
                plugin.getSuperiorSkyblock().getNMSAbstraction().setWorldBorder(e.getPlayer(), island);
        }, 5L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIslandCreate(IslandNewEvent e){
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Island island = plugin.getGrid().getIslandAt(e.getIslandLocation());

            if(island == null || e.getPlayer() == null || !island.inIslandSpace(e.getPlayer().getLocation()))
                return;

            plugin.getSuperiorSkyblock().getNMSAbstraction().setWorldBorder(e.getPlayer(), island);
        }, 10L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIslandCreate(PlayerRespawnEvent e){
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Island island = plugin.getGrid().getIslandAt(e.getRespawnLocation());

            if(island == null || e.getPlayer() == null || !island.inIslandSpace(e.getPlayer().getLocation()))
                return;

            plugin.getSuperiorSkyblock().getNMSAbstraction().setWorldBorder(e.getPlayer(), island);
        }, 10L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIslandPreDelete(IslandPreDeleteEvent e){
        if(!plugin.getSuperiorSkyblock().getSettings().stackedBlocksEnabled)
            return;

        Island island = e.getIsland();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Location location : island.getStackedBlockLocations()) {
                Location hologramLocation = location.add(0.5, 2, 0.5);
                if(HologramUtils.isHologramAtLocation(hologramLocation)){
                    island.removeStackedBlock(location);
                    HologramUtils.destroyHologram(hologramLocation);
                }else{
                    deleteSpawner(location);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIslandChangeOwner(IslandChangeOwnerEvent e){
        new Thread(() -> FileUtils.deleteIsland(e.getOldOwner())).start();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIslandDelete(IslandDeleteEvent e){
        new Thread(() -> FileUtils.deleteIsland(e.getPlayerUUID())).start();
    }

    private void deleteSpawner(Location location){
        if(plugin.getServer().getPluginManager().isPluginEnabled("EpicSpawners")){
            EpicSpawnersPlugin plugin = EpicSpawnersPlugin.getInstance();
            Spawner spawner = plugin.getSpawnerManager().getSpawnerFromWorld(location);
            if(spawner != null) {
                plugin.getSpawnerManager().removeSpawnerFromWorld(location);
                plugin.getHologramHandler().updateHologram(spawner);
                plugin.getAppearanceHandler().removeDisplayItem(spawner);
                ((ESpawnerManager) plugin.getSpawnerManager()).removeCooldown(spawner);
            }
        }
    }

}
