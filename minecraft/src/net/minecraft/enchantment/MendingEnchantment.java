package net.minecraft.enchantment;

public class MendingEnchantment extends Enchantment {
	public MendingEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	@Override
	public boolean isTreasure() {
		return true;
	}
}
