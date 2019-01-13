package com.ome_r.superiorskyblock.upgrades;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandUpgrade implements Upgrade {

    private final String name;
    private final Map<Integer, Double> prices;
    private final Map<Integer, List<String>> commands;
    private final Map<Integer, String> placeHolders;

    private int itemSlot;
    private ItemStack maxLevelItem, nextLevelItem;

    public CommandUpgrade(String name){
        this.name = name;
        this.prices = new HashMap<>();
        this.commands = new HashMap<>();
        this.placeHolders = new HashMap<>();
        this.itemSlot = 0;
        this.maxLevelItem = this.nextLevelItem = new ItemStack(Material.STONE);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice(int level){
        return prices.getOrDefault(level, 0.0);
    }

    @Override
    public void setPrice(int level, double price){
        prices.put(level, price);
    }

    @Override
    public int getMaximumLevel(){
        int level = 1;

        while(commands.containsKey(level))
            level++;

        return level - 1;
    }

    @Override
    public int getItemSlot() {
        return itemSlot;
    }

    @Override
    public void setItemSlot(int itemSlot) {
        this.itemSlot = itemSlot;
    }

    @Override
    public void setMaxLevelItem(ItemStack maxLevelItem) {
        this.maxLevelItem = maxLevelItem.clone();
    }

    @Override
    public void setNextLevelItem(ItemStack nextLevelItem) {
        this.nextLevelItem = nextLevelItem.clone();
    }

    @Override
    public ItemStack getMaxLevelItem() {
        return maxLevelItem.clone();
    }

    @Override
    public ItemStack getNextLevelItem() {
        return nextLevelItem.clone();
    }

    public String getPlaceholder(int level) {
        return placeHolders.getOrDefault(level, String.valueOf(level));
    }

    public void setPlaceholder(int level, String placeHolder){
        placeHolders.put(level, placeHolder);
    }

    public List<String> getCommands(int level){
        return commands.getOrDefault(level, new ArrayList<>());
    }

    public void setCommands(int level, List<String> commands){
        this.commands.put(level, commands);
    }

}
