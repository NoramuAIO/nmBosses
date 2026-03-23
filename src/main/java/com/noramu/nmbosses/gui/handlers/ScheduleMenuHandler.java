package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ScheduleMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public ScheduleMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String bossId) {
        Material mat = clicked.getType();
        
        if (mat == Material.ARROW) {
            plugin.getGUIManager().openSpawnMenu(player, bossId);
            return;
        }
        
        if (mat == Material.PAPER) {
            plugin.getGUIManager().openDaysMenu(player, bossId);
            return;
        }
        
        if (mat == Material.LIME_DYE) {
            player.closeInventory();
            player.sendMessage("§eEnter start time (HH:mm):");
            session.setWaitingForInput(player.getUniqueId(), "start_hour");
            session.setEditingBoss(player.getUniqueId(), bossId);
            return;
        }
        
        if (mat == Material.RED_DYE) {
            player.closeInventory();
            player.sendMessage("§eEnter end time (HH:mm):");
            session.setWaitingForInput(player.getUniqueId(), "end_hour");
            session.setEditingBoss(player.getUniqueId(), bossId);
            return;
        }
        
        if (mat == Material.BARRIER) {
            config.updateConfig(bossId, "spawn.schedule.days", new ArrayList<>());
            config.updateConfig(bossId, "spawn.schedule.startHour", "00:00");
            config.updateConfig(bossId, "spawn.schedule.endHour", "23:59");
            player.sendMessage("§eTime settings reset!");
            plugin.getGUIManager().openScheduleMenu(player, bossId);
        }
    }

    public void handleDaysClick(Player player, ItemStack clicked, String bossId) {
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openScheduleMenu(player, bossId);
            return;
        }
        
        if (clicked.getType() == Material.EMERALD) {
            List<String> allDays = new ArrayList<>();
            allDays.add("MONDAY");
            allDays.add("TUESDAY");
            allDays.add("WEDNESDAY");
            allDays.add("THURSDAY");
            allDays.add("FRIDAY");
            allDays.add("SATURDAY");
            allDays.add("SUNDAY");
            config.updateConfig(bossId, "spawn.schedule.days", allDays);
            plugin.getGUIManager().openDaysMenu(player, bossId);
            return;
        }
        
        if (clicked.getType() == Material.REDSTONE) {
            config.updateConfig(bossId, "spawn.schedule.days", new ArrayList<>());
            plugin.getGUIManager().openDaysMenu(player, bossId);
            return;
        }
        
        if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE || clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
            if (clicked.hasItemMeta() && clicked.getItemMeta() != null && clicked.getItemMeta().hasLore()) {
                for (String lore : clicked.getItemMeta().getLore()) {
                    String dayId = extractIdFromLore(lore);
                    if (StringUtils.isNotEmpty(dayId)) {
                        config.toggleDay(bossId, dayId);
                        plugin.getGUIManager().openDaysMenu(player, bossId);
                        return;
                    }
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
