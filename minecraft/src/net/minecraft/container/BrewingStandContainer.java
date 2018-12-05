package net.minecraft.container;

import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.server.network.ServerPlayerEntity;

public class BrewingStandContainer extends Container {
	private final Inventory inventory;
	private final Slot slotIngredient;
	private int field_7786;
	private int field_7785;

	public BrewingStandContainer(PlayerInventory playerInventory, Inventory inventory) {
		this.inventory = inventory;
		this.addSlot(new BrewingStandContainer.SlotPotion(inventory, 0, 56, 51));
		this.addSlot(new BrewingStandContainer.SlotPotion(inventory, 1, 79, 58));
		this.addSlot(new BrewingStandContainer.SlotPotion(inventory, 2, 102, 51));
		this.slotIngredient = this.addSlot(new BrewingStandContainer.SlotIngredient(inventory, 3, 79, 17));
		this.addSlot(new BrewingStandContainer.SlotFuel(inventory, 4, 17, 17));

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
	public void sendContentUpdates() {
		super.sendContentUpdates();

		for (int i = 0; i < this.listeners.size(); i++) {
			ContainerListener containerListener = (ContainerListener)this.listeners.get(i);
			if (this.field_7786 != this.inventory.getInvProperty(0)) {
				containerListener.onContainerPropertyUpdate(this, 0, this.inventory.getInvProperty(0));
			}

			if (this.field_7785 != this.inventory.getInvProperty(1)) {
				containerListener.onContainerPropertyUpdate(this, 1, this.inventory.getInvProperty(1));
			}
		}

		this.field_7786 = this.inventory.getInvProperty(0);
		this.field_7785 = this.inventory.getInvProperty(1);
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
			if ((i < 0 || i > 2) && i != 3 && i != 4) {
				if (this.slotIngredient.canInsert(itemStack2)) {
					if (!this.insertItem(itemStack2, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (BrewingStandContainer.SlotPotion.matches(itemStack) && itemStack.getAmount() == 1) {
					if (!this.insertItem(itemStack2, 0, 3, false)) {
						return ItemStack.EMPTY;
					}
				} else if (BrewingStandContainer.SlotFuel.matches(itemStack)) {
					if (!this.insertItem(itemStack2, 4, 5, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 5 && i < 32) {
					if (!this.insertItem(itemStack2, 32, 41, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 32 && i < 41) {
					if (!this.insertItem(itemStack2, 5, 32, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 5, 41, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!this.insertItem(itemStack2, 5, 41, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
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

	static class SlotFuel extends Slot {
		public SlotFuel(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack itemStack) {
			return matches(itemStack);
		}

		public static boolean matches(ItemStack itemStack) {
			return itemStack.getItem() == Items.field_8183;
		}

		@Override
		public int getMaxStackAmount() {
			return 64;
		}
	}

	static class SlotIngredient extends Slot {
		public SlotIngredient(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack itemStack) {
			return BrewingRecipeRegistry.isValidIngredient(itemStack);
		}

		@Override
		public int getMaxStackAmount() {
			return 64;
		}
	}

	static class SlotPotion extends Slot {
		public SlotPotion(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack itemStack) {
			return matches(itemStack);
		}

		@Override
		public int getMaxStackAmount() {
			return 1;
		}

		@Override
		public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
			Potion potion = PotionUtil.getPotion(itemStack);
			if (playerEntity instanceof ServerPlayerEntity) {
				CriterionCriterions.BREWED_POTION.handle((ServerPlayerEntity)playerEntity, potion);
			}

			super.onTakeItem(playerEntity, itemStack);
			return itemStack;
		}

		public static boolean matches(ItemStack itemStack) {
			Item item = itemStack.getItem();
			return item == Items.field_8574 || item == Items.field_8436 || item == Items.field_8150 || item == Items.field_8469;
		}
	}
}
