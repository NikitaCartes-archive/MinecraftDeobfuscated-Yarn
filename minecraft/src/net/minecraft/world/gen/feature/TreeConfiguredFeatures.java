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
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.foliage.CherryFoliagePlacer;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliage.JungleFoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.foliage.MegaPineFoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;
import net.minecraft.world.gen.root.AboveRootPlacement;
import net.minecraft.world.gen.root.MangroveRootPlacement;
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
import net.minecraft.world.gen.trunk.CherryTrunkPlacer;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.MegaJungleTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.UpwardsBranchingTrunkPlacer;

public class TreeConfiguredFeatures {
	public static final RegistryKey<ConfiguredFeature<?, ?>> CRIMSON_FUNGUS = ConfiguredFeatures.of("crimson_fungus");
	public static final RegistryKey<ConfiguredFeature<?, ?>> CRIMSON_FUNGUS_PLANTED = ConfiguredFeatures.of("crimson_fungus_planted");
	public static final RegistryKey<ConfiguredFeature<?, ?>> WARPED_FUNGUS = ConfiguredFeatures.of("warped_fungus");
	public static final RegistryKey<ConfiguredFeature<?, ?>> WARPED_FUNGUS_PLANTED = ConfiguredFeatures.of("warped_fungus_planted");
	public static final RegistryKey<ConfiguredFeature<?, ?>> HUGE_BROWN_MUSHROOM = ConfiguredFeatures.of("huge_brown_mushroom");
	public static final RegistryKey<ConfiguredFeature<?, ?>> HUGE_RED_MUSHROOM = ConfiguredFeatures.of("huge_red_mushroom");
	public static final RegistryKey<ConfiguredFeature<?, ?>> OAK = ConfiguredFeatures.of("oak");
	public static final RegistryKey<ConfiguredFeature<?, ?>> DARK_OAK = ConfiguredFeatures.of("dark_oak");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BIRCH = ConfiguredFeatures.of("birch");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ACACIA = ConfiguredFeatures.of("acacia");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SPRUCE = ConfiguredFeatures.of("spruce");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PINE = ConfiguredFeatures.of("pine");
	public static final RegistryKey<ConfiguredFeature<?, ?>> JUNGLE_TREE = ConfiguredFeatures.of("jungle_tree");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FANCY_OAK = ConfiguredFeatures.of("fancy_oak");
	public static final RegistryKey<ConfiguredFeature<?, ?>> JUNGLE_TREE_NO_VINE = ConfiguredFeatures.of("jungle_tree_no_vine");
	public static final RegistryKey<ConfiguredFeature<?, ?>> MEGA_JUNGLE_TREE = ConfiguredFeatures.of("mega_jungle_tree");
	public static final RegistryKey<ConfiguredFeature<?, ?>> MEGA_SPRUCE = ConfiguredFeatures.of("mega_spruce");
	public static final RegistryKey<ConfiguredFeature<?, ?>> MEGA_PINE = ConfiguredFeatures.of("mega_pine");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SUPER_BIRCH_BEES_0002 = ConfiguredFeatures.of("super_birch_bees_0002");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SUPER_BIRCH_BEES = ConfiguredFeatures.of("super_birch_bees");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SWAMP_OAK = ConfiguredFeatures.of("swamp_oak");
	public static final RegistryKey<ConfiguredFeature<?, ?>> JUNGLE_BUSH = ConfiguredFeatures.of("jungle_bush");
	public static final RegistryKey<ConfiguredFeature<?, ?>> AZALEA_TREE = ConfiguredFeatures.of("azalea_tree");
	public static final RegistryKey<ConfiguredFeature<?, ?>> MANGROVE = ConfiguredFeatures.of("mangrove");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TALL_MANGROVE = ConfiguredFeatures.of("tall_mangrove");
	public static final RegistryKey<ConfiguredFeature<?, ?>> CHERRY = ConfiguredFeatures.of("cherry");
	public static final RegistryKey<ConfiguredFeature<?, ?>> OAK_BEES_0002 = ConfiguredFeatures.of("oak_bees_0002");
	public static final RegistryKey<ConfiguredFeature<?, ?>> OAK_BEES_002 = ConfiguredFeatures.of("oak_bees_002");
	public static final RegistryKey<ConfiguredFeature<?, ?>> OAK_BEES_005 = ConfiguredFeatures.of("oak_bees_005");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BIRCH_BEES_0002 = ConfiguredFeatures.of("birch_bees_0002");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BIRCH_BEES_002 = ConfiguredFeatures.of("birch_bees_002");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BIRCH_BEES_005 = ConfiguredFeatures.of("birch_bees_005");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FANCY_OAK_BEES_0002 = ConfiguredFeatures.of("fancy_oak_bees_0002");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FANCY_OAK_BEES_002 = ConfiguredFeatures.of("fancy_oak_bees_002");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FANCY_OAK_BEES_005 = ConfiguredFeatures.of("fancy_oak_bees_005");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FANCY_OAK_BEES = ConfiguredFeatures.of("fancy_oak_bees");
	public static final RegistryKey<ConfiguredFeature<?, ?>> CHERRY_BEES_005 = ConfiguredFeatures.of("cherry_bees_005");

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

	private static TreeFeatureConfig.Builder cherry() {
		return new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.CHERRY_LOG),
				new CherryTrunkPlacer(
					7,
					1,
					0,
					new WeightedListIntProvider(
						DataPool.<IntProvider>builder().add(ConstantIntProvider.create(1), 1).add(ConstantIntProvider.create(2), 1).add(ConstantIntProvider.create(3), 1).build()
					),
					UniformIntProvider.create(2, 4),
					UniformIntProvider.create(-4, -3),
					UniformIntProvider.create(-1, 0)
				),
				BlockStateProvider.of(Blocks.CHERRY_LEAVES),
				new CherryFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(0), ConstantIntProvider.create(5), 0.25F, 0.5F, 0.16666667F, 0.33333334F),
				new TwoLayersFeatureSize(1, 0, 2)
			)
			.ignoreVines();
	}

	public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable) {
		RegistryEntryLookup<Block> registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.BLOCK);
		BlockPredicate blockPredicate = BlockPredicate.matchingBlocks(
			Blocks.OAK_SAPLING,
			Blocks.SPRUCE_SAPLING,
			Blocks.BIRCH_SAPLING,
			Blocks.JUNGLE_SAPLING,
			Blocks.ACACIA_SAPLING,
			Blocks.CHERRY_SAPLING,
			Blocks.DARK_OAK_SAPLING,
			Blocks.MANGROVE_PROPAGULE,
			Blocks.DANDELION,
			Blocks.TORCHFLOWER,
			Blocks.POPPY,
			Blocks.BLUE_ORCHID,
			Blocks.ALLIUM,
			Blocks.AZURE_BLUET,
			Blocks.RED_TULIP,
			Blocks.ORANGE_TULIP,
			Blocks.WHITE_TULIP,
			Blocks.PINK_TULIP,
			Blocks.OXEYE_DAISY,
			Blocks.CORNFLOWER,
			Blocks.WITHER_ROSE,
			Blocks.LILY_OF_THE_VALLEY,
			Blocks.BROWN_MUSHROOM,
			Blocks.RED_MUSHROOM,
			Blocks.WHEAT,
			Blocks.SUGAR_CANE,
			Blocks.ATTACHED_PUMPKIN_STEM,
			Blocks.ATTACHED_MELON_STEM,
			Blocks.PUMPKIN_STEM,
			Blocks.MELON_STEM,
			Blocks.LILY_PAD,
			Blocks.NETHER_WART,
			Blocks.COCOA,
			Blocks.CARROTS,
			Blocks.POTATOES,
			Blocks.CHORUS_PLANT,
			Blocks.CHORUS_FLOWER,
			Blocks.TORCHFLOWER_CROP,
			Blocks.PITCHER_CROP,
			Blocks.BEETROOTS,
			Blocks.SWEET_BERRY_BUSH,
			Blocks.WARPED_FUNGUS,
			Blocks.CRIMSON_FUNGUS,
			Blocks.WEEPING_VINES,
			Blocks.WEEPING_VINES_PLANT,
			Blocks.TWISTING_VINES,
			Blocks.TWISTING_VINES_PLANT,
			Blocks.CAVE_VINES,
			Blocks.CAVE_VINES_PLANT,
			Blocks.SPORE_BLOSSOM,
			Blocks.AZALEA,
			Blocks.FLOWERING_AZALEA,
			Blocks.MOSS_CARPET,
			Blocks.PINK_PETALS,
			Blocks.BIG_DRIPLEAF,
			Blocks.BIG_DRIPLEAF_STEM,
			Blocks.SMALL_DRIPLEAF
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			CRIMSON_FUNGUS,
			Feature.HUGE_FUNGUS,
			new HugeFungusFeatureConfig(
				Blocks.CRIMSON_NYLIUM.getDefaultState(),
				Blocks.CRIMSON_STEM.getDefaultState(),
				Blocks.NETHER_WART_BLOCK.getDefaultState(),
				Blocks.SHROOMLIGHT.getDefaultState(),
				blockPredicate,
				false
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			CRIMSON_FUNGUS_PLANTED,
			Feature.HUGE_FUNGUS,
			new HugeFungusFeatureConfig(
				Blocks.CRIMSON_NYLIUM.getDefaultState(),
				Blocks.CRIMSON_STEM.getDefaultState(),
				Blocks.NETHER_WART_BLOCK.getDefaultState(),
				Blocks.SHROOMLIGHT.getDefaultState(),
				blockPredicate,
				true
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			WARPED_FUNGUS,
			Feature.HUGE_FUNGUS,
			new HugeFungusFeatureConfig(
				Blocks.WARPED_NYLIUM.getDefaultState(),
				Blocks.WARPED_STEM.getDefaultState(),
				Blocks.WARPED_WART_BLOCK.getDefaultState(),
				Blocks.SHROOMLIGHT.getDefaultState(),
				blockPredicate,
				false
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			WARPED_FUNGUS_PLANTED,
			Feature.HUGE_FUNGUS,
			new HugeFungusFeatureConfig(
				Blocks.WARPED_NYLIUM.getDefaultState(),
				Blocks.WARPED_STEM.getDefaultState(),
				Blocks.WARPED_WART_BLOCK.getDefaultState(),
				Blocks.SHROOMLIGHT.getDefaultState(),
				blockPredicate,
				true
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			HUGE_BROWN_MUSHROOM,
			Feature.HUGE_BROWN_MUSHROOM,
			new HugeMushroomFeatureConfig(
				BlockStateProvider.of(
					Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.UP, Boolean.valueOf(true)).with(MushroomBlock.DOWN, Boolean.valueOf(false))
				),
				BlockStateProvider.of(
					Blocks.MUSHROOM_STEM.getDefaultState().with(MushroomBlock.UP, Boolean.valueOf(false)).with(MushroomBlock.DOWN, Boolean.valueOf(false))
				),
				3
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			HUGE_RED_MUSHROOM,
			Feature.HUGE_RED_MUSHROOM,
			new HugeMushroomFeatureConfig(
				BlockStateProvider.of(Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false))),
				BlockStateProvider.of(
					Blocks.MUSHROOM_STEM.getDefaultState().with(MushroomBlock.UP, Boolean.valueOf(false)).with(MushroomBlock.DOWN, Boolean.valueOf(false))
				),
				2
			)
		);
		BeehiveTreeDecorator beehiveTreeDecorator = new BeehiveTreeDecorator(0.002F);
		BeehiveTreeDecorator beehiveTreeDecorator2 = new BeehiveTreeDecorator(0.01F);
		BeehiveTreeDecorator beehiveTreeDecorator3 = new BeehiveTreeDecorator(0.02F);
		BeehiveTreeDecorator beehiveTreeDecorator4 = new BeehiveTreeDecorator(0.05F);
		BeehiveTreeDecorator beehiveTreeDecorator5 = new BeehiveTreeDecorator(1.0F);
		ConfiguredFeatures.register(featureRegisterable, OAK, Feature.TREE, oak().build());
		ConfiguredFeatures.register(
			featureRegisterable,
			DARK_OAK,
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
		ConfiguredFeatures.register(featureRegisterable, BIRCH, Feature.TREE, birch().build());
		ConfiguredFeatures.register(
			featureRegisterable,
			ACACIA,
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
		ConfiguredFeatures.register(featureRegisterable, CHERRY, Feature.TREE, cherry().build());
		ConfiguredFeatures.register(featureRegisterable, CHERRY_BEES_005, Feature.TREE, cherry().decorators(List.of(beehiveTreeDecorator4)).build());
		ConfiguredFeatures.register(
			featureRegisterable,
			SPRUCE,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			PINE,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			JUNGLE_TREE,
			Feature.TREE,
			jungle()
				.decorators(ImmutableList.of(new CocoaBeansTreeDecorator(0.2F), TrunkVineTreeDecorator.INSTANCE, new LeavesVineTreeDecorator(0.25F)))
				.ignoreVines()
				.build()
		);
		ConfiguredFeatures.register(featureRegisterable, FANCY_OAK, Feature.TREE, fancyOak().build());
		ConfiguredFeatures.register(featureRegisterable, JUNGLE_TREE_NO_VINE, Feature.TREE, jungle().ignoreVines().build());
		ConfiguredFeatures.register(
			featureRegisterable,
			MEGA_JUNGLE_TREE,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			MEGA_SPRUCE,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			MEGA_PINE,
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
		ConfiguredFeatures.register(featureRegisterable, SUPER_BIRCH_BEES_0002, Feature.TREE, superBirch().decorators(ImmutableList.of(beehiveTreeDecorator)).build());
		ConfiguredFeatures.register(featureRegisterable, SUPER_BIRCH_BEES, Feature.TREE, superBirch().decorators(ImmutableList.of(beehiveTreeDecorator5)).build());
		ConfiguredFeatures.register(
			featureRegisterable,
			SWAMP_OAK,
			Feature.TREE,
			builder(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 5, 3, 0, 3).decorators(ImmutableList.of(new LeavesVineTreeDecorator(0.25F))).build()
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			JUNGLE_BUSH,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			AZALEA_TREE,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			MANGROVE,
			Feature.TREE,
			new TreeFeatureConfig.Builder(
					BlockStateProvider.of(Blocks.MANGROVE_LOG),
					new UpwardsBranchingTrunkPlacer(
						2, 1, 4, UniformIntProvider.create(1, 4), 0.5F, UniformIntProvider.create(0, 1), registryEntryLookup.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
					),
					BlockStateProvider.of(Blocks.MANGROVE_LEAVES),
					new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 70),
					Optional.of(
						new MangroveRootPlacer(
							UniformIntProvider.create(1, 3),
							BlockStateProvider.of(Blocks.MANGROVE_ROOTS),
							Optional.of(new AboveRootPlacement(BlockStateProvider.of(Blocks.MOSS_CARPET), 0.5F)),
							new MangroveRootPlacement(
								registryEntryLookup.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH),
								RegistryEntryList.of(Block::getRegistryEntry, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS),
								BlockStateProvider.of(Blocks.MUDDY_MANGROVE_ROOTS),
								8,
								15,
								0.2F
							)
						)
					),
					new TwoLayersFeatureSize(2, 0, 2)
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
						beehiveTreeDecorator2
					)
				)
				.ignoreVines()
				.build()
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TALL_MANGROVE,
			Feature.TREE,
			new TreeFeatureConfig.Builder(
					BlockStateProvider.of(Blocks.MANGROVE_LOG),
					new UpwardsBranchingTrunkPlacer(
						4, 1, 9, UniformIntProvider.create(1, 6), 0.5F, UniformIntProvider.create(0, 1), registryEntryLookup.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
					),
					BlockStateProvider.of(Blocks.MANGROVE_LEAVES),
					new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 70),
					Optional.of(
						new MangroveRootPlacer(
							UniformIntProvider.create(3, 7),
							BlockStateProvider.of(Blocks.MANGROVE_ROOTS),
							Optional.of(new AboveRootPlacement(BlockStateProvider.of(Blocks.MOSS_CARPET), 0.5F)),
							new MangroveRootPlacement(
								registryEntryLookup.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH),
								RegistryEntryList.of(Block::getRegistryEntry, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS),
								BlockStateProvider.of(Blocks.MUDDY_MANGROVE_ROOTS),
								8,
								15,
								0.2F
							)
						)
					),
					new TwoLayersFeatureSize(3, 0, 2)
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
						beehiveTreeDecorator2
					)
				)
				.ignoreVines()
				.build()
		);
		ConfiguredFeatures.register(featureRegisterable, OAK_BEES_0002, Feature.TREE, oak().decorators(List.of(beehiveTreeDecorator)).build());
		ConfiguredFeatures.register(featureRegisterable, OAK_BEES_002, Feature.TREE, oak().decorators(List.of(beehiveTreeDecorator3)).build());
		ConfiguredFeatures.register(featureRegisterable, OAK_BEES_005, Feature.TREE, oak().decorators(List.of(beehiveTreeDecorator4)).build());
		ConfiguredFeatures.register(featureRegisterable, BIRCH_BEES_0002, Feature.TREE, birch().decorators(List.of(beehiveTreeDecorator)).build());
		ConfiguredFeatures.register(featureRegisterable, BIRCH_BEES_002, Feature.TREE, birch().decorators(List.of(beehiveTreeDecorator3)).build());
		ConfiguredFeatures.register(featureRegisterable, BIRCH_BEES_005, Feature.TREE, birch().decorators(List.of(beehiveTreeDecorator4)).build());
		ConfiguredFeatures.register(featureRegisterable, FANCY_OAK_BEES_0002, Feature.TREE, fancyOak().decorators(List.of(beehiveTreeDecorator)).build());
		ConfiguredFeatures.register(featureRegisterable, FANCY_OAK_BEES_002, Feature.TREE, fancyOak().decorators(List.of(beehiveTreeDecorator3)).build());
		ConfiguredFeatures.register(featureRegisterable, FANCY_OAK_BEES_005, Feature.TREE, fancyOak().decorators(List.of(beehiveTreeDecorator4)).build());
		ConfiguredFeatures.register(featureRegisterable, FANCY_OAK_BEES, Feature.TREE, fancyOak().decorators(List.of(beehiveTreeDecorator5)).build());
	}
}
