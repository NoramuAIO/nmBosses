package com.noramu.nmbosses.listeners;

import com.noramu.nmbosses.ActiveBoss;
import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Chunk load/unload olaylarını dinler ve boss'ları yönetir
 */
public class ChunkListener implements Listener {

    private final NmBosses plugin;
    // Boss'ların chunk unload öncesi konumlarını sakla
    private final Map<UUID, BossChunkData> unloadedBosses = new HashMap<>();

    public ChunkListener(NmBosses plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        // Chunk'taki tüm entity'leri kontrol et
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            
            LivingEntity livingEntity = (LivingEntity) entity;
            
            // Bu entity bir boss mu?
            ActiveBoss activeBoss = plugin.getBossManager().getActiveBoss(entity.getUniqueId());
            if (activeBoss != null) {
                // Boss'u temizle (bossbar, task'lar vb.) ama listeden kaldırma
                // Entity persistent true olduğu için Minecraft tarafından kaydedilecek
                // Sunucu açılınca reinitializeExistingBosses() yeniden initialize edecek
                activeBoss.remove();
                
                plugin.getLogger().info("[ChunkListener] Boss unloaded from chunk (will be persisted): " + activeBoss.getBoss().getId());
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // Chunk'taki entity'leri kontrol et
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            
            LivingEntity livingEntity = (LivingEntity) entity;
            
            // nmBoss tag'i var mı?
            for (String tag : entity.getScoreboardTags()) {
                if (tag.startsWith("nmBoss:")) {
                    String bossId = tag.substring(7); // "nmBoss:" sonrası
                    
                    // Bu boss ActiveBoss listesinde var mı?
                    ActiveBoss existingBoss = plugin.getBossManager().getActiveBoss(entity.getUniqueId());
                    
                    if (existingBoss != null) {
                        // Boss zaten listede var - bossbar vb. yeniden başlat
                        plugin.getLogger().info("[ChunkListener] Boss re-loaded from chunk: " + bossId);
                        break;
                    }
                    
                    // Boss listede yok - bu duplicate olabilir mi kontrol et
                    // Aynı boss ID'sine sahip başka bir entity var mı?
                    boolean hasDuplicate = false;
                    for (ActiveBoss activeBoss : plugin.getBossManager().getActiveBosses().values()) {
                        if (activeBoss.getBoss().getId().equals(bossId)) {
                            // Aynı boss ID'sine sahip başka bir entity var - bu duplicate
                            hasDuplicate = true;
                            plugin.getLogger().info("[ChunkListener] Found duplicate boss entity for " + bossId + ", removing...");
                            entity.remove();
                            break;
                        }
                    }
                    
                    if (!hasDuplicate) {
                        // Duplicate değil - yeniden initialize et
                        Boss boss = plugin.getBossManager().getBoss(bossId);
                        if (boss != null) {
                            // Yeni ActiveBoss oluştur
                            plugin.getLogger().info("[ChunkListener] Re-initializing boss " + bossId + " after chunk load");
                            ActiveBoss activeBoss = new ActiveBoss(plugin, boss, livingEntity);
                            plugin.getBossManager().getActiveBosses().put(entity.getUniqueId(), activeBoss);
                        } else {
                            // Boss config'de yok - eski/geçersiz entity, sil
                            plugin.getLogger().info("[ChunkListener] Removing invalid boss entity (no config): " + bossId);
                            entity.remove();
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Boss chunk data holder
     */
    private static class BossChunkData {
        final String bossId;
        final Location location;
        final double health;
        final int phaseNumber;

        BossChunkData(String bossId, Location location, double health, int phaseNumber) {
            this.bossId = bossId;
            this.location = location;
            this.health = health;
            this.phaseNumber = phaseNumber;
        }
    }
}
