package net.minecraft.registry;

import java.util.concurrent.CompletableFuture;

public class OneTwentyOneBuiltinRegistries {
	private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder();

	public static CompletableFuture<RegistryWrapper.WrapperLookup> createWrapperLookup(CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		return ExperimentalRegistriesValidator.validate(registriesFuture, REGISTRY_BUILDER);
	}
}
