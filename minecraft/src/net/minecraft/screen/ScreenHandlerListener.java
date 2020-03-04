package net.minecraft.screen;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public interface ScreenHandlerListener {
	void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> defaultedList);

	void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack itemStack);

	void onPropertyUpdate(ScreenHandler handler, int propertyId, int i);
}
