package net.minecraft.container;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FurnaceFuelSlot extends Slot {
	private final AbstractFurnaceContainer container;

	public FurnaceFuelSlot(AbstractFurnaceContainer abstractFurnaceContainer, Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
		this.container = abstractFurnaceContainer;
	}

	@Override
	public boolean method_7680(ItemStack itemStack) {
		return this.container.method_16945(itemStack) || method_7636(itemStack);
	}

	@Override
	public int method_7676(ItemStack itemStack) {
		return method_7636(itemStack) ? 1 : super.method_7676(itemStack);
	}

	public static boolean method_7636(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8550;
	}
}
