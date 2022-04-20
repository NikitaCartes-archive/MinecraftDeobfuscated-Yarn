package net.minecraft.tag;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PaintingVariantTags {
	public static final TagKey<PaintingVariant> PLACEABLE = of("placeable");

	private PaintingVariantTags() {
	}

	private static TagKey<PaintingVariant> of(String id) {
		return TagKey.of(Registry.PAINTING_VARIANT_KEY, new Identifier(id));
	}
}
