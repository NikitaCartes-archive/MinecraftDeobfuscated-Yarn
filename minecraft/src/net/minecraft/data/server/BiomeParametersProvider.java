package net.minecraft.data.server;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.slf4j.Logger;

public class BiomeParametersProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Path path;

	public BiomeParametersProvider(DataOutput dataGenerator) {
		this.path = dataGenerator.resolvePath(DataOutput.OutputType.REPORTS).resolve("biome_parameters");
	}

	@Override
	public void run(DataWriter writer) {
		DynamicRegistryManager dynamicRegistryManager = BuiltinRegistries.createBuiltinRegistryManager();
		DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
		Registry<Biome> registry = dynamicRegistryManager.get(Registry.BIOME_KEY);
		MultiNoiseBiomeSource.Preset.streamPresets().forEach(preset -> {
			MultiNoiseBiomeSource multiNoiseBiomeSource = ((MultiNoiseBiomeSource.Preset)preset.getSecond()).getBiomeSource(registry, false);
			write(this.resolvePath((Identifier)preset.getFirst()), writer, dynamicOps, MultiNoiseBiomeSource.CODEC, multiNoiseBiomeSource);
		});
	}

	private static <E> void write(Path path, DataWriter writer, DynamicOps<JsonElement> ops, Encoder<E> codec, E biomeSource) {
		try {
			Optional<JsonElement> optional = codec.encodeStart(ops, biomeSource)
				.resultOrPartial(error -> LOGGER.error("Couldn't serialize element {}: {}", path, error));
			if (optional.isPresent()) {
				DataProvider.writeToPath(writer, (JsonElement)optional.get(), path);
			}
		} catch (IOException var6) {
			LOGGER.error("Couldn't save element {}", path, var6);
		}
	}

	private Path resolvePath(Identifier id) {
		return this.path.resolve(id.getNamespace()).resolve(id.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Biome Parameters";
	}
}
