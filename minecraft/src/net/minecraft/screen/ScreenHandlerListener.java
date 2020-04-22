package net.minecraft.screen;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ScreenHandlerListener {
	void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> stacks);

	void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack);

	void onPropertyUpdate(ScreenHandler handler, int property, int value);
}
