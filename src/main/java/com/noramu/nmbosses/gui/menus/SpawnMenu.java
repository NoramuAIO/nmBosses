package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.gui.GUIHelper;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.gui.handlers.ConfigHelper;
import com.noramu.nmbosses.utils.GuiConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class SpawnMenu {

    private final GUIHelper helper;
    private final ConfigHelper configHelper;

    public SpawnMenu(GUIHelper helper) {
        this.helper = helper;
        this.configHelper = new ConfigHelper(helper.getPlugin());
    }

    public void open(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 45, GUIManager.TITLE_SPAWN_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(11, helper.createToggleItem(Material.REPEATER, GuiConstants.AUTO_SPAWN, boss.isAutoSpawn()));

        // Read respawn delay directly from config to get latest value
        long respawnDelay = (long) configHelper.getCurrentDoubleValue(bossId, "spawn.respawnDelay");
        if (respawnDelay == 0) respawnDelay = boss.getRespawnDelay(); // Fallback to boss object
        
        inv.setItem(13, helper.createStatItem(Material.CLOCK, GuiConstants.RESPAWN_DELAY,
                respawnDelay + "s", "§8▪ §7Respawn time after death", helper.clickInfo(10, 60)));

        // Read max count directly from config to get latest value
        int maxCount = (int) configHelper.getCurrentDoubleValue(bossId, "spawn.maxCount");
        if (maxCount == 0) maxCount = boss.getMaxSpawnCount(); // Fallback to boss object
        
        inv.setItem(15, helper.createStatItem(Material.SPAWNER, GuiConstants.MAX_COUNT,
                String.valueOf(maxCount), "§8▪ §7Max simultaneous spawns", helper.clickInfo(1, 1)));

        inv.setItem(29, helper.createItem(Material.SUNFLOWER, GuiConstants.SPAWN_TIME, false,
                "§7Current: §e" + boss.getSpawnTime(), "§7(DAY, NIGHT, ALWAYS)", GuiConstants.CLICK_CHANGE));

        String daysDisplay = (boss.getSpawnDays() == null || boss.getSpawnDays().isEmpty())
                ? "§aEvery day" : "§e" + boss.getSpawnDays().size() + " days";
        inv.setItem(31, helper.createItem(Material.CLOCK, GuiConstants.SCHEDULE, true,
                "§8§m----------------",
                "§7Days: " + daysDisplay,
                "§7Hours: §f" + boss.getSpawnHourStart() + " - " + boss.getSpawnHourEnd(),
                "§8§m----------------",
                GuiConstants.CLICK_CHANGE));

        String locDisplay = boss.getSpawnLocation() != null
                ? "§f" + (int)boss.getSpawnLocation().getX() + ", " + (int)boss.getSpawnLocation().getY() + ", " + (int)boss.getSpawnLocation().getZ()
                : "§cNone";
        inv.setItem(33, helper.createItem(Material.COMPASS, GuiConstants.LOCATION, true,
                "§7Location: " + locDisplay, "§a▸ Save current location"));

        inv.setItem(36, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }

    public void openScheduleMenu(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 45, GUIManager.TITLE_SCHEDULE_PREFIX + bossId);
        helper.decorateGui(inv);

        inv.setItem(4, helper.createItem(Material.CLOCK, "§3§lTime Settings", true));

        String daysDisplay = (boss.getSpawnDays() == null || boss.getSpawnDays().isEmpty())
                ? "§aEvery day" : helper.formatDays(boss.getSpawnDays());
        inv.setItem(20, helper.createItem(Material.PAPER, GuiConstants.DAYS, true,
                "§7Current: " + daysDisplay, GuiConstants.CLICK_CHANGE));

        inv.setItem(22, helper.createItem(Material.LIME_DYE, GuiConstants.START_HOUR, false,
                "§7Current: §f" + boss.getSpawnHourStart(), "§7Spawn start time", GuiConstants.CLICK_CHANGE));

        inv.setItem(24, helper.createItem(Material.RED_DYE, GuiConstants.END_HOUR, false,
                "§7Current: §f" + boss.getSpawnHourEnd(), "§7Spawn end time", GuiConstants.CLICK_CHANGE));

        inv.setItem(40, helper.createItem(Material.BARRIER, GuiConstants.RESET, false, "§7Reset all time settings"));
        inv.setItem(36, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }

    public void openDaysMenu(Player player, String bossId) {
        Boss boss = helper.getPlugin().getBossManager().getBoss(bossId);
        if (boss == null) return;

        Inventory inv = Bukkit.createInventory(null, 45, GUIManager.TITLE_DAYS_PREFIX + bossId);
        helper.decorateGui(inv);

        List<String> currentDays = boss.getSpawnDays();
        if (currentDays == null) currentDays = new ArrayList<>();

        String[][] days = {
            {"MONDAY", "§9§lMonday"},
            {"TUESDAY", "§a§lTuesday"},
            {"WEDNESDAY", "§e§lWednesday"},
            {"THURSDAY", "§6§lThursday"},
            {"FRIDAY", "§c§lFriday"},
            {"SATURDAY", "§d§lSaturday"},
            {"SUNDAY", "§b§lSunday"}
        };

        int[] slots = {10,11,12,13,14,15,16};
        for (int i = 0; i < days.length; i++) {
            boolean selected = currentDays.contains(days[i][0]);
            Material mat = selected ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
            String status = selected ? GuiConstants.SELECTED : GuiConstants.NOT_SELECTED;
            inv.setItem(slots[i], helper.createItem(mat, days[i][1], selected, "§7Status: " + status, "§7ID: §f" + days[i][0], GuiConstants.CLICK_CHANGE));
        }

        inv.setItem(29, helper.createItem(Material.EMERALD, GuiConstants.SELECT_ALL, true));
        inv.setItem(33, helper.createItem(Material.REDSTONE, GuiConstants.CLEAR_ALL, false));
        inv.setItem(40, helper.createItem(Material.ARROW, GuiConstants.BTN_BACK, false));
        player.openInventory(inv);
    }
}
