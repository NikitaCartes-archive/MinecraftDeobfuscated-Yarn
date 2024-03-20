package net.minecraft.enchantment;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

public class UnbreakingEnchantment extends Enchantment {
	protected UnbreakingEnchantment(Enchantment.Properties properties) {
		super(properties);
	}

	public static boolean shouldPreventDamage(ItemStack item, int level, Random random) {
		if (item.getItem() instanceof ArmorItem && random.nextFloat() < 0.6F) {
			return false;
		} else {
			return random.nextInt(level + 1) > 0;
		}
	}
}
