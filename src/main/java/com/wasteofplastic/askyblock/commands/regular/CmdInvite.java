package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CmdInvite implements ICommand {

    private final HashMap<UUID, UUID> inviteList = new HashMap<>();

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("invite");
    }

    @Override
    public String getPermission() {
        return "askyblock.team.create";
    }

    @Override
    public String getUsage() {
        return "is invite [player]";
    }

    @Override
    public String getDescription() {
        return "Invite a player to join your island.";
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

        Island island = plugin.getGrid().getIsland(player.getUniqueId());

        if(island == null){
            Locale.INVALID_ISLAND.send(player);
            return;
        }

        if(!island.getOwner().equals(player.getUniqueId())){
            Locale.MUST_BE_LEADER.send(player);
            return;
        }

        List<UUID> teamMembers = plugin.getPlayers().getMembers(island.getOwner());

        if(args.length == 1){
            Locale.INVITE_HELP.send(player);

            int maxSize = plugin.getPlayers().getMaxTeamSize(player);

            if (teamMembers.size() < maxSize) {
                Locale.INVITE_LEFT_PLAYERS.send(player, maxSize - teamMembers.size());
            } else {
                Locale.ISLAND_FULL.send(player);
            }
            return;
        }

        else if(args.length == 2){
            Player invitedPlayer = plugin.getServer().getPlayer(args[1]);

            if (invitedPlayer == null || !player.canSee(invitedPlayer)) {
                Locale.INVALID_PLAYER.send(player, args[1]);
                return;
            }

            if (player.getUniqueId().equals(invitedPlayer.getUniqueId())) {
                Locale.SELF_INVITE.send(player);
                return;
            }

            long time = plugin.getPlayers().getInviteCoolDownTime(invitedPlayer.getUniqueId(), plugin.getPlayers().getIslandLocation(player.getUniqueId()));

            if (time > 0 && !player.isOp()) {
                Locale.INVITE_COOLDOWN.send(player, time);
                return;
            }

            if (plugin.getPlayers().inTeam(player.getUniqueId())) {
                if(!island.getOwner().equals(player.getUniqueId())){
                    Locale.MUST_BE_LEADER.send(player);
                    return;
                }

                // Player has space in their team
                int maxSize = plugin.getPlayers().getMaxTeamSize(player);

                if(teamMembers.size() >= maxSize){
                    Locale.ISLAND_FULL.send(player);
                    return;
                }
            }

            if(plugin.getPlayers().inTeam(invitedPlayer.getUniqueId())){
                Locale.PLAYER_ALREADY_IN_TEAM.send(player, invitedPlayer.getName());
                return;
            }

            if (inviteList.containsValue(player.getUniqueId())) {
                inviteList.remove(getKeyByValue(inviteList, player.getUniqueId()));
                Locale.REMOVING_INVITE.send(player);
            }

            inviteList.put(invitedPlayer.getUniqueId(), player.getUniqueId());
            Locale.SENT_INVITE_TO_PLAYER.send(player, invitedPlayer.getName());

            Locale.GOT_INVITED.send(invitedPlayer, player.getName());
            Locale.COMMAND_USAGE.send(invitedPlayer, "/is [accept/reject]", "Accept or reject an invitation.");

            if(plugin.getPlayers().hasIsland(invitedPlayer.getUniqueId())){
                Locale.WARNING_LOSE_ISLAND.send(invitedPlayer);
            }

            if(Settings.inviteTimeout > 0){
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (inviteList.containsKey(invitedPlayer.getUniqueId()) && inviteList.get(invitedPlayer.getUniqueId()).equals(player.getUniqueId())) {
                        inviteList.remove(invitedPlayer.getUniqueId());
                        Locale.INVITE_REMOVE.send(player);
                        Locale.INVITE_REMOVE.send(invitedPlayer);
                    }

                }, Settings.inviteTimeout);
            }
        }

    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
