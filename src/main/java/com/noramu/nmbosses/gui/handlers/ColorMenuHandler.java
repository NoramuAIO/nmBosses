package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ColorMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public ColorMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openAppearanceMenu(player, session.getEditingBoss(player.getUniqueId()));
            return;
        }
        
        if (clicked.getItemMeta() == null || clicked.getItemMeta().getDisplayName() == null) {
            return;
        }
        
        String colorName = clicked.getItemMeta().getDisplayName().replace("§f", "");
        String bossId = session.getEditingBoss(player.getUniqueId());
        String configKey = session.getInputType(player.getUniqueId());
        config.updateConfig(bossId, configKey, colorName);
        plugin.getGUIManager().openAppearanceMenu(player, bossId);
    }
}
