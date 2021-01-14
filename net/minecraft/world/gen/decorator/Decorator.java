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
import net.minecraft.world.gen.decorator.DepthAverageDecorator;
import net.minecraft.world.gen.decorator.DepthAverageDecoratorConfig;
import net.minecraft.world.gen.decorator.EmeraldOreDecorator;
import net.minecraft.world.gen.decorator.EndGatewayDecorator;
import net.minecraft.world.gen.decorator.EndIslandDecorator;
import net.minecraft.world.gen.decorator.FireDecorator;
import net.minecraft.world.gen.decorator.GlowstoneDecorator;
import net.minecraft.world.gen.decorator.HeightmapWorldSurfaceDecorator;
import net.minecraft.world.gen.decorator.IcebergDecorator;
import net.minecraft.world.gen.decorator.LavaLakeDecorator;
import net.minecraft.world.gen.decorator.MagmaDecorator;
import net.minecraft.world.gen.decorator.MotionBlockingHeightmapDecorator;
import net.minecraft.world.gen.decorator.NopeDecorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeBiasedDecorator;
import net.minecraft.world.gen.decorator.RangeDecorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeVeryBiasedDecorator;
import net.minecraft.world.gen.decorator.Spread32AboveDecorator;
import net.minecraft.world.gen.decorator.SpreadDoubleHeightmapDecorator;
import net.minecraft.world.gen.decorator.SquareDecorator;
import net.minecraft.world.gen.decorator.TopSolidHeightmapDecorator;
import net.minecraft.world.gen.decorator.WaterLakeDecorator;

public abstract class Decorator<DC extends DecoratorConfig> {
    public static final Decorator<NopeDecoratorConfig> NOPE = Decorator.register("nope", new NopeDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<ChanceDecoratorConfig> CHANCE = Decorator.register("chance", new ChanceDecorator(ChanceDecoratorConfig.CODEC));
    public static final Decorator<CountConfig> COUNT = Decorator.register("count", new CountDecorator(CountConfig.CODEC));
    public static final Decorator<CountNoiseDecoratorConfig> COUNT_NOISE = Decorator.register("count_noise", new CountNoiseDecorator(CountNoiseDecoratorConfig.CODEC));
    public static final Decorator<CountNoiseBiasedDecoratorConfig> COUNT_NOISE_BIASED = Decorator.register("count_noise_biased", new CountNoiseBiasedDecorator(CountNoiseBiasedDecoratorConfig.CODEC));
    public static final Decorator<CountExtraDecoratorConfig> COUNT_EXTRA = Decorator.register("count_extra", new CountExtraDecorator(CountExtraDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> SQUARE = Decorator.register("square", new SquareDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> HEIGHTMAP = Decorator.register("heightmap", new MotionBlockingHeightmapDecorator<NopeDecoratorConfig>(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = Decorator.register("heightmap_spread_double", new SpreadDoubleHeightmapDecorator<NopeDecoratorConfig>(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> TOP_SOLID_HEIGHTMAP = Decorator.register("top_solid_heightmap", new TopSolidHeightmapDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> HEIGHTMAP_WORLD_SURFACE = Decorator.register("heightmap_world_surface", new HeightmapWorldSurfaceDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> RANGE = Decorator.register("range", new RangeDecorator(RangeDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> RANGE_BIASED = Decorator.register("range_biased", new RangeBiasedDecorator(RangeDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> RANGE_VERY_BIASED = Decorator.register("range_very_biased", new RangeVeryBiasedDecorator(RangeDecoratorConfig.CODEC));
    public static final Decorator<DepthAverageDecoratorConfig> DEPTH_AVERAGE = Decorator.register("depth_average", new DepthAverageDecorator(DepthAverageDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> SPREAD_32_ABOVE = Decorator.register("spread_32_above", new Spread32AboveDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<CarvingMaskDecoratorConfig> CARVING_MASK = Decorator.register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig.CODEC));
    public static final Decorator<CountConfig> FIRE = Decorator.register("fire", new FireDecorator(CountConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> MAGMA = Decorator.register("magma", new MagmaDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> EMERALD_ORE = Decorator.register("emerald_ore", new EmeraldOreDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<ChanceDecoratorConfig> LAVA_LAKE = Decorator.register("lava_lake", new LavaLakeDecorator(ChanceDecoratorConfig.CODEC));
    public static final Decorator<ChanceDecoratorConfig> WATER_LAKE = Decorator.register("water_lake", new WaterLakeDecorator(ChanceDecoratorConfig.CODEC));
    public static final Decorator<CountConfig> GLOWSTONE = Decorator.register("glowstone", new GlowstoneDecorator(CountConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> END_GATEWAY = Decorator.register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> DARK_OAK_TREE = Decorator.register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> ICEBERG = Decorator.register("iceberg", new IcebergDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> END_ISLAND = Decorator.register("end_island", new EndIslandDecorator(NopeDecoratorConfig.CODEC));
    public static final Decorator<DecoratedDecoratorConfig> DECORATED = Decorator.register("decorated", new DecoratedDecorator(DecoratedDecoratorConfig.CODEC));
    public static final Decorator<CountConfig> COUNT_MULTILAYER = Decorator.register("count_multilayer", new CountMultilayerDecorator(CountConfig.CODEC));
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

