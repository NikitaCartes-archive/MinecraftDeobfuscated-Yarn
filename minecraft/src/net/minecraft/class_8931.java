package net.minecraft;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.PlacedFeature;

public class class_8931 {
	public static CompletableFuture<RegistryWrapper.WrapperLookup> method_54840(
		CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, RegistryBuilder registryBuilder
	) {
		return completableFuture.thenApply(
			wrapperLookup -> {
				DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.of(Registries.REGISTRIES);
				RegistryWrapper.WrapperLookup wrapperLookup2 = registryBuilder.createWrapperLookup(immutable, wrapperLookup);
				Optional<RegistryWrapper.Impl<Biome>> optional = wrapperLookup2.getOptionalWrapper(RegistryKeys.BIOME);
				Optional<RegistryWrapper.Impl<PlacedFeature>> optional2 = wrapperLookup2.getOptionalWrapper(RegistryKeys.PLACED_FEATURE);
				if (optional.isPresent() || optional2.isPresent()) {
					BuiltinRegistries.validate(
						(RegistryEntryLookup<PlacedFeature>)optional2.orElseGet(() -> wrapperLookup.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE)),
						(RegistryWrapper<Biome>)optional.orElseGet(() -> wrapperLookup.getWrapperOrThrow(RegistryKeys.BIOME))
					);
				}

				return wrapperLookup2;
			}
		);
	}
}
