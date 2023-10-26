package net.minecraft.client.gui.screen.world;

import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSet;
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

@Environment(EnvType.CLIENT)
public interface LevelScreenProvider {
	Map<Optional<RegistryKey<WorldPreset>>, LevelScreenProvider> WORLD_PRESET_TO_SCREEN_PROVIDER = Map.of(
		Optional.of(WorldPresets.FLAT),
		(LevelScreenProvider)(parent, generatorOptionsHolder) -> {
			ChunkGenerator chunkGenerator = generatorOptionsHolder.selectedDimensions().getChunkGenerator();
			DynamicRegistryManager dynamicRegistryManager = generatorOptionsHolder.getCombinedRegistryManager();
			RegistryEntryLookup<Biome> registryEntryLookup = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.BIOME);
			RegistryEntryLookup<StructureSet> registryEntryLookup2 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.STRUCTURE_SET);
			RegistryEntryLookup<PlacedFeature> registryEntryLookup3 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE);
			return new CustomizeFlatLevelScreen(
				parent,
				config -> parent.getWorldCreator().applyModifier(createModifier(config)),
				chunkGenerator instanceof FlatChunkGenerator
					? ((FlatChunkGenerator)chunkGenerator).getConfig()
					: FlatChunkGeneratorConfig.getDefaultConfig(registryEntryLookup, registryEntryLookup2, registryEntryLookup3)
			);
		},
		Optional.of(WorldPresets.SINGLE_BIOME_SURFACE),
		(LevelScreenProvider)(parent, generatorOptionsHolder) -> new CustomizeBuffetLevelScreen(
				parent, generatorOptionsHolder, biomeEntry -> parent.getWorldCreator().applyModifier(createModifier(biomeEntry))
			)
	);

	Screen createEditScreen(CreateWorldScreen parent, GeneratorOptionsHolder generatorOptionsHolder);

	private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(FlatChunkGeneratorConfig config) {
		return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
			ChunkGenerator chunkGenerator = new FlatChunkGenerator(config);
			return dimensionsRegistryHolder.with(dynamicRegistryManager, chunkGenerator);
		};
	}

	private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(RegistryEntry<Biome> biomeEntry) {
		return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
			Registry<ChunkGeneratorSettings> registry = dynamicRegistryManager.get(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
			RegistryEntry<ChunkGeneratorSettings> registryEntry2 = registry.entryOf(ChunkGeneratorSettings.OVERWORLD);
			BiomeSource biomeSource = new FixedBiomeSource(biomeEntry);
			ChunkGenerator chunkGenerator = new NoiseChunkGenerator(biomeSource, registryEntry2);
			return dimensionsRegistryHolder.with(dynamicRegistryManager, chunkGenerator);
		};
	}
}
