package com.noramu.nmbosses;

import com.noramu.nmbosses.commands.BossCommand;
import com.noramu.nmbosses.commands.BossTabCompleter;
import com.noramu.nmbosses.gui.GUIListener;
import com.noramu.nmbosses.gui.GUIManager;
import com.noramu.nmbosses.listeners.BossDamageListener;
import com.noramu.nmbosses.listeners.BossDeathListener;
import com.noramu.nmbosses.listeners.ChunkListener;
import com.noramu.nmbosses.listeners.PlayerLocaleListener;
import com.noramu.nmbosses.managers.AbilityManager;
import com.noramu.nmbosses.managers.BossManager;
import com.noramu.nmbosses.managers.LocaleManager;
import com.noramu.nmbosses.managers.SpawnManager;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class NmBosses extends JavaPlugin {

    private static NmBosses instance;
    private BossManager bossManager;
    private SpawnManager spawnManager;
    private AbilityManager abilityManager;
    private GUIManager guiManager;
    private LocaleManager localeManager;
    
    // Listener references for cleanup
    private BossDeathListener bossDeathListener;
    private BossDamageListener bossDamageListener;
    private ChunkListener chunkListener;
    private GUIListener guiListener;
    private PlayerLocaleListener playerLocaleListener;

    @Override
    public void onEnable() {
        instance = this;

        // Config dosyalarını kaydet
        saveDefaultConfig();

        // Bosses klasörünü oluştur
        File bossesDir = new File(getDataFolder(), "bosses");
        if (!bossesDir.exists()) {
            bossesDir.mkdirs();
        }

        // Manager'ları başlat
        this.localeManager = new LocaleManager(this);
        this.abilityManager = new AbilityManager(this);
        this.bossManager = new BossManager(this);
        this.spawnManager = new SpawnManager(this);
        this.guiManager = new GUIManager(this);

        // Boss dosyalarını yükle (1 tick sonra - dünyalar yüklendikten sonra)
        getServer().getScheduler().runTaskLater(this, () -> {
            bossManager.loadAllBosses();
            spawnManager.loadSpawnLocations();
            
            // Mevcut boss entity'lerini yeniden initialize et (persistent entity'ler)
            bossManager.reinitializeExistingBosses();
            
            // Otomatik spawn görevini başlat (boss config'leri yüklendikten sonra)
            spawnManager.startAutoSpawnTask();
            
            getLogger().info("Boss configurations loaded after world initialization.");
        }, 1L);

        // Listener'ları kaydet (references tutarak)
        this.bossDeathListener = new BossDeathListener(this);
        this.bossDamageListener = new BossDamageListener(this);
        this.chunkListener = new ChunkListener(this);
        this.guiListener = new GUIListener(this);
        this.playerLocaleListener = new PlayerLocaleListener(this);
        
        getServer().getPluginManager().registerEvents(bossDeathListener, this);
        getServer().getPluginManager().registerEvents(bossDamageListener, this);
        getServer().getPluginManager().registerEvents(chunkListener, this);
        getServer().getPluginManager().registerEvents(guiListener, this);
        getServer().getPluginManager().registerEvents(playerLocaleListener, this);

        // Komutları kaydet
        getCommand("nmbosses").setExecutor(new BossCommand(this));
        getCommand("nmbosses").setTabCompleter(new BossTabCompleter(this));

        getServer().getConsoleSender().sendMessage("");
        getServer().getConsoleSender().sendMessage("§a═══════════════════════════════════════════");
        getServer().getConsoleSender().sendMessage("§6       nmBosses §e- §aBoss Plugin Enabled!");
        getServer().getConsoleSender().sendMessage("§7       Developer: §fNoramu");
        getServer().getConsoleSender().sendMessage("§7       Version: §f" + getDescription().getVersion());
        getServer().getConsoleSender().sendMessage("§7       Locales: §f" + String.join(", ", localeManager.getAvailableLocales()));
        getServer().getConsoleSender().sendMessage("§a═══════════════════════════════════════════");
        getServer().getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§c nmBosses disabling...");
        
        // Aktif boss'ları config'e kaydet (sunucu kapanırken)
        if (bossManager != null) {
            getServer().getConsoleSender().sendMessage("§c Saving active bosses...");
            bossManager.saveActiveBosses();
        }
        
        // Listener'ları unregister et (memory leak prevention)
        HandlerList.unregisterAll(bossDeathListener);
        HandlerList.unregisterAll(bossDamageListener);
        HandlerList.unregisterAll(chunkListener);
        HandlerList.unregisterAll(guiListener);
        HandlerList.unregisterAll(playerLocaleListener);
        
        // Aktif boss'ları temizle
        if (bossManager != null) {
            bossManager.removeAllActiveBosses();
        }

        // Spawn görevlerini durdur
        if (spawnManager != null) {
            spawnManager.stopAllTasks();
        }

        getServer().getConsoleSender().sendMessage("§c nmBosses disabled!");
    }

    public static NmBosses getInstance() {
        return instance;
    }

    public BossManager getBossManager() {
        return bossManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public boolean isDebugEnabled() {
        return getConfig().getBoolean("settings.debug", false);
    }
}
