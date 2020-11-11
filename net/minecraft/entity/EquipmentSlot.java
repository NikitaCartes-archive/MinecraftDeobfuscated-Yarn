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

    private EquipmentSlot(Type type, int entityId, int armorStandId, String name) {
        this.type = type;
        this.entityId = entityId;
        this.armorStandId = armorStandId;
        this.name = name;
    }

    public Type getType() {
        return this.type;
    }

    public int getEntitySlotId() {
        return this.entityId;
    }

    public int method_32320(int i) {
        return i + this.entityId;
    }

    public int getArmorStandSlotId() {
        return this.armorStandId;
    }

    public String getName() {
        return this.name;
    }

    public static EquipmentSlot byName(String name) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (!equipmentSlot.getName().equals(name)) continue;
            return equipmentSlot;
        }
        throw new IllegalArgumentException("Invalid slot '" + name + "'");
    }

    public static EquipmentSlot fromTypeIndex(Type type, int index) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() != type || equipmentSlot.getEntitySlotId() != index) continue;
            return equipmentSlot;
        }
        throw new IllegalArgumentException("Invalid slot '" + (Object)((Object)type) + "': " + index);
    }

    public static enum Type {
        HAND,
        ARMOR;

    }
}

