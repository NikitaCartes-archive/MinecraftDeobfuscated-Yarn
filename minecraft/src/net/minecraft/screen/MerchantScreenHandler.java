package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.village.SimpleTrader;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderOfferList;

public class MerchantScreenHandler extends ScreenHandler {
	private final Trader trader;
	private final TraderInventory traderInventory;
	@Environment(EnvType.CLIENT)
	private int levelProgress;
	@Environment(EnvType.CLIENT)
	private boolean leveled;
	@Environment(EnvType.CLIENT)
	private boolean canRefreshTrades;

	public MerchantScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleTrader(playerInventory.player));
	}

	public MerchantScreenHandler(int syncId, PlayerInventory playerInventory, Trader trader) {
		super(ScreenHandlerType.MERCHANT, syncId);
		this.trader = trader;
		this.traderInventory = new TraderInventory(trader);
		this.addSlot(new Slot(this.traderInventory, 0, 136, 37));
		this.addSlot(new Slot(this.traderInventory, 1, 162, 37));
		this.addSlot(new TradeOutputSlot(playerInventory.player, trader, this.traderInventory, 2, 220, 37));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 108 + i * 18, 142));
		}
	}

	@Environment(EnvType.CLIENT)
	public void setCanLevel(boolean canLevel) {
		this.leveled = canLevel;
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.traderInventory.updateRecipes();
		super.onContentChanged(inventory);
	}

	public void setRecipeIndex(int index) {
		this.traderInventory.setRecipeIndex(index);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.trader.getCurrentCustomer() == player;
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
	public void setExperienceFromServer(int experience) {
		this.trader.setExperienceFromServer(experience);
	}

	@Environment(EnvType.CLIENT)
	public int getLevelProgress() {
		return this.levelProgress;
	}

	@Environment(EnvType.CLIENT)
	public void setLevelProgress(int progress) {
		this.levelProgress = progress;
	}

	@Environment(EnvType.CLIENT)
	public void setRefreshTrades(boolean refreshable) {
		this.canRefreshTrades = refreshable;
	}

	@Environment(EnvType.CLIENT)
	public boolean canRefreshTrades() {
		return this.canRefreshTrades;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return false;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
				this.playYesSound();
			} else if (index != 0 && index != 1) {
				if (index >= 3 && index < 30) {
					if (!this.insertItem(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 30 && index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
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

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	private void playYesSound() {
		if (!this.trader.getTraderWorld().isClient) {
			Entity entity = (Entity)this.trader;
			this.trader.getTraderWorld().playSound(entity.getX(), entity.getY(), entity.getZ(), this.trader.getYesSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
		}
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.trader.setCurrentCustomer(null);
		if (!this.trader.getTraderWorld().isClient) {
			if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected()) {
				ItemStack itemStack = this.traderInventory.removeStack(0);
				if (!itemStack.isEmpty()) {
					player.dropItem(itemStack, false);
				}

				itemStack = this.traderInventory.removeStack(1);
				if (!itemStack.isEmpty()) {
					player.dropItem(itemStack, false);
				}
			} else {
				player.inventory.offerOrDrop(player.world, this.traderInventory.removeStack(0));
				player.inventory.offerOrDrop(player.world, this.traderInventory.removeStack(1));
			}
		}
	}

	public void switchTo(int recipeIndex) {
		if (this.getRecipes().size() > recipeIndex) {
			ItemStack itemStack = this.traderInventory.getStack(0);
			if (!itemStack.isEmpty()) {
				if (!this.insertItem(itemStack, 3, 39, true)) {
					return;
				}

				this.traderInventory.setStack(0, itemStack);
			}

			ItemStack itemStack2 = this.traderInventory.getStack(1);
			if (!itemStack2.isEmpty()) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return;
				}

				this.traderInventory.setStack(1, itemStack2);
			}

			if (this.traderInventory.getStack(0).isEmpty() && this.traderInventory.getStack(1).isEmpty()) {
				ItemStack itemStack3 = ((TradeOffer)this.getRecipes().get(recipeIndex)).getAdjustedFirstBuyItem();
				this.autofill(0, itemStack3);
				ItemStack itemStack4 = ((TradeOffer)this.getRecipes().get(recipeIndex)).getSecondBuyItem();
				this.autofill(1, itemStack4);
			}
		}
	}

	private void autofill(int slot, ItemStack stack) {
		if (!stack.isEmpty()) {
			for (int i = 3; i < 39; i++) {
				ItemStack itemStack = ((Slot)this.slots.get(i)).getStack();
				if (!itemStack.isEmpty() && this.equals(stack, itemStack)) {
					ItemStack itemStack2 = this.traderInventory.getStack(slot);
					int j = itemStack2.isEmpty() ? 0 : itemStack2.getCount();
					int k = Math.min(stack.getMaxCount() - j, itemStack.getCount());
					ItemStack itemStack3 = itemStack.copy();
					int l = j + k;
					itemStack.decrement(k);
					itemStack3.setCount(l);
					this.traderInventory.setStack(slot, itemStack3);
					if (l >= stack.getMaxCount()) {
						break;
					}
				}
			}
		}
	}

	private boolean equals(ItemStack itemStack, ItemStack otherItemStack) {
		return itemStack.getItem() == otherItemStack.getItem() && ItemStack.areTagsEqual(itemStack, otherItemStack);
	}

	@Environment(EnvType.CLIENT)
	public void setOffers(TraderOfferList offers) {
		this.trader.setOffersFromServer(offers);
	}

	public TraderOfferList getRecipes() {
		return this.trader.getOffers();
	}

	@Environment(EnvType.CLIENT)
	public boolean isLeveled() {
		return this.leveled;
	}
}
