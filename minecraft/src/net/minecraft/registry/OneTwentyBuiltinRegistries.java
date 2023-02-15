package net.minecraft.registry;

import java.util.concurrent.CompletableFuture;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.item.trim.ArmorTrimPatterns;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.gen.WorldPresets;

public class OneTwentyBuiltinRegistries {
	private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder()
		.addRegistry(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterials::oneTwentyBootstrap)
		.addRegistry(RegistryKeys.TRIM_PATTERN, ArmorTrimPatterns::oneTwentyBootstrap)
		.addRegistry(RegistryKeys.BIOME, BuiltinBiomes::method_49391)
		.addRegistry(RegistryKeys.WORLD_PRESET, WorldPresets::bootstrapOneTwenty);

	public static CompletableFuture<RegistryWrapper.WrapperLookup> createWrapperLookup(CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		return registriesFuture.thenApply(wrapperLookup -> {
			DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.of(Registries.REGISTRIES);
			RegistryWrapper.WrapperLookup wrapperLookup2 = REGISTRY_BUILDER.createWrapperLookup(immutable, wrapperLookup);
			BuiltinRegistries.validate(wrapperLookup.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE), wrapperLookup2.getWrapperOrThrow(RegistryKeys.BIOME));
			return wrapperLookup2;
		});
	}
}
