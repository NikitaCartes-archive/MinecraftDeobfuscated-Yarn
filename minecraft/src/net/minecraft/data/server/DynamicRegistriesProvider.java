package net.minecraft.data.server;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

public class DynamicRegistriesProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataOutput output;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

	public DynamicRegistriesProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		this.registryLookupFuture = registryLookupFuture;
		this.output = output;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registryLookupFuture
			.thenCompose(
				lookup -> {
					DynamicOps<JsonElement> dynamicOps = lookup.getOps(JsonOps.INSTANCE);
					return CompletableFuture.allOf(
						(CompletableFuture[])RegistryLoader.DYNAMIC_REGISTRIES
							.stream()
							.flatMap(entry -> this.writeRegistryEntries(writer, lookup, dynamicOps, entry).stream())
							.toArray(CompletableFuture[]::new)
					);
				}
			);
	}

	private <T> Optional<CompletableFuture<?>> writeRegistryEntries(
		DataWriter writer, RegistryWrapper.WrapperLookup lookup, DynamicOps<JsonElement> ops, RegistryLoader.Entry<T> registry
	) {
		RegistryKey<? extends Registry<T>> registryKey = registry.key();
		return lookup.getOptionalWrapper(registryKey)
			.map(
				wrapper -> {
					DataOutput.PathResolver pathResolver = this.output.getResolver(DataOutput.OutputType.DATA_PACK, registryKey.getValue().getPath());
					return CompletableFuture.allOf(
						(CompletableFuture[])wrapper.streamEntries()
							.map(entryx -> writeToPath(pathResolver.resolveJson(entryx.registryKey().getValue()), writer, ops, registry.elementCodec(), (T)entryx.value()))
							.toArray(CompletableFuture[]::new)
					);
				}
			);
	}

	private static <E> CompletableFuture<?> writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
		Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial(error -> LOGGER.error("Couldn't serialize element {}: {}", path, error));
		return optional.isPresent() ? DataProvider.writeToPath(cache, (JsonElement)optional.get(), path) : CompletableFuture.completedFuture(null);
	}

	@Override
	public final String getName() {
		return "Registries";
	}
}
