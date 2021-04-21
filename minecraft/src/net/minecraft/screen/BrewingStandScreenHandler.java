package net.minecraft.screen;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class BrewingStandScreenHandler extends ScreenHandler {
	private static final int field_30763 = 0;
	private static final int field_30764 = 2;
	private static final int field_30765 = 3;
	private static final int field_30766 = 4;
	private static final int field_30767 = 5;
	private static final int field_30768 = 2;
	private static final int field_30769 = 5;
	private static final int field_30770 = 32;
	private static final int field_30771 = 32;
	private static final int field_30772 = 41;
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;
	private final Slot ingredientSlot;

	public BrewingStandScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(5), new ArrayPropertyDelegate(2));
	}

	public BrewingStandScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.BREWING_STAND, syncId);
		checkSize(inventory, 5);
		checkDataCount(propertyDelegate, 2);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		this.addSlot(new BrewingStandScreenHandler.PotionSlot(inventory, 0, 56, 51));
		this.addSlot(new BrewingStandScreenHandler.PotionSlot(inventory, 1, 79, 58));
		this.addSlot(new BrewingStandScreenHandler.PotionSlot(inventory, 2, 102, 51));
		this.ingredientSlot = this.addSlot(new BrewingStandScreenHandler.IngredientSlot(inventory, 3, 79, 17));
		this.addSlot(new BrewingStandScreenHandler.FuelSlot(inventory, 4, 17, 17));
		this.addProperties(propertyDelegate);

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
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if ((index < 0 || index > 2) && index != 3 && index != 4) {
				if (BrewingStandScreenHandler.FuelSlot.matches(itemStack)) {
					if (this.insertItem(itemStack2, 4, 5, false) || this.ingredientSlot.canInsert(itemStack2) && !this.insertItem(itemStack2, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (this.ingredientSlot.canInsert(itemStack2)) {
					if (!this.insertItem(itemStack2, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (BrewingStandScreenHandler.PotionSlot.matches(itemStack) && itemStack.getCount() == 1) {
					if (!this.insertItem(itemStack2, 0, 3, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 5 && index < 32) {
					if (!this.insertItem(itemStack2, 32, 41, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 32 && index < 41) {
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

				slot.onQuickTransfer(itemStack2, itemStack);
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

	public int getFuel() {
		return this.propertyDelegate.get(1);
	}

	public int getBrewTime() {
		return this.propertyDelegate.get(0);
	}

	static class FuelSlot extends Slot {
		public FuelSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return matches(stack);
		}

		public static boolean matches(ItemStack stack) {
			return stack.isOf(Items.BLAZE_POWDER);
		}

		@Override
		public int getMaxItemCount() {
			return 64;
		}
	}

	static class IngredientSlot extends Slot {
		public IngredientSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return BrewingRecipeRegistry.isValidIngredient(stack);
		}

		@Override
		public int getMaxItemCount() {
			return 64;
		}
	}

	static class PotionSlot extends Slot {
		public PotionSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return matches(stack);
		}

		@Override
		public int getMaxItemCount() {
			return 1;
		}

		@Override
		public void onTakeItem(PlayerEntity player, ItemStack stack) {
			Potion potion = PotionUtil.getPotion(stack);
			if (player instanceof ServerPlayerEntity) {
				Criteria.BREWED_POTION.trigger((ServerPlayerEntity)player, potion);
			}

			super.onTakeItem(player, stack);
		}

		public static boolean matches(ItemStack stack) {
			return stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.GLASS_BOTTLE);
		}
	}
}
