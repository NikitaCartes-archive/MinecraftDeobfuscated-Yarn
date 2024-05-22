package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class AirBlockItem extends Item {
	private final Block block;

	public AirBlockItem(Block block, Item.Settings settings) {
		super(settings);
		this.block = block;
	}

	@Override
	public String getTranslationKey() {
		return this.block.getTranslationKey();
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);
		this.block.appendTooltip(stack, context, tooltip, type);
	}
}
