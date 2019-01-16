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
	private final PhantomSpawner phantomSpawner = new PhantomSpawner();

	public FlatChunkGenerator(IWorld iWorld, BiomeSource biomeSource, FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
		super(iWorld, biomeSource, flatChunkGeneratorConfig);
		this.biome = this.method_12589();
	}

	private Biome method_12589() {
		Biome biome = this.config.getBiome();
		FlatChunkGenerator.class_2898 lv = new FlatChunkGenerator.class_2898(
			biome.getSurfaceBuilder(),
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
		Map<String, Map<String, String>> map = this.config.getStructures();

		for (String string : map.keySet()) {
			ConfiguredFeature<?>[] configuredFeatures = (ConfiguredFeature<?>[])FlatChunkGeneratorConfig.field_14073.get(string);
			if (configuredFeatures != null) {
				for (ConfiguredFeature<?> configuredFeature : configuredFeatures) {
					lv.addFeature((GenerationStep.Feature)FlatChunkGeneratorConfig.field_14069.get(configuredFeature), configuredFeature);
					ConfiguredFeature<?> configuredFeature2 = ((DecoratedFeatureConfig)configuredFeature.config).feature;
					if (configuredFeature2.feature instanceof StructureFeature) {
						StructureFeature<FeatureConfig> structureFeature = (StructureFeature<FeatureConfig>)configuredFeature2.feature;
						FeatureConfig featureConfig = biome.getStructureFeatureConfig(structureFeature);
						lv.addStructureFeature(
							structureFeature, featureConfig != null ? featureConfig : (FeatureConfig)FlatChunkGeneratorConfig.field_14080.get(configuredFeature)
						);
					}
				}
			}
		}

		boolean bl = (!this.config.method_14320() || biome == Biomes.field_9473) && map.containsKey("decoration");
		if (bl) {
			List<GenerationStep.Feature> list = Lists.<GenerationStep.Feature>newArrayList();
			list.add(GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			list.add(GenerationStep.Feature.SURFACE_STRUCTURES);

			for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
				if (!list.contains(feature)) {
					for (ConfiguredFeature<?> configuredFeature2 : biome.getFeaturesForStep(feature)) {
						lv.addFeature(feature, configuredFeature2);
					}
				}
			}
		}

		return lv;
	}

	@Override
	public void buildSurface(Chunk chunk) {
	}

	@Override
	public int getSpawnHeight() {
		Chunk chunk = this.world.getChunk(0, 0);
		return chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, 8, 8);
	}

	@Override
	protected Biome getDecorationBiome(Chunk chunk) {
		return this.biome;
	}

	@Override
	protected Biome getDecorationBiome(ChunkRegion chunkRegion, int i, int j) {
		return this.biome;
	}

	@Override
	public void populateNoise(IWorld iWorld, Chunk chunk) {
		BlockState[] blockStates = this.config.getLayerBlocks();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap heightmap = ((ProtoChunk)chunk).getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = ((ProtoChunk)chunk).getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i];
			if (blockState != null) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						chunk.setBlockState(mutable.set(j, i, k), blockState, false);
						heightmap.trackUpdate(j, i, k, blockState);
						heightmap2.trackUpdate(j, i, k, blockState);
					}
				}
			}
		}
	}

	@Override
	public int produceHeight(int i, int j, Heightmap.Type type) {
		BlockState[] blockStates = this.config.getLayerBlocks();

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
		this.phantomSpawner.spawn(world, bl, bl2);
	}

	@Override
	public boolean hasStructure(Biome biome, StructureFeature<? extends FeatureConfig> structureFeature) {
		return this.biome.hasStructureFeature(structureFeature);
	}

	@Nullable
	@Override
	public <C extends FeatureConfig> C getStructureConfig(Biome biome, StructureFeature<C> structureFeature) {
		return this.biome.getStructureFeatureConfig(structureFeature);
	}

	@Nullable
	@Override
	public BlockPos locateStructure(World world, String string, BlockPos blockPos, int i, boolean bl) {
		return !this.config.getStructures().keySet().contains(string.toLowerCase(Locale.ROOT)) ? null : super.locateStructure(world, string, blockPos, i, bl);
	}

	class class_2898 extends Biome {
		protected class_2898(
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
					.surfaceBuilder(configuredSurfaceBuilder)
					.precipitation(precipitation)
					.category(category)
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
