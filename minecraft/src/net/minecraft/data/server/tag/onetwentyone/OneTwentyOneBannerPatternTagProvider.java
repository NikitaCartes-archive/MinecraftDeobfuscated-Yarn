package net.minecraft.data.server.tag.onetwentyone;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BannerPatternTags;

public class OneTwentyOneBannerPatternTagProvider extends TagProvider<BannerPattern> {
	public OneTwentyOneBannerPatternTagProvider(
		DataOutput output,
		CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture,
		CompletableFuture<TagProvider.TagLookup<BannerPattern>> bannerPatternTagLookupFuture
	) {
		super(output, RegistryKeys.BANNER_PATTERN, registryLookupFuture, bannerPatternTagLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(BannerPatternTags.FLOW_PATTERN_ITEM).add(BannerPatterns.FLOW);
		this.getOrCreateTagBuilder(BannerPatternTags.GUSTER_PATTERN_ITEM).add(BannerPatterns.GUSTER);
	}
}
