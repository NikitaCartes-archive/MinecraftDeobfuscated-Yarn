package net.minecraft.item;

public class NetherStarItem extends Item {
	public NetherStarItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}
}
