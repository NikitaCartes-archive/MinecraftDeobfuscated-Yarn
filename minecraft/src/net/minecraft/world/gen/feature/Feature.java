package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class Feature<FC extends FeatureConfig> {
	public static final Feature<DefaultFeatureConfig> NO_OP = register("no_op", new NoOpFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<TreeFeatureConfig> TREE = register("tree", new TreeFeature(TreeFeatureConfig.CODEC));
	public static final FlowerFeature<RandomPatchFeatureConfig> FLOWER = register("flower", new DefaultFlowerFeature(RandomPatchFeatureConfig.CODEC));
	public static final FlowerFeature<RandomPatchFeatureConfig> NO_BONEMEAL_FLOWER = register(
		"no_bonemeal_flower", new DefaultFlowerFeature(RandomPatchFeatureConfig.CODEC)
	);
	public static final Feature<RandomPatchFeatureConfig> RANDOM_PATCH = register("random_patch", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
	public static final Feature<BlockPileFeatureConfig> BLOCK_PILE = register("block_pile", new BlockPileFeature(BlockPileFeatureConfig.CODEC));
	public static final Feature<SpringFeatureConfig> SPRING_FEATURE = register("spring_feature", new SpringFeature(SpringFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<EmeraldOreFeatureConfig> EMERALD_ORE = register("emerald_ore", new EmeraldOreFeature(EmeraldOreFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> VOID_START_PLATFORM = register(
		"void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig.CODEC)
	);
	public static final Feature<DefaultFeatureConfig> DESERT_WELL = register("desert_well", new DesertWellFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> FOSSIL = register("fossil", new FossilFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<HugeMushroomFeatureConfig> HUGE_RED_MUSHROOM = register(
		"huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfig.CODEC)
	);
	public static final Feature<HugeMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = register(
		"huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfig.CODEC)
	);
	public static final Feature<DefaultFeatureConfig> ICE_SPIKE = register("ice_spike", new IceSpikeFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneBlobFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> FREEZE_TOP_LAYER = register("freeze_top_layer", new FreezeTopLayerFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> VINES = register("vines", new VinesFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> MONSTER_ROOM = register("monster_room", new DungeonFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> BLUE_ICE = register("blue_ice", new BlueIceFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<SingleStateFeatureConfig> ICEBERG = register("iceberg", new IcebergFeature(SingleStateFeatureConfig.CODEC));
	public static final Feature<SingleStateFeatureConfig> FOREST_ROCK = register("forest_rock", new ForestRockFeature(SingleStateFeatureConfig.CODEC));
	public static final Feature<DiskFeatureConfig> DISK = register("disk", new UnderwaterDiskFeature(DiskFeatureConfig.CODEC));
	public static final Feature<DiskFeatureConfig> ICE_PATCH = register("ice_patch", new IcePatchFeature(DiskFeatureConfig.CODEC));
	public static final Feature<SingleStateFeatureConfig> LAKE = register("lake", new LakeFeature(SingleStateFeatureConfig.CODEC));
	public static final Feature<OreFeatureConfig> ORE = register("ore", new OreFeature(OreFeatureConfig.CODEC));
	public static final Feature<EndSpikeFeatureConfig> END_SPIKE = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> END_ISLAND = register("end_island", new EndIslandFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<EndGatewayFeatureConfig> END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayFeatureConfig.CODEC));
	public static final SeagrassFeature SEAGRASS = register("seagrass", new SeagrassFeature(ProbabilityConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> KELP = register("kelp", new KelpFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> CORAL_TREE = register("coral_tree", new CoralTreeFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> CORAL_CLAW = register("coral_claw", new CoralClawFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<CountConfig> SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(CountConfig.CODEC));
	public static final Feature<SimpleBlockFeatureConfig> SIMPLE_BLOCK = register("simple_block", new SimpleBlockFeature(SimpleBlockFeatureConfig.CODEC));
	public static final Feature<ProbabilityConfig> BAMBOO = register("bamboo", new BambooFeature(ProbabilityConfig.CODEC));
	public static final Feature<HugeFungusFeatureConfig> HUGE_FUNGUS = register("huge_fungus", new HugeFungusFeature(HugeFungusFeatureConfig.CODEC));
	public static final Feature<BlockPileFeatureConfig> NETHER_FOREST_VEGETATION = register(
		"nether_forest_vegetation", new NetherForestVegetationFeature(BlockPileFeatureConfig.CODEC)
	);
	public static final Feature<DefaultFeatureConfig> WEEPING_VINES = register("weeping_vines", new WeepingVinesFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> TWISTING_VINES = register("twisting_vines", new TwistingVinesFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<BasaltColumnsFeatureConfig> BASALT_COLUMNS = register("basalt_columns", new BasaltColumnsFeature(BasaltColumnsFeatureConfig.CODEC));
	public static final Feature<DeltaFeatureConfig> DELTA_FEATURE = register("delta_feature", new DeltaFeature(DeltaFeatureConfig.CODEC));
	public static final Feature<ReplaceBlobsFeatureConfig> NETHERRACK_REPLACE_BLOBS = register(
		"netherrack_replace_blobs", new ReplaceBlobsFeature(ReplaceBlobsFeatureConfig.CODEC)
	);
	public static final Feature<FillLayerFeatureConfig> FILL_LAYER = register("fill_layer", new FillLayerFeature(FillLayerFeatureConfig.CODEC));
	public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> BASALT_PILLAR = register("basalt_pillar", new BasaltPillarFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<OreFeatureConfig> NO_SURFACE_ORE = register("no_surface_ore", new NoSurfaceOreFeature(OreFeatureConfig.CODEC));
	public static final Feature<RandomFeatureConfig> RANDOM_SELECTOR = register("random_selector", new RandomFeature(RandomFeatureConfig.CODEC));
	public static final Feature<SimpleRandomFeatureConfig> SIMPLE_RANDOM_SELECTOR = register(
		"simple_random_selector", new SimpleRandomFeature(SimpleRandomFeatureConfig.CODEC)
	);
	public static final Feature<RandomBooleanFeatureConfig> RANDOM_BOOLEAN_SELECTOR = register(
		"random_boolean_selector", new RandomBooleanFeature(RandomBooleanFeatureConfig.CODEC)
	);
	public static final Feature<DecoratedFeatureConfig> DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfig.CODEC));
	private final Codec<ConfiguredFeature<FC, Feature<FC>>> codec;

	private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
		return Registry.register(Registry.FEATURE, name, feature);
	}

	public Feature(Codec<FC> configCodec) {
		this.codec = configCodec.fieldOf("config")
			.<ConfiguredFeature<FC, Feature<FC>>>xmap(config -> new ConfiguredFeature<>(this, config), configuredFeature -> configuredFeature.config)
			.codec();
	}

	public Codec<ConfiguredFeature<FC, Feature<FC>>> getCodec() {
		return this.codec;
	}

	public ConfiguredFeature<FC, ?> configure(FC config) {
		return new ConfiguredFeature<>(this, config);
	}

	protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state, 3);
	}

	public abstract boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, FC config);

	protected static boolean isStone(Block block) {
		return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
	}

	public static boolean isSoil(Block block) {
		return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.MYCELIUM;
	}

	public static boolean isSoil(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> isSoil(state.getBlock()));
	}

	public static boolean isAir(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, AbstractBlock.AbstractBlockState::isAir);
	}
}
