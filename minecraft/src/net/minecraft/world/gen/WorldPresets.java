package net.minecraft.world.gen;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

public class WorldPresets {
	public static final RegistryKey<WorldPreset> DEFAULT = of("normal");
	public static final RegistryKey<WorldPreset> FLAT = of("flat");
	public static final RegistryKey<WorldPreset> LARGE_BIOMES = of("large_biomes");
	public static final RegistryKey<WorldPreset> AMPLIFIED = of("amplified");
	public static final RegistryKey<WorldPreset> SINGLE_BIOME_SURFACE = of("single_biome_surface");
	public static final RegistryKey<WorldPreset> DEBUG_ALL_BLOCK_STATES = of("debug_all_block_states");

	public static void bootstrap(Registerable<WorldPreset> presetRegisterable) {
		new WorldPresets.Registrar(presetRegisterable).bootstrap();
	}

	private static RegistryKey<WorldPreset> of(String id) {
		return RegistryKey.of(RegistryKeys.WORLD_PRESET, Identifier.ofVanilla(id));
	}

	public static Optional<RegistryKey<WorldPreset>> getWorldPreset(DimensionOptionsRegistryHolder registry) {
		return registry.getOrEmpty(DimensionOptions.OVERWORLD).flatMap(overworld -> {
			Object var10000;
			Objects.requireNonNull(var10000);
			ChunkGenerator chunkGenerator = (ChunkGenerator)var10000;

			overworld.chunkGenerator();
			return switch (chunkGenerator) {
				case FlatChunkGenerator flatChunkGenerator -> Optional.of(FLAT);
				case DebugChunkGenerator debugChunkGenerator -> Optional.of(DEBUG_ALL_BLOCK_STATES);
				case NoiseChunkGenerator noiseChunkGenerator -> Optional.of(DEFAULT);
				default -> Optional.empty();
			};
		});
	}

	public static DimensionOptionsRegistryHolder createDemoOptions(DynamicRegistryManager dynamicRegistryManager) {
		return dynamicRegistryManager.get(RegistryKeys.WORLD_PRESET).entryOf(DEFAULT).value().createDimensionsRegistryHolder();
	}

	public static DimensionOptions getDefaultOverworldOptions(DynamicRegistryManager dynamicRegistryManager) {
		return (DimensionOptions)dynamicRegistryManager.get(RegistryKeys.WORLD_PRESET).entryOf(DEFAULT).value().getOverworld().orElseThrow();
	}

	static class Registrar {
		private final Registerable<WorldPreset> presetRegisterable;
		private final RegistryEntryLookup<ChunkGeneratorSettings> chunkGeneratorSettingsLookup;
		private final RegistryEntryLookup<Biome> biomeLookup;
		private final RegistryEntryLookup<PlacedFeature> featureLookup;
		private final RegistryEntryLookup<StructureSet> structureSetLookup;
		private final RegistryEntryLookup<MultiNoiseBiomeSourceParameterList> multiNoisePresetLookup;
		private final RegistryEntry<DimensionType> overworldDimensionType;
		private final DimensionOptions netherDimensionOptions;
		private final DimensionOptions endDimensionOptions;

		Registrar(Registerable<WorldPreset> presetRegisterable) {
			this.presetRegisterable = presetRegisterable;
			RegistryEntryLookup<DimensionType> registryEntryLookup = presetRegisterable.getRegistryLookup(RegistryKeys.DIMENSION_TYPE);
			this.chunkGeneratorSettingsLookup = presetRegisterable.getRegistryLookup(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
			this.biomeLookup = presetRegisterable.getRegistryLookup(RegistryKeys.BIOME);
			this.featureLookup = presetRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
			this.structureSetLookup = presetRegisterable.getRegistryLookup(RegistryKeys.STRUCTURE_SET);
			this.multiNoisePresetLookup = presetRegisterable.getRegistryLookup(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
			this.overworldDimensionType = registryEntryLookup.getOrThrow(DimensionTypes.OVERWORLD);
			RegistryEntry<DimensionType> registryEntry = registryEntryLookup.getOrThrow(DimensionTypes.THE_NETHER);
			RegistryEntry<ChunkGeneratorSettings> registryEntry2 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.NETHER);
			RegistryEntry.Reference<MultiNoiseBiomeSourceParameterList> reference = this.multiNoisePresetLookup.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER);
			this.netherDimensionOptions = new DimensionOptions(registryEntry, new NoiseChunkGenerator(MultiNoiseBiomeSource.create(reference), registryEntry2));
			RegistryEntry<DimensionType> registryEntry3 = registryEntryLookup.getOrThrow(DimensionTypes.THE_END);
			RegistryEntry<ChunkGeneratorSettings> registryEntry4 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.END);
			this.endDimensionOptions = new DimensionOptions(registryEntry3, new NoiseChunkGenerator(TheEndBiomeSource.createVanilla(this.biomeLookup), registryEntry4));
		}

		private DimensionOptions createOverworldOptions(ChunkGenerator chunkGenerator) {
			return new DimensionOptions(this.overworldDimensionType, chunkGenerator);
		}

		private DimensionOptions createOverworldOptions(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings) {
			return this.createOverworldOptions(new NoiseChunkGenerator(biomeSource, chunkGeneratorSettings));
		}

		private WorldPreset createPreset(DimensionOptions dimensionOptions) {
			return new WorldPreset(
				Map.of(DimensionOptions.OVERWORLD, dimensionOptions, DimensionOptions.NETHER, this.netherDimensionOptions, DimensionOptions.END, this.endDimensionOptions)
			);
		}

		private void register(RegistryKey<WorldPreset> key, DimensionOptions dimensionOptions) {
			this.presetRegisterable.register(key, this.createPreset(dimensionOptions));
		}

		private void bootstrap(BiomeSource biomeSource) {
			RegistryEntry<ChunkGeneratorSettings> registryEntry = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
			this.register(WorldPresets.DEFAULT, this.createOverworldOptions(biomeSource, registryEntry));
			RegistryEntry<ChunkGeneratorSettings> registryEntry2 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.LARGE_BIOMES);
			this.register(WorldPresets.LARGE_BIOMES, this.createOverworldOptions(biomeSource, registryEntry2));
			RegistryEntry<ChunkGeneratorSettings> registryEntry3 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.AMPLIFIED);
			this.register(WorldPresets.AMPLIFIED, this.createOverworldOptions(biomeSource, registryEntry3));
		}

		public void bootstrap() {
			RegistryEntry.Reference<MultiNoiseBiomeSourceParameterList> reference = this.multiNoisePresetLookup
				.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
			this.bootstrap(MultiNoiseBiomeSource.create(reference));
			RegistryEntry<ChunkGeneratorSettings> registryEntry = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
			RegistryEntry.Reference<Biome> reference2 = this.biomeLookup.getOrThrow(BiomeKeys.PLAINS);
			this.register(WorldPresets.SINGLE_BIOME_SURFACE, this.createOverworldOptions(new FixedBiomeSource(reference2), registryEntry));
			this.register(
				WorldPresets.FLAT,
				this.createOverworldOptions(
					new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig(this.biomeLookup, this.structureSetLookup, this.featureLookup))
				)
			);
			this.register(WorldPresets.DEBUG_ALL_BLOCK_STATES, this.createOverworldOptions(new DebugChunkGenerator(reference2)));
		}
	}
}
