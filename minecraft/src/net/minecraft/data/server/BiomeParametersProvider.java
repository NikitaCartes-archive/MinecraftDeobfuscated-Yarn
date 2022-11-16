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
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.slf4j.Logger;

public class BiomeParametersProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Path path;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

	public BiomeParametersProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		this.path = output.resolvePath(DataOutput.OutputType.REPORTS).resolve("biome_parameters");
		this.registryLookupFuture = registryLookupFuture;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registryLookupFuture.thenCompose(lookup -> {
			DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, lookup);
			RegistryEntryLookup<Biome> registryEntryLookup = lookup.getWrapperOrThrow(RegistryKeys.BIOME);
			return CompletableFuture.allOf((CompletableFuture[])MultiNoiseBiomeSource.Preset.streamPresets().map(preset -> {
				MultiNoiseBiomeSource multiNoiseBiomeSource = ((MultiNoiseBiomeSource.Preset)preset.getSecond()).getBiomeSource(registryEntryLookup, false);
				return write(this.resolvePath((Identifier)preset.getFirst()), writer, dynamicOps, MultiNoiseBiomeSource.CODEC, multiNoiseBiomeSource);
			}).toArray(CompletableFuture[]::new));
		});
	}

	private static <E> CompletableFuture<?> write(Path path, DataWriter writer, DynamicOps<JsonElement> ops, Encoder<E> codec, E biomeSource) {
		Optional<JsonElement> optional = codec.encodeStart(ops, biomeSource).resultOrPartial(error -> LOGGER.error("Couldn't serialize element {}: {}", path, error));
		return optional.isPresent() ? DataProvider.writeToPath(writer, (JsonElement)optional.get(), path) : CompletableFuture.completedFuture(null);
	}

	private Path resolvePath(Identifier id) {
		return this.path.resolve(id.getNamespace()).resolve(id.getPath() + ".json");
	}

	@Override
	public final String getName() {
		return "Biome Parameters";
	}
}
