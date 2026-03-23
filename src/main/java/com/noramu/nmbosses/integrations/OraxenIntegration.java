package com.noramu.nmbosses.integrations;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
//import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Oraxen integration utility
 * TEMPORARILY DISABLED - Will be re-enabled when API is available
 */
public class OraxenIntegration {

    /**
     * Check if Oraxen is installed
     */
    public static boolean isEnabled() {
        return NmBosses.getInstance().getServer().getPluginManager().getPlugin("Oraxen") != null;
    }

    /**
     * Get Oraxen custom item by ID
     */
    public static ItemStack getCustomItem(String itemId) {
        // TEMPORARILY DISABLED
        return null;
        /*
        if (!isEnabled() || itemId == null || itemId.isEmpty()) {
            return null;
        }

        try {
            ItemStack item = OraxenItems.getItemById(itemId).build();
            if (item != null) {
                DebugLogger.log("Oraxen", "Loaded custom item: " + itemId);
                return item;
            }
        } catch (Exception e) {
            DebugLogger.log("Oraxen", "Error loading custom item: " + itemId, e);
        }

        return null;
        */
    }

    /**
     * Check if item is Oraxen custom item
     */
    public static boolean isCustomItem(ItemStack item) {
        // TEMPORARILY DISABLED
        return false;
        /*
        if (!isEnabled() || item == null) {
            return false;
        }

        try {
            return OraxenItems.getIdByItem(item) != null;
        } catch (Exception e) {
            return false;
        }
        */
    }

    /**
     * Get Oraxen custom item ID
     */
    public static String getCustomItemId(ItemStack item) {
        // TEMPORARILY DISABLED
        return null;
        /*
        if (!isEnabled() || item == null) {
            return null;
        }

        try {
            return OraxenItems.getIdByItem(item);
        } catch (Exception e) {
            DebugLogger.log("Oraxen", "Error getting custom item ID", e);
        }

        return null;
        */
    }

    /**
     * Get all available Oraxen custom items
     */
    public static List<String> getAvailableItems() {
        List<String> items = new ArrayList<>();

        if (!isEnabled()) {
            return items;
        }

        try {
            // Oraxen API doesn't provide a direct way to list all items
            // This would need to be implemented based on Oraxen's actual API
            DebugLogger.log("Oraxen", "Custom item listing not yet implemented");
        } catch (Exception e) {
            DebugLogger.log("Oraxen", "Error getting available items", e);
        }

        return items;
    }
}
