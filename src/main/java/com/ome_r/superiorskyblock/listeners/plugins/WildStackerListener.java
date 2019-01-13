package com.ome_r.superiorskyblock.listeners.plugins;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xyz.wildseries.wildstacker.api.events.SpawnerPlaceEvent;
import xyz.wildseries.wildstacker.api.events.SpawnerStackEvent;
import xyz.wildseries.wildstacker.api.events.SpawnerUnstackEvent;

public class WildStackerListener implements Listener {

    private ASkyBlock plugin;

    public WildStackerListener(ASkyBlock plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnerStack(SpawnerPlaceEvent e){
        int amount = e.getSpawner().getStackAmount();

        if(amount <= 1)
            return;

        Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

        if(island == null)
            return;

        island.handleBlockPlacement(e.getSpawner().getLocation().getBlock(), amount - 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnerStack(SpawnerStackEvent e){
        Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

        if(island == null)
            return;

        island.handleBlockPlacement(e.getSpawner().getLocation().getBlock(), e.getTarget().getStackAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnerUnstack(SpawnerUnstackEvent e){
        Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

        if(island == null)
            return;

        island.handleBlockBreakment(e.getSpawner().getLocation().getBlock(), e.getAmount());
    }

}
