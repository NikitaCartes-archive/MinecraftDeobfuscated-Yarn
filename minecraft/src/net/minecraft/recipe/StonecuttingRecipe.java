package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StonecuttingRecipe extends CuttingRecipe {
	public StonecuttingRecipe(Identifier id, String group, Ingredient input, ItemStack output) {
		super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTING, id, group, input, output);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.input.test(inventory.getStack(0));
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.STONECUTTER);
	}
}
