package net.minecraft.item.equipment;

import java.util.EnumMap;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

public interface ArmorMaterials {
	ArmorMaterial LEATHER = new ArmorMaterial(5, Util.make(new EnumMap(EquipmentType.class), map -> {
		map.put(EquipmentType.BOOTS, 1);
		map.put(EquipmentType.LEGGINGS, 2);
		map.put(EquipmentType.CHESTPLATE, 3);
		map.put(EquipmentType.HELMET, 1);
		map.put(EquipmentType.BODY, 3);
	}), 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, ItemTags.REPAIRS_LEATHER_ARMOR, EquipmentModels.LEATHER);
	ArmorMaterial CHAIN = new ArmorMaterial(15, Util.make(new EnumMap(EquipmentType.class), map -> {
		map.put(EquipmentType.BOOTS, 1);
		map.put(EquipmentType.LEGGINGS, 4);
		map.put(EquipmentType.CHESTPLATE, 5);
		map.put(EquipmentType.HELMET, 2);
		map.put(EquipmentType.BODY, 4);
	}), 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, ItemTags.REPAIRS_CHAIN_ARMOR, EquipmentModels.CHAINMAIL);
	ArmorMaterial IRON = new ArmorMaterial(15, Util.make(new EnumMap(EquipmentType.class), map -> {
		map.put(EquipmentType.BOOTS, 2);
		map.put(EquipmentType.LEGGINGS, 5);
		map.put(EquipmentType.CHESTPLATE, 6);
		map.put(EquipmentType.HELMET, 2);
		map.put(EquipmentType.BODY, 5);
	}), 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, ItemTags.REPAIRS_IRON_ARMOR, EquipmentModels.IRON);
	ArmorMaterial GOLD = new ArmorMaterial(7, Util.make(new EnumMap(EquipmentType.class), map -> {
		map.put(EquipmentType.BOOTS, 1);
		map.put(EquipmentType.LEGGINGS, 3);
		map.put(EquipmentType.CHESTPLATE, 5);
		map.put(EquipmentType.HELMET, 2);
		map.put(EquipmentType.BODY, 7);
	}), 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F, 0.0F, ItemTags.REPAIRS_GOLD_ARMOR, EquipmentModels.GOLD);
	ArmorMaterial DIAMOND = new ArmorMaterial(33, Util.make(new EnumMap(EquipmentType.class), map -> {
		map.put(EquipmentType.BOOTS, 3);
		map.put(EquipmentType.LEGGINGS, 6);
		map.put(EquipmentType.CHESTPLATE, 8);
		map.put(EquipmentType.HELMET, 3);
		map.put(EquipmentType.BODY, 11);
	}), 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, ItemTags.REPAIRS_DIAMOND_ARMOR, EquipmentModels.DIAMOND);
	ArmorMaterial TURTLE_SCUTE = new ArmorMaterial(25, Util.make(new EnumMap(EquipmentType.class), map -> {
		map.put(EquipmentType.BOOTS, 2);
		map.put(EquipmentType.LEGGINGS, 5);
		map.put(EquipmentType.CHESTPLATE, 6);
		map.put(EquipmentType.HELMET, 2);
		map.put(EquipmentType.BODY, 5);
	}), 9, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, ItemTags.REPAIRS_TURTLE_HELMET, EquipmentModels.TURTLE_SCUTE);
	ArmorMaterial NETHERITE = new ArmorMaterial(37, Util.make(new EnumMap(EquipmentType.class), map -> {
		map.put(EquipmentType.BOOTS, 3);
		map.put(EquipmentType.LEGGINGS, 6);
		map.put(EquipmentType.CHESTPLATE, 8);
		map.put(EquipmentType.HELMET, 3);
		map.put(EquipmentType.BODY, 11);
	}), 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentModels.NETHERITE);
	ArmorMaterial ARMADILLO_SCUTE = new ArmorMaterial(4, Util.make(new EnumMap(EquipmentType.class), map -> {
		map.put(EquipmentType.BOOTS, 3);
		map.put(EquipmentType.LEGGINGS, 6);
		map.put(EquipmentType.CHESTPLATE, 8);
		map.put(EquipmentType.HELMET, 3);
		map.put(EquipmentType.BODY, 11);
	}), 10, SoundEvents.ITEM_ARMOR_EQUIP_WOLF, 0.0F, 0.0F, ItemTags.REPAIRS_WOLF_ARMOR, EquipmentModels.ARMADILLO_SCUTE);
}
