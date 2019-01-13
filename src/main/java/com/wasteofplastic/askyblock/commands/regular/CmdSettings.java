package com.wasteofplastic.askyblock.commands.regular;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdSettings implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("settings");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.settings";
    }

    @Override
    public String getUsage() {
        return "is settings";
    }

    @Override
    public String getDescription() {
        return "See island protection and game settings.";
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
        player.openInventory(plugin.getSettingsPanel().islandGuardPanel(player));
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
