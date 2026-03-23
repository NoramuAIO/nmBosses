package com.noramu.nmbosses.managers;

import com.noramu.nmbosses.ActiveBoss;
import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.BossAbility;
import com.noramu.nmbosses.BossPhase;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossManager {

    private final NmBosses plugin;
    private final Map<String, Boss> bosses = new ConcurrentHashMap<>();
    private final Map<UUID, ActiveBoss> activeBosses = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> invalidEntityTicks = new ConcurrentHashMap<>();
    private final File bossesFolder;
    private Scoreboard scoreboard;
    private BossPersistenceManager persistenceManager;
    private BossConfigHelper configHelper;
    private BossLoader bossLoader;

    public BossManager(NmBosses plugin) {
        this.plugin = plugin;
        this.bossesFolder = new File(plugin.getDataFolder(), "bosses");
        if (!bossesFolder.exists()) {
            bossesFolder.mkdirs();
        }
        
        // Null check for scoreboard
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = (manager != null) ? manager.getMainScoreboard() : null;
        
        // Initialize helper managers
        this.persistenceManager = new BossPersistenceManager(plugin, bossesFolder, activeBosses, bosses);
        this.persistenceManager.setBossManager(this);
        this.configHelper = new BossConfigHelper(bossesFolder, bosses);
        this.configHelper.setBossManager(this);
        this.bossLoader = new BossLoader(configHelper);
        
        startCleanupTask();
    }

    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Iterator<Map.Entry<UUID, ActiveBoss>> iterator = activeBosses.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, ActiveBoss> entry = iterator.next();
                ActiveBoss activeBoss = entry.getValue();
                LivingEntity entity = activeBoss.getEntity();
                UUID entityUUID = entry.getKey();

                // Entity null veya dead ise kaldır
                if (entity == null) {
                    DebugLogger.log("BossManager", "Removing boss (entity is null): " + activeBoss.getBoss().getId());
                    activeBoss.remove();
                    iterator.remove();
                    invalidEntityTicks.remove(entityUUID);
                    continue;
                }
                
                if (entity.isDead()) {
                    DebugLogger.log("BossManager", "Removing boss (entity is dead): " + activeBoss.getBoss().getId());
                    activeBoss.remove();
                    iterator.remove();
                    invalidEntityTicks.remove(entityUUID);
                    continue;
                }
                
                // Entity invalid ise - birkaç tick bekle
                if (!entity.isValid()) {
                    // Invalid tick sayısını artır
                    int ticks = invalidEntityTicks.getOrDefault(entityUUID, 0) + 1;
                    invalidEntityTicks.put(entityUUID, ticks);
                    
                    // 10 tick (0.5 saniye) bekle
                    if (ticks >= 10) {
                        // Chunk loaded mı kontrol et
                        boolean chunkLoaded = entity.getLocation().getChunk().isLoaded();
                        if (chunkLoaded) {
                            // Chunk loaded ama entity hala invalid - boss gerçekten ölmüş
                            DebugLogger.log("BossManager", "Removing boss (entity invalid for 10 ticks, chunk loaded): " + activeBoss.getBoss().getId());
                            activeBoss.remove();
                            iterator.remove();
                            invalidEntityTicks.remove(entityUUID);
                        } else {
                            // Chunk unloaded - boss hala aktif, counter'ı sıfırla
                            DebugLogger.log("BossManager", "Keeping boss (entity invalid, chunk unloaded): " + activeBoss.getBoss().getId() + " at " + entity.getLocation().getWorld().getName());
                            invalidEntityTicks.remove(entityUUID);
                        }
                    }
                } else {
                    // Entity valid - counter'ı sıfırla
                    invalidEntityTicks.remove(entityUUID);
                }
            }
        }, 100L, 100L);
    }

    /**
     * Tüm boss dosyalarını yükler
     */
    public void loadAllBosses() {
        bosses.clear();

        File[] files = bossesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            createDefaultBoss();
            files = bossesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        }

        if (files != null) {
            for (File file : files) {
                Boss boss = loadBoss(file);
                if (boss != null) {
                    bosses.put(boss.getId(), boss);
                    DebugLogger.log("BossManager", "Boss yüklendi: " + boss.getId());
                }
            }
        }

        DebugLogger.log("BossManager", "Toplam " + bosses.size() + " boss yüklendi.");
    }

    /**
     * Reload sırasında aktif boss'ları kaldırmadan sadece config'leri yükler
     */
    public void reloadBossConfigs() {
        // Eski config'leri temizle
        bosses.clear();

        File[] files = bossesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                Boss boss = loadBoss(file);
                if (boss != null) {
                    bosses.put(boss.getId(), boss);
                }
            }
        }

        // Aktif boss'ların Boss referanslarını güncelle
        for (ActiveBoss activeBoss : activeBosses.values()) {
            String bossId = activeBoss.getBoss().getId();
            Boss newBoss = bosses.get(bossId);
            if (newBoss != null) {
                activeBoss.updateBossConfig(newBoss);
            }
        }

        DebugLogger.log("BossManager", "Boss config'leri yeniden yüklendi. Aktif boss'lar korundu.");
    }

    /**
     * Tek bir boss dosyasını yükler
     */
    public Boss loadBoss(File file) {
        return bossLoader.loadBoss(file);
    }

    /**
     * Varsayılan boss oluşturur
     */
    public void createDefaultBoss() {
        File defaultBoss = new File(bossesFolder, "dragon_knight.yml");
        if (defaultBoss.exists())
            return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(defaultBoss);

        config.set("displayName", "&c&l⚔ Dragon Knight ⚔");
        config.set("bossType", "MELEE");
        config.set("entityType", "ZOMBIE");

        config.set("health", 500);
        config.set("damage", 15);
        config.set("speed", 0.28);
        config.set("knockbackResistance", 0.8);
        config.set("attackSpeed", 1.2);
        config.set("followRange", 40);
        config.set("attackRange", 3);
        config.set("knockbackPower", 0.6);

        config.set("glowing", true);
        config.set("glowColor", "RED");
        config.set("nameVisible", true);
        config.set("showHealth", true);

        config.set("bossBar.enabled", true);
        config.set("bossBar.color", "RED");
        config.set("bossBar.style", "SEGMENTED_10");
        config.set("bossBar.radius", 50.0);

        config.set("armor.helmet", "NETHERITE_HELMET");
        config.set("armor.chestplate", "NETHERITE_CHESTPLATE");
        config.set("armor.leggings", "NETHERITE_LEGGINGS");
        config.set("armor.boots", "NETHERITE_BOOTS");

        config.set("equipment.mainHand", "NETHERITE_SWORD");
        config.set("equipment.offHand", "SHIELD");
        config.set("equipment.shield", true);

        config.set("rewards.xp", 500);
        config.set("rewards.dropChance", 100);
        config.set("rewards.items", Arrays.asList("DIAMOND:5", "NETHERITE_INGOT:1", "GOLDEN_APPLE:3"));
        config.set("rewards.commands", Arrays.asList(
                "broadcast &6{player} &e%boss% &6adlı boss'u öldürdü!",
                "give {player} diamond 10"));

        config.set("messages.spawn", "&c&l⚠ &eDragon Knight &cspawn oldu! Dikkatli olun!");
        config.set("messages.death", "&a&l✓ &eDragon Knight &ayenildi!");
        config.set("messages.periodic", Arrays.asList(
                "&c[Dragon Knight] &7Sizi yok edeceğim!",
                "&c[Dragon Knight] &7Güçsüz ölümlüler..."));
        config.set("messages.periodicInterval", 30);

        config.set("spawn.autoSpawn", false);
        config.set("spawn.respawnDelay", 300);
        config.set("spawn.maxCount", 1);
        config.set("spawn.time", "ALWAYS");
        config.set("spawn.location.world", "world");
        config.set("spawn.location.x", 0);
        config.set("spawn.location.y", 64);
        config.set("spawn.location.z", 0);

        config.set("sounds.spawn", "ENTITY_ENDER_DRAGON_GROWL");
        config.set("sounds.death", "ENTITY_ENDER_DRAGON_DEATH");
        config.set("sounds.hit", "ENTITY_IRON_GOLEM_HURT");
        config.set("sounds.attack", "ENTITY_PLAYER_ATTACK_STRONG");

        config.set("particles.spawn", "EXPLOSION_LARGE");
        config.set("particles.death", "EXPLOSION_HUGE");
        config.set("particles.attack", "CRIT");

        config.set("abilities.enabled", true);
        config.set("abilities.list.fireball.name", "Ateş Topu");
        config.set("abilities.list.fireball.type", "FIREBALL");
        config.set("abilities.list.fireball.cooldown", 15);
        config.set("abilities.list.fireball.chance", 30);
        config.set("abilities.list.fireball.damage", 8);
        config.set("abilities.list.fireball.range", 20);
        config.set("abilities.list.fireball.message", "&c[Dragon Knight] &7Yanacaksınız!");
        config.set("abilities.list.fireball.sound", "ENTITY_BLAZE_SHOOT");
        config.set("abilities.list.fireball.particle", "FLAME");

        config.set("abilities.list.summon.name", "Minyon Çağırma");
        config.set("abilities.list.summon.type", "SUMMON");
        config.set("abilities.list.summon.cooldown", 30);
        config.set("abilities.list.summon.chance", 20);
        config.set("abilities.list.summon.range", 15);
        config.set("abilities.list.summon.summonType", "ZOMBIE");
        config.set("abilities.list.summon.summonCount", 3);
        config.set("abilities.list.summon.message", "&c[Dragon Knight] &7Yardıma gelin!");
        config.set("abilities.list.summon.sound", "ENTITY_EVOKER_PREPARE_SUMMON");

        config.set("phases.enabled", true);
        config.set("phases.list.phase1.number", 1);
        config.set("phases.list.phase1.name", "Öfkeli");
        config.set("phases.list.phase1.healthThreshold", 50);
        config.set("phases.list.phase1.damageMultiplier", 1.5);
        config.set("phases.list.phase1.speedMultiplier", 1.2);
        config.set("phases.list.phase1.attackSpeedMultiplier", 1.3);
        config.set("phases.list.phase1.glowing", true);
        config.set("phases.list.phase1.bossBarColor", "YELLOW");
        config.set("phases.list.phase1.nameColor", "&e");
        config.set("phases.list.phase1.startMessage", "&e&l⚠ Dragon Knight öfkeleniyor!");
        config.set("phases.list.phase1.startSound", "ENTITY_ENDER_DRAGON_GROWL");
        config.set("phases.list.phase1.startParticle", "EXPLOSION_LARGE");
        config.set("phases.list.phase1.regeneration", false);
        config.set("phases.list.phase1.invulnerableOnChange", true);
        config.set("phases.list.phase1.invulnerableDuration", 40);

        config.set("phases.list.phase2.number", 2);
        config.set("phases.list.phase2.name", "Çılgınlık");
        config.set("phases.list.phase2.healthThreshold", 25);
        config.set("phases.list.phase2.damageMultiplier", 2.0);
        config.set("phases.list.phase2.speedMultiplier", 1.5);
        config.set("phases.list.phase2.attackSpeedMultiplier", 1.5);
        config.set("phases.list.phase2.glowing", true);
        config.set("phases.list.phase2.bossBarColor", "RED");
        config.set("phases.list.phase2.nameColor", "&4");
        config.set("phases.list.phase2.startMessage", "&4&l⚠ Dragon Knight çıldırıyor!");
        config.set("phases.list.phase2.startSound", "ENTITY_WITHER_SPAWN");
        config.set("phases.list.phase2.startParticle", "EXPLOSION_HUGE");
        config.set("phases.list.phase2.regeneration", true);
        config.set("phases.list.phase2.regenerationAmount", 2);
        config.set("phases.list.phase2.invulnerableOnChange", true);
        config.set("phases.list.phase2.invulnerableDuration", 60);

        try {
            config.save(defaultBoss);
            DebugLogger.log("BossManager", "Varsayılan boss oluşturuldu: dragon_knight.yml");
        } catch (IOException e) {
            DebugLogger.log("BossManager", "Varsayılan boss oluşturulurken hata", e);
        }
    }

    /**
     * Yeni bir boss oluşturur
     */
    public boolean createBoss(String id, String entityType) {
        File bossFile = new File(bossesFolder, id + ".yml");
        if (bossFile.exists()) {
            return false;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);

        config.set("displayName", "&c" + id);
        config.set("bossType", "MELEE");
        config.set("entityType", entityType);

        config.set("health", 100);
        config.set("damage", 10);
        config.set("speed", 0.25);
        config.set("knockbackResistance", 0.5);
        config.set("attackSpeed", 1.0);
        config.set("followRange", 32);
        config.set("attackRange", 3);
        config.set("knockbackPower", 0.4);

        config.set("glowing", false);
        config.set("glowColor", "WHITE");
        config.set("nameVisible", true);
        config.set("showHealth", true);

        config.set("bossBar.enabled", true);
        config.set("bossBar.color", "RED");
        config.set("bossBar.style", "SOLID");
        config.set("bossBar.radius", 40.0);

        config.set("armor.helmet", "AIR");
        config.set("armor.chestplate", "AIR");
        config.set("armor.leggings", "AIR");
        config.set("armor.boots", "AIR");

        config.set("equipment.mainHand", "AIR");
        config.set("equipment.offHand", "AIR");
        config.set("equipment.shield", false);

        config.set("rewards.xp", 100);
        config.set("rewards.dropChance", 100);
        config.set("rewards.items", new ArrayList<>());
        config.set("rewards.commands", new ArrayList<>());

        config.set("messages.spawn", "");
        config.set("messages.death", "");
        config.set("messages.periodic", new ArrayList<>());
        config.set("messages.periodicInterval", 30);

        config.set("spawn.autoSpawn", false);
        config.set("spawn.respawnDelay", 300);
        config.set("spawn.maxCount", 1);
        config.set("spawn.time", "ALWAYS");
        config.set("spawn.schedule.days", new ArrayList<>());
        config.set("spawn.schedule.startHour", "00:00");
        config.set("spawn.schedule.endHour", "23:59");

        config.set("sounds.spawn", "ENTITY_ENDER_DRAGON_GROWL");
        config.set("sounds.death", "ENTITY_ENDER_DRAGON_DEATH");
        config.set("sounds.hit", "ENTITY_IRON_GOLEM_HURT");
        config.set("sounds.attack", "ENTITY_PLAYER_ATTACK_STRONG");

        config.set("particles.spawn", "EXPLOSION_LARGE");
        config.set("particles.death", "EXPLOSION_HUGE");
        config.set("particles.attack", "CRIT");

        config.set("abilities.enabled", false);
        config.set("phases.enabled", false);

        try {
            config.save(bossFile);
            Boss boss = loadBoss(bossFile);
            if (boss != null) {
                bosses.put(id, boss);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Boss'u siler
     */
    public boolean deleteBoss(String id) {
        Boss boss = bosses.get(id);
        if (boss == null) {
            return false;
        }

        despawnBoss(id);

        File bossFile = new File(bossesFolder, id + ".yml");
        if (bossFile.exists()) {
            bossFile.delete();
        }

        bosses.remove(id);
        return true;
    }

    /**
     * Boss'u spawn eder
     */
    public LivingEntity spawnBoss(String id, Location location) {
        Boss boss = bosses.get(id);
        if (boss == null) {
            return null;
        }

        // Zaman kontrolü
        if (!canSpawnAtTime(boss, location.getWorld())) {
            return null;
        }

        // Max spawn kontrolü - Read from config to get latest value
        int maxSpawnCount = boss.getMaxSpawnCount();
        
        // Try to read from config for latest value
        try {
            File bossFile = new File(getBossesFolder(), id + ".yml");
            if (bossFile.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
                int configMaxCount = config.getInt("spawn.maxCount", maxSpawnCount);
                if (configMaxCount > 0) {
                    maxSpawnCount = configMaxCount;
                }
            }
        } catch (Exception e) {
            DebugLogger.log("BossManager", "Error reading maxCount from config, using Boss object value", e);
        }
        
        int currentCount = 0;
        for (ActiveBoss activeBoss : activeBosses.values()) {
            if (activeBoss.getBoss().getId().equals(id)) {
                currentCount++;
                DebugLogger.log("BossManager", "Found active boss " + id + " in world: " + activeBoss.getEntity().getLocation().getWorld().getName());
            }
        }

        DebugLogger.log("BossManager", "Spawn check for " + id + ": current=" + currentCount + ", max=" + maxSpawnCount + ", total active bosses=" + activeBosses.size());

        if (currentCount >= maxSpawnCount) {
            DebugLogger.log("BossManager", "Cannot spawn boss " + id + ": max count reached (" + currentCount + "/" + maxSpawnCount + ")");
            return null;
        }

        // Entity oluştur
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(boss.getEntityType().toUpperCase());
        } catch (Exception e) {
            entityType = EntityType.ZOMBIE;
        }

        Entity entity = location.getWorld().spawnEntity(location, entityType);
        if (!(entity instanceof LivingEntity)) {
            entity.remove();
            return null;
        }

        LivingEntity livingEntity = (LivingEntity) entity;

        // İsim ayarla
        livingEntity.setCustomName(colorize(boss.getDisplayName()));
        livingEntity.setCustomNameVisible(boss.isNameVisible());

        // Glow ayarla
        livingEntity.setGlowing(boss.isGlowing());

        // Glow rengi ayarla
        if (boss.isGlowing() && boss.getGlowColor() != null) {
            setGlowColor(livingEntity, boss.getGlowColor(), id);
        }

        // Sağlık ayarla
        if (livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(boss.getHealth());
            livingEntity.setHealth(boss.getHealth());
        }

        // Saldırı hasarı ayarla
        if (livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
            livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(boss.getDamage());
        }

        // Hız ayarla
        if (livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
            livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(boss.getSpeed());
        }

        // Knockback direnci ayarla
        if (livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE) != null) {
            livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)
                    .setBaseValue(boss.getKnockbackResistance());
        }

        // Takip mesafesi ayarla
        if (livingEntity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE) != null) {
            livingEntity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(boss.getFollowRange());
        }

        // Saldırı hızı ayarla
        if (livingEntity.getAttribute(Attribute.GENERIC_ATTACK_SPEED) != null) {
            livingEntity.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(boss.getAttackSpeed());
        }

        // Ekipman ayarla
        EntityEquipment equipment = livingEntity.getEquipment();
        if (equipment != null) {
            if (boss.getArmor() != null) {
                equipment.setHelmet(boss.getArmor().get("helmet"));
                equipment.setChestplate(boss.getArmor().get("chestplate"));
                equipment.setLeggings(boss.getArmor().get("leggings"));
                equipment.setBoots(boss.getArmor().get("boots"));
            }

            if (boss.getMainHand() != null) {
                equipment.setItemInMainHand(boss.getMainHand());
            }

            if (boss.getOffHand() != null) {
                equipment.setItemInOffHand(boss.getOffHand());
            }

            equipment.setHelmetDropChance(0);
            equipment.setChestplateDropChance(0);
            equipment.setLeggingsDropChance(0);
            equipment.setBootsDropChance(0);
            equipment.setItemInMainHandDropChance(0);
            equipment.setItemInOffHandDropChance(0);
        }

        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setPersistent(true); // Entity must be persistent to survive chunk unload
        livingEntity.addScoreboardTag("nmBoss:" + id);

        // ActiveBoss olarak kaydet
        ActiveBoss activeBoss = new ActiveBoss(plugin, boss, livingEntity);
        activeBosses.put(livingEntity.getUniqueId(), activeBoss);
        
        DebugLogger.log("BossManager", "Boss spawned successfully: " + id + " (UUID: " + livingEntity.getUniqueId() + "), total active: " + activeBosses.size());

        // Spawn mesajı
        if (boss.getSpawnMessage() != null && !boss.getSpawnMessage().isEmpty()) {
            String message = boss.getSpawnMessage()
                    .replace("{boss}", boss.getDisplayName())
                    .replace("{world}", location.getWorld().getName())
                    .replace("{x}", String.valueOf((int) location.getX()))
                    .replace("{y}", String.valueOf((int) location.getY()))
                    .replace("{z}", String.valueOf((int) location.getZ()));
            for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(colorize(message));
            }
        }

        // Spawn sesi
        if (boss.getSpawnSound() != null && !boss.getSpawnSound().isEmpty()) {
            try {
                Sound sound = Sound.valueOf(boss.getSpawnSound().toUpperCase());
                location.getWorld().playSound(location, sound, 1.0f, 1.0f);
            } catch (Exception ignored) {
            }
        }

        // Spawn parçacıkları
        if (boss.getSpawnParticle() != null && !boss.getSpawnParticle().isEmpty()) {
            try {
                Particle particle = Particle.valueOf(boss.getSpawnParticle().toUpperCase());
                location.getWorld().spawnParticle(particle, location, 50, 1, 1, 1, 0.1);
            } catch (Exception ignored) {
            }
        }

        return livingEntity;
    }

    /**
     * Glow rengini ayarlar
     */
    private void setGlowColor(LivingEntity entity, String colorName, String bossId) {
        ChatColor color;
        try {
            color = ChatColor.valueOf(colorName.toUpperCase());
        } catch (Exception e) {
            color = ChatColor.WHITE;
        }

        String teamName = "nmBoss_" + bossId;
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.setColor(color);
        team.addEntry(entity.getUniqueId().toString());
    }

    /**
     * Zaman kontrolü
     */
    private boolean canSpawnAtTime(Boss boss, World world) {
        String spawnTime = boss.getSpawnTime();
        if (spawnTime == null || spawnTime.equalsIgnoreCase("ALWAYS")) {
            return true;
        }

        long time = world.getTime();
        boolean isDay = time >= 0 && time < 12300;
        boolean isNight = time >= 12300 && time < 24000;

        if (spawnTime.equalsIgnoreCase("DAY")) {
            return isDay;
        } else if (spawnTime.equalsIgnoreCase("NIGHT")) {
            return isNight;
        }

        return true;
    }

    /**
     * Belirli bir boss ID'sine ait tüm aktif boss'ları kaldırır
     */
    public void despawnBoss(String id) {
        Iterator<Map.Entry<UUID, ActiveBoss>> iterator = activeBosses.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ActiveBoss> entry = iterator.next();
            if (entry.getValue().getBoss().getId().equals(id)) {
                entry.getValue().remove();
                if (entry.getValue().getEntity() != null && !entry.getValue().getEntity().isDead()) {
                    entry.getValue().getEntity().remove();
                }
                iterator.remove();
            }
        }
    }

    /**
     * Tüm aktif boss'ları kaldırır
     */
    public void removeAllActiveBosses() {
        for (ActiveBoss activeBoss : activeBosses.values()) {
            activeBoss.remove();
            if (activeBoss.getEntity() != null && !activeBoss.getEntity().isDead()) {
                activeBoss.getEntity().remove();
            }
        }
        activeBosses.clear();
    }

    public ActiveBoss getActiveBoss(UUID entityUUID) {
        return activeBosses.get(entityUUID);
    }

    public Map<UUID, ActiveBoss> getActiveBosses() {
        return activeBosses;
    }

    public void removeActiveBoss(UUID entityUUID) {
        ActiveBoss activeBoss = activeBosses.remove(entityUUID);
        if (activeBoss != null) {
            activeBoss.remove();
        }
    }

    public Boss getBoss(String id) {
        return bosses.get(id);
    }

    public Set<String> getBossIds() {
        return bosses.keySet();
    }

    public Collection<Boss> getAllBosses() {
        return bosses.values();
    }
    /**
     * Sunucu kapanırken aktif boss'ları config'e kaydeder
     */
    public void saveActiveBosses() {
        persistenceManager.saveActiveBosses();
    }

    /**
     * Sunucu açılırken mevcut boss entity'lerini yeniden initialize eder
     */
    public void reinitializeExistingBosses() {
        persistenceManager.reinitializeExistingBosses();
    }

    public int getActiveBossCount(String id) {
        int count = 0;
        for (ActiveBoss activeBoss : activeBosses.values()) {
            if (activeBoss.getBoss().getId().equals(id)) {
                count++;
            }
        }
        return count;
    }

    private String colorize(String text) {
        if (text == null)
            return "";
        return text.replace("&", "§");
    }

    // Delegation methods to BossConfigHelper
    public boolean setSpawnLocation(String id, Location location) {
        return configHelper.setSpawnLocation(id, location);
    }

    public boolean toggleAutoSpawn(String id) {
        return configHelper.toggleAutoSpawn(id);
    }

    public ItemStack parseItemStack(String str) {
        return configHelper.parseItemStack(str);
    }

    public ItemStack parseItemStackWithAmount(String str) {
        return configHelper.parseItemStackWithAmount(str);
    }

    public File getBossesFolder() {
        return bossesFolder;
    }
}
