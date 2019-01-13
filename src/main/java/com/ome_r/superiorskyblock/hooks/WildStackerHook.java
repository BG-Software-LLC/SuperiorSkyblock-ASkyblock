package com.ome_r.superiorskyblock.hooks;

import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import xyz.wildseries.wildstacker.api.WildStackerAPI;

import java.util.List;

public class WildStackerHook {

    public static void ignoreDeathEvent(LivingEntity livingEntity){
        WildStackerAPI.getStackedEntity(livingEntity).ignoreDeathEvent();
    }

    public static void setDrops(LivingEntity livingEntity, List<ItemStack> drops){
        WildStackerAPI.getStackedEntity(livingEntity).setDrops(drops);
    }

    public static List<ItemStack> getStackedEntityDrops(LivingEntity livingEntity){
        int lootBonusLevel = 0;

        if(livingEntity.getKiller() != null && livingEntity.getKiller().getItemInHand() != null){
            lootBonusLevel = livingEntity.getKiller().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }

        return WildStackerAPI.getStackedEntity(livingEntity).getDrops(lootBonusLevel);
    }

    public static int getSpawnerAmount(Location location){
        return WildStackerAPI.getSpawnersAmount((CreatureSpawner) location.getBlock().getState());
    }



}
