package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.gui.GUIHelper;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EquipmentMenu {

    private final GUIHelper helper;

    public EquipmentMenu(GUIHelper helper) {
        this.helper = helper;
    }

    public void open(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_EQUIPMENT_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(4, helper.createItem(Material.ARMOR_STAND, "§d§lEquipment", true,
                "§7Click item from inventory", "§c▸ Shift+Click to remove"));

        ItemStack helmet = boss.getArmor() != null ? boss.getArmor().get("helmet") : null;
        inv.setItem(10, createEquipmentSlot(helmet, GuiConstants.HELMET, "helmet"));

        ItemStack chestplate = boss.getArmor() != null ? boss.getArmor().get("chestplate") : null;
        inv.setItem(19, createEquipmentSlot(chestplate, GuiConstants.CHESTPLATE, "chestplate"));

        ItemStack leggings = boss.getArmor() != null ? boss.getArmor().get("leggings") : null;
        inv.setItem(28, createEquipmentSlot(leggings, GuiConstants.LEGGINGS, "leggings"));

        ItemStack boots = boss.getArmor() != null ? boss.getArmor().get("boots") : null;
        inv.setItem(37, createEquipmentSlot(boots, GuiConstants.BOOTS, "boots"));

        inv.setItem(14, createEquipmentSlot(boss.getMainHand(), GuiConstants.MAIN_HAND, "mainHand"));
        inv.setItem(23, createEquipmentSlot(boss.getOffHand(), GuiConstants.OFF_HAND, "offHand"));
        inv.setItem(32, helper.createToggleItem(Material.SHIELD, GuiConstants.SHIELD, boss.hasShield()));

        inv.setItem(49, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }

    private ItemStack createEquipmentSlot(ItemStack current, String name, String slot) {
        if (current != null && current.getType() != Material.AIR) {
            ItemStack display = current.clone();
            ItemMeta meta = display.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§8§m----------------");
            lore.add("§7Slot: §f" + slot);
            lore.add("§7Item: §f" + current.getType().name());
            if (current.getItemMeta() != null && current.getItemMeta().hasEnchants()) {
                lore.add("§7Enchants:");
                current.getEnchantments().forEach((e, l) -> lore.add("§8▪ §d" + e.getKey().getKey() + " " + l));
            }
            lore.add("§8§m----------------");
            lore.add("§e▸ Click inventory to set");
            lore.add("§c▸ Shift+Click to remove");
            meta.setLore(lore);
            meta.setDisplayName(name);
            display.setItemMeta(meta);
            return display;
        }
        return helper.createItem(Material.BARRIER, name, false, "§7Slot: §f" + slot, "§7Item: §cNone", "§e▸ Click inventory to set");
    }
}
