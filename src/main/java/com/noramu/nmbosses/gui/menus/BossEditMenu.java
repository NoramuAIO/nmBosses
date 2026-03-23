package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.gui.GUIHelper;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BossEditMenu {

    private final GUIHelper helper;

    public BossEditMenu(GUIHelper helper) {
        this.helper = helper;
    }

    public void open(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_EDIT_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(4, helper.createBossItem(player, boss));

        // Temel Özellikler
        inv.setItem(20, helper.createItem(Material.DIAMOND_SWORD, GuiConstants.BASIC_STATS, false,
                "§8§m----------------",
                "§8▪ §7HP: §c" + (int)boss.getHealth() + " ❤",
                "§8▪ §7DMG: §c" + boss.getDamage() + " ⚔",
                "§8▪ §7Speed: §f" + boss.getSpeed(),
                "§8§m----------------",
                GuiConstants.CLICK_CHANGE));

        // Görünüm
        inv.setItem(21, helper.createItem(Material.ARMOR_STAND, GuiConstants.APPEARANCE, false,
                "§8§m----------------",
                "§8▪ §7Glow: " + (boss.isGlowing() ? GuiConstants.YES : GuiConstants.NO),
                "§8▪ §7Name: " + (boss.isNameVisible() ? GuiConstants.YES : GuiConstants.NO),
                "§8§m----------------",
                GuiConstants.CLICK_CHANGE));

        // Ekipman
        inv.setItem(22, helper.createItem(Material.NETHERITE_CHESTPLATE, GuiConstants.EQUIPMENT, true,
                "§8§m----------------",
                "§8▪ §7Helmet: §f" + helper.getArmorName(boss, "helmet"),
                "§8▪ §7Chest: §f" + helper.getArmorName(boss, "chestplate"),
                "§8▪ §7Weapon: §f" + (boss.getMainHand() != null ? boss.getMainHand().getType().name() : "None"),
                "§8§m----------------",
                GuiConstants.CLICK_CHANGE));

        // Spawn
        inv.setItem(23, helper.createItem(Material.ENDER_PEARL, GuiConstants.SPAWN_SETTINGS, false,
                "§8§m----------------",
                "§8▪ §7Auto: " + (boss.isAutoSpawn() ? GuiConstants.YES : GuiConstants.NO),
                "§8▪ §7Delay: §f" + boss.getRespawnDelay() + "s",
                "§8▪ §7Time: §f" + boss.getSpawnTime(),
                "§8§m----------------",
                GuiConstants.CLICK_CHANGE));

        // Yetenekler
        int abilCount = boss.getAbilities() != null ? boss.getAbilities().size() : 0;
        inv.setItem(29, helper.createItem(Material.BLAZE_POWDER, GuiConstants.ABILITIES, boss.isAbilitiesEnabled(),
                "§7Count: §e" + abilCount,
                "§7Status: " + (boss.isAbilitiesEnabled() ? GuiConstants.ACTIVE : GuiConstants.INACTIVE)));

        // Ödüller
        int dropCount = boss.getDrops() != null ? boss.getDrops().size() : 0;
        inv.setItem(30, helper.createItem(Material.CHEST, GuiConstants.REWARDS, true,
                "§8§m----------------",
                "§8▪ §7Drops: §e" + dropCount,
                "§8▪ §7XP: §a" + boss.getXp(),
                "§8▪ §7Chance: §f" + (int)boss.getDropChance() + "%",
                "§8§m----------------"));

        // Komutlar
        int cmdCount = boss.getDropCommands() != null ? boss.getDropCommands().size() : 0;
        inv.setItem(32, helper.createItem(Material.COMMAND_BLOCK, GuiConstants.COMMANDS, false,
                "§7Count: §e" + cmdCount));

        // Fazlar
        int phaseCount = boss.getPhases() != null ? boss.getPhases().size() : 0;
        inv.setItem(33, helper.createItem(Material.REDSTONE, GuiConstants.PHASES, boss.isPhaseSystemEnabled(),
                "§7Count: §e" + phaseCount,
                "§7Status: " + (boss.isPhaseSystemEnabled() ? GuiConstants.ACTIVE : GuiConstants.INACTIVE)));

        // Model Plugins
        inv.setItem(34, helper.createItem(Material.SCULK_SENSOR, "§6Model Plugins", false,
                "§8§m----------------",
                "§8▪ §7ModelEngine",
                "§8▪ §7MythicMobs",
                "§8▪ §7Oraxen",
                "§8▪ §7ItemsAdder",
                "§8▪ §7Nexo",
                "§8§m----------------",
                GuiConstants.CLICK_CHANGE));

        // Aksiyonlar
        inv.setItem(48, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        inv.setItem(40, helper.createItem(Material.DRAGON_EGG, GuiConstants.SPAWN_BOSS, true));
        inv.setItem(41, helper.createItem(Material.TNT, GuiConstants.DESPAWN_BOSS, false));
        inv.setItem(50, helper.createItem(Material.LAVA_BUCKET, GuiConstants.DELETE_BOSS, false));

        player.openInventory(inv);
    }
}
