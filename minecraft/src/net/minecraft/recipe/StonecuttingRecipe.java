package net.minecraft.recipe;

import net.minecraft.class_3972;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StonecuttingRecipe extends class_3972 {
	public StonecuttingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack) {
		super(RecipeType.field_17641, RecipeSerializer.field_17640, identifier, string, ingredient, itemStack);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.input.matches(inventory.getInvStack(0));
	}
}
