package net.minecraft.registry;

import java.util.concurrent.CompletableFuture;

public class OneTwentyOneBuiltinRegistries {
	public static CompletableFuture<RegistryWrapper.WrapperLookup> createWrapperLookup(CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		return registriesFuture;
	}
}
