package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class BundleTooltipData implements TooltipData {
	private final DefaultedList<ItemStack> inventory;
	private final int field_28353;

	public BundleTooltipData(DefaultedList<ItemStack> inventory, int i) {
		this.inventory = inventory;
		this.field_28353 = i;
	}

	public DefaultedList<ItemStack> getInventory() {
		return this.inventory;
	}

	public int hasSpace() {
		return this.field_28353;
	}
}
