package net.minecraft;

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
public abstract class class_5317 {
	public static final class_5317 field_25050 = new class_5317("default") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
		}
	};
	private static final class_5317 field_25054 = new class_5317("flat") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig());
		}
	};
	private static final class_5317 field_25055 = new class_5317("large_biomes") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, true), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
		}
	};
	public static final class_5317 field_25051 = new class_5317("amplified") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.AMPLIFIED.getChunkGeneratorType());
		}
	};
	private static final class_5317 field_25056 = new class_5317("single_biome_surface") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
		}
	};
	private static final class_5317 field_25057 = new class_5317("single_biome_caves") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.NETHER.getChunkGeneratorType());
		}
	};
	private static final class_5317 field_25058 = new class_5317("single_biome_floating_islands") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.END.getChunkGeneratorType());
		}
	};
	private static final class_5317 field_25059 = new class_5317("debug_all_block_states") {
		@Override
		protected ChunkGenerator method_29076(long l) {
			return DebugChunkGenerator.INSTANCE;
		}
	};
	public static final List<class_5317> field_25052 = Lists.<class_5317>newArrayList(
		field_25050, field_25054, field_25055, field_25051, field_25056, field_25057, field_25058, field_25059
	);
	public static final Map<Optional<class_5317>, class_5317.class_5293> field_25053 = ImmutableMap.of(
		Optional.of(field_25054),
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
								GeneratorOptions.method_28608(generatorOptions.method_28609(), new FlatChunkGenerator(flatChunkGeneratorConfig))
							)
						),
				chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).method_28545() : FlatChunkGeneratorConfig.getDefaultConfig()
			);
		},
		Optional.of(field_25056),
		(createWorldScreen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				biome -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(method_29079(generatorOptions, field_25056, biome)),
				method_29083(generatorOptions)
			),
		Optional.of(field_25057),
		(createWorldScreen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				biome -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(method_29079(generatorOptions, field_25057, biome)),
				method_29083(generatorOptions)
			),
		Optional.of(field_25058),
		(createWorldScreen, generatorOptions) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				biome -> createWorldScreen.moreOptionsDialog.setGeneratorOptions(method_29079(generatorOptions, field_25058, biome)),
				method_29083(generatorOptions)
			)
	);
	private final Text field_25060;

	private class_5317(String string) {
		this.field_25060 = new TranslatableText("generator." + string);
	}

	private static GeneratorOptions method_29079(GeneratorOptions generatorOptions, class_5317 arg, Biome biome) {
		BiomeSource biomeSource = new FixedBiomeSource(biome);
		ChunkGeneratorType chunkGeneratorType;
		if (arg == field_25057) {
			chunkGeneratorType = ChunkGeneratorType.Preset.NETHER.getChunkGeneratorType();
		} else if (arg == field_25058) {
			chunkGeneratorType = ChunkGeneratorType.Preset.END.getChunkGeneratorType();
		} else {
			chunkGeneratorType = ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType();
		}

		return new GeneratorOptions(
			generatorOptions.getSeed(),
			generatorOptions.shouldGenerateStructures(),
			generatorOptions.hasBonusChest(),
			GeneratorOptions.method_28608(generatorOptions.method_28609(), new SurfaceChunkGenerator(biomeSource, generatorOptions.getSeed(), chunkGeneratorType))
		);
	}

	private static Biome method_29083(GeneratorOptions generatorOptions) {
		return (Biome)generatorOptions.getChunkGenerator().getBiomeSource().method_28443().stream().findFirst().orElse(Biomes.OCEAN);
	}

	public static Optional<class_5317> method_29078(GeneratorOptions generatorOptions) {
		ChunkGenerator chunkGenerator = generatorOptions.getChunkGenerator();
		if (chunkGenerator instanceof FlatChunkGenerator) {
			return Optional.of(field_25054);
		} else {
			return chunkGenerator instanceof DebugChunkGenerator ? Optional.of(field_25059) : Optional.empty();
		}
	}

	public Text method_29075() {
		return this.field_25060;
	}

	public GeneratorOptions method_29077(long l, boolean bl, boolean bl2) {
		return new GeneratorOptions(l, bl, bl2, GeneratorOptions.method_28608(DimensionType.method_28517(l), this.method_29076(l)));
	}

	protected abstract ChunkGenerator method_29076(long l);

	@Environment(EnvType.CLIENT)
	public interface class_5293 {
		Screen createEditScreen(CreateWorldScreen createWorldScreen, GeneratorOptions generatorOptions);
	}
}
