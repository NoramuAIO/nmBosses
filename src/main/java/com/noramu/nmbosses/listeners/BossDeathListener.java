package com.noramu.nmbosses.listeners;

import com.noramu.nmbosses.ActiveBoss;
import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class BossDeathListener implements Listener {

    private final NmBosses plugin;

    public BossDeathListener(NmBosses plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBossDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        ActiveBoss activeBoss = plugin.getBossManager().getActiveBoss(entity.getUniqueId());

        if (activeBoss == null) {
            return;
        }

        Boss boss = activeBoss.getBoss();
        Player killer = entity.getKiller();

        // Varsayılan dropları temizle
        event.getDrops().clear();
        event.setDroppedExp(0);

        // Boss droplarını ekle
        if (boss.getDrops() != null && !boss.getDrops().isEmpty()) {
            for (ItemStack drop : boss.getDrops()) {
                if (drop != null && drop.getType() != Material.AIR) {
                    // Drop şansı kontrolü
                    if (Math.random() * 100 <= boss.getDropChance()) {
                        event.getDrops().add(drop.clone());
                    }
                }
            }
        }

        // XP ödülü
        event.setDroppedExp(boss.getXp());

        // Komutları çalıştır
        if (boss.getDropCommands() != null && !boss.getDropCommands().isEmpty()) {
            for (String command : boss.getDropCommands()) {
                String parsedCommand = parseCommand(command, boss, killer, entity.getLocation());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
            }
        }

        // Ölüm mesajı gönder
        if (StringUtils.isNotEmpty(boss.getDeathMessage())) {
            String message = StringUtils.replacePlaceholders(boss.getDeathMessage(),
                    "{boss}", boss.getDisplayName(),
                    "{player}", killer != null ? killer.getName() : "Bilinmeyen",
                    "{world}", entity.getWorld().getName(),
                    "{x}", String.valueOf((int) entity.getLocation().getX()),
                    "{y}", String.valueOf((int) entity.getLocation().getY()),
                    "{z}", String.valueOf((int) entity.getLocation().getZ()));
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(StringUtils.colorize(message));
            }
        }

        // Ölüm sesi çal
        StringUtils.playSound(entity, boss.getDeathSound(), 1.0f, 1.0f);

        // Ölüm parçacıkları
        if (StringUtils.isNotEmpty(boss.getDeathParticle())) {
            try {
                Particle particle = Particle.valueOf(boss.getDeathParticle().toUpperCase());
                entity.getWorld().spawnParticle(particle, entity.getLocation(), 100, 2, 2, 2, 0.1);
            } catch (IllegalArgumentException ignored) {
            }
        }

        // ActiveBoss'u kaldır
        plugin.getBossManager().removeActiveBoss(entity.getUniqueId());

        // SpawnManager'a bildir
        plugin.getSpawnManager().onBossDeath(boss.getId());
    }

    private String parseCommand(String command, Boss boss, Player killer, Location location) {
        return StringUtils.replacePlaceholders(command,
                "{player}", killer != null ? killer.getName() : "",
                "%player%", killer != null ? killer.getName() : "",
                "{boss}", StringUtils.stripColor(boss.getDisplayName()),
                "%boss%", StringUtils.stripColor(boss.getDisplayName()),
                "{world}", location.getWorld().getName(),
                "{x}", String.valueOf((int) location.getX()),
                "{y}", String.valueOf((int) location.getY()),
                "{z}", String.valueOf((int) location.getZ()));
    }
}
