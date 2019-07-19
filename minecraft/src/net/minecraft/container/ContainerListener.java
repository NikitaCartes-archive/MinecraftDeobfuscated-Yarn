package net.minecraft.container;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public interface ContainerListener {
	void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList);

	void onContainerSlotUpdate(Container container, int slotId, ItemStack itemStack);

	void onContainerPropertyUpdate(Container container, int propertyId, int i);
}
