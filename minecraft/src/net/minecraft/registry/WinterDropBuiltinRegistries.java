package net.minecraft.registry;

import java.util.concurrent.CompletableFuture;
import net.minecraft.world.biome.WinterDropBuiltinBiomes;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;

public class WinterDropBuiltinRegistries {
	private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder()
		.addRegistry(RegistryKeys.BIOME, WinterDropBuiltinBiomes::bootstrap)
		.addRegistry(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterLists::bootstrapWinterDrop);

	public static CompletableFuture<RegistryBuilder.FullPatchesRegistriesPair> validate(CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		return ExperimentalRegistriesValidator.validate(registriesFuture, REGISTRY_BUILDER)
			.thenApply(
				fullPatchesRegistriesPair -> {
					BuiltinRegistries.validate(
						fullPatchesRegistriesPair.full().getOrThrow(RegistryKeys.PLACED_FEATURE), fullPatchesRegistriesPair.full().getOrThrow(RegistryKeys.BIOME)
					);
					return fullPatchesRegistriesPair;
				}
			);
	}
}
