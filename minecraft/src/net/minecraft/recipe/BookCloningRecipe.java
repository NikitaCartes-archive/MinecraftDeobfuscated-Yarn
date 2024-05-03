package net.minecraft.recipe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BookCloningRecipe extends SpecialCraftingRecipe {
	public BookCloningRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < craftingRecipeInput.getSize(); j++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.isOf(Items.WRITTEN_BOOK)) {
					if (!itemStack.isEmpty()) {
						return false;
					}

					itemStack = itemStack2;
				} else {
					if (!itemStack2.isOf(Items.WRITABLE_BOOK)) {
						return false;
					}

					i++;
				}
			}
		}

		return !itemStack.isEmpty() && i > 0;
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < craftingRecipeInput.getSize(); j++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.isOf(Items.WRITTEN_BOOK)) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2;
				} else {
					if (!itemStack2.isOf(Items.WRITABLE_BOOK)) {
						return ItemStack.EMPTY;
					}

					i++;
				}
			}
		}

		WrittenBookContentComponent writtenBookContentComponent = itemStack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
		if (!itemStack.isEmpty() && i >= 1 && writtenBookContentComponent != null) {
			WrittenBookContentComponent writtenBookContentComponent2 = writtenBookContentComponent.copy();
			if (writtenBookContentComponent2 == null) {
				return ItemStack.EMPTY;
			} else {
				ItemStack itemStack3 = itemStack.copyWithCount(i);
				itemStack3.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, writtenBookContentComponent2);
				return itemStack3;
			}
		} else {
			return ItemStack.EMPTY;
		}
	}

	public DefaultedList<ItemStack> getRemainder(CraftingRecipeInput craftingRecipeInput) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingRecipeInput.getSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
			if (itemStack.getItem().hasRecipeRemainder()) {
				defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
			} else if (itemStack.getItem() instanceof WrittenBookItem) {
				defaultedList.set(i, itemStack.copyWithCount(1));
				break;
			}
		}

		return defaultedList;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.BOOK_CLONING;
	}

	@Override
	public boolean fits(int width, int height) {
		return width >= 3 && height >= 3;
	}
}
