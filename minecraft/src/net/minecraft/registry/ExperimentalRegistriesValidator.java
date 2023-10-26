package net.minecraft.registry;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ExperimentalRegistriesValidator {
	public static CompletableFuture<RegistryWrapper.WrapperLookup> validate(
		CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, RegistryBuilder builder
	) {
		return registriesFuture.thenApply(
			lookup -> {
				DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.of(Registries.REGISTRIES);
				RegistryWrapper.WrapperLookup wrapperLookup = builder.createWrapperLookup(immutable, lookup);
				Optional<RegistryWrapper.Impl<Biome>> optional = wrapperLookup.getOptionalWrapper(RegistryKeys.BIOME);
				Optional<RegistryWrapper.Impl<PlacedFeature>> optional2 = wrapperLookup.getOptionalWrapper(RegistryKeys.PLACED_FEATURE);
				if (optional.isPresent() || optional2.isPresent()) {
					BuiltinRegistries.validate(
						(RegistryEntryLookup<PlacedFeature>)optional2.orElseGet(() -> lookup.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE)),
						(RegistryWrapper<Biome>)optional.orElseGet(() -> lookup.getWrapperOrThrow(RegistryKeys.BIOME))
					);
				}

				return wrapperLookup;
			}
		);
	}
}
