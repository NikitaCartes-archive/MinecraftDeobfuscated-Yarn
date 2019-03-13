package net.minecraft.village;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public class TraderInventory implements Inventory {
	private final Trader field_7844;
	private final DefaultedList<ItemStack> field_7845 = DefaultedList.create(3, ItemStack.EMPTY);
	@Nullable
	private TraderRecipe field_7843;
	private int recipeIndex;
	private int field_18668;

	public TraderInventory(Trader trader) {
		this.field_7844 = trader;
	}

	@Override
	public int getInvSize() {
		return this.field_7845.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_7845) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack method_5438(int i) {
		return this.field_7845.get(i);
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		ItemStack itemStack = this.field_7845.get(i);
		if (i == 2 && !itemStack.isEmpty()) {
			return Inventories.method_5430(this.field_7845, i, itemStack.getAmount());
		} else {
			ItemStack itemStack2 = Inventories.method_5430(this.field_7845, i, j);
			if (!itemStack2.isEmpty() && this.needRecipeUpdate(i)) {
				this.updateRecipes();
			}

			return itemStack2;
		}
	}

	private boolean needRecipeUpdate(int i) {
		return i == 0 || i == 1;
	}

	@Override
	public ItemStack method_5441(int i) {
		return Inventories.method_5428(this.field_7845, i);
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		this.field_7845.set(i, itemStack);
		if (!itemStack.isEmpty() && itemStack.getAmount() > this.getInvMaxStackAmount()) {
			itemStack.setAmount(this.getInvMaxStackAmount());
		}

		if (this.needRecipeUpdate(i)) {
			this.updateRecipes();
		}
	}

	@Override
	public boolean method_5443(PlayerEntity playerEntity) {
		return this.field_7844.getCurrentCustomer() == playerEntity;
	}

	@Override
	public void markDirty() {
		this.updateRecipes();
	}

	public void updateRecipes() {
		this.field_7843 = null;
		ItemStack itemStack;
		ItemStack itemStack2;
		if (this.field_7845.get(0).isEmpty()) {
			itemStack = this.field_7845.get(1);
			itemStack2 = ItemStack.EMPTY;
		} else {
			itemStack = this.field_7845.get(0);
			itemStack2 = this.field_7845.get(1);
		}

		if (itemStack.isEmpty()) {
			this.method_5447(2, ItemStack.EMPTY);
			this.field_18668 = 0;
		} else {
			TraderRecipeList traderRecipeList = this.field_7844.method_8264();
			if (!traderRecipeList.isEmpty()) {
				TraderRecipe traderRecipe = traderRecipeList.getValidRecipe(itemStack, itemStack2, this.recipeIndex);
				if (traderRecipe == null || traderRecipe.isDisabled()) {
					this.field_7843 = traderRecipe;
					traderRecipe = traderRecipeList.getValidRecipe(itemStack2, itemStack, this.recipeIndex);
				}

				if (traderRecipe != null && !traderRecipe.isDisabled()) {
					this.field_7843 = traderRecipe;
					this.method_5447(2, traderRecipe.getSellItem());
					this.field_18668 = traderRecipe.getSellItem().getAmount();
				} else {
					this.method_5447(2, ItemStack.EMPTY);
					this.field_18668 = 0;
				}
			}

			this.field_7844.onSellingItem(this.method_5438(2));
		}
	}

	@Nullable
	public TraderRecipe method_7642() {
		return this.field_7843;
	}

	public void setRecipeIndex(int i) {
		this.recipeIndex = i;
		this.updateRecipes();
	}

	@Override
	public void clear() {
		this.field_7845.clear();
	}

	@Environment(EnvType.CLIENT)
	public int method_19252() {
		return this.field_18668;
	}
}
