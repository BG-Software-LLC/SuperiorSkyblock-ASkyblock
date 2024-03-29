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

import com.ome_r.superiorskyblock.legacy.LegacyArt;
import com.ome_r.superiorskyblock.legacy.LegacyBiome;
import com.ome_r.superiorskyblock.legacy.LegacyMaterial;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.Settings.GameType;
import com.wasteofplastic.askyblock.commands.IslandCmd;
import com.wasteofplastic.askyblock.nms.NMSAdapter;
import com.wasteofplastic.askyblock.util.Util;
import com.wasteofplastic.askyblock.util.VaultHelper;
import com.wasteofplastic.org.jnbt.ByteArrayTag;
import com.wasteofplastic.org.jnbt.ByteTag;
import com.wasteofplastic.org.jnbt.CompoundTag;
import com.wasteofplastic.org.jnbt.FloatTag;
import com.wasteofplastic.org.jnbt.IntTag;
import com.wasteofplastic.org.jnbt.ListTag;
import com.wasteofplastic.org.jnbt.NBTInputStream;
import com.wasteofplastic.org.jnbt.ShortTag;
import com.wasteofplastic.org.jnbt.StringTag;
import com.wasteofplastic.org.jnbt.Tag;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Schematic implements ISchematic {
    private ASkyBlock plugin;
    //private short[] blocks;
    //private byte[] data;
    private short width;
    private short length;
    private short height;
    private Map<BlockVector, Map<String, Tag>> tileEntitiesMap = new HashMap<BlockVector, Map<String, Tag>>();
    //private HashMap<BlockVector, EntityType> entitiesMap = new HashMap<BlockVector, EntityType>();
    private List<EntityObject> entitiesList = new ArrayList<EntityObject>();
    private File file;
    private String heading;
    private String name;
    private String perm;
    private String description;
    private int rating;
    private boolean useDefaultChest;
    private Material icon;    
    private Biome biome;
    private boolean usePhysics;
    private boolean pasteEntities;
    private boolean visible;
    private int order;
    // These hashmaps enable translation between WorldEdit strings and Bukkit names
    private List<EntityType> islandCompanion;
    private List<String> companionNames;
    private ItemStack[] defaultChestItems;
    // Name of a schematic this one is paired with
    private String partnerName;
    // Key blocks
    private Vector bedrock;
    private Vector chest;
    private Vector welcomeSign;
    private Vector topGrass;
    private Vector playerSpawn;
    private NMSAdapter nms;
    private Set<Integer> attachable = new HashSet<Integer>();
    private Map<String, Art> paintingList = new HashMap<String, Art>();
    private Map<Byte, BlockFace> facingList = new HashMap<Byte, BlockFace>();
    private Map<Byte, Rotation> rotationList = new HashMap<Byte, Rotation>();
    private List<IslandBlock> islandBlocks;
    private int durability;
    private int levelHandicap;
    private double cost;
    // The reason why this schematic is being pasted
    public enum PasteReason {
        /**
         * This is a new island
         */
        NEW_ISLAND,
        /**
         * This is a partner island
         */
        PARTNER,
        /**
         * This is a reset
         */
        RESET
    };

    public Schematic(ASkyBlock plugin) {
        this.plugin = plugin;
        // Initialize 
        name = "";
        heading = "";
        description = "Default Island";
        perm = "";
        icon = Material.MAP;
        rating = 50;
        useDefaultChest = true;	
        biome = Settings.defaultBiome;
        usePhysics = Settings.usePhysics;
        file = null;
        islandCompanion = new ArrayList<EntityType>();
        islandCompanion.add(Settings.islandCompanion);
        companionNames = Settings.companionNames;
        defaultChestItems = Settings.chestItems;
        visible = true;
        order = 0;
        bedrock = null;
        chest = null;
        welcomeSign = null;
        topGrass = null;
        playerSpawn = null;
        //playerSpawnBlock = null;
        partnerName = "";
    }

    public Schematic(ASkyBlock plugin, File file) throws IOException {
        this.plugin = plugin;
        // Initialize
        short[] blocks;
        byte[] data;
        name = file.getName();
        heading = "";
        description = "";
        perm = "";
        icon = Material.MAP;
        rating = 50;
        useDefaultChest = true;
        biome = Settings.defaultBiome;
        usePhysics = Settings.usePhysics;
        islandCompanion = new ArrayList<>();
        islandCompanion.add(Settings.islandCompanion);
        companionNames = Settings.companionNames;
        defaultChestItems = Settings.chestItems;
        pasteEntities = false;
        visible = true;
        order = 0;
        bedrock = null;
        chest = null;
        welcomeSign = null;
        topGrass = null;
        playerSpawn = null;
        partnerName = "";

        attachable.add(Material.STONE_BUTTON.getId());
        attachable.add(LegacyMaterial.OAK_BUTTON.getId());
        attachable.add(Material.COCOA.getId());
        attachable.add(Material.LADDER.getId());
        attachable.add(Material.LEVER.getId());
        attachable.add(LegacyMaterial.PISTON_HEAD.getId());
        attachable.add(LegacyMaterial.getRedstoneTorch(false).getId());
        attachable.add(LegacyMaterial.getRedstoneTorch(true).getId());
        attachable.add(Material.WALL_SIGN.getId());
        attachable.add(Material.TORCH.getId());
        attachable.add(LegacyMaterial.OAK_TRAPDOOR.getId());
        attachable.add(Material.TRIPWIRE_HOOK.getId());
        attachable.add(Material.VINE.getId());
        attachable.add(LegacyMaterial.OAK_DOOR.getId());
        attachable.add(Material.IRON_DOOR.getId());
        attachable.add(Material.RED_MUSHROOM.getId());
        attachable.add(Material.BROWN_MUSHROOM.getId());
        attachable.add(LegacyMaterial.NETHER_PORTAL.getId());

        // Painting list, useful to check if painting exsits or nor
        paintingList.put("Kebab", Art.KEBAB);
        paintingList.put("Aztec", Art.AZTEC);
        paintingList.put("Alban", Art.ALBAN);
        paintingList.put("Aztec2", Art.AZTEC2);
        paintingList.put("Bomb", Art.BOMB);
        paintingList.put("Plant", Art.PLANT);
        paintingList.put("Wasteland", Art.WASTELAND);
        paintingList.put("Wanderer", Art.WANDERER);
        paintingList.put("Graham", Art.GRAHAM);
        paintingList.put("Pool", Art.POOL);
        paintingList.put("Courbet", Art.COURBET);
        paintingList.put("Sunset", Art.SUNSET);
        paintingList.put("Sea", Art.SEA);
        paintingList.put("Creebet", Art.CREEBET);
        paintingList.put("Match", Art.MATCH);
        paintingList.put("Bust", Art.BUST);
        paintingList.put("Stage", Art.STAGE);
        paintingList.put("Void", Art.VOID);
        paintingList.put("SkullAndRoses", Art.SKULL_AND_ROSES);
        paintingList.put("Wither", Art.WITHER);
        paintingList.put("Fighters", Art.FIGHTERS);
        paintingList.put("Skeleton", Art.SKELETON);
        paintingList.put("DonkeyKong", LegacyArt.DONKEY_KONG);
        paintingList.put("Pointer", Art.POINTER);
        paintingList.put("Pigscene", Art.PIGSCENE);
        paintingList.put("BurningSkull", LegacyArt.BURNING_SKULL);

        facingList.put((byte) 0, BlockFace.SOUTH);
        facingList.put((byte) 1, BlockFace.WEST);
        facingList.put((byte) 2, BlockFace.NORTH);
        facingList.put((byte) 3, BlockFace.EAST);

        rotationList.put((byte) 0, Rotation.NONE);
        rotationList.put((byte) 2, Rotation.CLOCKWISE);
        rotationList.put((byte) 4, Rotation.FLIPPED);
        rotationList.put((byte) 6, Rotation.COUNTER_CLOCKWISE);

        if (!Bukkit.getServer().getVersion().contains("(MC: 1.7")) {
            rotationList.put((byte) 1, Rotation.CLOCKWISE_45);
            rotationList.put((byte) 3, Rotation.CLOCKWISE_135);
            rotationList.put((byte) 5, Rotation.FLIPPED_45);
            rotationList.put((byte) 7, Rotation.COUNTER_CLOCKWISE_45);
        }

        try {
            nms = Util.checkVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Establish the World Edit to Material look up
        // V1.8 items
        if (!Bukkit.getServer().getVersion().contains("(MC: 1.7")) {
            attachable.add(Material.IRON_TRAPDOOR.getId());
            attachable.add(LegacyMaterial.WHITE_WALL_BANNER.getId());
            attachable.add(Material.ACACIA_DOOR.getId());
            attachable.add(Material.BIRCH_DOOR.getId());
            attachable.add(Material.SPRUCE_DOOR.getId());
            attachable.add(Material.DARK_OAK_DOOR.getId());
            attachable.add(Material.JUNGLE_DOOR.getId());  
        }

        // Entities
        /*
	WEtoME.put("LAVASLIME", EntityType.MAGMA_CUBE);
	WEtoME.put("ENTITYHORSE", EntityType.HORSE);
	WEtoME.put("OZELOT", EntityType.OCELOT);
	WEtoME.put("MUSHROOMCOW", EntityType.MUSHROOM_COW);
	WEtoME.put("PIGZOMBIE", EntityType.PIG_ZOMBIE);
         */
        this.file = file;
        // Try to load the file
        try {
            FileInputStream stream = new FileInputStream(file);
            // InputStream is = new DataInputStream(new
            // GZIPInputStream(stream));
            NBTInputStream nbtStream = new NBTInputStream(stream);

            CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
            nbtStream.close();
            stream.close();
            if (!schematicTag.getName().equals("Schematic")) {
                throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
            }

            Map<String, Tag> schematic = schematicTag.getValue();

            Vector origin = new Vector(0,0,0);
            try {
                int originX = getChildTag(schematic, "WEOriginX", IntTag.class).getValue();
                int originY = getChildTag(schematic, "WEOriginY", IntTag.class).getValue();
                int originZ = getChildTag(schematic, "WEOriginZ", IntTag.class).getValue();
                Vector min = new Vector(originX, originY, originZ);
                origin = min.clone();
            } catch (Exception ignored) {}


            if (!schematic.containsKey("Blocks")) {
                throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
            }

            width = getChildTag(schematic, "Width", ShortTag.class).getValue();
            length = getChildTag(schematic, "Length", ShortTag.class).getValue();
            height = getChildTag(schematic, "Height", ShortTag.class).getValue();

            String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
            if (!materials.equals("Alpha")) {
                throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
            }

            byte[] blockId = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
            data = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
            byte[] addId = new byte[0];
            blocks = new short[blockId.length]; // Have to later combine IDs
            // We support 4096 block IDs using the same method as vanilla
            // Minecraft, where
            // the highest 4 bits are stored in a separate byte array.
            if (schematic.containsKey("AddBlocks")) {
                addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
            }

            // Combine the AddBlocks data with the first 8-bit block ID
            for (int index = 0; index < blockId.length; index++) {
                if ((index >> 1) >= addId.length) { // No corresponding
                    // AddBlocks index
                    blocks[index] = (short) (blockId[index] & 0xFF);
                } else {
                    if ((index & 1) == 0) {
                        blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
                    } else {
                        blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
                    }
                }
            }
            // Entities
            List<Tag> entities = getChildTag(schematic, "Entities", ListTag.class).getValue();
            for (Tag tag : entities) {
                if (!(tag instanceof CompoundTag))
                    continue;

                CompoundTag t = (CompoundTag) tag;
                EntityObject ent = new EntityObject();
                for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) { ;
                    if (entry.getKey().equals("id")) {
                        String id = ((StringTag)entry.getValue()).getValue().toUpperCase();
                        // The mob type might be prefixed with "Minecraft:"
                        if (id.startsWith("MINECRAFT:")) {
                            id = id.substring(10);
                        }
                        if (IslandBlock.WEtoME.containsKey(id)) {
                            ent.setType(IslandBlock.WEtoME.get(id));
                        } else if (!id.equalsIgnoreCase("ITEM")){
                            for (EntityType type : EntityType.values()) {
                                if (type.toString().equals(id)) {
                                    ent.setType(type);
                                    break;
                                }
                            }                            
                        }
                    }

                    if (entry.getKey().equals("Pos")) {
                        if (entry.getValue() instanceof ListTag) {
                            List<Tag> pos = ((ListTag) entry.getValue()).getValue();
                            if (pos.size() == 3) {                               
                                double x = (double)pos.get(0).getValue() - origin.getX();
                                double y = (double)pos.get(1).getValue() - origin.getY();
                                double z = (double)pos.get(2).getValue() - origin.getZ();
                                ent.setLocation(new BlockVector(x,y,z));
                            } else {
                                ent.setLocation(new BlockVector(0,0,0));
                            }
                        }
                    } else if (entry.getKey().equals("Motion")) {
                        if (entry.getValue() instanceof ListTag) {
                            List<Tag> pos = ((ListTag) entry.getValue()).getValue();
                            if (pos.size() == 3) {
                                ent.setMotion(new Vector((double)pos.get(0).getValue(), (double)pos.get(1).getValue()
                                        ,(double)pos.get(2).getValue()));
                            } else {
                                ent.setMotion(new Vector(0,0,0));
                            }
                        }
                    } else if (entry.getKey().equals("Rotation")) {
                        if (entry.getValue() instanceof ListTag) {
                            List<Tag> pos = ((ListTag) entry.getValue()).getValue();
                            if (pos.size() == 2) {
                                ent.setYaw((float)pos.get(0).getValue());
                                ent.setPitch((float)pos.get(1).getValue());
                            } else {
                                ent.setYaw(0F);
                                ent.setPitch(0F);
                            }
                        }
                    } else if (entry.getKey().equals("Color")) {
                        if (entry.getValue() instanceof ByteTag) {
                            ent.setColor(((ByteTag) entry.getValue()).getValue());
                        }
                    } else if (entry.getKey().equals("Sheared")) {
                        if (entry.getValue() instanceof ByteTag) {
                            if (((ByteTag) entry.getValue()).getValue() != (byte)0) {
                                ent.setSheared(true);
                            } else {
                                ent.setSheared(false);
                            }
                        }
                    } else if (entry.getKey().equals("RabbitType")) {
                        if (entry.getValue() instanceof IntTag) {
                            ent.setRabbitType(((IntTag)entry.getValue()).getValue());
                        }
                    } else if (entry.getKey().equals("Profession")) {
                        if (entry.getValue() instanceof IntTag) {
                            ent.setProfession(((IntTag)entry.getValue()).getValue());
                        }
                    } else if (entry.getKey().equals("CarryingChest")) {
                        if (entry.getValue() instanceof ByteTag) {
                            ent.setCarryingChest(((ByteTag) entry.getValue()).getValue());
                        }
                    } else if (entry.getKey().equals("OwnerUUID")) {
                        ent.setOwned(true);
                    } else if (entry.getKey().equals("CollarColor")) {
                        if (entry.getValue() instanceof ByteTag) {
                            ent.setCollarColor(((ByteTag) entry.getValue()).getValue());
                        }
                    } else if (entry.getKey().equals("Facing")) {
                        if (entry.getValue() instanceof ByteTag) {
                            ent.setFacing(((ByteTag) entry.getValue()).getValue());
                        }
                    } else if (entry.getKey().equals("Motive")) {
                        if (entry.getValue() instanceof StringTag) {
                            ent.setMotive(((StringTag) entry.getValue()).getValue());
                        }
                    } else if (entry.getKey().equals("ItemDropChance")) {
                        if (entry.getValue() instanceof FloatTag) {
                            ent.setItemDropChance(((FloatTag) entry.getValue()).getValue());
                        }
                    } else if (entry.getKey().equals("ItemRotation")) {
                        if (entry.getValue() instanceof ByteTag){
                            ent.setItemRotation(((ByteTag) entry.getValue()).getValue());
                        }
                    } else if (entry.getKey().equals("Item")) {
                        if (entry.getValue() instanceof CompoundTag) {
                            CompoundTag itemTag = (CompoundTag) entry.getValue();
                            for (Map.Entry<String, Tag> itemEntry : itemTag.getValue().entrySet()) {
                                if (itemEntry.getKey().equals("Count")){
                                    if (itemEntry.getValue() instanceof ByteTag){
                                        ent.setCount(((ByteTag) itemEntry.getValue()).getValue());
                                    }
                                } else if (itemEntry.getKey().equals("Damage")){
                                    if (itemEntry.getValue() instanceof ShortTag){
                                        ent.setDamage(((ShortTag) itemEntry.getValue()).getValue());
                                    }
                                } else if (itemEntry.getKey().equals("id")){
                                    if (itemEntry.getValue() instanceof StringTag){
                                        ent.setId(((StringTag) itemEntry.getValue()).getValue());
                                    }
                                } 
                            }
                        }
                    } else if (entry.getKey().equals("TileX")){
                        if (entry.getValue() instanceof IntTag){
                            ent.setTileX((double)((IntTag)entry.getValue()).getValue() - origin.getX());
                        }
                    } else if (entry.getKey().equals("TileY")){
                        if (entry.getValue() instanceof IntTag){
                            ent.setTileY((double)((IntTag)entry.getValue()).getValue() - origin.getY());
                        }
                    } else if (entry.getKey().equals("TileZ")){
                        if (entry.getValue() instanceof IntTag){
                            ent.setTileZ((double)((IntTag)entry.getValue()).getValue() - origin.getZ());
                        }
                    }
                }

                if (ent.getType() != null) {
                    entitiesList.add(ent);
                }
            }
            // Tile entities
            List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class).getValue();

            for (Tag tag : tileEntities) {
                if (!(tag instanceof CompoundTag))
                    continue;
                CompoundTag t = (CompoundTag) tag;

                int x = 0;
                int y = 0;
                int z = 0;

                Map<String, Tag> values = new HashMap<String, Tag>();

                for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
                    if (entry.getKey().equals("x")) {
                        if (entry.getValue() instanceof IntTag) {
                            x = ((IntTag) entry.getValue()).getValue();
                        }
                    } else if (entry.getKey().equals("y")) {
                        if (entry.getValue() instanceof IntTag) {
                            y = ((IntTag) entry.getValue()).getValue();
                        }
                    } else if (entry.getKey().equals("z")) {
                        if (entry.getValue() instanceof IntTag) {
                            z = ((IntTag) entry.getValue()).getValue();
                        }
                    }

                    values.put(entry.getKey(), entry.getValue());
                }

                BlockVector vec = new BlockVector(x, y, z);
                tileEntitiesMap.put(vec, values);
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("Could not load island schematic! Error in file.");
            e.printStackTrace();
            throw new IOException();
        }

        // Check for key blocks
        // Find top most bedrock - this is the key stone
        // Find top most chest
        // Find top most grass
        List<Vector> grassBlocks = new ArrayList<Vector>();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    if (blocks[index] == 7) {
                        // Last bedrock
                        if (bedrock == null || bedrock.getY() < y) {
                            bedrock = new Vector(x, y, z);
                        }
                    } else if (blocks[index] == 54) {
                        // Last chest
                        if (chest == null || chest.getY() < y) {
                            chest = new Vector(x, y, z);
                        }
                    } else if (blocks[index] == 63) {
                        // Sign
                        if (welcomeSign == null || welcomeSign.getY() < y) {
                            welcomeSign = new Vector(x, y, z);
                        }
                    } else if (blocks[index] == 2) {
                        // Grass
                        grassBlocks.add(new Vector(x,y,z));
                    } 
                }
            }
        }
        if (bedrock == null) {
            Bukkit.getLogger().severe("Schematic must have at least one bedrock in it!");
            throw new IOException();
        }
        // Find other key blocks
        if (!grassBlocks.isEmpty()) {
            // Sort by height
            List<Vector> sorted = new ArrayList<Vector>();
            for (Vector v : grassBlocks) {
                // Add to sorted list
                boolean inserted = false;
                for (int i = 0; i < sorted.size(); i++) {
                    if (v.getBlockY() > sorted.get(i).getBlockY()) {
                        sorted.add(i, v);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    // just add to the end of the list
                    sorted.add(v);
                }
            }
            topGrass = sorted.get(0);
        } else {
            topGrass = null;
        }

        // Preload the blocks
        prePasteSchematic(blocks, data);
    }

    /**
     * @return the biome
     */
    @Override
    public Biome getBiome() {
        return biome;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @return the heading
     */
    @Override
    public String getHeading() {
        return heading;
    }

    /**
     * @return the icon
     */
    @Override
    public Material getIcon() {
        return icon;
    }

    /**
     * @return the durability of the icon
     */
    @Override
    public int getDurability() {
        return durability;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the perm
     */
    @Override
    public String getPerm() {
        return perm;
    }

    /**
     * @return the rating
     */
    @Override
    public int getRating() {
        return rating;
    }

    /**
     * This method pastes a schematic.
     * @param loc - where to paste it
     * @param player - who for
     * @param teleport - should the player be teleported after pasting?
     * @param reason - why this was pasted
     */
    @Override
    public void pasteSchematic(final Location loc, final Player player, boolean teleport, final PasteReason reason) {
        // If this is not a file schematic, paste the default island
        if (this.file == null) {
            if (Settings.GAMETYPE == GameType.ACIDISLAND) {
                generateIslandBlocks(loc,player, reason);
            } else {
                loc.getBlock().setType(Material.BEDROCK);
                ASkyBlock.getPlugin().getLogger().severe("Missing schematic - using bedrock block only");
            }
            return;
        }
        World world = loc.getWorld();
        Location blockLoc = new Location(world, loc.getX(), loc.getY(), loc.getZ());
        blockLoc.subtract(bedrock);
        // Paste the island blocks
        for (IslandBlock b : islandBlocks) {
            b.paste(nms, blockLoc, this.usePhysics, biome);
        }
        // PASTE ENTS
        if (pasteEntities) {
            for (EntityObject ent : entitiesList) {
                if(ent.getTileX() != null && ent.getTileY() != null && ent.getTileZ() != null){
                    ent.setLocation(new BlockVector(ent.getTileX(),ent.getTileY(),ent.getTileZ()));
                }

                Location entitySpot = ent.getLocation().toLocation(blockLoc.getWorld()).add(blockLoc.toVector());
                entitySpot.setPitch(ent.getPitch());
                entitySpot.setYaw(ent.getYaw());
                if(ent.getType() == EntityType.PAINTING){

                    try {
                        Painting painting = blockLoc.getWorld().spawn(entitySpot, Painting.class);
                        if (painting != null) {
                            // Set default
                            painting.setArt(paintingList.getOrDefault(ent.getMotive(), Art.ALBAN), true);

                            // http://minecraft.gamepedia.com/Painting#Data_values
                            //set default direction
                            painting.setFacingDirection(facingList.getOrDefault(ent.getFacing(), BlockFace.NORTH), true);
                        }
                    } catch (IllegalArgumentException ignored) { }
                } else if(ent.getType() == EntityType.ITEM_FRAME) {

                    ItemFrame itemFrame = (ItemFrame) blockLoc.getWorld().spawnEntity(entitySpot, EntityType.ITEM_FRAME);
                    if (itemFrame != null) {
                        // Need to improve this shity fix ...
                        Material material = Material.matchMaterial(ent.getId().substring(10).toUpperCase());;

                        if(material == null && IslandBlock.WEtoM.containsKey(ent.getId().substring(10).toUpperCase())){
                            material = IslandBlock.WEtoM.get(ent.getId().substring(10).toUpperCase());
                        }

                        ItemStack item;

                        if(material != null){
                            if(ent.getCount() != null){
                                if(ent.getDamage() != null){
                                    item = new ItemStack(material, ent.getCount(), ent.getDamage());
                                } else {
                                    item = new ItemStack(material, ent.getCount(), (short) 0);
                                }
                            } else {
                                if(ent.getDamage() != null){
                                    item = new ItemStack(material, 1, ent.getDamage());
                                } else {
                                    item = new ItemStack(material, 1, (short) 0);
                                }
                            }
                        } else {
                            // Set to default content
                            item = new ItemStack(Material.STONE, 0, (short) 4);
                        }

                        ItemMeta itemMeta = item.getItemMeta();

                        // TODO: Implement methods to get enchantement, names, lore etc.

                        item.setItemMeta(itemMeta);
                        itemFrame.setItem(item);

                        //set default direction
                        itemFrame.setFacingDirection(facingList.getOrDefault(ent.getFacing(), BlockFace.NORTH), true);

                        // TODO: Implements code to handle the rotation of the item in the itemframe
                        // Set default direction
                        itemFrame.setRotation(rotationList.getOrDefault(ent.getItemRotation(), Rotation.NONE));
                    }
                } else {
                    Entity spawned = blockLoc.getWorld().spawnEntity(entitySpot, ent.getType());
                    if (spawned != null) {
                        spawned.setVelocity(ent.getMotion());
                        if (ent.getType() == EntityType.SHEEP) {
                            Sheep sheep = (Sheep)spawned;
                            if (ent.isSheared()) {   
                                sheep.setSheared(true);
                            }
                            DyeColor[] set = DyeColor.values();
                            sheep.setColor(set[ent.getColor()]);
                            sheep.setAge(ent.getAge());
                        } else if (ent.getType() == EntityType.HORSE) {
                            Horse horse = (Horse)spawned;
                            Horse.Color[] set = Horse.Color.values();
                            horse.setColor(set[ent.getColor()]);
                            horse.setAge(ent.getAge());
                            horse.setCarryingChest(ent.isCarryingChest());
                        } else if (ent.getType() == EntityType.VILLAGER) {
                            Villager villager = (Villager)spawned;
                            villager.setAge(ent.getAge());
                            Profession[] proffs = Profession.values();
                            villager.setProfession(proffs[ent.getProfession()]);
                        } else if (!Bukkit.getServer().getVersion().contains("(MC: 1.7") && ent.getType() == EntityType.RABBIT) {
                            Rabbit rabbit = (Rabbit)spawned;
                            Rabbit.Type[] set = Rabbit.Type.values();
                            rabbit.setRabbitType(set[ent.getRabbitType()]);
                            rabbit.setAge(ent.getAge());
                        } else if (ent.getType() == EntityType.OCELOT) {
                            Ocelot cat = (Ocelot)spawned;
                            if (ent.isOwned()) {
                                cat.setTamed(true);
                                cat.setOwner(player);
                            }
                            Ocelot.Type[] set = Ocelot.Type.values();
                            cat.setCatType(set[ent.getCatType()]);
                            cat.setAge(ent.getAge());
                            cat.setSitting(ent.isSitting());
                        } else if (ent.getType() == EntityType.WOLF) {
                            Wolf wolf = (Wolf)spawned;
                            if (ent.isOwned()) {
                                wolf.setTamed(true);
                                wolf.setOwner(player);
                            }
                            wolf.setAge(ent.getAge());
                            wolf.setSitting(ent.isSitting());
                            DyeColor[] color = DyeColor.values();
                            wolf.setCollarColor(color[ent.getCollarColor()]);
                        }
                    }
                }
            }
        }
        // Find the grass spot
        final Location grass;
        if (topGrass != null) {
            Location gr = topGrass.clone().toLocation(loc.getWorld()).subtract(bedrock);
            gr.add(loc.toVector());
            gr.add(new Vector(0.5D,1.1D,0.5D)); // Center of block and a bit up so the animal drops a bit
            grass = gr;
        } else {
            grass = null;
        }	

        Block blockToChange;
        // Place a helpful sign in front of player
        if (welcomeSign != null) {
            Vector ws = welcomeSign.clone().subtract(bedrock);
            ws.add(loc.toVector());
            blockToChange = ws.toLocation(world).getBlock();
            BlockState signState = blockToChange.getState();
            if (signState instanceof Sign) {
                Sign sign = (Sign) signState;
                if (sign.getLine(0).isEmpty()) {
                    sign.setLine(0, plugin.myLocale(player.getUniqueId()).signLine1.replace("[player]", player.getName()));
                }
                if (sign.getLine(1).isEmpty()) {
                    sign.setLine(1, plugin.myLocale(player.getUniqueId()).signLine2.replace("[player]", player.getName()));
                }
                if (sign.getLine(2).isEmpty()) {
                    sign.setLine(2, plugin.myLocale(player.getUniqueId()).signLine3.replace("[player]", player.getName()));
                }
                if (sign.getLine(3).isEmpty()) {
                    sign.setLine(3, plugin.myLocale(player.getUniqueId()).signLine4.replace("[player]", player.getName()));
                }
                sign.update(true, false);
            }
        }
        if (chest != null) {
            Vector ch = chest.clone().subtract(bedrock);
            ch.add(loc.toVector());
            // Place the chest - no need to use the safe spawn function because we
            // know what this island looks like
            blockToChange = ch.toLocation(world).getBlock();
            if (useDefaultChest) {
                // Fill the chest
                if (blockToChange.getType() == Material.CHEST) {
                    final Chest islandChest = (Chest) blockToChange.getState();
                    DoubleChest doubleChest = null;
                    InventoryHolder iH = islandChest.getInventory().getHolder();
                    if (iH instanceof DoubleChest) {
                        doubleChest = (DoubleChest) iH;
                    }
                    if (doubleChest != null) {
                        Inventory inventory = doubleChest.getInventory();
                        inventory.clear();
                        inventory.setContents(defaultChestItems);
                    } else {
                        Inventory inventory = islandChest.getInventory();
                        inventory.clear();
                        inventory.setContents(defaultChestItems);
                    }
                }
            }
        }

        if (teleport) {
            plugin.getPlayers().setInTeleport(player.getUniqueId(), true);
            // Check distance. If it's too close, warp to spawn to try to clear the client's cache
            if (player.getWorld().equals(world)) {
                int distSq = (int)((player.getLocation().distanceSquared(loc) - ((double)Settings.islandDistance * Settings.islandDistance)/16));
                if (plugin.getServer().getViewDistance() * plugin.getServer().getViewDistance() < distSq) {
                    player.teleport(world.getSpawnLocation());
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                plugin.getGrid().homeTeleport(player);
                plugin.getPlayers().setInTeleport(player.getUniqueId(), false);
                // Reset any inventory, etc. This is done AFTER the teleport because other plugins may switch out inventory based on world
                plugin.resetPlayer(player);
                // Reset money if required
                if (Settings.resetMoney) {
                    resetMoney(player);
                }
                // Show fancy titles!
                if (!Bukkit.getServer().getVersion().contains("(MC: 1.7")) {
                    if (!plugin.myLocale(player.getUniqueId()).islandSubTitle.isEmpty()) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                "minecraft:title " + player.getName() + " subtitle {\"text\":\"" + plugin.myLocale(player.getUniqueId()).islandSubTitle.replace("[player]", player.getName()) + "\", \"color\":\"" + plugin.myLocale(player.getUniqueId()).islandSubTitleColor + "\"}");
                    }
                    if (!plugin.myLocale(player.getUniqueId()).islandTitle.isEmpty()) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                "minecraft:title " + player.getName() + " title {\"text\":\"" + plugin.myLocale(player.getUniqueId()).islandTitle.replace("[player]", player.getName()) + "\", \"color\":\"" + plugin.myLocale(player.getUniqueId()).islandTitleColor + "\"}");
                    }
                    if (!plugin.myLocale(player.getUniqueId()).islandDonate.isEmpty() && !plugin.myLocale(player.getUniqueId()).islandURL.isEmpty()) {
                        plugin.getServer().dispatchCommand(
                                plugin.getServer().getConsoleSender(),
                                "minecraft:tellraw " + player.getName() + " {\"text\":\"" + plugin.myLocale(player.getUniqueId()).islandDonate.replace("[player]", player.getName()) + "\",\"color\":\"" + plugin.myLocale(player.getUniqueId()).islandDonateColor + "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\""
                                        + plugin.myLocale(player.getUniqueId()).islandURL + "\"}}");
                    }
                }
                if (reason.equals(PasteReason.NEW_ISLAND)) {
                    // Run any commands that need to be run at the start
                    if (!player.hasPermission(Settings.PERMPREFIX + "command.newexempt")) {
                        IslandCmd.runCommands(Settings.startCommands, player);
                    }
                } else if (reason.equals(PasteReason.RESET)) {
                    // Run any commands that need to be run at reset
                    if (!player.hasPermission(Settings.PERMPREFIX + "command.resetexempt")) {
                        IslandCmd.runCommands(Settings.resetCommands, player);
                    }
                }

            }, 10L);

        }
        if (!islandCompanion.isEmpty() && grass != null) {
            Bukkit.getServer().getScheduler().runTaskLater(ASkyBlock.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    spawnCompanion(player, grass);
                }
            }, 40L);
        }
        // Set the bedrock block meta data to the original spawn location
        // Doesn't survive a server restart. TODO: change to add this info elsewhere.
    }
    /**
     * This method prepares to pastes a schematic.
     * @param blocks
     * @param data
     */
    private void prePasteSchematic(short[] blocks, byte[] data) {
        islandBlocks = new ArrayList<>();
        Map<BlockVector, Map<String, Tag>> tileEntitiesMap = this.tileEntitiesMap;
        // Start with non-attached blocks
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    // Only bother if this block is above ground zero and 
                    // only bother with air if it is below sea level
                    // TODO: need to check max world height too?
                    int h = Settings.islandHeight + y - bedrock.getBlockY();
                    if (h >= 0 && h < 255 && (blocks[index] != 0 || h < Settings.seaHeight)){
                        // Only bother if the schematic blocks are within the range that y can be
                        IslandBlock block = new IslandBlock(x, y, z);
                        if (!attachable.contains((int)blocks[index]) || blocks[index] == 179) {
                            if (Bukkit.getServer().getVersion().contains("(MC: 1.7") && blocks[index] == 179) {
                                // Red sandstone - use red sand instead
                                block.setBlock(12, (byte)1);
                            } else {
                                block.setBlock(blocks[index], data[index]);
                            }
                            // Tile Entities
                            if (tileEntitiesMap.containsKey(new BlockVector(x, y, z))) {
                                if (plugin.isOnePointEight()) {
                                    if (block.getTypeId() == LegacyMaterial.WHITE_WALL_BANNER.getId()) {
                                        block.setBanner(tileEntitiesMap.get(new BlockVector(x, y, z)));
                                    }
                                    else if (block.getTypeId() == LegacyMaterial.SKULL.getId()) {
                                        block.setSkull(tileEntitiesMap.get(new BlockVector(x, y, z)), block.getData());
                                    }
                                    else if (block.getTypeId() == Material.FLOWER_POT.getId()) {
                                        block.setFlowerPot(tileEntitiesMap.get(new BlockVector(x, y, z)));
                                    }
                                }
                                // Monster spawner blocks
                                if (block.getTypeId() == LegacyMaterial.SPAWNER.getId()) {
                                    block.setSpawnerType(tileEntitiesMap.get(new BlockVector(x, y, z)));
                                } else if ((block.getTypeId() == LegacyMaterial.SIGN_WALL.getId())) {
                                    block.setSign(tileEntitiesMap.get(new BlockVector(x, y, z)));
                                } else if (block.getTypeId() == Material.CHEST.getId()
                                        || block.getTypeId() == Material.TRAPPED_CHEST.getId()
                                        || block.getTypeId() == Material.FURNACE.getId()
                                        || block.getTypeId() == LegacyMaterial.BURNING_FURNACE.getId()
                                        || block.getTypeId() == Material.DISPENSER.getId()
                                        || block.getTypeId() == Material.HOPPER.getId()
                                        || block.getTypeId() == Material.DROPPER.getId()
                                        || block.getTypeId() == LegacyMaterial.CHEST_MINECART.getId()
                                        || block.getTypeId() == Material.HOPPER_MINECART.getId()
                                        || block.getTypeId() == LegacyMaterial.FURNACE_MINECART.getId()
                                        || Material.getMaterial(block.getTypeId()).name().contains("SHULKER_BOX")
                                        ) {
                                    block.setChest(nms, tileEntitiesMap.get(new BlockVector(x, y, z)));
                                } 
                            }
                            islandBlocks.add(block);
                        }
                    }
                }
            }
        }
        // Second pass - just paste attachables and deal with chests etc.
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int h = Settings.islandHeight + y - bedrock.getBlockY();
                    if (h >= 0 && h < 255){
                        int index = y * width * length + z * width + x;
                        IslandBlock block = new IslandBlock(x, y, z);
                        if (attachable.contains((int)blocks[index])) {
                            block.setBlock(blocks[index], data[index]);
                            // Tile Entities
                            if (tileEntitiesMap.containsKey(new BlockVector(x, y, z))) {
                                if (plugin.isOnePointEight()) {
                                    if (block.getTypeId() == LegacyMaterial.WHITE_WALL_BANNER.getId()) {
                                        block.setBanner(tileEntitiesMap.get(new BlockVector(x, y, z)));
                                    }
                                }
                                // Wall Sign
                                if (block.getTypeId() == Material.WALL_SIGN.getId()) {
                                    block.setSign(tileEntitiesMap.get(new BlockVector(x, y, z)));
                                }
                            }
                            islandBlocks.add(block);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param biome the biome to set
     */
    @Override
    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    /**
     * @param description the description to set
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param heading the heading to set
     */
    @Override
    public void setHeading(String heading) {
        this.heading = heading;
    }

    @Override
    public void setIcon(Material icon, int damage) {
        this.icon = icon;
        this.durability = damage;    
    }
    /**
     * @param icon the icon to set
     */
    @Override
    public void setIcon(Material icon) {
        this.icon = icon;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param perm the perm to set
     */
    @Override
    public void setPerm(String perm) {
        this.perm = perm;
    }

    /**
     * @param rating the rating to set
     */
    @Override
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * @param useDefaultChest the useDefaultChest to set
     */
    @Override
    public void setUseDefaultChest(boolean useDefaultChest) {
        this.useDefaultChest = useDefaultChest;
    }

    /**
     * @param usePhysics the usePhysics to set
     */
    @Override
    public void setUsePhysics(boolean usePhysics) {
        this.usePhysics = usePhysics;
    }


    /**
     * Removes all the air blocks if they are not to be pasted.
     * @param pasteAir the pasteAir to set
     */
    @Override
    public void setPasteAir(boolean pasteAir) {
        if (!pasteAir) {
            islandBlocks.removeIf(b -> b.getTypeId() == 0);
        }
        
    }

    /**
     * Creates the AcidIsland default island block by block
     * @param islandLoc
     * @param player
     * @param reason 
     */
    @Override
    public void generateIslandBlocks(final Location islandLoc, final Player player, PasteReason reason) {
        // AcidIsland
        // Build island layer by layer
        // Start from the base
        // half sandstone; half sand
        int x = islandLoc.getBlockX();
        int z = islandLoc.getBlockZ();
        World world = islandLoc.getWorld();
        int y = 0;
        for (int x_space = x - 4; x_space <= x + 4; x_space++) {
            for (int z_space = z - 4; z_space <= z + 4; z_space++) {
                final Block b = world.getBlockAt(x_space, y, z_space);
                b.setType(Material.BEDROCK);
                b.setBiome(biome);
            }
        }
        for (y = 1; y < Settings.islandHeight + 5; y++) {
            for (int x_space = x - 4; x_space <= x + 4; x_space++) {
                for (int z_space = z - 4; z_space <= z + 4; z_space++) {
                    final Block b = world.getBlockAt(x_space, y, z_space);
                    if (y < (Settings.islandHeight / 2)) {
                        b.setType(Material.SANDSTONE);
                    } else {
                        b.setType(Material.SAND);
                        b.getState().setRawData((byte) 0);
                    }
                }
            }
        }
        // Then cut off the corners to make it round-ish
        for (y = 0; y < Settings.islandHeight + 5; y++) {
            for (int x_space = x - 4; x_space <= x + 4; x_space += 8) {
                for (int z_space = z - 4; z_space <= z + 4; z_space += 8) {
                    final Block b = world.getBlockAt(x_space, y, z_space);
                    b.setType(Material.WATER);
                }
            }
        }
        // Add some grass
        for (y = Settings.islandHeight + 4; y < Settings.islandHeight + 5; y++) {
            for (int x_space = x - 2; x_space <= x + 2; x_space++) {
                for (int z_space = z - 2; z_space <= z + 2; z_space++) {
                    final Block blockToChange = world.getBlockAt(x_space, y, z_space);
                    blockToChange.setType(Material.GRASS);
                }
            }
        }
        // Place bedrock - MUST be there (ensures island are not
        // overwritten
        Block b = world.getBlockAt(x, Settings.islandHeight, z);
        b.setType(Material.BEDROCK);
        // Then add some more dirt in the classic shape
        y = Settings.islandHeight + 3;
        for (int x_space = x - 2; x_space <= x + 2; x_space++) {
            for (int z_space = z - 2; z_space <= z + 2; z_space++) {
                b = world.getBlockAt(x_space, y, z_space);
                b.setType(Material.DIRT);
            }
        }
        b = world.getBlockAt(x - 3, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x + 3, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z - 3);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z + 3);
        b.setType(Material.DIRT);
        y = Settings.islandHeight + 2;
        for (int x_space = x - 1; x_space <= x + 1; x_space++) {
            for (int z_space = z - 1; z_space <= z + 1; z_space++) {
                b = world.getBlockAt(x_space, y, z_space);
                b.setType(Material.DIRT);
            }
        }
        b = world.getBlockAt(x - 2, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x + 2, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z - 2);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z + 2);
        b.setType(Material.DIRT);
        y = Settings.islandHeight + 1;
        b = world.getBlockAt(x - 1, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x + 1, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z - 1);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z + 1);
        b.setType(Material.DIRT);

        // Add island items
        y = Settings.islandHeight;
        // Add tree (natural)
        final Location treeLoc = new Location(world, x, y + 5D, z);
        world.generateTree(treeLoc, TreeType.ACACIA);
        // Place the cow
        final Location location = new Location(world, x, (Settings.islandHeight + 5), z - 2);

        // Place a helpful sign in front of player
        Block blockToChange = world.getBlockAt(x, Settings.islandHeight + 5, z + 3);
        blockToChange.setType(LegacyMaterial.SIGN_WALL);
        Sign sign = (Sign) blockToChange.getState();
        sign.setLine(0, ASkyBlock.getPlugin().myLocale(player.getUniqueId()).signLine1.replace("[player]", player.getName()));
        sign.setLine(1, ASkyBlock.getPlugin().myLocale(player.getUniqueId()).signLine2.replace("[player]", player.getName()));
        sign.setLine(2, ASkyBlock.getPlugin().myLocale(player.getUniqueId()).signLine3.replace("[player]", player.getName()));
        sign.setLine(3, ASkyBlock.getPlugin().myLocale(player.getUniqueId()).signLine4.replace("[player]", player.getName()));
        ((org.bukkit.material.Sign) sign.getData()).setFacingDirection(BlockFace.NORTH);
        sign.update(true, false);
        // Place the chest - no need to use the safe spawn function
        // because we
        // know what this island looks like
        blockToChange = world.getBlockAt(x, Settings.islandHeight + 5, z + 1);
        blockToChange.setType(Material.CHEST);
        // Only set if the config has items in it
        if (Settings.chestItems.length > 0) {
            final InventoryHolder chest = (InventoryHolder) blockToChange.getState();
            final Inventory inventory = chest.getInventory();
            //inventory.clear();
            inventory.setContents(Settings.chestItems);
        }
        // Fill the chest and orient it correctly (1.8 faces it north!
        DirectionalContainer dc = (DirectionalContainer) blockToChange.getState().getData();
        dc.setFacingDirection(BlockFace.SOUTH);
        blockToChange.getState().setRawData(dc.getData());
        // Teleport player
        plugin.getGrid().homeTeleport(player);
        // Reset any inventory, etc. This is done AFTER the teleport because other plugins may switch out inventory based on world
        plugin.resetPlayer(player);
        // Reset money if required
        if (Settings.resetMoney) {
            resetMoney(player);
        }
        // Show fancy titles!
        if (!Bukkit.getServer().getVersion().contains("(MC: 1.7")) {
            if (!plugin.myLocale(player.getUniqueId()).islandSubTitle.isEmpty()) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                        "minecraft:title " + player.getName() + " subtitle {\"text\":\"" + plugin.myLocale(player.getUniqueId()).islandSubTitle.replace("[player]", player.getName()) + "\", \"color\":\"" + plugin.myLocale(player.getUniqueId()).islandSubTitleColor + "\"}");
            }
            if (!plugin.myLocale(player.getUniqueId()).islandTitle.isEmpty()) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                        "minecraft:title " + player.getName() + " title {\"text\":\"" + plugin.myLocale(player.getUniqueId()).islandTitle.replace("[player]", player.getName()) + "\", \"color\":\"" + plugin.myLocale(player.getUniqueId()).islandTitleColor + "\"}");
            }
            if (!plugin.myLocale(player.getUniqueId()).islandDonate.isEmpty() && !plugin.myLocale(player.getUniqueId()).islandURL.isEmpty()) {
                plugin.getServer().dispatchCommand(
                        plugin.getServer().getConsoleSender(),
                        "minecraft:tellraw " + player.getName() + " {\"text\":\"" + plugin.myLocale(player.getUniqueId()).islandDonate.replace("[player]", player.getName()) + "\",\"color\":\"" + plugin.myLocale(player.getUniqueId()).islandDonateColor + "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\""
                                + plugin.myLocale(player.getUniqueId()).islandURL + "\"}}");
            }
        }
        if (reason.equals(PasteReason.NEW_ISLAND)) {
            // Run any commands that need to be run at the start
            if (!player.hasPermission(Settings.PERMPREFIX + "command.newexempt")) {
                IslandCmd.runCommands(Settings.startCommands, player);
            }
        } else if (reason.equals(PasteReason.RESET)) {
            // Run any commands that need to be run at reset
            if (!player.hasPermission(Settings.PERMPREFIX + "command.resetexempt")) {
                IslandCmd.runCommands(Settings.resetCommands, player);
            }
        }
        if (!islandCompanion.isEmpty()) {
            Bukkit.getServer().getScheduler().runTaskLater(ASkyBlock.getPlugin(), () -> spawnCompanion(player, location), 40L);
        }
    }
    /**
     * Get child tag of a NBT structure.
     * 
     * @param items
     *            The parent tag map
     * @param key
     *            The name of the tag to get
     * @param expected
     *            The expected type of the tag
     * @return child tag casted to the expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws IllegalArgumentException {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
    /**
     * Spawns a random companion for the player with a random name at the location given
     * @param player
     * @param location
     */
    protected void spawnCompanion(Player player, Location location) {
        // Older versions of the server require custom names to only apply to Living Entities
        if (!islandCompanion.isEmpty() && location != null) {
            Random rand = new Random();
            int randomNum = rand.nextInt(islandCompanion.size());
            EntityType type = islandCompanion.get(randomNum);
            if (type != null) {
                LivingEntity companion = (LivingEntity) location.getWorld().spawnEntity(location, type);
                if (!companionNames.isEmpty()) {
                    randomNum = rand.nextInt(companionNames.size());
                    String name = companionNames.get(randomNum).replace("[player]", player.getName());
                    companion.setCustomName(name);
                    companion.setCustomNameVisible(true);
                } 
            }
        }
    }

    /**
     * @param islandCompanion the islandCompanion to set
     */
    @Override
    public void setIslandCompanion(List<EntityType> islandCompanion) {
        this.islandCompanion = islandCompanion;
    }

    /**
     * @param companionNames the companionNames to set
     */
    @Override
    public void setCompanionNames(List<String> companionNames) {
        this.companionNames = companionNames;
    }

    /**
     * @param defaultChestItems the defaultChestItems to set
     */
    @Override
    public void setDefaultChestItems(ItemStack[] defaultChestItems) {
        this.defaultChestItems = defaultChestItems;
    }

    /**
     * @return if Biome is HELL, this is true
     */
    @Override
    public boolean isInNether() {
        return biome == LegacyBiome.NETHER;
    }

    /**
     * @return the partnerName
     */
    @Override
    public String getPartnerName() {
        return partnerName;
    }

    /**
     * @param partnerName the partnerName to set
     */
    @Override
    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    /**
     * @return the pasteEntities
     */
    @Override
    public boolean isPasteEntities() {
        return pasteEntities;
    }

    /**
     * @param pasteEntities the pasteEntities to set
     */
    @Override
    public void setPasteEntities(boolean pasteEntities) {
        this.pasteEntities = pasteEntities;
    }

    /**
     * Whether the schematic is visible or not
     * @return the visible
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets if the schematic can be seen in the schematics GUI or not by the player
     * @param visible the visible to set
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the order
     */
    @Override
    public int getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    @Override
    public void setOrder(int order) {
        this.order = order;
    }


    /**
     * @return true if player spawn exists in this schematic
     */
    @Override
    public boolean isPlayerSpawn() {
        if (playerSpawn == null) {
            return false;
        }
        return true;
    }

    /**
     * @return the playerSpawn Location given a paste location
     */
    @Override
    public Location getPlayerSpawn(Location pasteLocation) {
        return pasteLocation.clone().add(playerSpawn);
    }

    /**
     * @param playerSpawnBlock the playerSpawnBlock to set
     * @return true if block is found otherwise false
     */
    @Override
    public boolean setPlayerSpawnBlock(Material playerSpawnBlock) {
        if (bedrock == null) {
            return false;
        }
        playerSpawn = null;
        // Run through the schematic and try and find the spawnBlock
        for (IslandBlock islandBlock : islandBlocks) {
            if (islandBlock.getTypeId() == playerSpawnBlock.getId()) {
                playerSpawn = islandBlock.getVector().subtract(bedrock).add(new Vector(0.5D,-1D,0.5D));
                // Set the block to air
                islandBlock.setTypeId((short)0);
                return true;
            }
        }
        return false;
    }


    /**
     * @return the levelHandicap
     */
    @Override
    public int getLevelHandicap() {
        return levelHandicap;
    }

    /**
     * @param levelHandicap the levelHandicap to set
     */
    @Override
    public void setLevelHandicap(int levelHandicap) {
        this.levelHandicap = levelHandicap;
    }

    /**
     * Set the cost
     * @param cost
     */
    @Override
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * @return the cost
     */
    @Override
    public double getCost() {
        return cost;
    }

    private void resetMoney(Player player) {
        if (!Settings.useEconomy) {
            return;
        }
        // Set player's balance in acid island to the starting balance
        try {
            if (VaultHelper.econ == null) {
                VaultHelper.setupEconomy();
            }
            Double playerBalance = VaultHelper.econ.getBalance(player, Settings.worldName);
            // Round the balance to 2 decimal places and slightly down to
            // avoid issues when withdrawing the amount later
            BigDecimal bd = new BigDecimal(playerBalance);
            bd = bd.setScale(2, RoundingMode.HALF_DOWN);
            playerBalance = bd.doubleValue();
            if (!playerBalance.equals(Settings.startingMoney)) {
                if (playerBalance > Settings.startingMoney) {
                    Double difference = playerBalance - Settings.startingMoney;
                    EconomyResponse response = VaultHelper.econ.withdrawPlayer(player, Settings.worldName, difference);
                    if (response.transactionSuccess()) {
                        plugin.getLogger().info(
                                "FYI:" + player.getName() + " had " + VaultHelper.econ.format(playerBalance) + " when they typed /island and it was set to "
                                        + Settings.startingMoney);
                    } else {
                        plugin.getLogger().warning(
                                "Problem trying to withdraw " + playerBalance + " from " + player.getName() + "'s account when they typed /island!");
                        plugin.getLogger().warning("Error from economy was: " + response.errorMessage);
                    }
                } else {
                    Double difference = Settings.startingMoney - playerBalance;
                    EconomyResponse response = VaultHelper.econ.depositPlayer(player, Settings.worldName, difference);
                    if (response.transactionSuccess()) {
                        plugin.getLogger().info(
                                "FYI:" + player.getName() + " had " + VaultHelper.econ.format(playerBalance) + " when they typed /island and it was set to "
                                        + Settings.startingMoney);
                    } else {
                        plugin.getLogger().warning(
                                "Problem trying to deposit " + playerBalance + " from " + player.getName() + "'s account when they typed /island!");
                        plugin.getLogger().warning("Error from economy was: " + response.errorMessage);
                    }

                }
            }
        } catch (final Exception e) {
            plugin.getLogger().severe("Error trying to zero " + player.getName() + "'s account when they typed /island!");
            plugin.getLogger().severe(e.getMessage());
        }

    }
}