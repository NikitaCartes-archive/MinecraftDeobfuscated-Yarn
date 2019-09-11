package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.village.SimpleTrader;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderOfferList;

public class MerchantContainer extends Container {
	private final Trader trader;
	private final TraderInventory traderInventory;
	@Environment(EnvType.CLIENT)
	private int levelProgress;
	@Environment(EnvType.CLIENT)
	private boolean levelled;
	@Environment(EnvType.CLIENT)
	private boolean canRefreshTrades;

	public MerchantContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, new SimpleTrader(playerInventory.player));
	}

	public MerchantContainer(int i, PlayerInventory playerInventory, Trader trader) {
		super(ContainerType.MERCHANT, i);
		this.trader = trader;
		this.traderInventory = new TraderInventory(trader);
		this.addSlot(new Slot(this.traderInventory, 0, 136, 37));
		this.addSlot(new Slot(this.traderInventory, 1, 162, 37));
		this.addSlot(new TradeOutputSlot(playerInventory.player, trader, this.traderInventory, 2, 220, 37));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 108 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 108 + j * 18, 142));
		}
	}

	@Environment(EnvType.CLIENT)
	public void setCanLevel(boolean bl) {
		this.levelled = bl;
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.traderInventory.updateRecipes();
		super.onContentChanged(inventory);
	}

	public void setRecipeIndex(int i) {
		this.traderInventory.setRecipeIndex(i);
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.trader.getCurrentCustomer() == playerEntity;
	}

	@Environment(EnvType.CLIENT)
	public int getExperience() {
		return this.trader.getExperience();
	}

	@Environment(EnvType.CLIENT)
	public int getTraderRewardedExperience() {
		return this.traderInventory.getTraderRewardedExperience();
	}

	@Environment(EnvType.CLIENT)
	public void setExperienceFromServer(int i) {
		this.trader.setExperienceFromServer(i);
	}

	@Environment(EnvType.CLIENT)
	public int getLevelProgress() {
		return this.levelProgress;
	}

	@Environment(EnvType.CLIENT)
	public void setLevelProgress(int i) {
		this.levelProgress = i;
	}

	@Environment(EnvType.CLIENT)
	public void setRefreshTrades(boolean bl) {
		this.canRefreshTrades = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean canRefreshTrades() {
		return this.canRefreshTrades;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
		return false;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
				this.playYesSound();
			} else if (i != 0 && i != 1) {
				if (i >= 3 && i < 30) {
					if (!this.insertItem(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 30 && i < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(playerEntity, itemStack2);
		}

		return itemStack;
	}

	private void playYesSound() {
		if (!this.trader.getTraderWorld().isClient) {
			Entity entity = (Entity)this.trader;
			this.trader.getTraderWorld().playSound(entity.x, entity.y, entity.z, this.trader.getYesSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
		}
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.trader.setCurrentCustomer(null);
		if (!this.trader.getTraderWorld().isClient) {
			if (!playerEntity.isAlive() || playerEntity instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerEntity).method_14239()) {
				ItemStack itemStack = this.traderInventory.removeInvStack(0);
				if (!itemStack.isEmpty()) {
					playerEntity.dropItem(itemStack, false);
				}

				itemStack = this.traderInventory.removeInvStack(1);
				if (!itemStack.isEmpty()) {
					playerEntity.dropItem(itemStack, false);
				}
			} else {
				playerEntity.inventory.offerOrDrop(playerEntity.world, this.traderInventory.removeInvStack(0));
				playerEntity.inventory.offerOrDrop(playerEntity.world, this.traderInventory.removeInvStack(1));
			}
		}
	}

	public void switchTo(int i) {
		if (this.getRecipes().size() > i) {
			ItemStack itemStack = this.traderInventory.getInvStack(0);
			if (!itemStack.isEmpty()) {
				if (!this.insertItem(itemStack, 3, 39, true)) {
					return;
				}

				this.traderInventory.setInvStack(0, itemStack);
			}

			ItemStack itemStack2 = this.traderInventory.getInvStack(1);
			if (!itemStack2.isEmpty()) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return;
				}

				this.traderInventory.setInvStack(1, itemStack2);
			}

			if (this.traderInventory.getInvStack(0).isEmpty() && this.traderInventory.getInvStack(1).isEmpty()) {
				ItemStack itemStack3 = ((TradeOffer)this.getRecipes().get(i)).getAdjustedFirstBuyItem();
				this.autofill(0, itemStack3);
				ItemStack itemStack4 = ((TradeOffer)this.getRecipes().get(i)).getSecondBuyItem();
				this.autofill(1, itemStack4);
			}
		}
	}

	private void autofill(int i, ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			for (int j = 3; j < 39; j++) {
				ItemStack itemStack2 = ((Slot)this.slotList.get(j)).getStack();
				if (!itemStack2.isEmpty() && this.equals(itemStack, itemStack2)) {
					ItemStack itemStack3 = this.traderInventory.getInvStack(i);
					int k = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
					int l = Math.min(itemStack.getMaxCount() - k, itemStack2.getCount());
					ItemStack itemStack4 = itemStack2.copy();
					int m = k + l;
					itemStack2.decrement(l);
					itemStack4.setCount(m);
					this.traderInventory.setInvStack(i, itemStack4);
					if (m >= itemStack.getMaxCount()) {
						break;
					}
				}
			}
		}
	}

	private boolean equals(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(itemStack, itemStack2);
	}

	@Environment(EnvType.CLIENT)
	public void setOffers(TraderOfferList traderOfferList) {
		this.trader.setOffersFromServer(traderOfferList);
	}

	public TraderOfferList getRecipes() {
		return this.trader.getOffers();
	}

	@Environment(EnvType.CLIENT)
	public boolean isLevelled() {
		return this.levelled;
	}
}
