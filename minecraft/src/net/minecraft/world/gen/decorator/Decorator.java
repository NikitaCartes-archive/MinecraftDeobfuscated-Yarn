package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.CountConfig;

public abstract class Decorator<DC extends DecoratorConfig> {
	public static final Decorator<NopeDecoratorConfig> NOPE = register("nope", new NopeDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<DecoratedDecoratorConfig> DECORATED = register("decorated", new DecoratedDecorator(DecoratedDecoratorConfig.CODEC));
	public static final Decorator<CarvingMaskDecoratorConfig> CARVING_MASK = register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> COUNT_MULTILAYER = register("count_multilayer", new CountMultilayerDecorator(CountConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> SQUARE = register("square", new SquareDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> ICEBERG = register("iceberg", new IcebergDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<ChanceDecoratorConfig> CHANCE = register("chance", new ChanceDecorator(ChanceDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> COUNT = register("count", new CountDecorator(CountConfig.CODEC));
	public static final Decorator<CountNoiseDecoratorConfig> COUNT_NOISE = register("count_noise", new CountNoiseDecorator(CountNoiseDecoratorConfig.CODEC));
	public static final Decorator<CountNoiseBiasedDecoratorConfig> COUNT_NOISE_BIASED = register(
		"count_noise_biased", new CountNoiseBiasedDecorator(CountNoiseBiasedDecoratorConfig.CODEC)
	);
	public static final Decorator<CountExtraDecoratorConfig> COUNT_EXTRA = register("count_extra", new CountExtraDecorator(CountExtraDecoratorConfig.CODEC));
	public static final Decorator<ChanceDecoratorConfig> LAVA_LAKE = register("lava_lake", new LavaLakeDecorator(ChanceDecoratorConfig.CODEC));
	public static final Decorator<HeightmapDecoratorConfig> HEIGHTMAP = register("heightmap", new HeightmapDecorator(HeightmapDecoratorConfig.CODEC));
	public static final Decorator<HeightmapDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = register(
		"heightmap_spread_double", new SpreadDoubleHeightmapDecorator(HeightmapDecoratorConfig.CODEC)
	);
	public static final Decorator<SurfaceRelativeThresholdDecoratorConfig> SURFACE_RELATIVE_THRESHOLD = register(
		"surface_relative_threshold", new SurfaceRelativeThresholdDecorator(SurfaceRelativeThresholdDecoratorConfig.CODEC)
	);
	public static final Decorator<WaterDepthThresholdDecoratorConfig> WATER_DEPTH_THRESHOLD = register(
		"water_depth_threshold", new WaterDepthThresholdDecorator(WaterDepthThresholdDecoratorConfig.CODEC)
	);
	public static final Decorator<CaveSurfaceDecoratorConfig> CAVE_SURFACE = register("cave_surface", new CaveSurfaceDecorator(CaveSurfaceDecoratorConfig.CODEC));
	public static final Decorator<RangeDecoratorConfig> RANGE = register("range", new RangeDecorator(RangeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> SPREAD_32_ABOVE = register("spread_32_above", new Spread32AboveDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> END_GATEWAY = register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<BlockSurvivesFilterDecoratorConfig> BLOCK_SURVIVES_FILTER = register(
		"block_survives_filter", new BlockSurvivesFilterDecorator(BlockSurvivesFilterDecoratorConfig.CODEC)
	);
	public static final Decorator<BlockFilterDecoratorConfig> BLOCK_FILTER = register("block_filter", new BlockFilterDecorator(BlockFilterDecoratorConfig.CODEC));
	private final Codec<ConfiguredDecorator<DC>> codec;

	private static <T extends DecoratorConfig, G extends Decorator<T>> G register(String registryName, G decorator) {
		return Registry.register(Registry.DECORATOR, registryName, decorator);
	}

	public Decorator(Codec<DC> configCodec) {
		this.codec = configCodec.fieldOf("config")
			.<ConfiguredDecorator<DC>>xmap(config -> new ConfiguredDecorator<>(this, (DC)config), ConfiguredDecorator::getConfig)
			.codec();
	}

	public ConfiguredDecorator<DC> configure(DC config) {
		return new ConfiguredDecorator<>(this, config);
	}

	public Codec<ConfiguredDecorator<DC>> getCodec() {
		return this.codec;
	}

	public abstract Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos);

	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
	}
}
