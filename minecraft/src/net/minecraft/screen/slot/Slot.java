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

	public void onStackChanged(ItemStack originalItem, ItemStack itemStack) {
		int i = itemStack.getCount() - originalItem.getCount();
		if (i > 0) {
			this.onCrafted(itemStack, i);
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
		return this.inventory.getInvStack(this.index);
	}

	public boolean hasStack() {
		return !this.getStack().isEmpty();
	}

	public void setStack(ItemStack stack) {
		this.inventory.setInvStack(this.index, stack);
		this.markDirty();
	}

	public void markDirty() {
		this.inventory.markDirty();
	}

	public int getMaxStackAmount() {
		return this.inventory.getInvMaxStackAmount();
	}

	public int getMaxStackAmount(ItemStack stack) {
		return this.getMaxStackAmount();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Pair<Identifier, Identifier> getBackgroundSprite() {
		return null;
	}

	public ItemStack takeStack(int amount) {
		return this.inventory.takeInvStack(this.index, amount);
	}

	public boolean canTakeItems(PlayerEntity playerEntity) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public boolean doDrawHoveringEffect() {
		return true;
	}
}
