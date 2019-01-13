package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CmdWarps implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("warps");
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getUsage() {
        return "is warps";
    }

    @Override
    public String getDescription() {
        return "List all available welcome-sign warps.";
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

        // Step through warp table
        Set<UUID> warpList = plugin.getWarpSignsListener().listWarps();

        if (warpList.isEmpty()) {
            Locale.NO_AVAILABLE_WARPS.send(player);
            if(player.hasPermission("askyblock.island.addwarp") && plugin.getGrid().playerIsOnIsland(player)){
                Locale.ISLAND_WARP_TIP.send(player);
            }
            return;
        }

        if (Settings.useWarpPanel) {
            // Try the warp panel
            player.openInventory(plugin.getWarpPanel().getWarpPanel(0));
            return;
        }

        boolean hasWarp = false;
        String wlist = "";

        for (UUID w : warpList) {
            if (w == null)
                continue;
            if (wlist.isEmpty()) {
                wlist = plugin.getPlayers().getName(w);
            } else {
                wlist += ", " + plugin.getPlayers().getName(w);
            }
            if (w.equals(player.getUniqueId())) {
                hasWarp = true;
            }
        }

        Locale.AVAILABLE_WARPS_LIST.send(player, wlist);

        if (!hasWarp && player.hasPermission("askyblock.island.addwarp")) {
            Locale.ISLAND_WARP_TIP.send(player);
        }
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
