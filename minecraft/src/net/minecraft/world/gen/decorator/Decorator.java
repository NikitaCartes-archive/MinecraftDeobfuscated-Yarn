package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class Decorator<DC extends DecoratorConfig> {
	public static final Decorator<CountDecoratorConfig> field_14238 = register("count_heightmap", new CountHeightmapDecorator(CountDecoratorConfig::deserialize));
	public static final Decorator<CountDecoratorConfig> field_14245 = register("count_top_solid", new CountTopSolidDecorator(CountDecoratorConfig::deserialize));
	public static final Decorator<CountDecoratorConfig> field_14253 = register(
		"count_heightmap_32", new CountHeightmap32Decorator(CountDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDecoratorConfig> field_14240 = register(
		"count_heightmap_double", new CountHeightmapDoubleDecorator(CountDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDecoratorConfig> field_14249 = register("count_height_64", new CountHeight64Decorator(CountDecoratorConfig::deserialize));
	public static final Decorator<NoiseHeightmapDecoratorConfig> field_14254 = register(
		"noise_heightmap_32", new NoiseHeightmap32Decorator(NoiseHeightmapDecoratorConfig::deserialize)
	);
	public static final Decorator<NoiseHeightmapDecoratorConfig> field_14236 = register(
		"noise_heightmap_double", new NoiseHeightmapDoubleDecorator(NoiseHeightmapDecoratorConfig::deserialize)
	);
	public static final Decorator<NopeDecoratorConfig> field_14250 = register("nope", new NopeDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<ChanceDecoratorConfig> field_14259 = register(
		"chance_heightmap", new ChanceHeightmapDecorator(ChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<ChanceDecoratorConfig> field_14263 = register(
		"chance_heightmap_double", new ChanceHeightmapDoubleDecorator(ChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<ChanceDecoratorConfig> field_14246 = register(
		"chance_passthrough", new ChancePassthroughDecorator(ChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<ChanceDecoratorConfig> field_14258 = register(
		"chance_top_solid_heightmap", new ChanceTopSolidHeightmapDecorator(ChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<CountExtraChanceDecoratorConfig> field_14267 = register(
		"count_extra_heightmap", new CountExtraHeightmapDecorator(CountExtraChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<RangeDecoratorConfig> field_14241 = register("count_range", new CountRangeDecorator(RangeDecoratorConfig::deserialize));
	public static final Decorator<RangeDecoratorConfig> field_14255 = register(
		"count_biased_range", new CountBiasedRangeDecorator(RangeDecoratorConfig::deserialize)
	);
	public static final Decorator<RangeDecoratorConfig> field_14266 = register(
		"count_very_biased_range", new CountVeryBiasedRangeDecorator(RangeDecoratorConfig::deserialize)
	);
	public static final Decorator<RangeDecoratorConfig> field_14260 = register(
		"random_count_range", new RandomCountRangeDecorator(RangeDecoratorConfig::deserialize)
	);
	public static final Decorator<ChanceRangeDecoratorConfig> field_14248 = register(
		"chance_range", new ChanceRangeDecorator(ChanceRangeDecoratorConfig::deserialize)
	);
	public static final Decorator<CountChanceDecoratorConfig> field_14234 = register(
		"count_chance_heightmap", new CountChanceHeightmapDecorator(CountChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<CountChanceDecoratorConfig> field_14261 = register(
		"count_chance_heightmap_double", new CountChanceHeightmapDoubleDecorator(CountChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDepthDecoratorConfig> field_14252 = register(
		"count_depth_average", new CountDepthAverageDecorator(CountDepthDecoratorConfig::deserialize)
	);
	public static final Decorator<NopeDecoratorConfig> field_14231 = register("top_solid_heightmap", new HeightmapDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<HeightmapRangeDecoratorConfig> field_14262 = register(
		"top_solid_heightmap_range", new HeightmapRangeDecorator(HeightmapRangeDecoratorConfig::deserialize)
	);
	public static final Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> field_14247 = register(
		"top_solid_heightmap_noise_biased", new HeightmapNoiseBiasedDecorator(TopSolidHeightmapNoiseBiasedDecoratorConfig::deserialize)
	);
	public static final Decorator<CarvingMaskDecoratorConfig> field_14229 = register(
		"carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDecoratorConfig> field_14264 = register("forest_rock", new ForestRockDecorator(CountDecoratorConfig::deserialize));
	public static final Decorator<CountDecoratorConfig> field_14235 = register("hell_fire", new HellFireDecorator(CountDecoratorConfig::deserialize));
	public static final Decorator<CountDecoratorConfig> field_14244 = register("magma", new MagmaDecorator(CountDecoratorConfig::deserialize));
	public static final Decorator<NopeDecoratorConfig> field_14268 = register("emerald_ore", new EmeraldOreDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<LakeDecoratorConfig> field_14237 = register("lava_lake", new LakeLakeDecorator(LakeDecoratorConfig::deserialize));
	public static final Decorator<LakeDecoratorConfig> field_14242 = register("water_lake", new WaterLakeDecorator(LakeDecoratorConfig::deserialize));
	public static final Decorator<DungeonDecoratorConfig> field_14265 = register("dungeons", new DungeonsDecorator(DungeonDecoratorConfig::deserialize));
	public static final Decorator<NopeDecoratorConfig> field_14239 = register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<ChanceDecoratorConfig> field_14243 = register("iceberg", new IcebergDecorator(ChanceDecoratorConfig::deserialize));
	public static final Decorator<CountDecoratorConfig> field_14256 = register("light_gem_chance", new LightGemChanceDecorator(CountDecoratorConfig::deserialize));
	public static final Decorator<NopeDecoratorConfig> field_14251 = register("end_island", new EndIslandDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<NopeDecoratorConfig> field_14257 = register("chorus_plant", new ChorusPlantDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<NopeDecoratorConfig> field_14230 = register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig::deserialize));
	private final Function<Dynamic<?>, ? extends DC> configDeserializer;

	private static <T extends DecoratorConfig, G extends Decorator<T>> G register(String string, G decorator) {
		return Registry.register(Registry.DECORATOR, string, decorator);
	}

	public Decorator(Function<Dynamic<?>, ? extends DC> function) {
		this.configDeserializer = function;
	}

	public DC deserialize(Dynamic<?> dynamic) {
		return (DC)this.configDeserializer.apply(dynamic);
	}

	protected <FC extends FeatureConfig> boolean generate(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		DC decoratorConfig,
		ConfiguredFeature<FC> configuredFeature
	) {
		AtomicBoolean atomicBoolean = new AtomicBoolean(false);
		this.getPositions(iWorld, chunkGenerator, random, decoratorConfig, blockPos).forEach(blockPosx -> {
			boolean bl = configuredFeature.generate(iWorld, chunkGenerator, random, blockPosx);
			atomicBoolean.set(atomicBoolean.get() || bl);
		});
		return atomicBoolean.get();
	}

	public abstract Stream<BlockPos> getPositions(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, DC decoratorConfig, BlockPos blockPos
	);

	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
	}
}
