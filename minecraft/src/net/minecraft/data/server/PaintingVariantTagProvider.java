package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.tag.PaintingVariantTags;
import net.minecraft.util.registry.Registry;

public class PaintingVariantTagProvider extends AbstractTagProvider<PaintingVariant> {
	public PaintingVariantTagProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Registry.PAINTING_VARIANT);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(PaintingVariantTags.PLACEABLE)
			.add(
				PaintingVariants.KEBAB,
				PaintingVariants.AZTEC,
				PaintingVariants.ALBAN,
				PaintingVariants.AZTEC2,
				PaintingVariants.BOMB,
				PaintingVariants.PLANT,
				PaintingVariants.WASTELAND,
				PaintingVariants.POOL,
				PaintingVariants.COURBET,
				PaintingVariants.SEA,
				PaintingVariants.SUNSET,
				PaintingVariants.CREEBET,
				PaintingVariants.WANDERER,
				PaintingVariants.GRAHAM,
				PaintingVariants.MATCH,
				PaintingVariants.BUST,
				PaintingVariants.STAGE,
				PaintingVariants.VOID,
				PaintingVariants.SKULL_AND_ROSES,
				PaintingVariants.WITHER,
				PaintingVariants.FIGHTERS,
				PaintingVariants.POINTER,
				PaintingVariants.PIGSCENE,
				PaintingVariants.BURNING_SKULL,
				PaintingVariants.SKELETON,
				PaintingVariants.DONKEY_KONG
			);
	}
}
