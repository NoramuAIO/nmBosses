package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.BossAbility;
import com.noramu.nmbosses.gui.GUIHelper;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class AbilitiesMenu {

    private final GUIHelper helper;

    public AbilitiesMenu(GUIHelper helper) {
        this.helper = helper;
    }

    public void open(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_ABILITIES_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(4, helper.createToggleItem(Material.BLAZE_POWDER, GuiConstants.ABILITIES, boss.isAbilitiesEnabled()));

        List<BossAbility> abilities = boss.getAbilities();
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34};

        if (abilities != null) {
            for (int i = 0; i < abilities.size() && i < slots.length; i++) {
                BossAbility ability = abilities.get(i);
                Material mat = helper.getAbilityMaterial(ability.getType());
                inv.setItem(slots[i], helper.createItem(mat, "§d§l" + ability.getName(), true,
                        "§8§m----------------",
                        "§7ID: §f" + ability.getId(),
                        "§7Type: §e" + ability.getType(),
                        "§7Cooldown: §f" + ability.getCooldown() + "s",
                        "§7Chance: §f" + (int)ability.getChance() + "%",
                        "§7Damage: §c" + ability.getDamage(),
                        "§7Range: §f" + ability.getRange(),
                        "§8§m----------------",
                        GuiConstants.CLICK_CHANGE,
                        "§c▸ Shift+Click to delete"));
            }
        }

        inv.setItem(49, helper.createItem(Material.LIME_DYE, GuiConstants.NEW_ABILITY, true));
        inv.setItem(45, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }

    public void openEditMenu(Player player, String bossId, String abilityId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        BossAbility ability = null;
        if (boss.getAbilities() != null) {
            for (BossAbility a : boss.getAbilities()) {
                if (a.getId().equals(abilityId)) { ability = a; break; }
            }
        }
        if (ability == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_ABILITY_EDIT_PREFIX + abilityId);
        helper.decorateGui(inv);

        inv.setItem(4, helper.createItem(helper.getAbilityMaterial(ability.getType()), "§d§l" + ability.getName(), true,
                "§7ID: §f" + ability.getId(), "§7Type: §e" + ability.getType()));

        inv.setItem(10, helper.createItem(Material.NAME_TAG, GuiConstants.ABILITY_NAME, false,
                "§7Current: §f" + ability.getName(), GuiConstants.CLICK_CHANGE));

        inv.setItem(11, helper.createItem(Material.BEACON, GuiConstants.ABILITY_TYPE, true,
                "§7Current: §e" + ability.getType(), GuiConstants.CLICK_CHANGE));

        inv.setItem(19, helper.createStatItem(Material.CLOCK, GuiConstants.COOLDOWN,
                ability.getCooldown() + "s", "§8▪ §7Time between uses", helper.clickInfo(1, 5)));

        inv.setItem(20, helper.createStatItem(Material.RABBIT_FOOT, GuiConstants.CHANCE,
                (int)ability.getChance() + "%", "§8▪ §7Chance per check", helper.clickInfo(5, 5)));

        inv.setItem(21, helper.createStatItem(Material.IRON_SWORD, GuiConstants.DAMAGE,
                String.valueOf(ability.getDamage()), "§8▪ §7Ability damage", helper.clickInfo(1, 5)));

        inv.setItem(22, helper.createStatItem(Material.BOW, GuiConstants.RANGE,
                String.valueOf(ability.getRange()), "§8▪ §7Effective distance", helper.clickInfo(1, 1)));

        inv.setItem(28, helper.createItem(Material.PAPER, GuiConstants.MESSAGE, false,
                "§7Current: §f" + (ability.getMessage().isEmpty() ? "None" : ability.getMessage()), GuiConstants.CLICK_CHANGE));

        inv.setItem(29, helper.createItem(Material.NOTE_BLOCK, GuiConstants.SOUND, false,
                "§7Current: §f" + (ability.getSound().isEmpty() ? "None" : ability.getSound()), GuiConstants.CLICK_CHANGE));

        inv.setItem(30, helper.createItem(Material.FIREWORK_ROCKET, GuiConstants.PARTICLE, false,
                "§7Current: §f" + (ability.getParticle().isEmpty() ? "None" : ability.getParticle()), GuiConstants.CLICK_CHANGE));

        inv.setItem(45, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }

    public void openTypeMenu(Player player, String bossId, String abilityId) {
        Inventory inv = Bukkit.createInventory(null, 45, GUIManager.TITLE_ABILITY_TYPE_PREFIX + abilityId);
        helper.decorateGui(inv);

        String[][] types = {
            {"FIREBALL", "§c§lFireball"}, {"LIGHTNING", "§e§lLightning"},
            {"TELEPORT", "§5§lTeleport"}, {"SUMMON", "§2§lSummon"},
            {"HEAL", "§a§lHeal"}, {"EXPLOSION", "§4§lExplosion"},
            {"EFFECT", "§d§lEffect"}, {"PULL", "§9§lPull"},
            {"PUSH", "§b§lPush"}, {"METEOR", "§6§lMeteor"},
            {"GROUND_SLAM", "§8§lGround Slam"}, {"CHARGE", "§c§lCharge"}
        };

        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25};
        for (int i = 0; i < types.length && i < slots.length; i++) {
            inv.setItem(slots[i], helper.createItem(helper.getAbilityMaterial(types[i][0]), types[i][1], false,
                    "§7ID: §f" + types[i][0], GuiConstants.CLICK_CHANGE));
        }

        inv.setItem(40, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }
}
