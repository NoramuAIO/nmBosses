package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.gui.menus.CustomItemsMenu;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Drops Menu click handler
 */
public class DropsMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;
    private final CustomItemsMenu customItemsMenu;

    public DropsMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
        this.customItemsMenu = new CustomItemsMenu(plugin);
    }

    public void handle(Player player, ItemStack clicked, String bossId, ClickType click, int slot) {
        Material mat = clicked.getType();
        
        if (mat == Material.ARROW) {
            plugin.getGUIManager().openBossEditMenu(player, bossId);
            return;
        }
        
        if (mat == Material.EXPERIENCE_BOTTLE) {
            double change = click.isShiftClick() ? 500 : 50;
            if (click.isLeftClick()) change = -change;
            config.updateConfig(bossId, "rewards.xp", (int)Math.max(0, config.getCurrentDoubleValue(bossId, "rewards.xp") + change));
            plugin.getGUIManager().openDropsMenu(player, bossId);
            return;
        }
        
        if (mat == Material.RABBIT_FOOT) {
            double change = click.isLeftClick() ? -5 : 5;
            config.updateConfig(bossId, "rewards.dropChance", Math.max(0, Math.min(100, config.getCurrentDoubleValue(bossId, "rewards.dropChance") + change)));
            plugin.getGUIManager().openDropsMenu(player, bossId);
            return;
        }
        
        if (mat == Material.LIME_DYE) {
            session.setWaitingForInput(player.getUniqueId(), "add_drop_item");
            session.setEditingBoss(player.getUniqueId(), bossId);
            player.sendMessage("§eClick an item from your inventory!");
            return;
        }

        // Right-click to open custom items menu
        if (mat == Material.LIME_DYE && click.isRightClick()) {
            player.openInventory(customItemsMenu.openDropCustomItemsMenu(bossId));
            return;
        }

        // View custom item drops
        if (mat == Material.DIAMOND) {
            player.openInventory(customItemsMenu.openCustomItemDropsListMenu(bossId));
            return;
        }
        
        // Remove drop item with shift+click
        for (int i = 0; i < GuiConstants.DROP_SLOTS.length; i++) {
            if (GuiConstants.DROP_SLOTS[i] == slot && click.isShiftClick()) {
                config.removeDropItem(bossId, i);
                player.sendMessage("§eDrop removed!");
                plugin.getGUIManager().openDropsMenu(player, bossId);
                return;
            }
        }
    }
}
