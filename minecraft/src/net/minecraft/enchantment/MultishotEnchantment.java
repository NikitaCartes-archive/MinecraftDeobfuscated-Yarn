package net.minecraft.enchantment;

public class MultishotEnchantment extends Enchantment {
	public MultishotEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.PIERCING;
	}
}
