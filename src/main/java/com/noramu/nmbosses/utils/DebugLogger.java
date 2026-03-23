package com.noramu.nmbosses.utils;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.Bukkit;

/**
 * Debug logging utility - logs only when debug mode is enabled in config
 */
public class DebugLogger {

    private static final String PREFIX = "§8[§7nmBosses§8] ";

    /**
     * Log a debug message to console
     */
    public static void log(String message) {
        if (NmBosses.getInstance().isDebugEnabled()) {
            Bukkit.getConsoleSender().sendMessage(PREFIX + message);
        }
    }

    /**
     * Log a debug message with a category
     */
    public static void log(String category, String message) {
        if (NmBosses.getInstance().isDebugEnabled()) {
            Bukkit.getConsoleSender().sendMessage(PREFIX + "§7[" + category + "]§f " + message);
        }
    }

    /**
     * Log a debug message with exception
     */
    public static void log(String message, Exception e) {
        if (NmBosses.getInstance().isDebugEnabled()) {
            Bukkit.getConsoleSender().sendMessage(PREFIX + "§c" + message);
            e.printStackTrace();
        }
    }

    /**
     * Log a debug message with category and exception
     */
    public static void log(String category, String message, Exception e) {
        if (NmBosses.getInstance().isDebugEnabled()) {
            Bukkit.getConsoleSender().sendMessage(PREFIX + "§7[" + category + "]§c " + message);
            e.printStackTrace();
        }
    }
}
