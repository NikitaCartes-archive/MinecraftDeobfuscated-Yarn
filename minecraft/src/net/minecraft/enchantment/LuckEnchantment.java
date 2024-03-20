package net.minecraft.enchantment;

public class LuckEnchantment extends Enchantment {
	protected LuckEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.SILK_TOUCH;
	}
}
