package com.ome_r.superiorskyblock;

import com.ome_r.superiorskyblock.handlers.DataHandler;
import com.ome_r.superiorskyblock.handlers.SettingsHandler;
import com.ome_r.superiorskyblock.handlers.TopHandler;
import com.ome_r.superiorskyblock.handlers.UpgradesHandler;
import com.ome_r.superiorskyblock.hooks.DropsHook;
import com.ome_r.superiorskyblock.hooks.DropsHook_CustomDrops;
import com.ome_r.superiorskyblock.hooks.DropsHook_Default;
import com.ome_r.superiorskyblock.hooks.DropsHook_DropEdit;
import com.ome_r.superiorskyblock.hooks.DropsHook_EditDrops;
import com.ome_r.superiorskyblock.hooks.PlaceholderHook;
import com.ome_r.superiorskyblock.listeners.BlocksListener;
import com.ome_r.superiorskyblock.listeners.InventoriesListener;
import com.ome_r.superiorskyblock.listeners.IslandTopListener;
import com.ome_r.superiorskyblock.listeners.IslandsListener;
import com.ome_r.superiorskyblock.listeners.UpgradesListener;
import com.ome_r.superiorskyblock.listeners.plugins.WildStackerListener;
import com.ome_r.superiorskyblock.utils.HologramUtils;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Updater;
import com.wasteofplastic.askyblock.handlers.SchematicsHandler;
import com.wasteofplastic.askyblock.nms.NMSAdapter;
import com.wasteofplastic.askyblock.util.Util;
import com.wasteofplastic.askyblock.util.VaultHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SuperiorSkyblock {

    private ASkyBlock plugin;

    private SettingsHandler settingsHandler;
    private UpgradesHandler upgradesHandler;
    private SchematicsHandler schematicsHandler;
    private TopHandler topHandler;

    private NMSAdapter nmsAbstraction;
    private DropsHook dropsHook;

    public SuperiorSkyblock(ASkyBlock plugin){
        this.plugin = plugin;
    }

    public void onEnable(){
        //Run the config load in the first tick
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            DataHandler.loadAllOldData();

            this.settingsHandler = new SettingsHandler(plugin);
            this.upgradesHandler = new UpgradesHandler(plugin);
            this.schematicsHandler = new SchematicsHandler(plugin);
            this.topHandler = new TopHandler(plugin);

            Locale.reload();

            plugin.getServer().getPluginManager().registerEvents(new BlocksListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new UpgradesListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new IslandsListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new IslandTopListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new InventoriesListener(plugin), plugin);

            if(Bukkit.getPluginManager().isPluginEnabled("WildStacker"))
                plugin.getServer().getPluginManager().registerEvents(new WildStackerListener(plugin), plugin);

            loadCustomDrops();

            //Load all the holograms
            for(Island island : plugin.getGrid().getOwnedIslands().values()){
                for(Location location : island.getStackedBlockLocations()){
                    Location hologramLocation = location.clone().add(0.5, 2, 0.5);
                    HologramUtils.updateHologram(hologramLocation, location.getBlock());
                }
            }

            if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
                PlaceholderHook.register();

            try{
                nmsAbstraction = Util.checkVersion();
            }catch(Exception ex){
                ex.printStackTrace();
            }

            if(!VaultHelper.setupEconomy()){
                log("Couldn't load Vault.");
                log("Make sure you have Vault & Economy plugin enabled.");
                log("Disabling plugin...");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }

            if(Updater.isOutdated()) {
                log("");
                log("A new version is available (v" + Updater.getLatestVersion() + ")!");
                log("Version's description: \"" + Updater.getVersionDescription() + "\"");
                log("");
            }

        }, 20L);
    }

    public void onDisable(){
        //Destroy all the holograms
        for(Island island : plugin.getGrid().getOwnedIslands().values()){
            for(Location location : island.getStackedBlockLocations()){
                Location hologramLocation = location.clone().add(0.5, 2, 0.5);
                HologramUtils.destroyHologram(hologramLocation);
            }
        }
        //Close all inventories
        for(Player player : Bukkit.getOnlinePlayers())
            player.closeInventory();
    }

    private void loadCustomDrops(){
        if(Bukkit.getPluginManager().isPluginEnabled("CustomDrops"))
            dropsHook = new DropsHook_CustomDrops();
        else if(Bukkit.getPluginManager().isPluginEnabled("DropEdit"))
            dropsHook = new DropsHook_DropEdit();
        else if(Bukkit.getPluginManager().isPluginEnabled("EditDrops"))
            dropsHook = new DropsHook_EditDrops();
        else
            dropsHook = new DropsHook_Default();
    }

    public DropsHook getDropsHook(){
        return dropsHook;
    }

    public SettingsHandler getSettings() {
        return settingsHandler;
    }

    public UpgradesHandler getUpgrades() {
        return upgradesHandler;
    }

    public SchematicsHandler getSchematics(){
        return schematicsHandler;
    }

    public TopHandler getTopHandler() {
        return topHandler;
    }

    public NMSAdapter getNMSAbstraction(){
        return nmsAbstraction;
    }

    public static void log(String message){
        for(String line : message.split("\n"))
            System.out.println("[SuperiorSkyblock] " + line);
    }

}
