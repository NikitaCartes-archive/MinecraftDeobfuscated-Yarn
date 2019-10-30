package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class NameTagItem extends Item {
	public NameTagItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (stack.hasCustomName() && !(entity instanceof PlayerEntity)) {
			if (entity.isAlive()) {
				entity.setCustomName(stack.getName());
				if (entity instanceof MobEntity) {
					((MobEntity)entity).setPersistent();
				}

				stack.decrement(1);
			}

			return true;
		} else {
			return false;
		}
	}
}
