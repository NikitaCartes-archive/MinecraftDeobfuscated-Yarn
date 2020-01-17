package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

public class FlatChunkGenerator extends ChunkGenerator<FlatChunkGeneratorConfig> {
	private final Biome biome;
	private final PhantomSpawner phantomSpawner = new PhantomSpawner();
	private final CatSpawner catSpawner = new CatSpawner();

	public FlatChunkGenerator(IWorld world, BiomeSource biomeSource, FlatChunkGeneratorConfig config) {
		super(world, biomeSource, config);
		this.biome = this.getBiome();
	}

	private Biome getBiome() {
		Biome biome = this.config.getBiome();
		FlatChunkGenerator.FlatChunkGeneratorBiome flatChunkGeneratorBiome = new FlatChunkGenerator.FlatChunkGeneratorBiome(
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
			ConfiguredFeature<?, ?>[] configuredFeatures = (ConfiguredFeature<?, ?>[])FlatChunkGeneratorConfig.STRUCTURE_TO_FEATURES.get(string);
			if (configuredFeatures != null) {
				for (ConfiguredFeature<?, ?> configuredFeature : configuredFeatures) {
					flatChunkGeneratorBiome.addFeature((GenerationStep.Feature)FlatChunkGeneratorConfig.FEATURE_TO_GENERATION_STEP.get(configuredFeature), configuredFeature);
					ConfiguredFeature<?, ?> configuredFeature2 = ((DecoratedFeatureConfig)configuredFeature.config).feature;
					if (configuredFeature2.feature instanceof StructureFeature) {
						StructureFeature<FeatureConfig> structureFeature = (StructureFeature<FeatureConfig>)configuredFeature2.feature;
						FeatureConfig featureConfig = biome.getStructureFeatureConfig(structureFeature);
						FeatureConfig featureConfig2 = featureConfig != null
							? featureConfig
							: (FeatureConfig)FlatChunkGeneratorConfig.FEATURE_TO_FEATURE_CONFIG.get(configuredFeature);
						flatChunkGeneratorBiome.addStructureFeature(structureFeature.configure(featureConfig2));
					}
				}
			}
		}

		boolean bl = (!this.config.hasNoTerrain() || biome == Biomes.THE_VOID) && map.containsKey("decoration");
		if (bl) {
			List<GenerationStep.Feature> list = Lists.<GenerationStep.Feature>newArrayList();
			list.add(GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			list.add(GenerationStep.Feature.SURFACE_STRUCTURES);

			for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
				if (!list.contains(feature)) {
					for (ConfiguredFeature<?, ?> configuredFeature2 : biome.getFeaturesForStep(feature)) {
						flatChunkGeneratorBiome.addFeature(feature, configuredFeature2);
					}
				}
			}
		}

		BlockState[] blockStates = this.config.getLayerBlocks();

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i];
			if (blockState != null && !Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				this.config.removeLayerBlock(i);
				flatChunkGeneratorBiome.addFeature(
					GenerationStep.Feature.TOP_LAYER_MODIFICATION,
					Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(i, blockState)).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
				);
			}
		}

		return flatChunkGeneratorBiome;
	}

	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk) {
	}

	@Override
	public int getSpawnHeight() {
		Chunk chunk = this.world.getChunk(0, 0);
		return chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, 8, 8);
	}

	@Override
	protected Biome getDecorationBiome(BiomeAccess biomeAccess, BlockPos pos) {
		return this.biome;
	}

	@Override
	public void populateNoise(IWorld world, Chunk chunk) {
		BlockState[] blockStates = this.config.getLayerBlocks();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

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
	public int getHeightOnGround(int x, int z, Heightmap.Type heightmapType) {
		BlockState[] blockStates = this.config.getLayerBlocks();

		for (int i = blockStates.length - 1; i >= 0; i--) {
			BlockState blockState = blockStates[i];
			if (blockState != null && heightmapType.getBlockPredicate().test(blockState)) {
				return i + 1;
			}
		}

		return 0;
	}

	@Override
	public void spawnEntities(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		this.phantomSpawner.spawn(world, spawnMonsters, spawnAnimals);
		this.catSpawner.spawn(world, spawnMonsters, spawnAnimals);
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
	public BlockPos locateStructure(World world, String id, BlockPos center, int radius, boolean skipExistingChunks) {
		return !this.config.getStructures().keySet().contains(id.toLowerCase(Locale.ROOT))
			? null
			: super.locateStructure(world, id, center, radius, skipExistingChunks);
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
