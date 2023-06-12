package net.minecraft.screen;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class CartographyTableScreenHandler extends ScreenHandler {
	public static final int MAP_SLOT_INDEX = 0;
	public static final int MATERIAL_SLOT_INDEX = 1;
	public static final int RESULT_SLOT_INDEX = 2;
	private static final int INVENTORY_START = 3;
	private static final int INVENTORY_END = 30;
	private static final int HOTBAR_START = 30;
	private static final int HOTBAR_END = 39;
	private final ScreenHandlerContext context;
	long lastTakeResultTime;
	public final Inventory inventory = new SimpleInventory(2) {
		@Override
		public void markDirty() {
			CartographyTableScreenHandler.this.onContentChanged(this);
			super.markDirty();
		}
	};
	private final CraftingResultInventory resultInventory = new CraftingResultInventory() {
		@Override
		public void markDirty() {
			CartographyTableScreenHandler.this.onContentChanged(this);
			super.markDirty();
		}
	};

	public CartographyTableScreenHandler(int syncId, PlayerInventory inventory) {
		this(syncId, inventory, ScreenHandlerContext.EMPTY);
	}

	public CartographyTableScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.CARTOGRAPHY_TABLE, syncId);
		this.context = context;
		this.addSlot(new Slot(this.inventory, 0, 15, 15) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isOf(Items.FILLED_MAP);
			}
		});
		this.addSlot(new Slot(this.inventory, 1, 15, 52) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isOf(Items.PAPER) || stack.isOf(Items.MAP) || stack.isOf(Items.GLASS_PANE);
			}
		});
		this.addSlot(new Slot(this.resultInventory, 2, 145, 39) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public void onTakeItem(PlayerEntity player, ItemStack stack) {
				CartographyTableScreenHandler.this.slots.get(0).takeStack(1);
				CartographyTableScreenHandler.this.slots.get(1).takeStack(1);
				stack.getItem().onCraft(stack, player.getWorld(), player);
				context.run((world, pos) -> {
					long l = world.getTime();
					if (CartographyTableScreenHandler.this.lastTakeResultTime != l) {
						world.playSound(null, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
						CartographyTableScreenHandler.this.lastTakeResultTime = l;
					}
				});
				super.onTakeItem(player, stack);
			}
		});

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, Blocks.CARTOGRAPHY_TABLE);
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.inventory.getStack(0);
		ItemStack itemStack2 = this.inventory.getStack(1);
		ItemStack itemStack3 = this.resultInventory.getStack(2);
		if (itemStack3.isEmpty() || !itemStack.isEmpty() && !itemStack2.isEmpty()) {
			if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
				this.updateResult(itemStack, itemStack2, itemStack3);
			}
		} else {
			this.resultInventory.removeStack(2);
		}
	}

	private void updateResult(ItemStack map, ItemStack item, ItemStack oldResult) {
		this.context.run((world, pos) -> {
			MapState mapState = FilledMapItem.getMapState(map, world);
			if (mapState != null) {
				ItemStack itemStack4;
				if (item.isOf(Items.PAPER) && !mapState.locked && mapState.scale < 4) {
					itemStack4 = map.copyWithCount(1);
					itemStack4.getOrCreateNbt().putInt("map_scale_direction", 1);
					this.sendContentUpdates();
				} else if (item.isOf(Items.GLASS_PANE) && !mapState.locked) {
					itemStack4 = map.copyWithCount(1);
					itemStack4.getOrCreateNbt().putBoolean("map_to_lock", true);
					this.sendContentUpdates();
				} else {
					if (!item.isOf(Items.MAP)) {
						this.resultInventory.removeStack(2);
						this.sendContentUpdates();
						return;
					}

					itemStack4 = map.copyWithCount(2);
					this.sendContentUpdates();
				}

				if (!ItemStack.areEqual(itemStack4, oldResult)) {
					this.resultInventory.setStack(2, itemStack4);
					this.sendContentUpdates();
				}
			}
		});
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.resultInventory && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			if (slot == 2) {
				itemStack2.getItem().onCraft(itemStack2, player.getWorld(), player);
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
			} else if (slot != 1 && slot != 0) {
				if (itemStack2.isOf(Items.FILLED_MAP)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!itemStack2.isOf(Items.PAPER) && !itemStack2.isOf(Items.MAP) && !itemStack2.isOf(Items.GLASS_PANE)) {
					if (slot >= 3 && slot < 30) {
						if (!this.insertItem(itemStack2, 30, 39, false)) {
							return ItemStack.EMPTY;
						}
					} else if (slot >= 30 && slot < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			}

			slot2.markDirty();
			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.resultInventory.removeStack(2);
		this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
	}
}
