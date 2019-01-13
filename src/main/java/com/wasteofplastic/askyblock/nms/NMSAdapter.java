/*******************************************************************************
 * This file is part of ASkyBlock.
 *
 *     ASkyBlock is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ASkyBlock is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ASkyBlock.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package com.wasteofplastic.askyblock.nms;

import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.org.jnbt.Tag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMSAdapter {

    /**
     * Update the low-level chunk information for the given block to the new block ID and data.  This
     * change will not be propagated to clients until the chunk is refreshed to them.
     * @param block
     * @param blockId
     * @param data
     * @param applyPhysics
     */
    void setBlockSuperFast(Block block, int blockId, byte data, boolean applyPhysics);

    ItemStack setBook(Tag item);

    /**
     * Sets a block to be an item stack
     * @param block
     * @param itemStack
     */
    void setFlowerPotBlock(Block block, ItemStack itemStack);

    boolean isPotion(ItemStack item);

    /**
     * Returns a potion ItemStack
     * @param itemMaterial 
     * @param itemTag
     * @param chestItem
     * @return
     */
    ItemStack setPotion(Material itemMaterial, Tag itemTag, ItemStack chestItem);
    
    /**
     * Gets a monster egg itemstack
     * @param type
     * @param amount
     * @return itemstack
     */
    ItemStack getSpawnEgg(EntityType type, int amount);

    void setWorldBorder(Player player, Island island);

    void pasteIsland(Location location, String islandName);

    void saveIsland(Location corner1, Location corner2, String islandName);

    int getSpawnerDelay(CreatureSpawner creatureSpawner);

}
