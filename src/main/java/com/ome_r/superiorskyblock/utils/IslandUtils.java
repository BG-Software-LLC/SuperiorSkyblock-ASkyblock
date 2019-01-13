package com.ome_r.superiorskyblock.utils;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.CoopPlay;
import com.wasteofplastic.askyblock.DeleteIslandChunk;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.ResetWaitTime;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.events.IslandLeaveEvent;
import com.wasteofplastic.askyblock.events.IslandNewEvent;
import com.wasteofplastic.askyblock.events.IslandPreDeleteEvent;
import com.wasteofplastic.askyblock.events.IslandResetEvent;
import com.wasteofplastic.askyblock.handlers.SchematicsHandler;
import com.wasteofplastic.askyblock.schematics.ISchematic;
import com.wasteofplastic.askyblock.schematics.Schematic;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class IslandUtils {

    private static ASkyBlock plugin = ASkyBlock.getPlugin(ASkyBlock.class);
    private static SchematicsHandler schematics = plugin.getSuperiorSkyblock().getSchematics();

    public static Set<UUID> pendingNewIslandSelection = new HashSet<>();
    public static Set<UUID> resettingIsland = new HashSet<>();

    private static HashMap<UUID, Location> islandSpot = new HashMap<>();
    private static Location last = null;

    public static void resetPlayer(Player player, Island oldIsland) {
        // Deduct the reset
        plugin.getPlayers().setResetsLeft(player.getUniqueId(), plugin.getPlayers().getResetsLeft(player.getUniqueId()) - 1);
        // Reset deaths
        if (Settings.islandResetDeathReset) {
            plugin.getPlayers().setDeaths(player.getUniqueId(), 0);
        }
        // Remove any coop invitees and grab their stuff
        CoopPlay.getInstance().clearMyInvitedCoops(player);
        CoopPlay.getInstance().clearMyCoops(player);
        // Remove any warps
        plugin.getWarpSignsListener().removeWarp(player.getUniqueId());
        // Delete the old island, if it exists
        if (oldIsland != null) {
            plugin.getServer().getPluginManager().callEvent(new IslandPreDeleteEvent(player.getUniqueId(), oldIsland));
            // Remove any coops
            CoopPlay.getInstance().clearAllIslandCoops(oldIsland.getCenter());
            plugin.getGrid().removePlayersFromIsland(oldIsland, player.getUniqueId());
            new DeleteIslandChunk(plugin, oldIsland);
            // Fire event
            final IslandResetEvent event = new IslandResetEvent(player, oldIsland.getCenter());
            plugin.getServer().getPluginManager().callEvent(event);
        }
        plugin.getGrid().saveGrid();
    }

    public static void newIsland(final Player player, final ISchematic schematic) {
        ASkyBlock plugin = ASkyBlock.getPlugin(ASkyBlock.class);
        //long time = System.nanoTime();
        final UUID playerUUID = player.getUniqueId();
        boolean firstTime = false;
        if (!plugin.getPlayers().hasIsland(playerUUID)) {
            firstTime = true;
        }

        Location next = getNextIsland(player.getUniqueId());
        // Set the player's parameters to this island
        plugin.getPlayers().setHasIsland(playerUUID, true);
        // Clear any old home locations (they should be clear, but just in case)
        plugin.getPlayers().clearHomeLocations(playerUUID);
        // Set the player's island location to this new spot
        plugin.getPlayers().setIslandLocation(playerUUID, next);

        // Teleport to the new home
        if (schematic.isPlayerSpawn()) {
            // Set home and teleport
            plugin.getPlayers().setHomeLocation(playerUUID, schematic.getPlayerSpawn(next), 1);
            // Save it for later reference
            plugin.getPlayers().setHomeLocation(playerUUID, schematic.getPlayerSpawn(next), -1);
        }

        // Sets a flag to temporarily disable cleanstone generation
        plugin.setNewIsland(true);

        // Paste the starting island. If it is a HELL biome, then we start in the Nether
        if (Settings.createNether && schematic.isInNether() && Settings.newNether && ASkyBlock.getNetherWorld() != null) {
            // Nether start
            // Paste the overworld if it exists
            if (!schematic.getPartnerName().isEmpty() && schematics.isSchematic(schematic.getPartnerName())) {
                // A partner schematic is available
                pastePartner(schematics.getSchematic(schematic.getPartnerName()),next, player);
            }
            // Switch home location to the Nether
            next = next.toVector().toLocation(ASkyBlock.getNetherWorld());
            // Set the player's island location to this new spot
            plugin.getPlayers().setIslandLocation(playerUUID, next);
            schematic.pasteSchematic(next, player, true, firstTime ? Schematic.PasteReason.NEW_ISLAND: Schematic.PasteReason.RESET);
        } else {
            // Paste the island and teleport the player home
            schematic.pasteSchematic(next, player, true, firstTime ? Schematic.PasteReason.NEW_ISLAND: Schematic.PasteReason.RESET);
            //double diff = (System.nanoTime() - timer)/1000000;
            if (Settings.createNether && Settings.newNether && ASkyBlock.getNetherWorld() != null) {
                // Paste the other world schematic
                final Location netherLoc = next.toVector().toLocation(ASkyBlock.getNetherWorld());
                if (schematic.getPartnerName().isEmpty()) {
                    // This will paste the over world schematic again
                    pastePartner(schematic, netherLoc, player);
                } else {
                    if (schematics.isSchematic(schematic.getPartnerName())) {
                        // A partner schematic is available
                        pastePartner(schematics.getSchematic(schematic.getPartnerName()),netherLoc, player);
                    } else {
                        plugin.getLogger().severe("Partner schematic heading '" + schematic.getPartnerName() + "' does not exist");
                    }
                }
            }
        }
        // Record the rating of this schematic - not used for anything right now
        plugin.getPlayers().setStartIslandRating(playerUUID, schematic.getRating());
        // Clear the cleanstone flag so events can happen again
        plugin.setNewIsland(false);
        // Add to the grid
        Island myIsland = plugin.getGrid().addIsland(next.getBlockX(), next.getBlockZ(), playerUUID);
        myIsland.setLevelHandicap(schematic.getLevelHandicap());
        // Save the player so that if the server is reset weird things won't happen
        plugin.getPlayers().save(playerUUID);

        // Start the reset cooldown
        if (!firstTime) {
            setResetWaitTime(player);
        }
        // Set the custom protection range if appropriate
        // Dynamic island range sizes with permissions
        int range = Settings.islandProtectionRange;
        for (PermissionAttachmentInfo perms : player.getEffectivePermissions()) {
            if (perms.getPermission().startsWith(Settings.PERMPREFIX + "island.range.")) {
                if (perms.getPermission().contains(Settings.PERMPREFIX + "island.range.*")) {
                    range = Settings.islandProtectionRange;
                    break;
                } else {
                    String[] spl = perms.getPermission().split(Settings.PERMPREFIX + "island.range.");
                    if (spl.length > 1) {
                        if (!NumberUtils.isDigits(spl[1])) {
                            plugin.getLogger().severe("Player " + player.getName() + " has permission: " + perms.getPermission() + " <-- the last part MUST be a number! Ignoring...");
                        } else {
                            range = Math.max(range, Integer.valueOf(spl[1]));
                        }
                    }
                }
            }
        }
        // Do some sanity checking
        if (range % 2 != 0) {
            range--;
            plugin.getLogger().warning("Protection range must be even, using " + range + " for " + player.getName());
        }
        if (range > Settings.islandDistance) {
            plugin.getLogger().warning("Player has " + Settings.PERMPREFIX + "island.range." + range);
            range = Settings.islandDistance;
            plugin.getLogger().warning(
                    "Island protection range must be " + Settings.islandDistance + " or less. Setting to: " + range);
        }
        myIsland.setProtectionSize(range);

        // Save grid just in case there's a crash
        plugin.getGrid().saveGrid();
        // Done - fire event
        final IslandNewEvent event = new IslandNewEvent(player,schematic, myIsland);
        plugin.getServer().getPluginManager().callEvent(event);
    }

    public static Location getNextIsland(UUID playerUUID) {
        // See if there is a reserved spot
        if (islandSpot.containsKey(playerUUID)) {
            Location next = plugin.getGrid().getClosestIsland(islandSpot.get(playerUUID));
            // Single shot only
            islandSpot.remove(playerUUID);
            // Check if it is already occupied (shouldn't be)
            Island island = plugin.getGrid().getIslandAt(next);
            if (island == null || island.getOwner() == null) {
                // it's still free
                return next;
            }
            // Else, fall back to the random pick
        }
        // Find the next free spot
        if (last == null) {
            last = new Location(ASkyBlock.getIslandWorld(), Settings.islandXOffset + Settings.islandStartX, Settings.islandHeight, Settings.islandZOffset + Settings.islandStartZ);
        }
        Location next = last.clone();

        while (plugin.getGrid().islandAtLocation(next) || islandSpot.containsValue(next)) {
            next = nextGridLocation(next);
        }
        // Make the last next, last
        last = next.clone();
        return next;
    }

    public static void pastePartner(final ISchematic schematic, final Location loc, final Player player) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            schematic.pasteSchematic(loc, player, false, Schematic.PasteReason.PARTNER);
            if (schematic.isPlayerSpawn()) {
                // Set partner home
                plugin.getPlayers().setHomeLocation(player.getUniqueId(), schematic.getPlayerSpawn(loc), -2);
            }
        }, 60L);
    }

    public static void setResetWaitTime(final Player player) {
        ResetWaitTime.resetWaitTime.put(player.getUniqueId(), Calendar.getInstance().getTimeInMillis() + Settings.resetWait * 1000);
    }

    public static Location nextGridLocation(final Location lastIsland) {
        // plugin.getLogger().info("DEBUG nextIslandLocation");
        final int x = lastIsland.getBlockX();
        final int z = lastIsland.getBlockZ();
        final Location nextPos = lastIsland.clone();
        if (x < z) {
            if (-1 * x < z) {
                nextPos.setX(nextPos.getX() + Settings.islandDistance);
                return nextPos;
            }
            nextPos.setZ(nextPos.getZ() + Settings.islandDistance);
            return nextPos;
        }
        if (x > z) {
            if (-1 * x >= z) {
                nextPos.setX(nextPos.getX() - Settings.islandDistance);
                return nextPos;
            }
            nextPos.setZ(nextPos.getZ() - Settings.islandDistance);
            return nextPos;
        }
        if (x <= 0) {
            nextPos.setZ(nextPos.getZ() + Settings.islandDistance);
            return nextPos;
        }
        nextPos.setZ(nextPos.getZ() - Settings.islandDistance);
        return nextPos;
    }

    public static boolean removePlayerFromTeam(final UUID playerUUID, final UUID teamLeader, boolean makeLeader) {
        // Remove player from the team
        plugin.getPlayers().removeMember(teamLeader, playerUUID);
        // If player is online
        // If player is not the leader of their own team
        if (teamLeader == null || !playerUUID.equals(teamLeader)) {
            if (!plugin.getPlayers().setLeaveTeam(playerUUID)) {
                return false;
            }
            //plugin.getPlayers().setHomeLocation(player, null);
            plugin.getPlayers().clearHomeLocations(playerUUID);
            plugin.getPlayers().setIslandLocation(playerUUID, null);
            plugin.getPlayers().setTeamIslandLocation(playerUUID, null);
            if (!makeLeader) {
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerUUID);
                if (offlinePlayer.isOnline()) {
                    // Check perms
                    if (!((Player)offlinePlayer).hasPermission(Settings.PERMPREFIX + "command.leaveexempt")) {
                        runCommands(Settings.leaveCommands, offlinePlayer);
                    }
                } else {
                    // If offline, all commands are run, sorry
                    runCommands(Settings.leaveCommands, offlinePlayer);
                }
                // Deduct a reset
                if (Settings.leaversLoseReset && Settings.resetLimit >= 0) {
                    int resetsLeft = plugin.getPlayers().getResetsLeft(playerUUID);
                    if (resetsLeft > 0) {
                        resetsLeft--;
                        plugin.getPlayers().setResetsLeft(playerUUID, resetsLeft);
                    }
                }
            }
            // Fire event
            if (teamLeader != null) {
                final Island island = plugin.getGrid().getIsland(teamLeader);
                final IslandLeaveEvent event = new IslandLeaveEvent(playerUUID, island);
                plugin.getServer().getPluginManager().callEvent(event);
            }
        } else {
            // Ex-Leaders keeps their island, but the rest of the team members are
            // removed
            if (!plugin.getPlayers().setLeaveTeam(playerUUID)) {
                // Event was cancelled for some reason
                return false;
            }
        }
        return true;
    }

    public static void runCommands(List<String> commands, OfflinePlayer offlinePlayer) {
        // Run commands
        for (String cmd : commands) {
            if (cmd.startsWith("[SELF]")) {
                cmd = cmd.substring(6,cmd.length()).replace("[player]", offlinePlayer.getName()).trim();
                if (offlinePlayer.isOnline()) {
                    try {
                        Bukkit.getLogger().info("Running command '" + cmd + "' as " + offlinePlayer.getName());
                        ((Player)offlinePlayer).performCommand(cmd);
                    } catch (Exception e) {
                        Bukkit.getLogger().severe("Problem executing island command executed by player - skipping!");
                        Bukkit.getLogger().severe("Command was : " + cmd);
                        Bukkit.getLogger().severe("Error was: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                continue;
            }
            // Substitute in any references to player
            try {
                //plugin.getLogger().info("Running command " + cmd + " as console.");
                if (!Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.replace("[player]", offlinePlayer.getName()))) {
                    Bukkit.getLogger().severe("Problem executing island command - skipping!");
                    Bukkit.getLogger().severe("Command was : " + cmd);
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe("Problem executing island command - skipping!");
                Bukkit.getLogger().severe("Command was : " + cmd);
                Bukkit.getLogger().severe("Error was: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

}
