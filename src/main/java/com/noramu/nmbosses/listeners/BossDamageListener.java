package com.noramu.nmbosses.listeners;

import com.noramu.nmbosses.ActiveBoss;
import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.util.Vector;

public class BossDamageListener implements Listener {

    private final NmBosses plugin;

    public BossDamageListener(NmBosses plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBossDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();
        ActiveBoss activeBoss = plugin.getBossManager().getActiveBoss(entity.getUniqueId());

        if (activeBoss == null) {
            return;
        }

        // Dokunulmazlık kontrolü (faz değişimi sırasında)
        if (activeBoss.isInvulnerable()) {
            event.setCancelled(true);
            return;
        }

        Boss boss = activeBoss.getBoss();

        // Hit sesi çal
        StringUtils.playSound(entity, boss.getHitSound(), 0.5f, 1.0f);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBossAttack(EntityDamageByEntityEvent event) {
        // Boss saldırıyor mu kontrol et
        if (event.getDamager() instanceof LivingEntity) {
            LivingEntity damager = (LivingEntity) event.getDamager();
            ActiveBoss activeBoss = plugin.getBossManager().getActiveBoss(damager.getUniqueId());

            if (activeBoss != null && event.getEntity() instanceof Player) {
                Boss boss = activeBoss.getBoss();
                Player victim = (Player) event.getEntity();

                // Faz çarpanını uygula
                double damage = boss.getDamage();
                if (activeBoss.getCurrentPhase() != null) {
                    damage *= activeBoss.getCurrentPhase().getDamageMultiplier();
                }
                event.setDamage(damage);

                // Knockback gücünü uygula
                double knockbackPower = boss.getKnockbackPower();
                if (knockbackPower > 0) {
                    Vector direction = victim.getLocation().toVector()
                            .subtract(damager.getLocation().toVector()).normalize();
                    direction.multiply(knockbackPower);
                    direction.setY(knockbackPower * 0.5);

                    // Bir tick sonra uygula (vanilla knockback'i geçersiz kılmak için)
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        victim.setVelocity(direction);
                    }, 1L);
                }

                // Saldırı sesi
                StringUtils.playSound(damager, boss.getAttackSound(), 0.7f, 1.0f);

                // Saldırı parçacıkları
                if (StringUtils.isNotEmpty(boss.getAttackParticle())) {
                    try {
                        Particle particle = Particle.valueOf(boss.getAttackParticle().toUpperCase());
                        victim.getWorld().spawnParticle(particle,
                                victim.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.05);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        // Summoned mob'ların boss'a saldırmasını engelle
        if (isSummonedMob(event.getDamager())) {
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity victim = (LivingEntity) event.getEntity();
                ActiveBoss victimBoss = plugin.getBossManager().getActiveBoss(victim.getUniqueId());

                if (victimBoss != null) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Boss'un kendi summoned mob'larına saldırmasını engelle
        if (event.getDamager() instanceof LivingEntity) {
            LivingEntity damager = (LivingEntity) event.getDamager();
            ActiveBoss attackerBoss = plugin.getBossManager().getActiveBoss(damager.getUniqueId());

            if (attackerBoss != null && isSummonedMob(event.getEntity())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Summoned mob'ların boss'u hedef almasını engelle
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();
        LivingEntity target = event.getTarget();

        if (target == null) return;

        // Summoned mob boss'u hedef alıyor mu?
        if (isSummonedMob(entity)) {
            ActiveBoss targetBoss = plugin.getBossManager().getActiveBoss(target.getUniqueId());
            if (targetBoss != null) {
                event.setCancelled(true);
                return;
            }
        }

        // Boss summoned mob'u hedef alıyor mu?
        if (entity instanceof LivingEntity) {
            ActiveBoss entityBoss = plugin.getBossManager().getActiveBoss(entity.getUniqueId());
            if (entityBoss != null && isSummonedMob(target)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Entity'nin summoned mob olup olmadığını kontrol et
     */
    private boolean isSummonedMob(Entity entity) {
        return entity.getScoreboardTags().stream().anyMatch(tag -> tag.startsWith("nmBossSummon"));
    }
}