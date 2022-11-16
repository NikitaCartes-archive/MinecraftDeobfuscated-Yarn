/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
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
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

public class WorldPresets {
    public static final RegistryKey<WorldPreset> DEFAULT = WorldPresets.of("normal");
    public static final RegistryKey<WorldPreset> FLAT = WorldPresets.of("flat");
    public static final RegistryKey<WorldPreset> LARGE_BIOMES = WorldPresets.of("large_biomes");
    public static final RegistryKey<WorldPreset> AMPLIFIED = WorldPresets.of("amplified");
    public static final RegistryKey<WorldPreset> SINGLE_BIOME_SURFACE = WorldPresets.of("single_biome_surface");
    public static final RegistryKey<WorldPreset> DEBUG_ALL_BLOCK_STATES = WorldPresets.of("debug_all_block_states");

    public static void bootstrap(Registerable<WorldPreset> presetRegisterable) {
        new Registrar(presetRegisterable).bootstrap();
    }

    private static RegistryKey<WorldPreset> of(String id) {
        return RegistryKey.of(RegistryKeys.WORLD_PRESET, new Identifier(id));
    }

    public static Optional<RegistryKey<WorldPreset>> getWorldPreset(Registry<DimensionOptions> registry) {
        return registry.getOrEmpty(DimensionOptions.OVERWORLD).flatMap(overworld -> {
            ChunkGenerator chunkGenerator = overworld.chunkGenerator();
            if (chunkGenerator instanceof FlatChunkGenerator) {
                return Optional.of(FLAT);
            }
            if (chunkGenerator instanceof DebugChunkGenerator) {
                return Optional.of(DEBUG_ALL_BLOCK_STATES);
            }
            return Optional.empty();
        });
    }

    public static DimensionOptionsRegistryHolder createDemoOptions(DynamicRegistryManager dynamicRegistryManager) {
        return dynamicRegistryManager.get(RegistryKeys.WORLD_PRESET).entryOf(DEFAULT).value().createDimensionsRegistryHolder();
    }

    public static DimensionOptions getDefaultOverworldOptions(DynamicRegistryManager dynamicRegistryManager) {
        return dynamicRegistryManager.get(RegistryKeys.WORLD_PRESET).entryOf(DEFAULT).value().getOverworld().orElseThrow();
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
            RegistryEntryLookup<DimensionType> registryEntryLookup = presetRegisterable.getRegistryLookup(RegistryKeys.DIMENSION_TYPE);
            this.chunkGeneratorSettingsLookup = presetRegisterable.getRegistryLookup(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
            this.biomeLookup = presetRegisterable.getRegistryLookup(RegistryKeys.BIOME);
            this.featureLookup = presetRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
            this.structureSetLookup = presetRegisterable.getRegistryLookup(RegistryKeys.STRUCTURE_SET);
            this.overworldDimensionType = registryEntryLookup.getOrThrow(DimensionTypes.OVERWORLD);
            RegistryEntry.Reference<DimensionType> registryEntry = registryEntryLookup.getOrThrow(DimensionTypes.THE_NETHER);
            RegistryEntry.Reference<ChunkGeneratorSettings> registryEntry2 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.NETHER);
            this.netherDimensionOptions = new DimensionOptions(registryEntry, new NoiseChunkGenerator((BiomeSource)MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(this.biomeLookup), registryEntry2));
            RegistryEntry.Reference<DimensionType> registryEntry3 = registryEntryLookup.getOrThrow(DimensionTypes.THE_END);
            RegistryEntry.Reference<ChunkGeneratorSettings> registryEntry4 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.END);
            this.endDimensionOptions = new DimensionOptions(registryEntry3, new NoiseChunkGenerator((BiomeSource)TheEndBiomeSource.createVanilla(this.biomeLookup), registryEntry4));
        }

        private DimensionOptions createOverworldOptions(ChunkGenerator chunkGenerator) {
            return new DimensionOptions(this.overworldDimensionType, chunkGenerator);
        }

        private DimensionOptions createOverworldOptions(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettings) {
            return this.createOverworldOptions(new NoiseChunkGenerator(biomeSource, chunkGeneratorSettings));
        }

        private WorldPreset createPreset(DimensionOptions dimensionOptions) {
            return new WorldPreset(Map.of(DimensionOptions.OVERWORLD, dimensionOptions, DimensionOptions.NETHER, this.netherDimensionOptions, DimensionOptions.END, this.endDimensionOptions));
        }

        private void register(RegistryKey<WorldPreset> key, DimensionOptions dimensionOptions) {
            this.presetRegisterable.register(key, this.createPreset(dimensionOptions));
        }

        public void bootstrap() {
            MultiNoiseBiomeSource multiNoiseBiomeSource = MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(this.biomeLookup);
            RegistryEntry.Reference<ChunkGeneratorSettings> registryEntry = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
            this.register(DEFAULT, this.createOverworldOptions(multiNoiseBiomeSource, registryEntry));
            RegistryEntry.Reference<ChunkGeneratorSettings> registryEntry2 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.LARGE_BIOMES);
            this.register(LARGE_BIOMES, this.createOverworldOptions(multiNoiseBiomeSource, registryEntry2));
            RegistryEntry.Reference<ChunkGeneratorSettings> registryEntry3 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.AMPLIFIED);
            this.register(AMPLIFIED, this.createOverworldOptions(multiNoiseBiomeSource, registryEntry3));
            RegistryEntry.Reference<Biome> reference = this.biomeLookup.getOrThrow(BiomeKeys.PLAINS);
            this.register(SINGLE_BIOME_SURFACE, this.createOverworldOptions(new FixedBiomeSource(reference), registryEntry));
            this.register(FLAT, this.createOverworldOptions(new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig(this.biomeLookup, this.structureSetLookup, this.featureLookup))));
            this.register(DEBUG_ALL_BLOCK_STATES, this.createOverworldOptions(new DebugChunkGenerator(reference)));
        }
    }
}

