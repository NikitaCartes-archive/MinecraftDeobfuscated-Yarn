package net.minecraft.enchantment;

public class RiptideEnchantment extends Enchantment {
	public RiptideEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.LOYALTY && other != Enchantments.CHANNELING;
	}
}
