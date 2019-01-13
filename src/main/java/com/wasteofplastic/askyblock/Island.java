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

package com.wasteofplastic.askyblock;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.ome_r.superiorskyblock.legacy.LegacyMaterial;
import com.ome_r.superiorskyblock.objects.WrappedLocation;
import com.ome_r.superiorskyblock.upgrades.MultiplierUpgrade;
import com.ome_r.superiorskyblock.upgrades.Upgrade;
import com.ome_r.superiorskyblock.utils.ListUtils;
import com.ome_r.superiorskyblock.utils.StringUtils;
import com.wasteofplastic.askyblock.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores all the info about an island
 * Managed by GridManager
 *
 * @author tastybento
 *
 */
@SerializableAs("Island")
public class Island implements ConfigurationSerializable {

    private static ASkyBlock plugin = ASkyBlock.getPlugin();

    private int minX;
    private int minZ;
    private int minProtectedX;
    private int minProtectedZ;
    private int protectionRange;
    private int y;
    private WrappedLocation center;
    private World world;
    private UUID owner;
    private long createdDate;
    private long updatedDate;
    private String password;
    private int votes;
    private int islandDistance;
    private boolean locked = false;
    private boolean isSpawn = false;
    private boolean purgeProtected;
    private WrappedLocation spawnPoint;
    private Multiset<Material> tileEntityCount = HashMultiset.create();
    private Biome biome;

    //SuperiorSkyblock
    private int hoppersAmount;
    private Map<String, Integer> upgradeLevels;
    private Map<String, Integer> blocksCounter;
    private Map<WrappedLocation, Integer> stackedBlocks = new HashMap<>();
    private String paypal, discord;
    private long worth;

    // Island protection settings
    private static final List<String> islandSettingsKey = new ArrayList<>();

    static {
        islandSettingsKey.clear();
        islandSettingsKey.add("");
    }

    private HashMap<SettingsFlag, Boolean> igs = new HashMap<>();
    private int levelHandicap;

    public Island(YamlConfiguration cfg){
        protectionRange = cfg.getInt("protection-range");
        islandDistance = cfg.getInt("island-distance");

        int x = cfg.getInt("center.x"), z = cfg.getInt("center.z");

        minX = x - (islandDistance / 2);
        y = cfg.getInt("center.y");
        minZ = z - (islandDistance / 2);
        minProtectedX = x - (protectionRange / 2);
        minProtectedZ = z - (protectionRange / 2);
        world = Bukkit.getWorld(Settings.worldName);
        center = new WrappedLocation(world, x, y, z);
        createdDate = new Date().getTime();
        updatedDate = createdDate;
        password = "";
        votes = 0;
        locked = cfg.getBoolean("locked");
        purgeProtected = cfg.getBoolean("purge-protected");

        owner = null;

        if(!cfg.getString("owner").equals("null")){
            if(cfg.getString("owner").equals("spawn")){
                isSpawn = true;
                if(cfg.contains("spawn-point")) {
                    spawnPoint = new WrappedLocation(Util.getLocationString(cfg.getString("spawn-point")));
                }
            }else{
                owner = UUID.fromString(cfg.getString("owner"));
            }
        }
        biome = Biome.valueOf(cfg.getString("biome"));
        levelHandicap = cfg.getInt("level");
        hoppersAmount = cfg.getInt("hoppers-amount");
        upgradeLevels = new HashMap<>();
        //New save method
        for (String upgradeInfo : cfg.getStringList("upgrades")) {
            upgradeLevels.put(upgradeInfo.split(";")[0], Integer.valueOf(upgradeInfo.split(";")[1]));
        }
        blocksCounter = new HashMap<>();
        if(cfg.contains("counted-blocks")){
            for(String key : cfg.getStringList("counted-blocks")){
                blocksCounter.put(key.split(";")[0], Integer.valueOf(key.split(";")[1]));
            }
        }
        worth = cfg.getLong("blocks-calculation");
        paypal = cfg.getString("paypal", "");
        discord = cfg.getString("discord", "");

        setStackedBlocks(cfg.getStringList("stacked-blocks"));
        setSettings(cfg.getStringList("settings"));
    }

    public Island(Map<String, Object> args){
        protectionRange = (int) args.get("protection-range");
        minX = ((int) args.get("center.x")) - islandDistance / 2;
        islandDistance = (int) args.get("island-distance");

        int x = (int) args.get("center.x"), z = (int) args.get("center.z");

        minX = ((int) args.get("center.x")) - islandDistance / 2;
        y = (int) args.get("center.y");
        minZ = ((int) args.get("center.z")) - islandDistance / 2;
        minProtectedX = x - protectionRange / 2;
        minProtectedZ = z - protectionRange / 2;
        world = Bukkit.getWorld(Settings.worldName);
        center = new WrappedLocation(world, x, y, z);

        createdDate = new Date().getTime();
        updatedDate = createdDate;
        password = "";
        votes = 0;
        locked = (boolean) args.get("locked");
        purgeProtected = (boolean) args.get("purge-protected");

        owner = null;
        if(!args.get("owner").equals("null")){
            if(args.get("owner").equals("spawn")){
                isSpawn = true;
                if(args.containsKey("spawn-point"))
                    spawnPoint = new WrappedLocation(Util.getLocationString((String) args.get("spawn-point")));
            }else{
                owner = UUID.fromString((String) args.get("owner"));
            }
        }

        biome = Biome.valueOf((String) args.get("biome"));
        levelHandicap = (int) args.get("level");
        hoppersAmount = (int) args.get("hoppers-amount");
        upgradeLevels = new HashMap<>();
        //Old save method
        if(args.containsKey("hoppers-level"))
            upgradeLevels.put("hoppers-level", (int) args.get("hoppers-level"));
        if(args.containsKey("spawn-rate"))
            upgradeLevels.put("spawn-rate", (int) args.get("spawn-rate"));
        if(args.containsKey("mob-drops"))
            upgradeLevels.put("mob-drops", (int) args.get("mob-drops"));
        if(args.containsKey("crop-growth"))
            upgradeLevels.put("crop-growth", (int) args.get("crop-growth"));
        //New save method
        if(args.containsKey("upgrades")) {
            for (String upgradeInfo : (List<String>) args.get("upgrades")) {
                upgradeLevels.put(upgradeInfo.split(";")[0], Integer.valueOf(upgradeInfo.split(";")[1]));
            }
        }
        blocksCounter = new HashMap<>();
        if(args.containsKey("counted-blocks")){
            for(String key : (List<String>) args.get("counted-blocks")){
                blocksCounter.put(key.split(";")[0], Integer.valueOf(key.split(";")[1]));
            }
        }
        worth = (int) args.get("blocks-calculation");
        paypal = "";
        discord = "";

        setStackedBlocks((List<String>) args.get("stacked-blocks"));
        setSettings((List<String>) args.get("settings"));
    }

    public Island(ASkyBlock plugin, String serial, List<String> settingsKey) {
        // Bukkit.getLogger().info("DEBUG: adding serialized island to grid ");
        // Deserialize
        // Format:
        // x:height:z:protection range:island distance:owner UUID: locked: protected
        String[] split = serial.split(":");
        try {
            protectionRange = Integer.parseInt(split[3]);
            islandDistance = Integer.parseInt(split[4]);
            int x = Integer.parseInt(split[0]);
            int z = Integer.parseInt(split[2]);
            minX = x - islandDistance / 2;
            y = Integer.parseInt(split[1]);
            minZ = z - islandDistance / 2;
            minProtectedX = x - protectionRange / 2;
            minProtectedZ = z - protectionRange / 2;
            this.world = ASkyBlock.getIslandWorld();
            this.center = new WrappedLocation(world, x, y, z);
            this.createdDate = new Date().getTime();
            this.updatedDate = createdDate;
            this.password = "";
            this.votes = 0;
            if (split.length > 6) {
                // Bukkit.getLogger().info("DEBUG: " + split[6]);
                // Get locked status
                this.locked = split[6].equalsIgnoreCase("true");
                // Bukkit.getLogger().info("DEBUG: " + locked);
            } else {
                this.locked = false;
            }
            // Check if deletable
            if (split.length > 7) {
                this.purgeProtected = split[7].equalsIgnoreCase("true");
            } else {
                this.purgeProtected = false;
            }
            if (!split[5].equals("null")) {
                if (split[5].equals("spawn")) {
                    isSpawn = true;
                    // Try to get the spawn point
                    if (split.length > 8) {
                        //plugin.getLogger().info("DEBUG: " + serial.substring(serial.indexOf(":SP:") + 4));
                        spawnPoint = new WrappedLocation(Util.getLocationString(serial.substring(serial.indexOf(":SP:") + 4)));
                    }
                } else {
                    owner = UUID.fromString(split[5]);
                }
            }
            // Check if protection options there
            if (split.length > 8) {
                setSettings(split[8], settingsKey);
            } else {
                setSettings(null, settingsKey);
            }

            // Get the biome
            if (split.length > 9) {
                try {
                    biome = Biome.valueOf(split[9]);

                } catch (IllegalArgumentException ee) {
                    // Unknown biome
                }
            }
            // Get island level handicap
            if (split.length > 10) {
                try {
                    this.levelHandicap = Integer.valueOf(split[10]);
                } catch (Exception e) {
                    this.levelHandicap = 0;
                }
            }

            this.hoppersAmount = 0;
            blocksCounter = new HashMap<>();
            this.upgradeLevels = new HashMap<>();
            this.worth = 0;
            paypal = "";
            discord = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new island using the island center method
     */
    public Island(ASkyBlock plugin, int x, int z) {
        this(plugin, x, z, null);
    }

    public Island(ASkyBlock plugin, int x, int z, UUID owner) {
        // Calculate min minX and z
        this.minX = x - Settings.islandDistance / 2;
        this.minZ = z - Settings.islandDistance / 2;
        this.minProtectedX = x - Settings.islandProtectionRange / 2;
        this.minProtectedZ = z - Settings.islandProtectionRange / 2;
        this.y = Settings.islandHeight;
        this.islandDistance = Settings.islandDistance;
        this.protectionRange = Settings.islandProtectionRange;
        this.world = ASkyBlock.getIslandWorld();
        this.center = new WrappedLocation(world, x, y, z);
        this.createdDate = new Date().getTime();
        this.updatedDate = createdDate;
        this.password = "";
        this.votes = 0;
        this.owner = owner;
        this.hoppersAmount = 0;
        this.upgradeLevels = new HashMap<>();
        blocksCounter = new HashMap<>();
        this.worth = 0;
        paypal = "";
        discord = "";
        // Island Guard Settings
        setIgsDefaults();
    }

    /**
     * Used for copying the island
     */
    public Island(Island island) {
        this.biome = island.biome == null ? null : Biome.valueOf(island.biome.name());
        this.center = island.center;
        this.createdDate = island.createdDate;
        island.igs.forEach((k,v) -> this.igs.put(k, v));
        this.islandDistance = island.islandDistance;
        this.isSpawn = island.isSpawn;
        this.locked = island.locked;
        this.levelHandicap = island.levelHandicap;
        this.minProtectedX = island.minProtectedX;
        this.minProtectedZ = island.minProtectedZ;
        this.minX = island.minX;
        this.minZ = island.minZ;
        this.owner = island.owner == null ? null : UUID.fromString(island.owner.toString());
        this.password = island.password;
        this.protectionRange = island.protectionRange;
        this.purgeProtected = island.purgeProtected;
        this.spawnPoint = island.spawnPoint == null ? null : island.spawnPoint;
        this.tileEntityCount.addAll(island.tileEntityCount);
        this.updatedDate = island.updatedDate;
        this.votes = island.votes;
        this.world = island.world == null ? null : Bukkit.getWorld(island.world.getUID());
        this.y = island.y;
        this.hoppersAmount = island.hoppersAmount;
        this.upgradeLevels = island.upgradeLevels;
        this.worth = island.worth;
        this.blocksCounter = island.blocksCounter;
        this.paypal = island.paypal;
        this.discord = island.discord;
    }

    public boolean onIsland(Location target) {
        if (world != null) {
            // If the new nether is being used, islands exist in the nether too
            if (target.getWorld().equals(world) || (Settings.createNether && Settings.newNether && ASkyBlock.getNetherWorld() != null && target.getWorld().equals(ASkyBlock.getNetherWorld()))) {
                return target.getBlockX() >= minProtectedX && target.getBlockX() < (minProtectedX
                        + protectionRange)
                        && target.getBlockZ() >= minProtectedZ && target.getBlockZ() < (minProtectedZ
                                + protectionRange);
            }
        }
        return false;
    }

    public boolean inIslandSpace(Location target) {
        if (target.getWorld().equals(ASkyBlock.getIslandWorld()) || target.getWorld().equals(ASkyBlock.getNetherWorld())) {
            return target.getX() >= center.getX() - islandDistance / 2
                    && target.getX() < center.getX() + islandDistance / 2
                    && target.getZ() >= center.getZ() - islandDistance / 2
                    && target.getZ() < center.getZ() + islandDistance / 2;
        }
        return false;
    }

    public boolean inIslandSpace(int x, int z) {
        return x >= center.getX() - islandDistance / 2
                && x < center.getX() + islandDistance / 2
                && z >= center.getZ() - islandDistance / 2
                && z < center.getZ() + islandDistance / 2;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinZ() {
        return minZ;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public int getMinProtectedX() {
        return minProtectedX;
    }

    public int getMinProtectedZ() {
        return minProtectedZ;
    }

    public int getProtectionSize() {
        return protectionRange;
    }

    public void setProtectionSize(int protectionSize) {
        this.protectionRange = protectionSize;
        this.minProtectedX = center.getX() - protectionSize / 2;
        this.minProtectedZ = center.getZ() - protectionSize / 2;

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (inIslandSpace(onlinePlayer.getLocation())) {
                plugin.getSuperiorSkyblock().getNMSAbstraction().setWorldBorder(onlinePlayer, this);
            }
        }

    }

    public int getIslandDistance() {
        return islandDistance;
    }

    public void setIslandDistance(int islandDistance) {
        this.islandDistance = islandDistance;
    }

    public Location getCenter() {
        return center.parseLocation();
    }

    public void setCenter(Location center) {
        this.center = new WrappedLocation(center);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        // Bukkit.getLogger().info("DEBUG: island is now " + locked);
        this.locked = locked;
    }

    public List<String> getSettings(){
        List<String> settings = new ArrayList<>();

        for (SettingsFlag flag: SettingsFlag.values()) {
            settings.add(flag.name() + ":" + this.igs.getOrDefault(flag, false));
        }

        return settings;
    }

    public boolean getIgsFlag(SettingsFlag flag) {
        //plugin.getLogger().info("DEBUG: asking for " + flag + " = " + igs.get(flag));
        if (this.igs.containsKey(flag)) {
            return igs.get(flag);
        }
        return false;
    }

    public void setIgsFlag(SettingsFlag flag, boolean value) {
        this.igs.put(flag, value);
    }

    public List<UUID> getMembers() {
        Set<UUID> result = new HashSet<>();
        // Add any coop members for this island
        result.addAll(CoopPlay.getInstance().getCoopPlayers(center.toVector().toLocation(ASkyBlock.getIslandWorld())));
        if (Settings.createNether && Settings.newNether && ASkyBlock.getNetherWorld() != null) {
            result.addAll(CoopPlay.getInstance().getCoopPlayers(center.toVector().toLocation(ASkyBlock.getNetherWorld())));
        }
        if (owner == null) {
            return new ArrayList<>(result);
        }
        result.add(owner);
        // Add any team members
        result.addAll(plugin.getPlayers().getMembers(owner));
        return new ArrayList<>(result);
    }

    public boolean isSpawn() {
        return isSpawn;
    }

    public void setSpawn(boolean isSpawn) {
        this.isSpawn = isSpawn;
    }

    public boolean isPurgeProtected() {
        return purgeProtected;
    }

    public void setPurgeProtected(boolean purgeProtected) {
        this.purgeProtected = purgeProtected;
    }

    public int getPopulation() {
        int result = 0;
        for (int x = getMinProtectedX() /16; x <= (getMinProtectedX() + getProtectionSize() - 1)/16; x++) {
            for (int z = getMinProtectedZ() /16; z <= (getMinProtectedZ() + getProtectionSize() - 1)/16; z++) {
                for (Entity entity : world.getChunkAt(x, z).getEntities()) {
                    if (entity instanceof Villager && onIsland(entity.getLocation())) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public long getWorth() {
        return worth;
    }

    public int getTileEntityCount(Material material, World world) {
        int result = 0;
        for (int x = getMinProtectedX() /16; x <= (getMinProtectedX() + getProtectionSize() - 1)/16; x++) {
            for (int z = getMinProtectedZ() /16; z <= (getMinProtectedZ() + getProtectionSize() - 1)/16; z++) {
                for (BlockState holder : world.getChunkAt(x, z).getTileEntities()) {
                    //plugin.getLogger().info("DEBUG: tile entity: " + holder.getType());
                    if (onIsland(holder.getLocation())) {
                        if (holder.getType() == material) {
                            result++;
                        } else if (material.name().equals("REDSTONE_COMPARATOR_OFF")) {
                            if (holder.getType().name().equals("REDSTONE_COMPARATOR_ON")) {
                                result++;
                            }
                        } else if (material.name().equals("FURNACE")) {
                            if (holder.getType().name().equals("BURNING_FURNACE")) {
                                result++;
                            }
                        } else if (material.toString().endsWith("BANNER")) {
                            if (holder.getType().toString().endsWith("BANNER")) {
                                result++;
                            }
                        } else if (material.equals(LegacyMaterial.SIGN_WALL)) {
                            if (holder.getType().equals(LegacyMaterial.SIGN_WALL)) {
                                result++;
                            }
                        }
                    }
                }
                for (Entity holder : world.getChunkAt(x, z).getEntities()) {
                    //plugin.getLogger().info("DEBUG: entity: " + holder.getType());
                    if (holder.getType().toString().equals(material.toString()) && onIsland(holder.getLocation())) {
                        result++;
                    }
                }
            }
        }
        // Version 1.7.x counts differently to 1.8 (ugh)
        // In 1.7, the entity is present before it is cancelled and so gets counted.
        // Remove 1 from count if it is 1.7.x
        if (!plugin.isOnePointEight()) {
            result--;
        }
        return result;
    }

    public void setSpawnPoint(Location location) {
        spawnPoint = new WrappedLocation(location);
    }

    public Location getSpawnPoint() {
        return spawnPoint == null ? null : spawnPoint.parseLocation();
    }

    public void toggleIgs(SettingsFlag flag) {
        if (igs.containsKey(flag)) {
            igs.put(flag, !igs.get(flag));
        }

    }

    public Biome getBiome() {
        if (biome == null) {
            biome = center.getBlock().getBiome();
        }
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public int getLevelHandicap() {
        return levelHandicap;
    }

    public void setLevelHandicap(int levelHandicap) {
        this.levelHandicap = levelHandicap;
    }

    public void setSettings(List<String> settings) {
        // Start with defaults
        if (isSpawn) {
            setSpawnDefaults();
        } else {
            setIgsDefaults();
        }

        if(settings == null || settings.isEmpty())
            return;

        for(String line : settings){
            String[] sections = line.split(":");
            setIgsFlag(SettingsFlag.valueOf(sections[0]), Boolean.valueOf(sections[1]));
        }
    }

    //Old way of loading settings
    public void setSettings(String settings, List<String> settingsKey) {

        // Start with defaults
        if (isSpawn) {
            setSpawnDefaults();
        } else {
            setIgsDefaults();
        }
        if(settings == null || settings.isEmpty())
            return;
        if (settingsKey.size() != settings.length()) {
            plugin.getLogger().severe("Island settings does not match settings key in islands.yml. Using defaults.");
            return;
        }
    }

    public void setStackedBlocks(List<String> stackedBlocks){
        Matcher matcher;
        for (String line : stackedBlocks) {
            if((matcher = Pattern.compile("(.*);(.*)").matcher(line)).matches()) {
                Location location = Util.getLocationString(matcher.group(1));
                if (location != null && location.getBlock() != null && location.getBlock().getType() != Material.AIR) {
                    this.stackedBlocks.put(new WrappedLocation(location), Integer.valueOf(matcher.group(2)));
                }
            }
        }
    }

    public List<String> getStackedBlocks(){
        List<String> stackedBlocks = new ArrayList<>();

        for(Map.Entry<WrappedLocation, Integer> entry : this.stackedBlocks.entrySet()){
            String location = Util.getStringLocation(entry.getKey().parseLocation());
            if(!location.isEmpty()) {
                stackedBlocks.add(location + ";" + entry.getValue());
            }
        }

        return stackedBlocks;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();

        result.put("center.x", center.getX());
        result.put("center.y", center.getY());
        result.put("center.z", center.getZ());
        result.put("protection-range", protectionRange);
        result.put("island-distance", islandDistance);
        result.put("owner", isSpawn ? "spawn" : owner == null ? "null" : owner.toString());
        result.put("locked", locked);
        result.put("purge-protected", purgeProtected);
        if(spawnPoint != null)
            result.put("spawn-point", spawnPoint);
        result.put("settings", getSettings());
        result.put("biome", getBiome().toString());
        result.put("level", levelHandicap);
        result.put("hoppers-amount", hoppersAmount);
        result.put("upgrades", getUpgradesList());
        result.put("counted-blocks", getCountedBlocksList());
        result.put("stacked-blocks", getStackedBlocks());
        result.put("blocks-calculation", worth);

        return result;
    }

    public void handleBlockPlacement(Block block, int multiplier){
        String key = StringUtils.getItemKey(block);
        worth += ListUtils.get(Settings.blockValues, 0, key) * multiplier;
        if(ListUtils.contains(plugin.getSuperiorSkyblock().getSettings().countedBlockSlots, key)){
            blocksCounter.put(ListUtils.getKey(plugin.getSuperiorSkyblock().getSettings().countedBlockSlots, key), ListUtils.get(blocksCounter, 0, key) + multiplier);
        }
    }

    public void handleBlockBreakment(Block block, int multiplier){
        String key = StringUtils.getItemKey(block);
        worth -= ListUtils.get(Settings.blockValues, 0, key) * multiplier;
        if(worth < 0)
            worth = 0;
        if(ListUtils.contains(plugin.getSuperiorSkyblock().getSettings().countedBlockSlots, key)){
            if(ListUtils.get(blocksCounter, 0, key) - multiplier <= 0)
                ListUtils.remove(blocksCounter, key);
            else if(ListUtils.contains(blocksCounter, key)) {
                blocksCounter.put(ListUtils.getKey(plugin.getSuperiorSkyblock().getSettings().countedBlockSlots, key), ListUtils.get(blocksCounter, 0, key) - multiplier);
            }
        }
    }

    public void handleHopperPlacement(){
        this.hoppersAmount++;
    }

    public void handleHopperBreakment(){
        hoppersAmount = hoppersAmount <= 0 ? 0 : hoppersAmount - 1;
    }

    public int getHoppersAmount(){
        return hoppersAmount;
    }

    public void setHoppersAmount(int hoppersAmount){
        this.hoppersAmount = hoppersAmount;
    }

    public int getUpgradeLevel(String upgradeName){
        return upgradeLevels.getOrDefault(upgradeName, 0);
    }

    public void setUpgradeLevel(String upgradeName, int upgradeLevel){
        upgradeLevels.put(upgradeName, upgradeLevel);
    }

    public double getUpgradeMultiplier(String upgradeName){
        for(Upgrade upgrade : plugin.getSuperiorSkyblock().getSettings().upgrades){
            if(upgrade instanceof MultiplierUpgrade && upgrade.getName().equals(upgradeName)){
                return ((MultiplierUpgrade) upgrade).getMultiplier(upgradeLevels.getOrDefault(upgradeName, 0));
            }
        }

        throw new NullPointerException("Couldn't find upgrade " + upgradeName);
    }

    public void setHoppersLevel(int hoppersLevel){
        setUpgradeLevel("hoppers-limit", hoppersLevel);
    }

    public int getHoppersLevel(){
        return getUpgradeLevel("hoppers-limit");
    }

    public double getHoppersLimit(){
        return getUpgradeMultiplier("hoppers-limit");
    }

    public boolean hasSpawnRatesMultiplier(){
        return getSpawnRatesMultiplier() > 1;
    }

    public void setSpawnRatesLevel(int spawnRatesLevel){
        setUpgradeLevel("spawner-rates", spawnRatesLevel);
    }

    public int getSpawnRatesLevel(){
        return getUpgradeLevel("spawner-rates");
    }

    public double getSpawnRatesMultiplier(){
        return getUpgradeMultiplier("spawner-rates");
    }

    public boolean hasMobDropsMultiplier(){
        return getMobDropsMultiplier() > 1;
    }

    public void setMobDropsLevel(int mobDropsLevel){
        setUpgradeLevel("mob-drops", mobDropsLevel);
    }

    public int getMobDropsLevel(){
        return getUpgradeLevel("mob-drops");
    }

    public double getMobDropsMultiplier(){
        return getUpgradeMultiplier("mob-drops");
    }

    public boolean hasCropGrowthMultiplier(){
        return getCropGrowthMultiplier() > 1;
    }

    public void setCropGrowthLevel(int cropGrowthLevel){
        setUpgradeLevel("crop-growth", cropGrowthLevel);
    }

    public int getCropGrowthLevel(){
        return getUpgradeLevel("crop-growth");
    }

    public double getCropGrowthMultiplier(){
        return getUpgradeMultiplier("crop-growth");
    }

    public boolean isStackedBlock(Location location){
        return stackedBlocks.containsKey(new WrappedLocation(location));
    }

    public void setStackedBlock(Location location, int amount){
        stackedBlocks.put(new WrappedLocation(location), amount);
    }

    public void removeStackedBlock(Location location){
        stackedBlocks.remove(new WrappedLocation(location));
    }

    public int getStackedBlockAmount(Location location){
        return stackedBlocks.getOrDefault(new WrappedLocation(location), 1);
    }

    public List<Location> getStackedBlockLocations(){
        List<Location> stackedBlocks = new ArrayList<>();

        this.stackedBlocks.keySet().forEach(wrappedLocation -> stackedBlocks.add(wrappedLocation.parseLocation()));

        return stackedBlocks;
    }

    public void setWorth(long worth){
        this.worth = worth;
    }

    public long getIslandLevel(){
        return plugin.getPlayers().getIslandLevel(owner);
    }

    public void setIslandLevel(long islandLevel){
        plugin.getPlayers().setIslandLevel(owner, islandLevel);
    }

    public int getBlockCount(String key){
        return ListUtils.get(blocksCounter, 0, key);
    }

    public int getBlockCount(EntityType entityType){
        return getBlockCount(StringUtils.getItemKey(entityType));
    }

    public int getBlockCount(ItemStack itemStack){
        return getBlockCount(StringUtils.getItemKey(itemStack));
    }

    public int getBlockCount(Block block){
        return getBlockCount(StringUtils.getItemKey(block));
    }

    public void clearCountedBlocks(){
        blocksCounter.clear();
    }

    public List<String> getUpgradesList(){
        List<String> upgradesList = new ArrayList<>();

        for(String upgradeName : upgradeLevels.keySet())
            upgradesList.add(upgradeName + ";" + upgradeLevels.get(upgradeName));

        return upgradesList;
    }

    public List<String> getCountedBlocksList(){
        List<String> countedBlocksList = new ArrayList<>();

        for(String blockKey : blocksCounter.keySet()) {
            if(!blockKey.isEmpty())
                countedBlocksList.add(blockKey + ";" + blocksCounter.get(blockKey));
        }

        return countedBlocksList;
    }

    public void setPaypal(String paypal) {
        this.paypal = paypal;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public String getPaypal() {
        return paypal;
    }

    public String getDiscord() {
        return discord;
    }

    /**
     * Island Guard Setting flags
     * Covers island, spawn and system settings
     */
    public enum SettingsFlag {
        /**
         * Water is acid above sea level
         */
        ACID_DAMAGE,
        /**
         * Anvil use
         */
        ANVIL,
        /**
         * Armor stand use
         */
        ARMOR_STAND,
        /**
         * Beacon use
         */
        BEACON,
        /**
         * Bed use
         */
        BED,
        /**
         * Can break blocks
         */
        BREAK_BLOCKS,
        /**
         * Can breed animals
         */
        BREEDING,
        /**
         * Can use brewing stand
         */
        BREWING,
        /**
         * Can empty or fill buckets
         */
        BUCKET,
        /**
         * Can collect lava
         */
        COLLECT_LAVA,
        /**
         * Can collect water
         */
        COLLECT_WATER,
        /**
         * Can open chests or hoppers or dispensers
         */
        CHEST,
        /**
         * Can eat and teleport with chorus fruit
         */
        CHORUS_FRUIT,
        /**
         * Can use the work bench
         */
        CRAFTING,
        /**
         * Allow creepers to hurt players (but not damage blocks)
         */
        CREEPER_PAIN,
        /**
         * Can trample crops
         */
        CROP_TRAMPLE,
        /**
         * Can open doors or trapdoors
         */
        DOOR,
        /**
         * Chicken eggs can be thrown
         */
        EGGS,
        /**
         * Can use the enchanting table
         */
        ENCHANTING,
        /**
         * Can throw ender pearls
         */
        ENDER_PEARL,
        /**
         * Can toggle enter/exit names to island
         */
        ENTER_EXIT_MESSAGES,
        /**
         * Fire use/placement in general
         */
        FIRE,
        /**
         * Can extinguish fires by punching them
         */
        FIRE_EXTINGUISH,
        /**
         * Allow fire spread
         */
        FIRE_SPREAD,
        /**
         * Can use furnaces
         */
        FURNACE,
        /**
         * Can use gates
         */
        GATE,
        /**
         * Can open horse or other animal inventories, e.g. llama
         */
        HORSE_INVENTORY,
        /**
         * Can ride an animal
         */
        HORSE_RIDING,
        /**
         * Can hurt friendly mobs, e.g. cows
         */
        HURT_MOBS,
        /**
         * Can hurt monsters
         */
        HURT_MONSTERS,
        /**
         * Can leash or unleash animals
         */
        LEASH,
        /**
         * Can use buttons or levers
         */
        LEVER_BUTTON,
        /**
         * Animals, etc. can spawn
         */
        MILKING,
        /**
         * Can do PVP in the nether
         */
        MOB_SPAWN,
        /**
         * Monsters can spawn
         */
        MONSTER_SPAWN,
        /**
         * Can operate jukeboxes, note boxes etc.
         */
        MUSIC,
        /**
         * Can place blocks
         */
        NETHER_PVP,
        /**
         * Can interact with redstone items, like diodes
         */
        PLACE_BLOCKS,
        /**
         * Can go through portals
         */
        PORTAL,
        /**
         * Will activate pressure plates
         */
        PRESSURE_PLATE,
        /**
         * Can do PVP in the overworld
         */
        PVP,
        /**
         * Cows can be milked
         */
        REDSTONE,
        /**
         * Spawn eggs can be used
         */
        SPAWN_EGGS,
        /**
         * Can shear sheep
         */
        SHEARING,
        /**
         * Can trade with villagers
         */
        VILLAGER_TRADING,
        /**
         * Visitors can drop items
         */
        VISITOR_ITEM_DROP,
        /**
         * Visitors can pick up items
         */
        VISITOR_ITEM_PICKUP
    }

    /**
     * Resets the protection settings to their default as set in config.yml for this island
     */
    public void setIgsDefaults() {
        for (SettingsFlag flag: SettingsFlag.values()) {
            if (!Settings.defaultIslandSettings.containsKey(flag)) {
                // Default default
                if (flag.equals(SettingsFlag.MOB_SPAWN) || flag.equals(SettingsFlag.MONSTER_SPAWN)) {
                    this.igs.put(flag, true);
                } else {
                    this.igs.put(flag, false);
                }
            } else {
                if (Settings.defaultIslandSettings.get(flag) == null) {
                    //plugin.getLogger().info("DEBUG: null flag " + flag);
                    if (flag.equals(SettingsFlag.MOB_SPAWN) || flag.equals(SettingsFlag.MONSTER_SPAWN)) {
                        this.igs.put(flag, true);
                    } else {
                        this.igs.put(flag, false);
                    }
                } else {
                    this.igs.put(flag, Settings.defaultIslandSettings.get(flag));
                }
            }
        }
    }

    /**
     * Reset spawn protection settings to their default as set in config.yml for this island
     */
    public void setSpawnDefaults() {
        for (SettingsFlag flag: SettingsFlag.values()) {
            if (!Settings.defaultSpawnSettings.containsKey(flag)) {
                // Default default
                if (flag.equals(SettingsFlag.MOB_SPAWN) || flag.equals(SettingsFlag.MONSTER_SPAWN)) {
                    this.igs.put(flag, true);
                } else {
                    this.igs.put(flag, false);
                }
            } else {
                if (Settings.defaultSpawnSettings.get(flag) == null) {
                    if (flag.equals(SettingsFlag.MOB_SPAWN) || flag.equals(SettingsFlag.MONSTER_SPAWN)) {
                        this.igs.put(flag, true);
                    } else {
                        this.igs.put(flag, false);
                    }
                } else {
                    this.igs.put(flag, Settings.defaultSpawnSettings.get(flag));
                }
            }
        }
    }

}
