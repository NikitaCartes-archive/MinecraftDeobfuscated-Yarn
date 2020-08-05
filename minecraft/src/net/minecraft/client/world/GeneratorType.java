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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
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
		protected ChunkGenerator getChunkGenerator(Registry<Biome> registry, Registry<ChunkGeneratorSettings> registry2, long l) {
			return new NoiseChunkGenerator(new VanillaLayeredBiomeSource(l, false, false, registry), l, () -> registry2.method_31140(ChunkGeneratorSettings.OVERWORLD));
		}
	};
	private static final GeneratorType FLAT = new GeneratorType("flat") {
		@Override
		protected ChunkGenerator getChunkGenerator(Registry<Biome> registry, Registry<ChunkGeneratorSettings> registry2, long l) {
			return new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig());
		}
	};
	private static final GeneratorType LARGE_BIOMES = new GeneratorType("large_biomes") {
		@Override
		protected ChunkGenerator getChunkGenerator(Registry<Biome> registry, Registry<ChunkGeneratorSettings> registry2, long l) {
			return new NoiseChunkGenerator(new VanillaLayeredBiomeSource(l, false, true, registry), l, () -> registry2.method_31140(ChunkGeneratorSettings.OVERWORLD));
		}
	};
	public static final GeneratorType AMPLIFIED = new GeneratorType("amplified") {
		@Override
		protected ChunkGenerator getChunkGenerator(Registry<Biome> registry, Registry<ChunkGeneratorSettings> registry2, long l) {
			return new NoiseChunkGenerator(new VanillaLayeredBiomeSource(l, false, false, registry), l, () -> registry2.method_31140(ChunkGeneratorSettings.AMPLIFIED));
		}
	};
	private static final GeneratorType SINGLE_BIOME_SURFACE = new GeneratorType("single_biome_surface") {
		@Override
		protected ChunkGenerator getChunkGenerator(Registry<Biome> registry, Registry<ChunkGeneratorSettings> registry2, long l) {
			return new NoiseChunkGenerator(new FixedBiomeSource(registry.method_31140(Biomes.PLAINS)), l, () -> registry2.method_31140(ChunkGeneratorSettings.OVERWORLD));
		}
	};
	private static final GeneratorType SINGLE_BIOME_CAVES = new GeneratorType("single_biome_caves") {
		@Override
		public GeneratorOptions method_29077(DynamicRegistryManager.Impl impl, long l, boolean bl, boolean bl2) {
			Registry<Biome> registry = impl.get(Registry.BIOME_KEY);
			Registry<DimensionType> registry2 = impl.get(Registry.DIMENSION_TYPE_KEY);
			Registry<ChunkGeneratorSettings> registry3 = impl.get(Registry.NOISE_SETTINGS_WORLDGEN);
			return new GeneratorOptions(
				l,
				bl,
				bl2,
				GeneratorOptions.method_29962(
					DimensionType.method_28517(registry2, registry, registry3, l),
					() -> registry2.method_31140(DimensionType.OVERWORLD_CAVES_REGISTRY_KEY),
					this.getChunkGenerator(registry, registry3, l)
				)
			);
		}

		@Override
		protected ChunkGenerator getChunkGenerator(Registry<Biome> registry, Registry<ChunkGeneratorSettings> registry2, long l) {
			return new NoiseChunkGenerator(new FixedBiomeSource(registry.method_31140(Biomes.PLAINS)), l, () -> registry2.method_31140(ChunkGeneratorSettings.CAVES));
		}
	};
	private static final GeneratorType SINGLE_BIOME_FLOATING_ISLANDS = new GeneratorType("single_biome_floating_islands") {
		@Override
		protected ChunkGenerator getChunkGenerator(Registry<Biome> registry, Registry<ChunkGeneratorSettings> registry2, long l) {
			return new NoiseChunkGenerator(
				new FixedBiomeSource(registry.method_31140(Biomes.PLAINS)), l, () -> registry2.method_31140(ChunkGeneratorSettings.FLOATING_ISLANDS)
			);
		}
	};
	private static final GeneratorType DEBUG_ALL_BLOCK_STATES = new GeneratorType("debug_all_block_states") {
		@Override
		protected ChunkGenerator getChunkGenerator(Registry<Biome> registry, Registry<ChunkGeneratorSettings> registry2, long l) {
			return DebugChunkGenerator.INSTANCE;
		}
	};
	public static final List<GeneratorType> VALUES = Lists.<GeneratorType>newArrayList(
		DEFAULT, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, SINGLE_BIOME_CAVES, SINGLE_BIOME_FLOATING_ISLANDS, DEBUG_ALL_BLOCK_STATES
	);
	public static final Map<Optional<GeneratorType>, GeneratorType.ScreenProvider> field_25053 = ImmutableMap.of(
		Optional.of(FLAT),
		(createWorldScreen, generatorOptions) -> {
			ChunkGenerator chunkGenerator = generatorOptions.getChunkGenerator();
			return new CustomizeFlatLevelScreen(
				createWorldScreen,
				flatChunkGeneratorConfig -> createWorldScreen.moreOptionsDialog
						.setGeneratorOptions(
							new GeneratorOptions(
								generatorOptions.getSeed(),
								generatorOptions.shouldGenerateStructures(),
								generatorOptions.hasBonusChest(),
								GeneratorOptions.method_28608(
									createWorldScreen.moreOptionsDialog.method_29700().get(Registry.DIMENSION_TYPE_KEY),
									generatorOptions.getDimensionMap(),
									new FlatChunkGenerator(flatChunkGeneratorConfig)
								)
							)
						),
				chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).method_28545() : FlatChunkGeneratorConfig.getDefaultConfig()
			);
		},
		Optional.of(SINGLE_BIOME_SURFACE),
		(createWorldScreen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				createWorldScreen.moreOptionsDialog.method_29700(),
				biome -> createWorldScreen.moreOptionsDialog
						.setGeneratorOptions(method_29079(createWorldScreen.moreOptionsDialog.method_29700(), generatorOptions, SINGLE_BIOME_SURFACE, biome)),
				getFirstBiome(createWorldScreen.moreOptionsDialog.method_29700(), generatorOptions)
			),
		Optional.of(SINGLE_BIOME_CAVES),
		(createWorldScreen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				createWorldScreen.moreOptionsDialog.method_29700(),
				biome -> createWorldScreen.moreOptionsDialog
						.setGeneratorOptions(method_29079(createWorldScreen.moreOptionsDialog.method_29700(), generatorOptions, SINGLE_BIOME_CAVES, biome)),
				getFirstBiome(createWorldScreen.moreOptionsDialog.method_29700(), generatorOptions)
			),
		Optional.of(SINGLE_BIOME_FLOATING_ISLANDS),
		(createWorldScreen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				createWorldScreen.moreOptionsDialog.method_29700(),
				biome -> createWorldScreen.moreOptionsDialog
						.setGeneratorOptions(method_29079(createWorldScreen.moreOptionsDialog.method_29700(), generatorOptions, SINGLE_BIOME_FLOATING_ISLANDS, biome)),
				getFirstBiome(createWorldScreen.moreOptionsDialog.method_29700(), generatorOptions)
			)
	);
	private final Text translationKey;

	private GeneratorType(String translationKey) {
		this.translationKey = new TranslatableText("generator." + translationKey);
	}

	private static GeneratorOptions method_29079(
		DynamicRegistryManager dynamicRegistryManager, GeneratorOptions generatorOptions, GeneratorType generatorType, Biome biome
	) {
		BiomeSource biomeSource = new FixedBiomeSource(biome);
		Registry<DimensionType> registry = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY);
		Registry<ChunkGeneratorSettings> registry2 = dynamicRegistryManager.get(Registry.NOISE_SETTINGS_WORLDGEN);
		Supplier<ChunkGeneratorSettings> supplier;
		if (generatorType == SINGLE_BIOME_CAVES) {
			supplier = () -> registry2.method_31140(ChunkGeneratorSettings.CAVES);
		} else if (generatorType == SINGLE_BIOME_FLOATING_ISLANDS) {
			supplier = () -> registry2.method_31140(ChunkGeneratorSettings.FLOATING_ISLANDS);
		} else {
			supplier = () -> registry2.method_31140(ChunkGeneratorSettings.OVERWORLD);
		}

		return new GeneratorOptions(
			generatorOptions.getSeed(),
			generatorOptions.shouldGenerateStructures(),
			generatorOptions.hasBonusChest(),
			GeneratorOptions.method_28608(registry, generatorOptions.getDimensionMap(), new NoiseChunkGenerator(biomeSource, generatorOptions.getSeed(), supplier))
		);
	}

	private static Biome getFirstBiome(DynamicRegistryManager dynamicRegistryManager, GeneratorOptions generatorOptions) {
		return (Biome)generatorOptions.getChunkGenerator()
			.getBiomeSource()
			.getBiomes()
			.stream()
			.findFirst()
			.orElse(dynamicRegistryManager.get(Registry.BIOME_KEY).method_31140(Biomes.PLAINS));
	}

	public static Optional<GeneratorType> method_29078(GeneratorOptions generatorOptions) {
		ChunkGenerator chunkGenerator = generatorOptions.getChunkGenerator();
		if (chunkGenerator instanceof FlatChunkGenerator) {
			return Optional.of(FLAT);
		} else {
			return chunkGenerator instanceof DebugChunkGenerator ? Optional.of(DEBUG_ALL_BLOCK_STATES) : Optional.empty();
		}
	}

	public Text getTranslationKey() {
		return this.translationKey;
	}

	public GeneratorOptions method_29077(DynamicRegistryManager.Impl impl, long l, boolean bl, boolean bl2) {
		Registry<Biome> registry = impl.get(Registry.BIOME_KEY);
		Registry<DimensionType> registry2 = impl.get(Registry.DIMENSION_TYPE_KEY);
		Registry<ChunkGeneratorSettings> registry3 = impl.get(Registry.NOISE_SETTINGS_WORLDGEN);
		return new GeneratorOptions(
			l,
			bl,
			bl2,
			GeneratorOptions.method_28608(registry2, DimensionType.method_28517(registry2, registry, registry3, l), this.getChunkGenerator(registry, registry3, l))
		);
	}

	protected abstract ChunkGenerator getChunkGenerator(Registry<Biome> registry, Registry<ChunkGeneratorSettings> registry2, long l);

	@Environment(EnvType.CLIENT)
	public interface ScreenProvider {
		Screen createEditScreen(CreateWorldScreen createWorldScreen, GeneratorOptions generatorOptions);
	}
}
