package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class SaddleItem extends Item {
	public SaddleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean useOnEntity(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		if (livingEntity instanceof PigEntity) {
			PigEntity pigEntity = (PigEntity)livingEntity;
			if (pigEntity.isAlive() && !pigEntity.isSaddled() && !pigEntity.isBaby()) {
				pigEntity.setSaddled(true);
				pigEntity.world.playSound(playerEntity, pigEntity.x, pigEntity.y, pigEntity.z, SoundEvents.field_14824, SoundCategory.field_15254, 0.5F, 1.0F);
				itemStack.decrement(1);
			}

			return true;
		} else {
			return false;
		}
	}
}
