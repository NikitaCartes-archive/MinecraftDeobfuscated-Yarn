package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemUsage {
	public static TypedActionResult<ItemStack> consumeHeldItem(World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.setCurrentHand(hand);
		return TypedActionResult.consume(playerEntity.getStackInHand(hand));
	}

	public static ItemStack method_30270(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2, boolean bl) {
		boolean bl2 = playerEntity.abilities.creativeMode;
		if (bl && bl2) {
			if (!playerEntity.inventory.contains(itemStack2)) {
				playerEntity.inventory.insertStack(itemStack2);
			}

			return itemStack;
		} else {
			if (!bl2) {
				itemStack.decrement(1);
			}

			if (itemStack.isEmpty()) {
				return itemStack2;
			} else {
				if (!playerEntity.inventory.insertStack(itemStack2)) {
					playerEntity.dropItem(itemStack2, false);
				}

				return itemStack;
			}
		}
	}

	public static ItemStack method_30012(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2) {
		return method_30270(itemStack, playerEntity, itemStack2, true);
	}
}
