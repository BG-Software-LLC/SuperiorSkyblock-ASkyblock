package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.CoopPlay;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdLock implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("lock");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.lock";
    }

    @Override
    public String getUsage() {
        return "is lock";
    }

    @Override
    public String getDescription() {
        return "Locks island so visitors cannot enter it.";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public void perform(ASkyBlock plugin, CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Island island = plugin.getGrid().getIsland(player.getUniqueId());

        if (island == null) {
            Util.sendMessage(player, ChatColor.RED + plugin.myLocale(player.getUniqueId()).errorNoIsland);
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        if(island.isLocked()){
            Locale.ISLAND_UNLOCK.send(player, player.getName());
            plugin.getMessages().tellOfflineTeam(player.getUniqueId(), Locale.ISLAND_UNLOCK.getMessage(player.getName()));
            plugin.getMessages().tellTeam(player.getUniqueId(), Locale.ISLAND_UNLOCK.getMessage(player.getName()));
            island.setLocked(false);
            return;
        }

        for (Player target : plugin.getServer().getOnlinePlayers()) {
            if (target == null || target.hasMetadata("NPC") || target.isOp() || player.equals(target) || player.hasPermission("askyblock.mod.bypasslock"))
                continue;

            // See if target is on this player's island and not a coop player
            if (plugin.getGrid().isOnIsland(player, target)
                    && !CoopPlay.getInstance().getCoopPlayers(island.getCenter()).contains(target.getUniqueId())) {
                // Send them home
                if (plugin.getPlayers().inTeam(target.getUniqueId()) || plugin.getPlayers().hasIsland(target.getUniqueId())) {
                    plugin.getGrid().homeTeleport(target);
                } else {
                    // Just move target to spawn
                    if (!target.performCommand(Settings.SPAWNCOMMAND)) {
                        target.teleport(player.getWorld().getSpawnLocation());
                    }
                }
                Locale.GOT_EXPELLED.send(target);
                Locale.EXPEL_SUCCESS.send(player, target.getName());
            }
        }

        Locale.ISLAND_LOCK.send(player, player.getName());
        plugin.getMessages().tellOfflineTeam(player.getUniqueId(), Locale.ISLAND_LOCK.getMessage(player.getName()));
        plugin.getMessages().tellTeam(player.getUniqueId(), Locale.ISLAND_LOCK.getMessage(player.getName()));
        island.setLocked(true);
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
