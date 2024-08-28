package net.minecraft.registry;

import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ExperimentalRegistriesValidator {
	public static CompletableFuture<RegistryBuilder.FullPatchesRegistriesPair> validate(
		CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, RegistryBuilder builder
	) {
		return registriesFuture.thenApply(
			registries -> {
				DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.of(Registries.REGISTRIES);
				RegistryCloner.CloneableRegistries cloneableRegistries = new RegistryCloner.CloneableRegistries();
				RegistryLoader.DYNAMIC_REGISTRIES.forEach(entry -> entry.addToCloner(cloneableRegistries::add));
				RegistryBuilder.FullPatchesRegistriesPair fullPatchesRegistriesPair = builder.createWrapperLookup(immutable, registries, cloneableRegistries);
				RegistryWrapper.WrapperLookup wrapperLookup = fullPatchesRegistriesPair.full();
				Optional<? extends RegistryWrapper.Impl<Biome>> optional = wrapperLookup.getOptional(RegistryKeys.BIOME);
				Optional<? extends RegistryWrapper.Impl<PlacedFeature>> optional2 = wrapperLookup.getOptional(RegistryKeys.PLACED_FEATURE);
				if (optional.isPresent() || optional2.isPresent()) {
					BuiltinRegistries.validate(
						DataFixUtils.orElseGet(optional2, () -> registries.getOrThrow(RegistryKeys.PLACED_FEATURE)),
						DataFixUtils.orElseGet(optional, () -> registries.getOrThrow(RegistryKeys.BIOME))
					);
				}

				return fullPatchesRegistriesPair;
			}
		);
	}
}
