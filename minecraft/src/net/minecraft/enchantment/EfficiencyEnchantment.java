package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class EfficiencyEnchantment extends Enchantment {
	protected EfficiencyEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9069, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 1 + 10 * (i - 1);
	}

	@Override
	public int getMaximumPower(int i) {
		return super.getMinimumPower(i) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}

	@Override
	public boolean isAcceptableItem(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8868 ? true : super.isAcceptableItem(itemStack);
	}
}
