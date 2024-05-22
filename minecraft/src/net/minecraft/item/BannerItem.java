package net.minecraft.item;

import java.util.List;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.Validate;

public class BannerItem extends VerticallyAttachableBlockItem {
	public BannerItem(Block bannerBlock, Block wallBannerBlock, Item.Settings settings) {
		super(bannerBlock, wallBannerBlock, settings, Direction.DOWN);
		Validate.isInstanceOf(AbstractBannerBlock.class, bannerBlock);
		Validate.isInstanceOf(AbstractBannerBlock.class, wallBannerBlock);
	}

	public static void appendBannerTooltip(ItemStack stack, List<Text> tooltip) {
		BannerPatternsComponent bannerPatternsComponent = stack.get(DataComponentTypes.BANNER_PATTERNS);
		if (bannerPatternsComponent != null) {
			for (int i = 0; i < Math.min(bannerPatternsComponent.layers().size(), 6); i++) {
				BannerPatternsComponent.Layer layer = (BannerPatternsComponent.Layer)bannerPatternsComponent.layers().get(i);
				tooltip.add(layer.getTooltipText().formatted(Formatting.GRAY));
			}
		}
	}

	public DyeColor getColor() {
		return ((AbstractBannerBlock)this.getBlock()).getColor();
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		appendBannerTooltip(stack, tooltip);
	}
}
