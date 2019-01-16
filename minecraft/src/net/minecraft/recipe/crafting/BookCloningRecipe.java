package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BookCloningRecipe extends SpecialCraftingRecipe {
	public BookCloningRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_17706(CraftingInventory craftingInventory, World world) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < craftingInventory.getInvSize(); j++) {
			ItemStack itemStack2 = craftingInventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() == Items.field_8360) {
					if (!itemStack.isEmpty()) {
						return false;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.field_8674) {
						return false;
					}

					i++;
				}
			}
		}

		return !itemStack.isEmpty() && itemStack.hasTag() && i > 0;
	}

	public ItemStack method_17705(CraftingInventory craftingInventory) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < craftingInventory.getInvSize(); j++) {
			ItemStack itemStack2 = craftingInventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() == Items.field_8360) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.field_8674) {
						return ItemStack.EMPTY;
					}

					i++;
				}
			}
		}

		if (!itemStack.isEmpty() && itemStack.hasTag() && i >= 1 && WrittenBookItem.getBookGeneration(itemStack) < 2) {
			ItemStack itemStack3 = new ItemStack(Items.field_8360, i);
			CompoundTag compoundTag = itemStack.getTag().copy();
			compoundTag.putInt("generation", WrittenBookItem.getBookGeneration(itemStack) + 1);
			itemStack3.setTag(compoundTag);
			return itemStack3;
		} else {
			return ItemStack.EMPTY;
		}
	}

	public DefaultedList<ItemStack> method_17707(CraftingInventory craftingInventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.create(craftingInventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = craftingInventory.getInvStack(i);
			if (itemStack.getItem().hasRecipeRemainder()) {
				defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
			} else if (itemStack.getItem() instanceof WrittenBookItem) {
				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setAmount(1);
				defaultedList.set(i, itemStack2);
				break;
			}
		}

		return defaultedList;
	}

	@Override
	public RecipeSerializer<?> method_8119() {
		return RecipeSerializer.field_9029;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i >= 3 && j >= 3;
	}
}
