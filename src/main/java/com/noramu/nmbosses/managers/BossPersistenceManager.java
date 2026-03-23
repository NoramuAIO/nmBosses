package com.noramu.nmbosses.managers;

import com.noramu.nmbosses.ActiveBoss;
import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * Boss state'ini (konumu, sağlığı) config'e kaydetme ve yükleme
 */
public class BossPersistenceManager {

    private final NmBosses plugin;
    private final File bossesFolder;
    private final Map<UUID, ActiveBoss> activeBosses;
    private final Map<String, Boss> bosses;
    private BossManager bossManager; // Lazy initialization

    public BossPersistenceManager(NmBosses plugin, File bossesFolder, Map<UUID, ActiveBoss> activeBosses, Map<String, Boss> bosses) {
        this.plugin = plugin;
        this.bossesFolder = bossesFolder;
        this.activeBosses = activeBosses;
        this.bosses = bosses;
    }

    public void setBossManager(BossManager bossManager) {
        this.bossManager = bossManager;
    }

    /**
     * Sunucu kapanırken aktif boss'ları config'e kaydeder
     * (entity'lerin konumu ve sağlığı kaydedilir)
     */
    public void saveActiveBosses() {
        DebugLogger.log("BossPersistenceManager", "Starting to save active bosses. Count: " + activeBosses.size());
        
        for (ActiveBoss activeBoss : activeBosses.values()) {
            try {
                String bossId = activeBoss.getBoss().getId();
                File bossFile = new File(bossesFolder, bossId + ".yml");
                if (!bossFile.exists()) {
                    DebugLogger.log("BossPersistenceManager", "Boss file not found: " + bossId);
                    continue;
                }

                FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
                
                // Boss konumunu kaydet
                org.bukkit.Location loc = activeBoss.getEntity().getLocation();
                config.set("spawn.location.world", loc.getWorld().getName());
                config.set("spawn.location.x", loc.getX());
                config.set("spawn.location.y", loc.getY());
                config.set("spawn.location.z", loc.getZ());
                config.set("spawn.location.yaw", loc.getYaw());
                config.set("spawn.location.pitch", loc.getPitch());
                
                // Boss sağlığını kaydet
                config.set("spawn.lastHealth", activeBoss.getEntity().getHealth());
                
                config.save(bossFile);
                DebugLogger.log("BossPersistenceManager", "Saved active boss state: " + bossId + " at " + loc.getWorld().getName() + " (" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ")");
            } catch (Exception e) {
                DebugLogger.log("BossPersistenceManager", "Error saving active boss state", e);
            }
        }
        
        DebugLogger.log("BossPersistenceManager", "Finished saving active bosses");
    }

    /**
     * Sunucu açılırken mevcut boss entity'lerini yeniden initialize eder
     * (persistent entity'ler Minecraft tarafından kaydedilmiş olabilir)
     * 
     * Ayrıca config'den kaydedilen boss'ları spawn eder
     */
    public void reinitializeExistingBosses() {
        DebugLogger.log("BossPersistenceManager", "Starting boss re-initialization...");
        
        // 1. Mevcut persistent entity'leri kontrol et
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof LivingEntity)) continue;
                
                LivingEntity livingEntity = (LivingEntity) entity;
                
                // nmBoss tag'i var mı?
                for (String tag : entity.getScoreboardTags()) {
                    if (tag.startsWith("nmBoss:")) {
                        String bossId = tag.substring(7); // "nmBoss:" sonrası
                        
                        // Bu boss config'de var mı?
                        Boss boss = bosses.get(bossId);
                        if (boss != null) {
                            // Bu boss zaten listede var mı?
                            if (activeBosses.containsKey(entity.getUniqueId())) {
                                DebugLogger.log("BossPersistenceManager", "Boss already in list: " + bossId);
                                break;
                            }
                            
                            // Yeniden initialize et
                            DebugLogger.log("BossPersistenceManager", "Re-initializing persisted boss: " + bossId + " (UUID: " + entity.getUniqueId() + ")");
                            ActiveBoss activeBoss = new ActiveBoss(plugin, boss, livingEntity);
                            
                            // Kaydedilmiş sağlık bilgisini yükle
                            try {
                                File bossFile = new File(bossesFolder, bossId + ".yml");
                                if (bossFile.exists()) {
                                    FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
                                    double lastHealth = config.getDouble("spawn.lastHealth", boss.getHealth());
                                    
                                    // Sağlığı ayarla
                                    if (lastHealth > 0 && lastHealth <= boss.getHealth()) {
                                        livingEntity.setHealth(lastHealth);
                                        DebugLogger.log("BossPersistenceManager", "Restored health for " + bossId + ": " + lastHealth);
                                    }
                                }
                            } catch (Exception e) {
                                DebugLogger.log("BossPersistenceManager", "Error loading saved boss state for " + bossId, e);
                            }
                            
                            activeBosses.put(entity.getUniqueId(), activeBoss);
                        } else {
                            // Boss config'de yok - eski/geçersiz entity, sil
                            DebugLogger.log("BossPersistenceManager", "Removing invalid persisted boss entity (no config): " + bossId);
                            entity.remove();
                        }
                        break;
                    }
                }
            }
        }
        
        // 2. Config'den kaydedilen boss'ları spawn et (persistent entity yok ise)
        File[] bossFiles = bossesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (bossFiles != null) {
            for (File bossFile : bossFiles) {
                try {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
                    String bossId = bossFile.getName().replace(".yml", "");
                    
                    // Boss config'de var mı?
                    Boss boss = bosses.get(bossId);
                    if (boss == null) {
                        DebugLogger.log("BossPersistenceManager", "Boss config not loaded: " + bossId);
                        continue;
                    }
                    
                    // Kaydedilen konum var mı?
                    String worldName = config.getString("spawn.location.world");
                    if (worldName == null || worldName.isEmpty()) {
                        DebugLogger.log("BossPersistenceManager", "No saved location for boss: " + bossId);
                        continue;
                    }
                    
                    org.bukkit.World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        DebugLogger.log("BossPersistenceManager", "World not found for boss: " + bossId + " (world: " + worldName + ")");
                        continue;
                    }
                    
                    double x = config.getDouble("spawn.location.x", 0);
                    double y = config.getDouble("spawn.location.y", 64);
                    double z = config.getDouble("spawn.location.z", 0);
                    float yaw = (float) config.getDouble("spawn.location.yaw", 0);
                    float pitch = (float) config.getDouble("spawn.location.pitch", 0);
                    
                    Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
                    
                    // Bu boss zaten spawn edilmiş mi?
                    boolean alreadySpawned = false;
                    for (ActiveBoss activeBoss : activeBosses.values()) {
                        if (activeBoss.getBoss().getId().equals(bossId)) {
                            alreadySpawned = true;
                            DebugLogger.log("BossPersistenceManager", "Boss already spawned (from persistent entity): " + bossId);
                            break;
                        }
                    }
                    
                    if (alreadySpawned) {
                        continue; // Zaten spawn edilmiş, config'den spawn etme
                    }
                            DebugLogger.log("BossPersistenceManager", "Spawning saved boss: " + bossId + " at " + worldName + " (" + x + ", " + y + ", " + z + ")");
                            
                            if (bossManager != null) {
                                // Y koordinatını kontrol et - geçersiz ise dünya spawn noktasını kullan
                                if (y < world.getMinHeight() || y > world.getMaxHeight()) {
                                    DebugLogger.log("BossPersistenceManager", "Invalid Y coordinate for " + bossId + ": " + y + " (world min: " + world.getMinHeight() + ", max: " + world.getMaxHeight() + ")");
                                    Location spawnLoc = world.getSpawnLocation();
                                    spawnLocation = new Location(world, x, spawnLoc.getY(), z, yaw, pitch);
                                    DebugLogger.log("BossPersistenceManager", "Using world spawn location instead: " + spawnLoc.getY());
                                }
                                
                                // Boss'u spawn et
                                LivingEntity spawnedEntity = bossManager.spawnBoss(bossId, spawnLocation);
                                
                                if (spawnedEntity != null) {
                                    // Kaydedilen sağlığı restore et
                                    double lastHealth = config.getDouble("spawn.lastHealth", boss.getHealth());
                                    if (lastHealth > 0 && lastHealth <= boss.getHealth()) {
                                        spawnedEntity.setHealth(lastHealth);
                                        DebugLogger.log("BossPersistenceManager", "Restored health for " + bossId + ": " + lastHealth);
                                    }
                                    
                                    DebugLogger.log("BossPersistenceManager", "Successfully spawned saved boss: " + bossId);
                                } else {
                                    DebugLogger.log("BossPersistenceManager", "Failed to spawn saved boss: " + bossId);
                                }
                            } else {
                                DebugLogger.log("BossPersistenceManager", "BossManager not set, cannot spawn saved boss: " + bossId);
                            }
                } catch (Exception e) {
                    DebugLogger.log("BossPersistenceManager", "Error processing boss file: " + bossFile.getName(), e);
                }
            }
        }
        
        DebugLogger.log("BossPersistenceManager", "Re-initialization complete. Active bosses: " + activeBosses.size());
    }
}
