package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.math.MathHelper;

public class BreachEnchantment extends Enchantment {
	public BreachEnchantment() {
		super(
			Enchantment.properties(
				ItemTags.MACE_ENCHANTABLE,
				2,
				4,
				Enchantment.leveledCost(15, 9),
				Enchantment.leveledCost(65, 9),
				4,
				FeatureSet.of(FeatureFlags.UPDATE_1_21),
				EquipmentSlot.MAINHAND
			)
		);
	}

	public static float getFactor(float level, float f) {
		return MathHelper.clamp(f - 0.15F * level, 0.0F, 1.0F);
	}
}
