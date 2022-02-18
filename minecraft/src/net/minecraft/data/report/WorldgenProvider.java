package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
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
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.slf4j.Logger;

public class WorldgenProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;

	public WorldgenProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(DataCache cache) {
		Path path = this.generator.getOutput();
		DynamicRegistryManager dynamicRegistryManager = (DynamicRegistryManager)DynamicRegistryManager.BUILTIN.get();
		int i = 0;
		Registry<DimensionOptions> registry = DimensionType.createDefaultDimensionOptions(dynamicRegistryManager, 0L, false);
		ChunkGenerator chunkGenerator = GeneratorOptions.createOverworldGenerator(dynamicRegistryManager, 0L, false);
		Registry<DimensionOptions> registry2 = GeneratorOptions.getRegistryWithReplacedOverworldGenerator(
			dynamicRegistryManager.getManaged(Registry.DIMENSION_TYPE_KEY), registry, chunkGenerator
		);
		DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
		DynamicRegistryManager.getInfos().forEach(info -> writeRegistryEntries(cache, path, dynamicRegistryManager, dynamicOps, info));
		writeRegistryEntries(path, cache, dynamicOps, Registry.DIMENSION_KEY, registry2, DimensionOptions.CODEC);
	}

	private static <T> void writeRegistryEntries(
		DataCache cache, Path path, DynamicRegistryManager registryManager, DynamicOps<JsonElement> json, DynamicRegistryManager.Info<T> info
	) {
		writeRegistryEntries(path, cache, json, info.registry(), registryManager.getManaged(info.registry()), info.entryCodec());
	}

	private static <E, T extends Registry<E>> void writeRegistryEntries(
		Path path, DataCache cache, DynamicOps<JsonElement> json, RegistryKey<? extends T> registryKey, T registry, Encoder<E> encoder
	) {
		for (Entry<RegistryKey<E>, E> entry : registry.getEntrySet()) {
			Path path2 = getPath(path, registryKey.getValue(), ((RegistryKey)entry.getKey()).getValue());
			writeToPath(path2, cache, json, encoder, (E)entry.getValue());
		}
	}

	private static <E> void writeToPath(Path path, DataCache cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
		try {
			Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial(string -> LOGGER.error("Couldn't serialize element {}: {}", path, string));
			if (optional.isPresent()) {
				DataProvider.writeToPath(GSON, cache, (JsonElement)optional.get(), path);
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
