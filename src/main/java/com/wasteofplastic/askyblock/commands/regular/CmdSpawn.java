package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.events.IslandPreTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

public class CmdSpawn implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("spawn");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.spawn";
    }

    @Override
    public String getUsage() {
        return "is spawn";
    }

    @Override
    public String getDescription() {
        return "";
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

        Island spawn = plugin.getGrid().getSpawn();

        if(spawn == null){
            Locale.UNKNOWN_COMMAND.send(player);
            return;
        }

        Location spawnLocation = ASkyBlock.getIslandWorld().getSpawnLocation();

        spawnLocation.add(new Vector(0.5,0,0.5));

        if (spawn.getSpawnPoint() != null) {
            spawnLocation = spawn.getSpawnPoint();
        }

        IslandPreTeleportEvent event = new IslandPreTeleportEvent(player, IslandPreTeleportEvent.Type.SPAWN, spawnLocation);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            player.teleport(event.getLocation());
        }
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
