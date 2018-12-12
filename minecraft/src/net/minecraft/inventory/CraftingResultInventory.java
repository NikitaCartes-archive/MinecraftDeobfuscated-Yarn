package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;

public class CraftingResultInventory implements Inventory, RecipeUnlocker {
	private final DefaultedList<ItemStack> stack = DefaultedList.create(1, ItemStack.EMPTY);
	private Recipe lastRecipe;

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.stack) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return this.stack.get(0);
	}

	@Override
	public TextComponent getName() {
		return new StringTextComponent("Result");
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		return InventoryUtil.removeStack(this.stack, 0);
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return InventoryUtil.removeStack(this.stack, 0);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		this.stack.set(0, itemStack);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public void clearInv() {
		this.stack.clear();
	}

	@Override
	public void setLastRecipe(@Nullable Recipe recipe) {
		this.lastRecipe = recipe;
	}

	@Nullable
	@Override
	public Recipe getLastRecipe() {
		return this.lastRecipe;
	}
}
