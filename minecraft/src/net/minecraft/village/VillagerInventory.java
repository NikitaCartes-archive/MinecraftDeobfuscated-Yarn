package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;

public class VillagerInventory implements Inventory {
	private final Villager villager;
	private final DefaultedList<ItemStack> inventory = DefaultedList.create(3, ItemStack.EMPTY);
	@Nullable
	private VillagerRecipe villagerRecipe;
	private int recipeIndex;

	public VillagerInventory(Villager villager) {
		this.villager = villager;
	}

	@Override
	public int getInvSize() {
		return this.inventory.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return this.inventory.get(i);
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		ItemStack itemStack = this.inventory.get(i);
		if (i == 2 && !itemStack.isEmpty()) {
			return InventoryUtil.splitStack(this.inventory, i, itemStack.getAmount());
		} else {
			ItemStack itemStack2 = InventoryUtil.splitStack(this.inventory, i, j);
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
	public ItemStack removeInvStack(int i) {
		return InventoryUtil.removeStack(this.inventory, i);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		this.inventory.set(i, itemStack);
		if (!itemStack.isEmpty() && itemStack.getAmount() > this.getInvMaxStackAmount()) {
			itemStack.setAmount(this.getInvMaxStackAmount());
		}

		if (this.needRecipeUpdate(i)) {
			this.updateRecipes();
		}
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.villager.getCurrentCustomer() == playerEntity;
	}

	@Override
	public void markDirty() {
		this.updateRecipes();
	}

	public void updateRecipes() {
		this.villagerRecipe = null;
		ItemStack itemStack;
		ItemStack itemStack2;
		if (this.inventory.get(0).isEmpty()) {
			itemStack = this.inventory.get(1);
			itemStack2 = ItemStack.EMPTY;
		} else {
			itemStack = this.inventory.get(0);
			itemStack2 = this.inventory.get(1);
		}

		if (itemStack.isEmpty()) {
			this.setInvStack(2, ItemStack.EMPTY);
		} else {
			VillagerRecipeList villagerRecipeList = this.villager.getRecipes();
			if (!villagerRecipeList.isEmpty()) {
				VillagerRecipe villagerRecipe = villagerRecipeList.getValidRecipe(itemStack, itemStack2, this.recipeIndex);
				if (villagerRecipe == null || villagerRecipe.isDisabled()) {
					villagerRecipe = villagerRecipeList.getValidRecipe(itemStack2, itemStack, this.recipeIndex);
				}

				if (villagerRecipe != null && !villagerRecipe.isDisabled()) {
					this.villagerRecipe = villagerRecipe;
					this.setInvStack(2, villagerRecipe.method_18019());
				} else {
					this.setInvStack(2, ItemStack.EMPTY);
				}
			}

			this.villager.onSellingItem(this.getInvStack(2));
		}
	}

	@Nullable
	public VillagerRecipe getVillagerRecipe() {
		return this.villagerRecipe;
	}

	public void setRecipeIndex(int i) {
		this.recipeIndex = i;
		this.updateRecipes();
	}

	@Override
	public void clearInv() {
		this.inventory.clear();
	}
}
