package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChorusFruitItem extends Item {
	public ChorusFruitItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity livingEntity) {
		ItemStack itemStack = super.finishUsing(stack, world, livingEntity);
		if (!world.isClient) {
			double d = livingEntity.getX();
			double e = livingEntity.getY();
			double f = livingEntity.getZ();

			for (int i = 0; i < 16; i++) {
				double g = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
				double h = MathHelper.clamp(
					livingEntity.getY() + (double)(livingEntity.getRandom().nextInt(16) - 8),
					(double)world.getSectionCount(),
					(double)(world.getSectionCount() + ((ServerWorld)world).getHeightLimit() - 1)
				);
				double j = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
				if (livingEntity.hasVehicle()) {
					livingEntity.stopRiding();
				}

				if (livingEntity.teleport(g, h, j, true)) {
					SoundEvent soundEvent = livingEntity instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
					world.playSound(null, d, e, f, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
					livingEntity.playSound(soundEvent, 1.0F, 1.0F);
					break;
				}
			}

			if (livingEntity instanceof PlayerEntity) {
				((PlayerEntity)livingEntity).getItemCooldownManager().set(this, 20);
			}
		}

		return itemStack;
	}
}
