package net.minecraft.data.server.tag.onetwentyone;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;

public class OneTwentyOneBiomeTagProvider extends TagProvider<Biome> {
	public OneTwentyOneBiomeTagProvider(
		DataOutput output,
		CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture,
		CompletableFuture<TagProvider.TagLookup<Biome>> biomeTagLookupFuture
	) {
		super(output, RegistryKeys.BIOME, registryLookupFuture, biomeTagLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(BiomeTags.TRIAL_CHAMBERS_HAS_STRUCTURE).addTag(BiomeTags.IS_OVERWORLD);
	}
}
