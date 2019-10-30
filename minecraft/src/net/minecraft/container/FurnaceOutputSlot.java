package net.minecraft.container;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class FurnaceOutputSlot extends Slot {
	private final PlayerEntity player;
	private int amount;

	public FurnaceOutputSlot(PlayerEntity player, Inventory inventory, int invSlot, int xPosition, int yPosition) {
		super(inventory, invSlot, xPosition, yPosition);
		this.player = player;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int amount) {
		if (this.hasStack()) {
			this.amount = this.amount + Math.min(amount, this.getStack().getCount());
		}

		return super.takeStack(amount);
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
		this.onCrafted(stack);
		super.onTakeItem(player, stack);
		return stack;
	}

	@Override
	protected void onCrafted(ItemStack stack, int amount) {
		this.amount += amount;
		this.onCrafted(stack);
	}

	@Override
	protected void onCrafted(ItemStack stack) {
		stack.onCraft(this.player.world, this.player, this.amount);
		if (!this.player.world.isClient && this.inventory instanceof AbstractFurnaceBlockEntity) {
			((AbstractFurnaceBlockEntity)this.inventory).dropExperience(this.player);
		}

		this.amount = 0;
	}
}
