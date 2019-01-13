package com.ome_r.superiorskyblock.utils;

import com.ome_r.superiorskyblock.legacy.LegacyMaterial;
import org.bukkit.ChatColor;
import org.bukkit.block.CreatureSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListUtils {

    public static boolean contains(Map<String, ?> map, String key){
        return contains(new ArrayList<>(map.keySet()), key);
    }

    public static boolean contains(List<String> list, String key){
        String material = key.split(":")[0];
        return list.contains(material) || list.contains(key);
    }

    public static String getKey(Map<String, ?> map, String key){
        return getKey(new ArrayList<>(map.keySet()), key);
    }

    public static String getKey(List<String> list, String key){
        String material = key.split(":")[0];

        if(list.contains(key))
            return key;
        else if(list.contains(material))
            return material;

        return "";
    }

    public static <T> T get(Map<String, T> map, T def, String key){
        if(contains(new ArrayList<>(map.keySet()), key)){
            String material = key.split(":")[0];
            if(map.containsKey(key))
                return map.get(key);
            else
                return map.get(material);
        }
        return def;
    }

    public static void remove(Map<String, ?> map, String key){
        if(contains(new ArrayList<>(map.keySet()), key)){
            String material = key.split(":")[0];
            if(map.containsKey(material))
                map.remove(material);
            else
                map.remove(key);
        }
    }

    public static List<String> translateAlternateColorCodes(char altColorChar, List<String> listToTranslate){
        List<String> translatedList = new ArrayList<>();

        listToTranslate.forEach(line -> translatedList.add(ChatColor.translateAlternateColorCodes(altColorChar, line)));

        return translatedList;
    }

    public static List<String> formatList(List<String> listToFormat, Object... objects){
        List<String> formattedList = new ArrayList<>();

        listToFormat.forEach(line -> formattedList.add(StringUtils.formatString(line, objects)));

        return formattedList;
    }

    private static boolean contains(Map<String, ?> map, CreatureSpawner creatureSpawner){
        return map.containsKey(LegacyMaterial.SPAWNER.name()) ||
                map.containsKey(LegacyMaterial.SPAWNER.name() + ":" + creatureSpawner.getSpawnedType().name().toUpperCase());
    }

}
