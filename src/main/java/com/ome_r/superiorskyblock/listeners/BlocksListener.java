package com.ome_r.superiorskyblock.listeners;

import com.ome_r.superiorskyblock.utils.HologramUtils;
import com.ome_r.superiorskyblock.utils.ListUtils;
import com.ome_r.superiorskyblock.utils.StringUtils;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlocksListener implements Listener {

    private ASkyBlock plugin;
    public BlocksListener(ASkyBlock instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Island island = plugin.getGrid().getIslandAt(e.getBlockPlaced().getLocation());

        if(island == null)
            return;

        if(island.isStackedBlock(e.getBlock().getLocation())){
            Location hologramLocation = e.getBlock().getLocation().add(0.5, 2, 0.5);
            island.removeStackedBlock(e.getBlock().getLocation());
            HologramUtils.destroyHologram(hologramLocation);
        }

        try{
            if(e.getHand() == EquipmentSlot.OFF_HAND)
                return;
        }catch(Throwable ignored){}

        if(!plugin.getSuperiorSkyblock().getSettings().stackedBlocksEnabled)
            return;

        if(!ListUtils.contains(plugin.getSuperiorSkyblock().getSettings().stackedBlockMaterials, StringUtils.getItemKey(e.getBlockPlaced())))
            return;

        if(plugin.getSuperiorSkyblock().getSettings().stackedBlocksDisabledWorlds.contains(e.getBlockPlaced().getWorld().getName()))
            return;

        if(e.getBlockPlaced().getType() != e.getBlockAgainst().getType())
            return;

        e.setCancelled(true);

        int amount = 0;

        if(e.getPlayer().isSneaking()){
            Inventory inventory = e.getPlayer().getInventory();
            for(int i = 0; i < inventory.getSize(); i++){
                ItemStack item = inventory.getItem(i);
                if(item != null && item.isSimilar(e.getBlockAgainst().getState().getData().toItemStack(1))){
                    amount += item.getAmount();
                }
            }
        }else{
            amount = 1;
        }

        Location hologramLocation = e.getBlockAgainst().getLocation().add(0.5, 2, 0.5);

        island.setStackedBlock(e.getBlockAgainst().getLocation(), island.getStackedBlockAmount(e.getBlockAgainst().getLocation()) + amount);
        island.handleBlockPlacement(e.getBlockAgainst(), amount);

        HologramUtils.updateHologram(hologramLocation, e.getBlockAgainst());

        if(e.getPlayer().getGameMode() != GameMode.CREATIVE){
            ItemStack itemStack = e.getItemInHand().clone();
            itemStack.setAmount(amount);
            e.getPlayer().getInventory().removeItem(itemStack);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Island island = plugin.getGrid().getIslandAt(e.getBlock().getLocation());

        if(island == null)
            return;

        Location hologramLocation = e.getBlock().getLocation().add(0.5, 2, 0.5);
        Location blockLocation = e.getBlock().getLocation();

        if(!island.isStackedBlock(blockLocation))
            return;

        e.setCancelled(true);

        int stackAmount = island.getStackedBlockAmount(blockLocation);
        island.removeStackedBlock(blockLocation);
        island.handleBlockBreakment(e.getBlock(), stackAmount);

        //Let's set the block to air and drop the item
        ItemStack blockItem = e.getBlock().getState().getData().toItemStack(stackAmount);
        //e.getBlock().getWorld().dropItemNaturally(blockLocation, blockItem);
        dropStackedBlock(blockItem, blockLocation, stackAmount);
        e.getBlock().setType(Material.AIR);

        //Reduce durability by one from the tool
        if(isTool(e.getPlayer().getItemInHand())){
            ItemStack itemStack = e.getPlayer().getItemInHand();
            itemStack.setDurability((short) (itemStack.getDurability() + 1));
            e.getPlayer().setItemInHand(itemStack);
        }

        HologramUtils.destroyHologram(hologramLocation);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(EntityExplodeEvent e) {
        Island island = plugin.getGrid().getIslandAt(e.getLocation());

        if(island == null)
            return;

        List<Block> blockList = new ArrayList<>(e.blockList());

        for(Block block : blockList){
            if(!island.isStackedBlock(block.getLocation()))
                continue;

            int stackAmount = island.getStackedBlockAmount(block.getLocation());
            island.removeStackedBlock(block.getLocation());

            //Let's set the block to air and drop the item
            ItemStack blockItem = block.getState().getData().toItemStack(stackAmount);
            dropStackedBlock(blockItem, block.getLocation(), stackAmount);
            //block.getWorld().dropItemNaturally(block.getLocation(), blockItem);
            block.setType(Material.AIR);
            e.blockList().remove(block);

            HologramUtils.destroyHologram(block.getLocation().add(0.5, 2, 0.5));
        }
    }

    private Set<UUID> noOffHandPatch = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockInteract(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(noOffHandPatch.contains(e.getPlayer().getUniqueId())){
            e.setCancelled(true);
            return;
        }

        Island island = plugin.getGrid().getIslandAt(e.getClickedBlock().getLocation());

        if(island == null)
            return;

        Location hologramLocation = e.getClickedBlock().getLocation().add(0.5, 2, 0.5);
        Location blockLocation = e.getClickedBlock().getLocation();

        if(!island.isStackedBlock(blockLocation) || e.getItem() != null)
            return;

        if(!island.getMembers().contains(e.getPlayer().getUniqueId())){
            e.setCancelled(true);
            return;
        }

        noOffHandPatch.add(e.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> noOffHandPatch.remove(e.getPlayer().getUniqueId()), 5L);

        int stackAmount = island.getStackedBlockAmount(blockLocation);
        int breakAmount = e.getPlayer().isSneaking() ? stackAmount < 64 ? stackAmount : 64 : 1;
        island.handleBlockBreakment(e.getClickedBlock(), breakAmount);

        if(stackAmount - breakAmount > 1) {
            island.setStackedBlock(blockLocation, stackAmount - breakAmount);
            HologramUtils.updateHologram(hologramLocation, e.getClickedBlock());
        }else{
            island.removeStackedBlock(blockLocation);
            HologramUtils.destroyHologram(hologramLocation);
        }

        ItemStack blockItem = e.getClickedBlock().getState().getData().toItemStack(breakAmount);
        e.getClickedBlock().getWorld().dropItemNaturally(blockLocation.clone().add(0, 1, 0), blockItem);

        if(stackAmount - breakAmount <= 0){
            e.getClickedBlock().setType(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBugInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getItem() == null)
            return;

        if(!ListUtils.contains(plugin.getSuperiorSkyblock().getSettings().stackedBlockMaterials, StringUtils.getItemKey(e.getItem())))
            return;

        if(e.getClickedBlock().getType().isSolid())
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e){
        for(Block block : e.getBlocks()){
            Island island = plugin.getGrid().getIslandAt(block.getLocation());

            if(island == null || !island.isStackedBlock(block.getLocation()))
                continue;

            e.setCancelled(true);
            break;
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e){
        for(Block block : e.getBlocks()){
            Island island = plugin.getGrid().getIslandAt(block.getLocation());

            if(island == null || !island.isStackedBlock(block.getLocation()))
                continue;

            e.setCancelled(true);
            break;
        }
    }

    private boolean isTool(ItemStack itemStack){
        return itemStack != null && (itemStack.getType().name().contains("_PICKAXE") ||
                itemStack.getType().name().contains("_SPADE") || itemStack.getType().name().contains("_AXE"));
    }

    private void dropStackedBlock(ItemStack stackedBlock, Location location, int amount){
        int stackAmounts = amount / 64;
        int leftOvers = amount % 64;

        if(stackAmounts > 0){
            ItemStack cloneStack = stackedBlock.clone();
            cloneStack.setAmount(64);
            for(int i = 0; i < stackAmounts; i++){
                location.getWorld().dropItemNaturally(location, cloneStack);
            }
        }

        if(leftOvers > 0){
            ItemStack cloneStack = stackedBlock.clone();
            cloneStack.setAmount(leftOvers);
            location.getWorld().dropItemNaturally(location, cloneStack);
        }

    }

}
