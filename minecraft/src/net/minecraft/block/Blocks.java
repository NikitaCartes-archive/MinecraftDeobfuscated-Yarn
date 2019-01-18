package net.minecraft.block;

import net.minecraft.block.sapling.AcaciaSaplingGenerator;
import net.minecraft.block.sapling.BirchSaplingGenerator;
import net.minecraft.block.sapling.DarkOakSaplingGenerator;
import net.minecraft.block.sapling.JungleSaplingGenerator;
import net.minecraft.block.sapling.OakSaplingGenerator;
import net.minecraft.block.sapling.SpruceSaplingGenerator;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

public class Blocks {
	public static final Block field_10124 = register("air", new AirBlock(Block.Settings.of(Material.AIR).noCollision().dropsNothing()));
	public static final Block field_10340 = register("stone", new Block(Block.Settings.of(Material.STONE, MaterialColor.STONE).strength(1.5F, 6.0F)));
	public static final Block field_10474 = register("granite", new Block(Block.Settings.of(Material.STONE, MaterialColor.DIRT).strength(1.5F, 6.0F)));
	public static final Block field_10289 = register("polished_granite", new Block(Block.Settings.of(Material.STONE, MaterialColor.DIRT).strength(1.5F, 6.0F)));
	public static final Block field_10508 = register("diorite", new Block(Block.Settings.of(Material.STONE, MaterialColor.QUARTZ).strength(1.5F, 6.0F)));
	public static final Block field_10346 = register("polished_diorite", new Block(Block.Settings.of(Material.STONE, MaterialColor.QUARTZ).strength(1.5F, 6.0F)));
	public static final Block field_10115 = register("andesite", new Block(Block.Settings.of(Material.STONE, MaterialColor.STONE).strength(1.5F, 6.0F)));
	public static final Block field_10093 = register("polished_andesite", new Block(Block.Settings.of(Material.STONE, MaterialColor.STONE).strength(1.5F, 6.0F)));
	public static final Block field_10219 = register(
		"grass_block", new GrassBlock(Block.Settings.of(Material.ORGANIC).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10566 = register(
		"dirt", new Block(Block.Settings.of(Material.EARTH, MaterialColor.DIRT).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block field_10253 = register(
		"coarse_dirt", new Block(Block.Settings.of(Material.EARTH, MaterialColor.DIRT).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block field_10520 = register(
		"podzol", new SnowyBlock(Block.Settings.of(Material.EARTH, MaterialColor.SPRUCE).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block field_10445 = register("cobblestone", new Block(Block.Settings.of(Material.STONE).strength(2.0F, 6.0F)));
	public static final Block field_10161 = register(
		"oak_planks", new Block(Block.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_9975 = register(
		"spruce_planks", new Block(Block.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10148 = register(
		"birch_planks", new Block(Block.Settings.of(Material.WOOD, MaterialColor.SAND).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10334 = register(
		"jungle_planks", new Block(Block.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10218 = register(
		"acacia_planks", new Block(Block.Settings.of(Material.WOOD, MaterialColor.ORANGE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10075 = register(
		"dark_oak_planks", new Block(Block.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10394 = register(
		"oak_sapling",
		new SaplingBlock(new OakSaplingGenerator(), Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10217 = register(
		"spruce_sapling",
		new SaplingBlock(new SpruceSaplingGenerator(), Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10575 = register(
		"birch_sapling",
		new SaplingBlock(new BirchSaplingGenerator(), Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10276 = register(
		"jungle_sapling",
		new SaplingBlock(new JungleSaplingGenerator(), Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10385 = register(
		"acacia_sapling",
		new SaplingBlock(new AcaciaSaplingGenerator(), Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10160 = register(
		"dark_oak_sapling",
		new SaplingBlock(
			new DarkOakSaplingGenerator(), Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)
		)
	);
	public static final Block field_9987 = register("bedrock", new Block(Block.Settings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing()));
	public static final Block field_10382 = register(
		"water", new FluidBlock(Fluids.WATER, Block.Settings.of(Material.WATER).noCollision().strength(100.0F).dropsNothing())
	);
	public static final Block field_10164 = register(
		"lava", new FluidBlock(Fluids.LAVA, Block.Settings.of(Material.LAVA).noCollision().ticksRandomly().strength(100.0F).lightLevel(15).dropsNothing())
	);
	public static final Block field_10102 = register(
		"sand", new SandBlock(14406560, Block.Settings.of(Material.SAND, MaterialColor.SAND).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10534 = register(
		"red_sand", new SandBlock(11098145, Block.Settings.of(Material.SAND, MaterialColor.ORANGE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10255 = register(
		"gravel", new GravelBlock(Block.Settings.of(Material.SAND, MaterialColor.STONE).strength(0.6F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block field_10571 = register("gold_ore", new OreBlock(Block.Settings.of(Material.STONE).strength(3.0F, 3.0F)));
	public static final Block field_10212 = register("iron_ore", new OreBlock(Block.Settings.of(Material.STONE).strength(3.0F, 3.0F)));
	public static final Block field_10418 = register("coal_ore", new OreBlock(Block.Settings.of(Material.STONE).strength(3.0F, 3.0F)));
	public static final Block field_10431 = register(
		"oak_log", new LogBlock(MaterialColor.WOOD, Block.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10037 = register(
		"spruce_log", new LogBlock(MaterialColor.SPRUCE, Block.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10511 = register(
		"birch_log", new LogBlock(MaterialColor.SAND, Block.Settings.of(Material.WOOD, MaterialColor.QUARTZ).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10306 = register(
		"jungle_log", new LogBlock(MaterialColor.DIRT, Block.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10533 = register(
		"acacia_log", new LogBlock(MaterialColor.ORANGE, Block.Settings.of(Material.WOOD, MaterialColor.STONE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10010 = register(
		"dark_oak_log", new LogBlock(MaterialColor.BROWN, Block.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10436 = register(
		"stripped_spruce_log", new LogBlock(MaterialColor.SPRUCE, Block.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10366 = register(
		"stripped_birch_log", new LogBlock(MaterialColor.SAND, Block.Settings.of(Material.WOOD, MaterialColor.SAND).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10254 = register(
		"stripped_jungle_log", new LogBlock(MaterialColor.DIRT, Block.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10622 = register(
		"stripped_acacia_log", new LogBlock(MaterialColor.ORANGE, Block.Settings.of(Material.WOOD, MaterialColor.ORANGE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10244 = register(
		"stripped_dark_oak_log", new LogBlock(MaterialColor.BROWN, Block.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10519 = register(
		"stripped_oak_log", new LogBlock(MaterialColor.WOOD, Block.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10126 = register(
		"oak_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10155 = register(
		"spruce_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10307 = register(
		"birch_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.SAND).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10303 = register(
		"jungle_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_9999 = register(
		"acacia_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.GRAY).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10178 = register(
		"dark_oak_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10250 = register(
		"stripped_oak_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10558 = register(
		"stripped_spruce_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10204 = register(
		"stripped_birch_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.SAND).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10084 = register(
		"stripped_jungle_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10103 = register(
		"stripped_acacia_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.ORANGE).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10374 = register(
		"stripped_dark_oak_wood", new PillarBlock(Block.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10503 = register(
		"oak_leaves", new LeavesBlock(Block.Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_9988 = register(
		"spruce_leaves", new LeavesBlock(Block.Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10539 = register(
		"birch_leaves", new LeavesBlock(Block.Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10335 = register(
		"jungle_leaves", new LeavesBlock(Block.Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10098 = register(
		"acacia_leaves", new LeavesBlock(Block.Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10035 = register(
		"dark_oak_leaves", new LeavesBlock(Block.Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10258 = register("sponge", new SpongeBlock(Block.Settings.of(Material.SPONGE).strength(0.6F).sounds(BlockSoundGroup.GRASS)));
	public static final Block field_10562 = register(
		"wet_sponge", new WetSpongeBlock(Block.Settings.of(Material.SPONGE).strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10033 = register("glass", new GlassBlock(Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS)));
	public static final Block field_10090 = register("lapis_ore", new OreBlock(Block.Settings.of(Material.STONE).strength(3.0F, 3.0F)));
	public static final Block field_10441 = register("lapis_block", new Block(Block.Settings.of(Material.METAL, MaterialColor.LAPIS).strength(3.0F, 3.0F)));
	public static final Block field_10200 = register("dispenser", new DispenserBlock(Block.Settings.of(Material.STONE).strength(3.5F)));
	public static final Block field_9979 = register("sandstone", new Block(Block.Settings.of(Material.STONE, MaterialColor.SAND).strength(0.8F)));
	public static final Block field_10292 = register("chiseled_sandstone", new Block(Block.Settings.of(Material.STONE, MaterialColor.SAND).strength(0.8F)));
	public static final Block field_10361 = register("cut_sandstone", new Block(Block.Settings.of(Material.STONE, MaterialColor.SAND).strength(0.8F)));
	public static final Block field_10179 = register("note_block", new NoteBlock(Block.Settings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(0.8F)));
	public static final Block field_10120 = register(
		"white_bed", new BedBlock(DyeColor.WHITE, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10410 = register(
		"orange_bed", new BedBlock(DyeColor.ORANGE, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10230 = register(
		"magenta_bed", new BedBlock(DyeColor.MAGENTA, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10621 = register(
		"light_blue_bed", new BedBlock(DyeColor.LIGHT_BLUE, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10356 = register(
		"yellow_bed", new BedBlock(DyeColor.YELLOW, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10180 = register(
		"lime_bed", new BedBlock(DyeColor.LIME, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10610 = register(
		"pink_bed", new BedBlock(DyeColor.PINK, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10141 = register(
		"gray_bed", new BedBlock(DyeColor.GRAY, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10326 = register(
		"light_gray_bed", new BedBlock(DyeColor.LIGHT_GRAY, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10109 = register(
		"cyan_bed", new BedBlock(DyeColor.CYAN, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10019 = register(
		"purple_bed", new BedBlock(DyeColor.PURPLE, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10527 = register(
		"blue_bed", new BedBlock(DyeColor.BLUE, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10288 = register(
		"brown_bed", new BedBlock(DyeColor.BROWN, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10561 = register(
		"green_bed", new BedBlock(DyeColor.GREEN, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10069 = register(
		"red_bed", new BedBlock(DyeColor.RED, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10461 = register(
		"black_bed", new BedBlock(DyeColor.BLACK, Block.Settings.of(Material.WOOL).sounds(BlockSoundGroup.WOOD).strength(0.2F))
	);
	public static final Block field_10425 = register(
		"powered_rail", new PoweredRailBlock(Block.Settings.of(Material.PART).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10025 = register(
		"detector_rail", new DetectorRailBlock(Block.Settings.of(Material.PART).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10615 = register("sticky_piston", new PistonBlock(true, Block.Settings.of(Material.PISTON).strength(0.5F)));
	public static final Block field_10343 = register("cobweb", new WebBlock(Block.Settings.of(Material.COBWEB).noCollision().strength(4.0F)));
	public static final Block field_10479 = register(
		"grass", new FernBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10112 = register(
		"fern", new FernBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10428 = register(
		"dead_bush",
		new DeadBushBlock(Block.Settings.of(Material.REPLACEABLE_PLANT, MaterialColor.WOOD).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10376 = register(
		"seagrass", new SeagrassBlock(Block.Settings.of(Material.SEAGRASS).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block field_10238 = register(
		"tall_seagrass", new TallSeagrassBlock(Block.Settings.of(Material.SEAGRASS).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block field_10560 = register("piston", new PistonBlock(false, Block.Settings.of(Material.PISTON).strength(0.5F)));
	public static final Block field_10379 = register("piston_head", new PistonHeadBlock(Block.Settings.of(Material.PISTON).strength(0.5F).dropsNothing()));
	public static final Block field_10446 = register(
		"white_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.WHITE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10095 = register(
		"orange_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.ORANGE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10215 = register(
		"magenta_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.MAGENTA).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10294 = register(
		"light_blue_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.LIGHT_BLUE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10490 = register(
		"yellow_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.YELLOW).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10028 = register(
		"lime_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.LIME).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10459 = register(
		"pink_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.PINK).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10423 = register(
		"gray_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.GRAY).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10222 = register(
		"light_gray_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.LIGHT_GRAY).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10619 = register(
		"cyan_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.CYAN).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10259 = register(
		"purple_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.PURPLE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10514 = register(
		"blue_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.BLUE).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10113 = register(
		"brown_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.BROWN).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10170 = register(
		"green_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.GREEN).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10314 = register(
		"red_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.RED).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10146 = register(
		"black_wool", new Block(Block.Settings.of(Material.WOOL, MaterialColor.BLACK).strength(0.8F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10008 = register(
		"moving_piston", new PistonExtensionBlock(Block.Settings.of(Material.PISTON).strength(-1.0F).hasDynamicBounds().dropsNothing())
	);
	public static final Block field_10182 = register(
		"dandelion", new FlowerBlock(StatusEffects.field_5922, 5, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10449 = register(
		"poppy", new FlowerBlock(StatusEffects.field_5904, 6, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10086 = register(
		"blue_orchid", new FlowerBlock(StatusEffects.field_5922, 5, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10226 = register(
		"allium", new FlowerBlock(StatusEffects.field_5918, 4, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10573 = register(
		"azure_bluet", new FlowerBlock(StatusEffects.field_5919, 8, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10270 = register(
		"red_tulip", new FlowerBlock(StatusEffects.field_5911, 9, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10048 = register(
		"orange_tulip", new FlowerBlock(StatusEffects.field_5911, 9, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10156 = register(
		"white_tulip", new FlowerBlock(StatusEffects.field_5911, 9, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10315 = register(
		"pink_tulip", new FlowerBlock(StatusEffects.field_5911, 9, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10554 = register(
		"oxeye_daisy", new FlowerBlock(StatusEffects.field_5924, 8, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_9995 = register(
		"cornflower", new FlowerBlock(StatusEffects.field_5913, 6, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10606 = register(
		"wither_rose", new WitherRoseBlock(StatusEffects.field_5920, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10548 = register(
		"lily_of_the_valley",
		new FlowerBlock(StatusEffects.field_5899, 12, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10251 = register(
		"brown_mushroom",
		new MushroomPlantBlock(Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS).lightLevel(1))
	);
	public static final Block field_10559 = register(
		"red_mushroom", new MushroomPlantBlock(Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10205 = register(
		"gold_block", new Block(Block.Settings.of(Material.METAL, MaterialColor.GOLD).strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10085 = register(
		"iron_block", new Block(Block.Settings.of(Material.METAL, MaterialColor.IRON).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10104 = register("bricks", new Block(Block.Settings.of(Material.STONE, MaterialColor.RED).strength(2.0F, 6.0F)));
	public static final Block field_10375 = register("tnt", new TntBlock(Block.Settings.of(Material.TNT).breakInstantly().sounds(BlockSoundGroup.GRASS)));
	public static final Block field_10504 = register("bookshelf", new Block(Block.Settings.of(Material.WOOD).strength(1.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block field_9989 = register("mossy_cobblestone", new Block(Block.Settings.of(Material.STONE).strength(2.0F, 6.0F)));
	public static final Block field_10540 = register("obsidian", new Block(Block.Settings.of(Material.STONE, MaterialColor.BLACK).strength(50.0F, 1200.0F)));
	public static final Block field_10336 = register(
		"torch", new TorchBlock(Block.Settings.of(Material.PART).noCollision().breakInstantly().lightLevel(14).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10099 = register(
		"wall_torch",
		new WallTorchBlock(Block.Settings.of(Material.PART).noCollision().breakInstantly().lightLevel(14).sounds(BlockSoundGroup.WOOD).dropsLike(field_10336))
	);
	public static final Block field_10036 = register(
		"fire",
		new FireBlock(
			Block.Settings.of(Material.FIRE, MaterialColor.LAVA)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.lightLevel(15)
				.sounds(BlockSoundGroup.WOOL)
				.dropsNothing()
		)
	);
	public static final Block field_10260 = register(
		"spawner", new MobSpawnerBlock(Block.Settings.of(Material.STONE).strength(5.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10563 = register("oak_stairs", new StairsBlock(field_10161.getDefaultState(), Block.Settings.copy(field_10161)));
	public static final Block field_10034 = register("chest", new ChestBlock(Block.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block field_10091 = register("redstone_wire", new RedstoneWireBlock(Block.Settings.of(Material.PART).noCollision().breakInstantly()));
	public static final Block field_10442 = register("diamond_ore", new OreBlock(Block.Settings.of(Material.STONE).strength(3.0F, 3.0F)));
	public static final Block field_10201 = register(
		"diamond_block", new Block(Block.Settings.of(Material.METAL, MaterialColor.DIAMOND).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_9980 = register(
		"crafting_table", new CraftingTableBlock(Block.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10293 = register(
		"wheat", new CropBlock(Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block field_10362 = register(
		"farmland", new FarmlandBlock(Block.Settings.of(Material.EARTH).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block field_10181 = register("furnace", new FurnaceBlock(Block.Settings.of(Material.STONE).strength(3.5F).lightLevel(13)));
	public static final Block field_10121 = register(
		"oak_sign", new StandingSignBlock(Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10411 = register(
		"spruce_sign", new StandingSignBlock(Block.Settings.of(Material.WOOD, field_10037.materialColor).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10231 = register(
		"birch_sign", new StandingSignBlock(Block.Settings.of(Material.WOOD, MaterialColor.SAND).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10284 = register(
		"acacia_sign", new StandingSignBlock(Block.Settings.of(Material.WOOD, MaterialColor.ORANGE).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10544 = register(
		"jungle_sign", new StandingSignBlock(Block.Settings.of(Material.WOOD, field_10306.materialColor).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10330 = register(
		"dark_oak_sign", new StandingSignBlock(Block.Settings.of(Material.WOOD, field_10010.materialColor).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10149 = register(
		"oak_door", new DoorBlock(Block.Settings.of(Material.WOOD, field_10161.materialColor).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_9983 = register("ladder", new LadderBlock(Block.Settings.of(Material.PART).strength(0.4F).sounds(BlockSoundGroup.LADDER)));
	public static final Block field_10167 = register(
		"rail", new RailBlock(Block.Settings.of(Material.PART).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10596 = register("cobblestone_stairs", new StairsBlock(field_10445.getDefaultState(), Block.Settings.copy(field_10445)));
	public static final Block field_10187 = register(
		"oak_wall_sign", new WallSignBlock(Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10121))
	);
	public static final Block field_10088 = register(
		"spruce_wall_sign", new WallSignBlock(Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10411))
	);
	public static final Block field_10391 = register(
		"birch_wall_sign", new WallSignBlock(Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10231))
	);
	public static final Block field_10401 = register(
		"acacia_wall_sign", new WallSignBlock(Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10284))
	);
	public static final Block field_10587 = register(
		"jungle_wall_sign", new WallSignBlock(Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10544))
	);
	public static final Block field_10265 = register(
		"dark_oak_wall_sign", new WallSignBlock(Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10330))
	);
	public static final Block field_10363 = register(
		"lever", new LeverBlock(Block.Settings.of(Material.PART).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10158 = register(
		"stone_pressure_plate", new PressurePlateBlock(PressurePlateBlock.Type.STONE, Block.Settings.of(Material.STONE).noCollision().strength(0.5F))
	);
	public static final Block field_9973 = register(
		"iron_door", new DoorBlock(Block.Settings.of(Material.METAL, MaterialColor.IRON).strength(5.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10484 = register(
		"oak_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.Type.WOOD, Block.Settings.of(Material.WOOD, field_10161.materialColor).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block field_10332 = register(
		"spruce_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.Type.WOOD, Block.Settings.of(Material.WOOD, field_9975.materialColor).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block field_10592 = register(
		"birch_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.Type.WOOD, Block.Settings.of(Material.WOOD, field_10148.materialColor).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block field_10026 = register(
		"jungle_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.Type.WOOD, Block.Settings.of(Material.WOOD, field_10334.materialColor).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block field_10397 = register(
		"acacia_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.Type.WOOD, Block.Settings.of(Material.WOOD, field_10218.materialColor).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block field_10470 = register(
		"dark_oak_pressure_plate",
		new PressurePlateBlock(
			PressurePlateBlock.Type.WOOD, Block.Settings.of(Material.WOOD, field_10075.materialColor).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block field_10080 = register(
		"redstone_ore", new RedstoneOreBlock(Block.Settings.of(Material.STONE).ticksRandomly().lightLevel(9).strength(3.0F, 3.0F))
	);
	public static final Block field_10523 = register(
		"redstone_torch", new RedstoneTorchBlock(Block.Settings.of(Material.PART).noCollision().breakInstantly().lightLevel(7).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10301 = register(
		"redstone_wall_torch",
		new RedstoneTorchWallBlock(Block.Settings.of(Material.PART).noCollision().breakInstantly().lightLevel(7).sounds(BlockSoundGroup.WOOD).dropsLike(field_10523))
	);
	public static final Block field_10494 = register("stone_button", new StoneButtonBlock(Block.Settings.of(Material.PART).noCollision().strength(0.5F)));
	public static final Block field_10477 = register(
		"snow", new SnowBlock(Block.Settings.of(Material.SNOW).ticksRandomly().strength(0.1F).sounds(BlockSoundGroup.SNOW))
	);
	public static final Block field_10295 = register(
		"ice", new IceBlock(Block.Settings.of(Material.ICE).friction(0.98F).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10491 = register("snow_block", new Block(Block.Settings.of(Material.SNOW_BLOCK).strength(0.2F).sounds(BlockSoundGroup.SNOW)));
	public static final Block field_10029 = register(
		"cactus", new CactusBlock(Block.Settings.of(Material.CACTUS).ticksRandomly().strength(0.4F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10460 = register("clay", new Block(Block.Settings.of(Material.CLAY).strength(0.6F).sounds(BlockSoundGroup.GRAVEL)));
	public static final Block field_10424 = register(
		"sugar_cane", new SugarCaneBlock(Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10223 = register("jukebox", new JukeboxBlock(Block.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F, 6.0F)));
	public static final Block field_10620 = register(
		"oak_fence", new FenceBlock(Block.Settings.of(Material.WOOD, field_10161.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10261 = register(
		"pumpkin", new PumpkinBlock(Block.Settings.of(Material.PUMPKIN, MaterialColor.ORANGE).strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10515 = register("netherrack", new Block(Block.Settings.of(Material.STONE, MaterialColor.NETHER).strength(0.4F)));
	public static final Block field_10114 = register(
		"soul_sand", new SoulSandBlock(Block.Settings.of(Material.SAND, MaterialColor.BROWN).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10171 = register(
		"glowstone", new Block(Block.Settings.of(Material.GLASS, MaterialColor.SAND).strength(0.3F).sounds(BlockSoundGroup.GLASS).lightLevel(15))
	);
	public static final Block field_10316 = register(
		"nether_portal",
		new PortalBlock(Block.Settings.of(Material.PORTAL).noCollision().ticksRandomly().strength(-1.0F).sounds(BlockSoundGroup.GLASS).lightLevel(11).dropsNothing())
	);
	public static final Block field_10147 = register(
		"carved_pumpkin", new CarvedPumpkinBlock(Block.Settings.of(Material.PUMPKIN, MaterialColor.ORANGE).strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10009 = register(
		"jack_o_lantern",
		new CarvedPumpkinBlock(Block.Settings.of(Material.PUMPKIN, MaterialColor.ORANGE).strength(1.0F).sounds(BlockSoundGroup.WOOD).lightLevel(15))
	);
	public static final Block field_10183 = register("cake", new CakeBlock(Block.Settings.of(Material.CAKE).strength(0.5F).sounds(BlockSoundGroup.WOOL)));
	public static final Block field_10450 = register("repeater", new RepeaterBlock(Block.Settings.of(Material.PART).breakInstantly().sounds(BlockSoundGroup.WOOD)));
	public static final Block field_10087 = register(
		"white_stained_glass", new StainedGlassBlock(DyeColor.WHITE, Block.Settings.of(Material.GLASS, DyeColor.WHITE).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10227 = register(
		"orange_stained_glass",
		new StainedGlassBlock(DyeColor.ORANGE, Block.Settings.of(Material.GLASS, DyeColor.ORANGE).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10574 = register(
		"magenta_stained_glass",
		new StainedGlassBlock(DyeColor.MAGENTA, Block.Settings.of(Material.GLASS, DyeColor.MAGENTA).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10271 = register(
		"light_blue_stained_glass",
		new StainedGlassBlock(DyeColor.LIGHT_BLUE, Block.Settings.of(Material.GLASS, DyeColor.LIGHT_BLUE).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10049 = register(
		"yellow_stained_glass",
		new StainedGlassBlock(DyeColor.YELLOW, Block.Settings.of(Material.GLASS, DyeColor.YELLOW).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10157 = register(
		"lime_stained_glass", new StainedGlassBlock(DyeColor.LIME, Block.Settings.of(Material.GLASS, DyeColor.LIME).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10317 = register(
		"pink_stained_glass", new StainedGlassBlock(DyeColor.PINK, Block.Settings.of(Material.GLASS, DyeColor.PINK).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10555 = register(
		"gray_stained_glass", new StainedGlassBlock(DyeColor.GRAY, Block.Settings.of(Material.GLASS, DyeColor.GRAY).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_9996 = register(
		"light_gray_stained_glass",
		new StainedGlassBlock(DyeColor.LIGHT_GRAY, Block.Settings.of(Material.GLASS, DyeColor.LIGHT_GRAY).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10248 = register(
		"cyan_stained_glass", new StainedGlassBlock(DyeColor.CYAN, Block.Settings.of(Material.GLASS, DyeColor.CYAN).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10399 = register(
		"purple_stained_glass",
		new StainedGlassBlock(DyeColor.PURPLE, Block.Settings.of(Material.GLASS, DyeColor.PURPLE).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10060 = register(
		"blue_stained_glass", new StainedGlassBlock(DyeColor.BLUE, Block.Settings.of(Material.GLASS, DyeColor.BLUE).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10073 = register(
		"brown_stained_glass", new StainedGlassBlock(DyeColor.BROWN, Block.Settings.of(Material.GLASS, DyeColor.BROWN).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10357 = register(
		"green_stained_glass", new StainedGlassBlock(DyeColor.GREEN, Block.Settings.of(Material.GLASS, DyeColor.GREEN).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10272 = register(
		"red_stained_glass", new StainedGlassBlock(DyeColor.RED, Block.Settings.of(Material.GLASS, DyeColor.RED).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_9997 = register(
		"black_stained_glass", new StainedGlassBlock(DyeColor.BLACK, Block.Settings.of(Material.GLASS, DyeColor.BLACK).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10137 = register(
		"oak_trapdoor", new TrapdoorBlock(Block.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10323 = register(
		"spruce_trapdoor", new TrapdoorBlock(Block.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10486 = register(
		"birch_trapdoor", new TrapdoorBlock(Block.Settings.of(Material.WOOD, MaterialColor.SAND).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10017 = register(
		"jungle_trapdoor", new TrapdoorBlock(Block.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10608 = register(
		"acacia_trapdoor", new TrapdoorBlock(Block.Settings.of(Material.WOOD, MaterialColor.ORANGE).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10246 = register(
		"dark_oak_trapdoor", new TrapdoorBlock(Block.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10056 = register("stone_bricks", new Block(Block.Settings.of(Material.STONE).strength(1.5F, 6.0F)));
	public static final Block field_10065 = register("mossy_stone_bricks", new Block(Block.Settings.of(Material.STONE).strength(1.5F, 6.0F)));
	public static final Block field_10416 = register("cracked_stone_bricks", new Block(Block.Settings.of(Material.STONE).strength(1.5F, 6.0F)));
	public static final Block field_10552 = register("chiseled_stone_bricks", new Block(Block.Settings.of(Material.STONE).strength(1.5F, 6.0F)));
	public static final Block field_10277 = register("infested_stone", new InfestedBlock(field_10340, Block.Settings.of(Material.CLAY).strength(0.0F, 0.75F)));
	public static final Block field_10492 = register(
		"infested_cobblestone", new InfestedBlock(field_10445, Block.Settings.of(Material.CLAY).strength(0.0F, 0.75F))
	);
	public static final Block field_10387 = register(
		"infested_stone_bricks", new InfestedBlock(field_10056, Block.Settings.of(Material.CLAY).strength(0.0F, 0.75F))
	);
	public static final Block field_10480 = register(
		"infested_mossy_stone_bricks", new InfestedBlock(field_10065, Block.Settings.of(Material.CLAY).strength(0.0F, 0.75F))
	);
	public static final Block field_10100 = register(
		"infested_cracked_stone_bricks", new InfestedBlock(field_10416, Block.Settings.of(Material.CLAY).strength(0.0F, 0.75F))
	);
	public static final Block field_10176 = register(
		"infested_chiseled_stone_bricks", new InfestedBlock(field_10552, Block.Settings.of(Material.CLAY).strength(0.0F, 0.75F))
	);
	public static final Block field_10580 = register(
		"brown_mushroom_block", new MushroomBlock(Block.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10240 = register(
		"red_mushroom_block", new MushroomBlock(Block.Settings.of(Material.WOOD, MaterialColor.RED).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10556 = register(
		"mushroom_stem", new MushroomBlock(Block.Settings.of(Material.WOOD, MaterialColor.WEB).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10576 = register(
		"iron_bars", new PaneBlock(Block.Settings.of(Material.METAL, MaterialColor.AIR).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10285 = register("glass_pane", new PaneBlock(Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS)));
	public static final Block field_10545 = register(
		"melon", new MelonBlock(Block.Settings.of(Material.PUMPKIN, MaterialColor.LIME).strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10331 = register(
		"attached_pumpkin_stem",
		new AttachedStemBlock((GourdBlock)field_10261, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10150 = register(
		"attached_melon_stem",
		new AttachedStemBlock((GourdBlock)field_10545, Block.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_9984 = register(
		"pumpkin_stem",
		new StemBlock((GourdBlock)field_10261, Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10168 = register(
		"melon_stem",
		new StemBlock((GourdBlock)field_10545, Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10597 = register(
		"vine", new VineBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).noCollision().ticksRandomly().strength(0.2F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10188 = register(
		"oak_fence_gate", new FenceGateBlock(Block.Settings.of(Material.WOOD, field_10161.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10089 = register("brick_stairs", new StairsBlock(field_10104.getDefaultState(), Block.Settings.copy(field_10104)));
	public static final Block field_10392 = register("stone_brick_stairs", new StairsBlock(field_10056.getDefaultState(), Block.Settings.copy(field_10056)));
	public static final Block field_10402 = register(
		"mycelium", new MyceliumBlock(Block.Settings.of(Material.ORGANIC, MaterialColor.PURPLE).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10588 = register(
		"lily_pad", new WaterlilyBlock(Block.Settings.of(Material.PLANT).breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10266 = register("nether_bricks", new Block(Block.Settings.of(Material.STONE, MaterialColor.NETHER).strength(2.0F, 6.0F)));
	public static final Block field_10364 = register(
		"nether_brick_fence", new FenceBlock(Block.Settings.of(Material.STONE, MaterialColor.NETHER).strength(2.0F, 6.0F))
	);
	public static final Block field_10159 = register("nether_brick_stairs", new StairsBlock(field_10266.getDefaultState(), Block.Settings.copy(field_10266)));
	public static final Block field_9974 = register(
		"nether_wart", new NetherWartBlock(Block.Settings.of(Material.PLANT, MaterialColor.RED).noCollision().ticksRandomly().sounds(BlockSoundGroup.NETHER_WART))
	);
	public static final Block field_10485 = register(
		"enchanting_table", new EnchantingTableBlock(Block.Settings.of(Material.STONE, MaterialColor.RED).strength(5.0F, 1200.0F))
	);
	public static final Block field_10333 = register("brewing_stand", new BrewingStandBlock(Block.Settings.of(Material.METAL).strength(0.5F).lightLevel(1)));
	public static final Block field_10593 = register("cauldron", new CauldronBlock(Block.Settings.of(Material.METAL, MaterialColor.STONE).strength(2.0F)));
	public static final Block field_10027 = register(
		"end_portal",
		new EndPortalBlock(Block.Settings.of(Material.PORTAL, MaterialColor.BLACK).noCollision().lightLevel(15).strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block field_10398 = register(
		"end_portal_frame",
		new EndPortalFrameBlock(
			Block.Settings.of(Material.STONE, MaterialColor.GREEN).sounds(BlockSoundGroup.GLASS).lightLevel(1).strength(-1.0F, 3600000.0F).dropsNothing()
		)
	);
	public static final Block field_10471 = register("end_stone", new Block(Block.Settings.of(Material.STONE, MaterialColor.SAND).strength(3.0F, 9.0F)));
	public static final Block field_10081 = register(
		"dragon_egg", new DragonEggBlock(Block.Settings.of(Material.DRAGON_EGG, MaterialColor.BLACK).strength(3.0F, 9.0F).lightLevel(1))
	);
	public static final Block field_10524 = register(
		"redstone_lamp", new RedstoneLampBlock(Block.Settings.of(Material.REDSTONE_LAMP).lightLevel(15).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10302 = register(
		"cocoa", new CocoaBlock(Block.Settings.of(Material.PLANT).ticksRandomly().strength(0.2F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10142 = register("sandstone_stairs", new StairsBlock(field_9979.getDefaultState(), Block.Settings.copy(field_9979)));
	public static final Block field_10013 = register("emerald_ore", new OreBlock(Block.Settings.of(Material.STONE).strength(3.0F, 3.0F)));
	public static final Block field_10443 = register("ender_chest", new EnderChestBlock(Block.Settings.of(Material.STONE).strength(22.5F, 600.0F).lightLevel(7)));
	public static final Block field_10348 = register("tripwire_hook", new TripwireHookBlock(Block.Settings.of(Material.PART).noCollision()));
	public static final Block field_10589 = register("tripwire", new TripwireBlock((TripwireHookBlock)field_10348, Block.Settings.of(Material.PART).noCollision()));
	public static final Block field_10234 = register(
		"emerald_block", new Block(Block.Settings.of(Material.METAL, MaterialColor.EMERALD).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10569 = register("spruce_stairs", new StairsBlock(field_9975.getDefaultState(), Block.Settings.copy(field_9975)));
	public static final Block field_10408 = register("birch_stairs", new StairsBlock(field_10148.getDefaultState(), Block.Settings.copy(field_10148)));
	public static final Block field_10122 = register("jungle_stairs", new StairsBlock(field_10334.getDefaultState(), Block.Settings.copy(field_10334)));
	public static final Block field_10525 = register(
		"command_block", new CommandBlock(Block.Settings.of(Material.METAL, MaterialColor.BROWN).strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block field_10327 = register(
		"beacon", new BeaconBlock(Block.Settings.of(Material.GLASS, MaterialColor.DIAMOND).strength(3.0F).lightLevel(15))
	);
	public static final Block field_10625 = register("cobblestone_wall", new WallBlock(Block.Settings.copy(field_10445)));
	public static final Block field_9990 = register("mossy_cobblestone_wall", new WallBlock(Block.Settings.copy(field_10445)));
	public static final Block field_10495 = register("flower_pot", new FlowerPotBlock(field_10124, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10468 = register("potted_oak_sapling", new FlowerPotBlock(field_10394, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10192 = register("potted_spruce_sapling", new FlowerPotBlock(field_10217, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10577 = register("potted_birch_sapling", new FlowerPotBlock(field_10575, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10304 = register("potted_jungle_sapling", new FlowerPotBlock(field_10276, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10564 = register("potted_acacia_sapling", new FlowerPotBlock(field_10385, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10076 = register("potted_dark_oak_sapling", new FlowerPotBlock(field_10160, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10128 = register("potted_fern", new FlowerPotBlock(field_10112, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10354 = register("potted_dandelion", new FlowerPotBlock(field_10182, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10151 = register("potted_poppy", new FlowerPotBlock(field_10449, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_9981 = register("potted_blue_orchid", new FlowerPotBlock(field_10086, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10162 = register("potted_allium", new FlowerPotBlock(field_10226, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10365 = register("potted_azure_bluet", new FlowerPotBlock(field_10573, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10598 = register("potted_red_tulip", new FlowerPotBlock(field_10270, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10249 = register("potted_orange_tulip", new FlowerPotBlock(field_10048, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10400 = register("potted_white_tulip", new FlowerPotBlock(field_10156, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10061 = register("potted_pink_tulip", new FlowerPotBlock(field_10315, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10074 = register("potted_oxeye_daisy", new FlowerPotBlock(field_10554, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10358 = register("potted_cornflower", new FlowerPotBlock(field_9995, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10273 = register(
		"potted_lily_of_the_valley", new FlowerPotBlock(field_10548, Block.Settings.of(Material.PART).breakInstantly())
	);
	public static final Block field_9998 = register("potted_wither_rose", new FlowerPotBlock(field_10606, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10138 = register("potted_red_mushroom", new FlowerPotBlock(field_10559, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10324 = register("potted_brown_mushroom", new FlowerPotBlock(field_10251, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10487 = register("potted_dead_bush", new FlowerPotBlock(field_10428, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10018 = register("potted_cactus", new FlowerPotBlock(field_10029, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10609 = register(
		"carrots", new CarrotsBlock(Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block field_10247 = register(
		"potatoes", new PotatoesBlock(Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block field_10057 = register(
		"oak_button", new WoodButtonBlock(Block.Settings.of(Material.PART).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10066 = register(
		"spruce_button", new WoodButtonBlock(Block.Settings.of(Material.PART).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10417 = register(
		"birch_button", new WoodButtonBlock(Block.Settings.of(Material.PART).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10553 = register(
		"jungle_button", new WoodButtonBlock(Block.Settings.of(Material.PART).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10278 = register(
		"acacia_button", new WoodButtonBlock(Block.Settings.of(Material.PART).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10493 = register(
		"dark_oak_button", new WoodButtonBlock(Block.Settings.of(Material.PART).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10481 = register("skeleton_skull", new SkullBlock(SkullBlock.Type.SKELETON, Block.Settings.of(Material.PART).strength(1.0F)));
	public static final Block field_10388 = register(
		"skeleton_wall_skull", new WallSkullBlock(SkullBlock.Type.SKELETON, Block.Settings.of(Material.PART).strength(1.0F).dropsLike(field_10481))
	);
	public static final Block field_10177 = register("wither_skeleton_skull", new WitherSkullBlock(Block.Settings.of(Material.PART).strength(1.0F)));
	public static final Block field_10101 = register(
		"wither_skeleton_wall_skull", new WallWitherSkullBlock(Block.Settings.of(Material.PART).strength(1.0F).dropsLike(field_10177))
	);
	public static final Block field_10241 = register("zombie_head", new SkullBlock(SkullBlock.Type.ZOMBIE, Block.Settings.of(Material.PART).strength(1.0F)));
	public static final Block field_10581 = register(
		"zombie_wall_head", new WallSkullBlock(SkullBlock.Type.ZOMBIE, Block.Settings.of(Material.PART).strength(1.0F).dropsLike(field_10241))
	);
	public static final Block field_10432 = register("player_head", new PlayerSkullBlock(Block.Settings.of(Material.PART).strength(1.0F)));
	public static final Block field_10208 = register(
		"player_wall_head", new WallPlayerSkullBlock(Block.Settings.of(Material.PART).strength(1.0F).dropsLike(field_10432))
	);
	public static final Block field_10042 = register("creeper_head", new SkullBlock(SkullBlock.Type.CREEPER, Block.Settings.of(Material.PART).strength(1.0F)));
	public static final Block field_10509 = register(
		"creeper_wall_head", new WallSkullBlock(SkullBlock.Type.CREEPER, Block.Settings.of(Material.PART).strength(1.0F).dropsLike(field_10042))
	);
	public static final Block field_10337 = register("dragon_head", new SkullBlock(SkullBlock.Type.DRAGON, Block.Settings.of(Material.PART).strength(1.0F)));
	public static final Block field_10472 = register(
		"dragon_wall_head", new WallSkullBlock(SkullBlock.Type.DRAGON, Block.Settings.of(Material.PART).strength(1.0F).dropsLike(field_10337))
	);
	public static final Block field_10535 = register(
		"anvil", new AnvilBlock(Block.Settings.of(Material.ANVIL, MaterialColor.IRON).strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block field_10105 = register(
		"chipped_anvil", new AnvilBlock(Block.Settings.of(Material.ANVIL, MaterialColor.IRON).strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block field_10414 = register(
		"damaged_anvil", new AnvilBlock(Block.Settings.of(Material.ANVIL, MaterialColor.IRON).strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block field_10380 = register(
		"trapped_chest", new TrappedChestBlock(Block.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10224 = register(
		"light_weighted_pressure_plate",
		new WeightedPressurePlateBlock(15, Block.Settings.of(Material.METAL, MaterialColor.GOLD).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10582 = register(
		"heavy_weighted_pressure_plate",
		new WeightedPressurePlateBlock(150, Block.Settings.of(Material.METAL).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10377 = register(
		"comparator", new ComparatorBlock(Block.Settings.of(Material.PART).breakInstantly().sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10429 = register(
		"daylight_detector", new DaylightDetectorBlock(Block.Settings.of(Material.WOOD).strength(0.2F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10002 = register(
		"redstone_block", new RedstoneBlock(Block.Settings.of(Material.METAL, MaterialColor.LAVA).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10213 = register(
		"nether_quartz_ore", new OreBlock(Block.Settings.of(Material.STONE, MaterialColor.NETHER).strength(3.0F, 3.0F))
	);
	public static final Block field_10312 = register(
		"hopper", new HopperBlock(Block.Settings.of(Material.METAL, MaterialColor.STONE).strength(3.0F, 4.8F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10153 = register("quartz_block", new Block(Block.Settings.of(Material.STONE, MaterialColor.QUARTZ).strength(0.8F)));
	public static final Block field_10044 = register("chiseled_quartz_block", new Block(Block.Settings.of(Material.STONE, MaterialColor.QUARTZ).strength(0.8F)));
	public static final Block field_10437 = register("quartz_pillar", new PillarBlock(Block.Settings.of(Material.STONE, MaterialColor.QUARTZ).strength(0.8F)));
	public static final Block field_10451 = register("quartz_stairs", new StairsBlock(field_10153.getDefaultState(), Block.Settings.copy(field_10153)));
	public static final Block field_10546 = register(
		"activator_rail", new PoweredRailBlock(Block.Settings.of(Material.PART).noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10228 = register("dropper", new DropperBlock(Block.Settings.of(Material.STONE).strength(3.5F)));
	public static final Block field_10611 = register(
		"white_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.WHITE_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10184 = register(
		"orange_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.ORANGE_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10015 = register(
		"magenta_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.MAGENTA_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10325 = register(
		"light_blue_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.LIGHT_BLUE_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10143 = register(
		"yellow_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.YELLOW_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10014 = register(
		"lime_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.LIME_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10444 = register(
		"pink_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.PINK_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10349 = register(
		"gray_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.GRAY_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10590 = register(
		"light_gray_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.LIGHT_GRAY_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10235 = register(
		"cyan_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.CYAN_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10570 = register(
		"purple_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.PURPLE_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10409 = register(
		"blue_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.BLUE_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10123 = register(
		"brown_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.BROWN_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10526 = register(
		"green_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.GREEN_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10328 = register(
		"red_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.RED_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_10626 = register(
		"black_terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.BLACK_TERRACOTTA).strength(1.25F, 4.2F))
	);
	public static final Block field_9991 = register(
		"white_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.WHITE, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10496 = register(
		"orange_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.ORANGE, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10469 = register(
		"magenta_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.MAGENTA, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10193 = register(
		"light_blue_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.LIGHT_BLUE, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10578 = register(
		"yellow_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.YELLOW, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10305 = register(
		"lime_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.LIME, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10565 = register(
		"pink_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.PINK, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10077 = register(
		"gray_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.GRAY, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10129 = register(
		"light_gray_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.LIGHT_GRAY, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10355 = register(
		"cyan_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.CYAN, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10152 = register(
		"purple_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.PURPLE, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_9982 = register(
		"blue_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.BLUE, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10163 = register(
		"brown_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.BROWN, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10419 = register(
		"green_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.GREEN, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10118 = register(
		"red_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.RED, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10070 = register(
		"black_stained_glass_pane", new StainedGlassPaneBlock(DyeColor.BLACK, Block.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10256 = register("acacia_stairs", new StairsBlock(field_10218.getDefaultState(), Block.Settings.copy(field_10218)));
	public static final Block field_10616 = register("dark_oak_stairs", new StairsBlock(field_10075.getDefaultState(), Block.Settings.copy(field_10075)));
	public static final Block field_10030 = register(
		"slime_block", new SlimeBlock(Block.Settings.of(Material.CLAY, MaterialColor.GRASS).friction(0.8F).sounds(BlockSoundGroup.SLIME))
	);
	public static final Block field_10499 = register("barrier", new BarrierBlock(Block.Settings.of(Material.BARRIER).strength(-1.0F, 3600000.8F).dropsNothing()));
	public static final Block field_10453 = register(
		"iron_trapdoor", new TrapdoorBlock(Block.Settings.of(Material.METAL).strength(5.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block field_10135 = register("prismarine", new Block(Block.Settings.of(Material.STONE, MaterialColor.CYAN).strength(1.5F, 6.0F)));
	public static final Block field_10006 = register("prismarine_bricks", new Block(Block.Settings.of(Material.STONE, MaterialColor.DIAMOND).strength(1.5F, 6.0F)));
	public static final Block field_10297 = register("dark_prismarine", new Block(Block.Settings.of(Material.STONE, MaterialColor.DIAMOND).strength(1.5F, 6.0F)));
	public static final Block field_10350 = register("prismarine_stairs", new StairsBlock(field_10135.getDefaultState(), Block.Settings.copy(field_10135)));
	public static final Block field_10190 = register("prismarine_brick_stairs", new StairsBlock(field_10006.getDefaultState(), Block.Settings.copy(field_10006)));
	public static final Block field_10130 = register("dark_prismarine_stairs", new StairsBlock(field_10297.getDefaultState(), Block.Settings.copy(field_10297)));
	public static final Block field_10389 = register("prismarine_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.CYAN).strength(1.5F, 6.0F)));
	public static final Block field_10236 = register(
		"prismarine_brick_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.DIAMOND).strength(1.5F, 6.0F))
	);
	public static final Block field_10623 = register(
		"dark_prismarine_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.DIAMOND).strength(1.5F, 6.0F))
	);
	public static final Block field_10174 = register(
		"sea_lantern", new Block(Block.Settings.of(Material.GLASS, MaterialColor.QUARTZ).strength(0.3F).sounds(BlockSoundGroup.GLASS).lightLevel(15))
	);
	public static final Block field_10359 = register(
		"hay_block", new HayBlock(Block.Settings.of(Material.ORGANIC, MaterialColor.YELLOW).strength(0.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10466 = register(
		"white_carpet", new CarpetBlock(DyeColor.WHITE, Block.Settings.of(Material.CARPET, MaterialColor.WHITE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_9977 = register(
		"orange_carpet", new CarpetBlock(DyeColor.ORANGE, Block.Settings.of(Material.CARPET, MaterialColor.ORANGE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10482 = register(
		"magenta_carpet", new CarpetBlock(DyeColor.MAGENTA, Block.Settings.of(Material.CARPET, MaterialColor.MAGENTA).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10290 = register(
		"light_blue_carpet",
		new CarpetBlock(DyeColor.LIGHT_BLUE, Block.Settings.of(Material.CARPET, MaterialColor.LIGHT_BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10512 = register(
		"yellow_carpet", new CarpetBlock(DyeColor.YELLOW, Block.Settings.of(Material.CARPET, MaterialColor.YELLOW).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10040 = register(
		"lime_carpet", new CarpetBlock(DyeColor.LIME, Block.Settings.of(Material.CARPET, MaterialColor.LIME).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10393 = register(
		"pink_carpet", new CarpetBlock(DyeColor.PINK, Block.Settings.of(Material.CARPET, MaterialColor.PINK).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10591 = register(
		"gray_carpet", new CarpetBlock(DyeColor.GRAY, Block.Settings.of(Material.CARPET, MaterialColor.GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10209 = register(
		"light_gray_carpet",
		new CarpetBlock(DyeColor.LIGHT_GRAY, Block.Settings.of(Material.CARPET, MaterialColor.LIGHT_GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10433 = register(
		"cyan_carpet", new CarpetBlock(DyeColor.CYAN, Block.Settings.of(Material.CARPET, MaterialColor.CYAN).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10510 = register(
		"purple_carpet", new CarpetBlock(DyeColor.PURPLE, Block.Settings.of(Material.CARPET, MaterialColor.PURPLE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10043 = register(
		"blue_carpet", new CarpetBlock(DyeColor.BLUE, Block.Settings.of(Material.CARPET, MaterialColor.BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10473 = register(
		"brown_carpet", new CarpetBlock(DyeColor.BROWN, Block.Settings.of(Material.CARPET, MaterialColor.BROWN).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10338 = register(
		"green_carpet", new CarpetBlock(DyeColor.GREEN, Block.Settings.of(Material.CARPET, MaterialColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10536 = register(
		"red_carpet", new CarpetBlock(DyeColor.RED, Block.Settings.of(Material.CARPET, MaterialColor.RED).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10106 = register(
		"black_carpet", new CarpetBlock(DyeColor.BLACK, Block.Settings.of(Material.CARPET, MaterialColor.BLACK).strength(0.1F).sounds(BlockSoundGroup.WOOL))
	);
	public static final Block field_10415 = register("terracotta", new Block(Block.Settings.of(Material.STONE, MaterialColor.ORANGE).strength(1.25F, 4.2F)));
	public static final Block field_10381 = register("coal_block", new Block(Block.Settings.of(Material.STONE, MaterialColor.BLACK).strength(5.0F, 6.0F)));
	public static final Block field_10225 = register(
		"packed_ice", new Block(Block.Settings.of(Material.PACKED_ICE).friction(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10583 = register(
		"sunflower", new TallFlowerBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10378 = register(
		"lilac", new TallFlowerBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10430 = register(
		"rose_bush", new TallFlowerBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10003 = register(
		"peony", new TallFlowerBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10214 = register(
		"tall_grass", new TallPlantBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10313 = register(
		"large_fern", new TallPlantBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10154 = register(
		"white_banner", new StandingBannerBlock(DyeColor.WHITE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10045 = register(
		"orange_banner", new StandingBannerBlock(DyeColor.ORANGE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10438 = register(
		"magenta_banner", new StandingBannerBlock(DyeColor.MAGENTA, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10452 = register(
		"light_blue_banner", new StandingBannerBlock(DyeColor.LIGHT_BLUE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10547 = register(
		"yellow_banner", new StandingBannerBlock(DyeColor.YELLOW, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10229 = register(
		"lime_banner", new StandingBannerBlock(DyeColor.LIME, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10612 = register(
		"pink_banner", new StandingBannerBlock(DyeColor.PINK, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10185 = register(
		"gray_banner", new StandingBannerBlock(DyeColor.GRAY, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_9985 = register(
		"light_gray_banner", new StandingBannerBlock(DyeColor.LIGHT_GRAY, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10165 = register(
		"cyan_banner", new StandingBannerBlock(DyeColor.CYAN, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10368 = register(
		"purple_banner", new StandingBannerBlock(DyeColor.PURPLE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10281 = register(
		"blue_banner", new StandingBannerBlock(DyeColor.BLUE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10602 = register(
		"brown_banner", new StandingBannerBlock(DyeColor.BROWN, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10198 = register(
		"green_banner", new StandingBannerBlock(DyeColor.GREEN, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10406 = register(
		"red_banner", new StandingBannerBlock(DyeColor.RED, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10062 = register(
		"black_banner", new StandingBannerBlock(DyeColor.BLACK, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10202 = register(
		"white_wall_banner",
		new WallBannerBlock(DyeColor.WHITE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10154))
	);
	public static final Block field_10599 = register(
		"orange_wall_banner",
		new WallBannerBlock(DyeColor.ORANGE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10045))
	);
	public static final Block field_10274 = register(
		"magenta_wall_banner",
		new WallBannerBlock(DyeColor.MAGENTA, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10438))
	);
	public static final Block field_10050 = register(
		"light_blue_wall_banner",
		new WallBannerBlock(DyeColor.LIGHT_BLUE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10452))
	);
	public static final Block field_10139 = register(
		"yellow_wall_banner",
		new WallBannerBlock(DyeColor.YELLOW, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10547))
	);
	public static final Block field_10318 = register(
		"lime_wall_banner",
		new WallBannerBlock(DyeColor.LIME, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10229))
	);
	public static final Block field_10531 = register(
		"pink_wall_banner",
		new WallBannerBlock(DyeColor.PINK, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10612))
	);
	public static final Block field_10267 = register(
		"gray_wall_banner",
		new WallBannerBlock(DyeColor.GRAY, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10185))
	);
	public static final Block field_10604 = register(
		"light_gray_wall_banner",
		new WallBannerBlock(DyeColor.LIGHT_GRAY, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_9985))
	);
	public static final Block field_10372 = register(
		"cyan_wall_banner",
		new WallBannerBlock(DyeColor.CYAN, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10165))
	);
	public static final Block field_10054 = register(
		"purple_wall_banner",
		new WallBannerBlock(DyeColor.PURPLE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10368))
	);
	public static final Block field_10067 = register(
		"blue_wall_banner",
		new WallBannerBlock(DyeColor.BLUE, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10281))
	);
	public static final Block field_10370 = register(
		"brown_wall_banner",
		new WallBannerBlock(DyeColor.BROWN, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10602))
	);
	public static final Block field_10594 = register(
		"green_wall_banner",
		new WallBannerBlock(DyeColor.GREEN, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10198))
	);
	public static final Block field_10279 = register(
		"red_wall_banner",
		new WallBannerBlock(DyeColor.RED, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10406))
	);
	public static final Block field_10537 = register(
		"black_wall_banner",
		new WallBannerBlock(DyeColor.BLACK, Block.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(field_10062))
	);
	public static final Block field_10344 = register("red_sandstone", new Block(Block.Settings.of(Material.STONE, MaterialColor.ORANGE).strength(0.8F)));
	public static final Block field_10117 = register("chiseled_red_sandstone", new Block(Block.Settings.of(Material.STONE, MaterialColor.ORANGE).strength(0.8F)));
	public static final Block field_10518 = register("cut_red_sandstone", new Block(Block.Settings.of(Material.STONE, MaterialColor.ORANGE).strength(0.8F)));
	public static final Block field_10420 = register("red_sandstone_stairs", new StairsBlock(field_10344.getDefaultState(), Block.Settings.copy(field_10344)));
	public static final Block field_10119 = register(
		"oak_slab", new SlabBlock(Block.Settings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10071 = register(
		"spruce_slab", new SlabBlock(Block.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10257 = register(
		"birch_slab", new SlabBlock(Block.Settings.of(Material.WOOD, MaterialColor.SAND).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10617 = register(
		"jungle_slab", new SlabBlock(Block.Settings.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10031 = register(
		"acacia_slab", new SlabBlock(Block.Settings.of(Material.WOOD, MaterialColor.ORANGE).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10500 = register(
		"dark_oak_slab", new SlabBlock(Block.Settings.of(Material.WOOD, MaterialColor.BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10454 = register("stone_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 6.0F)));
	public static final Block field_10136 = register(
		"smooth_stone_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 6.0F))
	);
	public static final Block field_10007 = register("sandstone_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.SAND).strength(2.0F, 6.0F)));
	public static final Block field_10298 = register(
		"petrified_oak_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.WOOD).strength(2.0F, 6.0F))
	);
	public static final Block field_10351 = register(
		"cobblestone_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 6.0F))
	);
	public static final Block field_10191 = register("brick_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.RED).strength(2.0F, 6.0F)));
	public static final Block field_10131 = register(
		"stone_brick_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 6.0F))
	);
	public static final Block field_10390 = register(
		"nether_brick_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.NETHER).strength(2.0F, 6.0F))
	);
	public static final Block field_10237 = register("quartz_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.QUARTZ).strength(2.0F, 6.0F)));
	public static final Block field_10624 = register(
		"red_sandstone_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.ORANGE).strength(2.0F, 6.0F))
	);
	public static final Block field_10175 = register("purpur_slab", new SlabBlock(Block.Settings.of(Material.STONE, MaterialColor.MAGENTA).strength(2.0F, 6.0F)));
	public static final Block field_10360 = register("smooth_stone", new Block(Block.Settings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 6.0F)));
	public static final Block field_10467 = register("smooth_sandstone", new Block(Block.Settings.of(Material.STONE, MaterialColor.SAND).strength(2.0F, 6.0F)));
	public static final Block field_9978 = register("smooth_quartz", new Block(Block.Settings.of(Material.STONE, MaterialColor.QUARTZ).strength(2.0F, 6.0F)));
	public static final Block field_10483 = register(
		"smooth_red_sandstone", new Block(Block.Settings.of(Material.STONE, MaterialColor.ORANGE).strength(2.0F, 6.0F))
	);
	public static final Block field_10291 = register(
		"spruce_fence_gate", new FenceGateBlock(Block.Settings.of(Material.WOOD, field_9975.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10513 = register(
		"birch_fence_gate", new FenceGateBlock(Block.Settings.of(Material.WOOD, field_10148.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10041 = register(
		"jungle_fence_gate", new FenceGateBlock(Block.Settings.of(Material.WOOD, field_10334.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10457 = register(
		"acacia_fence_gate", new FenceGateBlock(Block.Settings.of(Material.WOOD, field_10218.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10196 = register(
		"dark_oak_fence_gate", new FenceGateBlock(Block.Settings.of(Material.WOOD, field_10075.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10020 = register(
		"spruce_fence", new FenceBlock(Block.Settings.of(Material.WOOD, field_9975.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10299 = register(
		"birch_fence", new FenceBlock(Block.Settings.of(Material.WOOD, field_10148.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10319 = register(
		"jungle_fence", new FenceBlock(Block.Settings.of(Material.WOOD, field_10334.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10144 = register(
		"acacia_fence", new FenceBlock(Block.Settings.of(Material.WOOD, field_10218.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10132 = register(
		"dark_oak_fence", new FenceBlock(Block.Settings.of(Material.WOOD, field_10075.materialColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10521 = register(
		"spruce_door", new DoorBlock(Block.Settings.of(Material.WOOD, field_9975.materialColor).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10352 = register(
		"birch_door", new DoorBlock(Block.Settings.of(Material.WOOD, field_10148.materialColor).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10627 = register(
		"jungle_door", new DoorBlock(Block.Settings.of(Material.WOOD, field_10334.materialColor).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10232 = register(
		"acacia_door", new DoorBlock(Block.Settings.of(Material.WOOD, field_10218.materialColor).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10403 = register(
		"dark_oak_door", new DoorBlock(Block.Settings.of(Material.WOOD, field_10075.materialColor).strength(3.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10455 = register(
		"end_rod", new EndRodBlock(Block.Settings.of(Material.PART).breakInstantly().lightLevel(14).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10021 = register(
		"chorus_plant", new ChorusPlantBlock(Block.Settings.of(Material.PLANT, MaterialColor.PURPLE).strength(0.4F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_10528 = register(
		"chorus_flower",
		new ChorusFlowerBlock(
			(ChorusPlantBlock)field_10021, Block.Settings.of(Material.PLANT, MaterialColor.PURPLE).ticksRandomly().strength(0.4F).sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block field_10286 = register("purpur_block", new Block(Block.Settings.of(Material.STONE, MaterialColor.MAGENTA).strength(1.5F, 6.0F)));
	public static final Block field_10505 = register(
		"purpur_pillar", new PillarBlock(Block.Settings.of(Material.STONE, MaterialColor.MAGENTA).strength(1.5F, 6.0F))
	);
	public static final Block field_9992 = register("purpur_stairs", new StairsBlock(field_10286.getDefaultState(), Block.Settings.copy(field_10286)));
	public static final Block field_10462 = register("end_stone_bricks", new Block(Block.Settings.of(Material.STONE, MaterialColor.SAND).strength(0.8F)));
	public static final Block field_10341 = register(
		"beetroots", new BeetrootsBlock(Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP))
	);
	public static final Block field_10194 = register(
		"grass_path", new GrassPathBlock(Block.Settings.of(Material.EARTH).strength(0.65F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10613 = register(
		"end_gateway",
		new EndGatewayBlock(Block.Settings.of(Material.PORTAL, MaterialColor.BLACK).noCollision().lightLevel(15).strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block field_10263 = register(
		"repeating_command_block", new CommandBlock(Block.Settings.of(Material.METAL, MaterialColor.PURPLE).strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block field_10395 = register(
		"chain_command_block", new CommandBlock(Block.Settings.of(Material.METAL, MaterialColor.GREEN).strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block field_10110 = register(
		"frosted_ice", new FrostedIceBlock(Block.Settings.of(Material.ICE).friction(0.98F).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10092 = register(
		"magma_block", new MagmaBlock(Block.Settings.of(Material.STONE, MaterialColor.NETHER).lightLevel(3).ticksRandomly().strength(0.5F))
	);
	public static final Block field_10541 = register(
		"nether_wart_block", new Block(Block.Settings.of(Material.ORGANIC, MaterialColor.RED).strength(1.0F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_9986 = register("red_nether_bricks", new Block(Block.Settings.of(Material.STONE, MaterialColor.NETHER).strength(2.0F, 6.0F)));
	public static final Block field_10166 = register("bone_block", new PillarBlock(Block.Settings.of(Material.STONE, MaterialColor.SAND).strength(2.0F)));
	public static final Block field_10369 = register(
		"structure_void", new StructureVoidBlock(Block.Settings.of(Material.STRUCTURE_VOID).noCollision().dropsNothing())
	);
	public static final Block field_10282 = register("observer", new ObserverBlock(Block.Settings.of(Material.STONE).strength(3.0F)));
	public static final Block field_10603 = register(
		"shulker_box", new ShulkerBoxBlock(null, Block.Settings.of(Material.SHULKER_BOX).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10199 = register(
		"white_shulker_box", new ShulkerBoxBlock(DyeColor.WHITE, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.WHITE).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10407 = register(
		"orange_shulker_box", new ShulkerBoxBlock(DyeColor.ORANGE, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.ORANGE).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10063 = register(
		"magenta_shulker_box",
		new ShulkerBoxBlock(DyeColor.MAGENTA, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.MAGENTA).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10203 = register(
		"light_blue_shulker_box",
		new ShulkerBoxBlock(DyeColor.LIGHT_BLUE, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.LIGHT_BLUE).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10600 = register(
		"yellow_shulker_box", new ShulkerBoxBlock(DyeColor.YELLOW, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.YELLOW).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10275 = register(
		"lime_shulker_box", new ShulkerBoxBlock(DyeColor.LIME, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.LIME).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10051 = register(
		"pink_shulker_box", new ShulkerBoxBlock(DyeColor.PINK, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.PINK).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10140 = register(
		"gray_shulker_box", new ShulkerBoxBlock(DyeColor.GRAY, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.GRAY).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10320 = register(
		"light_gray_shulker_box",
		new ShulkerBoxBlock(DyeColor.LIGHT_GRAY, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.LIGHT_GRAY).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10532 = register(
		"cyan_shulker_box", new ShulkerBoxBlock(DyeColor.CYAN, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.CYAN).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10268 = register(
		"purple_shulker_box",
		new ShulkerBoxBlock(DyeColor.PURPLE, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.PURPLE_TERRACOTTA).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10605 = register(
		"blue_shulker_box", new ShulkerBoxBlock(DyeColor.BLUE, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.BLUE).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10373 = register(
		"brown_shulker_box", new ShulkerBoxBlock(DyeColor.BROWN, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.BROWN).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10055 = register(
		"green_shulker_box", new ShulkerBoxBlock(DyeColor.GREEN, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.GREEN).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10068 = register(
		"red_shulker_box", new ShulkerBoxBlock(DyeColor.RED, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.RED).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10371 = register(
		"black_shulker_box", new ShulkerBoxBlock(DyeColor.BLACK, Block.Settings.of(Material.SHULKER_BOX, MaterialColor.BLACK).strength(2.0F).hasDynamicBounds())
	);
	public static final Block field_10595 = register(
		"white_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.WHITE).strength(1.4F))
	);
	public static final Block field_10280 = register(
		"orange_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.ORANGE).strength(1.4F))
	);
	public static final Block field_10538 = register(
		"magenta_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.MAGENTA).strength(1.4F))
	);
	public static final Block field_10345 = register(
		"light_blue_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.LIGHT_BLUE).strength(1.4F))
	);
	public static final Block field_10096 = register(
		"yellow_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.YELLOW).strength(1.4F))
	);
	public static final Block field_10046 = register(
		"lime_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.LIME).strength(1.4F))
	);
	public static final Block field_10567 = register(
		"pink_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.PINK).strength(1.4F))
	);
	public static final Block field_10220 = register(
		"gray_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.GRAY).strength(1.4F))
	);
	public static final Block field_10052 = register(
		"light_gray_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.LIGHT_GRAY).strength(1.4F))
	);
	public static final Block field_10078 = register(
		"cyan_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.CYAN).strength(1.4F))
	);
	public static final Block field_10426 = register(
		"purple_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.PURPLE).strength(1.4F))
	);
	public static final Block field_10550 = register(
		"blue_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.BLUE).strength(1.4F))
	);
	public static final Block field_10004 = register(
		"brown_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.BROWN).strength(1.4F))
	);
	public static final Block field_10475 = register(
		"green_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.GREEN).strength(1.4F))
	);
	public static final Block field_10383 = register(
		"red_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.RED).strength(1.4F))
	);
	public static final Block field_10501 = register(
		"black_glazed_terracotta", new GlazedTerracottaBlock(Block.Settings.of(Material.STONE, DyeColor.BLACK).strength(1.4F))
	);
	public static final Block field_10107 = register("white_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.WHITE).strength(1.8F)));
	public static final Block field_10210 = register("orange_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.ORANGE).strength(1.8F)));
	public static final Block field_10585 = register("magenta_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.MAGENTA).strength(1.8F)));
	public static final Block field_10242 = register("light_blue_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.LIGHT_BLUE).strength(1.8F)));
	public static final Block field_10542 = register("yellow_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.YELLOW).strength(1.8F)));
	public static final Block field_10421 = register("lime_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.LIME).strength(1.8F)));
	public static final Block field_10434 = register("pink_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.PINK).strength(1.8F)));
	public static final Block field_10038 = register("gray_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.GRAY).strength(1.8F)));
	public static final Block field_10172 = register("light_gray_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.LIGHT_GRAY).strength(1.8F)));
	public static final Block field_10308 = register("cyan_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.CYAN).strength(1.8F)));
	public static final Block field_10206 = register("purple_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.PURPLE).strength(1.8F)));
	public static final Block field_10011 = register("blue_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.BLUE).strength(1.8F)));
	public static final Block field_10439 = register("brown_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.BROWN).strength(1.8F)));
	public static final Block field_10367 = register("green_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.GREEN).strength(1.8F)));
	public static final Block field_10058 = register("red_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.RED).strength(1.8F)));
	public static final Block field_10458 = register("black_concrete", new Block(Block.Settings.of(Material.STONE, DyeColor.BLACK).strength(1.8F)));
	public static final Block field_10197 = register(
		"white_concrete_powder", new ConcretePowderBlock(field_10107, Block.Settings.of(Material.SAND, DyeColor.WHITE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10022 = register(
		"orange_concrete_powder", new ConcretePowderBlock(field_10210, Block.Settings.of(Material.SAND, DyeColor.ORANGE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10300 = register(
		"magenta_concrete_powder",
		new ConcretePowderBlock(field_10585, Block.Settings.of(Material.SAND, DyeColor.MAGENTA).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10321 = register(
		"light_blue_concrete_powder",
		new ConcretePowderBlock(field_10242, Block.Settings.of(Material.SAND, DyeColor.LIGHT_BLUE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10145 = register(
		"yellow_concrete_powder", new ConcretePowderBlock(field_10542, Block.Settings.of(Material.SAND, DyeColor.YELLOW).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10133 = register(
		"lime_concrete_powder", new ConcretePowderBlock(field_10421, Block.Settings.of(Material.SAND, DyeColor.LIME).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10522 = register(
		"pink_concrete_powder", new ConcretePowderBlock(field_10434, Block.Settings.of(Material.SAND, DyeColor.PINK).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10353 = register(
		"gray_concrete_powder", new ConcretePowderBlock(field_10038, Block.Settings.of(Material.SAND, DyeColor.GRAY).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10628 = register(
		"light_gray_concrete_powder",
		new ConcretePowderBlock(field_10172, Block.Settings.of(Material.SAND, DyeColor.LIGHT_GRAY).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10233 = register(
		"cyan_concrete_powder", new ConcretePowderBlock(field_10308, Block.Settings.of(Material.SAND, DyeColor.CYAN).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10404 = register(
		"purple_concrete_powder", new ConcretePowderBlock(field_10206, Block.Settings.of(Material.SAND, DyeColor.PURPLE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10456 = register(
		"blue_concrete_powder", new ConcretePowderBlock(field_10011, Block.Settings.of(Material.SAND, DyeColor.BLUE).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10023 = register(
		"brown_concrete_powder", new ConcretePowderBlock(field_10439, Block.Settings.of(Material.SAND, DyeColor.BROWN).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10529 = register(
		"green_concrete_powder", new ConcretePowderBlock(field_10367, Block.Settings.of(Material.SAND, DyeColor.GREEN).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10287 = register(
		"red_concrete_powder", new ConcretePowderBlock(field_10058, Block.Settings.of(Material.SAND, DyeColor.RED).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_10506 = register(
		"black_concrete_powder", new ConcretePowderBlock(field_10458, Block.Settings.of(Material.SAND, DyeColor.BLACK).strength(0.5F).sounds(BlockSoundGroup.SAND))
	);
	public static final Block field_9993 = register(
		"kelp", new KelpBlock(Block.Settings.of(Material.UNDERWATER_PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block field_10463 = register(
		"kelp_plant",
		new KelpPlantBlock((KelpBlock)field_9993, Block.Settings.of(Material.UNDERWATER_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block field_10342 = register(
		"dried_kelp_block", new Block(Block.Settings.of(Material.ORGANIC, MaterialColor.GREEN).strength(0.5F, 2.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block field_10195 = register(
		"turtle_egg", new TurtleEggBlock(Block.Settings.of(Material.DRAGON_EGG, MaterialColor.SAND).strength(0.5F).sounds(BlockSoundGroup.METAL).ticksRandomly())
	);
	public static final Block field_10614 = register(
		"dead_tube_coral_block", new Block(Block.Settings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F))
	);
	public static final Block field_10264 = register(
		"dead_brain_coral_block", new Block(Block.Settings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F))
	);
	public static final Block field_10396 = register(
		"dead_bubble_coral_block", new Block(Block.Settings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F))
	);
	public static final Block field_10111 = register(
		"dead_fire_coral_block", new Block(Block.Settings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F))
	);
	public static final Block field_10488 = register(
		"dead_horn_coral_block", new Block(Block.Settings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F))
	);
	public static final Block field_10309 = register(
		"tube_coral_block",
		new CoralBlockBlock(field_10614, Block.Settings.of(Material.STONE, MaterialColor.BLUE).strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL))
	);
	public static final Block field_10629 = register(
		"brain_coral_block",
		new CoralBlockBlock(field_10264, Block.Settings.of(Material.STONE, MaterialColor.PINK).strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL))
	);
	public static final Block field_10000 = register(
		"bubble_coral_block",
		new CoralBlockBlock(field_10396, Block.Settings.of(Material.STONE, MaterialColor.PURPLE).strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL))
	);
	public static final Block field_10516 = register(
		"fire_coral_block", new CoralBlockBlock(field_10111, Block.Settings.of(Material.STONE, MaterialColor.RED).strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL))
	);
	public static final Block field_10464 = register(
		"horn_coral_block",
		new CoralBlockBlock(field_10488, Block.Settings.of(Material.STONE, MaterialColor.YELLOW).strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL))
	);
	public static final Block field_10082 = register(
		"dead_tube_coral", new DeadCoralBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10572 = register(
		"dead_brain_coral", new DeadCoralBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10296 = register(
		"dead_bubble_coral", new DeadCoralBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10579 = register(
		"dead_fire_coral", new DeadCoralBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10032 = register(
		"dead_horn_coral", new DeadCoralBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10125 = register(
		"tube_coral",
		new CoralBlock(field_10082, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.BLUE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block field_10339 = register(
		"brain_coral",
		new CoralBlock(field_10572, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PINK).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block field_10134 = register(
		"bubble_coral",
		new CoralBlock(
			field_10296, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PURPLE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block field_10618 = register(
		"fire_coral",
		new CoralBlock(field_10579, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.RED).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))
	);
	public static final Block field_10169 = register(
		"horn_coral",
		new CoralBlock(
			field_10032, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.YELLOW).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block field_10448 = register(
		"dead_tube_coral_fan", new DeadCoralFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10097 = register(
		"dead_brain_coral_fan", new DeadCoralFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10047 = register(
		"dead_bubble_coral_fan", new DeadCoralFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10568 = register(
		"dead_fire_coral_fan", new DeadCoralFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10221 = register(
		"dead_horn_coral_fan", new DeadCoralFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly())
	);
	public static final Block field_10053 = register(
		"tube_coral_fan",
		new CoralTubeFanBlock(
			field_10448, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.BLUE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block field_10079 = register(
		"brain_coral_fan",
		new CoralTubeFanBlock(
			field_10097, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PINK).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block field_10427 = register(
		"bubble_coral_fan",
		new CoralTubeFanBlock(
			field_10047, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PURPLE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block field_10551 = register(
		"fire_coral_fan",
		new CoralTubeFanBlock(
			field_10568, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.RED).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block field_10005 = register(
		"horn_coral_fan",
		new CoralTubeFanBlock(
			field_10221, Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.YELLOW).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS)
		)
	);
	public static final Block field_10347 = register(
		"dead_tube_coral_wall_fan",
		new DeadCoralWallFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly().dropsLike(field_10448))
	);
	public static final Block field_10116 = register(
		"dead_brain_coral_wall_fan",
		new DeadCoralWallFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly().dropsLike(field_10097))
	);
	public static final Block field_10094 = register(
		"dead_bubble_coral_wall_fan",
		new DeadCoralWallFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly().dropsLike(field_10047))
	);
	public static final Block field_10557 = register(
		"dead_fire_coral_wall_fan",
		new DeadCoralWallFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly().dropsLike(field_10568))
	);
	public static final Block field_10239 = register(
		"dead_horn_coral_wall_fan",
		new DeadCoralWallFanBlock(Block.Settings.of(Material.STONE, MaterialColor.GRAY).noCollision().breakInstantly().dropsLike(field_10221))
	);
	public static final Block field_10584 = register(
		"tube_coral_wall_fan",
		new CoralWallFanBlock(
			field_10347,
			Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.BLUE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).dropsLike(field_10053)
		)
	);
	public static final Block field_10186 = register(
		"brain_coral_wall_fan",
		new CoralWallFanBlock(
			field_10116,
			Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PINK).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).dropsLike(field_10079)
		)
	);
	public static final Block field_10447 = register(
		"bubble_coral_wall_fan",
		new CoralWallFanBlock(
			field_10094,
			Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.PURPLE).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).dropsLike(field_10427)
		)
	);
	public static final Block field_10498 = register(
		"fire_coral_wall_fan",
		new CoralWallFanBlock(
			field_10557,
			Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.RED).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).dropsLike(field_10551)
		)
	);
	public static final Block field_9976 = register(
		"horn_coral_wall_fan",
		new CoralWallFanBlock(
			field_10239,
			Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.YELLOW).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).dropsLike(field_10005)
		)
	);
	public static final Block field_10476 = register(
		"sea_pickle", new SeaPickleBlock(Block.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.GREEN).lightLevel(3).sounds(BlockSoundGroup.SLIME))
	);
	public static final Block field_10384 = register(
		"blue_ice", new TransparentBlock(Block.Settings.of(Material.PACKED_ICE).strength(2.8F).friction(0.989F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block field_10502 = register(
		"conduit", new ConduitBlock(Block.Settings.of(Material.GLASS, MaterialColor.DIAMOND).strength(3.0F).lightLevel(15))
	);
	public static final Block field_10108 = register(
		"bamboo_sapling",
		new BambooSaplingBlock(
			Block.Settings.of(Material.BAMBOO_SAPLING).ticksRandomly().breakInstantly().noCollision().strength(1.0F).sounds(BlockSoundGroup.BAMBOO_SAPLING)
		)
	);
	public static final Block field_10211 = register(
		"bamboo",
		new BambooBlock(Block.Settings.of(Material.BAMBOO, MaterialColor.FOLIAGE).ticksRandomly().breakInstantly().strength(1.0F).sounds(BlockSoundGroup.BAMBOO))
	);
	public static final Block field_10586 = register("potted_bamboo", new FlowerPotBlock(field_10211, Block.Settings.of(Material.PART).breakInstantly()));
	public static final Block field_10243 = register("void_air", new AirBlock(Block.Settings.of(Material.AIR).noCollision().dropsNothing()));
	public static final Block field_10543 = register("cave_air", new AirBlock(Block.Settings.of(Material.AIR).noCollision().dropsNothing()));
	public static final Block field_10422 = register(
		"bubble_column", new BubbleColumnBlock(Block.Settings.of(Material.BUBBLE_COLUMN).noCollision().dropsNothing())
	);
	public static final Block field_10435 = register("polished_granite_stairs", new StairsBlock(field_10289.getDefaultState(), Block.Settings.copy(field_10289)));
	public static final Block field_10039 = register(
		"smooth_red_sandstone_stairs", new StairsBlock(field_10483.getDefaultState(), Block.Settings.copy(field_10483))
	);
	public static final Block field_10173 = register("mossy_stone_brick_stairs", new StairsBlock(field_10065.getDefaultState(), Block.Settings.copy(field_10065)));
	public static final Block field_10310 = register("polished_diorite_stairs", new StairsBlock(field_10346.getDefaultState(), Block.Settings.copy(field_10346)));
	public static final Block field_10207 = register("mossy_cobblestone_stairs", new StairsBlock(field_9989.getDefaultState(), Block.Settings.copy(field_9989)));
	public static final Block field_10012 = register("end_stone_brick_stairs", new StairsBlock(field_10462.getDefaultState(), Block.Settings.copy(field_10462)));
	public static final Block field_10440 = register("stone_stairs", new StairsBlock(field_10340.getDefaultState(), Block.Settings.copy(field_10340)));
	public static final Block field_10549 = register("smooth_sandstone_stairs", new StairsBlock(field_10467.getDefaultState(), Block.Settings.copy(field_10467)));
	public static final Block field_10245 = register("smooth_quartz_stairs", new StairsBlock(field_9978.getDefaultState(), Block.Settings.copy(field_9978)));
	public static final Block field_10607 = register("granite_stairs", new StairsBlock(field_10474.getDefaultState(), Block.Settings.copy(field_10474)));
	public static final Block field_10386 = register("andesite_stairs", new StairsBlock(field_10115.getDefaultState(), Block.Settings.copy(field_10115)));
	public static final Block field_10497 = register("red_nether_brick_stairs", new StairsBlock(field_9986.getDefaultState(), Block.Settings.copy(field_9986)));
	public static final Block field_9994 = register("polished_andesite_stairs", new StairsBlock(field_10093.getDefaultState(), Block.Settings.copy(field_10093)));
	public static final Block field_10216 = register("diorite_stairs", new StairsBlock(field_10508.getDefaultState(), Block.Settings.copy(field_10508)));
	public static final Block field_10329 = register("polished_granite_slab", new SlabBlock(Block.Settings.copy(field_10289)));
	public static final Block field_10283 = register("smooth_red_sandstone_slab", new SlabBlock(Block.Settings.copy(field_10483)));
	public static final Block field_10024 = register("mossy_stone_brick_slab", new SlabBlock(Block.Settings.copy(field_10065)));
	public static final Block field_10412 = register("polished_diorite_slab", new SlabBlock(Block.Settings.copy(field_10346)));
	public static final Block field_10405 = register("mossy_cobblestone_slab", new SlabBlock(Block.Settings.copy(field_9989)));
	public static final Block field_10064 = register("end_stone_brick_slab", new SlabBlock(Block.Settings.copy(field_10462)));
	public static final Block field_10262 = register("smooth_sandstone_slab", new SlabBlock(Block.Settings.copy(field_10467)));
	public static final Block field_10601 = register("smooth_quartz_slab", new SlabBlock(Block.Settings.copy(field_9978)));
	public static final Block field_10189 = register("granite_slab", new SlabBlock(Block.Settings.copy(field_10474)));
	public static final Block field_10016 = register("andesite_slab", new SlabBlock(Block.Settings.copy(field_10115)));
	public static final Block field_10478 = register("red_nether_brick_slab", new SlabBlock(Block.Settings.copy(field_9986)));
	public static final Block field_10322 = register("polished_andesite_slab", new SlabBlock(Block.Settings.copy(field_10093)));
	public static final Block field_10507 = register("diorite_slab", new SlabBlock(Block.Settings.copy(field_10508)));
	public static final Block field_10269 = register("brick_wall", new WallBlock(Block.Settings.copy(field_10104)));
	public static final Block field_10530 = register("prismarine_wall", new WallBlock(Block.Settings.copy(field_10135)));
	public static final Block field_10413 = register("red_sandstone_wall", new WallBlock(Block.Settings.copy(field_10344)));
	public static final Block field_10059 = register("mossy_stone_brick_wall", new WallBlock(Block.Settings.copy(field_10065)));
	public static final Block field_10072 = register("granite_wall", new WallBlock(Block.Settings.copy(field_10474)));
	public static final Block field_10252 = register("stone_brick_wall", new WallBlock(Block.Settings.copy(field_10056)));
	public static final Block field_10127 = register("nether_brick_wall", new WallBlock(Block.Settings.copy(field_10266)));
	public static final Block field_10489 = register("andesite_wall", new WallBlock(Block.Settings.copy(field_10115)));
	public static final Block field_10311 = register("red_nether_brick_wall", new WallBlock(Block.Settings.copy(field_9986)));
	public static final Block field_10630 = register("sandstone_wall", new WallBlock(Block.Settings.copy(field_9979)));
	public static final Block field_10001 = register("end_stone_brick_wall", new WallBlock(Block.Settings.copy(field_10462)));
	public static final Block field_10517 = register("diorite_wall", new WallBlock(Block.Settings.copy(field_10508)));
	public static final Block field_16492 = register(
		"scaffolding", new ScaffoldingBlock(Block.Settings.of(Material.PART).noCollision().sounds(BlockSoundGroup.SCAFFOLDING))
	);
	public static final Block field_10083 = register("loom", new LoomBlock(Block.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block field_16328 = register("barrel", new BarrelBlock(Block.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block field_16334 = register("smoker", new SmokerBlock(Block.Settings.of(Material.STONE).strength(3.5F).lightLevel(13)));
	public static final Block field_16333 = register("blast_furnace", new BlastFurnaceBlock(Block.Settings.of(Material.STONE).strength(3.5F).lightLevel(13)));
	public static final Block field_16336 = register(
		"cartography_table", new CartographyTableBlock(Block.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_16331 = register(
		"fletching_table", new FletchingTableBlock(Block.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_16337 = register(
		"grindstone", new GrindstoneBlock(Block.Settings.of(Material.ANVIL, MaterialColor.IRON).strength(2.0F, 6.0F).sounds(BlockSoundGroup.STONE))
	);
	public static final Block field_16330 = register("lectern", new LecternBlock(Block.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)));
	public static final Block field_16329 = register(
		"smithing_table", new SmithingTableBlock(Block.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD))
	);
	public static final Block field_16335 = register("stonecutter", new StonecutterBlock(Block.Settings.of(Material.STONE).strength(3.5F)));
	public static final Block field_16332 = register(
		"bell", new BellBlock(Block.Settings.of(Material.METAL, MaterialColor.GOLD).strength(5.0F).sounds(BlockSoundGroup.ANVIL))
	);
	public static final Block field_16541 = register(
		"lantern", new LanternBlock(Block.Settings.of(Material.METAL).strength(3.5F).sounds(BlockSoundGroup.METAL).lightLevel(15))
	);
	public static final Block field_17350 = register(
		"campfire",
		new CampfireBlock(Block.Settings.of(Material.WOOD, MaterialColor.SPRUCE).strength(2.0F).sounds(BlockSoundGroup.WOOD).lightLevel(15).ticksRandomly())
	);
	public static final Block field_16999 = register(
		"sweet_berry_bush", new SweetBerryBushBlock(Block.Settings.of(Material.PLANT).ticksRandomly().noCollision().sounds(BlockSoundGroup.SWEET_BERRY_BUSH))
	);
	public static final Block field_10465 = register(
		"structure_block", new StructureBlock(Block.Settings.of(Material.METAL, MaterialColor.LIGHT_GRAY).strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block field_16540 = register(
		"jigsaw", new JigsawBlock(Block.Settings.of(Material.METAL, MaterialColor.LIGHT_GRAY).strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block field_17563 = register("composter", new ComposterBlock(Block.Settings.of(Material.WOOD).strength(0.6F).sounds(BlockSoundGroup.WOOD)));

	private static Block register(String string, Block block) {
		return Registry.register(Registry.BLOCK, string, block);
	}

	static {
		for (Block block : Registry.BLOCK) {
			for (BlockState blockState : block.getStateFactory().getStates()) {
				blockState.initShapeCache();
				Block.STATE_IDS.add(blockState);
			}

			block.getDropTableId();
		}
	}
}
