package com.noramu.nmbosses.managers;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.BossAbility;
import com.noramu.nmbosses.BossPhase;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Boss config dosyalarından Boss objesi yükleme
 */
public class BossLoader {

    private final BossConfigHelper configHelper;

    public BossLoader(BossConfigHelper configHelper) {
        this.configHelper = configHelper;
    }

    /**
     * Boss config dosyasından Boss objesi yükler
     */
    public Boss loadBoss(File file) {
        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            String id = file.getName().replace(".yml", "");
            String displayName = config.getString("displayName", "&c" + id);
            String bossType = config.getString("bossType", "MELEE");
            String entityType = config.getString("entityType", "ZOMBIE");

            // Temel özellikler
            double health = config.getDouble("health", 100);
            double damage = config.getDouble("damage", 10);
            double speed = config.getDouble("speed", 0.25);
            double knockbackResistance = config.getDouble("knockbackResistance", 0.5);
            double attackSpeed = config.getDouble("attackSpeed", 1.0);
            double followRange = config.getDouble("followRange", 32);
            double attackRange = config.getDouble("attackRange", 3);
            double knockbackPower = config.getDouble("knockbackPower", 0.4);

            // Görünüm
            boolean glowing = config.getBoolean("glowing", false);
            String glowColor = config.getString("glowColor", "RED");
            boolean nameVisible = config.getBoolean("nameVisible", true);
            boolean showHealth = config.getBoolean("showHealth", true);
            boolean showBossBar = config.getBoolean("bossBar.enabled", true);
            String bossBarColor = config.getString("bossBar.color", "RED");
            String bossBarStyle = config.getString("bossBar.style", "SEGMENTED_10");
            double bossBarRadius = config.getDouble("bossBar.radius", 50.0);

            // Armor yükle
            Map<String, ItemStack> armor = new HashMap<>();
            if (config.isConfigurationSection("armor")) {
                armor.put("helmet", configHelper.parseItemStack(config.getString("armor.helmet", "AIR")));
                armor.put("chestplate", configHelper.parseItemStack(config.getString("armor.chestplate", "AIR")));
                armor.put("leggings", configHelper.parseItemStack(config.getString("armor.leggings", "AIR")));
                armor.put("boots", configHelper.parseItemStack(config.getString("armor.boots", "AIR")));
            }

            ItemStack mainHand = configHelper.parseItemStack(config.getString("equipment.mainHand", "AIR"));
            ItemStack offHand = configHelper.parseItemStack(config.getString("equipment.offHand", "AIR"));
            boolean hasShield = config.getBoolean("equipment.shield", false);

            if (hasShield && (offHand == null || offHand.getType() == Material.AIR)) {
                offHand = new ItemStack(Material.SHIELD);
            }

            // Drop komutları ve itemleri
            List<String> dropCommands = config.getStringList("rewards.commands");
            List<ItemStack> drops = new ArrayList<>();
            if (config.isList("rewards.items")) {
                for (String itemStr : config.getStringList("rewards.items")) {
                    ItemStack item = configHelper.parseItemStackWithAmount(itemStr);
                    if (item != null && item.getType() != Material.AIR) {
                        drops.add(item);
                    }
                }
            }

            int xp = config.getInt("rewards.xp", 100);
            double dropChance = config.getDouble("rewards.dropChance", 100);

            // Mesajlar
            String spawnMessage = config.getString("messages.spawn", "");
            String deathMessage = config.getString("messages.death", "");
            List<String> periodicMessages = config.getStringList("messages.periodic");
            int periodicMessageInterval = config.getInt("messages.periodicInterval", 30);

            // Spawn ayarları
            Location spawnLocation = null;
            if (config.isConfigurationSection("spawn.location")) {
                String worldName = config.getString("spawn.location.world", "world");
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    double x = config.getDouble("spawn.location.x", 0);
                    double y = config.getDouble("spawn.location.y", 64);
                    double z = config.getDouble("spawn.location.z", 0);
                    float yaw = (float) config.getDouble("spawn.location.yaw", 0);
                    float pitch = (float) config.getDouble("spawn.location.pitch", 0);
                    spawnLocation = new Location(world, x, y, z, yaw, pitch);
                    DebugLogger.log("BossLoader", "Spawn location loaded for " + id + ": " + worldName + " (" + x + ", " + y + ", " + z + ")");
                } else {
                    DebugLogger.log("BossLoader", "WARNING: World '" + worldName + "' not found for boss " + id + ". Available worlds: " + Bukkit.getWorlds().stream().map(World::getName).reduce((a, b) -> a + ", " + b).orElse("none"));
                }
            }

            long respawnDelay = config.getLong("spawn.respawnDelay", 300);
            boolean autoSpawn = config.getBoolean("spawn.autoSpawn", false);
            int maxSpawnCount = config.getInt("spawn.maxCount", 1);
            String spawnTime = config.getString("spawn.time", "ALWAYS");
            
            // Gerçek dünya zaman ayarları
            List<String> spawnDays = config.getStringList("spawn.schedule.days");
            String spawnHourStart = config.getString("spawn.schedule.startHour", "00:00");
            String spawnHourEnd = config.getString("spawn.schedule.endHour", "23:59");

            // Ses efektleri
            String spawnSound = config.getString("sounds.spawn", "ENTITY_ENDER_DRAGON_GROWL");
            String deathSound = config.getString("sounds.death", "ENTITY_ENDER_DRAGON_DEATH");
            String hitSound = config.getString("sounds.hit", "ENTITY_IRON_GOLEM_HURT");
            String attackSound = config.getString("sounds.attack", "ENTITY_PLAYER_ATTACK_STRONG");

            // Parçacık efektleri
            String spawnParticle = config.getString("particles.spawn", "EXPLOSION_LARGE");
            String deathParticle = config.getString("particles.death", "EXPLOSION_HUGE");
            String attackParticle = config.getString("particles.attack", "CRIT");

            // Yetenekler
            boolean abilitiesEnabled = config.getBoolean("abilities.enabled", false);
            List<BossAbility> abilities = new ArrayList<>();
            if (config.isConfigurationSection("abilities.list")) {
                ConfigurationSection abilitiesSection = config.getConfigurationSection("abilities.list");
                for (String abilityId : abilitiesSection.getKeys(false)) {
                    ConfigurationSection abilitySection = abilitiesSection.getConfigurationSection(abilityId);
                    if (abilitySection != null) {
                        abilities.add(new BossAbility(
                                abilityId,
                                abilitySection.getString("name", abilityId),
                                abilitySection.getString("type", "FIREBALL"),
                                abilitySection.getDouble("cooldown", 10),
                                abilitySection.getDouble("chance", 30),
                                abilitySection.getDouble("damage", 5),
                                abilitySection.getDouble("range", 10),
                                abilitySection.getString("message", ""),
                                abilitySection.getString("sound", ""),
                                abilitySection.getString("particle", ""),
                                abilitySection.getInt("effectDuration", 100),
                                abilitySection.getInt("effectAmplifier", 0),
                                abilitySection.getString("effectType", "POISON"),
                                abilitySection.getString("summonType", "ZOMBIE"),
                                abilitySection.getInt("summonCount", 3)));
                    }
                }
            }

            // Faz sistemi
            boolean phaseSystemEnabled = config.getBoolean("phases.enabled", false);
            List<BossPhase> phases = new ArrayList<>();
            if (config.isConfigurationSection("phases.list")) {
                ConfigurationSection phasesSection = config.getConfigurationSection("phases.list");
                for (String phaseId : phasesSection.getKeys(false)) {
                    ConfigurationSection phaseSection = phasesSection.getConfigurationSection(phaseId);
                    if (phaseSection != null) {
                        phases.add(new BossPhase(
                                phaseSection.getInt("number", 1),
                                phaseSection.getString("name", "Faz " + phaseId),
                                phaseSection.getDouble("healthThreshold", 50),
                                phaseSection.getDouble("damageMultiplier", 1.5),
                                phaseSection.getDouble("speedMultiplier", 1.2),
                                phaseSection.getDouble("attackSpeedMultiplier", 1.3),
                                phaseSection.getBoolean("glowing", true),
                                phaseSection.getString("bossBarColor", "YELLOW"),
                                phaseSection.getString("nameColor", "&e"),
                                phaseSection.getString("startMessage", ""),
                                phaseSection.getString("startSound", "ENTITY_WITHER_SPAWN"),
                                phaseSection.getString("startParticle", "EXPLOSION_LARGE"),
                                phaseSection.getBoolean("regeneration", false),
                                phaseSection.getDouble("regenerationAmount", 1),
                                phaseSection.getBoolean("invulnerableOnChange", true),
                                phaseSection.getInt("invulnerableDuration", 40)));
                    }
                }
            }

            // Fazları sırala (yüksek threshold önce)
            phases.sort((a, b) -> Double.compare(b.getHealthThreshold(), a.getHealthThreshold()));

            return new Boss(id, displayName, bossType, entityType, health, damage, speed, knockbackResistance,
                    attackSpeed, followRange, attackRange, knockbackPower,
                    glowing, glowColor, nameVisible, showHealth, showBossBar,
                    bossBarColor, bossBarStyle, bossBarRadius, armor, mainHand, offHand, hasShield, dropCommands, drops,
                    xp, dropChance,
                    spawnMessage, deathMessage, periodicMessages, periodicMessageInterval, spawnLocation, respawnDelay,
                    autoSpawn, maxSpawnCount, spawnTime, spawnDays, spawnHourStart, spawnHourEnd,
                    spawnSound, deathSound, hitSound, attackSound,
                    spawnParticle, deathParticle, attackParticle, abilitiesEnabled, abilities, phaseSystemEnabled,
                    phases);

        } catch (Exception e) {
            DebugLogger.log("BossLoader", "Boss dosyası yüklenirken hata: " + file.getName(), e);
            return null;
        }
    }
}
