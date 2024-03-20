package net.minecraft.enchantment;

public class SwiftSneakEnchantment extends Enchantment {
	public SwiftSneakEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public boolean isAvailableForEnchantedBookOffer() {
		return false;
	}

	@Override
	public boolean isAvailableForRandomSelection() {
		return false;
	}
}
