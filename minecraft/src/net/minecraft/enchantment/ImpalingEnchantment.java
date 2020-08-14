package net.minecraft.enchantment;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class ImpalingEnchantment extends Enchantment {
	public ImpalingEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.TRIDENT, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 1 + (level - 1) * 8;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 20;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public float getAttackDamage(int level, LivingEntity livingEntity) {
		return livingEntity == null || livingEntity.getGroup() != EntityGroup.AQUATIC && !livingEntity.isTouchingWaterOrRain() ? 0.0F : (float)level * 2.5F;
	}
}
