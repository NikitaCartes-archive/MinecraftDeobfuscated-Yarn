package net.minecraft.item;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;

public class ArmorItem extends Item {
	public ArmorItem(ArmorMaterial material, EquipmentType type, Item.Settings settings) {
		super(material.applySettings(settings, type));
	}
}
