package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.BossPhase;
import com.noramu.nmbosses.gui.GUIHelper;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class PhasesMenu {

    private final GUIHelper helper;

    public PhasesMenu(GUIHelper helper) {
        this.helper = helper;
    }

    public void open(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_PHASES_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(4, helper.createToggleItem(Material.REDSTONE, GuiConstants.PHASES, boss.isPhaseSystemEnabled()));

        List<BossPhase> phases = boss.getPhases();
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25};

        if (phases != null) {
            for (int i = 0; i < phases.size() && i < slots.length; i++) {
                BossPhase phase = phases.get(i);
                inv.setItem(slots[i], helper.createItem(Material.HEART_OF_THE_SEA,
                        "§c§lPhase " + phase.getPhaseNumber() + ": " + phase.getName(), true,
                        "§8§m----------------",
                        "§7Threshold: §c" + (int)phase.getHealthThreshold() + "%",
                        "§7Damage: §ex" + phase.getDamageMultiplier(),
                        "§7Speed: §ex" + phase.getSpeedMultiplier(),
                        "§7Glow: " + (phase.isGlowing() ? GuiConstants.YES : GuiConstants.NO),
                        "§7Regen: " + (phase.isRegeneration() ? "§a" + phase.getRegenerationAmount() + "/s" : GuiConstants.NO),
                        "§8§m----------------",
                        GuiConstants.CLICK_CHANGE,
                        "§c▸ Shift+Click to delete"));
            }
        }

        inv.setItem(49, helper.createItem(Material.LIME_DYE, GuiConstants.NEW_PHASE, true));
        inv.setItem(45, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }

    public void openEditMenu(Player player, String bossId, int phaseNumber) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        BossPhase phase = null;
        if (boss.getPhases() != null) {
            for (BossPhase p : boss.getPhases()) {
                if (p.getPhaseNumber() == phaseNumber) { phase = p; break; }
            }
        }
        if (phase == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_PHASE_EDIT_PREFIX + "phase" + phaseNumber);
        helper.decorateGui(inv);

        inv.setItem(4, helper.createItem(Material.HEART_OF_THE_SEA, "§c§lPhase " + phase.getPhaseNumber(), true,
                "§7Name: §f" + phase.getName()));

        inv.setItem(10, helper.createItem(Material.NAME_TAG, GuiConstants.PHASE_NAME, false,
                "§7Current: §f" + phase.getName(), GuiConstants.CLICK_CHANGE));

        inv.setItem(11, helper.createStatItem(Material.APPLE, GuiConstants.THRESHOLD,
                (int)phase.getHealthThreshold() + "%", "§8▪ §7Activates below this %", helper.clickInfo(5, 5)));

        inv.setItem(19, helper.createStatItem(Material.IRON_SWORD, GuiConstants.DAMAGE_MULT,
                "x" + phase.getDamageMultiplier(), "§8▪ §7Damage multiplier", helper.clickInfo(0.1, 0.1)));

        inv.setItem(20, helper.createStatItem(Material.SUGAR, GuiConstants.SPEED_MULT,
                "x" + phase.getSpeedMultiplier(), "§8▪ §7Speed multiplier", helper.clickInfo(0.1, 0.1)));

        inv.setItem(21, helper.createStatItem(Material.CLOCK, GuiConstants.ATTACK_SPEED,
                "x" + phase.getAttackSpeedMultiplier(), "§8▪ §7Attack speed multiplier", helper.clickInfo(0.1, 0.1)));

        inv.setItem(28, helper.createToggleItem(Material.GLOWSTONE, GuiConstants.GLOW, phase.isGlowing()));

        inv.setItem(29, helper.createItem(Material.MAGENTA_DYE, GuiConstants.BOSSBAR_COLOR, false,
                "§7Current: §e" + phase.getBossBarColor(), GuiConstants.CLICK_CHANGE));

        inv.setItem(30, helper.createToggleItem(Material.GOLDEN_APPLE, GuiConstants.REGEN, phase.isRegeneration()));

        inv.setItem(31, helper.createStatItem(Material.APPLE, GuiConstants.REGEN_AMOUNT,
                phase.getRegenerationAmount() + "/s", "§8▪ §7Health per second", helper.clickInfo(1, 1)));

        inv.setItem(32, helper.createToggleItem(Material.SHIELD, GuiConstants.INVULNERABLE, phase.isInvulnerableOnPhaseChange()));

        inv.setItem(37, helper.createItem(Material.PAPER, GuiConstants.START_MESSAGE, false,
                "§7Current: §f" + (phase.getPhaseStartMessage().isEmpty() ? "None" : phase.getPhaseStartMessage()),
                GuiConstants.CLICK_CHANGE));

        inv.setItem(45, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }
}
