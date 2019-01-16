package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class BindingCurseEnchantment extends Enchantment {
	public BindingCurseEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.WEARABLE, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 25;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}

	@Override
	public boolean isLootOnly() {
		return true;
	}

	@Override
	public boolean isCursed() {
		return true;
	}
}
