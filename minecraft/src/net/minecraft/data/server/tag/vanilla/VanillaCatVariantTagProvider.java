package net.minecraft.data.server.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.CatVariantTags;

public class VanillaCatVariantTagProvider extends TagProvider<CatVariant> {
	public VanillaCatVariantTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.CAT_VARIANT, registryLookupFuture);
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
