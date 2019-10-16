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
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class Decorator<DC extends DecoratorConfig> {
	public static final Decorator<NopeDecoratorConfig> NOPE = register("nope", new NopeDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<CountDecoratorConfig> COUNT_HEIGHTMAP = register(
		"count_heightmap", new CountHeightmapDecorator(CountDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDecoratorConfig> COUNT_TOP_SOLID = register(
		"count_top_solid", new CountTopSolidDecorator(CountDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDecoratorConfig> COUNT_HEIGHTMAP_32 = register(
		"count_heightmap_32", new CountHeightmap32Decorator(CountDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDecoratorConfig> COUNT_HEIGHTMAP_DOUBLE = register(
		"count_heightmap_double", new CountHeightmapDoubleDecorator(CountDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDecoratorConfig> COUNT_HEIGHT_64 = register(
		"count_height_64", new CountHeight64Decorator(CountDecoratorConfig::deserialize)
	);
	public static final Decorator<NoiseHeightmapDecoratorConfig> NOISE_HEIGHTMAP_32 = register(
		"noise_heightmap_32", new NoiseHeightmap32Decorator(NoiseHeightmapDecoratorConfig::deserialize)
	);
	public static final Decorator<NoiseHeightmapDecoratorConfig> NOISE_HEIGHTMAP_DOUBLE = register(
		"noise_heightmap_double", new NoiseHeightmapDoubleDecorator(NoiseHeightmapDecoratorConfig::deserialize)
	);
	public static final Decorator<LakeDecoratorConfig> CHANCE_HEIGHTMAP = register(
		"chance_heightmap", new ChanceHeightmapDecorator(LakeDecoratorConfig::deserialize)
	);
	public static final Decorator<LakeDecoratorConfig> CHANCE_HEIGHTMAP_DOUBLE = register(
		"chance_heightmap_double", new ChanceHeightmapDoubleDecorator(LakeDecoratorConfig::deserialize)
	);
	public static final Decorator<LakeDecoratorConfig> CHANCE_PASSTHROUGH = register(
		"chance_passthrough", new ChancePassthroughDecorator(LakeDecoratorConfig::deserialize)
	);
	public static final Decorator<LakeDecoratorConfig> CHANCE_TOP_SOLID_HEIGHTMAP = register(
		"chance_top_solid_heightmap", new ChanceTopSolidHeightmapDecorator(LakeDecoratorConfig::deserialize)
	);
	public static final Decorator<CountExtraChanceDecoratorConfig> COUNT_EXTRA_HEIGHTMAP = register(
		"count_extra_heightmap", new CountExtraHeightmapDecorator(CountExtraChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<RangeDecoratorConfig> COUNT_RANGE = register("count_range", new CountRangeDecorator(RangeDecoratorConfig::deserialize));
	public static final Decorator<RangeDecoratorConfig> COUNT_BIASED_RANGE = register(
		"count_biased_range", new CountBiasedRangeDecorator(RangeDecoratorConfig::deserialize)
	);
	public static final Decorator<RangeDecoratorConfig> COUNT_VERY_BIASED_RANGE = register(
		"count_very_biased_range", new CountVeryBiasedRangeDecorator(RangeDecoratorConfig::deserialize)
	);
	public static final Decorator<RangeDecoratorConfig> RANDOM_COUNT_RANGE = register(
		"random_count_range", new RandomCountRangeDecorator(RangeDecoratorConfig::deserialize)
	);
	public static final Decorator<ChanceRangeDecoratorConfig> CHANCE_RANGE = register(
		"chance_range", new ChanceRangeDecorator(ChanceRangeDecoratorConfig::deserialize)
	);
	public static final Decorator<CountChanceDecoratorConfig> COUNT_CHANCE_HEIGHTMAP = register(
		"count_chance_heightmap", new CountChanceHeightmapDecorator(CountChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<CountChanceDecoratorConfig> COUNT_CHANCE_HEIGHTMAP_DOUBLE = register(
		"count_chance_heightmap_double", new CountChanceHeightmapDoubleDecorator(CountChanceDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDepthDecoratorConfig> COUNT_DEPTH_AVERAGE = register(
		"count_depth_average", new CountDepthAverageDecorator(CountDepthDecoratorConfig::deserialize)
	);
	public static final Decorator<NopeDecoratorConfig> TOP_SOLID_HEIGHTMAP = register(
		"top_solid_heightmap", new HeightmapDecorator(NopeDecoratorConfig::deserialize)
	);
	public static final Decorator<HeightmapRangeDecoratorConfig> TOP_SOLID_HEIGHTMAP_RANGE = register(
		"top_solid_heightmap_range", new HeightmapRangeDecorator(HeightmapRangeDecoratorConfig::deserialize)
	);
	public static final Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> TOP_SOLID_HEIGHTMAP_NOISE_BIASED = register(
		"top_solid_heightmap_noise_biased", new HeightmapNoiseBiasedDecorator(TopSolidHeightmapNoiseBiasedDecoratorConfig::deserialize)
	);
	public static final Decorator<CarvingMaskDecoratorConfig> CARVING_MASK = register(
		"carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig::deserialize)
	);
	public static final Decorator<CountDecoratorConfig> FOREST_ROCK = register("forest_rock", new ForestRockDecorator(CountDecoratorConfig::deserialize));
	public static final Decorator<CountDecoratorConfig> HELL_FIRE = register("hell_fire", new HellFireDecorator(CountDecoratorConfig::deserialize));
	public static final Decorator<CountDecoratorConfig> MAGMA = register("magma", new MagmaDecorator(CountDecoratorConfig::deserialize));
	public static final Decorator<NopeDecoratorConfig> EMERALD_ORE = register("emerald_ore", new EmeraldOreDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<LakeDecoratorConfig> LAVA_LAKE = register("lava_lake", new LakeLakeDecorator(LakeDecoratorConfig::deserialize));
	public static final Decorator<LakeDecoratorConfig> WATER_LAKE = register("water_lake", new WaterLakeDecorator(LakeDecoratorConfig::deserialize));
	public static final Decorator<LakeDecoratorConfig> DUNGEONS = register("dungeons", new DungeonsDecorator(LakeDecoratorConfig::deserialize));
	public static final Decorator<NopeDecoratorConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<LakeDecoratorConfig> ICEBERG = register("iceberg", new IcebergDecorator(LakeDecoratorConfig::deserialize));
	public static final Decorator<CountDecoratorConfig> LIGHT_GEM_CHANCE = register(
		"light_gem_chance", new LightGemChanceDecorator(CountDecoratorConfig::deserialize)
	);
	public static final Decorator<NopeDecoratorConfig> END_ISLAND = register("end_island", new EndIslandDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<NopeDecoratorConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantDecorator(NopeDecoratorConfig::deserialize));
	public static final Decorator<NopeDecoratorConfig> END_GATEWAY = register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig::deserialize));
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

	public ConfiguredDecorator<DC> configure(DC decoratorConfig) {
		return new ConfiguredDecorator<>(this, decoratorConfig);
	}

	protected <FC extends FeatureConfig, F extends Feature<FC>> boolean generate(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		DC decoratorConfig,
		ConfiguredFeature<FC, F> configuredFeature
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
