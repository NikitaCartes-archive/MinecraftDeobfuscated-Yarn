package net.minecraft.block;

import java.util.function.ToIntFunction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.sapling.AcaciaSaplingGenerator;
import net.minecraft.block.sapling.BirchSaplingGenerator;
import net.minecraft.block.sapling.DarkOakSaplingGenerator;
import net.minecraft.block.sapling.JungleSaplingGenerator;
import net.minecraft.block.sapling.OakSaplingGenerator;
import net.minecraft.block.sapling.SpruceSaplingGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;

public class Blocks {
	public static final Block AIR = register("air", new AirBlock(AbstractBlock.Settings.of(Material.AIR).noCollision().dropsNothing().air()));
	public static final Block STONE = register(
		"stone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block GRANITE = register(
		"granite", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.DIRT).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_GRANITE = register(
		"polished_granite", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.DIRT).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DIORITE = register(
		"diorite", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.QUARTZ).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_DIORITE = register(
		"polished_diorite", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.QUARTZ).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block ANDESITE = register(
		"andesite", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_ANDESITE = register(
		"polished_andesite", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block GRASS_BLOCK = register(
		"grass_block", new GrassBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block DIRT = register(
		"dirt", new Block(AbstractBlock.Settings.of(Material.SOIL, MaterialColor.DIRT).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block COARSE_DIRT = register(
		"coarse_dirt", new Block(AbstractBlock.Settings.of(Material.SOIL, MaterialColor.DIRT).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block PODZOL = register(
		"podzol", new SnowyBlock(AbstractBlock.Settings.of(Material.SOIL, MaterialColor.SPRUCE).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block COBBLESTONE = register("cobblestone", new Block(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(2.0F, 6.0F)));
	public static final Block OAK_PLANKS = register(
		"oak_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_PLANKS = register(
		"spruce_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_PLANKS = register(
		"birch_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SAND).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_PLANKS = register(
		"jungle_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_PLANKS = register(
		"acacia_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.ORANGE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_PLANKS = register(
		"dark_oak_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
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
	public static final Block BEDROCK = register(
		"bedrock", new Block(AbstractBlock.Settings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing().allowsSpawning(Blocks::never))
	);
	public static final Block WATER = register(
		"water", new FluidBlock(Fluids.WATER, AbstractBlock.Settings.of(Material.WATER).noCollision().strength(100.0F).dropsNothing())
	);
	public static final Block LAVA = register(
		"lava",
		new FluidBlock(Fluids.LAVA, AbstractBlock.Settings.of(Material.LAVA).noCollision().ticksRandomly().strength(100.0F).lightLevel(state -> 15).dropsNothing())
	);
	public static final Block SAND = register(
		"sand", new SandBlock(14406560, AbstractBlock.Settings.of(Material.AGGREGATE, MaterialColor.SAND).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block RED_SAND = register(
		"red_sand", new SandBlock(11098145, AbstractBlock.Settings.of(Material.AGGREGATE, MaterialColor.ORANGE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block GRAVEL = register(
		"gravel", new GravelBlock(AbstractBlock.Settings.of(Material.AGGREGATE, MaterialColor.STONE).strength(0.6F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block GOLD_ORE = register("gold_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F)));
	public static final Block IRON_ORE = register("iron_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F)));
	public static final Block COAL_ORE = register("coal_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F)));
	public static final Block NETHER_GOLD_ORE = register(
		"nether_gold_ore",
		new OreBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.NETHER_GOLD_ORE))
	);
	public static final Block OAK_LOG = register("oak_log", createLogBlock(MaterialColor.WOOD, MaterialColor.SPRUCE));
	public static final Block SPRUCE_LOG = register("spruce_log", createLogBlock(MaterialColor.SPRUCE, MaterialColor.BROWN));
	public static final Block BIRCH_LOG = register("birch_log", createLogBlock(MaterialColor.SAND, MaterialColor.QUARTZ));
	public static final Block JUNGLE_LOG = register("jungle_log", createLogBlock(MaterialColor.DIRT, MaterialColor.SPRUCE));
	public static final Block ACACIA_LOG = register("acacia_log", createLogBlock(MaterialColor.ORANGE, MaterialColor.STONE));
	public static final Block DARK_OAK_LOG = register("dark_oak_log", createLogBlock(MaterialColor.BROWN, MaterialColor.BROWN));
	public static final Block STRIPPED_SPRUCE_LOG = register("stripped_spruce_log", createLogBlock(MaterialColor.SPRUCE, MaterialColor.SPRUCE));
	public static final Block STRIPPED_BIRCH_LOG = register("stripped_birch_log", createLogBlock(MaterialColor.SAND, MaterialColor.SAND));
	public static final Block STRIPPED_JUNGLE_LOG = register("stripped_jungle_log", createLogBlock(MaterialColor.DIRT, MaterialColor.DIRT));
	public static final Block STRIPPED_ACACIA_LOG = register("stripped_acacia_log", createLogBlock(MaterialColor.ORANGE, MaterialColor.ORANGE));
	public static final Block STRIPPED_DARK_OAK_LOG = register("stripped_dark_oak_log", createLogBlock(MaterialColor.BROWN, MaterialColor.BROWN));
	public static final Block STRIPPED_OAK_LOG = register("stripped_oak_log", createLogBlock(MaterialColor.WOOD, MaterialColor.WOOD));
	public static final Block OAK_WOOD = register(
		"oak_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_WOOD = register(
		"spruce_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_WOOD = register(
		"birch_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SAND).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_WOOD = register(
		"jungle_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_WOOD = register(
		"acacia_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.GRAY).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_WOOD = register(
		"dark_oak_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_OAK_WOOD = register(
		"stripped_oak_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_SPRUCE_WOOD = register(
		"stripped_spruce_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_BIRCH_WOOD = register(
		"stripped_birch_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SAND).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_JUNGLE_WOOD = register(
		"stripped_jungle_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_ACACIA_WOOD = register(
		"stripped_acacia_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.ORANGE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STRIPPED_DARK_OAK_WOOD = register(
		"stripped_dark_oak_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block OAK_LEAVES = register("oak_leaves", createLeavesBlock());
	public static final Block SPRUCE_LEAVES = register("spruce_leaves", createLeavesBlock());
	public static final Block BIRCH_LEAVES = register("birch_leaves", createLeavesBlock());
	public static final Block JUNGLE_LEAVES = register("jungle_leaves", createLeavesBlock());
	public static final Block ACACIA_LEAVES = register("acacia_leaves", createLeavesBlock());
	public static final Block DARK_OAK_LEAVES = register("dark_oak_leaves", createLeavesBlock());
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
	public static final Block LAPIS_ORE = register("lapis_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F)));
	public static final Block LAPIS_BLOCK = register(
		"lapis_block", new Block(AbstractBlock.Settings.of(Material.METAL, MaterialColor.LAPIS).requiresTool().strength(3.0F, 3.0F))
	);
	public static final Block DISPENSER = register("dispenser", new DispenserBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F)));
	public static final Block SANDSTONE = register(
		"sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().strength(0.8F))
	);
	public static final Block CHISELED_SANDSTONE = register(
		"chiseled_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().strength(0.8F))
	);
	public static final Block CUT_SANDSTONE = register(
		"cut_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().strength(0.8F))
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
		"powered_rail", new PoweredRailBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block DETECTOR_RAIL = register(
		"detector_rail", new DetectorRailBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block STICKY_PISTON = register("sticky_piston", createPistonBlock(true));
	public static final Block COBWEB = register("cobweb", new CobwebBlock(AbstractBlock.Settings.of(Material.COBWEB).noCollision().requiresTool().strength(4.0F)));
	public static final Block GRASS = register(
		"grass", new FernBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block FERN = register(
		"fern", new FernBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block DEAD_BUSH = register(
		"dead_bush",
		new DeadBushBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT, MaterialColor.WOOD).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block SEAGRASS = register(
		"seagrass",
		new SeagrassBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_UNDERWATER_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block TALL_SEAGRASS = register(
		"tall_seagrass",
		new TallSeagrassBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_UNDERWATER_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block PISTON = register("piston", createPistonBlock(false));
	public static final Block PISTON_HEAD = register("piston_head", new PistonHeadBlock(AbstractBlock.Settings.of(Material.PISTON).strength(1.5F).dropsNothing()));
	public static final Block WHITE_WOOL = register(
		"white_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.WHITE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block ORANGE_WOOL = register(
		"orange_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.ORANGE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block MAGENTA_WOOL = register(
		"magenta_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.MAGENTA).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIGHT_BLUE_WOOL = register(
		"light_blue_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.LIGHT_BLUE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block YELLOW_WOOL = register(
		"yellow_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.YELLOW).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIME_WOOL = register(
		"lime_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.LIME).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block PINK_WOOL = register(
		"pink_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.PINK).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block GRAY_WOOL = register(
		"gray_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.GRAY).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIGHT_GRAY_WOOL = register(
		"light_gray_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.LIGHT_GRAY).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block CYAN_WOOL = register(
		"cyan_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.CYAN).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block PURPLE_WOOL = register(
		"purple_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.PURPLE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BLUE_WOOL = register(
		"blue_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.BLUE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BROWN_WOOL = register(
		"brown_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.BROWN).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block GREEN_WOOL = register(
		"green_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.GREEN).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block RED_WOOL = register(
		"red_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.RED).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BLACK_WOOL = register(
		"black_wool", new Block(AbstractBlock.Settings.of(Material.WOOL, MaterialColor.BLACK).strength(0.8F).sounds(BlockSoundGroup.WOOL))
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
		new FlowerBlock(StatusEffects.SATURATION, 7, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block POPPY = register(
		"poppy",
		new FlowerBlock(StatusEffects.NIGHT_VISION, 5, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block BLUE_ORCHID = register(
		"blue_orchid",
		new FlowerBlock(StatusEffects.SATURATION, 7, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block ALLIUM = register(
		"allium",
		new FlowerBlock(StatusEffects.FIRE_RESISTANCE, 4, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block AZURE_BLUET = register(
		"azure_bluet",
		new FlowerBlock(StatusEffects.BLINDNESS, 8, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block RED_TULIP = register(
		"red_tulip",
		new FlowerBlock(StatusEffects.WEAKNESS, 9, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block ORANGE_TULIP = register(
		"orange_tulip",
		new FlowerBlock(StatusEffects.WEAKNESS, 9, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block WHITE_TULIP = register(
		"white_tulip",
		new FlowerBlock(StatusEffects.WEAKNESS, 9, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block PINK_TULIP = register(
		"pink_tulip",
		new FlowerBlock(StatusEffects.WEAKNESS, 9, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block OXEYE_DAISY = register(
		"oxeye_daisy",
		new FlowerBlock(StatusEffects.REGENERATION, 8, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block CORNFLOWER = register(
		"cornflower",
		new FlowerBlock(StatusEffects.JUMP_BOOST, 6, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block WITHER_ROSE = register(
		"wither_rose",
		new WitherRoseBlock(StatusEffects.WITHER, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block LILY_OF_THE_VALLEY = register(
		"lily_of_the_valley",
		new FlowerBlock(StatusEffects.POISON, 12, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block BROWN_MUSHROOM = register(
		"brown_mushroom",
		new MushroomPlantBlock(
			AbstractBlock.Settings.of(Material.PLANT, MaterialColor.BROWN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.lightLevel(state -> 1)
				.postProcess(Blocks::always)
		)
	);
	public static final Block RED_MUSHROOM = register(
		"red_mushroom",
		new MushroomPlantBlock(
			AbstractBlock.Settings.of(Material.PLANT, MaterialColor.RED)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.postProcess(Blocks::always)
		)
	);
	public static final Block GOLD_BLOCK = register(
		"gold_block", new Block(AbstractBlock.Settings.of(Material.METAL, MaterialColor.GOLD).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block IRON_BLOCK = register(
		"iron_block", new Block(AbstractBlock.Settings.of(Material.METAL, MaterialColor.IRON).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block BRICKS = register(
		"bricks", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.RED).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block TNT = register("tnt", new TntBlock(AbstractBlock.Settings.of(Material.TNT).breakInstantly().sounds(BlockSoundGroup.GRASS)));
	public static final Block BOOKSHELF = register("bookshelf", new Block(AbstractBlock.Settings.of(Material.WOOD).strength(1.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block MOSSY_COBBLESTONE = register(
		"mossy_cobblestone", new Block(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block OBSIDIAN = register(
		"obsidian", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK).requiresTool().strength(50.0F, 1200.0F))
	);
	public static final Block TORCH = register(
		"torch",
		new TorchBlock(
			AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().breakInstantly().lightLevel(state -> 14).sounds(BlockSoundGroup.WOOD), ParticleTypes.FLAME
		)
	);
	public static final Block WALL_TORCH = register(
		"wall_torch",
		new WallTorchBlock(
			AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().breakInstantly().lightLevel(state -> 14).sounds(BlockSoundGroup.WOOD).dropsLike(TORCH),
			ParticleTypes.FLAME
		)
	);
	public static final Block FIRE = register(
		"fire",
		new FireBlock(
			AbstractBlock.Settings.of(Material.FIRE, MaterialColor.LAVA).noCollision().breakInstantly().lightLevel(state -> 15).sounds(BlockSoundGroup.WOOL)
		)
	);
	public static final Block SOUL_FIRE = register(
		"soul_fire",
		new SoulFireBlock(
			AbstractBlock.Settings.of(Material.FIRE, MaterialColor.LIGHT_BLUE).noCollision().breakInstantly().lightLevel(state -> 10).sounds(BlockSoundGroup.WOOL)
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
		"redstone_wire", new RedstoneWireBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().breakInstantly())
	);
	public static final Block DIAMOND_ORE = register("diamond_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F)));
	public static final Block DIAMOND_BLOCK = register(
		"diamond_block",
		new Block(AbstractBlock.Settings.of(Material.METAL, MaterialColor.DIAMOND).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block CRAFTING_TABLE = register(
		"crafting_table", new CraftingTableBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WHEAT = register(
		"wheat", new CropBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block FARMLAND = register(
		"farmland",
		new FarmlandBlock(AbstractBlock.Settings.of(Material.SOIL).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRAVEL).blockVision(Blocks::always))
	);
	public static final Block FURNACE = register(
		"furnace", new FurnaceBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).lightLevel(createLightLevelFromBlockState(13)))
	);
	public static final Block OAK_SIGN = register(
		"oak_sign", new SignBlock(AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.OAK)
	);
	public static final Block SPRUCE_SIGN = register(
		"spruce_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.WOOD, SPRUCE_LOG.getDefaultMaterialColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.SPRUCE
		)
	);
	public static final Block BIRCH_SIGN = register(
		"birch_sign",
		new SignBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SAND).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.BIRCH)
	);
	public static final Block ACACIA_SIGN = register(
		"acacia_sign",
		new SignBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.ORANGE).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.ACACIA)
	);
	public static final Block JUNGLE_SIGN = register(
		"jungle_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.WOOD, JUNGLE_LOG.getDefaultMaterialColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.JUNGLE
		)
	);
	public static final Block DARK_OAK_SIGN = register(
		"dark_oak_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_LOG.getDefaultMaterialColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD),
			SignType.DARK_OAK
		)
	);
	public static final Block OAK_DOOR = register(
		"oak_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, OAK_PLANKS.getDefaultMaterialColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block LADDER = register(
		"ladder", new LadderBlock(AbstractBlock.Settings.of(Material.SUPPORTED).strength(0.4F).sounds(BlockSoundGroup.LADDER).nonOpaque())
	);
	public static final Block RAIL = register(
		"rail", new RailBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
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
			AbstractBlock.Settings.of(Material.WOOD, SPRUCE_LOG.getDefaultMaterialColor())
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(SPRUCE_SIGN),
			SignType.SPRUCE
		)
	);
	public static final Block BIRCH_WALL_SIGN = register(
		"birch_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SAND).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(BIRCH_SIGN), SignType.BIRCH
		)
	);
	public static final Block ACACIA_WALL_SIGN = register(
		"acacia_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.ORANGE).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(ACACIA_SIGN),
			SignType.ACACIA
		)
	);
	public static final Block JUNGLE_WALL_SIGN = register(
		"jungle_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, JUNGLE_LOG.getDefaultMaterialColor())
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(JUNGLE_SIGN),
			SignType.JUNGLE
		)
	);
	public static final Block DARK_OAK_WALL_SIGN = register(
		"dark_oak_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_LOG.getDefaultMaterialColor())
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(DARK_OAK_SIGN),
			SignType.DARK_OAK
		)
	);
	public static final Block LEVER = register(
		"lever", new LeverBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STONE_PRESSURE_PLATE = register(
		"stone_pressure_plate",
		new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.STONE).requiresTool().noCollision().strength(0.5F))
	);
	public static final Block IRON_DOOR = register(
		"iron_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.IRON).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block OAK_PRESSURE_PLATE = register(
		"oak_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, OAK_PLANKS.getDefaultMaterialColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block SPRUCE_PRESSURE_PLATE = register(
		"spruce_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, SPRUCE_PLANKS.getDefaultMaterialColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block BIRCH_PRESSURE_PLATE = register(
		"birch_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, BIRCH_PLANKS.getDefaultMaterialColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block JUNGLE_PRESSURE_PLATE = register(
		"jungle_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, JUNGLE_PLANKS.getDefaultMaterialColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block ACACIA_PRESSURE_PLATE = register(
		"acacia_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, ACACIA_PLANKS.getDefaultMaterialColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block DARK_OAK_PRESSURE_PLATE = register(
		"dark_oak_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_PLANKS.getDefaultMaterialColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block REDSTONE_ORE = register(
		"redstone_ore",
		new RedstoneOreBlock(
			AbstractBlock.Settings.of(Material.STONE).requiresTool().ticksRandomly().lightLevel(createLightLevelFromBlockState(9)).strength(3.0F, 3.0F)
		)
	);
	public static final Block REDSTONE_TORCH = register(
		"redstone_torch",
		new RedstoneTorchBlock(
			AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().breakInstantly().lightLevel(createLightLevelFromBlockState(7)).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block REDSTONE_WALL_TORCH = register(
		"redstone_wall_torch",
		new WallRedstoneTorchBlock(
			AbstractBlock.Settings.of(Material.SUPPORTED)
				.noCollision()
				.breakInstantly()
				.lightLevel(createLightLevelFromBlockState(7))
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(REDSTONE_TORCH)
		)
	);
	public static final Block STONE_BUTTON = register(
		"stone_button", new StoneButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F))
	);
	public static final Block SNOW = register(
		"snow", new SnowBlock(AbstractBlock.Settings.of(Material.SNOW_LAYER).ticksRandomly().strength(0.1F).requiresTool().sounds(BlockSoundGroup.SNOW))
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
	public static final Block JUKEBOX = register("jukebox", new JukeboxBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F, 6.0F)));
	public static final Block OAK_FENCE = register(
		"oak_fence", new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, OAK_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block PUMPKIN = register(
		"pumpkin", new PumpkinBlock(AbstractBlock.Settings.of(Material.GOURD, MaterialColor.ORANGE).strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block NETHERRACK = register(
		"netherrack",
		new NetherrackBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER).requiresTool().strength(0.4F).sounds(BlockSoundGroup.NETHERRACK))
	);
	public static final Block SOUL_SAND = register(
		"soul_sand",
		new SoulSandBlock(
			AbstractBlock.Settings.of(Material.AGGREGATE, MaterialColor.BROWN)
				.strength(0.5F)
				.velocityMultiplier(0.4F)
				.sounds(BlockSoundGroup.SOUL_SAND)
				.allowsSpawning(Blocks::always)
				.solidBlock(Blocks::always)
				.blockVision(Blocks::always)
		)
	);
	public static final Block SOUL_SOIL = register(
		"soul_soil", new Block(AbstractBlock.Settings.of(Material.SOIL, MaterialColor.BROWN).strength(0.5F).sounds(BlockSoundGroup.SOUL_SOIL))
	);
	public static final Block BASALT = register(
		"basalt", new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK).requiresTool().strength(1.25F, 4.2F).sounds(BlockSoundGroup.BASALT))
	);
	public static final Block POLISHED_BASALT = register(
		"polished_basalt",
		new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK).requiresTool().strength(1.25F, 4.2F).sounds(BlockSoundGroup.BASALT))
	);
	public static final Block SOUL_TORCH = register(
		"soul_torch",
		new TorchBlock(
			AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().breakInstantly().lightLevel(state -> 10).sounds(BlockSoundGroup.WOOD),
			ParticleTypes.SOUL_FIRE_FLAME
		)
	);
	public static final Block SOUL_WALL_TORCH = register(
		"soul_wall_torch",
		new WallTorchBlock(
			AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().breakInstantly().lightLevel(state -> 10).sounds(BlockSoundGroup.WOOD).dropsLike(SOUL_TORCH),
			ParticleTypes.SOUL_FIRE_FLAME
		)
	);
	public static final Block GLOWSTONE = register(
		"glowstone", new Block(AbstractBlock.Settings.of(Material.GLASS, MaterialColor.SAND).strength(0.3F).sounds(BlockSoundGroup.GLASS).lightLevel(state -> 15))
	);
	public static final Block NETHER_PORTAL = register(
		"nether_portal",
		new NetherPortalBlock(
			AbstractBlock.Settings.of(Material.PORTAL).noCollision().ticksRandomly().strength(-1.0F).sounds(BlockSoundGroup.GLASS).lightLevel(state -> 11)
		)
	);
	public static final Block CARVED_PUMPKIN = register(
		"carved_pumpkin",
		new CarvedPumpkinBlock(
			AbstractBlock.Settings.of(Material.GOURD, MaterialColor.ORANGE).strength(1.0F).sounds(BlockSoundGroup.WOOD).allowsSpawning(Blocks::always)
		)
	);
	public static final Block JACK_O_LANTERN = register(
		"jack_o_lantern",
		new CarvedPumpkinBlock(
			AbstractBlock.Settings.of(Material.GOURD, MaterialColor.ORANGE)
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.lightLevel(state -> 15)
				.allowsSpawning(Blocks::always)
		)
	);
	public static final Block CAKE = register("cake", new CakeBlock(AbstractBlock.Settings.of(Material.CAKE).strength(0.5F).sounds(BlockSoundGroup.WOOL)));
	public static final Block REPEATER = register(
		"repeater", new RepeaterBlock(AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().sounds(BlockSoundGroup.WOOD))
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
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block SPRUCE_TRAPDOOR = register(
		"spruce_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block BIRCH_TRAPDOOR = register(
		"birch_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SAND).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block JUNGLE_TRAPDOOR = register(
		"jungle_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block ACACIA_TRAPDOOR = register(
		"acacia_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.ORANGE).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block DARK_OAK_TRAPDOOR = register(
		"dark_oak_trapdoor",
		new TrapdoorBlock(
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(Blocks::never)
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
	public static final Block INFESTED_STONE = register(
		"infested_stone", new InfestedBlock(STONE, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0.0F, 0.75F))
	);
	public static final Block INFESTED_COBBLESTONE = register(
		"infested_cobblestone", new InfestedBlock(COBBLESTONE, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0.0F, 0.75F))
	);
	public static final Block INFESTED_STONE_BRICKS = register(
		"infested_stone_bricks", new InfestedBlock(STONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0.0F, 0.75F))
	);
	public static final Block INFESTED_MOSSY_STONE_BRICKS = register(
		"infested_mossy_stone_bricks", new InfestedBlock(MOSSY_STONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0.0F, 0.75F))
	);
	public static final Block INFESTED_CRACKED_STONE_BRICKS = register(
		"infested_cracked_stone_bricks", new InfestedBlock(CRACKED_STONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0.0F, 0.75F))
	);
	public static final Block INFESTED_CHISELED_STONE_BRICKS = register(
		"infested_chiseled_stone_bricks", new InfestedBlock(CHISELED_STONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0.0F, 0.75F))
	);
	public static final Block BROWN_MUSHROOM_BLOCK = register(
		"brown_mushroom_block", new MushroomBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block RED_MUSHROOM_BLOCK = register(
		"red_mushroom_block", new MushroomBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.RED).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block MUSHROOM_STEM = register(
		"mushroom_stem", new MushroomBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.WEB).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block IRON_BARS = register(
		"iron_bars",
		new PaneBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.CLEAR).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block CHAIN = register(
		"chain",
		new ChainBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.CLEAR).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.CHAIN).nonOpaque())
	);
	public static final Block GLASS_PANE = register(
		"glass_pane", new PaneBlock(AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block MELON = register(
		"melon", new MelonBlock(AbstractBlock.Settings.of(Material.GOURD, MaterialColor.LIME).strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ATTACHED_PUMPKIN_STEM = register(
		"attached_pumpkin_stem",
		new AttachedStemBlock((GourdBlock)PUMPKIN, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ATTACHED_MELON_STEM = register(
		"attached_melon_stem",
		new AttachedStemBlock((GourdBlock)MELON, AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block PUMPKIN_STEM = register(
		"pumpkin_stem",
		new StemBlock((GourdBlock)PUMPKIN, AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.STEM))
	);
	public static final Block MELON_STEM = register(
		"melon_stem",
		new StemBlock((GourdBlock)MELON, AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.STEM))
	);
	public static final Block VINE = register(
		"vine", new VineBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().ticksRandomly().strength(0.2F).sounds(BlockSoundGroup.VINE))
	);
	public static final Block OAK_FENCE_GATE = register(
		"oak_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, OAK_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BRICK_STAIRS = register("brick_stairs", new StairsBlock(BRICKS.getDefaultState(), AbstractBlock.Settings.copy(BRICKS)));
	public static final Block STONE_BRICK_STAIRS = register(
		"stone_brick_stairs", new StairsBlock(STONE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(STONE_BRICKS))
	);
	public static final Block MYCELIUM = register(
		"mycelium",
		new MyceliumBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MaterialColor.PURPLE).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block LILY_PAD = register(
		"lily_pad", new LilyPadBlock(AbstractBlock.Settings.of(Material.PLANT).breakInstantly().sounds(BlockSoundGroup.LILY_PAD).nonOpaque())
	);
	public static final Block NETHER_BRICKS = register(
		"nether_bricks",
		new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block NETHER_BRICK_FENCE = register(
		"nether_brick_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block NETHER_BRICK_STAIRS = register(
		"nether_brick_stairs", new StairsBlock(NETHER_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(NETHER_BRICKS))
	);
	public static final Block NETHER_WART = register(
		"nether_wart",
		new NetherWartBlock(AbstractBlock.Settings.of(Material.PLANT, MaterialColor.RED).noCollision().ticksRandomly().sounds(BlockSoundGroup.NETHER_WART))
	);
	public static final Block ENCHANTING_TABLE = register(
		"enchanting_table", new EnchantingTableBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.RED).requiresTool().strength(5.0F, 1200.0F))
	);
	public static final Block BREWING_STAND = register(
		"brewing_stand", new BrewingStandBlock(AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(0.5F).lightLevel(state -> 1).nonOpaque())
	);
	public static final Block CAULDRON = register(
		"cauldron", new CauldronBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.STONE).requiresTool().strength(2.0F).nonOpaque())
	);
	public static final Block END_PORTAL = register(
		"end_portal",
		new EndPortalBlock(
			AbstractBlock.Settings.of(Material.PORTAL, MaterialColor.BLACK).noCollision().lightLevel(state -> 15).strength(-1.0F, 3600000.0F).dropsNothing()
		)
	);
	public static final Block END_PORTAL_FRAME = register(
		"end_portal_frame",
		new EndPortalFrameBlock(
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.GREEN)
				.sounds(BlockSoundGroup.GLASS)
				.lightLevel(state -> 1)
				.strength(-1.0F, 3600000.0F)
				.dropsNothing()
		)
	);
	public static final Block END_STONE = register(
		"end_stone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().strength(3.0F, 9.0F))
	);
	public static final Block DRAGON_EGG = register(
		"dragon_egg", new DragonEggBlock(AbstractBlock.Settings.of(Material.EGG, MaterialColor.BLACK).strength(3.0F, 9.0F).lightLevel(state -> 1).nonOpaque())
	);
	public static final Block REDSTONE_LAMP = register(
		"redstone_lamp",
		new RedstoneLampBlock(
			AbstractBlock.Settings.of(Material.REDSTONE_LAMP)
				.lightLevel(createLightLevelFromBlockState(15))
				.strength(0.3F)
				.sounds(BlockSoundGroup.GLASS)
				.allowsSpawning(Blocks::always)
		)
	);
	public static final Block COCOA = register(
		"cocoa", new CocoaBlock(AbstractBlock.Settings.of(Material.PLANT).ticksRandomly().strength(0.2F, 3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block SANDSTONE_STAIRS = register("sandstone_stairs", new StairsBlock(SANDSTONE.getDefaultState(), AbstractBlock.Settings.copy(SANDSTONE)));
	public static final Block EMERALD_ORE = register("emerald_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F)));
	public static final Block ENDER_CHEST = register(
		"ender_chest", new EnderChestBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(22.5F, 600.0F).lightLevel(state -> 7))
	);
	public static final Block TRIPWIRE_HOOK = register("tripwire_hook", new TripwireHookBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision()));
	public static final Block TRIPWIRE = register(
		"tripwire", new TripwireBlock((TripwireHookBlock)TRIPWIRE_HOOK, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision())
	);
	public static final Block EMERALD_BLOCK = register(
		"emerald_block",
		new Block(AbstractBlock.Settings.of(Material.METAL, MaterialColor.EMERALD).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block SPRUCE_STAIRS = register(
		"spruce_stairs", new StairsBlock(SPRUCE_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(SPRUCE_PLANKS))
	);
	public static final Block BIRCH_STAIRS = register("birch_stairs", new StairsBlock(BIRCH_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(BIRCH_PLANKS)));
	public static final Block JUNGLE_STAIRS = register(
		"jungle_stairs", new StairsBlock(JUNGLE_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(JUNGLE_PLANKS))
	);
	public static final Block COMMAND_BLOCK = register(
		"command_block", new CommandBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.BROWN).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block BEACON = register(
		"beacon",
		new BeaconBlock(AbstractBlock.Settings.of(Material.GLASS, MaterialColor.DIAMOND).strength(3.0F).lightLevel(state -> 15).nonOpaque().solidBlock(Blocks::never))
	);
	public static final Block COBBLESTONE_WALL = register("cobblestone_wall", new WallBlock(AbstractBlock.Settings.copy(COBBLESTONE)));
	public static final Block MOSSY_COBBLESTONE_WALL = register("mossy_cobblestone_wall", new WallBlock(AbstractBlock.Settings.copy(COBBLESTONE)));
	public static final Block FLOWER_POT = register(
		"flower_pot", new FlowerPotBlock(AIR, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_OAK_SAPLING = register(
		"potted_oak_sapling", new FlowerPotBlock(OAK_SAPLING, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_SPRUCE_SAPLING = register(
		"potted_spruce_sapling", new FlowerPotBlock(SPRUCE_SAPLING, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_BIRCH_SAPLING = register(
		"potted_birch_sapling", new FlowerPotBlock(BIRCH_SAPLING, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_JUNGLE_SAPLING = register(
		"potted_jungle_sapling", new FlowerPotBlock(JUNGLE_SAPLING, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_ACACIA_SAPLING = register(
		"potted_acacia_sapling", new FlowerPotBlock(ACACIA_SAPLING, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_DARK_OAK_SAPLING = register(
		"potted_dark_oak_sapling", new FlowerPotBlock(DARK_OAK_SAPLING, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_FERN = register(
		"potted_fern", new FlowerPotBlock(FERN, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_DANDELION = register(
		"potted_dandelion", new FlowerPotBlock(DANDELION, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_POPPY = register(
		"potted_poppy", new FlowerPotBlock(POPPY, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_BLUE_ORCHID = register(
		"potted_blue_orchid", new FlowerPotBlock(BLUE_ORCHID, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_ALLIUM = register(
		"potted_allium", new FlowerPotBlock(ALLIUM, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_AZURE_BLUET = register(
		"potted_azure_bluet", new FlowerPotBlock(AZURE_BLUET, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_RED_TULIP = register(
		"potted_red_tulip", new FlowerPotBlock(RED_TULIP, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_ORANGE_TULIP = register(
		"potted_orange_tulip", new FlowerPotBlock(ORANGE_TULIP, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_WHITE_TULIP = register(
		"potted_white_tulip", new FlowerPotBlock(WHITE_TULIP, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_PINK_TULIP = register(
		"potted_pink_tulip", new FlowerPotBlock(PINK_TULIP, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_OXEYE_DAISY = register(
		"potted_oxeye_daisy", new FlowerPotBlock(OXEYE_DAISY, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_CORNFLOWER = register(
		"potted_cornflower", new FlowerPotBlock(CORNFLOWER, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_LILY_OF_THE_VALLEY = register(
		"potted_lily_of_the_valley", new FlowerPotBlock(LILY_OF_THE_VALLEY, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_WITHER_ROSE = register(
		"potted_wither_rose", new FlowerPotBlock(WITHER_ROSE, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_RED_MUSHROOM = register(
		"potted_red_mushroom", new FlowerPotBlock(RED_MUSHROOM, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_BROWN_MUSHROOM = register(
		"potted_brown_mushroom", new FlowerPotBlock(BROWN_MUSHROOM, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_DEAD_BUSH = register(
		"potted_dead_bush", new FlowerPotBlock(DEAD_BUSH, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_CACTUS = register(
		"potted_cactus", new FlowerPotBlock(CACTUS, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block CARROTS = register(
		"carrots", new CarrotsBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block POTATOES = register(
		"potatoes", new PotatoesBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block OAK_BUTTON = register(
		"oak_button", new WoodButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_BUTTON = register(
		"spruce_button", new WoodButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_BUTTON = register(
		"birch_button", new WoodButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_BUTTON = register(
		"jungle_button", new WoodButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_BUTTON = register(
		"acacia_button", new WoodButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_BUTTON = register(
		"dark_oak_button", new WoodButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SKELETON_SKULL = register(
		"skeleton_skull", new SkullBlock(SkullBlock.Type.SKELETON, AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F))
	);
	public static final Block SKELETON_WALL_SKULL = register(
		"skeleton_wall_skull", new WallSkullBlock(SkullBlock.Type.SKELETON, AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F).dropsLike(SKELETON_SKULL))
	);
	public static final Block WITHER_SKELETON_SKULL = register(
		"wither_skeleton_skull", new WitherSkullBlock(AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F))
	);
	public static final Block WITHER_SKELETON_WALL_SKULL = register(
		"wither_skeleton_wall_skull", new WallWitherSkullBlock(AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F).dropsLike(WITHER_SKELETON_SKULL))
	);
	public static final Block ZOMBIE_HEAD = register(
		"zombie_head", new SkullBlock(SkullBlock.Type.ZOMBIE, AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F))
	);
	public static final Block ZOMBIE_WALL_HEAD = register(
		"zombie_wall_head", new WallSkullBlock(SkullBlock.Type.ZOMBIE, AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F).dropsLike(ZOMBIE_HEAD))
	);
	public static final Block PLAYER_HEAD = register("player_head", new PlayerSkullBlock(AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F)));
	public static final Block PLAYER_WALL_HEAD = register(
		"player_wall_head", new WallPlayerSkullBlock(AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F).dropsLike(PLAYER_HEAD))
	);
	public static final Block CREEPER_HEAD = register(
		"creeper_head", new SkullBlock(SkullBlock.Type.CREEPER, AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F))
	);
	public static final Block CREEPER_WALL_HEAD = register(
		"creeper_wall_head", new WallSkullBlock(SkullBlock.Type.CREEPER, AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F).dropsLike(CREEPER_HEAD))
	);
	public static final Block DRAGON_HEAD = register(
		"dragon_head", new SkullBlock(SkullBlock.Type.DRAGON, AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F))
	);
	public static final Block DRAGON_WALL_HEAD = register(
		"dragon_wall_head", new WallSkullBlock(SkullBlock.Type.DRAGON, AbstractBlock.Settings.of(Material.SUPPORTED).strength(1.0F).dropsLike(DRAGON_HEAD))
	);
	public static final Block ANVIL = register(
		"anvil",
		new AnvilBlock(AbstractBlock.Settings.of(Material.REPAIR_STATION, MaterialColor.IRON).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block CHIPPED_ANVIL = register(
		"chipped_anvil",
		new AnvilBlock(AbstractBlock.Settings.of(Material.REPAIR_STATION, MaterialColor.IRON).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block DAMAGED_ANVIL = register(
		"damaged_anvil",
		new AnvilBlock(AbstractBlock.Settings.of(Material.REPAIR_STATION, MaterialColor.IRON).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block TRAPPED_CHEST = register(
		"trapped_chest", new TrappedChestBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block LIGHT_WEIGHTED_PRESSURE_PLATE = register(
		"light_weighted_pressure_plate",
		new WeightedPressurePlateBlock(
			15, AbstractBlock.Settings.of(Material.METAL, MaterialColor.GOLD).requiresTool().noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block HEAVY_WEIGHTED_PRESSURE_PLATE = register(
		"heavy_weighted_pressure_plate",
		new WeightedPressurePlateBlock(150, AbstractBlock.Settings.of(Material.METAL).requiresTool().noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block COMPARATOR = register(
		"comparator", new ComparatorBlock(AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DAYLIGHT_DETECTOR = register(
		"daylight_detector", new DaylightDetectorBlock(AbstractBlock.Settings.of(Material.WOOD).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block REDSTONE_BLOCK = register(
		"redstone_block",
		new RedstoneBlock(
			AbstractBlock.Settings.of(Material.METAL, MaterialColor.LAVA).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).solidBlock(Blocks::never)
		)
	);
	public static final Block NETHER_QUARTZ_ORE = register(
		"nether_quartz_ore",
		new OreBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.NETHER_ORE))
	);
	public static final Block HOPPER = register(
		"hopper",
		new HopperBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.STONE).requiresTool().strength(3.0F, 4.8F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block QUARTZ_BLOCK = register(
		"quartz_block", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.QUARTZ).requiresTool().strength(0.8F))
	);
	public static final Block CHISELED_QUARTZ_BLOCK = register(
		"chiseled_quartz_block", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.QUARTZ).requiresTool().strength(0.8F))
	);
	public static final Block QUARTZ_PILLAR = register(
		"quartz_pillar", new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.QUARTZ).requiresTool().strength(0.8F))
	);
	public static final Block QUARTZ_STAIRS = register("quartz_stairs", new StairsBlock(QUARTZ_BLOCK.getDefaultState(), AbstractBlock.Settings.copy(QUARTZ_BLOCK)));
	public static final Block ACTIVATOR_RAIL = register(
		"activator_rail", new PoweredRailBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block DROPPER = register("dropper", new DropperBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F)));
	public static final Block WHITE_TERRACOTTA = register(
		"white_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.WHITE_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block ORANGE_TERRACOTTA = register(
		"orange_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.ORANGE_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block MAGENTA_TERRACOTTA = register(
		"magenta_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.MAGENTA_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block LIGHT_BLUE_TERRACOTTA = register(
		"light_blue_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.LIGHT_BLUE_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block YELLOW_TERRACOTTA = register(
		"yellow_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.YELLOW_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block LIME_TERRACOTTA = register(
		"lime_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.LIME_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block PINK_TERRACOTTA = register(
		"pink_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.PINK_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block GRAY_TERRACOTTA = register(
		"gray_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block LIGHT_GRAY_TERRACOTTA = register(
		"light_gray_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.LIGHT_GRAY_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block CYAN_TERRACOTTA = register(
		"cyan_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.CYAN_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block PURPLE_TERRACOTTA = register(
		"purple_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.PURPLE_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block BLUE_TERRACOTTA = register(
		"blue_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLUE_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block BROWN_TERRACOTTA = register(
		"brown_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.BROWN_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block GREEN_TERRACOTTA = register(
		"green_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GREEN_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block RED_TERRACOTTA = register(
		"red_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.RED_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block BLACK_TERRACOTTA = register(
		"black_terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK_TERRACOTTA).requiresTool().strength(1.25F, 4.2F))
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
	public static final Block SLIME_BLOCK = register(
		"slime_block",
		new SlimeBlock(AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MaterialColor.GRASS).slipperiness(0.8F).sounds(BlockSoundGroup.SLIME).nonOpaque())
	);
	public static final Block BARRIER = register(
		"barrier", new BarrierBlock(AbstractBlock.Settings.of(Material.BARRIER).strength(-1.0F, 3600000.8F).dropsNothing().nonOpaque().allowsSpawning(Blocks::never))
	);
	public static final Block IRON_TRAPDOOR = register(
		"iron_trapdoor", new TrapdoorBlock(AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block PRISMARINE = register(
		"prismarine", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.CYAN).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PRISMARINE_BRICKS = register(
		"prismarine_bricks", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.DIAMOND).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DARK_PRISMARINE = register(
		"dark_prismarine", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.DIAMOND).requiresTool().strength(1.5F, 6.0F))
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
		"prismarine_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.CYAN).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PRISMARINE_BRICK_SLAB = register(
		"prismarine_brick_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.DIAMOND).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DARK_PRISMARINE_SLAB = register(
		"dark_prismarine_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.DIAMOND).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block SEA_LANTERN = register(
		"sea_lantern",
		new Block(AbstractBlock.Settings.of(Material.GLASS, MaterialColor.QUARTZ).strength(0.3F).sounds(BlockSoundGroup.GLASS).lightLevel(state -> 15))
	);
	public static final Block HAY_BLOCK = register(
		"hay_block", new HayBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MaterialColor.YELLOW).strength(0.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block WHITE_CARPET = register(
		"white_carpet", new CarpetBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.WHITE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block ORANGE_CARPET = register(
		"orange_carpet",
		new CarpetBlock(DyeColor.ORANGE, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.ORANGE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block MAGENTA_CARPET = register(
		"magenta_carpet",
		new CarpetBlock(DyeColor.MAGENTA, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.MAGENTA).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIGHT_BLUE_CARPET = register(
		"light_blue_carpet",
		new CarpetBlock(DyeColor.LIGHT_BLUE, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.LIGHT_BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block YELLOW_CARPET = register(
		"yellow_carpet",
		new CarpetBlock(DyeColor.YELLOW, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.YELLOW).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIME_CARPET = register(
		"lime_carpet", new CarpetBlock(DyeColor.LIME, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.LIME).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block PINK_CARPET = register(
		"pink_carpet", new CarpetBlock(DyeColor.PINK, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.PINK).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block GRAY_CARPET = register(
		"gray_carpet", new CarpetBlock(DyeColor.GRAY, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block LIGHT_GRAY_CARPET = register(
		"light_gray_carpet",
		new CarpetBlock(DyeColor.LIGHT_GRAY, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.LIGHT_GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block CYAN_CARPET = register(
		"cyan_carpet", new CarpetBlock(DyeColor.CYAN, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.CYAN).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block PURPLE_CARPET = register(
		"purple_carpet",
		new CarpetBlock(DyeColor.PURPLE, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.PURPLE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BLUE_CARPET = register(
		"blue_carpet", new CarpetBlock(DyeColor.BLUE, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BROWN_CARPET = register(
		"brown_carpet", new CarpetBlock(DyeColor.BROWN, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.BROWN).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block GREEN_CARPET = register(
		"green_carpet", new CarpetBlock(DyeColor.GREEN, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block RED_CARPET = register(
		"red_carpet", new CarpetBlock(DyeColor.RED, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.RED).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block BLACK_CARPET = register(
		"black_carpet", new CarpetBlock(DyeColor.BLACK, AbstractBlock.Settings.of(Material.CARPET, MaterialColor.BLACK).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block TERRACOTTA = register(
		"terracotta", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.ORANGE).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block COAL_BLOCK = register(
		"coal_block", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK).requiresTool().strength(5.0F, 6.0F))
	);
	public static final Block PACKED_ICE = register(
		"packed_ice", new Block(AbstractBlock.Settings.of(Material.DENSE_ICE).slipperiness(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block SUNFLOWER = register(
		"sunflower", new TallFlowerBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block LILAC = register(
		"lilac", new TallFlowerBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block ROSE_BUSH = register(
		"rose_bush", new TallFlowerBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block PEONY = register(
		"peony", new TallFlowerBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block TALL_GRASS = register(
		"tall_grass", new TallPlantBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block LARGE_FERN = register(
		"large_fern", new TallPlantBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
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
		"red_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.ORANGE).requiresTool().strength(0.8F))
	);
	public static final Block CHISELED_RED_SANDSTONE = register(
		"chiseled_red_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.ORANGE).requiresTool().strength(0.8F))
	);
	public static final Block CUT_RED_SANDSTONE = register(
		"cut_red_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.ORANGE).requiresTool().strength(0.8F))
	);
	public static final Block RED_SANDSTONE_STAIRS = register(
		"red_sandstone_stairs", new StairsBlock(RED_SANDSTONE.getDefaultState(), AbstractBlock.Settings.copy(RED_SANDSTONE))
	);
	public static final Block OAK_SLAB = register(
		"oak_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_SLAB = register(
		"spruce_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_SLAB = register(
		"birch_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SAND).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_SLAB = register(
		"jungle_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_SLAB = register(
		"acacia_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.ORANGE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_SLAB = register(
		"dark_oak_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STONE_SLAB = register(
		"stone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_STONE_SLAB = register(
		"smooth_stone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SANDSTONE_SLAB = register(
		"sandstone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block CUT_SANDSTONE_SLAB = register(
		"cut_sandstone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block PETRIFIED_OAK_SLAB = register(
		"petrified_oak_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.WOOD).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block COBBLESTONE_SLAB = register(
		"cobblestone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block BRICK_SLAB = register(
		"brick_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.RED).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block STONE_BRICK_SLAB = register(
		"stone_brick_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block NETHER_BRICK_SLAB = register(
		"nether_brick_slab",
		new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block QUARTZ_SLAB = register(
		"quartz_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.QUARTZ).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block RED_SANDSTONE_SLAB = register(
		"red_sandstone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.ORANGE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block CUT_RED_SANDSTONE_SLAB = register(
		"cut_red_sandstone_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.ORANGE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block PURPUR_SLAB = register(
		"purpur_slab", new SlabBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.MAGENTA).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_STONE = register(
		"smooth_stone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_SANDSTONE = register(
		"smooth_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_QUARTZ = register(
		"smooth_quartz", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.QUARTZ).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_RED_SANDSTONE = register(
		"smooth_red_sandstone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.ORANGE).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SPRUCE_FENCE_GATE = register(
		"spruce_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, SPRUCE_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_FENCE_GATE = register(
		"birch_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, BIRCH_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_FENCE_GATE = register(
		"jungle_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, JUNGLE_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_FENCE_GATE = register(
		"acacia_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, ACACIA_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_FENCE_GATE = register(
		"dark_oak_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_FENCE = register(
		"spruce_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, SPRUCE_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BIRCH_FENCE = register(
		"birch_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, BIRCH_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block JUNGLE_FENCE = register(
		"jungle_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, JUNGLE_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block ACACIA_FENCE = register(
		"acacia_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, ACACIA_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block DARK_OAK_FENCE = register(
		"dark_oak_fence",
		new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_PLANKS.getDefaultMaterialColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block SPRUCE_DOOR = register(
		"spruce_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, SPRUCE_PLANKS.getDefaultMaterialColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block BIRCH_DOOR = register(
		"birch_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, BIRCH_PLANKS.getDefaultMaterialColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block JUNGLE_DOOR = register(
		"jungle_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, JUNGLE_PLANKS.getDefaultMaterialColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block ACACIA_DOOR = register(
		"acacia_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, ACACIA_PLANKS.getDefaultMaterialColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block DARK_OAK_DOOR = register(
		"dark_oak_door",
		new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, DARK_OAK_PLANKS.getDefaultMaterialColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block END_ROD = register(
		"end_rod", new EndRodBlock(AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().lightLevel(state -> 14).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block CHORUS_PLANT = register(
		"chorus_plant", new ChorusPlantBlock(AbstractBlock.Settings.of(Material.PLANT, MaterialColor.PURPLE).strength(0.4F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block CHORUS_FLOWER = register(
		"chorus_flower",
		new ChorusFlowerBlock(
			(ChorusPlantBlock)CHORUS_PLANT,
			AbstractBlock.Settings.of(Material.PLANT, MaterialColor.PURPLE).ticksRandomly().strength(0.4F).sounds(BlockSoundGroup.WOOD).nonOpaque()
		)
	);
	public static final Block PURPUR_BLOCK = register(
		"purpur_block", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.MAGENTA).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PURPUR_PILLAR = register(
		"purpur_pillar", new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.MAGENTA).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PURPUR_STAIRS = register("purpur_stairs", new StairsBlock(PURPUR_BLOCK.getDefaultState(), AbstractBlock.Settings.copy(PURPUR_BLOCK)));
	public static final Block END_STONE_BRICKS = register(
		"end_stone_bricks", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().strength(3.0F, 9.0F))
	);
	public static final Block BEETROOTS = register(
		"beetroots", new BeetrootsBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block GRASS_PATH = register(
		"grass_path", new GrassPathBlock(AbstractBlock.Settings.of(Material.SOIL).strength(0.65F).sounds(BlockSoundGroup.GRASS).blockVision(Blocks::always))
	);
	public static final Block END_GATEWAY = register(
		"end_gateway",
		new EndGatewayBlock(
			AbstractBlock.Settings.of(Material.PORTAL, MaterialColor.BLACK).noCollision().lightLevel(state -> 15).strength(-1.0F, 3600000.0F).dropsNothing()
		)
	);
	public static final Block REPEATING_COMMAND_BLOCK = register(
		"repeating_command_block",
		new CommandBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.PURPLE).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block CHAIN_COMMAND_BLOCK = register(
		"chain_command_block",
		new CommandBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.GREEN).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
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
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER)
				.requiresTool()
				.lightLevel(state -> 3)
				.ticksRandomly()
				.strength(0.5F)
				.allowsSpawning((state, world, pos, entityType) -> entityType.isFireImmune())
				.postProcess(Blocks::always)
				.emissiveLighting(Blocks::always)
		)
	);
	public static final Block NETHER_WART_BLOCK = register(
		"nether_wart_block", new Block(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MaterialColor.RED).strength(1.0F).sounds(BlockSoundGroup.WART_BLOCK))
	);
	public static final Block RED_NETHER_BRICKS = register(
		"red_nether_bricks",
		new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block BONE_BLOCK = register(
		"bone_block", new PillarBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().strength(2.0F).sounds(BlockSoundGroup.BONE))
	);
	public static final Block STRUCTURE_VOID = register(
		"structure_void", new StructureVoidBlock(AbstractBlock.Settings.of(Material.STRUCTURE_VOID).noCollision().dropsNothing())
	);
	public static final Block OBSERVER = register(
		"observer", new ObserverBlock(AbstractBlock.Settings.of(Material.STONE).strength(3.0F).requiresTool().solidBlock(Blocks::never))
	);
	public static final Block SHULKER_BOX = register("shulker_box", createShulkerBoxBlock(null, AbstractBlock.Settings.of(Material.SHULKER_BOX)));
	public static final Block WHITE_SHULKER_BOX = register(
		"white_shulker_box", createShulkerBoxBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.WHITE))
	);
	public static final Block ORANGE_SHULKER_BOX = register(
		"orange_shulker_box", createShulkerBoxBlock(DyeColor.ORANGE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.ORANGE))
	);
	public static final Block MAGENTA_SHULKER_BOX = register(
		"magenta_shulker_box", createShulkerBoxBlock(DyeColor.MAGENTA, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.MAGENTA))
	);
	public static final Block LIGHT_BLUE_SHULKER_BOX = register(
		"light_blue_shulker_box", createShulkerBoxBlock(DyeColor.LIGHT_BLUE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.LIGHT_BLUE))
	);
	public static final Block YELLOW_SHULKER_BOX = register(
		"yellow_shulker_box", createShulkerBoxBlock(DyeColor.YELLOW, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.YELLOW))
	);
	public static final Block LIME_SHULKER_BOX = register(
		"lime_shulker_box", createShulkerBoxBlock(DyeColor.LIME, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.LIME))
	);
	public static final Block PINK_SHULKER_BOX = register(
		"pink_shulker_box", createShulkerBoxBlock(DyeColor.PINK, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.PINK))
	);
	public static final Block GRAY_SHULKER_BOX = register(
		"gray_shulker_box", createShulkerBoxBlock(DyeColor.GRAY, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.GRAY))
	);
	public static final Block LIGHT_GRAY_SHULKER_BOX = register(
		"light_gray_shulker_box", createShulkerBoxBlock(DyeColor.LIGHT_GRAY, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.LIGHT_GRAY))
	);
	public static final Block CYAN_SHULKER_BOX = register(
		"cyan_shulker_box", createShulkerBoxBlock(DyeColor.CYAN, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.CYAN))
	);
	public static final Block PURPLE_SHULKER_BOX = register(
		"purple_shulker_box", createShulkerBoxBlock(DyeColor.PURPLE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.PURPLE_TERRACOTTA))
	);
	public static final Block BLUE_SHULKER_BOX = register(
		"blue_shulker_box", createShulkerBoxBlock(DyeColor.BLUE, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.BLUE))
	);
	public static final Block BROWN_SHULKER_BOX = register(
		"brown_shulker_box", createShulkerBoxBlock(DyeColor.BROWN, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.BROWN))
	);
	public static final Block GREEN_SHULKER_BOX = register(
		"green_shulker_box", createShulkerBoxBlock(DyeColor.GREEN, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.GREEN))
	);
	public static final Block RED_SHULKER_BOX = register(
		"red_shulker_box", createShulkerBoxBlock(DyeColor.RED, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.RED))
	);
	public static final Block BLACK_SHULKER_BOX = register(
		"black_shulker_box", createShulkerBoxBlock(DyeColor.BLACK, AbstractBlock.Settings.of(Material.SHULKER_BOX, MaterialColor.BLACK))
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
		"dried_kelp_block", new Block(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MaterialColor.GREEN).strength(0.5F, 2.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block TURTLE_EGG = register(
		"turtle_egg",
		new TurtleEggBlock(AbstractBlock.Settings.of(Material.EGG, MaterialColor.SAND).strength(0.5F).sounds(BlockSoundGroup.METAL).ticksRandomly().nonOpaque())
	);
	public static final Block DEAD_TUBE_CORAL_BLOCK = register(
		"dead_tube_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_BRAIN_CORAL_BLOCK = register(
		"dead_brain_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_BUBBLE_CORAL_BLOCK = register(
		"dead_bubble_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_FIRE_CORAL_BLOCK = register(
		"dead_fire_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_HORN_CORAL_BLOCK = register(
		"dead_horn_coral_block", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block TUBE_CORAL_BLOCK = register(
		"tube_coral_block",
		new CoralBlockBlock(
			DEAD_TUBE_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLUE).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block BRAIN_CORAL_BLOCK = register(
		"brain_coral_block",
		new CoralBlockBlock(
			DEAD_BRAIN_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MaterialColor.PINK).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block BUBBLE_CORAL_BLOCK = register(
		"bubble_coral_block",
		new CoralBlockBlock(
			DEAD_BUBBLE_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MaterialColor.PURPLE).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block FIRE_CORAL_BLOCK = register(
		"fire_coral_block",
		new CoralBlockBlock(
			DEAD_FIRE_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MaterialColor.RED).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block HORN_CORAL_BLOCK = register(
		"horn_coral_block",
		new CoralBlockBlock(
			DEAD_HORN_CORAL_BLOCK, AbstractBlock.Settings.of(Material.STONE, MaterialColor.YELLOW).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block DEAD_TUBE_CORAL = register(
		"dead_tube_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_BRAIN_CORAL = register(
		"dead_brain_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_BUBBLE_CORAL = register(
		"dead_bubble_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_FIRE_CORAL = register(
		"dead_fire_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_HORN_CORAL = register(
		"dead_horn_coral", new DeadCoralBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block TUBE_CORAL = register(
		"tube_coral",
		new CoralBlock(
			DEAD_TUBE_CORAL, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.BLUE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block BRAIN_CORAL = register(
		"brain_coral",
		new CoralBlock(
			DEAD_BRAIN_CORAL, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PINK).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block BUBBLE_CORAL = register(
		"bubble_coral",
		new CoralBlock(
			DEAD_BUBBLE_CORAL,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PURPLE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block FIRE_CORAL = register(
		"fire_coral",
		new CoralBlock(
			DEAD_FIRE_CORAL, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.RED).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block HORN_CORAL = register(
		"horn_coral",
		new CoralBlock(
			DEAD_HORN_CORAL, AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.YELLOW).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block DEAD_TUBE_CORAL_FAN = register(
		"dead_tube_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_BRAIN_CORAL_FAN = register(
		"dead_brain_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_BUBBLE_CORAL_FAN = register(
		"dead_bubble_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_FIRE_CORAL_FAN = register(
		"dead_fire_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block DEAD_HORN_CORAL_FAN = register(
		"dead_horn_coral_fan", new DeadCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())
	);
	public static final Block TUBE_CORAL_FAN = register(
		"tube_coral_fan",
		new CoralFanBlock(
			DEAD_TUBE_CORAL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.BLUE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block BRAIN_CORAL_FAN = register(
		"brain_coral_fan",
		new CoralFanBlock(
			DEAD_BRAIN_CORAL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PINK).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block BUBBLE_CORAL_FAN = register(
		"bubble_coral_fan",
		new CoralFanBlock(
			DEAD_BUBBLE_CORAL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PURPLE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block FIRE_CORAL_FAN = register(
		"fire_coral_fan",
		new CoralFanBlock(
			DEAD_FIRE_CORAL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.RED).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block HORN_CORAL_FAN = register(
		"horn_coral_fan",
		new CoralFanBlock(
			DEAD_HORN_CORAL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.YELLOW).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block DEAD_TUBE_CORAL_WALL_FAN = register(
		"dead_tube_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_TUBE_CORAL_FAN)
		)
	);
	public static final Block DEAD_BRAIN_CORAL_WALL_FAN = register(
		"dead_brain_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_BRAIN_CORAL_FAN)
		)
	);
	public static final Block DEAD_BUBBLE_CORAL_WALL_FAN = register(
		"dead_bubble_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_BUBBLE_CORAL_FAN)
		)
	);
	public static final Block DEAD_FIRE_CORAL_WALL_FAN = register(
		"dead_fire_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_FIRE_CORAL_FAN)
		)
	);
	public static final Block DEAD_HORN_CORAL_WALL_FAN = register(
		"dead_horn_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(DEAD_HORN_CORAL_FAN)
		)
	);
	public static final Block TUBE_CORAL_WALL_FAN = register(
		"tube_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_TUBE_CORAL_WALL_FAN,
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.BLUE)
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
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PINK)
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
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PURPLE)
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
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.RED)
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
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.YELLOW)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(HORN_CORAL_FAN)
		)
	);
	public static final Block SEA_PICKLE = register(
		"sea_pickle",
		new SeaPickleBlock(
			AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.GREEN)
				.lightLevel(state -> SeaPickleBlock.isDry(state) ? 0 : 3 + 3 * (Integer)state.get(SeaPickleBlock.PICKLES))
				.sounds(BlockSoundGroup.SLIME)
				.nonOpaque()
		)
	);
	public static final Block BLUE_ICE = register(
		"blue_ice", new TransparentBlock(AbstractBlock.Settings.of(Material.DENSE_ICE).strength(2.8F).slipperiness(0.989F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block CONDUIT = register(
		"conduit", new ConduitBlock(AbstractBlock.Settings.of(Material.GLASS, MaterialColor.DIAMOND).strength(3.0F).lightLevel(state -> 15).nonOpaque())
	);
	public static final Block BAMBOO_SAPLING = register(
		"bamboo_sapling",
		new BambooSaplingBlock(
			AbstractBlock.Settings.of(Material.BAMBOO_SAPLING).ticksRandomly().breakInstantly().noCollision().strength(1.0F).sounds(BlockSoundGroup.BAMBOO_SAPLING)
		)
	);
	public static final Block BAMBOO = register(
		"bamboo",
		new BambooBlock(
			AbstractBlock.Settings.of(Material.BAMBOO, MaterialColor.FOLIAGE).ticksRandomly().breakInstantly().strength(1.0F).sounds(BlockSoundGroup.BAMBOO).nonOpaque()
		)
	);
	public static final Block POTTED_BAMBOO = register(
		"potted_bamboo", new FlowerPotBlock(BAMBOO, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
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
	public static final Block NETHER_BRICK_WALL = register("nether_brick_wall", new WallBlock(AbstractBlock.Settings.copy(NETHER_BRICKS)));
	public static final Block ANDESITE_WALL = register("andesite_wall", new WallBlock(AbstractBlock.Settings.copy(ANDESITE)));
	public static final Block RED_NETHER_BRICK_WALL = register("red_nether_brick_wall", new WallBlock(AbstractBlock.Settings.copy(RED_NETHER_BRICKS)));
	public static final Block SANDSTONE_WALL = register("sandstone_wall", new WallBlock(AbstractBlock.Settings.copy(SANDSTONE)));
	public static final Block END_STONE_BRICK_WALL = register("end_stone_brick_wall", new WallBlock(AbstractBlock.Settings.copy(END_STONE_BRICKS)));
	public static final Block DIORITE_WALL = register("diorite_wall", new WallBlock(AbstractBlock.Settings.copy(DIORITE)));
	public static final Block SCAFFOLDING = register(
		"scaffolding",
		new ScaffoldingBlock(AbstractBlock.Settings.of(Material.SUPPORTED, MaterialColor.SAND).noCollision().sounds(BlockSoundGroup.SCAFFOLDING).dynamicBounds())
	);
	public static final Block LOOM = register("loom", new LoomBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block BARREL = register("barrel", new BarrelBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block SMOKER = register(
		"smoker", new SmokerBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).lightLevel(createLightLevelFromBlockState(13)))
	);
	public static final Block BLAST_FURNACE = register(
		"blast_furnace",
		new BlastFurnaceBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).lightLevel(createLightLevelFromBlockState(13)))
	);
	public static final Block CARTOGRAPHY_TABLE = register(
		"cartography_table", new CartographyTableBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block FLETCHING_TABLE = register(
		"fletching_table", new FletchingTableBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block GRINDSTONE = register(
		"grindstone",
		new GrindstoneBlock(AbstractBlock.Settings.of(Material.REPAIR_STATION, MaterialColor.IRON).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.STONE))
	);
	public static final Block LECTERN = register("lectern", new LecternBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block SMITHING_TABLE = register(
		"smithing_table", new SmithingTableBlock(AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block STONECUTTER = register("stonecutter", new StonecutterBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F)));
	public static final Block BELL = register(
		"bell", new BellBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.GOLD).requiresTool().strength(5.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block LANTERN = register(
		"lantern",
		new LanternBlock(AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(3.5F).sounds(BlockSoundGroup.LANTERN).lightLevel(state -> 15).nonOpaque())
	);
	public static final Block SOUL_LANTERN = register(
		"soul_lantern",
		new LanternBlock(AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(3.5F).sounds(BlockSoundGroup.LANTERN).lightLevel(state -> 10).nonOpaque())
	);
	public static final Block CAMPFIRE = register(
		"campfire",
		new CampfireBlock(
			true,
			1,
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SPRUCE)
				.strength(2.0F)
				.sounds(BlockSoundGroup.WOOD)
				.lightLevel(createLightLevelFromBlockState(15))
				.nonOpaque()
		)
	);
	public static final Block SOUL_CAMPFIRE = register(
		"soul_campfire",
		new CampfireBlock(
			false,
			2,
			AbstractBlock.Settings.of(Material.WOOD, MaterialColor.SPRUCE)
				.strength(2.0F)
				.sounds(BlockSoundGroup.WOOD)
				.lightLevel(createLightLevelFromBlockState(10))
				.nonOpaque()
		)
	);
	public static final Block SWEET_BERRY_BUSH = register(
		"sweet_berry_bush", new SweetBerryBushBlock(AbstractBlock.Settings.of(Material.PLANT).ticksRandomly().noCollision().sounds(BlockSoundGroup.SWEET_BERRY_BUSH))
	);
	public static final Block WARPED_STEM = register("warped_stem", createNetherStemBlock(MaterialColor.field_25706));
	public static final Block STRIPPED_WARPED_STEM = register("stripped_warped_stem", createNetherStemBlock(MaterialColor.field_25706));
	public static final Block WARPED_HYPHAE = register(
		"warped_hyphae",
		new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.field_25707).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM))
	);
	public static final Block STRIPPED_WARPED_HYPHAE = register(
		"stripped_warped_hyphae",
		new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.field_25707).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM))
	);
	public static final Block WARPED_NYLIUM = register(
		"warped_nylium",
		new NyliumBlock(
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.field_25705).requiresTool().strength(0.4F).sounds(BlockSoundGroup.NYLIUM).ticksRandomly()
		)
	);
	public static final Block WARPED_FUNGUS = register(
		"warped_fungus",
		new FungusBlock(
			AbstractBlock.Settings.of(Material.PLANT, MaterialColor.CYAN).breakInstantly().noCollision().sounds(BlockSoundGroup.FUNGUS),
			() -> Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG)
		)
	);
	public static final Block WARPED_WART_BLOCK = register(
		"warped_wart_block",
		new Block(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MaterialColor.field_25708).strength(1.0F).sounds(BlockSoundGroup.WART_BLOCK))
	);
	public static final Block WARPED_ROOTS = register(
		"warped_roots",
		new RootsBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT, MaterialColor.CYAN).noCollision().breakInstantly().sounds(BlockSoundGroup.ROOTS))
	);
	public static final Block NETHER_SPROUTS = register(
		"nether_sprouts",
		new SproutsBlock(
			AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT, MaterialColor.CYAN).noCollision().breakInstantly().sounds(BlockSoundGroup.NETHER_SPROUTS)
		)
	);
	public static final Block CRIMSON_STEM = register("crimson_stem", createNetherStemBlock(MaterialColor.field_25703));
	public static final Block STRIPPED_CRIMSON_STEM = register("stripped_crimson_stem", createNetherStemBlock(MaterialColor.field_25703));
	public static final Block CRIMSON_HYPHAE = register(
		"crimson_hyphae",
		new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.field_25704).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM))
	);
	public static final Block STRIPPED_CRIMSON_HYPHAE = register(
		"stripped_crimson_hyphae",
		new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.field_25704).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM))
	);
	public static final Block CRIMSON_NYLIUM = register(
		"crimson_nylium",
		new NyliumBlock(
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.field_25702).requiresTool().strength(0.4F).sounds(BlockSoundGroup.NYLIUM).ticksRandomly()
		)
	);
	public static final Block CRIMSON_FUNGUS = register(
		"crimson_fungus",
		new FungusBlock(
			AbstractBlock.Settings.of(Material.PLANT, MaterialColor.NETHER).breakInstantly().noCollision().sounds(BlockSoundGroup.FUNGUS),
			() -> Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG)
		)
	);
	public static final Block SHROOMLIGHT = register(
		"shroomlight",
		new Block(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MaterialColor.RED).strength(1.0F).sounds(BlockSoundGroup.SHROOMLIGHT).lightLevel(state -> 15))
	);
	public static final Block WEEPING_VINES = register(
		"weeping_vines",
		new WeepingVinesBlock(
			AbstractBlock.Settings.of(Material.PLANT, MaterialColor.NETHER).ticksRandomly().noCollision().breakInstantly().sounds(BlockSoundGroup.WEEPING_VINES)
		)
	);
	public static final Block WEEPING_VINES_PLANT = register(
		"weeping_vines_plant",
		new WeepingVinesPlantBlock(
			AbstractBlock.Settings.of(Material.PLANT, MaterialColor.NETHER).noCollision().breakInstantly().sounds(BlockSoundGroup.WEEPING_VINES)
		)
	);
	public static final Block TWISTING_VINES = register(
		"twisting_vines",
		new TwistingVinesBlock(
			AbstractBlock.Settings.of(Material.PLANT, MaterialColor.CYAN).ticksRandomly().noCollision().breakInstantly().sounds(BlockSoundGroup.WEEPING_VINES)
		)
	);
	public static final Block TWISTING_VINES_PLANT = register(
		"twisting_vines_plant",
		new TwistingVinesPlantBlock(
			AbstractBlock.Settings.of(Material.PLANT, MaterialColor.CYAN).noCollision().breakInstantly().sounds(BlockSoundGroup.WEEPING_VINES)
		)
	);
	public static final Block CRIMSON_ROOTS = register(
		"crimson_roots",
		new RootsBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT, MaterialColor.NETHER).noCollision().breakInstantly().sounds(BlockSoundGroup.ROOTS))
	);
	public static final Block CRIMSON_PLANKS = register(
		"crimson_planks", new Block(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.field_25703).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_PLANKS = register(
		"warped_planks", new Block(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.field_25706).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_SLAB = register(
		"crimson_slab", new SlabBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.NETHER).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_SLAB = register(
		"warped_slab", new SlabBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.CYAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_PRESSURE_PLATE = register(
		"crimson_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.NETHER).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block WARPED_PRESSURE_PLATE = register(
		"warped_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.ActivationRule.EVERYTHING,
			AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.CYAN).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block CRIMSON_FENCE = register(
		"crimson_fence", new FenceBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.NETHER).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_FENCE = register(
		"warped_fence", new FenceBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.CYAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_TRAPDOOR = register(
		"crimson_trapdoor",
		new TrapdoorBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.NETHER).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block WARPED_TRAPDOOR = register(
		"warped_trapdoor",
		new TrapdoorBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.CYAN).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block CRIMSON_FENCE_GATE = register(
		"crimson_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.NETHER).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_FENCE_GATE = register(
		"warped_fence_gate",
		new FenceGateBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.CYAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_STAIRS = register(
		"crimson_stairs", new StairsBlock(CRIMSON_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(CRIMSON_PLANKS))
	);
	public static final Block WARPED_STAIRS = register(
		"warped_stairs", new StairsBlock(WARPED_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(WARPED_PLANKS))
	);
	public static final Block CRIMSON_BUTTON = register(
		"crimson_button", new WoodButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block WARPED_BUTTON = register(
		"warped_button", new WoodButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block CRIMSON_DOOR = register(
		"crimson_door",
		new DoorBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD, CRIMSON_PLANKS.getDefaultMaterialColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()
		)
	);
	public static final Block WARPED_DOOR = register(
		"warped_door",
		new DoorBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD, WARPED_PLANKS.getDefaultMaterialColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()
		)
	);
	public static final Block CRIMSON_SIGN = register(
		"crimson_sign",
		new SignBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.NETHER).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.CRIMSON
		)
	);
	public static final Block WARPED_SIGN = register(
		"warped_sign",
		new SignBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, MaterialColor.CYAN).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), SignType.WARPED)
	);
	public static final Block CRIMSON_WALL_SIGN = register(
		"crimson_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(CRIMSON_SIGN), SignType.CRIMSON
		)
	);
	public static final Block WARPED_WALL_SIGN = register(
		"warped_wall_sign",
		new WallSignBlock(
			AbstractBlock.Settings.of(Material.NETHER_WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(WARPED_SIGN), SignType.WARPED
		)
	);
	public static final Block STRUCTURE_BLOCK = register(
		"structure_block",
		new StructureBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.LIGHT_GRAY).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block JIGSAW = register(
		"jigsaw", new JigsawBlock(AbstractBlock.Settings.of(Material.METAL, MaterialColor.LIGHT_GRAY).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block COMPOSTER = register(
		"composter", new ComposterBlock(AbstractBlock.Settings.of(Material.WOOD).strength(0.6F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block TARGET = register(
		"target", new TargetBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC, MaterialColor.QUARTZ).strength(0.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block BEE_NEST = register(
		"bee_nest", new BeehiveBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.YELLOW).strength(0.3F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block BEEHIVE = register("beehive", new BeehiveBlock(AbstractBlock.Settings.of(Material.WOOD).strength(0.6F).sounds(BlockSoundGroup.WOOD)));
	public static final Block HONEY_BLOCK = register(
		"honey_block",
		new HoneyBlock(
			AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MaterialColor.ORANGE)
				.velocityMultiplier(0.4F)
				.jumpVelocityMultiplier(0.5F)
				.nonOpaque()
				.sounds(BlockSoundGroup.HONEY)
		)
	);
	public static final Block HONEYCOMB_BLOCK = register(
		"honeycomb_block", new Block(AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MaterialColor.ORANGE).strength(0.6F).sounds(BlockSoundGroup.CORAL))
	);
	public static final Block NETHERITE_BLOCK = register(
		"netherite_block",
		new Block(AbstractBlock.Settings.of(Material.METAL, MaterialColor.BLACK).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.NETHERITE))
	);
	public static final Block ANCIENT_DEBRIS = register(
		"ancient_debris",
		new Block(AbstractBlock.Settings.of(Material.METAL, MaterialColor.BLACK).requiresTool().strength(30.0F, 1200.0F).sounds(BlockSoundGroup.ANCIENT_DEBRIS))
	);
	public static final Block CRYING_OBSIDIAN = register(
		"crying_obsidian",
		new CryingObsidianBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK).requiresTool().strength(50.0F, 1200.0F).lightLevel(state -> 10))
	);
	public static final Block RESPAWN_ANCHOR = register(
		"respawn_anchor",
		new RespawnAnchorBlock(
			AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK)
				.requiresTool()
				.strength(50.0F, 1200.0F)
				.lightLevel(state -> RespawnAnchorBlock.getLightLevel(state, 15))
		)
	);
	public static final Block POTTED_CRIMSON_FUNGUS = register(
		"potted_crimson_fungus", new FlowerPotBlock(CRIMSON_FUNGUS, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_WARPED_FUNGUS = register(
		"potted_warped_fungus", new FlowerPotBlock(WARPED_FUNGUS, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_CRIMSON_ROOTS = register(
		"potted_crimson_roots", new FlowerPotBlock(CRIMSON_ROOTS, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block POTTED_WARPED_ROOTS = register(
		"potted_warped_roots", new FlowerPotBlock(WARPED_ROOTS, AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().nonOpaque())
	);
	public static final Block LODESTONE = register(
		"lodestone", new Block(AbstractBlock.Settings.of(Material.REPAIR_STATION).requiresTool().strength(3.5F).sounds(BlockSoundGroup.LODESTONE))
	);
	public static final Block BLACKSTONE = register(
		"blackstone", new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK).requiresTool().strength(1.5F, 6.0F))
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
			PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK).requiresTool().noCollision().strength(0.5F)
		)
	);
	public static final Block POLISHED_BLACKSTONE_BUTTON = register(
		"polished_blackstone_button", new StoneButtonBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F))
	);
	public static final Block POLISHED_BLACKSTONE_WALL = register("polished_blackstone_wall", new WallBlock(AbstractBlock.Settings.copy(POLISHED_BLACKSTONE)));
	public static final Block CHISELED_NETHER_BRICKS = register(
		"chiseled_nether_bricks",
		new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block CRACKED_NETHER_BRICKS = register(
		"cracked_nether_bricks",
		new Block(AbstractBlock.Settings.of(Material.STONE, MaterialColor.NETHER).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.NETHER_BRICKS))
	);
	public static final Block QUARTZ_BRICKS = register("quartz_bricks", new Block(AbstractBlock.Settings.copy(QUARTZ_BLOCK)));

	private static ToIntFunction<BlockState> createLightLevelFromBlockState(int litLevel) {
		return blockState -> blockState.get(Properties.LIT) ? litLevel : 0;
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
			AbstractBlock.Settings.of(Material.WOOL, blockState -> blockState.get(BedBlock.PART) == BedPart.FOOT ? color.getMaterialColor() : MaterialColor.WEB)
				.sounds(BlockSoundGroup.WOOD)
				.strength(0.2F)
				.nonOpaque()
		);
	}

	private static PillarBlock createLogBlock(MaterialColor topMaterialColor, MaterialColor sideMaterialColor) {
		return new PillarBlock(
			AbstractBlock.Settings.of(Material.WOOD, blockState -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMaterialColor : sideMaterialColor)
				.strength(2.0F)
				.sounds(BlockSoundGroup.WOOD)
		);
	}

	private static Block createNetherStemBlock(MaterialColor materialColor) {
		return new PillarBlock(AbstractBlock.Settings.of(Material.NETHER_WOOD, blockState -> materialColor).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM));
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

	private static LeavesBlock createLeavesBlock() {
		return new LeavesBlock(
			AbstractBlock.Settings.of(Material.LEAVES)
				.strength(0.2F)
				.ticksRandomly()
				.sounds(BlockSoundGroup.GRASS)
				.nonOpaque()
				.allowsSpawning(Blocks::canSpawnOnLeaves)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		);
	}

	private static ShulkerBoxBlock createShulkerBoxBlock(DyeColor color, AbstractBlock.Settings settings) {
		AbstractBlock.ContextPredicate contextPredicate = (blockState, blockView, blockPos) -> {
			BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
			if (!(blockEntity instanceof ShulkerBoxBlockEntity)) {
				return true;
			} else {
				ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
				return shulkerBoxBlockEntity.suffocates();
			}
		};
		return new ShulkerBoxBlock(color, settings.strength(2.0F).dynamicBounds().nonOpaque().suffocates(contextPredicate).blockVision(contextPredicate));
	}

	private static PistonBlock createPistonBlock(boolean sticky) {
		AbstractBlock.ContextPredicate contextPredicate = (blockState, blockView, blockPos) -> !(Boolean)blockState.get(PistonBlock.EXTENDED);
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
