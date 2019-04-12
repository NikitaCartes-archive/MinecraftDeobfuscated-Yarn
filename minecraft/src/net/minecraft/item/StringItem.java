package net.minecraft.item;

import net.minecraft.block.Block;

public class StringItem extends BlockItem {
	public StringItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}
}
