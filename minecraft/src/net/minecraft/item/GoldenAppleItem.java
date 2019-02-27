package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;

public class GoldenAppleItem extends FoodItem {
	public GoldenAppleItem(int i, float f, boolean bl, Item.Settings settings) {
		super(i, f, bl, settings);
	}

	@Override
	protected void onConsumed(ItemStack itemStack, World world, LivingEntity livingEntity) {
		if (!world.isClient) {
			livingEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5924, 100, 1));
			livingEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5898, 2400, 0));
		}
	}
}
