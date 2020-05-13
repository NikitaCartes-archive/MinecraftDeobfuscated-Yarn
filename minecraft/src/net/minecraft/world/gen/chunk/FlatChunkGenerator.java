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
	private final FlatChunkGeneratorConfig field_24510;

	public FlatChunkGenerator(FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
		super(new FixedBiomeSource(flatChunkGeneratorConfig.getBiome()), flatChunkGeneratorConfig.method_28051());
		this.field_24510 = flatChunkGeneratorConfig;
		this.biome = this.getBiome();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator method_27997(long l) {
		return this;
	}

	private Biome getBiome() {
		Biome biome = this.field_24510.getBiome();
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
		Map<String, Map<String, String>> map = this.field_24510.getStructures();

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

		boolean bl = (!this.field_24510.hasNoTerrain() || biome == Biomes.THE_VOID) && map.containsKey("decoration");
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

		BlockState[] blockStates = this.field_24510.getLayerBlocks();

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i];
			if (blockState != null && !Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				this.field_24510.removeLayerBlock(i);
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
		BlockState[] blockStates = this.field_24510.getLayerBlocks();

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
	public boolean hasStructure(StructureFeature<?> structureFeature) {
		return this.biome.hasStructureFeature(structureFeature);
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor structureAccessor, Chunk chunk) {
		BlockState[] blockStates = this.field_24510.getLayerBlocks();
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
		BlockState[] blockStates = this.field_24510.getLayerBlocks();

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
			(BlockState[])Arrays.stream(this.field_24510.getLayerBlocks())
				.map(blockState -> blockState == null ? Blocks.AIR.getDefaultState() : blockState)
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
	public <C extends FeatureConfig> C getStructureConfig(Biome biome, StructureFeature<C> structureFeature) {
		return this.biome.getStructureFeatureConfig(structureFeature);
	}

	@Nullable
	@Override
	public BlockPos locateStructure(ServerWorld serverWorld, String id, BlockPos center, int radius, boolean skipExistingChunks) {
		return !this.field_24510.getStructures().keySet().contains(id.toLowerCase(Locale.ROOT))
			? null
			: super.locateStructure(serverWorld, id, center, radius, skipExistingChunks);
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
			BiomeEffects biomeEffects,
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
					.effects(biomeEffects)
					.parent(string)
			);
		}
	}
}
