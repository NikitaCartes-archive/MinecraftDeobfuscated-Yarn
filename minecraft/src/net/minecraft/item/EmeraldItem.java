package net.minecraft.item;

import net.minecraft.class_8293;

public class EmeraldItem extends Item {
	public EmeraldItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return class_8293.field_43608.method_50116() ? "item.minecraft.ruby" : super.getTranslationKey(stack);
	}
}
