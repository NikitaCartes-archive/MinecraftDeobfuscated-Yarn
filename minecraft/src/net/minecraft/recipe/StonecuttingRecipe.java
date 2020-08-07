package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StonecuttingRecipe extends CuttingRecipe {
	public StonecuttingRecipe(Identifier id, String group, Ingredient input, ItemStack output) {
		super(RecipeType.field_17641, RecipeSerializer.field_17640, id, group, input, output);
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return this.input.method_8093(inv.getStack(0));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.field_16335);
	}
}
