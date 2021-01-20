/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorConfig
implements HeightLimitView {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.biomeRegistry), ((MapCodec)StructuresConfig.CODEC.fieldOf("structures")).forGetter(FlatChunkGeneratorConfig::getStructuresConfig), ((MapCodec)FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers")).forGetter(FlatChunkGeneratorConfig::getLayers), ((MapCodec)Codec.BOOL.fieldOf("lakes")).orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.hasLakes), ((MapCodec)Codec.BOOL.fieldOf("features")).orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.hasFeatures), Biome.REGISTRY_CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter(flatChunkGeneratorConfig -> Optional.of(flatChunkGeneratorConfig.biome))).apply((Applicative<FlatChunkGeneratorConfig, ?>)instance, FlatChunkGeneratorConfig::new)).comapFlatMap(FlatChunkGeneratorConfig::method_33067, Function.identity()).stable();
    private static final Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> STRUCTURE_TO_FEATURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(StructureFeature.MINESHAFT, ConfiguredStructureFeatures.MINESHAFT);
        hashMap.put(StructureFeature.VILLAGE, ConfiguredStructureFeatures.VILLAGE_PLAINS);
        hashMap.put(StructureFeature.STRONGHOLD, ConfiguredStructureFeatures.STRONGHOLD);
        hashMap.put(StructureFeature.SWAMP_HUT, ConfiguredStructureFeatures.SWAMP_HUT);
        hashMap.put(StructureFeature.DESERT_PYRAMID, ConfiguredStructureFeatures.DESERT_PYRAMID);
        hashMap.put(StructureFeature.JUNGLE_PYRAMID, ConfiguredStructureFeatures.JUNGLE_PYRAMID);
        hashMap.put(StructureFeature.IGLOO, ConfiguredStructureFeatures.IGLOO);
        hashMap.put(StructureFeature.OCEAN_RUIN, ConfiguredStructureFeatures.OCEAN_RUIN_COLD);
        hashMap.put(StructureFeature.SHIPWRECK, ConfiguredStructureFeatures.SHIPWRECK);
        hashMap.put(StructureFeature.MONUMENT, ConfiguredStructureFeatures.MONUMENT);
        hashMap.put(StructureFeature.END_CITY, ConfiguredStructureFeatures.END_CITY);
        hashMap.put(StructureFeature.MANSION, ConfiguredStructureFeatures.MANSION);
        hashMap.put(StructureFeature.FORTRESS, ConfiguredStructureFeatures.FORTRESS);
        hashMap.put(StructureFeature.PILLAGER_OUTPOST, ConfiguredStructureFeatures.PILLAGER_OUTPOST);
        hashMap.put(StructureFeature.RUINED_PORTAL, ConfiguredStructureFeatures.RUINED_PORTAL);
        hashMap.put(StructureFeature.BASTION_REMNANT, ConfiguredStructureFeatures.BASTION_REMNANT);
    });
    private final Registry<Biome> biomeRegistry;
    private final StructuresConfig structuresConfig;
    private final List<FlatChunkGeneratorLayer> layers = Lists.newArrayList();
    private Supplier<Biome> biome;
    private final BlockState[] layerBlocks;
    private boolean hasNoTerrain;
    private boolean hasFeatures;
    private boolean hasLakes;

    private static DataResult<FlatChunkGeneratorConfig> method_33067(FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
        int i = flatChunkGeneratorConfig.layers.stream().mapToInt(FlatChunkGeneratorLayer::getThickness).sum();
        if (i > DimensionType.field_28134) {
            return DataResult.error("Sum of layer heights is > " + DimensionType.field_28134, flatChunkGeneratorConfig);
        }
        return DataResult.success(flatChunkGeneratorConfig);
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
        this.layerBlocks = new BlockState[this.getSectionCount()];
    }

    @Environment(value=EnvType.CLIENT)
    public FlatChunkGeneratorConfig withStructuresConfig(StructuresConfig structuresConfig) {
        return this.method_29965(this.layers, structuresConfig);
    }

    @Environment(value=EnvType.CLIENT)
    public FlatChunkGeneratorConfig method_29965(List<FlatChunkGeneratorLayer> list, StructuresConfig structuresConfig) {
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig, this.biomeRegistry);
        for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : list) {
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
        boolean bl;
        Biome biome = this.getBiome();
        GenerationSettings generationSettings = biome.getGenerationSettings();
        GenerationSettings.Builder builder = new GenerationSettings.Builder().surfaceBuilder(generationSettings.getSurfaceBuilder());
        if (this.hasLakes) {
            builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_WATER);
            builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
        }
        for (Map.Entry<StructureFeature<?>, StructureConfig> entry : this.structuresConfig.getStructures().entrySet()) {
            builder.structureFeature(generationSettings.method_30978(STRUCTURE_TO_FEATURES.get(entry.getKey())));
        }
        boolean bl2 = bl = (!this.hasNoTerrain || this.biomeRegistry.getKey(biome).equals(Optional.of(BiomeKeys.THE_VOID))) && this.hasFeatures;
        if (bl) {
            List<List<Supplier<ConfiguredFeature<?, ?>>>> list = generationSettings.getFeatures();
            for (i = 0; i < list.size(); ++i) {
                if (i == GenerationStep.Feature.UNDERGROUND_STRUCTURES.ordinal() || i == GenerationStep.Feature.SURFACE_STRUCTURES.ordinal()) continue;
                List<Supplier<ConfiguredFeature<?, ?>>> list2 = list.get(i);
                for (Supplier<ConfiguredFeature<?, ?>> supplier : list2) {
                    builder.feature(i, supplier);
                }
            }
        }
        BlockState[] blockStates = this.getLayerBlocks();
        for (i = 0; i < blockStates.length; ++i) {
            BlockState blockState = blockStates[i];
            if (blockState == null || Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) continue;
            this.layerBlocks[i] = null;
            int j = this.getBottomSectionLimit() + i;
            builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(j, blockState)));
        }
        return new Biome.Builder().precipitation(biome.getPrecipitation()).category(biome.getCategory()).depth(biome.getDepth()).scale(biome.getScale()).temperature(biome.getTemperature()).downfall(biome.getDownfall()).effects(biome.getEffects()).generationSettings(builder.build()).spawnSettings(biome.getSpawnSettings()).build();
    }

    public StructuresConfig getStructuresConfig() {
        return this.structuresConfig;
    }

    public Biome getBiome() {
        return this.biome.get();
    }

    @Environment(value=EnvType.CLIENT)
    public void setBiome(Supplier<Biome> biome) {
        this.biome = biome;
    }

    public List<FlatChunkGeneratorLayer> getLayers() {
        return this.layers;
    }

    public BlockState[] getLayerBlocks() {
        return this.layerBlocks;
    }

    public void updateLayerBlocks() {
        Arrays.fill(this.layerBlocks, 0, this.layerBlocks.length, null);
        int i = this.getBottomSectionLimit();
        for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.layers) {
            flatChunkGeneratorLayer.setStartY(i);
            i += flatChunkGeneratorLayer.getThickness();
        }
        this.hasNoTerrain = true;
        for (FlatChunkGeneratorLayer flatChunkGeneratorLayer2 : this.layers) {
            for (int j = flatChunkGeneratorLayer2.getStartY(); j < flatChunkGeneratorLayer2.getStartY() + flatChunkGeneratorLayer2.getThickness(); ++j) {
                BlockState blockState = flatChunkGeneratorLayer2.getBlockState();
                if (blockState.isOf(Blocks.AIR)) continue;
                this.hasNoTerrain = false;
                this.layerBlocks[this.method_31926((int)j)] = blockState;
            }
        }
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

    public int method_31926(int i) {
        return i - this.getBottomSectionLimit();
    }

    @Override
    public int getBottomSectionLimit() {
        return 0;
    }

    @Override
    public int getSectionCount() {
        return 256;
    }
}

