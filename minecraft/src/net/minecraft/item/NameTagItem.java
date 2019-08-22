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
	public boolean useOnEntity(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		if (itemStack.hasCustomName() && !(livingEntity instanceof PlayerEntity)) {
			if (livingEntity.isAlive()) {
				livingEntity.setCustomName(itemStack.getName());
				if (livingEntity instanceof MobEntity) {
					((MobEntity)livingEntity).setPersistent();
				}

				itemStack.decrement(1);
			}

			return true;
		} else {
			return false;
		}
	}
}
