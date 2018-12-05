package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface Recipe {
	boolean matches(Inventory inventory, World world);

	ItemStack craft(Inventory inventory);

	@Environment(EnvType.CLIENT)
	boolean fits(int i, int j);

	ItemStack getOutput();

	default DefaultedList<ItemStack> getRemainingStacks(Inventory inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.create(inventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			Item item = inventory.getInvStack(i).getItem();
			if (item.hasContainerItem()) {
				defaultedList.set(i, new ItemStack(item.getContainerItem()));
			}
		}

		return defaultedList;
	}

	default DefaultedList<Ingredient> getPreviewInputs() {
		return DefaultedList.create();
	}

	default boolean isIgnoredInRecipeBook() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	default String getGroup() {
		return "";
	}

	Identifier getId();

	RecipeSerializer<?> getSerializer();
}
