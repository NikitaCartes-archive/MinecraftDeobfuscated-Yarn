package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

public class FlatChunkGenerator extends ChunkGenerator {
	private final Biome biome;
	private final PhantomSpawner phantomSpawner = new PhantomSpawner();
	private final CatSpawner catSpawner = new CatSpawner();
	private final FlatChunkGeneratorConfig generatorConfig;

	public FlatChunkGenerator(FlatChunkGeneratorConfig config) {
		super(new FixedBiomeSource(config.getBiome()), config.getConfig());
		this.generatorConfig = config;
		this.biome = this.getBiome();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator create(long seed) {
		return this;
	}

	private Biome getBiome() {
		Biome biome = this.generatorConfig.getBiome();
		FlatChunkGenerator.FlatChunkGeneratorBiome flatChunkGeneratorBiome = new FlatChunkGenerator.FlatChunkGeneratorBiome(
			biome.getSurfaceBuilder(),
			biome.getPrecipitation(),
			biome.getCategory(),
			biome.getDepth(),
			biome.getScale(),
			biome.getTemperature(),
			biome.getRainfall(),
			biome.getEffects(),
			biome.getParent()
		);
		Map<String, Map<String, String>> map = this.generatorConfig.getStructures();

		for (String string : map.keySet()) {
			ConfiguredFeature<?, ?>[] configuredFeatures = (ConfiguredFeature<?, ?>[])FlatChunkGeneratorConfig.STRUCTURE_TO_FEATURES.get(string);
			if (configuredFeatures != null) {
				for (ConfiguredFeature<?, ?> configuredFeature : configuredFeatures) {
					flatChunkGeneratorBiome.addFeature((GenerationStep.Feature)FlatChunkGeneratorConfig.FEATURE_TO_GENERATION_STEP.get(configuredFeature), configuredFeature);
					if (configuredFeature.feature instanceof StructureFeature) {
						StructureFeature<FeatureConfig> structureFeature = (StructureFeature<FeatureConfig>)configuredFeature.feature;
						FeatureConfig featureConfig = biome.getStructureFeatureConfig(structureFeature);
						FeatureConfig featureConfig2 = featureConfig != null
							? featureConfig
							: (FeatureConfig)FlatChunkGeneratorConfig.FEATURE_TO_FEATURE_CONFIG.get(configuredFeature);
						flatChunkGeneratorBiome.addStructureFeature(structureFeature.configure(featureConfig2));
					}
				}
			}
		}

		boolean bl = (!this.generatorConfig.hasNoTerrain() || biome == Biomes.THE_VOID) && map.containsKey("decoration");
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

		BlockState[] blockStates = this.generatorConfig.getLayerBlocks();

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i];
			if (blockState != null && !Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				this.generatorConfig.removeLayerBlock(i);
				flatChunkGeneratorBiome.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(i, blockState)));
			}
		}

		return flatChunkGeneratorBiome;
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
	}

	@Override
	public int getSpawnHeight() {
		BlockState[] blockStates = this.generatorConfig.getLayerBlocks();

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i] == null ? Blocks.AIR.getDefaultState() : blockStates[i];
			if (!Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				return i - 1;
			}
		}

		return blockStates.length;
	}

	@Override
	protected Biome getDecorationBiome(BiomeAccess biomeAccess, BlockPos pos) {
		return this.biome;
	}

	@Override
	public boolean hasStructure(StructureFeature<?> feature) {
		return this.biome.hasStructureFeature(feature);
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
		BlockState[] blockStates = this.generatorConfig.getLayerBlocks();
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
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		BlockState[] blockStates = this.generatorConfig.getLayerBlocks();

		for (int i = blockStates.length - 1; i >= 0; i--) {
			BlockState blockState = blockStates[i];
			if (blockState != null && heightmapType.getBlockPredicate().test(blockState)) {
				return i + 1;
			}
		}

		return 0;
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		return new VerticalBlockSample(
			(BlockState[])Arrays.stream(this.generatorConfig.getLayerBlocks())
				.map(state -> state == null ? Blocks.AIR.getDefaultState() : state)
				.toArray(BlockState[]::new)
		);
	}

	@Override
	public void spawnEntities(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		this.phantomSpawner.spawn(world, spawnMonsters, spawnAnimals);
		this.catSpawner.spawn(world, spawnMonsters, spawnAnimals);
	}

	@Override
	public boolean hasStructure(Biome biome, StructureFeature<? extends FeatureConfig> feature) {
		return this.biome.hasStructureFeature(feature);
	}

	@Nullable
	@Override
	public <C extends FeatureConfig> C getStructureConfig(Biome biome, StructureFeature<C> feature) {
		return this.biome.getStructureFeatureConfig(feature);
	}

	@Nullable
	@Override
	public BlockPos locateStructure(ServerWorld world, String id, BlockPos center, int radius, boolean skipExistingChunks) {
		return !this.generatorConfig.getStructures().keySet().contains(id.toLowerCase(Locale.ROOT))
			? null
			: super.locateStructure(world, id, center, radius, skipExistingChunks);
	}

	class FlatChunkGeneratorBiome extends Biome {
		protected FlatChunkGeneratorBiome(
			ConfiguredSurfaceBuilder<?> surfaceBuilder,
			Biome.Precipitation precipitation,
			Biome.Category category,
			float depth,
			float scale,
			float temperature,
			float downfall,
			BiomeEffects effects,
			@Nullable String parent
		) {
			super(
				new Biome.Settings()
					.surfaceBuilder(surfaceBuilder)
					.precipitation(precipitation)
					.category(category)
					.depth(depth)
					.scale(scale)
					.temperature(temperature)
					.downfall(downfall)
					.effects(effects)
					.parent(parent)
			);
		}
	}
}
