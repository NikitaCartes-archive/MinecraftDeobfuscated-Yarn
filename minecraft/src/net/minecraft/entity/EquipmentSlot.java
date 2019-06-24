package net.minecraft.entity;

public enum EquipmentSlot {
	MAINHAND(EquipmentSlot.Type.HAND, 0, 0, "mainhand"),
	OFFHAND(EquipmentSlot.Type.HAND, 1, 5, "offhand"),
	FEET(EquipmentSlot.Type.ARMOR, 0, 1, "feet"),
	LEGS(EquipmentSlot.Type.ARMOR, 1, 2, "legs"),
	CHEST(EquipmentSlot.Type.ARMOR, 2, 3, "chest"),
	HEAD(EquipmentSlot.Type.ARMOR, 3, 4, "head");

	private final EquipmentSlot.Type type;
	private final int entityId;
	private final int armorStandId;
	private final String name;

	private EquipmentSlot(EquipmentSlot.Type type, int j, int k, String string2) {
		this.type = type;
		this.entityId = j;
		this.armorStandId = k;
		this.name = string2;
	}

	public EquipmentSlot.Type getType() {
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
		for (EquipmentSlot equipmentSlot : values()) {
			if (equipmentSlot.getName().equals(string)) {
				return equipmentSlot;
			}
		}

		throw new IllegalArgumentException("Invalid slot '" + string + "'");
	}

	public static EquipmentSlot fromTypeIndex(EquipmentSlot.Type type, int i) {
		for (EquipmentSlot equipmentSlot : values()) {
			if (equipmentSlot.getType() == type && equipmentSlot.getEntitySlotId() == i) {
				return equipmentSlot;
			}
		}

		throw new IllegalArgumentException("Invalid slot '" + type + "': " + i);
	}

	public static enum Type {
		HAND,
		ARMOR;
	}
}
