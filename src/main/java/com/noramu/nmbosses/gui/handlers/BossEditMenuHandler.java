package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BossEditMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public BossEditMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String bossId) {
        Material mat = clicked.getType();
        
        if (mat == Material.ARROW) {
            plugin.getGUIManager().openBossListMenu(player, 0);
        } else if (mat == Material.DIAMOND_SWORD) {
            plugin.getGUIManager().openStatsMenu(player, bossId);
        } else if (mat == Material.ARMOR_STAND) {
            plugin.getGUIManager().openAppearanceMenu(player, bossId);
        } else if (mat == Material.NETHERITE_CHESTPLATE) {
            session.setEditingBoss(player.getUniqueId(), bossId);
            plugin.getGUIManager().openEquipmentMenu(player, bossId);
        } else if (mat == Material.ENDER_PEARL) {
            plugin.getGUIManager().openSpawnMenu(player, bossId);
        } else if (mat == Material.CHEST) {
            session.setEditingBoss(player.getUniqueId(), bossId);
            plugin.getGUIManager().openDropsMenu(player, bossId);
        } else if (mat == Material.COMMAND_BLOCK) {
            session.setEditingBoss(player.getUniqueId(), bossId);
            plugin.getGUIManager().openCommandsMenu(player, bossId);
        } else if (mat == Material.BLAZE_POWDER) {
            session.setEditingBoss(player.getUniqueId(), bossId);
            plugin.getGUIManager().openAbilitiesMenu(player, bossId);
        } else if (mat == Material.REDSTONE) {
            session.setEditingBoss(player.getUniqueId(), bossId);
            plugin.getGUIManager().openPhasesMenu(player, bossId);
        } else if (mat == Material.SCULK_SENSOR) {
            // Models menu
            session.setEditingBoss(player.getUniqueId(), bossId);
            plugin.getGUIManager().openModelsMenu(player, bossId);
        } else if (mat == Material.DRAGON_EGG) {
            player.closeInventory();
            boolean spawned = plugin.getBossManager().spawnBoss(bossId, player.getLocation()) != null;
            player.sendMessage(spawned ? "§aSpawn successful!" : "§cSpawn failed!");
        } else if (mat == Material.TNT) {
            plugin.getBossManager().despawnBoss(bossId);
            player.sendMessage("§aBoss removed!");
        } else if (mat == Material.LAVA_BUCKET) {
            player.closeInventory();
            player.sendMessage("§eType 'confirm' to DELETE this boss:");
            session.setWaitingForInput(player.getUniqueId(), "confirm_delete");
            session.setInputType(player.getUniqueId(), bossId);
        }
    }
}
