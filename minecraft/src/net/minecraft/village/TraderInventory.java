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
	private final Trader trader;
	private final DefaultedList<ItemStack> inventory = DefaultedList.create(3, ItemStack.EMPTY);
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
	public ItemStack getInvStack(int i) {
		return this.inventory.get(i);
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		ItemStack itemStack = this.inventory.get(i);
		if (i == 2 && !itemStack.isEmpty()) {
			return Inventories.splitStack(this.inventory, i, itemStack.getAmount());
		} else {
			ItemStack itemStack2 = Inventories.splitStack(this.inventory, i, j);
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
		return Inventories.removeStack(this.inventory, i);
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
		return this.trader.getCurrentCustomer() == playerEntity;
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

	public void setRecipeIndex(int i) {
		this.recipeIndex = i;
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
