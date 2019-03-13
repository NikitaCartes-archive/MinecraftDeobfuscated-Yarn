package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

public class FlatChunkGenerator extends ChunkGenerator<FlatChunkGeneratorConfig> {
	private final Biome biome;
	private final PhantomSpawner field_13184 = new PhantomSpawner();

	public FlatChunkGenerator(IWorld iWorld, BiomeSource biomeSource, FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
		super(iWorld, biomeSource, flatChunkGeneratorConfig);
		this.biome = this.getBiome();
	}

	private Biome getBiome() {
		Biome biome = this.field_16567.getBiome();
		FlatChunkGenerator.FlatChunkGeneratorBiome flatChunkGeneratorBiome = new FlatChunkGenerator.FlatChunkGeneratorBiome(
			biome.method_8692(),
			biome.getPrecipitation(),
			biome.getCategory(),
			biome.getDepth(),
			biome.getScale(),
			biome.getTemperature(),
			biome.getRainfall(),
			biome.getWaterColor(),
			biome.getWaterFogColor(),
			biome.getParent()
		);
		Map<String, Map<String, String>> map = this.field_16567.getStructures();

		for (String string : map.keySet()) {
			ConfiguredFeature<?>[] configuredFeatures = (ConfiguredFeature<?>[])FlatChunkGeneratorConfig.STRUCTURE_TO_FEATURES.get(string);
			if (configuredFeatures != null) {
				for (ConfiguredFeature<?> configuredFeature : configuredFeatures) {
					flatChunkGeneratorBiome.method_8719((GenerationStep.Feature)FlatChunkGeneratorConfig.FEATURE_TO_GENERATION_STEP.get(configuredFeature), configuredFeature);
					ConfiguredFeature<?> configuredFeature2 = ((DecoratedFeatureConfig)configuredFeature.field_13375).feature;
					if (configuredFeature2.field_13376 instanceof StructureFeature) {
						StructureFeature<FeatureConfig> structureFeature = (StructureFeature<FeatureConfig>)configuredFeature2.field_13376;
						FeatureConfig featureConfig = biome.method_8706(structureFeature);
						flatChunkGeneratorBiome.method_8710(
							structureFeature, featureConfig != null ? featureConfig : (FeatureConfig)FlatChunkGeneratorConfig.FEATURE_TO_FEATURE_CONFIG.get(configuredFeature)
						);
					}
				}
			}
		}

		boolean bl = (!this.field_16567.hasNoTerrain() || biome == Biomes.field_9473) && map.containsKey("decoration");
		if (bl) {
			List<GenerationStep.Feature> list = Lists.<GenerationStep.Feature>newArrayList();
			list.add(GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			list.add(GenerationStep.Feature.SURFACE_STRUCTURES);

			for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
				if (!list.contains(feature)) {
					for (ConfiguredFeature<?> configuredFeature2 : biome.method_8721(feature)) {
						flatChunkGeneratorBiome.method_8719(feature, configuredFeature2);
					}
				}
			}
		}

		return flatChunkGeneratorBiome;
	}

	@Override
	public void buildSurface(Chunk chunk) {
	}

	@Override
	public int getSpawnHeight() {
		Chunk chunk = this.world.method_8392(0, 0);
		return chunk.method_12005(Heightmap.Type.MOTION_BLOCKING, 8, 8);
	}

	@Override
	protected Biome getDecorationBiome(Chunk chunk) {
		return this.biome;
	}

	@Override
	protected Biome method_16554(ChunkRegion chunkRegion, int i, int j) {
		return this.biome;
	}

	@Override
	public void populateNoise(IWorld iWorld, Chunk chunk) {
		BlockState[] blockStates = this.field_16567.getLayerBlocks();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap heightmap = ((ProtoChunk)chunk).method_12032(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = ((ProtoChunk)chunk).method_12032(Heightmap.Type.WORLD_SURFACE_WG);

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i];
			if (blockState != null) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						chunk.method_12010(mutable.set(j, i, k), blockState, false);
						heightmap.trackUpdate(j, i, k, blockState);
						heightmap2.trackUpdate(j, i, k, blockState);
					}
				}
			}
		}
	}

	@Override
	public int method_16397(int i, int j, Heightmap.Type type) {
		BlockState[] blockStates = this.field_16567.getLayerBlocks();

		for (int k = blockStates.length - 1; k >= 0; k--) {
			BlockState blockState = blockStates[k];
			if (blockState != null && type.getBlockPredicate().test(blockState)) {
				return k + 1;
			}
		}

		return 0;
	}

	@Override
	public void spawnEntities(World world, boolean bl, boolean bl2) {
		this.field_13184.spawn(world, bl, bl2);
	}

	@Override
	public boolean method_12097(Biome biome, StructureFeature<? extends FeatureConfig> structureFeature) {
		return this.biome.method_8684(structureFeature);
	}

	@Nullable
	@Override
	public <C extends FeatureConfig> C method_12105(Biome biome, StructureFeature<C> structureFeature) {
		return this.biome.method_8706(structureFeature);
	}

	@Nullable
	@Override
	public BlockPos method_12103(World world, String string, BlockPos blockPos, int i, boolean bl) {
		return !this.field_16567.getStructures().keySet().contains(string.toLowerCase(Locale.ROOT)) ? null : super.method_12103(world, string, blockPos, i, bl);
	}

	class FlatChunkGeneratorBiome extends Biome {
		protected FlatChunkGeneratorBiome(
			ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder,
			Biome.Precipitation precipitation,
			Biome.Category category,
			float f,
			float g,
			float h,
			float i,
			int j,
			int k,
			@Nullable String string
		) {
			super(
				new Biome.Settings()
					.method_8731(configuredSurfaceBuilder)
					.precipitation(precipitation)
					.method_8738(category)
					.depth(f)
					.scale(g)
					.temperature(h)
					.downfall(i)
					.waterColor(j)
					.waterFogColor(k)
					.parent(string)
			);
		}
	}
}
