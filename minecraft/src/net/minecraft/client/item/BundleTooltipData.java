package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class BundleTooltipData implements TooltipData {
	private final DefaultedList<ItemStack> inventory;
	private final boolean hasSpace;

	public BundleTooltipData(DefaultedList<ItemStack> inventory, boolean hasSpace) {
		this.inventory = inventory;
		this.hasSpace = hasSpace;
	}

	public DefaultedList<ItemStack> getInventory() {
		return this.inventory;
	}

	public boolean hasSpace() {
		return this.hasSpace;
	}
}
