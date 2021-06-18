package net.minecraft.screen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.SimpleMerchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

public class MerchantScreenHandler extends ScreenHandler {
	protected static final int field_30830 = 0;
	protected static final int field_30831 = 1;
	protected static final int field_30832 = 2;
	private static final int field_30833 = 3;
	private static final int field_30834 = 30;
	private static final int field_30835 = 30;
	private static final int field_30836 = 39;
	private static final int field_30837 = 136;
	private static final int field_30838 = 162;
	private static final int field_30839 = 220;
	private static final int field_30840 = 37;
	private final Merchant merchant;
	private final MerchantInventory merchantInventory;
	private int levelProgress;
	private boolean leveled;
	private boolean canRefreshTrades;

	public MerchantScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleMerchant(playerInventory.player));
	}

	public MerchantScreenHandler(int syncId, PlayerInventory playerInventory, Merchant merchant) {
		super(ScreenHandlerType.MERCHANT, syncId);
		this.merchant = merchant;
		this.merchantInventory = new MerchantInventory(merchant);
		this.addSlot(new Slot(this.merchantInventory, 0, 136, 37));
		this.addSlot(new Slot(this.merchantInventory, 1, 162, 37));
		this.addSlot(new TradeOutputSlot(playerInventory.player, merchant, this.merchantInventory, 2, 220, 37));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 108 + i * 18, 142));
		}
	}

	public void setCanLevel(boolean canLevel) {
		this.leveled = canLevel;
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.merchantInventory.updateOffers();
		super.onContentChanged(inventory);
	}

	public void setRecipeIndex(int index) {
		this.merchantInventory.setOfferIndex(index);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.merchant.getCurrentCustomer() == player;
	}

	public int getExperience() {
		return this.merchant.getExperience();
	}

	public int getMerchantRewardedExperience() {
		return this.merchantInventory.getMerchantRewardedExperience();
	}

	public void setExperienceFromServer(int experience) {
		this.merchant.setExperienceFromServer(experience);
	}

	public int getLevelProgress() {
		return this.levelProgress;
	}

	public void setLevelProgress(int progress) {
		this.levelProgress = progress;
	}

	public void setRefreshTrades(boolean refreshable) {
		this.canRefreshTrades = refreshable;
	}

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
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(itemStack2, itemStack);
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
		if (!this.merchant.getMerchantWorld().isClient) {
			Entity entity = (Entity)this.merchant;
			this.merchant
				.getMerchantWorld()
				.playSound(entity.getX(), entity.getY(), entity.getZ(), this.merchant.getYesSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
		}
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.merchant.setCurrentCustomer(null);
		if (!this.merchant.getMerchantWorld().isClient) {
			if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected()) {
				ItemStack itemStack = this.merchantInventory.removeStack(0);
				if (!itemStack.isEmpty()) {
					player.dropItem(itemStack, false);
				}

				itemStack = this.merchantInventory.removeStack(1);
				if (!itemStack.isEmpty()) {
					player.dropItem(itemStack, false);
				}
			} else if (player instanceof ServerPlayerEntity) {
				player.getInventory().offerOrDrop(this.merchantInventory.removeStack(0));
				player.getInventory().offerOrDrop(this.merchantInventory.removeStack(1));
			}
		}
	}

	public void switchTo(int recipeIndex) {
		if (this.getRecipes().size() > recipeIndex) {
			ItemStack itemStack = this.merchantInventory.getStack(0);
			if (!itemStack.isEmpty()) {
				if (!this.insertItem(itemStack, 3, 39, true)) {
					return;
				}

				this.merchantInventory.setStack(0, itemStack);
			}

			ItemStack itemStack2 = this.merchantInventory.getStack(1);
			if (!itemStack2.isEmpty()) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return;
				}

				this.merchantInventory.setStack(1, itemStack2);
			}

			if (this.merchantInventory.getStack(0).isEmpty() && this.merchantInventory.getStack(1).isEmpty()) {
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
				ItemStack itemStack = this.slots.get(i).getStack();
				if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
					ItemStack itemStack2 = this.merchantInventory.getStack(slot);
					int j = itemStack2.isEmpty() ? 0 : itemStack2.getCount();
					int k = Math.min(stack.getMaxCount() - j, itemStack.getCount());
					ItemStack itemStack3 = itemStack.copy();
					int l = j + k;
					itemStack.decrement(k);
					itemStack3.setCount(l);
					this.merchantInventory.setStack(slot, itemStack3);
					if (l >= stack.getMaxCount()) {
						break;
					}
				}
			}
		}
	}

	public void setOffers(TradeOfferList offers) {
		this.merchant.setOffersFromServer(offers);
	}

	public TradeOfferList getRecipes() {
		return this.merchant.getOffers();
	}

	public boolean isLeveled() {
		return this.leveled;
	}
}
