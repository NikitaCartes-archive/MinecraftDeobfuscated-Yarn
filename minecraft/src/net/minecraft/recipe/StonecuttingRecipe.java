package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class StonecuttingRecipe extends CuttingRecipe {
	public StonecuttingRecipe(String group, Ingredient ingredient, Item result, int count) {
		super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTING, group, ingredient, new ItemStack(result, count));
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.ingredient.test(inventory.getStack(0));
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.STONECUTTER);
	}
}
