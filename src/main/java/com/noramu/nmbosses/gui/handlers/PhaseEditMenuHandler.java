package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PhaseEditMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public PhaseEditMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String phaseId, ClickType click) {
        String bossId = session.getEditingBoss(player.getUniqueId());
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openPhasesMenu(player, bossId);
            return;
        }
        
        String path = "phases.list." + phaseId + ".";
        Material mat = clicked.getType();

        if (mat == Material.NAME_TAG) {
            player.closeInventory();
            player.sendMessage("§eEnter phase name:");
            session.setWaitingForInput(player.getUniqueId(), "phase_name");
            session.setInputType(player.getUniqueId(), phaseId);
            return;
        }
        
        if (mat == Material.RED_DYE) {
            double change = click.isShiftClick() ? 10.0 : 1.0;
            if (click.isLeftClick()) change = -change;
            double thresholdValue = config.getCurrentDoubleValue(bossId, path + "healthThreshold") + change;
            if (thresholdValue < 0.0) thresholdValue = 0.0;
            config.updateConfig(bossId, path + "healthThreshold", thresholdValue);
        } else if (mat == Material.IRON_SWORD) {
            double change = click.isShiftClick() ? 0.5 : 0.1;
            if (click.isLeftClick()) change = -change;
            double damageMultValue = config.getCurrentDoubleValue(bossId, path + "damageMultiplier") + change;
            if (damageMultValue < 0.1) damageMultValue = 0.1;
            config.updateConfig(bossId, path + "damageMultiplier", damageMultValue);
        } else if (mat == Material.SUGAR) {
            double change = click.isShiftClick() ? 0.5 : 0.1;
            if (click.isLeftClick()) change = -change;
            double speedMultValue = config.getCurrentDoubleValue(bossId, path + "speedMultiplier") + change;
            if (speedMultValue < 0.1) speedMultValue = 0.1;
            config.updateConfig(bossId, path + "speedMultiplier", speedMultValue);
        } else if (mat == Material.GOLDEN_APPLE) {
            try {
                int phaseNumber = Integer.parseInt(phaseId.replaceAll("\\D", ""));
                config.toggleConfig(bossId, "phases.list.phase" + phaseNumber + ".regeneration");
            } catch (NumberFormatException e) {
                player.sendMessage("§cError toggling regeneration!");
            }
        } else if (mat == Material.PAPER) {
            player.closeInventory();
            player.sendMessage("§eEnter phase start message:");
            session.setWaitingForInput(player.getUniqueId(), "phase_message");
            session.setInputType(player.getUniqueId(), phaseId);
            return;
        }
        
        try {
            int phaseNumber = Integer.parseInt(phaseId.replaceAll("\\D", ""));
            plugin.getGUIManager().openPhaseEditMenu(player, bossId, phaseNumber);
        } catch (NumberFormatException e) {
            player.sendMessage("§cError opening phase edit menu!");
        }
    }
}
