package net.minecraft.item;

import net.minecraft.block.Block;

public class AliasedBlockItem extends BlockItem {
	public AliasedBlockItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}
}
