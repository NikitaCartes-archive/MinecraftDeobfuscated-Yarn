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
	field_7897("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.field_14581, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.field_8745)),
	CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.field_15191, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.field_8620)),
	field_7892("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.field_14862, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.field_8620)),
	field_7895("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.field_14761, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.field_8695)),
	field_7889("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.field_15103, 2.0F, 0.0F, () -> Ingredient.ofItems(Items.field_8477)),
	field_7890("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEvents.field_14684, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.field_8161)),
	field_21977("netherite", 37, new int[]{3, 6, 8, 3}, 15, SoundEvents.field_21866, 3.0F, 0.1F, () -> Ingredient.ofItems(Items.field_22020));

	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] protectionAmounts;
	private final int enchantability;
	private final SoundEvent equipSound;
	private final float toughness;
	private final float knockbackResistance;
	private final Lazy<Ingredient> repairIngredientSupplier;

	private ArmorMaterials(
		String name,
		int durabilityMultiplier,
		int[] protectionAmounts,
		int enchantability,
		SoundEvent equipSound,
		float toughness,
		float knockbackResistance,
		Supplier<Ingredient> repairIngredientSupplier
	) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.protectionAmounts = protectionAmounts;
		this.enchantability = enchantability;
		this.equipSound = equipSound;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairIngredientSupplier = new Lazy<>(repairIngredientSupplier);
	}

	@Override
	public int getDurability(EquipmentSlot slot) {
		return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
	}

	@Override
	public int getProtectionAmount(EquipmentSlot slot) {
		return this.protectionAmounts[slot.getEntitySlotId()];
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.equipSound;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredientSupplier.get();
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

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
