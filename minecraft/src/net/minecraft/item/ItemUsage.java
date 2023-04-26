package net.minecraft.item;

import java.util.stream.Stream;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Utility methods related to item usage.
 */
public class ItemUsage {
	/**
	 * Consumes the item that {@code player} holds. This should be called inside
	 * {@link Item#use} for consumable items, such as milk bucket.
	 * 
	 * @return the action result of consuming
	 */
	public static TypedActionResult<ItemStack> consumeHeldItem(World world, PlayerEntity player, Hand hand) {
		player.setCurrentHand(hand);
		return TypedActionResult.consume(player.getStackInHand(hand));
	}

	/**
	 * {@return the stack to put in the player's hand after exchanging stacks}
	 * 
	 * <p>Exchanging stacks causes the input stack to be decremented and the output stack to
	 * be inserted to the player's inventory (or dropped if it cannot be inserted.)
	 * For example, milking a cow exchanges one empty bucket and one milk bucket.
	 * If {@code creativeOverride} is {@code true} and the player is in creative mode,
	 * the player only receives the new stack when they do not have the output stack.
	 * 
	 * @see #exchangeStack(ItemStack, PlayerEntity, ItemStack)
	 */
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

	/**
	 * {@return the stack to put in the player's hand after exchanging stacks}
	 * 
	 * <p>Exchanging stacks causes the input stack to be decremented and the output stack to
	 * be inserted to the player's inventory (or dropped if it cannot be inserted.)
	 * For example, milking a cow exchanges one empty bucket and one milk bucket.
	 * If the player is in creative mode, the player only receives the new stack when
	 * they do not have the output stack.
	 * 
	 * @see #exchangeStack(ItemStack, PlayerEntity, ItemStack, boolean)
	 */
	public static ItemStack exchangeStack(ItemStack inputStack, PlayerEntity player, ItemStack outputStack) {
		return exchangeStack(inputStack, player, outputStack, true);
	}

	/**
	 * Spawns item entity's contents at the item entity's position. This should be
	 * called inside {@link Item#onItemEntityDestroyed} for items that hold other items,
	 * such as shulker boxes.
	 * 
	 * @see Item#onItemEntityDestroyed
	 */
	public static void spawnItemContents(ItemEntity itemEntity, Stream<ItemStack> contents) {
		World world = itemEntity.getWorld();
		if (!world.isClient) {
			contents.forEach(stack -> world.spawnEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), stack)));
		}
	}
}
