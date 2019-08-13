package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EmptyMapItem extends NetworkSyncedItem {
	public EmptyMapItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = FilledMapItem.createMap(world, MathHelper.floor(playerEntity.x), MathHelper.floor(playerEntity.z), (byte)0, true, false);
		ItemStack itemStack2 = playerEntity.getStackInHand(hand);
		if (!playerEntity.abilities.creativeMode) {
			itemStack2.decrement(1);
		}

		if (itemStack2.isEmpty()) {
			return new TypedActionResult<>(ActionResult.field_5812, itemStack);
		} else {
			if (!playerEntity.inventory.insertStack(itemStack.copy())) {
				playerEntity.dropItem(itemStack, false);
			}

			playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
			return new TypedActionResult<>(ActionResult.field_5812, itemStack2);
		}
	}
}
