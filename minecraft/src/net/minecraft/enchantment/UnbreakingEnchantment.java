package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class UnbreakingEnchantment extends Enchantment {
	protected UnbreakingEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.BREAKABLE, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 5 + (level - 1) * 8;
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.isDamageable() ? true : super.isAcceptableItem(stack);
	}

	public static boolean shouldPreventDamage(ItemStack item, int level, Random random) {
		return item.getItem() instanceof ArmorItem && random.nextFloat() < 0.6F ? false : random.nextInt(level + 1) > 0;
	}
}
