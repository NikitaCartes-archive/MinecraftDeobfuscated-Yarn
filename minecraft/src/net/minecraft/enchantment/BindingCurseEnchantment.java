package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BindingCurseEnchantment extends Enchantment {
	public BindingCurseEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.WEARABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 25;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public boolean isCursed() {
		return true;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return !stack.isOf(Items.SHIELD) && super.isAcceptableItem(stack);
	}
}
