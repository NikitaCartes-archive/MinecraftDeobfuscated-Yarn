package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChorusFruitItem extends Item {
	public ChorusFruitItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		ItemStack itemStack2 = super.finishUsing(itemStack, world, livingEntity);
		if (!world.isClient) {
			double d = livingEntity.getX();
			double e = livingEntity.getY();
			double f = livingEntity.getZ();

			for (int i = 0; i < 16; i++) {
				double g = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
				double h = MathHelper.clamp(livingEntity.getY() + (double)(livingEntity.getRandom().nextInt(16) - 8), 0.0, (double)(world.getEffectiveHeight() - 1));
				double j = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
				if (livingEntity.hasVehicle()) {
					livingEntity.stopRiding();
				}

				if (livingEntity.teleport(g, h, j, true)) {
					world.playSound(null, d, e, f, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
					livingEntity.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
					break;
				}
			}

			if (livingEntity instanceof PlayerEntity) {
				((PlayerEntity)livingEntity).getItemCooldownManager().set(this, 20);
			}
		}

		return itemStack2;
	}
}
