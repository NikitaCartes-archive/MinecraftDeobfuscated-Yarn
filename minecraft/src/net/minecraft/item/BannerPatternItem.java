package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

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
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.getDescription().formatted(Formatting.GRAY));
	}

	public MutableText getDescription() {
		return Text.translatable(this.getTranslationKey() + ".desc");
	}
}
