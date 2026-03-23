package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AbilityTypeMenuHandler {

    private final NmBosses plugin;
    private final SessionData session;
    private final ConfigHelper config;

    public AbilityTypeMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String abilityId) {
        String bossId = session.getEditingBoss(player.getUniqueId());
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openAbilityEditMenu(player, bossId, abilityId);
            return;
        }
        
        if (clicked.hasItemMeta() && clicked.getItemMeta() != null && clicked.getItemMeta().hasLore()) {
            for (String lore : clicked.getItemMeta().getLore()) {
                if (lore.contains("Type: §f")) {
                    String type = lore.split("Type: §f")[1];
                    config.updateConfig(bossId, "abilities.list." + abilityId + ".type", type);
                    plugin.getGUIManager().openAbilityEditMenu(player, bossId, abilityId);
                    return;
                }
            }
        }
    }
}
