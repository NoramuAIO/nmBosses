package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.gui.GUIHelper;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.utils.GuiConstants;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AppearanceMenu {

    private final GUIHelper helper;

    public AppearanceMenu(GUIHelper helper) {
        this.helper = helper;
    }

    public void open(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 45, GUIManager.TITLE_APPEARANCE_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(11, helper.createToggleItem(Material.GLOWSTONE, GuiConstants.GLOW, boss.isGlowing()));
        inv.setItem(20, helper.createItem(Material.WHITE_DYE, GuiConstants.GLOW_COLOR, false,
                "§7Current: " + helper.getSafeColorDisplay(boss.getGlowColor()), GuiConstants.CLICK_CHANGE));

        inv.setItem(13, helper.createToggleItem(Material.NAME_TAG, GuiConstants.NAME_VISIBLE, boss.isNameVisible()));
        inv.setItem(22, helper.createItem(Material.OAK_SIGN, GuiConstants.DISPLAY_NAME, true,
                "§7Current:", "§r" + StringUtils.colorize(boss.getDisplayName()), GuiConstants.CLICK_CHANGE));

        inv.setItem(15, helper.createToggleItem(Material.APPLE, GuiConstants.SHOW_HEALTH, boss.isShowHealth()));

        inv.setItem(30, helper.createToggleItem(Material.EXPERIENCE_BOTTLE, GuiConstants.BOSSBAR, boss.isShowBossBar()));
        inv.setItem(31, helper.createItem(Material.MAGENTA_DYE, GuiConstants.BOSSBAR_COLOR, false,
                "§7Current: §e" + boss.getBossBarColor(), GuiConstants.CLICK_CHANGE));
        inv.setItem(32, helper.createItem(Material.CHAIN, GuiConstants.BOSSBAR_STYLE, false,
                "§7Current: §e" + boss.getBossBarStyle(), GuiConstants.CLICK_CHANGE));

        inv.setItem(34, helper.createStatItem(Material.SPYGLASS, GuiConstants.VIEW_DISTANCE,
                String.valueOf(boss.getBossBarRadius()), "§8▪ §7BossBar visibility range", helper.clickInfo(10, 50)));

        inv.setItem(36, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }

    public void openColorMenu(Player player, String bossId, String type) {
        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_COLOR_PREFIX + type);
        helper.decorateGui(inv);

        String[] colors;
        Material[] materials;

        if (type.equals("glowColor")) {
            colors = new String[]{"WHITE","YELLOW","GOLD","RED","DARK_RED","LIGHT_PURPLE","DARK_PURPLE","BLUE","DARK_BLUE","AQUA","DARK_AQUA","GREEN","DARK_GREEN","GRAY","DARK_GRAY","BLACK"};
            materials = new Material[]{Material.WHITE_DYE,Material.YELLOW_DYE,Material.ORANGE_DYE,Material.RED_DYE,Material.PINK_DYE,Material.MAGENTA_DYE,Material.PURPLE_DYE,Material.BLUE_DYE,Material.CYAN_DYE,Material.LIGHT_BLUE_DYE,Material.CYAN_DYE,Material.LIME_DYE,Material.GREEN_DYE,Material.LIGHT_GRAY_DYE,Material.GRAY_DYE,Material.BLACK_DYE};
        } else {
            colors = new String[]{"RED","PINK","BLUE","GREEN","YELLOW","PURPLE","WHITE"};
            materials = new Material[]{Material.RED_DYE,Material.PINK_DYE,Material.BLUE_DYE,Material.LIME_DYE,Material.YELLOW_DYE,Material.PURPLE_DYE,Material.WHITE_DYE};
        }

        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34};
        for (int i = 0; i < colors.length && i < slots.length; i++) {
            inv.setItem(slots[i], helper.createItem(materials[i % materials.length], "§f" + colors[i], false, GuiConstants.CLICK_CHANGE));
        }

        inv.setItem(49, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }
}
