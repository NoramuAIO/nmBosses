package com.noramu.nmbosses;

/**
 * Boss özel yeteneği
 */
public class BossAbility {

    private final String id;
    private final String name;
    private final String type; // FIREBALL, LIGHTNING, TELEPORT, SUMMON, HEAL, EXPLOSION, POISON, EFFECT, PULL,
                               // PUSH
    private final double cooldown; // Saniye cinsinden
    private final double chance; // 0-100 arası yüzde
    private final double damage;
    private final double range;
    private final String message; // Yetenek kullanıldığında mesaj
    private final String sound;
    private final String particle;

    // Özel parametreler
    private final int effectDuration; // Efekt süresi (tick)
    private final int effectAmplifier; // Efekt seviyesi
    private final String effectType; // POISON, WITHER, SLOWNESS, vb.
    private final String summonType; // Çağırılacak mob tipi
    private final int summonCount; // Çağırılacak mob sayısı

    public BossAbility(String id, String name, String type, double cooldown, double chance,
            double damage, double range, String message, String sound, String particle,
            int effectDuration, int effectAmplifier, String effectType,
            String summonType, int summonCount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.cooldown = cooldown;
        this.chance = chance;
        this.damage = damage;
        this.range = range;
        this.message = message;
        this.sound = sound;
        this.particle = particle;
        this.effectDuration = effectDuration;
        this.effectAmplifier = effectAmplifier;
        this.effectType = effectType;
        this.summonType = summonType;
        this.summonCount = summonCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getCooldown() {
        return cooldown;
    }

    public double getChance() {
        return chance;
    }

    public double getDamage() {
        return damage;
    }

    public double getRange() {
        return range;
    }

    public String getMessage() {
        return message;
    }

    public String getSound() {
        return sound;
    }

    public String getParticle() {
        return particle;
    }

    public int getEffectDuration() {
        return effectDuration;
    }

    public int getEffectAmplifier() {
        return effectAmplifier;
    }

    public String getEffectType() {
        return effectType;
    }

    public String getSummonType() {
        return summonType;
    }

    public int getSummonCount() {
        return summonCount;
    }
}
