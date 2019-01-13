package com.ome_r.superiorskyblock.hooks;

import net.aminecraftdev.customdrops.CustomDropsAPI;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DropsHook_CustomDrops implements DropsHook {

    @Override
    public List<ItemStack> getDrops(EntityType entityType, List<ItemStack> naturalDrops) {
        if(CustomDropsAPI.getNaturalDrops(entityType))
            return new ArrayList<>(naturalDrops);

        return CustomDropsAPI.getCustomDrops(entityType).stream()
                .filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR).collect(Collectors.toList());
    }

}
