package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.panels.ControlPanel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CmdControlPanel implements ICommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("cp", "controlpanel");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.controlpanel";
    }

    @Override
    public String getUsage() {
        return "is controlpanel [on/off]";
    }

    @Override
    public String getDescription() {
        return "Open the island GUI.";
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

        if(args.length == 1) {
            player.openInventory(ControlPanel.controlPanel.get(ControlPanel.getDefaultPanelName()));
        }

        else{
            if (args[1].equalsIgnoreCase("on")) {
                plugin.getPlayers().setControlPanel(player.getUniqueId(), true);
            } else if (args[1].equalsIgnoreCase("off")) {
                plugin.getPlayers().setControlPanel(player.getUniqueId(), false);
            }
            Locale.CONTROL_PANEL_TOGGLE.send(player, args[1].toLowerCase());
        }
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
