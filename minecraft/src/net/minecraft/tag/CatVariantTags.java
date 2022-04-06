package net.minecraft.tag;

import net.minecraft.entity.passive.CatVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CatVariantTags {
	public static final TagKey<CatVariant> DEFAULT_SPAWNS = of("default_spawns");
	public static final TagKey<CatVariant> FULL_MOON_SPAWNS = of("full_moon_spawns");

	private CatVariantTags() {
	}

	private static TagKey<CatVariant> of(String id) {
		return TagKey.of(Registry.CAT_VARIANT_KEY, new Identifier(id));
	}
}
