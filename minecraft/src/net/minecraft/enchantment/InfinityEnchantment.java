package net.minecraft.enchantment;

public class InfinityEnchantment extends Enchantment {
	public InfinityEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return other instanceof MendingEnchantment ? false : super.canAccept(other);
	}
}
