package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.List;

public class CmdBiomes implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("biomes");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.biomes";
    }

    @Override
    public String getUsage() {
        return "is biomes";
    }

    @Override
    public String getDescription() {
        return "Open the biomes GUI.";
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

        if(!island.getOwner().equals(player.getUniqueId())){
            Locale.MUST_BE_LEADER.send(player);
            return;
        }

        if (plugin.getPlayers().getIslandLocation(player.getUniqueId()).getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            Locale.CANNOT_BE_DONE_IN_NETHER.send(player);
            return;
        }

        Inventory inv = plugin.getBiomes().getBiomePanel(player);

        if (inv != null) {
            player.openInventory(inv);
        }
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
