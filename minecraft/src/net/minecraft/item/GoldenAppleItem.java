package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class GoldenAppleItem extends FoodItem {
	public GoldenAppleItem(int i, float f, boolean bl, Item.Settings settings) {
		super(i, f, bl, settings);
	}

	@Override
	protected void onConsumed(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		if (!world.isRemote) {
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5924, 100, 1));
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5898, 2400, 0));
		}
	}
}
