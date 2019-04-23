/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

public enum EquipmentSlot {
    MAINHAND(Type.HAND, 0, 0, "mainhand"),
    OFFHAND(Type.HAND, 1, 5, "offhand"),
    FEET(Type.ARMOR, 0, 1, "feet"),
    LEGS(Type.ARMOR, 1, 2, "legs"),
    CHEST(Type.ARMOR, 2, 3, "chest"),
    HEAD(Type.ARMOR, 3, 4, "head");

    private final Type type;
    private final int entityId;
    private final int armorStandId;
    private final String name;

    private EquipmentSlot(Type type, int j, int k, String string2) {
        this.type = type;
        this.entityId = j;
        this.armorStandId = k;
        this.name = string2;
    }

    public Type getType() {
        return this.type;
    }

    public int getEntitySlotId() {
        return this.entityId;
    }

    public int getArmorStandSlotId() {
        return this.armorStandId;
    }

    public String getName() {
        return this.name;
    }

    public static EquipmentSlot byName(String string) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (!equipmentSlot.getName().equals(string)) continue;
            return equipmentSlot;
        }
        throw new IllegalArgumentException("Invalid slot '" + string + "'");
    }

    public static EquipmentSlot fromTypeIndex(Type type, int i) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() != type || equipmentSlot.getEntitySlotId() != i) continue;
            return equipmentSlot;
        }
        throw new IllegalArgumentException("Invalid slot '" + (Object)((Object)type) + "': " + i);
    }

    public static enum Type {
        HAND,
        ARMOR;

    }
}

