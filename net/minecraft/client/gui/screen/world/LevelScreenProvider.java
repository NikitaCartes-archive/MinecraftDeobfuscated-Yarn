/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

@Environment(value=EnvType.CLIENT)
public interface LevelScreenProvider {
    public static final Map<Optional<RegistryKey<WorldPreset>>, LevelScreenProvider> WORLD_PRESET_TO_SCREEN_PROVIDER = Map.of(Optional.of(WorldPresets.FLAT), (parent, generatorOptionsHolder) -> {
        ChunkGenerator chunkGenerator = generatorOptionsHolder.selectedDimensions().getChunkGenerator();
        DynamicRegistryManager.Immutable dynamicRegistryManager = generatorOptionsHolder.getCombinedRegistryManager();
        RegistryWrapper.Impl<Biome> registryEntryLookup = dynamicRegistryManager.getWrapperOrThrow(Registry.BIOME_KEY);
        RegistryWrapper.Impl<StructureSet> registryEntryLookup2 = dynamicRegistryManager.getWrapperOrThrow(Registry.STRUCTURE_SET_KEY);
        RegistryWrapper.Impl<PlacedFeature> registryEntryLookup3 = dynamicRegistryManager.getWrapperOrThrow(Registry.PLACED_FEATURE_KEY);
        return new CustomizeFlatLevelScreen(parent, config -> createWorldScreen.moreOptionsDialog.apply(LevelScreenProvider.createModifier(config)), chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).getConfig() : FlatChunkGeneratorConfig.getDefaultConfig(registryEntryLookup, registryEntryLookup2, registryEntryLookup3));
    }, Optional.of(WorldPresets.SINGLE_BIOME_SURFACE), (parent, generatorOptionsHolder) -> new CustomizeBuffetLevelScreen(parent, generatorOptionsHolder, biomeEntry -> createWorldScreen.moreOptionsDialog.apply(LevelScreenProvider.createModifier(biomeEntry))));

    public Screen createEditScreen(CreateWorldScreen var1, GeneratorOptionsHolder var2);

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(FlatChunkGeneratorConfig config) {
        return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
            FlatChunkGenerator chunkGenerator = new FlatChunkGenerator(config);
            return dimensionsRegistryHolder.with((DynamicRegistryManager)dynamicRegistryManager, chunkGenerator);
        };
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(RegistryEntry<Biome> biomeEntry) {
        return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
            Registry<ChunkGeneratorSettings> registry = dynamicRegistryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
            RegistryEntry.Reference<ChunkGeneratorSettings> registryEntry2 = registry.entryOf(ChunkGeneratorSettings.OVERWORLD);
            FixedBiomeSource biomeSource = new FixedBiomeSource(biomeEntry);
            NoiseChunkGenerator chunkGenerator = new NoiseChunkGenerator((BiomeSource)biomeSource, registryEntry2);
            return dimensionsRegistryHolder.with((DynamicRegistryManager)dynamicRegistryManager, chunkGenerator);
        };
    }
}

