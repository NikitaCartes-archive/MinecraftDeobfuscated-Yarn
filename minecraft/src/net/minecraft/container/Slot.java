package net.minecraft.container;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class Slot {
	private final int invSlot;
	public final Inventory inventory;
	public int id;
	public int xPosition;
	public int yPosition;

	public Slot(Inventory inventory, int i, int j, int k) {
		this.inventory = inventory;
		this.invSlot = i;
		this.xPosition = j;
		this.yPosition = k;
	}

	public void method_7670(ItemStack itemStack, ItemStack itemStack2) {
		int i = itemStack2.getAmount() - itemStack.getAmount();
		if (i > 0) {
			this.method_7678(itemStack2, i);
		}
	}

	protected void method_7678(ItemStack itemStack, int i) {
	}

	protected void onTake(int i) {
	}

	protected void method_7669(ItemStack itemStack) {
	}

	public ItemStack method_7667(PlayerEntity playerEntity, ItemStack itemStack) {
		this.markDirty();
		return itemStack;
	}

	public boolean method_7680(ItemStack itemStack) {
		return true;
	}

	public ItemStack method_7677() {
		return this.inventory.method_5438(this.invSlot);
	}

	public boolean hasStack() {
		return !this.method_7677().isEmpty();
	}

	public void method_7673(ItemStack itemStack) {
		this.inventory.method_5447(this.invSlot, itemStack);
		this.markDirty();
	}

	public void markDirty() {
		this.inventory.markDirty();
	}

	public int getMaxStackAmount() {
		return this.inventory.getInvMaxStackAmount();
	}

	public int method_7676(ItemStack itemStack) {
		return this.getMaxStackAmount();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String getBackgroundSprite() {
		return null;
	}

	public ItemStack method_7671(int i) {
		return this.inventory.method_5434(this.invSlot, i);
	}

	public boolean canTakeItems(PlayerEntity playerEntity) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public boolean doDrawHoveringEffect() {
		return true;
	}
}
