package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntityTypeMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public EntityTypeMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String bossId) {
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openStatsMenu(player, bossId);
            return;
        }
        
        if (clicked.getItemMeta() == null || !clicked.getItemMeta().hasLore()) {
            return;
        }
        
        for (String lore : clicked.getItemMeta().getLore()) {
            if (lore.contains("Type: §f")) {
                config.updateConfig(bossId, "entityType", lore.split("Type: §f")[1]);
                plugin.getGUIManager().openStatsMenu(player, bossId);
                return;
            }
        }
    }
}
