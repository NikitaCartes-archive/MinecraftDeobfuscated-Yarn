package net.minecraft.block;

import java.util.function.ToIntFunction;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.block.sapling.AcaciaSaplingGenerator;
import net.minecraft.block.sapling.BirchSaplingGenerator;
import net.minecraft.block.sapling.DarkOakSaplingGenerator;
import net.minecraft.block.sapling.JungleSaplingGenerator;
import net.minecraft.block.sapling.OakSaplingGenerator;
import net.minecraft.block.sapling.SpruceSaplingGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

/**
 * Contains all the minecraft blocks.
 */
public class Blocks {
	public static final Block AIR = register("air", new AirBlock(AbstractBlock.Settings.of(Material.AIR).noCollision().dropsNothing().air()));
	public static final Block STONE = register(
		"stone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block GRANITE = register(
		"granite", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.DIRT_BROWN).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_GRANITE = register(
		"polished_granite", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.DIRT_BROWN).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DIORITE = register(
		"diorite", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.OFF_WHITE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_DIORITE = register(
		"polished_diorite", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.OFF_WHITE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block ANDESITE = register(
		"andesite", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_ANDESITE = register(
		"polished_andesite", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block GRASS_BLOCK = register(
		"grass_block", new GrassBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block DIRT = register(
		"dirt", new Block(AbstractBlock.Settings.of(Material.SOIL, MapColor.DIRT_BROWN).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block COARSE_DIRT = register(
		"coarse_dirt", new Block(AbstractBlock.Settings.of(Material.SOIL, MapColor.DIRT_BROWN).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block PODZOL = register(
		"podzol", new SnowyBlock(AbstractBlock.Settings.of(Material.SOIL, MapColor.SPRUCE_BROWN).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block COBBLESTONE = register("cobblestone", new Block(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(2.0F, 6.0F)));
	public static final Block OAK_PLANKS = register(
		"oak_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_PLANKS = register(
		"spruce_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_PLANKS = register(
		"birch_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_PLANKS = register(
		"jungle_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_PLANKS = register(
		"acacia_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.ORANGE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_PLANKS = register(
		"dark_oak_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block MANGROVE_PLANKS = register(
		"mangrove_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.RED).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block OAK_SAPLING = register(
		"oak_sapling",
		new SaplingBlock(
			new OakSaplingGenerator(), AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)
		)
	);
	public static final Block SPRUCE_SAPLING = register(
		"spruce_sapling",
		new SaplingBlock(
			new SpruceSaplingGenerator(), AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)
		)
	);
	public static final Block BIRCH_SAPLING = register(
		"birch_sapling",
		new SaplingBlock(
			new BirchSaplingGenerator(), AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)
		)
	);
	public static final Block JUNGLE_SAPLING = register(
		"jungle_sapling",
		new SaplingBlock(
			new JungleSaplingGenerator(), AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)
		)
	);
	public static final Block ACACIA_SAPLING = register(
		"acacia_sapling",
		new SaplingBlock(
			new AcaciaSaplingGenerator(), AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)
		)
	);
	public static final Block DARK_OAK_SAPLING = register(
		"dark_oak_sapling",
		new SaplingBlock(
			new DarkOakSaplingGenerator(), AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)
		)
	);
	public static final Block MANGROVE_PROPAGULE = register(
		"mangrove_propagule",
		new PropaguleBlock(
			AbstractBlock.Settings.of(Material.PLANT)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block BEDROCK = register(
		"bedrock", new Block(AbstractBlock.Settings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing().allowsSpawning(Blocks::never))
	);
	public static final Block WATER = register(
		"water", new FluidBlock(Fluids.WATER, AbstractBlock.Settings.of(Material.WATER).noCollision().strength(100.0F).dropsNothing())
	);
	public static final Block LAVA = register(
		"lava",
		new FluidBlock(Fluids.LAVA, AbstractBlock.Settings.of(Material.LAVA).noCollision().ticksRandomly().strength(100.0F).luminance(state -> 15).dropsNothing())
	);
	public static final Block SAND = register(
		"sand", new SandBlock(14406560, AbstractBlock.Settings.of(Material.AGGREGATE, MapColor.PALE_YELLOW).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block RED_SAND = register(
		"red_sand", new SandBlock(11098145, AbstractBlock.Settings.of(Material.AGGREGATE, MapColor.ORANGE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block GRAVEL = register(
		"gravel", new GravelBlock(AbstractBlock.Settings.of(Material.AGGREGATE, MapColor.STONE_GRAY).strength(0.6F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block GOLD_ORE = register("gold_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F)));
	public static final Block DEEPSLATE_GOLD_ORE = register(
		"deepslate_gold_ore",
		new OreBlock(AbstractBlock.Settings.copy(GOLD_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE))
	);
	public static final Block IRON_ORE = register("iron_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F)));
	public static final Block DEEPSLATE_IRON_ORE = register(
		"deepslate_iron_ore",
		new OreBlock(AbstractBlock.Settings.copy(IRON_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE))
	);
	public static final Block COAL_ORE = register(
		"coal_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F), UniformIntProvider.create(0, 2))
	);
	public static final Block DEEPSLATE_COAL_ORE = register(
		"deepslate_coal_ore",
		new OreBlock(
			AbstractBlock.Settings.copy(COAL_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
			UniformIntProvider.create(0, 2)
		)
	);
	public static final Block NETHER_GOLD_ORE = register(
		"nether_gold_ore",
		new OreBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.NETHER_GOLD_ORE),
			UniformIntProvider.create(0, 1)
		)
	);
	public static final Block OAK_LOG = register("oak_log", createLogBlock(MapColor.OAK_TAN, MapColor.SPRUCE_BROWN));
	public static final Block SPRUCE_LOG = register("spruce_log", createLogBlock(MapColor.SPRUCE_BROWN, MapColor.BROWN));
	public static final Block BIRCH_LOG = register("birch_log", createLogBlock(MapColor.PALE_YELLOW, MapColor.OFF_WHITE));
	public static final Block JUNGLE_LOG = register("jungle_log", createLogBlock(MapColor.DIRT_BROWN, MapColor.SPRUCE_BROWN));
	public static final Block ACACIA_LOG = register("acacia_log", createLogBlock(MapColor.ORANGE, MapColor.STONE_GRAY));
	public static final Block DARK_OAK_LOG = register("dark_oak_log", createLogBlock(MapColor.BROWN, MapColor.BROWN));
	public static final Block MANGROVE_LOG = register("mangrove_log", createLogBlock(MapColor.RED, MapColor.SPRUCE_BROWN));
	public static final Block MANGROVE_ROOTS = register(
		"mangrove_roots",
		new MangroveRootsBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.SPRUCE_BROWN)
				.strength(0.7F)
				.ticksRandomly()
				.sounds(BlockSoundGroup.MANGROVE_ROOTS)
				.nonOpaque()
				.allowsSpawning(Blocks::canSpawnOnLeaves)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
				.nonOpaque()
		)
	);
	public static final Block MUDDY_MANGROVE_ROOTS = register(
		"muddy_mangrove_roots",
		new PillarBlock(AbstractBlock.Settings.of(Material.SOIL, MapColor.SPRUCE_BROWN).strength(0.7F).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS))
	);
	public static final Block STRIPPED_SPRUCE_LOG = register("stripped_spruce_log", createLogBlock(MapColor.SPRUCE_BROWN, MapColor.SPRUCE_BROWN));
	public static final Block STRIPPED_BIRCH_LOG = register("stripped_birch_log", createLogBlock(MapColor.PALE_YELLOW, MapColor.PALE_YELLOW));
	public static final Block STRIPPED_JUNGLE_LOG = register("stripped_jungle_log", createLogBlock(MapColor.DIRT_BROWN, MapColor.DIRT_BROWN));
	public static final Block STRIPPED_ACACIA_LOG = register("stripped_acacia_log", createLogBlock(MapColor.ORANGE, MapColor.ORANGE));
	public static final Block STRIPPED_DARK_OAK_LOG = register("stripped_dark_oak_log", createLogBlock(MapColor.BROWN, MapColor.BROWN));
	public static final Block STRIPPED_OAK_LOG = register("stripped_oak_log", createLogBlock(MapColor.OAK_TAN, MapColor.OAK_TAN));
	public static final Block STRIPPED_MANGROVE_LOG = register("stripped_mangrove_log", createLogBlock(MapColor.RED, MapColor.RED));
	public static final Block OAK_WOOD = register(
		"oak_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_WOOD = register(
		"spruce_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_WOOD = register(
		"birch_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_WOOD = register(
		"jungle_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_WOOD = register(
		"acacia_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.GRAY).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_WOOD = register(
		"dark_oak_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block MANGROVE_WOOD = register(
		"mangrove_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.RED).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_OAK_WOOD = register(
		"stripped_oak_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_SPRUCE_WOOD = register(
		"stripped_spruce_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_BIRCH_WOOD = register(
		"stripped_birch_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_JUNGLE_WOOD = register(
		"stripped_jungle_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_ACACIA_WOOD = register(
		"stripped_acacia_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.ORANGE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_DARK_OAK_WOOD = register(
		"stripped_dark_oak_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_MANGROVE_WOOD = register("stripped_mangrove_wood", createLogBlock(MapColor.RED, MapColor.RED));
	public static final Block OAK_LEAVES = register("oak_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block SPRUCE_LEAVES = register("spruce_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block BIRCH_LEAVES = register("birch_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block JUNGLE_LEAVES = register("jungle_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block ACACIA_LEAVES = register("acacia_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block DARK_OAK_LEAVES = register("dark_oak_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block MANGROVE_LEAVES = register(
		"mangrove_leaves",
		new MangroveLeavesBlock(
			AbstractBlock.Settings.of(Material.LEAVES)
				.strength(0.2F)
				.ticksRandomly()
				.sounds(BlockSoundGroup.GRASS)
				.nonOpaque()
				.allowsSpawning(Blocks::canSpawnOnLeaves)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		)
	);
	public static final Block AZALEA_LEAVES = register("azalea_leaves", createLeavesBlock(BlockSoundGroup.AZALEA_LEAVES));
	public static final Block FLOWERING_AZALEA_LEAVES = register("flowering_azalea_leaves", createLeavesBlock(BlockSoundGroup.AZALEA_LEAVES));
	public static final Block SPONGE = register("sponge", new SpongeBlock(AbstractBlock.Settings.of(Material.SPONGE).strength(0.6F).sounds(BlockSoundGroup.GRASS)));
	public static final Block WET_SPONGE = register(
		"wet_sponge", new WetSpongeBlock(AbstractBlock.Settings.of(Material.SPONGE).strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block GLASS = register(
		"glass",
		new GlassBlock(
			AbstractBlock.Settings.of(Material.GLASS)
				.strength(0.3F)
				.sounds(BlockSoundGroup.GLASS)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.solidBlock(Blocks::never)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		)
	);
	public static final Block LAPIS_ORE = register(
		"lapis_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F), UniformIntProvider.create(2, 5))
	);
	public static final Block DEEPSLATE_LAPIS_ORE = register(
		"deepslate_lapis_ore",
		new OreBlock(
			AbstractBlock.Settings.copy(LAPIS_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
			UniformIntProvider.create(2, 5)
		)
	);
	public static final Block LAPIS_BLOCK = register(
		"lapis_block", new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.LAPIS_BLUE).requiresTool().strength(3.0F, 3.0F))
	);
	public static final Block DISPENSER = register("dispenser", new DispenserBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F)));
	public static final Block SANDSTONE = register(
		"sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().strength(0.8F))
	);
	public static final Block CHISELED_SANDSTONE = register(
		"chiseled_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().strength(0.8F))
	);
	public static final Block CUT_SANDSTONE = register(
		"cut_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().strength(0.8F))
	);
	public static final Block NOTE_BLOCK = register(
		"note_block", new NoteBlock(AbstractBlock.Settings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(0.8F))
	);
	public static final Block WHITE_BED = register("white_bed", createBedBlock(DyeColor.WHITE));
	public static final Block ORANGE_BED = register("orange_bed", createBedBlock(DyeColor.ORANGE));
	public static final Block MAGENTA_BED = register("magenta_bed", createBedBlock(DyeColor.MAGENTA));
	public static final Block LIGHT_BLUE_BED = register("light_blue_bed", createBedBlock(DyeColor.LIGHT_BLUE));
	public static final Block YELLOW_BED = register("yellow_bed", createBedBlock(DyeColor.YELLOW));
	public static final Block LIME_BED = register("lime_bed", createBedBlock(DyeColor.LIME));
	public static final Block PINK_BED = register("pink_bed", createBedBlock(DyeColor.PINK));
	public static final Block GRAY_BED = register("gray_bed", createBedBlock(DyeColor.GRAY));
	public static final Block LIGHT_GRAY_BED = register("light_gray_bed", createBedBlock(DyeColor.LIGHT_GRAY));
	public static final Block CYAN_BED = register("cyan_bed", createBedBlock(DyeColor.CYAN));
	public static final Block PURPLE_BED = register("purple_bed", createBedBlock(DyeColor.PURPLE));
	public static final Block BLUE_BED = register("blue_bed", createBedBlock(DyeColor.BLUE));
	public static final Block BROWN_BED = register("brown_bed", createBedBlock(DyeColor.BROWN));
	public static final Block GREEN_BED = register("green_bed", createBedBlock(DyeColor.GREEN));
	public static final Block RED_BED = register("red_bed", createBedBlock(DyeColor.RED));
	public static final Block BLACK_BED = register("black_bed", createBedBlock(DyeColor.BLACK));
	public static final Block POWERED_RAIL = register(
		"powered_rail", new PoweredRailBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block DETECTOR_RAIL = register(
		"detector_rail", new DetectorRailBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block STICKY_PISTON = register("sticky_piston", createPistonBlock(true));
	public static final Block COBWEB = register("cobweb", new CobwebBlock(AbstractBlock.Settings.of(Material.COBWEB).noCollision().requiresTool().strength(4.0F)));
	public static final Block GRASS = register(
		"grass",
		new FernBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XYZ)
		)
	);
	public static final Block FERN = register(
		"fern",
		new FernBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XYZ)
		)
	);
	public static final Block DEAD_BUSH = register(
		"dead_bush",
		new DeadBushBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT, MapColor.OAK_TAN).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block SEAGRASS = register(
		"seagrass",
		new SeagrassBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_UNDERWATER_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block TALL_SEAGRASS = register(
		"tall_seagrass",
		new TallSeagrassBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_UNDERWATER_PLANT)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block PISTON = register("piston", createPistonBlock(false));
	public static final Block PISTON_HEAD = register("piston_head", new PistonHeadBlock(AbstractBlock.Settings.of(Material.PISTON).strength(1.5F).dropsNothing()));
	public static final Block WHITE_WOOL = register(
		"white_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.WHITE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block ORANGE_WOOL = register(
		"orange_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.ORANGE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block MAGENTA_WOOL = register(
		"magenta_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.MAGENTA).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIGHT_BLUE_WOOL = register(
		"light_blue_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.LIGHT_BLUE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block YELLOW_WOOL = register(
		"yellow_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.YELLOW).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIME_WOOL = register(
		"lime_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.LIME).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block PINK_WOOL = register(
		"pink_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.PINK).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block GRAY_WOOL = register(
		"gray_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.GRAY).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIGHT_GRAY_WOOL = register(
		"light_gray_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.LIGHT_GRAY).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block CYAN_WOOL = register(
		"cyan_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.CYAN).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block PURPLE_WOOL = register(
		"purple_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.PURPLE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BLUE_WOOL = register(
		"blue_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.BLUE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BROWN_WOOL = register(
		"brown_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.BROWN).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block GREEN_WOOL = register(
		"green_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.GREEN).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block RED_WOOL = register(
		"red_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.RED).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BLACK_WOOL = register(
		"black_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MapColor.BLACK).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block MOVING_PISTON = register(
		"moving_piston",
		new PistonExtensionBlock(
			AbstractBlock.Settings.of(Material.PISTON)
				.strength(-1.0F)
				.dynamicBounds()
				.dropsNothing()
				.nonOpaque()
				.solidBlock(Blocks::never)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		)
	);
	public static final Block DANDELION = register(
		"dandelion",
		new FlowerBlock(
			StatusEffects.SATURATION,
			7,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block POPPY = register(
		"poppy",
		new FlowerBlock(
			StatusEffects.NIGHT_VISION,
			5,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block BLUE_ORCHID = register(
		"blue_orchid",
		new FlowerBlock(
			StatusEffects.SATURATION,
			7,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block ALLIUM = register(
		"allium",
		new FlowerBlock(
			StatusEffects.FIRE_RESISTANCE,
			4,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block AZURE_BLUET = register(
		"azure_bluet",
		new FlowerBlock(
			StatusEffects.BLINDNESS,
			8,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block RED_TULIP = register(
		"red_tulip",
		new FlowerBlock(
			StatusEffects.WEAKNESS,
			9,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block ORANGE_TULIP = register(
		"orange_tulip",
		new FlowerBlock(
			StatusEffects.WEAKNESS,
			9,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block WHITE_TULIP = register(
		"white_tulip",
		new FlowerBlock(
			StatusEffects.WEAKNESS,
			9,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block PINK_TULIP = register(
		"pink_tulip",
		new FlowerBlock(
			StatusEffects.WEAKNESS,
			9,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block OXEYE_DAISY = register(
		"oxeye_daisy",
		new FlowerBlock(
			StatusEffects.REGENERATION,
			8,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block CORNFLOWER = register(
		"cornflower",
		new FlowerBlock(
			StatusEffects.JUMP_BOOST,
			6,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block WITHER_ROSE = register(
		"wither_rose",
		new WitherRoseBlock(
			StatusEffects.WITHER,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block LILY_OF_THE_VALLEY = register(
		"lily_of_the_valley",
		new FlowerBlock(
			StatusEffects.POISON,
			12,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block BROWN_MUSHROOM = register(
		"brown_mushroom",
		new MushroomPlantBlock(
			AbstractBlock.Settings.of(Material.PLANT, MapColor.BROWN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.luminance(state -> 1)
				.postProcess(Blocks::always),
			() -> TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM
		)
	);
	public static final Block RED_MUSHROOM = register(
		"red_mushroom",
		new MushroomPlantBlock(
			AbstractBlock.Settings.of(Material.PLANT, MapColor.RED)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.postProcess(Blocks::always),
			() -> TreeConfiguredFeatures.HUGE_RED_MUSHROOM
		)
	);
	public static final Block GOLD_BLOCK = register(
		"gold_block", new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.GOLD).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block IRON_BLOCK = register(
		"iron_block", new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block BRICKS = register("bricks", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.RED).requiresTool().strength(2.0F, 6.0F)));
	public static final Block TNT = register("tnt", new TntBlock(AbstractBlock.Settings.of(Material.TNT).breakInstantly().sounds(BlockSoundGroup.GRASS)));
	public static final Block BOOKSHELF = register("bookshelf", new Block(AbstractBlock.Settings.of(Material.WOOD).strength(1.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block MOSSY_COBBLESTONE = register(
		"mossy_cobblestone", new Block(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block OBSIDIAN = register(
		"obsidian", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(50.0F, 1200.0F))
	);
	public static final Block TORCH = register(
		"torch",
		new TorchBlock(
			AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly().luminance(state -> 14).sounds(BlockSoundGroup.WOOD), ParticleTypes.FLAME
		)
	);
	public static final Block WALL_TORCH = register(
		"wall_torch",
		new WallTorchBlock(
			AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly().luminance(state -> 14).sounds(BlockSoundGroup.WOOD).dropsLike(TORCH),
			ParticleTypes.FLAME
		)
	);
	public static final Block FIRE = register(
		"fire",
		new FireBlock(
			AbstractBlock.Settings.of(Material.FIRE, MapColor.BRIGHT_RED).noCollision().breakInstantly().luminance(state -> 15).sounds(BlockSoundGroup.WOOL)
		)
	);
	public static final Block SOUL_FIRE = register(
		"soul_fire",
		new SoulFireBlock(
			AbstractBlock.Settings.of(Material.FIRE, MapColor.LIGHT_BLUE).noCollision().breakInstantly().luminance(state -> 10).sounds(BlockSoundGroup.WOOL)
		)
	);
	public static final Block SPAWNER = register(
		"spawner", new SpawnerBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block OAK_STAIRS = register("oak_stairs", new StairsBlock(OAK_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(OAK_PLANKS)));
	public static final Block CHEST = register(
		"chest", new ChestBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD), () -> BlockEntityType.CHEST)
	);
	public static final Block REDSTONE_WIRE = register(
		"redstone_wire", new RedstoneWireBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly())
	);
	public static final Block DIAMOND_ORE = register(
		"diamond_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F), UniformIntProvider.create(3, 7))
	);
	public static final Block DEEPSLATE_DIAMOND_ORE = register(
		"deepslate_diamond_ore",
		new OreBlock(
			AbstractBlock.Settings.copy(DIAMOND_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
			UniformIntProvider.create(3, 7)
		)
	);
	public static final Block DIAMOND_BLOCK = register(
		"diamond_block",
		new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.DIAMOND_BLUE).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block CRAFTING_TABLE = register(
		"crafting_table", new CraftingTableBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WHEAT = register(
		"wheat", new CropBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block FARMLAND = register(
		"farmland",
		new FarmlandBlock(
			AbstractBlock.Settings.of(Material.SOIL)
				.ticksRandomly()
				.strength(0.6F)
				.sounds(BlockSoundGroup.GRAVEL)
				.blockVision(Blocks::always)
				.suffocates(Blocks::always)
		)
	);
	public static final Block FURNACE = register(
		"furnace", new FurnaceBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).luminance(createLightLevelFromLitBlockState(13)))
	);
	public static final Block OAK_SIGN = register(
		"oak_sign", new SignBlock(AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.OAK)
	);
	public static final Block SPRUCE_SIGN = register(
		"spruce_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.WOOD, SPRUCE_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.SPRUCE
		)
	);
	public static final Block BIRCH_SIGN = register(
		"birch_sign",
		new SignBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.PALE_YELLOW).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.BIRCH)
	);
	public static final Block ACACIA_SIGN = register(
		"acacia_sign",
		new SignBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.ORANGE).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.ACACIA)
	);
	public static final Block JUNGLE_SIGN = register(
		"jungle_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.WOOD, JUNGLE_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.JUNGLE
		)
	);
	public static final Block DARK_OAK_SIGN = register(
		"dark_oak_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.DARK_OAK
		)
	);
	public static final Block MANGROVE_SIGN = register(
		"mangrove_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.WOOD, MANGROVE_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.MANGROVE
		)
	);
	public static final Block OAK_DOOR = register(
		"oak_door", new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, OAK_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block LADDER = register(
		"ladder", new LadderBlock(AbstractBlock.Settings.of(Material.DECORATION).strength(0.4F).sounds(BlockSoundGroup.LADDER).nonOpaque())
	);
	public static final Block RAIL = register(
		"rail", new RailBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block COBBLESTONE_STAIRS = register(
		"cobblestone_stairs", new StairsBlock(COBBLESTONE.getDefaultState(), AbstractBlock.Settings.copy(COBBLESTONE))
	);
	public static final Block OAK_WALL_SIGN = register(
		"oak_wall_sign",
		new WallSignBlock(AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(OAK_SIGN), SignType.OAK)
	);
	public static final Block SPRUCE_WALL_SIGN = register(
		"spruce_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, SPRUCE_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(SPRUCE_SIGN),
			SignType.SPRUCE
		)
	);
	public static final Block BIRCH_WALL_SIGN = register(
		"birch_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.PALE_YELLOW).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(BIRCH_SIGN),
			SignType.BIRCH
		)
	);
	public static final Block ACACIA_WALL_SIGN = register(
		"acacia_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.ORANGE).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(ACACIA_SIGN), SignType.ACACIA
		)
	);
	public static final Block JUNGLE_WALL_SIGN = register(
		"jungle_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, JUNGLE_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(JUNGLE_SIGN),
			SignType.JUNGLE
		)
	);
	public static final Block DARK_OAK_WALL_SIGN = register(
		"dark_oak_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_LOG.getDefaultMapColor())
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(DARK_OAK_SIGN),
			SignType.DARK_OAK
		)
	);
	public static final Block MANGROVE_WALL_SIGN = register(
		"mangrove_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, MANGROVE_LOG.getDefaultMapColor())
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(MANGROVE_SIGN),
			SignType.MANGROVE
		)
	);
	public static final Block LEVER = register(
		"lever", new LeverBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STONE_PRESSURE_PLATE = register(
		"stone_pressure_plate",
		new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.STONE).requiresTool().noCollision().strength(0.5F))
	);
	public static final Block IRON_DOOR = register(
		"iron_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block OAK_PRESSURE_PLATE = register(
		"oak_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, OAK_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block SPRUCE_PRESSURE_PLATE = register(
		"spruce_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, SPRUCE_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block BIRCH_PRESSURE_PLATE = register(
		"birch_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, BIRCH_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block JUNGLE_PRESSURE_PLATE = register(
		"jungle_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, JUNGLE_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block ACACIA_PRESSURE_PLATE = register(
		"acacia_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, ACACIA_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block DARK_OAK_PRESSURE_PLATE = register(
		"dark_oak_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block MANGROVE_PRESSURE_PLATE = register(
		"mangrove_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, MANGROVE_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block REDSTONE_ORE = register(
		"redstone_ore",
		new RedstoneOreBlock(
			AbstractBlock.Settings.of(Material.STONE).requiresTool().ticksRandomly().luminance(createLightLevelFromLitBlockState(9)).strength(3.0F, 3.0F)
		)
	);
	public static final Block DEEPSLATE_REDSTONE_ORE = register(
		"deepslate_redstone_ore",
		new RedstoneOreBlock(AbstractBlock.Settings.copy(REDSTONE_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE))
	);
	public static final Block REDSTONE_TORCH = register(
		"redstone_torch",
		new RedstoneTorchBlock(
			AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly().luminance(createLightLevelFromLitBlockState(7)).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block REDSTONE_WALL_TORCH = register(
		"redstone_wall_torch",
		new WallRedstoneTorchBlock(
			AbstractBlock.Settings.of(Material.DECORATION)
				.noCollision()
				.breakInstantly()
				.luminance(createLightLevelFromLitBlockState(7))
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(REDSTONE_TORCH)
		)
	);
	public static final Block STONE_BUTTON = register(
		"stone_button", new StoneButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F))
	);
	public static final Block SNOW = register(
		"snow",
		new SnowBlock(
			AbstractBlock.Settings.of(Material.SNOW_LAYER)
				.ticksRandomly()
				.strength(0.1F)
				.requiresTool()
				.sounds(BlockSoundGroup.SNOW)
				.blockVision((state, world, pos) -> (Integer)state.get(SnowBlock.LAYERS) >= 8)
		)
	);
	public static final Block ICE = register(
		"ice",
		new IceBlock(
			AbstractBlock.Settings.of(Material.ICE)
				.slipperiness(0.98F)
				.ticksRandomly()
				.strength(0.5F)
				.sounds(BlockSoundGroup.GLASS)
				.nonOpaque()
				.allowsSpawning((state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR)
		)
	);
	public static final Block SNOW_BLOCK = register(
		"snow_block", new Block(AbstractBlock.Settings.of(Material.SNOW_BLOCK).requiresTool().strength(0.2F).sounds(BlockSoundGroup.SNOW))
	);
	public static final Block CACTUS = register(
		"cactus", new CactusBlock(AbstractBlock.Settings.of(Material.CACTUS).ticksRandomly().strength(0.4F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block CLAY = register("clay", new Block(AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0.6F).sounds(BlockSoundGroup.GRAVEL)));
	public static final Block SUGAR_CANE = register(
		"sugar_cane", new SugarCaneBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block JUKEBOX = register("jukebox", new JukeboxBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(2.0F, 6.0F)));
	public static final Block OAK_FENCE = register(
		"oak_fence", new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, OAK_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block PUMPKIN = register(
		"pumpkin", new PumpkinBlock(AbstractBlock.Settings.of(Material.GOURD, MapColor.ORANGE).strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block NETHERRACK = register(
		"netherrack",
		new NetherrackBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(0.4F).sounds(BlockSoundGroup.NETHERRACK))
	);
	public static final Block SOUL_SAND = register(
		"soul_sand",
		new SoulSandBlock(
			AbstractBlock.Settings.of(Material.AGGREGATE, MapColor.BROWN)
				.strength(0.5F)
				.velocityMultiplier(0.4F)
				.sounds(BlockSoundGroup.SOUL_SAND)
				.allowsSpawning(Blocks::always)
				.solidBlock(Blocks::always)
				.blockVision(Blocks::always)
				.suffocates(Blocks::always)
		)
	);
	public static final Block SOUL_SOIL = register(
		"soul_soil", new Block(AbstractBlock.Settings.of(Material.SOIL, MapColor.BROWN).strength(0.5F).sounds(BlockSoundGroup.SOUL_SOIL))
	);
	public static final Block BASALT = register(
		"basalt", new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(1.25F, 4.2F).sounds(BlockSoundGroup.BASALT))
	);
	public static final Block POLISHED_BASALT = register(
		"polished_basalt",
		new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(1.25F, 4.2F).sounds(BlockSoundGroup.BASALT))
	);
	public static final Block SOUL_TORCH = register(
		"soul_torch",
		new TorchBlock(
			AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly().luminance(state -> 10).sounds(BlockSoundGroup.WOOD),
			ParticleTypes.SOUL_FIRE_FLAME
		)
	);
	public static final Block SOUL_WALL_TORCH = register(
		"soul_wall_torch",
		new WallTorchBlock(
			AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly().luminance(state -> 10).sounds(BlockSoundGroup.WOOD).dropsLike(SOUL_TORCH),
			ParticleTypes.SOUL_FIRE_FLAME
		)
	);
	public static final Block GLOWSTONE = register(
		"glowstone", new Block(AbstractBlock.Settings.of(Material.GLASS, MapColor.PALE_YELLOW).strength(0.3F).sounds(BlockSoundGroup.GLASS).luminance(state -> 15))
	);
	public static final Block NETHER_PORTAL = register(
		"nether_portal",
		new NetherPortalBlock(
			AbstractBlock.Settings.of(Material.PORTAL).noCollision().ticksRandomly().strength(-1.0F).sounds(BlockSoundGroup.GLASS).luminance(state -> 11)
		)
	);
	public static final Block CARVED_PUMPKIN = register(
		"carved_pumpkin",
		new CarvedPumpkinBlock(AbstractBlock.Settings.of(Material.GOURD, MapColor.ORANGE).strength(1.0F).sounds(BlockSoundGroup.WOOD).allowsSpawning(Blocks::always))
	);
	public static final Block JACK_O_LANTERN = register(
		"jack_o_lantern",
		new CarvedPumpkinBlock(
			AbstractBlock.Settings.of(Material.GOURD, MapColor.ORANGE).strength(1.0F).sounds(BlockSoundGroup.WOOD).luminance(state -> 15).allowsSpawning(Blocks::always)
		)
	);
	public static final Block CAKE = register("cake", new CakeBlock(AbstractBlock.Settings.of(Material.CAKE).strength(0.5F).sounds(BlockSoundGroup.WOOL)));
	public static final Block REPEATER = register(
		"repeater", new RepeaterBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WHITE_STAINED_GLASS = register("white_stained_glass", createStainedGlassBlock(DyeColor.WHITE));
	public static final Block ORANGE_STAINED_GLASS = register("orange_stained_glass", createStainedGlassBlock(DyeColor.ORANGE));
	public static final Block MAGENTA_STAINED_GLASS = register("magenta_stained_glass", createStainedGlassBlock(DyeColor.MAGENTA));
	public static final Block LIGHT_BLUE_STAINED_GLASS = register("light_blue_stained_glass", createStainedGlassBlock(DyeColor.LIGHT_BLUE));
	public static final Block YELLOW_STAINED_GLASS = register("yellow_stained_glass", createStainedGlassBlock(DyeColor.YELLOW));
	public static final Block LIME_STAINED_GLASS = register("lime_stained_glass", createStainedGlassBlock(DyeColor.LIME));
	public static final Block PINK_STAINED_GLASS = register("pink_stained_glass", createStainedGlassBlock(DyeColor.PINK));
	public static final Block GRAY_STAINED_GLASS = register("gray_stained_glass", createStainedGlassBlock(DyeColor.GRAY));
	public static final Block LIGHT_GRAY_STAINED_GLASS = register("light_gray_stained_glass", createStainedGlassBlock(DyeColor.LIGHT_GRAY));
	public static final Block CYAN_STAINED_GLASS = register("cyan_stained_glass", createStainedGlassBlock(DyeColor.CYAN));
	public static final Block PURPLE_STAINED_GLASS = register("purple_stained_glass", createStainedGlassBlock(DyeColor.PURPLE));
	public static final Block BLUE_STAINED_GLASS = register("blue_stained_glass", createStainedGlassBlock(DyeColor.BLUE));
	public static final Block BROWN_STAINED_GLASS = register("brown_stained_glass", createStainedGlassBlock(DyeColor.BROWN));
	public static final Block GREEN_STAINED_GLASS = register("green_stained_glass", createStainedGlassBlock(DyeColor.GREEN));
	public static final Block RED_STAINED_GLASS = register("red_stained_glass", createStainedGlassBlock(DyeColor.RED));
	public static final Block BLACK_STAINED_GLASS = register("black_stained_glass", createStainedGlassBlock(DyeColor.BLACK));
	public static final Block OAK_TRAPDOOR = register(
		"oak_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block SPRUCE_TRAPDOOR = register(
		"spruce_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block BIRCH_TRAPDOOR = register(
		"birch_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block JUNGLE_TRAPDOOR = register(
		"jungle_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block ACACIA_TRAPDOOR = register(
		"acacia_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.ORANGE).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block DARK_OAK_TRAPDOOR = register(
		"dark_oak_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block MANGROVE_TRAPDOOR = register(
		"mangrove_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MapColor.RED).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block STONE_BRICKS = register("stone_bricks", new Block(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(1.5F, 6.0F)));
	public static final Block MOSSY_STONE_BRICKS = register(
		"mossy_stone_bricks", new Block(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block CRACKED_STONE_BRICKS = register(
		"cracked_stone_bricks", new Block(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block CHISELED_STONE_BRICKS = register(
		"chiseled_stone_bricks", new Block(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PACKED_MUD = register(
		"packed_mud", new Block(AbstractBlock.Settings.copy(DIRT).strength(1.0F, 3.0F).sounds(BlockSoundGroup.PACKED_MUD))
	);
	public static final Block MUD_BRICKS = register(
		"mud_bricks",
		new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_LIGHT_GRAY).requiresTool().strength(1.5F, 3.0F).sounds(BlockSoundGroup.MUD_BRICKS))
	);
	public static final Block INFESTED_STONE = register("infested_stone", new InfestedBlock(STONE, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT)));
	public static final Block INFESTED_COBBLESTONE = register(
		"infested_cobblestone", new InfestedBlock(COBBLESTONE, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT))
	);
	public static final Block INFESTED_STONE_BRICKS = register(
		"infested_stone_bricks", new InfestedBlock(STONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT))
	);
	public static final Block INFESTED_MOSSY_STONE_BRICKS = register(
		"infested_mossy_stone_bricks", new InfestedBlock(MOSSY_STONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT))
	);
	public static final Block INFESTED_CRACKED_STONE_BRICKS = register(
		"infested_cracked_stone_bricks", new InfestedBlock(CRACKED_STONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT))
	);
	public static final Block INFESTED_CHISELED_STONE_BRICKS = register(
		"infested_chiseled_stone_bricks", new InfestedBlock(CHISELED_STONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT))
	);
	public static final Block BROWN_MUSHROOM_BLOCK = register(
		"brown_mushroom_block", new MushroomBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block RED_MUSHROOM_BLOCK = register(
		"red_mushroom_block", new MushroomBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.RED).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block MUSHROOM_STEM = register(
		"mushroom_stem", new MushroomBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.WHITE_GRAY).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block IRON_BARS = register(
		"iron_bars",
		new PaneBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.CLEAR).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block CHAIN = register(
		"chain",
		new ChainBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.CLEAR).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.CHAIN).nonOpaque())
	);
	public static final Block GLASS_PANE = register(
		"glass_pane", new PaneBlock(AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block MELON = register(
		"melon", new MelonBlock(AbstractBlock.Settings.of(Material.GOURD, MapColor.LIME).strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ATTACHED_PUMPKIN_STEM = register(
		"attached_pumpkin_stem",
		new AttachedStemBlock(
			(GourdBlock)PUMPKIN, () -> Items.PUMPKIN_SEEDS, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block ATTACHED_MELON_STEM = register(
		"attached_melon_stem",
		new AttachedStemBlock(
			(GourdBlock)MELON, () -> Items.MELON_SEEDS, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block PUMPKIN_STEM = register(
		"pumpkin_stem",
		new StemBlock(
			(GourdBlock)PUMPKIN,
			() -> Items.PUMPKIN_SEEDS,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.STEM)
		)
	);
	public static final Block MELON_STEM = register(
		"melon_stem",
		new StemBlock(
			(GourdBlock)MELON,
			() -> Items.MELON_SEEDS,
			AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.STEM)
		)
	);
	public static final Block VINE = register(
		"vine", new VineBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().ticksRandomly().strength(0.2F).sounds(BlockSoundGroup.VINE))
	);
	public static final Block GLOW_LICHEN = register(
		"glow_lichen",
		new GlowLichenBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT, MapColor.LICHEN_GREEN)
				.noCollision()
				.strength(0.2F)
				.sounds(BlockSoundGroup.GLOW_LICHEN)
				.luminance(GlowLichenBlock.getLuminanceSupplier(7))
		)
	);
	public static final Block OAK_FENCE_GATE = register(
		"oak_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, OAK_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BRICK_STAIRS = register("brick_stairs", new StairsBlock(BRICKS.getDefaultState(), AbstractBlock.Settings.copy(BRICKS)));
	public static final Block STONE_BRICK_STAIRS = register(
		"stone_brick_stairs", new StairsBlock(STONE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(STONE_BRICKS))
	);
	public static final Block MUD_BRICK_STAIRS = register(
		"mud_brick_stairs", new StairsBlock(MUD_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(MUD_BRICKS))
	);
	public static final Block MYCELIUM = register(
		"mycelium",
		new MyceliumBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MapColor.PURPLE).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block LILY_PAD = register(
		"lily_pad", new LilyPadBlock(AbstractBlock.Settings.of(Material.PLANT).breakInstantly().sounds(BlockSoundGroup.LILY_PAD).nonOpaque())
	);
	public static final Block NETHER_BRICKS = register(
		"nether_bricks",
		new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block NETHER_BRICK_FENCE = register(
		"nether_brick_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block NETHER_BRICK_STAIRS = register(
		"nether_brick_stairs", new StairsBlock(NETHER_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(NETHER_BRICKS))
	);
	public static final Block NETHER_WART = register(
		"nether_wart", new NetherWartBlock(AbstractBlock.Settings.of(Material.PLANT, MapColor.RED).noCollision().ticksRandomly().sounds(BlockSoundGroup.NETHER_WART))
	);
	public static final Block ENCHANTING_TABLE = register(
		"enchanting_table",
		new EnchantingTableBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.RED).requiresTool().luminance(state -> 7).strength(5.0F, 1200.0F))
	);
	public static final Block BREWING_STAND = register(
		"brewing_stand", new BrewingStandBlock(AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(0.5F).luminance(state -> 1).nonOpaque())
	);
	public static final Block CAULDRON = register(
		"cauldron", new CauldronBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.STONE_GRAY).requiresTool().strength(2.0F).nonOpaque())
	);
	public static final Block WATER_CAULDRON = register(
		"water_cauldron",
		new LeveledCauldronBlock(AbstractBlock.Settings.copy(CAULDRON), LeveledCauldronBlock.RAIN_PREDICATE, CauldronBehavior.WATER_CAULDRON_BEHAVIOR)
	);
	public static final Block LAVA_CAULDRON = register("lava_cauldron", new LavaCauldronBlock(AbstractBlock.Settings.copy(CAULDRON).luminance(state -> 15)));
	public static final Block POWDER_SNOW_CAULDRON = register(
		"powder_snow_cauldron",
		new PowderSnowCauldronBlock(AbstractBlock.Settings.copy(CAULDRON), LeveledCauldronBlock.SNOW_PREDICATE, CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR)
	);
	public static final Block END_PORTAL = register(
		"end_portal",
		new EndPortalBlock(AbstractBlock.Settings.of(Material.PORTAL, MapColor.BLACK).noCollision().luminance(state -> 15).strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block END_PORTAL_FRAME = register(
		"end_portal_frame",
		new EndPortalFrameBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.GREEN).sounds(BlockSoundGroup.GLASS).luminance(state -> 1).strength(-1.0F, 3600000.0F).dropsNothing()
		)
	);
	public static final Block END_STONE = register(
		"end_stone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().strength(3.0F, 9.0F))
	);
	public static final Block DRAGON_EGG = register(
		"dragon_egg", new DragonEggBlock(AbstractBlock.Settings.of(Material.EGG, MapColor.BLACK).strength(3.0F, 9.0F).luminance(state -> 1).nonOpaque())
	);
	public static final Block REDSTONE_LAMP = register(
		"redstone_lamp",
		new RedstoneLampBlock(
			AbstractBlock.Settings.of(Material.REDSTONE_LAMP)
				.luminance(createLightLevelFromLitBlockState(15))
				.strength(0.3F)
				.sounds(BlockSoundGroup.GLASS)
				.allowsSpawning(Blocks::always)
		)
	);
	public static final Block COCOA = register(
		"cocoa", new CocoaBlock(AbstractBlock.Settings.of(Material.PLANT).ticksRandomly().strength(0.2F, 3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block SANDSTONE_STAIRS = register("sandstone_stairs", new StairsBlock(SANDSTONE.getDefaultState(), AbstractBlock.Settings.copy(SANDSTONE)));
	public static final Block EMERALD_ORE = register(
		"emerald_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F), UniformIntProvider.create(3, 7))
	);
	public static final Block DEEPSLATE_EMERALD_ORE = register(
		"deepslate_emerald_ore",
		new OreBlock(
			AbstractBlock.Settings.copy(EMERALD_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
			UniformIntProvider.create(3, 7)
		)
	);
	public static final Block ENDER_CHEST = register(
		"ender_chest", new EnderChestBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(22.5F, 600.0F).luminance(state -> 7))
	);
	public static final Block TRIPWIRE_HOOK = register("tripwire_hook", new TripwireHookBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision()));
	public static final Block TRIPWIRE = register(
		"tripwire", new TripwireBlock((TripwireHookBlock)TRIPWIRE_HOOK, AbstractBlock.Settings.of(Material.DECORATION).noCollision())
	);
	public static final Block EMERALD_BLOCK = register(
		"emerald_block",
		new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.EMERALD_GREEN).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block SPRUCE_STAIRS = register(
		"spruce_stairs", new StairsBlock(SPRUCE_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(SPRUCE_PLANKS))
	);
	public static final Block BIRCH_STAIRS = register("birch_stairs", new StairsBlock(BIRCH_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(BIRCH_PLANKS)));
	public static final Block JUNGLE_STAIRS = register(
		"jungle_stairs", new StairsBlock(JUNGLE_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(JUNGLE_PLANKS))
	);
	public static final Block COMMAND_BLOCK = register(
		"command_block", new CommandBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.BROWN).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing(), false)
	);
	public static final Block BEACON = register(
		"beacon",
		new BeaconBlock(AbstractBlock.Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE).strength(3.0F).luminance(state -> 15).nonOpaque().solidBlock(Blocks::never))
	);
	public static final Block COBBLESTONE_WALL = register("cobblestone_wall", new WallBlock(AbstractBlock.Settings.copy(COBBLESTONE)));
	public static final Block MOSSY_COBBLESTONE_WALL = register("mossy_cobblestone_wall", new WallBlock(AbstractBlock.Settings.copy(COBBLESTONE)));
	public static final Block FLOWER_POT = register(
		"flower_pot", new FlowerPotBlock(AIR, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_OAK_SAPLING = register(
		"potted_oak_sapling", new FlowerPotBlock(OAK_SAPLING, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_SPRUCE_SAPLING = register(
		"potted_spruce_sapling", new FlowerPotBlock(SPRUCE_SAPLING, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_BIRCH_SAPLING = register(
		"potted_birch_sapling", new FlowerPotBlock(BIRCH_SAPLING, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_JUNGLE_SAPLING = register(
		"potted_jungle_sapling", new FlowerPotBlock(JUNGLE_SAPLING, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_ACACIA_SAPLING = register(
		"potted_acacia_sapling", new FlowerPotBlock(ACACIA_SAPLING, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_DARK_OAK_SAPLING = register(
		"potted_dark_oak_sapling", new FlowerPotBlock(DARK_OAK_SAPLING, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_MANGROVE_PROPAGULE = register(
		"potted_mangrove_propagule", new FlowerPotBlock(MANGROVE_PROPAGULE, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_FERN = register(
		"potted_fern", new FlowerPotBlock(FERN, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_DANDELION = register(
		"potted_dandelion", new FlowerPotBlock(DANDELION, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_POPPY = register(
		"potted_poppy", new FlowerPotBlock(POPPY, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_BLUE_ORCHID = register(
		"potted_blue_orchid", new FlowerPotBlock(BLUE_ORCHID, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_ALLIUM = register(
		"potted_allium", new FlowerPotBlock(ALLIUM, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_AZURE_BLUET = register(
		"potted_azure_bluet", new FlowerPotBlock(AZURE_BLUET, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_RED_TULIP = register(
		"potted_red_tulip", new FlowerPotBlock(RED_TULIP, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_ORANGE_TULIP = register(
		"potted_orange_tulip", new FlowerPotBlock(ORANGE_TULIP, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_WHITE_TULIP = register(
		"potted_white_tulip", new FlowerPotBlock(WHITE_TULIP, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_PINK_TULIP = register(
		"potted_pink_tulip", new FlowerPotBlock(PINK_TULIP, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_OXEYE_DAISY = register(
		"potted_oxeye_daisy", new FlowerPotBlock(OXEYE_DAISY, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_CORNFLOWER = register(
		"potted_cornflower", new FlowerPotBlock(CORNFLOWER, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_LILY_OF_THE_VALLEY = register(
		"potted_lily_of_the_valley", new FlowerPotBlock(LILY_OF_THE_VALLEY, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_WITHER_ROSE = register(
		"potted_wither_rose", new FlowerPotBlock(WITHER_ROSE, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_RED_MUSHROOM = register(
		"potted_red_mushroom", new FlowerPotBlock(RED_MUSHROOM, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_BROWN_MUSHROOM = register(
		"potted_brown_mushroom", new FlowerPotBlock(BROWN_MUSHROOM, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_DEAD_BUSH = register(
		"potted_dead_bush", new FlowerPotBlock(DEAD_BUSH, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_CACTUS = register(
		"potted_cactus", new FlowerPotBlock(CACTUS, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block CARROTS = register(
		"carrots", new CarrotsBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block POTATOES = register(
		"potatoes", new PotatoesBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block OAK_BUTTON = register(
		"oak_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_BUTTON = register(
		"spruce_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_BUTTON = register(
		"birch_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_BUTTON = register(
		"jungle_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_BUTTON = register(
		"acacia_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_BUTTON = register(
		"dark_oak_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block MANGROVE_BUTTON = register(
		"mangrove_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SKELETON_SKULL = register(
		"skeleton_skull", new SkullBlock(SkullBlock.Type.SKELETON, AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F))
	);
	public static final Block SKELETON_WALL_SKULL = register(
		"skeleton_wall_skull", new WallSkullBlock(SkullBlock.Type.SKELETON, AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F).dropsLike(SKELETON_SKULL))
	);
	public static final Block WITHER_SKELETON_SKULL = register(
		"wither_skeleton_skull", new WitherSkullBlock(AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F))
	);
	public static final Block WITHER_SKELETON_WALL_SKULL = register(
		"wither_skeleton_wall_skull", new WallWitherSkullBlock(AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F).dropsLike(WITHER_SKELETON_SKULL))
	);
	public static final Block ZOMBIE_HEAD = register(
		"zombie_head", new SkullBlock(SkullBlock.Type.ZOMBIE, AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F))
	);
	public static final Block ZOMBIE_WALL_HEAD = register(
		"zombie_wall_head", new WallSkullBlock(SkullBlock.Type.ZOMBIE, AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F).dropsLike(ZOMBIE_HEAD))
	);
	public static final Block PLAYER_HEAD = register("player_head", new PlayerSkullBlock(AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F)));
	public static final Block PLAYER_WALL_HEAD = register(
		"player_wall_head", new WallPlayerSkullBlock(AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F).dropsLike(PLAYER_HEAD))
	);
	public static final Block CREEPER_HEAD = register(
		"creeper_head", new SkullBlock(SkullBlock.Type.CREEPER, AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F))
	);
	public static final Block CREEPER_WALL_HEAD = register(
		"creeper_wall_head", new WallSkullBlock(SkullBlock.Type.CREEPER, AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F).dropsLike(CREEPER_HEAD))
	);
	public static final Block DRAGON_HEAD = register(
		"dragon_head", new SkullBlock(SkullBlock.Type.DRAGON, AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F))
	);
	public static final Block DRAGON_WALL_HEAD = register(
		"dragon_wall_head", new WallSkullBlock(SkullBlock.Type.DRAGON, AbstractBlock.Settings.of(Material.DECORATION).strength(1.0F).dropsLike(DRAGON_HEAD))
	);
	public static final Block ANVIL = register(
		"anvil",
		new AnvilBlock(AbstractBlock.Settings.of(Material.REPAIR_STATION, MapColor.IRON_GRAY).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block CHIPPED_ANVIL = register(
		"chipped_anvil",
		new AnvilBlock(AbstractBlock.Settings.of(Material.REPAIR_STATION, MapColor.IRON_GRAY).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block DAMAGED_ANVIL = register(
		"damaged_anvil",
		new AnvilBlock(AbstractBlock.Settings.of(Material.REPAIR_STATION, MapColor.IRON_GRAY).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block TRAPPED_CHEST = register(
		"trapped_chest", new TrappedChestBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block LIGHT_WEIGHTED_PRESSURE_PLATE = register(
		"light_weighted_pressure_plate",
		new WeightedPressurePlateBlock(
			15, AbstractBlock.Settings.of(Material.METAL, MapColor.GOLD).requiresTool().noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block HEAVY_WEIGHTED_PRESSURE_PLATE = register(
		"heavy_weighted_pressure_plate",
		new WeightedPressurePlateBlock(150, AbstractBlock.Settings.of(Material.METAL).requiresTool().noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block COMPARATOR = register(
		"comparator", new ComparatorBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DAYLIGHT_DETECTOR = register(
		"daylight_detector", new DaylightDetectorBlock(AbstractBlock.Settings.of(Material.WOOD).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block REDSTONE_BLOCK = register(
		"redstone_block",
		new RedstoneBlock(
			AbstractBlock.Settings.of(Material.METAL, MapColor.BRIGHT_RED).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).solidBlock(Blocks::never)
		)
	);
	public static final Block NETHER_QUARTZ_ORE = register(
		"nether_quartz_ore",
		new OreBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.NETHER_ORE),
			UniformIntProvider.create(2, 5)
		)
	);
	public static final Block HOPPER = register(
		"hopper",
		new HopperBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.STONE_GRAY).requiresTool().strength(3.0F, 4.8F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block QUARTZ_BLOCK = register(
		"quartz_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.OFF_WHITE).requiresTool().strength(0.8F))
	);
	public static final Block CHISELED_QUARTZ_BLOCK = register(
		"chiseled_quartz_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.OFF_WHITE).requiresTool().strength(0.8F))
	);
	public static final Block QUARTZ_PILLAR = register(
		"quartz_pillar", new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.OFF_WHITE).requiresTool().strength(0.8F))
	);
	public static final Block QUARTZ_STAIRS = register("quartz_stairs", new StairsBlock(QUARTZ_BLOCK.getDefaultState(), AbstractBlock.Settings.copy(QUARTZ_BLOCK)));
	public static final Block ACTIVATOR_RAIL = register(
		"activator_rail", new PoweredRailBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block DROPPER = register("dropper", new DropperBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F)));
	public static final Block WHITE_TERRACOTTA = register(
		"white_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_WHITE).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block ORANGE_TERRACOTTA = register(
		"orange_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_ORANGE).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block MAGENTA_TERRACOTTA = register(
		"magenta_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_MAGENTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block LIGHT_BLUE_TERRACOTTA = register(
		"light_blue_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_LIGHT_BLUE).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block YELLOW_TERRACOTTA = register(
		"yellow_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_YELLOW).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block LIME_TERRACOTTA = register(
		"lime_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_LIME).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block PINK_TERRACOTTA = register(
		"pink_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_PINK).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block GRAY_TERRACOTTA = register(
		"gray_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_GRAY).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block LIGHT_GRAY_TERRACOTTA = register(
		"light_gray_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_LIGHT_GRAY).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block CYAN_TERRACOTTA = register(
		"cyan_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_CYAN).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block PURPLE_TERRACOTTA = register(
		"purple_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_PURPLE).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block BLUE_TERRACOTTA = register(
		"blue_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_BLUE).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block BROWN_TERRACOTTA = register(
		"brown_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_BROWN).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block GREEN_TERRACOTTA = register(
		"green_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_GREEN).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block RED_TERRACOTTA = register(
		"red_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_RED).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block BLACK_TERRACOTTA = register(
		"black_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_BLACK).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block WHITE_STAINED_GLASS_PANE = register(
		"white_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block ORANGE_STAINED_GLASS_PANE = register(
		"orange_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.ORANGE, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block MAGENTA_STAINED_GLASS_PANE = register(
		"magenta_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.MAGENTA, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block LIGHT_BLUE_STAINED_GLASS_PANE = register(
		"light_blue_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.LIGHT_BLUE, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block YELLOW_STAINED_GLASS_PANE = register(
		"yellow_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.YELLOW, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block LIME_STAINED_GLASS_PANE = register(
		"lime_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.LIME, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block PINK_STAINED_GLASS_PANE = register(
		"pink_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.PINK, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block GRAY_STAINED_GLASS_PANE = register(
		"gray_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.GRAY, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block LIGHT_GRAY_STAINED_GLASS_PANE = register(
		"light_gray_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.LIGHT_GRAY, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block CYAN_STAINED_GLASS_PANE = register(
		"cyan_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.CYAN, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block PURPLE_STAINED_GLASS_PANE = register(
		"purple_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.PURPLE, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block BLUE_STAINED_GLASS_PANE = register(
		"blue_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.BLUE, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block BROWN_STAINED_GLASS_PANE = register(
		"brown_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.BROWN, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block GREEN_STAINED_GLASS_PANE = register(
		"green_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.GREEN, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block RED_STAINED_GLASS_PANE = register(
		"red_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.RED, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block BLACK_STAINED_GLASS_PANE = register(
		"black_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.BLACK, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block ACACIA_STAIRS = register(
		"acacia_stairs", new StairsBlock(ACACIA_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(ACACIA_PLANKS))
	);
	public static final Block DARK_OAK_STAIRS = register(
		"dark_oak_stairs", new StairsBlock(DARK_OAK_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(DARK_OAK_PLANKS))
	);
	public static final Block MANGROVE_STAIRS = register(
		"mangrove_stairs", new StairsBlock(MANGROVE_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(MANGROVE_PLANKS))
	);
	public static final Block SLIME_BLOCK = register(
		"slime_block",
		new SlimeBlock(AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MapColor.PALE_GREEN).slipperiness(0.8F).sounds(BlockSoundGroup.SLIME).nonOpaque())
	);
	public static final Block BARRIER = register(
		"barrier", new BarrierBlock(AbstractBlock.Settings.of(Material.BARRIER).strength(-1.0F, 3600000.8F).dropsNothing().nonOpaque().allowsSpawning(Blocks::never))
	);
	public static final Block LIGHT = register(
		"light",
		new LightBlock(AbstractBlock.Settings.of(Material.AIR).strength(-1.0F, 3600000.8F).dropsNothing().nonOpaque().luminance(LightBlock.STATE_TO_LUMINANCE))
	);
	public static final Block IRON_TRAPDOOR = register(
		"iron_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block PRISMARINE = register(
		"prismarine", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.CYAN).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PRISMARINE_BRICKS = register(
		"prismarine_bricks", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.DIAMOND_BLUE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DARK_PRISMARINE = register(
		"dark_prismarine", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.DIAMOND_BLUE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PRISMARINE_STAIRS = register(
		"prismarine_stairs", new StairsBlock(PRISMARINE.getDefaultState(), AbstractBlock.Settings.copy(PRISMARINE))
	);
	public static final Block PRISMARINE_BRICK_STAIRS = register(
		"prismarine_brick_stairs", new StairsBlock(PRISMARINE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(PRISMARINE_BRICKS))
	);
	public static final Block DARK_PRISMARINE_STAIRS = register(
		"dark_prismarine_stairs", new StairsBlock(DARK_PRISMARINE.getDefaultState(), AbstractBlock.Settings.copy(DARK_PRISMARINE))
	);
	public static final Block PRISMARINE_SLAB = register(
		"prismarine_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.CYAN).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PRISMARINE_BRICK_SLAB = register(
		"prismarine_brick_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.DIAMOND_BLUE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DARK_PRISMARINE_SLAB = register(
		"dark_prismarine_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.DIAMOND_BLUE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block SEA_LANTERN = register(
		"sea_lantern", new Block(AbstractBlock.Settings.of(Material.GLASS, MapColor.OFF_WHITE).strength(0.3F).sounds(BlockSoundGroup.GLASS).luminance(state -> 15))
	);
	public static final Block HAY_BLOCK = register(
		"hay_block", new HayBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MapColor.YELLOW).strength(0.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block WHITE_CARPET = register(
		"white_carpet", new DyedCarpetBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.CARPET, MapColor.WHITE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block ORANGE_CARPET = register(
		"orange_carpet",
		new DyedCarpetBlock(DyeColor.ORANGE, AbstractBlock.Settings.of(Material.CARPET, MapColor.ORANGE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block MAGENTA_CARPET = register(
		"magenta_carpet",
		new DyedCarpetBlock(DyeColor.MAGENTA, AbstractBlock.Settings.of(Material.CARPET, MapColor.MAGENTA).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIGHT_BLUE_CARPET = register(
		"light_blue_carpet",
		new DyedCarpetBlock(DyeColor.LIGHT_BLUE, AbstractBlock.Settings.of(Material.CARPET, MapColor.LIGHT_BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block YELLOW_CARPET = register(
		"yellow_carpet",
		new DyedCarpetBlock(DyeColor.YELLOW, AbstractBlock.Settings.of(Material.CARPET, MapColor.YELLOW).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIME_CARPET = register(
		"lime_carpet", new DyedCarpetBlock(DyeColor.LIME, AbstractBlock.Settings.of(Material.CARPET, MapColor.LIME).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block PINK_CARPET = register(
		"pink_carpet", new DyedCarpetBlock(DyeColor.PINK, AbstractBlock.Settings.of(Material.CARPET, MapColor.PINK).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block GRAY_CARPET = register(
		"gray_carpet", new DyedCarpetBlock(DyeColor.GRAY, AbstractBlock.Settings.of(Material.CARPET, MapColor.GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIGHT_GRAY_CARPET = register(
		"light_gray_carpet",
		new DyedCarpetBlock(DyeColor.LIGHT_GRAY, AbstractBlock.Settings.of(Material.CARPET, MapColor.LIGHT_GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block CYAN_CARPET = register(
		"cyan_carpet", new DyedCarpetBlock(DyeColor.CYAN, AbstractBlock.Settings.of(Material.CARPET, MapColor.CYAN).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block PURPLE_CARPET = register(
		"purple_carpet",
		new DyedCarpetBlock(DyeColor.PURPLE, AbstractBlock.Settings.of(Material.CARPET, MapColor.PURPLE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BLUE_CARPET = register(
		"blue_carpet", new DyedCarpetBlock(DyeColor.BLUE, AbstractBlock.Settings.of(Material.CARPET, MapColor.BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BROWN_CARPET = register(
		"brown_carpet", new DyedCarpetBlock(DyeColor.BROWN, AbstractBlock.Settings.of(Material.CARPET, MapColor.BROWN).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block GREEN_CARPET = register(
		"green_carpet", new DyedCarpetBlock(DyeColor.GREEN, AbstractBlock.Settings.of(Material.CARPET, MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block RED_CARPET = register(
		"red_carpet", new DyedCarpetBlock(DyeColor.RED, AbstractBlock.Settings.of(Material.CARPET, MapColor.RED).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BLACK_CARPET = register(
		"black_carpet", new DyedCarpetBlock(DyeColor.BLACK, AbstractBlock.Settings.of(Material.CARPET, MapColor.BLACK).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block TERRACOTTA = register(
		"terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.ORANGE).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block COAL_BLOCK = register(
		"coal_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(5.0F, 6.0F))
	);
	public static final Block PACKED_ICE = register(
		"packed_ice", new Block(AbstractBlock.Settings.of(Material.DENSE_ICE).slipperiness(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block SUNFLOWER = register(
		"sunflower",
		new TallFlowerBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block LILAC = register(
		"lilac",
		new TallFlowerBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block ROSE_BUSH = register(
		"rose_bush",
		new TallFlowerBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block PEONY = register(
		"peony",
		new TallFlowerBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block TALL_GRASS = register(
		"tall_grass",
		new TallPlantBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block LARGE_FERN = register(
		"large_fern",
		new TallPlantBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block WHITE_BANNER = register(
		"white_banner", new BannerBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ORANGE_BANNER = register(
		"orange_banner", new BannerBlock(DyeColor.ORANGE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block MAGENTA_BANNER = register(
		"magenta_banner", new BannerBlock(DyeColor.MAGENTA, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block LIGHT_BLUE_BANNER = register(
		"light_blue_banner", new BannerBlock(DyeColor.LIGHT_BLUE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block YELLOW_BANNER = register(
		"yellow_banner", new BannerBlock(DyeColor.YELLOW, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block LIME_BANNER = register(
		"lime_banner", new BannerBlock(DyeColor.LIME, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block PINK_BANNER = register(
		"pink_banner", new BannerBlock(DyeColor.PINK, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block GRAY_BANNER = register(
		"gray_banner", new BannerBlock(DyeColor.GRAY, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block LIGHT_GRAY_BANNER = register(
		"light_gray_banner", new BannerBlock(DyeColor.LIGHT_GRAY, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CYAN_BANNER = register(
		"cyan_banner", new BannerBlock(DyeColor.CYAN, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block PURPLE_BANNER = register(
		"purple_banner", new BannerBlock(DyeColor.PURPLE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BLUE_BANNER = register(
		"blue_banner", new BannerBlock(DyeColor.BLUE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BROWN_BANNER = register(
		"brown_banner", new BannerBlock(DyeColor.BROWN, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block GREEN_BANNER = register(
		"green_banner", new BannerBlock(DyeColor.GREEN, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block RED_BANNER = register(
		"red_banner", new BannerBlock(DyeColor.RED, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BLACK_BANNER = register(
		"black_banner", new BannerBlock(DyeColor.BLACK, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WHITE_WALL_BANNER = register(
		"white_wall_banner",
		new WallBannerBlock(
			DyeColor.WHITE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(WHITE_BANNER)
		)
	);
	public static final Block ORANGE_WALL_BANNER = register(
		"orange_wall_banner",
		new WallBannerBlock(
			DyeColor.ORANGE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(ORANGE_BANNER)
		)
	);
	public static final Block MAGENTA_WALL_BANNER = register(
		"magenta_wall_banner",
		new WallBannerBlock(
			DyeColor.MAGENTA, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(MAGENTA_BANNER)
		)
	);
	public static final Block LIGHT_BLUE_WALL_BANNER = register(
		"light_blue_wall_banner",
		new WallBannerBlock(
			DyeColor.LIGHT_BLUE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(LIGHT_BLUE_BANNER)
		)
	);
	public static final Block YELLOW_WALL_BANNER = register(
		"yellow_wall_banner",
		new WallBannerBlock(
			DyeColor.YELLOW, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(YELLOW_BANNER)
		)
	);
	public static final Block LIME_WALL_BANNER = register(
		"lime_wall_banner",
		new WallBannerBlock(DyeColor.LIME, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(LIME_BANNER))
	);
	public static final Block PINK_WALL_BANNER = register(
		"pink_wall_banner",
		new WallBannerBlock(DyeColor.PINK, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(PINK_BANNER))
	);
	public static final Block GRAY_WALL_BANNER = register(
		"gray_wall_banner",
		new WallBannerBlock(DyeColor.GRAY, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(GRAY_BANNER))
	);
	public static final Block LIGHT_GRAY_WALL_BANNER = register(
		"light_gray_wall_banner",
		new WallBannerBlock(
			DyeColor.LIGHT_GRAY, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(LIGHT_GRAY_BANNER)
		)
	);
	public static final Block CYAN_WALL_BANNER = register(
		"cyan_wall_banner",
		new WallBannerBlock(DyeColor.CYAN, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(CYAN_BANNER))
	);
	public static final Block PURPLE_WALL_BANNER = register(
		"purple_wall_banner",
		new WallBannerBlock(
			DyeColor.PURPLE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(PURPLE_BANNER)
		)
	);
	public static final Block BLUE_WALL_BANNER = register(
		"blue_wall_banner",
		new WallBannerBlock(DyeColor.BLUE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(BLUE_BANNER))
	);
	public static final Block BROWN_WALL_BANNER = register(
		"brown_wall_banner",
		new WallBannerBlock(
			DyeColor.BROWN, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(BROWN_BANNER)
		)
	);
	public static final Block GREEN_WALL_BANNER = register(
		"green_wall_banner",
		new WallBannerBlock(
			DyeColor.GREEN, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(GREEN_BANNER)
		)
	);
	public static final Block RED_WALL_BANNER = register(
		"red_wall_banner",
		new WallBannerBlock(DyeColor.RED, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(RED_BANNER))
	);
	public static final Block BLACK_WALL_BANNER = register(
		"black_wall_banner",
		new WallBannerBlock(
			DyeColor.BLACK, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(BLACK_BANNER)
		)
	);
	public static final Block RED_SANDSTONE = register(
		"red_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.ORANGE).requiresTool().strength(0.8F))
	);
	public static final Block CHISELED_RED_SANDSTONE = register(
		"chiseled_red_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.ORANGE).requiresTool().strength(0.8F))
	);
	public static final Block CUT_RED_SANDSTONE = register(
		"cut_red_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.ORANGE).requiresTool().strength(0.8F))
	);
	public static final Block RED_SANDSTONE_STAIRS = register(
		"red_sandstone_stairs", new StairsBlock(RED_SANDSTONE.getDefaultState(), AbstractBlock.Settings.copy(RED_SANDSTONE))
	);
	public static final Block OAK_SLAB = register(
		"oak_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_SLAB = register(
		"spruce_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_SLAB = register(
		"birch_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_SLAB = register(
		"jungle_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_SLAB = register(
		"acacia_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.ORANGE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_SLAB = register(
		"dark_oak_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block MANGROVE_SLAB = register(
		"mangrove_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.RED).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STONE_SLAB = register(
		"stone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_STONE_SLAB = register(
		"smooth_stone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SANDSTONE_SLAB = register(
		"sandstone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block CUT_SANDSTONE_SLAB = register(
		"cut_sandstone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block PETRIFIED_OAK_SLAB = register(
		"petrified_oak_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.OAK_TAN).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block COBBLESTONE_SLAB = register(
		"cobblestone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block BRICK_SLAB = register(
		"brick_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.RED).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block STONE_BRICK_SLAB = register(
		"stone_brick_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block MUD_BRICK_SLAB = register(
		"mud_brick_slab",
		new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.BROWN).requiresTool().strength(1.5F, 3.0F).sounds(BlockSoundGroup.MUD_BRICKS))
	);
	public static final Block NETHER_BRICK_SLAB = register(
		"nether_brick_slab",
		new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block QUARTZ_SLAB = register(
		"quartz_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.OFF_WHITE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block RED_SANDSTONE_SLAB = register(
		"red_sandstone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.ORANGE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block CUT_RED_SANDSTONE_SLAB = register(
		"cut_red_sandstone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.ORANGE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block PURPUR_SLAB = register(
		"purpur_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.MAGENTA).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_STONE = register(
		"smooth_stone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_SANDSTONE = register(
		"smooth_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_QUARTZ = register(
		"smooth_quartz", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.OFF_WHITE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_RED_SANDSTONE = register(
		"smooth_red_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.ORANGE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SPRUCE_FENCE_GATE = register(
		"spruce_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, SPRUCE_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_FENCE_GATE = register(
		"birch_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, BIRCH_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_FENCE_GATE = register(
		"jungle_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, JUNGLE_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_FENCE_GATE = register(
		"acacia_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, ACACIA_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_FENCE_GATE = register(
		"dark_oak_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block MANGROVE_FENCE_GATE = register(
		"mangrove_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, MANGROVE_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_FENCE = register(
		"spruce_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, SPRUCE_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_FENCE = register(
		"birch_fence", new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, BIRCH_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_FENCE = register(
		"jungle_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, JUNGLE_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_FENCE = register(
		"acacia_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, ACACIA_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_FENCE = register(
		"dark_oak_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block MANGROVE_FENCE = register(
		"mangrove_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, MANGROVE_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_DOOR = register(
		"spruce_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, SPRUCE_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block BIRCH_DOOR = register(
		"birch_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, BIRCH_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block JUNGLE_DOOR = register(
		"jungle_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, JUNGLE_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block ACACIA_DOOR = register(
		"acacia_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, ACACIA_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block DARK_OAK_DOOR = register(
		"dark_oak_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block MANGROVE_DOOR = register(
		"mangrove_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, MANGROVE_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block END_ROD = register(
		"end_rod", new EndRodBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().luminance(state -> 14).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block CHORUS_PLANT = register(
		"chorus_plant", new ChorusPlantBlock(AbstractBlock.Settings.of(Material.PLANT, MapColor.PURPLE).strength(0.4F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block CHORUS_FLOWER = register(
		"chorus_flower",
		new ChorusFlowerBlock(
			(ChorusPlantBlock)CHORUS_PLANT,
			AbstractBlock.Settings.of(Material.PLANT, MapColor.PURPLE).ticksRandomly().strength(0.4F).sounds(BlockSoundGroup.WOOD).nonOpaque()
		)
	);
	public static final Block PURPUR_BLOCK = register(
		"purpur_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.MAGENTA).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PURPUR_PILLAR = register(
		"purpur_pillar", new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.MAGENTA).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PURPUR_STAIRS = register("purpur_stairs", new StairsBlock(PURPUR_BLOCK.getDefaultState(), AbstractBlock.Settings.copy(PURPUR_BLOCK)));
	public static final Block END_STONE_BRICKS = register(
		"end_stone_bricks", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().strength(3.0F, 9.0F))
	);
	public static final Block BEETROOTS = register(
		"beetroots", new BeetrootsBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block DIRT_PATH = register(
		"dirt_path",
		new DirtPathBlock(
			AbstractBlock.Settings.of(Material.SOIL).strength(0.65F).sounds(BlockSoundGroup.GRASS).blockVision(Blocks::always).suffocates(Blocks::always)
		)
	);
	public static final Block END_GATEWAY = register(
		"end_gateway",
		new EndGatewayBlock(
			AbstractBlock.Settings.of(Material.PORTAL, MapColor.BLACK).noCollision().luminance(state -> 15).strength(-1.0F, 3600000.0F).dropsNothing()
		)
	);
	public static final Block REPEATING_COMMAND_BLOCK = register(
		"repeating_command_block",
		new CommandBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.PURPLE).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing(), false)
	);
	public static final Block CHAIN_COMMAND_BLOCK = register(
		"chain_command_block",
		new CommandBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.GREEN).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing(), true)
	);
	public static final Block FROSTED_ICE = register(
		"frosted_ice",
		new FrostedIceBlock(
			AbstractBlock.Settings.of(Material.ICE)
				.slipperiness(0.98F)
				.ticksRandomly()
				.strength(0.5F)
				.sounds(BlockSoundGroup.GLASS)
				.nonOpaque()
				.allowsSpawning((state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR)
		)
	);
	public static final Block MAGMA_BLOCK = register(
		"magma_block",
		new MagmaBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED)
				.requiresTool()
				.luminance(state -> 3)
				.ticksRandomly()
				.strength(0.5F)
				.allowsSpawning((state, world, pos, entityType) -> entityType.isFireImmune())
				.postProcess(Blocks::always)
				.emissiveLighting(Blocks::always)
		)
	);
	public static final Block NETHER_WART_BLOCK = register(
		"nether_wart_block", new Block(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MapColor.RED).strength(1.0F).sounds(BlockSoundGroup.WART_BLOCK))
	);
	public static final Block RED_NETHER_BRICKS = register(
		"red_nether_bricks",
		new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block BONE_BLOCK = register(
		"bone_block", new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().strength(2.0F).sounds(BlockSoundGroup.BONE))
	);
	public static final Block STRUCTURE_VOID = register(
		"structure_void", new StructureVoidBlock(AbstractBlock.Settings.of(Material.STRUCTURE_VOID).noCollision().dropsNothing())
	);
	public static final Block OBSERVER = register(
		"observer", new ObserverBlock(AbstractBlock.Settings.of(Material.STONE).strength(3.0F).requiresTool().solidBlock(Blocks::never))
	);
	public static final Block SHULKER_BOX = register("shulker_box", createShulkerBoxBlock(null, AbstractBlock.Settings.of(Material.SHULKER_BOX)));
	public static final Block WHITE_SHULKER_BOX = register(
		"white_shulker_box", createShulkerBoxBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.WHITE))
	);
	public static final Block ORANGE_SHULKER_BOX = register(
		"orange_shulker_box", createShulkerBoxBlock(DyeColor.ORANGE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.ORANGE))
	);
	public static final Block MAGENTA_SHULKER_BOX = register(
		"magenta_shulker_box", createShulkerBoxBlock(DyeColor.MAGENTA, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.MAGENTA))
	);
	public static final Block LIGHT_BLUE_SHULKER_BOX = register(
		"light_blue_shulker_box", createShulkerBoxBlock(DyeColor.LIGHT_BLUE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.LIGHT_BLUE))
	);
	public static final Block YELLOW_SHULKER_BOX = register(
		"yellow_shulker_box", createShulkerBoxBlock(DyeColor.YELLOW, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.YELLOW))
	);
	public static final Block LIME_SHULKER_BOX = register(
		"lime_shulker_box", createShulkerBoxBlock(DyeColor.LIME, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.LIME))
	);
	public static final Block PINK_SHULKER_BOX = register(
		"pink_shulker_box", createShulkerBoxBlock(DyeColor.PINK, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.PINK))
	);
	public static final Block GRAY_SHULKER_BOX = register(
		"gray_shulker_box", createShulkerBoxBlock(DyeColor.GRAY, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.GRAY))
	);
	public static final Block LIGHT_GRAY_SHULKER_BOX = register(
		"light_gray_shulker_box", createShulkerBoxBlock(DyeColor.LIGHT_GRAY, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.LIGHT_GRAY))
	);
	public static final Block CYAN_SHULKER_BOX = register(
		"cyan_shulker_box", createShulkerBoxBlock(DyeColor.CYAN, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.CYAN))
	);
	public static final Block PURPLE_SHULKER_BOX = register(
		"purple_shulker_box", createShulkerBoxBlock(DyeColor.PURPLE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.TERRACOTTA_PURPLE))
	);
	public static final Block BLUE_SHULKER_BOX = register(
		"blue_shulker_box", createShulkerBoxBlock(DyeColor.BLUE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.BLUE))
	);
	public static final Block BROWN_SHULKER_BOX = register(
		"brown_shulker_box", createShulkerBoxBlock(DyeColor.BROWN, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.BROWN))
	);
	public static final Block GREEN_SHULKER_BOX = register(
		"green_shulker_box", createShulkerBoxBlock(DyeColor.GREEN, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.GREEN))
	);
	public static final Block RED_SHULKER_BOX = register(
		"red_shulker_box", createShulkerBoxBlock(DyeColor.RED, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.RED))
	);
	public static final Block BLACK_SHULKER_BOX = register(
		"black_shulker_box", createShulkerBoxBlock(DyeColor.BLACK, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.BLACK))
	);
	public static final Block WHITE_GLAZED_TERRACOTTA = register(
		"white_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.WHITE).requiresTool().strength(1.4F))
	);
	public static final Block ORANGE_GLAZED_TERRACOTTA = register(
		"orange_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.ORANGE).requiresTool().strength(1.4F))
	);
	public static final Block MAGENTA_GLAZED_TERRACOTTA = register(
		"magenta_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.MAGENTA).requiresTool().strength(1.4F))
	);
	public static final Block LIGHT_BLUE_GLAZED_TERRACOTTA = register(
		"light_blue_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.LIGHT_BLUE).requiresTool().strength(1.4F))
	);
	public static final Block YELLOW_GLAZED_TERRACOTTA = register(
		"yellow_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.YELLOW).requiresTool().strength(1.4F))
	);
	public static final Block LIME_GLAZED_TERRACOTTA = register(
		"lime_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.LIME).requiresTool().strength(1.4F))
	);
	public static final Block PINK_GLAZED_TERRACOTTA = register(
		"pink_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.PINK).requiresTool().strength(1.4F))
	);
	public static final Block GRAY_GLAZED_TERRACOTTA = register(
		"gray_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.GRAY).requiresTool().strength(1.4F))
	);
	public static final Block LIGHT_GRAY_GLAZED_TERRACOTTA = register(
		"light_gray_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.LIGHT_GRAY).requiresTool().strength(1.4F))
	);
	public static final Block CYAN_GLAZED_TERRACOTTA = register(
		"cyan_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.CYAN).requiresTool().strength(1.4F))
	);
	public static final Block PURPLE_GLAZED_TERRACOTTA = register(
		"purple_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.PURPLE).requiresTool().strength(1.4F))
	);
	public static final Block BLUE_GLAZED_TERRACOTTA = register(
		"blue_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.BLUE).requiresTool().strength(1.4F))
	);
	public static final Block BROWN_GLAZED_TERRACOTTA = register(
		"brown_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.BROWN).requiresTool().strength(1.4F))
	);
	public static final Block GREEN_GLAZED_TERRACOTTA = register(
		"green_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.GREEN).requiresTool().strength(1.4F))
	);
	public static final Block RED_GLAZED_TERRACOTTA = register(
		"red_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.RED).requiresTool().strength(1.4F))
	);
	public static final Block BLACK_GLAZED_TERRACOTTA = register(
		"black_glazed_terracotta", new GlazedTerracottaBlock(AbstractBlock.Settings.of(Material.STONE, DyeColor.BLACK).requiresTool().strength(1.4F))
	);
	public static final Block WHITE_CONCRETE = register(
		"white_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.WHITE).requiresTool().strength(1.8F))
	);
	public static final Block ORANGE_CONCRETE = register(
		"orange_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.ORANGE).requiresTool().strength(1.8F))
	);
	public static final Block MAGENTA_CONCRETE = register(
		"magenta_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.MAGENTA).requiresTool().strength(1.8F))
	);
	public static final Block LIGHT_BLUE_CONCRETE = register(
		"light_blue_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.LIGHT_BLUE).requiresTool().strength(1.8F))
	);
	public static final Block YELLOW_CONCRETE = register(
		"yellow_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.YELLOW).requiresTool().strength(1.8F))
	);
	public static final Block LIME_CONCRETE = register(
		"lime_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.LIME).requiresTool().strength(1.8F))
	);
	public static final Block PINK_CONCRETE = register(
		"pink_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.PINK).requiresTool().strength(1.8F))
	);
	public static final Block GRAY_CONCRETE = register(
		"gray_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.GRAY).requiresTool().strength(1.8F))
	);
	public static final Block LIGHT_GRAY_CONCRETE = register(
		"light_gray_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.LIGHT_GRAY).requiresTool().strength(1.8F))
	);
	public static final Block CYAN_CONCRETE = register(
		"cyan_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.CYAN).requiresTool().strength(1.8F))
	);
	public static final Block PURPLE_CONCRETE = register(
		"purple_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.PURPLE).requiresTool().strength(1.8F))
	);
	public static final Block BLUE_CONCRETE = register(
		"blue_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.BLUE).requiresTool().strength(1.8F))
	);
	public static final Block BROWN_CONCRETE = register(
		"brown_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.BROWN).requiresTool().strength(1.8F))
	);
	public static final Block GREEN_CONCRETE = register(
		"green_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.GREEN).requiresTool().strength(1.8F))
	);
	public static final Block RED_CONCRETE = register(
		"red_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.RED).requiresTool().strength(1.8F))
	);
	public static final Block BLACK_CONCRETE = register(
		"black_concrete", new Block(AbstractBlock.Settings.of(Material.STONE, DyeColor.BLACK).requiresTool().strength(1.8F))
	);
	public static final Block WHITE_CONCRETE_POWDER = register(
		"white_concrete_powder",
		new ConcretePowderBlock(WHITE_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.WHITE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block ORANGE_CONCRETE_POWDER = register(
		"orange_concrete_powder",
		new ConcretePowderBlock(ORANGE_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.ORANGE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block MAGENTA_CONCRETE_POWDER = register(
		"magenta_concrete_powder",
		new ConcretePowderBlock(MAGENTA_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.MAGENTA).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block LIGHT_BLUE_CONCRETE_POWDER = register(
		"light_blue_concrete_powder",
		new ConcretePowderBlock(LIGHT_BLUE_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.LIGHT_BLUE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block YELLOW_CONCRETE_POWDER = register(
		"yellow_concrete_powder",
		new ConcretePowderBlock(YELLOW_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.YELLOW).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block LIME_CONCRETE_POWDER = register(
		"lime_concrete_powder",
		new ConcretePowderBlock(LIME_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.LIME).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block PINK_CONCRETE_POWDER = register(
		"pink_concrete_powder",
		new ConcretePowderBlock(PINK_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.PINK).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block GRAY_CONCRETE_POWDER = register(
		"gray_concrete_powder",
		new ConcretePowderBlock(GRAY_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.GRAY).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block LIGHT_GRAY_CONCRETE_POWDER = register(
		"light_gray_concrete_powder",
		new ConcretePowderBlock(LIGHT_GRAY_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.LIGHT_GRAY).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block CYAN_CONCRETE_POWDER = register(
		"cyan_concrete_powder",
		new ConcretePowderBlock(CYAN_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.CYAN).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block PURPLE_CONCRETE_POWDER = register(
		"purple_concrete_powder",
		new ConcretePowderBlock(PURPLE_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.PURPLE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block BLUE_CONCRETE_POWDER = register(
		"blue_concrete_powder",
		new ConcretePowderBlock(BLUE_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.BLUE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block BROWN_CONCRETE_POWDER = register(
		"brown_concrete_powder",
		new ConcretePowderBlock(BROWN_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.BROWN).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block GREEN_CONCRETE_POWDER = register(
		"green_concrete_powder",
		new ConcretePowderBlock(GREEN_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.GREEN).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block RED_CONCRETE_POWDER = register(
		"red_concrete_powder",
		new ConcretePowderBlock(RED_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.RED).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block BLACK_CONCRETE_POWDER = register(
		"black_concrete_powder",
		new ConcretePowderBlock(BLACK_CONCRETE, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColor.BLACK).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block KELP = register(
		"kelp", new KelpBlock(AbstractBlock.Settings.of(Material.UNDERWATER_PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block KELP_PLANT = register(
		"kelp_plant", new KelpPlantBlock(AbstractBlock.Settings.of(Material.UNDERWATER_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block DRIED_KELP_BLOCK = register(
		"dried_kelp_block", new Block(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MapColor.GREEN).strength(0.5F, 2.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block TURTLE_EGG = register(
		"turtle_egg",
		new TurtleEggBlock(AbstractBlock.Settings.of(Material.EGG, MapColor.PALE_YELLOW).strength(0.5F).sounds(BlockSoundGroup.METAL).ticksRandomly().nonOpaque())
	);
	public static final Block DEAD_TUBE_CORAL_BLOCK = register(
		"dead_tube_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_BRAIN_CORAL_BLOCK = register(
		"dead_brain_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_BUBBLE_CORAL_BLOCK = register(
		"dead_bubble_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_FIRE_CORAL_BLOCK = register(
		"dead_fire_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_HORN_CORAL_BLOCK = register(
		"dead_horn_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block TUBE_CORAL_BLOCK = register(
		"tube_coral_block",
		new CoralBlockBlock(
			DEAD_TUBE_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MapColor.BLUE).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block BRAIN_CORAL_BLOCK = register(
		"brain_coral_block",
		new CoralBlockBlock(
			DEAD_BRAIN_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MapColor.PINK).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block BUBBLE_CORAL_BLOCK = register(
		"bubble_coral_block",
		new CoralBlockBlock(
			DEAD_BUBBLE_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MapColor.PURPLE).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block FIRE_CORAL_BLOCK = register(
		"fire_coral_block",
		new CoralBlockBlock(
			DEAD_FIRE_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MapColor.RED).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block HORN_CORAL_BLOCK = register(
		"horn_coral_block",
		new CoralBlockBlock(
			DEAD_HORN_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MapColor.YELLOW).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block DEAD_TUBE_CORAL = register(
		"dead_tube_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_BRAIN_CORAL = register(
		"dead_brain_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_BUBBLE_CORAL = register(
		"dead_bubble_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_FIRE_CORAL = register(
		"dead_fire_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_HORN_CORAL = register(
		"dead_horn_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block TUBE_CORAL = register(
		"tube_coral",
		new CoralBlock(
			DEAD_TUBE_CORAL, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.BLUE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block BRAIN_CORAL = register(
		"brain_coral",
		new CoralBlock(
			DEAD_BRAIN_CORAL, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.PINK).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block BUBBLE_CORAL = register(
		"bubble_coral",
		new CoralBlock(
			DEAD_BUBBLE_CORAL, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.PURPLE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block FIRE_CORAL = register(
		"fire_coral",
		new CoralBlock(
			DEAD_FIRE_CORAL, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.RED).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block HORN_CORAL = register(
		"horn_coral",
		new CoralBlock(
			DEAD_HORN_CORAL, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.YELLOW).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block DEAD_TUBE_CORAL_FAN = register(
		"dead_tube_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_BRAIN_CORAL_FAN = register(
		"dead_brain_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_BUBBLE_CORAL_FAN = register(
		"dead_bubble_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_FIRE_CORAL_FAN = register(
		"dead_fire_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_HORN_CORAL_FAN = register(
		"dead_horn_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block TUBE_CORAL_FAN = register(
		"tube_coral_fan",
		new CoralFanBlock(
			DEAD_TUBE_CORAL_FAN, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.BLUE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block BRAIN_CORAL_FAN = register(
		"brain_coral_fan",
		new CoralFanBlock(
			DEAD_BRAIN_CORAL_FAN, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.PINK).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block BUBBLE_CORAL_FAN = register(
		"bubble_coral_fan",
		new CoralFanBlock(
			DEAD_BUBBLE_CORAL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.PURPLE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block FIRE_CORAL_FAN = register(
		"fire_coral_fan",
		new CoralFanBlock(
			DEAD_FIRE_CORAL_FAN, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.RED).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block HORN_CORAL_FAN = register(
		"horn_coral_fan",
		new CoralFanBlock(
			DEAD_HORN_CORAL_FAN, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.YELLOW).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block DEAD_TUBE_CORAL_WALL_FAN = register(
		"dead_tube_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_TUBE_CORAL_FAN)
		)
	);
	public static final Block DEAD_BRAIN_CORAL_WALL_FAN = register(
		"dead_brain_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_BRAIN_CORAL_FAN)
		)
	);
	public static final Block DEAD_BUBBLE_CORAL_WALL_FAN = register(
		"dead_bubble_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_BUBBLE_CORAL_FAN)
		)
	);
	public static final Block DEAD_FIRE_CORAL_WALL_FAN = register(
		"dead_fire_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_FIRE_CORAL_FAN)
		)
	);
	public static final Block DEAD_HORN_CORAL_WALL_FAN = register(
		"dead_horn_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_HORN_CORAL_FAN)
		)
	);
	public static final Block TUBE_CORAL_WALL_FAN = register(
		"tube_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_TUBE_CORAL_WALL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.BLUE)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(TUBE_CORAL_FAN)
		)
	);
	public static final Block BRAIN_CORAL_WALL_FAN = register(
		"brain_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_BRAIN_CORAL_WALL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.PINK)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(BRAIN_CORAL_FAN)
		)
	);
	public static final Block BUBBLE_CORAL_WALL_FAN = register(
		"bubble_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_BUBBLE_CORAL_WALL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.PURPLE)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(BUBBLE_CORAL_FAN)
		)
	);
	public static final Block FIRE_CORAL_WALL_FAN = register(
		"fire_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_FIRE_CORAL_WALL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.RED)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(FIRE_CORAL_FAN)
		)
	);
	public static final Block HORN_CORAL_WALL_FAN = register(
		"horn_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_HORN_CORAL_WALL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.YELLOW)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(HORN_CORAL_FAN)
		)
	);
	public static final Block SEA_PICKLE = register(
		"sea_pickle",
		new SeaPickleBlock(
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MapColor.GREEN)
				.luminance(state -> SeaPickleBlock.isDry(state) ? 0 : 3 + 3 * (Integer)state.get(SeaPickleBlock.PICKLES))
				.sounds(BlockSoundGroup.SLIME)
				.nonOpaque()
		)
	);
	public static final Block BLUE_ICE = register(
		"blue_ice", new TransparentBlock(AbstractBlock.Settings.of(Material.DENSE_ICE).strength(2.8F).slipperiness(0.989F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block CONDUIT = register(
		"conduit", new ConduitBlock(AbstractBlock.Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE).strength(3.0F).luminance(state -> 15).nonOpaque())
	);
	public static final Block BAMBOO_SAPLING = register(
		"bamboo_sapling",
		new BambooSaplingBlock(
			AbstractBlock.Settings.of(Material.BAMBOO_SAPLING)
				.ticksRandomly()
				.breakInstantly()
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.BAMBOO_SAPLING)
				.offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block BAMBOO = register(
		"bamboo",
		new BambooBlock(
			AbstractBlock.Settings.of(Material.BAMBOO, MapColor.DARK_GREEN)
				.ticksRandomly()
				.breakInstantly()
				.strength(1.0F)
				.sounds(BlockSoundGroup.BAMBOO)
				.nonOpaque()
				.dynamicBounds()
				.offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block POTTED_BAMBOO = register(
		"potted_bamboo", new FlowerPotBlock(BAMBOO, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block VOID_AIR = register("void_air", new AirBlock(AbstractBlock.Settings.of(Material.AIR).noCollision().dropsNothing().air()));
	public static final Block CAVE_AIR = register("cave_air", new AirBlock(AbstractBlock.Settings.of(Material.AIR).noCollision().dropsNothing().air()));
	public static final Block BUBBLE_COLUMN = register(
		"bubble_column", new BubbleColumnBlock(AbstractBlock.Settings.of(Material.BUBBLE_COLUMN).noCollision().dropsNothing())
	);
	public static final Block POLISHED_GRANITE_STAIRS = register(
		"polished_granite_stairs", new StairsBlock(POLISHED_GRANITE.getDefaultState(), AbstractBlock.Settings.copy(POLISHED_GRANITE))
	);
	public static final Block SMOOTH_RED_SANDSTONE_STAIRS = register(
		"smooth_red_sandstone_stairs", new StairsBlock(SMOOTH_RED_SANDSTONE.getDefaultState(), AbstractBlock.Settings.copy(SMOOTH_RED_SANDSTONE))
	);
	public static final Block MOSSY_STONE_BRICK_STAIRS = register(
		"mossy_stone_brick_stairs", new StairsBlock(MOSSY_STONE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(MOSSY_STONE_BRICKS))
	);
	public static final Block POLISHED_DIORITE_STAIRS = register(
		"polished_diorite_stairs", new StairsBlock(POLISHED_DIORITE.getDefaultState(), AbstractBlock.Settings.copy(POLISHED_DIORITE))
	);
	public static final Block MOSSY_COBBLESTONE_STAIRS = register(
		"mossy_cobblestone_stairs", new StairsBlock(MOSSY_COBBLESTONE.getDefaultState(), AbstractBlock.Settings.copy(MOSSY_COBBLESTONE))
	);
	public static final Block END_STONE_BRICK_STAIRS = register(
		"end_stone_brick_stairs", new StairsBlock(END_STONE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(END_STONE_BRICKS))
	);
	public static final Block STONE_STAIRS = register("stone_stairs", new StairsBlock(STONE.getDefaultState(), AbstractBlock.Settings.copy(STONE)));
	public static final Block SMOOTH_SANDSTONE_STAIRS = register(
		"smooth_sandstone_stairs", new StairsBlock(SMOOTH_SANDSTONE.getDefaultState(), AbstractBlock.Settings.copy(SMOOTH_SANDSTONE))
	);
	public static final Block SMOOTH_QUARTZ_STAIRS = register(
		"smooth_quartz_stairs", new StairsBlock(SMOOTH_QUARTZ.getDefaultState(), AbstractBlock.Settings.copy(SMOOTH_QUARTZ))
	);
	public static final Block GRANITE_STAIRS = register("granite_stairs", new StairsBlock(GRANITE.getDefaultState(), AbstractBlock.Settings.copy(GRANITE)));
	public static final Block ANDESITE_STAIRS = register("andesite_stairs", new StairsBlock(ANDESITE.getDefaultState(), AbstractBlock.Settings.copy(ANDESITE)));
	public static final Block RED_NETHER_BRICK_STAIRS = register(
		"red_nether_brick_stairs", new StairsBlock(RED_NETHER_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(RED_NETHER_BRICKS))
	);
	public static final Block POLISHED_ANDESITE_STAIRS = register(
		"polished_andesite_stairs", new StairsBlock(POLISHED_ANDESITE.getDefaultState(), AbstractBlock.Settings.copy(POLISHED_ANDESITE))
	);
	public static final Block DIORITE_STAIRS = register("diorite_stairs", new StairsBlock(DIORITE.getDefaultState(), AbstractBlock.Settings.copy(DIORITE)));
	public static final Block POLISHED_GRANITE_SLAB = register("polished_granite_slab", new SlabBlock(AbstractBlock.Settings.copy(POLISHED_GRANITE)));
	public static final Block SMOOTH_RED_SANDSTONE_SLAB = register("smooth_red_sandstone_slab", new SlabBlock(AbstractBlock.Settings.copy(SMOOTH_RED_SANDSTONE)));
	public static final Block MOSSY_STONE_BRICK_SLAB = register("mossy_stone_brick_slab", new SlabBlock(AbstractBlock.Settings.copy(MOSSY_STONE_BRICKS)));
	public static final Block POLISHED_DIORITE_SLAB = register("polished_diorite_slab", new SlabBlock(AbstractBlock.Settings.copy(POLISHED_DIORITE)));
	public static final Block MOSSY_COBBLESTONE_SLAB = register("mossy_cobblestone_slab", new SlabBlock(AbstractBlock.Settings.copy(MOSSY_COBBLESTONE)));
	public static final Block END_STONE_BRICK_SLAB = register("end_stone_brick_slab", new SlabBlock(AbstractBlock.Settings.copy(END_STONE_BRICKS)));
	public static final Block SMOOTH_SANDSTONE_SLAB = register("smooth_sandstone_slab", new SlabBlock(AbstractBlock.Settings.copy(SMOOTH_SANDSTONE)));
	public static final Block SMOOTH_QUARTZ_SLAB = register("smooth_quartz_slab", new SlabBlock(AbstractBlock.Settings.copy(SMOOTH_QUARTZ)));
	public static final Block GRANITE_SLAB = register("granite_slab", new SlabBlock(AbstractBlock.Settings.copy(GRANITE)));
	public static final Block ANDESITE_SLAB = register("andesite_slab", new SlabBlock(AbstractBlock.Settings.copy(ANDESITE)));
	public static final Block RED_NETHER_BRICK_SLAB = register("red_nether_brick_slab", new SlabBlock(AbstractBlock.Settings.copy(RED_NETHER_BRICKS)));
	public static final Block POLISHED_ANDESITE_SLAB = register("polished_andesite_slab", new SlabBlock(AbstractBlock.Settings.copy(POLISHED_ANDESITE)));
	public static final Block DIORITE_SLAB = register("diorite_slab", new SlabBlock(AbstractBlock.Settings.copy(DIORITE)));
	public static final Block BRICK_WALL = register("brick_wall", new WallBlock(AbstractBlock.Settings.copy(BRICKS)));
	public static final Block PRISMARINE_WALL = register("prismarine_wall", new WallBlock(AbstractBlock.Settings.copy(PRISMARINE)));
	public static final Block RED_SANDSTONE_WALL = register("red_sandstone_wall", new WallBlock(AbstractBlock.Settings.copy(RED_SANDSTONE)));
	public static final Block MOSSY_STONE_BRICK_WALL = register("mossy_stone_brick_wall", new WallBlock(AbstractBlock.Settings.copy(MOSSY_STONE_BRICKS)));
	public static final Block GRANITE_WALL = register("granite_wall", new WallBlock(AbstractBlock.Settings.copy(GRANITE)));
	public static final Block STONE_BRICK_WALL = register("stone_brick_wall", new WallBlock(AbstractBlock.Settings.copy(STONE_BRICKS)));
	public static final Block MUD_BRICK_WALL = register("mud_brick_wall", new WallBlock(AbstractBlock.Settings.copy(MUD_BRICKS)));
	public static final Block NETHER_BRICK_WALL = register("nether_brick_wall", new WallBlock(AbstractBlock.Settings.copy(NETHER_BRICKS)));
	public static final Block ANDESITE_WALL = register("andesite_wall", new WallBlock(AbstractBlock.Settings.copy(ANDESITE)));
	public static final Block RED_NETHER_BRICK_WALL = register("red_nether_brick_wall", new WallBlock(AbstractBlock.Settings.copy(RED_NETHER_BRICKS)));
	public static final Block SANDSTONE_WALL = register("sandstone_wall", new WallBlock(AbstractBlock.Settings.copy(SANDSTONE)));
	public static final Block END_STONE_BRICK_WALL = register("end_stone_brick_wall", new WallBlock(AbstractBlock.Settings.copy(END_STONE_BRICKS)));
	public static final Block DIORITE_WALL = register("diorite_wall", new WallBlock(AbstractBlock.Settings.copy(DIORITE)));
	public static final Block SCAFFOLDING = register(
		"scaffolding",
		new ScaffoldingBlock(AbstractBlock.Settings.of(Material.DECORATION, MapColor.PALE_YELLOW).noCollision().sounds(BlockSoundGroup.SCAFFOLDING).dynamicBounds())
	);
	public static final Block LOOM = register("loom", new LoomBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block BARREL = register("barrel", new BarrelBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block SMOKER = register(
		"smoker", new SmokerBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).luminance(createLightLevelFromLitBlockState(13)))
	);
	public static final Block BLAST_FURNACE = register(
		"blast_furnace",
		new BlastFurnaceBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).luminance(createLightLevelFromLitBlockState(13)))
	);
	public static final Block CARTOGRAPHY_TABLE = register(
		"cartography_table", new CartographyTableBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block FLETCHING_TABLE = register(
		"fletching_table", new FletchingTableBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block GRINDSTONE = register(
		"grindstone",
		new GrindstoneBlock(AbstractBlock.Settings.of(Material.REPAIR_STATION, MapColor.IRON_GRAY).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.STONE))
	);
	public static final Block LECTERN = register("lectern", new LecternBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block SMITHING_TABLE = register(
		"smithing_table", new SmithingTableBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STONECUTTER = register("stonecutter", new StonecutterBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F)));
	public static final Block BELL = register(
		"bell", new BellBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.GOLD).requiresTool().strength(5.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block LANTERN = register(
		"lantern",
		new LanternBlock(AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(3.5F).sounds(BlockSoundGroup.LANTERN).luminance(state -> 15).nonOpaque())
	);
	public static final Block SOUL_LANTERN = register(
		"soul_lantern",
		new LanternBlock(AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(3.5F).sounds(BlockSoundGroup.LANTERN).luminance(state -> 10).nonOpaque())
	);
	public static final Block CAMPFIRE = register(
		"campfire",
		new CampfireBlock(
			true,
			1,
			AbstractBlock.Settings.of(Material.WOOD, MapColor.SPRUCE_BROWN)
				.strength(2.0F)
				.sounds(BlockSoundGroup.WOOD)
				.luminance(createLightLevelFromLitBlockState(15))
				.nonOpaque()
		)
	);
	public static final Block SOUL_CAMPFIRE = register(
		"soul_campfire",
		new CampfireBlock(
			false,
			2,
			AbstractBlock.Settings.of(Material.WOOD, MapColor.SPRUCE_BROWN)
				.strength(2.0F)
				.sounds(BlockSoundGroup.WOOD)
				.luminance(createLightLevelFromLitBlockState(10))
				.nonOpaque()
		)
	);
	public static final Block SWEET_BERRY_BUSH = register(
		"sweet_berry_bush", new SweetBerryBushBlock(AbstractBlock.Settings.of(Material.PLANT).ticksRandomly().noCollision().sounds(BlockSoundGroup.SWEET_BERRY_BUSH))
	);
	public static final Block WARPED_STEM = register("warped_stem", createNetherStemBlock(MapColor.DARK_AQUA));
	public static final Block STRIPPED_WARPED_STEM = register("stripped_warped_stem", createNetherStemBlock(MapColor.DARK_AQUA));
	public static final Block WARPED_HYPHAE = register(
		"warped_hyphae", new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MapColor.DARK_DULL_PINK).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM))
	);
	public static final Block STRIPPED_WARPED_HYPHAE = register(
		"stripped_warped_hyphae",
		new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MapColor.DARK_DULL_PINK).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM))
	);
	public static final Block WARPED_NYLIUM = register(
		"warped_nylium",
		new NyliumBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.TEAL).requiresTool().strength(0.4F).sounds(BlockSoundGroup.NYLIUM).ticksRandomly())
	);
	public static final Block WARPED_FUNGUS = register(
		"warped_fungus",
		new FungusBlock(
			AbstractBlock.Settings.of(Material.PLANT, MapColor.CYAN).breakInstantly().noCollision().sounds(BlockSoundGroup.FUNGUS),
			() -> TreeConfiguredFeatures.WARPED_FUNGUS_PLANTED
		)
	);
	public static final Block WARPED_WART_BLOCK = register(
		"warped_wart_block", new Block(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MapColor.BRIGHT_TEAL).strength(1.0F).sounds(BlockSoundGroup.WART_BLOCK))
	);
	public static final Block WARPED_ROOTS = register(
		"warped_roots",
		new RootsBlock(
			AbstractBlock.Settings.of(Material.NETHER_SHOOTS, MapColor.CYAN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.ROOTS)
				.offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block NETHER_SPROUTS = register(
		"nether_sprouts",
		new SproutsBlock(
			AbstractBlock.Settings.of(Material.NETHER_SHOOTS, MapColor.CYAN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.NETHER_SPROUTS)
				.offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block CRIMSON_STEM = register("crimson_stem", createNetherStemBlock(MapColor.DULL_PINK));
	public static final Block STRIPPED_CRIMSON_STEM = register("stripped_crimson_stem", createNetherStemBlock(MapColor.DULL_PINK));
	public static final Block CRIMSON_HYPHAE = register(
		"crimson_hyphae", new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MapColor.DARK_CRIMSON).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM))
	);
	public static final Block STRIPPED_CRIMSON_HYPHAE = register(
		"stripped_crimson_hyphae",
		new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MapColor.DARK_CRIMSON).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM))
	);
	public static final Block CRIMSON_NYLIUM = register(
		"crimson_nylium",
		new NyliumBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.DULL_RED).requiresTool().strength(0.4F).sounds(BlockSoundGroup.NYLIUM).ticksRandomly())
	);
	public static final Block CRIMSON_FUNGUS = register(
		"crimson_fungus",
		new FungusBlock(
			AbstractBlock.Settings.of(Material.PLANT, MapColor.DARK_RED).breakInstantly().noCollision().sounds(BlockSoundGroup.FUNGUS),
			() -> TreeConfiguredFeatures.CRIMSON_FUNGUS_PLANTED
		)
	);
	public static final Block SHROOMLIGHT = register(
		"shroomlight",
		new Block(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MapColor.RED).strength(1.0F).sounds(BlockSoundGroup.SHROOMLIGHT).luminance(state -> 15))
	);
	public static final Block WEEPING_VINES = register(
		"weeping_vines",
		new WeepingVinesBlock(
			AbstractBlock.Settings.of(Material.PLANT, MapColor.DARK_RED).ticksRandomly().noCollision().breakInstantly().sounds(BlockSoundGroup.WEEPING_VINES)
		)
	);
	public static final Block WEEPING_VINES_PLANT = register(
		"weeping_vines_plant",
		new WeepingVinesPlantBlock(AbstractBlock.Settings.of(Material.PLANT, MapColor.DARK_RED).noCollision().breakInstantly().sounds(BlockSoundGroup.WEEPING_VINES))
	);
	public static final Block TWISTING_VINES = register(
		"twisting_vines",
		new TwistingVinesBlock(
			AbstractBlock.Settings.of(Material.PLANT, MapColor.CYAN).ticksRandomly().noCollision().breakInstantly().sounds(BlockSoundGroup.WEEPING_VINES)
		)
	);
	public static final Block TWISTING_VINES_PLANT = register(
		"twisting_vines_plant",
		new TwistingVinesPlantBlock(AbstractBlock.Settings.of(Material.PLANT, MapColor.CYAN).noCollision().breakInstantly().sounds(BlockSoundGroup.WEEPING_VINES))
	);
	public static final Block CRIMSON_ROOTS = register(
		"crimson_roots",
		new RootsBlock(
			AbstractBlock.Settings.of(Material.NETHER_SHOOTS, MapColor.DARK_RED)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.ROOTS)
				.offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block CRIMSON_PLANKS = register(
		"crimson_planks", new Block(AbstractBlock.Settings.of(Material.NETHER_WOOD, MapColor.DULL_PINK).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_PLANKS = register(
		"warped_planks", new Block(AbstractBlock.Settings.of(Material.NETHER_WOOD, MapColor.DARK_AQUA).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_SLAB = register(
		"crimson_slab",
		new SlabBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, CRIMSON_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_SLAB = register(
		"warped_slab",
		new SlabBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, WARPED_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_PRESSURE_PLATE = register(
		"crimson_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.NETHER_WOOD, CRIMSON_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block WARPED_PRESSURE_PLATE = register(
		"warped_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.NETHER_WOOD, WARPED_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block CRIMSON_FENCE = register(
		"crimson_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, CRIMSON_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_FENCE = register(
		"warped_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, WARPED_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_TRAPDOOR = register(
		"crimson_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD, CRIMSON_PLANKS.getDefaultMapColor())
				.strength(3.0F)
				.sounds(BlockSoundGroup.WOOD)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
		)
	);
	public static final Block WARPED_TRAPDOOR = register(
		"warped_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD, WARPED_PLANKS.getDefaultMapColor())
				.strength(3.0F)
				.sounds(BlockSoundGroup.WOOD)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
		)
	);
	public static final Block CRIMSON_FENCE_GATE = register(
		"crimson_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, CRIMSON_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_FENCE_GATE = register(
		"warped_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, WARPED_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_STAIRS = register(
		"crimson_stairs", new StairsBlock(CRIMSON_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(CRIMSON_PLANKS))
	);
	public static final Block WARPED_STAIRS = register(
		"warped_stairs", new StairsBlock(WARPED_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(WARPED_PLANKS))
	);
	public static final Block CRIMSON_BUTTON = register(
		"crimson_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_BUTTON = register(
		"warped_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_DOOR = register(
		"crimson_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, CRIMSON_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block WARPED_DOOR = register(
		"warped_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, WARPED_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block CRIMSON_SIGN = register(
		"crimson_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD, CRIMSON_PLANKS.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD),
			SignType.CRIMSON
		)
	);
	public static final Block WARPED_SIGN = register(
		"warped_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD, WARPED_PLANKS.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD),
			SignType.WARPED
		)
	);
	public static final Block CRIMSON_WALL_SIGN = register(
		"crimson_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD, CRIMSON_PLANKS.getDefaultMapColor())
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(CRIMSON_SIGN),
			SignType.CRIMSON
		)
	);
	public static final Block WARPED_WALL_SIGN = register(
		"warped_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD, WARPED_PLANKS.getDefaultMapColor())
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(WARPED_SIGN),
			SignType.WARPED
		)
	);
	public static final Block STRUCTURE_BLOCK = register(
		"structure_block",
		new StructureBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.LIGHT_GRAY).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block JIGSAW = register(
		"jigsaw", new JigsawBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.LIGHT_GRAY).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block COMPOSTER = register(
		"composter", new ComposterBlock(AbstractBlock.Settings.of(Material.WOOD).strength(0.6F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block TARGET = register(
		"target", new TargetBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MapColor.OFF_WHITE).strength(0.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block BEE_NEST = register(
		"bee_nest", new BeehiveBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.YELLOW).strength(0.3F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BEEHIVE = register("beehive", new BeehiveBlock(AbstractBlock.Settings.of(Material.WOOD).strength(0.6F).sounds(BlockSoundGroup.WOOD)));
	public static final Block HONEY_BLOCK = register(
		"honey_block",
		new HoneyBlock(
			AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MapColor.ORANGE)
				.velocityMultiplier(0.4F)
				.jumpVelocityMultiplier(0.5F)
				.nonOpaque()
				.sounds(BlockSoundGroup.HONEY)
		)
	);
	public static final Block HONEYCOMB_BLOCK = register(
		"honeycomb_block", new Block(AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MapColor.ORANGE).strength(0.6F).sounds(BlockSoundGroup.CORAL))
	);
	public static final Block NETHERITE_BLOCK = register(
		"netherite_block",
		new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.BLACK).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.NETHERITE))
	);
	public static final Block ANCIENT_DEBRIS = register(
		"ancient_debris",
		new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.BLACK).requiresTool().strength(30.0F, 1200.0F).sounds(BlockSoundGroup.ANCIENT_DEBRIS))
	);
	public static final Block CRYING_OBSIDIAN = register(
		"crying_obsidian",
		new CryingObsidianBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(50.0F, 1200.0F).luminance(state -> 10))
	);
	public static final Block RESPAWN_ANCHOR = register(
		"respawn_anchor",
		new RespawnAnchorBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK)
				.requiresTool()
				.strength(50.0F, 1200.0F)
				.luminance(state -> RespawnAnchorBlock.getLightLevel(state, 15))
		)
	);
	public static final Block POTTED_CRIMSON_FUNGUS = register(
		"potted_crimson_fungus", new FlowerPotBlock(CRIMSON_FUNGUS, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_WARPED_FUNGUS = register(
		"potted_warped_fungus", new FlowerPotBlock(WARPED_FUNGUS, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_CRIMSON_ROOTS = register(
		"potted_crimson_roots", new FlowerPotBlock(CRIMSON_ROOTS, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_WARPED_ROOTS = register(
		"potted_warped_roots", new FlowerPotBlock(WARPED_ROOTS, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block LODESTONE = register(
		"lodestone", new Block(AbstractBlock.Settings.of(Material.REPAIR_STATION).requiresTool().strength(3.5F).sounds(BlockSoundGroup.LODESTONE))
	);
	public static final Block BLACKSTONE = register(
		"blackstone", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block BLACKSTONE_STAIRS = register(
		"blackstone_stairs", new StairsBlock(BLACKSTONE.getDefaultState(), AbstractBlock.Settings.copy(BLACKSTONE))
	);
	public static final Block BLACKSTONE_WALL = register("blackstone_wall", new WallBlock(AbstractBlock.Settings.copy(BLACKSTONE)));
	public static final Block BLACKSTONE_SLAB = register("blackstone_slab", new SlabBlock(AbstractBlock.Settings.copy(BLACKSTONE).strength(2.0F, 6.0F)));
	public static final Block POLISHED_BLACKSTONE = register("polished_blackstone", new Block(AbstractBlock.Settings.copy(BLACKSTONE).strength(2.0F, 6.0F)));
	public static final Block POLISHED_BLACKSTONE_BRICKS = register(
		"polished_blackstone_bricks", new Block(AbstractBlock.Settings.copy(POLISHED_BLACKSTONE).strength(1.5F, 6.0F))
	);
	public static final Block CRACKED_POLISHED_BLACKSTONE_BRICKS = register(
		"cracked_polished_blackstone_bricks", new Block(AbstractBlock.Settings.copy(POLISHED_BLACKSTONE_BRICKS))
	);
	public static final Block CHISELED_POLISHED_BLACKSTONE = register(
		"chiseled_polished_blackstone", new Block(AbstractBlock.Settings.copy(POLISHED_BLACKSTONE).strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_BLACKSTONE_BRICK_SLAB = register(
		"polished_blackstone_brick_slab", new SlabBlock(AbstractBlock.Settings.copy(POLISHED_BLACKSTONE_BRICKS).strength(2.0F, 6.0F))
	);
	public static final Block POLISHED_BLACKSTONE_BRICK_STAIRS = register(
		"polished_blackstone_brick_stairs", new StairsBlock(POLISHED_BLACKSTONE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(POLISHED_BLACKSTONE_BRICKS))
	);
	public static final Block POLISHED_BLACKSTONE_BRICK_WALL = register(
		"polished_blackstone_brick_wall", new WallBlock(AbstractBlock.Settings.copy(POLISHED_BLACKSTONE_BRICKS))
	);
	public static final Block GILDED_BLACKSTONE = register(
		"gilded_blackstone", new Block(AbstractBlock.Settings.copy(BLACKSTONE).sounds(BlockSoundGroup.GILDED_BLACKSTONE))
	);
	public static final Block POLISHED_BLACKSTONE_STAIRS = register(
		"polished_blackstone_stairs", new StairsBlock(POLISHED_BLACKSTONE.getDefaultState(), AbstractBlock.Settings.copy(POLISHED_BLACKSTONE))
	);
	public static final Block POLISHED_BLACKSTONE_SLAB = register("polished_blackstone_slab", new SlabBlock(AbstractBlock.Settings.copy(POLISHED_BLACKSTONE)));
	public static final Block POLISHED_BLACKSTONE_PRESSURE_PLATE = register(
		"polished_blackstone_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().noCollision().strength(0.5F)
		)
	);
	public static final Block POLISHED_BLACKSTONE_BUTTON = register(
		"polished_blackstone_button", new StoneButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F))
	);
	public static final Block POLISHED_BLACKSTONE_WALL = register("polished_blackstone_wall", new WallBlock(AbstractBlock.Settings.copy(POLISHED_BLACKSTONE)));
	public static final Block CHISELED_NETHER_BRICKS = register(
		"chiseled_nether_bricks",
		new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block CRACKED_NETHER_BRICKS = register(
		"cracked_nether_bricks",
		new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block QUARTZ_BRICKS = register("quartz_bricks", new Block(AbstractBlock.Settings.copy(QUARTZ_BLOCK)));
	public static final Block CANDLE = register(
		"candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.PALE_YELLOW)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block WHITE_CANDLE = register(
		"white_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.WHITE_GRAY)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block ORANGE_CANDLE = register(
		"orange_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.ORANGE)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block MAGENTA_CANDLE = register(
		"magenta_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.MAGENTA)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block LIGHT_BLUE_CANDLE = register(
		"light_blue_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.LIGHT_BLUE)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block YELLOW_CANDLE = register(
		"yellow_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.YELLOW)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block LIME_CANDLE = register(
		"lime_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.LIME)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block PINK_CANDLE = register(
		"pink_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.PINK)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block GRAY_CANDLE = register(
		"gray_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.GRAY)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block LIGHT_GRAY_CANDLE = register(
		"light_gray_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.LIGHT_GRAY)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block CYAN_CANDLE = register(
		"cyan_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.CYAN)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block PURPLE_CANDLE = register(
		"purple_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.PURPLE)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block BLUE_CANDLE = register(
		"blue_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.BLUE)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block BROWN_CANDLE = register(
		"brown_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.BROWN)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block GREEN_CANDLE = register(
		"green_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.GREEN)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block RED_CANDLE = register(
		"red_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.RED)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block BLACK_CANDLE = register(
		"black_candle",
		new CandleBlock(
			AbstractBlock.Settings.of(Material.DECORATION, MapColor.BLACK)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
		)
	);
	public static final Block CANDLE_CAKE = register(
		"candle_cake", new CandleCakeBlock(CANDLE, AbstractBlock.Settings.copy(CAKE).luminance(createLightLevelFromLitBlockState(3)))
	);
	public static final Block WHITE_CANDLE_CAKE = register("white_candle_cake", new CandleCakeBlock(WHITE_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block ORANGE_CANDLE_CAKE = register("orange_candle_cake", new CandleCakeBlock(ORANGE_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block MAGENTA_CANDLE_CAKE = register("magenta_candle_cake", new CandleCakeBlock(MAGENTA_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block LIGHT_BLUE_CANDLE_CAKE = register(
		"light_blue_candle_cake", new CandleCakeBlock(LIGHT_BLUE_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE))
	);
	public static final Block YELLOW_CANDLE_CAKE = register("yellow_candle_cake", new CandleCakeBlock(YELLOW_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block LIME_CANDLE_CAKE = register("lime_candle_cake", new CandleCakeBlock(LIME_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block PINK_CANDLE_CAKE = register("pink_candle_cake", new CandleCakeBlock(PINK_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block GRAY_CANDLE_CAKE = register("gray_candle_cake", new CandleCakeBlock(GRAY_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block LIGHT_GRAY_CANDLE_CAKE = register(
		"light_gray_candle_cake", new CandleCakeBlock(LIGHT_GRAY_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE))
	);
	public static final Block CYAN_CANDLE_CAKE = register("cyan_candle_cake", new CandleCakeBlock(CYAN_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block PURPLE_CANDLE_CAKE = register("purple_candle_cake", new CandleCakeBlock(PURPLE_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block BLUE_CANDLE_CAKE = register("blue_candle_cake", new CandleCakeBlock(BLUE_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block BROWN_CANDLE_CAKE = register("brown_candle_cake", new CandleCakeBlock(BROWN_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block GREEN_CANDLE_CAKE = register("green_candle_cake", new CandleCakeBlock(GREEN_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block RED_CANDLE_CAKE = register("red_candle_cake", new CandleCakeBlock(RED_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block BLACK_CANDLE_CAKE = register("black_candle_cake", new CandleCakeBlock(BLACK_CANDLE, AbstractBlock.Settings.copy(CANDLE_CAKE)));
	public static final Block AMETHYST_BLOCK = register(
		"amethyst_block",
		new AmethystBlock(AbstractBlock.Settings.of(Material.AMETHYST, MapColor.PURPLE).strength(1.5F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool())
	);
	public static final Block BUDDING_AMETHYST = register(
		"budding_amethyst",
		new BuddingAmethystBlock(AbstractBlock.Settings.of(Material.AMETHYST).ticksRandomly().strength(1.5F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool())
	);
	public static final Block AMETHYST_CLUSTER = register(
		"amethyst_cluster",
		new AmethystClusterBlock(
			7, 3, AbstractBlock.Settings.of(Material.AMETHYST).nonOpaque().ticksRandomly().sounds(BlockSoundGroup.AMETHYST_CLUSTER).strength(1.5F).luminance(state -> 5)
		)
	);
	public static final Block LARGE_AMETHYST_BUD = register(
		"large_amethyst_bud",
		new AmethystClusterBlock(5, 3, AbstractBlock.Settings.copy(AMETHYST_CLUSTER).sounds(BlockSoundGroup.MEDIUM_AMETHYST_BUD).luminance(state -> 4))
	);
	public static final Block MEDIUM_AMETHYST_BUD = register(
		"medium_amethyst_bud",
		new AmethystClusterBlock(4, 3, AbstractBlock.Settings.copy(AMETHYST_CLUSTER).sounds(BlockSoundGroup.LARGE_AMETHYST_BUD).luminance(state -> 2))
	);
	public static final Block SMALL_AMETHYST_BUD = register(
		"small_amethyst_bud",
		new AmethystClusterBlock(3, 4, AbstractBlock.Settings.copy(AMETHYST_CLUSTER).sounds(BlockSoundGroup.SMALL_AMETHYST_BUD).luminance(state -> 1))
	);
	public static final Block TUFF = register(
		"tuff", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_GRAY).sounds(BlockSoundGroup.TUFF).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block CALCITE = register(
		"calcite", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_WHITE).sounds(BlockSoundGroup.CALCITE).requiresTool().strength(0.75F))
	);
	public static final Block TINTED_GLASS = register(
		"tinted_glass",
		new TintedGlassBlock(
			AbstractBlock.Settings.copy(GLASS)
				.mapColor(MapColor.GRAY)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.solidBlock(Blocks::never)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		)
	);
	public static final Block POWDER_SNOW = register(
		"powder_snow", new PowderSnowBlock(AbstractBlock.Settings.of(Material.POWDER_SNOW).strength(0.25F).sounds(BlockSoundGroup.POWDER_SNOW).dynamicBounds())
	);
	public static final Block SCULK_SENSOR = register(
		"sculk_sensor",
		new SculkSensorBlock(
			AbstractBlock.Settings.of(Material.SCULK, MapColor.CYAN)
				.strength(1.5F)
				.sounds(BlockSoundGroup.SCULK_SENSOR)
				.luminance(state -> 1)
				.emissiveLighting((state, world, pos) -> SculkSensorBlock.getPhase(state) == SculkSensorPhase.ACTIVE),
			8
		)
	);
	public static final Block SCULK = register("sculk", new SculkBlock(AbstractBlock.Settings.of(Material.SCULK).strength(0.2F).sounds(BlockSoundGroup.SCULK)));
	public static final Block SCULK_VEIN = register(
		"sculk_vein", new SculkVeinBlock(AbstractBlock.Settings.of(Material.SCULK).noCollision().strength(0.2F).sounds(BlockSoundGroup.SCULK_VEIN))
	);
	public static final Block SCULK_CATALYST = register(
		"sculk_catalyst",
		new SculkCatalystBlock(AbstractBlock.Settings.of(Material.SCULK).strength(3.0F, 3.0F).sounds(BlockSoundGroup.SCULK_CATALYST).luminance(state -> 6))
	);
	public static final Block SCULK_SHRIEKER = register(
		"sculk_shrieker",
		new SculkShriekerBlock(AbstractBlock.Settings.of(Material.SCULK, MapColor.BLACK).strength(3.0F, 3.0F).sounds(BlockSoundGroup.SCULK_SHRIEKER))
	);
	public static final Block OXIDIZED_COPPER = register(
		"oxidized_copper",
		new OxidizableBlock(
			Oxidizable.OxidationLevel.OXIDIZED,
			AbstractBlock.Settings.of(Material.METAL, MapColor.TEAL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)
		)
	);
	public static final Block WEATHERED_COPPER = register(
		"weathered_copper",
		new OxidizableBlock(
			Oxidizable.OxidationLevel.WEATHERED,
			AbstractBlock.Settings.of(Material.METAL, MapColor.DARK_AQUA).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)
		)
	);
	public static final Block EXPOSED_COPPER = register(
		"exposed_copper",
		new OxidizableBlock(
			Oxidizable.OxidationLevel.EXPOSED,
			AbstractBlock.Settings.of(Material.METAL, MapColor.TERRACOTTA_LIGHT_GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)
		)
	);
	public static final Block COPPER_BLOCK = register(
		"copper_block",
		new OxidizableBlock(
			Oxidizable.OxidationLevel.UNAFFECTED,
			AbstractBlock.Settings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)
		)
	);
	public static final Block COPPER_ORE = register("copper_ore", new OreBlock(AbstractBlock.Settings.copy(IRON_ORE)));
	public static final Block DEEPSLATE_COPPER_ORE = register(
		"deepslate_copper_ore",
		new OreBlock(AbstractBlock.Settings.copy(COPPER_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE))
	);
	public static final Block OXIDIZED_CUT_COPPER = register(
		"oxidized_cut_copper", new OxidizableBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(OXIDIZED_COPPER))
	);
	public static final Block WEATHERED_CUT_COPPER = register(
		"weathered_cut_copper", new OxidizableBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(WEATHERED_COPPER))
	);
	public static final Block EXPOSED_CUT_COPPER = register(
		"exposed_cut_copper", new OxidizableBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(EXPOSED_COPPER))
	);
	public static final Block CUT_COPPER = register(
		"cut_copper", new OxidizableBlock(Oxidizable.OxidationLevel.UNAFFECTED, AbstractBlock.Settings.copy(COPPER_BLOCK))
	);
	public static final Block OXIDIZED_CUT_COPPER_STAIRS = register(
		"oxidized_cut_copper_stairs",
		new OxidizableStairsBlock(Oxidizable.OxidationLevel.OXIDIZED, OXIDIZED_CUT_COPPER.getDefaultState(), AbstractBlock.Settings.copy(OXIDIZED_CUT_COPPER))
	);
	public static final Block WEATHERED_CUT_COPPER_STAIRS = register(
		"weathered_cut_copper_stairs",
		new OxidizableStairsBlock(Oxidizable.OxidationLevel.WEATHERED, WEATHERED_CUT_COPPER.getDefaultState(), AbstractBlock.Settings.copy(WEATHERED_COPPER))
	);
	public static final Block EXPOSED_CUT_COPPER_STAIRS = register(
		"exposed_cut_copper_stairs",
		new OxidizableStairsBlock(Oxidizable.OxidationLevel.EXPOSED, EXPOSED_CUT_COPPER.getDefaultState(), AbstractBlock.Settings.copy(EXPOSED_COPPER))
	);
	public static final Block CUT_COPPER_STAIRS = register(
		"cut_copper_stairs", new OxidizableStairsBlock(Oxidizable.OxidationLevel.UNAFFECTED, CUT_COPPER.getDefaultState(), AbstractBlock.Settings.copy(COPPER_BLOCK))
	);
	public static final Block OXIDIZED_CUT_COPPER_SLAB = register(
		"oxidized_cut_copper_slab", new OxidizableSlabBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(OXIDIZED_CUT_COPPER).requiresTool())
	);
	public static final Block WEATHERED_CUT_COPPER_SLAB = register(
		"weathered_cut_copper_slab", new OxidizableSlabBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(WEATHERED_CUT_COPPER).requiresTool())
	);
	public static final Block EXPOSED_CUT_COPPER_SLAB = register(
		"exposed_cut_copper_slab", new OxidizableSlabBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(EXPOSED_CUT_COPPER).requiresTool())
	);
	public static final Block CUT_COPPER_SLAB = register(
		"cut_copper_slab", new OxidizableSlabBlock(Oxidizable.OxidationLevel.UNAFFECTED, AbstractBlock.Settings.copy(CUT_COPPER).requiresTool())
	);
	public static final Block WAXED_COPPER_BLOCK = register("waxed_copper_block", new Block(AbstractBlock.Settings.copy(COPPER_BLOCK)));
	public static final Block WAXED_WEATHERED_COPPER = register("waxed_weathered_copper", new Block(AbstractBlock.Settings.copy(WEATHERED_COPPER)));
	public static final Block WAXED_EXPOSED_COPPER = register("waxed_exposed_copper", new Block(AbstractBlock.Settings.copy(EXPOSED_COPPER)));
	public static final Block WAXED_OXIDIZED_COPPER = register("waxed_oxidized_copper", new Block(AbstractBlock.Settings.copy(OXIDIZED_COPPER)));
	public static final Block WAXED_OXIDIZED_CUT_COPPER = register("waxed_oxidized_cut_copper", new Block(AbstractBlock.Settings.copy(OXIDIZED_COPPER)));
	public static final Block WAXED_WEATHERED_CUT_COPPER = register("waxed_weathered_cut_copper", new Block(AbstractBlock.Settings.copy(WEATHERED_COPPER)));
	public static final Block WAXED_EXPOSED_CUT_COPPER = register("waxed_exposed_cut_copper", new Block(AbstractBlock.Settings.copy(EXPOSED_COPPER)));
	public static final Block WAXED_CUT_COPPER = register("waxed_cut_copper", new Block(AbstractBlock.Settings.copy(COPPER_BLOCK)));
	public static final Block WAXED_OXIDIZED_CUT_COPPER_STAIRS = register(
		"waxed_oxidized_cut_copper_stairs", new StairsBlock(WAXED_OXIDIZED_CUT_COPPER.getDefaultState(), AbstractBlock.Settings.copy(OXIDIZED_COPPER))
	);
	public static final Block WAXED_WEATHERED_CUT_COPPER_STAIRS = register(
		"waxed_weathered_cut_copper_stairs", new StairsBlock(WAXED_WEATHERED_CUT_COPPER.getDefaultState(), AbstractBlock.Settings.copy(WEATHERED_COPPER))
	);
	public static final Block WAXED_EXPOSED_CUT_COPPER_STAIRS = register(
		"waxed_exposed_cut_copper_stairs", new StairsBlock(WAXED_EXPOSED_CUT_COPPER.getDefaultState(), AbstractBlock.Settings.copy(EXPOSED_COPPER))
	);
	public static final Block WAXED_CUT_COPPER_STAIRS = register(
		"waxed_cut_copper_stairs", new StairsBlock(WAXED_CUT_COPPER.getDefaultState(), AbstractBlock.Settings.copy(COPPER_BLOCK))
	);
	public static final Block WAXED_OXIDIZED_CUT_COPPER_SLAB = register(
		"waxed_oxidized_cut_copper_slab", new SlabBlock(AbstractBlock.Settings.copy(WAXED_OXIDIZED_CUT_COPPER).requiresTool())
	);
	public static final Block WAXED_WEATHERED_CUT_COPPER_SLAB = register(
		"waxed_weathered_cut_copper_slab", new SlabBlock(AbstractBlock.Settings.copy(WAXED_WEATHERED_CUT_COPPER).requiresTool())
	);
	public static final Block WAXED_EXPOSED_CUT_COPPER_SLAB = register(
		"waxed_exposed_cut_copper_slab", new SlabBlock(AbstractBlock.Settings.copy(WAXED_EXPOSED_CUT_COPPER).requiresTool())
	);
	public static final Block WAXED_CUT_COPPER_SLAB = register(
		"waxed_cut_copper_slab", new SlabBlock(AbstractBlock.Settings.copy(WAXED_CUT_COPPER).requiresTool())
	);
	public static final Block LIGHTNING_ROD = register(
		"lightning_rod",
		new LightningRodBlock(
			AbstractBlock.Settings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER).nonOpaque()
		)
	);
	public static final Block POINTED_DRIPSTONE = register(
		"pointed_dripstone",
		new PointedDripstoneBlock(
			AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_BROWN)
				.nonOpaque()
				.sounds(BlockSoundGroup.POINTED_DRIPSTONE)
				.ticksRandomly()
				.strength(1.5F, 3.0F)
				.dynamicBounds()
				.offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block DRIPSTONE_BLOCK = register(
		"dripstone_block",
		new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_BROWN).sounds(BlockSoundGroup.DRIPSTONE_BLOCK).requiresTool().strength(1.5F, 1.0F))
	);
	public static final Block CAVE_VINES = register(
		"cave_vines",
		new CaveVinesHeadBlock(
			AbstractBlock.Settings.of(Material.PLANT)
				.ticksRandomly()
				.noCollision()
				.luminance(CaveVines.getLuminanceSupplier(14))
				.breakInstantly()
				.sounds(BlockSoundGroup.CAVE_VINES)
		)
	);
	public static final Block CAVE_VINES_PLANT = register(
		"cave_vines_plant",
		new CaveVinesBodyBlock(
			AbstractBlock.Settings.of(Material.PLANT).noCollision().luminance(CaveVines.getLuminanceSupplier(14)).breakInstantly().sounds(BlockSoundGroup.CAVE_VINES)
		)
	);
	public static final Block SPORE_BLOSSOM = register(
		"spore_blossom", new SporeBlossomBlock(AbstractBlock.Settings.of(Material.PLANT).breakInstantly().noCollision().sounds(BlockSoundGroup.SPORE_BLOSSOM))
	);
	public static final Block AZALEA = register(
		"azalea", new AzaleaBlock(AbstractBlock.Settings.of(Material.PLANT).breakInstantly().sounds(BlockSoundGroup.AZALEA).nonOpaque())
	);
	public static final Block FLOWERING_AZALEA = register(
		"flowering_azalea", new AzaleaBlock(AbstractBlock.Settings.of(Material.PLANT).breakInstantly().sounds(BlockSoundGroup.FLOWERING_AZALEA).nonOpaque())
	);
	public static final Block MOSS_CARPET = register(
		"moss_carpet", new CarpetBlock(AbstractBlock.Settings.of(Material.PLANT, MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.MOSS_CARPET))
	);
	public static final Block MOSS_BLOCK = register(
		"moss_block", new MossBlock(AbstractBlock.Settings.of(Material.MOSS_BLOCK, MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.MOSS_BLOCK))
	);
	public static final Block BIG_DRIPLEAF = register(
		"big_dripleaf", new BigDripleafBlock(AbstractBlock.Settings.of(Material.PLANT).strength(0.1F).sounds(BlockSoundGroup.BIG_DRIPLEAF))
	);
	public static final Block BIG_DRIPLEAF_STEM = register(
		"big_dripleaf_stem", new BigDripleafStemBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().strength(0.1F).sounds(BlockSoundGroup.BIG_DRIPLEAF))
	);
	public static final Block SMALL_DRIPLEAF = register(
		"small_dripleaf",
		new SmallDripleafBlock(
			AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.SMALL_DRIPLEAF).offsetType(AbstractBlock.OffsetType.XYZ)
		)
	);
	public static final Block HANGING_ROOTS = register(
		"hanging_roots",
		new HangingRootsBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT, MapColor.DIRT_BROWN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.HANGING_ROOTS)
				.offsetType(AbstractBlock.OffsetType.XZ)
		)
	);
	public static final Block ROOTED_DIRT = register(
		"rooted_dirt", new RootedDirtBlock(AbstractBlock.Settings.of(Material.SOIL, MapColor.DIRT_BROWN).strength(0.5F).sounds(BlockSoundGroup.ROOTED_DIRT))
	);
	public static final Block MUD = register(
		"mud",
		new MudBlock(
			AbstractBlock.Settings.copy(DIRT)
				.mapColor(MapColor.TERRACOTTA_CYAN)
				.allowsSpawning(Blocks::always)
				.solidBlock(Blocks::always)
				.blockVision(Blocks::always)
				.suffocates(Blocks::always)
				.sounds(BlockSoundGroup.MUD)
		)
	);
	public static final Block DEEPSLATE = register(
		"deepslate",
		new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.DEEPSLATE))
	);
	public static final Block COBBLED_DEEPSLATE = register("cobbled_deepslate", new Block(AbstractBlock.Settings.copy(DEEPSLATE).strength(3.5F, 6.0F)));
	public static final Block COBBLED_DEEPSLATE_STAIRS = register(
		"cobbled_deepslate_stairs", new StairsBlock(COBBLED_DEEPSLATE.getDefaultState(), AbstractBlock.Settings.copy(COBBLED_DEEPSLATE))
	);
	public static final Block COBBLED_DEEPSLATE_SLAB = register("cobbled_deepslate_slab", new SlabBlock(AbstractBlock.Settings.copy(COBBLED_DEEPSLATE)));
	public static final Block COBBLED_DEEPSLATE_WALL = register("cobbled_deepslate_wall", new WallBlock(AbstractBlock.Settings.copy(COBBLED_DEEPSLATE)));
	public static final Block POLISHED_DEEPSLATE = register(
		"polished_deepslate", new Block(AbstractBlock.Settings.copy(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.POLISHED_DEEPSLATE))
	);
	public static final Block POLISHED_DEEPSLATE_STAIRS = register(
		"polished_deepslate_stairs", new StairsBlock(POLISHED_DEEPSLATE.getDefaultState(), AbstractBlock.Settings.copy(POLISHED_DEEPSLATE))
	);
	public static final Block POLISHED_DEEPSLATE_SLAB = register("polished_deepslate_slab", new SlabBlock(AbstractBlock.Settings.copy(POLISHED_DEEPSLATE)));
	public static final Block POLISHED_DEEPSLATE_WALL = register("polished_deepslate_wall", new WallBlock(AbstractBlock.Settings.copy(POLISHED_DEEPSLATE)));
	public static final Block DEEPSLATE_TILES = register(
		"deepslate_tiles", new Block(AbstractBlock.Settings.copy(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE_TILES))
	);
	public static final Block DEEPSLATE_TILE_STAIRS = register(
		"deepslate_tile_stairs", new StairsBlock(DEEPSLATE_TILES.getDefaultState(), AbstractBlock.Settings.copy(DEEPSLATE_TILES))
	);
	public static final Block DEEPSLATE_TILE_SLAB = register("deepslate_tile_slab", new SlabBlock(AbstractBlock.Settings.copy(DEEPSLATE_TILES)));
	public static final Block DEEPSLATE_TILE_WALL = register("deepslate_tile_wall", new WallBlock(AbstractBlock.Settings.copy(DEEPSLATE_TILES)));
	public static final Block DEEPSLATE_BRICKS = register(
		"deepslate_bricks", new Block(AbstractBlock.Settings.copy(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE_BRICKS))
	);
	public static final Block DEEPSLATE_BRICK_STAIRS = register(
		"deepslate_brick_stairs", new StairsBlock(DEEPSLATE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(DEEPSLATE_BRICKS))
	);
	public static final Block DEEPSLATE_BRICK_SLAB = register("deepslate_brick_slab", new SlabBlock(AbstractBlock.Settings.copy(DEEPSLATE_BRICKS)));
	public static final Block DEEPSLATE_BRICK_WALL = register("deepslate_brick_wall", new WallBlock(AbstractBlock.Settings.copy(DEEPSLATE_BRICKS)));
	public static final Block CHISELED_DEEPSLATE = register(
		"chiseled_deepslate", new Block(AbstractBlock.Settings.copy(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE_BRICKS))
	);
	public static final Block CRACKED_DEEPSLATE_BRICKS = register("cracked_deepslate_bricks", new Block(AbstractBlock.Settings.copy(DEEPSLATE_BRICKS)));
	public static final Block CRACKED_DEEPSLATE_TILES = register("cracked_deepslate_tiles", new Block(AbstractBlock.Settings.copy(DEEPSLATE_TILES)));
	public static final Block INFESTED_DEEPSLATE = register(
		"infested_deepslate",
		new RotatedInfestedBlock(DEEPSLATE, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MapColor.DEEPSLATE_GRAY).sounds(BlockSoundGroup.DEEPSLATE))
	);
	public static final Block SMOOTH_BASALT = register("smooth_basalt", new Block(AbstractBlock.Settings.copy(BASALT)));
	public static final Block RAW_IRON_BLOCK = register(
		"raw_iron_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.RAW_IRON_PINK).requiresTool().strength(5.0F, 6.0F))
	);
	public static final Block RAW_COPPER_BLOCK = register(
		"raw_copper_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.ORANGE).requiresTool().strength(5.0F, 6.0F))
	);
	public static final Block RAW_GOLD_BLOCK = register(
		"raw_gold_block", new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.GOLD).requiresTool().strength(5.0F, 6.0F))
	);
	public static final Block POTTED_AZALEA_BUSH = register(
		"potted_azalea_bush", new FlowerPotBlock(AZALEA, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_FLOWERING_AZALEA_BUSH = register(
		"potted_flowering_azalea_bush", new FlowerPotBlock(FLOWERING_AZALEA, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque())
	);
	public static final Block OCHRE_FROGLIGHT = register(
		"ochre_froglight",
		new PillarBlock(AbstractBlock.Settings.of(Material.FROGLIGHT, MapColor.PALE_YELLOW).strength(0.3F).luminance(state -> 15).sounds(BlockSoundGroup.FROGLIGHT))
	);
	public static final Block VERDANT_FROGLIGHT = register(
		"verdant_froglight",
		new PillarBlock(AbstractBlock.Settings.of(Material.FROGLIGHT, MapColor.LICHEN_GREEN).strength(0.3F).luminance(state -> 15).sounds(BlockSoundGroup.FROGLIGHT))
	);
	public static final Block PEARLESCENT_FROGLIGHT = register(
		"pearlescent_froglight",
		new PillarBlock(AbstractBlock.Settings.of(Material.FROGLIGHT, MapColor.PINK).strength(0.3F).luminance(state -> 15).sounds(BlockSoundGroup.FROGLIGHT))
	);
	public static final Block FROGSPAWN = register(
		"frogspawn", new FrogspawnBlock(AbstractBlock.Settings.of(Material.FROGSPAWN).breakInstantly().nonOpaque().noCollision().sounds(BlockSoundGroup.FROGSPAWN))
	);
	public static final Block REINFORCED_DEEPSLATE = register(
		"reinforced_deepslate",
		new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).sounds(BlockSoundGroup.DEEPSLATE).strength(55.0F, 1200.0F))
	);

	private static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
		return state -> state.get(Properties.LIT) ? litLevel : 0;
	}

	/**
	 * A shortcut to always return {@code false} in a typed context predicate with an
	 * {@link EntityType}, used like {@code settings.allowSpawning(Blocks::never)}.
	 */
	private static Boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
		return false;
	}

	/**
	 * A shortcut to always return {@code true} in a typed context predicate with an
	 * {@link EntityType}, used like {@code settings.allowSpawning(Blocks::always)}.
	 */
	private static Boolean always(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
		return true;
	}

	private static Boolean canSpawnOnLeaves(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
		return type == EntityType.OCELOT || type == EntityType.PARROT;
	}

	private static BedBlock createBedBlock(DyeColor color) {
		return new BedBlock(
			color,
			AbstractBlock.Settings.of(Material.WOOL, state -> state.get(BedBlock.PART) == BedPart.FOOT ? color.getMapColor() : MapColor.WHITE_GRAY)
				.sounds(BlockSoundGroup.WOOD)
				.strength(0.2F)
				.nonOpaque()
		);
	}

	private static PillarBlock createLogBlock(MapColor topMapColor, MapColor sideMapColor) {
		return new PillarBlock(
			AbstractBlock.Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor)
				.strength(2.0F)
				.sounds(BlockSoundGroup.WOOD)
		);
	}

	private static Block createNetherStemBlock(MapColor mapColor) {
		return new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, state -> mapColor).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM));
	}

	/**
	 * A shortcut to always return {@code true} a context predicate, used as
	 * {@code settings.solidBlock(Blocks::always)}.
	 */
	private static boolean always(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	/**
	 * A shortcut to always return {@code false} a context predicate, used as
	 * {@code settings.solidBlock(Blocks::never)}.
	 */
	private static boolean never(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	private static StainedGlassBlock createStainedGlassBlock(DyeColor color) {
		return new StainedGlassBlock(
			color,
			AbstractBlock.Settings.of(Material.GLASS, color)
				.strength(0.3F)
				.sounds(BlockSoundGroup.GLASS)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.solidBlock(Blocks::never)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		);
	}

	private static LeavesBlock createLeavesBlock(BlockSoundGroup soundGroup) {
		return new LeavesBlock(
			AbstractBlock.Settings.of(Material.LEAVES)
				.strength(0.2F)
				.ticksRandomly()
				.sounds(soundGroup)
				.nonOpaque()
				.allowsSpawning(Blocks::canSpawnOnLeaves)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		);
	}

	private static ShulkerBoxBlock createShulkerBoxBlock(DyeColor color, AbstractBlock.Settings settings) {
		AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> !(world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity)
				? true
				: shulkerBoxBlockEntity.suffocates();
		return new ShulkerBoxBlock(color, settings.strength(2.0F).dynamicBounds().nonOpaque().suffocates(contextPredicate).blockVision(contextPredicate));
	}

	private static PistonBlock createPistonBlock(boolean sticky) {
		AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> !(Boolean)state.get(PistonBlock.EXTENDED);
		return new PistonBlock(
			sticky, AbstractBlock.Settings.of(Material.PISTON).strength(1.5F).solidBlock(Blocks::never).suffocates(contextPredicate).blockVision(contextPredicate)
		);
	}

	private static Block register(String id, Block block) {
		return Registry.register(Registry.BLOCK, id, block);
	}

	public static void refreshShapeCache() {
		Block.STATE_IDS.forEach(AbstractBlock.AbstractBlockState::initShapeCache);
	}

	static {
		for (Block block : Registry.BLOCK) {
			for (BlockState blockState : block.getStateManager().getStates()) {
				Block.STATE_IDS.add(blockState);
			}

			block.getLootTableId();
		}
	}
}
