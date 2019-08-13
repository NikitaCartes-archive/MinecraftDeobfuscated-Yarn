package net.minecraft.enchantment;

import net.minecraft.entity.EntityGroup;
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
	public float getAttackDamage(int i, EntityGroup entityGroup) {
		return entityGroup == EntityGroup.AQUATIC ? (float)i * 2.5F : 0.0F;
	}
}
