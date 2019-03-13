package net.minecraft.item;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;

public enum ArmorMaterials implements ArmorMaterial {
	field_7897("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.field_14581, 0.0F, () -> Ingredient.method_8091(Items.field_8745)),
	field_7887("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.field_15191, 0.0F, () -> Ingredient.method_8091(Items.field_8620)),
	field_7892("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.field_14862, 0.0F, () -> Ingredient.method_8091(Items.field_8620)),
	field_7895("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.field_14761, 0.0F, () -> Ingredient.method_8091(Items.field_8695)),
	field_7889("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.field_15103, 2.0F, () -> Ingredient.method_8091(Items.field_8477)),
	field_7890("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEvents.field_14684, 0.0F, () -> Ingredient.method_8091(Items.field_8161));

	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] protectionAmounts;
	private final int enchantability;
	private final SoundEvent field_7886;
	private final float toughness;
	private final Lazy<Ingredient> field_7885;

	private ArmorMaterials(String string2, int j, int[] is, int k, SoundEvent soundEvent, float f, Supplier<Ingredient> supplier) {
		this.name = string2;
		this.durabilityMultiplier = j;
		this.protectionAmounts = is;
		this.enchantability = k;
		this.field_7886 = soundEvent;
		this.toughness = f;
		this.field_7885 = new Lazy<>(supplier);
	}

	@Override
	public int getDurability(EquipmentSlot equipmentSlot) {
		return BASE_DURABILITY[equipmentSlot.getEntitySlotId()] * this.durabilityMultiplier;
	}

	@Override
	public int getProtectionAmount(EquipmentSlot equipmentSlot) {
		return this.protectionAmounts[equipmentSlot.getEntitySlotId()];
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public SoundEvent method_7698() {
		return this.field_7886;
	}

	@Override
	public Ingredient method_7695() {
		return this.field_7885.get();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}
}
