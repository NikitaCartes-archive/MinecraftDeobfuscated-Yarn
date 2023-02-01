package net.minecraft.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Represents a type of item that is wearable in an armor equipment slot.
 * 
 * <p>This type of item can be targeted by the {@code minecraft:binding_curse} enchantment.
 */
public interface Wearable extends Vanishable {
	default TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		ItemStack itemStack2 = user.getEquippedStack(equipmentSlot);
		if (ItemStack.areEqual(itemStack, itemStack2)) {
			return TypedActionResult.fail(itemStack);
		} else {
			user.equipStack(equipmentSlot, itemStack.copy());
			if (!world.isClient()) {
				user.incrementStat(Stats.USED.getOrCreateStat(item));
			}

			if (itemStack2.isEmpty()) {
				itemStack.setCount(0);
			} else {
				user.setStackInHand(hand, itemStack2.copy());
			}

			return TypedActionResult.success(itemStack, world.isClient());
		}
	}
}
