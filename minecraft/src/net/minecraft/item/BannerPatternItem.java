package net.minecraft.item;

import java.util.List;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BannerPatternItem extends Item {
	private final TagKey<BannerPattern> patternItemTag;

	public BannerPatternItem(TagKey<BannerPattern> patternItemTag, Item.Settings settings) {
		super(settings);
		this.patternItemTag = patternItemTag;
	}

	public TagKey<BannerPattern> getPattern() {
		return this.patternItemTag;
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		tooltip.add(this.getDescription().formatted(Formatting.GRAY));
	}

	public MutableText getDescription() {
		return Text.translatable(this.getTranslationKey() + ".desc");
	}
}
