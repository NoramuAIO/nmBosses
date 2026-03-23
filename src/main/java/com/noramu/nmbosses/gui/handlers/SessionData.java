package com.noramu.nmbosses.gui.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Oyuncu oturum verilerini tutan sınıf
 */
public class SessionData {

    private final Map<UUID, String> editingBoss = new HashMap<>();
    private final Map<UUID, String> waitingForInput = new HashMap<>();
    private final Map<UUID, String> inputType = new HashMap<>();
    private final Map<UUID, String> equipmentSlot = new HashMap<>();
    private final Map<UUID, Integer> editingCommandIndex = new HashMap<>();

    // Getters
    public String getEditingBoss(UUID uuid) { return editingBoss.get(uuid); }
    public String getWaitingForInput(UUID uuid) { return waitingForInput.get(uuid); }
    public String getInputType(UUID uuid) { return inputType.get(uuid); }
    public String getEquipmentSlot(UUID uuid) { return equipmentSlot.get(uuid); }
    public Integer getEditingCommandIndex(UUID uuid) { return editingCommandIndex.get(uuid); }

    // Setters
    public void setEditingBoss(UUID uuid, String bossId) { editingBoss.put(uuid, bossId); }
    public void setWaitingForInput(UUID uuid, String action) { waitingForInput.put(uuid, action); }
    public void setInputType(UUID uuid, String type) { inputType.put(uuid, type); }
    public void setEquipmentSlot(UUID uuid, String slot) { equipmentSlot.put(uuid, slot); }
    public void setEditingCommandIndex(UUID uuid, int index) { editingCommandIndex.put(uuid, index); }

    // Removers
    public String removeWaitingForInput(UUID uuid) { return waitingForInput.remove(uuid); }
    public String removeInputType(UUID uuid) { return inputType.remove(uuid); }
    public String removeEquipmentSlot(UUID uuid) { return equipmentSlot.remove(uuid); }
    public Integer removeEditingCommandIndex(UUID uuid) { return editingCommandIndex.remove(uuid); }

    // Checkers
    public boolean hasWaitingForInput(UUID uuid) { return waitingForInput.containsKey(uuid); }
    public boolean hasEquipmentSlot(UUID uuid) { return equipmentSlot.containsKey(uuid); }
}
