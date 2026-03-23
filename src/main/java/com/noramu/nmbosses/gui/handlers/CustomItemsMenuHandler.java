package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.gui.menus.CustomItemsMenu;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handler for custom items menu interactions
 */
public class CustomItemsMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper configHelper;
    private final CustomItemsMenu customItemsMenu;

    public CustomItemsMenuHandler(NmBosses plugin) {
        this.plugin = plugin;
        this.configHelper = new ConfigHelper(plugin);
        this.customItemsMenu = new CustomItemsMenu(plugin);
    }

    /**
     * Handle equipment custom items selection
     */
    public void handleEquipmentCustomItemsClick(InventoryClickEvent event, String bossId, String slot) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        String itemNamespace = getItemNamespace(clicked);
        if (itemNamespace != null) {
            configHelper.setEquipmentItem(bossId, slot, itemNamespace);
            DebugLogger.log("CustomItems", "Set " + slot + " to " + itemNamespace + " for boss " + bossId);
            player.closeInventory();
        }
    }

    /**
     * Handle drop custom items selection
     */
    public void handleDropCustomItemsClick(InventoryClickEvent event, String bossId) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        String itemNamespace = getItemNamespace(clicked);
        if (itemNamespace != null) {
            configHelper.addCustomItemDrop(bossId, itemNamespace, 100.0);
            DebugLogger.log("CustomItems", "Added " + itemNamespace + " to drops for boss " + bossId);
            player.openInventory(customItemsMenu.openCustomItemDropsListMenu(bossId));
        }
    }

    /**
     * Handle custom item drops list removal
     */
    public void handleCustomItemDropsListClick(InventoryClickEvent event, String bossId) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        if (slot >= 53) {
            return; // Back button
        }

        configHelper.removeCustomItemDrop(bossId, slot);
        DebugLogger.log("CustomItems", "Removed custom item drop at index " + slot + " for boss " + bossId);
        player.openInventory(customItemsMenu.openCustomItemDropsListMenu(bossId));
    }

    /**
     * Extract item namespace from ItemMeta lore
     */
    private String getItemNamespace(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return null;
        }

        java.util.List<String> lore = item.getItemMeta().getLore();
        if (lore == null || lore.isEmpty()) {
            return null;
        }

        for (String line : lore) {
            if (line.contains("Namespace:")) {
                // Extract namespace from "Namespace: itemsadder:custom_item"
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    return parts[parts.length - 1].trim();
                }
            }
        }

        return null;
    }
}
