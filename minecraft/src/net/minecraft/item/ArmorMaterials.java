package net.minecraft.item;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1741;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.LazyCachedSupplier;

public enum ArmorMaterials implements class_1741 {
	field_7897("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.field_14581, 0.0F, () -> Ingredient.ofItems(Items.field_8745)),
	field_7887("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.field_15191, 0.0F, () -> Ingredient.ofItems(Items.field_8620)),
	field_7892("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.field_14862, 0.0F, () -> Ingredient.ofItems(Items.field_8620)),
	field_7895("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.field_14761, 0.0F, () -> Ingredient.ofItems(Items.field_8695)),
	field_7889("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.field_15103, 2.0F, () -> Ingredient.ofItems(Items.field_8477)),
	field_7890("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEvents.field_14684, 0.0F, () -> Ingredient.ofItems(Items.field_8161));

	private static final int[] field_7891 = new int[]{13, 15, 16, 11};
	private final String name;
	private final int field_7883;
	private final int[] protectionAmounts;
	private final int field_7896;
	private final SoundEvent equipSound;
	private final float toughness;
	private final LazyCachedSupplier<Ingredient> repairIngredientSupplier;

	private ArmorMaterials(String string2, int j, int[] is, int k, SoundEvent soundEvent, float f, Supplier<Ingredient> supplier) {
		this.name = string2;
		this.field_7883 = j;
		this.protectionAmounts = is;
		this.field_7896 = k;
		this.equipSound = soundEvent;
		this.toughness = f;
		this.repairIngredientSupplier = new LazyCachedSupplier<>(supplier);
	}

	@Override
	public int method_7696(EquipmentSlot equipmentSlot) {
		return field_7891[equipmentSlot.getEntitySlotId()] * this.field_7883;
	}

	@Override
	public int method_7697(EquipmentSlot equipmentSlot) {
		return this.protectionAmounts[equipmentSlot.getEntitySlotId()];
	}

	@Override
	public int method_7699() {
		return this.field_7896;
	}

	@Override
	public SoundEvent method_7698() {
		return this.equipSound;
	}

	@Override
	public Ingredient method_7695() {
		return this.repairIngredientSupplier.get();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_7694() {
		return this.name;
	}

	@Override
	public float method_7700() {
		return this.toughness;
	}
}
