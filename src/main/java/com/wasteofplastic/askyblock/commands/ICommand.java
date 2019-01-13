package com.wasteofplastic.askyblock.commands;

import com.wasteofplastic.askyblock.ASkyBlock;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand {

    List<String> getAliases();

    String getPermission();

    String getUsage();

    String getDescription();

    int getMinArgs();

    int getMaxArgs();

    void perform(ASkyBlock plugin, CommandSender sender, String[] args);

    void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args);

}
