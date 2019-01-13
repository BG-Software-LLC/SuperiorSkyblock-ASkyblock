package com.wasteofplastic.askyblock.commands.regular;

import com.ome_r.superiorskyblock.Locale;
import com.ome_r.superiorskyblock.legacy.LegacyMaterial;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.GridManager;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.commands.ICommand;
import com.wasteofplastic.askyblock.events.IslandPreTeleportEvent;
import com.wasteofplastic.askyblock.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CmdWarp implements ICommand {

    private ASkyBlock plugin = ASkyBlock.getPlugin(ASkyBlock.class);

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("warp");
    }

    @Override
    public String getPermission() {
        return "askyblock.island.warp";
    }

    @Override
    public String getUsage() {
        return "is warp <player>";
    }

    @Override
    public String getDescription() {
        return "Warp to player's welcome sign.";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public void perform(ASkyBlock plugin, CommandSender sender, String[] args) {
        Player player = (Player) sender;

        final Set<UUID> warpList = plugin.getWarpSignsListener().listWarps();

        if (warpList.isEmpty()) {
            Locale.NO_ISLAND_WARPS.send(player);
            if(player.hasPermission("askyblock.island.addwarp")){
                Locale.ISLAND_WARP_TIP.send(player);
            }
            return;
        }

        UUID targetUUID = plugin.getPlayers().getUUID(args[1]);
        Location warpSpot = targetUUID == null ? null : plugin.getWarpSignsListener().getWarp(targetUUID);

        if(warpSpot == null){
            Locale.INVALID_WARP.send(player);
            return;
        }

        // Find out if island is locked
        Island island = plugin.getGrid().getIslandAt(warpSpot);

        if(island != null && !player.hasPermission("askyblock.mod.bypasslock") && !player.hasPermission("askyblock.mod.bypassprotect")){
            if(plugin.getPlayers().isBanned(island.getOwner(), player.getUniqueId())){
                Locale.BANNED_FROM_ISLAND.send(player, Bukkit.getOfflinePlayer(island.getOwner()).getName());
                return;
            }

            if(island.isLocked()){
                Locale.ISLAND_LOCKED.send(player, Bukkit.getOfflinePlayer(island.getOwner()).getName());
                return;
            }
        }

        boolean pvp = false;
        if (island != null && ((warpSpot.getWorld().equals(ASkyBlock.getIslandWorld()) && island.getIgsFlag(Island.SettingsFlag.PVP))
                || (warpSpot.getWorld().equals(ASkyBlock.getNetherWorld()) && island.getIgsFlag(Island.SettingsFlag.NETHER_PVP)))) {
            pvp = true;
        }

        // Find out which direction the warp is facing
        Block b = warpSpot.getBlock();
        if (b.getType().equals(LegacyMaterial.SIGN_WALL) || b.getType().equals(Material.WALL_SIGN)) {
            Sign sign = (Sign) b.getState();
            org.bukkit.material.Sign s = (org.bukkit.material.Sign) sign.getData();
            BlockFace directionFacing = s.getFacing();
            Location inFront = b.getRelative(directionFacing).getLocation();
            Location oneDown = b.getRelative(directionFacing).getRelative(BlockFace.DOWN).getLocation();
            if ((GridManager.isSafeLocation(inFront))) {
                warpPlayer(player, inFront, targetUUID, directionFacing, pvp);
                return;
            } else if (b.getType().equals(Material.WALL_SIGN) && GridManager.isSafeLocation(oneDown)) {
                // Try one block down if this is a wall sign
                warpPlayer(player, oneDown, targetUUID, directionFacing, pvp);
                return;
            }
        } else {
            // Warp has been removed
            Locale.INVALID_WARP.send(player);
            plugin.getWarpSignsListener().removeWarp(warpSpot);
            return;
        }

        if (!(GridManager.isSafeLocation(warpSpot))) {
            Locale.UNSAFE_ISLAND_WARP.send(player);
            return;
        }

        Location actualWarp = new Location(warpSpot.getWorld(), warpSpot.getBlockX() + 0.5D, warpSpot.getBlockY(),warpSpot.getBlockZ() + 0.5D);
        player.teleport(actualWarp);

        if (pvp) {
            Util.sendMessage(player, ChatColor.BOLD + "" + ChatColor.RED + plugin.myLocale(player.getUniqueId()).igs.get(Island.SettingsFlag.PVP) + " " + plugin.myLocale(player.getUniqueId()).igsAllowed);
            if (plugin.getServer().getVersion().contains("(MC: 1.8") || plugin.getServer().getVersion().contains("(MC: 1.7")) {
                player.getWorld().playSound(player.getLocation(), Sound.valueOf("ARROW_HIT"), 1F, 1F);
            } else {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 1F, 1F);
            }
        } else {
            if (plugin.getServer().getVersion().contains("(MC: 1.8") || plugin.getServer().getVersion().contains("(MC: 1.7")) {
                player.getWorld().playSound(player.getLocation(), Sound.valueOf("BAT_TAKEOFF"), 1F, 1F);
            } else {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1F, 1F);
            }
        }

    }

    @Override
    public void tabComplete(ASkyBlock plugin, CommandSender sender, String[] args) {

    }

    private void warpPlayer(Player player, Location inFront, UUID foundWarp, BlockFace directionFacing, boolean pvp) {
        // convert blockface to angle
        float yaw = Util.blockFaceToFloat(directionFacing);
        final Location actualWarp = new Location(inFront.getWorld(), inFront.getBlockX() + 0.5D, inFront.getBlockY(),
                inFront.getBlockZ() + 0.5D, yaw, 30F);
        IslandPreTeleportEvent event = new IslandPreTeleportEvent(player, IslandPreTeleportEvent.Type.WARP, actualWarp);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            player.teleport(event.getLocation());
            if (pvp) {
                Util.sendMessage(player, ChatColor.BOLD + "" + ChatColor.RED + plugin.myLocale(player.getUniqueId()).igs.get(Island.SettingsFlag.PVP) + " " + plugin.myLocale(player.getUniqueId()).igsAllowed);
                if (plugin.getServer().getVersion().contains("(MC: 1.8") || plugin.getServer().getVersion().contains("(MC: 1.7")) {
                    player.getWorld().playSound(player.getLocation(), Sound.valueOf("ARROW_HIT"), 1F, 1F);
                } else {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 1F, 1F);
                }
            } else {
                if (plugin.getServer().getVersion().contains("(MC: 1.8") || plugin.getServer().getVersion().contains("(MC: 1.7")) {
                    player.getWorld().playSound(player.getLocation(), Sound.valueOf("BAT_TAKEOFF"), 1F, 1F);
                } else {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1F, 1F);
                }
            }
            Player warpOwner = plugin.getServer().getPlayer(foundWarp);
            if (warpOwner != null && !warpOwner.equals(player)) {
                Util.sendMessage(warpOwner, plugin.myLocale(foundWarp).warpsPlayerWarped.replace("[name]", player.getName()));
            }
        }
    }

}
