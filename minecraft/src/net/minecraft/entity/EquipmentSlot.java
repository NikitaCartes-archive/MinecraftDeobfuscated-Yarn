package net.minecraft.entity;

/**
 * Provides enum types for several key slots found within an entity {@link net.minecraft.inventory.Inventory}.
 * <p>
 * Each equipment slot has a type, which represents what inventory category it is contained within.
 * The {@code HAND} category covers the mainhand and offhand slots, while the {@code ARMOR} category covers the 4
 * types of armor slots found in {@link net.minecraft.entity.LivingEntity}.
 * <p>
 * Each equipment slot contains information on where that slot should be located within a parent {@link net.minecraft.inventory.Inventory}.
 * {@link #getEntitySlotId()} will provide the base slot index a slot should occupy (starting from {@code 0}),
 * while {@link #getOffsetEntitySlotId(int)} will return the same value added to an offset index.
 * <p>
 * An equipment slot can be used to quickly access the item held by an inventory slot in a {@link LivingEntity} through
 * methods such as {@link LivingEntity#getEquippedStack(EquipmentSlot)}, which will return the {@link net.minecraft.item.ItemStack}
 * held in the entity's inventory slot pointed at by the target slot.
 */
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

	private EquipmentSlot(EquipmentSlot.Type type, int entityId, int armorStandId, String name) {
		this.type = type;
		this.entityId = entityId;
		this.armorStandId = armorStandId;
		this.name = name;
	}

	/**
	 * {@return the target {@link EquipmentSlot.Type} that this slot targets}
	 * 
	 * <p>
	 * An equipment slot either targets the hand or body type, which can be used to determine whether a request
	 * to manipulate slot data on an entity should be applied to an armor inventory or general item inventory.
	 */
	public EquipmentSlot.Type getType() {
		return this.type;
	}

	/**
	 * {@return the index of the inventory slot this slot should occupy}
	 * 
	 * <p>
	 * In the case of {@link #MAINHAND} and {@link #OFFHAND}, this method will return 0 and 1, respectively.
	 * The remaining armor slots re-start at index 0 and end at index 3.
	 * 
	 * <p>
	 * To calculate the target index of an inventory slot for a slot relative to the offset index of an entire
	 * inventory, visit {@link #getOffsetEntitySlotId(int)}.
	 */
	public int getEntitySlotId() {
		return this.entityId;
	}

	/**
	 * {@return the index of the inventory slot this slot  should occupy, plus the passed in {@code offset} amount}
	 */
	public int getOffsetEntitySlotId(int offset) {
		return offset + this.entityId;
	}

	/**
	 * {@return the index of the inventory slot this slot occupies in an {@link net.minecraft.entity.decoration.ArmorStandEntity}}
	 */
	public int getArmorStandSlotId() {
		return this.armorStandId;
	}

	/**
	 * {@return the unique name of this equipment slot}
	 * 
	 * <p>The returned value will be a lower-case string (such as "chest" for {@link #CHEST}).
	 */
	public String getName() {
		return this.name;
	}

	public boolean isArmorSlot() {
		return this.type == EquipmentSlot.Type.ARMOR;
	}

	/**
	 * {@return the slot where {@linkplain #getName the name} is equal to {@code name}}
	 * If no slot matching the input name is found, this throws {@link IllegalArgumentException}.
	 * 
	 * @throws IllegalArgumentException if no slot type could be found matching {@code name}
	 */
	public static EquipmentSlot byName(String name) {
		for (EquipmentSlot equipmentSlot : values()) {
			if (equipmentSlot.getName().equals(name)) {
				return equipmentSlot;
			}
		}

		throw new IllegalArgumentException("Invalid slot '" + name + "'");
	}

	/**
	 * {@return the equipment slot where {@linkplain #getEntitySlotId() the slot ID} is equal to {@code index} and the type of the slot is equal to {@code type}}
	 * If no slot could be found matching the input {@code type} and {@code index}, throws {@link IllegalArgumentException}.
	 * 
	 * @throws IllegalArgumentException if no slot type could be found matching {@code type} and {@code index}
	 */
	public static EquipmentSlot fromTypeIndex(EquipmentSlot.Type type, int index) {
		for (EquipmentSlot equipmentSlot : values()) {
			if (equipmentSlot.getType() == type && equipmentSlot.getEntitySlotId() == index) {
				return equipmentSlot;
			}
		}

		throw new IllegalArgumentException("Invalid slot '" + type + "': " + index);
	}

	/**
	 * The type of body item slot an {@link EquipmentSlot} targets.
	 */
	public static enum Type {
		HAND,
		ARMOR;
	}
}
