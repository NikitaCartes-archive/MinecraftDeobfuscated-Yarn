package net.minecraft.item;

public class BookItem extends Item {
	public BookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isEnchantable(ItemStack itemStack) {
		return itemStack.getCount() == 1;
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
