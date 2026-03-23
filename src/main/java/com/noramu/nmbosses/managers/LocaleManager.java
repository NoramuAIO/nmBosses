package com.noramu.nmbosses.managers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Mesaj dil desteği yöneticisi (sadece chat mesajları için)
 */
public class LocaleManager {

    private final NmBosses plugin;
    private final Map<String, FileConfiguration> messageLocales = new HashMap<>();
    private final Map<UUID, String> playerLocales = new HashMap<>();
    
    private String defaultLocale = "en";
    private String serverLocale = "en";
    private boolean autoDetectPlayer = false;
    
    private static final Map<String, String> REGION_MAP = new HashMap<>();
    
    static {
        REGION_MAP.put("tr", "tr");
        REGION_MAP.put("tr_tr", "tr");
        REGION_MAP.put("en", "en");
        REGION_MAP.put("en_us", "en");
        REGION_MAP.put("en_gb", "en");
    }

    public LocaleManager(NmBosses plugin) {
        this.plugin = plugin;
        loadConfig();
        createDirectories();
        saveDefaultLocales();
        loadAllLocales();
    }

    private void loadConfig() {
        plugin.reloadConfig();
        this.defaultLocale = plugin.getConfig().getString("locale.default", "en").toLowerCase();
        this.autoDetectPlayer = plugin.getConfig().getBoolean("locale.auto-detect-player", false);
        
        String serverSetting = plugin.getConfig().getString("locale.server", "manual").toLowerCase();
        
        if (serverSetting.equals("auto")) {
            // Sistem dilini algıla
            String systemLang = Locale.getDefault().getLanguage().toLowerCase();
            this.serverLocale = REGION_MAP.getOrDefault(systemLang, defaultLocale);
            DebugLogger.log("LocaleManager", "Auto-detected server locale: " + serverLocale);
        } else if (serverSetting.equals("manual")) {
            // Manuel mod - default dili kullan
            this.serverLocale = defaultLocale;
            DebugLogger.log("LocaleManager", "Manual mode - using default locale: " + serverLocale);
        } else {
            // Direkt dil belirtilmiş (tr, en, vs.)
            this.serverLocale = REGION_MAP.getOrDefault(serverSetting, serverSetting);
            DebugLogger.log("LocaleManager", "Using specified server locale: " + serverLocale);
        }
        
        DebugLogger.log("LocaleManager", "Locale config - default: " + defaultLocale + ", server: " + serverLocale + ", auto-detect-player: " + autoDetectPlayer);
    }

    private void createDirectories() {
        File msgDir = new File(plugin.getDataFolder(), "locale/messages");
        if (!msgDir.exists()) msgDir.mkdirs();
    }

    private void saveDefaultLocales() {
        saveResourceIfNotExists("locale/messages/en.yml");
        saveResourceIfNotExists("locale/messages/tr.yml");
    }

    private void saveResourceIfNotExists(String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);
        if (!file.exists()) {
            try {
                plugin.saveResource(resourcePath, false);
            } catch (Exception e) {
                DebugLogger.log("LocaleManager", "Could not save resource: " + resourcePath, e);
            }
        }
    }

    public void loadAllLocales() {
        messageLocales.clear();
        File msgDir = new File(plugin.getDataFolder(), "locale/messages");
        loadLocalesFromDirectory(msgDir, messageLocales);
        DebugLogger.log("LocaleManager", "Loaded " + messageLocales.size() + " message locales.");
    }

    private void loadLocalesFromDirectory(File dir, Map<String, FileConfiguration> localeMap) {
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String localeName = file.getName().replace(".yml", "").toLowerCase();
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            localeMap.put(localeName, config);
            DebugLogger.log("LocaleManager", "Loaded locale: " + localeName);
        }
    }

    public void setPlayerLocale(UUID playerId, String locale) {
        playerLocales.put(playerId, locale.toLowerCase());
    }

    public void detectPlayerLocale(Player player) {
        // auto-detect-player kapalıysa sunucu dilini kullan
        if (!autoDetectPlayer) {
            playerLocales.put(player.getUniqueId(), serverLocale);
            return;
        }
        
        String clientLocale = player.getLocale().toLowerCase();
        String mappedLocale = REGION_MAP.getOrDefault(clientLocale, 
                REGION_MAP.getOrDefault(clientLocale.split("_")[0], defaultLocale));
        
        if (!messageLocales.containsKey(mappedLocale)) {
            mappedLocale = defaultLocale;
        }
        
        playerLocales.put(player.getUniqueId(), mappedLocale);
    }

    public String getPlayerLocale(Player player) {
        // Oyuncu için kayıtlı dil yoksa sunucu dilini döndür
        if (!playerLocales.containsKey(player.getUniqueId())) {
            return serverLocale;
        }
        return playerLocales.get(player.getUniqueId());
    }
    
    public boolean isAutoDetectPlayer() {
        return autoDetectPlayer;
    }
    
    public String getServerLocale() {
        return serverLocale;
    }

    public String getMessage(Player player, String key) {
        return getMessage(getPlayerLocale(player), key);
    }

    public String getMessage(String locale, String key) {
        FileConfiguration config = messageLocales.get(locale);
        if (config == null) config = messageLocales.get(defaultLocale);
        if (config == null) return key;
        
        String value = config.getString(key);
        if (value == null) {
            config = messageLocales.get(defaultLocale);
            if (config != null) value = config.getString(key);
        }
        
        return value != null ? StringUtils.colorize(value) : key;
    }

    public String getMessage(Player player, String key, Object... args) {
        String message = getMessage(player, key);
        return StringUtils.replacePlaceholders(message, args);
    }

    public String[] getAvailableLocales() {
        return messageLocales.keySet().toArray(new String[0]);
    }

    public void reload() {
        playerLocales.clear();
        loadConfig();
        loadAllLocales();
        
        // Online oyuncuların locale'lerini güncelle
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            detectPlayerLocale(player);
        }
    }
}
