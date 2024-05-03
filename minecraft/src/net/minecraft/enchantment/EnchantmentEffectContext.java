package net.minecraft.enchantment;

import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public record EnchantmentEffectContext(ItemStack stack, @Nullable EquipmentSlot slot, @Nullable LivingEntity owner, Runnable onBreak) {
	public EnchantmentEffectContext(ItemStack stack, EquipmentSlot slot, LivingEntity owner) {
		this(stack, slot, owner, () -> owner.sendEquipmentBreakStatus(slot));
	}
}
