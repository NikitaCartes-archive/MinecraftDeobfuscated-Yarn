package net.minecraft.screen.slot;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Slot {
	private final int index;
	public final Inventory inventory;
	public int id;
	public final int x;
	public final int y;

	public Slot(Inventory inventory, int index, int x, int y) {
		this.inventory = inventory;
		this.index = index;
		this.x = x;
		this.y = y;
	}

	public void onStackChanged(ItemStack originalItem, ItemStack newItem) {
		int i = newItem.getCount() - originalItem.getCount();
		if (i > 0) {
			this.onCrafted(newItem, i);
		}
	}

	protected void onCrafted(ItemStack stack, int amount) {
	}

	public void onTake(int amount) {
	}

	protected void onCrafted(ItemStack stack) {
	}

	public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
		this.markDirty();
		return stack;
	}

	public boolean canInsert(ItemStack stack) {
		return true;
	}

	public ItemStack getStack() {
		return this.inventory.getStack(this.index);
	}

	public boolean hasStack() {
		return !this.getStack().isEmpty();
	}

	public void setStack(ItemStack stack) {
		this.inventory.setStack(this.index, stack);
		this.markDirty();
	}

	public void markDirty() {
		this.inventory.markDirty();
	}

	public int getMaxItemCount() {
		return this.inventory.getMaxCountPerStack();
	}

	public int getMaxItemCount(ItemStack stack) {
		return Math.min(this.getMaxItemCount(), stack.getMaxCount());
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Pair<Identifier, Identifier> getBackgroundSprite() {
		return null;
	}

	public ItemStack takeStack(int amount) {
		return this.inventory.removeStack(this.index, amount);
	}

	public boolean canTakeItems(PlayerEntity playerEntity) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public boolean doDrawHoveringEffect() {
		return true;
	}

	public ItemStack method_32753(int i, int j, PlayerEntity playerEntity) {
		if (!this.canTakeItems(playerEntity)) {
			return ItemStack.EMPTY;
		} else if (!this.method_32754(playerEntity) && j < this.getStack().getCount()) {
			return ItemStack.EMPTY;
		} else {
			if (!this.method_32754(playerEntity)) {
				i = this.getStack().getCount();
			}

			i = Math.min(i, j);
			ItemStack itemStack = this.takeStack(i);
			if (this.getStack().isEmpty()) {
				this.setStack(ItemStack.EMPTY);
			}

			this.onTakeItem(playerEntity, itemStack);
			return itemStack;
		}
	}

	public ItemStack method_32756(ItemStack itemStack) {
		return this.method_32755(itemStack, itemStack.getCount());
	}

	public ItemStack method_32755(ItemStack itemStack, int i) {
		if (!itemStack.isEmpty() && this.canInsert(itemStack)) {
			ItemStack itemStack2 = this.getStack();
			int j = Math.min(Math.min(i, itemStack.getCount()), this.getMaxItemCount(itemStack) - itemStack2.getCount());
			if (itemStack2.isEmpty()) {
				this.setStack(itemStack.split(j));
			} else if (ItemStack.canCombine(itemStack2, itemStack)) {
				itemStack.decrement(j);
				itemStack2.increment(j);
				this.setStack(itemStack2);
			}

			return itemStack;
		} else {
			return itemStack;
		}
	}

	public boolean method_32754(PlayerEntity playerEntity) {
		return this.canTakeItems(playerEntity) && this.canInsert(this.getStack());
	}
}
