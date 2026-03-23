package com.noramu.nmbosses.managers;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnManager {

    private final NmBosses plugin;
    private final Map<String, BukkitTask> autoSpawnTasks = new HashMap<>();
    private final Map<String, Long> lastDeathTime = new HashMap<>();
    private File spawnFile;
    private FileConfiguration spawnConfig;

    public SpawnManager(NmBosses plugin) {
        this.plugin = plugin;
        this.spawnFile = new File(plugin.getDataFolder(), "spawns.yml");
        loadSpawnLocations();
    }

    /**
     * Spawn lokasyonlarını yükler
     */
    public void loadSpawnLocations() {
        if (!spawnFile.exists()) {
            try {
                spawnFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
    }

    /**
     * Otomatik spawn görevini başlatır
     */
    public void startAutoSpawnTask() {
        // Her boss için kontrol
        for (Boss boss : plugin.getBossManager().getAllBosses()) {
            if (boss.isAutoSpawn()) {
                startAutoSpawnForBoss(boss.getId());
            }
        }
    }

    /**
     * Belirli bir boss için otomatik spawn başlatır
     */
    public void startAutoSpawnForBoss(String bossId) {
        // Önceki görevi iptal et
        if (autoSpawnTasks.containsKey(bossId)) {
            autoSpawnTasks.get(bossId).cancel();
        }

        Boss boss = plugin.getBossManager().getBoss(bossId);
        if (boss == null || !boss.isAutoSpawn()) {
            return;
        }

        // Her 5 saniyede bir kontrol et (100 tick)
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Boss currentBoss = plugin.getBossManager().getBoss(bossId);
            if (currentBoss == null || !currentBoss.isAutoSpawn()) {
                stopAutoSpawnForBoss(bossId);
                return;
            }

            // Aktif boss sayısını kontrol et
            int activeCount = plugin.getBossManager().getActiveBossCount(bossId);
            if (activeCount >= currentBoss.getMaxSpawnCount()) {
                DebugLogger.log("SpawnManager", "Cannot auto-spawn " + bossId + ": max count reached (" + activeCount + "/" + currentBoss.getMaxSpawnCount() + ")");
                return;
            }

            // Son ölüm zamanını kontrol et (respawn delay)
            Long lastDeath = lastDeathTime.get(bossId);
            if (lastDeath != null) {
                long timeSinceDeath = (System.currentTimeMillis() - lastDeath) / 1000;
                if (timeSinceDeath < currentBoss.getRespawnDelay()) {
                    DebugLogger.log("SpawnManager", "Cannot auto-spawn " + bossId + ": respawn delay not met (" + timeSinceDeath + "/" + currentBoss.getRespawnDelay() + "s)");
                    return;
                }
            }

            // Spawn lokasyonunu kontrol et
            Location spawnLoc = currentBoss.getSpawnLocation();
            if (spawnLoc == null || spawnLoc.getWorld() == null) {
                DebugLogger.log("SpawnManager", "Cannot auto-spawn " + bossId + ": no spawn location");
                return;
            }

            // Gerçek zaman kontrolü
            if (!canSpawnAtRealTime(currentBoss)) {
                DebugLogger.log("SpawnManager", "Cannot auto-spawn " + bossId + ": not in spawn time window");
                return;
            }

            // Boss'u spawn et
            DebugLogger.log("SpawnManager", "Auto-spawning " + bossId);
            plugin.getBossManager().spawnBoss(bossId, spawnLoc);

        }, 100L, 100L); // Her 5 saniyede kontrol et

        autoSpawnTasks.put(bossId, task);
        DebugLogger.log("SpawnManager", "Auto-spawn task started for: " + bossId);
    }

    /**
     * Belirli bir boss için otomatik spawn'u durdurur
     */
    public void stopAutoSpawnForBoss(String bossId) {
        BukkitTask task = autoSpawnTasks.remove(bossId);
        if (task != null) {
            task.cancel();
            DebugLogger.log("SpawnManager", "Otomatik spawn durduruldu: " + bossId);
        }
    }

    /**
     * Tüm otomatik spawn görevlerini durdurur
     */
    public void stopAllTasks() {
        for (BukkitTask task : autoSpawnTasks.values()) {
            task.cancel();
        }
        autoSpawnTasks.clear();
    }

    /**
     * Boss öldüğünde çağrılır
     */
    public void onBossDeath(String bossId) {
        lastDeathTime.put(bossId, System.currentTimeMillis());
        DebugLogger.log("SpawnManager", "Boss died: " + bossId + ", respawn delay timer started");
    }

    /**
     * Spawn config'i kaydeder
     */
    public void saveSpawnConfig() {
        try {
            spawnConfig.save(spawnFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gerçek dünya zamanına göre spawn kontrolü yapar
     */
    public boolean canSpawnAtRealTime(Boss boss) {
        LocalDateTime now = LocalDateTime.now();
        
        // Gün kontrolü
        List<String> spawnDays = boss.getSpawnDays();
        if (spawnDays != null && !spawnDays.isEmpty()) {
            DayOfWeek today = now.getDayOfWeek();
            boolean dayMatch = false;
            for (String day : spawnDays) {
                try {
                    DayOfWeek targetDay = DayOfWeek.valueOf(day.toUpperCase());
                    if (today == targetDay) {
                        dayMatch = true;
                        break;
                    }
                } catch (Exception ignored) {}
            }
            if (!dayMatch) {
                return false;
            }
        }
        
        // Saat kontrolü
        String startHour = boss.getSpawnHourStart();
        String endHour = boss.getSpawnHourEnd();
        
        if (startHour != null && endHour != null && !startHour.equals("00:00") || !endHour.equals("23:59")) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime start = LocalTime.parse(startHour, formatter);
                LocalTime end = LocalTime.parse(endHour, formatter);
                LocalTime currentTime = now.toLocalTime();
                
                // Gece yarısını geçen aralık kontrolü (örn: 22:00 - 06:00)
                if (start.isAfter(end)) {
                    if (currentTime.isBefore(start) && currentTime.isAfter(end)) {
                        return false;
                    }
                } else {
                    if (currentTime.isBefore(start) || currentTime.isAfter(end)) {
                        return false;
                    }
                }
            } catch (Exception e) {
                // Parse hatası varsa spawn'a izin ver
            }
        }
        
        return true;
    }

    /**
     * Gün adını Türkçe'ye çevirir
     */
    public static String getDayNameTurkish(String day) {
        switch (day.toUpperCase()) {
            case "MONDAY": return "Pazartesi";
            case "TUESDAY": return "Salı";
            case "WEDNESDAY": return "Çarşamba";
            case "THURSDAY": return "Perşembe";
            case "FRIDAY": return "Cuma";
            case "SATURDAY": return "Cumartesi";
            case "SUNDAY": return "Pazar";
            default: return day;
        }
    }
}
