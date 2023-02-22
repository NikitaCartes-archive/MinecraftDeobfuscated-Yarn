package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.level.LevelProperties;

public record DimensionOptionsRegistryHolder(Registry<DimensionOptions> dimensions) {
	public static final MapCodec<DimensionOptionsRegistryHolder> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RegistryCodecs.createKeyedRegistryCodec(RegistryKeys.DIMENSION, Lifecycle.stable(), DimensionOptions.CODEC)
						.fieldOf("dimensions")
						.forGetter(DimensionOptionsRegistryHolder::dimensions)
				)
				.apply(instance, instance.stable(DimensionOptionsRegistryHolder::new))
	);
	private static final Set<RegistryKey<DimensionOptions>> VANILLA_KEYS = ImmutableSet.of(
		DimensionOptions.OVERWORLD, DimensionOptions.NETHER, DimensionOptions.END
	);
	private static final int VANILLA_KEY_COUNT = VANILLA_KEYS.size();

	public DimensionOptionsRegistryHolder(Registry<DimensionOptions> dimensions) {
		DimensionOptions dimensionOptions = dimensions.get(DimensionOptions.OVERWORLD);
		if (dimensionOptions == null) {
			throw new IllegalStateException("Overworld settings missing");
		} else {
			this.dimensions = dimensions;
		}
	}

	public static Stream<RegistryKey<DimensionOptions>> streamAll(Stream<RegistryKey<DimensionOptions>> otherKeys) {
		return Stream.concat(VANILLA_KEYS.stream(), otherKeys.filter(key -> !VANILLA_KEYS.contains(key)));
	}

	public DimensionOptionsRegistryHolder with(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator) {
		Registry<DimensionType> registry = dynamicRegistryManager.get(RegistryKeys.DIMENSION_TYPE);
		Registry<DimensionOptions> registry2 = createRegistry(registry, this.dimensions, chunkGenerator);
		return new DimensionOptionsRegistryHolder(registry2);
	}

	public static Registry<DimensionOptions> createRegistry(
		Registry<DimensionType> dynamicRegistry, Registry<DimensionOptions> currentRegistry, ChunkGenerator chunkGenerator
	) {
		DimensionOptions dimensionOptions = currentRegistry.get(DimensionOptions.OVERWORLD);
		RegistryEntry<DimensionType> registryEntry = (RegistryEntry<DimensionType>)(dimensionOptions == null
			? dynamicRegistry.entryOf(DimensionTypes.OVERWORLD)
			: dimensionOptions.dimensionTypeEntry());
		return createRegistry(currentRegistry, registryEntry, chunkGenerator);
	}

	public static Registry<DimensionOptions> createRegistry(
		Registry<DimensionOptions> currentRegistry, RegistryEntry<DimensionType> overworldEntry, ChunkGenerator chunkGenerator
	) {
		MutableRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(RegistryKeys.DIMENSION, Lifecycle.experimental());
		mutableRegistry.add(DimensionOptions.OVERWORLD, new DimensionOptions(overworldEntry, chunkGenerator), Lifecycle.stable());

		for (java.util.Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : currentRegistry.getEntrySet()) {
			RegistryKey<DimensionOptions> registryKey = (RegistryKey<DimensionOptions>)entry.getKey();
			if (registryKey != DimensionOptions.OVERWORLD) {
				mutableRegistry.add(registryKey, (DimensionOptions)entry.getValue(), currentRegistry.getEntryLifecycle((DimensionOptions)entry.getValue()));
			}
		}

		return mutableRegistry.freeze();
	}

	public ChunkGenerator getChunkGenerator() {
		DimensionOptions dimensionOptions = this.dimensions.get(DimensionOptions.OVERWORLD);
		if (dimensionOptions == null) {
			throw new IllegalStateException("Overworld settings missing");
		} else {
			return dimensionOptions.chunkGenerator();
		}
	}

	public Optional<DimensionOptions> getOrEmpty(RegistryKey<DimensionOptions> key) {
		return this.dimensions.getOrEmpty(key);
	}

	public ImmutableSet<RegistryKey<World>> getWorldKeys() {
		return (ImmutableSet<RegistryKey<World>>)this.dimensions()
			.getEntrySet()
			.stream()
			.map(java.util.Map.Entry::getKey)
			.map(RegistryKeys::toWorldKey)
			.collect(ImmutableSet.toImmutableSet());
	}

	public boolean isDebug() {
		return this.getChunkGenerator() instanceof DebugChunkGenerator;
	}

	private static LevelProperties.SpecialProperty getSpecialProperty(Registry<DimensionOptions> dimensionOptionsRegistry) {
		return (LevelProperties.SpecialProperty)dimensionOptionsRegistry.getOrEmpty(DimensionOptions.OVERWORLD).map(overworldEntry -> {
			ChunkGenerator chunkGenerator = overworldEntry.chunkGenerator();
			if (chunkGenerator instanceof DebugChunkGenerator) {
				return LevelProperties.SpecialProperty.DEBUG;
			} else {
				return chunkGenerator instanceof FlatChunkGenerator ? LevelProperties.SpecialProperty.FLAT : LevelProperties.SpecialProperty.NONE;
			}
		}).orElse(LevelProperties.SpecialProperty.NONE);
	}

	static Lifecycle getLifecycle(RegistryKey<DimensionOptions> key, DimensionOptions dimensionOptions) {
		return isVanilla(key, dimensionOptions) ? Lifecycle.stable() : Lifecycle.experimental();
	}

	private static boolean isVanilla(RegistryKey<DimensionOptions> key, DimensionOptions dimensionOptions) {
		if (key == DimensionOptions.OVERWORLD) {
			return isOverworldVanilla(dimensionOptions);
		} else if (key == DimensionOptions.NETHER) {
			return isNetherVanilla(dimensionOptions);
		} else {
			return key == DimensionOptions.END ? isTheEndVanilla(dimensionOptions) : false;
		}
	}

	private static boolean isOverworldVanilla(DimensionOptions dimensionOptions) {
		RegistryEntry<DimensionType> registryEntry = dimensionOptions.dimensionTypeEntry();
		if (!registryEntry.matchesKey(DimensionTypes.OVERWORLD) && !registryEntry.matchesKey(DimensionTypes.OVERWORLD_CAVES)) {
			return false;
		} else {
			if (dimensionOptions.chunkGenerator().getBiomeSource() instanceof MultiNoiseBiomeSource multiNoiseBiomeSource
				&& !multiNoiseBiomeSource.matchesInstance(MultiNoiseBiomeSourceParameterLists.OVERWORLD)) {
				return false;
			}

			return true;
		}
	}

	private static boolean isNetherVanilla(DimensionOptions dimensionOptions) {
		return dimensionOptions.dimensionTypeEntry().matchesKey(DimensionTypes.THE_NETHER)
			&& dimensionOptions.chunkGenerator() instanceof NoiseChunkGenerator noiseChunkGenerator
			&& noiseChunkGenerator.matchesSettings(ChunkGeneratorSettings.NETHER)
			&& noiseChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource multiNoiseBiomeSource
			&& multiNoiseBiomeSource.matchesInstance(MultiNoiseBiomeSourceParameterLists.NETHER);
	}

	private static boolean isTheEndVanilla(DimensionOptions dimensionOptions) {
		return dimensionOptions.dimensionTypeEntry().matchesKey(DimensionTypes.THE_END)
			&& dimensionOptions.chunkGenerator() instanceof NoiseChunkGenerator noiseChunkGenerator
			&& noiseChunkGenerator.matchesSettings(ChunkGeneratorSettings.END)
			&& noiseChunkGenerator.getBiomeSource() instanceof TheEndBiomeSource;
	}

	public DimensionOptionsRegistryHolder.DimensionsConfig toConfig(Registry<DimensionOptions> existingRegistry) {
		Stream<RegistryKey<DimensionOptions>> stream = Stream.concat(existingRegistry.getKeys().stream(), this.dimensions.getKeys().stream()).distinct();

		record Entry(RegistryKey<DimensionOptions> key, DimensionOptions value) {

			Lifecycle getLifecycle() {
				return DimensionOptionsRegistryHolder.getLifecycle(this.key, this.value);
			}
		}

		List<Entry> list = new ArrayList();
		streamAll(stream)
			.forEach(
				key -> existingRegistry.getOrEmpty(key).or(() -> this.dimensions.getOrEmpty(key)).ifPresent(dimensionOptions -> list.add(new Entry(key, dimensionOptions)))
			);
		Lifecycle lifecycle = list.size() == VANILLA_KEY_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
		MutableRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<>(RegistryKeys.DIMENSION, lifecycle);
		list.forEach(entry -> mutableRegistry.add(entry.key, entry.value, entry.getLifecycle()));
		Registry<DimensionOptions> registry = mutableRegistry.freeze();
		LevelProperties.SpecialProperty specialProperty = getSpecialProperty(registry);
		return new DimensionOptionsRegistryHolder.DimensionsConfig(registry.freeze(), specialProperty);
	}

	public static record DimensionsConfig(Registry<DimensionOptions> dimensions, LevelProperties.SpecialProperty specialWorldProperty) {
		public Lifecycle getLifecycle() {
			return this.dimensions.getLifecycle();
		}

		public DynamicRegistryManager.Immutable toDynamicRegistryManager() {
			return new DynamicRegistryManager.ImmutableImpl(List.of(this.dimensions)).toImmutable();
		}
	}
}
