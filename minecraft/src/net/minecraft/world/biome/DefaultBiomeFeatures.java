package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.decorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.decorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.TrunkVineTreeDecorator;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
import net.minecraft.world.gen.feature.BushFeatureConfig;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;
import net.minecraft.world.gen.placer.ColumnPlacer;
import net.minecraft.world.gen.placer.DoublePlantPlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.ForestFlowerStateProvider;
import net.minecraft.world.gen.stateprovider.PlainFlowerStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedStateProvider;

public class DefaultBiomeFeatures {
	private static final BlockState GRASS = Blocks.GRASS.getDefaultState();
	private static final BlockState FERN = Blocks.FERN.getDefaultState();
	private static final BlockState PODZOL = Blocks.PODZOL.getDefaultState();
	private static final BlockState OAK_LOG = Blocks.OAK_LOG.getDefaultState();
	private static final BlockState OAK_LEAVES = Blocks.OAK_LEAVES.getDefaultState();
	private static final BlockState JUNGLE_LOG = Blocks.JUNGLE_LOG.getDefaultState();
	private static final BlockState JUNGLE_LEAVES = Blocks.JUNGLE_LEAVES.getDefaultState();
	private static final BlockState SPRUCE_LOG = Blocks.SPRUCE_LOG.getDefaultState();
	private static final BlockState SPRUCE_LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();
	private static final BlockState ACACIA_LOG = Blocks.ACACIA_LOG.getDefaultState();
	private static final BlockState ACACIA_LEAVES = Blocks.ACACIA_LEAVES.getDefaultState();
	private static final BlockState BIRCH_LOG = Blocks.BIRCH_LOG.getDefaultState();
	private static final BlockState BIRCH_LEAVES = Blocks.BIRCH_LEAVES.getDefaultState();
	private static final BlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.getDefaultState();
	private static final BlockState DARK_OAK_LEAVES = Blocks.DARK_OAK_LEAVES.getDefaultState();
	private static final BlockState WATER = Blocks.WATER.getDefaultState();
	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final BlockState GRANITE = Blocks.GRANITE.getDefaultState();
	private static final BlockState DIORITE = Blocks.DIORITE.getDefaultState();
	private static final BlockState ANDESITE = Blocks.ANDESITE.getDefaultState();
	private static final BlockState COAL_ORE = Blocks.COAL_ORE.getDefaultState();
	private static final BlockState IRON_ORE = Blocks.IRON_ORE.getDefaultState();
	private static final BlockState GOLD_ORE = Blocks.GOLD_ORE.getDefaultState();
	private static final BlockState REDSTONE_ORE = Blocks.REDSTONE_ORE.getDefaultState();
	private static final BlockState DIAMOND_ORE = Blocks.DIAMOND_ORE.getDefaultState();
	private static final BlockState LAPIS_ORE = Blocks.LAPIS_ORE.getDefaultState();
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState EMERALD_ORE = Blocks.EMERALD_ORE.getDefaultState();
	private static final BlockState INFESTED_STONE = Blocks.INFESTED_STONE.getDefaultState();
	private static final BlockState SAND = Blocks.SAND.getDefaultState();
	private static final BlockState CLAY = Blocks.CLAY.getDefaultState();
	private static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();
	private static final BlockState MOSSY_COBBLESTONE = Blocks.MOSSY_COBBLESTONE.getDefaultState();
	private static final BlockState LARGE_FERN = Blocks.LARGE_FERN.getDefaultState();
	private static final BlockState TALL_GRASS = Blocks.TALL_GRASS.getDefaultState();
	private static final BlockState LILAC = Blocks.LILAC.getDefaultState();
	private static final BlockState ROSE_BUSH = Blocks.ROSE_BUSH.getDefaultState();
	private static final BlockState PEONY = Blocks.PEONY.getDefaultState();
	private static final BlockState BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.getDefaultState();
	private static final BlockState RED_MUSHROOM = Blocks.RED_MUSHROOM.getDefaultState();
	private static final BlockState SEAGRASS = Blocks.SEAGRASS.getDefaultState();
	private static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
	private static final BlockState BLUE_ICE = Blocks.BLUE_ICE.getDefaultState();
	private static final BlockState LILY_OF_THE_VALLEY = Blocks.LILY_OF_THE_VALLEY.getDefaultState();
	private static final BlockState BLUE_ORCHID = Blocks.BLUE_ORCHID.getDefaultState();
	private static final BlockState POPPY = Blocks.POPPY.getDefaultState();
	private static final BlockState DANDELION = Blocks.DANDELION.getDefaultState();
	private static final BlockState DEAD_BUSH = Blocks.DEAD_BUSH.getDefaultState();
	private static final BlockState MELON = Blocks.MELON.getDefaultState();
	private static final BlockState PUMPKIN = Blocks.PUMPKIN.getDefaultState();
	private static final BlockState SWEET_BERRY_BUSH = Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3));
	private static final BlockState FIRE = Blocks.FIRE.getDefaultState();
	private static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
	private static final BlockState LILY_PAD = Blocks.LILY_PAD.getDefaultState();
	private static final BlockState SNOW = Blocks.SNOW.getDefaultState();
	private static final BlockState JACK_O_LANTERN = Blocks.JACK_O_LANTERN.getDefaultState();
	private static final BlockState SUNFLOWER = Blocks.SUNFLOWER.getDefaultState();
	private static final BlockState CACTUS = Blocks.CACTUS.getDefaultState();
	private static final BlockState SUGAR_CANE = Blocks.SUGAR_CANE.getDefaultState();
	private static final BlockState RED_MUSHROOM_BLOCK = Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false));
	private static final BlockState BROWN_MUSHROOM_BLOCK = Blocks.BROWN_MUSHROOM_BLOCK
		.getDefaultState()
		.with(MushroomBlock.UP, Boolean.valueOf(true))
		.with(MushroomBlock.DOWN, Boolean.valueOf(false));
	private static final BlockState MUSHROOM_BLOCK = Blocks.MUSHROOM_STEM
		.getDefaultState()
		.with(MushroomBlock.UP, Boolean.valueOf(false))
		.with(MushroomBlock.DOWN, Boolean.valueOf(false));
	public static final BranchedTreeFeatureConfig OAK_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig JUNGLE_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(JUNGLE_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(8)
		.foliageHeight(3)
		.treeDecorators(ImmutableList.of(new CocoaBeansTreeDecorator(0.2F), new TrunkVineTreeDecorator(), new LeaveVineTreeDecorator()))
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig JUNGLE_SAPLING_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(JUNGLE_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(8)
		.foliageHeight(3)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig PINE_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES), new PineFoliagePlacer(1, 0)
		)
		.baseHeight(7)
		.heightRandA(4)
		.trunkTopOffset(1)
		.foliageHeight(3)
		.foliageHeightRandom(1)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig SPRUCE_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES), new SpruceFoliagePlacer(2, 1)
		)
		.baseHeight(6)
		.heightRandA(3)
		.trunkHeight(1)
		.trunkHeightRandom(1)
		.trunkTopOffsetRandom(2)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig ACACIA_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(ACACIA_LOG), new SimpleStateProvider(ACACIA_LEAVES), new AcaciaFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.heightRandB(2)
		.trunkHeight(0)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig BIRCH_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig LARGE_BIRCH_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.heightRandB(6)
		.foliageHeight(3)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig SWAMP_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(3, 0)
		)
		.baseHeight(5)
		.heightRandA(3)
		.foliageHeight(3)
		.maxWaterDepth(1)
		.treeDecorators(ImmutableList.of(new LeaveVineTreeDecorator()))
		.build();
	public static final BranchedTreeFeatureConfig FANCY_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0)
		)
		.build();
	public static final BranchedTreeFeatureConfig OAK_TREE_WITH_BEEHIVES_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
		.build();
	public static final BranchedTreeFeatureConfig FANCY_TREE_WITH_BEEHIVES_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0)
		)
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
		.build();
	public static final BranchedTreeFeatureConfig field_21193 = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.01F)))
		.build();
	public static final BranchedTreeFeatureConfig field_21194 = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0)
		)
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.01F)))
		.build();
	public static final BranchedTreeFeatureConfig field_21195 = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.01F)))
		.build();
	public static final TreeFeatureConfig JUNGLE_GROUND_BUSH_CONFIG = new TreeFeatureConfig.Builder(
			new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(OAK_LEAVES)
		)
		.baseHeight(4)
		.build();
	public static final MegaTreeFeatureConfig field_21197 = new MegaTreeFeatureConfig.Builder(
			new SimpleStateProvider(DARK_OAK_LOG), new SimpleStateProvider(DARK_OAK_LEAVES)
		)
		.baseHeight(6)
		.build();
	public static final MegaTreeFeatureConfig field_21198 = new MegaTreeFeatureConfig.Builder(
			new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES)
		)
		.baseHeight(13)
		.heightInterval(15)
		.crownHeight(13)
		.method_23411(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleStateProvider(PODZOL))))
		.build();
	public static final MegaTreeFeatureConfig field_21199 = new MegaTreeFeatureConfig.Builder(
			new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES)
		)
		.baseHeight(13)
		.heightInterval(15)
		.crownHeight(3)
		.method_23411(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleStateProvider(PODZOL))))
		.build();
	public static final MegaTreeFeatureConfig MEGA_JUNGLE_TREE_CONFIG = new MegaTreeFeatureConfig.Builder(
			new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(JUNGLE_LEAVES)
		)
		.baseHeight(10)
		.heightInterval(20)
		.method_23411(ImmutableList.of(new TrunkVineTreeDecorator(), new LeaveVineTreeDecorator()))
		.build();
	public static final RandomPatchFeatureConfig field_21201 = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(GRASS), new SimpleBlockPlacer())
		.tries(32)
		.build();
	public static final RandomPatchFeatureConfig field_21202 = new RandomPatchFeatureConfig.Builder(
			new WeightedStateProvider().addState(GRASS, 1).addState(FERN, 4), new SimpleBlockPlacer()
		)
		.tries(32)
		.build();
	public static final RandomPatchFeatureConfig field_21203 = new RandomPatchFeatureConfig.Builder(
			new WeightedStateProvider().addState(GRASS, 3).addState(FERN, 1), new SimpleBlockPlacer()
		)
		.blacklist(ImmutableSet.of(PODZOL))
		.tries(32)
		.build();
	public static final RandomPatchFeatureConfig field_21204 = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(LILY_OF_THE_VALLEY), new SimpleBlockPlacer()
		)
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig field_21205 = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(BLUE_ORCHID), new SimpleBlockPlacer())
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig field_21206 = new RandomPatchFeatureConfig.Builder(
			new WeightedStateProvider().addState(POPPY, 2).addState(DANDELION, 1), new SimpleBlockPlacer()
		)
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig field_21088 = new RandomPatchFeatureConfig.Builder(new PlainFlowerStateProvider(), new SimpleBlockPlacer())
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig field_21089 = new RandomPatchFeatureConfig.Builder(new ForestFlowerStateProvider(), new SimpleBlockPlacer())
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig field_21090 = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(DEAD_BUSH), new SimpleBlockPlacer())
		.tries(4)
		.build();
	public static final RandomPatchFeatureConfig field_21091 = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(MELON), new SimpleBlockPlacer())
		.tries(64)
		.whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock()))
		.canReplace()
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig field_21092 = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(PUMPKIN), new SimpleBlockPlacer())
		.tries(64)
		.whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock()))
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig SWEET_BERRY_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(SWEET_BERRY_BUSH), new SimpleBlockPlacer()
		)
		.tries(64)
		.whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock()))
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig field_21094 = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(FIRE), new SimpleBlockPlacer())
		.tries(64)
		.whitelist(ImmutableSet.of(NETHERRACK.getBlock()))
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig field_21095 = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(LILY_PAD), new SimpleBlockPlacer())
		.tries(10)
		.build();
	public static final RandomPatchFeatureConfig RED_MUSHROOM_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(RED_MUSHROOM), new SimpleBlockPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig BROWN_MUSHROOM_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(BROWN_MUSHROOM), new SimpleBlockPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig LILAC_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(LILAC), new DoublePlantPlacer())
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig ROSE_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(ROSE_BUSH), new DoublePlantPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig PEONY_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(PEONY), new DoublePlantPlacer())
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig SUNFLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(SUNFLOWER), new DoublePlantPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig TALL_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(TALL_GRASS), new DoublePlantPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig LARGE_FERN_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(LARGE_FERN), new DoublePlantPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig CACTUS_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(CACTUS), new ColumnPlacer(1, 2))
		.tries(10)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig SUGAR_CANE_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(SUGAR_CANE), new ColumnPlacer(2, 2)
		)
		.tries(20)
		.spreadX(4)
		.spreadY(0)
		.spreadZ(4)
		.cannotProject()
		.needsWater()
		.build();
	public static final BlockPileFeatureConfig HAY_PILE_CONFIG = new BlockPileFeatureConfig(new BlockStateProvider(Blocks.HAY_BLOCK));
	public static final BlockPileFeatureConfig SNOW_PILE_CONFIG = new BlockPileFeatureConfig(new SimpleStateProvider(SNOW));
	public static final BlockPileFeatureConfig MELON_PILE_CONFIG = new BlockPileFeatureConfig(new SimpleStateProvider(MELON));
	public static final BlockPileFeatureConfig PUMPKIN_PILE_CONFIG = new BlockPileFeatureConfig(
		new WeightedStateProvider().addState(PUMPKIN, 19).addState(JACK_O_LANTERN, 1)
	);
	public static final BlockPileFeatureConfig BLUE_ICE_PILE_CONFIG = new BlockPileFeatureConfig(
		new WeightedStateProvider().addState(BLUE_ICE, 1).addState(PACKED_ICE, 5)
	);
	public static final SpringFeatureConfig field_21111 = new SpringFeatureConfig(
		Fluids.WATER.getDefaultState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE)
	);
	public static final SpringFeatureConfig field_21112 = new SpringFeatureConfig(
		Fluids.LAVA.getDefaultState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE)
	);
	public static final SpringFeatureConfig field_21113 = new SpringFeatureConfig(Fluids.LAVA.getDefaultState(), false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK));
	public static final SpringFeatureConfig field_21141 = new SpringFeatureConfig(Fluids.LAVA.getDefaultState(), false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK));
	public static final HugeMushroomFeatureConfig field_21142 = new HugeMushroomFeatureConfig(
		new SimpleStateProvider(RED_MUSHROOM_BLOCK), new SimpleStateProvider(MUSHROOM_BLOCK), 2
	);
	public static final HugeMushroomFeatureConfig field_21143 = new HugeMushroomFeatureConfig(
		new SimpleStateProvider(BROWN_MUSHROOM_BLOCK), new SimpleStateProvider(MUSHROOM_BLOCK), 3
	);

	public static void addLandCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.14285715F)));
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CANYON, new ProbabilityConfig(0.02F)));
	}

	public static void addOceanCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.06666667F)));
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CANYON, new ProbabilityConfig(0.02F)));
		biome.addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver(Carver.UNDERWATER_CANYON, new ProbabilityConfig(0.02F)));
		biome.addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver(Carver.UNDERWATER_CAVE, new ProbabilityConfig(0.06666667F)));
	}

	public static void addDefaultStructures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Feature.MINESHAFT
				.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.NORMAL))
				.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.PILLAGER_OUTPOST.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Feature.STRONGHOLD.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.SWAMP_HUT.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.DESERT_PYRAMID.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.JUNGLE_TEMPLE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.IGLOO.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false)).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.OCEAN_MONUMENT.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.WOODLAND_MANSION.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.OCEAN_RUIN
				.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F))
				.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Feature.BURIED_TREASURE.configure(new BuriedTreasureFeatureConfig(0.01F)).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.VILLAGE
				.configure(new VillageFeatureConfig("village/plains/town_centers", 6))
				.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addDefaultLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.LAKE.configure(new BushFeatureConfig(WATER)).createDecoratedFeature(Decorator.WATER_LAKE.configure(new LakeDecoratorConfig(4)))
		);
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.LAKE.configure(new BushFeatureConfig(LAVA)).createDecoratedFeature(Decorator.LAVA_LAKE.configure(new LakeDecoratorConfig(80)))
		);
	}

	public static void addDesertLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.LAKE.configure(new BushFeatureConfig(LAVA)).createDecoratedFeature(Decorator.LAVA_LAKE.configure(new LakeDecoratorConfig(80)))
		);
	}

	public static void addDungeons(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Feature.MONSTER_ROOM.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.DUNGEONS.configure(new LakeDecoratorConfig(8)))
		);
	}

	public static void addMineables(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, DIRT, 33))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 256)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GRAVEL, 33))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(8, 0, 0, 256)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GRANITE, 33))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 80)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, DIORITE, 33))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 80)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, ANDESITE, 33))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 80)))
		);
	}

	public static void addDefaultOres(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, COAL_ORE, 17))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 128)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, IRON_ORE, 9))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 64)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GOLD_ORE, 9))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(2, 0, 0, 32)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, REDSTONE_ORE, 8))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(8, 0, 0, 16)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, DIAMOND_ORE, 8))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(1, 0, 0, 16)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, LAPIS_ORE, 7))
				.createDecoratedFeature(Decorator.COUNT_DEPTH_AVERAGE.configure(new CountDepthDecoratorConfig(1, 16, 16)))
		);
	}

	public static void addExtraGoldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GOLD_ORE, 9))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 32, 32, 80)))
		);
	}

	public static void addEmeraldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.EMERALD_ORE
				.configure(new EmeraldOreFeatureConfig(STONE, EMERALD_ORE))
				.createDecoratedFeature(Decorator.EMERALD_ORE.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addInfestedStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, INFESTED_STONE, 9))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(7, 0, 0, 64)))
		);
	}

	public static void addDefaultDisks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.DISK
				.configure(new DiskFeatureConfig(SAND, 7, 2, Lists.<BlockState>newArrayList(DIRT, GRASS_BLOCK)))
				.createDecoratedFeature(Decorator.COUNT_TOP_SOLID.configure(new CountDecoratorConfig(3)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.DISK
				.configure(new DiskFeatureConfig(CLAY, 4, 1, Lists.<BlockState>newArrayList(DIRT, CLAY)))
				.createDecoratedFeature(Decorator.COUNT_TOP_SOLID.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.DISK
				.configure(new DiskFeatureConfig(GRAVEL, 6, 2, Lists.<BlockState>newArrayList(DIRT, GRASS_BLOCK)))
				.createDecoratedFeature(Decorator.COUNT_TOP_SOLID.configure(new CountDecoratorConfig(1)))
		);
	}

	public static void addClay(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Feature.DISK
				.configure(new DiskFeatureConfig(CLAY, 4, 1, Lists.<BlockState>newArrayList(DIRT, CLAY)))
				.createDecoratedFeature(Decorator.COUNT_TOP_SOLID.configure(new CountDecoratorConfig(1)))
		);
	}

	public static void addMossyRocks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.FOREST_ROCK
				.configure(new BoulderFeatureConfig(MOSSY_COBBLESTONE, 0))
				.createDecoratedFeature(Decorator.FOREST_ROCK.configure(new CountDecoratorConfig(3)))
		);
	}

	public static void addLargeFerns(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(LARGE_FERN_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(7)))
		);
	}

	public static void addSweetBerryBushesSnowy(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(SWEET_BERRY_BUSH_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new LakeDecoratorConfig(12)))
		);
	}

	public static void addSweetBerryBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(SWEET_BERRY_BUSH_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1)))
		);
	}

	public static void addBamboo(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.BAMBOO.configure(new ProbabilityConfig(0.0F)).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(16)))
		);
	}

	public static void addBambooJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.BAMBOO
				.configure(new ProbabilityConfig(0.2F))
				.createDecoratedFeature(
					Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0, 0.3, Heightmap.Type.WORLD_SURFACE_WG))
				)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.FANCY_TREE.configure(FANCY_TREE_CONFIG).withChance(0.05F),
							Feature.JUNGLE_GROUND_BUSH.configure(JUNGLE_GROUND_BUSH_CONFIG).withChance(0.15F),
							Feature.MEGA_JUNGLE_TREE.configure(MEGA_JUNGLE_TREE_CONFIG).withChance(0.7F)
						),
						Feature.RANDOM_PATCH.configure(field_21203)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(30, 0.1F, 1)))
		);
	}

	public static void addTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.NORMAL_TREE.configure(PINE_TREE_CONFIG).withChance(0.33333334F)), Feature.NORMAL_TREE.configure(SPRUCE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addWaterBiomeOakTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(ImmutableList.of(Feature.FANCY_TREE.configure(FANCY_TREE_CONFIG).withChance(0.1F)), Feature.NORMAL_TREE.configure(OAK_TREE_CONFIG))
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
		);
	}

	public static void addBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.NORMAL_TREE
				.configure(BIRCH_TREE_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addForestTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.NORMAL_TREE.configure(BIRCH_TREE_CONFIG).withChance(0.2F), Feature.FANCY_TREE.configure(FANCY_TREE_CONFIG).withChance(0.1F)),
						Feature.NORMAL_TREE.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addTallBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.NORMAL_TREE.configure(LARGE_BIRCH_TREE_CONFIG).withChance(0.5F)), Feature.NORMAL_TREE.configure(BIRCH_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.ACACIA_TREE.configure(ACACIA_TREE_CONFIG).withChance(0.8F)), Feature.NORMAL_TREE.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(1, 0.1F, 1)))
		);
	}

	public static void addExtraSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.ACACIA_TREE.configure(ACACIA_TREE_CONFIG).withChance(0.8F)), Feature.NORMAL_TREE.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
		);
	}

	public static void addMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.NORMAL_TREE.configure(SPRUCE_TREE_CONFIG).withChance(0.666F), Feature.FANCY_TREE.configure(FANCY_TREE_CONFIG).withChance(0.1F)),
						Feature.NORMAL_TREE.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
		);
	}

	public static void addExtraMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.NORMAL_TREE.configure(SPRUCE_TREE_CONFIG).withChance(0.666F), Feature.FANCY_TREE.configure(FANCY_TREE_CONFIG).withChance(0.1F)),
						Feature.NORMAL_TREE.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(3, 0.1F, 1)))
		);
	}

	public static void addJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.FANCY_TREE.configure(FANCY_TREE_CONFIG).withChance(0.1F),
							Feature.JUNGLE_GROUND_BUSH.configure(JUNGLE_GROUND_BUSH_CONFIG).withChance(0.5F),
							Feature.MEGA_JUNGLE_TREE.configure(MEGA_JUNGLE_TREE_CONFIG).withChance(0.33333334F)
						),
						Feature.NORMAL_TREE.configure(JUNGLE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(50, 0.1F, 1)))
		);
	}

	public static void addJungleEdgeTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.FANCY_TREE.configure(FANCY_TREE_CONFIG).withChance(0.1F), Feature.JUNGLE_GROUND_BUSH.configure(JUNGLE_GROUND_BUSH_CONFIG).withChance(0.5F)
						),
						Feature.NORMAL_TREE.configure(JUNGLE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
		);
	}

	public static void addBadlandsPlateauTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.NORMAL_TREE
				.configure(OAK_TREE_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(5, 0.1F, 1)))
		);
	}

	public static void addSnowySpruceTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.NORMAL_TREE
				.configure(SPRUCE_TREE_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
		);
	}

	public static void addGiantSpruceTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.MEGA_SPRUCE_TREE.configure(field_21198).withChance(0.33333334F), Feature.NORMAL_TREE.configure(PINE_TREE_CONFIG).withChance(0.33333334F)
						),
						Feature.NORMAL_TREE.configure(SPRUCE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addGiantTreeTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.MEGA_SPRUCE_TREE.configure(field_21198).withChance(0.025641026F),
							Feature.MEGA_SPRUCE_TREE.configure(field_21199).withChance(0.30769232F),
							Feature.NORMAL_TREE.configure(PINE_TREE_CONFIG).withChance(0.33333334F)
						),
						Feature.NORMAL_TREE.configure(SPRUCE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addJungleGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21203).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(25)))
		);
	}

	public static void addSavannaTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(TALL_GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(7)))
		);
	}

	public static void addShatteredSavannaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21201).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(5)))
		);
	}

	public static void addSavannaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21201).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(20)))
		);
	}

	public static void addBadlandsGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21201).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21090).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(20)))
		);
	}

	public static void addForestFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_RANDOM_SELECTOR
				.configure(
					new RandomRandomFeatureConfig(
						ImmutableList.of(
							Feature.RANDOM_PATCH.configure(LILAC_CONFIG),
							Feature.RANDOM_PATCH.configure(ROSE_BUSH_CONFIG),
							Feature.RANDOM_PATCH.configure(PEONY_CONFIG),
							Feature.FLOWER.configure(field_21204)
						),
						0
					)
				)
				.createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(5)))
		);
	}

	public static void addForestGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21201).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(2)))
		);
	}

	public static void addSwampFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.NORMAL_TREE
				.configure(SWAMP_TREE_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.FLOWER.configure(field_21205).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21201).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(5)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21090).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21095).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(4)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(BROWN_MUSHROOM_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP.configure(new CountChanceDecoratorConfig(8, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(RED_MUSHROOM_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configure(new CountChanceDecoratorConfig(8, 0.125F)))
		);
	}

	public static void addMushroomFieldsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_BOOLEAN_SELECTOR
				.configure(new RandomBooleanFeatureConfig(Feature.HUGE_RED_MUSHROOM.configure(field_21142), Feature.HUGE_BROWN_MUSHROOM.configure(field_21143)))
				.createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(BROWN_MUSHROOM_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP.configure(new CountChanceDecoratorConfig(1, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(RED_MUSHROOM_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configure(new CountChanceDecoratorConfig(1, 0.125F)))
		);
	}

	public static void addPlainsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.FANCY_TREE.configure(FANCY_TREE_WITH_BEEHIVES_CONFIG).withChance(0.33333334F)),
						Feature.NORMAL_TREE.configure(OAK_TREE_WITH_BEEHIVES_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(0, 0.05F, 1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.FLOWER.configure(field_21088).createDecoratedFeature(Decorator.NOISE_HEIGHTMAP_32.configure(new NoiseHeightmapDecoratorConfig(-0.8, 15, 4)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(field_21201)
				.createDecoratedFeature(Decorator.NOISE_HEIGHTMAP_DOUBLE.configure(new NoiseHeightmapDecoratorConfig(-0.8, 5, 10)))
		);
	}

	public static void addDesertDeadBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21090).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(2)))
		);
	}

	public static void addGiantTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21202).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(7)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21090).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(BROWN_MUSHROOM_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP.configure(new CountChanceDecoratorConfig(3, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(RED_MUSHROOM_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configure(new CountChanceDecoratorConfig(3, 0.125F)))
		);
	}

	public static void addDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.FLOWER.configure(field_21206).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(2)))
		);
	}

	public static void addExtraDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.FLOWER.configure(field_21206).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(4)))
		);
	}

	public static void addDefaultGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21201).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1)))
		);
	}

	public static void addTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21202).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(BROWN_MUSHROOM_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP.configure(new CountChanceDecoratorConfig(1, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(RED_MUSHROOM_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configure(new CountChanceDecoratorConfig(1, 0.125F)))
		);
	}

	public static void addPlainsTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH
				.configure(TALL_GRASS_CONFIG)
				.createDecoratedFeature(Decorator.NOISE_HEIGHTMAP_32.configure(new NoiseHeightmapDecoratorConfig(-0.8, 0, 7)))
		);
	}

	public static void addDefaultMushrooms(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new LakeDecoratorConfig(4)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new LakeDecoratorConfig(8)))
		);
	}

	public static void addDefaultVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(10)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21092).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new LakeDecoratorConfig(32)))
		);
	}

	public static void addBadlandsVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(13)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21092).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new LakeDecoratorConfig(32)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(CACTUS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(5)))
		);
	}

	public static void addJungleVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21091).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.VINES.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.COUNT_HEIGHT_64.configure(new CountDecoratorConfig(50)))
		);
	}

	public static void addDesertVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(60)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21092).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new LakeDecoratorConfig(32)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(CACTUS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(10)))
		);
	}

	public static void addSwampVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(20)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_PATCH.configure(field_21092).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new LakeDecoratorConfig(32)))
		);
	}

	public static void addDesertFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.DESERT_WELL.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new LakeDecoratorConfig(1000)))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.FOSSIL.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_PASSTHROUGH.configure(new LakeDecoratorConfig(64)))
		);
	}

	public static void addFossils(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.FOSSIL.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_PASSTHROUGH.configure(new LakeDecoratorConfig(64)))
		);
	}

	public static void addKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.KELP
				.configure(FeatureConfig.DEFAULT)
				.createDecoratedFeature(
					Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(120, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG))
				)
		);
	}

	public static void addSeagrassOnStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SIMPLE_BLOCK
				.configure(new SimpleBlockFeatureConfig(SEAGRASS, new BlockState[]{STONE}, new BlockState[]{WATER}, new BlockState[]{WATER}))
				.createDecoratedFeature(Decorator.CARVING_MASK.configure(new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID, 0.1F)))
		);
	}

	public static void addSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SEAGRASS.configure(new SeagrassFeatureConfig(80, 0.3)).createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addMoreSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SEAGRASS.configure(new SeagrassFeatureConfig(80, 0.8)).createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addLessKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.KELP
				.configure(FeatureConfig.DEFAULT)
				.createDecoratedFeature(
					Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(80, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG))
				)
		);
	}

	public static void addSprings(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SPRING_FEATURE.configure(field_21111).createDecoratedFeature(Decorator.COUNT_BIASED_RANGE.configure(new RangeDecoratorConfig(50, 8, 8, 256)))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SPRING_FEATURE.configure(field_21112).createDecoratedFeature(Decorator.COUNT_VERY_BIASED_RANGE.configure(new RangeDecoratorConfig(20, 8, 16, 256)))
		);
	}

	public static void addIcebergs(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.ICEBERG.configure(new BushFeatureConfig(PACKED_ICE)).createDecoratedFeature(Decorator.ICEBERG.configure(new LakeDecoratorConfig(16)))
		);
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.ICEBERG.configure(new BushFeatureConfig(BLUE_ICE)).createDecoratedFeature(Decorator.ICEBERG.configure(new LakeDecoratorConfig(200)))
		);
	}

	public static void addBlueIce(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.BLUE_ICE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.RANDOM_COUNT_RANGE.configure(new RangeDecoratorConfig(20, 30, 32, 64)))
		);
	}

	public static void addFrozenTopLayer(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.TOP_LAYER_MODIFICATION,
			Feature.FREEZE_TOP_LAYER.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addEndCities(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.END_CITY.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
	}
}
