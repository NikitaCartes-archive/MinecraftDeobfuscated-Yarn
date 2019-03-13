package net.minecraft.container;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public interface ContainerListener {
	void method_7634(Container container, DefaultedList<ItemStack> defaultedList);

	void method_7635(Container container, int i, ItemStack itemStack);

	void onContainerPropertyUpdate(Container container, int i, int j);
}
