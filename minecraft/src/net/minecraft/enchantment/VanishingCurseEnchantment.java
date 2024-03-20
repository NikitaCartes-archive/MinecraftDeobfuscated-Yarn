package net.minecraft.enchantment;

public class VanishingCurseEnchantment extends Enchantment {
	public VanishingCurseEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public boolean isCursed() {
		return true;
	}
}
