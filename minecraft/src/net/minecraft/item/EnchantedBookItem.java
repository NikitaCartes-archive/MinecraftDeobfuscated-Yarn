package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentLevelEntry;

public class EnchantedBookItem extends Item {
	public EnchantedBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public static ItemStack forEnchantment(EnchantmentLevelEntry info) {
		ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
		itemStack.addEnchantment(info.enchantment, info.level);
		return itemStack;
	}
}
