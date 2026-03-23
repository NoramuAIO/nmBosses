package com.noramu.nmbosses.gui;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.gui.handlers.*;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * GUI Click Event Handler - Routes clicks to appropriate handlers
 * Modular design: delegates to specific handler classes
 */
public class GUIListener implements Listener {

    private final NmBosses plugin;
    private final SessionData session;
    private final ConfigHelper config;
    
    // Menu handlers
    private final BossListMenuHandler bossListHandler;
    private final BossEditMenuHandler bossEditHandler;
    private final ColorMenuHandler colorHandler;
    private final EntityTypeMenuHandler entityTypeHandler;
    private final CommandsMenuHandler commandsHandler;
    private final ScheduleMenuHandler scheduleHandler;
    private final DaysMenuHandler daysHandler;
    private final StatsMenuHandler statsHandler;
    private final AppearanceMenuHandler appearanceHandler;
    private final EquipmentMenuHandler equipmentHandler;
    private final DropsMenuHandler dropsHandler;
    private final AbilitiesMenuHandler abilitiesHandler;
    private final AbilityEditMenuHandler abilityEditHandler;
    private final AbilityTypeMenuHandler abilityTypeHandler;
    private final PhasesMenuHandler phasesHandler;
    private final PhaseEditMenuHandler phaseEditHandler;
    private final SpawnMenuHandler spawnHandler;
    private final CustomItemsMenuHandler customItemsHandler;
    private final ModelsMenuHandler modelsHandler;

    public GUIListener(NmBosses plugin) {
        this.plugin = plugin;
        this.session = new SessionData();
        this.config = new ConfigHelper(plugin);
        
        // Initialize all handlers
        this.bossListHandler = new BossListMenuHandler(plugin, config, session);
        this.bossEditHandler = new BossEditMenuHandler(plugin, config, session);
        this.colorHandler = new ColorMenuHandler(plugin, config, session);
        this.entityTypeHandler = new EntityTypeMenuHandler(plugin, config, session);
        this.commandsHandler = new CommandsMenuHandler(plugin, config, session);
        this.scheduleHandler = new ScheduleMenuHandler(plugin, config, session);
        this.daysHandler = new DaysMenuHandler(plugin, config, session);
        this.statsHandler = new StatsMenuHandler(plugin, config);
        this.appearanceHandler = new AppearanceMenuHandler(plugin, config, session);
        this.equipmentHandler = new EquipmentMenuHandler(plugin, config, session);
        this.dropsHandler = new DropsMenuHandler(plugin, config, session);
        this.abilitiesHandler = new AbilitiesMenuHandler(plugin, config, session);
        this.abilityEditHandler = new AbilityEditMenuHandler(plugin, config, session);
        this.abilityTypeHandler = new AbilityTypeMenuHandler(plugin, config, session);
        this.phasesHandler = new PhasesMenuHandler(plugin, config, session);
        this.phaseEditHandler = new PhaseEditMenuHandler(plugin, config, session);
        this.spawnHandler = new SpawnMenuHandler(plugin, config, session);
        this.customItemsHandler = new CustomItemsMenuHandler(plugin);
        this.modelsHandler = new ModelsMenuHandler(plugin, config, session);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (!title.startsWith("§8▸")) return;

        event.setCancelled(true);
        if (clicked.getType().name().contains("STAINED_GLASS_PANE")) return;

        String bossId = extractBossId(title);
        ClickType clickType = event.getClick();
        int slot = event.getSlot();

        routeClick(player, clicked, title, bossId, clickType, slot);
    }

    private void routeClick(Player player, ItemStack clicked, String title, String bossId, ClickType clickType, int slot) {
        if (title.equals(GUIManager.TITLE_LIST) || title.startsWith(GUIManager.TITLE_LIST)) {
            bossListHandler.handle(player, clicked);
        } else if (title.startsWith(GUIManager.TITLE_EDIT_PREFIX)) {
            bossEditHandler.handle(player, clicked, bossId);
        } else if (title.startsWith(GUIManager.TITLE_STATS_PREFIX)) {
            statsHandler.handle(player, clicked, bossId, clickType);
        } else if (title.startsWith(GUIManager.TITLE_APPEARANCE_PREFIX)) {
            appearanceHandler.handle(player, clicked, bossId, clickType);
        } else if (title.startsWith(GUIManager.TITLE_SPAWN_PREFIX)) {
            spawnHandler.handle(player, clicked, bossId, clickType, slot);
        } else if (title.startsWith(GUIManager.TITLE_COLOR_PREFIX)) {
            colorHandler.handle(player, clicked);
        } else if (title.startsWith(GUIManager.TITLE_ENTITY_PREFIX)) {
            entityTypeHandler.handle(player, clicked, bossId);
        } else if (title.startsWith(GUIManager.TITLE_EQUIPMENT_PREFIX)) {
            equipmentHandler.handle(player, clicked, bossId, clickType, slot);
        } else if (title.startsWith(GUIManager.TITLE_DROPS_PREFIX)) {
            dropsHandler.handle(player, clicked, bossId, clickType, slot);
        } else if (title.startsWith(GUIManager.TITLE_COMMANDS_PREFIX)) {
            commandsHandler.handle(player, clicked, bossId, clickType, slot);
        } else if (title.startsWith(GUIManager.TITLE_SCHEDULE_PREFIX)) {
            scheduleHandler.handle(player, clicked, bossId);
        } else if (title.startsWith(GUIManager.TITLE_DAYS_PREFIX)) {
            daysHandler.handle(player, clicked, bossId);
        } else if (title.startsWith(GUIManager.TITLE_ABILITIES_PREFIX)) {
            abilitiesHandler.handle(player, clicked, bossId, clickType);
        } else if (title.startsWith(GUIManager.TITLE_ABILITY_EDIT_PREFIX)) {
            abilityEditHandler.handle(player, clicked, extractBossId(title), clickType);
        } else if (title.startsWith(GUIManager.TITLE_ABILITY_TYPE_PREFIX)) {
            abilityTypeHandler.handle(player, clicked, extractBossId(title));
        } else if (title.startsWith(GUIManager.TITLE_PHASES_PREFIX)) {
            phasesHandler.handle(player, clicked, bossId, clickType);
        } else if (title.startsWith(GUIManager.TITLE_PHASE_EDIT_PREFIX)) {
            phaseEditHandler.handle(player, clicked, extractBossId(title), clickType);
        } else if (title.contains("Custom Items")) {
            handleCustomItemsMenuClick(player, clicked, title, bossId, slot);
        } else if (title.contains("Model")) {
            handleModelsMenuClick(player, clicked, title, bossId, slot);
        }
    }

    private void handleCustomItemsMenuClick(Player player, ItemStack clicked, String title, String bossId, int slot) {
        if (title.contains("Equipment")) {
            String slotName = extractSlotName(title);
            customItemsHandler.handleEquipmentCustomItemsClick(new InventoryClickEvent(null, null, slot, null, null) {
                @Override
                public ItemStack getCurrentItem() {
                    return clicked;
                }
            }, bossId, slotName);
        } else if (title.contains("Drop")) {
            if (title.contains("List")) {
                customItemsHandler.handleCustomItemDropsListClick(new InventoryClickEvent(null, null, slot, null, null) {
                    @Override
                    public ItemStack getCurrentItem() {
                        return clicked;
                    }
                }, bossId);
            } else {
                customItemsHandler.handleDropCustomItemsClick(new InventoryClickEvent(null, null, slot, null, null) {
                    @Override
                    public ItemStack getCurrentItem() {
                        return clicked;
                    }
                }, bossId);
            }
        }
    }

    private void handleModelsMenuClick(Player player, ItemStack clicked, String title, String bossId, int slot) {
        if (title.contains("ModelEngine")) {
            modelsHandler.handleModelEngineConfigClick(player, clicked, bossId, slot);
        } else if (title.contains("MythicMobs")) {
            modelsHandler.handleMythicMobsConfigClick(player, clicked, bossId, slot);
        } else if (title.contains("Model Plugins")) {
            modelsHandler.handleModelsMenuClick(player, clicked, bossId, slot);
        }
    }

    private String extractSlotName(String title) {
        if (title.contains("helmet")) return "helmet";
        if (title.contains("chestplate")) return "chestplate";
        if (title.contains("leggings")) return "leggings";
        if (title.contains("boots")) return "boots";
        if (title.contains("main_hand")) return "main_hand";
        if (title.contains("off_hand")) return "off_hand";
        return "";
    }

    private String extractBossId(String title) {
        int lastColon = title.lastIndexOf(":");
        if (lastColon != -1 && lastColon + 1 < title.length()) {
            return StringUtils.stripColor(title.substring(lastColon + 1));
        }
        return "";
    }
}
