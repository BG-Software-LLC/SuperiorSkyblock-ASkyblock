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

import com.wasteofplastic.askyblock.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles offline messaging to players and teams
 * 
 * @author tastybento
 * 
 */
public class Messages {
    private final ASkyBlock plugin;
    // Offline Messages
    private final HashMap<UUID, List<String>> messages = new HashMap<>();
    private YamlConfiguration messageStore;


    /**
     * @param plugin - ASkyBlock plugin object
     */
    public Messages(ASkyBlock plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns what messages are waiting for the player or null if none
     * 
     * @param playerUUID - the player's UUID - player's UUID
     * @return List of messages
     */
    public List<String> getMessages(UUID playerUUID) {
        return messages.get(playerUUID);
    }

    /**
     * Clears any messages for player
     * 
     * @param playerUUID - the player's UUID - player's UUID
     */
    public void clearMessages(UUID playerUUID) {
        messages.remove(playerUUID);
    }

    public void saveMessages(boolean async) {
        if (messageStore == null) {
            return;
        }
        plugin.getLogger().info("Saving offline messages...");
        try {
            // Convert to a serialized string
            Map<String, Object> offlineMessages = new HashMap<>();
            for (UUID p : messages.keySet()) {
                if (p != null) {
                    offlineMessages.put(p.toString(), messages.get(p));
                }
            }
            // Convert to YAML
            messageStore.set("messages", offlineMessages);
            Util.saveYamlFile(messageStore, "messages.yml", async);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean loadMessages() {
        plugin.getLogger().info("Loading offline messages...");
        try {
            messageStore = Util.loadYamlFile("messages.yml");
            if (messageStore.getConfigurationSection("messages") == null) {
                messageStore.createSection("messages"); // This is only used to
                // create
            }
            HashMap<String, Object> temp = (HashMap<String, Object>) messageStore.getConfigurationSection("messages").getValues(true);
            for (String s : temp.keySet()) {
                List<String> messageList = messageStore.getStringList("messages." + s);
                if (!messageList.isEmpty()) {
                    messages.put(UUID.fromString(s), messageList);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Provides the messages for the player
     * 
     * @param playerUUID - the player's UUID - player's UUID
     * @return List of messages
     */
    public List<String> get(UUID playerUUID) {
        return messages.get(playerUUID);
    }

    /**
     * Stores a message for player
     * 
     * @param playerUUID - the player's UUID
     * @param playerMessages
     */
    public void put(UUID playerUUID, List<String> playerMessages) {
        messages.put(playerUUID, playerMessages);

    }

    /**
     * Sends a message to every player in the team that is offline
     * 
     * @param playerUUID - the player's UUID - player's UUID
     * @param message - message to send
     */
    public void tellOfflineTeam(UUID playerUUID, String message) {
        if (!plugin.getPlayers().inTeam(playerUUID)) {
            return;
        }
        UUID teamLeader = plugin.getPlayers().getTeamLeader(playerUUID);
        List<UUID> teamMembers = plugin.getPlayers().getMembers(teamLeader);
        // Offline player
        teamMembers.stream().filter(member -> plugin.getServer().getPlayer(member) == null)
            .forEach(member -> setMessage(member, message));
    }

    /**
     * Tells all online team members something happened
     * 
     * @param playerUUID - the player's UUID - player's UUID
     * @param message - message to send
     */
    public void tellTeam(UUID playerUUID, String message) {
        if (!plugin.getPlayers().inTeam(playerUUID)) {
            return;
        }
        UUID teamLeader = plugin.getPlayers().getTeamLeader(playerUUID);
        List<UUID> teamMembers = plugin.getPlayers().getMembers(teamLeader);
        for (UUID member : teamMembers) {
            if (!member.equals(playerUUID) && plugin.getServer().getPlayer(member) != null) {
                // Online player
                Util.sendMessage(plugin.getServer().getPlayer(member), message);
            }
        }
    }

    /**
     * Sets a message for the player to receive next time they login
     * 
     * @param playerUUID - the player's UUID - player's UUID
     * @param message - message to set
     * @return true if player is offline, false if online
     */
    public boolean setMessage(UUID playerUUID, String message) {
        Player player = plugin.getServer().getPlayer(playerUUID);
        // Check if player is online
        if (player != null) {
            if (player.isOnline()) {
                // Util.sendMessage(player, message);
                return false;
            }
        }
        storeMessage(playerUUID, message);
        return true;
    }

    /**
     * Stores a message without any online check
     * @param playerUUID - the player's UUID - player's UUID
     * @param message - message to store
     */
    public void storeMessage(UUID playerUUID, String message) {
        List<String> playerMessages = get(playerUUID);
        if (playerMessages != null) {
            playerMessages.add(message);
        } else {
            playerMessages = new ArrayList<>(Collections.singletonList(message));
        }
        put(playerUUID, playerMessages);
    }
}
