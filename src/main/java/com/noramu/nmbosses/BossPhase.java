package com.noramu.nmbosses;

/**
 * Boss faz sistemi - belirli can yüzdelerinde güçlenme
 */
public class BossPhase {

    private final int phaseNumber;
    private final String name;
    private final double healthThreshold; // Bu yüzdenin altına düşünce aktif olur (0-100)

    // Güçlenme değerleri (çarpan olarak)
    private final double damageMultiplier;
    private final double speedMultiplier;
    private final double attackSpeedMultiplier;

    // Görünüm değişiklikleri
    private final boolean glowing;
    private final String bossBarColor;
    private final String nameColor; // Renk kodu (&c gibi)

    // Faz başladığında
    private final String phaseStartMessage;
    private final String phaseStartSound;
    private final String phaseStartParticle;

    // Özel efektler
    private final boolean regeneration; // Can yenileme aktif mi
    private final double regenerationAmount; // Saniyede yenilenecek can
    private final boolean invulnerableOnPhaseChange; // Faz değişiminde kısa süre hasar almaz mı
    private final int invulnerableDuration; // Tick cinsinden

    public BossPhase(int phaseNumber, String name, double healthThreshold,
            double damageMultiplier, double speedMultiplier, double attackSpeedMultiplier,
            boolean glowing, String bossBarColor, String nameColor,
            String phaseStartMessage, String phaseStartSound, String phaseStartParticle,
            boolean regeneration, double regenerationAmount,
            boolean invulnerableOnPhaseChange, int invulnerableDuration) {
        this.phaseNumber = phaseNumber;
        this.name = name;
        this.healthThreshold = healthThreshold;
        this.damageMultiplier = damageMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.attackSpeedMultiplier = attackSpeedMultiplier;
        this.glowing = glowing;
        this.bossBarColor = bossBarColor;
        this.nameColor = nameColor;
        this.phaseStartMessage = phaseStartMessage;
        this.phaseStartSound = phaseStartSound;
        this.phaseStartParticle = phaseStartParticle;
        this.regeneration = regeneration;
        this.regenerationAmount = regenerationAmount;
        this.invulnerableOnPhaseChange = invulnerableOnPhaseChange;
        this.invulnerableDuration = invulnerableDuration;
    }

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public String getName() {
        return name;
    }

    public double getHealthThreshold() {
        return healthThreshold;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getAttackSpeedMultiplier() {
        return attackSpeedMultiplier;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public String getBossBarColor() {
        return bossBarColor;
    }

    public String getNameColor() {
        return nameColor;
    }

    public String getPhaseStartMessage() {
        return phaseStartMessage;
    }

    public String getPhaseStartSound() {
        return phaseStartSound;
    }

    public String getPhaseStartParticle() {
        return phaseStartParticle;
    }

    public boolean isRegeneration() {
        return regeneration;
    }

    public double getRegenerationAmount() {
        return regenerationAmount;
    }

    public boolean isInvulnerableOnPhaseChange() {
        return invulnerableOnPhaseChange;
    }

    public int getInvulnerableDuration() {
        return invulnerableDuration;
    }
}
