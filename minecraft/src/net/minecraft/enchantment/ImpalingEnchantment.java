package net.minecraft.enchantment;

import net.minecraft.class_1310;
import net.minecraft.entity.EquipmentSlot;

public class ImpalingEnchantment extends Enchantment {
	public ImpalingEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9073, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 1 + (i - 1) * 8;
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + 20;
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}

	@Override
	public float getAttackDamage(int i, class_1310 arg) {
		return arg == class_1310.field_6292 ? (float)i * 2.5F : 0.0F;
	}
}
