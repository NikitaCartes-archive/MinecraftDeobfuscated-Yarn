package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;

/**
 * Defines the material stats of a {@link net.minecraft.item.ToolItem} item.
 * <p>
 * To view available vanilla tool materials, visit {@link net.minecraft.item.ToolMaterials}.
 */
public interface ToolMaterial {
	/**
	 * {@return the total amount of durability a {@link net.minecraft.item.ToolItem} using this {@link ToolMaterial} has}
	 * <p>
	 * The value returned here will set the {@link net.minecraft.item.Item.Settings} max durability option when passed
	 * into {@link net.minecraft.item.ToolItem#ToolItem(net.minecraft.item.ToolMaterial, net.minecraft.item.Item.Settings)}
	 * if the value was not already specified.
	 */
	int getDurability();

	/**
	 * {@return the mining speed bonus applied when a {@link net.minecraft.item.ToolItem} using this material is breaking an appropriate block}
	 * {@code 1.0f} will result in no speed change.
	 */
	float getMiningSpeedMultiplier();

	/**
	 * {@return the attack damage bonus applied to any {@link net.minecraft.item.ToolItem} using this {@link ToolMaterial}}
	 * <p>
	 * In the case of {@link net.minecraft.item.MiningToolItem} or {@link net.minecraft.item.SwordItem}, the value returned
	 * here will be added on top of the {@code attackDamage} value passed into the tool's constructor.
	 */
	float getAttackDamage();

	TagKey<Block> getInverseTag();

	/**
	 * {@return the enchantment value sent back to {@link net.minecraft.item.Item#getEnchantability()} for tools using this material}
	 * <p>
	 * By default, {@link ToolMaterial} will override {@link net.minecraft.item.Item#getEnchantability()}
	 * and delegate the call back to this method.
	 * <p>
	 * A higher return value will result in better enchantment results when using an {@code Enchanting Table}.
	 * The highest enchantability value in vanilla is Netherite, at {@code 37}.
	 */
	int getEnchantability();

	/**
	 * {@return the {@link Ingredient} used to repair items using this {@link ToolMaterial}}
	 * <p>
	 * By default, {@link net.minecraft.item.ToolMaterial} will delegate {@link net.minecraft.item.Item#canRepair(ItemStack, ItemStack)}
	 * back to this method.
	 */
	Ingredient getRepairIngredient();

	default ToolComponent createComponent(TagKey<Block> tag) {
		return new ToolComponent(
			List.of(ToolComponent.Rule.ofNeverDropping(this.getInverseTag()), ToolComponent.Rule.ofAlwaysDropping(tag, this.getMiningSpeedMultiplier())), 1.0F, 1
		);
	}
}
