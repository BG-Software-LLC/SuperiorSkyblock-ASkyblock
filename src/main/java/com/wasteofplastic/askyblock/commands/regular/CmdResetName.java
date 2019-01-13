package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdResetName implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("resetname");
    }

    @Override
    public String getPermission() {
        return "askyblock.mod.resetname:";
    }

    @Override
    public String getUsage() {
        return "is resetname";
    }

    @Override
    public String getDescription() {
        return "Reset your island name.";
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

        plugin.getGrid().setIslandName(player.getUniqueId(), null);
        Locale.NAME_RESET_SUCCESS.send(sender);
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
