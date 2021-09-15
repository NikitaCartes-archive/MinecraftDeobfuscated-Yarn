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
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.CarvingMaskDecorator;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.CaveSurfaceDecorator;
import net.minecraft.world.gen.decorator.CaveSurfaceDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecorator;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountDecorator;
import net.minecraft.world.gen.decorator.CountExtraDecorator;
import net.minecraft.world.gen.decorator.CountExtraDecoratorConfig;
import net.minecraft.world.gen.decorator.CountMultilayerDecorator;
import net.minecraft.world.gen.decorator.CountNoiseBiasedDecorator;
import net.minecraft.world.gen.decorator.CountNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.CountNoiseDecorator;
import net.minecraft.world.gen.decorator.CountNoiseDecoratorConfig;
import net.minecraft.world.gen.decorator.DarkOakTreeDecorator;
import net.minecraft.world.gen.decorator.DecoratedDecorator;
import net.minecraft.world.gen.decorator.DecoratedDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.EndGatewayDecorator;
import net.minecraft.world.gen.decorator.HeightmapDecorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.IcebergDecorator;
import net.minecraft.world.gen.decorator.LavaLakeDecorator;
import net.minecraft.world.gen.decorator.NopeDecorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.Spread32AboveDecorator;
import net.minecraft.world.gen.decorator.SpreadDoubleHeightmapDecorator;
import net.minecraft.world.gen.decorator.SquareDecorator;
import net.minecraft.world.gen.decorator.SurfaceRelativeThresholdDecorator;
import net.minecraft.world.gen.decorator.SurfaceRelativeThresholdDecoratorConfig;
import net.minecraft.world.gen.decorator.WaterDepthThresholdDecorator;
import net.minecraft.world.gen.decorator.WaterDepthThresholdDecoratorConfig;

public abstract class Decorator<DC extends DecoratorConfig> {
    public static final Decorator<NopeDecoratorConfig> NOPE = Decorator.register("nope", new NopeDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<DecoratedDecoratorConfig> DECORATED = Decorator.register("decorated", new DecoratedDecorator(DecoratedDecoratorConfig.CODEC));
    public static final Decorator<CarvingMaskDecoratorConfig> CARVING_MASK = Decorator.register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig.CODEC));
    public static final Decorator<CountConfig> COUNT_MULTILAYER = Decorator.register("count_multilayer", new CountMultilayerDecorator(CountConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> SQUARE = Decorator.register("square", new SquareDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> DARK_OAK_TREE = Decorator.register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> ICEBERG = Decorator.register("iceberg", new IcebergDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<ChanceDecoratorConfig> CHANCE = Decorator.register("chance", new ChanceDecorator(ChanceDecoratorConfig.CODEC));
    public static final Decorator<CountConfig> COUNT = Decorator.register("count", new CountDecorator(CountConfig.CODEC));
    public static final Decorator<CountNoiseDecoratorConfig> COUNT_NOISE = Decorator.register("count_noise", new CountNoiseDecorator(CountNoiseDecoratorConfig.CODEC));
    public static final Decorator<CountNoiseBiasedDecoratorConfig> COUNT_NOISE_BIASED = Decorator.register("count_noise_biased", new CountNoiseBiasedDecorator(CountNoiseBiasedDecoratorConfig.CODEC));
    public static final Decorator<CountExtraDecoratorConfig> COUNT_EXTRA = Decorator.register("count_extra", new CountExtraDecorator(CountExtraDecoratorConfig.CODEC));
    public static final Decorator<ChanceDecoratorConfig> LAVA_LAKE = Decorator.register("lava_lake", new LavaLakeDecorator(ChanceDecoratorConfig.CODEC));
    public static final Decorator<HeightmapDecoratorConfig> HEIGHTMAP = Decorator.register("heightmap", new HeightmapDecorator(HeightmapDecoratorConfig.CODEC));
    public static final Decorator<HeightmapDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = Decorator.register("heightmap_spread_double", new SpreadDoubleHeightmapDecorator(HeightmapDecoratorConfig.CODEC));
    public static final Decorator<SurfaceRelativeThresholdDecoratorConfig> SURFACE_RELATIVE_THRESHOLD = Decorator.register("surface_relative_threshold", new SurfaceRelativeThresholdDecorator(SurfaceRelativeThresholdDecoratorConfig.CODEC));
    public static final Decorator<WaterDepthThresholdDecoratorConfig> WATER_DEPTH_THRESHOLD = Decorator.register("water_depth_threshold", new WaterDepthThresholdDecorator(WaterDepthThresholdDecoratorConfig.CODEC));
    public static final Decorator<CaveSurfaceDecoratorConfig> CAVE_SURFACE = Decorator.register("cave_surface", new CaveSurfaceDecorator(CaveSurfaceDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> RANGE = Decorator.register("range", new RangeDecorator(RangeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> SPREAD_32_ABOVE = Decorator.register("spread_32_above", new Spread32AboveDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> END_GATEWAY = Decorator.register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig.CODEC));
    private final Codec<ConfiguredDecorator<DC>> codec;

    private static <T extends DecoratorConfig, G extends Decorator<T>> G register(String registryName, G decorator) {
        return (G)Registry.register(Registry.DECORATOR, registryName, decorator);
    }

    public Decorator(Codec<DC> configCodec) {
        this.codec = ((MapCodec)configCodec.fieldOf("config")).xmap(decoratorConfig -> new ConfiguredDecorator<DecoratorConfig>(this, (DecoratorConfig)decoratorConfig), ConfiguredDecorator::getConfig).codec();
    }

    public ConfiguredDecorator<DC> configure(DC config) {
        return new ConfiguredDecorator<DC>(this, config);
    }

    public Codec<ConfiguredDecorator<DC>> getCodec() {
        return this.codec;
    }

    public abstract Stream<BlockPos> getPositions(DecoratorContext var1, Random var2, DC var3, BlockPos var4);

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
}

