package com.ome_r.superiorskyblock.hooks;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DropsHook_Default implements DropsHook {

    @Override
    public List<ItemStack> getDrops(EntityType entityType, List<ItemStack> naturalDrops) {
        return new ArrayList<>(naturalDrops);
    }


}
