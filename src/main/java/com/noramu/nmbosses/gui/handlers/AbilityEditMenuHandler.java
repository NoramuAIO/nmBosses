package com.noramu.nmbosses.gui.handlers;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class AbilityEditMenuHandler {

    private final NmBosses plugin;
    private final SessionData session;
    private final ConfigHelper config;

    public AbilityEditMenuHandler(NmBosses plugin, ConfigHelper config, SessionData session) {
        this.plugin = plugin;
        this.config = config;
        this.session = session;
    }

    public void handle(Player player, ItemStack clicked, String abilityId, ClickType click) {
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
}
