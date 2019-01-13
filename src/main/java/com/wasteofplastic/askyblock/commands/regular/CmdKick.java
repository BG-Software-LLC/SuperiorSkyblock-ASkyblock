package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.ome_r.superiorskyblock.utils.IslandUtils;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.CoopPlay;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CmdKick implements ICommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("kick", "remove");
    }

    @Override
    public String getPermission() {
        return "askyblock.team.kick";
    }

    @Override
    public String getUsage() {
        return "is kick <player>";
    }

    @Override
    public String getDescription() {
        return "Remove a team player from your island.";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public void perform(ASkyBlock plugin, CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Island island = plugin.getGrid().getIsland(player.getUniqueId());

        if(island == null){
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        if(!island.getOwner().equals(player.getUniqueId())){
            Locale.MUST_BE_LEADER.send(player);
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);

        if(targetPlayer == null){
            Locale.INVALID_PLAYER.send(player);
            return;
        }

        if(!island.getMembers().contains(targetPlayer.getUniqueId())){
            Locale.PLAYER_NOT_IN_TEAM.send(player, targetPlayer.getName());
            return;
        }

        if(player.getUniqueId().equals(targetPlayer.getUniqueId())){
            Locale.SELF_KICK.send(player);
            return;
        }

        // Try to kick player
        if (!IslandUtils.removePlayerFromTeam(targetPlayer.getUniqueId(), island.getOwner(), false))
            return;

        // Log the location that this player left so they
        // cannot join again before the cool down ends

        plugin.getPlayers().startInviteCoolDownTimer(targetPlayer.getUniqueId(), plugin.getPlayers().getIslandLocation(player.getUniqueId()));
        if (Settings.resetChallenges) {
            // Reset the player's challenge status
            plugin.getPlayers().resetAllChallenges(targetPlayer.getUniqueId(), false);
        }

        // Reset the island level
        plugin.getPlayers().setIslandLevel(targetPlayer.getUniqueId(), 0);

        if(targetPlayer.isOnline()){
            Player target = targetPlayer.getPlayer();
            Locale.GOT_KICKED.send(target, player.getName());

            CoopPlay.getInstance().clearMyInvitedCoops(target);
            CoopPlay.getInstance().clearMyCoops(target);

            // Clear the player out and throw their stuff at the leader
            if (target.getWorld().equals(ASkyBlock.getIslandWorld())) {
                if (!Settings.kickedKeepInv) {
                    for (ItemStack i : target.getInventory().getContents()) {
                        if (i != null) {
                            try {
                                // Fire an event to see if this item should be dropped or not
                                // Some plugins may not want items to be dropped
                                Item drop = player.getWorld().dropItemNaturally(player.getLocation(), i);
                                PlayerDropItemEvent event = new PlayerDropItemEvent(target, drop);
                                plugin.getServer().getPluginManager().callEvent(event);
                            } catch (Exception e) {
                            }
                        }
                    }
                    // plugin.resetPlayer(target); <- no good if
                    // reset inventory is false
                    // Clear their inventory and equipment and set
                    // them as survival
                    target.getInventory().clear(); // Javadocs are
                    // wrong - this
                    // does not
                    // clear armor slots! So...
                    // plugin.getLogger().info("DEBUG: Clearing kicked player's inventory");
                    target.getInventory().setArmorContents(null);
                    target.getInventory().setHelmet(null);
                    target.getInventory().setChestplate(null);
                    target.getInventory().setLeggings(null);
                    target.getInventory().setBoots(null);
                    target.getEquipment().clear();
                    // Update the inventory
                    target.updateInventory();
                }
            }
            if (!target.performCommand(Settings.SPAWNCOMMAND)) {
                target.teleport(ASkyBlock.getIslandWorld().getSpawnLocation());
            }
        }else{
            plugin.getMessages().setMessage(targetPlayer.getUniqueId(), Locale.GOT_KICKED.getMessage(player.getName()));
        }

        // Remove any warps
        plugin.getWarpSignsListener().removeWarp(targetPlayer.getUniqueId());
        // Tell leader they removed the player

        Locale.KICK_SUCCESS.send(player, targetPlayer.getName());

        List<UUID> teamMembers = plugin.getPlayers().getMembers(island.getOwner());

        //removePlayerFromTeam(targetPlayer, teamLeader);
        teamMembers.remove(targetPlayer.getUniqueId());
        if (teamMembers.size() < 2) {
            if (!IslandUtils.removePlayerFromTeam(player.getUniqueId(), island.getOwner(), false)) {
                return;
            }
        }
        plugin.getPlayers().save(targetPlayer.getUniqueId());
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
