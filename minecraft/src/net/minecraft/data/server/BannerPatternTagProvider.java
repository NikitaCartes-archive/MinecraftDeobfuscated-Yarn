package net.minecraft.data.server;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.data.DataGenerator;
import net.minecraft.tag.BannerPatternTags;
import net.minecraft.util.registry.Registry;

public class BannerPatternTagProvider extends AbstractTagProvider<BannerPattern> {
	public BannerPatternTagProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Registry.BANNER_PATTERN);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(BannerPatternTags.NO_ITEM_REQUIRED)
			.add(
				BannerPatterns.SQUARE_BOTTOM_LEFT,
				BannerPatterns.SQUARE_BOTTOM_RIGHT,
				BannerPatterns.SQUARE_TOP_LEFT,
				BannerPatterns.SQUARE_TOP_RIGHT,
				BannerPatterns.STRIPE_BOTTOM,
				BannerPatterns.STRIPE_TOP,
				BannerPatterns.STRIPE_LEFT,
				BannerPatterns.STRIPE_RIGHT,
				BannerPatterns.STRIPE_CENTER,
				BannerPatterns.STRIPE_MIDDLE,
				BannerPatterns.STRIPE_DOWNRIGHT,
				BannerPatterns.STRIPE_DOWNLEFT,
				BannerPatterns.SMALL_STRIPES,
				BannerPatterns.CROSS,
				BannerPatterns.STRAIGHT_CROSS,
				BannerPatterns.TRIANGLE_BOTTOM,
				BannerPatterns.TRIANGLE_TOP,
				BannerPatterns.TRIANGLES_BOTTOM,
				BannerPatterns.TRIANGLES_TOP,
				BannerPatterns.DIAGONAL_LEFT,
				BannerPatterns.DIAGONAL_UP_RIGHT,
				BannerPatterns.DIAGONAL_UP_LEFT,
				BannerPatterns.DIAGONAL_RIGHT,
				BannerPatterns.CIRCLE,
				BannerPatterns.RHOMBUS,
				BannerPatterns.HALF_VERTICAL,
				BannerPatterns.HALF_HORIZONTAL,
				BannerPatterns.HALF_VERTICAL_RIGHT,
				BannerPatterns.HALF_HORIZONTAL_BOTTOM,
				BannerPatterns.BORDER,
				BannerPatterns.CURLY_BORDER,
				BannerPatterns.GRADIENT,
				BannerPatterns.GRADIENT_UP,
				BannerPatterns.BRICKS
			);
		this.getOrCreateTagBuilder(BannerPatternTags.FLOWER_PATTERN_ITEM).add(BannerPatterns.FLOWER);
		this.getOrCreateTagBuilder(BannerPatternTags.CREEPER_PATTERN_ITEM).add(BannerPatterns.CREEPER);
		this.getOrCreateTagBuilder(BannerPatternTags.SKULL_PATTERN_ITEM).add(BannerPatterns.SKULL);
		this.getOrCreateTagBuilder(BannerPatternTags.MOJANG_PATTERN_ITEM).add(BannerPatterns.MOJANG);
		this.getOrCreateTagBuilder(BannerPatternTags.GLOBE_PATTERN_ITEM).add(BannerPatterns.GLOBE);
		this.getOrCreateTagBuilder(BannerPatternTags.PIGLIN_PATTERN_ITEM).add(BannerPatterns.PIGLIN);
	}
}
