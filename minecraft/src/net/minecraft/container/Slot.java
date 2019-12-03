package net.minecraft.container;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Slot {
	private final int invSlot;
	public final Inventory inventory;
	public int id;
	public final int xPosition;
	public final int yPosition;

	public Slot(Inventory inventory, int invSlot, int xPosition, int yPosition) {
		this.inventory = inventory;
		this.invSlot = invSlot;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	public void onStackChanged(ItemStack originalItem, ItemStack itemStack) {
		int i = itemStack.getCount() - originalItem.getCount();
		if (i > 0) {
			this.onCrafted(itemStack, i);
		}
	}

	protected void onCrafted(ItemStack stack, int amount) {
	}

	protected void onTake(int amount) {
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
		return this.inventory.getInvStack(this.invSlot);
	}

	public boolean hasStack() {
		return !this.getStack().isEmpty();
	}

	public void setStack(ItemStack itemStack) {
		this.inventory.setInvStack(this.invSlot, itemStack);
		this.markDirty();
	}

	public void markDirty() {
		this.inventory.markDirty();
	}

	public int getMaxStackAmount() {
		return this.inventory.getInvMaxStackAmount();
	}

	public int getMaxStackAmount(ItemStack itemStack) {
		return this.getMaxStackAmount();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Pair<Identifier, Identifier> getBackgroundSprite() {
		return null;
	}

	public ItemStack takeStack(int amount) {
		return this.inventory.takeInvStack(this.invSlot, amount);
	}

	public boolean canTakeItems(PlayerEntity playerEntity) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public boolean doDrawHoveringEffect() {
		return true;
	}
}
