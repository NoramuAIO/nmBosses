package com.noramu.nmbosses.integrations;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
//import com.nexomc.nexo.api.NexoItems;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Nexo integration utility
 * TEMPORARILY DISABLED - Will be re-enabled when API is available
 */
public class NexoIntegration {

    /**
     * Check if Nexo is installed
     */
    public static boolean isEnabled() {
        return NmBosses.getInstance().getServer().getPluginManager().getPlugin("Nexo") != null;
    }

    /**
     * Get Nexo custom item by ID
     */
    public static ItemStack getCustomItem(String itemId) {
        // TEMPORARILY DISABLED
        return null;
        /*
        if (!isEnabled() || itemId == null || itemId.isEmpty()) {
            return null;
        }

        try {
            ItemStack item = NexoItems.itemFromId(itemId);
            if (item != null) {
                DebugLogger.log("Nexo", "Loaded custom item: " + itemId);
                return item;
            }
        } catch (Exception e) {
            DebugLogger.log("Nexo", "Error loading custom item: " + itemId, e);
        }

        return null;
        */
    }

    /**
     * Check if item is Nexo custom item
     */
    public static boolean isCustomItem(ItemStack item) {
        // TEMPORARILY DISABLED
        return false;
        /*
        if (!isEnabled() || item == null) {
            return false;
        }

        try {
            return NexoItems.idFromItem(item) != null;
        } catch (Exception e) {
            return false;
        }
        */
    }

    /**
     * Get Nexo custom item ID
     */
    public static String getCustomItemId(ItemStack item) {
        // TEMPORARILY DISABLED
        return null;
        /*
        if (!isEnabled() || item == null) {
            return null;
        }

        try {
            return NexoItems.idFromItem(item);
        } catch (Exception e) {
            DebugLogger.log("Nexo", "Error getting custom item ID", e);
        }

        return null;
        */
    }

    /**
     * Get all available Nexo custom items
     */
    public static List<String> getAvailableItems() {
        List<String> items = new ArrayList<>();

        if (!isEnabled()) {
            return items;
        }

        try {
            // Nexo API doesn't provide a direct way to list all items
            // This would need to be implemented based on Nexo's actual API
            DebugLogger.log("Nexo", "Custom item listing not yet implemented");
        } catch (Exception e) {
            DebugLogger.log("Nexo", "Error getting available items", e);
        }

        return items;
    }
}
