package com.noramu.nmbosses.utils;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Utility class for common string and color operations
 */
public class StringUtils {

    private static final String COLOR_PATTERN = "§[0-9a-fk-or]";
    private static final String AMPERSAND_PATTERN = "&[0-9a-fk-or]";

    /**
     * Colorize text - replace & with §
     */
    public static String colorize(String text) {
        return text == null ? "" : text.replace("&", "§");
    }

    /**
     * Strip all color codes from text
     */
    public static String stripColor(String text) {
        if (text == null) return "";
        return text.replaceAll(COLOR_PATTERN, "").replaceAll(AMPERSAND_PATTERN, "");
    }

    /**
     * Play a sound safely - handles null/invalid sounds
     */
    public static void playSound(LivingEntity entity, String soundName, float volume, float pitch) {
        if (entity == null || soundName == null || soundName.isEmpty()) {
            return;
        }

        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            entity.getWorld().playSound(entity.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            // Invalid sound name - silently ignore
        }
    }

    /**
     * Replace multiple placeholders in a string
     */
    public static String replacePlaceholders(String text, Object... args) {
        if (text == null) return "";
        
        String result = text;
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                result = result.replace(String.valueOf(args[i]), String.valueOf(args[i + 1]));
            }
        }
        
        // Parse PlaceholderAPI placeholders if available
        result = PlaceholderUtil.parsePlaceholders(result);
        
        return result;
    }

    /**
     * Replace multiple placeholders in a string for a player
     */
    public static String replacePlaceholders(Player player, String text, Object... args) {
        if (text == null) return "";
        
        String result = text;
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                result = result.replace(String.valueOf(args[i]), String.valueOf(args[i + 1]));
            }
        }
        
        // Parse PlaceholderAPI placeholders if available
        result = PlaceholderUtil.parsePlaceholders(player, result);
        
        return result;
    }

    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty();
    }
}
