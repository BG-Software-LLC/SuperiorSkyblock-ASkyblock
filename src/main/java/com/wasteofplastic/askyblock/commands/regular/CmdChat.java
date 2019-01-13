package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CmdChat implements ICommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("teamchat", "tc", "chat");
    }

    @Override
    public String getPermission() {
        return "askyblock.team.chat:";
    }

    @Override
    public String getUsage() {
        return "is chat";
    }

    @Override
    public String getDescription() {
        return "Turn on/off team chat.";
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

        if (!Settings.teamChat) {
            Locale.UNKNOWN_COMMAND.send(player);
            return;
        }

        if(plugin.getGrid().getIsland(player.getUniqueId()) == null){
            Locale.INVALID_CHAT_TEAM.send(player);
            return;
        }

        if (plugin.getChatListener().isTeamChat(player.getUniqueId())) {
            Locale.CHAT_TEAM_OFF.send(player);
            plugin.getChatListener().unSetPlayer(player.getUniqueId());
        } else {
            Locale.CHAT_TEAM_ON.send(player);
            plugin.getChatListener().setPlayer(player.getUniqueId());
        }
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }
}
