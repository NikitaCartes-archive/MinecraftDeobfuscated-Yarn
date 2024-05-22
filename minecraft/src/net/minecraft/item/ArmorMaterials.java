package net.minecraft.item;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ArmorMaterials {
	public static final RegistryEntry<ArmorMaterial> LEATHER = register(
		"leather",
		Util.make(new EnumMap(ArmorItem.Type.class), map -> {
			map.put(ArmorItem.Type.BOOTS, 1);
			map.put(ArmorItem.Type.LEGGINGS, 2);
			map.put(ArmorItem.Type.CHESTPLATE, 3);
			map.put(ArmorItem.Type.HELMET, 1);
			map.put(ArmorItem.Type.BODY, 3);
		}),
		15,
		SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
		0.0F,
		0.0F,
		() -> Ingredient.ofItems(Items.LEATHER),
		List.of(new ArmorMaterial.Layer(Identifier.ofVanilla("leather"), "", true), new ArmorMaterial.Layer(Identifier.ofVanilla("leather"), "_overlay", false))
	);
	public static final RegistryEntry<ArmorMaterial> CHAIN = register("chainmail", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 1);
		map.put(ArmorItem.Type.LEGGINGS, 4);
		map.put(ArmorItem.Type.CHESTPLATE, 5);
		map.put(ArmorItem.Type.HELMET, 2);
		map.put(ArmorItem.Type.BODY, 4);
	}), 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.IRON_INGOT));
	public static final RegistryEntry<ArmorMaterial> IRON = register("iron", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 2);
		map.put(ArmorItem.Type.LEGGINGS, 5);
		map.put(ArmorItem.Type.CHESTPLATE, 6);
		map.put(ArmorItem.Type.HELMET, 2);
		map.put(ArmorItem.Type.BODY, 5);
	}), 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.IRON_INGOT));
	public static final RegistryEntry<ArmorMaterial> GOLD = register("gold", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 1);
		map.put(ArmorItem.Type.LEGGINGS, 3);
		map.put(ArmorItem.Type.CHESTPLATE, 5);
		map.put(ArmorItem.Type.HELMET, 2);
		map.put(ArmorItem.Type.BODY, 7);
	}), 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.GOLD_INGOT));
	public static final RegistryEntry<ArmorMaterial> DIAMOND = register("diamond", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 11);
	}), 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> Ingredient.ofItems(Items.DIAMOND));
	public static final RegistryEntry<ArmorMaterial> TURTLE = register("turtle", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 2);
		map.put(ArmorItem.Type.LEGGINGS, 5);
		map.put(ArmorItem.Type.CHESTPLATE, 6);
		map.put(ArmorItem.Type.HELMET, 2);
		map.put(ArmorItem.Type.BODY, 5);
	}), 9, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.TURTLE_SCUTE));
	public static final RegistryEntry<ArmorMaterial> NETHERITE = register("netherite", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 11);
	}), 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));
	public static final RegistryEntry<ArmorMaterial> ARMADILLO = register("armadillo", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 11);
	}), 10, SoundEvents.ITEM_ARMOR_EQUIP_WOLF, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.ARMADILLO_SCUTE));

	public static RegistryEntry<ArmorMaterial> getDefault(Registry<ArmorMaterial> registry) {
		return LEATHER;
	}

	private static RegistryEntry<ArmorMaterial> register(
		String id,
		EnumMap<ArmorItem.Type, Integer> defense,
		int enchantability,
		RegistryEntry<SoundEvent> equipSound,
		float toughness,
		float knockbackResistance,
		Supplier<Ingredient> repairIngredient
	) {
		List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(Identifier.ofVanilla(id)));
		return register(id, defense, enchantability, equipSound, toughness, knockbackResistance, repairIngredient, list);
	}

	private static RegistryEntry<ArmorMaterial> register(
		String id,
		EnumMap<ArmorItem.Type, Integer> defense,
		int enchantability,
		RegistryEntry<SoundEvent> equipSound,
		float toughness,
		float knockbackResistance,
		Supplier<Ingredient> repairIngredient,
		List<ArmorMaterial.Layer> layers
	) {
		EnumMap<ArmorItem.Type, Integer> enumMap = new EnumMap(ArmorItem.Type.class);

		for (ArmorItem.Type type : ArmorItem.Type.values()) {
			enumMap.put(type, (Integer)defense.get(type));
		}

		return Registry.registerReference(
			Registries.ARMOR_MATERIAL,
			Identifier.ofVanilla(id),
			new ArmorMaterial(enumMap, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResistance)
		);
	}
}
