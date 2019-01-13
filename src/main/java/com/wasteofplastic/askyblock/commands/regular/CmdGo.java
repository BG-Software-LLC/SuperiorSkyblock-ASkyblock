package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdGo implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("go");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.go";
    }

    @Override
    public String getUsage() {
        return "is go [#]";
    }

    @Override
    public String getDescription() {
        return "Teleport to your island.";
    }

    @Override
    public int getMinArgs() {
        return 1;
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

        int number = 1;

        if(args.length == 2){
            try{
                number = Integer.valueOf(args[1]);
            }catch(IllegalArgumentException ex){
                Locale.INVALID_HOME.send(player, args[1]);
            }
        }

        if (number <= 1) {
            plugin.getGrid().homeTeleport(player,1);
        }

        else {
            // Dynamic home sizes with permissions
            int maxHomes = plugin.getPlayers().getMaxHomes(player);

            if (number > maxHomes) {
                if (maxHomes > 1) {
                    Locale.INVALID_HOME.send(player, maxHomes);
                } else {
                    plugin.getGrid().homeTeleport(player,1);
                }
            } else {
                // Teleport home
                plugin.getGrid().homeTeleport(player,number);
            }
        }

        if (Settings.islandRemoveMobs) {
            plugin.getGrid().removeMobs(player.getLocation());
        }

    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }

}
