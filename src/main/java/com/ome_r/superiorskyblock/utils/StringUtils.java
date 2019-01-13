package com.ome_r.superiorskyblock.utils;

import com.ome_r.superiorskyblock.legacy.LegacyMaterial;
import com.wasteofplastic.askyblock.ASkyBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.text.NumberFormat;

public class StringUtils {

    private static ASkyBlock plugin = ASkyBlock.getPlugin();

    public static String formatString(String stringToFormat, Object... objects){
        String formattedString = stringToFormat;

        for(int i = 0; i < objects.length; i++)
            formattedString = formattedString.replace("{" + i + "}", objects[i].toString());

        return formattedString;
    }

    public static String getFormattedType(String typeName) {
        if(plugin.getSuperiorSkyblock().getSettings().customNames.containsKey(typeName))
            return plugin.getSuperiorSkyblock().getSettings().customNames.get(typeName);

        StringBuilder name = new StringBuilder();

        typeName = typeName.replace(" ", "_");

        for (String section : typeName.split("_")) {
            name.append(section.substring(0, 1).toUpperCase()).append(section.substring(1).toLowerCase()).append(" ");
        }

        return name.substring(0, name.length() - 1);
    }

    public static String getItemKey(EntityType entityType){
        return LegacyMaterial.SPAWNER.name() + ":" + entityType.name();
    }

    public static String getItemKey(Block block){
        if(block.getType() == LegacyMaterial.SPAWNER){
            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            return LegacyMaterial.SPAWNER.name() + ":" + creatureSpawner.getSpawnedType().name();
        }
        return getItemKey(block.getState().getData().toItemStack(1));
    }

    public static String getItemKey(Material material, short damage){
        return getItemKey(new ItemStack(material, 1, damage));
    }

    public static String getItemKey(ItemStack itemStack){
        if(itemStack.getType() == LegacyMaterial.SPAWNER){
            EntityType entityType = ((CreatureSpawner) ((BlockStateMeta) itemStack.getItemMeta()).getBlockState()).getSpawnedType();
            return LegacyMaterial.SPAWNER.name() + ":" + entityType.name();
        }else{
            return itemStack.getType().name() + ":" + itemStack.getDurability();
        }
    }

    public static String fancyLocation(Location location){
        return location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    public static String getFormattedNumber(long worth){
        return NumberFormat.getInstance().format(worth);
    }

}
