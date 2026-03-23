package com.noramu.nmbosses.gui;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.GuiConstants;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GUI için ortak yardımcı metodlar
 */
public class GUIHelper {

    private final NmBosses plugin;

    public GUIHelper(NmBosses plugin) {
        this.plugin = plugin;
    }

    public NmBosses getPlugin() { return plugin; }

    public void decorateGui(Inventory inv) {
        int size = inv.getSize();
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", false);
        ItemStack border = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", false);

        for (int i = 0; i < size; i++) inv.setItem(i, filler);
        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, border);
        }
    }

    public ItemStack createItem(Material material, String name, boolean glowing, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) meta.setLore(Arrays.asList(lore));
        if (glowing) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createBossItem(Player player, Boss boss) {
        Material material = getMaterialForEntityType(boss.getEntityType());
        int activeCount = plugin.getBossManager().getActiveBossCount(boss.getId());
        String activeColor = activeCount > 0 ? "§a" : "§7";

        return createItem(material, StringUtils.colorize(boss.getDisplayName()), false,
                "§8§m----------------",
                "§8▪ §7ID: §f" + boss.getId(),
                "§8▪ §7Type: §f" + boss.getBossType(),
                "§8▪ §7HP: §c" + (int)boss.getHealth() + " ❤",
                "§8§m----------------",
                "§8▪ §7Active: " + activeColor + activeCount,
                "§8▪ §7Auto: " + (boss.isAutoSpawn() ? GuiConstants.YES : GuiConstants.NO),
                "§8§m----------------");
    }

    public ItemStack createStatItem(Material material, String name, String value, String desc, String clickInfo) {
        return createItem(material, name, false, desc, "§7", "§fValue: §b" + value, "§8§m----------------", clickInfo);
    }

    public ItemStack createToggleItem(Material material, String name, boolean value) {
        String status = value ? GuiConstants.BTN_ON : GuiConstants.BTN_OFF;
        return createItem(material, name, value, "§7", "§fStatus: " + status, "§7", GuiConstants.CLICK_CHANGE);
    }

    public Material getMaterialForEntityType(String entityType) {
        switch (entityType.toUpperCase()) {
            case "ZOMBIE": return Material.ZOMBIE_HEAD;
            case "SKELETON": return Material.SKELETON_SKULL;
            case "WITHER_SKELETON": return Material.WITHER_SKELETON_SKULL;
            case "CREEPER": return Material.CREEPER_HEAD;
            case "ENDERMAN": return Material.ENDER_PEARL;
            case "BLAZE": return Material.BLAZE_ROD;
            case "IRON_GOLEM": return Material.IRON_BLOCK;
            case "PIGLIN_BRUTE": return Material.PIGLIN_HEAD;
            case "VINDICATOR": return Material.IRON_AXE;
            case "WITHER": return Material.NETHER_STAR;
            default: return Material.GHAST_TEAR;
        }
    }

    public Material getAbilityMaterial(String type) {
        switch (type.toUpperCase()) {
            case "FIREBALL": return Material.FIRE_CHARGE;
            case "LIGHTNING": return Material.LIGHTNING_ROD;
            case "TELEPORT": return Material.ENDER_PEARL;
            case "SUMMON": return Material.SPAWNER;
            case "HEAL": return Material.GOLDEN_APPLE;
            case "EXPLOSION": return Material.TNT;
            case "EFFECT": return Material.POTION;
            case "PULL": return Material.FISHING_ROD;
            case "PUSH": return Material.PISTON;
            case "METEOR": return Material.MAGMA_BLOCK;
            case "GROUND_SLAM": return Material.ANVIL;
            case "CHARGE": return Material.IRON_SWORD;
            default: return Material.BLAZE_POWDER;
        }
    }

    public String getArmorName(Boss boss, String slot) {
        if (boss.getArmor() == null) return "None";
        ItemStack item = boss.getArmor().get(slot);
        if (item == null || item.getType() == Material.AIR) return "None";
        return item.getType().name();
    }

    public String getSafeColorDisplay(String colorCode) {
        return colorCode != null ? "§" + getColorCode(colorCode) + colorCode : "§fDefault";
    }

    public char getColorCode(String colorName) {
        switch (colorName.toUpperCase()) {
            case "RED": return 'c';
            case "BLUE": return '9';
            case "GREEN": return 'a';
            case "YELLOW": return 'e';
            case "GOLD": return '6';
            case "WHITE": return 'f';
            case "BLACK": return '0';
            default: return 'f';
        }
    }

    public String formatDays(List<String> days) {
        if (days == null || days.isEmpty()) return "Every day";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(getDayShort(days.get(i)));
        }
        return sb.toString();
    }

    public String getDayShort(String day) {
        switch (day.toUpperCase()) {
            case "MONDAY": return "Mon";
            case "TUESDAY": return "Tue";
            case "WEDNESDAY": return "Wed";
            case "THURSDAY": return "Thu";
            case "FRIDAY": return "Fri";
            case "SATURDAY": return "Sat";
            case "SUNDAY": return "Sun";
            default: return day;
        }
    }
    
    public String clickInfo(double small, double big) {
        String s = small == (int)small ? String.valueOf((int)small) : String.valueOf(small);
        String b = big == (int)big ? String.valueOf((int)big) : String.valueOf(big);
        return "§eL: §c-" + s + " §7| §eR: §a+" + s + " §7| §eShift: §b±" + b;
    }
}
