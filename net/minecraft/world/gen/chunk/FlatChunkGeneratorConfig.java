/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import org.slf4j.Logger;

public class FlatChunkGeneratorConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.biomeRegistry), ((MapCodec)StructuresConfig.CODEC.fieldOf("structures")).forGetter(FlatChunkGeneratorConfig::getStructuresConfig), ((MapCodec)FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers")).forGetter(FlatChunkGeneratorConfig::getLayers), ((MapCodec)Codec.BOOL.fieldOf("lakes")).orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.hasLakes), ((MapCodec)Codec.BOOL.fieldOf("features")).orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.hasFeatures), Biome.REGISTRY_CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter(flatChunkGeneratorConfig -> Optional.of(flatChunkGeneratorConfig.biome))).apply((Applicative<FlatChunkGeneratorConfig, ?>)instance, FlatChunkGeneratorConfig::new)).comapFlatMap(FlatChunkGeneratorConfig::checkHeight, Function.identity()).stable();
    private final Registry<Biome> biomeRegistry;
    private final StructuresConfig structuresConfig;
    private final List<FlatChunkGeneratorLayer> layers = Lists.newArrayList();
    private Supplier<Biome> biome;
    private final List<BlockState> layerBlocks;
    private boolean hasNoTerrain;
    private boolean hasFeatures;
    private boolean hasLakes;

    private static DataResult<FlatChunkGeneratorConfig> checkHeight(FlatChunkGeneratorConfig config) {
        int i = config.layers.stream().mapToInt(FlatChunkGeneratorLayer::getThickness).sum();
        if (i > DimensionType.MAX_HEIGHT) {
            return DataResult.error("Sum of layer heights is > " + DimensionType.MAX_HEIGHT, config);
        }
        return DataResult.success(config);
    }

    private FlatChunkGeneratorConfig(Registry<Biome> biomeRegistry, StructuresConfig structuresConfig, List<FlatChunkGeneratorLayer> layers, boolean hasLakes, boolean hasFeatures, Optional<Supplier<Biome>> biome) {
        this(structuresConfig, biomeRegistry);
        if (hasLakes) {
            this.enableLakes();
        }
        if (hasFeatures) {
            this.enableFeatures();
        }
        this.layers.addAll(layers);
        this.updateLayerBlocks();
        if (!biome.isPresent()) {
            LOGGER.error("Unknown biome, defaulting to plains");
            this.biome = () -> biomeRegistry.getOrThrow(BiomeKeys.PLAINS);
        } else {
            this.biome = biome.get();
        }
    }

    public FlatChunkGeneratorConfig(StructuresConfig structuresConfig, Registry<Biome> biomeRegistry) {
        this.biomeRegistry = biomeRegistry;
        this.structuresConfig = structuresConfig;
        this.biome = () -> biomeRegistry.getOrThrow(BiomeKeys.PLAINS);
        this.layerBlocks = Lists.newArrayList();
    }

    public FlatChunkGeneratorConfig withStructuresConfig(StructuresConfig structuresConfig) {
        return this.withLayers(this.layers, structuresConfig);
    }

    public FlatChunkGeneratorConfig withLayers(List<FlatChunkGeneratorLayer> layers, StructuresConfig structuresConfig) {
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig, this.biomeRegistry);
        for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : layers) {
            flatChunkGeneratorConfig.layers.add(new FlatChunkGeneratorLayer(flatChunkGeneratorLayer.getThickness(), flatChunkGeneratorLayer.getBlockState().getBlock()));
            flatChunkGeneratorConfig.updateLayerBlocks();
        }
        flatChunkGeneratorConfig.setBiome(this.biome);
        if (this.hasFeatures) {
            flatChunkGeneratorConfig.enableFeatures();
        }
        if (this.hasLakes) {
            flatChunkGeneratorConfig.enableLakes();
        }
        return flatChunkGeneratorConfig;
    }

    public void enableFeatures() {
        this.hasFeatures = true;
    }

    public void enableLakes() {
        this.hasLakes = true;
    }

    public Biome createBiome() {
        int i;
        List<Object> list;
        boolean bl;
        Biome biome = this.getBiome();
        GenerationSettings generationSettings = biome.getGenerationSettings();
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        if (this.hasLakes) {
            builder.feature(GenerationStep.Feature.LAKES, MiscPlacedFeatures.LAKE_LAVA_UNDERGROUND);
            builder.feature(GenerationStep.Feature.LAKES, MiscPlacedFeatures.LAKE_LAVA_SURFACE);
        }
        boolean bl2 = bl = (!this.hasNoTerrain || this.biomeRegistry.getKey(biome).equals(Optional.of(BiomeKeys.THE_VOID))) && this.hasFeatures;
        if (bl) {
            list = generationSettings.getFeatures();
            for (i = 0; i < list.size(); ++i) {
                if (i == GenerationStep.Feature.UNDERGROUND_STRUCTURES.ordinal() || i == GenerationStep.Feature.SURFACE_STRUCTURES.ordinal()) continue;
                List list2 = (List)list.get(i);
                for (Supplier supplier : list2) {
                    builder.feature(i, supplier);
                }
            }
        }
        list = this.getLayerBlocks();
        for (i = 0; i < list.size(); ++i) {
            BlockState blockState = (BlockState)list.get(i);
            if (Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) continue;
            list.set(i, null);
            builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(i, blockState)).withPlacement(new PlacementModifier[0]));
        }
        return new Biome.Builder().precipitation(biome.getPrecipitation()).category(biome.getCategory()).temperature(biome.getTemperature()).downfall(biome.getDownfall()).effects(biome.getEffects()).generationSettings(builder.build()).spawnSettings(biome.getSpawnSettings()).build();
    }

    public StructuresConfig getStructuresConfig() {
        return this.structuresConfig;
    }

    public Biome getBiome() {
        return this.biome.get();
    }

    public void setBiome(Supplier<Biome> biome) {
        this.biome = biome;
    }

    public List<FlatChunkGeneratorLayer> getLayers() {
        return this.layers;
    }

    public List<BlockState> getLayerBlocks() {
        return this.layerBlocks;
    }

    public void updateLayerBlocks() {
        this.layerBlocks.clear();
        for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.layers) {
            for (int i = 0; i < flatChunkGeneratorLayer.getThickness(); ++i) {
                this.layerBlocks.add(flatChunkGeneratorLayer.getBlockState());
            }
        }
        this.hasNoTerrain = this.layerBlocks.stream().allMatch(state -> state.isOf(Blocks.AIR));
    }

    public static FlatChunkGeneratorConfig getDefaultConfig(Registry<Biome> biomeRegistry) {
        StructuresConfig structuresConfig = new StructuresConfig(Optional.of(StructuresConfig.DEFAULT_STRONGHOLD), Maps.newHashMap(ImmutableMap.of(StructureFeature.VILLAGE, StructuresConfig.DEFAULT_STRUCTURES.get(StructureFeature.VILLAGE))));
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig, biomeRegistry);
        flatChunkGeneratorConfig.biome = () -> biomeRegistry.getOrThrow(BiomeKeys.PLAINS);
        flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
        flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
        flatChunkGeneratorConfig.updateLayerBlocks();
        return flatChunkGeneratorConfig;
    }
}

