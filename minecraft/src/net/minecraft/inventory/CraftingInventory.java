package net.minecraft.inventory;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.DefaultedList;

public class CraftingInventory implements Inventory, RecipeInputProvider {
	private final DefaultedList<ItemStack> field_7805;
	private final int width;
	private final int height;
	private final Container container;

	public CraftingInventory(Container container, int i, int j) {
		this.field_7805 = DefaultedList.create(i * j, ItemStack.EMPTY);
		this.container = container;
		this.width = i;
		this.height = j;
	}

	@Override
	public int getInvSize() {
		return this.field_7805.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_7805) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack method_5438(int i) {
		return i >= this.getInvSize() ? ItemStack.EMPTY : this.field_7805.get(i);
	}

	@Override
	public ItemStack method_5441(int i) {
		return Inventories.method_5428(this.field_7805, i);
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		ItemStack itemStack = Inventories.method_5430(this.field_7805, i, j);
		if (!itemStack.isEmpty()) {
			this.container.onContentChanged(this);
		}

		return itemStack;
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		this.field_7805.set(i, itemStack);
		this.container.onContentChanged(this);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean method_5443(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public void clear() {
		this.field_7805.clear();
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public void provideRecipeInputs(RecipeFinder recipeFinder) {
		for (ItemStack itemStack : this.field_7805) {
			recipeFinder.method_7404(itemStack);
		}
	}
}
