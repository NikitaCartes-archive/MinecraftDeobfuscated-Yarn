package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
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
	private final PropertyDelegate field_17292;
	private final Slot field_7787;

	public BrewingStandContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, new BasicInventory(5), new ArrayPropertyDelegate(2));
	}

	public BrewingStandContainer(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ContainerType.BREWING_STAND, i);
		checkContainerSize(inventory, 5);
		method_17361(propertyDelegate, 2);
		this.inventory = inventory;
		this.field_17292 = propertyDelegate;
		this.method_7621(new BrewingStandContainer.SlotPotion(inventory, 0, 56, 51));
		this.method_7621(new BrewingStandContainer.SlotPotion(inventory, 1, 79, 58));
		this.method_7621(new BrewingStandContainer.SlotPotion(inventory, 2, 102, 51));
		this.field_7787 = this.method_7621(new BrewingStandContainer.SlotIngredient(inventory, 3, 79, 17));
		this.method_7621(new BrewingStandContainer.SlotFuel(inventory, 4, 17, 17));
		this.method_17360(propertyDelegate);

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new Slot(playerInventory, j, 8 + j * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.inventory.method_5443(playerEntity);
	}

	@Override
	public ItemStack method_7601(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.method_7677();
			itemStack = itemStack2.copy();
			if ((i < 0 || i > 2) && i != 3 && i != 4) {
				if (this.field_7787.method_7680(itemStack2)) {
					if (!this.method_7616(itemStack2, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (BrewingStandContainer.SlotPotion.method_7631(itemStack) && itemStack.getAmount() == 1) {
					if (!this.method_7616(itemStack2, 0, 3, false)) {
						return ItemStack.EMPTY;
					}
				} else if (BrewingStandContainer.SlotFuel.method_7630(itemStack)) {
					if (!this.method_7616(itemStack2, 4, 5, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 5 && i < 32) {
					if (!this.method_7616(itemStack2, 32, 41, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 32 && i < 41) {
					if (!this.method_7616(itemStack2, 5, 32, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.method_7616(itemStack2, 5, 41, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!this.method_7616(itemStack2, 5, 41, true)) {
					return ItemStack.EMPTY;
				}

				slot.method_7670(itemStack2, itemStack);
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

	@Environment(EnvType.CLIENT)
	public int method_17377() {
		return this.field_17292.get(1);
	}

	@Environment(EnvType.CLIENT)
	public int method_17378() {
		return this.field_17292.get(0);
	}

	static class SlotFuel extends Slot {
		public SlotFuel(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean method_7680(ItemStack itemStack) {
			return method_7630(itemStack);
		}

		public static boolean method_7630(ItemStack itemStack) {
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
		public boolean method_7680(ItemStack itemStack) {
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
		public boolean method_7680(ItemStack itemStack) {
			return method_7631(itemStack);
		}

		@Override
		public int getMaxStackAmount() {
			return 1;
		}

		@Override
		public ItemStack method_7667(PlayerEntity playerEntity, ItemStack itemStack) {
			Potion potion = PotionUtil.getPotion(itemStack);
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.BREWED_POTION.method_8784((ServerPlayerEntity)playerEntity, potion);
			}

			super.method_7667(playerEntity, itemStack);
			return itemStack;
		}

		public static boolean method_7631(ItemStack itemStack) {
			Item item = itemStack.getItem();
			return item == Items.field_8574 || item == Items.field_8436 || item == Items.field_8150 || item == Items.field_8469;
		}
	}
}
