package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public class CreativePlayerInventoryContainerListener implements ContainerListener {
	private final MinecraftClient client;

	public CreativePlayerInventoryContainerListener(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
	}

	@Override
	public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
		this.client.interactionManager.clickCreativeStack(itemStack, i);
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int i, int j) {
	}
}
