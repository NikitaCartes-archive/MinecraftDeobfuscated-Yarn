package net.minecraft.item;

import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class SaddleItem extends Item {
	public SaddleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (entity instanceof ItemSteerable) {
			ItemSteerable itemSteerable = (ItemSteerable)entity;
			if (entity.isAlive() && !itemSteerable.isSaddled() && !entity.isBaby()) {
				itemSteerable.setSaddled(true);
				entity.world.playSound(user, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
				stack.decrement(1);
				return true;
			}
		}

		return false;
	}
}
