package com.ome_r.superiorskyblock.utils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Created by Brennan on 8/8/2018.
 */

public class HologramUtils {

    private static ASkyBlock plugin = ASkyBlock.getPlugin();

    public static boolean isHologramAtLocation(Location location){
        return getHologramAtLocation(location) != null;
    }

    public static Hologram getHologramAtLocation(Location location) {
        for(Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            if(location.equals(hologram.getLocation())) {
                return hologram;
            }
        }
        return null;
    }


    public static void createHologram(Location hologramLocation, Block stackedBlock) {
        Island island = plugin.getGrid().getIslandAt(stackedBlock.getLocation());

        if(island == null)
            return;

        Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);

        hologram.appendTextLine(getCustomName(island, stackedBlock));
        hologram.appendItemLine(stackedBlock.getState().getData().toItemStack(1));
    }

    public static void destroyHologram(Location hologramLocation) {
        Hologram hologram = getHologramAtLocation(hologramLocation);

        if(hologram != null)
            hologram.delete();
    }

    public static void updateHologram(Location hologramLocation, Block stackedBlock) {
        Hologram hologram = getHologramAtLocation(hologramLocation);

        if(hologram == null) {
            createHologram(hologramLocation, stackedBlock);
            return;
        }

        Island island = plugin.getGrid().getIslandAt(stackedBlock.getLocation());

        if(island == null)
            return;

        hologram.insertTextLine(0, getCustomName(island, stackedBlock)).getParent().removeLine(1);
    }

    private static String getCustomName(Island island, Block block){
        String customName = plugin.getSuperiorSkyblock().getSettings().stackedBlockName;

//        customName = customName.replace("{0}", island.getStackedBlockAmount(block.getLocation()) + "")
//                .replace("{1}", WordUtils.capitalizeFully(block.getType().name().replace("_", " ")));

        customName = customName
                .replace("{0}", island.getStackedBlockAmount(block.getLocation()) + "")
                .replace("{1}", StringUtils.getFormattedType(block.getType().name()));

        return customName;
    }

}
