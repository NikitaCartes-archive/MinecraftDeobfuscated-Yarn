package net.minecraft.data.server;

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
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.slf4j.Logger;

public class BiomeParametersProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator dataGenerator;

	public BiomeParametersProvider(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	@Override
	public void run(DataCache cache) {
		Path path = this.dataGenerator.getOutput();
		DynamicRegistryManager.Immutable immutable = (DynamicRegistryManager.Immutable)DynamicRegistryManager.BUILTIN.get();
		DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, immutable);
		Registry<Biome> registry = immutable.get(Registry.BIOME_KEY);
		MultiNoiseBiomeSource.Preset.method_41415().forEach(pair -> {
			MultiNoiseBiomeSource multiNoiseBiomeSource = ((MultiNoiseBiomeSource.Preset)pair.getSecond()).getBiomeSource(registry, false);
			method_42030(method_42032(path, (Identifier)pair.getFirst()), cache, dynamicOps, MultiNoiseBiomeSource.CODEC, multiNoiseBiomeSource);
		});
	}

	private static <E> void method_42030(Path path, DataCache dataCache, DynamicOps<JsonElement> dynamicOps, Encoder<E> encoder, E object) {
		try {
			Optional<JsonElement> optional = encoder.encodeStart(dynamicOps, object)
				.resultOrPartial(string -> LOGGER.error("Couldn't serialize element {}: {}", path, string));
			if (optional.isPresent()) {
				DataProvider.writeToPath(GSON, dataCache, (JsonElement)optional.get(), path);
			}
		} catch (IOException var6) {
			LOGGER.error("Couldn't save element {}", path, var6);
		}
	}

	private static Path method_42032(Path path, Identifier identifier) {
		return method_42029(path).resolve(identifier.getNamespace()).resolve(identifier.getPath() + ".json");
	}

	private static Path method_42029(Path path) {
		return path.resolve("reports").resolve("biome_parameters");
	}

	@Override
	public String getName() {
		return "Biome Parameters";
	}
}
