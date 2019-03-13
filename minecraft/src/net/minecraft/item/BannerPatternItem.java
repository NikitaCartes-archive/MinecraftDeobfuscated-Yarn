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
	private final BannerPattern field_7900;

	public BannerPatternItem(BannerPattern bannerPattern, Item.Settings settings) {
		super(settings);
		this.field_7900 = bannerPattern;
	}

	public BannerPattern method_7704() {
		return this.field_7900;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		list.add(this.method_7703().applyFormat(TextFormat.field_1080));
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_7703() {
		return new TranslatableTextComponent(this.getTranslationKey() + ".desc");
	}
}
