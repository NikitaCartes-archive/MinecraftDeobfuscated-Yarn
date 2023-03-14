package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Represents a type of item that is wearable in an armor equipment slot, or a shield.
 * 
 * <p>This type of item can be targeted by the {@code minecraft:binding_curse} enchantment.
 */
public interface Equipment extends Vanishable {
	EquipmentSlot getSlotType();

	default SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
	}

	default TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		ItemStack itemStack2 = user.getEquippedStack(equipmentSlot);
		if (!EnchantmentHelper.hasBindingCurse(itemStack2) && !ItemStack.areEqual(itemStack, itemStack2)) {
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
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	@Nullable
	static Equipment fromStack(ItemStack stack) {
		Item equipment2 = stack.getItem();
		if (equipment2 instanceof Equipment) {
			return (Equipment)equipment2;
		} else {
			if (stack.getItem() instanceof BlockItem blockItem) {
				Block var6 = blockItem.getBlock();
				if (var6 instanceof Equipment) {
					return (Equipment)var6;
				}
			}

			return null;
		}
	}
}