package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class DensityEnchantment extends Enchantment {
	public DensityEnchantment() {
		super(
			Enchantment.properties(
				ItemTags.MACE_ENCHANTABLE,
				10,
				5,
				Enchantment.leveledCost(1, 11),
				Enchantment.leveledCost(21, 11),
				1,
				FeatureSet.of(FeatureFlags.UPDATE_1_21),
				EquipmentSlot.MAINHAND
			)
		);
	}

	public static float getDamage(int level, float fallDistance) {
		return fallDistance * (float)level;
	}
}
