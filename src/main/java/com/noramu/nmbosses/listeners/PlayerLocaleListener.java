package com.noramu.nmbosses.listeners;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

/**
 * Oyuncu dil algılama listener'ı
 * Oyuncunun Minecraft client dilini otomatik algılar
 */
public class PlayerLocaleListener implements Listener {

    private final NmBosses plugin;

    public PlayerLocaleListener(NmBosses plugin) {
        this.plugin = plugin;
    }

    /**
     * Oyuncu sunucuya katıldığında dilini algıla
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Biraz gecikme ile algıla (client locale bilgisi gecikmeli gelebilir)
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // detectPlayerLocale içinde auto-detect kontrolü yapılıyor
            plugin.getLocaleManager().detectPlayerLocale(player);
        }, 20L); // 1 saniye sonra
    }

    /**
     * Oyuncu dilini değiştirdiğinde güncelle
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLocaleChange(PlayerLocaleChangeEvent event) {
        Player player = event.getPlayer();
        
        // Sadece auto-detect açıksa client değişikliğini takip et
        if (plugin.getLocaleManager().isAutoDetectPlayer()) {
            plugin.getLocaleManager().detectPlayerLocale(player);
        }
    }
}
