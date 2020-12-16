package net.minecraft.item;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
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

	public static Optional<ActionResult> fillEntityBucket(PlayerEntity player, Hand hand, LivingEntity entity, SoundEvent sound, Supplier<ItemStack> supplier) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == Items.WATER_BUCKET && entity.isAlive()) {
			entity.playSound(sound, 1.0F, 1.0F);
			itemStack.decrement(1);
			ItemStack itemStack2 = (ItemStack)supplier.get();
			World world = entity.world;
			if (!world.isClient) {
				Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)player, itemStack2);
			}

			if (itemStack.isEmpty()) {
				player.setStackInHand(hand, itemStack2);
			} else if (!player.getInventory().insertStack(itemStack2)) {
				player.dropItem(itemStack2, false);
			}

			entity.discard();
			return Optional.of(ActionResult.success(world.isClient));
		} else {
			return Optional.empty();
		}
	}
}
