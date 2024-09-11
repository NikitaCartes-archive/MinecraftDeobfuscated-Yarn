package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EmptyMapItem extends Item {
	public EmptyMapItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			itemStack.decrementUnlessCreative(1, user);
			user.incrementStat(Stats.USED.getOrCreateStat(this));
			user.getWorld().playSoundFromEntity(null, user, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, user.getSoundCategory(), 1.0F, 1.0F);
			ItemStack itemStack2 = FilledMapItem.createMap(world, user.getBlockX(), user.getBlockZ(), (byte)0, true, false);
			if (itemStack.isEmpty()) {
				return ActionResult.SUCCESS.withNewHandStack(itemStack2);
			} else {
				if (!user.getInventory().insertStack(itemStack2.copy())) {
					user.dropItem(itemStack2, false);
				}

				return ActionResult.SUCCESS;
			}
		}
	}
}
