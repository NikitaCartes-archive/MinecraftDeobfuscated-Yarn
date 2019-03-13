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
	public boolean method_7847(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		if (itemStack.hasDisplayName() && !(livingEntity instanceof PlayerEntity)) {
			if (livingEntity.isValid()) {
				livingEntity.method_5665(itemStack.method_7964());
				if (livingEntity instanceof MobEntity) {
					((MobEntity)livingEntity).setPersistent();
				}

				itemStack.subtractAmount(1);
			}

			return true;
		} else {
			return false;
		}
	}
}
