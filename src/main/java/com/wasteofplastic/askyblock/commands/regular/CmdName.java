package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdName implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("name");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.name:";
    }

    @Override
    public String getUsage() {
        return "is name <name>";
    }

    @Override
    public String getDescription() {
        return "Set a name for your island.";
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

        if(plugin.getGrid().getIsland(player.getUniqueId()) == null){
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        if(args[0].length() < Settings.minNameLength){
            Locale.NAME_TOO_SHORT.send(sender, Settings.minNameLength);
            return;
        }else if(args[0].length() > Settings.maxNameLength){
            Locale.NAME_TOO_LONG.send(sender, Settings.maxNameLength);
            return;
        }

        plugin.getGrid().setIslandName(player.getUniqueId(), ChatColor.translateAlternateColorCodes('&', args[0]));
        Locale.NAME_CHANGE_SUCCESS.send(sender, args[0]);
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
