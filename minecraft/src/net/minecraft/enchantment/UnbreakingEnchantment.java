package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class UnbreakingEnchantment extends Enchantment {
	protected UnbreakingEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.TOOL, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 5 + (i - 1) * 8;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean isAcceptableItem(ItemStack itemStack) {
		return itemStack.hasDurability() ? true : super.isAcceptableItem(itemStack);
	}

	public static boolean method_8176(ItemStack itemStack, int i, Random random) {
		return itemStack.getItem() instanceof ArmorItem && random.nextFloat() < 0.6F ? false : random.nextInt(i + 1) > 0;
	}
}
