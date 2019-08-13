package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class UnbreakingEnchantment extends Enchantment {
	protected UnbreakingEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9082, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 5 + (i - 1) * 8;
	}

	@Override
	public int getMaximumPower(int i) {
		return super.getMinimumPower(i) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean isAcceptableItem(ItemStack itemStack) {
		return itemStack.isDamageable() ? true : super.isAcceptableItem(itemStack);
	}

	public static boolean shouldPreventDamage(ItemStack itemStack, int i, Random random) {
		return itemStack.getItem() instanceof ArmorItem && random.nextFloat() < 0.6F ? false : random.nextInt(i + 1) > 0;
	}
}
