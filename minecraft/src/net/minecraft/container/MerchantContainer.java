package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.village.SimpleTrader;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderRecipeList;

public class MerchantContainer extends Container {
	private final Trader villager;
	private final TraderInventory villagerInventory;

	public MerchantContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, new SimpleTrader(playerInventory.player));
	}

	public MerchantContainer(int i, PlayerInventory playerInventory, Trader trader) {
		super(ContainerType.MERCHANT, i);
		this.villager = trader;
		this.villagerInventory = new TraderInventory(trader);
		this.addSlot(new Slot(this.villagerInventory, 0, 36, 53));
		this.addSlot(new Slot(this.villagerInventory, 1, 62, 53));
		this.addSlot(new VillagerOutputSlot(playerInventory.player, trader, this.villagerInventory, 2, 120, 53));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.villagerInventory.updateRecipes();
		super.onContentChanged(inventory);
	}

	public void setRecipeIndex(int i) {
		this.villagerInventory.setRecipeIndex(i);
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.villager.getCurrentCustomer() == playerEntity;
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

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(playerEntity, itemStack2);
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.villager.setCurrentCustomer(null);
		if (!this.villager.getTraderWorld().isClient) {
			ItemStack itemStack = this.villagerInventory.removeInvStack(0);
			if (!itemStack.isEmpty()) {
				playerEntity.dropItem(itemStack, false);
			}

			itemStack = this.villagerInventory.removeInvStack(1);
			if (!itemStack.isEmpty()) {
				playerEntity.dropItem(itemStack, false);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_17437(TraderRecipeList traderRecipeList) {
		this.villager.setServerRecipes(traderRecipeList);
	}

	@Environment(EnvType.CLIENT)
	public TraderRecipeList method_17438() {
		return this.villager.getRecipes();
	}
}
