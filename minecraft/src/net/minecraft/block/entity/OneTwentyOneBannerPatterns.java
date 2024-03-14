package net.minecraft.block.entity;

import net.minecraft.registry.Registerable;

public interface OneTwentyOneBannerPatterns {
	static void bootstrap(Registerable<BannerPattern> bannerPatternRegisterable) {
		BannerPatterns.register(bannerPatternRegisterable, BannerPatterns.FLOW);
		BannerPatterns.register(bannerPatternRegisterable, BannerPatterns.GUSTER);
	}
}
