package net.minecraft.container;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class FurnaceOutputSlot extends Slot {
	private final PlayerEntity player;
	private int field_7819;

	public FurnaceOutputSlot(PlayerEntity playerEntity, Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
		this.player = playerEntity;
	}

	@Override
	public boolean canInsert(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int i) {
		if (this.hasStack()) {
			this.field_7819 = this.field_7819 + Math.min(i, this.getStack().getAmount());
		}

		return super.takeStack(i);
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
		this.onCrafted(itemStack);
		super.onTakeItem(playerEntity, itemStack);
		return itemStack;
	}

	@Override
	protected void onCrafted(ItemStack itemStack, int i) {
		this.field_7819 += i;
		this.onCrafted(itemStack);
	}

	@Override
	protected void onCrafted(ItemStack itemStack) {
		itemStack.onCrafted(this.player.world, this.player, this.field_7819);
		if (!this.player.world.isClient && this.inventory instanceof AbstractFurnaceBlockEntity) {
			((AbstractFurnaceBlockEntity)this.inventory).method_17763(this.player);
		}

		this.field_7819 = 0;
	}
}
