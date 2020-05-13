/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
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
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import org.jetbrains.annotations.Nullable;

public class FlatChunkGenerator
extends ChunkGenerator {
    private final Biome biome;
    private final PhantomSpawner phantomSpawner = new PhantomSpawner();
    private final CatSpawner catSpawner = new CatSpawner();
    private final FlatChunkGeneratorConfig field_24510;

    public FlatChunkGenerator(FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
        super(new FixedBiomeSource(flatChunkGeneratorConfig.getBiome()), flatChunkGeneratorConfig.method_28051());
        this.field_24510 = flatChunkGeneratorConfig;
        this.biome = this.getBiome();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ChunkGenerator method_27997(long l) {
        return this;
    }

    /*
     * WARNING - void declaration
     */
    private Biome getBiome() {
        void var6_11;
        boolean bl;
        Biome biome = this.field_24510.getBiome();
        FlatChunkGeneratorBiome flatChunkGeneratorBiome = new FlatChunkGeneratorBiome(biome.getSurfaceBuilder(), biome.getPrecipitation(), biome.getCategory(), biome.getDepth(), biome.getScale(), biome.getTemperature(), biome.getRainfall(), biome.getEffects(), biome.getParent());
        Map<String, Map<String, String>> map = this.field_24510.getStructures();
        for (String string : map.keySet()) {
            ConfiguredFeature<?, ?>[] configuredFeatureArray = FlatChunkGeneratorConfig.STRUCTURE_TO_FEATURES.get(string);
            if (configuredFeatureArray == null) continue;
            ConfiguredFeature<?, ?>[] configuredFeatureArray2 = configuredFeatureArray;
            int n = configuredFeatureArray2.length;
            for (int i = 0; i < n; ++i) {
                ConfiguredFeature<?, ?> configuredFeature = configuredFeatureArray2[i];
                flatChunkGeneratorBiome.addFeature(FlatChunkGeneratorConfig.FEATURE_TO_GENERATION_STEP.get(configuredFeature), configuredFeature);
                if (!(configuredFeature.feature instanceof StructureFeature)) continue;
                StructureFeature structureFeature = (StructureFeature)configuredFeature.feature;
                Object featureConfig = biome.getStructureFeatureConfig(structureFeature);
                Object featureConfig2 = featureConfig != null ? featureConfig : FlatChunkGeneratorConfig.FEATURE_TO_FEATURE_CONFIG.get(configuredFeature);
                flatChunkGeneratorBiome.addStructureFeature(structureFeature.configure(featureConfig2));
            }
        }
        boolean bl2 = bl = (!this.field_24510.hasNoTerrain() || biome == Biomes.THE_VOID) && map.containsKey("decoration");
        if (bl) {
            ArrayList<GenerationStep.Feature> list = Lists.newArrayList();
            list.add(GenerationStep.Feature.UNDERGROUND_STRUCTURES);
            list.add(GenerationStep.Feature.SURFACE_STRUCTURES);
            for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
                if (list.contains((Object)feature)) continue;
                for (ConfiguredFeature<?, ?> configuredFeature2 : biome.getFeaturesForStep(feature)) {
                    flatChunkGeneratorBiome.addFeature(feature, configuredFeature2);
                }
            }
        }
        BlockState[] blockStates = this.field_24510.getLayerBlocks();
        boolean bl3 = false;
        while (var6_11 < blockStates.length) {
            BlockState blockState = blockStates[var6_11];
            if (blockState != null && !Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
                this.field_24510.removeLayerBlock((int)var6_11);
                flatChunkGeneratorBiome.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig((int)var6_11, blockState)));
            }
            ++var6_11;
        }
        return flatChunkGeneratorBiome;
    }

    @Override
    public void buildSurface(ChunkRegion region, Chunk chunk) {
    }

    @Override
    public int getSpawnHeight() {
        BlockState[] blockStates = this.field_24510.getLayerBlocks();
        for (int i = 0; i < blockStates.length; ++i) {
            BlockState blockState;
            BlockState blockState2 = blockState = blockStates[i] == null ? Blocks.AIR.getDefaultState() : blockStates[i];
            if (Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) continue;
            return i - 1;
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
        for (int i = 0; i < blockStates.length; ++i) {
            BlockState blockState = blockStates[i];
            if (blockState == null) continue;
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    chunk.setBlockState(mutable.set(j, i, k), blockState, false);
                    heightmap.trackUpdate(j, i, k, blockState);
                    heightmap2.trackUpdate(j, i, k, blockState);
                }
            }
        }
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        BlockState[] blockStates = this.field_24510.getLayerBlocks();
        for (int i = blockStates.length - 1; i >= 0; --i) {
            BlockState blockState = blockStates[i];
            if (blockState == null || !heightmapType.getBlockPredicate().test(blockState)) continue;
            return i + 1;
        }
        return 0;
    }

    @Override
    public BlockView getColumnSample(int x, int z) {
        return new VerticalBlockSample((BlockState[])Arrays.stream(this.field_24510.getLayerBlocks()).map(blockState -> blockState == null ? Blocks.AIR.getDefaultState() : blockState).toArray(BlockState[]::new));
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

    @Override
    @Nullable
    public <C extends FeatureConfig> C getStructureConfig(Biome biome, StructureFeature<C> structureFeature) {
        return this.biome.getStructureFeatureConfig(structureFeature);
    }

    @Override
    @Nullable
    public BlockPos locateStructure(ServerWorld serverWorld, String id, BlockPos center, int radius, boolean skipExistingChunks) {
        if (!this.field_24510.getStructures().keySet().contains(id.toLowerCase(Locale.ROOT))) {
            return null;
        }
        return super.locateStructure(serverWorld, id, center, radius, skipExistingChunks);
    }

    class FlatChunkGeneratorBiome
    extends Biome {
        protected FlatChunkGeneratorBiome(ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder, Biome.Precipitation precipitation, Biome.Category category, float f, float g, float h, float i, @Nullable BiomeEffects biomeEffects, String string) {
            super(new Biome.Settings().surfaceBuilder(configuredSurfaceBuilder).precipitation(precipitation).category(category).depth(f).scale(g).temperature(h).downfall(i).effects(biomeEffects).parent(string));
        }
    }
}

