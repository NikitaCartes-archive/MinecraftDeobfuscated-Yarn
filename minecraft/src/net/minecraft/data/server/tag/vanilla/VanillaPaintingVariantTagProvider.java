package net.minecraft.data.server.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.PaintingVariantTags;

public class VanillaPaintingVariantTagProvider extends TagProvider<PaintingVariant> {
	public VanillaPaintingVariantTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.PAINTING_VARIANT, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
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
