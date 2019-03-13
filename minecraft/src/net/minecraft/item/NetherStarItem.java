package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class NetherStarItem extends Item {
	public NetherStarItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(ItemStack itemStack) {
		return true;
	}
}
