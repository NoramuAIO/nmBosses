package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PhasesMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public PhasesMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String bossId, ClickType click) {
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openBossEditMenu(player, bossId);
            return;
        }
        
        if (clicked.getType() == Material.REDSTONE) {
            if (clicked.getItemMeta() != null && clicked.getItemMeta().hasEnchants()) {
                config.toggleConfig(bossId, "phases.enabled");
                plugin.getGUIManager().openPhasesMenu(player, bossId);
            }
            return;
        }
        
        if (clicked.getType() == Material.LIME_DYE) {
            player.closeInventory();
            player.sendMessage("§eEnter phase name:");
            session.setWaitingForInput(player.getUniqueId(), "new_phase");
            session.setEditingBoss(player.getUniqueId(), bossId);
            return;
        }
        
        if (clicked.hasItemMeta() && clicked.getItemMeta() != null && clicked.getItemMeta().hasLore()) {
            for (String lore : clicked.getItemMeta().getLore()) {
                String phaseId = extractIdFromLore(lore);
                if (StringUtils.isNotEmpty(phaseId)) {
                    if (click.isShiftClick()) {
                        try {
                            int phaseNumber = Integer.parseInt(phaseId.replaceAll("\\D", ""));
                            config.removePhase(bossId, phaseNumber);
                            player.sendMessage("§ePhase removed!");
                            plugin.getGUIManager().openPhasesMenu(player, bossId);
                        } catch (NumberFormatException e) {
                            player.sendMessage("§cError removing phase!");
                        }
                    } else {
                        try {
                            int phaseNumber = Integer.parseInt(phaseId.replaceAll("\\D", ""));
                            session.setEditingBoss(player.getUniqueId(), bossId);
                            session.setInputType(player.getUniqueId(), phaseId);
                            plugin.getGUIManager().openPhaseEditMenu(player, bossId, phaseNumber);
                        } catch (NumberFormatException e) {
                            player.sendMessage("§cError opening phase edit menu!");
                        }
                    }
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
