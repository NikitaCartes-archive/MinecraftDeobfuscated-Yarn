package net.minecraft.client.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

@Environment(EnvType.CLIENT)
public abstract class GeneratorType {
	public static final GeneratorType DEFAULT = new GeneratorType("default") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
		}
	};
	private static final GeneratorType FLAT = new GeneratorType("flat") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig());
		}
	};
	private static final GeneratorType LARGE_BIOMES = new GeneratorType("large_biomes") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, true), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
		}
	};
	public static final GeneratorType AMPLIFIED = new GeneratorType("amplified") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.AMPLIFIED.getChunkGeneratorType());
		}
	};
	private static final GeneratorType SINGLE_BIOME_SURFACE = new GeneratorType("single_biome_surface") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
		}
	};
	private static final GeneratorType SINGLE_BIOME_CAVES = new GeneratorType("single_biome_caves") {
		@Override
		public GeneratorOptions method_29077(RegistryTracker.Modifiable modifiable, long l, boolean bl, boolean bl2) {
			return new GeneratorOptions(
				l, bl, bl2, GeneratorOptions.method_29962(DimensionType.method_28517(l), DimensionType::getOverworldCavesDimensionType, this.method_29076(l))
			);
		}

		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.CAVES.getChunkGeneratorType());
		}
	};
	private static final GeneratorType SINGLE_BIOME_FLOATING_ISLANDS = new GeneratorType("single_biome_floating_islands") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.FLOATING_ISLANDS.getChunkGeneratorType());
		}
	};
	private static final GeneratorType DEBUG_ALL_BLOCK_STATES = new GeneratorType("debug_all_block_states") {
		@Override
		protected ChunkGenerator method_29076(long l) {
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
								GeneratorOptions.method_28608(generatorOptions.getDimensionMap(), new FlatChunkGenerator(flatChunkGeneratorConfig))
							)
						),
				chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).method_28545() : FlatChunkGeneratorConfig.getDefaultConfig()
			);
		},
		Optional.of(SINGLE_BIOME_SURFACE),
		(createWorldScreen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				biome -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(method_29079(generatorOptions, SINGLE_BIOME_SURFACE, biome)),
				method_29083(generatorOptions)
			),
		Optional.of(SINGLE_BIOME_CAVES),
		(createWorldScreen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				biome -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(method_29079(generatorOptions, SINGLE_BIOME_CAVES, biome)),
				method_29083(generatorOptions)
			),
		Optional.of(SINGLE_BIOME_FLOATING_ISLANDS),
		(createWorldScreen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				biome -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(method_29079(generatorOptions, SINGLE_BIOME_FLOATING_ISLANDS, biome)),
				method_29083(generatorOptions)
			)
	);
	private final Text translationKey;

	private GeneratorType(String translationKey) {
		this.translationKey = new TranslatableText("generator." + translationKey);
	}

	private static GeneratorOptions method_29079(GeneratorOptions generatorOptions, GeneratorType generatorType, Biome biome) {
		BiomeSource biomeSource = new FixedBiomeSource(biome);
		ChunkGeneratorType chunkGeneratorType;
		if (generatorType == SINGLE_BIOME_CAVES) {
			chunkGeneratorType = ChunkGeneratorType.Preset.CAVES.getChunkGeneratorType();
		} else if (generatorType == SINGLE_BIOME_FLOATING_ISLANDS) {
			chunkGeneratorType = ChunkGeneratorType.Preset.FLOATING_ISLANDS.getChunkGeneratorType();
		} else {
			chunkGeneratorType = ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType();
		}

		return new GeneratorOptions(
			generatorOptions.getSeed(),
			generatorOptions.shouldGenerateStructures(),
			generatorOptions.hasBonusChest(),
			GeneratorOptions.method_28608(generatorOptions.getDimensionMap(), new SurfaceChunkGenerator(biomeSource, generatorOptions.getSeed(), chunkGeneratorType))
		);
	}

	private static Biome method_29083(GeneratorOptions generatorOptions) {
		return (Biome)generatorOptions.getChunkGenerator().getBiomeSource().method_28443().stream().findFirst().orElse(Biomes.OCEAN);
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

	public GeneratorOptions method_29077(RegistryTracker.Modifiable modifiable, long l, boolean bl, boolean bl2) {
		return new GeneratorOptions(l, bl, bl2, GeneratorOptions.method_28608(DimensionType.method_28517(l), this.method_29076(l)));
	}

	protected abstract ChunkGenerator method_29076(long l);

	@Environment(EnvType.CLIENT)
	public interface ScreenProvider {
		Screen createEditScreen(CreateWorldScreen createWorldScreen, GeneratorOptions generatorOptions);
	}
}
