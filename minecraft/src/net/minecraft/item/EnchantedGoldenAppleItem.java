package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class EnchantedGoldenAppleItem extends FoodItem {
	public EnchantedGoldenAppleItem(int i, float f, boolean bl, Item.Settings settings) {
		super(i, f, bl, settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasEnchantmentGlint(ItemStack itemStack) {
		return true;
	}

	@Override
	protected void onConsumed(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		if (!world.isClient) {
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5924, 400, 1));
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5907, 6000, 0));
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5918, 6000, 0));
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5898, 2400, 3));
		}
	}
}
