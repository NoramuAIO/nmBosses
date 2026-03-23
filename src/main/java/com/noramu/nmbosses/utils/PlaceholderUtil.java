package com.noramu.nmbosses.utils;

import com.noramu.nmbosses.NmBosses;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI integration utility
 */
public class PlaceholderUtil {

    /**
     * Check if PlaceholderAPI is installed
     */
    public static boolean isPlaceholderAPIEnabled() {
        return NmBosses.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    /**
     * Parse placeholders in a message for a player
     */
    public static String parsePlaceholders(Player player, String message) {
        if (!isPlaceholderAPIEnabled() || player == null || message == null) {
            return message;
        }
        
        try {
            return PlaceholderAPI.setPlaceholders(player, message);
        } catch (Exception e) {
            DebugLogger.log("PlaceholderUtil", "Error parsing placeholders", e);
            return message;
        }
    }

    /**
     * Parse placeholders in a message without a player
     */
    public static String parsePlaceholders(String message) {
        if (!isPlaceholderAPIEnabled() || message == null) {
            return message;
        }
        
        try {
            return PlaceholderAPI.setPlaceholders(null, message);
        } catch (Exception e) {
            DebugLogger.log("PlaceholderUtil", "Error parsing placeholders", e);
            return message;
        }
    }
}
