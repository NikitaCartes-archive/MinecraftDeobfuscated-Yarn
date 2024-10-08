package net.minecraft.data.server.tag.winterdrop;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.WinterDropBuiltinBiomes;

public class WinterDropBiomeTagProvider extends TagProvider<Biome> {
	public WinterDropBiomeTagProvider(
		DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, CompletableFuture<TagProvider.TagLookup<Biome>> parentTagLookupFuture
	) {
		super(output, RegistryKeys.BIOME, registriesFuture, parentTagLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries) {
		this.getOrCreateTagBuilder(BiomeTags.IS_FOREST).add(WinterDropBuiltinBiomes.PALE_GARDEN);
		this.getOrCreateTagBuilder(BiomeTags.STRONGHOLD_BIASED_TO).add(WinterDropBuiltinBiomes.PALE_GARDEN);
		this.getOrCreateTagBuilder(BiomeTags.IS_OVERWORLD).add(WinterDropBuiltinBiomes.PALE_GARDEN);
	}
}
