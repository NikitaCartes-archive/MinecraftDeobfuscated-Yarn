package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class Decorator<DC extends DecoratorConfig> {
	public static final Decorator<NopeDecoratorConfig> NOPE = register("nope", new NopeDecorator(NopeDecoratorConfig.field_24891));
	public static final Decorator<CountDecoratorConfig> COUNT_HEIGHTMAP = register(
		"count_heightmap", new CountHeightmapDecorator(CountDecoratorConfig.field_24985)
	);
	public static final Decorator<CountDecoratorConfig> COUNT_TOP_SOLID = register("count_top_solid", new CountTopSolidDecorator(CountDecoratorConfig.field_24985));
	public static final Decorator<CountDecoratorConfig> COUNT_HEIGHTMAP_32 = register(
		"count_heightmap_32", new CountHeightmap32Decorator(CountDecoratorConfig.field_24985)
	);
	public static final Decorator<CountDecoratorConfig> COUNT_HEIGHTMAP_DOUBLE = register(
		"count_heightmap_double", new CountHeightmapDoubleDecorator(CountDecoratorConfig.field_24985)
	);
	public static final Decorator<CountDecoratorConfig> COUNT_HEIGHT_64 = register("count_height_64", new CountHeight64Decorator(CountDecoratorConfig.field_24985));
	public static final Decorator<NoiseHeightmapDecoratorConfig> NOISE_HEIGHTMAP_32 = register(
		"noise_heightmap_32", new NoiseHeightmap32Decorator(NoiseHeightmapDecoratorConfig.CODEC)
	);
	public static final Decorator<NoiseHeightmapDecoratorConfig> NOISE_HEIGHTMAP_DOUBLE = register(
		"noise_heightmap_double", new NoiseHeightmapDoubleDecorator(NoiseHeightmapDecoratorConfig.CODEC)
	);
	public static final Decorator<ChanceDecoratorConfig> CHANCE_HEIGHTMAP = register(
		"chance_heightmap", new ChanceHeightmapDecorator(ChanceDecoratorConfig.field_24980)
	);
	public static final Decorator<ChanceDecoratorConfig> CHANCE_HEIGHTMAP_DOUBLE = register(
		"chance_heightmap_double", new ChanceHeightmapDoubleDecorator(ChanceDecoratorConfig.field_24980)
	);
	public static final Decorator<ChanceDecoratorConfig> CHANCE_PASSTHROUGH = register(
		"chance_passthrough", new ChancePassthroughDecorator(ChanceDecoratorConfig.field_24980)
	);
	public static final Decorator<ChanceDecoratorConfig> CHANCE_TOP_SOLID_HEIGHTMAP = register(
		"chance_top_solid_heightmap", new ChanceTopSolidHeightmapDecorator(ChanceDecoratorConfig.field_24980)
	);
	public static final Decorator<CountExtraChanceDecoratorConfig> COUNT_EXTRA_HEIGHTMAP = register(
		"count_extra_heightmap", new CountExtraHeightmapDecorator(CountExtraChanceDecoratorConfig.CODEC)
	);
	public static final Decorator<RangeDecoratorConfig> COUNT_RANGE = register("count_range", new CountRangeDecorator(RangeDecoratorConfig.CODEC));
	public static final Decorator<RangeDecoratorConfig> COUNT_BIASED_RANGE = register(
		"count_biased_range", new CountBiasedRangeDecorator(RangeDecoratorConfig.CODEC)
	);
	public static final Decorator<RangeDecoratorConfig> COUNT_VERY_BIASED_RANGE = register(
		"count_very_biased_range", new CountVeryBiasedRangeDecorator(RangeDecoratorConfig.CODEC)
	);
	public static final Decorator<RangeDecoratorConfig> RANDOM_COUNT_RANGE = register(
		"random_count_range", new RandomCountRangeDecorator(RangeDecoratorConfig.CODEC)
	);
	public static final Decorator<ChanceRangeDecoratorConfig> CHANCE_RANGE = register("chance_range", new ChanceRangeDecorator(ChanceRangeDecoratorConfig.CODEC));
	public static final Decorator<CountChanceDecoratorConfig> COUNT_CHANCE_HEIGHTMAP = register(
		"count_chance_heightmap", new CountChanceHeightmapDecorator(CountChanceDecoratorConfig.CODEC)
	);
	public static final Decorator<CountChanceDecoratorConfig> COUNT_CHANCE_HEIGHTMAP_DOUBLE = register(
		"count_chance_heightmap_double", new CountChanceHeightmapDoubleDecorator(CountChanceDecoratorConfig.CODEC)
	);
	public static final Decorator<CountDepthDecoratorConfig> COUNT_DEPTH_AVERAGE = register(
		"count_depth_average", new CountDepthAverageDecorator(CountDepthDecoratorConfig.field_24982)
	);
	public static final Decorator<NopeDecoratorConfig> TOP_SOLID_HEIGHTMAP = register(
		"top_solid_heightmap", new HeightmapDecorator(NopeDecoratorConfig.field_24891)
	);
	public static final Decorator<HeightmapRangeDecoratorConfig> TOP_SOLID_HEIGHTMAP_RANGE = register(
		"top_solid_heightmap_range", new HeightmapRangeDecorator(HeightmapRangeDecoratorConfig.CODEC)
	);
	public static final Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> TOP_SOLID_HEIGHTMAP_NOISE_BIASED = register(
		"top_solid_heightmap_noise_biased", new HeightmapNoiseBiasedDecorator(TopSolidHeightmapNoiseBiasedDecoratorConfig.CODEC)
	);
	public static final Decorator<CarvingMaskDecoratorConfig> CARVING_MASK = register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig.CODEC));
	public static final Decorator<CountDecoratorConfig> FOREST_ROCK = register("forest_rock", new ForestRockDecorator(CountDecoratorConfig.field_24985));
	public static final Decorator<CountDecoratorConfig> FIRE = register("fire", new HellFireDecorator(CountDecoratorConfig.field_24985));
	public static final Decorator<CountDecoratorConfig> MAGMA = register("magma", new MagmaDecorator(CountDecoratorConfig.field_24985));
	public static final Decorator<NopeDecoratorConfig> EMERALD_ORE = register("emerald_ore", new EmeraldOreDecorator(NopeDecoratorConfig.field_24891));
	public static final Decorator<ChanceDecoratorConfig> LAVA_LAKE = register("lava_lake", new LavaLakeDecorator(ChanceDecoratorConfig.field_24980));
	public static final Decorator<ChanceDecoratorConfig> WATER_LAKE = register("water_lake", new WaterLakeDecorator(ChanceDecoratorConfig.field_24980));
	public static final Decorator<ChanceDecoratorConfig> DUNGEONS = register("dungeons", new DungeonsDecorator(ChanceDecoratorConfig.field_24980));
	public static final Decorator<NopeDecoratorConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig.field_24891));
	public static final Decorator<ChanceDecoratorConfig> ICEBERG = register("iceberg", new IcebergDecorator(ChanceDecoratorConfig.field_24980));
	public static final Decorator<CountDecoratorConfig> LIGHT_GEM_CHANCE = register(
		"light_gem_chance", new LightGemChanceDecorator(CountDecoratorConfig.field_24985)
	);
	public static final Decorator<NopeDecoratorConfig> END_ISLAND = register("end_island", new EndIslandDecorator(NopeDecoratorConfig.field_24891));
	public static final Decorator<NopeDecoratorConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantDecorator(NopeDecoratorConfig.field_24891));
	public static final Decorator<NopeDecoratorConfig> END_GATEWAY = register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig.field_24891));
	private final Codec<ConfiguredDecorator<DC>> codec;

	private static <T extends DecoratorConfig, G extends Decorator<T>> G register(String registryName, G decorator) {
		return Registry.register(Registry.DECORATOR, registryName, decorator);
	}

	public Decorator(Codec<DC> configCodec) {
		this.codec = configCodec.fieldOf("config")
			.<ConfiguredDecorator<DC>>xmap(decoratorConfig -> new ConfiguredDecorator<>(this, (DC)decoratorConfig), configuredDecorator -> configuredDecorator.config)
			.codec();
	}

	public ConfiguredDecorator<DC> configure(DC config) {
		return new ConfiguredDecorator<>(this, config);
	}

	public Codec<ConfiguredDecorator<DC>> getCodec() {
		return this.codec;
	}

	protected <FC extends FeatureConfig, F extends Feature<FC>> boolean generate(
		ServerWorldAccess serverWorldAccess,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		DC decoratorConfig,
		ConfiguredFeature<FC, F> configuredFeature
	) {
		MutableBoolean mutableBoolean = new MutableBoolean();
		this.getPositions(serverWorldAccess, chunkGenerator, random, decoratorConfig, blockPos).forEach(blockPosx -> {
			if (configuredFeature.generate(serverWorldAccess, chunkGenerator, random, blockPosx)) {
				mutableBoolean.setTrue();
			}
		});
		return mutableBoolean.isTrue();
	}

	public abstract Stream<BlockPos> getPositions(WorldAccess world, ChunkGenerator generator, Random random, DC config, BlockPos pos);

	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
	}
}
