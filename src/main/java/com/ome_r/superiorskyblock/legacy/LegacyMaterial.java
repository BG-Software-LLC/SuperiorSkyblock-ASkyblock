package com.ome_r.superiorskyblock.legacy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LegacyMaterial{

    private static boolean isLegacy = !Bukkit.getVersion().contains("1.13");

    public static Material WRITABLE_BOOK = Material.valueOf(isLegacy ? "BOOK_AND_QUILL" : "WRITABLE_BOOK");
    public static Material SIGN_WALL = Material.valueOf(isLegacy ? "SIGN_POST" : "WALL_SIGN");
    public static Material OAK_BOAT = Material.valueOf(isLegacy ? "BOAT" : "OAK_BOAT");
    public static Material GOLDEN_AXE = Material.valueOf(isLegacy ? "GOLD_AXE" : "GOLDEN_AXE");
    public static Material OAK_SAPLING = Material.valueOf(isLegacy ? "SAPLING" : "OAK_SAPLING");
    public static Material NETHER_PORTAL = Material.valueOf(isLegacy ? "PORTAL" : "NETHER_PORTAL");
    public static Material OAK_DOOR = Material.valueOf(isLegacy ? "WOODEN_DOOR" : "OAK_DOOR");
    public static Material OAK_TRAPDOOR = Material.valueOf(isLegacy ? "TRAP_DOOR" : "OAK_TRAPDOOR");
    public static Material PISTON_HEAD = Material.valueOf(isLegacy ? "PISTON_EXTENSION" : "PISTON_HEAD");
    public static Material OAK_BUTTON = Material.valueOf(isLegacy ? "WOOD_BUTTON" : "OAK_BUTTON");
    public static Material WHITE_WALL_BANNER = Material.valueOf(isLegacy ? "WALL_BANNER" : "WHITE_WALL_BANNER");
    public static Material CARROT = Material.valueOf(isLegacy ? "CARROT_ITEM" : "CARROT");
    public static Material WHEAT_SEEDS = Material.valueOf(isLegacy ? "SEEDS" : "WHEAT_SEEDS");
    public static Material ENCHANTING_TABLE = Material.valueOf(isLegacy ? "ENCHANTMENT_TABLE" : "ENCHANTING_TABLE");
    public static Material BREWING_STAND = Material.valueOf(isLegacy ? "BREWING_STAND_ITEM" : "BREWING_STAND");
    public static Material CARROT_ON_A_STICK = Material.valueOf(isLegacy ? "CARROT_STICK" : "CARROT_ON_A_STICK");
    public static Material CAULDRON = Material.valueOf(isLegacy ? "CAULDRON_ITEM" : "CAULDRON");
    public static Material CHEST_MINECART = Material.valueOf(isLegacy ? "STORAGE_MINECART" : "CHEST_MINECART");
    public static Material CLOCK = Material.valueOf(isLegacy ? "WATCH" : "CLOCK");
    public static Material COBBLESTONE_WALL = Material.valueOf(isLegacy ? "COBBLE_WALL" : "COBBLESTONE_WALL");
    public static Material COMMAND_BLOCK = Material.valueOf(isLegacy ? "ENCHANTMENT_TABLE" : "COMMAND_BLOCK");
    public static Material COMMANDBLOCK_MINECART = Material.valueOf(isLegacy ? "COMMAND_MINECART" : "COMMAND_BLOCK_MINECART");
    public static Material COMPARATOR = Material.valueOf(isLegacy ? "REDSTONE_COMPARATOR" : "COMPARATOR");
    public static Material COOKED_PORKCHOP = Material.valueOf(isLegacy ? "GRILLED_PORK" : "COOKED_PORKCHOP");
    public static Material CRAFTING_TABLE = Material.valueOf(isLegacy ? "WORKBENCH" : "CRAFTING_TABLE");
    public static Material DIAMOND_HORSE_ARMOR = Material.valueOf(isLegacy ? "DIAMOND_BARDING" : "DIAMOND_HORSE_ARMOR");
    public static Material DIAMOND_SHOVEL = Material.valueOf(isLegacy ? "DIAMOND_SPADE" : "DIAMOND_SHOVEL");
    public static Material INK_SAC = Material.valueOf(isLegacy ? "INK_SACK" : "INK_SAC");
    public static Material END_PORTAL_FRAME = Material.valueOf(isLegacy ? "ENDER_PORTAL_FRAME" : "END_PORTAL_FRAME");
    public static Material END_PORTAL = Material.valueOf(isLegacy ? "ENDER_PORTAL" : "END_PORTAL");
    public static Material END_STONE = Material.valueOf(isLegacy ? "ENDER_STONE" : "END_STONE");
    public static Material EXPERIENCE_BOTTLE = Material.valueOf(isLegacy ? "EXP_BOTTLE" : "EXPERIENCE_BOTTLE");
    public static Material FIRE_CHARGE = Material.valueOf(isLegacy ? "FIREBALL" : "FIRE_CHARGE");
    public static Material FIREWORKS = Material.valueOf(isLegacy ? "FIREWORK" : "FIREWORK_ROCKET");
    public static Material FLOWER_POT = Material.valueOf(isLegacy ? "FLOWER_POT_ITEM" : "FLOWER_POT");
    public static Material GLASS_PANE = Material.valueOf(isLegacy ? "THIN_GLASS" : "GLASS_PANE");
    public static Material GOLDEN_CHESTPLATE = Material.valueOf(isLegacy ? "GOLD_CHESTPLATE" : "GOLDEN_CHESTPLATE");
    public static Material GOLDEN_HORSE_ARMOR = Material.valueOf(isLegacy ? "GOLD_BARDING" : "GOLDEN_HORSE_ARMOR");
    public static Material GOLDEN_LEGGINGS = Material.valueOf(isLegacy ? "GOLD_LEGGINGS" : "GOLDEN_LEGGINGS");
    public static Material GOLDEN_PICKAXE = Material.valueOf(isLegacy ? "GOLD_PICKAXE" : "GOLDEN_PICKAXE");
    public static Material GOLDEN_SHOVEL = Material.valueOf(isLegacy ? "GOLD_SPADE" : "GOLDEN_SHOVEL");
    public static Material GOLDEN_SWORD = Material.valueOf(isLegacy ? "GOLD_SWORD" : "GOLDEN_SWORD");
    public static Material GOLDEN_HELMET = Material.valueOf(isLegacy ? "GOLD_HELMET" : "GOLDEN_HELMET");
    public static Material GOLDEN_HOE = Material.valueOf(isLegacy ? "GOLD_HOE" : "GOLDEN_HOE");
    public static Material GOLDEN_BOOTS = Material.valueOf(isLegacy ? "GOLD_BOOTS" : "GOLDEN_BOOTS");
    public static Material GUNPOWDER = Material.valueOf(isLegacy ? "SULPHUR" : "GUNPOWDER");
    public static Material HARDENED_CLAY = Material.valueOf(isLegacy ? "HARD_CLAY" : "WHITE_TERRACOTTA");
    public static Material HEAVY_WEIGHTED_PRESSURE_PLATE = Material.valueOf(isLegacy ? "GOLD_PLATE" : "HEAVY_WEIGHTED_PRESSURE_PLATE");
    public static Material IRON_BARS = Material.valueOf(isLegacy ? "IRON_FENCE" : "IRON_BARS");
    public static Material IRON_HORSE_ARMOR = Material.valueOf(isLegacy ? "IRON_BARDING" : "IRON_HORSE_ARMOR");
    public static Material IRON_SHOVEL = Material.valueOf(isLegacy ? "IRON_SPADE" : "IRON_SHOVEL");
    public static Material LEAD = Material.valueOf(isLegacy ? "LEASH" : "LEAD");
    public static Material LIGHT_WEIGHTED_PRESSURE_PLATE = Material.valueOf(isLegacy ? "IRON_PLATE" : "LIGHT_WEIGHTED_PRESSURE_PLATE");
    public static Material MAP = Material.valueOf(isLegacy ? "EMPTY_MAP" : "MAP");
    public static Material FILLED_MAP = Material.valueOf(isLegacy ? "MAP" : "FILLED_MAP");
    public static Material MYCELIUM = Material.valueOf(isLegacy ? "MYCEL" : "MYCELIUM");
    public static Material NETHER_BRICK_FENCE = Material.valueOf(isLegacy ? "NETHER_FENCE" : "NETHER_BRICK_FENCE");
    public static Material NETHER_WART = Material.valueOf(isLegacy ? "NETHER_STALK" : "NETHER_WART");
    public static Material NETHERBRICK = Material.valueOf(isLegacy ? "NETHER_BRICK_ITEM" : "NETHER_BRICK");
    public static Material OAK_STAIRS = Material.valueOf(isLegacy ? "WOOD_STAIRS" : "OAK_STAIRS");
    public static Material PISTON = Material.valueOf(isLegacy ? "PISTON_BASE" : "PISTON");
    public static Material PLANKS = Material.valueOf(isLegacy ? "WOOD" : "OAK_PLANKS");
    public static Material POTATO = Material.valueOf(isLegacy ? "POTATO_ITEM" : "POTATO");
    public static Material RAIL = Material.valueOf(isLegacy ? "RAILS" : "RAIL");
    public static Material MUSIC_DISC_11 = Material.valueOf(isLegacy ? "RECORD_11" : "MUSIC_DISC_11");
    public static Material MUSIC_DISC_13 = Material.valueOf(isLegacy ? "GOLD_RECORD" : "MUSIC_DISC_13");
    public static Material MUSIC_DISC_BLOCKS = Material.valueOf(isLegacy ? "RECORD_3" : "MUSIC_DISC_BLOCKS");
    public static Material MUSIC_DISC_CAT = Material.valueOf(isLegacy ? "GREEN_RECORD" : "MUSIC_DISC_CAT");
    public static Material MUSIC_DISC_CHIRP = Material.valueOf(isLegacy ? "RECORD_4" : "MUSIC_DISC_CHIRP");
    public static Material MUSIC_DISC_FAR = Material.valueOf(isLegacy ? "RECORD_5" : "MUSIC_DISC_FAR");
    public static Material MUSIC_DISC_MALL = Material.valueOf(isLegacy ? "RECORD_6" : "MUSIC_DISC_MALL");
    public static Material MUSIC_DISC_MELLOHI = Material.valueOf(isLegacy ? "RECORD_7" : "MUSIC_DISC_MELLOHI");
    public static Material MUSIC_DISC_STAL = Material.valueOf(isLegacy ? "RECORD_8" : "MUSIC_DISC_STAL");
    public static Material MUSIC_DISC_STRAD = Material.valueOf(isLegacy ? "RECORD_9" : "MUSIC_DISC_STRAD");
    public static Material MUSIC_DISC_WARD = Material.valueOf(isLegacy ? "RECORD_10" : "MUSIC_DISC_WARD");
    public static Material MUSIC_DISC_WAIT = Material.valueOf(isLegacy ? "RECORD_12" : "MUSIC_DISC_WAIT");
    public static Material REPEATER = Material.valueOf(isLegacy ? "DIODE" : "REPEATER");
    public static Material SKULL = Material.valueOf(isLegacy ? "SKULL_ITEM" : "SKELETON_SKULL");
    public static Material STICKY_PISTON = Material.valueOf(isLegacy ? "PISTON_STICKY_BASE" : "STICKY_PISTON");
    public static Material STONE_SHOVEL = Material.valueOf(isLegacy ? "STONE_SPADE" : "STONE_SHOVEL");
    public static Material STONE_SLAB = Material.valueOf(isLegacy ? "STEP" : "STONE_SLAB");
    public static Material TNT_MINECART = Material.valueOf(isLegacy ? "EXPLOSIVE_MINECART" : "TNT_MINECART");
    public static Material WATERLILY = Material.valueOf(isLegacy ? "WATER_LILY" : "LILY_PAD");
    public static Material WOODEN_AXE = Material.valueOf(isLegacy ? "WOOD_AXE" : "WOODEN_AXE");
    public static Material WOODEN_HOE = Material.valueOf(isLegacy ? "WOOD_HOE" : "WOODEN_HOE");
    public static Material WOODEN_PICKAXE = Material.valueOf(isLegacy ? "WOOD_PICKAXE" : "WOODEN_PICKAXE");
    public static Material WOODEN_PRESSURE_PLATE = Material.valueOf(isLegacy ? "WOOD_PLATE" : "OAK_PRESSURE_PLATE");
    public static Material WOODEN_SHOVEL = Material.valueOf(isLegacy ? "WOOD_SPADE" : "WOODEN_SHOVEL");
    public static Material OAK_SLAB = Material.valueOf(isLegacy ? "WOOD_STEP" : "OAK_SLAB");
    public static Material WOODEN_SWORD = Material.valueOf(isLegacy ? "WOOD_SWORD" : "WOODEN_SWORD");
    public static Material MUSHROOM_STEW = Material.valueOf(isLegacy ? "MUSHROOM_SOUP" : "MUSHROOM_STEW");
    public static Material RED_FLOWER = Material.valueOf(isLegacy ? "RED_ROSE" : "POPPY");
    public static Material PIG_SPAWN_EGG = Material.valueOf(isLegacy ? "MONSTER_EGG" : "PIG_SPAWN_EGG");
    public static Material SPAWNER = Material.valueOf(isLegacy ? "MOB_SPAWNER" : "SPAWNER");
    public static Material BURNING_FURNACE = Material.valueOf(isLegacy ? "BURNING_FURNACE" : "FURNACE");
    public static Material FURNACE_MINECART = Material.valueOf(isLegacy ? "POWERED_MINECART" : "FURNACE_MINECART");
    public static Material OAK_LOG = Material.valueOf(isLegacy ? "LOG_2" : "OAK_LOG");
    public static Material OAK_LEAVES = Material.valueOf(isLegacy ? "LEAVES_2" : "OAK_LEAVES");
    public static Material STATIONARY_LAVA = Material.valueOf(isLegacy ? "STATIONARY_LAVA" : "LAVA");
    public static Material STATIONARY_WATER = Material.valueOf(isLegacy ? "STATIONARY_WATER" : "WATER");


    public static Material getDoor(String doorType){
        return isLegacy ? Material.valueOf(doorType + "_DOOR_ITEM") : Material.valueOf(doorType + "_DOOR");
    }

    public static Material getStairs(String stairs){
        return isLegacy ? Material.valueOf(stairs + "_WOOD_STAIRS") : Material.valueOf(stairs + "_STAIRS");
    }

    public static Material getRedstoneTorch(boolean on){
        return isLegacy ? on ? Material.valueOf("REDSTONE_TORCH_ON") : Material.valueOf("REDSTONE_TORCH_OFF") : Material.valueOf("REDSTONE_TORCH");
    }

    public static Material getRedstonComparator(boolean on){
        return isLegacy ? on ? Material.valueOf("REDSTONE_COMPARATOR_ON") : Material.valueOf("REDSTONE_COMPARATOR_OFF") : Material.valueOf("COMPARATOR");
    }

    public static ItemStack getPlayerHead(int amount){
        return isLegacy ? new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3) : new ItemStack(Material.valueOf("PLAYER_HEAD"), amount);
    }

    public static boolean isMonsterEgg(Material material){
        return isLegacy ? material.name().contains("MONSTER_EGG") : material.name().contains("SPAWN_EGG");
    }

    public static boolean isPortal(Material material){
        return material.name().contains("PORTAL");
    }

    public static boolean isLava(Material material){
        return material.name().contains("LAVA");
    }

    public static boolean isWater(Material material){
        return material.name().contains("WATER");
    }

    public static boolean isBoat(Material material){
        return material.name().contains("BOAT");
    }

    public static boolean isFence(Material material){
        return material.name().contains("FENCE");
    }

    public static boolean isLog(Material material){
        return material.name().contains("LOG");
    }

    public static boolean isLeaves(Material material){
        return material.name().contains("LEAVES");
    }

    public static boolean isShulkerBox(Material material){
        return material.name().contains("LEAVES");
    }

}