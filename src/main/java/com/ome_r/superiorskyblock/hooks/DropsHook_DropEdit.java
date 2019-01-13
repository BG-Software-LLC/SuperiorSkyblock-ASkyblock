package com.ome_r.superiorskyblock.hooks;

import de.Linus122.DropEdit.Main;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DropsHook_DropEdit implements DropsHook {

    @Override
    public List<ItemStack> getDrops(EntityType entityType, List<ItemStack> naturalDrops) {
        List<ItemStack> drops = JavaPlugin.getPlugin(Main.class).getDrops(entityType);

        if(drops == null)
            return new ArrayList<>(naturalDrops);

        return drops.stream().filter(itemStack -> itemStack.getType() != Material.AIR)
                .collect(Collectors.toList());
    }

}
