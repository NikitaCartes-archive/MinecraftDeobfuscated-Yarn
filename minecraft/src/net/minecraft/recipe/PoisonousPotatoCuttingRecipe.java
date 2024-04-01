package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PoisonousPotatoCuttingRecipe extends CuttingRecipe {
	public PoisonousPotatoCuttingRecipe(String group, Ingredient ingredient, ItemStack result) {
		super(RecipeType.POISONOUS_POTATO_CUTTING, RecipeSerializer.POISONOUS_POTATO_CUTTING, group, ingredient, result);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.ingredient.test(inventory.getStack(0));
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.POISONOUS_POTATO_CUTTER);
	}
}
