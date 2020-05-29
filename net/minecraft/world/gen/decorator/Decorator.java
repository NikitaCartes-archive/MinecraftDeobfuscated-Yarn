/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.CarvingMaskDecorator;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceHeightmapDecorator;
import net.minecraft.world.gen.decorator.ChanceHeightmapDoubleDecorator;
import net.minecraft.world.gen.decorator.ChancePassthroughDecorator;
import net.minecraft.world.gen.decorator.ChanceRangeDecorator;
import net.minecraft.world.gen.decorator.ChanceRangeDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceTopSolidHeightmapDecorator;
import net.minecraft.world.gen.decorator.ChorusPlantDecorator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountBiasedRangeDecorator;
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountChanceHeightmapDecorator;
import net.minecraft.world.gen.decorator.CountChanceHeightmapDoubleDecorator;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDepthAverageDecorator;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraHeightmapDecorator;
import net.minecraft.world.gen.decorator.CountHeight64Decorator;
import net.minecraft.world.gen.decorator.CountHeightmap32Decorator;
import net.minecraft.world.gen.decorator.CountHeightmapDecorator;
import net.minecraft.world.gen.decorator.CountHeightmapDoubleDecorator;
import net.minecraft.world.gen.decorator.CountRangeDecorator;
import net.minecraft.world.gen.decorator.CountTopSolidDecorator;
import net.minecraft.world.gen.decorator.CountVeryBiasedRangeDecorator;
import net.minecraft.world.gen.decorator.DarkOakTreeDecorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DungeonsDecorator;
import net.minecraft.world.gen.decorator.EmeraldOreDecorator;
import net.minecraft.world.gen.decorator.EndGatewayDecorator;
import net.minecraft.world.gen.decorator.EndIslandDecorator;
import net.minecraft.world.gen.decorator.ForestRockDecorator;
import net.minecraft.world.gen.decorator.HeightmapDecorator;
import net.minecraft.world.gen.decorator.HeightmapNoiseBiasedDecorator;
import net.minecraft.world.gen.decorator.HeightmapRangeDecorator;
import net.minecraft.world.gen.decorator.HeightmapRangeDecoratorConfig;
import net.minecraft.world.gen.decorator.HellFireDecorator;
import net.minecraft.world.gen.decorator.IcebergDecorator;
import net.minecraft.world.gen.decorator.LavaLakeDecorator;
import net.minecraft.world.gen.decorator.LightGemChanceDecorator;
import net.minecraft.world.gen.decorator.MagmaDecorator;
import net.minecraft.world.gen.decorator.NoiseHeightmap32Decorator;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.NoiseHeightmapDoubleDecorator;
import net.minecraft.world.gen.decorator.NopeDecorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RandomCountRangeDecorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.WaterLakeDecorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class Decorator<DC extends DecoratorConfig> {
    public static final Decorator<NopeDecoratorConfig> NOPE = Decorator.register("nope", new NopeDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<CountDecoratorConfig> COUNT_HEIGHTMAP = Decorator.register("count_heightmap", new CountHeightmapDecorator(CountDecoratorConfig.field_24985));
    public static final Decorator<CountDecoratorConfig> COUNT_TOP_SOLID = Decorator.register("count_top_solid", new CountTopSolidDecorator(CountDecoratorConfig.field_24985));
    public static final Decorator<CountDecoratorConfig> COUNT_HEIGHTMAP_32 = Decorator.register("count_heightmap_32", new CountHeightmap32Decorator(CountDecoratorConfig.field_24985));
    public static final Decorator<CountDecoratorConfig> COUNT_HEIGHTMAP_DOUBLE = Decorator.register("count_heightmap_double", new CountHeightmapDoubleDecorator(CountDecoratorConfig.field_24985));
    public static final Decorator<CountDecoratorConfig> COUNT_HEIGHT_64 = Decorator.register("count_height_64", new CountHeight64Decorator(CountDecoratorConfig.field_24985));
    public static final Decorator<NoiseHeightmapDecoratorConfig> NOISE_HEIGHTMAP_32 = Decorator.register("noise_heightmap_32", new NoiseHeightmap32Decorator(NoiseHeightmapDecoratorConfig.CODEC));
    public static final Decorator<NoiseHeightmapDecoratorConfig> NOISE_HEIGHTMAP_DOUBLE = Decorator.register("noise_heightmap_double", new NoiseHeightmapDoubleDecorator(NoiseHeightmapDecoratorConfig.CODEC));
    public static final Decorator<ChanceDecoratorConfig> CHANCE_HEIGHTMAP = Decorator.register("chance_heightmap", new ChanceHeightmapDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<ChanceDecoratorConfig> CHANCE_HEIGHTMAP_DOUBLE = Decorator.register("chance_heightmap_double", new ChanceHeightmapDoubleDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<ChanceDecoratorConfig> CHANCE_PASSTHROUGH = Decorator.register("chance_passthrough", new ChancePassthroughDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<ChanceDecoratorConfig> CHANCE_TOP_SOLID_HEIGHTMAP = Decorator.register("chance_top_solid_heightmap", new ChanceTopSolidHeightmapDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<CountExtraChanceDecoratorConfig> COUNT_EXTRA_HEIGHTMAP = Decorator.register("count_extra_heightmap", new CountExtraHeightmapDecorator(CountExtraChanceDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> COUNT_RANGE = Decorator.register("count_range", new CountRangeDecorator(RangeDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> COUNT_BIASED_RANGE = Decorator.register("count_biased_range", new CountBiasedRangeDecorator(RangeDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> COUNT_VERY_BIASED_RANGE = Decorator.register("count_very_biased_range", new CountVeryBiasedRangeDecorator(RangeDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> RANDOM_COUNT_RANGE = Decorator.register("random_count_range", new RandomCountRangeDecorator(RangeDecoratorConfig.CODEC));
    public static final Decorator<ChanceRangeDecoratorConfig> CHANCE_RANGE = Decorator.register("chance_range", new ChanceRangeDecorator(ChanceRangeDecoratorConfig.CODEC));
    public static final Decorator<CountChanceDecoratorConfig> COUNT_CHANCE_HEIGHTMAP = Decorator.register("count_chance_heightmap", new CountChanceHeightmapDecorator(CountChanceDecoratorConfig.CODEC));
    public static final Decorator<CountChanceDecoratorConfig> COUNT_CHANCE_HEIGHTMAP_DOUBLE = Decorator.register("count_chance_heightmap_double", new CountChanceHeightmapDoubleDecorator(CountChanceDecoratorConfig.CODEC));
    public static final Decorator<CountDepthDecoratorConfig> COUNT_DEPTH_AVERAGE = Decorator.register("count_depth_average", new CountDepthAverageDecorator(CountDepthDecoratorConfig.field_24982));
    public static final Decorator<NopeDecoratorConfig> TOP_SOLID_HEIGHTMAP = Decorator.register("top_solid_heightmap", new HeightmapDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<HeightmapRangeDecoratorConfig> TOP_SOLID_HEIGHTMAP_RANGE = Decorator.register("top_solid_heightmap_range", new HeightmapRangeDecorator(HeightmapRangeDecoratorConfig.CODEC));
    public static final Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> TOP_SOLID_HEIGHTMAP_NOISE_BIASED = Decorator.register("top_solid_heightmap_noise_biased", new HeightmapNoiseBiasedDecorator(TopSolidHeightmapNoiseBiasedDecoratorConfig.CODEC));
    public static final Decorator<CarvingMaskDecoratorConfig> CARVING_MASK = Decorator.register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig.CODEC));
    public static final Decorator<CountDecoratorConfig> FOREST_ROCK = Decorator.register("forest_rock", new ForestRockDecorator(CountDecoratorConfig.field_24985));
    public static final Decorator<CountDecoratorConfig> FIRE = Decorator.register("fire", new HellFireDecorator(CountDecoratorConfig.field_24985));
    public static final Decorator<CountDecoratorConfig> MAGMA = Decorator.register("magma", new MagmaDecorator(CountDecoratorConfig.field_24985));
    public static final Decorator<NopeDecoratorConfig> EMERALD_ORE = Decorator.register("emerald_ore", new EmeraldOreDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<ChanceDecoratorConfig> LAVA_LAKE = Decorator.register("lava_lake", new LavaLakeDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<ChanceDecoratorConfig> WATER_LAKE = Decorator.register("water_lake", new WaterLakeDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<ChanceDecoratorConfig> DUNGEONS = Decorator.register("dungeons", new DungeonsDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<NopeDecoratorConfig> DARK_OAK_TREE = Decorator.register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<ChanceDecoratorConfig> ICEBERG = Decorator.register("iceberg", new IcebergDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<CountDecoratorConfig> LIGHT_GEM_CHANCE = Decorator.register("light_gem_chance", new LightGemChanceDecorator(CountDecoratorConfig.field_24985));
    public static final Decorator<NopeDecoratorConfig> END_ISLAND = Decorator.register("end_island", new EndIslandDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> CHORUS_PLANT = Decorator.register("chorus_plant", new ChorusPlantDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> END_GATEWAY = Decorator.register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig.field_24891));
    private final Codec<ConfiguredDecorator<DC>> field_24983;

    private static <T extends DecoratorConfig, G extends Decorator<T>> G register(String registryName, G decorator) {
        return (G)Registry.register(Registry.DECORATOR, registryName, decorator);
    }

    public Decorator(Codec<DC> codec) {
        this.field_24983 = ((MapCodec)codec.fieldOf("config")).xmap(decoratorConfig -> new ConfiguredDecorator<DecoratorConfig>(this, (DecoratorConfig)decoratorConfig), configuredDecorator -> configuredDecorator.config).codec();
    }

    public ConfiguredDecorator<DC> configure(DC decoratorConfig) {
        return new ConfiguredDecorator<DC>(this, decoratorConfig);
    }

    public Codec<ConfiguredDecorator<DC>> method_28928() {
        return this.field_24983;
    }

    protected <FC extends FeatureConfig, F extends Feature<FC>> boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos2, DC decoratorConfig, ConfiguredFeature<FC, F> configuredFeature) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        this.getPositions(serverWorldAccess, chunkGenerator, random, decoratorConfig, blockPos2).forEach(blockPos -> {
            if (configuredFeature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, (BlockPos)blockPos)) {
                mutableBoolean.setTrue();
            }
        });
        return mutableBoolean.isTrue();
    }

    public abstract Stream<BlockPos> getPositions(WorldAccess var1, ChunkGenerator var2, Random var3, DC var4, BlockPos var5);

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
}

