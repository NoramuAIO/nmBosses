package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BossListMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public BossListMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked) {
        Material mat = clicked.getType();
        
        if (mat == Material.EMERALD) {
            player.closeInventory();
            player.sendMessage("§eEnter a name for the new boss:");
            session.setWaitingForInput(player.getUniqueId(), "new_boss_name");
            return;
        }
        
        if (mat == Material.BARRIER) {
            player.closeInventory();
            return;
        }
        
        if (mat == Material.COMMAND_BLOCK) {
            plugin.getBossManager().reloadBossConfigs();
            player.sendMessage("§aReload successful!");
            plugin.getGUIManager().openBossListMenu(player, 0);
            return;
        }
        
        if (mat == Material.ARROW) {
            if (clicked.getItemMeta() == null || clicked.getItemMeta().getDisplayName() == null) {
                return;
            }
            String name = clicked.getItemMeta().getDisplayName();
            if (name.contains("▶")) plugin.getGUIManager().openBossListMenu(player, 1);
            else if (name.contains("◀")) plugin.getGUIManager().openBossListMenu(player, 0);
            return;
        }
        
        if (clicked.hasItemMeta() && clicked.getItemMeta() != null && clicked.getItemMeta().hasLore()) {
            for (String lore : clicked.getItemMeta().getLore()) {
                String bossId = extractIdFromLore(lore);
                if (StringUtils.isNotEmpty(bossId)) {
                    session.setEditingBoss(player.getUniqueId(), bossId);
                    plugin.getGUIManager().openBossEditMenu(player, bossId);
                    return;
                }
            }
        }
    }

    private String extractIdFromLore(String lore) {
        if (StringUtils.isEmpty(lore) || !lore.contains("ID:")) return null;
        int idx = lore.lastIndexOf("§f");
        if (idx != -1 && idx + 2 <= lore.length()) {
            return lore.substring(idx + 2);
        }
        return null;
    }
}
