package net.minecraft.data.report;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.slf4j.Logger;

public class WorldgenProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataGenerator generator;

	public WorldgenProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(DataWriter writer) {
		DynamicRegistryManager dynamicRegistryManager = (DynamicRegistryManager)DynamicRegistryManager.BUILTIN.get();
		DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
		DynamicRegistryManager.getInfos().forEach(info -> this.writeRegistryEntries(writer, dynamicRegistryManager, dynamicOps, info));
	}

	private <T> void writeRegistryEntries(
		DataWriter dataWriter, DynamicRegistryManager dynamicRegistryManager, DynamicOps<JsonElement> dynamicOps, DynamicRegistryManager.Info<T> info
	) {
		RegistryKey<? extends Registry<T>> registryKey = info.registry();
		Registry<T> registry = dynamicRegistryManager.getManaged(registryKey);
		DataGenerator.PathResolver pathResolver = this.generator.createPathResolver(DataGenerator.OutputType.REPORTS, registryKey.getValue().getPath());

		for (Entry<RegistryKey<T>, T> entry : registry.getEntrySet()) {
			writeToPath(pathResolver.resolveJson(((RegistryKey)entry.getKey()).getValue()), dataWriter, dynamicOps, info.entryCodec(), (T)entry.getValue());
		}
	}

	private static <E> void writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
		try {
			Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial(string -> LOGGER.error("Couldn't serialize element {}: {}", path, string));
			if (optional.isPresent()) {
				DataProvider.writeToPath(cache, (JsonElement)optional.get(), path);
			}
		} catch (IOException var6) {
			LOGGER.error("Couldn't save element {}", path, var6);
		}
	}

	@Override
	public String getName() {
		return "Worldgen";
	}
}
