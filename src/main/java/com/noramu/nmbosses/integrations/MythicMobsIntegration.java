package com.noramu.nmbosses.integrations;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
//import io.lumine.mythic.api.MythicProvider;
//import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * MythicMobs integration utility
 * TEMPORARILY DISABLED - Will be re-enabled when API is available
 */
public class MythicMobsIntegration {

    /**
     * Check if MythicMobs is installed
     */
    public static boolean isEnabled() {
        return NmBosses.getInstance().getServer().getPluginManager().getPlugin("MythicMobs") != null;
    }

    /**
     * Check if entity is MythicMob
     */
    public static boolean isMythicMob(Entity entity) {
        // TEMPORARILY DISABLED
        return false;
        /*
        if (!isEnabled() || entity == null) {
            return false;
        }

        try {
            return MythicProvider.get().getMobManager().getMythicMobInstance(entity) != null;
        } catch (Exception e) {
            return false;
        }
        */
    }

    /**
     * Get MythicMob type name
     */
    public static String getMythicMobType(Entity entity) {
        // TEMPORARILY DISABLED
        return null;
        /*
        if (!isEnabled() || entity == null) {
            return null;
        }

        try {
            var mobInstance = MythicProvider.get().getMobManager().getMythicMobInstance(entity);
            if (mobInstance != null) {
                return mobInstance.getType().getInternalName();
            }
        } catch (Exception e) {
            DebugLogger.log("MythicMobs", "Error getting MythicMob type", e);
        }

        return null;
        */
    }

    /**
     * Spawn MythicMob at location
     */
    public static LivingEntity spawnMythicMob(String mobType, org.bukkit.Location location) {
        // TEMPORARILY DISABLED
        return null;
        /*
        if (!isEnabled() || mobType == null || location == null) {
            return null;
        }

        try {
            MythicMob mythicMob = MythicProvider.get().getMobManager().getMythicMob(mobType);
            if (mythicMob != null) {
                var mobInstance = mythicMob.spawn(location, 1);
                if (mobInstance != null && mobInstance.getEntity() != null) {
                    DebugLogger.log("MythicMobs", "Spawned MythicMob: " + mobType);
                    return mobInstance.getEntity().getBukkitEntity();
                }
            }
        } catch (Exception e) {
            DebugLogger.log("MythicMobs", "Error spawning MythicMob: " + mobType, e);
        }

        return null;
        */
    }
}
