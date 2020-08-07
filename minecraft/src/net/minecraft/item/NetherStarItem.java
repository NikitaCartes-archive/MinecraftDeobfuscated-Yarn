package net.minecraft.item;

public class NetherStarItem extends Item {
	public NetherStarItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}
}
