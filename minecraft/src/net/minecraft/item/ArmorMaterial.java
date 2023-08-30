package net.minecraft.item;

import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

/**
 * Defines the material stats of an {@link ArmorItem} item.
 * 
 * <p>
 * To view available vanilla armor materials, visit {@link ArmorMaterials}.
 */
public interface ArmorMaterial {
	/**
	 * Returns the total amount of durability points an {@link ArmorItem} using this {@link ArmorMaterial} has.
	 * 
	 * <p>
	 * The value returned here will set the {@link Item.Settings} max durability option when passed
	 * into {@link ArmorItem#ArmorItem(net.minecraft.item.ArmorMaterial, ArmorItem.Type, Item.Settings)}
	 * if the value was not already specified.
	 * 
	 * @return the total durability an {@link ArmorItem} with this {@link ArmorMaterial} has
	 * 
	 * @param type the {@link ArmorItem.Type} of the {@link Item} with this {@link ArmorMaterial}
	 */
	int getDurability(ArmorItem.Type type);

	/**
	 * Returns the amount of armor protection points offered by an {@link ArmorItem}
	 * using this {@link ArmorMaterial} while it is worn by a player.
	 * 
	 * <p>
	 * The protection value returned here is applied as an {@link net.minecraft.entity.attribute.EntityAttributeModifier}
	 * to a player wearing the {@link ArmorItem} piece via the {@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADDITION} modifier.
	 * 
	 * @return the amount of armor protection points offered by an {@link ArmorItem} with this {@link ArmorMaterial}
	 * 
	 * @param type the {@link ArmorItem.Type} of the {@link Item} with this {@link ArmorMaterial}
	 */
	int getProtection(ArmorItem.Type type);

	/**
	 * Returns the base enchantment value used by {@link ArmorItem} with this material.
	 * 
	 * <p>
	 * By default, {@link ArmorItem} will override {@link Item#getEnchantability()}
	 * and delegate the call back to this method.
	 * 
	 * <p>
	 * A higher return value will result in better enchantment results when using an {@code Enchanting Table}.
	 * The highest enchantability value in vanilla is Netherite, at {@code 37}.
	 * 
	 * @return the enchantment value sent back to {@link Item#getEnchantability()} for armor using this material
	 */
	int getEnchantability();

	/**
	 * @return the {@link SoundEvent} played when a {@link net.minecraft.entity.LivingEntity} equips an {@link ArmorItem} using this {@link ArmorMaterial}
	 */
	SoundEvent getEquipSound();

	/**
	 * Returns the {@link Ingredient} used to repair items using this {@link ArmorMaterial}.
	 * 
	 * <p>
	 * By default, {@link ArmorItem} will delegate {@link Item#canRepair(ItemStack, ItemStack)}
	 * back to this method.
	 * 
	 * @return the {@link Ingredient} required to repair items with this {@link ArmorMaterial}
	 */
	Ingredient getRepairIngredient();

	/**
	 * Returns the {@code name} of this {@link ArmorMaterial}, which is used for locating armor texture files.
	 * 
	 * <p>
	 * The return value of this method should be in {@code snake_case}.
	 * {@link net.minecraft.client.render.entity.feature.ArmorFeatureRenderer} will expect to find an armor
	 * texture file matching {@code minecraft:textures/models/armor/{material_name}_layer_[1/2].png}
	 * based off the return result of this method.
	 * 
	 * <p>
	 * Example: given a return value of {@code cool_material}, {@code ArmorFeatureRenderer} will require a file
	 * at {@code minecraft:textures/models/armor/cool_material_layer_1.png} and {@code minecraft:textures/models/armor/cool_material_layer_2.png}.
	 * 
	 * @return the {@code name} of this armor material in snake_case, used for finding armor textures
	 */
	String getName();

	/**
	 * Returns the toughness value of an {@link ArmorItem} piece using this {@link ArmorMaterial}.
	 * 
	 * <p>
	 * {@link ArmorItem} will cover the value returned here into the {@link net.minecraft.entity.attribute.EntityAttributes#GENERIC_ARMOR_TOUGHNESS}
	 * statistic with the {@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADDITION} modifier type.
	 * 
	 * @return the toughness value of any {@link ArmorItem} using this {@link ArmorMaterial}
	 */
	float getToughness();

	/**
	 * {@return the knockback resistance value of an {@link ArmorItem} piece using this {@link ArmorMaterial}}
	 * 
	 * <p>
	 * {@link ArmorItem} will cover the value returned here into the {@link net.minecraft.entity.attribute.EntityAttributes#GENERIC_KNOCKBACK_RESISTANCE}
	 * statistic with the {@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADDITION} modifier type.
	 */
	float getKnockbackResistance();
}
