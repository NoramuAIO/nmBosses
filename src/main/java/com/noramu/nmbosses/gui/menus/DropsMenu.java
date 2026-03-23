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

public class DropsMenu {

    private final GUIHelper helper;

    public DropsMenu(GUIHelper helper) {
        this.helper = helper;
    }

    public void open(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_DROPS_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(4, helper.createItem(Material.CHEST, "§e§lRewards", true, "§7Click item from inventory"));

        inv.setItem(48, helper.createStatItem(Material.EXPERIENCE_BOTTLE, GuiConstants.XP_AMOUNT,
                String.valueOf(boss.getXp()), "§8▪ §7XP given on kill", helper.clickInfo(50, 500)));

        inv.setItem(50, helper.createStatItem(Material.RABBIT_FOOT, GuiConstants.DROP_CHANCE,
                (int)boss.getDropChance() + "%", "§8▪ §7Item drop chance", helper.clickInfo(5, 5)));

        List<ItemStack> drops = boss.getDrops();
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34};

        if (drops != null) {
            for (int i = 0; i < drops.size() && i < slots.length; i++) {
                ItemStack drop = drops.get(i);
                ItemStack display = drop.clone();
                ItemMeta meta = display.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("§8§m----------------");
                lore.add("§7Amount: §f" + drop.getAmount());
                if (drop.hasItemMeta() && drop.getItemMeta().hasEnchants()) {
                    lore.add("§7Enchants:");
                    drop.getEnchantments().forEach((e, l) -> lore.add("§8▪ §d" + e.getKey().getKey() + " " + l));
                }
                lore.add("§8§m----------------");
                lore.add("§c▸ Shift+Click to delete");
                meta.setLore(lore);
                display.setItemMeta(meta);
                inv.setItem(slots[i], display);
            }
        }

        inv.setItem(40, helper.createItem(Material.LIME_DYE, GuiConstants.ADD_ITEM, true));
        inv.setItem(45, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }

    public void openCommandsMenu(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_COMMANDS_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(4, helper.createItem(Material.COMMAND_BLOCK, "§6§lCommands", true));

        List<String> commands = boss.getDropCommands();
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34};

        if (commands != null) {
            for (int i = 0; i < commands.size() && i < slots.length; i++) {
                String cmd = commands.get(i);
                String shortCmd = cmd.length() > 30 ? cmd.substring(0, 27) + "..." : cmd;
                inv.setItem(slots[i], helper.createItem(Material.PAPER, "§f/" + shortCmd, false,
                        "§7Full Command:", "§f" + cmd, "§c▸ Shift+Click to delete", "§e▸ Click to edit"));
            }
        }

        inv.setItem(40, helper.createItem(Material.LIME_DYE, GuiConstants.ADD_COMMAND, true));
        inv.setItem(45, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }
}
