package net.minecraft.enchantment;

public class BindingCurseEnchantment extends Enchantment {
	public BindingCurseEnchantment(Enchantment.Properties properties) {
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
