package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EmptyMapItem extends NetworkSyncedItem {
	public EmptyMapItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = FilledMapItem.createMap(world, MathHelper.floor(user.getX()), MathHelper.floor(user.getZ()), (byte)0, true, false);
		ItemStack itemStack2 = user.getStackInHand(hand);
		if (!user.abilities.creativeMode) {
			itemStack2.decrement(1);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (itemStack2.isEmpty()) {
			return TypedActionResult.success(itemStack);
		} else {
			if (!user.inventory.insertStack(itemStack.copy())) {
				user.dropItem(itemStack, false);
			}

			return TypedActionResult.success(itemStack2);
		}
	}
}
