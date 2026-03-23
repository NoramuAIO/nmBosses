package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.integrations.ItemsAdderIntegration;
import com.noramu.nmbosses.integrations.OraxenIntegration;
import com.noramu.nmbosses.integrations.NexoIntegration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Config dosyası işlemleri için yardımcı sınıf
 */
public class ConfigHelper {

    private final NmBosses plugin;

    public ConfigHelper(NmBosses plugin) {
        this.plugin = plugin;
    }

    public void updateConfig(String bossId, String key, Object value) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set(key, value);
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleConfig(String bossId, String key) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set(key, !config.getBoolean(key));
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getCurrentDoubleValue(String bossId, String key) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return 0;
        return YamlConfiguration.loadConfiguration(bossFile).getDouble(key, 0);
    }

    public String getConfigString(String bossId, String key) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return "";
        return YamlConfiguration.loadConfiguration(bossFile).getString(key, "");
    }

    public String serializeItemWithEnchants(ItemStack item) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getType().name());
        
        if (item.getAmount() > 1 || !item.getEnchantments().isEmpty()) {
            sb.append(":").append(item.getAmount());
        }
        
        if (!item.getEnchantments().isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                sb.append(":").append(entry.getKey().getKey().getKey().toUpperCase());
                sb.append(":").append(entry.getValue());
            }
        }
        
        return sb.toString();
    }

    public void addDropItem(String bossId, ItemStack item) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> items = config.getStringList("rewards.items");
        if (items == null) items = new ArrayList<>();
        
        items.add(serializeItemWithEnchants(item));
        config.set("rewards.items", items);
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeDropItem(String bossId, int index) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> items = config.getStringList("rewards.items");
        if (items == null || index >= items.size()) return;
        
        items.remove(index);
        config.set("rewards.items", items);
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCommand(String bossId, String command) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> commands = config.getStringList("rewards.commands");
        if (commands == null) commands = new ArrayList<>();
        
        commands.add(command);
        config.set("rewards.commands", commands);
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeCommand(String bossId, int index) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> commands = config.getStringList("rewards.commands");
        if (commands == null || index >= commands.size()) return;
        
        commands.remove(index);
        config.set("rewards.commands", commands);
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editCommand(String bossId, int index, String newCommand) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> commands = config.getStringList("rewards.commands");
        if (commands == null || index >= commands.size()) return;
        
        commands.set(index, newCommand);
        config.set("rewards.commands", commands);
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleDay(String bossId, String day) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> days = config.getStringList("spawn.schedule.days");
        if (days == null) days = new ArrayList<>();
        
        if (days.contains(day)) {
            days.remove(day);
        } else {
            days.add(day);
        }
        
        config.set("spawn.schedule.days", days);
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewAbility(String bossId, String abilityId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        String path = "abilities.list." + abilityId + ".";
        
        config.set(path + "name", abilityId);
        config.set(path + "type", "FIREBALL");
        config.set(path + "cooldown", 10);
        config.set(path + "chance", 30);
        config.set(path + "damage", 5);
        config.set(path + "range", 15);
        config.set(path + "message", "");
        config.set(path + "sound", "ENTITY_BLAZE_SHOOT");
        config.set(path + "particle", "FLAME");
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeAbility(String bossId, String abilityId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("abilities.list." + abilityId, null);
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewPhase(String bossId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        
        int nextPhase = 1;
        if (config.isConfigurationSection("phases.list")) {
            nextPhase = config.getConfigurationSection("phases.list").getKeys(false).size() + 1;
        }
        
        String path = "phases.list.phase" + nextPhase + ".";
        
        config.set(path + "number", nextPhase);
        config.set(path + "name", "Phase " + nextPhase);
        config.set(path + "healthThreshold", 50 / nextPhase);
        config.set(path + "damageMultiplier", 1.0 + (nextPhase * 0.5));
        config.set(path + "speedMultiplier", 1.0 + (nextPhase * 0.2));
        config.set(path + "attackSpeedMultiplier", 1.0 + (nextPhase * 0.3));
        config.set(path + "glowing", true);
        config.set(path + "bossBarColor", "YELLOW");
        config.set(path + "nameColor", "&e");
        config.set(path + "startMessage", "");
        config.set(path + "startSound", "ENTITY_WITHER_SPAWN");
        config.set(path + "startParticle", "EXPLOSION_LARGE");
        config.set(path + "regeneration", false);
        config.set(path + "regenerationAmount", 0);
        config.set(path + "invulnerableOnChange", true);
        config.set(path + "invulnerableDuration", 40);
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removePhase(String bossId, int phaseNumber) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("phases.list.phase" + phaseNumber, null);
        
        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidTimeFormat(String time) {
        if (time == null || !time.matches("\\d{2}:\\d{2}")) return false;
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
        } catch (Exception e) {
            return false;
        }
    }

    // ===== Custom Item Methods =====

    /**
     * Get custom item by namespace (ItemsAdder, Oraxen, Nexo)
     */
    public ItemStack getCustomItem(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return null;
        }

        // Try ItemsAdder
        if (ItemsAdderIntegration.isEnabled()) {
            ItemStack item = ItemsAdderIntegration.getCustomItem(namespace);
            if (item != null) return item;
        }

        // Try Oraxen
        if (OraxenIntegration.isEnabled()) {
            ItemStack item = OraxenIntegration.getCustomItem(namespace);
            if (item != null) return item;
        }

        // Try Nexo
        if (NexoIntegration.isEnabled()) {
            ItemStack item = NexoIntegration.getCustomItem(namespace);
            if (item != null) return item;
        }

        return null;
    }

    /**
     * Get all available custom items from enabled plugins
     */
    public List<String> getAvailableCustomItems() {
        List<String> items = new ArrayList<>();

        if (ItemsAdderIntegration.isEnabled()) {
            items.addAll(ItemsAdderIntegration.getAvailableItems());
        }

        if (OraxenIntegration.isEnabled()) {
            items.addAll(OraxenIntegration.getAvailableItems());
        }

        if (NexoIntegration.isEnabled()) {
            items.addAll(NexoIntegration.getAvailableItems());
        }

        return items;
    }

    /**
     * Set equipment item (helmet, chestplate, leggings, boots, main_hand, off_hand)
     */
    public void setEquipmentItem(String bossId, String slot, String customItemNamespace) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("equipment." + slot, customItemNamespace);

        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get equipment item namespace
     */
    public String getEquipmentItem(String bossId, String slot) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return "";

        return YamlConfiguration.loadConfiguration(bossFile).getString("equipment." + slot, "");
    }

    /**
     * Add custom item to drop rewards
     */
    public void addCustomItemDrop(String bossId, String customItemNamespace, double chance) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        String path = "rewards.custom_items";

        List<String> customItems = config.getStringList(path);
        if (customItems == null) customItems = new ArrayList<>();

        customItems.add(customItemNamespace + ":" + chance);
        config.set(path, customItems);

        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove custom item from drop rewards
     */
    public void removeCustomItemDrop(String bossId, int index) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> customItems = config.getStringList("rewards.custom_items");
        if (customItems == null || index >= customItems.size()) return;

        customItems.remove(index);
        config.set("rewards.custom_items", customItems);

        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all custom item drops for a boss
     */
    public List<String> getCustomItemDrops(String bossId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return new ArrayList<>();

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> customItems = config.getStringList("rewards.custom_items");
        return customItems != null ? customItems : new ArrayList<>();
    }

    // ===== Model Plugin Methods =====

    /**
     * Set ModelEngine model for boss
     */
    public void setModelEngineModel(String bossId, String modelId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("models.modelEngine.enabled", true);
        config.set("models.modelEngine.modelId", modelId);

        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get ModelEngine model ID for boss
     */
    public String getModelEngineModel(String bossId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return "";

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        return config.getString("models.modelEngine.modelId", "");
    }

    /**
     * Set MythicMobs type for boss
     */
    public void setMythicMobsType(String bossId, String mobType) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("models.mythicMobs.enabled", true);
        config.set("models.mythicMobs.mobType", mobType);

        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get MythicMobs type for boss
     */
    public String getMythicMobsType(String bossId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return "";

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        return config.getString("models.mythicMobs.mobType", "");
    }

    /**
     * Add MythicMobs ability to boss
     */
    public void addMythicMobsAbility(String bossId, String abilityId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> abilities = config.getStringList("models.mythicMobs.abilities");
        if (abilities == null) abilities = new ArrayList<>();

        if (!abilities.contains(abilityId)) {
            abilities.add(abilityId);
            config.set("models.mythicMobs.abilities", abilities);

            try {
                config.save(bossFile);
                plugin.getBossManager().reloadBossConfigs();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove MythicMobs ability from boss
     */
    public void removeMythicMobsAbility(String bossId, String abilityId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> abilities = config.getStringList("models.mythicMobs.abilities");
        if (abilities == null) return;

        if (abilities.remove(abilityId)) {
            config.set("models.mythicMobs.abilities", abilities);

            try {
                config.save(bossFile);
                plugin.getBossManager().reloadBossConfigs();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all MythicMobs abilities for boss
     */
    public List<String> getMythicMobsAbilities(String bossId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return new ArrayList<>();

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        List<String> abilities = config.getStringList("models.mythicMobs.abilities");
        return abilities != null ? abilities : new ArrayList<>();
    }

    /**
     * Set Oraxen model for boss
     */
    public void setOraxenModel(String bossId, String modelId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("models.oraxen.enabled", true);
        config.set("models.oraxen.modelId", modelId);

        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Oraxen model ID for boss
     */
    public String getOraxenModel(String bossId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return "";

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        return config.getString("models.oraxen.modelId", "");
    }

    /**
     * Set ItemsAdder model for boss
     */
    public void setItemsAdderModel(String bossId, String modelId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("models.itemsAdder.enabled", true);
        config.set("models.itemsAdder.modelId", modelId);

        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get ItemsAdder model ID for boss
     */
    public String getItemsAdderModel(String bossId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return "";

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        return config.getString("models.itemsAdder.modelId", "");
    }

    /**
     * Set Nexo model for boss
     */
    public void setNexoModel(String bossId, String modelId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("models.nexo.enabled", true);
        config.set("models.nexo.modelId", modelId);

        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Nexo model ID for boss
     */
    public String getNexoModel(String bossId) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return "";

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        return config.getString("models.nexo.modelId", "");
    }

    /**
     * Check if model plugin is enabled for boss
     */
    public boolean isModelPluginEnabled(String bossId, String pluginName) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return false;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        return config.getBoolean("models." + pluginName + ".enabled", false);
    }

    /**
     * Enable/disable model plugin for boss
     */
    public void setModelPluginEnabled(String bossId, String pluginName, boolean enabled) {
        File bossFile = new File(plugin.getBossManager().getBossesFolder(), bossId + ".yml");
        if (!bossFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("models." + pluginName + ".enabled", enabled);

        try {
            config.save(bossFile);
            plugin.getBossManager().reloadBossConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
