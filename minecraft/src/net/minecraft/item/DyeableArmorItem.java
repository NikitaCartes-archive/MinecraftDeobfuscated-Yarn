package net.minecraft.item;

import net.minecraft.entity.EquipmentSlot;

public class DyeableArmorItem extends ArmorItem implements DyeableItem {
	public DyeableArmorItem(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Item.Settings settings) {
		super(armorMaterial, equipmentSlot, settings);
	}
}
