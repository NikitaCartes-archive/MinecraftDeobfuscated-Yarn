package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.CountConfig;

public abstract class Decorator<DC extends DecoratorConfig> {
	public static final Decorator<NopeDecoratorConfig> NOPE = register("nope", new NopeDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<ChanceDecoratorConfig> CHANCE = register("chance", new ChanceDecorator(ChanceDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> COUNT = register("count", new CountDecorator(CountConfig.CODEC));
	public static final Decorator<CountNoiseDecoratorConfig> COUNT_NOISE = register("count_noise", new CountNoiseDecorator(CountNoiseDecoratorConfig.CODEC));
	public static final Decorator<CountNoiseBiasedDecoratorConfig> COUNT_NOISE_BIASED = register(
		"count_noise_biased", new CountNoiseBiasedDecorator(CountNoiseBiasedDecoratorConfig.CODEC)
	);
	public static final Decorator<CountExtraDecoratorConfig> COUNT_EXTRA = register("count_extra", new CountExtraDecorator(CountExtraDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> SQUARE = register("square", new SquareDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> HEIGHTMAP = register("heightmap", new MotionBlockingHeightmapDecorator<>(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = register(
		"heightmap_spread_double", new HeightmapSpreadDoubleDecorator<>(NopeDecoratorConfig.CODEC)
	);
	public static final Decorator<NopeDecoratorConfig> TOP_SOLID_HEIGHTMAP = register(
		"top_solid_heightmap", new TopSolidHeightmapDecorator(NopeDecoratorConfig.CODEC)
	);
	public static final Decorator<NopeDecoratorConfig> HEIGHTMAP_WORLD_SURFACE = register(
		"heightmap_world_surface", new HeightmapWorldSurfaceDecorator(NopeDecoratorConfig.CODEC)
	);
	public static final Decorator<RangeDecoratorConfig> RANGE = register("range", new RangeDecorator(RangeDecoratorConfig.CODEC));
	public static final Decorator<RangeDecoratorConfig> RANGE_BIASED = register("range_biased", new RangeBiasedDecorator(RangeDecoratorConfig.CODEC));
	public static final Decorator<RangeDecoratorConfig> RANGE_VERY_BIASED = register("range_very_biased", new RangeVeryBiasedDecorator(RangeDecoratorConfig.CODEC));
	public static final Decorator<DepthAverageDecoratorConfig> DEPTH_AVERAGE = register(
		"depth_average", new DepthAverageDecorator(DepthAverageDecoratorConfig.CODEC)
	);
	public static final Decorator<NopeDecoratorConfig> SPREAD_32_ABOVE = register("spread_32_above", new Spread32AboveDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<CarvingMaskDecoratorConfig> CARVING_MASK = register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> FIRE = register("fire", new FireDecorator(CountConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> MAGMA = register("magma", new MagmaDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> EMERALD_ORE = register("emerald_ore", new EmeraldOreDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<ChanceDecoratorConfig> LAVA_LAKE = register("lava_lake", new LavaLakeDecorator(ChanceDecoratorConfig.CODEC));
	public static final Decorator<ChanceDecoratorConfig> WATER_LAKE = register("water_lake", new WaterLakeDecorator(ChanceDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> GLOWSTONE = register("glowstone", new GlowstoneDecorator(CountConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> END_GATEWAY = register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> ICEBERG = register("iceberg", new IcebergDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> END_ISLAND = register("end_island", new EndIslandDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<DecoratedDecoratorConfig> DECORATED = register("decorated", new DecoratedDecorator(DecoratedDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> COUNT_MULTILAYER = register("count_multilayer", new CountMultilayerDecorator(CountConfig.CODEC));
	private final Codec<ConfiguredDecorator<DC>> codec;

	private static <T extends DecoratorConfig, G extends Decorator<T>> G register(String registryName, G decorator) {
		return Registry.register(Registry.DECORATOR, registryName, decorator);
	}

	public Decorator(Codec<DC> configCodec) {
		this.codec = configCodec.fieldOf("config")
			.<ConfiguredDecorator<DC>>xmap(decoratorConfig -> new ConfiguredDecorator<>(this, (DC)decoratorConfig), ConfiguredDecorator::getConfig)
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
