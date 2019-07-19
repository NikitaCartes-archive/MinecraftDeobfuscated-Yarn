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
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack itemStack = super.finishUsing(stack, world, user);
		if (!world.isClient) {
			double d = user.x;
			double e = user.y;
			double f = user.z;

			for (int i = 0; i < 16; i++) {
				double g = user.x + (user.getRandom().nextDouble() - 0.5) * 16.0;
				double h = MathHelper.clamp(user.y + (double)(user.getRandom().nextInt(16) - 8), 0.0, (double)(world.getEffectiveHeight() - 1));
				double j = user.z + (user.getRandom().nextDouble() - 0.5) * 16.0;
				if (user.hasVehicle()) {
					user.stopRiding();
				}

				if (user.teleport(g, h, j, true)) {
					world.playSound(null, d, e, f, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
					user.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
					break;
				}
			}

			if (user instanceof PlayerEntity) {
				((PlayerEntity)user).getItemCooldownManager().set(this, 20);
			}
		}

		return itemStack;
	}
}
