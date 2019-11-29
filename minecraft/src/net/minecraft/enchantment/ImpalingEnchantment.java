package net.minecraft.enchantment;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class ImpalingEnchantment extends Enchantment {
	public ImpalingEnchantment(Enchantment.Weight weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.TRIDENT, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 1 + (level - 1) * 8;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 20;
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}

	@Override
	public float getAttackDamage(int level, LivingEntity livingEntity) {
		return livingEntity == null || livingEntity.getGroup() != EntityGroup.AQUATIC && !livingEntity.isInsideWaterOrRain() ? 0.0F : (float)level * 2.5F;
	}
}
