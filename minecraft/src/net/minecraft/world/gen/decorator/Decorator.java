package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.CountConfig;

public abstract class Decorator<DC extends DecoratorConfig> {
	public static final Decorator<NopeDecoratorConfig> field_14250 = register("nope", new NopeDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<ChanceDecoratorConfig> field_25861 = register("chance", new ChanceDecorator(ChanceDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> field_25862 = register("count", new CountDecorator(CountConfig.CODEC));
	public static final Decorator<CountNoiseDecoratorConfig> field_25863 = register("count_noise", new CountNoiseDecorator(CountNoiseDecoratorConfig.CODEC));
	public static final Decorator<CountNoiseBiasedDecoratorConfig> field_25864 = register(
		"count_noise_biased", new CountNoiseBiasedDecorator(CountNoiseBiasedDecoratorConfig.CODEC)
	);
	public static final Decorator<CountExtraDecoratorConfig> field_25865 = register("count_extra", new CountExtraDecorator(CountExtraDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_25866 = register("square", new SquareDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_25867 = register("heightmap", new MotionBlockingHeightmapDecorator<>(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_25868 = register(
		"heightmap_spread_double", new HeightmapSpreadDoubleDecorator<>(NopeDecoratorConfig.CODEC)
	);
	public static final Decorator<NopeDecoratorConfig> field_14231 = register("top_solid_heightmap", new TopSolidHeightmapDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_25869 = register(
		"heightmap_world_surface", new HeightmapWorldSurfaceDecorator(NopeDecoratorConfig.CODEC)
	);
	public static final Decorator<RangeDecoratorConfig> field_25870 = register("range", new RangeDecorator(RangeDecoratorConfig.CODEC));
	public static final Decorator<RangeDecoratorConfig> field_25871 = register("range_biased", new RangeBiasedDecorator(RangeDecoratorConfig.CODEC));
	public static final Decorator<RangeDecoratorConfig> field_25872 = register("range_very_biased", new RangeVeryBiasedDecorator(RangeDecoratorConfig.CODEC));
	public static final Decorator<DepthAverageDecoratorConfig> field_25873 = register(
		"depth_average", new DepthAverageDecorator(DepthAverageDecoratorConfig.CODEC)
	);
	public static final Decorator<NopeDecoratorConfig> field_25874 = register("spread_32_above", new Spread32AboveDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<CarvingMaskDecoratorConfig> field_14229 = register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> field_14235 = register("fire", new FireDecorator(CountConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_14244 = register("magma", new MagmaDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_14268 = register("emerald_ore", new EmeraldOreDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<ChanceDecoratorConfig> field_14237 = register("lava_lake", new LavaLakeDecorator(ChanceDecoratorConfig.CODEC));
	public static final Decorator<ChanceDecoratorConfig> field_14242 = register("water_lake", new WaterLakeDecorator(ChanceDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> field_25875 = register("glowstone", new GlowstoneDecorator(CountConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_14230 = register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_14239 = register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_14243 = register("iceberg", new IcebergDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<NopeDecoratorConfig> field_14251 = register("end_island", new EndIslandDecorator(NopeDecoratorConfig.CODEC));
	public static final Decorator<DecoratedDecoratorConfig> field_25859 = register("decorated", new DecoratedDecorator(DecoratedDecoratorConfig.CODEC));
	public static final Decorator<CountConfig> field_25860 = register("count_multilayer", new CountMultilayerDecorator(CountConfig.CODEC));
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
