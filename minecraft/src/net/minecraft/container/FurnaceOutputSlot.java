package net.minecraft.container;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class FurnaceOutputSlot extends Slot {
	private final PlayerEntity player;
	private int amountCrafted;

	public FurnaceOutputSlot(PlayerEntity playerEntity, Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
		this.player = playerEntity;
	}

	@Override
	public boolean method_7680(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack method_7671(int i) {
		if (this.hasStack()) {
			this.amountCrafted = this.amountCrafted + Math.min(i, this.method_7677().getAmount());
		}

		return super.method_7671(i);
	}

	@Override
	public ItemStack method_7667(PlayerEntity playerEntity, ItemStack itemStack) {
		this.method_7669(itemStack);
		super.method_7667(playerEntity, itemStack);
		return itemStack;
	}

	@Override
	protected void method_7678(ItemStack itemStack, int i) {
		this.amountCrafted += i;
		this.method_7669(itemStack);
	}

	@Override
	protected void method_7669(ItemStack itemStack) {
		itemStack.method_7982(this.player.field_6002, this.player, this.amountCrafted);
		if (!this.player.field_6002.isClient && this.inventory instanceof AbstractFurnaceBlockEntity) {
			((AbstractFurnaceBlockEntity)this.inventory).dropExperience(this.player);
		}

		this.amountCrafted = 0;
	}
}
