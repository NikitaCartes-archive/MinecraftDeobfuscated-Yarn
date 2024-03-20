package net.minecraft.enchantment;

public class PiercingEnchantment extends Enchantment {
	public PiercingEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.MULTISHOT;
	}
}
