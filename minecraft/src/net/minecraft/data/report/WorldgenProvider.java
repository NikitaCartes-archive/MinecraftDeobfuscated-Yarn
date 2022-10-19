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
	private final DataOutput field_40665;

	public WorldgenProvider(DataOutput dataOutput) {
		this.field_40665 = dataOutput;
	}

	@Override
	public void run(DataWriter writer) {
		DynamicRegistryManager dynamicRegistryManager = BuiltinRegistries.createBuiltinRegistryManager();
		DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
		RegistryLoader.DYNAMIC_REGISTRIES.forEach(info -> this.writeRegistryEntries(writer, dynamicRegistryManager, dynamicOps, info));
	}

	private <T> void writeRegistryEntries(DataWriter writer, DynamicRegistryManager registryManager, DynamicOps<JsonElement> ops, RegistryLoader.Entry<T> registry) {
		RegistryKey<? extends Registry<T>> registryKey = registry.key();
		Registry<T> registry2 = registryManager.get(registryKey);
		DataOutput.PathResolver pathResolver = this.field_40665.getResolver(DataOutput.OutputType.DATA_PACK, registryKey.getValue().getPath());

		for (Entry<RegistryKey<T>, T> entry : registry2.getEntrySet()) {
			writeToPath(pathResolver.resolveJson(((RegistryKey)entry.getKey()).getValue()), writer, ops, registry.elementCodec(), (T)entry.getValue());
		}
	}

	private static <E> void writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
		try {
			Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial(error -> LOGGER.error("Couldn't serialize element {}: {}", path, error));
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
