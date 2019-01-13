package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.panels.ControlPanel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CmdMiniShop implements ICommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("minishop", "ms", "shop");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.minishop";
    }

    @Override
    public String getUsage() {
        return "is shop";
    }

    @Override
    public String getDescription() {
        return "Opens the MiniShop";
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

        if(!Settings.useEconomy || !Settings.useMinishop){
            Locale.SHOP_DISABLED.send(player);
            return;
        }

        if (plugin.getGrid().getIsland(player.getUniqueId()) == null) {
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        player.openInventory(ControlPanel.miniShop);
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
