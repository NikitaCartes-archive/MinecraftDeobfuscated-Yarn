package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
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
public interface Equipment {
	EquipmentSlot getSlotType();

	default RegistryEntry<SoundEvent> getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
	}

	default TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		EquipmentSlot equipmentSlot = user.getPreferredEquipmentSlot(itemStack);
		if (!user.canUseSlot(equipmentSlot)) {
			return TypedActionResult.pass(itemStack);
		} else {
			ItemStack itemStack2 = user.getEquippedStack(equipmentSlot);
			if ((!EnchantmentHelper.hasAnyEnchantmentsWith(itemStack2, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) || user.isCreative())
				&& !ItemStack.areEqual(itemStack, itemStack2)) {
				if (!world.isClient()) {
					user.incrementStat(Stats.USED.getOrCreateStat(item));
				}

				ItemStack itemStack3 = itemStack2.isEmpty() ? itemStack : itemStack2.copyAndEmpty();
				ItemStack itemStack4 = user.isCreative() ? itemStack.copy() : itemStack.copyAndEmpty();
				user.equipStack(equipmentSlot, itemStack4);
				return TypedActionResult.success(itemStack3, world.isClient());
			} else {
				return TypedActionResult.fail(itemStack);
			}
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
