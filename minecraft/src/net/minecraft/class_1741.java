package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public interface class_1741 {
	int method_7696(EquipmentSlot equipmentSlot);

	int method_7697(EquipmentSlot equipmentSlot);

	int method_7699();

	SoundEvent method_7698();

	Ingredient method_7695();

	@Environment(EnvType.CLIENT)
	String method_7694();

	float method_7700();
}
