package net.minecraft.enchantment;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;

public class ImpalingEnchantment extends Enchantment {
	public ImpalingEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.TRIDENT_ENCHANTABLE, slotTypes);
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
	public float getAttackDamage(int level, @Nullable EntityType<?> entityType) {
		return entityType != null && entityType.isIn(EntityTypeTags.AQUATIC) ? (float)level * 2.5F : 0.0F;
	}
}
