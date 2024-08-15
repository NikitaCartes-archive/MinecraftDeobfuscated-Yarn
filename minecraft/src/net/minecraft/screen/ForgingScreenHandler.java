package net.minecraft.screen;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.screen.slot.Slot;

public abstract class ForgingScreenHandler extends ScreenHandler {
	private static final int field_41901 = 9;
	private static final int field_41902 = 3;
	protected final ScreenHandlerContext context;
	protected final PlayerEntity player;
	protected final Inventory input;
	private final List<Integer> inputSlotIndices;
	protected final CraftingResultInventory output = new CraftingResultInventory();
	private final int resultSlotIndex;

	protected abstract boolean canTakeOutput(PlayerEntity player, boolean present);

	protected abstract void onTakeOutput(PlayerEntity player, ItemStack stack);

	protected abstract boolean canUse(BlockState state);

	public ForgingScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId);
		this.context = context;
		this.player = playerInventory.player;
		ForgingSlotsManager forgingSlotsManager = this.getForgingSlotsManager();
		this.input = this.createInputInventory(forgingSlotsManager.getInputSlotCount());
		this.inputSlotIndices = forgingSlotsManager.getInputSlotIndices();
		this.resultSlotIndex = forgingSlotsManager.getResultSlotIndex();
		this.addInputSlots(forgingSlotsManager);
		this.addResultSlot(forgingSlotsManager);
		this.addPlayerSlots(playerInventory, 8, 84);
	}

	private void addInputSlots(ForgingSlotsManager forgingSlotsManager) {
		for (final ForgingSlotsManager.ForgingSlot forgingSlot : forgingSlotsManager.getInputSlots()) {
			this.addSlot(new Slot(this.input, forgingSlot.slotId(), forgingSlot.x(), forgingSlot.y()) {
				@Override
				public boolean canInsert(ItemStack stack) {
					return forgingSlot.mayPlace().test(stack);
				}
			});
		}
	}

	private void addResultSlot(ForgingSlotsManager forgingSlotsManager) {
		this.addSlot(
			new Slot(this.output, forgingSlotsManager.getResultSlot().slotId(), forgingSlotsManager.getResultSlot().x(), forgingSlotsManager.getResultSlot().y()) {
				@Override
				public boolean canInsert(ItemStack stack) {
					return false;
				}

				@Override
				public boolean canTakeItems(PlayerEntity playerEntity) {
					return ForgingScreenHandler.this.canTakeOutput(playerEntity, this.hasStack());
				}

				@Override
				public void onTakeItem(PlayerEntity player, ItemStack stack) {
					ForgingScreenHandler.this.onTakeOutput(player, stack);
				}
			}
		);
	}

	public abstract void updateResult();

	protected abstract ForgingSlotsManager getForgingSlotsManager();

	private SimpleInventory createInputInventory(int size) {
		return new SimpleInventory(size) {
			@Override
			public void markDirty() {
				super.markDirty();
				ForgingScreenHandler.this.onContentChanged(this);
			}
		};
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
		if (inventory == this.input) {
			this.updateResult();
		}
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.input));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.context.get((world, pos) -> !this.canUse(world.getBlockState(pos)) ? false : player.canInteractWithBlockAt(pos, 4.0), true);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			int i = this.getPlayerInventoryStartIndex();
			int j = this.getPlayerHotbarEndIndex();
			if (slot == this.getResultSlotIndex()) {
				if (!this.insertItem(itemStack2, i, j, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
			} else if (this.inputSlotIndices.contains(slot)) {
				if (!this.insertItem(itemStack2, i, j, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.isValidIngredient(itemStack2) && slot >= this.getPlayerInventoryStartIndex() && slot < this.getPlayerHotbarEndIndex()) {
				int k = this.getSlotFor(itemStack);
				if (!this.insertItem(itemStack2, k, this.getResultSlotIndex(), false)) {
					return ItemStack.EMPTY;
				}
			} else if (slot >= this.getPlayerInventoryStartIndex() && slot < this.getPlayerInventoryEndIndex()) {
				if (!this.insertItem(itemStack2, this.getPlayerHotbarStartIndex(), this.getPlayerHotbarEndIndex(), false)) {
					return ItemStack.EMPTY;
				}
			} else if (slot >= this.getPlayerHotbarStartIndex()
				&& slot < this.getPlayerHotbarEndIndex()
				&& !this.insertItem(itemStack2, this.getPlayerInventoryStartIndex(), this.getPlayerInventoryEndIndex(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			} else {
				slot2.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	protected boolean isValidIngredient(ItemStack stack) {
		return true;
	}

	public int getSlotFor(ItemStack stack) {
		return this.input.isEmpty() ? 0 : (Integer)this.inputSlotIndices.get(0);
	}

	public int getResultSlotIndex() {
		return this.resultSlotIndex;
	}

	private int getPlayerInventoryStartIndex() {
		return this.getResultSlotIndex() + 1;
	}

	private int getPlayerInventoryEndIndex() {
		return this.getPlayerInventoryStartIndex() + 27;
	}

	private int getPlayerHotbarStartIndex() {
		return this.getPlayerInventoryEndIndex();
	}

	private int getPlayerHotbarEndIndex() {
		return this.getPlayerHotbarStartIndex() + 9;
	}
}
