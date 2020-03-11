package net.minecraft.village;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class TraderInventory implements Inventory {
	private final Trader trader;
	private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	@Nullable
	private TradeOffer traderRecipe;
	private int recipeIndex;
	private int traderRewardedExperience;

	public TraderInventory(Trader trader) {
		this.trader = trader;
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
	public ItemStack getInvStack(int slot) {
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack itemStack = this.inventory.get(slot);
		if (slot == 2 && !itemStack.isEmpty()) {
			return Inventories.splitStack(this.inventory, slot, itemStack.getCount());
		} else {
			ItemStack itemStack2 = Inventories.splitStack(this.inventory, slot, amount);
			if (!itemStack2.isEmpty() && this.needRecipeUpdate(slot)) {
				this.updateRecipes();
			}

			return itemStack2;
		}
	}

	private boolean needRecipeUpdate(int slot) {
		return slot == 0 || slot == 1;
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return Inventories.removeStack(this.inventory, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}

		if (this.needRecipeUpdate(slot)) {
			this.updateRecipes();
		}
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.trader.getCurrentCustomer() == player;
	}

	@Override
	public void markDirty() {
		this.updateRecipes();
	}

	public void updateRecipes() {
		this.traderRecipe = null;
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
			this.traderRewardedExperience = 0;
		} else {
			TraderOfferList traderOfferList = this.trader.getOffers();
			if (!traderOfferList.isEmpty()) {
				TradeOffer tradeOffer = traderOfferList.getValidRecipe(itemStack, itemStack2, this.recipeIndex);
				if (tradeOffer == null || tradeOffer.isDisabled()) {
					this.traderRecipe = tradeOffer;
					tradeOffer = traderOfferList.getValidRecipe(itemStack2, itemStack, this.recipeIndex);
				}

				if (tradeOffer != null && !tradeOffer.isDisabled()) {
					this.traderRecipe = tradeOffer;
					this.setInvStack(2, tradeOffer.getSellItem());
					this.traderRewardedExperience = tradeOffer.getTraderExperience();
				} else {
					this.setInvStack(2, ItemStack.EMPTY);
					this.traderRewardedExperience = 0;
				}
			}

			this.trader.onSellingItem(this.getInvStack(2));
		}
	}

	@Nullable
	public TradeOffer getTradeOffer() {
		return this.traderRecipe;
	}

	public void setRecipeIndex(int index) {
		this.recipeIndex = index;
		this.updateRecipes();
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Environment(EnvType.CLIENT)
	public int getTraderRewardedExperience() {
		return this.traderRewardedExperience;
	}
}
