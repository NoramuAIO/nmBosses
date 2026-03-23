package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.gui.menus.CustomItemsMenu;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Equipment Menu click handler
 */
public class EquipmentMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;
    private final CustomItemsMenu customItemsMenu;

    public EquipmentMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
        this.customItemsMenu = new CustomItemsMenu(plugin);
    }

    public void handle(Player player, ItemStack clicked, String bossId, ClickType click, int slot) {
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openBossEditMenu(player, bossId);
            return;
        }
        
        if (clicked.getType() == Material.SHIELD || 
            (clicked.getItemMeta() != null && clicked.getItemMeta().getDisplayName().contains("Shield"))) {
            config.toggleConfig(bossId, "equipment.shield");
            plugin.getGUIManager().openEquipmentMenu(player, bossId);
            return;
        }

        // Right-click to open custom items menu
        if (click.isRightClick()) {
            String slotName = getSlotNameForCustomItems(slot);
            if (slotName != null) {
                player.openInventory(customItemsMenu.openEquipmentCustomItemsMenu(bossId, slotName));
            }
            return;
        }
        
        String configPath = getConfigPathForSlot(slot);
        if (configPath == null) return;
        
        if (click.isShiftClick()) {
            config.updateConfig(bossId, configPath, "AIR");
            player.sendMessage("§e" + configPath + " removed!");
            plugin.getGUIManager().openEquipmentMenu(player, bossId);
        } else {
            session.setEquipmentSlot(player.getUniqueId(), configPath);
            session.setEditingBoss(player.getUniqueId(), bossId);
            player.sendMessage("§eClick an item from your inventory!");
        }
    }

    private String getConfigPathForSlot(int slot) {
        switch (slot) {
            case GuiConstants.HELMET_SLOT: return "armor.helmet";
            case GuiConstants.CHESTPLATE_SLOT: return "armor.chestplate";
            case GuiConstants.LEGGINGS_SLOT: return "armor.leggings";
            case GuiConstants.BOOTS_SLOT: return "armor.boots";
            case GuiConstants.MAIN_HAND_SLOT: return "equipment.mainHand";
            case GuiConstants.OFF_HAND_SLOT: return "equipment.offHand";
            default: return null;
        }
    }

    private String getSlotNameForCustomItems(int slot) {
        switch (slot) {
            case GuiConstants.HELMET_SLOT: return "helmet";
            case GuiConstants.CHESTPLATE_SLOT: return "chestplate";
            case GuiConstants.LEGGINGS_SLOT: return "leggings";
            case GuiConstants.BOOTS_SLOT: return "boots";
            case GuiConstants.MAIN_HAND_SLOT: return "main_hand";
            case GuiConstants.OFF_HAND_SLOT: return "off_hand";
            default: return null;
        }
    }
}
