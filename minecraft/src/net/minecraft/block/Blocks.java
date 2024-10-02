package net.minecraft.block;

import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.enums.VaultState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemKeys;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ColorCode;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;

/**
 * Contains all the minecraft blocks.
 */
public class Blocks {
	private static final AbstractBlock.ContextPredicate SHULKER_BOX_SUFFOCATES_PREDICATE = (state, world, pos) -> world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity
			? shulkerBoxBlockEntity.suffocates()
			: true;
	private static final AbstractBlock.ContextPredicate PISTON_SUFFOCATES_PREDICATE = (state, world, pos) -> !(Boolean)state.get(PistonBlock.EXTENDED);
	public static final Block AIR = register("air", AirBlock::new, AbstractBlock.Settings.create().replaceable().noCollision().dropsNothing().air());
	public static final Block STONE = register(
		"stone", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block GRANITE = register(
		"granite", AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block POLISHED_GRANITE = register(
		"polished_granite",
		AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block DIORITE = register(
		"diorite", AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block POLISHED_DIORITE = register(
		"polished_diorite", AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block ANDESITE = register(
		"andesite", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block POLISHED_ANDESITE = register(
		"polished_andesite",
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block GRASS_BLOCK = register(
		"grass_block", GrassBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.PALE_GREEN).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS)
	);
	public static final Block DIRT = register("dirt", AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).strength(0.5F).sounds(BlockSoundGroup.GRAVEL));
	public static final Block COARSE_DIRT = register(
		"coarse_dirt", AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).strength(0.5F).sounds(BlockSoundGroup.GRAVEL)
	);
	public static final Block PODZOL = register(
		"podzol", SnowyBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).strength(0.5F).sounds(BlockSoundGroup.GRAVEL)
	);
	public static final Block COBBLESTONE = register(
		"cobblestone", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block OAK_PLANKS = register(
		"oak_planks",
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block SPRUCE_PLANKS = register(
		"spruce_planks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.SPRUCE_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block BIRCH_PLANKS = register(
		"birch_planks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_YELLOW)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block JUNGLE_PLANKS = register(
		"jungle_planks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DIRT_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block ACACIA_PLANKS = register(
		"acacia_planks",
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block CHERRY_PLANKS = register(
		"cherry_planks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_WHITE)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.CHERRY_WOOD)
			.burnable()
	);
	public static final Block DARK_OAK_PLANKS = register(
		"dark_oak_planks",
		AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block PALE_OAK_WOOD = register(
		"pale_oak_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block PALE_OAK_PLANKS = register(
		"pale_oak_planks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OFF_WHITE)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_PLANKS = register(
		"mangrove_planks",
		AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block BAMBOO_PLANKS = register(
		"bamboo_planks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.YELLOW)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.BAMBOO_WOOD)
			.burnable()
	);
	public static final Block BAMBOO_MOSAIC = register(
		"bamboo_mosaic",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.YELLOW)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.BAMBOO_WOOD)
			.burnable()
	);
	public static final Block OAK_SAPLING = register(
		"oak_sapling",
		settings -> new SaplingBlock(SaplingGenerator.OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SPRUCE_SAPLING = register(
		"spruce_sapling",
		settings -> new SaplingBlock(SaplingGenerator.SPRUCE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BIRCH_SAPLING = register(
		"birch_sapling",
		settings -> new SaplingBlock(SaplingGenerator.BIRCH, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block JUNGLE_SAPLING = register(
		"jungle_sapling",
		settings -> new SaplingBlock(SaplingGenerator.JUNGLE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ACACIA_SAPLING = register(
		"acacia_sapling",
		settings -> new SaplingBlock(SaplingGenerator.ACACIA, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CHERRY_SAPLING = register(
		"cherry_sapling",
		settings -> new SaplingBlock(SaplingGenerator.CHERRY, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PINK)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.CHERRY_SAPLING)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DARK_OAK_SAPLING = register(
		"dark_oak_sapling",
		settings -> new SaplingBlock(SaplingGenerator.DARK_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PALE_OAK_SAPLING = register(
		"pale_oak_sapling",
		settings -> new SaplingBlock(SaplingGenerator.PALE_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_PROPAGULE = register(
		"mangrove_propagule",
		settings -> new PropaguleBlock(SaplingGenerator.MANGROVE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BEDROCK = register(
		"bedrock",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.strength(-1.0F, 3600000.0F)
			.dropsNothing()
			.allowsSpawning(Blocks::never)
	);
	public static final Block WATER = register(
		"water",
		settings -> new FluidBlock(Fluids.WATER, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WATER_BLUE)
			.replaceable()
			.noCollision()
			.strength(100.0F)
			.pistonBehavior(PistonBehavior.DESTROY)
			.dropsNothing()
			.liquid()
			.sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
	);
	public static final Block LAVA = register(
		"lava",
		settings -> new FluidBlock(Fluids.LAVA, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BRIGHT_RED)
			.replaceable()
			.noCollision()
			.ticksRandomly()
			.strength(100.0F)
			.luminance(state -> 15)
			.pistonBehavior(PistonBehavior.DESTROY)
			.dropsNothing()
			.liquid()
			.sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
	);
	public static final Block SAND = register(
		"sand",
		settings -> new ColoredFallingBlock(new ColorCode(14406560), settings),
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block SUSPICIOUS_SAND = register(
		"suspicious_sand",
		settings -> new BrushableBlock(SAND, SoundEvents.ITEM_BRUSH_BRUSHING_SAND, SoundEvents.ITEM_BRUSH_BRUSHING_SAND, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_YELLOW)
			.instrument(NoteBlockInstrument.SNARE)
			.strength(0.25F)
			.sounds(BlockSoundGroup.SUSPICIOUS_SAND)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block RED_SAND = register(
		"red_sand",
		settings -> new ColoredFallingBlock(new ColorCode(11098145), settings),
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block GRAVEL = register(
		"gravel",
		settings -> new ColoredFallingBlock(new ColorCode(-8356741), settings),
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.SNARE).strength(0.6F).sounds(BlockSoundGroup.GRAVEL)
	);
	public static final Block SUSPICIOUS_GRAVEL = register(
		"suspicious_gravel",
		settings -> new BrushableBlock(GRAVEL, SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL, SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.SNARE)
			.strength(0.25F)
			.sounds(BlockSoundGroup.SUSPICIOUS_GRAVEL)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block GOLD_ORE = register(
		"gold_ore",
		settings -> new ExperienceDroppingBlock(ConstantIntProvider.create(0), settings),
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
	);
	public static final Block DEEPSLATE_GOLD_ORE = register(
		"deepslate_gold_ore",
		settings -> new ExperienceDroppingBlock(ConstantIntProvider.create(0), settings),
		AbstractBlock.Settings.copyShallow(GOLD_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block IRON_ORE = register(
		"iron_ore",
		settings -> new ExperienceDroppingBlock(ConstantIntProvider.create(0), settings),
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
	);
	public static final Block DEEPSLATE_IRON_ORE = register(
		"deepslate_iron_ore",
		settings -> new ExperienceDroppingBlock(ConstantIntProvider.create(0), settings),
		AbstractBlock.Settings.copyShallow(IRON_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block COAL_ORE = register(
		"coal_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(0, 2), settings),
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
	);
	public static final Block DEEPSLATE_COAL_ORE = register(
		"deepslate_coal_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(0, 2), settings),
		AbstractBlock.Settings.copyShallow(COAL_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block NETHER_GOLD_ORE = register(
		"nether_gold_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(0, 1), settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(3.0F, 3.0F)
			.sounds(BlockSoundGroup.NETHER_GOLD_ORE)
	);
	public static final Block OAK_LOG = register("oak_log", PillarBlock::new, createLogSettings(MapColor.OAK_TAN, MapColor.SPRUCE_BROWN, BlockSoundGroup.WOOD));
	public static final Block SPRUCE_LOG = register("spruce_log", PillarBlock::new, createLogSettings(MapColor.SPRUCE_BROWN, MapColor.BROWN, BlockSoundGroup.WOOD));
	public static final Block BIRCH_LOG = register(
		"birch_log", PillarBlock::new, createLogSettings(MapColor.PALE_YELLOW, MapColor.OFF_WHITE, BlockSoundGroup.WOOD)
	);
	public static final Block JUNGLE_LOG = register(
		"jungle_log", PillarBlock::new, createLogSettings(MapColor.DIRT_BROWN, MapColor.SPRUCE_BROWN, BlockSoundGroup.WOOD)
	);
	public static final Block ACACIA_LOG = register("acacia_log", PillarBlock::new, createLogSettings(MapColor.ORANGE, MapColor.STONE_GRAY, BlockSoundGroup.WOOD));
	public static final Block CHERRY_LOG = register(
		"cherry_log", PillarBlock::new, createLogSettings(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_GRAY, BlockSoundGroup.CHERRY_WOOD)
	);
	public static final Block DARK_OAK_LOG = register("dark_oak_log", PillarBlock::new, createLogSettings(MapColor.BROWN, MapColor.BROWN, BlockSoundGroup.WOOD));
	public static final Block PALE_OAK_LOG = register(
		"pale_oak_log",
		PillarBlock::new,
		createLogSettings(PALE_OAK_PLANKS.getDefaultMapColor(), PALE_OAK_WOOD.getDefaultMapColor(), BlockSoundGroup.WOOD).requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_LOG = register(
		"mangrove_log", PillarBlock::new, createLogSettings(MapColor.RED, MapColor.SPRUCE_BROWN, BlockSoundGroup.WOOD)
	);
	public static final Block MANGROVE_ROOTS = register(
		"mangrove_roots",
		MangroveRootsBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.SPRUCE_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(0.7F)
			.sounds(BlockSoundGroup.MANGROVE_ROOTS)
			.nonOpaque()
			.suffocates(Blocks::never)
			.blockVision(Blocks::never)
			.nonOpaque()
			.burnable()
	);
	public static final Block MUDDY_MANGROVE_ROOTS = register(
		"muddy_mangrove_roots",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).strength(0.7F).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS)
	);
	public static final Block BAMBOO_BLOCK = register(
		"bamboo_block", PillarBlock::new, createLogSettings(MapColor.YELLOW, MapColor.DARK_GREEN, BlockSoundGroup.BAMBOO_WOOD)
	);
	public static final Block STRIPPED_SPRUCE_LOG = register(
		"stripped_spruce_log", PillarBlock::new, createLogSettings(MapColor.SPRUCE_BROWN, MapColor.SPRUCE_BROWN, BlockSoundGroup.WOOD)
	);
	public static final Block STRIPPED_BIRCH_LOG = register(
		"stripped_birch_log", PillarBlock::new, createLogSettings(MapColor.PALE_YELLOW, MapColor.PALE_YELLOW, BlockSoundGroup.WOOD)
	);
	public static final Block STRIPPED_JUNGLE_LOG = register(
		"stripped_jungle_log", PillarBlock::new, createLogSettings(MapColor.DIRT_BROWN, MapColor.DIRT_BROWN, BlockSoundGroup.WOOD)
	);
	public static final Block STRIPPED_ACACIA_LOG = register(
		"stripped_acacia_log", PillarBlock::new, createLogSettings(MapColor.ORANGE, MapColor.ORANGE, BlockSoundGroup.WOOD)
	);
	public static final Block STRIPPED_CHERRY_LOG = register(
		"stripped_cherry_log", PillarBlock::new, createLogSettings(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_PINK, BlockSoundGroup.CHERRY_WOOD)
	);
	public static final Block STRIPPED_DARK_OAK_LOG = register(
		"stripped_dark_oak_log", PillarBlock::new, createLogSettings(MapColor.BROWN, MapColor.BROWN, BlockSoundGroup.WOOD)
	);
	public static final Block STRIPPED_PALE_OAK_LOG = register(
		"stripped_pale_oak_log",
		PillarBlock::new,
		createLogSettings(PALE_OAK_PLANKS.getDefaultMapColor(), PALE_OAK_PLANKS.getDefaultMapColor(), BlockSoundGroup.WOOD).requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block STRIPPED_OAK_LOG = register(
		"stripped_oak_log", PillarBlock::new, createLogSettings(MapColor.OAK_TAN, MapColor.OAK_TAN, BlockSoundGroup.WOOD)
	);
	public static final Block STRIPPED_MANGROVE_LOG = register(
		"stripped_mangrove_log", PillarBlock::new, createLogSettings(MapColor.RED, MapColor.RED, BlockSoundGroup.WOOD)
	);
	public static final Block STRIPPED_BAMBOO_BLOCK = register(
		"stripped_bamboo_block", PillarBlock::new, createLogSettings(MapColor.YELLOW, MapColor.YELLOW, BlockSoundGroup.BAMBOO_WOOD)
	);
	public static final Block OAK_WOOD = register(
		"oak_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block SPRUCE_WOOD = register(
		"spruce_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block BIRCH_WOOD = register(
		"birch_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block JUNGLE_WOOD = register(
		"jungle_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block ACACIA_WOOD = register(
		"acacia_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block CHERRY_WOOD = register(
		"cherry_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_GRAY)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F)
			.sounds(BlockSoundGroup.CHERRY_WOOD)
			.burnable()
	);
	public static final Block DARK_OAK_WOOD = register(
		"dark_oak_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block MANGROVE_WOOD = register(
		"mangrove_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block STRIPPED_OAK_WOOD = register(
		"stripped_oak_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block STRIPPED_SPRUCE_WOOD = register(
		"stripped_spruce_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block STRIPPED_BIRCH_WOOD = register(
		"stripped_birch_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block STRIPPED_JUNGLE_WOOD = register(
		"stripped_jungle_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block STRIPPED_ACACIA_WOOD = register(
		"stripped_acacia_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block STRIPPED_CHERRY_WOOD = register(
		"stripped_cherry_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_PINK)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F)
			.sounds(BlockSoundGroup.CHERRY_WOOD)
			.burnable()
	);
	public static final Block STRIPPED_DARK_OAK_WOOD = register(
		"stripped_dark_oak_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block STRIPPED_PALE_OAK_WOOD = register(
		"stripped_pale_oak_wood",
		PillarBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block STRIPPED_MANGROVE_WOOD = register(
		"stripped_mangrove_wood", PillarBlock::new, createLogSettings(MapColor.RED, MapColor.RED, BlockSoundGroup.WOOD)
	);
	public static final Block OAK_LEAVES = register("oak_leaves", LeavesBlock::new, createLeavesSettings(BlockSoundGroup.GRASS));
	public static final Block SPRUCE_LEAVES = register("spruce_leaves", LeavesBlock::new, createLeavesSettings(BlockSoundGroup.GRASS));
	public static final Block BIRCH_LEAVES = register("birch_leaves", LeavesBlock::new, createLeavesSettings(BlockSoundGroup.GRASS));
	public static final Block JUNGLE_LEAVES = register("jungle_leaves", LeavesBlock::new, createLeavesSettings(BlockSoundGroup.GRASS));
	public static final Block ACACIA_LEAVES = register("acacia_leaves", LeavesBlock::new, createLeavesSettings(BlockSoundGroup.GRASS));
	public static final Block CHERRY_LEAVES = register(
		"cherry_leaves",
		CherryLeavesBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PINK)
			.strength(0.2F)
			.ticksRandomly()
			.sounds(BlockSoundGroup.CHERRY_LEAVES)
			.nonOpaque()
			.allowsSpawning(Blocks::canSpawnOnLeaves)
			.suffocates(Blocks::never)
			.blockVision(Blocks::never)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
			.solidBlock(Blocks::never)
	);
	public static final Block DARK_OAK_LEAVES = register("dark_oak_leaves", LeavesBlock::new, createLeavesSettings(BlockSoundGroup.GRASS));
	public static final Block PALE_OAK_LEAVES = register(
		"pale_oak_leaves",
		LeavesBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_GREEN)
			.strength(0.2F)
			.ticksRandomly()
			.sounds(BlockSoundGroup.GRASS)
			.nonOpaque()
			.allowsSpawning(Blocks::canSpawnOnLeaves)
			.suffocates(Blocks::never)
			.blockVision(Blocks::never)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
			.solidBlock(Blocks::never)
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_LEAVES = register("mangrove_leaves", MangroveLeavesBlock::new, createLeavesSettings(BlockSoundGroup.GRASS));
	public static final Block AZALEA_LEAVES = register("azalea_leaves", LeavesBlock::new, createLeavesSettings(BlockSoundGroup.AZALEA_LEAVES));
	public static final Block FLOWERING_AZALEA_LEAVES = register("flowering_azalea_leaves", LeavesBlock::new, createLeavesSettings(BlockSoundGroup.AZALEA_LEAVES));
	public static final Block SPONGE = register(
		"sponge", SpongeBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).strength(0.6F).sounds(BlockSoundGroup.SPONGE)
	);
	public static final Block WET_SPONGE = register(
		"wet_sponge", WetSpongeBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).strength(0.6F).sounds(BlockSoundGroup.WET_SPONGE)
	);
	public static final Block GLASS = register(
		"glass",
		TransparentBlock::new,
		AbstractBlock.Settings.create()
			.instrument(NoteBlockInstrument.HAT)
			.strength(0.3F)
			.sounds(BlockSoundGroup.GLASS)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.solidBlock(Blocks::never)
			.suffocates(Blocks::never)
			.blockVision(Blocks::never)
	);
	public static final Block LAPIS_ORE = register(
		"lapis_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(2, 5), settings),
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
	);
	public static final Block DEEPSLATE_LAPIS_ORE = register(
		"deepslate_lapis_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(2, 5), settings),
		AbstractBlock.Settings.copyShallow(LAPIS_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block LAPIS_BLOCK = register(
		"lapis_block", AbstractBlock.Settings.create().mapColor(MapColor.LAPIS_BLUE).requiresTool().strength(3.0F, 3.0F)
	);
	public static final Block DISPENSER = register(
		"dispenser",
		DispenserBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5F)
	);
	public static final Block SANDSTONE = register(
		"sandstone", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(0.8F)
	);
	public static final Block CHISELED_SANDSTONE = register(
		"chiseled_sandstone", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(0.8F)
	);
	public static final Block CUT_SANDSTONE = register(
		"cut_sandstone", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(0.8F)
	);
	public static final Block NOTE_BLOCK = register(
		"note_block",
		NoteBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).sounds(BlockSoundGroup.WOOD).strength(0.8F).burnable()
	);
	public static final Block WHITE_BED = registerBedBlock("white_bed", DyeColor.WHITE);
	public static final Block ORANGE_BED = registerBedBlock("orange_bed", DyeColor.ORANGE);
	public static final Block MAGENTA_BED = registerBedBlock("magenta_bed", DyeColor.MAGENTA);
	public static final Block LIGHT_BLUE_BED = registerBedBlock("light_blue_bed", DyeColor.LIGHT_BLUE);
	public static final Block YELLOW_BED = registerBedBlock("yellow_bed", DyeColor.YELLOW);
	public static final Block LIME_BED = registerBedBlock("lime_bed", DyeColor.LIME);
	public static final Block PINK_BED = registerBedBlock("pink_bed", DyeColor.PINK);
	public static final Block GRAY_BED = registerBedBlock("gray_bed", DyeColor.GRAY);
	public static final Block LIGHT_GRAY_BED = registerBedBlock("light_gray_bed", DyeColor.LIGHT_GRAY);
	public static final Block CYAN_BED = registerBedBlock("cyan_bed", DyeColor.CYAN);
	public static final Block PURPLE_BED = registerBedBlock("purple_bed", DyeColor.PURPLE);
	public static final Block BLUE_BED = registerBedBlock("blue_bed", DyeColor.BLUE);
	public static final Block BROWN_BED = registerBedBlock("brown_bed", DyeColor.BROWN);
	public static final Block GREEN_BED = registerBedBlock("green_bed", DyeColor.GREEN);
	public static final Block RED_BED = registerBedBlock("red_bed", DyeColor.RED);
	public static final Block BLACK_BED = registerBedBlock("black_bed", DyeColor.BLACK);
	public static final Block POWERED_RAIL = register(
		"powered_rail", PoweredRailBlock::new, AbstractBlock.Settings.create().noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL)
	);
	public static final Block DETECTOR_RAIL = register(
		"detector_rail", DetectorRailBlock::new, AbstractBlock.Settings.create().noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL)
	);
	public static final Block STICKY_PISTON = register("sticky_piston", settings -> new PistonBlock(true, settings), createPistonSettings());
	public static final Block COBWEB = register(
		"cobweb",
		CobwebBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WHITE_GRAY)
			.sounds(BlockSoundGroup.COBWEB)
			.solid()
			.noCollision()
			.requiresTool()
			.strength(4.0F)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SHORT_GRASS = register(
		"short_grass",
		ShortPlantBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XYZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block FERN = register(
		"fern",
		ShortPlantBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XYZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DEAD_BUSH = register(
		"dead_bush",
		DeadBushBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SEAGRASS = register(
		"seagrass",
		SeagrassBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WATER_BLUE)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block TALL_SEAGRASS = register(
		"tall_seagrass",
		TallSeagrassBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WATER_BLUE)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PISTON = register("piston", settings -> new PistonBlock(false, settings), createPistonSettings());
	public static final Block PISTON_HEAD = register(
		"piston_head",
		PistonHeadBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5F).dropsNothing().pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block WHITE_WOOL = register(
		"white_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.WHITE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block ORANGE_WOOL = register(
		"orange_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block MAGENTA_WOOL = register(
		"magenta_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block LIGHT_BLUE_WOOL = register(
		"light_blue_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block YELLOW_WOOL = register(
		"yellow_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block LIME_WOOL = register(
		"lime_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block PINK_WOOL = register(
		"pink_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.PINK).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block GRAY_WOOL = register(
		"gray_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block LIGHT_GRAY_WOOL = register(
		"light_gray_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_GRAY).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block CYAN_WOOL = register(
		"cyan_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.CYAN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block PURPLE_WOOL = register(
		"purple_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block BLUE_WOOL = register(
		"blue_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block BROWN_WOOL = register(
		"brown_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block GREEN_WOOL = register(
		"green_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.GREEN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block RED_WOOL = register(
		"red_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block BLACK_WOOL = register(
		"black_wool",
		AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block MOVING_PISTON = register(
		"moving_piston",
		PistonExtensionBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.solid()
			.strength(-1.0F)
			.dynamicBounds()
			.dropsNothing()
			.nonOpaque()
			.solidBlock(Blocks::never)
			.suffocates(Blocks::never)
			.blockVision(Blocks::never)
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block DANDELION = register(
		"dandelion",
		settings -> new FlowerBlock(StatusEffects.SATURATION, 0.35F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block TORCHFLOWER = register(
		"torchflower",
		settings -> new FlowerBlock(StatusEffects.NIGHT_VISION, 5.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block POPPY = register(
		"poppy",
		settings -> new FlowerBlock(StatusEffects.NIGHT_VISION, 5.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BLUE_ORCHID = register(
		"blue_orchid",
		settings -> new FlowerBlock(StatusEffects.SATURATION, 0.35F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ALLIUM = register(
		"allium",
		settings -> new FlowerBlock(StatusEffects.FIRE_RESISTANCE, 4.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block AZURE_BLUET = register(
		"azure_bluet",
		settings -> new FlowerBlock(StatusEffects.BLINDNESS, 8.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block RED_TULIP = register(
		"red_tulip",
		settings -> new FlowerBlock(StatusEffects.WEAKNESS, 9.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ORANGE_TULIP = register(
		"orange_tulip",
		settings -> new FlowerBlock(StatusEffects.WEAKNESS, 9.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WHITE_TULIP = register(
		"white_tulip",
		settings -> new FlowerBlock(StatusEffects.WEAKNESS, 9.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PINK_TULIP = register(
		"pink_tulip",
		settings -> new FlowerBlock(StatusEffects.WEAKNESS, 9.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block OXEYE_DAISY = register(
		"oxeye_daisy",
		settings -> new FlowerBlock(StatusEffects.REGENERATION, 8.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CORNFLOWER = register(
		"cornflower",
		settings -> new FlowerBlock(StatusEffects.JUMP_BOOST, 6.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WITHER_ROSE = register(
		"wither_rose",
		settings -> new WitherRoseBlock(StatusEffects.WITHER, 8.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block LILY_OF_THE_VALLEY = register(
		"lily_of_the_valley",
		settings -> new FlowerBlock(StatusEffects.POISON, 12.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BROWN_MUSHROOM = register(
		"brown_mushroom",
		settings -> new MushroomPlantBlock(TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BROWN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.luminance(state -> 1)
			.postProcess(Blocks::always)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block RED_MUSHROOM = register(
		"red_mushroom",
		settings -> new MushroomPlantBlock(TreeConfiguredFeatures.HUGE_RED_MUSHROOM, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.RED)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.postProcess(Blocks::always)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block GOLD_BLOCK = register(
		"gold_block",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.GOLD)
			.instrument(NoteBlockInstrument.BELL)
			.requiresTool()
			.strength(3.0F, 6.0F)
			.sounds(BlockSoundGroup.METAL)
	);
	public static final Block IRON_BLOCK = register(
		"iron_block",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.IRON_GRAY)
			.instrument(NoteBlockInstrument.IRON_XYLOPHONE)
			.requiresTool()
			.strength(5.0F, 6.0F)
			.sounds(BlockSoundGroup.METAL)
	);
	public static final Block BRICKS = register(
		"bricks", AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block TNT = register(
		"tnt",
		TntBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_RED).breakInstantly().sounds(BlockSoundGroup.GRASS).burnable().solidBlock(Blocks::never)
	);
	public static final Block BOOKSHELF = register(
		"bookshelf",
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(1.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block CHISELED_BOOKSHELF = register(
		"chiseled_bookshelf",
		ChiseledBookshelfBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(1.5F)
			.sounds(BlockSoundGroup.CHISELED_BOOKSHELF)
			.burnable()
	);
	public static final Block MOSSY_COBBLESTONE = register(
		"mossy_cobblestone",
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block OBSIDIAN = register(
		"obsidian", AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(50.0F, 1200.0F)
	);
	public static final Block TORCH = register(
		"torch",
		settings -> new TorchBlock(ParticleTypes.FLAME, settings),
		AbstractBlock.Settings.create().noCollision().breakInstantly().luminance(state -> 14).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WALL_TORCH = register(
		"wall_torch",
		settings -> new WallTorchBlock(ParticleTypes.FLAME, settings),
		copyLootTable(TORCH, true).noCollision().breakInstantly().luminance(state -> 14).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block FIRE = register(
		"fire",
		FireBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BRIGHT_RED)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.luminance(state -> 15)
			.sounds(BlockSoundGroup.WOOL)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SOUL_FIRE = register(
		"soul_fire",
		SoulFireBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.LIGHT_BLUE)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.luminance(state -> 10)
			.sounds(BlockSoundGroup.WOOL)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SPAWNER = register(
		"spawner",
		SpawnerBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(5.0F)
			.sounds(BlockSoundGroup.SPAWNER)
			.nonOpaque()
	);
	public static final Block CREAKING_HEART = register(
		"creaking_heart",
		CreakingHeartBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.ORANGE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.strength(5.0F)
			.sounds(BlockSoundGroup.CREAKING_HEART)
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block OAK_STAIRS = registerOldStairsBlock("oak_stairs", OAK_PLANKS);
	public static final Block CHEST = register(
		"chest",
		settings -> new ChestBlock(() -> BlockEntityType.CHEST, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block REDSTONE_WIRE = register(
		"redstone_wire", RedstoneWireBlock::new, AbstractBlock.Settings.create().noCollision().breakInstantly().pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DIAMOND_ORE = register(
		"diamond_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), settings),
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
	);
	public static final Block DEEPSLATE_DIAMOND_ORE = register(
		"deepslate_diamond_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), settings),
		AbstractBlock.Settings.copyShallow(DIAMOND_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block DIAMOND_BLOCK = register(
		"diamond_block", AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)
	);
	public static final Block CRAFTING_TABLE = register(
		"crafting_table",
		CraftingTableBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block WHEAT = register(
		"wheat",
		CropBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(state -> state.get(CropBlock.AGE) >= 6 ? MapColor.YELLOW : MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.CROP)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block FARMLAND = register(
		"farmland",
		FarmlandBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DIRT_BROWN)
			.ticksRandomly()
			.strength(0.6F)
			.sounds(BlockSoundGroup.GRAVEL)
			.blockVision(Blocks::always)
			.suffocates(Blocks::always)
	);
	public static final Block FURNACE = register(
		"furnace",
		FurnaceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(3.5F)
			.luminance(createLightLevelFromLitBlockState(13))
	);
	public static final Block OAK_SIGN = register(
		"oak_sign",
		settings -> new SignBlock(WoodType.OAK, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block SPRUCE_SIGN = register(
		"spruce_sign",
		settings -> new SignBlock(WoodType.SPRUCE, settings),
		AbstractBlock.Settings.create()
			.mapColor(SPRUCE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block BIRCH_SIGN = register(
		"birch_sign",
		settings -> new SignBlock(WoodType.BIRCH, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block ACACIA_SIGN = register(
		"acacia_sign",
		settings -> new SignBlock(WoodType.ACACIA, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block CHERRY_SIGN = register(
		"cherry_sign",
		settings -> new SignBlock(WoodType.CHERRY, settings),
		AbstractBlock.Settings.create()
			.mapColor(CHERRY_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block JUNGLE_SIGN = register(
		"jungle_sign",
		settings -> new SignBlock(WoodType.JUNGLE, settings),
		AbstractBlock.Settings.create()
			.mapColor(JUNGLE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block DARK_OAK_SIGN = register(
		"dark_oak_sign",
		settings -> new SignBlock(WoodType.DARK_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(DARK_OAK_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block PALE_OAK_SIGN = register(
		"pale_oak_sign",
		settings -> new SignBlock(WoodType.PALE_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_SIGN = register(
		"mangrove_sign",
		settings -> new SignBlock(WoodType.MANGROVE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MANGROVE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block BAMBOO_SIGN = register(
		"bamboo_sign",
		settings -> new SignBlock(WoodType.BAMBOO, settings),
		AbstractBlock.Settings.create()
			.mapColor(BAMBOO_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block OAK_DOOR = register(
		"oak_door",
		settings -> new DoorBlock(BlockSetType.OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(OAK_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block LADDER = register(
		"ladder",
		LadderBlock::new,
		AbstractBlock.Settings.create().notSolid().strength(0.4F).sounds(BlockSoundGroup.LADDER).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block RAIL = register("rail", RailBlock::new, AbstractBlock.Settings.create().noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL));
	public static final Block COBBLESTONE_STAIRS = registerOldStairsBlock("cobblestone_stairs", COBBLESTONE);
	public static final Block OAK_WALL_SIGN = register(
		"oak_wall_sign",
		settings -> new WallSignBlock(WoodType.OAK, settings),
		copyLootTable(OAK_SIGN, true).mapColor(MapColor.OAK_TAN).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block SPRUCE_WALL_SIGN = register(
		"spruce_wall_sign",
		settings -> new WallSignBlock(WoodType.SPRUCE, settings),
		copyLootTable(SPRUCE_SIGN, true)
			.mapColor(SPRUCE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block BIRCH_WALL_SIGN = register(
		"birch_wall_sign",
		settings -> new WallSignBlock(WoodType.BIRCH, settings),
		copyLootTable(BIRCH_SIGN, true).mapColor(MapColor.PALE_YELLOW).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block ACACIA_WALL_SIGN = register(
		"acacia_wall_sign",
		settings -> new WallSignBlock(WoodType.ACACIA, settings),
		copyLootTable(ACACIA_SIGN, true).mapColor(MapColor.ORANGE).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block CHERRY_WALL_SIGN = register(
		"cherry_wall_sign",
		settings -> new WallSignBlock(WoodType.CHERRY, settings),
		copyLootTable(CHERRY_SIGN, true)
			.mapColor(CHERRY_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block JUNGLE_WALL_SIGN = register(
		"jungle_wall_sign",
		settings -> new WallSignBlock(WoodType.JUNGLE, settings),
		copyLootTable(JUNGLE_SIGN, true)
			.mapColor(JUNGLE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block DARK_OAK_WALL_SIGN = register(
		"dark_oak_wall_sign",
		settings -> new WallSignBlock(WoodType.DARK_OAK, settings),
		copyLootTable(DARK_OAK_SIGN, true)
			.mapColor(DARK_OAK_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block PALE_OAK_WALL_SIGN = register(
		"pale_oak_wall_sign",
		settings -> new WallSignBlock(WoodType.PALE_OAK, settings),
		copyLootTable(PALE_OAK_SIGN, true)
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_WALL_SIGN = register(
		"mangrove_wall_sign",
		settings -> new WallSignBlock(WoodType.MANGROVE, settings),
		copyLootTable(MANGROVE_SIGN, true)
			.mapColor(MANGROVE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block BAMBOO_WALL_SIGN = register(
		"bamboo_wall_sign",
		settings -> new WallSignBlock(WoodType.BAMBOO, settings),
		copyLootTable(BAMBOO_SIGN, true)
			.mapColor(BAMBOO_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block OAK_HANGING_SIGN = register(
		"oak_hanging_sign",
		settings -> new HangingSignBlock(WoodType.OAK, settings),
		AbstractBlock.Settings.create().mapColor(OAK_LOG.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block SPRUCE_HANGING_SIGN = register(
		"spruce_hanging_sign",
		settings -> new HangingSignBlock(WoodType.SPRUCE, settings),
		AbstractBlock.Settings.create()
			.mapColor(SPRUCE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block BIRCH_HANGING_SIGN = register(
		"birch_hanging_sign",
		settings -> new HangingSignBlock(WoodType.BIRCH, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block ACACIA_HANGING_SIGN = register(
		"acacia_hanging_sign",
		settings -> new HangingSignBlock(WoodType.ACACIA, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block CHERRY_HANGING_SIGN = register(
		"cherry_hanging_sign",
		settings -> new HangingSignBlock(WoodType.CHERRY, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_PINK).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block JUNGLE_HANGING_SIGN = register(
		"jungle_hanging_sign",
		settings -> new HangingSignBlock(WoodType.JUNGLE, settings),
		AbstractBlock.Settings.create()
			.mapColor(JUNGLE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block DARK_OAK_HANGING_SIGN = register(
		"dark_oak_hanging_sign",
		settings -> new HangingSignBlock(WoodType.DARK_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(DARK_OAK_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block PALE_OAK_HANGING_SIGN = register(
		"pale_oak_hanging_sign",
		settings -> new HangingSignBlock(WoodType.PALE_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block CRIMSON_HANGING_SIGN = register(
		"crimson_hanging_sign",
		settings -> new HangingSignBlock(WoodType.CRIMSON, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.DULL_PINK).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F)
	);
	public static final Block WARPED_HANGING_SIGN = register(
		"warped_hanging_sign",
		settings -> new HangingSignBlock(WoodType.WARPED, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.DARK_AQUA).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F)
	);
	public static final Block MANGROVE_HANGING_SIGN = register(
		"mangrove_hanging_sign",
		settings -> new HangingSignBlock(WoodType.MANGROVE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MANGROVE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block BAMBOO_HANGING_SIGN = register(
		"bamboo_hanging_sign",
		settings -> new HangingSignBlock(WoodType.BAMBOO, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block OAK_WALL_HANGING_SIGN = register(
		"oak_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.OAK, settings),
		copyLootTable(OAK_HANGING_SIGN, true)
			.mapColor(OAK_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block SPRUCE_WALL_HANGING_SIGN = register(
		"spruce_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.SPRUCE, settings),
		copyLootTable(SPRUCE_HANGING_SIGN, true).mapColor(MapColor.OAK_TAN).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block BIRCH_WALL_HANGING_SIGN = register(
		"birch_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.BIRCH, settings),
		copyLootTable(BIRCH_HANGING_SIGN, true).mapColor(MapColor.PALE_YELLOW).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block ACACIA_WALL_HANGING_SIGN = register(
		"acacia_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.ACACIA, settings),
		copyLootTable(ACACIA_HANGING_SIGN, true).mapColor(MapColor.ORANGE).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block CHERRY_WALL_HANGING_SIGN = register(
		"cherry_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.CHERRY, settings),
		copyLootTable(CHERRY_HANGING_SIGN, true)
			.mapColor(MapColor.TERRACOTTA_PINK)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block JUNGLE_WALL_HANGING_SIGN = register(
		"jungle_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.JUNGLE, settings),
		copyLootTable(JUNGLE_HANGING_SIGN, true)
			.mapColor(JUNGLE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block DARK_OAK_WALL_HANGING_SIGN = register(
		"dark_oak_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.DARK_OAK, settings),
		copyLootTable(DARK_OAK_HANGING_SIGN, true)
			.mapColor(DARK_OAK_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block PALE_OAK_WALL_HANGING_SIGN = register(
		"pale_oak_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.PALE_OAK, settings),
		copyLootTable(PALE_OAK_HANGING_SIGN, true)
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_WALL_HANGING_SIGN = register(
		"mangrove_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.MANGROVE, settings),
		copyLootTable(MANGROVE_HANGING_SIGN, true)
			.mapColor(MANGROVE_LOG.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.burnable()
	);
	public static final Block CRIMSON_WALL_HANGING_SIGN = register(
		"crimson_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.CRIMSON, settings),
		copyLootTable(CRIMSON_HANGING_SIGN, true).mapColor(MapColor.DULL_PINK).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F)
	);
	public static final Block WARPED_WALL_HANGING_SIGN = register(
		"warped_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.WARPED, settings),
		copyLootTable(WARPED_HANGING_SIGN, true).mapColor(MapColor.DARK_AQUA).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F)
	);
	public static final Block BAMBOO_WALL_HANGING_SIGN = register(
		"bamboo_wall_hanging_sign",
		settings -> new WallHangingSignBlock(WoodType.BAMBOO, settings),
		copyLootTable(BAMBOO_HANGING_SIGN, true).mapColor(MapColor.YELLOW).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
	);
	public static final Block LEVER = register(
		"lever", LeverBlock::new, AbstractBlock.Settings.create().noCollision().strength(0.5F).sounds(BlockSoundGroup.STONE).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block STONE_PRESSURE_PLATE = register(
		"stone_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.STONE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.solid()
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.noCollision()
			.strength(0.5F)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block IRON_DOOR = register(
		"iron_door",
		settings -> new DoorBlock(BlockSetType.IRON, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0F).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block OAK_PRESSURE_PLATE = register(
		"oak_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(OAK_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SPRUCE_PRESSURE_PLATE = register(
		"spruce_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.SPRUCE, settings),
		AbstractBlock.Settings.create()
			.mapColor(SPRUCE_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BIRCH_PRESSURE_PLATE = register(
		"birch_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.BIRCH, settings),
		AbstractBlock.Settings.create()
			.mapColor(BIRCH_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block JUNGLE_PRESSURE_PLATE = register(
		"jungle_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.JUNGLE, settings),
		AbstractBlock.Settings.create()
			.mapColor(JUNGLE_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ACACIA_PRESSURE_PLATE = register(
		"acacia_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.ACACIA, settings),
		AbstractBlock.Settings.create()
			.mapColor(ACACIA_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CHERRY_PRESSURE_PLATE = register(
		"cherry_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.CHERRY, settings),
		AbstractBlock.Settings.create()
			.mapColor(CHERRY_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DARK_OAK_PRESSURE_PLATE = register(
		"dark_oak_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.DARK_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(DARK_OAK_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PALE_OAK_PRESSURE_PLATE = register(
		"pale_oak_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.PALE_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_PRESSURE_PLATE = register(
		"mangrove_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.MANGROVE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MANGROVE_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BAMBOO_PRESSURE_PLATE = register(
		"bamboo_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.BAMBOO, settings),
		AbstractBlock.Settings.create()
			.mapColor(BAMBOO_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block REDSTONE_ORE = register(
		"redstone_ore",
		RedstoneOreBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.ticksRandomly()
			.luminance(createLightLevelFromLitBlockState(9))
			.strength(3.0F, 3.0F)
	);
	public static final Block DEEPSLATE_REDSTONE_ORE = register(
		"deepslate_redstone_ore",
		RedstoneOreBlock::new,
		AbstractBlock.Settings.copyShallow(REDSTONE_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block REDSTONE_TORCH = register(
		"redstone_torch",
		RedstoneTorchBlock::new,
		AbstractBlock.Settings.create()
			.noCollision()
			.breakInstantly()
			.luminance(createLightLevelFromLitBlockState(7))
			.sounds(BlockSoundGroup.WOOD)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block REDSTONE_WALL_TORCH = register(
		"redstone_wall_torch",
		WallRedstoneTorchBlock::new,
		copyLootTable(REDSTONE_TORCH, true)
			.noCollision()
			.breakInstantly()
			.luminance(createLightLevelFromLitBlockState(7))
			.sounds(BlockSoundGroup.WOOD)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block STONE_BUTTON = register("stone_button", settings -> new ButtonBlock(BlockSetType.STONE, 20, settings), createButtonSettings());
	public static final Block SNOW = register(
		"snow",
		SnowBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WHITE)
			.replaceable()
			.notSolid()
			.ticksRandomly()
			.strength(0.1F)
			.requiresTool()
			.sounds(BlockSoundGroup.SNOW)
			.blockVision((state, world, pos) -> (Integer)state.get(SnowBlock.LAYERS) >= 8)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ICE = register(
		"ice",
		IceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_PURPLE)
			.slipperiness(0.98F)
			.ticksRandomly()
			.strength(0.5F)
			.sounds(BlockSoundGroup.GLASS)
			.nonOpaque()
			.allowsSpawning((state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR)
			.solidBlock(Blocks::never)
	);
	public static final Block SNOW_BLOCK = register(
		"snow_block", AbstractBlock.Settings.create().mapColor(MapColor.WHITE).requiresTool().strength(0.2F).sounds(BlockSoundGroup.SNOW)
	);
	public static final Block CACTUS = register(
		"cactus",
		CactusBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.ticksRandomly()
			.strength(0.4F)
			.sounds(BlockSoundGroup.WOOL)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CLAY = register(
		"clay",
		AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY).instrument(NoteBlockInstrument.FLUTE).strength(0.6F).sounds(BlockSoundGroup.GRAVEL)
	);
	public static final Block SUGAR_CANE = register(
		"sugar_cane",
		SugarCaneBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block JUKEBOX = register(
		"jukebox",
		JukeboxBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DIRT_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 6.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block OAK_FENCE = register(
		"oak_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(OAK_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block NETHERRACK = register(
		"netherrack",
		NetherrackBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(0.4F)
			.sounds(BlockSoundGroup.NETHERRACK)
	);
	public static final Block SOUL_SAND = register(
		"soul_sand",
		SoulSandBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BROWN)
			.instrument(NoteBlockInstrument.COW_BELL)
			.strength(0.5F)
			.velocityMultiplier(0.4F)
			.sounds(BlockSoundGroup.SOUL_SAND)
			.allowsSpawning(Blocks::always)
			.solidBlock(Blocks::always)
			.blockVision(Blocks::always)
			.suffocates(Blocks::always)
	);
	public static final Block SOUL_SOIL = register(
		"soul_soil", AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(0.5F).sounds(BlockSoundGroup.SOUL_SOIL)
	);
	public static final Block BASALT = register(
		"basalt",
		PillarBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLACK)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.25F, 4.2F)
			.sounds(BlockSoundGroup.BASALT)
	);
	public static final Block POLISHED_BASALT = register(
		"polished_basalt",
		PillarBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLACK)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.25F, 4.2F)
			.sounds(BlockSoundGroup.BASALT)
	);
	public static final Block SOUL_TORCH = register(
		"soul_torch",
		settings -> new TorchBlock(ParticleTypes.SOUL_FIRE_FLAME, settings),
		AbstractBlock.Settings.create().noCollision().breakInstantly().luminance(state -> 10).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SOUL_WALL_TORCH = register(
		"soul_wall_torch",
		settings -> new WallTorchBlock(ParticleTypes.SOUL_FIRE_FLAME, settings),
		copyLootTable(SOUL_TORCH, true).noCollision().breakInstantly().luminance(state -> 10).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block GLOWSTONE = register(
		"glowstone",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_YELLOW)
			.instrument(NoteBlockInstrument.PLING)
			.strength(0.3F)
			.sounds(BlockSoundGroup.GLASS)
			.luminance(state -> 15)
			.solidBlock(Blocks::never)
	);
	public static final Block NETHER_PORTAL = register(
		"nether_portal",
		NetherPortalBlock::new,
		AbstractBlock.Settings.create()
			.noCollision()
			.ticksRandomly()
			.strength(-1.0F)
			.sounds(BlockSoundGroup.GLASS)
			.luminance(state -> 11)
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block CARVED_PUMPKIN = register(
		"carved_pumpkin",
		CarvedPumpkinBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.ORANGE)
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.allowsSpawning(Blocks::always)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block JACK_O_LANTERN = register(
		"jack_o_lantern",
		CarvedPumpkinBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.ORANGE)
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.luminance(state -> 15)
			.allowsSpawning(Blocks::always)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CAKE = register(
		"cake", CakeBlock::new, AbstractBlock.Settings.create().solid().strength(0.5F).sounds(BlockSoundGroup.WOOL).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block REPEATER = register(
		"repeater", RepeaterBlock::new, AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.STONE).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WHITE_STAINED_GLASS = registerStainedGlassBlock("white_stained_glass", DyeColor.WHITE);
	public static final Block ORANGE_STAINED_GLASS = registerStainedGlassBlock("orange_stained_glass", DyeColor.ORANGE);
	public static final Block MAGENTA_STAINED_GLASS = registerStainedGlassBlock("magenta_stained_glass", DyeColor.MAGENTA);
	public static final Block LIGHT_BLUE_STAINED_GLASS = registerStainedGlassBlock("light_blue_stained_glass", DyeColor.LIGHT_BLUE);
	public static final Block YELLOW_STAINED_GLASS = registerStainedGlassBlock("yellow_stained_glass", DyeColor.YELLOW);
	public static final Block LIME_STAINED_GLASS = registerStainedGlassBlock("lime_stained_glass", DyeColor.LIME);
	public static final Block PINK_STAINED_GLASS = registerStainedGlassBlock("pink_stained_glass", DyeColor.PINK);
	public static final Block GRAY_STAINED_GLASS = registerStainedGlassBlock("gray_stained_glass", DyeColor.GRAY);
	public static final Block LIGHT_GRAY_STAINED_GLASS = registerStainedGlassBlock("light_gray_stained_glass", DyeColor.LIGHT_GRAY);
	public static final Block CYAN_STAINED_GLASS = registerStainedGlassBlock("cyan_stained_glass", DyeColor.CYAN);
	public static final Block PURPLE_STAINED_GLASS = registerStainedGlassBlock("purple_stained_glass", DyeColor.PURPLE);
	public static final Block BLUE_STAINED_GLASS = registerStainedGlassBlock("blue_stained_glass", DyeColor.BLUE);
	public static final Block BROWN_STAINED_GLASS = registerStainedGlassBlock("brown_stained_glass", DyeColor.BROWN);
	public static final Block GREEN_STAINED_GLASS = registerStainedGlassBlock("green_stained_glass", DyeColor.GREEN);
	public static final Block RED_STAINED_GLASS = registerStainedGlassBlock("red_stained_glass", DyeColor.RED);
	public static final Block BLACK_STAINED_GLASS = registerStainedGlassBlock("black_stained_glass", DyeColor.BLACK);
	public static final Block OAK_TRAPDOOR = register(
		"oak_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
	);
	public static final Block SPRUCE_TRAPDOOR = register(
		"spruce_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.SPRUCE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.SPRUCE_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
	);
	public static final Block BIRCH_TRAPDOOR = register(
		"birch_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.BIRCH, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_YELLOW)
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
	);
	public static final Block JUNGLE_TRAPDOOR = register(
		"jungle_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.JUNGLE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DIRT_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
	);
	public static final Block ACACIA_TRAPDOOR = register(
		"acacia_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.ACACIA, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.ORANGE)
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
	);
	public static final Block CHERRY_TRAPDOOR = register(
		"cherry_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.CHERRY, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_WHITE)
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
	);
	public static final Block DARK_OAK_TRAPDOOR = register(
		"dark_oak_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.DARK_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
	);
	public static final Block PALE_OAK_TRAPDOOR = register(
		"pale_oak_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.PALE_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_TRAPDOOR = register(
		"mangrove_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.MANGROVE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.RED)
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
	);
	public static final Block BAMBOO_TRAPDOOR = register(
		"bamboo_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.BAMBOO, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.YELLOW)
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.burnable()
	);
	public static final Block STONE_BRICKS = register(
		"stone_bricks", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block MOSSY_STONE_BRICKS = register(
		"mossy_stone_bricks",
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block CRACKED_STONE_BRICKS = register(
		"cracked_stone_bricks",
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block CHISELED_STONE_BRICKS = register(
		"chiseled_stone_bricks",
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block PACKED_MUD = register("packed_mud", AbstractBlock.Settings.copyShallow(DIRT).strength(1.0F, 3.0F).sounds(BlockSoundGroup.PACKED_MUD));
	public static final Block MUD_BRICKS = register(
		"mud_bricks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.5F, 3.0F)
			.sounds(BlockSoundGroup.MUD_BRICKS)
	);
	public static final Block INFESTED_STONE = register(
		"infested_stone", settings -> new InfestedBlock(STONE, settings), AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY)
	);
	public static final Block INFESTED_COBBLESTONE = register(
		"infested_cobblestone", settings -> new InfestedBlock(COBBLESTONE, settings), AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY)
	);
	public static final Block INFESTED_STONE_BRICKS = register(
		"infested_stone_bricks", settings -> new InfestedBlock(STONE_BRICKS, settings), AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY)
	);
	public static final Block INFESTED_MOSSY_STONE_BRICKS = register(
		"infested_mossy_stone_bricks",
		settings -> new InfestedBlock(MOSSY_STONE_BRICKS, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY)
	);
	public static final Block INFESTED_CRACKED_STONE_BRICKS = register(
		"infested_cracked_stone_bricks",
		settings -> new InfestedBlock(CRACKED_STONE_BRICKS, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY)
	);
	public static final Block INFESTED_CHISELED_STONE_BRICKS = register(
		"infested_chiseled_stone_bricks",
		settings -> new InfestedBlock(CHISELED_STONE_BRICKS, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY)
	);
	public static final Block BROWN_MUSHROOM_BLOCK = register(
		"brown_mushroom_block",
		MushroomBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(NoteBlockInstrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block RED_MUSHROOM_BLOCK = register(
		"red_mushroom_block",
		MushroomBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block MUSHROOM_STEM = register(
		"mushroom_stem",
		MushroomBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).instrument(NoteBlockInstrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block IRON_BARS = register(
		"iron_bars", PaneBlock::new, AbstractBlock.Settings.create().requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).nonOpaque()
	);
	public static final Block CHAIN = register(
		"chain", ChainBlock::new, AbstractBlock.Settings.create().solid().requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.CHAIN).nonOpaque()
	);
	public static final Block GLASS_PANE = register(
		"glass_pane", PaneBlock::new, AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block PUMPKIN = register(
		BlockKeys.PUMPKIN,
		PumpkinBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.ORANGE)
			.instrument(NoteBlockInstrument.DIDGERIDOO)
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block MELON = register(
		BlockKeys.MELON, AbstractBlock.Settings.create().mapColor(MapColor.LIME).strength(1.0F).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ATTACHED_PUMPKIN_STEM = register(
		BlockKeys.ATTACHED_PUMPKIN_STEM,
		settings -> new AttachedStemBlock(BlockKeys.PUMPKIN_STEM, BlockKeys.PUMPKIN, ItemKeys.PUMPKIN_SEEDS, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WOOD)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ATTACHED_MELON_STEM = register(
		BlockKeys.ATTACHED_MELON_STEM,
		settings -> new AttachedStemBlock(BlockKeys.MELON_STEM, BlockKeys.MELON, ItemKeys.MELON_SEEDS, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WOOD)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PUMPKIN_STEM = register(
		BlockKeys.PUMPKIN_STEM,
		settings -> new StemBlock(BlockKeys.PUMPKIN, BlockKeys.ATTACHED_PUMPKIN_STEM, ItemKeys.PUMPKIN_SEEDS, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.STEM)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block MELON_STEM = register(
		BlockKeys.MELON_STEM,
		settings -> new StemBlock(BlockKeys.MELON, BlockKeys.ATTACHED_MELON_STEM, ItemKeys.MELON_SEEDS, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.STEM)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block VINE = register(
		"vine",
		VineBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.replaceable()
			.noCollision()
			.ticksRandomly()
			.strength(0.2F)
			.sounds(BlockSoundGroup.VINE)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block GLOW_LICHEN = register(
		"glow_lichen",
		GlowLichenBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.LICHEN_GREEN)
			.replaceable()
			.noCollision()
			.strength(0.2F)
			.sounds(BlockSoundGroup.GLOW_LICHEN)
			.luminance(GlowLichenBlock.getLuminanceSupplier(7))
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block OAK_FENCE_GATE = register(
		"oak_fence_gate",
		settings -> new FenceGateBlock(WoodType.OAK, settings),
		AbstractBlock.Settings.create().mapColor(OAK_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
	);
	public static final Block BRICK_STAIRS = registerOldStairsBlock("brick_stairs", BRICKS);
	public static final Block STONE_BRICK_STAIRS = registerOldStairsBlock("stone_brick_stairs", STONE_BRICKS);
	public static final Block MUD_BRICK_STAIRS = registerOldStairsBlock("mud_brick_stairs", MUD_BRICKS);
	public static final Block MYCELIUM = register(
		"mycelium", MyceliumBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS)
	);
	public static final Block LILY_PAD = register(
		"lily_pad",
		LilyPadBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.breakInstantly()
			.sounds(BlockSoundGroup.LILY_PAD)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block NETHER_BRICKS = register(
		"nether_bricks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(2.0F, 6.0F)
			.sounds(BlockSoundGroup.NETHER_BRICKS)
	);
	public static final Block NETHER_BRICK_FENCE = register(
		"nether_brick_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(2.0F, 6.0F)
			.sounds(BlockSoundGroup.NETHER_BRICKS)
	);
	public static final Block NETHER_BRICK_STAIRS = registerOldStairsBlock("nether_brick_stairs", NETHER_BRICKS);
	public static final Block NETHER_WART = register(
		"nether_wart",
		NetherWartBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.RED)
			.noCollision()
			.ticksRandomly()
			.sounds(BlockSoundGroup.NETHER_WART)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ENCHANTING_TABLE = register(
		"enchanting_table",
		EnchantingTableBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().luminance(state -> 7).strength(5.0F, 1200.0F)
	);
	public static final Block BREWING_STAND = register(
		"brewing_stand",
		BrewingStandBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(0.5F).luminance(state -> 1).nonOpaque()
	);
	public static final Block CAULDRON = register(
		"cauldron", CauldronBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(2.0F).nonOpaque()
	);
	public static final Block WATER_CAULDRON = register(
		"water_cauldron",
		settings -> new LeveledCauldronBlock(Biome.Precipitation.RAIN, CauldronBehavior.WATER_CAULDRON_BEHAVIOR, settings),
		AbstractBlock.Settings.copyShallow(CAULDRON)
	);
	public static final Block LAVA_CAULDRON = register(
		"lava_cauldron", LavaCauldronBlock::new, AbstractBlock.Settings.copyShallow(CAULDRON).luminance(state -> 15)
	);
	public static final Block POWDER_SNOW_CAULDRON = register(
		"powder_snow_cauldron",
		settings -> new LeveledCauldronBlock(Biome.Precipitation.SNOW, CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR, settings),
		AbstractBlock.Settings.copyShallow(CAULDRON)
	);
	public static final Block END_PORTAL = register(
		"end_portal",
		EndPortalBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLACK)
			.noCollision()
			.luminance(state -> 15)
			.strength(-1.0F, 3600000.0F)
			.dropsNothing()
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block END_PORTAL_FRAME = register(
		"end_portal_frame",
		EndPortalFrameBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.GREEN)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.sounds(BlockSoundGroup.GLASS)
			.luminance(state -> 1)
			.strength(-1.0F, 3600000.0F)
			.dropsNothing()
	);
	public static final Block END_STONE = register(
		"end_stone", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0F, 9.0F)
	);
	public static final Block DRAGON_EGG = register(
		"dragon_egg",
		DragonEggBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(3.0F, 9.0F).luminance(state -> 1).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block REDSTONE_LAMP = register(
		"redstone_lamp",
		RedstoneLampBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_ORANGE)
			.luminance(createLightLevelFromLitBlockState(15))
			.strength(0.3F)
			.sounds(BlockSoundGroup.GLASS)
			.allowsSpawning(Blocks::always)
	);
	public static final Block COCOA = register(
		"cocoa",
		CocoaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.ticksRandomly()
			.strength(0.2F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SANDSTONE_STAIRS = registerOldStairsBlock("sandstone_stairs", SANDSTONE);
	public static final Block EMERALD_ORE = register(
		"emerald_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), settings),
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
	);
	public static final Block DEEPSLATE_EMERALD_ORE = register(
		"deepslate_emerald_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), settings),
		AbstractBlock.Settings.copyShallow(EMERALD_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block ENDER_CHEST = register(
		"ender_chest",
		EnderChestBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(22.5F, 600.0F)
			.luminance(state -> 7)
	);
	public static final Block TRIPWIRE_HOOK = register(
		"tripwire_hook", TripwireHookBlock::new, AbstractBlock.Settings.create().noCollision().sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block TRIPWIRE = register(
		"tripwire", settings -> new TripwireBlock(TRIPWIRE_HOOK, settings), AbstractBlock.Settings.create().noCollision().pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block EMERALD_BLOCK = register(
		"emerald_block",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.EMERALD_GREEN)
			.instrument(NoteBlockInstrument.BIT)
			.requiresTool()
			.strength(5.0F, 6.0F)
			.sounds(BlockSoundGroup.METAL)
	);
	public static final Block SPRUCE_STAIRS = registerOldStairsBlock("spruce_stairs", SPRUCE_PLANKS);
	public static final Block BIRCH_STAIRS = registerOldStairsBlock("birch_stairs", BIRCH_PLANKS);
	public static final Block JUNGLE_STAIRS = registerOldStairsBlock("jungle_stairs", JUNGLE_PLANKS);
	public static final Block COMMAND_BLOCK = register(
		"command_block",
		settings -> new CommandBlock(false, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.BROWN).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing()
	);
	public static final Block BEACON = register(
		"beacon",
		BeaconBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DIAMOND_BLUE)
			.instrument(NoteBlockInstrument.HAT)
			.strength(3.0F)
			.luminance(state -> 15)
			.nonOpaque()
			.solidBlock(Blocks::never)
	);
	public static final Block COBBLESTONE_WALL = register("cobblestone_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(COBBLESTONE).solid());
	public static final Block MOSSY_COBBLESTONE_WALL = register("mossy_cobblestone_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(COBBLESTONE).solid());
	public static final Block FLOWER_POT = register("flower_pot", settings -> new FlowerPotBlock(AIR, settings), createFlowerPotSettings());
	public static final Block POTTED_TORCHFLOWER = register("potted_torchflower", settings -> new FlowerPotBlock(TORCHFLOWER, settings), createFlowerPotSettings());
	public static final Block POTTED_OAK_SAPLING = register("potted_oak_sapling", settings -> new FlowerPotBlock(OAK_SAPLING, settings), createFlowerPotSettings());
	public static final Block POTTED_SPRUCE_SAPLING = register(
		"potted_spruce_sapling", settings -> new FlowerPotBlock(SPRUCE_SAPLING, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_BIRCH_SAPLING = register(
		"potted_birch_sapling", settings -> new FlowerPotBlock(BIRCH_SAPLING, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_JUNGLE_SAPLING = register(
		"potted_jungle_sapling", settings -> new FlowerPotBlock(JUNGLE_SAPLING, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_ACACIA_SAPLING = register(
		"potted_acacia_sapling", settings -> new FlowerPotBlock(ACACIA_SAPLING, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_CHERRY_SAPLING = register(
		"potted_cherry_sapling", settings -> new FlowerPotBlock(CHERRY_SAPLING, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_DARK_OAK_SAPLING = register(
		"potted_dark_oak_sapling", settings -> new FlowerPotBlock(DARK_OAK_SAPLING, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_PALE_OAK_SAPLING = register(
		"potted_pale_oak_sapling", settings -> new FlowerPotBlock(PALE_OAK_SAPLING, settings), createFlowerPotSettings().requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block POTTED_MANGROVE_PROPAGULE = register(
		"potted_mangrove_propagule", settings -> new FlowerPotBlock(MANGROVE_PROPAGULE, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_FERN = register("potted_fern", settings -> new FlowerPotBlock(FERN, settings), createFlowerPotSettings());
	public static final Block POTTED_DANDELION = register("potted_dandelion", settings -> new FlowerPotBlock(DANDELION, settings), createFlowerPotSettings());
	public static final Block POTTED_POPPY = register("potted_poppy", settings -> new FlowerPotBlock(POPPY, settings), createFlowerPotSettings());
	public static final Block POTTED_BLUE_ORCHID = register("potted_blue_orchid", settings -> new FlowerPotBlock(BLUE_ORCHID, settings), createFlowerPotSettings());
	public static final Block POTTED_ALLIUM = register("potted_allium", settings -> new FlowerPotBlock(ALLIUM, settings), createFlowerPotSettings());
	public static final Block POTTED_AZURE_BLUET = register("potted_azure_bluet", settings -> new FlowerPotBlock(AZURE_BLUET, settings), createFlowerPotSettings());
	public static final Block POTTED_RED_TULIP = register("potted_red_tulip", settings -> new FlowerPotBlock(RED_TULIP, settings), createFlowerPotSettings());
	public static final Block POTTED_ORANGE_TULIP = register(
		"potted_orange_tulip", settings -> new FlowerPotBlock(ORANGE_TULIP, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_WHITE_TULIP = register("potted_white_tulip", settings -> new FlowerPotBlock(WHITE_TULIP, settings), createFlowerPotSettings());
	public static final Block POTTED_PINK_TULIP = register("potted_pink_tulip", settings -> new FlowerPotBlock(PINK_TULIP, settings), createFlowerPotSettings());
	public static final Block POTTED_OXEYE_DAISY = register("potted_oxeye_daisy", settings -> new FlowerPotBlock(OXEYE_DAISY, settings), createFlowerPotSettings());
	public static final Block POTTED_CORNFLOWER = register("potted_cornflower", settings -> new FlowerPotBlock(CORNFLOWER, settings), createFlowerPotSettings());
	public static final Block POTTED_LILY_OF_THE_VALLEY = register(
		"potted_lily_of_the_valley", settings -> new FlowerPotBlock(LILY_OF_THE_VALLEY, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_WITHER_ROSE = register("potted_wither_rose", settings -> new FlowerPotBlock(WITHER_ROSE, settings), createFlowerPotSettings());
	public static final Block POTTED_RED_MUSHROOM = register(
		"potted_red_mushroom", settings -> new FlowerPotBlock(RED_MUSHROOM, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_BROWN_MUSHROOM = register(
		"potted_brown_mushroom", settings -> new FlowerPotBlock(BROWN_MUSHROOM, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_DEAD_BUSH = register("potted_dead_bush", settings -> new FlowerPotBlock(DEAD_BUSH, settings), createFlowerPotSettings());
	public static final Block POTTED_CACTUS = register("potted_cactus", settings -> new FlowerPotBlock(CACTUS, settings), createFlowerPotSettings());
	public static final Block CARROTS = register(
		"carrots",
		CarrotsBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.CROP)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block POTATOES = register(
		"potatoes",
		PotatoesBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.CROP)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block OAK_BUTTON = register("oak_button", settings -> new ButtonBlock(BlockSetType.OAK, 30, settings), createButtonSettings());
	public static final Block SPRUCE_BUTTON = register("spruce_button", settings -> new ButtonBlock(BlockSetType.SPRUCE, 30, settings), createButtonSettings());
	public static final Block BIRCH_BUTTON = register("birch_button", settings -> new ButtonBlock(BlockSetType.BIRCH, 30, settings), createButtonSettings());
	public static final Block JUNGLE_BUTTON = register("jungle_button", settings -> new ButtonBlock(BlockSetType.JUNGLE, 30, settings), createButtonSettings());
	public static final Block ACACIA_BUTTON = register("acacia_button", settings -> new ButtonBlock(BlockSetType.ACACIA, 30, settings), createButtonSettings());
	public static final Block CHERRY_BUTTON = register("cherry_button", settings -> new ButtonBlock(BlockSetType.CHERRY, 30, settings), createButtonSettings());
	public static final Block DARK_OAK_BUTTON = register(
		"dark_oak_button", settings -> new ButtonBlock(BlockSetType.DARK_OAK, 30, settings), createButtonSettings()
	);
	public static final Block PALE_OAK_BUTTON = register(
		"pale_oak_button", settings -> new ButtonBlock(BlockSetType.PALE_OAK, 30, settings), createButtonSettings().requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_BUTTON = register(
		"mangrove_button", settings -> new ButtonBlock(BlockSetType.MANGROVE, 30, settings), createButtonSettings()
	);
	public static final Block BAMBOO_BUTTON = register("bamboo_button", settings -> new ButtonBlock(BlockSetType.BAMBOO, 30, settings), createButtonSettings());
	public static final Block SKELETON_SKULL = register(
		"skeleton_skull",
		settings -> new SkullBlock(SkullBlock.Type.SKELETON, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SKELETON_WALL_SKULL = register(
		"skeleton_wall_skull",
		settings -> new WallSkullBlock(SkullBlock.Type.SKELETON, settings),
		copyLootTable(SKELETON_SKULL, true).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WITHER_SKELETON_SKULL = register(
		"wither_skeleton_skull",
		WitherSkullBlock::new,
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.WITHER_SKELETON).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WITHER_SKELETON_WALL_SKULL = register(
		"wither_skeleton_wall_skull", WallWitherSkullBlock::new, copyLootTable(WITHER_SKELETON_SKULL, true).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ZOMBIE_HEAD = register(
		"zombie_head",
		settings -> new SkullBlock(SkullBlock.Type.ZOMBIE, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.ZOMBIE).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ZOMBIE_WALL_HEAD = register(
		"zombie_wall_head",
		settings -> new WallSkullBlock(SkullBlock.Type.ZOMBIE, settings),
		copyLootTable(ZOMBIE_HEAD, true).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PLAYER_HEAD = register(
		"player_head",
		PlayerSkullBlock::new,
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.CUSTOM_HEAD).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PLAYER_WALL_HEAD = register(
		"player_wall_head", WallPlayerSkullBlock::new, copyLootTable(PLAYER_HEAD, true).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CREEPER_HEAD = register(
		"creeper_head",
		settings -> new SkullBlock(SkullBlock.Type.CREEPER, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CREEPER_WALL_HEAD = register(
		"creeper_wall_head",
		settings -> new WallSkullBlock(SkullBlock.Type.CREEPER, settings),
		copyLootTable(CREEPER_HEAD, true).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DRAGON_HEAD = register(
		"dragon_head",
		settings -> new SkullBlock(SkullBlock.Type.DRAGON, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.DRAGON).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DRAGON_WALL_HEAD = register(
		"dragon_wall_head",
		settings -> new WallSkullBlock(SkullBlock.Type.DRAGON, settings),
		copyLootTable(DRAGON_HEAD, true).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PIGLIN_HEAD = register(
		"piglin_head",
		settings -> new SkullBlock(SkullBlock.Type.PIGLIN, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.PIGLIN).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PIGLIN_WALL_HEAD = register(
		"piglin_wall_head", WallPiglinHeadBlock::new, copyLootTable(PIGLIN_HEAD, true).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ANVIL = register(
		"anvil",
		AnvilBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.IRON_GRAY)
			.requiresTool()
			.strength(5.0F, 1200.0F)
			.sounds(BlockSoundGroup.ANVIL)
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block CHIPPED_ANVIL = register(
		"chipped_anvil",
		AnvilBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.IRON_GRAY)
			.requiresTool()
			.strength(5.0F, 1200.0F)
			.sounds(BlockSoundGroup.ANVIL)
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block DAMAGED_ANVIL = register(
		"damaged_anvil",
		AnvilBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.IRON_GRAY)
			.requiresTool()
			.strength(5.0F, 1200.0F)
			.sounds(BlockSoundGroup.ANVIL)
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block TRAPPED_CHEST = register(
		"trapped_chest",
		TrappedChestBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block LIGHT_WEIGHTED_PRESSURE_PLATE = register(
		"light_weighted_pressure_plate",
		settings -> new WeightedPressurePlateBlock(15, BlockSetType.GOLD, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.GOLD).solid().requiresTool().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block HEAVY_WEIGHTED_PRESSURE_PLATE = register(
		"heavy_weighted_pressure_plate",
		settings -> new WeightedPressurePlateBlock(150, BlockSetType.IRON, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).solid().requiresTool().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block COMPARATOR = register(
		"comparator", ComparatorBlock::new, AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.STONE).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DAYLIGHT_DETECTOR = register(
		"daylight_detector",
		DaylightDetectorBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block REDSTONE_BLOCK = register(
		"redstone_block",
		RedstoneBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_RED).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).solidBlock(Blocks::never)
	);
	public static final Block NETHER_QUARTZ_ORE = register(
		"nether_quartz_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(2, 5), settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(3.0F, 3.0F)
			.sounds(BlockSoundGroup.NETHER_ORE)
	);
	public static final Block HOPPER = register(
		"hopper",
		HopperBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(3.0F, 4.8F).sounds(BlockSoundGroup.METAL).nonOpaque()
	);
	public static final Block QUARTZ_BLOCK = register(
		"quartz_block", AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(0.8F)
	);
	public static final Block CHISELED_QUARTZ_BLOCK = register(
		"chiseled_quartz_block", AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(0.8F)
	);
	public static final Block QUARTZ_PILLAR = register(
		"quartz_pillar",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(0.8F)
	);
	public static final Block QUARTZ_STAIRS = registerOldStairsBlock("quartz_stairs", QUARTZ_BLOCK);
	public static final Block ACTIVATOR_RAIL = register(
		"activator_rail", PoweredRailBlock::new, AbstractBlock.Settings.create().noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL)
	);
	public static final Block DROPPER = register(
		"dropper",
		DropperBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5F)
	);
	public static final Block WHITE_TERRACOTTA = register(
		"white_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block ORANGE_TERRACOTTA = register(
		"orange_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block MAGENTA_TERRACOTTA = register(
		"magenta_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block LIGHT_BLUE_TERRACOTTA = register(
		"light_blue_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block YELLOW_TERRACOTTA = register(
		"yellow_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block LIME_TERRACOTTA = register(
		"lime_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_LIME).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block PINK_TERRACOTTA = register(
		"pink_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_PINK).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block GRAY_TERRACOTTA = register(
		"gray_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block LIGHT_GRAY_TERRACOTTA = register(
		"light_gray_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block CYAN_TERRACOTTA = register(
		"cyan_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block PURPLE_TERRACOTTA = register(
		"purple_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_PURPLE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block BLUE_TERRACOTTA = register(
		"blue_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block BROWN_TERRACOTTA = register(
		"brown_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_BROWN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block GREEN_TERRACOTTA = register(
		"green_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_GREEN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block RED_TERRACOTTA = register(
		"red_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block BLACK_TERRACOTTA = register(
		"black_terracotta",
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block WHITE_STAINED_GLASS_PANE = register(
		"white_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.WHITE, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block ORANGE_STAINED_GLASS_PANE = register(
		"orange_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.ORANGE, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block MAGENTA_STAINED_GLASS_PANE = register(
		"magenta_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.MAGENTA, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block LIGHT_BLUE_STAINED_GLASS_PANE = register(
		"light_blue_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.LIGHT_BLUE, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block YELLOW_STAINED_GLASS_PANE = register(
		"yellow_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.YELLOW, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block LIME_STAINED_GLASS_PANE = register(
		"lime_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.LIME, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block PINK_STAINED_GLASS_PANE = register(
		"pink_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.PINK, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block GRAY_STAINED_GLASS_PANE = register(
		"gray_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.GRAY, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block LIGHT_GRAY_STAINED_GLASS_PANE = register(
		"light_gray_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.LIGHT_GRAY, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block CYAN_STAINED_GLASS_PANE = register(
		"cyan_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.CYAN, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block PURPLE_STAINED_GLASS_PANE = register(
		"purple_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.PURPLE, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block BLUE_STAINED_GLASS_PANE = register(
		"blue_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.BLUE, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block BROWN_STAINED_GLASS_PANE = register(
		"brown_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.BROWN, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block GREEN_STAINED_GLASS_PANE = register(
		"green_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.GREEN, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block RED_STAINED_GLASS_PANE = register(
		"red_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.RED, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block BLACK_STAINED_GLASS_PANE = register(
		"black_stained_glass_pane",
		settings -> new StainedGlassPaneBlock(DyeColor.BLACK, settings),
		AbstractBlock.Settings.create().instrument(NoteBlockInstrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
	);
	public static final Block ACACIA_STAIRS = registerOldStairsBlock("acacia_stairs", ACACIA_PLANKS);
	public static final Block CHERRY_STAIRS = registerOldStairsBlock("cherry_stairs", CHERRY_PLANKS);
	public static final Block DARK_OAK_STAIRS = registerOldStairsBlock("dark_oak_stairs", DARK_OAK_PLANKS);
	public static final Block PALE_OAK_STAIRS = registerOldStairsBlock("pale_oak_stairs", PALE_OAK_PLANKS);
	public static final Block MANGROVE_STAIRS = registerOldStairsBlock("mangrove_stairs", MANGROVE_PLANKS);
	public static final Block BAMBOO_STAIRS = registerOldStairsBlock("bamboo_stairs", BAMBOO_PLANKS);
	public static final Block BAMBOO_MOSAIC_STAIRS = registerOldStairsBlock("bamboo_mosaic_stairs", BAMBOO_MOSAIC);
	public static final Block SLIME_BLOCK = register(
		"slime_block", SlimeBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.PALE_GREEN).slipperiness(0.8F).sounds(BlockSoundGroup.SLIME).nonOpaque()
	);
	public static final Block BARRIER = register(
		"barrier",
		BarrierBlock::new,
		AbstractBlock.Settings.create()
			.strength(-1.0F, 3600000.8F)
			.mapColor(createMapColorFromWaterloggedBlockState(MapColor.CLEAR))
			.dropsNothing()
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.noBlockBreakParticles()
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block LIGHT = register(
		"light",
		LightBlock::new,
		AbstractBlock.Settings.create()
			.replaceable()
			.strength(-1.0F, 3600000.8F)
			.mapColor(createMapColorFromWaterloggedBlockState(MapColor.CLEAR))
			.dropsNothing()
			.nonOpaque()
			.luminance(LightBlock.STATE_TO_LUMINANCE)
	);
	public static final Block IRON_TRAPDOOR = register(
		"iron_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.IRON, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0F).nonOpaque().allowsSpawning(Blocks::never)
	);
	public static final Block PRISMARINE = register(
		"prismarine", AbstractBlock.Settings.create().mapColor(MapColor.CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block PRISMARINE_BRICKS = register(
		"prismarine_bricks",
		AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block DARK_PRISMARINE = register(
		"dark_prismarine",
		AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block PRISMARINE_STAIRS = registerOldStairsBlock("prismarine_stairs", PRISMARINE);
	public static final Block PRISMARINE_BRICK_STAIRS = registerOldStairsBlock("prismarine_brick_stairs", PRISMARINE_BRICKS);
	public static final Block DARK_PRISMARINE_STAIRS = registerOldStairsBlock("dark_prismarine_stairs", DARK_PRISMARINE);
	public static final Block PRISMARINE_SLAB = register(
		"prismarine_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block PRISMARINE_BRICK_SLAB = register(
		"prismarine_brick_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block DARK_PRISMARINE_SLAB = register(
		"dark_prismarine_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block SEA_LANTERN = register(
		"sea_lantern",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OFF_WHITE)
			.instrument(NoteBlockInstrument.HAT)
			.strength(0.3F)
			.sounds(BlockSoundGroup.GLASS)
			.luminance(state -> 15)
			.solidBlock(Blocks::never)
	);
	public static final Block HAY_BLOCK = register(
		"hay_block",
		HayBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(NoteBlockInstrument.BANJO).strength(0.5F).sounds(BlockSoundGroup.GRASS)
	);
	public static final Block WHITE_CARPET = register(
		"white_carpet",
		settings -> new DyedCarpetBlock(DyeColor.WHITE, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block ORANGE_CARPET = register(
		"orange_carpet",
		settings -> new DyedCarpetBlock(DyeColor.ORANGE, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block MAGENTA_CARPET = register(
		"magenta_carpet",
		settings -> new DyedCarpetBlock(DyeColor.MAGENTA, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block LIGHT_BLUE_CARPET = register(
		"light_blue_carpet",
		settings -> new DyedCarpetBlock(DyeColor.LIGHT_BLUE, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block YELLOW_CARPET = register(
		"yellow_carpet",
		settings -> new DyedCarpetBlock(DyeColor.YELLOW, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block LIME_CARPET = register(
		"lime_carpet",
		settings -> new DyedCarpetBlock(DyeColor.LIME, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.LIME).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block PINK_CARPET = register(
		"pink_carpet",
		settings -> new DyedCarpetBlock(DyeColor.PINK, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.PINK).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block GRAY_CARPET = register(
		"gray_carpet",
		settings -> new DyedCarpetBlock(DyeColor.GRAY, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block LIGHT_GRAY_CARPET = register(
		"light_gray_carpet",
		settings -> new DyedCarpetBlock(DyeColor.LIGHT_GRAY, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block CYAN_CARPET = register(
		"cyan_carpet",
		settings -> new DyedCarpetBlock(DyeColor.CYAN, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.CYAN).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block PURPLE_CARPET = register(
		"purple_carpet",
		settings -> new DyedCarpetBlock(DyeColor.PURPLE, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block BLUE_CARPET = register(
		"blue_carpet",
		settings -> new DyedCarpetBlock(DyeColor.BLUE, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block BROWN_CARPET = register(
		"brown_carpet",
		settings -> new DyedCarpetBlock(DyeColor.BROWN, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block GREEN_CARPET = register(
		"green_carpet",
		settings -> new DyedCarpetBlock(DyeColor.GREEN, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block RED_CARPET = register(
		"red_carpet",
		settings -> new DyedCarpetBlock(DyeColor.RED, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block BLACK_CARPET = register(
		"black_carpet",
		settings -> new DyedCarpetBlock(DyeColor.BLACK, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable()
	);
	public static final Block TERRACOTTA = register(
		"terracotta", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F)
	);
	public static final Block COAL_BLOCK = register(
		"coal_block", AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F)
	);
	public static final Block PACKED_ICE = register(
		"packed_ice",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_PURPLE)
			.instrument(NoteBlockInstrument.CHIME)
			.slipperiness(0.98F)
			.strength(0.5F)
			.sounds(BlockSoundGroup.GLASS)
	);
	public static final Block SUNFLOWER = register(
		"sunflower",
		TallFlowerBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block LILAC = register(
		"lilac",
		TallFlowerBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ROSE_BUSH = register(
		"rose_bush",
		TallFlowerBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PEONY = register(
		"peony",
		TallFlowerBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block TALL_GRASS = register(
		"tall_grass",
		TallPlantBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block LARGE_FERN = register(
		"large_fern",
		TallPlantBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.GRASS)
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WHITE_BANNER = register(
		"white_banner",
		settings -> new BannerBlock(DyeColor.WHITE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block ORANGE_BANNER = register(
		"orange_banner",
		settings -> new BannerBlock(DyeColor.ORANGE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block MAGENTA_BANNER = register(
		"magenta_banner",
		settings -> new BannerBlock(DyeColor.MAGENTA, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block LIGHT_BLUE_BANNER = register(
		"light_blue_banner",
		settings -> new BannerBlock(DyeColor.LIGHT_BLUE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block YELLOW_BANNER = register(
		"yellow_banner",
		settings -> new BannerBlock(DyeColor.YELLOW, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block LIME_BANNER = register(
		"lime_banner",
		settings -> new BannerBlock(DyeColor.LIME, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block PINK_BANNER = register(
		"pink_banner",
		settings -> new BannerBlock(DyeColor.PINK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block GRAY_BANNER = register(
		"gray_banner",
		settings -> new BannerBlock(DyeColor.GRAY, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block LIGHT_GRAY_BANNER = register(
		"light_gray_banner",
		settings -> new BannerBlock(DyeColor.LIGHT_GRAY, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block CYAN_BANNER = register(
		"cyan_banner",
		settings -> new BannerBlock(DyeColor.CYAN, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block PURPLE_BANNER = register(
		"purple_banner",
		settings -> new BannerBlock(DyeColor.PURPLE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block BLUE_BANNER = register(
		"blue_banner",
		settings -> new BannerBlock(DyeColor.BLUE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block BROWN_BANNER = register(
		"brown_banner",
		settings -> new BannerBlock(DyeColor.BROWN, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block GREEN_BANNER = register(
		"green_banner",
		settings -> new BannerBlock(DyeColor.GREEN, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block RED_BANNER = register(
		"red_banner",
		settings -> new BannerBlock(DyeColor.RED, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block BLACK_BANNER = register(
		"black_banner",
		settings -> new BannerBlock(DyeColor.BLACK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block WHITE_WALL_BANNER = register(
		"white_wall_banner",
		settings -> new WallBannerBlock(DyeColor.WHITE, settings),
		copyLootTable(WHITE_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block ORANGE_WALL_BANNER = register(
		"orange_wall_banner",
		settings -> new WallBannerBlock(DyeColor.ORANGE, settings),
		copyLootTable(ORANGE_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block MAGENTA_WALL_BANNER = register(
		"magenta_wall_banner",
		settings -> new WallBannerBlock(DyeColor.MAGENTA, settings),
		copyLootTable(MAGENTA_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block LIGHT_BLUE_WALL_BANNER = register(
		"light_blue_wall_banner",
		settings -> new WallBannerBlock(DyeColor.LIGHT_BLUE, settings),
		copyLootTable(LIGHT_BLUE_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block YELLOW_WALL_BANNER = register(
		"yellow_wall_banner",
		settings -> new WallBannerBlock(DyeColor.YELLOW, settings),
		copyLootTable(YELLOW_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block LIME_WALL_BANNER = register(
		"lime_wall_banner",
		settings -> new WallBannerBlock(DyeColor.LIME, settings),
		copyLootTable(LIME_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block PINK_WALL_BANNER = register(
		"pink_wall_banner",
		settings -> new WallBannerBlock(DyeColor.PINK, settings),
		copyLootTable(PINK_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block GRAY_WALL_BANNER = register(
		"gray_wall_banner",
		settings -> new WallBannerBlock(DyeColor.GRAY, settings),
		copyLootTable(GRAY_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block LIGHT_GRAY_WALL_BANNER = register(
		"light_gray_wall_banner",
		settings -> new WallBannerBlock(DyeColor.LIGHT_GRAY, settings),
		copyLootTable(LIGHT_GRAY_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block CYAN_WALL_BANNER = register(
		"cyan_wall_banner",
		settings -> new WallBannerBlock(DyeColor.CYAN, settings),
		copyLootTable(CYAN_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block PURPLE_WALL_BANNER = register(
		"purple_wall_banner",
		settings -> new WallBannerBlock(DyeColor.PURPLE, settings),
		copyLootTable(PURPLE_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block BLUE_WALL_BANNER = register(
		"blue_wall_banner",
		settings -> new WallBannerBlock(DyeColor.BLUE, settings),
		copyLootTable(BLUE_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block BROWN_WALL_BANNER = register(
		"brown_wall_banner",
		settings -> new WallBannerBlock(DyeColor.BROWN, settings),
		copyLootTable(BROWN_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block GREEN_WALL_BANNER = register(
		"green_wall_banner",
		settings -> new WallBannerBlock(DyeColor.GREEN, settings),
		copyLootTable(GREEN_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block RED_WALL_BANNER = register(
		"red_wall_banner",
		settings -> new WallBannerBlock(DyeColor.RED, settings),
		copyLootTable(RED_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block BLACK_WALL_BANNER = register(
		"black_wall_banner",
		settings -> new WallBannerBlock(DyeColor.BLACK, settings),
		copyLootTable(BLACK_BANNER, true)
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block RED_SANDSTONE = register(
		"red_sandstone", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(0.8F)
	);
	public static final Block CHISELED_RED_SANDSTONE = register(
		"chiseled_red_sandstone", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(0.8F)
	);
	public static final Block CUT_RED_SANDSTONE = register(
		"cut_red_sandstone", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(0.8F)
	);
	public static final Block RED_SANDSTONE_STAIRS = registerOldStairsBlock("red_sandstone_stairs", RED_SANDSTONE);
	public static final Block OAK_SLAB = register(
		"oak_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block SPRUCE_SLAB = register(
		"spruce_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.SPRUCE_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block BIRCH_SLAB = register(
		"birch_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_YELLOW)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block JUNGLE_SLAB = register(
		"jungle_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DIRT_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
	);
	public static final Block ACACIA_SLAB = register(
		"acacia_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block CHERRY_SLAB = register(
		"cherry_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_WHITE)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.CHERRY_WOOD)
			.burnable()
	);
	public static final Block DARK_OAK_SLAB = register(
		"dark_oak_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block PALE_OAK_SLAB = register(
		"pale_oak_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.WOOD)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_SLAB = register(
		"mangrove_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block BAMBOO_SLAB = register(
		"bamboo_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.YELLOW)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.BAMBOO_WOOD)
			.burnable()
	);
	public static final Block BAMBOO_MOSAIC_SLAB = register(
		"bamboo_mosaic_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.YELLOW)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.BAMBOO_WOOD)
			.burnable()
	);
	public static final Block STONE_SLAB = register(
		"stone_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block SMOOTH_STONE_SLAB = register(
		"smooth_stone_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block SANDSTONE_SLAB = register(
		"sandstone_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block CUT_SANDSTONE_SLAB = register(
		"cut_sandstone_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block PETRIFIED_OAK_SLAB = register(
		"petrified_oak_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block COBBLESTONE_SLAB = register(
		"cobblestone_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block BRICK_SLAB = register(
		"brick_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block STONE_BRICK_SLAB = register(
		"stone_brick_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block MUD_BRICK_SLAB = register(
		"mud_brick_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.5F, 3.0F)
			.sounds(BlockSoundGroup.MUD_BRICKS)
	);
	public static final Block NETHER_BRICK_SLAB = register(
		"nether_brick_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(2.0F, 6.0F)
			.sounds(BlockSoundGroup.NETHER_BRICKS)
	);
	public static final Block QUARTZ_SLAB = register(
		"quartz_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block RED_SANDSTONE_SLAB = register(
		"red_sandstone_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block CUT_RED_SANDSTONE_SLAB = register(
		"cut_red_sandstone_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block PURPUR_SLAB = register(
		"purpur_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block SMOOTH_STONE = register(
		"smooth_stone", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block SMOOTH_SANDSTONE = register(
		"smooth_sandstone",
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block SMOOTH_QUARTZ = register(
		"smooth_quartz", AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block SMOOTH_RED_SANDSTONE = register(
		"smooth_red_sandstone",
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F)
	);
	public static final Block SPRUCE_FENCE_GATE = register(
		"spruce_fence_gate",
		settings -> new FenceGateBlock(WoodType.SPRUCE, settings),
		AbstractBlock.Settings.create().mapColor(SPRUCE_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
	);
	public static final Block BIRCH_FENCE_GATE = register(
		"birch_fence_gate",
		settings -> new FenceGateBlock(WoodType.BIRCH, settings),
		AbstractBlock.Settings.create().mapColor(BIRCH_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
	);
	public static final Block JUNGLE_FENCE_GATE = register(
		"jungle_fence_gate",
		settings -> new FenceGateBlock(WoodType.JUNGLE, settings),
		AbstractBlock.Settings.create().mapColor(JUNGLE_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
	);
	public static final Block ACACIA_FENCE_GATE = register(
		"acacia_fence_gate",
		settings -> new FenceGateBlock(WoodType.ACACIA, settings),
		AbstractBlock.Settings.create().mapColor(ACACIA_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
	);
	public static final Block CHERRY_FENCE_GATE = register(
		"cherry_fence_gate",
		settings -> new FenceGateBlock(WoodType.CHERRY, settings),
		AbstractBlock.Settings.create().mapColor(CHERRY_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
	);
	public static final Block DARK_OAK_FENCE_GATE = register(
		"dark_oak_fence_gate",
		settings -> new FenceGateBlock(WoodType.DARK_OAK, settings),
		AbstractBlock.Settings.create().mapColor(DARK_OAK_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
	);
	public static final Block PALE_OAK_FENCE_GATE = register(
		"pale_oak_fence_gate",
		settings -> new FenceGateBlock(WoodType.PALE_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.burnable()
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_FENCE_GATE = register(
		"mangrove_fence_gate",
		settings -> new FenceGateBlock(WoodType.MANGROVE, settings),
		AbstractBlock.Settings.create().mapColor(MANGROVE_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
	);
	public static final Block BAMBOO_FENCE_GATE = register(
		"bamboo_fence_gate",
		settings -> new FenceGateBlock(WoodType.BAMBOO, settings),
		AbstractBlock.Settings.create().mapColor(BAMBOO_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
	);
	public static final Block SPRUCE_FENCE = register(
		"spruce_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(SPRUCE_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.burnable()
			.sounds(BlockSoundGroup.WOOD)
	);
	public static final Block BIRCH_FENCE = register(
		"birch_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(BIRCH_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.burnable()
			.sounds(BlockSoundGroup.WOOD)
	);
	public static final Block JUNGLE_FENCE = register(
		"jungle_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(JUNGLE_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.burnable()
			.sounds(BlockSoundGroup.WOOD)
	);
	public static final Block ACACIA_FENCE = register(
		"acacia_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(ACACIA_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.burnable()
			.sounds(BlockSoundGroup.WOOD)
	);
	public static final Block CHERRY_FENCE = register(
		"cherry_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(CHERRY_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.burnable()
			.sounds(BlockSoundGroup.CHERRY_WOOD)
	);
	public static final Block DARK_OAK_FENCE = register(
		"dark_oak_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DARK_OAK_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.burnable()
			.sounds(BlockSoundGroup.WOOD)
	);
	public static final Block PALE_OAK_FENCE = register(
		"pale_oak_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.burnable()
			.sounds(BlockSoundGroup.WOOD)
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_FENCE = register(
		"mangrove_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MANGROVE_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.burnable()
			.sounds(BlockSoundGroup.WOOD)
	);
	public static final Block BAMBOO_FENCE = register(
		"bamboo_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(BAMBOO_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.BAMBOO_WOOD)
			.burnable()
	);
	public static final Block SPRUCE_DOOR = register(
		"spruce_door",
		settings -> new DoorBlock(BlockSetType.SPRUCE, settings),
		AbstractBlock.Settings.create()
			.mapColor(SPRUCE_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BIRCH_DOOR = register(
		"birch_door",
		settings -> new DoorBlock(BlockSetType.BIRCH, settings),
		AbstractBlock.Settings.create()
			.mapColor(BIRCH_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block JUNGLE_DOOR = register(
		"jungle_door",
		settings -> new DoorBlock(BlockSetType.JUNGLE, settings),
		AbstractBlock.Settings.create()
			.mapColor(JUNGLE_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ACACIA_DOOR = register(
		"acacia_door",
		settings -> new DoorBlock(BlockSetType.ACACIA, settings),
		AbstractBlock.Settings.create()
			.mapColor(ACACIA_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CHERRY_DOOR = register(
		"cherry_door",
		settings -> new DoorBlock(BlockSetType.CHERRY, settings),
		AbstractBlock.Settings.create()
			.mapColor(CHERRY_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DARK_OAK_DOOR = register(
		"dark_oak_door",
		settings -> new DoorBlock(BlockSetType.DARK_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(DARK_OAK_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PALE_OAK_DOOR = register(
		"pale_oak_door",
		settings -> new DoorBlock(BlockSetType.PALE_OAK, settings),
		AbstractBlock.Settings.create()
			.mapColor(PALE_OAK_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block MANGROVE_DOOR = register(
		"mangrove_door",
		settings -> new DoorBlock(BlockSetType.MANGROVE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MANGROVE_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BAMBOO_DOOR = register(
		"bamboo_door",
		settings -> new DoorBlock(BlockSetType.BAMBOO, settings),
		AbstractBlock.Settings.create()
			.mapColor(BAMBOO_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block END_ROD = register(
		"end_rod", EndRodBlock::new, AbstractBlock.Settings.create().notSolid().breakInstantly().luminance(state -> 14).sounds(BlockSoundGroup.WOOD).nonOpaque()
	);
	public static final Block CHORUS_PLANT = register(
		"chorus_plant",
		ChorusPlantBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PURPLE)
			.notSolid()
			.strength(0.4F)
			.sounds(BlockSoundGroup.WOOD)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CHORUS_FLOWER = register(
		"chorus_flower",
		settings -> new ChorusFlowerBlock(CHORUS_PLANT, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PURPLE)
			.notSolid()
			.ticksRandomly()
			.strength(0.4F)
			.sounds(BlockSoundGroup.WOOD)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.pistonBehavior(PistonBehavior.DESTROY)
			.solidBlock(Blocks::never)
	);
	public static final Block PURPUR_BLOCK = register(
		"purpur_block", AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block PURPUR_PILLAR = register(
		"purpur_pillar",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block PURPUR_STAIRS = registerOldStairsBlock("purpur_stairs", PURPUR_BLOCK);
	public static final Block END_STONE_BRICKS = register(
		"end_stone_bricks",
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0F, 9.0F)
	);
	public static final Block TORCHFLOWER_CROP = register(
		"torchflower_crop",
		TorchflowerBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.CROP)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PITCHER_CROP = register(
		"pitcher_crop",
		PitcherCropBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.CROP)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PITCHER_PLANT = register(
		"pitcher_plant",
		TallPlantBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.CROP)
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BEETROOTS = register(
		"beetroots",
		BeetrootsBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.CROP)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DIRT_PATH = register(
		"dirt_path",
		DirtPathBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DIRT_BROWN)
			.strength(0.65F)
			.sounds(BlockSoundGroup.GRASS)
			.blockVision(Blocks::always)
			.suffocates(Blocks::always)
	);
	public static final Block END_GATEWAY = register(
		"end_gateway",
		EndGatewayBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLACK)
			.noCollision()
			.luminance(state -> 15)
			.strength(-1.0F, 3600000.0F)
			.dropsNothing()
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block REPEATING_COMMAND_BLOCK = register(
		"repeating_command_block",
		settings -> new CommandBlock(false, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing()
	);
	public static final Block CHAIN_COMMAND_BLOCK = register(
		"chain_command_block",
		settings -> new CommandBlock(true, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.GREEN).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing()
	);
	public static final Block FROSTED_ICE = register(
		"frosted_ice",
		FrostedIceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_PURPLE)
			.slipperiness(0.98F)
			.strength(0.5F)
			.sounds(BlockSoundGroup.GLASS)
			.nonOpaque()
			.allowsSpawning((state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR)
			.solidBlock(Blocks::never)
	);
	public static final Block MAGMA_BLOCK = register(
		"magma_block",
		MagmaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.luminance(state -> 3)
			.strength(0.5F)
			.allowsSpawning((state, world, pos, entityType) -> entityType.isFireImmune())
			.postProcess(Blocks::always)
			.emissiveLighting(Blocks::always)
	);
	public static final Block NETHER_WART_BLOCK = register(
		"nether_wart_block", AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(1.0F).sounds(BlockSoundGroup.WART_BLOCK)
	);
	public static final Block RED_NETHER_BRICKS = register(
		"red_nether_bricks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(2.0F, 6.0F)
			.sounds(BlockSoundGroup.NETHER_BRICKS)
	);
	public static final Block BONE_BLOCK = register(
		"bone_block",
		PillarBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_YELLOW)
			.instrument(NoteBlockInstrument.XYLOPHONE)
			.requiresTool()
			.strength(2.0F)
			.sounds(BlockSoundGroup.BONE)
	);
	public static final Block STRUCTURE_VOID = register(
		"structure_void",
		StructureVoidBlock::new,
		AbstractBlock.Settings.create().replaceable().noCollision().dropsNothing().noBlockBreakParticles().pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block OBSERVER = register(
		"observer",
		ObserverBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.strength(3.0F)
			.requiresTool()
			.solidBlock(Blocks::never)
	);
	public static final Block SHULKER_BOX = register("shulker_box", settings -> new ShulkerBoxBlock(null, settings), createShulkerBoxSettings(MapColor.PURPLE));
	public static final Block WHITE_SHULKER_BOX = register(
		"white_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.WHITE, settings), createShulkerBoxSettings(MapColor.WHITE)
	);
	public static final Block ORANGE_SHULKER_BOX = register(
		"orange_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.ORANGE, settings), createShulkerBoxSettings(MapColor.ORANGE)
	);
	public static final Block MAGENTA_SHULKER_BOX = register(
		"magenta_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.MAGENTA, settings), createShulkerBoxSettings(MapColor.MAGENTA)
	);
	public static final Block LIGHT_BLUE_SHULKER_BOX = register(
		"light_blue_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.LIGHT_BLUE, settings), createShulkerBoxSettings(MapColor.LIGHT_BLUE)
	);
	public static final Block YELLOW_SHULKER_BOX = register(
		"yellow_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.YELLOW, settings), createShulkerBoxSettings(MapColor.YELLOW)
	);
	public static final Block LIME_SHULKER_BOX = register(
		"lime_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.LIME, settings), createShulkerBoxSettings(MapColor.LIME)
	);
	public static final Block PINK_SHULKER_BOX = register(
		"pink_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.PINK, settings), createShulkerBoxSettings(MapColor.PINK)
	);
	public static final Block GRAY_SHULKER_BOX = register(
		"gray_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.GRAY, settings), createShulkerBoxSettings(MapColor.GRAY)
	);
	public static final Block LIGHT_GRAY_SHULKER_BOX = register(
		"light_gray_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.LIGHT_GRAY, settings), createShulkerBoxSettings(MapColor.LIGHT_GRAY)
	);
	public static final Block CYAN_SHULKER_BOX = register(
		"cyan_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.CYAN, settings), createShulkerBoxSettings(MapColor.CYAN)
	);
	public static final Block PURPLE_SHULKER_BOX = register(
		"purple_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.PURPLE, settings), createShulkerBoxSettings(MapColor.TERRACOTTA_PURPLE)
	);
	public static final Block BLUE_SHULKER_BOX = register(
		"blue_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.BLUE, settings), createShulkerBoxSettings(MapColor.BLUE)
	);
	public static final Block BROWN_SHULKER_BOX = register(
		"brown_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.BROWN, settings), createShulkerBoxSettings(MapColor.BROWN)
	);
	public static final Block GREEN_SHULKER_BOX = register(
		"green_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.GREEN, settings), createShulkerBoxSettings(MapColor.GREEN)
	);
	public static final Block RED_SHULKER_BOX = register(
		"red_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.RED, settings), createShulkerBoxSettings(MapColor.RED)
	);
	public static final Block BLACK_SHULKER_BOX = register(
		"black_shulker_box", settings -> new ShulkerBoxBlock(DyeColor.BLACK, settings), createShulkerBoxSettings(MapColor.BLACK)
	);
	public static final Block WHITE_GLAZED_TERRACOTTA = register(
		"white_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.WHITE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block ORANGE_GLAZED_TERRACOTTA = register(
		"orange_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.ORANGE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block MAGENTA_GLAZED_TERRACOTTA = register(
		"magenta_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.MAGENTA)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block LIGHT_BLUE_GLAZED_TERRACOTTA = register(
		"light_blue_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.LIGHT_BLUE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block YELLOW_GLAZED_TERRACOTTA = register(
		"yellow_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.YELLOW)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block LIME_GLAZED_TERRACOTTA = register(
		"lime_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.LIME)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block PINK_GLAZED_TERRACOTTA = register(
		"pink_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.PINK)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block GRAY_GLAZED_TERRACOTTA = register(
		"gray_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block LIGHT_GRAY_GLAZED_TERRACOTTA = register(
		"light_gray_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.LIGHT_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block CYAN_GLAZED_TERRACOTTA = register(
		"cyan_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.CYAN)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block PURPLE_GLAZED_TERRACOTTA = register(
		"purple_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.PURPLE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block BLUE_GLAZED_TERRACOTTA = register(
		"blue_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.BLUE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block BROWN_GLAZED_TERRACOTTA = register(
		"brown_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.BROWN)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block GREEN_GLAZED_TERRACOTTA = register(
		"green_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.GREEN)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block RED_GLAZED_TERRACOTTA = register(
		"red_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block BLACK_GLAZED_TERRACOTTA = register(
		"black_glazed_terracotta",
		GlazedTerracottaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(DyeColor.BLACK)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.4F)
			.pistonBehavior(PistonBehavior.PUSH_ONLY)
	);
	public static final Block WHITE_CONCRETE = register(
		"white_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block ORANGE_CONCRETE = register(
		"orange_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block MAGENTA_CONCRETE = register(
		"magenta_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block LIGHT_BLUE_CONCRETE = register(
		"light_blue_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block YELLOW_CONCRETE = register(
		"yellow_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block LIME_CONCRETE = register(
		"lime_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.LIME).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block PINK_CONCRETE = register(
		"pink_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.PINK).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block GRAY_CONCRETE = register(
		"gray_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block LIGHT_GRAY_CONCRETE = register(
		"light_gray_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block CYAN_CONCRETE = register(
		"cyan_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block PURPLE_CONCRETE = register(
		"purple_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.PURPLE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block BLUE_CONCRETE = register(
		"blue_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block BROWN_CONCRETE = register(
		"brown_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.BROWN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block GREEN_CONCRETE = register(
		"green_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.GREEN).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block RED_CONCRETE = register(
		"red_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block BLACK_CONCRETE = register(
		"black_concrete", AbstractBlock.Settings.create().mapColor(DyeColor.BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.8F)
	);
	public static final Block WHITE_CONCRETE_POWDER = register(
		"white_concrete_powder",
		settings -> new ConcretePowderBlock(WHITE_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block ORANGE_CONCRETE_POWDER = register(
		"orange_concrete_powder",
		settings -> new ConcretePowderBlock(ORANGE_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.ORANGE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block MAGENTA_CONCRETE_POWDER = register(
		"magenta_concrete_powder",
		settings -> new ConcretePowderBlock(MAGENTA_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.MAGENTA).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block LIGHT_BLUE_CONCRETE_POWDER = register(
		"light_blue_concrete_powder",
		settings -> new ConcretePowderBlock(LIGHT_BLUE_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_BLUE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block YELLOW_CONCRETE_POWDER = register(
		"yellow_concrete_powder",
		settings -> new ConcretePowderBlock(YELLOW_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.YELLOW).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block LIME_CONCRETE_POWDER = register(
		"lime_concrete_powder",
		settings -> new ConcretePowderBlock(LIME_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.LIME).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block PINK_CONCRETE_POWDER = register(
		"pink_concrete_powder",
		settings -> new ConcretePowderBlock(PINK_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.PINK).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block GRAY_CONCRETE_POWDER = register(
		"gray_concrete_powder",
		settings -> new ConcretePowderBlock(GRAY_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.GRAY).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block LIGHT_GRAY_CONCRETE_POWDER = register(
		"light_gray_concrete_powder",
		settings -> new ConcretePowderBlock(LIGHT_GRAY_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_GRAY).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block CYAN_CONCRETE_POWDER = register(
		"cyan_concrete_powder",
		settings -> new ConcretePowderBlock(CYAN_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.CYAN).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block PURPLE_CONCRETE_POWDER = register(
		"purple_concrete_powder",
		settings -> new ConcretePowderBlock(PURPLE_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.PURPLE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block BLUE_CONCRETE_POWDER = register(
		"blue_concrete_powder",
		settings -> new ConcretePowderBlock(BLUE_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.BLUE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block BROWN_CONCRETE_POWDER = register(
		"brown_concrete_powder",
		settings -> new ConcretePowderBlock(BROWN_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.BROWN).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block GREEN_CONCRETE_POWDER = register(
		"green_concrete_powder",
		settings -> new ConcretePowderBlock(GREEN_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.GREEN).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block RED_CONCRETE_POWDER = register(
		"red_concrete_powder",
		settings -> new ConcretePowderBlock(RED_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.RED).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block BLACK_CONCRETE_POWDER = register(
		"black_concrete_powder",
		settings -> new ConcretePowderBlock(BLACK_CONCRETE, settings),
		AbstractBlock.Settings.create().mapColor(DyeColor.BLACK).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
	);
	public static final Block KELP = register(
		"kelp",
		KelpBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WATER_BLUE)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block KELP_PLANT = register(
		"kelp_plant",
		KelpPlantBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WATER_BLUE)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DRIED_KELP_BLOCK = register(
		"dried_kelp_block", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.5F, 2.5F).sounds(BlockSoundGroup.GRASS)
	);
	public static final Block TURTLE_EGG = register(
		"turtle_egg",
		TurtleEggBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_YELLOW)
			.solid()
			.strength(0.5F)
			.sounds(BlockSoundGroup.METAL)
			.ticksRandomly()
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SNIFFER_EGG = register(
		"sniffer_egg", SnifferEggBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(0.5F).sounds(BlockSoundGroup.METAL).nonOpaque()
	);
	public static final Block DEAD_TUBE_CORAL_BLOCK = register(
		"dead_tube_coral_block",
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block DEAD_BRAIN_CORAL_BLOCK = register(
		"dead_brain_coral_block",
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block DEAD_BUBBLE_CORAL_BLOCK = register(
		"dead_bubble_coral_block",
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block DEAD_FIRE_CORAL_BLOCK = register(
		"dead_fire_coral_block",
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block DEAD_HORN_CORAL_BLOCK = register(
		"dead_horn_coral_block",
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block TUBE_CORAL_BLOCK = register(
		"tube_coral_block",
		settings -> new CoralBlockBlock(DEAD_TUBE_CORAL_BLOCK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLUE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.5F, 6.0F)
			.sounds(BlockSoundGroup.CORAL)
	);
	public static final Block BRAIN_CORAL_BLOCK = register(
		"brain_coral_block",
		settings -> new CoralBlockBlock(DEAD_BRAIN_CORAL_BLOCK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PINK)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.5F, 6.0F)
			.sounds(BlockSoundGroup.CORAL)
	);
	public static final Block BUBBLE_CORAL_BLOCK = register(
		"bubble_coral_block",
		settings -> new CoralBlockBlock(DEAD_BUBBLE_CORAL_BLOCK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PURPLE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.5F, 6.0F)
			.sounds(BlockSoundGroup.CORAL)
	);
	public static final Block FIRE_CORAL_BLOCK = register(
		"fire_coral_block",
		settings -> new CoralBlockBlock(DEAD_FIRE_CORAL_BLOCK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.5F, 6.0F)
			.sounds(BlockSoundGroup.CORAL)
	);
	public static final Block HORN_CORAL_BLOCK = register(
		"horn_coral_block",
		settings -> new CoralBlockBlock(DEAD_HORN_CORAL_BLOCK, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.YELLOW)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(1.5F, 6.0F)
			.sounds(BlockSoundGroup.CORAL)
	);
	public static final Block DEAD_TUBE_CORAL = register(
		"dead_tube_coral",
		DeadCoralBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block DEAD_BRAIN_CORAL = register(
		"dead_brain_coral",
		DeadCoralBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block DEAD_BUBBLE_CORAL = register(
		"dead_bubble_coral",
		DeadCoralBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block DEAD_FIRE_CORAL = register(
		"dead_fire_coral",
		DeadCoralBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block DEAD_HORN_CORAL = register(
		"dead_horn_coral",
		DeadCoralBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block TUBE_CORAL = register(
		"tube_coral",
		settings -> new CoralBlock(DEAD_TUBE_CORAL, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLUE)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BRAIN_CORAL = register(
		"brain_coral",
		settings -> new CoralBlock(DEAD_BRAIN_CORAL, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PINK)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BUBBLE_CORAL = register(
		"bubble_coral",
		settings -> new CoralBlock(DEAD_BUBBLE_CORAL, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PURPLE)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block FIRE_CORAL = register(
		"fire_coral",
		settings -> new CoralBlock(DEAD_FIRE_CORAL, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.RED)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block HORN_CORAL = register(
		"horn_coral",
		settings -> new CoralBlock(DEAD_HORN_CORAL, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.YELLOW)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DEAD_TUBE_CORAL_FAN = register(
		"dead_tube_coral_fan",
		DeadCoralFanBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block DEAD_BRAIN_CORAL_FAN = register(
		"dead_brain_coral_fan",
		DeadCoralFanBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block DEAD_BUBBLE_CORAL_FAN = register(
		"dead_bubble_coral_fan",
		DeadCoralFanBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block DEAD_FIRE_CORAL_FAN = register(
		"dead_fire_coral_fan",
		DeadCoralFanBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block DEAD_HORN_CORAL_FAN = register(
		"dead_horn_coral_fan",
		DeadCoralFanBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
	);
	public static final Block TUBE_CORAL_FAN = register(
		"tube_coral_fan",
		settings -> new CoralFanBlock(DEAD_TUBE_CORAL_FAN, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLUE)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BRAIN_CORAL_FAN = register(
		"brain_coral_fan",
		settings -> new CoralFanBlock(DEAD_BRAIN_CORAL_FAN, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PINK)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BUBBLE_CORAL_FAN = register(
		"bubble_coral_fan",
		settings -> new CoralFanBlock(DEAD_BUBBLE_CORAL_FAN, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PURPLE)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block FIRE_CORAL_FAN = register(
		"fire_coral_fan",
		settings -> new CoralFanBlock(DEAD_FIRE_CORAL_FAN, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.RED)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block HORN_CORAL_FAN = register(
		"horn_coral_fan",
		settings -> new CoralFanBlock(DEAD_HORN_CORAL_FAN, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.YELLOW)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block DEAD_TUBE_CORAL_WALL_FAN = register(
		"dead_tube_coral_wall_fan",
		DeadCoralWallFanBlock::new,
		copyLootTable(DEAD_TUBE_CORAL_FAN, false)
			.mapColor(MapColor.GRAY)
			.solid()
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.noCollision()
			.breakInstantly()
	);
	public static final Block DEAD_BRAIN_CORAL_WALL_FAN = register(
		"dead_brain_coral_wall_fan",
		DeadCoralWallFanBlock::new,
		copyLootTable(DEAD_BRAIN_CORAL_FAN, false)
			.mapColor(MapColor.GRAY)
			.solid()
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.noCollision()
			.breakInstantly()
	);
	public static final Block DEAD_BUBBLE_CORAL_WALL_FAN = register(
		"dead_bubble_coral_wall_fan",
		DeadCoralWallFanBlock::new,
		copyLootTable(DEAD_BUBBLE_CORAL_FAN, false)
			.mapColor(MapColor.GRAY)
			.solid()
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.noCollision()
			.breakInstantly()
	);
	public static final Block DEAD_FIRE_CORAL_WALL_FAN = register(
		"dead_fire_coral_wall_fan",
		DeadCoralWallFanBlock::new,
		copyLootTable(DEAD_FIRE_CORAL_FAN, false)
			.mapColor(MapColor.GRAY)
			.solid()
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.noCollision()
			.breakInstantly()
	);
	public static final Block DEAD_HORN_CORAL_WALL_FAN = register(
		"dead_horn_coral_wall_fan",
		DeadCoralWallFanBlock::new,
		copyLootTable(DEAD_HORN_CORAL_FAN, false)
			.mapColor(MapColor.GRAY)
			.solid()
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.noCollision()
			.breakInstantly()
	);
	public static final Block TUBE_CORAL_WALL_FAN = register(
		"tube_coral_wall_fan",
		settings -> new CoralWallFanBlock(DEAD_TUBE_CORAL_WALL_FAN, settings),
		copyLootTable(TUBE_CORAL_FAN, false)
			.mapColor(MapColor.BLUE)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BRAIN_CORAL_WALL_FAN = register(
		"brain_coral_wall_fan",
		settings -> new CoralWallFanBlock(DEAD_BRAIN_CORAL_WALL_FAN, settings),
		copyLootTable(BRAIN_CORAL_FAN, false)
			.mapColor(MapColor.PINK)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BUBBLE_CORAL_WALL_FAN = register(
		"bubble_coral_wall_fan",
		settings -> new CoralWallFanBlock(DEAD_BUBBLE_CORAL_WALL_FAN, settings),
		copyLootTable(BUBBLE_CORAL_FAN, false)
			.mapColor(MapColor.PURPLE)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block FIRE_CORAL_WALL_FAN = register(
		"fire_coral_wall_fan",
		settings -> new CoralWallFanBlock(DEAD_FIRE_CORAL_WALL_FAN, settings),
		copyLootTable(FIRE_CORAL_FAN, false)
			.mapColor(MapColor.RED)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block HORN_CORAL_WALL_FAN = register(
		"horn_coral_wall_fan",
		settings -> new CoralWallFanBlock(DEAD_HORN_CORAL_WALL_FAN, settings),
		copyLootTable(HORN_CORAL_FAN, false)
			.mapColor(MapColor.YELLOW)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WET_GRASS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SEA_PICKLE = register(
		"sea_pickle",
		SeaPickleBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.GREEN)
			.luminance(state -> SeaPickleBlock.isDry(state) ? 0 : 3 + 3 * (Integer)state.get(SeaPickleBlock.PICKLES))
			.sounds(BlockSoundGroup.SLIME)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BLUE_ICE = register(
		"blue_ice",
		TranslucentBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_PURPLE).strength(2.8F).slipperiness(0.989F).sounds(BlockSoundGroup.GLASS)
	);
	public static final Block CONDUIT = register(
		"conduit",
		ConduitBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).solid().instrument(NoteBlockInstrument.HAT).strength(3.0F).luminance(state -> 15).nonOpaque()
	);
	public static final Block BAMBOO_SAPLING = register(
		"bamboo_sapling",
		BambooShootBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.solid()
			.ticksRandomly()
			.breakInstantly()
			.noCollision()
			.strength(1.0F)
			.sounds(BlockSoundGroup.BAMBOO_SAPLING)
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BAMBOO = register(
		"bamboo",
		BambooBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.solid()
			.ticksRandomly()
			.breakInstantly()
			.strength(1.0F)
			.sounds(BlockSoundGroup.BAMBOO)
			.nonOpaque()
			.dynamicBounds()
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
			.solidBlock(Blocks::never)
	);
	public static final Block POTTED_BAMBOO = register("potted_bamboo", settings -> new FlowerPotBlock(BAMBOO, settings), createFlowerPotSettings());
	public static final Block VOID_AIR = register("void_air", AirBlock::new, AbstractBlock.Settings.create().replaceable().noCollision().dropsNothing().air());
	public static final Block CAVE_AIR = register("cave_air", AirBlock::new, AbstractBlock.Settings.create().replaceable().noCollision().dropsNothing().air());
	public static final Block BUBBLE_COLUMN = register(
		"bubble_column",
		BubbleColumnBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WATER_BLUE)
			.replaceable()
			.noCollision()
			.dropsNothing()
			.pistonBehavior(PistonBehavior.DESTROY)
			.liquid()
			.sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
	);
	public static final Block POLISHED_GRANITE_STAIRS = registerOldStairsBlock("polished_granite_stairs", POLISHED_GRANITE);
	public static final Block SMOOTH_RED_SANDSTONE_STAIRS = registerOldStairsBlock("smooth_red_sandstone_stairs", SMOOTH_RED_SANDSTONE);
	public static final Block MOSSY_STONE_BRICK_STAIRS = registerOldStairsBlock("mossy_stone_brick_stairs", MOSSY_STONE_BRICKS);
	public static final Block POLISHED_DIORITE_STAIRS = registerOldStairsBlock("polished_diorite_stairs", POLISHED_DIORITE);
	public static final Block MOSSY_COBBLESTONE_STAIRS = registerOldStairsBlock("mossy_cobblestone_stairs", MOSSY_COBBLESTONE);
	public static final Block END_STONE_BRICK_STAIRS = registerOldStairsBlock("end_stone_brick_stairs", END_STONE_BRICKS);
	public static final Block STONE_STAIRS = registerOldStairsBlock("stone_stairs", STONE);
	public static final Block SMOOTH_SANDSTONE_STAIRS = registerOldStairsBlock("smooth_sandstone_stairs", SMOOTH_SANDSTONE);
	public static final Block SMOOTH_QUARTZ_STAIRS = registerOldStairsBlock("smooth_quartz_stairs", SMOOTH_QUARTZ);
	public static final Block GRANITE_STAIRS = registerOldStairsBlock("granite_stairs", GRANITE);
	public static final Block ANDESITE_STAIRS = registerOldStairsBlock("andesite_stairs", ANDESITE);
	public static final Block RED_NETHER_BRICK_STAIRS = registerOldStairsBlock("red_nether_brick_stairs", RED_NETHER_BRICKS);
	public static final Block POLISHED_ANDESITE_STAIRS = registerOldStairsBlock("polished_andesite_stairs", POLISHED_ANDESITE);
	public static final Block DIORITE_STAIRS = registerOldStairsBlock("diorite_stairs", DIORITE);
	public static final Block POLISHED_GRANITE_SLAB = register("polished_granite_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_GRANITE));
	public static final Block SMOOTH_RED_SANDSTONE_SLAB = register(
		"smooth_red_sandstone_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(SMOOTH_RED_SANDSTONE)
	);
	public static final Block MOSSY_STONE_BRICK_SLAB = register("mossy_stone_brick_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(MOSSY_STONE_BRICKS));
	public static final Block POLISHED_DIORITE_SLAB = register("polished_diorite_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_DIORITE));
	public static final Block MOSSY_COBBLESTONE_SLAB = register("mossy_cobblestone_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(MOSSY_COBBLESTONE));
	public static final Block END_STONE_BRICK_SLAB = register("end_stone_brick_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(END_STONE_BRICKS));
	public static final Block SMOOTH_SANDSTONE_SLAB = register("smooth_sandstone_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(SMOOTH_SANDSTONE));
	public static final Block SMOOTH_QUARTZ_SLAB = register("smooth_quartz_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(SMOOTH_QUARTZ));
	public static final Block GRANITE_SLAB = register("granite_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(GRANITE));
	public static final Block ANDESITE_SLAB = register("andesite_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(ANDESITE));
	public static final Block RED_NETHER_BRICK_SLAB = register("red_nether_brick_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(RED_NETHER_BRICKS));
	public static final Block POLISHED_ANDESITE_SLAB = register("polished_andesite_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_ANDESITE));
	public static final Block DIORITE_SLAB = register("diorite_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(DIORITE));
	public static final Block BRICK_WALL = register("brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(BRICKS).solid());
	public static final Block PRISMARINE_WALL = register("prismarine_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(PRISMARINE).solid());
	public static final Block RED_SANDSTONE_WALL = register("red_sandstone_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(RED_SANDSTONE).solid());
	public static final Block MOSSY_STONE_BRICK_WALL = register(
		"mossy_stone_brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(MOSSY_STONE_BRICKS).solid()
	);
	public static final Block GRANITE_WALL = register("granite_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(GRANITE).solid());
	public static final Block STONE_BRICK_WALL = register("stone_brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(STONE_BRICKS).solid());
	public static final Block MUD_BRICK_WALL = register("mud_brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(MUD_BRICKS).solid());
	public static final Block NETHER_BRICK_WALL = register("nether_brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(NETHER_BRICKS).solid());
	public static final Block ANDESITE_WALL = register("andesite_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(ANDESITE).solid());
	public static final Block RED_NETHER_BRICK_WALL = register(
		"red_nether_brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(RED_NETHER_BRICKS).solid()
	);
	public static final Block SANDSTONE_WALL = register("sandstone_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(SANDSTONE).solid());
	public static final Block END_STONE_BRICK_WALL = register("end_stone_brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(END_STONE_BRICKS).solid());
	public static final Block DIORITE_WALL = register("diorite_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(DIORITE).solid());
	public static final Block SCAFFOLDING = register(
		"scaffolding",
		ScaffoldingBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_YELLOW)
			.noCollision()
			.sounds(BlockSoundGroup.SCAFFOLDING)
			.dynamicBounds()
			.allowsSpawning(Blocks::never)
			.pistonBehavior(PistonBehavior.DESTROY)
			.solidBlock(Blocks::never)
	);
	public static final Block LOOM = register(
		"loom",
		LoomBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block BARREL = register(
		"barrel",
		BarrelBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block SMOKER = register(
		"smoker",
		SmokerBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(3.5F)
			.luminance(createLightLevelFromLitBlockState(13))
	);
	public static final Block BLAST_FURNACE = register(
		"blast_furnace",
		BlastFurnaceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(3.5F)
			.luminance(createLightLevelFromLitBlockState(13))
	);
	public static final Block CARTOGRAPHY_TABLE = register(
		"cartography_table",
		CartographyTableBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block FLETCHING_TABLE = register(
		"fletching_table",
		FletchingTableBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block GRINDSTONE = register(
		"grindstone",
		GrindstoneBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.IRON_GRAY)
			.requiresTool()
			.strength(2.0F, 6.0F)
			.sounds(BlockSoundGroup.STONE)
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block LECTERN = register(
		"lectern",
		LecternBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block SMITHING_TABLE = register(
		"smithing_table",
		SmithingTableBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block STONECUTTER = register(
		"stonecutter",
		StonecutterBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5F)
	);
	public static final Block BELL = register(
		"bell",
		BellBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.GOLD)
			.solid()
			.requiresTool()
			.strength(5.0F)
			.sounds(BlockSoundGroup.ANVIL)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block LANTERN = register(
		"lantern",
		LanternBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.IRON_GRAY)
			.solid()
			.requiresTool()
			.strength(3.5F)
			.sounds(BlockSoundGroup.LANTERN)
			.luminance(state -> 15)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SOUL_LANTERN = register(
		"soul_lantern",
		LanternBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.IRON_GRAY)
			.solid()
			.requiresTool()
			.strength(3.5F)
			.sounds(BlockSoundGroup.LANTERN)
			.luminance(state -> 10)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CAMPFIRE = register(
		"campfire",
		settings -> new CampfireBlock(true, 1, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.SPRUCE_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F)
			.sounds(BlockSoundGroup.WOOD)
			.luminance(createLightLevelFromLitBlockState(15))
			.nonOpaque()
			.burnable()
	);
	public static final Block SOUL_CAMPFIRE = register(
		"soul_campfire",
		settings -> new CampfireBlock(false, 2, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.SPRUCE_BROWN)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F)
			.sounds(BlockSoundGroup.WOOD)
			.luminance(createLightLevelFromLitBlockState(10))
			.nonOpaque()
			.burnable()
	);
	public static final Block SWEET_BERRY_BUSH = register(
		"sweet_berry_bush",
		SweetBerryBushBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.ticksRandomly()
			.noCollision()
			.sounds(BlockSoundGroup.SWEET_BERRY_BUSH)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WARPED_STEM = register("warped_stem", PillarBlock::new, createNetherStemSettings(MapColor.DARK_AQUA));
	public static final Block STRIPPED_WARPED_STEM = register("stripped_warped_stem", PillarBlock::new, createNetherStemSettings(MapColor.DARK_AQUA));
	public static final Block WARPED_HYPHAE = register(
		"warped_hyphae",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DARK_DULL_PINK).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)
	);
	public static final Block STRIPPED_WARPED_HYPHAE = register(
		"stripped_warped_hyphae",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DARK_DULL_PINK).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)
	);
	public static final Block WARPED_NYLIUM = register(
		"warped_nylium",
		NyliumBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TEAL)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(0.4F)
			.sounds(BlockSoundGroup.NYLIUM)
			.ticksRandomly()
	);
	public static final Block WARPED_FUNGUS = register(
		"warped_fungus",
		settings -> new FungusBlock(TreeConfiguredFeatures.WARPED_FUNGUS_PLANTED, WARPED_NYLIUM, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.CYAN).breakInstantly().noCollision().sounds(BlockSoundGroup.FUNGUS).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WARPED_WART_BLOCK = register(
		"warped_wart_block", AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_TEAL).strength(1.0F).sounds(BlockSoundGroup.WART_BLOCK)
	);
	public static final Block WARPED_ROOTS = register(
		"warped_roots",
		RootsBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.CYAN)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.ROOTS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block NETHER_SPROUTS = register(
		"nether_sprouts",
		SproutsBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.CYAN)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.NETHER_SPROUTS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CRIMSON_STEM = register("crimson_stem", PillarBlock::new, createNetherStemSettings(MapColor.DULL_PINK));
	public static final Block STRIPPED_CRIMSON_STEM = register("stripped_crimson_stem", PillarBlock::new, createNetherStemSettings(MapColor.DULL_PINK));
	public static final Block CRIMSON_HYPHAE = register(
		"crimson_hyphae",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DARK_CRIMSON).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)
	);
	public static final Block STRIPPED_CRIMSON_HYPHAE = register(
		"stripped_crimson_hyphae",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DARK_CRIMSON).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)
	);
	public static final Block CRIMSON_NYLIUM = register(
		"crimson_nylium",
		NyliumBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DULL_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(0.4F)
			.sounds(BlockSoundGroup.NYLIUM)
			.ticksRandomly()
	);
	public static final Block CRIMSON_FUNGUS = register(
		"crimson_fungus",
		settings -> new FungusBlock(TreeConfiguredFeatures.CRIMSON_FUNGUS_PLANTED, CRIMSON_NYLIUM, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.breakInstantly()
			.noCollision()
			.sounds(BlockSoundGroup.FUNGUS)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SHROOMLIGHT = register(
		"shroomlight", AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(1.0F).sounds(BlockSoundGroup.SHROOMLIGHT).luminance(state -> 15)
	);
	public static final Block WEEPING_VINES = register(
		"weeping_vines",
		WeepingVinesBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.ticksRandomly()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WEEPING_VINES)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WEEPING_VINES_PLANT = register(
		"weeping_vines_plant",
		WeepingVinesPlantBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WEEPING_VINES)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block TWISTING_VINES = register(
		"twisting_vines",
		TwistingVinesBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.CYAN)
			.ticksRandomly()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WEEPING_VINES)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block TWISTING_VINES_PLANT = register(
		"twisting_vines_plant",
		TwistingVinesPlantBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.CYAN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.WEEPING_VINES)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CRIMSON_ROOTS = register(
		"crimson_roots",
		RootsBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.ROOTS)
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CRIMSON_PLANKS = register(
		"crimson_planks",
		AbstractBlock.Settings.create().mapColor(MapColor.DULL_PINK).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.NETHER_WOOD)
	);
	public static final Block WARPED_PLANKS = register(
		"warped_planks",
		AbstractBlock.Settings.create().mapColor(MapColor.DARK_AQUA).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.NETHER_WOOD)
	);
	public static final Block CRIMSON_SLAB = register(
		"crimson_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.NETHER_WOOD)
	);
	public static final Block WARPED_SLAB = register(
		"warped_slab",
		SlabBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(WARPED_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.NETHER_WOOD)
	);
	public static final Block CRIMSON_PRESSURE_PLATE = register(
		"crimson_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.CRIMSON, settings),
		AbstractBlock.Settings.create()
			.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WARPED_PRESSURE_PLATE = register(
		"warped_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.WARPED, settings),
		AbstractBlock.Settings.create()
			.mapColor(WARPED_PLANKS.getDefaultMapColor())
			.solid()
			.instrument(NoteBlockInstrument.BASS)
			.noCollision()
			.strength(0.5F)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CRIMSON_FENCE = register(
		"crimson_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.NETHER_WOOD)
	);
	public static final Block WARPED_FENCE = register(
		"warped_fence",
		FenceBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(WARPED_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F, 3.0F)
			.sounds(BlockSoundGroup.NETHER_WOOD)
	);
	public static final Block CRIMSON_TRAPDOOR = register(
		"crimson_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.CRIMSON, settings),
		AbstractBlock.Settings.create()
			.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
	);
	public static final Block WARPED_TRAPDOOR = register(
		"warped_trapdoor",
		settings -> new TrapdoorBlock(BlockSetType.WARPED, settings),
		AbstractBlock.Settings.create()
			.mapColor(WARPED_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
	);
	public static final Block CRIMSON_FENCE_GATE = register(
		"crimson_fence_gate",
		settings -> new FenceGateBlock(WoodType.CRIMSON, settings),
		AbstractBlock.Settings.create().mapColor(CRIMSON_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F)
	);
	public static final Block WARPED_FENCE_GATE = register(
		"warped_fence_gate",
		settings -> new FenceGateBlock(WoodType.WARPED, settings),
		AbstractBlock.Settings.create().mapColor(WARPED_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F)
	);
	public static final Block CRIMSON_STAIRS = registerOldStairsBlock("crimson_stairs", CRIMSON_PLANKS);
	public static final Block WARPED_STAIRS = registerOldStairsBlock("warped_stairs", WARPED_PLANKS);
	public static final Block CRIMSON_BUTTON = register("crimson_button", settings -> new ButtonBlock(BlockSetType.CRIMSON, 30, settings), createButtonSettings());
	public static final Block WARPED_BUTTON = register("warped_button", settings -> new ButtonBlock(BlockSetType.WARPED, 30, settings), createButtonSettings());
	public static final Block CRIMSON_DOOR = register(
		"crimson_door",
		settings -> new DoorBlock(BlockSetType.CRIMSON, settings),
		AbstractBlock.Settings.create()
			.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block WARPED_DOOR = register(
		"warped_door",
		settings -> new DoorBlock(BlockSetType.WARPED, settings),
		AbstractBlock.Settings.create()
			.mapColor(WARPED_PLANKS.getDefaultMapColor())
			.instrument(NoteBlockInstrument.BASS)
			.strength(3.0F)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CRIMSON_SIGN = register(
		"crimson_sign",
		settings -> new SignBlock(WoodType.CRIMSON, settings),
		AbstractBlock.Settings.create().mapColor(CRIMSON_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASS).solid().noCollision().strength(1.0F)
	);
	public static final Block WARPED_SIGN = register(
		"warped_sign",
		settings -> new SignBlock(WoodType.WARPED, settings),
		AbstractBlock.Settings.create().mapColor(WARPED_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASS).solid().noCollision().strength(1.0F)
	);
	public static final Block CRIMSON_WALL_SIGN = register(
		"crimson_wall_sign",
		settings -> new WallSignBlock(WoodType.CRIMSON, settings),
		copyLootTable(CRIMSON_SIGN, true).mapColor(CRIMSON_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASS).solid().noCollision().strength(1.0F)
	);
	public static final Block WARPED_WALL_SIGN = register(
		"warped_wall_sign",
		settings -> new WallSignBlock(WoodType.WARPED, settings),
		copyLootTable(WARPED_SIGN, true).mapColor(WARPED_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASS).solid().noCollision().strength(1.0F)
	);
	public static final Block STRUCTURE_BLOCK = register(
		"structure_block",
		StructureBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_GRAY).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing()
	);
	public static final Block JIGSAW = register(
		"jigsaw", JigsawBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_GRAY).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing()
	);
	public static final Block COMPOSTER = register(
		"composter",
		ComposterBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(0.6F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block TARGET = register(
		"target", TargetBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).strength(0.5F).sounds(BlockSoundGroup.GRASS)
	);
	public static final Block BEE_NEST = register(
		"bee_nest",
		BeehiveBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(NoteBlockInstrument.BASS).strength(0.3F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block BEEHIVE = register(
		"beehive",
		BeehiveBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(0.6F).sounds(BlockSoundGroup.WOOD).burnable()
	);
	public static final Block HONEY_BLOCK = register(
		"honey_block",
		HoneyBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).velocityMultiplier(0.4F).jumpVelocityMultiplier(0.5F).nonOpaque().sounds(BlockSoundGroup.HONEY)
	);
	public static final Block HONEYCOMB_BLOCK = register(
		"honeycomb_block", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(0.6F).sounds(BlockSoundGroup.CORAL)
	);
	public static final Block NETHERITE_BLOCK = register(
		"netherite_block", AbstractBlock.Settings.create().mapColor(MapColor.BLACK).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.NETHERITE)
	);
	public static final Block ANCIENT_DEBRIS = register(
		"ancient_debris", AbstractBlock.Settings.create().mapColor(MapColor.BLACK).requiresTool().strength(30.0F, 1200.0F).sounds(BlockSoundGroup.ANCIENT_DEBRIS)
	);
	public static final Block CRYING_OBSIDIAN = register(
		"crying_obsidian",
		CryingObsidianBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLACK)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(50.0F, 1200.0F)
			.luminance(state -> 10)
	);
	public static final Block RESPAWN_ANCHOR = register(
		"respawn_anchor",
		RespawnAnchorBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLACK)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(50.0F, 1200.0F)
			.luminance(state -> RespawnAnchorBlock.getLightLevel(state, 15))
	);
	public static final Block POTTED_CRIMSON_FUNGUS = register(
		"potted_crimson_fungus", settings -> new FlowerPotBlock(CRIMSON_FUNGUS, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_WARPED_FUNGUS = register(
		"potted_warped_fungus", settings -> new FlowerPotBlock(WARPED_FUNGUS, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_CRIMSON_ROOTS = register(
		"potted_crimson_roots", settings -> new FlowerPotBlock(CRIMSON_ROOTS, settings), createFlowerPotSettings()
	);
	public static final Block POTTED_WARPED_ROOTS = register(
		"potted_warped_roots", settings -> new FlowerPotBlock(WARPED_ROOTS, settings), createFlowerPotSettings()
	);
	public static final Block LODESTONE = register(
		"lodestone",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.IRON_GRAY)
			.requiresTool()
			.strength(3.5F)
			.sounds(BlockSoundGroup.LODESTONE)
			.pistonBehavior(PistonBehavior.BLOCK)
	);
	public static final Block BLACKSTONE = register(
		"blackstone", AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
	);
	public static final Block BLACKSTONE_STAIRS = registerOldStairsBlock("blackstone_stairs", BLACKSTONE);
	public static final Block BLACKSTONE_WALL = register("blackstone_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(BLACKSTONE).solid());
	public static final Block BLACKSTONE_SLAB = register("blackstone_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(BLACKSTONE).strength(2.0F, 6.0F));
	public static final Block POLISHED_BLACKSTONE = register("polished_blackstone", AbstractBlock.Settings.copyShallow(BLACKSTONE).strength(2.0F, 6.0F));
	public static final Block POLISHED_BLACKSTONE_BRICKS = register(
		"polished_blackstone_bricks", AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE).strength(1.5F, 6.0F)
	);
	public static final Block CRACKED_POLISHED_BLACKSTONE_BRICKS = register(
		"cracked_polished_blackstone_bricks", AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE_BRICKS)
	);
	public static final Block CHISELED_POLISHED_BLACKSTONE = register(
		"chiseled_polished_blackstone", AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE).strength(1.5F, 6.0F)
	);
	public static final Block POLISHED_BLACKSTONE_BRICK_SLAB = register(
		"polished_blackstone_brick_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE_BRICKS).strength(2.0F, 6.0F)
	);
	public static final Block POLISHED_BLACKSTONE_BRICK_STAIRS = registerOldStairsBlock("polished_blackstone_brick_stairs", POLISHED_BLACKSTONE_BRICKS);
	public static final Block POLISHED_BLACKSTONE_BRICK_WALL = register(
		"polished_blackstone_brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE_BRICKS).solid()
	);
	public static final Block GILDED_BLACKSTONE = register(
		"gilded_blackstone", AbstractBlock.Settings.copyShallow(BLACKSTONE).sounds(BlockSoundGroup.GILDED_BLACKSTONE)
	);
	public static final Block POLISHED_BLACKSTONE_STAIRS = registerOldStairsBlock("polished_blackstone_stairs", POLISHED_BLACKSTONE);
	public static final Block POLISHED_BLACKSTONE_SLAB = register(
		"polished_blackstone_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE)
	);
	public static final Block POLISHED_BLACKSTONE_PRESSURE_PLATE = register(
		"polished_blackstone_pressure_plate",
		settings -> new PressurePlateBlock(BlockSetType.POLISHED_BLACKSTONE, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLACK)
			.solid()
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.noCollision()
			.strength(0.5F)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block POLISHED_BLACKSTONE_BUTTON = register(
		"polished_blackstone_button", settings -> new ButtonBlock(BlockSetType.STONE, 20, settings), createButtonSettings()
	);
	public static final Block POLISHED_BLACKSTONE_WALL = register(
		"polished_blackstone_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE).solid()
	);
	public static final Block CHISELED_NETHER_BRICKS = register(
		"chiseled_nether_bricks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(2.0F, 6.0F)
			.sounds(BlockSoundGroup.NETHER_BRICKS)
	);
	public static final Block CRACKED_NETHER_BRICKS = register(
		"cracked_nether_bricks",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(2.0F, 6.0F)
			.sounds(BlockSoundGroup.NETHER_BRICKS)
	);
	public static final Block QUARTZ_BRICKS = register("quartz_bricks", AbstractBlock.Settings.copyShallow(QUARTZ_BLOCK));
	public static final Block CANDLE = register("candle", CandleBlock::new, createCandleSettings(MapColor.PALE_YELLOW));
	public static final Block WHITE_CANDLE = register("white_candle", CandleBlock::new, createCandleSettings(MapColor.WHITE_GRAY));
	public static final Block ORANGE_CANDLE = register("orange_candle", CandleBlock::new, createCandleSettings(MapColor.ORANGE));
	public static final Block MAGENTA_CANDLE = register("magenta_candle", CandleBlock::new, createCandleSettings(MapColor.MAGENTA));
	public static final Block LIGHT_BLUE_CANDLE = register("light_blue_candle", CandleBlock::new, createCandleSettings(MapColor.LIGHT_BLUE));
	public static final Block YELLOW_CANDLE = register("yellow_candle", CandleBlock::new, createCandleSettings(MapColor.YELLOW));
	public static final Block LIME_CANDLE = register("lime_candle", CandleBlock::new, createCandleSettings(MapColor.LIME));
	public static final Block PINK_CANDLE = register("pink_candle", CandleBlock::new, createCandleSettings(MapColor.PINK));
	public static final Block GRAY_CANDLE = register("gray_candle", CandleBlock::new, createCandleSettings(MapColor.GRAY));
	public static final Block LIGHT_GRAY_CANDLE = register("light_gray_candle", CandleBlock::new, createCandleSettings(MapColor.LIGHT_GRAY));
	public static final Block CYAN_CANDLE = register("cyan_candle", CandleBlock::new, createCandleSettings(MapColor.CYAN));
	public static final Block PURPLE_CANDLE = register("purple_candle", CandleBlock::new, createCandleSettings(MapColor.PURPLE));
	public static final Block BLUE_CANDLE = register("blue_candle", CandleBlock::new, createCandleSettings(MapColor.BLUE));
	public static final Block BROWN_CANDLE = register("brown_candle", CandleBlock::new, createCandleSettings(MapColor.BROWN));
	public static final Block GREEN_CANDLE = register("green_candle", CandleBlock::new, createCandleSettings(MapColor.GREEN));
	public static final Block RED_CANDLE = register("red_candle", CandleBlock::new, createCandleSettings(MapColor.RED));
	public static final Block BLACK_CANDLE = register("black_candle", CandleBlock::new, createCandleSettings(MapColor.BLACK));
	public static final Block CANDLE_CAKE = register(
		"candle_cake", settings -> new CandleCakeBlock(CANDLE, settings), AbstractBlock.Settings.copyShallow(CAKE).luminance(createLightLevelFromLitBlockState(3))
	);
	public static final Block WHITE_CANDLE_CAKE = register(
		"white_candle_cake", settings -> new CandleCakeBlock(WHITE_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block ORANGE_CANDLE_CAKE = register(
		"orange_candle_cake", settings -> new CandleCakeBlock(ORANGE_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block MAGENTA_CANDLE_CAKE = register(
		"magenta_candle_cake", settings -> new CandleCakeBlock(MAGENTA_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block LIGHT_BLUE_CANDLE_CAKE = register(
		"light_blue_candle_cake", settings -> new CandleCakeBlock(LIGHT_BLUE_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block YELLOW_CANDLE_CAKE = register(
		"yellow_candle_cake", settings -> new CandleCakeBlock(YELLOW_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block LIME_CANDLE_CAKE = register(
		"lime_candle_cake", settings -> new CandleCakeBlock(LIME_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block PINK_CANDLE_CAKE = register(
		"pink_candle_cake", settings -> new CandleCakeBlock(PINK_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block GRAY_CANDLE_CAKE = register(
		"gray_candle_cake", settings -> new CandleCakeBlock(GRAY_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block LIGHT_GRAY_CANDLE_CAKE = register(
		"light_gray_candle_cake", settings -> new CandleCakeBlock(LIGHT_GRAY_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block CYAN_CANDLE_CAKE = register(
		"cyan_candle_cake", settings -> new CandleCakeBlock(CYAN_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block PURPLE_CANDLE_CAKE = register(
		"purple_candle_cake", settings -> new CandleCakeBlock(PURPLE_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block BLUE_CANDLE_CAKE = register(
		"blue_candle_cake", settings -> new CandleCakeBlock(BLUE_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block BROWN_CANDLE_CAKE = register(
		"brown_candle_cake", settings -> new CandleCakeBlock(BROWN_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block GREEN_CANDLE_CAKE = register(
		"green_candle_cake", settings -> new CandleCakeBlock(GREEN_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block RED_CANDLE_CAKE = register(
		"red_candle_cake", settings -> new CandleCakeBlock(RED_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block BLACK_CANDLE_CAKE = register(
		"black_candle_cake", settings -> new CandleCakeBlock(BLACK_CANDLE, settings), AbstractBlock.Settings.copyShallow(CANDLE_CAKE)
	);
	public static final Block AMETHYST_BLOCK = register(
		"amethyst_block",
		AmethystBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).strength(1.5F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool()
	);
	public static final Block BUDDING_AMETHYST = register(
		"budding_amethyst",
		BuddingAmethystBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PURPLE)
			.ticksRandomly()
			.strength(1.5F)
			.sounds(BlockSoundGroup.AMETHYST_BLOCK)
			.requiresTool()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block AMETHYST_CLUSTER = register(
		"amethyst_cluster",
		settings -> new AmethystClusterBlock(7.0F, 3.0F, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PURPLE)
			.solid()
			.nonOpaque()
			.sounds(BlockSoundGroup.AMETHYST_CLUSTER)
			.strength(1.5F)
			.luminance(state -> 5)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block LARGE_AMETHYST_BUD = register(
		"large_amethyst_bud",
		settings -> new AmethystClusterBlock(5.0F, 3.0F, settings),
		AbstractBlock.Settings.copyShallow(AMETHYST_CLUSTER).sounds(BlockSoundGroup.MEDIUM_AMETHYST_BUD).luminance(state -> 4)
	);
	public static final Block MEDIUM_AMETHYST_BUD = register(
		"medium_amethyst_bud",
		settings -> new AmethystClusterBlock(4.0F, 3.0F, settings),
		AbstractBlock.Settings.copyShallow(AMETHYST_CLUSTER).sounds(BlockSoundGroup.LARGE_AMETHYST_BUD).luminance(state -> 2)
	);
	public static final Block SMALL_AMETHYST_BUD = register(
		"small_amethyst_bud",
		settings -> new AmethystClusterBlock(3.0F, 4.0F, settings),
		AbstractBlock.Settings.copyShallow(AMETHYST_CLUSTER).sounds(BlockSoundGroup.SMALL_AMETHYST_BUD).luminance(state -> 1)
	);
	public static final Block TUFF = register(
		"tuff",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.sounds(BlockSoundGroup.TUFF)
			.requiresTool()
			.strength(1.5F, 6.0F)
	);
	public static final Block TUFF_SLAB = register("tuff_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(TUFF));
	public static final Block TUFF_STAIRS = register(
		"tuff_stairs", settings -> new StairsBlock(TUFF.getDefaultState(), settings), AbstractBlock.Settings.copyShallow(TUFF)
	);
	public static final Block TUFF_WALL = register("tuff_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(TUFF).solid());
	public static final Block POLISHED_TUFF = register("polished_tuff", AbstractBlock.Settings.copyShallow(TUFF).sounds(BlockSoundGroup.POLISHED_TUFF));
	public static final Block POLISHED_TUFF_SLAB = register("polished_tuff_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_TUFF));
	public static final Block POLISHED_TUFF_STAIRS = register(
		"polished_tuff_stairs", settings -> new StairsBlock(POLISHED_TUFF.getDefaultState(), settings), AbstractBlock.Settings.copyShallow(POLISHED_TUFF)
	);
	public static final Block POLISHED_TUFF_WALL = register("polished_tuff_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_TUFF).solid());
	public static final Block CHISELED_TUFF = register("chiseled_tuff", AbstractBlock.Settings.copyShallow(TUFF));
	public static final Block TUFF_BRICKS = register("tuff_bricks", AbstractBlock.Settings.copyShallow(TUFF).sounds(BlockSoundGroup.TUFF_BRICKS));
	public static final Block TUFF_BRICK_SLAB = register("tuff_brick_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(TUFF_BRICKS));
	public static final Block TUFF_BRICK_STAIRS = register(
		"tuff_brick_stairs", settings -> new StairsBlock(TUFF_BRICKS.getDefaultState(), settings), AbstractBlock.Settings.copyShallow(TUFF_BRICKS)
	);
	public static final Block TUFF_BRICK_WALL = register("tuff_brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(TUFF_BRICKS).solid());
	public static final Block CHISELED_TUFF_BRICKS = register("chiseled_tuff_bricks", AbstractBlock.Settings.copyShallow(TUFF_BRICKS));
	public static final Block CALCITE = register(
		"calcite",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_WHITE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.sounds(BlockSoundGroup.CALCITE)
			.requiresTool()
			.strength(0.75F)
	);
	public static final Block TINTED_GLASS = register(
		"tinted_glass",
		TintedGlassBlock::new,
		AbstractBlock.Settings.copyShallow(GLASS)
			.mapColor(MapColor.GRAY)
			.nonOpaque()
			.allowsSpawning(Blocks::never)
			.solidBlock(Blocks::never)
			.suffocates(Blocks::never)
			.blockVision(Blocks::never)
	);
	public static final Block POWDER_SNOW = register(
		"powder_snow",
		PowderSnowBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WHITE)
			.strength(0.25F)
			.sounds(BlockSoundGroup.POWDER_SNOW)
			.dynamicBounds()
			.nonOpaque()
			.solidBlock(Blocks::never)
	);
	public static final Block SCULK_SENSOR = register(
		"sculk_sensor",
		SculkSensorBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.CYAN)
			.strength(1.5F)
			.sounds(BlockSoundGroup.SCULK_SENSOR)
			.luminance(state -> 1)
			.emissiveLighting((state, world, pos) -> SculkSensorBlock.getPhase(state) == SculkSensorPhase.ACTIVE)
	);
	public static final Block CALIBRATED_SCULK_SENSOR = register(
		"calibrated_sculk_sensor", CalibratedSculkSensorBlock::new, AbstractBlock.Settings.copyShallow(SCULK_SENSOR)
	);
	public static final Block SCULK = register(
		"sculk", SculkBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(0.2F).sounds(BlockSoundGroup.SCULK)
	);
	public static final Block SCULK_VEIN = register(
		"sculk_vein",
		SculkVeinBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.BLACK)
			.solid()
			.noCollision()
			.strength(0.2F)
			.sounds(BlockSoundGroup.SCULK_VEIN)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SCULK_CATALYST = register(
		"sculk_catalyst",
		SculkCatalystBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(3.0F, 3.0F).sounds(BlockSoundGroup.SCULK_CATALYST).luminance(state -> 6)
	);
	public static final Block SCULK_SHRIEKER = register(
		"sculk_shrieker",
		SculkShriekerBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(3.0F, 3.0F).sounds(BlockSoundGroup.SCULK_SHRIEKER)
	);
	public static final Block COPPER_BLOCK = register(
		"copper_block",
		settings -> new OxidizableBlock(Oxidizable.OxidationLevel.UNAFFECTED, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)
	);
	public static final Block EXPOSED_COPPER = register(
		"exposed_copper",
		settings -> new OxidizableBlock(Oxidizable.OxidationLevel.EXPOSED, settings),
		AbstractBlock.Settings.copy(COPPER_BLOCK).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
	);
	public static final Block WEATHERED_COPPER = register(
		"weathered_copper",
		settings -> new OxidizableBlock(Oxidizable.OxidationLevel.WEATHERED, settings),
		AbstractBlock.Settings.copy(COPPER_BLOCK).mapColor(MapColor.DARK_AQUA)
	);
	public static final Block OXIDIZED_COPPER = register(
		"oxidized_copper",
		settings -> new OxidizableBlock(Oxidizable.OxidationLevel.OXIDIZED, settings),
		AbstractBlock.Settings.copy(COPPER_BLOCK).mapColor(MapColor.TEAL)
	);
	public static final Block COPPER_ORE = register(
		"copper_ore", settings -> new ExperienceDroppingBlock(ConstantIntProvider.create(0), settings), AbstractBlock.Settings.copyShallow(IRON_ORE)
	);
	public static final Block DEEPSLATE_COPPER_ORE = register(
		"deepslate_copper_ore",
		settings -> new ExperienceDroppingBlock(ConstantIntProvider.create(0), settings),
		AbstractBlock.Settings.copyShallow(COPPER_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block OXIDIZED_CUT_COPPER = register(
		"oxidized_cut_copper", settings -> new OxidizableBlock(Oxidizable.OxidationLevel.OXIDIZED, settings), AbstractBlock.Settings.copy(OXIDIZED_COPPER)
	);
	public static final Block WEATHERED_CUT_COPPER = register(
		"weathered_cut_copper", settings -> new OxidizableBlock(Oxidizable.OxidationLevel.WEATHERED, settings), AbstractBlock.Settings.copy(WEATHERED_COPPER)
	);
	public static final Block EXPOSED_CUT_COPPER = register(
		"exposed_cut_copper", settings -> new OxidizableBlock(Oxidizable.OxidationLevel.EXPOSED, settings), AbstractBlock.Settings.copy(EXPOSED_COPPER)
	);
	public static final Block CUT_COPPER = register(
		"cut_copper", settings -> new OxidizableBlock(Oxidizable.OxidationLevel.UNAFFECTED, settings), AbstractBlock.Settings.copy(COPPER_BLOCK)
	);
	public static final Block OXIDIZED_CHISELED_COPPER = register(
		"oxidized_chiseled_copper", settings -> new OxidizableBlock(Oxidizable.OxidationLevel.OXIDIZED, settings), AbstractBlock.Settings.copy(OXIDIZED_COPPER)
	);
	public static final Block WEATHERED_CHISELED_COPPER = register(
		"weathered_chiseled_copper", settings -> new OxidizableBlock(Oxidizable.OxidationLevel.WEATHERED, settings), AbstractBlock.Settings.copy(WEATHERED_COPPER)
	);
	public static final Block EXPOSED_CHISELED_COPPER = register(
		"exposed_chiseled_copper", settings -> new OxidizableBlock(Oxidizable.OxidationLevel.EXPOSED, settings), AbstractBlock.Settings.copy(EXPOSED_COPPER)
	);
	public static final Block CHISELED_COPPER = register(
		"chiseled_copper", settings -> new OxidizableBlock(Oxidizable.OxidationLevel.UNAFFECTED, settings), AbstractBlock.Settings.copy(COPPER_BLOCK)
	);
	public static final Block WAXED_OXIDIZED_CHISELED_COPPER = register("waxed_oxidized_chiseled_copper", AbstractBlock.Settings.copy(OXIDIZED_CHISELED_COPPER));
	public static final Block WAXED_WEATHERED_CHISELED_COPPER = register("waxed_weathered_chiseled_copper", AbstractBlock.Settings.copy(WEATHERED_CHISELED_COPPER));
	public static final Block WAXED_EXPOSED_CHISELED_COPPER = register("waxed_exposed_chiseled_copper", AbstractBlock.Settings.copy(EXPOSED_CHISELED_COPPER));
	public static final Block WAXED_CHISELED_COPPER = register("waxed_chiseled_copper", AbstractBlock.Settings.copy(CHISELED_COPPER));
	public static final Block OXIDIZED_CUT_COPPER_STAIRS = register(
		"oxidized_cut_copper_stairs",
		settings -> new OxidizableStairsBlock(Oxidizable.OxidationLevel.OXIDIZED, OXIDIZED_CUT_COPPER.getDefaultState(), settings),
		AbstractBlock.Settings.copy(OXIDIZED_CUT_COPPER)
	);
	public static final Block WEATHERED_CUT_COPPER_STAIRS = register(
		"weathered_cut_copper_stairs",
		settings -> new OxidizableStairsBlock(Oxidizable.OxidationLevel.WEATHERED, WEATHERED_CUT_COPPER.getDefaultState(), settings),
		AbstractBlock.Settings.copy(WEATHERED_COPPER)
	);
	public static final Block EXPOSED_CUT_COPPER_STAIRS = register(
		"exposed_cut_copper_stairs",
		settings -> new OxidizableStairsBlock(Oxidizable.OxidationLevel.EXPOSED, EXPOSED_CUT_COPPER.getDefaultState(), settings),
		AbstractBlock.Settings.copy(EXPOSED_COPPER)
	);
	public static final Block CUT_COPPER_STAIRS = register(
		"cut_copper_stairs",
		settings -> new OxidizableStairsBlock(Oxidizable.OxidationLevel.UNAFFECTED, CUT_COPPER.getDefaultState(), settings),
		AbstractBlock.Settings.copy(COPPER_BLOCK)
	);
	public static final Block OXIDIZED_CUT_COPPER_SLAB = register(
		"oxidized_cut_copper_slab",
		settings -> new OxidizableSlabBlock(Oxidizable.OxidationLevel.OXIDIZED, settings),
		AbstractBlock.Settings.copy(OXIDIZED_CUT_COPPER)
	);
	public static final Block WEATHERED_CUT_COPPER_SLAB = register(
		"weathered_cut_copper_slab",
		settings -> new OxidizableSlabBlock(Oxidizable.OxidationLevel.WEATHERED, settings),
		AbstractBlock.Settings.copy(WEATHERED_CUT_COPPER)
	);
	public static final Block EXPOSED_CUT_COPPER_SLAB = register(
		"exposed_cut_copper_slab", settings -> new OxidizableSlabBlock(Oxidizable.OxidationLevel.EXPOSED, settings), AbstractBlock.Settings.copy(EXPOSED_CUT_COPPER)
	);
	public static final Block CUT_COPPER_SLAB = register(
		"cut_copper_slab", settings -> new OxidizableSlabBlock(Oxidizable.OxidationLevel.UNAFFECTED, settings), AbstractBlock.Settings.copy(CUT_COPPER)
	);
	public static final Block WAXED_COPPER_BLOCK = register("waxed_copper_block", AbstractBlock.Settings.copy(COPPER_BLOCK));
	public static final Block WAXED_WEATHERED_COPPER = register("waxed_weathered_copper", AbstractBlock.Settings.copy(WEATHERED_COPPER));
	public static final Block WAXED_EXPOSED_COPPER = register("waxed_exposed_copper", AbstractBlock.Settings.copy(EXPOSED_COPPER));
	public static final Block WAXED_OXIDIZED_COPPER = register("waxed_oxidized_copper", AbstractBlock.Settings.copy(OXIDIZED_COPPER));
	public static final Block WAXED_OXIDIZED_CUT_COPPER = register("waxed_oxidized_cut_copper", AbstractBlock.Settings.copy(OXIDIZED_COPPER));
	public static final Block WAXED_WEATHERED_CUT_COPPER = register("waxed_weathered_cut_copper", AbstractBlock.Settings.copy(WEATHERED_COPPER));
	public static final Block WAXED_EXPOSED_CUT_COPPER = register("waxed_exposed_cut_copper", AbstractBlock.Settings.copy(EXPOSED_COPPER));
	public static final Block WAXED_CUT_COPPER = register("waxed_cut_copper", AbstractBlock.Settings.copy(COPPER_BLOCK));
	public static final Block WAXED_OXIDIZED_CUT_COPPER_STAIRS = registerStairsBlock("waxed_oxidized_cut_copper_stairs", WAXED_OXIDIZED_CUT_COPPER);
	public static final Block WAXED_WEATHERED_CUT_COPPER_STAIRS = registerStairsBlock("waxed_weathered_cut_copper_stairs", WAXED_WEATHERED_CUT_COPPER);
	public static final Block WAXED_EXPOSED_CUT_COPPER_STAIRS = registerStairsBlock("waxed_exposed_cut_copper_stairs", WAXED_EXPOSED_CUT_COPPER);
	public static final Block WAXED_CUT_COPPER_STAIRS = registerStairsBlock("waxed_cut_copper_stairs", WAXED_CUT_COPPER);
	public static final Block WAXED_OXIDIZED_CUT_COPPER_SLAB = register(
		"waxed_oxidized_cut_copper_slab", SlabBlock::new, AbstractBlock.Settings.copy(WAXED_OXIDIZED_CUT_COPPER).requiresTool()
	);
	public static final Block WAXED_WEATHERED_CUT_COPPER_SLAB = register(
		"waxed_weathered_cut_copper_slab", SlabBlock::new, AbstractBlock.Settings.copy(WAXED_WEATHERED_CUT_COPPER).requiresTool()
	);
	public static final Block WAXED_EXPOSED_CUT_COPPER_SLAB = register(
		"waxed_exposed_cut_copper_slab", SlabBlock::new, AbstractBlock.Settings.copy(WAXED_EXPOSED_CUT_COPPER).requiresTool()
	);
	public static final Block WAXED_CUT_COPPER_SLAB = register(
		"waxed_cut_copper_slab", SlabBlock::new, AbstractBlock.Settings.copy(WAXED_CUT_COPPER).requiresTool()
	);
	public static final Block COPPER_DOOR = register(
		"copper_door",
		settings -> new OxidizableDoorBlock(BlockSetType.COPPER, Oxidizable.OxidationLevel.UNAFFECTED, settings),
		AbstractBlock.Settings.create()
			.mapColor(COPPER_BLOCK.getDefaultMapColor())
			.strength(3.0F, 6.0F)
			.nonOpaque()
			.requiresTool()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block EXPOSED_COPPER_DOOR = register(
		"exposed_copper_door",
		settings -> new OxidizableDoorBlock(BlockSetType.COPPER, Oxidizable.OxidationLevel.EXPOSED, settings),
		AbstractBlock.Settings.copy(COPPER_DOOR).mapColor(EXPOSED_COPPER.getDefaultMapColor())
	);
	public static final Block OXIDIZED_COPPER_DOOR = register(
		"oxidized_copper_door",
		settings -> new OxidizableDoorBlock(BlockSetType.COPPER, Oxidizable.OxidationLevel.OXIDIZED, settings),
		AbstractBlock.Settings.copy(COPPER_DOOR).mapColor(OXIDIZED_COPPER.getDefaultMapColor())
	);
	public static final Block WEATHERED_COPPER_DOOR = register(
		"weathered_copper_door",
		settings -> new OxidizableDoorBlock(BlockSetType.COPPER, Oxidizable.OxidationLevel.WEATHERED, settings),
		AbstractBlock.Settings.copy(COPPER_DOOR).mapColor(WEATHERED_COPPER.getDefaultMapColor())
	);
	public static final Block WAXED_COPPER_DOOR = register(
		"waxed_copper_door", settings -> new DoorBlock(BlockSetType.COPPER, settings), AbstractBlock.Settings.copy(COPPER_DOOR)
	);
	public static final Block WAXED_EXPOSED_COPPER_DOOR = register(
		"waxed_exposed_copper_door", settings -> new DoorBlock(BlockSetType.COPPER, settings), AbstractBlock.Settings.copy(EXPOSED_COPPER_DOOR)
	);
	public static final Block WAXED_OXIDIZED_COPPER_DOOR = register(
		"waxed_oxidized_copper_door", settings -> new DoorBlock(BlockSetType.COPPER, settings), AbstractBlock.Settings.copy(OXIDIZED_COPPER_DOOR)
	);
	public static final Block WAXED_WEATHERED_COPPER_DOOR = register(
		"waxed_weathered_copper_door", settings -> new DoorBlock(BlockSetType.COPPER, settings), AbstractBlock.Settings.copy(WEATHERED_COPPER_DOOR)
	);
	public static final Block COPPER_TRAPDOOR = register(
		"copper_trapdoor",
		settings -> new OxidizableTrapdoorBlock(BlockSetType.COPPER, Oxidizable.OxidationLevel.UNAFFECTED, settings),
		AbstractBlock.Settings.create().mapColor(COPPER_BLOCK.getDefaultMapColor()).strength(3.0F, 6.0F).requiresTool().nonOpaque().allowsSpawning(Blocks::never)
	);
	public static final Block EXPOSED_COPPER_TRAPDOOR = register(
		"exposed_copper_trapdoor",
		settings -> new OxidizableTrapdoorBlock(BlockSetType.COPPER, Oxidizable.OxidationLevel.EXPOSED, settings),
		AbstractBlock.Settings.copy(COPPER_TRAPDOOR).mapColor(EXPOSED_COPPER.getDefaultMapColor())
	);
	public static final Block OXIDIZED_COPPER_TRAPDOOR = register(
		"oxidized_copper_trapdoor",
		settings -> new OxidizableTrapdoorBlock(BlockSetType.COPPER, Oxidizable.OxidationLevel.OXIDIZED, settings),
		AbstractBlock.Settings.copy(COPPER_TRAPDOOR).mapColor(OXIDIZED_COPPER.getDefaultMapColor())
	);
	public static final Block WEATHERED_COPPER_TRAPDOOR = register(
		"weathered_copper_trapdoor",
		settings -> new OxidizableTrapdoorBlock(BlockSetType.COPPER, Oxidizable.OxidationLevel.WEATHERED, settings),
		AbstractBlock.Settings.copy(COPPER_TRAPDOOR).mapColor(WEATHERED_COPPER.getDefaultMapColor())
	);
	public static final Block WAXED_COPPER_TRAPDOOR = register(
		"waxed_copper_trapdoor", settings -> new TrapdoorBlock(BlockSetType.COPPER, settings), AbstractBlock.Settings.copy(COPPER_TRAPDOOR)
	);
	public static final Block WAXED_EXPOSED_COPPER_TRAPDOOR = register(
		"waxed_exposed_copper_trapdoor", settings -> new TrapdoorBlock(BlockSetType.COPPER, settings), AbstractBlock.Settings.copy(EXPOSED_COPPER_TRAPDOOR)
	);
	public static final Block WAXED_OXIDIZED_COPPER_TRAPDOOR = register(
		"waxed_oxidized_copper_trapdoor", settings -> new TrapdoorBlock(BlockSetType.COPPER, settings), AbstractBlock.Settings.copy(OXIDIZED_COPPER_TRAPDOOR)
	);
	public static final Block WAXED_WEATHERED_COPPER_TRAPDOOR = register(
		"waxed_weathered_copper_trapdoor", settings -> new TrapdoorBlock(BlockSetType.COPPER, settings), AbstractBlock.Settings.copy(WEATHERED_COPPER_TRAPDOOR)
	);
	public static final Block COPPER_GRATE = register(
		"copper_grate",
		settings -> new OxidizableGrateBlock(Oxidizable.OxidationLevel.UNAFFECTED, settings),
		AbstractBlock.Settings.create()
			.strength(3.0F, 6.0F)
			.sounds(BlockSoundGroup.COPPER_GRATE)
			.mapColor(MapColor.ORANGE)
			.nonOpaque()
			.requiresTool()
			.allowsSpawning(Blocks::never)
			.solidBlock(Blocks::never)
			.suffocates(Blocks::never)
			.blockVision(Blocks::never)
	);
	public static final Block EXPOSED_COPPER_GRATE = register(
		"exposed_copper_grate",
		settings -> new OxidizableGrateBlock(Oxidizable.OxidationLevel.EXPOSED, settings),
		AbstractBlock.Settings.copy(COPPER_GRATE).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
	);
	public static final Block WEATHERED_COPPER_GRATE = register(
		"weathered_copper_grate",
		settings -> new OxidizableGrateBlock(Oxidizable.OxidationLevel.WEATHERED, settings),
		AbstractBlock.Settings.copy(COPPER_GRATE).mapColor(MapColor.DARK_AQUA)
	);
	public static final Block OXIDIZED_COPPER_GRATE = register(
		"oxidized_copper_grate",
		settings -> new OxidizableGrateBlock(Oxidizable.OxidationLevel.OXIDIZED, settings),
		AbstractBlock.Settings.copy(COPPER_GRATE).mapColor(MapColor.TEAL)
	);
	public static final Block WAXED_COPPER_GRATE = register("waxed_copper_grate", GrateBlock::new, AbstractBlock.Settings.copy(COPPER_GRATE));
	public static final Block WAXED_EXPOSED_COPPER_GRATE = register(
		"waxed_exposed_copper_grate", GrateBlock::new, AbstractBlock.Settings.copy(EXPOSED_COPPER_GRATE)
	);
	public static final Block WAXED_WEATHERED_COPPER_GRATE = register(
		"waxed_weathered_copper_grate", GrateBlock::new, AbstractBlock.Settings.copy(WEATHERED_COPPER_GRATE)
	);
	public static final Block WAXED_OXIDIZED_COPPER_GRATE = register(
		"waxed_oxidized_copper_grate", GrateBlock::new, AbstractBlock.Settings.copy(OXIDIZED_COPPER_GRATE)
	);
	public static final Block COPPER_BULB = register(
		"copper_bulb",
		settings -> new OxidizableBulbBlock(Oxidizable.OxidationLevel.UNAFFECTED, settings),
		AbstractBlock.Settings.create()
			.mapColor(COPPER_BLOCK.getDefaultMapColor())
			.strength(3.0F, 6.0F)
			.sounds(BlockSoundGroup.COPPER_BULB)
			.requiresTool()
			.solidBlock(Blocks::never)
			.luminance(createLightLevelFromLitBlockState(15))
	);
	public static final Block EXPOSED_COPPER_BULB = register(
		"exposed_copper_bulb",
		settings -> new OxidizableBulbBlock(Oxidizable.OxidationLevel.EXPOSED, settings),
		AbstractBlock.Settings.copy(COPPER_BULB).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).luminance(createLightLevelFromLitBlockState(12))
	);
	public static final Block WEATHERED_COPPER_BULB = register(
		"weathered_copper_bulb",
		settings -> new OxidizableBulbBlock(Oxidizable.OxidationLevel.WEATHERED, settings),
		AbstractBlock.Settings.copy(COPPER_BULB).mapColor(MapColor.DARK_AQUA).luminance(createLightLevelFromLitBlockState(8))
	);
	public static final Block OXIDIZED_COPPER_BULB = register(
		"oxidized_copper_bulb",
		settings -> new OxidizableBulbBlock(Oxidizable.OxidationLevel.OXIDIZED, settings),
		AbstractBlock.Settings.copy(COPPER_BULB).mapColor(MapColor.TEAL).luminance(createLightLevelFromLitBlockState(4))
	);
	public static final Block WAXED_COPPER_BULB = register("waxed_copper_bulb", BulbBlock::new, AbstractBlock.Settings.copy(COPPER_BULB));
	public static final Block WAXED_EXPOSED_COPPER_BULB = register("waxed_exposed_copper_bulb", BulbBlock::new, AbstractBlock.Settings.copy(EXPOSED_COPPER_BULB));
	public static final Block WAXED_WEATHERED_COPPER_BULB = register(
		"waxed_weathered_copper_bulb", BulbBlock::new, AbstractBlock.Settings.copy(WEATHERED_COPPER_BULB)
	);
	public static final Block WAXED_OXIDIZED_COPPER_BULB = register(
		"waxed_oxidized_copper_bulb", BulbBlock::new, AbstractBlock.Settings.copy(OXIDIZED_COPPER_BULB)
	);
	public static final Block LIGHTNING_ROD = register(
		"lightning_rod",
		LightningRodBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).solid().requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER).nonOpaque()
	);
	public static final Block POINTED_DRIPSTONE = register(
		"pointed_dripstone",
		PointedDripstoneBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_BROWN)
			.solid()
			.instrument(NoteBlockInstrument.BASEDRUM)
			.nonOpaque()
			.sounds(BlockSoundGroup.POINTED_DRIPSTONE)
			.ticksRandomly()
			.strength(1.5F, 3.0F)
			.dynamicBounds()
			.offset(AbstractBlock.OffsetType.XZ)
			.pistonBehavior(PistonBehavior.DESTROY)
			.solidBlock(Blocks::never)
	);
	public static final Block DRIPSTONE_BLOCK = register(
		"dripstone_block",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.TERRACOTTA_BROWN)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.sounds(BlockSoundGroup.DRIPSTONE_BLOCK)
			.requiresTool()
			.strength(1.5F, 1.0F)
	);
	public static final Block CAVE_VINES = register(
		"cave_vines",
		CaveVinesHeadBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.ticksRandomly()
			.noCollision()
			.luminance(CaveVines.getLuminanceSupplier(14))
			.breakInstantly()
			.sounds(BlockSoundGroup.CAVE_VINES)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block CAVE_VINES_PLANT = register(
		"cave_vines_plant",
		CaveVinesBodyBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.luminance(CaveVines.getLuminanceSupplier(14))
			.breakInstantly()
			.sounds(BlockSoundGroup.CAVE_VINES)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SPORE_BLOSSOM = register(
		"spore_blossom",
		SporeBlossomBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.breakInstantly()
			.noCollision()
			.sounds(BlockSoundGroup.SPORE_BLOSSOM)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block AZALEA = register(
		"azalea",
		AzaleaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.notSolid()
			.breakInstantly()
			.sounds(BlockSoundGroup.AZALEA)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block FLOWERING_AZALEA = register(
		"flowering_azalea",
		AzaleaBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.notSolid()
			.breakInstantly()
			.sounds(BlockSoundGroup.FLOWERING_AZALEA)
			.nonOpaque()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block MOSS_CARPET = register(
		"moss_carpet",
		CarpetBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.MOSS_CARPET).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block PINK_PETALS = register(
		"pink_petals",
		FlowerbedBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.DARK_GREEN).noCollision().sounds(BlockSoundGroup.PINK_PETALS).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block MOSS_BLOCK = register(
		"moss_block",
		settings -> new MossBlock(UndergroundConfiguredFeatures.MOSS_PATCH_BONEMEAL, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.MOSS_BLOCK).pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BIG_DRIPLEAF = register(
		"big_dripleaf",
		BigDripleafBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.notSolid()
			.strength(0.1F)
			.sounds(BlockSoundGroup.BIG_DRIPLEAF)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block BIG_DRIPLEAF_STEM = register(
		"big_dripleaf_stem",
		BigDripleafStemBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.strength(0.1F)
			.sounds(BlockSoundGroup.BIG_DRIPLEAF)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block SMALL_DRIPLEAF = register(
		"small_dripleaf",
		SmallDripleafBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.SMALL_DRIPLEAF)
			.offset(AbstractBlock.OffsetType.XYZ)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block HANGING_ROOTS = register(
		"hanging_roots",
		HangingRootsBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DIRT_BROWN)
			.replaceable()
			.noCollision()
			.breakInstantly()
			.sounds(BlockSoundGroup.HANGING_ROOTS)
			.offset(AbstractBlock.OffsetType.XZ)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block ROOTED_DIRT = register(
		"rooted_dirt", RootedDirtBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).strength(0.5F).sounds(BlockSoundGroup.ROOTED_DIRT)
	);
	public static final Block MUD = register(
		"mud",
		MudBlock::new,
		AbstractBlock.Settings.copyShallow(DIRT)
			.mapColor(MapColor.TERRACOTTA_CYAN)
			.allowsSpawning(Blocks::always)
			.solidBlock(Blocks::always)
			.blockVision(Blocks::always)
			.suffocates(Blocks::always)
			.sounds(BlockSoundGroup.MUD)
	);
	public static final Block DEEPSLATE = register(
		"deepslate",
		PillarBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DEEPSLATE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(3.0F, 6.0F)
			.sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block COBBLED_DEEPSLATE = register("cobbled_deepslate", AbstractBlock.Settings.copyShallow(DEEPSLATE).strength(3.5F, 6.0F));
	public static final Block COBBLED_DEEPSLATE_STAIRS = registerOldStairsBlock("cobbled_deepslate_stairs", COBBLED_DEEPSLATE);
	public static final Block COBBLED_DEEPSLATE_SLAB = register("cobbled_deepslate_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE));
	public static final Block COBBLED_DEEPSLATE_WALL = register(
		"cobbled_deepslate_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).solid()
	);
	public static final Block POLISHED_DEEPSLATE = register(
		"polished_deepslate", AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.POLISHED_DEEPSLATE)
	);
	public static final Block POLISHED_DEEPSLATE_STAIRS = registerOldStairsBlock("polished_deepslate_stairs", POLISHED_DEEPSLATE);
	public static final Block POLISHED_DEEPSLATE_SLAB = register("polished_deepslate_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_DEEPSLATE));
	public static final Block POLISHED_DEEPSLATE_WALL = register(
		"polished_deepslate_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(POLISHED_DEEPSLATE).solid()
	);
	public static final Block DEEPSLATE_TILES = register(
		"deepslate_tiles", AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE_TILES)
	);
	public static final Block DEEPSLATE_TILE_STAIRS = registerOldStairsBlock("deepslate_tile_stairs", DEEPSLATE_TILES);
	public static final Block DEEPSLATE_TILE_SLAB = register("deepslate_tile_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(DEEPSLATE_TILES));
	public static final Block DEEPSLATE_TILE_WALL = register("deepslate_tile_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(DEEPSLATE_TILES).solid());
	public static final Block DEEPSLATE_BRICKS = register(
		"deepslate_bricks", AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
	);
	public static final Block DEEPSLATE_BRICK_STAIRS = registerOldStairsBlock("deepslate_brick_stairs", DEEPSLATE_BRICKS);
	public static final Block DEEPSLATE_BRICK_SLAB = register("deepslate_brick_slab", SlabBlock::new, AbstractBlock.Settings.copyShallow(DEEPSLATE_BRICKS));
	public static final Block DEEPSLATE_BRICK_WALL = register("deepslate_brick_wall", WallBlock::new, AbstractBlock.Settings.copyShallow(DEEPSLATE_BRICKS).solid());
	public static final Block CHISELED_DEEPSLATE = register(
		"chiseled_deepslate", AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
	);
	public static final Block CRACKED_DEEPSLATE_BRICKS = register("cracked_deepslate_bricks", AbstractBlock.Settings.copyShallow(DEEPSLATE_BRICKS));
	public static final Block CRACKED_DEEPSLATE_TILES = register("cracked_deepslate_tiles", AbstractBlock.Settings.copyShallow(DEEPSLATE_TILES));
	public static final Block INFESTED_DEEPSLATE = register(
		"infested_deepslate",
		settings -> new RotatedInfestedBlock(DEEPSLATE, settings),
		AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).sounds(BlockSoundGroup.DEEPSLATE)
	);
	public static final Block SMOOTH_BASALT = register("smooth_basalt", AbstractBlock.Settings.copyShallow(BASALT));
	public static final Block RAW_IRON_BLOCK = register(
		"raw_iron_block",
		AbstractBlock.Settings.create().mapColor(MapColor.RAW_IRON_PINK).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F)
	);
	public static final Block RAW_COPPER_BLOCK = register(
		"raw_copper_block", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F)
	);
	public static final Block RAW_GOLD_BLOCK = register(
		"raw_gold_block", AbstractBlock.Settings.create().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F)
	);
	public static final Block POTTED_AZALEA_BUSH = register("potted_azalea_bush", settings -> new FlowerPotBlock(AZALEA, settings), createFlowerPotSettings());
	public static final Block POTTED_FLOWERING_AZALEA_BUSH = register(
		"potted_flowering_azalea_bush", settings -> new FlowerPotBlock(FLOWERING_AZALEA, settings), createFlowerPotSettings()
	);
	public static final Block OCHRE_FROGLIGHT = register(
		"ochre_froglight",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.3F).luminance(state -> 15).sounds(BlockSoundGroup.FROGLIGHT)
	);
	public static final Block VERDANT_FROGLIGHT = register(
		"verdant_froglight",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.LICHEN_GREEN).strength(0.3F).luminance(state -> 15).sounds(BlockSoundGroup.FROGLIGHT)
	);
	public static final Block PEARLESCENT_FROGLIGHT = register(
		"pearlescent_froglight",
		PillarBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PINK).strength(0.3F).luminance(state -> 15).sounds(BlockSoundGroup.FROGLIGHT)
	);
	public static final Block FROGSPAWN = register(
		"frogspawn",
		FrogspawnBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.WATER_BLUE)
			.breakInstantly()
			.nonOpaque()
			.noCollision()
			.sounds(BlockSoundGroup.FROGSPAWN)
			.pistonBehavior(PistonBehavior.DESTROY)
	);
	public static final Block REINFORCED_DEEPSLATE = register(
		"reinforced_deepslate",
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DEEPSLATE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.sounds(BlockSoundGroup.DEEPSLATE)
			.strength(55.0F, 1200.0F)
	);
	public static final Block DECORATED_POT = register(
		"decorated_pot",
		DecoratedPotBlock::new,
		AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_RED).strength(0.0F, 0.0F).pistonBehavior(PistonBehavior.DESTROY).nonOpaque()
	);
	public static final Block CRAFTER = register("crafter", CrafterBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5F, 3.5F));
	public static final Block TRIAL_SPAWNER = register(
		"trial_spawner",
		TrialSpawnerBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.luminance(state -> ((TrialSpawnerState)state.get(TrialSpawnerBlock.TRIAL_SPAWNER_STATE)).getLuminance())
			.strength(50.0F)
			.sounds(BlockSoundGroup.TRIAL_SPAWNER)
			.blockVision(Blocks::never)
			.nonOpaque()
	);
	public static final Block VAULT = register(
		"vault",
		VaultBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.nonOpaque()
			.sounds(BlockSoundGroup.VAULT)
			.luminance(state -> ((VaultState)state.get(VaultBlock.VAULT_STATE)).getLuminance())
			.strength(50.0F)
			.blockVision(Blocks::never)
	);
	public static final Block HEAVY_CORE = register(
		"heavy_core",
		HeavyCoreBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.IRON_GRAY)
			.instrument(NoteBlockInstrument.SNARE)
			.sounds(BlockSoundGroup.HEAVY_CORE)
			.strength(10.0F)
			.pistonBehavior(PistonBehavior.NORMAL)
			.resistance(1200.0F)
	);
	public static final Block PALE_MOSS_BLOCK = register(
		"pale_moss_block",
		settings -> new MossBlock(VegetationConfiguredFeatures.PALE_MOSS_PATCH_BONEMEAL, settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.GRAY)
			.strength(0.1F)
			.sounds(BlockSoundGroup.MOSS_BLOCK)
			.pistonBehavior(PistonBehavior.DESTROY)
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block PALE_MOSS_CARPET = register(
		"pale_moss_carpet",
		PaleMossCarpetBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(PALE_MOSS_BLOCK.getDefaultMapColor())
			.strength(0.1F)
			.sounds(BlockSoundGroup.MOSS_CARPET)
			.pistonBehavior(PistonBehavior.DESTROY)
			.requires(FeatureFlags.WINTER_DROP)
	);
	public static final Block PALE_HANGING_MOSS = register(
		"pale_hanging_moss",
		HangingMossBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(PALE_MOSS_BLOCK.getDefaultMapColor())
			.strength(0.1F)
			.noCollision()
			.sounds(BlockSoundGroup.MOSS_CARPET)
			.pistonBehavior(PistonBehavior.DESTROY)
			.requires(FeatureFlags.WINTER_DROP)
	);

	private static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
		return state -> state.get(Properties.LIT) ? litLevel : 0;
	}

	private static Function<BlockState, MapColor> createMapColorFromWaterloggedBlockState(MapColor mapColor) {
		return state -> state.get(Properties.WATERLOGGED) ? MapColor.WATER_BLUE : mapColor;
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

	private static Block registerBedBlock(String id, DyeColor color) {
		return register(
			id,
			settings -> new BedBlock(color, settings),
			AbstractBlock.Settings.create()
				.mapColor(state -> state.get(BedBlock.PART) == BedPart.FOOT ? color.getMapColor() : MapColor.WHITE_GRAY)
				.sounds(BlockSoundGroup.WOOD)
				.strength(0.2F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		);
	}

	private static AbstractBlock.Settings createLogSettings(MapColor topMapColor, MapColor sideMapColor, BlockSoundGroup sounds) {
		return AbstractBlock.Settings.create()
			.mapColor(state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.0F)
			.sounds(sounds)
			.burnable();
	}

	private static AbstractBlock.Settings createNetherStemSettings(MapColor mapColor) {
		return AbstractBlock.Settings.create().mapColor(state -> mapColor).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM);
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

	private static Block registerStainedGlassBlock(String id, DyeColor color) {
		return register(
			id,
			settings -> new StainedGlassBlock(color, settings),
			AbstractBlock.Settings.create()
				.mapColor(color)
				.instrument(NoteBlockInstrument.HAT)
				.strength(0.3F)
				.sounds(BlockSoundGroup.GLASS)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.solidBlock(Blocks::never)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		);
	}

	private static AbstractBlock.Settings createLeavesSettings(BlockSoundGroup sounds) {
		return AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.strength(0.2F)
			.ticksRandomly()
			.sounds(sounds)
			.nonOpaque()
			.allowsSpawning(Blocks::canSpawnOnLeaves)
			.suffocates(Blocks::never)
			.blockVision(Blocks::never)
			.burnable()
			.pistonBehavior(PistonBehavior.DESTROY)
			.solidBlock(Blocks::never);
	}

	private static AbstractBlock.Settings createShulkerBoxSettings(MapColor mapColor) {
		return AbstractBlock.Settings.create()
			.mapColor(mapColor)
			.solid()
			.strength(2.0F)
			.dynamicBounds()
			.nonOpaque()
			.suffocates(SHULKER_BOX_SUFFOCATES_PREDICATE)
			.blockVision(SHULKER_BOX_SUFFOCATES_PREDICATE)
			.pistonBehavior(PistonBehavior.DESTROY);
	}

	private static AbstractBlock.Settings createPistonSettings() {
		return AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.strength(1.5F)
			.solidBlock(Blocks::never)
			.suffocates(PISTON_SUFFOCATES_PREDICATE)
			.blockVision(PISTON_SUFFOCATES_PREDICATE)
			.pistonBehavior(PistonBehavior.BLOCK);
	}

	private static AbstractBlock.Settings createButtonSettings() {
		return AbstractBlock.Settings.create().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY);
	}

	private static AbstractBlock.Settings createFlowerPotSettings() {
		return AbstractBlock.Settings.create().breakInstantly().nonOpaque().pistonBehavior(PistonBehavior.DESTROY);
	}

	private static AbstractBlock.Settings createCandleSettings(MapColor mapColor) {
		return AbstractBlock.Settings.create()
			.mapColor(mapColor)
			.nonOpaque()
			.strength(0.1F)
			.sounds(BlockSoundGroup.CANDLE)
			.luminance(CandleBlock.STATE_TO_LUMINANCE)
			.pistonBehavior(PistonBehavior.DESTROY);
	}

	@Deprecated
	private static Block registerOldStairsBlock(String id, Block base) {
		return register(id, settings -> new StairsBlock(base.getDefaultState(), settings), AbstractBlock.Settings.copyShallow(base));
	}

	private static Block registerStairsBlock(String id, Block base) {
		return register(id, settings -> new StairsBlock(base.getDefaultState(), settings), AbstractBlock.Settings.copy(base));
	}

	private static AbstractBlock.Settings copyLootTable(Block block, boolean copyTranslationKey) {
		AbstractBlock.Settings settings = block.getSettings();
		AbstractBlock.Settings settings2 = AbstractBlock.Settings.create().lootTable(block.getLootTableKey());
		if (copyTranslationKey) {
			settings2 = settings2.overrideTranslationKey(block.getTranslationKey());
		}

		return settings2;
	}

	private static Block register(RegistryKey<Block> key, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
		Block block = (Block)factory.apply(settings.registryKey(key));
		return Registry.register(Registries.BLOCK, key, block);
	}

	private static Block register(RegistryKey<Block> key, AbstractBlock.Settings settings) {
		return register(key, Block::new, settings);
	}

	private static RegistryKey<Block> keyOf(String id) {
		return RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla(id));
	}

	private static Block register(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
		return register(keyOf(id), factory, settings);
	}

	private static Block register(String id, AbstractBlock.Settings settings) {
		return register(id, Block::new, settings);
	}

	static {
		for (Block block : Registries.BLOCK) {
			for (BlockState blockState : block.getStateManager().getStates()) {
				Block.STATE_IDS.add(blockState);
				blockState.initShapeCache();
			}
		}
	}
}
