package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

public class LureEnchantment extends Enchantment {
	protected LureEnchantment(Enchantment.Rarity rarity, TagKey<Item> tagKey, EquipmentSlot... equipmentSlots) {
		super(rarity, tagKey, equipmentSlots);
	}

	@Override
	public int getMinPower(int level) {
		return 15 + (level - 1) * 9;
	}

	@Override
	public int getMaxPower(int level) {
		return super.getMinPower(level) + 50;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
