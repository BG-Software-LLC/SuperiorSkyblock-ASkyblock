package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CmdBanList implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("banlist");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.ban";
    }

    @Override
    public String getUsage() {
        return "is banlist";
    }

    @Override
    public String getDescription() {
        return "List banned players.";
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

        if(island == null){
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        List<UUID> banList = plugin.getPlayers().getBanList(player.getUniqueId());

        Locale.BAN_LIST_HEADER.send(player);

        if(banList.isEmpty()){
            Locale.BAN_LIST_LINE_EMPTY.send(player);
        }else{
            for(UUID uuid : banList){
                Locale.BAN_LIST_LINE.send(player, Bukkit.getOfflinePlayer(uuid).getName());
            }
        }

        Locale.BAN_LIST_FOOTER.send(player);

    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
