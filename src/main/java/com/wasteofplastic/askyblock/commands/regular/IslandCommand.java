package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IslandCommand implements CommandExecutor, TabCompleter {

    private ASkyBlock plugin;
    private List<ICommand> subCommands = new ArrayList<>();

    public IslandCommand(ASkyBlock plugin){
        this.plugin = plugin;
        subCommands.add(new CmdBan());
        subCommands.add(new CmdBanList());
        subCommands.add(new CmdBiomes());
        subCommands.add(new CmdChat());
        subCommands.add(new CmdConfirm());
        subCommands.add(new CmdControlPanel());
        subCommands.add(new CmdCreate());
        subCommands.add(new CmdExpel());
        subCommands.add(new CmdGo());
        subCommands.add(new CmdInvite());
        subCommands.add(new CmdLang());
        subCommands.add(new CmdLevel());
        subCommands.add(new CmdLock());
        subCommands.add(new CmdMiniShop());
        subCommands.add(new CmdName());
        subCommands.add(new CmdReset());
        subCommands.add(new CmdResetName());
        subCommands.add(new CmdSettings());
        subCommands.add(new CmdSpawn());
        subCommands.add(new CmdTop());
        subCommands.add(new CmdUnban());
        subCommands.add(new CmdUpgrade());
        subCommands.add(new CmdValue());
        subCommands.add(new CmdWarp());
        subCommands.add(new CmdWarps());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "I am sorry, but you can't use /is commands.");
            return false;
        }

        if(args.length != 0){
            for(ICommand command : subCommands){
                if(command.getAliases().contains(args[0].toLowerCase())){
                    if(!command.getPermission().isEmpty() && !sender.hasPermission(command.getPermission())){
                        Locale.NO_PERMISSION.send(sender);
                        return false;
                    }

                    if(args.length < command.getMinArgs() || args.length > command.getMaxArgs()){
                        Locale.COMMAND_USAGE.send(sender, command.getUsage());
                        return false;
                    }

                    command.perform(plugin, sender, args);
                    return true;
                }
            }

            //Unknown command
            Locale.UNKNOWN_COMMAND.send(sender);
            return true;
        }

        //Checking that the player has permission to use at least one of the commands.
        for(ICommand subCommand : subCommands){
            if(sender.hasPermission(subCommand.getPermission())){
                //Player has permission
                Locale.HELP_COMMAND_HEADER.send(sender);

                for(ICommand cmd : subCommands)
                    Locale.HELP_COMMAND_LINE.send(sender, cmd.getUsage(), cmd.getDescription());

                Locale.HELP_COMMAND_FOOTER.send(sender);
                return false;
            }
        }

        Locale.NO_PERMISSION.send(sender);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

}
