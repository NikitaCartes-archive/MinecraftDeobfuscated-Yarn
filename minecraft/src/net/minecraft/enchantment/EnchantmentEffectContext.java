package net.minecraft.enchantment;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public record EnchantmentEffectContext(ItemStack stack, @Nullable EquipmentSlot slot, @Nullable LivingEntity owner, Consumer<Item> breakCallback) {
	public EnchantmentEffectContext(ItemStack stack, EquipmentSlot slot, LivingEntity owner) {
		this(stack, slot, owner, item -> owner.sendEquipmentBreakStatus(item, slot));
	}
}
