package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;

public class SaddleItem extends Item {
	public SaddleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (entity instanceof Saddleable && entity.isAlive()) {
			Saddleable saddleable = (Saddleable)entity;
			if (!saddleable.isSaddled() && saddleable.canBeSaddled()) {
				if (!user.getWorld().isClient) {
					saddleable.saddle(SoundCategory.NEUTRAL);
					entity.getWorld().emitGameEvent(entity, GameEvent.EQUIP, entity.getPos());
					stack.decrement(1);
				}

				return ActionResult.success(user.getWorld().isClient);
			}
		}

		return ActionResult.PASS;
	}
}
