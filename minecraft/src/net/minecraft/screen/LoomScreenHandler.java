package net.minecraft.screen;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;

public class LoomScreenHandler extends ScreenHandler {
	private static final int field_30826 = 4;
	private static final int field_30827 = 31;
	private static final int field_30828 = 31;
	private static final int field_30829 = 40;
	private final ScreenHandlerContext context;
	final Property selectedPattern = Property.create();
	Runnable inventoryChangeListener = () -> {
	};
	final Slot bannerSlot;
	final Slot dyeSlot;
	private final Slot patternSlot;
	private final Slot outputSlot;
	long lastTakeResultTime;
	private final Inventory input = new SimpleInventory(3) {
		@Override
		public void markDirty() {
			super.markDirty();
			LoomScreenHandler.this.onContentChanged(this);
			LoomScreenHandler.this.inventoryChangeListener.run();
		}
	};
	private final Inventory output = new SimpleInventory(1) {
		@Override
		public void markDirty() {
			super.markDirty();
			LoomScreenHandler.this.inventoryChangeListener.run();
		}
	};

	public LoomScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public LoomScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.LOOM, syncId);
		this.context = context;
		this.bannerSlot = this.addSlot(new Slot(this.input, 0, 13, 26) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() instanceof BannerItem;
			}
		});
		this.dyeSlot = this.addSlot(new Slot(this.input, 1, 33, 26) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() instanceof DyeItem;
			}
		});
		this.patternSlot = this.addSlot(new Slot(this.input, 2, 23, 45) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() instanceof BannerPatternItem;
			}
		});
		this.outputSlot = this.addSlot(new Slot(this.output, 0, 143, 58) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public void onTakeItem(PlayerEntity player, ItemStack stack) {
				LoomScreenHandler.this.bannerSlot.takeStack(1);
				LoomScreenHandler.this.dyeSlot.takeStack(1);
				if (!LoomScreenHandler.this.bannerSlot.hasStack() || !LoomScreenHandler.this.dyeSlot.hasStack()) {
					LoomScreenHandler.this.selectedPattern.set(0);
				}

				context.run((world, blockPos) -> {
					long l = world.getTime();
					if (LoomScreenHandler.this.lastTakeResultTime != l) {
						world.playSound(null, blockPos, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
						LoomScreenHandler.this.lastTakeResultTime = l;
					}
				});
				super.onTakeItem(player, stack);
			}
		});

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}

		this.addProperty(this.selectedPattern);
	}

	public int getSelectedPattern() {
		return this.selectedPattern.get();
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, Blocks.LOOM);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (id > 0 && id <= BannerPattern.LOOM_APPLICABLE_COUNT) {
			this.selectedPattern.set(id);
			this.updateOutputSlot();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.bannerSlot.getStack();
		ItemStack itemStack2 = this.dyeSlot.getStack();
		ItemStack itemStack3 = this.patternSlot.getStack();
		ItemStack itemStack4 = this.outputSlot.getStack();
		if (itemStack4.isEmpty()
			|| !itemStack.isEmpty()
				&& !itemStack2.isEmpty()
				&& this.selectedPattern.get() > 0
				&& (this.selectedPattern.get() < BannerPattern.COUNT - BannerPattern.HAS_PATTERN_ITEM_COUNT || !itemStack3.isEmpty())) {
			if (!itemStack3.isEmpty() && itemStack3.getItem() instanceof BannerPatternItem) {
				NbtCompound nbtCompound = itemStack.getOrCreateSubTag("BlockEntityTag");
				boolean bl = nbtCompound.contains("Patterns", NbtElement.LIST_TYPE)
					&& !itemStack.isEmpty()
					&& nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE).size() >= 6;
				if (bl) {
					this.selectedPattern.set(0);
				} else {
					this.selectedPattern.set(((BannerPatternItem)itemStack3.getItem()).getPattern().ordinal());
				}
			}
		} else {
			this.outputSlot.setStack(ItemStack.EMPTY);
			this.selectedPattern.set(0);
		}

		this.updateOutputSlot();
		this.sendContentUpdates();
	}

	public void setInventoryChangeListener(Runnable inventoryChangeListener) {
		this.inventoryChangeListener = inventoryChangeListener;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index == this.outputSlot.id) {
				if (!this.insertItem(itemStack2, 4, 40, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(itemStack2, itemStack);
			} else if (index != this.dyeSlot.id && index != this.bannerSlot.id && index != this.patternSlot.id) {
				if (itemStack2.getItem() instanceof BannerItem) {
					if (!this.insertItem(itemStack2, this.bannerSlot.id, this.bannerSlot.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof DyeItem) {
					if (!this.insertItem(itemStack2, this.dyeSlot.id, this.dyeSlot.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof BannerPatternItem) {
					if (!this.insertItem(itemStack2, this.patternSlot.id, this.patternSlot.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 4 && index < 31) {
					if (!this.insertItem(itemStack2, 31, 40, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 31 && index < 40 && !this.insertItem(itemStack2, 4, 31, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 4, 40, false)) {
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

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.context.run((world, blockPos) -> this.dropInventory(playerEntity, this.input));
	}

	private void updateOutputSlot() {
		if (this.selectedPattern.get() > 0) {
			ItemStack itemStack = this.bannerSlot.getStack();
			ItemStack itemStack2 = this.dyeSlot.getStack();
			ItemStack itemStack3 = ItemStack.EMPTY;
			if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
				itemStack3 = itemStack.copy();
				itemStack3.setCount(1);
				BannerPattern bannerPattern = BannerPattern.values()[this.selectedPattern.get()];
				DyeColor dyeColor = ((DyeItem)itemStack2.getItem()).getColor();
				NbtCompound nbtCompound = itemStack3.getOrCreateSubTag("BlockEntityTag");
				NbtList nbtList;
				if (nbtCompound.contains("Patterns", NbtElement.LIST_TYPE)) {
					nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE);
				} else {
					nbtList = new NbtList();
					nbtCompound.put("Patterns", nbtList);
				}

				NbtCompound nbtCompound2 = new NbtCompound();
				nbtCompound2.putString("Pattern", bannerPattern.getId());
				nbtCompound2.putInt("Color", dyeColor.getId());
				nbtList.add(nbtCompound2);
			}

			if (!ItemStack.areEqual(itemStack3, this.outputSlot.getStack())) {
				this.outputSlot.setStack(itemStack3);
			}
		}
	}

	public Slot getBannerSlot() {
		return this.bannerSlot;
	}

	public Slot getDyeSlot() {
		return this.dyeSlot;
	}

	public Slot getPatternSlot() {
		return this.patternSlot;
	}

	public Slot getOutputSlot() {
		return this.outputSlot;
	}
}
