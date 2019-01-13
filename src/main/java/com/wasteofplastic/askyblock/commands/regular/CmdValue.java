package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.ome_r.superiorskyblock.utils.ListUtils;
import com.ome_r.superiorskyblock.utils.StringUtils;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.commands.ICommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdValue implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("value");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.value";
    }

    @Override
    public String getUsage() {
        return "is value [item-type]";
    }

    @Override
    public String getDescription() {
        return "Shows island level's hand value.";
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
        ItemStack itemStack = player.getItemInHand();

        if(args.length == 2) {
            String material = args[1];
            Material type = Material.AIR;
            short damage = 0;

            try {
                if (material.contains(":")) {
                    type = Material.valueOf(material.split(":")[0]);
                    damage = Short.valueOf(material.split(":")[1]);
                }else{
                    type = Material.valueOf(material);
                }
            }catch(IllegalArgumentException ignored){}

            itemStack = new ItemStack(type, 1, damage);
        }

        if(itemStack == null)
            itemStack = new ItemStack(Material.AIR);

        if(itemStack.getType().isBlock()){
            double multiplier = getMultiplier(player);

            if(player.getLocation().getBlockY() < Settings.seaHeight){
                multiplier *= Settings.underWaterMultiplier;
            }

            int value = 0;
            String key = StringUtils.getItemKey(itemStack);
            if(ListUtils.contains(Settings.blockValues, key))
                value = (int) (ListUtils.get(Settings.blockValues, 0, key) * multiplier);

            if(value > 0){
                Locale.ISLAND_BLOCK_VALUE.send(player, StringUtils.getFormattedType(itemStack.getType().name()), value);
            }

            else{
                Locale.ISLAND_BLOCK_WORTHLESS.send(player, StringUtils.getFormattedType(itemStack.getType().name()));
            }

            return;
        }

        Locale.ITEM_NOT_BLOCK.send(player, StringUtils.getFormattedType(itemStack.getType().name()));
    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }

    private double getMultiplier(Player player){
        double multiplier = 1.0;

        Matcher matcher;
        for (PermissionAttachmentInfo perms : player.getEffectivePermissions()) {
            if((matcher = Pattern.compile("askyblock\\.island\\.multiplier\\.(\\d)").matcher(perms.getPermission())).matches()){
                multiplier = Math.max(multiplier, Integer.valueOf(matcher.group(1)));
            }
        }

        return multiplier < 1 ? 1 : multiplier;
    }

}
