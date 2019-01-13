package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.LevelCalcByChunk;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CmdLevel implements ICommand {

    private ASkyBlock plugin = ASkyBlock.getPlugin(ASkyBlock.class);
    private HashMap<UUID, Long> levelWaitTime = new HashMap<>();

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("level");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.info";
    }

    @Override
    public String getUsage() {
        return "is level [player]";
    }

    @Override
    public String getDescription() {
        return "Calculate your island level.";
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
        UUID targetUUID = player.getUniqueId();

        if(args.length == 2){
            targetUUID = plugin.getPlayers().getUUID(args[1]);
        }

        if(targetUUID == null){
            Locale.INVALID_PLAYER.send(player, args[1]);
            return;
        }

        Island island = plugin.getGrid().getIsland(targetUUID);

        if(island == null){
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        calculateIslandLevel(player, targetUUID);
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }

    private boolean calculateIslandLevel(final CommandSender sender, final UUID targetPlayer) {
        return calculateIslandLevel(sender, targetPlayer, false);
    }

    private boolean calculateIslandLevel(final CommandSender sender, final UUID targetPlayer, boolean report) {
        if (sender instanceof Player) {
            Player asker = (Player)sender;

            if (asker.getUniqueId().equals(targetPlayer) || asker.isOp() || asker.hasPermission("askyblock.mod.info")) {
                if (!onLevelWaitTime(asker) || Settings.levelWait <= 0 || asker.isOp() || asker.hasPermission("askyblock.mod.info")) {
                    Locale.LEVEL_CALCULATING.send(asker);
                    setLevelWaitTime(asker);
                    new LevelCalcByChunk(plugin, plugin.getGrid().getIsland(targetPlayer), targetPlayer, asker, report);
                } else {
                    Locale.LEVEL_WAIT.send(asker, getLevelWaitTime(asker));
                }

            } else {
                Util.sendMessage(asker, ChatColor.GREEN + plugin.myLocale(asker.getUniqueId()).islandislandLevelis.replace("[level]", String.valueOf(plugin.getPlayers().getIslandLevel(targetPlayer))));
            }
        } else {
            // Console request
            Locale.LEVEL_CALCULATING.send(sender);
            new LevelCalcByChunk(plugin, plugin.getGrid().getIsland(targetPlayer), targetPlayer, sender, report);
        }
        return true;
    }

    private void setLevelWaitTime(final Player player) {
        levelWaitTime.put(player.getUniqueId(), Calendar.getInstance().getTimeInMillis() + Settings.levelWait * 1000);
    }

    private boolean onLevelWaitTime(final Player player) {
        if (levelWaitTime.containsKey(player.getUniqueId())) {
            return levelWaitTime.get(player.getUniqueId()) > Calendar.getInstance().getTimeInMillis();
        }

        return false;
    }

    private long getLevelWaitTime(final Player player) {
        if (levelWaitTime.containsKey(player.getUniqueId())) {
            if (levelWaitTime.get(player.getUniqueId()) > Calendar.getInstance().getTimeInMillis()) {
                return (levelWaitTime.get(player.getUniqueId()) - Calendar.getInstance().getTimeInMillis()) / 1000;
            }
        }

        return 0L;
    }

}
