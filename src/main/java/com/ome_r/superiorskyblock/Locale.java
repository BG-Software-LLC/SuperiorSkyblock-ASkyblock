package com.ome_r.superiorskyblock;

import com.ome_r.superiorskyblock.hooks.PlaceholderHook;
import com.wasteofplastic.askyblock.ASkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Locale {

    private static Map<String, Locale> localeMap = new HashMap<>();

    public static Locale MAX_HOPPERS = new Locale("MAX_HOPPERS");
    public static Locale UPGRADE_UNKNOWN = new Locale("UPGRADE_UNKNOWN");
    public static Locale UPGRADE_PURCHASE = new Locale("UPGRADE_PURCHASE");
    public static Locale UPGRADE_FAILURE = new Locale("UPGRADE_FAILURE");
    public static Locale UPGRADE_OWNED = new Locale("UPGRADE_OWNED");
    public static Locale NO_PERMISSION = new Locale("NO_PERMISSION");
    public static Locale COMMAND_USAGE = new Locale("COMMAND_USAGE");
    public static Locale INVALID_LEVEL = new Locale("INVALID_LEVEL");
    public static Locale SET_UPGRADE_SUCCESS = new Locale("SET_UPGRADE_SUCCESS");
    public static Locale GOT_SET_UPGRADE = new Locale("GOT_SET_UPGRADE");
    public static Locale BORDER_TOGGLE_ON = new Locale("BORDER_TOGGLE_ON");
    public static Locale BORDER_TOGGLE_OFF = new Locale("BORDER_TOGGLE_OFF");
    public static Locale ITEM_NOT_BLOCK = new Locale("ITEM_NOT_BLOCK");
    public static Locale ISLAND_BLOCK_VALUE = new Locale("ISLAND_BLOCK_VALUE");
    public static Locale ISLAND_BLOCK_WORTHLESS = new Locale("ISLAND_BLOCK_WORTHLESS");
    public static Locale INVALID_ISLAND = new Locale("INVALID_ISLAND");
    public static Locale NAME_TOO_SHORT = new Locale("NAME_TOO_SHORT");
    public static Locale NAME_TOO_LONG = new Locale("NAME_TOO_LONG");
    public static Locale NAME_CHANGE_SUCCESS = new Locale("NAME_CHANGE_SUCCESS");
    public static Locale NAME_RESET_SUCCESS = new Locale("NAME_RESET_SUCCESS");
    public static Locale INVALID_PLAYER = new Locale("INVALID_PLAYER");
    public static Locale SELF_EXPEL = new Locale("SELF_EXPEL");
    public static Locale CANNOT_EXPEL_PLAYER = new Locale("CANNOT_EXPEL_PLAYER");
    public static Locale COOP_REMOVED = new Locale("COOP_REMOVED");
    public static Locale COOP_REMOVE_SUCCESS = new Locale("COOP_REMOVE_SUCCESS");
    public static Locale GOT_EXPELLED = new Locale("GOT_EXPELLED");
    public static Locale EXPEL_SUCCESS = new Locale("EXPEL_SUCCESS");
    public static Locale UNKNOWN_COMMAND = new Locale("UNKNOWN_COMMAND");
    public static Locale INVALID_CHAT_TEAM = new Locale("INVALID_CHAT_TEAM");
    public static Locale CHAT_TEAM_ON = new Locale("CHAT_TEAM_ON");
    public static Locale CHAT_TEAM_OFF = new Locale("CHAT_TEAM_OFF");
    public static Locale BAN_LIST_HEADER = new Locale("BAN_LIST_HEADER");
    public static Locale BAN_LIST_LINE = new Locale("BAN_LIST_LINE");
    public static Locale BAN_LIST_LINE_EMPTY = new Locale("BAN_LIST_LINE_EMPTY");
    public static Locale BAN_LIST_FOOTER = new Locale("BAN_LIST_FOOTER");
    public static Locale SELF_BAN = new Locale("SELF_BAN");
    public static Locale CANNOT_BAN_PLAYER = new Locale("CANNOT_BAN_PLAYER");
    public static Locale PLAYER_ALREADY_BANNED = new Locale("PLAYER_ALREADY_BANNED");
    public static Locale GOT_BANNED = new Locale("GOT_BANNED");
    public static Locale BAN_SUCCESS = new Locale("BAN_SUCCESS");
    public static Locale SELF_UNBAN = new Locale("SELF_UNBAN");
    public static Locale PLAYER_NOT_BANNED = new Locale("PLAYER_NOT_BANNED");
    public static Locale CANNOT_UNBAN_PLAYER = new Locale("CANNOT_UNBAN_PLAYER");
    public static Locale GOT_UNBANNED = new Locale("GOT_UNBANNED");
    public static Locale UNBAN_SUCCESS = new Locale("UNBAN_SUCCESS");
    public static Locale CHANGE_LOCALE = new Locale("CHANGE_LOCALE");
    public static Locale ISLAND_UNLOCK = new Locale("ISLAND_UNLOCK");
    public static Locale ISLAND_LOCK = new Locale("ISLAND_LOCK");
    public static Locale INVALID_HOME = new Locale("INVALID_HOME");
    public static Locale CONTROL_PANEL_TOGGLE = new Locale("CONTROL_PANEL_TOGGLE");
    public static Locale SHOP_DISABLED = new Locale("SHOP_DISABLED");
    public static Locale NO_ISLAND_WARPS = new Locale("NO_ISLAND_WARPS");
    public static Locale ISLAND_WARP_TIP = new Locale("ISLAND_WARP_TIP");
    public static Locale INVALID_WARP = new Locale("INVALID_WARP");
    public static Locale BANNED_FROM_ISLAND = new Locale("BANNED_FROM_ISLAND");
    public static Locale ISLAND_LOCKED = new Locale("ISLAND_LOCKED");
    public static Locale UNSAFE_ISLAND_WARP = new Locale("UNSAFE_ISLAND_WARP");
    public static Locale NO_AVAILABLE_WARPS = new Locale("NO_AVAILABLE_WARPS");
    public static Locale AVAILABLE_WARPS_LIST = new Locale("AVAILABLE_WARPS_LIST");
    public static Locale REMOVE_PLAYERS_BEFORE_RESET = new Locale("REMOVE_PLAYERS_BEFORE_RESET");
    public static Locale NO_RESETS_LEFT = new Locale("NO_RESETS_LEFT");
    public static Locale RESETS_LEFT = new Locale("RESETS_LEFT");
    public static Locale RESET_CONFIRM = new Locale("RESET_CONFIRM");
    public static Locale RESET_WAIT = new Locale("RESET_WAIT");
    public static Locale RESET_WAIT_UNTIL_DONE = new Locale("RESET_WAIT_UNTIL_DONE");
    public static Locale MUST_BE_LEADER = new Locale("MUST_BE_LEADER");
    public static Locale CANNOT_BE_DONE_IN_NETHER = new Locale("CANNOT_BE_DONE_IN_NETHER");
    public static Locale LEVEL_CALCULATING = new Locale("LEVEL_CALCULATING");
    public static Locale LEVEL_WAIT = new Locale("LEVEL_WAIT");
    public static Locale INVITE_HELP = new Locale("INVITE_HELP");
    public static Locale ISLAND_FULL = new Locale("ISLAND_FULL");
    public static Locale INVITE_LEFT_PLAYERS = new Locale("INVITE_LEFT_PLAYERS");
    public static Locale SELF_INVITE = new Locale("SELF_INVITE");
    public static Locale INVITE_COOLDOWN = new Locale("INVITE_COOLDOWN");
    public static Locale PLAYER_ALREADY_IN_TEAM = new Locale("PLAYER_ALREADY_IN_TEAM");
    public static Locale REMOVING_INVITE = new Locale("REMOVING_INVITE");
    public static Locale SENT_INVITE_TO_PLAYER = new Locale("SENT_INVITE_TO_PLAYER");
    public static Locale GOT_INVITED = new Locale("GOT_INVITED");
    public static Locale WARNING_LOSE_ISLAND = new Locale("WARNING_LOSE_ISLAND");
    public static Locale INVITE_REMOVE = new Locale("INVITE_REMOVE");
    public static Locale HELP_COMMAND_HEADER = new Locale("HELP_COMMAND_HEADER");
    public static Locale HELP_COMMAND_LINE = new Locale("HELP_COMMAND_LINE");
    public static Locale HELP_COMMAND_FOOTER = new Locale("HELP_COMMAND_FOOTER");
    public static Locale PLAYER_NOT_IN_TEAM = new Locale("PLAYER_NOT_IN_TEAM");
    public static Locale SELF_KICK = new Locale("SELF_KICK");
    public static Locale GOT_KICKED = new Locale("GOT_KICKED");
    public static Locale KICK_SUCCESS = new Locale("KICK_SUCCESS");

    private Locale(String identifier){
        localeMap.put(identifier, this);
    }

    private String message;

    public String getMessage(Object... objects){
        if(message != null && !message.equals("")) {
            boolean replacePlaceholders = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && objects.length > 0 && objects[0] instanceof Player;
            String msg = message;

            for (int i = replacePlaceholders ? 1 : 0; i < objects.length; i++)
                msg = msg.replace("{" + i + "}", objects[i].toString());

            if(replacePlaceholders){
                msg = PlaceholderHook.format((Player) objects[0], msg);
            }

            return msg;
        }

        return null;
    }

    public void send(CommandSender sender, Object... objects){
        String message = getMessage(objects);
        if(message != null && sender != null) {
            if(sender instanceof Player && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
                sender.sendMessage(PlaceholderHook.format((Player) sender, message));
            else
                sender.sendMessage(message);
        }
    }

    private void setMessage(String message){
        this.message = message;
    }

    public static void reload(){
        long startTime = System.currentTimeMillis();
        int messagesAmount = 0;
        File file = new File(ASkyBlock.getPlugin().getDataFolder(), "lang.yml");

        if(!file.exists())
            ASkyBlock.getPlugin().saveResource("lang.yml", false);

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        for(String identifier : localeMap.keySet()){
            localeMap.get(identifier).setMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString(identifier, "")));
            messagesAmount++;
        }
    }

}
