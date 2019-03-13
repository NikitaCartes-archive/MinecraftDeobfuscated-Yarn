package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.SimpleTrader;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderRecipeList;

public class MerchantContainer extends Container {
	private final Trader field_7863;
	private final TraderInventory villagerInventory;
	@Environment(EnvType.CLIENT)
	private int field_18669;
	@Environment(EnvType.CLIENT)
	private boolean field_18670;

	public MerchantContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, new SimpleTrader(playerInventory.field_7546));
	}

	public MerchantContainer(int i, PlayerInventory playerInventory, Trader trader) {
		super(ContainerType.MERCHANT, i);
		this.field_7863 = trader;
		this.villagerInventory = new TraderInventory(trader);
		this.method_7621(new Slot(this.villagerInventory, 0, 36, 53));
		this.method_7621(new Slot(this.villagerInventory, 1, 62, 53));
		this.method_7621(new VillagerOutputSlot(playerInventory.field_7546, trader, this.villagerInventory, 2, 120, 53));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new Slot(playerInventory, j, 8 + j * 18, 142));
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_19253(boolean bl) {
		this.field_18670 = bl;
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
		return this.field_7863.getCurrentCustomer() == playerEntity;
	}

	@Environment(EnvType.CLIENT)
	public int method_19254() {
		return this.field_7863.method_19269();
	}

	@Environment(EnvType.CLIENT)
	public int method_19256() {
		return this.villagerInventory.method_19252();
	}

	@Environment(EnvType.CLIENT)
	public void method_19255(int i) {
		this.field_7863.method_19271(i);
	}

	@Environment(EnvType.CLIENT)
	public int method_19258() {
		return this.field_18669;
	}

	@Environment(EnvType.CLIENT)
	public void method_19257(int i) {
		this.field_18669 = i;
	}

	@Override
	public ItemStack method_7601(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.method_7677();
			itemStack = itemStack2.copy();
			if (i == 2) {
				if (!this.method_7616(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.method_7670(itemStack2, itemStack);
			} else if (i != 0 && i != 1) {
				if (i >= 3 && i < 30) {
					if (!this.method_7616(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 30 && i < 39 && !this.method_7616(itemStack2, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.method_7616(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.method_7673(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.method_7667(playerEntity, itemStack2);
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.field_7863.setCurrentCustomer(null);
		if (!this.field_7863.method_8260().isClient) {
			if (!playerEntity.isValid() || playerEntity instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerEntity).method_14239()) {
				ItemStack itemStack = this.villagerInventory.method_5441(0);
				if (!itemStack.isEmpty()) {
					playerEntity.method_7328(itemStack, false);
				}

				itemStack = this.villagerInventory.method_5441(1);
				if (!itemStack.isEmpty()) {
					playerEntity.method_7328(itemStack, false);
				}
			} else {
				playerEntity.inventory.method_7398(playerEntity.field_6002, this.villagerInventory.method_5441(0));
				playerEntity.inventory.method_7398(playerEntity.field_6002, this.villagerInventory.method_5441(1));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_17437(TraderRecipeList traderRecipeList) {
		this.field_7863.method_8261(traderRecipeList);
	}

	@Environment(EnvType.CLIENT)
	public TraderRecipeList method_17438() {
		return this.field_7863.method_8264();
	}

	@Environment(EnvType.CLIENT)
	public boolean method_19259() {
		return this.field_18670;
	}
}
