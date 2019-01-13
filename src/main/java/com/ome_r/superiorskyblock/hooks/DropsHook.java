package com.ome_r.superiorskyblock.hooks;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface DropsHook {

    List<ItemStack> getDrops(EntityType entityType, List<ItemStack> naturalDrops);

}
