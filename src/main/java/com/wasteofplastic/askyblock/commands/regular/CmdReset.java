package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.wasteofplastic.askyblock.ResetWaitTime.confirm;
import static com.wasteofplastic.askyblock.ResetWaitTime.resetWaitTime;

public class CmdReset implements ICommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("reset", "restart");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.reset";
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

        if (!plugin.getPlayers().hasIsland(player.getUniqueId())) {
            Util.runCommand(player, Settings.ISLANDCOMMAND);
            return;
        }


        if (plugin.getPlayers().inTeam(player.getUniqueId())) {
            if (!plugin.getPlayers().getTeamLeader(player.getUniqueId()).equals(player.getUniqueId())) {
                Locale.MUST_BE_LEADER.send(player);
            } else {
                Locale.REMOVE_PLAYERS_BEFORE_RESET.send(player);
            }
            return;
        }

        int resetsLeft = plugin.getPlayers().getResetsLeft(player.getUniqueId());

        // Check if the player has used up all their resets
        if (resetsLeft <= 0) {
            Locale.NO_RESETS_LEFT.send(player);
            return;
        }

        Locale.RESETS_LEFT.send(player, resetsLeft);

        if(onRestartWaitTime(player) && Settings.resetWait != 0 && !player.isOp()){
            Locale.RESET_WAIT.send(player, getResetWaitTime(player));
            return;
        }

        Locale.RESET_CONFIRM.send(player, Settings.resetConfirmWait);

        if (!confirm.containsKey(player.getUniqueId()) || !confirm.get(player.getUniqueId())) {
            confirm.put(player.getUniqueId(), true);
            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    confirm.put(player.getUniqueId(), false), (Settings.resetConfirmWait * 20));
        }
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }

    private boolean onRestartWaitTime(final Player player) {
        return resetWaitTime.containsKey(player.getUniqueId()) &&
                resetWaitTime.get(player.getUniqueId()) > Calendar.getInstance().getTimeInMillis();
    }

    private long getResetWaitTime(final Player player) {
        if (resetWaitTime.containsKey(player.getUniqueId())) {
            if (resetWaitTime.get(player.getUniqueId()) > Calendar.getInstance().getTimeInMillis()) {
                return (resetWaitTime.get(player.getUniqueId()) - Calendar.getInstance().getTimeInMillis()) / 1000;
            }
        }

        return 0L;
    }


}
