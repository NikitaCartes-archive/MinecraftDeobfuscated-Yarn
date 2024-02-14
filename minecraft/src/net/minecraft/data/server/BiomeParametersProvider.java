package net.minecraft.data.server;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.slf4j.Logger;

public class BiomeParametersProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Path path;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
	private static final MapCodec<RegistryKey<Biome>> BIOME_KEY_CODEC = RegistryKey.createCodec(RegistryKeys.BIOME).fieldOf("biome");
	private static final Codec<MultiNoiseUtil.Entries<RegistryKey<Biome>>> BIOME_ENTRY_CODEC = MultiNoiseUtil.Entries.createCodec(BIOME_KEY_CODEC)
		.fieldOf("biomes")
		.codec();

	public BiomeParametersProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		this.path = output.resolvePath(DataOutput.OutputType.REPORTS).resolve("biome_parameters");
		this.registryLookupFuture = registryLookupFuture;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registryLookupFuture
			.thenCompose(
				lookup -> {
					DynamicOps<JsonElement> dynamicOps = lookup.getOps(JsonOps.INSTANCE);
					List<CompletableFuture<?>> list = new ArrayList();
					MultiNoiseBiomeSourceParameterList.getPresetToEntriesMap()
						.forEach((preset, entries) -> list.add(write(this.resolvePath(preset.id()), writer, dynamicOps, BIOME_ENTRY_CODEC, entries)));
					return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
				}
			);
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
