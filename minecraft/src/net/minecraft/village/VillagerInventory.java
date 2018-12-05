package net.minecraft.village;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;

public class VillagerInventory implements Inventory {
	private final Villager villager;
	private final DefaultedList<ItemStack> inventory = DefaultedList.create(3, ItemStack.EMPTY);
	private final PlayerEntity player;
	private VillagerRecipe villagerRecipe;
	private int recipeIndex;

	public VillagerInventory(PlayerEntity playerEntity, Villager villager) {
		this.player = playerEntity;
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
	public TextComponent getName() {
		return new TranslatableTextComponent("mob.villager");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.villager.getCurrentCustomer() == playerEntity;
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
	}

	@Override
	public boolean isValidInvStack(int i, ItemStack itemStack) {
		return true;
	}

	@Override
	public void markDirty() {
		this.updateRecipes();
	}

	public void updateRecipes() {
		this.villagerRecipe = null;
		ItemStack itemStack = this.inventory.get(0);
		ItemStack itemStack2 = this.inventory.get(1);
		if (itemStack.isEmpty()) {
			itemStack = itemStack2;
			itemStack2 = ItemStack.EMPTY;
		}

		if (itemStack.isEmpty()) {
			this.setInvStack(2, ItemStack.EMPTY);
		} else {
			VillagerRecipeList villagerRecipeList = this.villager.getRecipes(this.player);
			if (villagerRecipeList != null) {
				VillagerRecipe villagerRecipe = villagerRecipeList.getValidRecipe(itemStack, itemStack2, this.recipeIndex);
				if (villagerRecipe != null && !villagerRecipe.isDisabled()) {
					this.villagerRecipe = villagerRecipe;
					this.setInvStack(2, villagerRecipe.getSellItem().copy());
				} else if (!itemStack2.isEmpty()) {
					villagerRecipe = villagerRecipeList.getValidRecipe(itemStack2, itemStack, this.recipeIndex);
					if (villagerRecipe != null && !villagerRecipe.isDisabled()) {
						this.villagerRecipe = villagerRecipe;
						this.setInvStack(2, villagerRecipe.getSellItem().copy());
					} else {
						this.setInvStack(2, ItemStack.EMPTY);
					}
				} else {
					this.setInvStack(2, ItemStack.EMPTY);
				}
			}

			this.villager.onSellingItem(this.getInvStack(2));
		}
	}

	public VillagerRecipe getVillagerRecipe() {
		return this.villagerRecipe;
	}

	public void setRecipeIndex(int i) {
		this.recipeIndex = i;
		this.updateRecipes();
	}

	@Override
	public int getInvProperty(int i) {
		return 0;
	}

	@Override
	public void setInvProperty(int i, int j) {
	}

	@Override
	public int getInvPropertyCount() {
		return 0;
	}

	@Override
	public void clearInv() {
		this.inventory.clear();
	}
}
