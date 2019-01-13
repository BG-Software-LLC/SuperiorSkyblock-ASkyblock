package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdBan implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("ban");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.ban";
    }

    @Override
    public String getUsage() {
        return "is ban <player>";
    }

    @Override
    public String getDescription() {
        return "Ban a player from your island.";
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

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null || !player.canSee(target.getPlayer())) {
            Locale.INVALID_PLAYER.send(player, args[1]);
            return;
        }

        if(player.equals(target)){
            Locale.SELF_BAN.send(player);
            return;
        }

        if(island.getMembers().contains(target.getUniqueId()) || target.hasPermission("askyblock.admin.noban")){
            Locale.CANNOT_BAN_PLAYER.send(player, target.getName());
            return;
        }

        if (plugin.getPlayers().isBanned(player.getUniqueId(), target.getUniqueId())) {
            Locale.PLAYER_ALREADY_BANNED.send(player, target.getName());
            return;
        }

        if(plugin.getGrid().isOnIsland(player, target)){
            if(plugin.getGrid().getIsland(target.getUniqueId()) != null) {
                plugin.getGrid().homeTeleport(target);
            }else{
                if (!target.performCommand(Settings.SPAWNCOMMAND)) {
                    target.teleport(player.getWorld().getSpawnLocation());
                }
            }
        }

        Locale.GOT_BANNED.send(target, player.getName());
        Locale.BAN_SUCCESS.send(player, target.getName());

        plugin.getMessages().tellTeam(player.getUniqueId(), Locale.BAN_SUCCESS.getMessage(target.getName()));

        plugin.getPlayers().ban(player.getUniqueId(), target.getUniqueId());
        plugin.getGrid().saveGrid();
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
