package net.minecraft.screen;

import java.util.function.BiConsumer;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CartographyTableScreenHandler extends ScreenHandler {
	private final ScreenHandlerContext context;
	private long lastTakeResultTime;
	public final Inventory inventory = new SimpleInventory(2) {
		@Override
		public void markDirty() {
			CartographyTableScreenHandler.this.onContentChanged(this);
			super.markDirty();
		}
	};
	private final CraftingResultInventory resultSlot = new CraftingResultInventory() {
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
		this.addSlot(new Slot(this.resultSlot, 2, 145, 39) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
				CartographyTableScreenHandler.this.slots.get(0).takeStack(1);
				CartographyTableScreenHandler.this.slots.get(1).takeStack(1);
				stack.getItem().onCraft(stack, player.world, player);
				context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
					long l = world.getTime();
					if (CartographyTableScreenHandler.this.lastTakeResultTime != l) {
						world.playSound(null, blockPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
						CartographyTableScreenHandler.this.lastTakeResultTime = l;
					}
				}));
				return super.onTakeItem(player, stack);
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
		ItemStack itemStack3 = this.resultSlot.getStack(2);
		if (itemStack3.isEmpty() || !itemStack.isEmpty() && !itemStack2.isEmpty()) {
			if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
				this.updateResult(itemStack, itemStack2, itemStack3);
			}
		} else {
			this.resultSlot.removeStack(2);
		}
	}

	private void updateResult(ItemStack itemStack, ItemStack itemStack2, ItemStack oldResult) {
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
			MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, world);
			if (mapState != null) {
				ItemStack itemStack4;
				if (itemStack2.isOf(Items.PAPER) && !mapState.locked && mapState.scale < 4) {
					itemStack4 = itemStack.copy();
					itemStack4.setCount(1);
					itemStack4.getOrCreateTag().putInt("map_scale_direction", 1);
					this.sendContentUpdates();
				} else if (itemStack2.isOf(Items.GLASS_PANE) && !mapState.locked) {
					itemStack4 = itemStack.copy();
					itemStack4.setCount(1);
					itemStack4.getOrCreateTag().putBoolean("map_to_lock", true);
					this.sendContentUpdates();
				} else {
					if (!itemStack2.isOf(Items.MAP)) {
						this.resultSlot.removeStack(2);
						this.sendContentUpdates();
						return;
					}

					itemStack4 = itemStack.copy();
					itemStack4.setCount(2);
					this.sendContentUpdates();
				}

				if (!ItemStack.areEqual(itemStack4, oldResult)) {
					this.resultSlot.setStack(2, itemStack4);
					this.sendContentUpdates();
				}
			}
		}));
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.resultSlot && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index == 2) {
				itemStack2.getItem().onCraft(itemStack2, player.world, player);
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (index != 1 && index != 0) {
				if (itemStack2.isOf(Items.FILLED_MAP)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!itemStack2.isOf(Items.PAPER) && !itemStack2.isOf(Items.MAP) && !itemStack2.isOf(Items.GLASS_PANE)) {
					if (index >= 3 && index < 30) {
						if (!this.insertItem(itemStack2, 30, 39, false)) {
							return ItemStack.EMPTY;
						}
					} else if (index >= 30 && index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			}

			slot.markDirty();
			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, itemStack2);
			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.resultSlot.removeStack(2);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.dropInventory(player, this.inventory)));
	}
}
