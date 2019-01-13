package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.wasteofplastic.askyblock.ASLocale;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.util.Util;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CmdLang implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("lang");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.lang";
    }

    @Override
    public String getUsage() {
        return "is lang [lang]";
    }

    @Override
    public String getDescription() {
        return "Select language.";
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

        if(args.length == 1){
            displayLocales(player);
            return;
        }

        if (!NumberUtils.isDigits(args[1])) {
            displayLocales(player);
            return;
        }

        else {
            int index = Integer.valueOf(args[1]);
            if (index < 1 || index > plugin.getAvailableLocales().size()) {
                displayLocales(player);
                return;
            }
            for (ASLocale locale : plugin.getAvailableLocales().values()) {
                if (locale.getIndex() == index) {
                    plugin.getPlayers().setLocale(player.getUniqueId(), locale.getLocaleName());
                    Locale.CHANGE_LOCALE.send(player);
                    return;
                }
            }
            // Not in the list
            displayLocales(player);
        }
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }

    private void displayLocales(Player player) {
        TreeMap<Integer,String> langs = new TreeMap<>();
        ASkyBlock plugin = ASkyBlock.getPlugin(ASkyBlock.class);
        for (ASLocale locale : plugin.getAvailableLocales().values()) {
            if (!locale.getLocaleName().equalsIgnoreCase("locale")) {
                langs.put(locale.getIndex(), locale.getLanguageName() + " (" + locale.getCountryName() + ")");
            }
        }
        for (Map.Entry<Integer, String> entry: langs.entrySet()) {
            Util.sendMessage(player, entry.getKey() + ": " + entry.getValue());
        }
    }

}
