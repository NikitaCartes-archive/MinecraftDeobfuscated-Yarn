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
		super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTING, id, group, input, output);
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return this.input.test(inv.getInvStack(0));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.STONECUTTER);
	}
}
