package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.PropaguleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliage.JungleFoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.foliage.MegaPineFoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;
import net.minecraft.world.gen.root.MangroveRootPlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.RandomizedIntBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.treedecorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.treedecorator.AttachedToLeavesTreeDecorator;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.treedecorator.LeavesVineTreeDecorator;
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.MegaJungleTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.UpwardsBranchingTrunkPlacer;

public class TreeConfiguredFeatures {
	public static final RegistryEntry<ConfiguredFeature<HugeFungusFeatureConfig, ?>> CRIMSON_FUNGUS = ConfiguredFeatures.register(
		"crimson_fungus",
		Feature.HUGE_FUNGUS,
		new HugeFungusFeatureConfig(
			Blocks.CRIMSON_NYLIUM.getDefaultState(),
			Blocks.CRIMSON_STEM.getDefaultState(),
			Blocks.NETHER_WART_BLOCK.getDefaultState(),
			Blocks.SHROOMLIGHT.getDefaultState(),
			false
		)
	);
	public static final RegistryEntry<ConfiguredFeature<HugeFungusFeatureConfig, ?>> CRIMSON_FUNGUS_PLANTED = ConfiguredFeatures.register(
		"crimson_fungus_planted",
		Feature.HUGE_FUNGUS,
		new HugeFungusFeatureConfig(
			Blocks.CRIMSON_NYLIUM.getDefaultState(),
			Blocks.CRIMSON_STEM.getDefaultState(),
			Blocks.NETHER_WART_BLOCK.getDefaultState(),
			Blocks.SHROOMLIGHT.getDefaultState(),
			true
		)
	);
	public static final RegistryEntry<ConfiguredFeature<HugeFungusFeatureConfig, ?>> WARPED_FUNGUS = ConfiguredFeatures.register(
		"warped_fungus",
		Feature.HUGE_FUNGUS,
		new HugeFungusFeatureConfig(
			Blocks.WARPED_NYLIUM.getDefaultState(),
			Blocks.WARPED_STEM.getDefaultState(),
			Blocks.WARPED_WART_BLOCK.getDefaultState(),
			Blocks.SHROOMLIGHT.getDefaultState(),
			false
		)
	);
	public static final RegistryEntry<ConfiguredFeature<HugeFungusFeatureConfig, ?>> WARPED_FUNGUS_PLANTED = ConfiguredFeatures.register(
		"warped_fungus_planted",
		Feature.HUGE_FUNGUS,
		new HugeFungusFeatureConfig(
			Blocks.WARPED_NYLIUM.getDefaultState(),
			Blocks.WARPED_STEM.getDefaultState(),
			Blocks.WARPED_WART_BLOCK.getDefaultState(),
			Blocks.SHROOMLIGHT.getDefaultState(),
			true
		)
	);
	public static final RegistryEntry<ConfiguredFeature<HugeMushroomFeatureConfig, ?>> HUGE_BROWN_MUSHROOM = ConfiguredFeatures.register(
		"huge_brown_mushroom",
		Feature.HUGE_BROWN_MUSHROOM,
		new HugeMushroomFeatureConfig(
			BlockStateProvider.of(
				Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.UP, Boolean.valueOf(true)).with(MushroomBlock.DOWN, Boolean.valueOf(false))
			),
			BlockStateProvider.of(Blocks.MUSHROOM_STEM.getDefaultState().with(MushroomBlock.UP, Boolean.valueOf(false)).with(MushroomBlock.DOWN, Boolean.valueOf(false))),
			3
		)
	);
	public static final RegistryEntry<ConfiguredFeature<HugeMushroomFeatureConfig, ?>> HUGE_RED_MUSHROOM = ConfiguredFeatures.register(
		"huge_red_mushroom",
		Feature.HUGE_RED_MUSHROOM,
		new HugeMushroomFeatureConfig(
			BlockStateProvider.of(Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false))),
			BlockStateProvider.of(Blocks.MUSHROOM_STEM.getDefaultState().with(MushroomBlock.UP, Boolean.valueOf(false)).with(MushroomBlock.DOWN, Boolean.valueOf(false))),
			2
		)
	);
	private static final BeehiveTreeDecorator BEES_0002 = new BeehiveTreeDecorator(0.002F);
	private static final BeehiveTreeDecorator BEES_002 = new BeehiveTreeDecorator(0.02F);
	private static final BeehiveTreeDecorator BEES_005 = new BeehiveTreeDecorator(0.05F);
	private static final BeehiveTreeDecorator BEES = new BeehiveTreeDecorator(1.0F);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> OAK = ConfiguredFeatures.register("oak", Feature.TREE, oak().build());
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> DARK_OAK = ConfiguredFeatures.register(
		"dark_oak",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.DARK_OAK_LOG),
				new DarkOakTrunkPlacer(6, 2, 1),
				BlockStateProvider.of(Blocks.DARK_OAK_LEAVES),
				new DarkOakFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0)),
				new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())
			)
			.ignoreVines()
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> BIRCH = ConfiguredFeatures.register("birch", Feature.TREE, birch().build());
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> ACACIA = ConfiguredFeatures.register(
		"acacia",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.ACACIA_LOG),
				new ForkingTrunkPlacer(5, 2, 2),
				BlockStateProvider.of(Blocks.ACACIA_LEAVES),
				new AcaciaFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
				new TwoLayersFeatureSize(1, 0, 2)
			)
			.ignoreVines()
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> SPRUCE = ConfiguredFeatures.register(
		"spruce",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.SPRUCE_LOG),
				new StraightTrunkPlacer(5, 2, 1),
				BlockStateProvider.of(Blocks.SPRUCE_LEAVES),
				new SpruceFoliagePlacer(UniformIntProvider.create(2, 3), UniformIntProvider.create(0, 2), UniformIntProvider.create(1, 2)),
				new TwoLayersFeatureSize(2, 0, 2)
			)
			.ignoreVines()
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> PINE = ConfiguredFeatures.register(
		"pine",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.SPRUCE_LOG),
				new StraightTrunkPlacer(6, 4, 0),
				BlockStateProvider.of(Blocks.SPRUCE_LEAVES),
				new PineFoliagePlacer(ConstantIntProvider.create(1), ConstantIntProvider.create(1), UniformIntProvider.create(3, 4)),
				new TwoLayersFeatureSize(2, 0, 2)
			)
			.ignoreVines()
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> JUNGLE_TREE = ConfiguredFeatures.register(
		"jungle_tree",
		Feature.TREE,
		jungle()
			.decorators(ImmutableList.of(new CocoaBeansTreeDecorator(0.2F), TrunkVineTreeDecorator.INSTANCE, new LeavesVineTreeDecorator(0.25F)))
			.ignoreVines()
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> FANCY_OAK = ConfiguredFeatures.register(
		"fancy_oak", Feature.TREE, fancyOak().build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> JUNGLE_TREE_NO_VINE = ConfiguredFeatures.register(
		"jungle_tree_no_vine", Feature.TREE, jungle().ignoreVines().build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> MEGA_JUNGLE_TREE = ConfiguredFeatures.register(
		"mega_jungle_tree",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.JUNGLE_LOG),
				new MegaJungleTrunkPlacer(10, 2, 19),
				BlockStateProvider.of(Blocks.JUNGLE_LEAVES),
				new JungleFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 2),
				new TwoLayersFeatureSize(1, 1, 2)
			)
			.decorators(ImmutableList.of(TrunkVineTreeDecorator.INSTANCE, new LeavesVineTreeDecorator(0.25F)))
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> MEGA_SPRUCE = ConfiguredFeatures.register(
		"mega_spruce",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.SPRUCE_LOG),
				new GiantTrunkPlacer(13, 2, 14),
				BlockStateProvider.of(Blocks.SPRUCE_LEAVES),
				new MegaPineFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0), UniformIntProvider.create(13, 17)),
				new TwoLayersFeatureSize(1, 1, 2)
			)
			.decorators(ImmutableList.of(new AlterGroundTreeDecorator(BlockStateProvider.of(Blocks.PODZOL))))
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> MEGA_PINE = ConfiguredFeatures.register(
		"mega_pine",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.SPRUCE_LOG),
				new GiantTrunkPlacer(13, 2, 14),
				BlockStateProvider.of(Blocks.SPRUCE_LEAVES),
				new MegaPineFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0), UniformIntProvider.create(3, 7)),
				new TwoLayersFeatureSize(1, 1, 2)
			)
			.decorators(ImmutableList.of(new AlterGroundTreeDecorator(BlockStateProvider.of(Blocks.PODZOL))))
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> SUPER_BIRCH_BEES_0002 = ConfiguredFeatures.register(
		"super_birch_bees_0002", Feature.TREE, superBirch().decorators(ImmutableList.of(BEES_0002)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> SUPER_BIRCH_BEES = ConfiguredFeatures.register(
		"super_birch_bees", Feature.TREE, superBirch().decorators(ImmutableList.of(BEES)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> SWAMP_OAK = ConfiguredFeatures.register(
		"swamp_oak", Feature.TREE, builder(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 5, 3, 0, 3).decorators(ImmutableList.of(new LeavesVineTreeDecorator(0.25F))).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> JUNGLE_BUSH = ConfiguredFeatures.register(
		"jungle_bush",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.JUNGLE_LOG),
				new StraightTrunkPlacer(1, 0, 0),
				BlockStateProvider.of(Blocks.OAK_LEAVES),
				new BushFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(1), 2),
				new TwoLayersFeatureSize(0, 0, 0)
			)
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> AZALEA_TREE = ConfiguredFeatures.register(
		"azalea_tree",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.OAK_LOG),
				new BendingTrunkPlacer(4, 2, 0, 3, UniformIntProvider.create(1, 2)),
				new WeightedBlockStateProvider(
					DataPool.<BlockState>builder().add(Blocks.AZALEA_LEAVES.getDefaultState(), 3).add(Blocks.FLOWERING_AZALEA_LEAVES.getDefaultState(), 1)
				),
				new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 50),
				new TwoLayersFeatureSize(1, 0, 1)
			)
			.dirtProvider(BlockStateProvider.of(Blocks.ROOTED_DIRT))
			.forceDirt()
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> MANGROVE = ConfiguredFeatures.register(
		"mangrove",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.MANGROVE_LOG),
				new UpwardsBranchingTrunkPlacer(
					2,
					1,
					4,
					UniformIntProvider.create(1, 4),
					0.5F,
					UniformIntProvider.create(0, 1),
					Registry.BLOCK.getOrCreateEntryList(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
				),
				BlockStateProvider.of(Blocks.MANGROVE_LEAVES),
				new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 70),
				Optional.of(
					new MangroveRootPlacer(
						BlockStateProvider.of(Blocks.MANGROVE_ROOTS),
						Registry.BLOCK.getOrCreateEntryList(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH),
						RegistryEntryList.of(Block::getRegistryEntry, Blocks.MUD),
						BlockStateProvider.of(Blocks.MUDDY_MANGROVE_ROOTS),
						8,
						15,
						UniformIntProvider.create(1, 3),
						0.2F
					)
				),
				new TwoLayersFeatureSize(1, 0, 2)
			)
			.decorators(
				List.of(
					new LeavesVineTreeDecorator(0.125F),
					new AttachedToLeavesTreeDecorator(
						0.14F,
						1,
						0,
						new RandomizedIntBlockStateProvider(
							BlockStateProvider.of(Blocks.MANGROVE_PROPAGULE.getDefaultState().with(PropaguleBlock.HANGING, Boolean.valueOf(true))),
							PropaguleBlock.AGE,
							UniformIntProvider.create(0, 4)
						),
						2,
						List.of(Direction.DOWN)
					),
					BEES_005
				)
			)
			.ignoreVines()
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> TALL_MANGROVE = ConfiguredFeatures.register(
		"tall_mangrove",
		Feature.TREE,
		new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.MANGROVE_LOG),
				new UpwardsBranchingTrunkPlacer(
					4,
					1,
					9,
					UniformIntProvider.create(1, 6),
					0.5F,
					UniformIntProvider.create(0, 1),
					Registry.BLOCK.getOrCreateEntryList(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
				),
				BlockStateProvider.of(Blocks.MANGROVE_LEAVES),
				new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 70),
				Optional.of(
					new MangroveRootPlacer(
						BlockStateProvider.of(Blocks.MANGROVE_ROOTS),
						Registry.BLOCK.getOrCreateEntryList(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH),
						RegistryEntryList.of(Block::getRegistryEntry, Blocks.MUD),
						BlockStateProvider.of(Blocks.MUDDY_MANGROVE_ROOTS),
						8,
						15,
						UniformIntProvider.create(3, 7),
						0.2F
					)
				),
				new TwoLayersFeatureSize(1, 0, 3)
			)
			.decorators(
				List.of(
					new LeavesVineTreeDecorator(0.125F),
					new AttachedToLeavesTreeDecorator(
						0.14F,
						1,
						0,
						new RandomizedIntBlockStateProvider(
							BlockStateProvider.of(Blocks.MANGROVE_PROPAGULE.getDefaultState().with(PropaguleBlock.HANGING, Boolean.valueOf(true))),
							PropaguleBlock.AGE,
							UniformIntProvider.create(0, 4)
						),
						2,
						List.of(Direction.DOWN)
					),
					BEES_005
				)
			)
			.ignoreVines()
			.build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> OAK_BEES_0002 = ConfiguredFeatures.register(
		"oak_bees_0002", Feature.TREE, oak().decorators(List.of(BEES_0002)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> OAK_BEES_002 = ConfiguredFeatures.register(
		"oak_bees_002", Feature.TREE, oak().decorators(List.of(BEES_002)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> OAK_BEES_005 = ConfiguredFeatures.register(
		"oak_bees_005", Feature.TREE, oak().decorators(List.of(BEES_005)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> BIRCH_BEES_0002 = ConfiguredFeatures.register(
		"birch_bees_0002", Feature.TREE, birch().decorators(List.of(BEES_0002)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> BIRCH_BEES_002 = ConfiguredFeatures.register(
		"birch_bees_002", Feature.TREE, birch().decorators(List.of(BEES_002)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> BIRCH_BEES_005 = ConfiguredFeatures.register(
		"birch_bees_005", Feature.TREE, birch().decorators(List.of(BEES_005)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> FANCY_OAK_BEES_0002 = ConfiguredFeatures.register(
		"fancy_oak_bees_0002", Feature.TREE, fancyOak().decorators(List.of(BEES_0002)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> FANCY_OAK_BEES_002 = ConfiguredFeatures.register(
		"fancy_oak_bees_002", Feature.TREE, fancyOak().decorators(List.of(BEES_002)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> FANCY_OAK_BEES_005 = ConfiguredFeatures.register(
		"fancy_oak_bees_005", Feature.TREE, fancyOak().decorators(List.of(BEES_005)).build()
	);
	public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> FANCY_OAK_BEES = ConfiguredFeatures.register(
		"fancy_oak_bees", Feature.TREE, fancyOak().decorators(List.of(BEES)).build()
	);

	private static TreeFeatureConfig.Builder builder(Block log, Block leaves, int baseHeight, int firstRandomHeight, int secondRandomHeight, int radius) {
		return new TreeFeatureConfig.Builder(
			BlockStateProvider.of(log),
			new StraightTrunkPlacer(baseHeight, firstRandomHeight, secondRandomHeight),
			BlockStateProvider.of(leaves),
			new BlobFoliagePlacer(ConstantIntProvider.create(radius), ConstantIntProvider.create(0), 3),
			new TwoLayersFeatureSize(1, 0, 1)
		);
	}

	private static TreeFeatureConfig.Builder oak() {
		return builder(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 4, 2, 0, 2).ignoreVines();
	}

	private static TreeFeatureConfig.Builder birch() {
		return builder(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 0, 2).ignoreVines();
	}

	private static TreeFeatureConfig.Builder superBirch() {
		return builder(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 6, 2).ignoreVines();
	}

	private static TreeFeatureConfig.Builder jungle() {
		return builder(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES, 4, 8, 0, 2);
	}

	private static TreeFeatureConfig.Builder fancyOak() {
		return new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.OAK_LOG),
				new LargeOakTrunkPlacer(3, 11, 0),
				BlockStateProvider.of(Blocks.OAK_LEAVES),
				new LargeOakFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(4), 4),
				new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
			)
			.ignoreVines();
	}
}
