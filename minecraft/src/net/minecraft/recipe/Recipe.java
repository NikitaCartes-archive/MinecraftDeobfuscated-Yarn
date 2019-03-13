package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface Recipe<C extends Inventory> {
	boolean method_8115(C inventory, World world);

	ItemStack craft(C inventory);

	@Environment(EnvType.CLIENT)
	boolean fits(int i, int j);

	ItemStack getOutput();

	default DefaultedList<ItemStack> method_8111(C inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.create(inventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			Item item = inventory.method_5438(i).getItem();
			if (item.hasRecipeRemainder()) {
				defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
			}
		}

		return defaultedList;
	}

	default DefaultedList<Ingredient> method_8117() {
		return DefaultedList.create();
	}

	default boolean isIgnoredInRecipeBook() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	default String getGroup() {
		return "";
	}

	@Environment(EnvType.CLIENT)
	default ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.field_9980);
	}

	Identifier method_8114();

	RecipeSerializer<?> method_8119();

	RecipeType<?> method_17716();
}
