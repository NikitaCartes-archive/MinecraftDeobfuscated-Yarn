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

	public static ItemStack method_30012(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2) {
		if (playerEntity.abilities.creativeMode) {
			if (!playerEntity.inventory.contains(itemStack2)) {
				playerEntity.inventory.insertStack(itemStack2);
			}

			return itemStack;
		} else {
			itemStack.decrement(1);
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
}
