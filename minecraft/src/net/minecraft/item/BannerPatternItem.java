package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class BannerPatternItem extends Item {
	private final BannerPattern pattern;

	public BannerPatternItem(BannerPattern pattern, Item.Settings settings) {
		super(settings);
		this.pattern = pattern;
	}

	public BannerPattern getPattern() {
		return this.pattern;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.getDescription().formatted(Formatting.GRAY));
	}

	public MutableText getDescription() {
		return new TranslatableText(this.getTranslationKey() + ".desc");
	}
}
