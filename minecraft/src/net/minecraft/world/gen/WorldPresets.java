package net.minecraft.world.gen;

import java.util.Map;
import java.util.Optional;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registerable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryLookup;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
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
		return RegistryKey.of(Registry.WORLD_PRESET_KEY, new Identifier(id));
	}

	public static Optional<RegistryKey<WorldPreset>> getWorldPreset(Registry<DimensionOptions> registry) {
		return registry.getOrEmpty(DimensionOptions.OVERWORLD).flatMap(overworld -> {
			ChunkGenerator chunkGenerator = overworld.chunkGenerator();
			if (chunkGenerator instanceof FlatChunkGenerator) {
				return Optional.of(FLAT);
			} else {
				return chunkGenerator instanceof DebugChunkGenerator ? Optional.of(DEBUG_ALL_BLOCK_STATES) : Optional.empty();
			}
		});
	}

	public static DimensionOptionsRegistryHolder createDemoOptions(DynamicRegistryManager dynamicRegistryManager) {
		return dynamicRegistryManager.get(Registry.WORLD_PRESET_KEY).entryOf(DEFAULT).value().createDimensionsRegistryHolder();
	}

	public static DimensionOptions getDefaultOverworldOptions(DynamicRegistryManager dynamicRegistryManager) {
		return (DimensionOptions)dynamicRegistryManager.get(Registry.WORLD_PRESET_KEY).entryOf(DEFAULT).value().getOverworld().orElseThrow();
	}

	static class Registrar {
		private final Registerable<WorldPreset> presetRegisterable;
		private final RegistryEntryLookup<ChunkGeneratorSettings> chunkGeneratorSettingsLookup;
		private final RegistryEntryLookup<Biome> biomeLookup;
		private final RegistryEntryLookup<PlacedFeature> featureLookup;
		private final RegistryEntryLookup<StructureSet> structureSetLookup;
		private final RegistryEntry<DimensionType> overworldDimensionType;
		private final DimensionOptions netherDimensionOptions;
		private final DimensionOptions endDimensionOptions;

		Registrar(Registerable<WorldPreset> presetRegisterable) {
			this.presetRegisterable = presetRegisterable;
			RegistryEntryLookup<DimensionType> registryEntryLookup = presetRegisterable.getRegistryLookup(Registry.DIMENSION_TYPE_KEY);
			this.chunkGeneratorSettingsLookup = presetRegisterable.getRegistryLookup(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
			this.biomeLookup = presetRegisterable.getRegistryLookup(Registry.BIOME_KEY);
			this.featureLookup = presetRegisterable.getRegistryLookup(Registry.PLACED_FEATURE_KEY);
			this.structureSetLookup = presetRegisterable.getRegistryLookup(Registry.STRUCTURE_SET_KEY);
			this.overworldDimensionType = registryEntryLookup.getOrThrow(DimensionTypes.OVERWORLD);
			RegistryEntry<DimensionType> registryEntry = registryEntryLookup.getOrThrow(DimensionTypes.THE_NETHER);
			RegistryEntry<ChunkGeneratorSettings> registryEntry2 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.NETHER);
			this.netherDimensionOptions = new DimensionOptions(
				registryEntry, new NoiseChunkGenerator(MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(this.biomeLookup), registryEntry2)
			);
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

		public void bootstrap() {
			MultiNoiseBiomeSource multiNoiseBiomeSource = MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(this.biomeLookup);
			RegistryEntry<ChunkGeneratorSettings> registryEntry = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
			this.register(WorldPresets.DEFAULT, this.createOverworldOptions(multiNoiseBiomeSource, registryEntry));
			RegistryEntry<ChunkGeneratorSettings> registryEntry2 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.LARGE_BIOMES);
			this.register(WorldPresets.LARGE_BIOMES, this.createOverworldOptions(multiNoiseBiomeSource, registryEntry2));
			RegistryEntry<ChunkGeneratorSettings> registryEntry3 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.AMPLIFIED);
			this.register(WorldPresets.AMPLIFIED, this.createOverworldOptions(multiNoiseBiomeSource, registryEntry3));
			RegistryEntry.Reference<Biome> reference = this.biomeLookup.getOrThrow(BiomeKeys.PLAINS);
			this.register(WorldPresets.SINGLE_BIOME_SURFACE, this.createOverworldOptions(new FixedBiomeSource(reference), registryEntry));
			this.register(
				WorldPresets.FLAT,
				this.createOverworldOptions(
					new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig(this.biomeLookup, this.structureSetLookup, this.featureLookup))
				)
			);
			this.register(WorldPresets.DEBUG_ALL_BLOCK_STATES, this.createOverworldOptions(new DebugChunkGenerator(reference)));
		}
	}
}
