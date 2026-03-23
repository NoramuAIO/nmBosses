package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.gui.menus.ModelsMenu;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Handler for models menu interactions
 */
public class ModelsMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper configHelper;
    private final ModelsMenu modelsMenu;
    private final SessionData session;

    public ModelsMenuHandler(NmBosses plugin, ConfigHelper configHelper, SessionData session) {
        this.plugin = plugin;
        this.configHelper = configHelper;
        this.session = session;
        this.modelsMenu = new ModelsMenu(plugin);
    }

    /**
     * Handle models menu click
     */
    public void handleModelsMenuClick(Player player, ItemStack clicked, String bossId, int slot) {
        if (clicked.getType() == Material.BARRIER) {
            plugin.getGUIManager().openBossEditMenu(player, bossId);
            return;
        }

        if (clicked.getType() == Material.ARMOR_STAND) {
            // ModelEngine
            player.openInventory(modelsMenu.openModelEngineConfigMenu(bossId));
            return;
        }

        if (clicked.getType() == Material.DRAGON_EGG) {
            // MythicMobs
            player.openInventory(modelsMenu.openMythicMobsConfigMenu(bossId));
            return;
        }

        if (clicked.getType() == Material.DIAMOND_PICKAXE) {
            // Oraxen
            session.setWaitingForInput(player.getUniqueId(), "set_oraxen_model");
            session.setEditingBoss(player.getUniqueId(), bossId);
            player.sendMessage("§eEnter Oraxen model ID in chat:");
            player.closeInventory();
            return;
        }

        if (clicked.getType() == Material.GOLDEN_APPLE) {
            // ItemsAdder
            session.setWaitingForInput(player.getUniqueId(), "set_itemsadder_model");
            session.setEditingBoss(player.getUniqueId(), bossId);
            player.sendMessage("§eEnter ItemsAdder model ID in chat:");
            player.closeInventory();
            return;
        }

        if (clicked.getType() == Material.AMETHYST_CLUSTER) {
            // Nexo
            session.setWaitingForInput(player.getUniqueId(), "set_nexo_model");
            session.setEditingBoss(player.getUniqueId(), bossId);
            player.sendMessage("§eEnter Nexo model ID in chat:");
            player.closeInventory();
            return;
        }
    }

    /**
     * Handle ModelEngine config menu click
     */
    public void handleModelEngineConfigClick(Player player, ItemStack clicked, String bossId, int slot) {
        if (clicked.getType() == Material.BARRIER) {
            player.openInventory(modelsMenu.openModelsMenu(bossId));
            return;
        }

        if (clicked.getType() == Material.LIME_CONCRETE || clicked.getType() == Material.RED_CONCRETE) {
            // Toggle enable/disable
            boolean enabled = configHelper.isModelPluginEnabled(bossId, "modelEngine");
            configHelper.setModelPluginEnabled(bossId, "modelEngine", !enabled);
            DebugLogger.log("Models", "ModelEngine toggled to " + !enabled + " for boss " + bossId);
            player.openInventory(modelsMenu.openModelEngineConfigMenu(bossId));
            return;
        }

        if (clicked.getType() == Material.ARMOR_STAND) {
            // Edit model ID
            session.setWaitingForInput(player.getUniqueId(), "set_modelengine_model");
            session.setEditingBoss(player.getUniqueId(), bossId);
            player.sendMessage("§eEnter ModelEngine model ID in chat:");
            player.closeInventory();
            return;
        }
    }

    /**
     * Handle MythicMobs config menu click
     */
    public void handleMythicMobsConfigClick(Player player, ItemStack clicked, String bossId, int slot) {
        if (clicked.getType() == Material.BARRIER) {
            player.openInventory(modelsMenu.openModelsMenu(bossId));
            return;
        }

        if (clicked.getType() == Material.LIME_CONCRETE || clicked.getType() == Material.RED_CONCRETE) {
            // Toggle enable/disable
            boolean enabled = configHelper.isModelPluginEnabled(bossId, "mythicMobs");
            configHelper.setModelPluginEnabled(bossId, "mythicMobs", !enabled);
            DebugLogger.log("Models", "MythicMobs toggled to " + !enabled + " for boss " + bossId);
            player.openInventory(modelsMenu.openMythicMobsConfigMenu(bossId));
            return;
        }

        if (clicked.getType() == Material.DRAGON_EGG) {
            // Edit mob type
            session.setWaitingForInput(player.getUniqueId(), "set_mythicmobs_type");
            session.setEditingBoss(player.getUniqueId(), bossId);
            player.sendMessage("§eEnter MythicMobs mob type in chat:");
            player.closeInventory();
            return;
        }

        if (clicked.getType() == Material.BOOK) {
            // Manage abilities
            session.setWaitingForInput(player.getUniqueId(), "manage_mythicmobs_abilities");
            session.setEditingBoss(player.getUniqueId(), bossId);
            player.sendMessage("§eEnter ability ID to add (or 'list' to view):");
            player.closeInventory();
            return;
        }
    }
}
