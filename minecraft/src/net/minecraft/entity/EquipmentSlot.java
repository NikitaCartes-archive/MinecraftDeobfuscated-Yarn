package net.minecraft.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;

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
public enum EquipmentSlot implements StringIdentifiable {
	MAINHAND(EquipmentSlot.Type.HAND, 0, 0, "mainhand"),
	OFFHAND(EquipmentSlot.Type.HAND, 1, 5, "offhand"),
	FEET(EquipmentSlot.Type.HUMANOID_ARMOR, 0, 1, 1, "feet"),
	LEGS(EquipmentSlot.Type.HUMANOID_ARMOR, 1, 1, 2, "legs"),
	CHEST(EquipmentSlot.Type.HUMANOID_ARMOR, 2, 1, 3, "chest"),
	HEAD(EquipmentSlot.Type.HUMANOID_ARMOR, 3, 1, 4, "head"),
	BODY(EquipmentSlot.Type.ANIMAL_ARMOR, 0, 1, 6, "body");

	public static final int NO_MAX_COUNT = 0;
	public static final StringIdentifiable.EnumCodec<EquipmentSlot> CODEC = StringIdentifiable.createCodec(EquipmentSlot::values);
	private final EquipmentSlot.Type type;
	private final int entityId;
	private final int maxCount;
	private final int armorStandId;
	private final String name;

	private EquipmentSlot(final EquipmentSlot.Type type, final int entityId, final int maxCount, final int armorStandId, final String name) {
		this.type = type;
		this.entityId = entityId;
		this.maxCount = maxCount;
		this.armorStandId = armorStandId;
		this.name = name;
	}

	private EquipmentSlot(final EquipmentSlot.Type type, final int entityId, final int armorStandId, final String name) {
		this(type, entityId, 0, armorStandId, name);
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

	public ItemStack split(ItemStack stack) {
		return this.maxCount > 0 ? stack.split(this.maxCount) : stack;
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
		return this.type == EquipmentSlot.Type.HUMANOID_ARMOR || this.type == EquipmentSlot.Type.ANIMAL_ARMOR;
	}

	@Override
	public String asString() {
		return this.name;
	}

	/**
	 * {@return the slot where {@linkplain #getName the name} is equal to {@code name}}
	 * If no slot matching the input name is found, this throws {@link IllegalArgumentException}.
	 * 
	 * @throws IllegalArgumentException if no slot type could be found matching {@code name}
	 */
	public static EquipmentSlot byName(String name) {
		EquipmentSlot equipmentSlot = (EquipmentSlot)CODEC.byId(name);
		if (equipmentSlot != null) {
			return equipmentSlot;
		} else {
			throw new IllegalArgumentException("Invalid slot '" + name + "'");
		}
	}

	/**
	 * The type of body item slot an {@link EquipmentSlot} targets.
	 */
	public static enum Type {
		HAND,
		HUMANOID_ARMOR,
		ANIMAL_ARMOR;
	}
}
