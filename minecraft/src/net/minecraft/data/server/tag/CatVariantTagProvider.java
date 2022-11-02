package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.tag.CatVariantTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryWrapper;

public class CatVariantTagProvider extends AbstractTagProvider<CatVariant> {
	public CatVariantTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, Registry.CAT_VARIANT_KEY, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
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
