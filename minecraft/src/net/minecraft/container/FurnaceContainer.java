package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1662;
import net.minecraft.class_1737;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.smelting.SmeltingRecipe;
import net.minecraft.world.World;

public class FurnaceContainer extends CraftingContainer {
	private final Inventory inventory;
	private final World field_7822;
	private int cookTime;
	private int totalCookTime;
	private int burnTime;
	private int fuelTime;

	public FurnaceContainer(PlayerInventory playerInventory, Inventory inventory) {
		this.inventory = inventory;
		this.field_7822 = playerInventory.player.world;
		this.addSlot(new Slot(inventory, 0, 56, 17));
		this.addSlot(new FurnaceFuelSlot(inventory, 1, 56, 53));
		this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 2, 116, 35));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public void addListener(ContainerListener containerListener) {
		super.addListener(containerListener);
		containerListener.onContainerInvRegistered(this, this.inventory);
	}

	@Override
	public void method_7654(class_1662 arg) {
		if (this.inventory instanceof class_1737) {
			((class_1737)this.inventory).method_7683(arg);
		}
	}

	@Override
	public void clearCraftingSlots() {
		this.inventory.clearInv();
	}

	@Override
	public boolean matches(Recipe recipe) {
		return recipe.matches(this.inventory, this.field_7822);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 2;
	}

	@Override
	public int getCraftingWidth() {
		return 1;
	}

	@Override
	public int getCrafitngHeight() {
		return 1;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_7658() {
		return 3;
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();

		for (ContainerListener containerListener : this.listeners) {
			if (this.cookTime != this.inventory.getInvProperty(2)) {
				containerListener.onContainerPropertyUpdate(this, 2, this.inventory.getInvProperty(2));
			}

			if (this.burnTime != this.inventory.getInvProperty(0)) {
				containerListener.onContainerPropertyUpdate(this, 0, this.inventory.getInvProperty(0));
			}

			if (this.fuelTime != this.inventory.getInvProperty(1)) {
				containerListener.onContainerPropertyUpdate(this, 1, this.inventory.getInvProperty(1));
			}

			if (this.totalCookTime != this.inventory.getInvProperty(3)) {
				containerListener.onContainerPropertyUpdate(this, 3, this.inventory.getInvProperty(3));
			}
		}

		this.cookTime = this.inventory.getInvProperty(2);
		this.burnTime = this.inventory.getInvProperty(0);
		this.fuelTime = this.inventory.getInvProperty(1);
		this.totalCookTime = this.inventory.getInvProperty(3);
	}

	@Override
	public void setProperty(int i, int j) {
		this.inventory.setInvProperty(i, j);
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.inventory.canPlayerUseInv(playerEntity);
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
			} else if (i != 1 && i != 0) {
				if (this.method_7640(itemStack2)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (FurnaceBlockEntity.canUseAsFuel(itemStack2)) {
					if (!this.insertItem(itemStack2, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 3 && i < 30) {
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

	private boolean method_7640(ItemStack itemStack) {
		for (Recipe recipe : this.field_7822.getRecipeManager().values()) {
			if (recipe instanceof SmeltingRecipe && recipe.getPreviewInputs().get(0).matches(itemStack)) {
				return true;
			}
		}

		return false;
	}
}
