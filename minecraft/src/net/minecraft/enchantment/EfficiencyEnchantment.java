package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class EfficiencyEnchantment extends Enchantment {
	protected EfficiencyEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.DIGGER, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 1 + 10 * (level - 1);
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() == Items.SHEARS ? true : super.isAcceptableItem(stack);
	}
}
