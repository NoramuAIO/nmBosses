package com.noramu.nmbosses;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Aktif bir boss entity'sini temsil eder
 */
public class ActiveBoss {

    private final NmBosses plugin;
    private Boss boss;
    private final LivingEntity entity;
    private final UUID entityUUID;
    private BossBar bossBar;
    private BukkitTask messageTask;
    private BukkitTask healthUpdateTask;
    private BukkitTask abilityTask;
    private BukkitTask regenerationTask;
    private int currentMessageIndex = 0;
    private BossPhase currentPhase = null;
    private Map<String, Long> abilityCooldowns = new HashMap<>();
    private boolean invulnerable = false;

    public ActiveBoss(NmBosses plugin, Boss boss, LivingEntity entity) {
        this.plugin = plugin;
        this.boss = boss;
        this.entity = entity;
        this.entityUUID = entity.getUniqueId();

        // İsim görünürlüğü ve ilk isim ataması
        entity.setCustomNameVisible(boss.isNameVisible());
        updateCustomName();

        // BossBar oluştur
        if (boss.isShowBossBar()) {
            createBossBar();
        }

        // Periyodik mesaj görevini başlat
        if (boss.getPeriodicMessages() != null && !boss.getPeriodicMessages().isEmpty()) {
            startPeriodicMessages();
        }

        // Sağlık güncelleme görevini başlat (BossBar, Faz sistemi veya İsim
        // güncellemesi için)
        if (boss.isShowBossBar() || boss.isPhaseSystemEnabled() || boss.isShowHealth() || boss.isNameVisible()) {
            startHealthUpdate();
        }

        // Yetenek görevini başlat
        if (boss.isAbilitiesEnabled() && boss.getAbilities() != null && !boss.getAbilities().isEmpty()) {
            startAbilityTask();
        }
    }

    private void createBossBar() {
        BarColor color;
        try {
            color = BarColor.valueOf(boss.getBossBarColor().toUpperCase());
        } catch (Exception e) {
            color = BarColor.RED;
        }

        BarStyle style;
        try {
            style = BarStyle.valueOf(boss.getBossBarStyle().toUpperCase());
        } catch (Exception e) {
            style = BarStyle.SOLID;
        }

        String title = colorize(boss.getDisplayName());
        if (boss.isShowHealth()) {
            title += " §7- §c" + (int) entity.getHealth() + "§7/§c" + (int) boss.getHealth() + " ❤";
        }

        this.bossBar = Bukkit.createBossBar(title, color, style);
        this.bossBar.setProgress(1.0);
    }

    private void startHealthUpdate() {
        this.healthUpdateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (entity == null || entity.isDead()) {
                remove();
                return;
            }

            // Faz kontrolü
            if (boss.isPhaseSystemEnabled()) {
                checkPhaseTransition();
            }

            if (bossBar != null) {
                // BossBar güncelle
                double progress = entity.getHealth() / boss.getHealth();
                progress = Math.max(0.0, Math.min(1.0, progress));
                bossBar.setProgress(progress);

                String title = colorize(boss.getDisplayName());

                // Faz ismi ekle
                if (currentPhase != null && currentPhase.getNameColor() != null) {
                    title = colorize(currentPhase.getNameColor() + stripColor(boss.getDisplayName()));
                }

                if (boss.isShowHealth()) {
                    title += " §7- §c" + (int) entity.getHealth() + "§7/§c" + (int) boss.getHealth() + " ❤";
                }

                // Faz ismini ekle
                if (currentPhase != null) {
                    title += " §7[§e" + currentPhase.getName() + "§7]";
                }

                bossBar.setTitle(title);

                // Faz bossbar rengini güncelle
                if (currentPhase != null && currentPhase.getBossBarColor() != null) {
                    try {
                        bossBar.setColor(BarColor.valueOf(currentPhase.getBossBarColor().toUpperCase()));
                    } catch (Exception ignored) {
                    }
                }

                // Mesafe kontrolü
                double radius = boss.getBossBarRadius();
                if (radius > 0) {
                    bossBar.removeAll(); // Herkesi çıkar
                    for (Player player : entity.getWorld().getPlayers()) {
                        if (player.getLocation().distance(entity.getLocation()) <= radius) {
                            bossBar.addPlayer(player);
                        }
                    }
                } else {
                    // Radius yoksa veya 0 ise dünyadaki herkesi ekle (ama sürekli eklemek yerine
                    // kontrol et)
                    // Performans için sadece eksikleri eklemek daha iyi olur
                    for (Player player : entity.getWorld().getPlayers()) {
                        if (!bossBar.getPlayers().contains(player)) {
                            bossBar.addPlayer(player);
                        }
                    }
                }
            }

            // Entity ismini güncelle (Can değiştiğinde)
            if (boss.isNameVisible()) {
                updateCustomName();
            }

        }, 0L, 20L); // Her saniye güncelle
    }

    private void checkPhaseTransition() {
        if (!boss.isPhaseSystemEnabled() || boss.getPhases() == null)
            return;

        BossPhase newPhase = boss.getCurrentPhase(entity.getHealth());

        if (newPhase != null && (currentPhase == null || newPhase.getPhaseNumber() != currentPhase.getPhaseNumber())) {
            // Yeni faza geçiş
            BossPhase oldPhase = currentPhase;
            currentPhase = newPhase;

            onPhaseChange(oldPhase, newPhase);
        }
    }

    private void onPhaseChange(BossPhase oldPhase, BossPhase newPhase) {
        // Faz başlangıç mesajı
        if (newPhase.getPhaseStartMessage() != null && !newPhase.getPhaseStartMessage().isEmpty()) {
            String message = newPhase.getPhaseStartMessage()
                    .replace("{boss}", boss.getDisplayName())
                    .replace("{phase}", newPhase.getName())
                    .replace("{phase_number}", String.valueOf(newPhase.getPhaseNumber()));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(entity.getWorld()) &&
                        player.getLocation().distance(entity.getLocation()) <= 50) {
                    player.sendMessage(colorize(message));
                }
            }
        }

        // Faz başlangıç sesi
        if (newPhase.getPhaseStartSound() != null && !newPhase.getPhaseStartSound().isEmpty()) {
            try {
                Sound sound = Sound.valueOf(newPhase.getPhaseStartSound().toUpperCase());
                entity.getWorld().playSound(entity.getLocation(), sound, 1.0f, 1.0f);
            } catch (Exception ignored) {
            }
        }

        // Faz başlangıç parçacıkları
        if (newPhase.getPhaseStartParticle() != null && !newPhase.getPhaseStartParticle().isEmpty()) {
            try {
                Particle particle = Particle.valueOf(newPhase.getPhaseStartParticle().toUpperCase());
                entity.getWorld().spawnParticle(particle, entity.getLocation(), 50, 1, 1, 1, 0.1);
            } catch (Exception ignored) {
            }
        }

        // Glow değişikliği
        entity.setGlowing(newPhase.isGlowing());

        // Hız değişikliği
        if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
            double newSpeed = boss.getSpeed() * newPhase.getSpeedMultiplier();
            entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(newSpeed);
        }

        // Hasar değişikliği
        if (entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
            double newDamage = boss.getDamage() * newPhase.getDamageMultiplier();
            entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(newDamage);
        }

        // Kısa süreli dokunulmazlık
        if (newPhase.isInvulnerableOnPhaseChange()) {
            invulnerable = true;
            Bukkit.getScheduler().runTaskLater(NmBosses.getInstance(), () -> {
                invulnerable = false;
            }, newPhase.getInvulnerableDuration());
        }

        // Can yenileme başlat
        if (newPhase.isRegeneration()) {
            startRegeneration(newPhase.getRegenerationAmount());
        } else {
            stopRegeneration();
        }
    }

    private void startRegeneration(double amount) {
        stopRegeneration();

        this.regenerationTask = Bukkit.getScheduler().runTaskTimer(NmBosses.getInstance(), () -> {
            if (entity == null || entity.isDead()) {
                return;
            }

            double newHealth = Math.min(entity.getHealth() + amount, boss.getHealth());
            entity.setHealth(newHealth);
        }, 20L, 20L); // Her saniye
    }

    private void stopRegeneration() {
        if (regenerationTask != null) {
            regenerationTask.cancel();
            regenerationTask = null;
        }
    }

    private void startAbilityTask() {
        this.abilityTask = Bukkit.getScheduler().runTaskTimer(NmBosses.getInstance(), () -> {
            if (entity == null || entity.isDead()) {
                return;
            }

            // En yakın oyuncuyu bul
            Player nearestPlayer = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(entity.getWorld())) {
                    double distance = player.getLocation().distance(entity.getLocation());
                    if (distance <= boss.getFollowRange() && distance < nearestDistance) {
                        nearestDistance = distance;
                        nearestPlayer = player;
                    }
                }
            }

            if (nearestPlayer == null)
                return;

            // Yetenekleri kontrol et
            for (BossAbility ability : boss.getAbilities()) {
                if (canUseAbility(ability) && Math.random() * 100 <= ability.getChance()) {
                    if (nearestDistance <= ability.getRange()) {
                        useAbility(ability, nearestPlayer);
                        break; // Bir seferde bir yetenek
                    }
                }
            }
        }, 20L, 20L); // Her saniye kontrol
    }

    private boolean canUseAbility(BossAbility ability) {
        Long lastUse = abilityCooldowns.get(ability.getId());
        if (lastUse == null)
            return true;

        long cooldownMs = (long) (ability.getCooldown() * 1000);
        return System.currentTimeMillis() - lastUse >= cooldownMs;
    }

    private void useAbility(BossAbility ability, Player target) {
        abilityCooldowns.put(ability.getId(), System.currentTimeMillis());

        // Yetenek mesajı
        if (ability.getMessage() != null && !ability.getMessage().isEmpty()) {
            String message = ability.getMessage()
                    .replace("{boss}", boss.getDisplayName())
                    .replace("{ability}", ability.getName())
                    .replace("{player}", target.getName());

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(entity.getWorld()) &&
                        player.getLocation().distance(entity.getLocation()) <= 50) {
                    player.sendMessage(colorize(message));
                }
            }
        }

        // Yetenek sesi
        if (ability.getSound() != null && !ability.getSound().isEmpty()) {
            try {
                Sound sound = Sound.valueOf(ability.getSound().toUpperCase());
                entity.getWorld().playSound(entity.getLocation(), sound, 1.0f, 1.0f);
            } catch (Exception ignored) {
            }
        }

        // Yetenek parçacıkları
        if (ability.getParticle() != null && !ability.getParticle().isEmpty()) {
            try {
                Particle particle = Particle.valueOf(ability.getParticle().toUpperCase());
                entity.getWorld().spawnParticle(particle, entity.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
            } catch (Exception ignored) {
            }
        }

        // Yetenek tipine göre işlem
        plugin.getAbilityManager().executeAbility(this, ability, target);
    }

    private void startPeriodicMessages() {
        int interval = boss.getPeriodicMessageInterval() * 20;

        this.messageTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (entity == null || entity.isDead()) {
                remove();
                return;
            }

            if (boss.getPeriodicMessages() != null && !boss.getPeriodicMessages().isEmpty()) {
                String message = boss.getPeriodicMessages().get(currentMessageIndex);
                message = message.replace("{boss}", boss.getDisplayName())
                        .replace("{health}", String.valueOf((int) entity.getHealth()))
                        .replace("{maxhealth}", String.valueOf((int) boss.getHealth()));

                if (currentPhase != null) {
                    message = message.replace("{phase}", currentPhase.getName());
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().equals(entity.getWorld())) {
                        if (player.getLocation().distance(entity.getLocation()) <= 50) {
                            player.sendMessage(colorize(message));
                        }
                    }
                }

                currentMessageIndex = (currentMessageIndex + 1) % boss.getPeriodicMessages().size();
            }
        }, interval, interval);
    }

    public void remove() {
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        if (messageTask != null) {
            messageTask.cancel();
            messageTask = null;
        }

        if (healthUpdateTask != null) {
            healthUpdateTask.cancel();
            healthUpdateTask = null;
        }

        if (abilityTask != null) {
            abilityTask.cancel();
            abilityTask = null;
        }

        if (regenerationTask != null) {
            regenerationTask.cancel();
            regenerationTask = null;
        }
    }

    public Boss getBoss() {
        return boss;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public BossPhase getCurrentPhase() {
        return currentPhase;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    /**
     * Boss config'ini günceller (reload için)
     */
    public void updateBossConfig(Boss newBoss) {
        this.boss = newBoss;

        // Entity özelliklerini güncelle
        if (entity != null && !entity.isDead()) {
            entity.setCustomNameVisible(newBoss.isNameVisible());
            updateCustomName();

            entity.setGlowing(newBoss.isGlowing());

            // BossBar durumunu güncelle
            if (newBoss.isShowBossBar()) {
                if (bossBar == null) {
                    createBossBar();
                } else {
                    try {
                        bossBar.setColor(BarColor.valueOf(newBoss.getBossBarColor().toUpperCase()));
                        bossBar.setStyle(BarStyle.valueOf(newBoss.getBossBarStyle().toUpperCase()));
                    } catch (Exception ignored) {
                    }
                }
            } else {
                if (bossBar != null) {
                    bossBar.removeAll();
                    bossBar = null;
                }
            }

            // Task başlat (Eğer önceden yoksa) - Örneğin BossBar kapalıydı şimdi açıldı
            if (healthUpdateTask == null || healthUpdateTask.isCancelled()) {
                if (newBoss.isShowBossBar() || newBoss.isPhaseSystemEnabled() || newBoss.isShowHealth()
                        || newBoss.isNameVisible()) {
                    startHealthUpdate();
                }
            }

            // Attribute'ları güncelle
            if (entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
                entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(newBoss.getDamage());
            }
            if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(newBoss.getSpeed());
            }
            if (entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE) != null) {
                entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)
                        .setBaseValue(newBoss.getKnockbackResistance());
            }
            if (entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE) != null) {
                entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(newBoss.getFollowRange());
            }
        }
    }

    private void updateCustomName() {
        if (!boss.isNameVisible())
            return;

        String name = colorize(boss.getDisplayName());

        // Faz ismi ekle
        if (currentPhase != null && currentPhase.getNameColor() != null) {
            name = colorize(currentPhase.getNameColor() + stripColor(boss.getDisplayName()));
        }

        if (boss.isShowHealth()) {
            name += " §7- §c" + (int) entity.getHealth() + "§7/§c" + (int) boss.getHealth() + " ❤";
        }

        // Faz etiketi
        if (currentPhase != null) {
            name += " §7[§e" + currentPhase.getName() + "§7]";
        }

        entity.setCustomName(name);
    }

    private String colorize(String text) {
        if (text == null)
            return "";
        return text.replace("&", "§");
    }

    private String stripColor(String text) {
        if (text == null)
            return "";
        return text.replaceAll("§[0-9a-fk-or]", "").replaceAll("&[0-9a-fk-or]", "");
    }
}
