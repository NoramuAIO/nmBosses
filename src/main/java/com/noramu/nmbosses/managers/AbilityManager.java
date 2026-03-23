package com.noramu.nmbosses.managers;

import com.noramu.nmbosses.ActiveBoss;
import com.noramu.nmbosses.BossAbility;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Boss yeteneklerini yöneten sınıf
 */
public class AbilityManager {

    private final NmBosses plugin;

    public AbilityManager(NmBosses plugin) {
        this.plugin = plugin;
    }

    /**
     * Yeteneği çalıştırır
     */
    public void executeAbility(ActiveBoss activeBoss, BossAbility ability, Player target) {
        LivingEntity boss = activeBoss.getEntity();

        switch (ability.getType().toUpperCase()) {
            case "FIREBALL":
                executeFireball(boss, target, ability);
                break;

            case "LIGHTNING":
                executeLightning(target, ability);
                break;

            case "TELEPORT":
                executeTeleport(boss, target, ability);
                break;

            case "SUMMON":
                executeSummon(boss, ability, activeBoss.getBoss().getId());
                break;

            case "HEAL":
                executeHeal(activeBoss, ability);
                break;

            case "EXPLOSION":
                executeExplosion(boss, ability);
                break;

            case "POISON":
            case "EFFECT":
                executeEffect(target, ability);
                break;

            case "PULL":
                executePull(boss, target, ability);
                break;

            case "PUSH":
                executePush(boss, target, ability);
                break;

            case "METEOR":
                executeMeteor(target, ability);
                break;

            case "GROUND_SLAM":
                executeGroundSlam(boss, ability);
                break;

            case "CHARGE":
                executeCharge(boss, target, ability);
                break;

            default:
                DebugLogger.log("AbilityManager", "Bilinmeyen yetenek tipi: " + ability.getType());
                break;
        }
    }

    private void executeFireball(LivingEntity boss, Player target, BossAbility ability) {
        Vector direction = target.getLocation().toVector()
                .subtract(boss.getLocation().toVector()).normalize();

        Fireball fireball = boss.getWorld().spawn(
                boss.getEyeLocation().add(direction),
                Fireball.class);
        fireball.setDirection(direction);
        fireball.setYield((float) ability.getDamage() / 4);
        fireball.setShooter(boss);
    }

    private void executeLightning(Player target, BossAbility ability) {
        target.getWorld().strikeLightning(target.getLocation());
        target.damage(ability.getDamage());
    }

    private void executeTeleport(LivingEntity boss, Player target, BossAbility ability) {
        Location targetLoc = target.getLocation().clone();
        Vector direction = targetLoc.getDirection().multiply(-2);
        Location teleportLoc = targetLoc.add(direction);
        teleportLoc.setY(target.getLocation().getY());

        // Parçacık efekti
        boss.getWorld().spawnParticle(Particle.PORTAL, boss.getLocation(), 50, 0.5, 1, 0.5, 0.1);

        boss.teleport(teleportLoc);

        // Varış parçacığı
        boss.getWorld().spawnParticle(Particle.PORTAL, boss.getLocation(), 50, 0.5, 1, 0.5, 0.1);
    }

    private void executeSummon(LivingEntity boss, BossAbility ability, String bossId) {
        EntityType summonType;
        try {
            summonType = EntityType.valueOf(ability.getSummonType().toUpperCase());
        } catch (Exception e) {
            summonType = EntityType.ZOMBIE;
        }

        for (int i = 0; i < ability.getSummonCount(); i++) {
            double angle = (2 * Math.PI / ability.getSummonCount()) * i;
            double x = boss.getLocation().getX() + Math.cos(angle) * 3;
            double z = boss.getLocation().getZ() + Math.sin(angle) * 3;

            Location spawnLoc = new Location(
                    boss.getWorld(),
                    x,
                    boss.getLocation().getY(),
                    z);

            Entity summoned = boss.getWorld().spawnEntity(spawnLoc, summonType);

            // Çağırılan mob'u işaretle - boss'a saldırmasını engelle
            summoned.addScoreboardTag("nmBossSummon");
            summoned.addScoreboardTag("nmBossSummon:" + bossId);
            summoned.addScoreboardTag("nmBossOwner:" + boss.getUniqueId().toString());

            // Eğer Mob ise, boss'u hedef olarak almayı engelle
            if (summoned instanceof Mob) {
                Mob mob = (Mob) summoned;
                // Boss'u hedef almamak için null yap ve sonra oyuncuyu hedef al
                mob.setTarget(null);

                // En yakın oyuncuyu hedef al
                Player nearestPlayer = null;
                double nearestDistance = Double.MAX_VALUE;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().equals(mob.getWorld())) {
                        double distance = player.getLocation().distance(mob.getLocation());
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestPlayer = player;
                        }
                    }
                }
                if (nearestPlayer != null) {
                    mob.setTarget(nearestPlayer);
                }
            }

            // Parçacık efekti
            boss.getWorld().spawnParticle(Particle.SMOKE_LARGE, spawnLoc, 10, 0.3, 0.5, 0.3, 0.05);
        }
    }

    private void executeHeal(ActiveBoss activeBoss, BossAbility ability) {
        LivingEntity boss = activeBoss.getEntity();
        double newHealth = Math.min(
                boss.getHealth() + ability.getDamage(),
                activeBoss.getBoss().getHealth());
        boss.setHealth(newHealth);

        // İyileşme parçacıkları
        boss.getWorld().spawnParticle(Particle.HEART, boss.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
    }

    private void executeExplosion(LivingEntity boss, BossAbility ability) {
        boss.getWorld().createExplosion(
                boss.getLocation(),
                (float) ability.getDamage() / 5,
                false,
                false,
                boss);
    }

    private void executeEffect(Player target, BossAbility ability) {
        PotionEffectType effectType = PotionEffectType.getByName(ability.getEffectType().toUpperCase());

        if (effectType == null) {
            effectType = PotionEffectType.POISON;
        }

        target.addPotionEffect(new PotionEffect(
                effectType,
                ability.getEffectDuration(),
                ability.getEffectAmplifier(),
                false,
                true));
    }

    private void executePull(LivingEntity boss, Player target, BossAbility ability) {
        Vector direction = boss.getLocation().toVector()
                .subtract(target.getLocation().toVector()).normalize();
        direction.multiply(ability.getDamage() / 5);
        direction.setY(0.5);
        target.setVelocity(direction);

        // Parçacık çizgisi
        drawParticleLine(target.getLocation(), boss.getLocation(), Particle.VILLAGER_HAPPY);
    }

    private void executePush(LivingEntity boss, Player target, BossAbility ability) {
        Vector direction = target.getLocation().toVector()
                .subtract(boss.getLocation().toVector()).normalize();
        direction.multiply(ability.getDamage() / 5);
        direction.setY(0.8);
        target.setVelocity(direction);
        target.damage(ability.getDamage() / 2);
    }

    private void executeMeteor(Player target, BossAbility ability) {
        Location meteorLoc = target.getLocation().add(0, 15, 0);
        final Location targetLocation = target.getLocation().clone();
        final World world = target.getWorld();

        // Parçacık yağmuru
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int ticks = 0;
            Location currentLoc = meteorLoc.clone();

            @Override
            public void run() {
                if (ticks >= 30) {
                    world.createExplosion(targetLocation, (float) ability.getDamage() / 5, false, false);
                    return;
                }

                currentLoc.subtract(0, 0.5, 0);
                world.spawnParticle(Particle.LAVA, currentLoc, 5, 0.5, 0.5, 0.5, 0.1);
                world.spawnParticle(Particle.FLAME, currentLoc, 10, 0.3, 0.3, 0.3, 0.05);

                ticks++;
            }
        }, 0L, 1L);
    }

    private void executeGroundSlam(LivingEntity boss, BossAbility ability) {
        Location center = boss.getLocation().clone();

        // Dalga efekti
        for (int ring = 1; ring <= (int) ability.getRange(); ring++) {
            final int currentRing = ring;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (int angle = 0; angle < 360; angle += 15) {
                    double rad = Math.toRadians(angle);
                    double x = center.getX() + currentRing * Math.cos(rad);
                    double z = center.getZ() + currentRing * Math.sin(rad);
                    Location particleLoc = new Location(center.getWorld(), x, center.getY(), z);
                    center.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, particleLoc, 3, 0.1, 0.1, 0.1, 0.05);
                }
            }, ring * 2L);
        }

        // Yakındaki oyunculara hasar
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(boss.getWorld())) {
                double distance = player.getLocation().distance(center);
                if (distance <= ability.getRange()) {
                    player.damage(ability.getDamage() * (1 - distance / ability.getRange()));
                    player.setVelocity(new Vector(0, 0.8, 0));
                }
            }
        }
    }

    private void executeCharge(LivingEntity boss, Player target, BossAbility ability) {
        Vector direction = target.getLocation().toVector()
                .subtract(boss.getLocation().toVector()).normalize();
        direction.multiply(2);

        // Hızlı hareket
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 10 || boss.isDead()) {
                    return;
                }

                boss.setVelocity(direction);
                boss.getWorld().spawnParticle(Particle.CLOUD, boss.getLocation(), 5, 0.2, 0.2, 0.2, 0.05);

                // Çarpışma kontrolü
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().equals(boss.getWorld())) {
                        if (player.getLocation().distance(boss.getLocation()) < 2) {
                            player.damage(ability.getDamage());
                            player.setVelocity(direction.clone().multiply(0.5).setY(0.5));
                        }
                    }
                }

                ticks++;
            }
        }, 0L, 2L);
    }

    private void drawParticleLine(Location start, Location end, Particle particle) {
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);

        for (double d = 0; d < distance; d += 0.5) {
            Location loc = start.clone().add(direction.clone().multiply(d));
            start.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);
        }
    }
}
