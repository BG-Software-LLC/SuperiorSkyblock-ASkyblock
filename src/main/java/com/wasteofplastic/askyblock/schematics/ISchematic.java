package com.wasteofplastic.askyblock.schematics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ISchematic {

    String getHeading();

    Material getIcon();

    int getDurability();

    String getName();

    String getPerm();

    int getRating();

    void pasteSchematic(Location loc, Player player, boolean teleport, Schematic.PasteReason reason);

    void setBiome(Biome biome);

    void setDescription(String description);

    void setHeading(String heading);

    void setIcon(Material icon, int damage);

    void setIcon(Material icon);

    void setName(String name);

    void setPerm(String perm);

    void setRating(int rating);

    void setUseDefaultChest(boolean useDefaultChest);

    void setUsePhysics(boolean usePhysics);

    void setPasteAir(boolean pasteAir);

    void generateIslandBlocks(Location islandLoc, Player player, Schematic.PasteReason reason);

    void setIslandCompanion(List<EntityType> islandCompanion);

    void setCompanionNames(List<String> companionNames);

    void setDefaultChestItems(ItemStack[] defaultChestItems);

    boolean isInNether();

    String getPartnerName();

    void setPartnerName(String partnerName);

    boolean isPasteEntities();

    void setPasteEntities(boolean pasteEntities);

    boolean isVisible();

    void setVisible(boolean visible);

    int getOrder();

    void setOrder(int order);

    boolean isPlayerSpawn();

    Location getPlayerSpawn(Location pasteLocation);

    boolean setPlayerSpawnBlock(Material playerSpawnBlock);

    int getLevelHandicap();

    void setLevelHandicap(int levelHandicap);

    void setCost(double cost);

    double getCost();

    enum PasteReason {
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
    }

    Biome getBiome();

    String getDescription();

}
