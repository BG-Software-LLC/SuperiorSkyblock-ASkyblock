package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.handlers.SchematicsHandler;
import com.wasteofplastic.askyblock.schematics.ISchematic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.ome_r.superiorskyblock.utils.IslandUtils.*;
import static com.wasteofplastic.askyblock.ResetWaitTime.confirm;

public class CmdConfirm implements ICommand {

    private ASkyBlock plugin = ASkyBlock.getPlugin(ASkyBlock.class);
    private Random random = new Random();

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("confirm");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "is reset";
    }

    @Override
    public String getDescription() {
        return "Confirm a restart.";
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

        if(!confirm.containsKey(player.getUniqueId()) || !confirm.get(player.getUniqueId())){
            Locale.COMMAND_USAGE.send(player, getUsage());
            return;
        }

        confirm.remove(player.getUniqueId());

        Locale.RESET_WAIT_UNTIL_DONE.send(player);

        int resetsLeft = plugin.getPlayers().getResetsLeft(player.getUniqueId());

        if(resetsLeft <= 0){
            Locale.NO_RESETS_LEFT.send(player);
        }else{
            Locale.RESETS_LEFT.send(player, resetsLeft);
        }


        SchematicsHandler schematics = plugin.getSuperiorSkyblock().getSchematics();

        // Show a schematic panel if the player has a choice
        // Get the schematics that this player is eligible to use
        List<ISchematic> schems = schematics.getSchematics(player, false);
        //plugin.getLogger().info("DEBUG: size of schematics for this player = " + schems.size());
        Island oldIsland = plugin.getGrid().getIsland(player.getUniqueId());
        if (schems.isEmpty()) {
            // No schematics - use default island
            newIsland(player, schematics.getSchematic("default"));
            resetPlayer(player,oldIsland);
        } else if (schems.size() == 1) {
            // Hobson's choice
            newIsland(player,schems.get(0));
            resetPlayer(player,oldIsland);
        } else {
            // A panel can only be shown if there is >1 viable schematic
            if (Settings.useSchematicPanel) {
                pendingNewIslandSelection.add(player.getUniqueId());
                resettingIsland.add(player.getUniqueId());
                player.openInventory(plugin.getSchematicsPanel().getPanel(player));
            } else {
                // No panel
                // Check schematics for specific permission
                schems = schematics.getSchematics(player,true);
                if (schems.isEmpty()) {
                    newIsland(player, schematics.getSchematic("default"));
                } else if (Settings.chooseIslandRandomly) {
                    // Choose an island randomly from the list
                    newIsland(player, schems.get(random.nextInt(schems.size())));
                } else {
                    // Do the first one in the list
                    newIsland(player, schems.get(0));
                }
                resetPlayer(player,oldIsland);
            }
        }

    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
