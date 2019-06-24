package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StonecuttingRecipe extends CuttingRecipe {
	public StonecuttingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack) {
		super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTING, identifier, string, ingredient, itemStack);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.ingredient.method_8093(inventory.getInvStack(0));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.STONECUTTER);
	}
}
