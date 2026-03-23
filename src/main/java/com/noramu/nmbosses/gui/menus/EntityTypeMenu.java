package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.gui.GUIHelper;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EntityTypeMenu {

    private final GUIHelper helper;

    public EntityTypeMenu(GUIHelper helper) {
        this.helper = helper;
    }

    public void open(Player player, String bossId) {
        Inventory inv = Bukkit.createInventory(null, 54, GUIManager.TITLE_ENTITY_PREFIX + bossId);
        helper.decorateGui(inv);

        Object[][] entities = {
            {Material.ZOMBIE_HEAD,"ZOMBIE","Zombie"},{Material.SKELETON_SKULL,"SKELETON","Skeleton"},
            {Material.WITHER_SKELETON_SKULL,"WITHER_SKELETON","Wither Skeleton"},{Material.PIGLIN_HEAD,"PIGLIN_BRUTE","Piglin Brute"},
            {Material.IRON_BLOCK,"IRON_GOLEM","Iron Golem"},{Material.ROTTEN_FLESH,"HUSK","Husk"},
            {Material.BONE,"STRAY","Stray"},{Material.LEATHER,"DROWNED","Drowned"},
            {Material.CROSSBOW,"PILLAGER","Pillager"},{Material.IRON_AXE,"VINDICATOR","Vindicator"},
            {Material.TOTEM_OF_UNDYING,"EVOKER","Evoker"},{Material.STRING,"SPIDER","Spider"},
            {Material.GUNPOWDER,"CREEPER","Creeper"},{Material.BLAZE_ROD,"BLAZE","Blaze"},
            {Material.GHAST_TEAR,"GHAST","Ghast"},{Material.MAGMA_CREAM,"MAGMA_CUBE","Magma Cube"},
            {Material.SLIME_BALL,"SLIME","Slime"},{Material.ENDER_PEARL,"ENDERMAN","Enderman"},
            {Material.NETHER_STAR,"WITHER","Wither"}
        };

        int slot = 10;
        for (Object[] entity : entities) {
            if (slot == 17 || slot == 26 || slot == 35) slot += 2;
            if (slot > 43) break;
            inv.setItem(slot++, helper.createItem((Material)entity[0], "§f" + entity[2], false, "§7Type: §f" + entity[1], GuiConstants.CLICK_CHANGE));
        }

        inv.setItem(49, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }
}
