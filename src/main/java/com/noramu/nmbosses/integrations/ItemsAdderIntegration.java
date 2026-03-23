package com.noramu.nmbosses.integrations;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemsAdder integration utility
 */
public class ItemsAdderIntegration {

    /**
     * Check if ItemsAdder is installed
     */
    public static boolean isEnabled() {
        return NmBosses.getInstance().getServer().getPluginManager().getPlugin("ItemsAdder") != null;
    }

    /**
     * Get ItemsAdder custom item by namespace
     */
    public static ItemStack getCustomItem(String namespace) {
        if (!isEnabled() || namespace == null || namespace.isEmpty()) {
            return null;
        }

        try {
            // Use reflection to avoid compile-time dependency
            Class<?> customStackClass = Class.forName("dev.lone.itemsadder.api.CustomStack");
            java.lang.reflect.Method getInstanceMethod = customStackClass.getMethod("getInstance", String.class);
            Object customStack = getInstanceMethod.invoke(null, namespace);
            
            if (customStack != null) {
                java.lang.reflect.Method getItemStackMethod = customStackClass.getMethod("getItemStack");
                ItemStack item = (ItemStack) getItemStackMethod.invoke(customStack);
                DebugLogger.log("ItemsAdder", "Loaded custom item: " + namespace);
                return item;
            }
        } catch (Exception e) {
            DebugLogger.log("ItemsAdder", "Error loading custom item: " + namespace, e);
        }

        return null;
    }

    /**
     * Check if item is ItemsAdder custom item
     */
    public static boolean isCustomItem(ItemStack item) {
        if (!isEnabled() || item == null) {
            return false;
        }

        try {
            Class<?> customStackClass = Class.forName("dev.lone.itemsadder.api.CustomStack");
            java.lang.reflect.Method byItemStackMethod = customStackClass.getMethod("byItemStack", ItemStack.class);
            return byItemStackMethod.invoke(null, item) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get ItemsAdder custom item namespace
     */
    public static String getCustomItemNamespace(ItemStack item) {
        if (!isEnabled() || item == null) {
            return null;
        }

        try {
            Class<?> customStackClass = Class.forName("dev.lone.itemsadder.api.CustomStack");
            java.lang.reflect.Method byItemStackMethod = customStackClass.getMethod("byItemStack", ItemStack.class);
            Object customStack = byItemStackMethod.invoke(null, item);
            
            if (customStack != null) {
                java.lang.reflect.Method getNamespaceMethod = customStackClass.getMethod("getNamespace");
                return (String) getNamespaceMethod.invoke(customStack);
            }
        } catch (Exception e) {
            DebugLogger.log("ItemsAdder", "Error getting custom item namespace", e);
        }

        return null;
    }

    /**
     * Get all available ItemsAdder custom items
     */
    public static List<String> getAvailableItems() {
        List<String> items = new ArrayList<>();

        if (!isEnabled()) {
            return items;
        }

        try {
            DebugLogger.log("ItemsAdder", "Custom item listing not yet implemented");
        } catch (Exception e) {
            DebugLogger.log("ItemsAdder", "Error getting available items", e);
        }

        return items;
    }
}
