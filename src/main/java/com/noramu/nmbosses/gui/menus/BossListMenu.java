package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.gui.GUIHelper;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class BossListMenu {

    private final GUIHelper helper;

    public BossListMenu(GUIHelper helper) {
        this.helper = helper;
    }

    public void open(Player player, int page) {
        // Sabit prefix kullan - routing için gerekli
        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_LIST);
        helper.decorateGui(inv);

        List<String> bossIds = new ArrayList<>(helper.getPlugin().getBossManager().getBossIds());
        int startIndex = page * 21;
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34};

        int slotIndex = 0;
        for (int i = startIndex; i < bossIds.size() && slotIndex < slots.length; i++) {
            Boss boss = helper.getPlugin().getBossManager().getBoss(bossIds.get(i));
            if (boss != null) {
                inv.setItem(slots[slotIndex++], helper.createBossItem(player, boss));
            }
        }

        inv.setItem(48, helper.createItem(Material.BARRIER, GuiConstants.BTN_CLOSE, false));
        inv.setItem(49, helper.createItem(Material.EMERALD, GuiConstants.NEW_BOSS, true));
        inv.setItem(50, helper.createItem(Material.COMMAND_BLOCK, GuiConstants.RELOAD, false));

        if (page > 0) inv.setItem(45, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        if ((page + 1) * 21 < bossIds.size()) inv.setItem(53, helper.createItem(Material.ARROW, "§e▶", false));

        player.openInventory(inv);
    }
}
