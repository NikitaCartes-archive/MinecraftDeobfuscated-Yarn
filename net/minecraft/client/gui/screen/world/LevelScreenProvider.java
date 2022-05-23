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
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

@Environment(value=EnvType.CLIENT)
public interface LevelScreenProvider {
    public static final Map<Optional<RegistryKey<WorldPreset>>, LevelScreenProvider> WORLD_PRESET_TO_SCREEN_PROVIDER = Map.of(Optional.of(WorldPresets.FLAT), (parent, generatorOptionsHolder) -> {
        ChunkGenerator chunkGenerator = generatorOptionsHolder.generatorOptions().getChunkGenerator();
        DynamicRegistryManager.Immutable dynamicRegistryManager = generatorOptionsHolder.dynamicRegistryManager();
        Registry<Biome> registry = dynamicRegistryManager.get(Registry.BIOME_KEY);
        Registry<StructureSet> registry2 = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
        return new CustomizeFlatLevelScreen(parent, config -> createWorldScreen.moreOptionsDialog.apply(LevelScreenProvider.createModifier(config)), chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).getConfig() : FlatChunkGeneratorConfig.getDefaultConfig(registry, registry2));
    }, Optional.of(WorldPresets.SINGLE_BIOME_SURFACE), (parent, generatorOptionsHolder) -> new CustomizeBuffetLevelScreen(parent, generatorOptionsHolder, biomeEntry -> createWorldScreen.moreOptionsDialog.apply(LevelScreenProvider.createModifier(biomeEntry))));

    public Screen createEditScreen(CreateWorldScreen var1, GeneratorOptionsHolder var2);

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(FlatChunkGeneratorConfig config) {
        return (dynamicRegistryManager, generatorOptions) -> {
            Registry<StructureSet> registry = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
            FlatChunkGenerator chunkGenerator = new FlatChunkGenerator(registry, config);
            return GeneratorOptions.create(dynamicRegistryManager, generatorOptions, chunkGenerator);
        };
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(RegistryEntry<Biome> biomeEntry) {
        return (dynamicRegistryManager, generatorOptions) -> {
            Registry<StructureSet> registry = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
            Registry<ChunkGeneratorSettings> registry2 = dynamicRegistryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
            Registry<DoublePerlinNoiseSampler.NoiseParameters> registry3 = dynamicRegistryManager.get(Registry.NOISE_KEY);
            RegistryEntry<ChunkGeneratorSettings> registryEntry2 = registry2.getOrCreateEntry(ChunkGeneratorSettings.OVERWORLD);
            FixedBiomeSource biomeSource = new FixedBiomeSource(biomeEntry);
            NoiseChunkGenerator chunkGenerator = new NoiseChunkGenerator(registry, registry3, (BiomeSource)biomeSource, registryEntry2);
            return GeneratorOptions.create(dynamicRegistryManager, generatorOptions, chunkGenerator);
        };
    }
}

