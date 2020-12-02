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
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
		ItemStack itemStack = super.finishUsing(stack, world, entity);
		if (!world.isClient) {
			double d = entity.getX();
			double e = entity.getY();
			double f = entity.getZ();

			for (int i = 0; i < 16; i++) {
				double g = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
				double h = MathHelper.clamp(
					entity.getY() + (double)(entity.getRandom().nextInt(16) - 8),
					(double)world.getSectionCount(),
					(double)(world.getSectionCount() + ((ServerWorld)world).getHeightLimit() - 1)
				);
				double j = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
				if (entity.hasVehicle()) {
					entity.stopRiding();
				}

				if (entity.teleport(g, h, j, true)) {
					SoundEvent soundEvent = entity instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
					world.playSound(null, d, e, f, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
					entity.playSound(soundEvent, 1.0F, 1.0F);
					break;
				}
			}

			if (entity instanceof PlayerEntity) {
				((PlayerEntity)entity).getItemCooldownManager().set(this, 20);
			}
		}

		return itemStack;
	}
}
