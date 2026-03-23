package com.noramu.nmbosses.managers;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Boss config dosyalarını okuma/yazma işlemleri
 */
public class BossConfigHelper {

    private final File bossesFolder;
    private final Map<String, Boss> bosses;
    private BossManager bossManager; // Lazy initialization

    public BossConfigHelper(File bossesFolder, Map<String, Boss> bosses) {
        this.bossesFolder = bossesFolder;
        this.bosses = bosses;
    }

    public void setBossManager(BossManager bossManager) {
        this.bossManager = bossManager;
    }

    /**
     * Boss spawn lokasyonunu config'e kaydeder
     */
    public boolean setSpawnLocation(String id, Location location) {
        File bossFile = new File(bossesFolder, id + ".yml");
        if (!bossFile.exists()) {
            return false;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        config.set("spawn.location.world", location.getWorld().getName());
        config.set("spawn.location.x", location.getX());
        config.set("spawn.location.y", location.getY());
        config.set("spawn.location.z", location.getZ());
        config.set("spawn.location.yaw", location.getYaw());
        config.set("spawn.location.pitch", location.getPitch());

        try {
            config.save(bossFile);
            // Boss config'i yeniden yükle
            if (bossManager != null) {
                Boss boss = bossManager.loadBoss(bossFile);
                if (boss != null) {
                    bosses.put(id, boss);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Boss auto spawn ayarını toggle eder
     */
    public boolean toggleAutoSpawn(String id) {
        File bossFile = new File(bossesFolder, id + ".yml");
        if (!bossFile.exists()) {
            return false;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossFile);
        boolean current = config.getBoolean("spawn.autoSpawn", false);
        config.set("spawn.autoSpawn", !current);

        try {
            config.save(bossFile);
            // Boss config'i yeniden yükle
            if (bossManager != null) {
                Boss boss = bossManager.loadBoss(bossFile);
                if (boss != null) {
                    bosses.put(id, boss);
                }
            }
            return !current;
        } catch (IOException e) {
            e.printStackTrace();
            return current;
        }
    }

    /**
     * String'den ItemStack parse eder
     * FORMAT: MATERIAL:ADET:ENCHANT:LEVEL:ENCHANT:LEVEL...
     */
    public ItemStack parseItemStack(String str) {
        if (str == null || str.isEmpty() || str.equalsIgnoreCase("AIR")) {
            return new ItemStack(Material.AIR);
        }

        String[] parts = str.split(":");
        try {
            Material material = Material.valueOf(parts[0].toUpperCase());
            ItemStack item = new ItemStack(material);
            
            // Adet varsa ayarla
            if (parts.length > 1) {
                try {
                    int amount = Integer.parseInt(parts[1]);
                    item.setAmount(amount);
                } catch (NumberFormatException ignored) {}
            }
            
            // Enchant'lar varsa ekle
            if (parts.length > 2) {
                for (int i = 2; i < parts.length - 1; i += 2) {
                    try {
                        String enchantName = parts[i].toLowerCase();
                        int level = Integer.parseInt(parts[i + 1]);
                        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
                        if (enchant != null) {
                            item.addUnsafeEnchantment(enchant, level);
                        }
                    } catch (Exception ignored) {}
                }
            }
            
            return item;
        } catch (Exception e) {
            return new ItemStack(Material.AIR);
        }
    }

    /**
     * String'den ItemStack parse eder (adet ile)
     * FORMAT: MATERIAL:ADET:ENCHANT:LEVEL:ENCHANT:LEVEL...
     */
    public ItemStack parseItemStackWithAmount(String str) {
        if (str == null || str.isEmpty()) {
            return new ItemStack(Material.AIR);
        }

        String[] parts = str.split(":");
        try {
            Material material = Material.valueOf(parts[0].toUpperCase());
            int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            ItemStack item = new ItemStack(material, amount);
            
            // Enchant'lar varsa ekle
            if (parts.length > 2) {
                for (int i = 2; i < parts.length - 1; i += 2) {
                    try {
                        String enchantName = parts[i].toLowerCase();
                        int level = Integer.parseInt(parts[i + 1]);
                        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
                        if (enchant != null) {
                            item.addUnsafeEnchantment(enchant, level);
                        }
                    } catch (Exception ignored) {}
                }
            }
            
            return item;
        } catch (Exception e) {
            return new ItemStack(Material.AIR);
        }
    }
}
