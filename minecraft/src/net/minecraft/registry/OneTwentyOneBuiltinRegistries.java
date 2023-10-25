package net.minecraft.registry;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_8931;

public class OneTwentyOneBuiltinRegistries {
	private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder();

	public static CompletableFuture<RegistryWrapper.WrapperLookup> createWrapperLookup(CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		return class_8931.method_54840(registriesFuture, REGISTRY_BUILDER);
	}
}
