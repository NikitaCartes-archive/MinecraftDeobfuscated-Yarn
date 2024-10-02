package net.minecraft.data.server;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryWrapper;

public class DynamicRegistriesProvider implements DataProvider {
	private final DataOutput output;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

	public DynamicRegistriesProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		this.registriesFuture = registriesFuture;
		this.output = output;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registriesFuture
			.thenCompose(
				registries -> {
					DynamicOps<JsonElement> dynamicOps = registries.getOps(JsonOps.INSTANCE);
					return CompletableFuture.allOf(
						(CompletableFuture[])RegistryLoader.DYNAMIC_REGISTRIES
							.stream()
							.flatMap(entry -> this.writeRegistryEntries(writer, registries, dynamicOps, entry).stream())
							.toArray(CompletableFuture[]::new)
					);
				}
			);
	}

	private <T> Optional<CompletableFuture<?>> writeRegistryEntries(
		DataWriter writer, RegistryWrapper.WrapperLookup registries, DynamicOps<JsonElement> ops, RegistryLoader.Entry<T> registry
	) {
		RegistryKey<? extends Registry<T>> registryKey = registry.key();
		return registries.getOptional(registryKey)
			.map(
				wrapper -> {
					DataOutput.PathResolver pathResolver = this.output.getResolver(registryKey);
					return CompletableFuture.allOf(
						(CompletableFuture[])wrapper.streamEntries()
							.map(entryx -> writeToPath(pathResolver.resolveJson(entryx.registryKey().getValue()), writer, ops, registry.elementCodec(), (T)entryx.value()))
							.toArray(CompletableFuture[]::new)
					);
				}
			);
	}

	private static <E> CompletableFuture<?> writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
		return encoder.encodeStart(json, value)
			.mapOrElse(
				jsonElement -> DataProvider.writeToPath(cache, jsonElement, path),
				error -> CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate file '" + path + "': " + error.message()))
			);
	}

	@Override
	public final String getName() {
		return "Registries";
	}
}
