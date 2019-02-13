package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public class class_478 implements ContainerListener {
	private final MinecraftClient field_2876;

	public class_478(MinecraftClient minecraftClient) {
		this.field_2876 = minecraftClient;
	}

	@Override
	public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
	}

	@Override
	public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
		this.field_2876.interactionManager.method_2909(itemStack, i);
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int i, int j) {
	}
}
