package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.util.DefaultedList;

public class CraftingResultInventory implements Inventory, RecipeUnlocker {
	private final DefaultedList<ItemStack> field_7866 = DefaultedList.create(1, ItemStack.EMPTY);
	private Recipe<?> field_7865;

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_7866) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack method_5438(int i) {
		return this.field_7866.get(0);
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		return Inventories.method_5428(this.field_7866, 0);
	}

	@Override
	public ItemStack method_5441(int i) {
		return Inventories.method_5428(this.field_7866, 0);
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		this.field_7866.set(0, itemStack);
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
		this.field_7866.clear();
	}

	@Override
	public void method_7662(@Nullable Recipe<?> recipe) {
		this.field_7865 = recipe;
	}

	@Nullable
	@Override
	public Recipe<?> method_7663() {
		return this.field_7865;
	}
}
