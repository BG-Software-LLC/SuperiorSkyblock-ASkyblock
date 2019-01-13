package com.ome_r.superiorskyblock.hooks;

import fr.ullrimax.editdrops.Allitems;
import fr.ullrimax.editdrops.Inventories;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropsHook_EditDrops implements DropsHook {

    private Random random;

    public DropsHook_EditDrops(){
        random = new Random();
    }

    @Override
    public List<ItemStack> getDrops(EntityType entityType, List<ItemStack> naturalDrops) {
        List<ItemStack> drops = new ArrayList<>();

        Inventory mobInventory = getInventory(entityType);

        if(mobInventory == null){
            return new ArrayList<>(naturalDrops);
        }

        int rdm = random.nextInt(52);

        if (mobInventory.getItem(52).equals(Allitems.naturalTrue.getItemStack())){
            drops.addAll(naturalDrops);
        }

        if(mobInventory.getItem(rdm) != null)
            drops.add(mobInventory.getItem(rdm));

        return drops;
    }

    private Inventory getInventory(EntityType entityType){
        String fieldName;
        switch (entityType.name()){
            case "IRON_GOLEM":
                fieldName = "golem";
                break;
            case "MUSHROOM_COW":
                fieldName = "mooshroom";
                break;
            case "WITHER_SKELETON":
                fieldName = "wskeleton";
                break;
            case "PIG_ZOMBIE":
                fieldName = "zombiepigman";
                break;
            default:
                fieldName = entityType.name().toLowerCase().replace("_", "");
                break;
        }

        try{
            Field inventoryField = Inventories.class.getDeclaredField(fieldName);
            return (Inventory) inventoryField.get(null);
        }catch(Exception ex){
            return null;
        }
    }

}
