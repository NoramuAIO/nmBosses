package com.noramu.nmbosses;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Boss veri sınıfı - bir boss'un tüm özelliklerini içerir
 */
public class Boss {

    private final String id;
    private final String displayName;
    private final String bossType; // MELEE, RANGED, TANK, MAGE, HYBRID
    private final String entityType;

    // Temel özellikler
    private final double health;
    private final double damage;
    private final double speed;
    private final double knockbackResistance;
    private final double attackSpeed;
    private final double followRange; // Oyuncuyu görme mesafesi
    private final double attackRange; // Saldırı menzili
    private final double knockbackPower; // Vurunca ne kadar uzağa atar

    // Görünüm
    private final boolean glowing;
    private final String glowColor; // Glow rengi (RED, BLUE, vb.)
    private final boolean nameVisible;
    private final boolean showHealth;
    private final boolean showBossBar;
    private final String bossBarColor;
    private final String bossBarStyle;
    private final double bossBarRadius; // BossBar görünürlük mesafesi

    // Ekipman
    private final Map<String, ItemStack> armor;
    private final ItemStack mainHand;
    private final ItemStack offHand;
    private final boolean hasShield;

    // Ödüller
    private final List<String> dropCommands;
    private final List<ItemStack> drops;
    private final int xp;
    private final double dropChance;

    // Mesajlar
    private final String spawnMessage;
    private final String deathMessage;
    private final List<String> periodicMessages;
    private final int periodicMessageInterval;

    // Spawn ayarları
    private final Location spawnLocation;
    private final long respawnDelay;
    private final boolean autoSpawn;
    private final int maxSpawnCount;
    private final String spawnTime; // DAY, NIGHT, ALWAYS (Minecraft zamanı)
    
    // Gerçek dünya zaman ayarları
    private final List<String> spawnDays; // MONDAY, TUESDAY, vb. veya boş = her gün
    private final String spawnHourStart; // "09:00" formatında
    private final String spawnHourEnd; // "21:00" formatında

    // Ses efektleri
    private final String spawnSound;
    private final String deathSound;
    private final String hitSound;
    private final String attackSound;

    // Parçacık efektleri
    private final String spawnParticle;
    private final String deathParticle;
    private final String attackParticle;

    // Özel Yetenekler
    private final boolean abilitiesEnabled;
    private final List<BossAbility> abilities;

    // Faz Sistemi
    private final boolean phaseSystemEnabled;
    private final List<BossPhase> phases;

    public Boss(String id, String displayName, String bossType, String entityType,
            double health, double damage, double speed, double knockbackResistance,
            double attackSpeed, double followRange, double attackRange, double knockbackPower,
            boolean glowing, String glowColor, boolean nameVisible, boolean showHealth, boolean showBossBar,
            String bossBarColor, String bossBarStyle, double bossBarRadius,
            Map<String, ItemStack> armor, ItemStack mainHand, ItemStack offHand, boolean hasShield,
            List<String> dropCommands, List<ItemStack> drops, int xp, double dropChance,
            String spawnMessage, String deathMessage, List<String> periodicMessages, int periodicMessageInterval,
            Location spawnLocation, long respawnDelay, boolean autoSpawn, int maxSpawnCount, String spawnTime,
            List<String> spawnDays, String spawnHourStart, String spawnHourEnd,
            String spawnSound, String deathSound, String hitSound, String attackSound,
            String spawnParticle, String deathParticle, String attackParticle,
            boolean abilitiesEnabled, List<BossAbility> abilities,
            boolean phaseSystemEnabled, List<BossPhase> phases) {
        this.id = id;
        this.displayName = displayName;
        this.bossType = bossType;
        this.entityType = entityType;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.knockbackResistance = knockbackResistance;
        this.attackSpeed = attackSpeed;
        this.followRange = followRange;
        this.attackRange = attackRange;
        this.knockbackPower = knockbackPower;
        this.glowing = glowing;
        this.glowColor = glowColor;
        this.nameVisible = nameVisible;
        this.showHealth = showHealth;
        this.showBossBar = showBossBar;
        this.bossBarColor = bossBarColor;
        this.bossBarStyle = bossBarStyle;
        this.bossBarRadius = bossBarRadius;
        this.armor = armor;
        this.mainHand = mainHand;
        this.offHand = offHand;
        this.hasShield = hasShield;
        this.dropCommands = dropCommands;
        this.drops = drops;
        this.xp = xp;
        this.dropChance = dropChance;
        this.spawnMessage = spawnMessage;
        this.deathMessage = deathMessage;
        this.periodicMessages = periodicMessages;
        this.periodicMessageInterval = periodicMessageInterval;
        this.spawnLocation = spawnLocation;
        this.respawnDelay = respawnDelay;
        this.autoSpawn = autoSpawn;
        this.maxSpawnCount = maxSpawnCount;
        this.spawnTime = spawnTime;
        this.spawnDays = spawnDays;
        this.spawnHourStart = spawnHourStart;
        this.spawnHourEnd = spawnHourEnd;
        this.spawnSound = spawnSound;
        this.deathSound = deathSound;
        this.hitSound = hitSound;
        this.attackSound = attackSound;
        this.spawnParticle = spawnParticle;
        this.deathParticle = deathParticle;
        this.attackParticle = attackParticle;
        this.abilitiesEnabled = abilitiesEnabled;
        this.abilities = abilities;
        this.phaseSystemEnabled = phaseSystemEnabled;
        this.phases = phases;
    }

    // Getter metodları
    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBossType() {
        return bossType;
    }

    public String getEntityType() {
        return entityType;
    }

    public double getHealth() {
        return health;
    }

    public double getDamage() {
        return damage;
    }

    public double getSpeed() {
        return speed;
    }

    public double getKnockbackResistance() {
        return knockbackResistance;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getFollowRange() {
        return followRange;
    }

    public double getAttackRange() {
        return attackRange;
    }

    public double getKnockbackPower() {
        return knockbackPower;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public String getGlowColor() {
        return glowColor;
    }

    public boolean isNameVisible() {
        return nameVisible;
    }

    public boolean isShowHealth() {
        return showHealth;
    }

    public boolean isShowBossBar() {
        return showBossBar;
    }

    public String getBossBarColor() {
        return bossBarColor;
    }

    public String getBossBarStyle() {
        return bossBarStyle;
    }

    public double getBossBarRadius() {
        return bossBarRadius;
    }

    public Map<String, ItemStack> getArmor() {
        return armor;
    }

    public ItemStack getMainHand() {
        return mainHand;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public List<String> getDropCommands() {
        return dropCommands;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public int getXp() {
        return xp;
    }

    public double getDropChance() {
        return dropChance;
    }

    public String getSpawnMessage() {
        return spawnMessage;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public List<String> getPeriodicMessages() {
        return periodicMessages;
    }

    public int getPeriodicMessageInterval() {
        return periodicMessageInterval;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public long getRespawnDelay() {
        return respawnDelay;
    }

    public boolean isAutoSpawn() {
        return autoSpawn;
    }

    public int getMaxSpawnCount() {
        return maxSpawnCount;
    }

    public String getSpawnTime() {
        return spawnTime;
    }

    public List<String> getSpawnDays() {
        return spawnDays;
    }

    public String getSpawnHourStart() {
        return spawnHourStart;
    }

    public String getSpawnHourEnd() {
        return spawnHourEnd;
    }

    public String getSpawnSound() {
        return spawnSound;
    }

    public String getDeathSound() {
        return deathSound;
    }

    public String getHitSound() {
        return hitSound;
    }

    public String getAttackSound() {
        return attackSound;
    }

    public String getSpawnParticle() {
        return spawnParticle;
    }

    public String getDeathParticle() {
        return deathParticle;
    }

    public String getAttackParticle() {
        return attackParticle;
    }

    public boolean isAbilitiesEnabled() {
        return abilitiesEnabled;
    }

    public List<BossAbility> getAbilities() {
        return abilities;
    }

    public boolean isPhaseSystemEnabled() {
        return phaseSystemEnabled;
    }

    public List<BossPhase> getPhases() {
        return phases;
    }

    /**
     * Belirli bir can yüzdesinde hangi fazda olduğunu hesaplar
     */
    public BossPhase getCurrentPhase(double currentHealth) {
        if (!phaseSystemEnabled || phases == null || phases.isEmpty()) {
            return null;
        }

        double healthPercent = (currentHealth / health) * 100;
        BossPhase currentPhase = null;

        for (BossPhase phase : phases) {
            if (healthPercent <= phase.getHealthThreshold()) {
                if (currentPhase == null || phase.getHealthThreshold() > currentPhase.getHealthThreshold()) {
                    currentPhase = phase;
                }
            }
        }

        return currentPhase;
    }
}
