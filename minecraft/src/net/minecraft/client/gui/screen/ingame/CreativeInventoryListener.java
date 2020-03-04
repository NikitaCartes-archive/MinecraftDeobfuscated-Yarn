package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public class CreativeInventoryListener implements ScreenHandlerListener {
	private final MinecraftClient client;

	public CreativeInventoryListener(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> defaultedList) {
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack itemStack) {
		this.client.interactionManager.clickCreativeStack(itemStack, slotId);
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int propertyId, int i) {
	}
}
