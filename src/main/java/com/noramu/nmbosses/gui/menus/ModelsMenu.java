package com.noramu.nmbosses.gui.menus;

import com.noramu.nmbosses.NmBosses;
import com.noramu.nmbosses.gui.handlers.ConfigHelper;
import com.noramu.nmbosses.integrations.ModelEngineIntegration;
import com.noramu.nmbosses.integrations.MythicMobsIntegration;
import com.noramu.nmbosses.integrations.OraxenIntegration;
import com.noramu.nmbosses.integrations.ItemsAdderIntegration;
import com.noramu.nmbosses.integrations.NexoIntegration;
import com.noramu.nmbosses.utils.GuiConstants;
import com.noramu.nmbosses.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Model plugins menu for boss customization
 */
public class ModelsMenu {

    private final NmBosses plugin;
    private final ConfigHelper configHelper;

    public ModelsMenu(NmBosses plugin) {
        this.plugin = plugin;
        this.configHelper = new ConfigHelper(plugin);
    }

    /**
     * Open models menu
     */
    public Inventory openModelsMenu(String bossId) {
        Inventory inv = Bukkit.createInventory(null, 27, StringUtils.colorize("&6Model Plugins"));

        int slot = 0;

        // ModelEngine
        if (ModelEngineIntegration.isEnabled()) {
            ItemStack modelEngine = new ItemStack(Material.ARMOR_STAND);
            ItemMeta meta = modelEngine.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(StringUtils.colorize("&bModelEngine"));
                List<String> lore = new ArrayList<>();
                lore.add(StringUtils.colorize("&7Custom 3D models"));
                boolean enabled = configHelper.isModelPluginEnabled(bossId, "modelEngine");
                lore.add(StringUtils.colorize(enabled ? "&a✓ Enabled" : "&c✗ Disabled"));
                lore.add("");
                lore.add(StringUtils.colorize("&aClick to configure"));
                meta.setLore(lore);
                modelEngine.setItemMeta(meta);
            }
            inv.setItem(slot, modelEngine);
        }
        slot++;

        // MythicMobs
        if (MythicMobsIntegration.isEnabled()) {
            ItemStack mythicMobs = new ItemStack(Material.DRAGON_EGG);
            ItemMeta meta = mythicMobs.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(StringUtils.colorize("&5MythicMobs"));
                List<String> lore = new ArrayList<>();
                lore.add(StringUtils.colorize("&7Custom mob types & abilities"));
                boolean enabled = configHelper.isModelPluginEnabled(bossId, "mythicMobs");
                lore.add(StringUtils.colorize(enabled ? "&a✓ Enabled" : "&c✗ Disabled"));
                lore.add("");
                lore.add(StringUtils.colorize("&aClick to configure"));
                meta.setLore(lore);
                mythicMobs.setItemMeta(meta);
            }
            inv.setItem(slot, mythicMobs);
        }
        slot++;

        // Oraxen
        if (OraxenIntegration.isEnabled()) {
            ItemStack oraxen = new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta meta = oraxen.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(StringUtils.colorize("&eOraxen"));
                List<String> lore = new ArrayList<>();
                lore.add(StringUtils.colorize("&7Custom items & models"));
                boolean enabled = configHelper.isModelPluginEnabled(bossId, "oraxen");
                lore.add(StringUtils.colorize(enabled ? "&a✓ Enabled" : "&c✗ Disabled"));
                lore.add("");
                lore.add(StringUtils.colorize("&aClick to configure"));
                meta.setLore(lore);
                oraxen.setItemMeta(meta);
            }
            inv.setItem(slot, oraxen);
        }
        slot++;

        // ItemsAdder
        if (ItemsAdderIntegration.isEnabled()) {
            ItemStack itemsAdder = new ItemStack(Material.GOLDEN_APPLE);
            ItemMeta meta = itemsAdder.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(StringUtils.colorize("&6ItemsAdder"));
                List<String> lore = new ArrayList<>();
                lore.add(StringUtils.colorize("&7Custom items & models"));
                boolean enabled = configHelper.isModelPluginEnabled(bossId, "itemsAdder");
                lore.add(StringUtils.colorize(enabled ? "&a✓ Enabled" : "&c✗ Disabled"));
                lore.add("");
                lore.add(StringUtils.colorize("&aClick to configure"));
                meta.setLore(lore);
                itemsAdder.setItemMeta(meta);
            }
            inv.setItem(slot, itemsAdder);
        }
        slot++;

        // Nexo
        if (NexoIntegration.isEnabled()) {
            ItemStack nexo = new ItemStack(Material.AMETHYST_CLUSTER);
            ItemMeta meta = nexo.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(StringUtils.colorize("&dNexo"));
                List<String> lore = new ArrayList<>();
                lore.add(StringUtils.colorize("&7Custom items & models"));
                boolean enabled = configHelper.isModelPluginEnabled(bossId, "nexo");
                lore.add(StringUtils.colorize(enabled ? "&a✓ Enabled" : "&c✗ Disabled"));
                lore.add("");
                lore.add(StringUtils.colorize("&aClick to configure"));
                meta.setLore(lore);
                nexo.setItemMeta(meta);
            }
            inv.setItem(slot, nexo);
        }

        // Back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(StringUtils.colorize(GuiConstants.BTN_BACK));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(26, backButton);

        return inv;
    }

    /**
     * Open ModelEngine configuration menu
     */
    public Inventory openModelEngineConfigMenu(String bossId) {
        Inventory inv = Bukkit.createInventory(null, 27, StringUtils.colorize("&bModelEngine Config"));

        String modelId = configHelper.getModelEngineModel(bossId);
        boolean enabled = configHelper.isModelPluginEnabled(bossId, "modelEngine");

        // Enable/Disable toggle
        ItemStack toggle = new ItemStack(enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE);
        ItemMeta toggleMeta = toggle.getItemMeta();
        if (toggleMeta != null) {
            toggleMeta.setDisplayName(StringUtils.colorize(enabled ? "&a✓ Enabled" : "&c✗ Disabled"));
            List<String> lore = new ArrayList<>();
            lore.add(StringUtils.colorize("&7Click to toggle"));
            toggleMeta.setLore(lore);
            toggle.setItemMeta(toggleMeta);
        }
        inv.setItem(0, toggle);

        // Model ID display
        ItemStack modelDisplay = new ItemStack(Material.ARMOR_STAND);
        ItemMeta modelMeta = modelDisplay.getItemMeta();
        if (modelMeta != null) {
            modelMeta.setDisplayName(StringUtils.colorize("&bModel ID"));
            List<String> lore = new ArrayList<>();
            lore.add(StringUtils.colorize("&7Current: &f" + (modelId.isEmpty() ? "None" : modelId)));
            lore.add("");
            lore.add(StringUtils.colorize("&eClick to edit"));
            modelMeta.setLore(lore);
            modelDisplay.setItemMeta(modelMeta);
        }
        inv.setItem(1, modelDisplay);

        // Back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(StringUtils.colorize(GuiConstants.BTN_BACK));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(26, backButton);

        return inv;
    }

    /**
     * Open MythicMobs configuration menu
     */
    public Inventory openMythicMobsConfigMenu(String bossId) {
        Inventory inv = Bukkit.createInventory(null, 27, StringUtils.colorize("&5MythicMobs Config"));

        String mobType = configHelper.getMythicMobsType(bossId);
        boolean enabled = configHelper.isModelPluginEnabled(bossId, "mythicMobs");

        // Enable/Disable toggle
        ItemStack toggle = new ItemStack(enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE);
        ItemMeta toggleMeta = toggle.getItemMeta();
        if (toggleMeta != null) {
            toggleMeta.setDisplayName(StringUtils.colorize(enabled ? "&a✓ Enabled" : "&c✗ Disabled"));
            List<String> lore = new ArrayList<>();
            lore.add(StringUtils.colorize("&7Click to toggle"));
            toggleMeta.setLore(lore);
            toggle.setItemMeta(toggleMeta);
        }
        inv.setItem(0, toggle);

        // Mob Type display
        ItemStack mobDisplay = new ItemStack(Material.DRAGON_EGG);
        ItemMeta mobMeta = mobDisplay.getItemMeta();
        if (mobMeta != null) {
            mobMeta.setDisplayName(StringUtils.colorize("&5Mob Type"));
            List<String> lore = new ArrayList<>();
            lore.add(StringUtils.colorize("&7Current: &f" + (mobType.isEmpty() ? "None" : mobType)));
            lore.add("");
            lore.add(StringUtils.colorize("&eClick to edit"));
            mobMeta.setLore(lore);
            mobDisplay.setItemMeta(mobMeta);
        }
        inv.setItem(1, mobDisplay);

        // Abilities list
        ItemStack abilitiesList = new ItemStack(Material.BOOK);
        ItemMeta abilitiesMeta = abilitiesList.getItemMeta();
        if (abilitiesMeta != null) {
            abilitiesMeta.setDisplayName(StringUtils.colorize("&5Abilities"));
            List<String> lore = new ArrayList<>();
            List<String> abilities = configHelper.getMythicMobsAbilities(bossId);
            lore.add(StringUtils.colorize("&7Count: &f" + abilities.size()));
            lore.add("");
            lore.add(StringUtils.colorize("&eClick to manage"));
            abilitiesMeta.setLore(lore);
            abilitiesList.setItemMeta(abilitiesMeta);
        }
        inv.setItem(2, abilitiesList);

        // Back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(StringUtils.colorize(GuiConstants.BTN_BACK));
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(26, backButton);

        return inv;
    }
}
