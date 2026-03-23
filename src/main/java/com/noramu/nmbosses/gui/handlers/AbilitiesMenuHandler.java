package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class AbilitiesMenuHandler {

    private final NmBosses plugin;
    private final ConfigHelper config;
    private final SessionData session;

    public AbilitiesMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String bossId, ClickType click) {
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openBossEditMenu(player, bossId);
            return;
        }
        
        if (clicked.getType() == Material.BLAZE_POWDER) {
            if (clicked.getItemMeta() != null && clicked.getItemMeta().hasEnchants()) {
                config.toggleConfig(bossId, "abilities.enabled");
                plugin.getGUIManager().openAbilitiesMenu(player, bossId);
            }
            return;
        }
        
        if (clicked.getType() == Material.LIME_DYE) {
            player.closeInventory();
            player.sendMessage("§eEnter ability ID:");
            session.setWaitingForInput(player.getUniqueId(), "new_ability");
            session.setEditingBoss(player.getUniqueId(), bossId);
            return;
        }
        
        if (clicked.hasItemMeta() && clicked.getItemMeta() != null && clicked.getItemMeta().hasLore()) {
            for (String lore : clicked.getItemMeta().getLore()) {
                String abilityId = extractIdFromLore(lore);
                if (StringUtils.isNotEmpty(abilityId)) {
                    if (click.isShiftClick()) {
                        config.removeAbility(bossId, abilityId);
                        player.sendMessage("§eAbility removed!");
                        plugin.getGUIManager().openAbilitiesMenu(player, bossId);
                    } else {
                        session.setEditingBoss(player.getUniqueId(), bossId);
                        session.setInputType(player.getUniqueId(), abilityId);
                        plugin.getGUIManager().openAbilityEditMenu(player, bossId, abilityId);
                    }
                    return;
                }
            }
        }
    }

    public void handleAbilityEditClick(Player player, ItemStack clicked, String abilityId, ClickType click) {
        String bossId = session.getEditingBoss(player.getUniqueId());
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openAbilitiesMenu(player, bossId);
            return;
        }
        
        String path = "abilities.list." + abilityId + ".";
        Material mat = clicked.getType();

        if (mat == Material.NAME_TAG) {
            player.closeInventory();
            player.sendMessage("§eEnter ability name:");
            session.setWaitingForInput(player.getUniqueId(), "ability_name");
            session.setInputType(player.getUniqueId(), abilityId);
            return;
        }
        
        if (mat == Material.BEACON) {
            plugin.getGUIManager().openAbilityTypeMenu(player, bossId, abilityId);
            return;
        }
        
        if (mat == Material.CLOCK) {
            double change = click.isShiftClick() ? 5.0 : 1.0;
            if (click.isLeftClick()) change = -change;
            double cooldownValue = config.getCurrentDoubleValue(bossId, path + "cooldown") + change;
            if (cooldownValue < 1.0) cooldownValue = 1.0;
            config.updateConfig(bossId, path + "cooldown", cooldownValue);
        } else if (mat == Material.RABBIT_FOOT) {
            double change = click.isLeftClick() ? -5.0 : 5.0;
            double chanceValue = config.getCurrentDoubleValue(bossId, path + "chance") + change;
            if (chanceValue < 0.0) chanceValue = 0.0;
            if (chanceValue > 100.0) chanceValue = 100.0;
            config.updateConfig(bossId, path + "chance", chanceValue);
        } else if (mat == Material.IRON_SWORD) {
            double change = click.isShiftClick() ? 5.0 : 1.0;
            if (click.isLeftClick()) change = -change;
            double damageValue = config.getCurrentDoubleValue(bossId, path + "damage") + change;
            if (damageValue < 0.0) damageValue = 0.0;
            config.updateConfig(bossId, path + "damage", damageValue);
        } else if (mat == Material.BOW) {
            double rangeChange = click.isLeftClick() ? -1.0 : 1.0;
            double rangeValue = config.getCurrentDoubleValue(bossId, path + "range") + rangeChange;
            if (rangeValue < 1.0) rangeValue = 1.0;
            config.updateConfig(bossId, path + "range", rangeValue);
        } else if (mat == Material.PAPER) {
            player.closeInventory();
            player.sendMessage("§eEnter ability message:");
            session.setWaitingForInput(player.getUniqueId(), "ability_message");
            session.setInputType(player.getUniqueId(), abilityId);
            return;
        } else if (mat == Material.NOTE_BLOCK) {
            player.closeInventory();
            player.sendMessage("§eEnter sound name:");
            session.setWaitingForInput(player.getUniqueId(), "ability_sound");
            session.setInputType(player.getUniqueId(), abilityId);
            return;
        } else if (mat == Material.FIREWORK_ROCKET) {
            player.closeInventory();
            player.sendMessage("§eEnter particle name:");
            session.setWaitingForInput(player.getUniqueId(), "ability_particle");
            session.setInputType(player.getUniqueId(), abilityId);
            return;
        }
        
        plugin.getGUIManager().openAbilityEditMenu(player, bossId, abilityId);
    }

    public void handleAbilityTypeClick(Player player, ItemStack clicked, String abilityId) {
        String bossId = session.getEditingBoss(player.getUniqueId());
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openAbilityEditMenu(player, bossId, abilityId);
            return;
        }
        
        if (clicked.hasItemMeta() && clicked.getItemMeta() != null && clicked.getItemMeta().hasLore()) {
            for (String lore : clicked.getItemMeta().getLore()) {
                if (lore.contains("Type: §f")) {
                    String type = lore.split("Type: §f")[1];
                    config.updateConfig(bossId, "abilities.list." + abilityId + ".type", type);
                    plugin.getGUIManager().openAbilityEditMenu(player, bossId, abilityId);
                    return;
                }
            }
        }
    }

    private String extractIdFromLore(String lore) {
        if (StringUtils.isEmpty(lore) || !lore.contains("ID:")) return null;
        int idx = lore.lastIndexOf("§f");
        if (idx != -1 && idx + 2 <= lore.length()) {
            return lore.substring(idx + 2);
        }
        return null;
    }
}
