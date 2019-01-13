package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdUnban implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("unban");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.ban";
    }

    @Override
    public String getUsage() {
        return "is unban <player>";
    }

    @Override
    public String getDescription() {
        return "Unban a player from your island.";
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
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if(plugin.getGrid().getIsland(player.getUniqueId()) == null){
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        if (target == null) {
            Locale.INVALID_PLAYER.send(player, args[1]);
            return;
        }

        if(player.equals(target)){
            Locale.SELF_UNBAN.send(player);
            return;
        }


        if (!plugin.getPlayers().isBanned(player.getUniqueId(), target.getUniqueId())) {
            Locale.PLAYER_NOT_BANNED.send(player, target.getName());
            return;
        }

        if (target.isOnline()) {
            Locale.GOT_UNBANNED.send((Player) target, player.getName());
        } else {
            plugin.getMessages().setMessage(target.getUniqueId(), Locale.GOT_UNBANNED.getMessage(player.getName()));
        }

        Locale.UNBAN_SUCCESS.send(player, target.getName());
        plugin.getMessages().tellTeam(player.getUniqueId(), Locale.UNBAN_SUCCESS.getMessage(target.getName()));

        plugin.getPlayers().unBan(player.getUniqueId(), target.getUniqueId());
        plugin.getGrid().saveGrid();
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
