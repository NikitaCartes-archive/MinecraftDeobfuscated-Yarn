package net.minecraft.item;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.tag.TagKey;

public class BannerPatternItem extends Item {
	private final TagKey<BannerPattern> patternItemTag;

	public BannerPatternItem(TagKey<BannerPattern> patternItemTag, Item.Settings settings) {
		super(settings);
		this.patternItemTag = patternItemTag;
	}

	public TagKey<BannerPattern> getPattern() {
		return this.patternItemTag;
	}
}
