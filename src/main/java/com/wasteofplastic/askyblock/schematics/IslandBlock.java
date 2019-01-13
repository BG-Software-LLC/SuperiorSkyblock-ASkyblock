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

package com.wasteofplastic.askyblock.schematics;

import com.ome_r.superiorskyblock.legacy.LegacyMaterial;
import com.wasteofplastic.askyblock.nms.NMSAdapter;
import com.wasteofplastic.org.jnbt.CompoundTag;
import com.wasteofplastic.org.jnbt.ListTag;
import com.wasteofplastic.org.jnbt.StringTag;
import com.wasteofplastic.org.jnbt.Tag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IslandBlock {
    private short typeId;
    private byte data;
    private int x;
    private int y;
    private int z;
    private List<String> signText;
    private BannerBlock banner;
    private SkullBlock skull;
    private PotBlock pot;
    private EntityType spawnerBlockType;
    // Chest contents
    private final Map<Byte,ItemStack> chestContents = new HashMap<>();
    protected static final Map<String, Material> WEtoM = new HashMap<>();
    protected static final Map<String, EntityType> WEtoME = new HashMap<>();

    static {
        // Establish the World Edit to Material look up
        // V1.8 items
        if (!Bukkit.getServer().getVersion().contains("(MC: 1.7")) {

            WEtoM.put("ARMORSTAND",Material.ARMOR_STAND);
            WEtoM.put("ACACIA_DOOR", LegacyMaterial.getDoor("ACACIA"));
            WEtoM.put("BIRCH_DOOR", LegacyMaterial.getDoor("BIRCH"));
            WEtoM.put("BIRCH_STAIRS", LegacyMaterial.getStairs("BIRCH"));
            WEtoM.put("DARK_OAK_DOOR", LegacyMaterial.getDoor("DARK_OAK"));
            WEtoM.put("JUNGLE_DOOR", LegacyMaterial.getDoor("JUNGLE"));
            WEtoM.put("SLIME",Material.SLIME_BLOCK);
            WEtoM.put("SPRUCE_DOOR", LegacyMaterial.getDoor("SPRUCE"));
        }
        WEtoM.put("BREWING_STAND", LegacyMaterial.BREWING_STAND);
        WEtoM.put("CARROT_ON_A_STICK", LegacyMaterial.CARROT_ON_A_STICK);
        WEtoM.put("CARROT", LegacyMaterial.CARROT);
        WEtoM.put("CAULDRON", LegacyMaterial.CAULDRON);
        WEtoM.put("CHEST_MINECART", LegacyMaterial.CHEST_MINECART);
        WEtoM.put("CLOCK", LegacyMaterial.CLOCK);
        WEtoM.put("COBBLESTONE_WALL", LegacyMaterial.COBBLESTONE_WALL);
        WEtoM.put("COMMAND_BLOCK", LegacyMaterial.COMMAND_BLOCK);
        WEtoM.put("COMMANDBLOCK_MINECART", LegacyMaterial.COMMANDBLOCK_MINECART);
        WEtoM.put("COMPARATOR", LegacyMaterial.COMPARATOR);
        WEtoM.put("COOKED_PORKCHOP", LegacyMaterial.COOKED_PORKCHOP);
        WEtoM.put("CRAFTING_TABLE", LegacyMaterial.CRAFTING_TABLE);
        WEtoM.put("DIAMOND_HORSE_ARMOR", LegacyMaterial.DIAMOND_HORSE_ARMOR);
        WEtoM.put("DIAMOND_SHOVEL", LegacyMaterial.DIAMOND_SHOVEL);
        WEtoM.put("DYE", LegacyMaterial.INK_SAC);
        WEtoM.put("ENCHANTING_TABLE", LegacyMaterial.ENCHANTING_TABLE); //1.11 rename
        WEtoM.put("END_PORTAL_FRAME", LegacyMaterial.END_PORTAL_FRAME);
        WEtoM.put("END_PORTAL", LegacyMaterial.END_PORTAL); // 1.11 rename
        WEtoM.put("END_STONE", LegacyMaterial.END_STONE);
        WEtoM.put("EXPERIENCE_BOTTLE", LegacyMaterial.EXPERIENCE_BOTTLE);
        WEtoM.put("FILLED_MAP", LegacyMaterial.FILLED_MAP);
        WEtoM.put("FIRE_CHARGE", LegacyMaterial.FIRE_CHARGE);
        WEtoM.put("FIREWORKS", LegacyMaterial.FIREWORKS);
        WEtoM.put("FLOWER_POT", LegacyMaterial.FLOWER_POT);
        WEtoM.put("GLASS_PANE", LegacyMaterial.GLASS_PANE);
        WEtoM.put("GOLDEN_CHESTPLATE", LegacyMaterial.GOLDEN_CHESTPLATE);
        WEtoM.put("GOLDEN_HORSE_ARMOR", LegacyMaterial.GOLDEN_HORSE_ARMOR);
        WEtoM.put("GOLDEN_LEGGINGS", LegacyMaterial.GOLDEN_LEGGINGS);
        WEtoM.put("GOLDEN_PICKAXE", LegacyMaterial.GOLDEN_PICKAXE);
        WEtoM.put("GOLDEN_RAIL", Material.POWERED_RAIL);
        WEtoM.put("GOLDEN_SHOVEL", LegacyMaterial.GOLDEN_SHOVEL);
        WEtoM.put("GOLDEN_SWORD", LegacyMaterial.GOLDEN_SWORD);
        WEtoM.put("GOLDEN_HELMET", LegacyMaterial.GOLDEN_HELMET);
        WEtoM.put("GOLDEN_HOE", LegacyMaterial.GOLDEN_HOE);
        WEtoM.put("GOLDEN_AXE", LegacyMaterial.GOLDEN_AXE);
        WEtoM.put("GOLDEN_BOOTS", LegacyMaterial.GOLDEN_BOOTS);
        WEtoM.put("GUNPOWDER", LegacyMaterial.GUNPOWDER);
        WEtoM.put("HARDENED_CLAY", LegacyMaterial.HARDENED_CLAY);
        WEtoM.put("HEAVY_WEIGHTED_PRESSURE_PLATE", LegacyMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE);
        WEtoM.put("IRON_BARS", LegacyMaterial.IRON_BARS);
        WEtoM.put("IRON_HORSE_ARMOR", LegacyMaterial.IRON_HORSE_ARMOR);
        WEtoM.put("IRON_SHOVEL", LegacyMaterial.IRON_SHOVEL);
        WEtoM.put("LEAD", LegacyMaterial.LEAD);
        WEtoM.put("LEAVES2", LegacyMaterial.OAK_LEAVES);
        WEtoM.put("LIGHT_WEIGHTED_PRESSURE_PLATE", LegacyMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE);
        WEtoM.put("LOG2", LegacyMaterial.OAK_LOG);
        WEtoM.put("MAP", LegacyMaterial.MAP);
        WEtoM.put("MYCELIUM", LegacyMaterial.MYCELIUM);
        WEtoM.put("NETHER_BRICK_FENCE", LegacyMaterial.NETHER_BRICK_FENCE);
        WEtoM.put("NETHER_WART", LegacyMaterial.NETHER_WART);
        WEtoM.put("NETHERBRICK", LegacyMaterial.NETHERBRICK);
        WEtoM.put("OAK_STAIRS", LegacyMaterial.OAK_STAIRS);
        WEtoM.put("PISTON", LegacyMaterial.PISTON);
        WEtoM.put("PLANKS", LegacyMaterial.PLANKS);
        WEtoM.put("POTATO",  LegacyMaterial.POTATO);
        WEtoM.put("RAIL", LegacyMaterial.RAIL);
        WEtoM.put("RECORD_11", LegacyMaterial.MUSIC_DISC_11);
        WEtoM.put("RECORD_13", LegacyMaterial.MUSIC_DISC_13);
        WEtoM.put("RECORD_BLOCKS", LegacyMaterial.MUSIC_DISC_BLOCKS);
        WEtoM.put("RECORD_CAT", LegacyMaterial.MUSIC_DISC_CAT);
        WEtoM.put("RECORD_CHIRP", LegacyMaterial.MUSIC_DISC_CHIRP);
        WEtoM.put("RECORD_FAR", LegacyMaterial.MUSIC_DISC_FAR);
        WEtoM.put("RECORD_MALL", LegacyMaterial.MUSIC_DISC_MALL);
        WEtoM.put("RECORD_MELLOHI", LegacyMaterial.MUSIC_DISC_MELLOHI);
        WEtoM.put("RECORD_STAL", LegacyMaterial.MUSIC_DISC_STAL);
        WEtoM.put("RECORD_STRAD", LegacyMaterial.MUSIC_DISC_STRAD);
        WEtoM.put("RECORD_WAIT", LegacyMaterial.MUSIC_DISC_WAIT);
        WEtoM.put("RECORD_WARD", LegacyMaterial.MUSIC_DISC_WARD);
        WEtoM.put("RED_FLOWER", LegacyMaterial.RED_FLOWER);
        WEtoM.put("REEDS",Material.SUGAR_CANE);
        WEtoM.put("REPEATER", LegacyMaterial.REPEATER);
        WEtoM.put("SKULL",  LegacyMaterial.SKULL);
        WEtoM.put("SPAWN_EGG", LegacyMaterial.PIG_SPAWN_EGG);
        WEtoM.put("STICKY_PISTON", LegacyMaterial.STICKY_PISTON);
        WEtoM.put("STONE_BRICK_STAIRS",Material.BRICK_STAIRS);
        //WEtoM.put("STONE_BRICK_STAIRS",Material.SMOOTH_STAIRS);
        WEtoM.put("STONE_SHOVEL", LegacyMaterial.STONE_SHOVEL);
        WEtoM.put("STONE_SLAB", LegacyMaterial.STONE_SLAB);
        WEtoM.put("STONE_STAIRS",Material.COBBLESTONE_STAIRS);
        WEtoM.put("TNT_MINECART", LegacyMaterial.TNT_MINECART);
        WEtoM.put("WATERLILY", LegacyMaterial.WATERLILY);
        WEtoM.put("WHEAT_SEEDS", LegacyMaterial.WHEAT_SEEDS);
        WEtoM.put("WOODEN_AXE", LegacyMaterial.WOODEN_AXE);
        WEtoM.put("WOODEN_BUTTON", LegacyMaterial.OAK_BUTTON);
        WEtoM.put("WOODEN_DOOR", LegacyMaterial.OAK_DOOR);
        WEtoM.put("WOODEN_HOE", LegacyMaterial.WOODEN_HOE);
        WEtoM.put("WOODEN_PICKAXE", LegacyMaterial.WOODEN_PICKAXE);
        WEtoM.put("WOODEN_PRESSURE_PLATE", LegacyMaterial.WOODEN_PRESSURE_PLATE);
        WEtoM.put("WOODEN_SHOVEL", LegacyMaterial.WOODEN_SHOVEL);
        WEtoM.put("WOODEN_SLAB", LegacyMaterial.OAK_SLAB);
        WEtoM.put("WOODEN_SWORD", LegacyMaterial.WOODEN_SWORD);
        WEtoM.put("MUSHROOM_STEW", LegacyMaterial.MUSHROOM_STEW);
        // Entities
        WEtoME.put("LAVASLIME", EntityType.MAGMA_CUBE);
        WEtoME.put("ENTITYHORSE", EntityType.HORSE);
        WEtoME.put("OZELOT", EntityType.OCELOT);
        WEtoME.put("MUSHROOMCOW", EntityType.MUSHROOM_COW);
        WEtoME.put("MOOSHROOM", EntityType.MUSHROOM_COW); // 1.11 rename
        WEtoME.put("PIGZOMBIE", EntityType.PIG_ZOMBIE);
        WEtoME.put("ZOMBIE_PIGMAN", EntityType.PIG_ZOMBIE); // 1.11 rename
        WEtoME.put("CAVESPIDER", EntityType.CAVE_SPIDER);
        WEtoME.put("XPORB", EntityType.EXPERIENCE_ORB);
        WEtoME.put("XP_ORB", EntityType.EXPERIENCE_ORB); // 1.11 rename
        WEtoME.put("MINECARTRIDEABLE", EntityType.MINECART);
        WEtoME.put("MINECARTHOPPER", EntityType.MINECART_HOPPER);
        WEtoME.put("HOPPER_MINECART", EntityType.MINECART_HOPPER);
        WEtoME.put("MINECARTFURNACE", EntityType.MINECART_FURNACE);
        WEtoME.put("FURNACE_MINECART", EntityType.MINECART_FURNACE);
        WEtoME.put("MINECARTMOBSPAWNER", EntityType.MINECART_MOB_SPAWNER);
        WEtoME.put("SPAWNER_MINECART", EntityType.MINECART_MOB_SPAWNER); // 1.11 rename
        WEtoME.put("MINECARTTNT", EntityType.MINECART_TNT);
        WEtoME.put("TNT_MINECART", EntityType.MINECART_TNT); // 1.11
        WEtoME.put("LEASH_KNOT",EntityType.LEASH_HITCH); // 1.11
        WEtoME.put("MINECARTCHEST", EntityType.MINECART_CHEST);
        WEtoME.put("CHEST_MINECART", EntityType.MINECART_CHEST); //1.11 rename
        WEtoME.put("VILLAGERGOLEM", EntityType.IRON_GOLEM);
        WEtoME.put("ENDERDRAGON", EntityType.ENDER_DRAGON);
        WEtoME.put("PAINTING", EntityType.PAINTING);
        WEtoME.put("ITEMFRAME", EntityType.ITEM_FRAME);
        if (!Bukkit.getServer().getVersion().contains("(MC: 1.7")) {
            WEtoME.put("ENDERCRYSTAL", EntityType.ENDER_CRYSTAL);
            WEtoME.put("ARMORSTAND", EntityType.ARMOR_STAND);
        }
        // 1.10 entities and materials
        if (!Bukkit.getServer().getVersion().contains("(MC: 1.7") && !Bukkit.getServer().getVersion().contains("(MC: 1.8") && !Bukkit.getServer().getVersion().contains("(MC: 1.9")) {
            WEtoME.put("POLARBEAR", EntityType.POLAR_BEAR);
            WEtoM.put("ENDER_CRYSTAL", Material.END_CRYSTAL); // 1.11
        }
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public IslandBlock(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        signText = null;
        banner = null;
        skull = null;
        pot = null;
        spawnerBlockType = null;
    }
    /**
     * @return the type
     */
    public int getTypeId() {
        return typeId;
    }
    /**
     * @param type the type to set
     */
    public void setTypeId(short type) {
        this.typeId = type;
    }
    /**
     * @return the data
     */
    public int getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(byte data) {
        this.data = data;
    }

    /**
     * @return the signText
     */
    public List<String> getSignText() {
        return signText;
    }
    /**
     * @param signText the signText to set
     */
    public void setSignText(List<String> signText) {
        this.signText = signText;
    }

    /**
     * @param s
     * @param b
     */
    public void setBlock(int s, byte b) {
        this.typeId = (short)s;
        this.data = b;
    }

    /**
     * Sets this block up with all the banner data required
     * @param map
     */
    public void setBanner(Map<String, Tag> map) {
        banner = new BannerBlock();
        banner.prep(map);
    }
    /**
     * Sets this block up with all the skull data required
     * @param map
     * @param dataValue
     */
    public void setSkull(Map<String, Tag> map, int dataValue) {
        skull = new SkullBlock();
        skull.prep(map, dataValue);
    }
    public void setFlowerPot(Map<String, Tag> map){
        pot = new PotBlock();
        pot.prep(map);
    }

    /**
     * Sets the spawner type if this block is a spawner
     * @param tileData
     */
    public void setSpawnerType(Map<String, Tag> tileData) {
        String creatureType = "";        
        if (tileData.containsKey("EntityId")) {
            creatureType = ((StringTag) tileData.get("EntityId")).getValue().toUpperCase();
        } else if (tileData.containsKey("SpawnData")) {
            // 1.9 format
            Map<String,Tag> spawnData = ((CompoundTag) tileData.get("SpawnData")).getValue();
            if (spawnData.containsKey("id")) {
                creatureType = ((StringTag) spawnData.get("id")).getValue().toUpperCase();
            }
        }
        // The mob type might be prefixed with "Minecraft:"
        if (creatureType.startsWith("MINECRAFT:")) {
            creatureType = creatureType.substring(10);
        }
        if (WEtoME.containsKey(creatureType)) {
            spawnerBlockType = WEtoME.get(creatureType);
        } else {
            try {
                spawnerBlockType = EntityType.valueOf(creatureType);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Spawner setting of " + creatureType + " is unknown for this server. Skipping.");
            }
        }
    }

    /**
     * Sets this block's sign data
     * @param tileData
     */
    public void setSign(Map<String, Tag> tileData) {
        signText = new ArrayList<>();
        List<String> text = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            String line = ((StringTag) tileData.get("Text" + String.valueOf(i))).getValue();
            // This value can actually be a string that says null sometimes.
            if (line.equalsIgnoreCase("null")) {
                line = "";
            }
            text.add(line);
        }

        JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = new ContainerFactory(){
            public List creatArrayContainer() {
                return new LinkedList();
            }

            public Map createObjectContainer() {
                return new LinkedHashMap();
            }

        };
        // This just removes all the JSON formatting and provides the raw text
        for (int line = 0; line < 4; line++) {
            StringBuilder lineText = new StringBuilder();
            if (!text.get(line).equals("\"\"") && !text.get(line).isEmpty()) {
                if (text.get(line).startsWith("{")) {
                    // JSON string
                    try {

                        Map json = (Map)parser.parse(text.get(line), containerFactory);
                        List list = (List) json.get("extra");
                        if (list != null) {
                            Iterator iter = list.iterator();
                            while(iter.hasNext()){
                                Object next = iter.next();
                                String format = JSONValue.toJSONString(next);
                                // This doesn't see right, but appears to be the easiest way to identify this string as JSON...
                                if (format.startsWith("{")) {
                                    // JSON string
                                    Map jsonFormat = (Map)parser.parse(format, containerFactory);
                                    Iterator formatIter = jsonFormat.entrySet().iterator();
                                    while (formatIter.hasNext()) {
                                        Map.Entry entry = (Map.Entry)formatIter.next();
                                        String key = entry.getKey().toString();
                                        String value = entry.getValue().toString();
                                        if (key.equalsIgnoreCase("color")) {
                                            try {
                                                lineText.append(ChatColor.valueOf(value.toUpperCase()));
                                            } catch (Exception noColor) {
                                                Bukkit.getLogger().warning("Unknown color " + value +" in sign when pasting schematic, skipping...");
                                            }
                                        } else if (key.equalsIgnoreCase("text")) {
                                            lineText.append(value);
                                        } else {
                                            // Formatting - usually the value is always true, but check just in case
                                            if (key.equalsIgnoreCase("obfuscated") && value.equalsIgnoreCase("true")) {
                                                lineText.append(ChatColor.MAGIC);
                                            } else if (key.equalsIgnoreCase("underlined") && value.equalsIgnoreCase("true")) {
                                                lineText.append(ChatColor.UNDERLINE);
                                            } else {
                                                // The rest of the formats
                                                try {
                                                    lineText.append(ChatColor.valueOf(key.toUpperCase()));
                                                } catch (Exception noFormat) {
                                                    // Ignore
                                                    Bukkit.getLogger().warning("Unknown format " + value +" in sign when pasting schematic, skipping...");
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // This is unformatted text. It is included in "". A reset is required to clear
                                    // any previous formatting
                                    if (format.length()>1) {
                                        lineText.append(ChatColor.RESET).append(format.substring(format.indexOf('"') + 1, format.lastIndexOf('"')));
                                    }
                                } 
                            }
                        } else {
                            // No extra tag
                            json = (Map)parser.parse(text.get(line), containerFactory);
                            String value = (String) json.get("text");
                            lineText.append(value);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    // This is unformatted text (not JSON). It is included in "".
                    if (text.get(line).length() > 1) {
                        try {
                            lineText = new StringBuilder(text.get(line).substring(text.get(line).indexOf('"') + 1, text.get(line).lastIndexOf('"')));
                        } catch (Exception e) {
                            //There may not be those "'s, so just use the raw line
                            lineText = new StringBuilder(text.get(line));
                        }
                    } else {
                        // just in case it isn't - show the raw line
                        lineText = new StringBuilder(text.get(line));
                    }
                }
            }
            signText.add(lineText.toString());
        }
    }

    public void setBook(Map<String, Tag> tileData) {
        Bukkit.getLogger().info(tileData.toString());
    }

    public void setChest(NMSAdapter nms, Map<String, Tag> tileData) {
        try {
            ListTag chestItems = (ListTag) tileData.get("Items");
            if (chestItems != null) {
                //int number = 0;
                for (Tag item : chestItems.getValue()) {
                    // Format for chest items is:
                    // id = short value of item id
                    // Damage = short value of item damage
                    // Count = the number of items
                    // Slot = the slot in the chest
                    // inventory

                    if (item instanceof CompoundTag) {
                        try {
                            // Id is a number
                            short itemType = (Short) ((CompoundTag) item).getValue().get("id").getValue();
                            short itemDamage = (Short) ((CompoundTag) item).getValue().get("Damage").getValue();
                            byte itemAmount = (Byte) ((CompoundTag) item).getValue().get("Count").getValue();
                            byte itemSlot = (Byte) ((CompoundTag) item).getValue().get("Slot").getValue();
                            ItemStack chestItem = new ItemStack(itemType, itemAmount, itemDamage);
                            chestContents.put(itemSlot, chestItem);
                        } catch (ClassCastException ex) {
                            // Id is a material
                            String itemType = (String) ((CompoundTag) item).getValue().get("id").getValue();
                            try {
                                // Get the material
                                if (itemType.startsWith("minecraft:")) {
                                    String material = itemType.substring(10).toUpperCase();
                                    // Special case for non-standard material names
                                    Material itemMaterial;

                                    if (WEtoM.containsKey(material)) {
                                        itemMaterial = WEtoM.get(material);
                                    } else {
                                        itemMaterial = Material.valueOf(material);
                                    }
                                    short itemDamage = (Short) ((CompoundTag) item).getValue().get("Damage").getValue();
                                    byte itemAmount = (Byte) ((CompoundTag) item).getValue().get("Count").getValue();
                                    byte itemSlot = (Byte) ((CompoundTag) item).getValue().get("Slot").getValue();
                                    ItemStack chestItem = new ItemStack(itemMaterial, itemAmount, itemDamage);
                                    if (itemMaterial.equals(Material.WRITTEN_BOOK)) {
                                        chestItem = nms.setBook(item);
                                    }
                                    // Check for potions
                                    if (itemMaterial.toString().contains("POTION")) {
                                        chestItem = nms.setPotion(itemMaterial, item, chestItem);
                                    }
                                    chestContents.put(itemSlot, chestItem);
                                }
                            } catch (Exception exx) {
                                Bukkit.getLogger().severe(
                                        "Could not parse item [" + itemType.substring(10).toUpperCase() + "] in schematic - skipping!");
                                exx.printStackTrace();
                            }

                        }

                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Could not parse schematic file item, skipping!");
        }
    }


    /**
     * Paste this block at blockLoc
     * @param nms
     * @param blockLoc
     */
    public void paste(NMSAdapter nms, Location blockLoc, boolean usePhysics, Biome biome) {
        // Only paste air if it is below the sea level and in the overworld
        Block block = new Location(blockLoc.getWorld(), x, y, z).add(blockLoc).getBlock();
        block.setBiome(biome);
        nms.setBlockSuperFast(block, typeId, data, usePhysics);
        if (signText != null) {
            if (block.getState().getRawData() != typeId) {
                block.getState().setRawData((byte) typeId);
            }
            // Sign
            Sign sign = (Sign) block.getState();
            int index = 0;
            for (String line : signText) {
                sign.setLine(index++, line);
            }
            sign.update(true, false);
        } else if (banner != null) {
            banner.set(block);
        } else if (skull != null){
            skull.set(block);
        } else if (pot != null){
            pot.set(nms, block);
        } else if (spawnerBlockType != null) {
            if (block.getState().getRawData() != typeId) {
                block.getState().setRawData((byte) typeId);
            }
            CreatureSpawner cs = (CreatureSpawner)block.getState();
            cs.setSpawnedType(spawnerBlockType);
            cs.update(true, false);
        } else if (!chestContents.isEmpty()) {
            if (block.getState().getRawData() != typeId) {
                block.getState().setRawData((byte) typeId);
            }
            // Check if this is a double chest
            
            InventoryHolder chestBlock = (InventoryHolder) block.getState();
            if (chestBlock instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) chestBlock;
                for (ItemStack chestItem: chestContents.values()) {
                    doubleChest.getInventory().addItem(chestItem);
                }
            } else {
                // Single chest
                for (Entry<Byte, ItemStack> en : chestContents.entrySet()) {
                    chestBlock.getInventory().setItem(en.getKey(), en.getValue());
                }
            }
        }
    }

    /**
     * @return Vector for where this block is in the schematic
     */
    public Vector getVector() {
        return new Vector(x,y,z);
    }
}
