package com.ome_r.superiorskyblock.upgrades;

import org.bukkit.inventory.ItemStack;

public interface Upgrade {

    String getName();

    double getPrice(int level);

    void setPrice(int level, double price);

    int getMaximumLevel();

    int getItemSlot();

    void setItemSlot(int itemSlot);

    void setMaxLevelItem(ItemStack maxLevelItem);

    void setNextLevelItem(ItemStack nextLevelItem);

    ItemStack getMaxLevelItem();

    ItemStack getNextLevelItem();

}
