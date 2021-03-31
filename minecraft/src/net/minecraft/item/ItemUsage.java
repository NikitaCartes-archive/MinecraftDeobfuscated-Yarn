package net.minecraft.item;

import java.util.stream.Stream;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemUsage {
	public static TypedActionResult<ItemStack> consumeHeldItem(World world, PlayerEntity player, Hand hand) {
		player.setCurrentHand(hand);
		return TypedActionResult.consume(player.getStackInHand(hand));
	}

	public static ItemStack exchangeStack(ItemStack inputStack, PlayerEntity player, ItemStack outputStack, boolean creativeOverride) {
		boolean bl = player.getAbilities().creativeMode;
		if (creativeOverride && bl) {
			if (!player.getInventory().contains(outputStack)) {
				player.getInventory().insertStack(outputStack);
			}

			return inputStack;
		} else {
			if (!bl) {
				inputStack.decrement(1);
			}

			if (inputStack.isEmpty()) {
				return outputStack;
			} else {
				if (!player.getInventory().insertStack(outputStack)) {
					player.dropItem(outputStack, false);
				}

				return inputStack;
			}
		}
	}

	public static ItemStack exchangeStack(ItemStack inputStack, PlayerEntity player, ItemStack outputStack) {
		return exchangeStack(inputStack, player, outputStack, true);
	}

	public static void spawnItemContents(ItemEntity itemEntity, Stream<ItemStack> contents) {
		World world = itemEntity.world;
		if (!world.isClient) {
			contents.forEach(stack -> world.spawnEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), stack)));
		}
	}
}
