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
	public ItemStack method_7861(ItemStack itemStack, World world, LivingEntity livingEntity) {
		ItemStack itemStack2 = super.method_7861(itemStack, world, livingEntity);
		if (!world.isClient) {
			double d = livingEntity.x;
			double e = livingEntity.y;
			double f = livingEntity.z;

			for (int i = 0; i < 16; i++) {
				double g = livingEntity.x + (livingEntity.getRand().nextDouble() - 0.5) * 16.0;
				double h = MathHelper.clamp(livingEntity.y + (double)(livingEntity.getRand().nextInt(16) - 8), 0.0, (double)(world.getEffectiveHeight() - 1));
				double j = livingEntity.z + (livingEntity.getRand().nextDouble() - 0.5) * 16.0;
				if (livingEntity.hasVehicle()) {
					livingEntity.stopRiding();
				}

				if (livingEntity.method_6082(g, h, j, true)) {
					world.method_8465(null, d, e, f, SoundEvents.field_14890, SoundCategory.field_15248, 1.0F, 1.0F);
					livingEntity.method_5783(SoundEvents.field_14890, 1.0F, 1.0F);
					break;
				}
			}

			if (livingEntity instanceof PlayerEntity) {
				((PlayerEntity)livingEntity).method_7357().set(this, 20);
			}
		}

		return itemStack2;
	}
}
