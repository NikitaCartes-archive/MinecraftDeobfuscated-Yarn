package net.minecraft.item;

import net.minecraft.class_8293;
import net.minecraft.class_8324;
import net.minecraft.block.Block;

public class AliasedBlockItem extends BlockItem {
	public AliasedBlockItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	public String getTranslationKey() {
		class_8324 lv = class_8293.field_43616.method_50145();
		if (lv != class_8324.ANY && this.foodComponent != null) {
			if (lv.method_50352() == this) {
				return lv.method_50354();
			}

			if (class_8324.field_43836.contains(this)) {
				return "rule.food_restriction.inedible." + this;
			}
		}

		return this.getOrCreateTranslationKey();
	}
}
