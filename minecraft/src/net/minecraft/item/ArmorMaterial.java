package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public interface ArmorMaterial {
	int getDurability(EquipmentSlot equipmentSlot);

	int getProtectionAmount(EquipmentSlot equipmentSlot);

	int getEnchantability();

	SoundEvent method_7698();

	Ingredient method_7695();

	@Environment(EnvType.CLIENT)
	String getName();

	float getToughness();
}
