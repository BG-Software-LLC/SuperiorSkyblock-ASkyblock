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

public class CmdExpel implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("expel");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.expel";
    }

    @Override
    public String getUsage() {
        return "is expel <player>";
    }

    @Override
    public String getDescription() {
        return "Force a player from your island.";
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
        Player player = (Player) sender, target = Bukkit.getPlayer(args[1]);
        Island island = plugin.getGrid().getIsland(player.getUniqueId());

        if(island == null){
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        if (target == null || !player.canSee(target)) {
            Locale.INVALID_PLAYER.send(player, args[1]);
            return;
        }

        if (target.equals(player)) {
            Locale.SELF_EXPEL.send(player);
            return;
        }

        if(target.hasPermission("askyblock.mod.bypassprotect") || target.hasPermission("askyblock.mod.bypassexpel") ||
                island.getMembers().contains(target.getUniqueId())){
            Locale.CANNOT_EXPEL_PLAYER.send(player, target.getName());
            return;
        }

        if(plugin.getGrid().isOnIsland(player, target)){
            if(plugin.getGrid().getIsland(target.getUniqueId()) != null){
                plugin.getGrid().homeTeleport(target);
            }else{
                if (!target.performCommand(Settings.SPAWNCOMMAND))
                    target.teleport(player.getWorld().getSpawnLocation());
            }
        }

        Locale.GOT_EXPELLED.send(target);
        Locale.EXPEL_SUCCESS.send(player, target.getName());
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
