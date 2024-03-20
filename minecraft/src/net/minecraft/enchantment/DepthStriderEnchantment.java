package net.minecraft.enchantment;

public class DepthStriderEnchantment extends Enchantment {
	public DepthStriderEnchantment(Enchantment.Properties weight) {
		super(weight);
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.FROST_WALKER;
	}
}
