package net.minecraft.item;

public class BookItem extends Item {
	public BookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isTool(ItemStack itemStack) {
		return itemStack.getAmount() == 1;
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
