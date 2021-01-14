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

	public void onQuickTransfer(ItemStack newItem, ItemStack original) {
		int i = original.getCount() - newItem.getCount();
		if (i > 0) {
			this.onCrafted(original, i);
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
		return this.getMaxItemCount();
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
}
