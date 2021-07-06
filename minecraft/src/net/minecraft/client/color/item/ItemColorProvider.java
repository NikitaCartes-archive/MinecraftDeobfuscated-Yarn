package net.minecraft.client.color.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public interface ItemColorProvider {
	/**
	 * {@return the color of the item stack for the specified tint index,
	 * or -1 if not tinted}
	 */
	int getColor(ItemStack stack, int tintIndex);
}
