package com.noramu.nmbosses.utils;

/**
 * GUI Constants - Hardcoded messages for GUI elements
 */
public class GuiConstants {

    // Values
    public static final String YES = "§a✔";
    public static final String NO = "§c✘";
    public static final String ACTIVE = "§aActive";
    public static final String INACTIVE = "§cInactive";
    
    // Buttons
    public static final String BTN_BACK = "§e◀ Back";
    public static final String BTN_CLOSE = "§c§lClose";
    public static final String BTN_ON = "§a✔ ON";
    public static final String BTN_OFF = "§c✘ OFF";
    public static final String CLICK_CHANGE = "§e▸ Click to change";
    
    // Boss List
    public static final String NEW_BOSS = "§a§l+ New Boss";
    public static final String RELOAD = "§6§lReload";
    
    // Edit Menu
    public static final String BASIC_STATS = "§c§lBasic Stats";
    public static final String APPEARANCE = "§b§lAppearance";
    public static final String EQUIPMENT = "§d§lEquipment";
    public static final String SPAWN_SETTINGS = "§a§lSpawn Settings";
    public static final String REWARDS = "§e§lRewards & Drops";
    public static final String COMMANDS = "§6§lCommands";
    public static final String ABILITIES = "§6§lAbilities";
    public static final String PHASES = "§4§lPhases";
    public static final String SPAWN_BOSS = "§2§l▶ Spawn Boss";
    public static final String DESPAWN_BOSS = "§cDespawn All";
    public static final String DELETE_BOSS = "§4§lDelete Boss";
    
    // Stats Menu
    public static final String HEALTH = "§c§lHealth";
    public static final String DAMAGE = "§c§lDamage";
    public static final String SPEED = "§b§lSpeed";
    public static final String KB_RESISTANCE = "§e§lKB Resistance";
    public static final String KB_POWER = "§6§lKB Power";
    public static final String ATTACK_SPEED = "§d§lAttack Speed";
    public static final String ATTACK_RANGE = "§c§lAttack Range";
    public static final String ENTITY_TYPE = "§2§lEntity Type";
    public static final String BOSS_CLASS = "§b§lBoss Class";
    
    // Appearance Menu
    public static final String GLOW = "§e§lGlow Effect";
    public static final String GLOW_COLOR = "§f§lGlow Color";
    public static final String NAME_VISIBLE = "§b§lName Visible";
    public static final String DISPLAY_NAME = "§6§lDisplay Name";
    public static final String SHOW_HEALTH = "§c§lShow Health";
    public static final String BOSSBAR = "§5§lBossBar";
    public static final String BOSSBAR_COLOR = "§d§lBossBar Color";
    public static final String BOSSBAR_STYLE = "§7§lBossBar Style";
    public static final String VIEW_DISTANCE = "§9§lView Distance";
    
    // Spawn Menu
    public static final String AUTO_SPAWN = "§a§lAuto Spawn";
    public static final String RESPAWN_DELAY = "§e§lRespawn Delay";
    public static final String MAX_COUNT = "§d§lMax Count";
    public static final String SPAWN_TIME = "§6§lSpawn Time";
    public static final String SCHEDULE = "§3§lSchedule";
    public static final String LOCATION = "§b§lSpawn Location";
    
    // Schedule Menu
    public static final String DAYS = "§9§lSpawn Days";
    public static final String START_HOUR = "§a§lStart Hour";
    public static final String END_HOUR = "§c§lEnd Hour";
    public static final String RESET = "§c§lReset";
    
    // Days
    public static final String SELECTED = "§a✔ SELECTED";
    public static final String NOT_SELECTED = "§c✘ NOT SELECTED";
    public static final String SELECT_ALL = "§a§lSelect All";
    public static final String CLEAR_ALL = "§c§lClear All";
    
    // Equipment Menu
    public static final String HELMET = "§e§lHelmet";
    public static final String CHESTPLATE = "§e§lChestplate";
    public static final String LEGGINGS = "§e§lLeggings";
    public static final String BOOTS = "§e§lBoots";
    public static final String MAIN_HAND = "§c§lMain Hand";
    public static final String OFF_HAND = "§b§lOff Hand";
    public static final String SHIELD = "§9§lShield";
    
    // Drops Menu
    public static final String XP_AMOUNT = "§a§lXP Amount";
    public static final String DROP_CHANCE = "§d§lDrop Chance";
    public static final String ADD_ITEM = "§a§l+ Add Item";
    public static final String ADD_COMMAND = "§a§l+ Add Command";
    
    // Abilities Menu
    public static final String NEW_ABILITY = "§a§l+ New Ability";
    public static final String NEW_PHASE = "§a§l+ New Phase";
    
    // Ability Edit
    public static final String ABILITY_NAME = "§e§lAbility Name";
    public static final String ABILITY_TYPE = "§b§lAbility Type";
    public static final String COOLDOWN = "§6§lCooldown";
    public static final String CHANCE = "§d§lChance";
    public static final String RANGE = "§a§lRange";
    public static final String MESSAGE = "§f§lMessage";
    public static final String SOUND = "§5§lSound";
    public static final String PARTICLE = "§e§lParticle";
    
    // Phase Edit
    public static final String PHASE_NAME = "§e§lPhase Name";
    public static final String THRESHOLD = "§c§lHealth Threshold";
    public static final String DAMAGE_MULT = "§c§lDamage Multiplier";
    public static final String SPEED_MULT = "§b§lSpeed Multiplier";
    public static final String REGEN = "§a§lRegeneration";
    public static final String REGEN_AMOUNT = "§a§lRegen Amount";
    public static final String INVULNERABLE = "§9§lInvulnerable";
    public static final String START_MESSAGE = "§f§lStart Message";

    // Slot constants for inventory
    public static final int[] DROP_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
    public static final int[] COMMAND_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
    
    // Equipment slots
    public static final int HELMET_SLOT = 10;
    public static final int CHESTPLATE_SLOT = 19;
    public static final int LEGGINGS_SLOT = 28;
    public static final int BOOTS_SLOT = 37;
    public static final int MAIN_HAND_SLOT = 14;
    public static final int OFF_HAND_SLOT = 23;

    // Boss types
    public static final String[] BOSS_TYPES = {"MELEE", "RANGED", "TANK", "MAGE", "HYBRID"};
    
    // Spawn times
    public static final String[] SPAWN_TIMES = {"ALWAYS", "DAY", "NIGHT"};
    
    // BossBar styles
    public static final String[] BOSSBAR_STYLES = {"SOLID", "SEGMENTED_6", "SEGMENTED_10", "SEGMENTED_12", "SEGMENTED_20"};
}
