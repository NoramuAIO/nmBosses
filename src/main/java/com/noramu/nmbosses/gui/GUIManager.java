package com.noramu.nmbosses.gui;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.gui.menus.*;
import org.bukkit.entity.Player;

/**
 * Boss GUI sistemini yöneten ana sınıf - Modüler Yapı
 */
public class GUIManager {

    private final NmBosses plugin;
    private final GUIHelper helper;
    
    // Menü sınıfları
    private final BossListMenu bossListMenu;
    private final BossEditMenu bossEditMenu;
    private final StatsMenu statsMenu;
    private final AppearanceMenu appearanceMenu;
    private final SpawnMenu spawnMenu;
    private final EquipmentMenu equipmentMenu;
    private final DropsMenu dropsMenu;
    private final AbilitiesMenu abilitiesMenu;
    private final PhasesMenu phasesMenu;
    private final EntityTypeMenu entityTypeMenu;
    private final ModelsMenu modelsMenu;

    // Menü Başlıkları (GUIListener eşleşmesi için)
    public static final String TITLE_LIST = "§8▸ §6§lBoss";
    public static final String TITLE_EDIT_PREFIX = "§8▸ §e§lEdit:";
    public static final String TITLE_STATS_PREFIX = "§8▸ §c§lStats:";
    public static final String TITLE_APPEARANCE_PREFIX = "§8▸ §b§lAppear:";
    public static final String TITLE_SPAWN_PREFIX = "§8▸ §a§lSpawn:";
    public static final String TITLE_COLOR_PREFIX = "§8▸ §d§lColor:";
    public static final String TITLE_ENTITY_PREFIX = "§8▸ §2§lEntity:";
    public static final String TITLE_EQUIPMENT_PREFIX = "§8▸ §d§lEquip:";
    public static final String TITLE_DROPS_PREFIX = "§8▸ §e§lDrops:";
    public static final String TITLE_COMMANDS_PREFIX = "§8▸ §6§lCmds:";
    public static final String TITLE_SCHEDULE_PREFIX = "§8▸ §3§lSched:";
    public static final String TITLE_DAYS_PREFIX = "§8▸ §9§lDays:";
    public static final String TITLE_ABILITIES_PREFIX = "§8▸ §6§lAbil:";
    public static final String TITLE_ABILITY_EDIT_PREFIX = "§8▸ §d§lAbilEdit:";
    public static final String TITLE_PHASES_PREFIX = "§8▸ §4§lPhases:";
    public static final String TITLE_PHASE_EDIT_PREFIX = "§8▸ §c§lPhaseEdit:";
    public static final String TITLE_ABILITY_TYPE_PREFIX = "§8▸ §e§lAbilType:";

    public GUIManager(NmBosses plugin) {
        this.plugin = plugin;
        this.helper = new GUIHelper(plugin);
        
        // Menüleri başlat
        this.bossListMenu = new BossListMenu(helper);
        this.bossEditMenu = new BossEditMenu(helper);
        this.statsMenu = new StatsMenu(helper);
        this.appearanceMenu = new AppearanceMenu(helper);
        this.spawnMenu = new SpawnMenu(helper);
        this.equipmentMenu = new EquipmentMenu(helper);
        this.dropsMenu = new DropsMenu(helper);
        this.abilitiesMenu = new AbilitiesMenu(helper);
        this.phasesMenu = new PhasesMenu(helper);
        this.entityTypeMenu = new EntityTypeMenu(helper);
        this.modelsMenu = new ModelsMenu(plugin);
    }

    public GUIHelper getHelper() { return helper; }

    // ═══════════════════════════════════════════════════════════
    //                      MENÜ AÇMA METODLARİ
    // ═══════════════════════════════════════════════════════════

    public void openBossListMenu(Player player, int page) {
        bossListMenu.open(player, page);
    }

    public void openBossEditMenu(Player player, String bossId) {
        bossEditMenu.open(player, bossId);
    }

    public void openStatsMenu(Player player, String bossId) {
        statsMenu.open(player, bossId);
    }

    public void openAppearanceMenu(Player player, String bossId) {
        appearanceMenu.open(player, bossId);
    }

    public void openColorMenu(Player player, String bossId, String type) {
        appearanceMenu.openColorMenu(player, bossId, type);
    }

    public void openSpawnMenu(Player player, String bossId) {
        spawnMenu.open(player, bossId);
    }

    public void openScheduleMenu(Player player, String bossId) {
        spawnMenu.openScheduleMenu(player, bossId);
    }

    public void openDaysMenu(Player player, String bossId) {
        spawnMenu.openDaysMenu(player, bossId);
    }

    public void openEquipmentMenu(Player player, String bossId) {
        equipmentMenu.open(player, bossId);
    }

    public void openDropsMenu(Player player, String bossId) {
        dropsMenu.open(player, bossId);
    }

    public void openCommandsMenu(Player player, String bossId) {
        dropsMenu.openCommandsMenu(player, bossId);
    }

    public void openAbilitiesMenu(Player player, String bossId) {
        abilitiesMenu.open(player, bossId);
    }

    public void openAbilityEditMenu(Player player, String bossId, String abilityId) {
        abilitiesMenu.openEditMenu(player, bossId, abilityId);
    }

    public void openAbilityTypeMenu(Player player, String bossId, String abilityId) {
        abilitiesMenu.openTypeMenu(player, bossId, abilityId);
    }

    public void openPhasesMenu(Player player, String bossId) {
        phasesMenu.open(player, bossId);
    }

    public void openPhaseEditMenu(Player player, String bossId, int phaseNumber) {
        phasesMenu.openEditMenu(player, bossId, phaseNumber);
    }

    public void openEntityTypeMenu(Player player, String bossId) {
        entityTypeMenu.open(player, bossId);
    }

    public void openModelsMenu(Player player, String bossId) {
        player.openInventory(modelsMenu.openModelsMenu(bossId));
    }
}
