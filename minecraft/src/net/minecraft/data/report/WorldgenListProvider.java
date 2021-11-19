package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldgenListProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;

	public WorldgenListProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(DataCache cache) {
		Path path = this.generator.getOutput();
		DynamicRegistryManager dynamicRegistryManager = DynamicRegistryManager.create();
		int i = 0;
		SimpleRegistry<DimensionOptions> simpleRegistry = DimensionType.createDefaultDimensionOptions(dynamicRegistryManager, 0L, false);
		ChunkGenerator chunkGenerator = GeneratorOptions.createOverworldGenerator(dynamicRegistryManager, 0L, false);
		SimpleRegistry<DimensionOptions> simpleRegistry2 = GeneratorOptions.getRegistryWithReplacedOverworldGenerator(
			dynamicRegistryManager.getMutable(Registry.DIMENSION_TYPE_KEY), simpleRegistry, chunkGenerator
		);
		DynamicOps<JsonElement> dynamicOps = RegistryReadingOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
		DynamicRegistryManager.getInfos().forEach(info -> writeRegistryEntries(cache, path, dynamicRegistryManager, dynamicOps, info));
		writeRegistryEntries(path, cache, dynamicOps, Registry.DIMENSION_KEY, simpleRegistry2, DimensionOptions.CODEC);
	}

	private static <T> void writeRegistryEntries(
		DataCache cache, Path path, DynamicRegistryManager registryManager, DynamicOps<JsonElement> json, DynamicRegistryManager.Info<T> info
	) {
		writeRegistryEntries(path, cache, json, info.registry(), registryManager.getMutable(info.registry()), info.entryCodec());
	}

	private static <E, T extends Registry<E>> void writeRegistryEntries(
		Path path, DataCache cache, DynamicOps<JsonElement> json, RegistryKey<? extends T> registryKey, T registry, Encoder<E> encoder
	) {
		for (Entry<RegistryKey<E>, E> entry : registry.getEntries()) {
			Path path2 = getPath(path, registryKey.getValue(), ((RegistryKey)entry.getKey()).getValue());
			writeToPath(path2, cache, json, encoder, (E)entry.getValue());
		}
	}

	private static <E> void writeToPath(Path path, DataCache cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
		try {
			Optional<JsonElement> optional = encoder.encodeStart(json, value).result();
			if (optional.isPresent()) {
				DataProvider.writeToPath(GSON, cache, (JsonElement)optional.get(), path);
			} else {
				LOGGER.error("Couldn't serialize element {}", path);
			}
		} catch (IOException var6) {
			LOGGER.error("Couldn't save element {}", path, var6);
		}
	}

	private static Path getPath(Path root, Identifier rootId, Identifier id) {
		return getRootPath(root).resolve(id.getNamespace()).resolve(rootId.getPath()).resolve(id.getPath() + ".json");
	}

	private static Path getRootPath(Path path) {
		return path.resolve("reports").resolve("worldgen");
	}

	@Override
	public String getName() {
		return "Worldgen";
	}
}
