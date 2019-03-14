package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
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
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		list.add(this.nameTextComponent().applyFormat(TextFormat.field_1080));
	}

	@Environment(EnvType.CLIENT)
	public TextComponent nameTextComponent() {
		return new TranslatableTextComponent(this.getTranslationKey() + ".desc");
	}
}
