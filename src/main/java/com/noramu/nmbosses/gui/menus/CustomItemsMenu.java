package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.gui.handlers.ConfigHelper;
import com.noramu.nmbosses.utils.GuiConstants;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom items selection menu for boss equipment and drops
 */
public class CustomItemsMenu {

    private final NmBosses plugin;
    private final ConfigHelper configHelper;

    public CustomItemsMenu(NmBosses plugin) {
        this.plugin = plugin;
        this.configHelper = new ConfigHelper(plugin);
    }

    /**
     * Open custom items menu for equipment slot selection
     */
    public Inventory openEquipmentCustomItemsMenu(String bossId, String slot) {
        Inventory inv = Bukkit.createInventory(null, 54, StringUtils.colorize("&6Custom Items - " + slot));

        List<String> availableItems = configHelper.getAvailableCustomItems();

        int slot_index = 0;
        for (String itemNamespace : availableItems) {
            if (slot_index >= 54) break;

            ItemStack customItem = configHelper.getCustomItem(itemNamespace);
            if (customItem == null) continue;

            ItemStack displayItem = customItem.clone();
            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>();
                lore.add(StringUtils.colorize("&7Namespace: &f" + itemNamespace));
                lore.add("");
                lore.add(StringUtils.colorize("&aClick to select"));
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }

            inv.setItem(slot_index, displayItem);
            slot_index++;
        }

        // Add back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(StringUtils.colorize(GuiConstants.BTN_BACK));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(53, backButton);

        return inv;
    }

    /**
     * Open custom items menu for drop rewards
     */
    public Inventory openDropCustomItemsMenu(String bossId) {
        Inventory inv = Bukkit.createInventory(null, 54, StringUtils.colorize("&6Custom Item Drops"));

        List<String> availableItems = configHelper.getAvailableCustomItems();

        int slot_index = 0;
        for (String itemNamespace : availableItems) {
            if (slot_index >= 54) break;

            ItemStack customItem = configHelper.getCustomItem(itemNamespace);
            if (customItem == null) continue;

            ItemStack displayItem = customItem.clone();
            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>();
                lore.add(StringUtils.colorize("&7Namespace: &f" + itemNamespace));
                lore.add("");
                lore.add(StringUtils.colorize("&aClick to add as drop"));
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }

            inv.setItem(slot_index, displayItem);
            slot_index++;
        }

        // Add back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(StringUtils.colorize(GuiConstants.BTN_BACK));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(53, backButton);

        return inv;
    }

    /**
     * Open custom item drops list menu
     */
    public Inventory openCustomItemDropsListMenu(String bossId) {
        Inventory inv = Bukkit.createInventory(null, 54, StringUtils.colorize("&6Custom Item Drops"));

        List<String> drops = configHelper.getCustomItemDrops(bossId);

        int slot_index = 0;
        for (String drop : drops) {
            if (slot_index >= 54) break;

            String[] parts = drop.split(":");
            String itemNamespace = parts[0];
            double chance = parts.length > 1 ? Double.parseDouble(parts[1]) : 100.0;

            ItemStack customItem = configHelper.getCustomItem(itemNamespace);
            if (customItem == null) continue;

            ItemStack displayItem = customItem.clone();
            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>();
                lore.add(StringUtils.colorize("&7Namespace: &f" + itemNamespace));
                lore.add(StringUtils.colorize("&7Drop Chance: &f" + chance + "%"));
                lore.add("");
                lore.add(StringUtils.colorize("&cClick to remove"));
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }

            inv.setItem(slot_index, displayItem);
            slot_index++;
        }

        // Add back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(StringUtils.colorize(GuiConstants.BTN_BACK));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(53, backButton);

        return inv;
    }
}
