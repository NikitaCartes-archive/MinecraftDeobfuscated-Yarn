package net.minecraft.block.entity;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public record BannerPattern() {
	public static Identifier getSpriteId(RegistryKey<BannerPattern> pattern, boolean banner) {
		String string = banner ? "banner" : "shield";
		return pattern.getValue().withPrefixedPath("entity/" + string + "/");
	}
}
