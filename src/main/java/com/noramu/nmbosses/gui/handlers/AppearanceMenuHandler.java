package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Appearance Menu click handler
 */
public class AppearanceMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public AppearanceMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String bossId, ClickType click) {
        Material mat = clicked.getType();
        
        if (mat == Material.ARROW) {
            plugin.getGUIManager().openBossEditMenu(player, bossId);
            return;
        }
        
        switch (mat) {
            case GLOWSTONE:
                config.toggleConfig(bossId, "glowing");
                break;
            case NAME_TAG:
                config.toggleConfig(bossId, "nameVisible");
                break;
            case APPLE:
                config.toggleConfig(bossId, "showHealth");
                break;
            case EXPERIENCE_BOTTLE:
                config.toggleConfig(bossId, "bossBar.enabled");
                break;
            case WHITE_DYE:
                session.setEditingBoss(player.getUniqueId(), bossId);
                session.setInputType(player.getUniqueId(), "glowColor");
                plugin.getGUIManager().openColorMenu(player, bossId, "glowColor");
                return;
            case OAK_SIGN:
                player.closeInventory();
                player.sendMessage("§eEnter new display name (color codes & supported):");
                session.setWaitingForInput(player.getUniqueId(), "displayName");
                session.setEditingBoss(player.getUniqueId(), bossId);
                return;
            case MAGENTA_DYE:
                session.setEditingBoss(player.getUniqueId(), bossId);
                session.setInputType(player.getUniqueId(), "bossBar.color");
                plugin.getGUIManager().openColorMenu(player, bossId, "bossBar.color");
                return;
            case CHAIN:
                Boss boss = plugin.getBossManager().getBoss(bossId);
                int idx = findIndex(GuiConstants.BOSSBAR_STYLES, boss.getBossBarStyle());
                config.updateConfig(bossId, "bossBar.style", GuiConstants.BOSSBAR_STYLES[(idx + 1) % GuiConstants.BOSSBAR_STYLES.length]);
                break;
            case SPYGLASS:
                double change = click.isShiftClick() ? 50 : 10;
                if (click.isLeftClick()) change = -change;
                config.updateConfig(bossId, "bossBar.radius", Math.max(0, config.getCurrentDoubleValue(bossId, "bossBar.radius") + change));
                break;
        }
        
        plugin.getGUIManager().openAppearanceMenu(player, bossId);
    }

    private int findIndex(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return 0;
    }
}
