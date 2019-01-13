package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CmdUpgrade implements ICommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("upgrade", "upgrades");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "is upgrade";
    }

    @Override
    public String getDescription() {
        return "Open the upgrades menu for the island.";
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

        if(plugin.getGrid().getIsland(player.getUniqueId()) == null){
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        player.openInventory(plugin.getSuperiorSkyblock().getUpgrades().getUpgradeGUI(player));
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
