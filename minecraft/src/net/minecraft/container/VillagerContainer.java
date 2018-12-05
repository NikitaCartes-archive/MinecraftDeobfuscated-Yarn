package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.village.Villager;
import net.minecraft.village.VillagerInventory;
import net.minecraft.world.World;

public class VillagerContainer extends Container {
	private final Villager villager;
	private final VillagerInventory villagerInventory;
	private final World world;

	public VillagerContainer(PlayerInventory playerInventory, Villager villager, World world) {
		this.villager = villager;
		this.world = world;
		this.villagerInventory = new VillagerInventory(playerInventory.player, villager);
		this.addSlot(new Slot(this.villagerInventory, 0, 36, 53));
		this.addSlot(new Slot(this.villagerInventory, 1, 62, 53));
		this.addSlot(new VillagerOutputSlot(playerInventory.player, villager, this.villagerInventory, 2, 120, 53));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	public VillagerInventory getVillagerInventory() {
		return this.villagerInventory;
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
		super.close(playerEntity);
		if (!this.world.isRemote) {
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
}
