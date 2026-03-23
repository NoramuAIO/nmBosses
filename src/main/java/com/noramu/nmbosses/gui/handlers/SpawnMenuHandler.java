package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Spawn Menu click handler
 */
public class SpawnMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public SpawnMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String bossId, ClickType click, int slot) {
        Material mat = clicked.getType();
        
        if (mat == Material.ARROW) {
            plugin.getGUIManager().openBossEditMenu(player, bossId);
            return;
        }
        
        switch (mat) {
            case REPEATER:
                boolean newState = plugin.getBossManager().toggleAutoSpawn(bossId);
                if (newState) {
                    plugin.getSpawnManager().startAutoSpawnForBoss(bossId);
                } else {
                    plugin.getSpawnManager().stopAutoSpawnForBoss(bossId);
                }
                break;
            case SPAWNER:
                // Left click = increase, Right click = decrease
                double currentMax = config.getCurrentDoubleValue(bossId, "spawn.maxCount");
                if (click.isLeftClick()) {
                    config.updateConfig(bossId, "spawn.maxCount", currentMax + 1);
                } else if (click.isRightClick()) {
                    config.updateConfig(bossId, "spawn.maxCount", Math.max(1, currentMax - 1));
                }
                break;
            case SUNFLOWER:
                Boss boss = plugin.getBossManager().getBoss(bossId);
                int idx = findIndex(GuiConstants.SPAWN_TIMES, boss.getSpawnTime());
                config.updateConfig(bossId, "spawn.time", GuiConstants.SPAWN_TIMES[(idx + 1) % GuiConstants.SPAWN_TIMES.length]);
                break;
            case COMPASS:
                plugin.getBossManager().setSpawnLocation(bossId, player.getLocation());
                player.sendMessage("§eLocation saved!");
                break;
            case CLOCK:
                if (clicked.getItemMeta() != null && clicked.getItemMeta().hasEnchants()) {
                    // Schedule menu (enchanted clock)
                    session.setEditingBoss(player.getUniqueId(), bossId);
                    plugin.getGUIManager().openScheduleMenu(player, bossId);
                    return;
                } else {
                    // Respawn delay (normal clock)
                    double currentDelay = config.getCurrentDoubleValue(bossId, "spawn.respawnDelay");
                    double change = click.isShiftClick() ? 60 : 10; // Shift = 60s, Normal = 10s
                    if (click.isLeftClick()) {
                        config.updateConfig(bossId, "spawn.respawnDelay", currentDelay + change);
                    } else if (click.isRightClick()) {
                        config.updateConfig(bossId, "spawn.respawnDelay", Math.max(0, currentDelay - change));
                    }
                }
                break;
        }
        
        plugin.getGUIManager().openSpawnMenu(player, bossId);
    }

    private int findIndex(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return 0;
    }
}
