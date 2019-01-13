package com.ome_r.superiorskyblock.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class WrappedLocation {

    private final int x, y, z;
    private final String world;

    public WrappedLocation(Location location){
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public WrappedLocation(World world, int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world.getName();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public World getWorld(){
        return Bukkit.getWorld(world);
    }

    public Location parseLocation(){
        return new Location(getWorld(), x, y, z);
    }

    public Vector toVector(){
        return parseLocation().toVector();
    }

    public Block getBlock(){
        return parseLocation().getBlock();
    }

    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof WrappedLocation)
            return parseLocation().equals(((WrappedLocation) obj).parseLocation());
        else if(obj instanceof Location)
            return parseLocation().equals(obj);
        return false;
    }
}
