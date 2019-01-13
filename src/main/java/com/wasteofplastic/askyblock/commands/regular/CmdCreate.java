package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.schematics.ISchematic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static com.ome_r.superiorskyblock.utils.IslandUtils.*;

public class CmdCreate implements ICommand {


    @Override
    public List<String> getAliases() {
        return Arrays.asList("create", "make");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "is make [schematic]";
    }

    @Override
    public String getDescription() {
        return "Create a new island.";
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

        if (!pendingNewIslandSelection.contains(player.getUniqueId())) {
            Locale.UNKNOWN_COMMAND.send(player);
            return;
        }

        pendingNewIslandSelection.remove(player.getUniqueId());

        String schemName = args.length != 2 ? "default" : args[1];

        ISchematic schematic = plugin.getSuperiorSkyblock().getSchematics().getSchematic(schemName);

        if(schematic == null){
            Locale.UNKNOWN_COMMAND.send(player);
            return;
        }

        if(!schematic.getPerm().isEmpty() && !player.hasPermission(schematic.getPerm())){
            Locale.NO_PERMISSION.send(player);
            return;
        }

        Island oldIsland = plugin.getGrid().getIsland(player.getUniqueId());
        newIsland(player, schematic);
        if (resettingIsland.contains(player.getUniqueId())) {
            resettingIsland.remove(player.getUniqueId());
            resetPlayer(player, oldIsland);
        }
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }

}
