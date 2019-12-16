package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public class CreativeInventoryListener implements ContainerListener {
	private final MinecraftClient client;

	public CreativeInventoryListener(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
	}

	@Override
	public void onContainerSlotUpdate(Container container, int slotId, ItemStack itemStack) {
		this.client.interactionManager.clickCreativeStack(itemStack, slotId);
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int propertyId, int i) {
	}
}
