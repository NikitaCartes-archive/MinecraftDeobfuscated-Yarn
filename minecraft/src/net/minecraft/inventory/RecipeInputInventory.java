package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeInputProvider;

public interface RecipeInputInventory extends Inventory, RecipeInputProvider {
	int getWidth();

	int getHeight();

	List<ItemStack> getInputStacks();
}
