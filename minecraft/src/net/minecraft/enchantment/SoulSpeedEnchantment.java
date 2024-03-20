package net.minecraft.enchantment;

public class SoulSpeedEnchantment extends Enchantment {
	public SoulSpeedEnchantment(Enchantment.Properties properties) {
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
