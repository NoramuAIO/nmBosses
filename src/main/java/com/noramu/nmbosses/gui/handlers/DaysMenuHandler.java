package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DaysMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public DaysMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String bossId) {
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
