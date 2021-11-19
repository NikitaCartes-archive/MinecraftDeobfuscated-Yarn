package net.minecraft.client.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

@Environment(EnvType.CLIENT)
public abstract class GeneratorType {
	public static final GeneratorType DEFAULT = new GeneratorType("default") {
		@Override
		protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
			return GeneratorOptions.createOverworldGenerator(registryManager, seed);
		}
	};
	private static final GeneratorType FLAT = new GeneratorType("flat") {
		@Override
		protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
			return new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig(registryManager.get(Registry.BIOME_KEY)));
		}
	};
	public static final GeneratorType LARGE_BIOMES = new GeneratorType("large_biomes") {
		@Override
		protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
			return GeneratorOptions.createGenerator(registryManager, seed, ChunkGeneratorSettings.LARGE_BIOMES);
		}
	};
	public static final GeneratorType AMPLIFIED = new GeneratorType("amplified") {
		@Override
		protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
			return GeneratorOptions.createGenerator(registryManager, seed, ChunkGeneratorSettings.AMPLIFIED);
		}
	};
	private static final GeneratorType SINGLE_BIOME_SURFACE = new GeneratorType("single_biome_surface") {
		@Override
		protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
			return GeneratorType.createNoiseChunkGenerator(registryManager, seed, ChunkGeneratorSettings.OVERWORLD);
		}
	};
	private static final GeneratorType DEBUG_ALL_BLOCK_STATES = new GeneratorType("debug_all_block_states") {
		@Override
		protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
			return new DebugChunkGenerator(registryManager.get(Registry.BIOME_KEY));
		}
	};
	public static final List<GeneratorType> VALUES = Lists.<GeneratorType>newArrayList(
		DEFAULT, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, DEBUG_ALL_BLOCK_STATES
	);
	public static final Map<Optional<GeneratorType>, GeneratorType.ScreenProvider> SCREEN_PROVIDERS = ImmutableMap.of(
		Optional.of(FLAT),
		(screen, generatorOptions) -> {
			ChunkGenerator chunkGenerator = generatorOptions.getChunkGenerator();
			return new CustomizeFlatLevelScreen(
				screen,
				config -> screen.moreOptionsDialog
						.setGeneratorOptions(
							new GeneratorOptions(
								generatorOptions.getSeed(),
								generatorOptions.shouldGenerateStructures(),
								generatorOptions.hasBonusChest(),
								GeneratorOptions.getRegistryWithReplacedOverworldGenerator(
									screen.moreOptionsDialog.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY), generatorOptions.getDimensions(), new FlatChunkGenerator(config)
								)
							)
						),
				chunkGenerator instanceof FlatChunkGenerator
					? ((FlatChunkGenerator)chunkGenerator).getConfig()
					: FlatChunkGeneratorConfig.getDefaultConfig(screen.moreOptionsDialog.getRegistryManager().get(Registry.BIOME_KEY))
			);
		},
		Optional.of(SINGLE_BIOME_SURFACE),
		(screen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				screen,
				screen.moreOptionsDialog.getRegistryManager(),
				biome -> screen.moreOptionsDialog
						.setGeneratorOptions(createFixedBiomeOptions(screen.moreOptionsDialog.getRegistryManager(), generatorOptions, SINGLE_BIOME_SURFACE, biome)),
				getFirstBiome(screen.moreOptionsDialog.getRegistryManager(), generatorOptions)
			)
	);
	private final Text displayName;

	static NoiseChunkGenerator createNoiseChunkGenerator(DynamicRegistryManager registryManager, long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
		return new NoiseChunkGenerator(
			registryManager.get(Registry.NOISE_WORLDGEN),
			new FixedBiomeSource(registryManager.get(Registry.BIOME_KEY).getOrThrow(BiomeKeys.PLAINS)),
			seed,
			() -> registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getOrThrow(settingsKey)
		);
	}

	GeneratorType(String translationKeySuffix) {
		this.displayName = new TranslatableText("generator." + translationKeySuffix);
	}

	private static GeneratorOptions createFixedBiomeOptions(
		DynamicRegistryManager registryManager, GeneratorOptions generatorOptions, GeneratorType type, Biome biome
	) {
		BiomeSource biomeSource = new FixedBiomeSource(biome);
		Registry<DimensionType> registry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
		Registry<ChunkGeneratorSettings> registry2 = registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
		Supplier<ChunkGeneratorSettings> supplier = () -> registry2.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
		return new GeneratorOptions(
			generatorOptions.getSeed(),
			generatorOptions.shouldGenerateStructures(),
			generatorOptions.hasBonusChest(),
			GeneratorOptions.getRegistryWithReplacedOverworldGenerator(
				registry,
				generatorOptions.getDimensions(),
				new NoiseChunkGenerator(registryManager.get(Registry.NOISE_WORLDGEN), biomeSource, generatorOptions.getSeed(), supplier)
			)
		);
	}

	private static Biome getFirstBiome(DynamicRegistryManager registryManager, GeneratorOptions options) {
		return (Biome)options.getChunkGenerator()
			.getBiomeSource()
			.getBiomes()
			.stream()
			.findFirst()
			.orElse(registryManager.get(Registry.BIOME_KEY).getOrThrow(BiomeKeys.PLAINS));
	}

	public static Optional<GeneratorType> fromGeneratorOptions(GeneratorOptions generatorOptions) {
		ChunkGenerator chunkGenerator = generatorOptions.getChunkGenerator();
		if (chunkGenerator instanceof FlatChunkGenerator) {
			return Optional.of(FLAT);
		} else {
			return chunkGenerator instanceof DebugChunkGenerator ? Optional.of(DEBUG_ALL_BLOCK_STATES) : Optional.empty();
		}
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public GeneratorOptions createDefaultOptions(DynamicRegistryManager.Impl registryManager, long seed, boolean generateStructures, boolean bonusChest) {
		return new GeneratorOptions(
			seed,
			generateStructures,
			bonusChest,
			GeneratorOptions.getRegistryWithReplacedOverworldGenerator(
				registryManager.get(Registry.DIMENSION_TYPE_KEY),
				DimensionType.createDefaultDimensionOptions(registryManager, seed),
				this.getChunkGenerator(registryManager, seed)
			)
		);
	}

	protected abstract ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed);

	public static boolean isNotDebug(GeneratorType generatorType) {
		return generatorType != DEBUG_ALL_BLOCK_STATES;
	}

	@Environment(EnvType.CLIENT)
	public interface ScreenProvider {
		Screen createEditScreen(CreateWorldScreen screen, GeneratorOptions generatorOptions);
	}
}
