package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public abstract class Feature<FC extends FeatureConfig> {
	public static final Feature<DefaultFeatureConfig> NO_OP = register("no_op", new NoOpFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<TreeFeatureConfig> TREE = register("tree", new TreeFeature(TreeFeatureConfig.CODEC));
	public static final Feature<RandomPatchFeatureConfig> FLOWER = register("flower", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
	public static final Feature<RandomPatchFeatureConfig> NO_BONEMEAL_FLOWER = register(
		"no_bonemeal_flower", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC)
	);
	public static final Feature<RandomPatchFeatureConfig> RANDOM_PATCH = register("random_patch", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
	public static final Feature<BlockPileFeatureConfig> BLOCK_PILE = register("block_pile", new BlockPileFeature(BlockPileFeatureConfig.CODEC));
	public static final Feature<SpringFeatureConfig> SPRING_FEATURE = register("spring_feature", new SpringFeature(SpringFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<EmeraldOreFeatureConfig> REPLACE_SINGLE_BLOCK = register(
		"replace_single_block", new EmeraldOreFeature(EmeraldOreFeatureConfig.CODEC)
	);
	public static final Feature<DefaultFeatureConfig> VOID_START_PLATFORM = register(
		"void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig.CODEC)
	);
	public static final Feature<DefaultFeatureConfig> DESERT_WELL = register("desert_well", new DesertWellFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<FossilFeatureConfig> FOSSIL = register("fossil", new FossilFeature(FossilFeatureConfig.CODEC));
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
	public static final Feature<BlockColumnFeatureConfig> BLOCK_COLUMN = register("block_column", new BlockColumnFeature(BlockColumnFeatureConfig.CODEC));
	public static final Feature<VegetationPatchFeatureConfig> VEGETATION_PATCH = register(
		"vegetation_patch", new VegetationPatchFeature(VegetationPatchFeatureConfig.CODEC)
	);
	public static final Feature<VegetationPatchFeatureConfig> WATERLOGGED_VEGETATION_PATCH = register(
		"waterlogged_vegetation_patch", new WaterloggedVegetationPatchFeature(VegetationPatchFeatureConfig.CODEC)
	);
	public static final Feature<RootSystemFeatureConfig> ROOT_SYSTEM = register("root_system", new RootSystemFeature(RootSystemFeatureConfig.CODEC));
	public static final Feature<MultifaceGrowthFeatureConfig> MULTIFACE_GROWTH = register(
		"multiface_growth", new MultifaceGrowthFeature(MultifaceGrowthFeatureConfig.CODEC)
	);
	public static final Feature<UnderwaterMagmaFeatureConfig> UNDERWATER_MAGMA = register(
		"underwater_magma", new UnderwaterMagmaFeature(UnderwaterMagmaFeatureConfig.CODEC)
	);
	public static final Feature<DefaultFeatureConfig> MONSTER_ROOM = register("monster_room", new DungeonFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> BLUE_ICE = register("blue_ice", new BlueIceFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<SingleStateFeatureConfig> ICEBERG = register("iceberg", new IcebergFeature(SingleStateFeatureConfig.CODEC));
	public static final Feature<SingleStateFeatureConfig> FOREST_ROCK = register("forest_rock", new ForestRockFeature(SingleStateFeatureConfig.CODEC));
	public static final Feature<DiskFeatureConfig> DISK = register("disk", new DiskFeature(DiskFeatureConfig.CODEC));
	public static final Feature<LakeFeature.Config> LAKE = register("lake", new LakeFeature(LakeFeature.Config.CODEC));
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
	public static final Feature<NetherForestVegetationFeatureConfig> NETHER_FOREST_VEGETATION = register(
		"nether_forest_vegetation", new NetherForestVegetationFeature(NetherForestVegetationFeatureConfig.VEGETATION_CODEC)
	);
	public static final Feature<DefaultFeatureConfig> WEEPING_VINES = register("weeping_vines", new WeepingVinesFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<TwistingVinesFeatureConfig> TWISTING_VINES = register("twisting_vines", new TwistingVinesFeature(TwistingVinesFeatureConfig.CODEC));
	public static final Feature<BasaltColumnsFeatureConfig> BASALT_COLUMNS = register("basalt_columns", new BasaltColumnsFeature(BasaltColumnsFeatureConfig.CODEC));
	public static final Feature<DeltaFeatureConfig> DELTA_FEATURE = register("delta_feature", new DeltaFeature(DeltaFeatureConfig.CODEC));
	public static final Feature<ReplaceBlobsFeatureConfig> NETHERRACK_REPLACE_BLOBS = register(
		"netherrack_replace_blobs", new ReplaceBlobsFeature(ReplaceBlobsFeatureConfig.CODEC)
	);
	public static final Feature<FillLayerFeatureConfig> FILL_LAYER = register("fill_layer", new FillLayerFeature(FillLayerFeatureConfig.CODEC));
	public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> BASALT_PILLAR = register("basalt_pillar", new BasaltPillarFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<OreFeatureConfig> SCATTERED_ORE = register("scattered_ore", new ScatteredOreFeature(OreFeatureConfig.CODEC));
	public static final Feature<RandomFeatureConfig> RANDOM_SELECTOR = register("random_selector", new RandomFeature(RandomFeatureConfig.CODEC));
	public static final Feature<SimpleRandomFeatureConfig> SIMPLE_RANDOM_SELECTOR = register(
		"simple_random_selector", new SimpleRandomFeature(SimpleRandomFeatureConfig.CODEC)
	);
	public static final Feature<RandomBooleanFeatureConfig> RANDOM_BOOLEAN_SELECTOR = register(
		"random_boolean_selector", new RandomBooleanFeature(RandomBooleanFeatureConfig.CODEC)
	);
	public static final Feature<GeodeFeatureConfig> GEODE = register("geode", new GeodeFeature(GeodeFeatureConfig.CODEC));
	public static final Feature<DripstoneClusterFeatureConfig> DRIPSTONE_CLUSTER = register(
		"dripstone_cluster", new DripstoneClusterFeature(DripstoneClusterFeatureConfig.CODEC)
	);
	public static final Feature<LargeDripstoneFeatureConfig> LARGE_DRIPSTONE = register(
		"large_dripstone", new LargeDripstoneFeature(LargeDripstoneFeatureConfig.CODEC)
	);
	public static final Feature<SmallDripstoneFeatureConfig> POINTED_DRIPSTONE = register(
		"pointed_dripstone", new SmallDripstoneFeature(SmallDripstoneFeatureConfig.CODEC)
	);
	public static final Feature<SculkPatchFeatureConfig> SCULK_PATCH = register("sculk_patch", new SculkPatchFeature(SculkPatchFeatureConfig.CODEC));
	private final Codec<ConfiguredFeature<FC, Feature<FC>>> codec;

	private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
		return Registry.register(Registries.FEATURE, name, feature);
	}

	public Feature(Codec<FC> configCodec) {
		this.codec = configCodec.fieldOf("config")
			.<ConfiguredFeature<FC, Feature<FC>>>xmap(config -> new ConfiguredFeature<>(this, config), ConfiguredFeature::config)
			.codec();
	}

	public Codec<ConfiguredFeature<FC, Feature<FC>>> getCodec() {
		return this.codec;
	}

	protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state, Block.NOTIFY_ALL);
	}

	public static Predicate<BlockState> notInBlockTagPredicate(TagKey<Block> tag) {
		return state -> !state.isIn(tag);
	}

	protected void setBlockStateIf(StructureWorldAccess world, BlockPos pos, BlockState state, Predicate<BlockState> predicate) {
		if (predicate.test(world.getBlockState(pos))) {
			world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
		}
	}

	public abstract boolean generate(FeatureContext<FC> context);

	public boolean generateIfValid(FC config, StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
		return world.isValidForSetBlock(pos) ? this.generate(new FeatureContext<>(Optional.empty(), world, chunkGenerator, random, pos, config)) : false;
	}

	protected static boolean isStone(BlockState state) {
		return state.isIn(BlockTags.BASE_STONE_OVERWORLD);
	}

	public static boolean isSoil(BlockState state) {
		return state.isIn(BlockTags.DIRT);
	}

	public static boolean isSoil(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, Feature::isSoil);
	}

	public static boolean testAdjacentStates(Function<BlockPos, BlockState> posToState, BlockPos pos, Predicate<BlockState> predicate) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : Direction.values()) {
			mutable.set(pos, direction);
			if (predicate.test((BlockState)posToState.apply(mutable))) {
				return true;
			}
		}

		return false;
	}

	public static boolean isExposedToAir(Function<BlockPos, BlockState> posToState, BlockPos pos) {
		return testAdjacentStates(posToState, pos, AbstractBlock.AbstractBlockState::isAir);
	}

	protected void markBlocksAboveForPostProcessing(StructureWorldAccess world, BlockPos pos) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = 0; i < 2; i++) {
			mutable.move(Direction.UP);
			if (world.getBlockState(mutable).isAir()) {
				return;
			}

			world.getChunk(mutable).markBlockForPostProcessing(mutable);
		}
	}
}
