package com.wasteofplastic.askyblock.util;

import com.ome_r.superiorskyblock.legacy.LegacyMaterial;
import com.wasteofplastic.askyblock.ASkyBlock;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HeadGetter {
    private final Map<UUID,HeadInfo> cachedHeads = new HashMap<>();
    private final Map<UUID,String> names = new ConcurrentHashMap<>();
    private final Map<UUID,Set<Requester>> headRequesters = new HashMap<>();
    private final ASkyBlock plugin;
    /**
     * @param plugin
     */
    public HeadGetter(ASkyBlock plugin) {
        super();
        this.plugin = plugin;
        runPlayerHeadGetter();
    }

    private void runPlayerHeadGetter() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            synchronized(names) {
                Iterator<Entry<UUID,String>> it = names.entrySet().iterator();
                if (it.hasNext()) {
                    Entry<UUID,String> en = it.next();
                    ItemStack playerSkull = LegacyMaterial.getPlayerHead(1);
                    SkullMeta meta = (SkullMeta) playerSkull.getItemMeta();
                    meta.setOwner(en.getValue());
                    meta.setDisplayName(ChatColor.WHITE + en.getValue());
                    playerSkull.setItemMeta(meta);
                    // Save in cache
                    cachedHeads.put(en.getKey(), new HeadInfo(en.getValue(), en.getKey(), playerSkull));
                    // Tell requesters the head came in
                    if (headRequesters.containsKey(en.getKey())) {
                        for (Requester req : headRequesters.get(en.getKey())) {
                            plugin.getServer().getScheduler().runTask(plugin, () -> req.setHead(new HeadInfo(en.getValue(), en.getKey(), playerSkull)));
                        }
                    }
                    it.remove();
                }
            }
        }, 0L, 20L);
    }

    public void getHead(UUID playerUUID, Requester requester) {
        if (playerUUID == null) {
            return;
        }
        String name = plugin.getPlayers().getName(playerUUID);
        if (name == null || name.isEmpty()) {
            return;
        }
        // Check if in cache
        if (cachedHeads.containsKey(playerUUID)) {
            requester.setHead(cachedHeads.get(playerUUID));
        } else {
            // Get the name
            headRequesters.putIfAbsent(playerUUID, new HashSet<>());
            Set<Requester> requesters = headRequesters.get(playerUUID);
            requesters.add(requester);
            headRequesters.put(playerUUID, requesters);
            names.put(playerUUID, name);
        }
    }
    
    public class HeadInfo {
        String name;
        UUID uuid;
        ItemStack head;
        /**
         * @param name
         * @param uuid
         * @param head
         */
        public HeadInfo(String name, UUID uuid, ItemStack head) {
            this.name = name;
            this.uuid = uuid;
            this.head = head;
        }
        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
        /**
         * @return the uuid
         */
        public UUID getUuid() {
            return uuid;
        }
        /**
         * @return the head
         */
        public ItemStack getHead() {
            return head.clone();
        }

    }
}
