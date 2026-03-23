package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CommandsMenuHandler {

    private final NmBosses plugin;
    private final SessionData session;
    private final ConfigHelper config;

    public CommandsMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String bossId, ClickType click, int slot) {
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openBossEditMenu(player, bossId);
            return;
        }
        
        if (clicked.getType() == Material.LIME_DYE) {
            player.closeInventory();
            player.sendMessage("§eEnter new command (without /):");
            session.setWaitingForInput(player.getUniqueId(), "add_command");
            session.setEditingBoss(player.getUniqueId(), bossId);
            return;
        }
        
        if (clicked.getType() == Material.PAPER) {
            int[] cmdSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
            for (int i = 0; i < cmdSlots.length; i++) {
                if (cmdSlots[i] == slot) {
                    if (click.isShiftClick()) {
                        config.removeCommand(bossId, i);
                        player.sendMessage("§eCommand removed!");
                        plugin.getGUIManager().openCommandsMenu(player, bossId);
                    } else {
                        player.closeInventory();
                        player.sendMessage("§eEnter new command:");
                        session.setWaitingForInput(player.getUniqueId(), "edit_command");
                        session.setEditingBoss(player.getUniqueId(), bossId);
                        session.setEditingCommandIndex(player.getUniqueId(), i);
                    }
                    return;
                }
            }
        }
    }
}
