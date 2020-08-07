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
	public static final Feature<DefaultFeatureConfig> field_21590 = register("no_op", new NoOpFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<TreeFeatureConfig> field_24134 = register("tree", new TreeFeature(TreeFeatureConfig.CODEC));
	public static final FlowerFeature<RandomPatchFeatureConfig> FLOWER = register("flower", new DefaultFlowerFeature(RandomPatchFeatureConfig.CODEC));
	public static final FlowerFeature<RandomPatchFeatureConfig> field_26361 = register(
		"no_bonemeal_flower", new DefaultFlowerFeature(RandomPatchFeatureConfig.CODEC)
	);
	public static final Feature<RandomPatchFeatureConfig> field_21220 = register("random_patch", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
	public static final Feature<BlockPileFeatureConfig> field_21221 = register("block_pile", new AbstractPileFeature(BlockPileFeatureConfig.CODEC));
	public static final Feature<SpringFeatureConfig> field_13513 = register("spring_feature", new SpringFeature(SpringFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13552 = register("chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<EmeraldOreFeatureConfig> field_13594 = register("emerald_ore", new EmeraldOreFeature(EmeraldOreFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13591 = register("void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13592 = register("desert_well", new DesertWellFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13516 = register("fossil", new FossilFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<HugeMushroomFeatureConfig> field_13571 = register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfig.CODEC));
	public static final Feature<HugeMushroomFeatureConfig> field_13531 = register(
		"huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfig.CODEC)
	);
	public static final Feature<DefaultFeatureConfig> field_13562 = register("ice_spike", new IceSpikeFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13568 = register("glowstone_blob", new GlowstoneBlobFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13539 = register("freeze_top_layer", new FreezeTopLayerFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13559 = register("vines", new VinesFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13579 = register("monster_room", new DungeonFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13560 = register("blue_ice", new BlueIceFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<SingleStateFeatureConfig> field_13544 = register("iceberg", new IcebergFeature(SingleStateFeatureConfig.CODEC));
	public static final Feature<SingleStateFeatureConfig> field_13584 = register("forest_rock", new ForestRockFeature(SingleStateFeatureConfig.CODEC));
	public static final Feature<DiskFeatureConfig> field_13509 = register("disk", new UnderwaterDiskFeature(DiskFeatureConfig.CODEC));
	public static final Feature<DiskFeatureConfig> field_13551 = register("ice_patch", new IcePatchFeature(DiskFeatureConfig.CODEC));
	public static final Feature<SingleStateFeatureConfig> field_13573 = register("lake", new LakeFeature(SingleStateFeatureConfig.CODEC));
	public static final Feature<OreFeatureConfig> field_13517 = register("ore", new OreFeature(OreFeatureConfig.CODEC));
	public static final Feature<EndSpikeFeatureConfig> field_13522 = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13574 = register("end_island", new EndIslandFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<EndGatewayFeatureConfig> field_13564 = register("end_gateway", new EndGatewayFeature(EndGatewayFeatureConfig.CODEC));
	public static final SeagrassFeature SEAGRASS = register("seagrass", new SeagrassFeature(ProbabilityConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13535 = register("kelp", new KelpFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13525 = register("coral_tree", new CoralTreeFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13585 = register("coral_mushroom", new CoralMushroomFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_13546 = register("coral_claw", new CoralClawFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<CountConfig> field_13575 = register("sea_pickle", new SeaPickleFeature(CountConfig.CODEC));
	public static final Feature<SimpleBlockFeatureConfig> field_13518 = register("simple_block", new SimpleBlockFeature(SimpleBlockFeatureConfig.CODEC));
	public static final Feature<ProbabilityConfig> field_13540 = register("bamboo", new BambooFeature(ProbabilityConfig.CODEC));
	public static final Feature<HugeFungusFeatureConfig> field_22185 = register("huge_fungus", new HugeFungusFeature(HugeFungusFeatureConfig.CODEC));
	public static final Feature<BlockPileFeatureConfig> field_22186 = register(
		"nether_forest_vegetation", new NetherForestVegetationFeature(BlockPileFeatureConfig.CODEC)
	);
	public static final Feature<DefaultFeatureConfig> field_22187 = register("weeping_vines", new WeepingVinesFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_23088 = register("twisting_vines", new TwistingVinesFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<BasaltColumnsFeatureConfig> field_23884 = register("basalt_columns", new BasaltColumnsFeature(BasaltColumnsFeatureConfig.CODEC));
	public static final Feature<DeltaFeatureConfig> field_23885 = register("delta_feature", new DeltaFeature(DeltaFeatureConfig.CODEC));
	public static final Feature<NetherrackReplaceBlobsFeatureConfig> field_23886 = register(
		"netherrack_replace_blobs", new NetherrackReplaceBlobsFeature(NetherrackReplaceBlobsFeatureConfig.field_25848)
	);
	public static final Feature<FillLayerFeatureConfig> field_19201 = register("fill_layer", new FillLayerFeature(FillLayerFeatureConfig.CODEC));
	public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> field_22188 = register("basalt_pillar", new BasaltPillarFeature(DefaultFeatureConfig.CODEC));
	public static final Feature<OreFeatureConfig> field_22189 = register("no_surface_ore", new NoSurfaceOreFeature(OreFeatureConfig.CODEC));
	public static final Feature<RandomFeatureConfig> field_13593 = register("random_selector", new RandomFeature(RandomFeatureConfig.CODEC));
	public static final Feature<SimpleRandomFeatureConfig> field_13555 = register(
		"simple_random_selector", new SimpleRandomFeature(SimpleRandomFeatureConfig.CODEC)
	);
	public static final Feature<RandomBooleanFeatureConfig> field_13550 = register(
		"random_boolean_selector", new RandomBooleanFeature(RandomBooleanFeatureConfig.CODEC)
	);
	public static final Feature<DecoratedFeatureConfig> field_21217 = register("decorated", new DecoratedFeature(DecoratedFeatureConfig.CODEC));
	private final Codec<ConfiguredFeature<FC, Feature<FC>>> codec;

	private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
		return Registry.register(Registry.FEATURE, name, feature);
	}

	public Feature(Codec<FC> configCodec) {
		this.codec = configCodec.fieldOf("config")
			.<ConfiguredFeature<FC, Feature<FC>>>xmap(featureConfig -> new ConfiguredFeature<>(this, featureConfig), configuredFeature -> configuredFeature.config)
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

	public abstract boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, FC featureConfig);

	protected static boolean isStone(Block block) {
		return block == Blocks.field_10340 || block == Blocks.field_10474 || block == Blocks.field_10508 || block == Blocks.field_10115;
	}

	public static boolean isSoil(Block block) {
		return block == Blocks.field_10566
			|| block == Blocks.field_10219
			|| block == Blocks.field_10520
			|| block == Blocks.field_10253
			|| block == Blocks.field_10402;
	}

	public static boolean isSoil(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> isSoil(state.getBlock()));
	}

	public static boolean isAir(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, AbstractBlock.AbstractBlockState::isAir);
	}
}
