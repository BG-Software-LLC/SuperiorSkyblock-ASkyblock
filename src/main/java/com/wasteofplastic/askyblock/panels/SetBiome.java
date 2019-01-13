package com.wasteofplastic.askyblock.panels;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetBiome {
    private static final int SPEED = 5000;
    private int xDone = 0;
    private int zDone = 0;
    private boolean inProgress = false;
    private int taskId;

    public SetBiome(final ASkyBlock plugin, final Island island, final Biome biomeType, CommandSender sender) {
        final World world = island.getCenter().getWorld();
        final UUID playerUUID = (sender instanceof Player) ? ((Player)sender).getUniqueId() : null;
        final String playerName = (sender instanceof Player) ? sender.getName() : "";
        if (sender != null) {
            plugin.getLogger().info("Starting biome change for " + playerName + " (" + biomeType.name() + ")");
        }
        // Update the settings so they can be checked later
        island.setBiome(biomeType);
        xDone = island.getMinX();
        zDone = island.getMinZ();
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (inProgress) {
                return;
            }
            inProgress = true;
            int count = 0;
            while (xDone < (island.getMinX() + island.getIslandDistance())) {
                while(zDone < (island.getMinZ() + island.getIslandDistance())) {
                    world.setBiome(xDone, zDone, biomeType);
                    if (count++ > SPEED) {
                        inProgress = false;
                        return;
                    }
                    zDone++;
                }
                zDone = island.getMinZ();
                xDone++;
            }
            Bukkit.getScheduler().cancelTask(taskId);
            plugin.getLogger().info("Finished biome change for " + playerName + " (" + biomeType.name() + ")");
            if (playerUUID != null) {
                Player p = plugin.getServer().getPlayer(playerUUID);
                if (p != null && p.isOnline()) {
                    Util.sendMessage(p, ChatColor.GREEN + plugin.myLocale(playerUUID).biomeSet.replace("[biome]", Util.prettifyText(biomeType.name())));
                    Util.sendMessage(p, ChatColor.GREEN + plugin.myLocale(playerUUID).needRelog);
                }
            } else {
                plugin.getMessages().setMessage(playerUUID, ChatColor.GREEN + plugin.myLocale(playerUUID).biomeSet.replace("[biome]", biomeType.name()));
            }
        }, 0L,20L).getTaskId();

    }

}
