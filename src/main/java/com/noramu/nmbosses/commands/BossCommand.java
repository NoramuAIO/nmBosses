package com.noramu.nmbosses.commands;

import com.noramu.nmbosses.Boss;
import com.noramu.nmbosses.NmBosses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Set;

public class BossCommand implements CommandExecutor {

    private final NmBosses plugin;
    private final String prefix = "§8[§6nmBosses§8] ";

    public BossCommand(NmBosses plugin) {
        this.plugin = plugin;
    }

    private static final String NO_PERM = "§cYou don't have permission!";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                sendHelp(sender);
                break;

            case "list":
                listBosses(sender);
                break;

            case "create":
                if (!sender.hasPermission("nmboss.create")) {
                    sender.sendMessage(prefix + NO_PERM);
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix + "§cUsage: /boss create <name> [entityType]");
                    return true;
                }
                String bossId = args[1];
                String entityType = args.length >= 3 ? args[2].toUpperCase() : "ZOMBIE";
                createBoss(sender, bossId, entityType);
                break;

            case "delete":
                if (!sender.hasPermission("nmboss.delete")) {
                    sender.sendMessage(prefix + NO_PERM);
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix + "§cUsage: /boss delete <name>");
                    return true;
                }
                deleteBoss(sender, args[1]);
                break;

            case "spawn":
                if (!sender.hasPermission("nmboss.spawn")) {
                    sender.sendMessage(prefix + NO_PERM);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix + "§cOnly players can use this command!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix + "§cUsage: /boss spawn <name> [x] [y] [z] [world]");
                    return true;
                }
                spawnBoss(sender, args);
                break;

            case "despawn":
                if (!sender.hasPermission("nmboss.spawn")) {
                    sender.sendMessage(prefix + NO_PERM);
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix + "§cUsage: /boss despawn <name|all>");
                    return true;
                }
                despawnBoss(sender, args[1]);
                break;

            case "setspawn":
                if (!sender.hasPermission("nmboss.spawn")) {
                    sender.sendMessage(prefix + NO_PERM);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix + "§cOnly players can use this command!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix + "§cUsage: /boss setspawn <name>");
                    return true;
                }
                setSpawnLocation(sender, args[1]);
                break;

            case "autospawn":
                if (!sender.hasPermission("nmboss.spawn")) {
                    sender.sendMessage(prefix + NO_PERM);
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix + "§cUsage: /boss autospawn <name>");
                    return true;
                }
                toggleAutoSpawn(sender, args[1]);
                break;

            case "reload":
                if (!sender.hasPermission("nmboss.reload")) {
                    sender.sendMessage(prefix + NO_PERM);
                    return true;
                }
                reloadPlugin(sender);
                break;

            case "gui":
                if (!sender.hasPermission("nmboss.gui")) {
                    sender.sendMessage(prefix + NO_PERM);
                    return true;
                }
                openGui(sender);
                break;

            default:
                sender.sendMessage(prefix + "§cUnknown command! Type /boss help");
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§6§l═══════════ nmBosses Yardım ═══════════");
        sender.sendMessage("");
        sender.sendMessage("§e/nm list §7- Tüm boss'ları listeler");
        sender.sendMessage("§e/nm create <isim> [tip] §7- Yeni boss oluşturur");
        sender.sendMessage("§e/nm delete <isim> §7- Boss'u siler");
        sender.sendMessage("§e/nm spawn <isim> §7- Boss'u spawn eder");
        sender.sendMessage("§e/nm despawn <isim|all> §7- Boss'u kaldırır");
        sender.sendMessage("§e/nm setspawn <isim> §7- Spawn noktası belirler");
        sender.sendMessage("§e/nm autospawn <isim> §7- Otomatik spawn açar/kapatır");
        sender.sendMessage("§e/nm gui §7- Boss yönetim menüsünü açar");
        sender.sendMessage("§e/nm reload §7- Eklentiyi yeniden yükler");
        sender.sendMessage("");
        sender.sendMessage("§7Alternatif: §f/nmbosses, /nmboss, /boss, /bosses");
        sender.sendMessage("");
        sender.sendMessage("§6§l══════════════════════════════════════");
        sender.sendMessage("");
    }

    private void listBosses(CommandSender sender) {
        Set<String> bossIds = plugin.getBossManager().getBossIds();

        if (bossIds.isEmpty()) {
            sender.sendMessage(prefix + "§cHenüz hiç boss oluşturulmamış!");
            return;
        }

        sender.sendMessage("");
        sender.sendMessage("§6§l═══════════ Boss Listesi ═══════════");
        sender.sendMessage("");

        for (String id : bossIds) {
            Boss boss = plugin.getBossManager().getBoss(id);
            int activeCount = plugin.getBossManager().getActiveBossCount(id);
            String autoSpawn = boss.isAutoSpawn() ? "§a✔" : "§c✘";
            String spawnTime = getSpawnTimeDisplay(boss.getSpawnTime());

            sender.sendMessage("§e• §f" + id + " §7(" + colorize(boss.getDisplayName()) + "§7)");
            sender.sendMessage("  §7Tip: §f" + boss.getEntityType() + " §7| Can: §c" + (int) boss.getHealth() + " ❤");
            sender.sendMessage("  §7Hasar: §c" + boss.getDamage() + " §7| Hız: §f" + boss.getSpeed());
            sender.sendMessage("  §7Aktif: §f" + activeCount + "/" + boss.getMaxSpawnCount() + " §7| Auto: " + autoSpawn
                    + " §7| Zaman: " + spawnTime);

            if (boss.isAbilitiesEnabled() && boss.getAbilities() != null) {
                sender.sendMessage("  §7Yetenekler: §d" + boss.getAbilities().size() + " adet");
            }
            if (boss.isPhaseSystemEnabled() && boss.getPhases() != null) {
                sender.sendMessage("  §7Fazlar: §e" + boss.getPhases().size() + " adet");
            }
        }

        sender.sendMessage("");
        sender.sendMessage("§6§l═════════════════════════════════════");
        sender.sendMessage("");
    }

    private void createBoss(CommandSender sender, String id, String entityType) {
        if (plugin.getBossManager().getBoss(id) != null) {
            sender.sendMessage(prefix + "§cBu isimde bir boss zaten mevcut!");
            return;
        }

        if (plugin.getBossManager().createBoss(id, entityType)) {
            sender.sendMessage(prefix + "§aBoss başarıyla oluşturuldu: §f" + id);
            sender.sendMessage(prefix + "§7Entity tipi: §f" + entityType);
            sender.sendMessage(prefix + "§7Düzenlemek için: §e/boss edit " + id);
        } else {
            sender.sendMessage(prefix + "§cBoss oluşturulurken bir hata oluştu!");
        }
    }

    private void deleteBoss(CommandSender sender, String id) {
        if (plugin.getBossManager().getBoss(id) == null) {
            sender.sendMessage(prefix + "§cBu isimde bir boss bulunamadı!");
            return;
        }

        if (plugin.getBossManager().deleteBoss(id)) {
            sender.sendMessage(prefix + "§aBoss başarıyla silindi: §f" + id);
        } else {
            sender.sendMessage(prefix + "§cBoss silinirken bir hata oluştu!");
        }
    }

    private void spawnBoss(CommandSender sender, String[] args) {
        String id = args[1];
        Boss boss = plugin.getBossManager().getBoss(id);

        if (boss == null) {
            sender.sendMessage(prefix + "§cBu isimde bir boss bulunamadı!");
            return;
        }

        Location location;

        if (args.length >= 5) {
            try {
                double x = Double.parseDouble(args[2]);
                double y = Double.parseDouble(args[3]);
                double z = Double.parseDouble(args[4]);
                String worldName = args.length >= 6 ? args[5] : ((Player) sender).getWorld().getName();

                if (Bukkit.getWorld(worldName) == null) {
                    sender.sendMessage(prefix + "§cBöyle bir dünya bulunamadı: " + worldName);
                    return;
                }

                location = new Location(Bukkit.getWorld(worldName), x, y, z);
            } catch (NumberFormatException e) {
                sender.sendMessage(prefix + "§cGeçersiz koordinatlar!");
                return;
            }
        } else {
            Player player = (Player) sender;
            location = player.getLocation();
        }

        LivingEntity entity = plugin.getBossManager().spawnBoss(id, location);

        if (entity != null) {
            sender.sendMessage(prefix + "§aBoss spawn edildi: §f" + id);
            sender.sendMessage(prefix + "§7Konum: §f" + (int) location.getX() + ", " + (int) location.getY() + ", "
                    + (int) location.getZ());
        } else {
            sender.sendMessage(prefix + "§cBoss spawn edilemedi!");
            sender.sendMessage(prefix + "§7- Maksimum sayıya ulaşılmış olabilir");
            sender.sendMessage(prefix + "§7- Zaman uygun olmayabilir (gece/gündüz)");
        }
    }

    private void despawnBoss(CommandSender sender, String id) {
        if (id.equalsIgnoreCase("all")) {
            plugin.getBossManager().removeAllActiveBosses();
            sender.sendMessage(prefix + "§aTüm boss'lar kaldırıldı!");
            return;
        }

        if (plugin.getBossManager().getBoss(id) == null) {
            sender.sendMessage(prefix + "§cBu isimde bir boss bulunamadı!");
            return;
        }

        plugin.getBossManager().despawnBoss(id);
        sender.sendMessage(prefix + "§aBoss kaldırıldı: §f" + id);
    }

    private void setSpawnLocation(CommandSender sender, String id) {
        Boss boss = plugin.getBossManager().getBoss(id);

        if (boss == null) {
            sender.sendMessage(prefix + "§cBu isimde bir boss bulunamadı!");
            return;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        if (plugin.getBossManager().setSpawnLocation(id, location)) {
            sender.sendMessage(prefix + "§aSpawn noktası ayarlandı: §f" + id);
            sender.sendMessage(prefix + "§7Konum: §f" + (int) location.getX() + ", " + (int) location.getY() + ", "
                    + (int) location.getZ());
        } else {
            sender.sendMessage(prefix + "§cSpawn noktası ayarlanırken bir hata oluştu!");
        }
    }

    private void toggleAutoSpawn(CommandSender sender, String id) {
        Boss boss = plugin.getBossManager().getBoss(id);

        if (boss == null) {
            sender.sendMessage(prefix + "§cBu isimde bir boss bulunamadı!");
            return;
        }

        boolean newState = plugin.getBossManager().toggleAutoSpawn(id);

        if (newState) {
            plugin.getSpawnManager().startAutoSpawnForBoss(id);
            sender.sendMessage(prefix + "§aOtomatik spawn §fAÇILDI§a: §f" + id);
        } else {
            plugin.getSpawnManager().stopAutoSpawnForBoss(id);
            sender.sendMessage(prefix + "§cOtomatik spawn §fKAPATILDI§c: §f" + id);
        }
    }

    private void reloadPlugin(CommandSender sender) {
        // Boss'ları kaldırmadan sadece config'leri yenile
        plugin.getBossManager().reloadBossConfigs();
        plugin.getLocaleManager().reload();
        plugin.getSpawnManager().loadSpawnLocations();

        // Otomatik spawn görevlerini yeniden başlat
        plugin.getSpawnManager().stopAllTasks();
        plugin.getSpawnManager().startAutoSpawnTask();

        sender.sendMessage(prefix + "§aEklenti yeniden yüklendi!");
        sender.sendMessage(prefix + "§7Yüklenen boss sayısı: §f" + plugin.getBossManager().getBossIds().size());
        sender.sendMessage(prefix + "§7Aktif boss'lar korundu ve ayarları güncellendi.");
    }

    private void openGui(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "§cBu komutu sadece oyuncular kullanabilir!");
            return;
        }
        plugin.getGUIManager().openBossListMenu((Player) sender, 0);
    }

    private String getSpawnTimeDisplay(String spawnTime) {
        if (spawnTime == null)
            return "§fHer zaman";
        switch (spawnTime.toUpperCase()) {
            case "DAY":
                return "§e☀ Gündüz";
            case "NIGHT":
                return "§9☾ Gece";
            default:
                return "§fHer zaman";
        }
    }

    private String colorize(String text) {
        if (text == null)
            return "";
        return text.replace("&", "§");
    }
}
