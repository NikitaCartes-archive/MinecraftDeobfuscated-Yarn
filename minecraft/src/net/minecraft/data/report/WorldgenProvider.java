package net.minecraft.data.report;

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
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryLoader;
import org.slf4j.Logger;

public class WorldgenProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataOutput output;

	public WorldgenProvider(DataOutput output) {
		this.output = output;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		DynamicRegistryManager dynamicRegistryManager = BuiltinRegistries.createBuiltinRegistryManager();
		DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
		return CompletableFuture.allOf(
			(CompletableFuture[])RegistryLoader.DYNAMIC_REGISTRIES
				.stream()
				.map(info -> this.writeRegistryEntries(writer, dynamicRegistryManager, dynamicOps, info))
				.toArray(CompletableFuture[]::new)
		);
	}

	private <T> CompletableFuture<?> writeRegistryEntries(
		DataWriter writer, DynamicRegistryManager registryManager, DynamicOps<JsonElement> ops, RegistryLoader.Entry<T> registry
	) {
		RegistryKey<? extends Registry<T>> registryKey = registry.key();
		Registry<T> registry2 = registryManager.get(registryKey);
		DataOutput.PathResolver pathResolver = this.output.getResolver(DataOutput.OutputType.DATA_PACK, registryKey.getValue().getPath());
		return CompletableFuture.allOf(
			(CompletableFuture[])registry2.getEntrySet()
				.stream()
				.map(entry -> writeToPath(pathResolver.resolveJson(((RegistryKey)entry.getKey()).getValue()), writer, ops, registry.elementCodec(), (T)entry.getValue()))
				.toArray(CompletableFuture[]::new)
		);
	}

	private static <E> CompletableFuture<?> writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
		Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial(error -> LOGGER.error("Couldn't serialize element {}: {}", path, error));
		return optional.isPresent() ? DataProvider.writeToPath(cache, (JsonElement)optional.get(), path) : CompletableFuture.completedFuture(null);
	}

	@Override
	public final String getName() {
		return "Worldgen";
	}
}
