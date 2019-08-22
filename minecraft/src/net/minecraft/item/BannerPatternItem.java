package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class BannerPatternItem extends Item {
	private final BannerPattern pattern;

	public BannerPatternItem(BannerPattern bannerPattern, Item.Settings settings) {
		super(settings);
		this.pattern = bannerPattern;
	}

	public BannerPattern getPattern() {
		return this.pattern;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		list.add(this.getDescription().formatted(Formatting.GRAY));
	}

	@Environment(EnvType.CLIENT)
	public Text getDescription() {
		return new TranslatableText(this.getTranslationKey() + ".desc");
	}
}
