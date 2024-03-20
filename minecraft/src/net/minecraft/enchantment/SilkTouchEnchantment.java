package net.minecraft.enchantment;

public class SilkTouchEnchantment extends Enchantment {
	protected SilkTouchEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.FORTUNE;
	}
}
