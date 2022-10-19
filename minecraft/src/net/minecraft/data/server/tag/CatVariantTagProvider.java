package net.minecraft.data.server.tag;

import net.minecraft.data.DataOutput;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.tag.CatVariantTags;
import net.minecraft.util.registry.Registry;

public class CatVariantTagProvider extends AbstractTagProvider<CatVariant> {
	public CatVariantTagProvider(DataOutput dataGenerator) {
		super(dataGenerator, Registry.CAT_VARIANT);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(CatVariantTags.DEFAULT_SPAWNS)
			.add(
				CatVariant.TABBY,
				CatVariant.BLACK,
				CatVariant.RED,
				CatVariant.SIAMESE,
				CatVariant.BRITISH_SHORTHAIR,
				CatVariant.CALICO,
				CatVariant.PERSIAN,
				CatVariant.RAGDOLL,
				CatVariant.WHITE,
				CatVariant.JELLIE
			);
		this.getOrCreateTagBuilder(CatVariantTags.FULL_MOON_SPAWNS).addTag(CatVariantTags.DEFAULT_SPAWNS).add(CatVariant.ALL_BLACK);
	}
}
