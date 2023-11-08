package net.minecraft.screen.slot;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class CrafterOutputSlot extends Slot {
	public CrafterOutputSlot(Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
	}

	@Override
	public void onQuickTransfer(ItemStack newItem, ItemStack original) {
	}

	@Override
	public boolean canTakeItems(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public Optional<ItemStack> tryTakeStackRange(int min, int max, PlayerEntity player) {
		return Optional.empty();
	}

	@Override
	public ItemStack takeStackRange(int min, int max, PlayerEntity player) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertStack(ItemStack stack) {
		return stack;
	}

	@Override
	public ItemStack insertStack(ItemStack stack, int count) {
		return this.insertStack(stack);
	}

	@Override
	public boolean canTakePartial(PlayerEntity player) {
		return false;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public void onTakeItem(PlayerEntity player, ItemStack stack) {
	}

	@Override
	public boolean canBeHighlighted() {
		return false;
	}

	@Override
	public boolean disablesDynamicDisplay() {
		return true;
	}
}
