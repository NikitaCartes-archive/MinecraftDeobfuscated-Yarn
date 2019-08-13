package net.minecraft.enchantment;

import net.minecraft.util.WeightedPicker;

public class InfoEnchantment extends WeightedPicker.Entry {
	public final Enchantment enchantment;
	public final int level;

	public InfoEnchantment(Enchantment enchantment, int i) {
		super(enchantment.getWeight().getWeight());
		this.enchantment = enchantment;
		this.level = i;
	}
}
