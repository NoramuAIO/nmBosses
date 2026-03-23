package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.gui.GUIHelper;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class StatsMenu {

    private final GUIHelper helper;

    public StatsMenu(GUIHelper helper) {
        this.helper = helper;
    }

    public void open(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 45, GUIManager.TITLE_STATS_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(10, helper.createStatItem(Material.RED_DYE, GuiConstants.HEALTH,
                String.valueOf((int)boss.getHealth()), "§8▪ §7Total Health", helper.clickInfo(10, 100)));

        inv.setItem(11, helper.createStatItem(Material.IRON_SWORD, GuiConstants.DAMAGE,
                String.valueOf(boss.getDamage()), "§8▪ §7Attack Power", helper.clickInfo(1, 10)));

        inv.setItem(12, helper.createStatItem(Material.SUGAR, GuiConstants.SPEED,
                String.valueOf(boss.getSpeed()), "§8▪ §7Movement Speed", helper.clickInfo(0.01, 0.1)));

        inv.setItem(13, helper.createStatItem(Material.SHIELD, GuiConstants.KB_RESISTANCE,
                String.valueOf(boss.getKnockbackResistance()), "§8▪ §7Knockback Resistance", helper.clickInfo(0.1, 0.1)));

        inv.setItem(14, helper.createStatItem(Material.PISTON, GuiConstants.KB_POWER,
                String.valueOf(boss.getKnockbackPower()), "§8▪ §7Knockback Force", helper.clickInfo(0.1, 0.1)));

        inv.setItem(15, helper.createStatItem(Material.CLOCK, GuiConstants.ATTACK_SPEED,
                String.valueOf(boss.getAttackSpeed()), "§8▪ §7Attack Frequency", helper.clickInfo(0.1, 0.1)));

        inv.setItem(16, helper.createStatItem(Material.BOW, GuiConstants.ATTACK_RANGE,
                String.valueOf(boss.getAttackRange()), "§8▪ §7Attack Distance", helper.clickInfo(1, 5)));

        inv.setItem(30, helper.createItem(Material.ZOMBIE_HEAD, GuiConstants.ENTITY_TYPE, true,
                "§7Current: §a" + boss.getEntityType(), GuiConstants.CLICK_CHANGE));

        inv.setItem(32, helper.createItem(Material.NAME_TAG, GuiConstants.BOSS_CLASS, false,
                "§7Current: §e" + boss.getBossType(), GuiConstants.CLICK_CHANGE));

        inv.setItem(36, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }
}
