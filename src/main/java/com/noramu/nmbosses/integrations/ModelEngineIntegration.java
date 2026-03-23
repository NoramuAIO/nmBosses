package com.noramu.nmbosses.integrations;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.DebugLogger;
import org.bukkit.entity.Entity;

/**
 * ModelEngine integration utility
 */
public class ModelEngineIntegration {

    /**
     * Check if ModelEngine is installed
     */
    public static boolean isEnabled() {
        return NmBosses.getInstance().getServer().getPluginManager().getPlugin("ModelEngine") != null;
    }

    /**
     * Check if entity has ModelEngine model
     */
    public static boolean hasModel(Entity entity) {
        if (!isEnabled() || entity == null) {
            return false;
        }

        try {
            // ModelEngine API check - entity has model if it's registered
            return entity.hasMetadata("model_engine_entity");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get ModelEngine model ID
     */
    public static String getModelId(Entity entity) {
        if (!isEnabled() || entity == null) {
            return null;
        }

        try {
            if (entity.hasMetadata("model_engine_entity")) {
                return entity.getMetadata("model_engine_entity").get(0).asString();
            }
        } catch (Exception e) {
            DebugLogger.log("ModelEngine", "Error getting model ID", e);
        }

        return null;
    }

    /**
     * Set ModelEngine model for entity
     */
    public static void setModel(Entity entity, String modelId) {
        if (!isEnabled() || entity == null || modelId == null) {
            return;
        }

        try {
            entity.setMetadata("model_engine_entity", 
                new org.bukkit.metadata.FixedMetadataValue(NmBosses.getInstance(), modelId));
            DebugLogger.log("ModelEngine", "Set model for entity: " + modelId);
        } catch (Exception e) {
            DebugLogger.log("ModelEngine", "Error setting model", e);
        }
    }
}
