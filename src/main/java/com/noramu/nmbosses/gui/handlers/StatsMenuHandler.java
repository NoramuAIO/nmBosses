package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Stats Menu click handler
 */
public class StatsMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;

    public StatsMenuHandler(NmBosses plugin, ConfigHelper config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void handle(Player player, ItemStack clicked, String bossId, ClickType click) {
        Material mat = clicked.getType();
        
        if (mat == Material.ARROW) {
            plugin.getGUIManager().openBossEditMenu(player, bossId);
            return;
        }
        
        if (mat == Material.ZOMBIE_HEAD) {
            plugin.getGUIManager().openEntityTypeMenu(player, bossId);
            return;
        }

        String key = null;
        double small = 0, big = 0;
        
        switch (mat) {
            case RED_DYE:
                key = "health";
                small = 10;
                big = 100;
                break;
            case IRON_SWORD:
                key = "damage";
                small = 1;
                big = 10;
                break;
            case SUGAR:
                key = "speed";
                small = 0.01;
                big = 0.1;
                break;
            case SHIELD:
                key = "knockbackResistance";
                small = 0.1;
                big = 0.1;
                break;
            case PISTON:
                key = "knockbackPower";
                small = 0.1;
                big = 0.1;
                break;
            case CLOCK:
                key = "attackSpeed";
                small = 0.1;
                big = 0.1;
                break;
            case BOW:
                key = "attackRange";
                small = 1;
                big = 5;
                break;
        }
        
        if (key != null) {
            double change = click.isShiftClick() ? big : small;
            if (click.isLeftClick()) change = -change;
            double newVal = Math.max(0, config.getCurrentDoubleValue(bossId, key) + change);
            if (key.equals("knockbackResistance")) newVal = Math.min(1.0, newVal);
            config.updateConfig(bossId, key, newVal);
            plugin.getGUIManager().openStatsMenu(player, bossId);
            return;
        }
        
        if (mat == Material.NAME_TAG) {
            Boss boss = plugin.getBossManager().getBoss(bossId);
            int idx = findIndex(GuiConstants.BOSS_TYPES, boss.getBossType());
            config.updateConfig(bossId, "bossType", GuiConstants.BOSS_TYPES[(idx + 1) % GuiConstants.BOSS_TYPES.length]);
            plugin.getGUIManager().openStatsMenu(player, bossId);
        }
    }

    private int findIndex(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return 0;
    }
}
