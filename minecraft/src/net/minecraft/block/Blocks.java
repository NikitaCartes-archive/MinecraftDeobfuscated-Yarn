package net.minecraft.block;

import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.Instrument;
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
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ColorCode;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

/**
 * Contains all the minecraft blocks.
 */
public class Blocks {
	private static final AbstractBlock.ContextPredicate SHULKER_BOX_SUFFOCATES_PREDICATE = (state, world, pos) -> world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity
			? shulkerBoxBlockEntity.suffocates()
			: true;
	public static final Block AIR = register("air", new AirBlock(AbstractBlock.Settings.create().replaceable().noCollision().dropsNothing().air()));
	public static final Block STONE = register(
		"stone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block GRANITE = register(
		"granite", new Block(AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_GRANITE = register(
		"polished_granite",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DIORITE = register(
		"diorite", new Block(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_DIORITE = register(
		"polished_diorite",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block ANDESITE = register(
		"andesite", new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_ANDESITE = register(
		"polished_andesite",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block GRASS_BLOCK = register(
		"grass_block", new GrassBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_GREEN).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block DIRT = register(
		"dirt", new Block(AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block COARSE_DIRT = register(
		"coarse_dirt", new Block(AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block PODZOL = register(
		"podzol", new SnowyBlock(AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).strength(0.5F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block COBBLESTONE = register(
		"cobblestone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block OAK_PLANKS = register(
		"oak_planks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block SPRUCE_PLANKS = register(
		"spruce_planks",
		new Block(
			AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block BIRCH_PLANKS = register(
		"birch_planks",
		new Block(
			AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block JUNGLE_PLANKS = register(
		"jungle_planks",
		new Block(
			AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block ACACIA_PLANKS = register(
		"acacia_planks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block CHERRY_PLANKS = register(
		"cherry_planks",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_WHITE)
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.sounds(BlockSoundGroup.CHERRY_WOOD)
				.burnable()
		)
	);
	public static final Block DARK_OAK_PLANKS = register(
		"dark_oak_planks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block MANGROVE_PLANKS = register(
		"mangrove_planks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block BAMBOO_PLANKS = register(
		"bamboo_planks",
		new Block(
			AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.BAMBOO_WOOD).burnable()
		)
	);
	public static final Block BAMBOO_MOSAIC = register(
		"bamboo_mosaic",
		new Block(
			AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.BAMBOO_WOOD).burnable()
		)
	);
	public static final Block OAK_SAPLING = register(
		"oak_sapling",
		new SaplingBlock(
			SaplingGenerator.OAK,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SPRUCE_SAPLING = register(
		"spruce_sapling",
		new SaplingBlock(
			SaplingGenerator.SPRUCE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BIRCH_SAPLING = register(
		"birch_sapling",
		new SaplingBlock(
			SaplingGenerator.BIRCH,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block JUNGLE_SAPLING = register(
		"jungle_sapling",
		new SaplingBlock(
			SaplingGenerator.JUNGLE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block ACACIA_SAPLING = register(
		"acacia_sapling",
		new SaplingBlock(
			SaplingGenerator.ACACIA,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CHERRY_SAPLING = register(
		"cherry_sapling",
		new SaplingBlock(
			SaplingGenerator.CHERRY,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PINK)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.CHERRY_SAPLING)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block DARK_OAK_SAPLING = register(
		"dark_oak_sapling",
		new SaplingBlock(
			SaplingGenerator.DARK_OAK,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block MANGROVE_PROPAGULE = register(
		"mangrove_propagule",
		new PropaguleBlock(
			SaplingGenerator.MANGROVE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BEDROCK = register(
		"bedrock",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.instrument(Instrument.BASEDRUM)
				.strength(-1.0F, 3600000.0F)
				.dropsNothing()
				.allowsSpawning(Blocks::never)
		)
	);
	public static final Block WATER = register(
		"water",
		new FluidBlock(
			Fluids.WATER,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WATER_BLUE)
				.replaceable()
				.noCollision()
				.strength(100.0F)
				.pistonBehavior(PistonBehavior.DESTROY)
				.dropsNothing()
				.liquid()
				.sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
		)
	);
	public static final Block LAVA = register(
		"lava",
		new FluidBlock(
			Fluids.LAVA,
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
		)
	);
	public static final Block SAND = register(
		"sand",
		new ColoredFallingBlock(
			new ColorCode(14406560),
			AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block SUSPICIOUS_SAND = register(
		"suspicious_sand",
		new BrushableBlock(
			SAND,
			SoundEvents.ITEM_BRUSH_BRUSHING_SAND,
			SoundEvents.ITEM_BRUSH_BRUSHING_SAND_COMPLETE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PALE_YELLOW)
				.instrument(Instrument.SNARE)
				.strength(0.25F)
				.sounds(BlockSoundGroup.SUSPICIOUS_SAND)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block RED_SAND = register(
		"red_sand",
		new ColoredFallingBlock(
			new ColorCode(11098145), AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block GRAVEL = register(
		"gravel",
		new ColoredFallingBlock(
			new ColorCode(-8356741),
			AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.SNARE).strength(0.6F).sounds(BlockSoundGroup.GRAVEL)
		)
	);
	public static final Block SUSPICIOUS_GRAVEL = register(
		"suspicious_gravel",
		new BrushableBlock(
			GRAVEL,
			SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
			SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.instrument(Instrument.SNARE)
				.strength(0.25F)
				.sounds(BlockSoundGroup.SUSPICIOUS_GRAVEL)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block GOLD_ORE = register(
		"gold_ore",
		new ExperienceDroppingBlock(
			ConstantIntProvider.create(0),
			AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
		)
	);
	public static final Block DEEPSLATE_GOLD_ORE = register(
		"deepslate_gold_ore",
		new ExperienceDroppingBlock(
			ConstantIntProvider.create(0),
			AbstractBlock.Settings.copyShallow(GOLD_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
		)
	);
	public static final Block IRON_ORE = register(
		"iron_ore",
		new ExperienceDroppingBlock(
			ConstantIntProvider.create(0),
			AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
		)
	);
	public static final Block DEEPSLATE_IRON_ORE = register(
		"deepslate_iron_ore",
		new ExperienceDroppingBlock(
			ConstantIntProvider.create(0),
			AbstractBlock.Settings.copyShallow(IRON_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
		)
	);
	public static final Block COAL_ORE = register(
		"coal_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(0, 2),
			AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
		)
	);
	public static final Block DEEPSLATE_COAL_ORE = register(
		"deepslate_coal_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(0, 2),
			AbstractBlock.Settings.copyShallow(COAL_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
		)
	);
	public static final Block NETHER_GOLD_ORE = register(
		"nether_gold_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(0, 1),
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(3.0F, 3.0F)
				.sounds(BlockSoundGroup.NETHER_GOLD_ORE)
		)
	);
	public static final Block OAK_LOG = register("oak_log", createLogBlock(MapColor.OAK_TAN, MapColor.SPRUCE_BROWN));
	public static final Block SPRUCE_LOG = register("spruce_log", createLogBlock(MapColor.SPRUCE_BROWN, MapColor.BROWN));
	public static final Block BIRCH_LOG = register("birch_log", createLogBlock(MapColor.PALE_YELLOW, MapColor.OFF_WHITE));
	public static final Block JUNGLE_LOG = register("jungle_log", createLogBlock(MapColor.DIRT_BROWN, MapColor.SPRUCE_BROWN));
	public static final Block ACACIA_LOG = register("acacia_log", createLogBlock(MapColor.ORANGE, MapColor.STONE_GRAY));
	public static final Block CHERRY_LOG = register("cherry_log", createLogBlock(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_GRAY, BlockSoundGroup.CHERRY_WOOD));
	public static final Block DARK_OAK_LOG = register("dark_oak_log", createLogBlock(MapColor.BROWN, MapColor.BROWN));
	public static final Block MANGROVE_LOG = register("mangrove_log", createLogBlock(MapColor.RED, MapColor.SPRUCE_BROWN));
	public static final Block MANGROVE_ROOTS = register(
		"mangrove_roots",
		new MangroveRootsBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.SPRUCE_BROWN)
				.instrument(Instrument.BASS)
				.strength(0.7F)
				.sounds(BlockSoundGroup.MANGROVE_ROOTS)
				.nonOpaque()
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
				.nonOpaque()
				.burnable()
		)
	);
	public static final Block MUDDY_MANGROVE_ROOTS = register(
		"muddy_mangrove_roots",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).strength(0.7F).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS))
	);
	public static final Block BAMBOO_BLOCK = register("bamboo_block", createLogBlock(MapColor.YELLOW, MapColor.DARK_GREEN, BlockSoundGroup.BAMBOO_WOOD));
	public static final Block STRIPPED_SPRUCE_LOG = register("stripped_spruce_log", createLogBlock(MapColor.SPRUCE_BROWN, MapColor.SPRUCE_BROWN));
	public static final Block STRIPPED_BIRCH_LOG = register("stripped_birch_log", createLogBlock(MapColor.PALE_YELLOW, MapColor.PALE_YELLOW));
	public static final Block STRIPPED_JUNGLE_LOG = register("stripped_jungle_log", createLogBlock(MapColor.DIRT_BROWN, MapColor.DIRT_BROWN));
	public static final Block STRIPPED_ACACIA_LOG = register("stripped_acacia_log", createLogBlock(MapColor.ORANGE, MapColor.ORANGE));
	public static final Block STRIPPED_CHERRY_LOG = register(
		"stripped_cherry_log", createLogBlock(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_PINK, BlockSoundGroup.CHERRY_WOOD)
	);
	public static final Block STRIPPED_DARK_OAK_LOG = register("stripped_dark_oak_log", createLogBlock(MapColor.BROWN, MapColor.BROWN));
	public static final Block STRIPPED_OAK_LOG = register("stripped_oak_log", createLogBlock(MapColor.OAK_TAN, MapColor.OAK_TAN));
	public static final Block STRIPPED_MANGROVE_LOG = register("stripped_mangrove_log", createLogBlock(MapColor.RED, MapColor.RED));
	public static final Block STRIPPED_BAMBOO_BLOCK = register(
		"stripped_bamboo_block", createLogBlock(MapColor.YELLOW, MapColor.YELLOW, BlockSoundGroup.BAMBOO_WOOD)
	);
	public static final Block OAK_WOOD = register(
		"oak_wood",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block SPRUCE_WOOD = register(
		"spruce_wood",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block BIRCH_WOOD = register(
		"birch_wood",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block JUNGLE_WOOD = register(
		"jungle_wood",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block ACACIA_WOOD = register(
		"acacia_wood",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block CHERRY_WOOD = register(
		"cherry_wood",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_GRAY).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.CHERRY_WOOD).burnable()
		)
	);
	public static final Block DARK_OAK_WOOD = register(
		"dark_oak_wood",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block MANGROVE_WOOD = register(
		"mangrove_wood",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block STRIPPED_OAK_WOOD = register(
		"stripped_oak_wood",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block STRIPPED_SPRUCE_WOOD = register(
		"stripped_spruce_wood",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block STRIPPED_BIRCH_WOOD = register(
		"stripped_birch_wood",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block STRIPPED_JUNGLE_WOOD = register(
		"stripped_jungle_wood",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block STRIPPED_ACACIA_WOOD = register(
		"stripped_acacia_wood",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block STRIPPED_CHERRY_WOOD = register(
		"stripped_cherry_wood",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_PINK).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.CHERRY_WOOD).burnable()
		)
	);
	public static final Block STRIPPED_DARK_OAK_WOOD = register(
		"stripped_dark_oak_wood",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block STRIPPED_MANGROVE_WOOD = register("stripped_mangrove_wood", createLogBlock(MapColor.RED, MapColor.RED));
	public static final Block OAK_LEAVES = register("oak_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block SPRUCE_LEAVES = register("spruce_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block BIRCH_LEAVES = register("birch_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block JUNGLE_LEAVES = register("jungle_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block ACACIA_LEAVES = register("acacia_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block CHERRY_LEAVES = register(
		"cherry_leaves",
		new CherryLeavesBlock(
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
		)
	);
	public static final Block DARK_OAK_LEAVES = register("dark_oak_leaves", createLeavesBlock(BlockSoundGroup.GRASS));
	public static final Block MANGROVE_LEAVES = register(
		"mangrove_leaves",
		new MangroveLeavesBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
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
		)
	);
	public static final Block AZALEA_LEAVES = register("azalea_leaves", createLeavesBlock(BlockSoundGroup.AZALEA_LEAVES));
	public static final Block FLOWERING_AZALEA_LEAVES = register("flowering_azalea_leaves", createLeavesBlock(BlockSoundGroup.AZALEA_LEAVES));
	public static final Block SPONGE = register(
		"sponge", new SpongeBlock(AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).strength(0.6F).sounds(BlockSoundGroup.SPONGE))
	);
	public static final Block WET_SPONGE = register(
		"wet_sponge", new WetSpongeBlock(AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).strength(0.6F).sounds(BlockSoundGroup.WET_SPONGE))
	);
	public static final Block GLASS = register(
		"glass",
		new TransparentBlock(
			AbstractBlock.Settings.create()
				.instrument(Instrument.HAT)
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
		"lapis_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(2, 5),
			AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
		)
	);
	public static final Block DEEPSLATE_LAPIS_ORE = register(
		"deepslate_lapis_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(2, 5),
			AbstractBlock.Settings.copyShallow(LAPIS_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
		)
	);
	public static final Block LAPIS_BLOCK = register(
		"lapis_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.LAPIS_BLUE).requiresTool().strength(3.0F, 3.0F))
	);
	public static final Block DISPENSER = register(
		"dispenser", new DispenserBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.5F))
	);
	public static final Block SANDSTONE = register(
		"sandstone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(0.8F))
	);
	public static final Block CHISELED_SANDSTONE = register(
		"chiseled_sandstone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(0.8F))
	);
	public static final Block CUT_SANDSTONE = register(
		"cut_sandstone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(0.8F))
	);
	public static final Block NOTE_BLOCK = register(
		"note_block",
		new NoteBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).sounds(BlockSoundGroup.WOOD).strength(0.8F).burnable())
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
		"powered_rail", new PoweredRailBlock(AbstractBlock.Settings.create().noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block DETECTOR_RAIL = register(
		"detector_rail", new DetectorRailBlock(AbstractBlock.Settings.create().noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block STICKY_PISTON = register("sticky_piston", createPistonBlock(true));
	public static final Block COBWEB = register(
		"cobweb",
		new CobwebBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WHITE_GRAY)
				.sounds(BlockSoundGroup.COBWEB)
				.solid()
				.noCollision()
				.requiresTool()
				.strength(4.0F)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SHORT_GRASS = register(
		"short_grass",
		new ShortPlantBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XYZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block FERN = register(
		"fern",
		new ShortPlantBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XYZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block DEAD_BUSH = register(
		"dead_bush",
		new DeadBushBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SEAGRASS = register(
		"seagrass",
		new SeagrassBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WATER_BLUE)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block TALL_SEAGRASS = register(
		"tall_seagrass",
		new TallSeagrassBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WATER_BLUE)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block PISTON = register("piston", createPistonBlock(false));
	public static final Block PISTON_HEAD = register(
		"piston_head",
		new PistonHeadBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5F).dropsNothing().pistonBehavior(PistonBehavior.BLOCK))
	);
	public static final Block WHITE_WOOL = register(
		"white_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block ORANGE_WOOL = register(
		"orange_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block MAGENTA_WOOL = register(
		"magenta_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block LIGHT_BLUE_WOOL = register(
		"light_blue_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block YELLOW_WOOL = register(
		"yellow_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block LIME_WOOL = register(
		"lime_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block PINK_WOOL = register(
		"pink_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.PINK).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block GRAY_WOOL = register(
		"gray_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block LIGHT_GRAY_WOOL = register(
		"light_gray_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_GRAY).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block CYAN_WOOL = register(
		"cyan_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.CYAN).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block PURPLE_WOOL = register(
		"purple_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block BLUE_WOOL = register(
		"blue_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.BLUE).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block BROWN_WOOL = register(
		"brown_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block GREEN_WOOL = register(
		"green_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block RED_WOOL = register(
		"red_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block BLACK_WOOL = register(
		"black_wool",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(Instrument.GUITAR).strength(0.8F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block MOVING_PISTON = register(
		"moving_piston",
		new PistonExtensionBlock(
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
		)
	);
	public static final Block DANDELION = register(
		"dandelion",
		new FlowerBlock(
			StatusEffects.SATURATION,
			0.35F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block TORCHFLOWER = register(
		"torchflower",
		new FlowerBlock(
			StatusEffects.NIGHT_VISION,
			5.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block POPPY = register(
		"poppy",
		new FlowerBlock(
			StatusEffects.NIGHT_VISION,
			5.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BLUE_ORCHID = register(
		"blue_orchid",
		new FlowerBlock(
			StatusEffects.SATURATION,
			0.35F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block ALLIUM = register(
		"allium",
		new FlowerBlock(
			StatusEffects.FIRE_RESISTANCE,
			4.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block AZURE_BLUET = register(
		"azure_bluet",
		new FlowerBlock(
			StatusEffects.BLINDNESS,
			8.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block RED_TULIP = register(
		"red_tulip",
		new FlowerBlock(
			StatusEffects.WEAKNESS,
			9.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block ORANGE_TULIP = register(
		"orange_tulip",
		new FlowerBlock(
			StatusEffects.WEAKNESS,
			9.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block WHITE_TULIP = register(
		"white_tulip",
		new FlowerBlock(
			StatusEffects.WEAKNESS,
			9.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block PINK_TULIP = register(
		"pink_tulip",
		new FlowerBlock(
			StatusEffects.WEAKNESS,
			9.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block OXEYE_DAISY = register(
		"oxeye_daisy",
		new FlowerBlock(
			StatusEffects.REGENERATION,
			8.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CORNFLOWER = register(
		"cornflower",
		new FlowerBlock(
			StatusEffects.JUMP_BOOST,
			6.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block WITHER_ROSE = register(
		"wither_rose",
		new WitherRoseBlock(
			StatusEffects.WITHER,
			8.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block LILY_OF_THE_VALLEY = register(
		"lily_of_the_valley",
		new FlowerBlock(
			StatusEffects.POISON,
			12.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BROWN_MUSHROOM = register(
		"brown_mushroom",
		new MushroomPlantBlock(
			TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BROWN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.luminance(state -> 1)
				.postProcess(Blocks::always)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block RED_MUSHROOM = register(
		"red_mushroom",
		new MushroomPlantBlock(
			TreeConfiguredFeatures.HUGE_RED_MUSHROOM,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.RED)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.postProcess(Blocks::always)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block GOLD_BLOCK = register(
		"gold_block",
		new Block(
			AbstractBlock.Settings.create().mapColor(MapColor.GOLD).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL)
		)
	);
	public static final Block IRON_BLOCK = register(
		"iron_block",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.instrument(Instrument.IRON_XYLOPHONE)
				.requiresTool()
				.strength(5.0F, 6.0F)
				.sounds(BlockSoundGroup.METAL)
		)
	);
	public static final Block BRICKS = register(
		"bricks", new Block(AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block TNT = register(
		"tnt",
		new TntBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_RED).breakInstantly().sounds(BlockSoundGroup.GRASS).burnable().solidBlock(Blocks::never)
		)
	);
	public static final Block BOOKSHELF = register(
		"bookshelf",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(1.5F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block CHISELED_BOOKSHELF = register(
		"chiseled_bookshelf",
		new ChiseledBookshelfBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(1.5F).sounds(BlockSoundGroup.CHISELED_BOOKSHELF).burnable()
		)
	);
	public static final Block MOSSY_COBBLESTONE = register(
		"mossy_cobblestone",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block OBSIDIAN = register(
		"obsidian", new Block(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(50.0F, 1200.0F))
	);
	public static final Block TORCH = register(
		"torch",
		new TorchBlock(
			ParticleTypes.FLAME,
			AbstractBlock.Settings.create().noCollision().breakInstantly().luminance(state -> 14).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block WALL_TORCH = register(
		"wall_torch",
		new WallTorchBlock(
			ParticleTypes.FLAME,
			AbstractBlock.Settings.create()
				.noCollision()
				.breakInstantly()
				.luminance(state -> 14)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(TORCH)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block FIRE = register(
		"fire",
		new FireBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BRIGHT_RED)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.luminance(state -> 15)
				.sounds(BlockSoundGroup.WOOL)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SOUL_FIRE = register(
		"soul_fire",
		new SoulFireBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.LIGHT_BLUE)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.luminance(state -> 10)
				.sounds(BlockSoundGroup.WOOL)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SPAWNER = register(
		"spawner",
		new SpawnerBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(5.0F)
				.sounds(BlockSoundGroup.METAL)
				.nonOpaque()
		)
	);
	public static final Block OAK_STAIRS = register("oak_stairs", createOldStairsBlock(OAK_PLANKS));
	public static final Block CHEST = register(
		"chest",
		new ChestBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable(),
			() -> BlockEntityType.CHEST
		)
	);
	public static final Block REDSTONE_WIRE = register(
		"redstone_wire", new RedstoneWireBlock(AbstractBlock.Settings.create().noCollision().breakInstantly().pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block DIAMOND_ORE = register(
		"diamond_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(3, 7),
			AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
		)
	);
	public static final Block DEEPSLATE_DIAMOND_ORE = register(
		"deepslate_diamond_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(3, 7),
			AbstractBlock.Settings.copyShallow(DIAMOND_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
		)
	);
	public static final Block DIAMOND_BLOCK = register(
		"diamond_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block CRAFTING_TABLE = register(
		"crafting_table",
		new CraftingTableBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block WHEAT = register(
		"wheat",
		new CropBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.CROP)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block FARMLAND = register(
		"farmland",
		new FarmlandBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DIRT_BROWN)
				.ticksRandomly()
				.strength(0.6F)
				.sounds(BlockSoundGroup.GRAVEL)
				.blockVision(Blocks::always)
				.suffocates(Blocks::always)
		)
	);
	public static final Block FURNACE = register(
		"furnace",
		new FurnaceBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(3.5F)
				.luminance(createLightLevelFromLitBlockState(13))
		)
	);
	public static final Block OAK_SIGN = register(
		"oak_sign",
		new SignBlock(
			WoodType.OAK, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block SPRUCE_SIGN = register(
		"spruce_sign",
		new SignBlock(
			WoodType.SPRUCE,
			AbstractBlock.Settings.create().mapColor(SPRUCE_LOG.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block BIRCH_SIGN = register(
		"birch_sign",
		new SignBlock(
			WoodType.BIRCH, AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block ACACIA_SIGN = register(
		"acacia_sign",
		new SignBlock(
			WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block CHERRY_SIGN = register(
		"cherry_sign",
		new SignBlock(
			WoodType.CHERRY,
			AbstractBlock.Settings.create().mapColor(CHERRY_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block JUNGLE_SIGN = register(
		"jungle_sign",
		new SignBlock(
			WoodType.JUNGLE,
			AbstractBlock.Settings.create().mapColor(JUNGLE_LOG.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block DARK_OAK_SIGN = register(
		"dark_oak_sign",
		new SignBlock(
			WoodType.DARK_OAK,
			AbstractBlock.Settings.create().mapColor(DARK_OAK_LOG.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block MANGROVE_SIGN = register(
		"mangrove_sign",
		new SignBlock(
			WoodType.MANGROVE,
			AbstractBlock.Settings.create().mapColor(MANGROVE_LOG.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block BAMBOO_SIGN = register(
		"bamboo_sign",
		new SignBlock(
			WoodType.BAMBOO,
			AbstractBlock.Settings.create().mapColor(BAMBOO_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block OAK_DOOR = register(
		"oak_door",
		new DoorBlock(
			BlockSetType.OAK,
			AbstractBlock.Settings.create()
				.mapColor(OAK_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block LADDER = register(
		"ladder",
		new LadderBlock(AbstractBlock.Settings.create().notSolid().strength(0.4F).sounds(BlockSoundGroup.LADDER).nonOpaque().pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block RAIL = register("rail", new RailBlock(AbstractBlock.Settings.create().noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL)));
	public static final Block COBBLESTONE_STAIRS = register("cobblestone_stairs", createOldStairsBlock(COBBLESTONE));
	public static final Block OAK_WALL_SIGN = register(
		"oak_wall_sign",
		new WallSignBlock(
			WoodType.OAK,
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).dropsLike(OAK_SIGN).burnable()
		)
	);
	public static final Block SPRUCE_WALL_SIGN = register(
		"spruce_wall_sign",
		new WallSignBlock(
			WoodType.SPRUCE,
			AbstractBlock.Settings.create()
				.mapColor(SPRUCE_LOG.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.dropsLike(SPRUCE_SIGN)
				.burnable()
		)
	);
	public static final Block BIRCH_WALL_SIGN = register(
		"birch_wall_sign",
		new WallSignBlock(
			WoodType.BIRCH,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PALE_YELLOW)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.dropsLike(BIRCH_SIGN)
				.burnable()
		)
	);
	public static final Block ACACIA_WALL_SIGN = register(
		"acacia_wall_sign",
		new WallSignBlock(
			WoodType.ACACIA,
			AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).dropsLike(ACACIA_SIGN).burnable()
		)
	);
	public static final Block CHERRY_WALL_SIGN = register(
		"cherry_wall_sign",
		new WallSignBlock(
			WoodType.CHERRY,
			AbstractBlock.Settings.create()
				.mapColor(CHERRY_LOG.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.dropsLike(CHERRY_SIGN)
				.burnable()
		)
	);
	public static final Block JUNGLE_WALL_SIGN = register(
		"jungle_wall_sign",
		new WallSignBlock(
			WoodType.JUNGLE,
			AbstractBlock.Settings.create()
				.mapColor(JUNGLE_LOG.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.dropsLike(JUNGLE_SIGN)
				.burnable()
		)
	);
	public static final Block DARK_OAK_WALL_SIGN = register(
		"dark_oak_wall_sign",
		new WallSignBlock(
			WoodType.DARK_OAK,
			AbstractBlock.Settings.create()
				.mapColor(DARK_OAK_LOG.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.dropsLike(DARK_OAK_SIGN)
				.burnable()
		)
	);
	public static final Block MANGROVE_WALL_SIGN = register(
		"mangrove_wall_sign",
		new WallSignBlock(
			WoodType.MANGROVE,
			AbstractBlock.Settings.create()
				.mapColor(MANGROVE_LOG.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.dropsLike(MANGROVE_SIGN)
				.burnable()
		)
	);
	public static final Block BAMBOO_WALL_SIGN = register(
		"bamboo_wall_sign",
		new WallSignBlock(
			WoodType.BAMBOO,
			AbstractBlock.Settings.create()
				.mapColor(BAMBOO_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.burnable()
				.dropsLike(BAMBOO_SIGN)
		)
	);
	public static final Block OAK_HANGING_SIGN = register(
		"oak_hanging_sign",
		new HangingSignBlock(
			WoodType.OAK,
			AbstractBlock.Settings.create().mapColor(OAK_LOG.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block SPRUCE_HANGING_SIGN = register(
		"spruce_hanging_sign",
		new HangingSignBlock(
			WoodType.SPRUCE,
			AbstractBlock.Settings.create().mapColor(SPRUCE_LOG.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block BIRCH_HANGING_SIGN = register(
		"birch_hanging_sign",
		new HangingSignBlock(
			WoodType.BIRCH, AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block ACACIA_HANGING_SIGN = register(
		"acacia_hanging_sign",
		new HangingSignBlock(
			WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block CHERRY_HANGING_SIGN = register(
		"cherry_hanging_sign",
		new HangingSignBlock(
			WoodType.CHERRY,
			AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_PINK).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block JUNGLE_HANGING_SIGN = register(
		"jungle_hanging_sign",
		new HangingSignBlock(
			WoodType.JUNGLE,
			AbstractBlock.Settings.create().mapColor(JUNGLE_LOG.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block DARK_OAK_HANGING_SIGN = register(
		"dark_oak_hanging_sign",
		new HangingSignBlock(
			WoodType.DARK_OAK,
			AbstractBlock.Settings.create().mapColor(DARK_OAK_LOG.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block CRIMSON_HANGING_SIGN = register(
		"crimson_hanging_sign",
		new HangingSignBlock(
			WoodType.CRIMSON, AbstractBlock.Settings.create().mapColor(MapColor.DULL_PINK).solid().instrument(Instrument.BASS).noCollision().strength(1.0F)
		)
	);
	public static final Block WARPED_HANGING_SIGN = register(
		"warped_hanging_sign",
		new HangingSignBlock(
			WoodType.WARPED, AbstractBlock.Settings.create().mapColor(MapColor.DARK_AQUA).solid().instrument(Instrument.BASS).noCollision().strength(1.0F)
		)
	);
	public static final Block MANGROVE_HANGING_SIGN = register(
		"mangrove_hanging_sign",
		new HangingSignBlock(
			WoodType.MANGROVE,
			AbstractBlock.Settings.create().mapColor(MANGROVE_LOG.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block BAMBOO_HANGING_SIGN = register(
		"bamboo_hanging_sign",
		new HangingSignBlock(
			WoodType.BAMBOO, AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable()
		)
	);
	public static final Block OAK_WALL_HANGING_SIGN = register(
		"oak_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.OAK,
			AbstractBlock.Settings.create()
				.mapColor(OAK_LOG.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.burnable()
				.dropsLike(OAK_HANGING_SIGN)
		)
	);
	public static final Block SPRUCE_WALL_HANGING_SIGN = register(
		"spruce_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.SPRUCE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.dropsLike(SPRUCE_HANGING_SIGN)
				.burnable()
		)
	);
	public static final Block BIRCH_WALL_HANGING_SIGN = register(
		"birch_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.BIRCH,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PALE_YELLOW)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.dropsLike(BIRCH_HANGING_SIGN)
				.burnable()
		)
	);
	public static final Block ACACIA_WALL_HANGING_SIGN = register(
		"acacia_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.ACACIA,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.ORANGE)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.burnable()
				.dropsLike(ACACIA_HANGING_SIGN)
		)
	);
	public static final Block CHERRY_WALL_HANGING_SIGN = register(
		"cherry_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.CHERRY,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_PINK)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.burnable()
				.dropsLike(CHERRY_HANGING_SIGN)
		)
	);
	public static final Block JUNGLE_WALL_HANGING_SIGN = register(
		"jungle_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.JUNGLE,
			AbstractBlock.Settings.create()
				.mapColor(JUNGLE_LOG.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.burnable()
				.dropsLike(JUNGLE_HANGING_SIGN)
		)
	);
	public static final Block DARK_OAK_WALL_HANGING_SIGN = register(
		"dark_oak_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.DARK_OAK,
			AbstractBlock.Settings.create()
				.mapColor(DARK_OAK_LOG.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.burnable()
				.dropsLike(DARK_OAK_HANGING_SIGN)
		)
	);
	public static final Block MANGROVE_WALL_HANGING_SIGN = register(
		"mangrove_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.MANGROVE,
			AbstractBlock.Settings.create()
				.mapColor(MANGROVE_LOG.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.burnable()
				.dropsLike(MANGROVE_HANGING_SIGN)
		)
	);
	public static final Block CRIMSON_WALL_HANGING_SIGN = register(
		"crimson_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.CRIMSON,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DULL_PINK)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.dropsLike(CRIMSON_HANGING_SIGN)
		)
	);
	public static final Block WARPED_WALL_HANGING_SIGN = register(
		"warped_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.WARPED,
			AbstractBlock.Settings.create().mapColor(MapColor.DARK_AQUA).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).dropsLike(WARPED_HANGING_SIGN)
		)
	);
	public static final Block BAMBOO_WALL_HANGING_SIGN = register(
		"bamboo_wall_hanging_sign",
		new WallHangingSignBlock(
			WoodType.BAMBOO,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.YELLOW)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.burnable()
				.dropsLike(BAMBOO_HANGING_SIGN)
		)
	);
	public static final Block LEVER = register(
		"lever", new LeverBlock(AbstractBlock.Settings.create().noCollision().strength(0.5F).sounds(BlockSoundGroup.STONE).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block STONE_PRESSURE_PLATE = register(
		"stone_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.STONE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.solid()
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.noCollision()
				.strength(0.5F)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block IRON_DOOR = register(
		"iron_door",
		new DoorBlock(
			BlockSetType.IRON,
			AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0F).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block OAK_PRESSURE_PLATE = register(
		"oak_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.OAK,
			AbstractBlock.Settings.create()
				.mapColor(OAK_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SPRUCE_PRESSURE_PLATE = register(
		"spruce_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.SPRUCE,
			AbstractBlock.Settings.create()
				.mapColor(SPRUCE_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BIRCH_PRESSURE_PLATE = register(
		"birch_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.BIRCH,
			AbstractBlock.Settings.create()
				.mapColor(BIRCH_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block JUNGLE_PRESSURE_PLATE = register(
		"jungle_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.JUNGLE,
			AbstractBlock.Settings.create()
				.mapColor(JUNGLE_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block ACACIA_PRESSURE_PLATE = register(
		"acacia_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.ACACIA,
			AbstractBlock.Settings.create()
				.mapColor(ACACIA_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CHERRY_PRESSURE_PLATE = register(
		"cherry_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.CHERRY,
			AbstractBlock.Settings.create()
				.mapColor(CHERRY_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block DARK_OAK_PRESSURE_PLATE = register(
		"dark_oak_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.DARK_OAK,
			AbstractBlock.Settings.create()
				.mapColor(DARK_OAK_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block MANGROVE_PRESSURE_PLATE = register(
		"mangrove_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.MANGROVE,
			AbstractBlock.Settings.create()
				.mapColor(MANGROVE_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BAMBOO_PRESSURE_PLATE = register(
		"bamboo_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.BAMBOO,
			AbstractBlock.Settings.create()
				.mapColor(BAMBOO_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block REDSTONE_ORE = register(
		"redstone_ore",
		new RedstoneOreBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.ticksRandomly()
				.luminance(createLightLevelFromLitBlockState(9))
				.strength(3.0F, 3.0F)
		)
	);
	public static final Block DEEPSLATE_REDSTONE_ORE = register(
		"deepslate_redstone_ore",
		new RedstoneOreBlock(
			AbstractBlock.Settings.copyShallow(REDSTONE_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
		)
	);
	public static final Block REDSTONE_TORCH = register(
		"redstone_torch",
		new RedstoneTorchBlock(
			AbstractBlock.Settings.create()
				.noCollision()
				.breakInstantly()
				.luminance(createLightLevelFromLitBlockState(7))
				.sounds(BlockSoundGroup.WOOD)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block REDSTONE_WALL_TORCH = register(
		"redstone_wall_torch",
		new WallRedstoneTorchBlock(
			AbstractBlock.Settings.create()
				.noCollision()
				.breakInstantly()
				.luminance(createLightLevelFromLitBlockState(7))
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(REDSTONE_TORCH)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block STONE_BUTTON = register("stone_button", createStoneButtonBlock());
	public static final Block SNOW = register(
		"snow",
		new SnowBlock(
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
		)
	);
	public static final Block ICE = register(
		"ice",
		new IceBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PALE_PURPLE)
				.slipperiness(0.98F)
				.ticksRandomly()
				.strength(0.5F)
				.sounds(BlockSoundGroup.GLASS)
				.nonOpaque()
				.allowsSpawning((state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR)
				.solidBlock(Blocks::never)
		)
	);
	public static final Block SNOW_BLOCK = register(
		"snow_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).requiresTool().strength(0.2F).sounds(BlockSoundGroup.SNOW))
	);
	public static final Block CACTUS = register(
		"cactus",
		new CactusBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.ticksRandomly()
				.strength(0.4F)
				.sounds(BlockSoundGroup.WOOL)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CLAY = register(
		"clay",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY).instrument(Instrument.FLUTE).strength(0.6F).sounds(BlockSoundGroup.GRAVEL))
	);
	public static final Block SUGAR_CANE = register(
		"sugar_cane",
		new SugarCaneBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block JUKEBOX = register(
		"jukebox",
		new JukeboxBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(Instrument.BASS).strength(2.0F, 6.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block OAK_FENCE = register(
		"oak_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(OAK_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block NETHERRACK = register(
		"netherrack",
		new NetherrackBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DARK_RED).instrument(Instrument.BASEDRUM).requiresTool().strength(0.4F).sounds(BlockSoundGroup.NETHERRACK)
		)
	);
	public static final Block SOUL_SAND = register(
		"soul_sand",
		new SoulSandBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BROWN)
				.instrument(Instrument.COW_BELL)
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
		"soul_soil", new Block(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(0.5F).sounds(BlockSoundGroup.SOUL_SOIL))
	);
	public static final Block BASALT = register(
		"basalt",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F).sounds(BlockSoundGroup.BASALT)
		)
	);
	public static final Block POLISHED_BASALT = register(
		"polished_basalt",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F).sounds(BlockSoundGroup.BASALT)
		)
	);
	public static final Block SOUL_TORCH = register(
		"soul_torch",
		new TorchBlock(
			ParticleTypes.SOUL_FIRE_FLAME,
			AbstractBlock.Settings.create().noCollision().breakInstantly().luminance(state -> 10).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SOUL_WALL_TORCH = register(
		"soul_wall_torch",
		new WallTorchBlock(
			ParticleTypes.SOUL_FIRE_FLAME,
			AbstractBlock.Settings.create()
				.noCollision()
				.breakInstantly()
				.luminance(state -> 10)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(SOUL_TORCH)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block GLOWSTONE = register(
		"glowstone",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PALE_YELLOW)
				.instrument(Instrument.PLING)
				.strength(0.3F)
				.sounds(BlockSoundGroup.GLASS)
				.luminance(state -> 15)
				.solidBlock(Blocks::never)
		)
	);
	public static final Block NETHER_PORTAL = register(
		"nether_portal",
		new NetherPortalBlock(
			AbstractBlock.Settings.create()
				.noCollision()
				.ticksRandomly()
				.strength(-1.0F)
				.sounds(BlockSoundGroup.GLASS)
				.luminance(state -> 11)
				.pistonBehavior(PistonBehavior.BLOCK)
		)
	);
	public static final Block CARVED_PUMPKIN = register(
		"carved_pumpkin",
		new WearableCarvedPumpkinBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.ORANGE)
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.allowsSpawning(Blocks::always)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block JACK_O_LANTERN = register(
		"jack_o_lantern",
		new CarvedPumpkinBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.ORANGE)
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.luminance(state -> 15)
				.allowsSpawning(Blocks::always)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CAKE = register(
		"cake", new CakeBlock(AbstractBlock.Settings.create().solid().strength(0.5F).sounds(BlockSoundGroup.WOOL).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block REPEATER = register(
		"repeater", new RepeaterBlock(AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.STONE).pistonBehavior(PistonBehavior.DESTROY))
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
			BlockSetType.OAK,
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable()
		)
	);
	public static final Block SPRUCE_TRAPDOOR = register(
		"spruce_trapdoor",
		new TrapdoorBlock(
			BlockSetType.SPRUCE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.SPRUCE_BROWN)
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.burnable()
		)
	);
	public static final Block BIRCH_TRAPDOOR = register(
		"birch_trapdoor",
		new TrapdoorBlock(
			BlockSetType.BIRCH,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PALE_YELLOW)
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.burnable()
		)
	);
	public static final Block JUNGLE_TRAPDOOR = register(
		"jungle_trapdoor",
		new TrapdoorBlock(
			BlockSetType.JUNGLE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DIRT_BROWN)
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.burnable()
		)
	);
	public static final Block ACACIA_TRAPDOOR = register(
		"acacia_trapdoor",
		new TrapdoorBlock(
			BlockSetType.ACACIA,
			AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable()
		)
	);
	public static final Block CHERRY_TRAPDOOR = register(
		"cherry_trapdoor",
		new TrapdoorBlock(
			BlockSetType.CHERRY,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_WHITE)
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.burnable()
		)
	);
	public static final Block DARK_OAK_TRAPDOOR = register(
		"dark_oak_trapdoor",
		new TrapdoorBlock(
			BlockSetType.DARK_OAK,
			AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(Instrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable()
		)
	);
	public static final Block MANGROVE_TRAPDOOR = register(
		"mangrove_trapdoor",
		new TrapdoorBlock(
			BlockSetType.MANGROVE,
			AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable()
		)
	);
	public static final Block BAMBOO_TRAPDOOR = register(
		"bamboo_trapdoor",
		new TrapdoorBlock(
			BlockSetType.BAMBOO,
			AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(Instrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable()
		)
	);
	public static final Block STONE_BRICKS = register(
		"stone_bricks", new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block MOSSY_STONE_BRICKS = register(
		"mossy_stone_bricks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block CRACKED_STONE_BRICKS = register(
		"cracked_stone_bricks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block CHISELED_STONE_BRICKS = register(
		"chiseled_stone_bricks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PACKED_MUD = register(
		"packed_mud", new Block(AbstractBlock.Settings.copyShallow(DIRT).strength(1.0F, 3.0F).sounds(BlockSoundGroup.PACKED_MUD))
	);
	public static final Block MUD_BRICKS = register(
		"mud_bricks",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.5F, 3.0F)
				.sounds(BlockSoundGroup.MUD_BRICKS)
		)
	);
	public static final Block INFESTED_STONE = register(
		"infested_stone", new InfestedBlock(STONE, AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY))
	);
	public static final Block INFESTED_COBBLESTONE = register(
		"infested_cobblestone", new InfestedBlock(COBBLESTONE, AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY))
	);
	public static final Block INFESTED_STONE_BRICKS = register(
		"infested_stone_bricks", new InfestedBlock(STONE_BRICKS, AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY))
	);
	public static final Block INFESTED_MOSSY_STONE_BRICKS = register(
		"infested_mossy_stone_bricks", new InfestedBlock(MOSSY_STONE_BRICKS, AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY))
	);
	public static final Block INFESTED_CRACKED_STONE_BRICKS = register(
		"infested_cracked_stone_bricks", new InfestedBlock(CRACKED_STONE_BRICKS, AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY))
	);
	public static final Block INFESTED_CHISELED_STONE_BRICKS = register(
		"infested_chiseled_stone_bricks", new InfestedBlock(CHISELED_STONE_BRICKS, AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY))
	);
	public static final Block BROWN_MUSHROOM_BLOCK = register(
		"brown_mushroom_block",
		new MushroomBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(Instrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block RED_MUSHROOM_BLOCK = register(
		"red_mushroom_block",
		new MushroomBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block MUSHROOM_STEM = register(
		"mushroom_stem",
		new MushroomBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).instrument(Instrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block IRON_BARS = register(
		"iron_bars", new PaneBlock(AbstractBlock.Settings.create().requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block CHAIN = register(
		"chain", new ChainBlock(AbstractBlock.Settings.create().solid().requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.CHAIN).nonOpaque())
	);
	public static final Block GLASS_PANE = register(
		"glass_pane", new PaneBlock(AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block PUMPKIN = register(
		BlockKeys.PUMPKIN,
		new PumpkinBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.ORANGE)
				.instrument(Instrument.DIDGERIDOO)
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block MELON = register(
		BlockKeys.MELON,
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIME).strength(1.0F).sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block ATTACHED_PUMPKIN_STEM = register(
		BlockKeys.ATTACHED_PUMPKIN_STEM,
		new AttachedStemBlock(
			BlockKeys.PUMPKIN_STEM,
			BlockKeys.PUMPKIN,
			ItemKeys.PUMPKIN_SEEDS,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WOOD)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block ATTACHED_MELON_STEM = register(
		BlockKeys.ATTACHED_MELON_STEM,
		new AttachedStemBlock(
			BlockKeys.MELON_STEM,
			BlockKeys.MELON,
			ItemKeys.MELON_SEEDS,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WOOD)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block PUMPKIN_STEM = register(
		BlockKeys.PUMPKIN_STEM,
		new StemBlock(
			BlockKeys.PUMPKIN,
			BlockKeys.ATTACHED_PUMPKIN_STEM,
			ItemKeys.PUMPKIN_SEEDS,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.STEM)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block MELON_STEM = register(
		BlockKeys.MELON_STEM,
		new StemBlock(
			BlockKeys.MELON,
			BlockKeys.ATTACHED_MELON_STEM,
			ItemKeys.MELON_SEEDS,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.STEM)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block VINE = register(
		"vine",
		new VineBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.replaceable()
				.noCollision()
				.ticksRandomly()
				.strength(0.2F)
				.sounds(BlockSoundGroup.VINE)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block GLOW_LICHEN = register(
		"glow_lichen",
		new GlowLichenBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.LICHEN_GREEN)
				.replaceable()
				.noCollision()
				.strength(0.2F)
				.sounds(BlockSoundGroup.GLOW_LICHEN)
				.luminance(GlowLichenBlock.getLuminanceSupplier(7))
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block OAK_FENCE_GATE = register(
		"oak_fence_gate",
		new FenceGateBlock(
			WoodType.OAK, AbstractBlock.Settings.create().mapColor(OAK_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable()
		)
	);
	public static final Block BRICK_STAIRS = register("brick_stairs", createOldStairsBlock(BRICKS));
	public static final Block STONE_BRICK_STAIRS = register("stone_brick_stairs", createOldStairsBlock(STONE_BRICKS));
	public static final Block MUD_BRICK_STAIRS = register("mud_brick_stairs", createOldStairsBlock(MUD_BRICKS));
	public static final Block MYCELIUM = register(
		"mycelium", new MyceliumBlock(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block LILY_PAD = register(
		"lily_pad",
		new LilyPadBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.breakInstantly()
				.sounds(BlockSoundGroup.LILY_PAD)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block NETHER_BRICKS = register(
		"nether_bricks",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(2.0F, 6.0F)
				.sounds(BlockSoundGroup.NETHER_BRICKS)
		)
	);
	public static final Block NETHER_BRICK_FENCE = register(
		"nether_brick_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(2.0F, 6.0F)
				.sounds(BlockSoundGroup.NETHER_BRICKS)
		)
	);
	public static final Block NETHER_BRICK_STAIRS = register("nether_brick_stairs", createOldStairsBlock(NETHER_BRICKS));
	public static final Block NETHER_WART = register(
		"nether_wart",
		new NetherWartBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.RED)
				.noCollision()
				.ticksRandomly()
				.sounds(BlockSoundGroup.NETHER_WART)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block ENCHANTING_TABLE = register(
		"enchanting_table",
		new EnchantingTableBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.BASEDRUM).requiresTool().luminance(state -> 7).strength(5.0F, 1200.0F)
		)
	);
	public static final Block BREWING_STAND = register(
		"brewing_stand",
		new BrewingStandBlock(AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(0.5F).luminance(state -> 1).nonOpaque())
	);
	public static final Block CAULDRON = register(
		"cauldron", new CauldronBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(2.0F).nonOpaque())
	);
	public static final Block WATER_CAULDRON = register(
		"water_cauldron", new LeveledCauldronBlock(Biome.Precipitation.RAIN, CauldronBehavior.WATER_CAULDRON_BEHAVIOR, AbstractBlock.Settings.copyShallow(CAULDRON))
	);
	public static final Block LAVA_CAULDRON = register("lava_cauldron", new LavaCauldronBlock(AbstractBlock.Settings.copyShallow(CAULDRON).luminance(state -> 15)));
	public static final Block POWDER_SNOW_CAULDRON = register(
		"powder_snow_cauldron",
		new LeveledCauldronBlock(Biome.Precipitation.SNOW, CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR, AbstractBlock.Settings.copyShallow(CAULDRON))
	);
	public static final Block END_PORTAL = register(
		"end_portal",
		new EndPortalBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BLACK)
				.noCollision()
				.luminance(state -> 15)
				.strength(-1.0F, 3600000.0F)
				.dropsNothing()
				.pistonBehavior(PistonBehavior.BLOCK)
		)
	);
	public static final Block END_PORTAL_FRAME = register(
		"end_portal_frame",
		new EndPortalFrameBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.GREEN)
				.instrument(Instrument.BASEDRUM)
				.sounds(BlockSoundGroup.GLASS)
				.luminance(state -> 1)
				.strength(-1.0F, 3600000.0F)
				.dropsNothing()
		)
	);
	public static final Block END_STONE = register(
		"end_stone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 9.0F))
	);
	public static final Block DRAGON_EGG = register(
		"dragon_egg",
		new DragonEggBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(3.0F, 9.0F).luminance(state -> 1).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block REDSTONE_LAMP = register(
		"redstone_lamp",
		new RedstoneLampBlock(
			AbstractBlock.Settings.create().luminance(createLightLevelFromLitBlockState(15)).strength(0.3F).sounds(BlockSoundGroup.GLASS).allowsSpawning(Blocks::always)
		)
	);
	public static final Block COCOA = register(
		"cocoa",
		new CocoaBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.ticksRandomly()
				.strength(0.2F, 3.0F)
				.sounds(BlockSoundGroup.WOOD)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SANDSTONE_STAIRS = register("sandstone_stairs", createOldStairsBlock(SANDSTONE));
	public static final Block EMERALD_ORE = register(
		"emerald_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(3, 7),
			AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F)
		)
	);
	public static final Block DEEPSLATE_EMERALD_ORE = register(
		"deepslate_emerald_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(3, 7),
			AbstractBlock.Settings.copyShallow(EMERALD_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
		)
	);
	public static final Block ENDER_CHEST = register(
		"ender_chest",
		new EnderChestBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(22.5F, 600.0F).luminance(state -> 7)
		)
	);
	public static final Block TRIPWIRE_HOOK = register(
		"tripwire_hook", new TripwireHookBlock(AbstractBlock.Settings.create().noCollision().sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block TRIPWIRE = register(
		"tripwire", new TripwireBlock(TRIPWIRE_HOOK, AbstractBlock.Settings.create().noCollision().pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block EMERALD_BLOCK = register(
		"emerald_block",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.EMERALD_GREEN)
				.instrument(Instrument.BIT)
				.requiresTool()
				.strength(5.0F, 6.0F)
				.sounds(BlockSoundGroup.METAL)
		)
	);
	public static final Block SPRUCE_STAIRS = register("spruce_stairs", createOldStairsBlock(SPRUCE_PLANKS));
	public static final Block BIRCH_STAIRS = register("birch_stairs", createOldStairsBlock(BIRCH_PLANKS));
	public static final Block JUNGLE_STAIRS = register("jungle_stairs", createOldStairsBlock(JUNGLE_PLANKS));
	public static final Block COMMAND_BLOCK = register(
		"command_block", new CommandBlock(false, AbstractBlock.Settings.create().mapColor(MapColor.BROWN).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block BEACON = register(
		"beacon",
		new BeaconBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DIAMOND_BLUE)
				.instrument(Instrument.HAT)
				.strength(3.0F)
				.luminance(state -> 15)
				.nonOpaque()
				.solidBlock(Blocks::never)
		)
	);
	public static final Block COBBLESTONE_WALL = register("cobblestone_wall", new WallBlock(AbstractBlock.Settings.copyShallow(COBBLESTONE).solid()));
	public static final Block MOSSY_COBBLESTONE_WALL = register("mossy_cobblestone_wall", new WallBlock(AbstractBlock.Settings.copyShallow(COBBLESTONE).solid()));
	public static final Block FLOWER_POT = register("flower_pot", createFlowerPotBlock(AIR));
	public static final Block POTTED_TORCHFLOWER = register("potted_torchflower", createFlowerPotBlock(TORCHFLOWER));
	public static final Block POTTED_OAK_SAPLING = register("potted_oak_sapling", createFlowerPotBlock(OAK_SAPLING));
	public static final Block POTTED_SPRUCE_SAPLING = register("potted_spruce_sapling", createFlowerPotBlock(SPRUCE_SAPLING));
	public static final Block POTTED_BIRCH_SAPLING = register("potted_birch_sapling", createFlowerPotBlock(BIRCH_SAPLING));
	public static final Block POTTED_JUNGLE_SAPLING = register("potted_jungle_sapling", createFlowerPotBlock(JUNGLE_SAPLING));
	public static final Block POTTED_ACACIA_SAPLING = register("potted_acacia_sapling", createFlowerPotBlock(ACACIA_SAPLING));
	public static final Block POTTED_CHERRY_SAPLING = register("potted_cherry_sapling", createFlowerPotBlock(CHERRY_SAPLING));
	public static final Block POTTED_DARK_OAK_SAPLING = register("potted_dark_oak_sapling", createFlowerPotBlock(DARK_OAK_SAPLING));
	public static final Block POTTED_MANGROVE_PROPAGULE = register("potted_mangrove_propagule", createFlowerPotBlock(MANGROVE_PROPAGULE));
	public static final Block POTTED_FERN = register("potted_fern", createFlowerPotBlock(FERN));
	public static final Block POTTED_DANDELION = register("potted_dandelion", createFlowerPotBlock(DANDELION));
	public static final Block POTTED_POPPY = register("potted_poppy", createFlowerPotBlock(POPPY));
	public static final Block POTTED_BLUE_ORCHID = register("potted_blue_orchid", createFlowerPotBlock(BLUE_ORCHID));
	public static final Block POTTED_ALLIUM = register("potted_allium", createFlowerPotBlock(ALLIUM));
	public static final Block POTTED_AZURE_BLUET = register("potted_azure_bluet", createFlowerPotBlock(AZURE_BLUET));
	public static final Block POTTED_RED_TULIP = register("potted_red_tulip", createFlowerPotBlock(RED_TULIP));
	public static final Block POTTED_ORANGE_TULIP = register("potted_orange_tulip", createFlowerPotBlock(ORANGE_TULIP));
	public static final Block POTTED_WHITE_TULIP = register("potted_white_tulip", createFlowerPotBlock(WHITE_TULIP));
	public static final Block POTTED_PINK_TULIP = register("potted_pink_tulip", createFlowerPotBlock(PINK_TULIP));
	public static final Block POTTED_OXEYE_DAISY = register("potted_oxeye_daisy", createFlowerPotBlock(OXEYE_DAISY));
	public static final Block POTTED_CORNFLOWER = register("potted_cornflower", createFlowerPotBlock(CORNFLOWER));
	public static final Block POTTED_LILY_OF_THE_VALLEY = register("potted_lily_of_the_valley", createFlowerPotBlock(LILY_OF_THE_VALLEY));
	public static final Block POTTED_WITHER_ROSE = register("potted_wither_rose", createFlowerPotBlock(WITHER_ROSE));
	public static final Block POTTED_RED_MUSHROOM = register("potted_red_mushroom", createFlowerPotBlock(RED_MUSHROOM));
	public static final Block POTTED_BROWN_MUSHROOM = register("potted_brown_mushroom", createFlowerPotBlock(BROWN_MUSHROOM));
	public static final Block POTTED_DEAD_BUSH = register("potted_dead_bush", createFlowerPotBlock(DEAD_BUSH));
	public static final Block POTTED_CACTUS = register("potted_cactus", createFlowerPotBlock(CACTUS));
	public static final Block CARROTS = register(
		"carrots",
		new CarrotsBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.CROP)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block POTATOES = register(
		"potatoes",
		new PotatoesBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.CROP)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block OAK_BUTTON = register("oak_button", createWoodenButtonBlock(BlockSetType.OAK));
	public static final Block SPRUCE_BUTTON = register("spruce_button", createWoodenButtonBlock(BlockSetType.SPRUCE));
	public static final Block BIRCH_BUTTON = register("birch_button", createWoodenButtonBlock(BlockSetType.BIRCH));
	public static final Block JUNGLE_BUTTON = register("jungle_button", createWoodenButtonBlock(BlockSetType.JUNGLE));
	public static final Block ACACIA_BUTTON = register("acacia_button", createWoodenButtonBlock(BlockSetType.ACACIA));
	public static final Block CHERRY_BUTTON = register("cherry_button", createWoodenButtonBlock(BlockSetType.CHERRY));
	public static final Block DARK_OAK_BUTTON = register("dark_oak_button", createWoodenButtonBlock(BlockSetType.DARK_OAK));
	public static final Block MANGROVE_BUTTON = register("mangrove_button", createWoodenButtonBlock(BlockSetType.MANGROVE));
	public static final Block BAMBOO_BUTTON = register("bamboo_button", createWoodenButtonBlock(BlockSetType.BAMBOO));
	public static final Block SKELETON_SKULL = register(
		"skeleton_skull",
		new SkullBlock(
			SkullBlock.Type.SKELETON, AbstractBlock.Settings.create().instrument(Instrument.SKELETON).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SKELETON_WALL_SKULL = register(
		"skeleton_wall_skull",
		new WallSkullBlock(SkullBlock.Type.SKELETON, AbstractBlock.Settings.create().strength(1.0F).dropsLike(SKELETON_SKULL).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block WITHER_SKELETON_SKULL = register(
		"wither_skeleton_skull",
		new WitherSkullBlock(AbstractBlock.Settings.create().instrument(Instrument.WITHER_SKELETON).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block WITHER_SKELETON_WALL_SKULL = register(
		"wither_skeleton_wall_skull",
		new WallWitherSkullBlock(AbstractBlock.Settings.create().strength(1.0F).dropsLike(WITHER_SKELETON_SKULL).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block ZOMBIE_HEAD = register(
		"zombie_head",
		new SkullBlock(SkullBlock.Type.ZOMBIE, AbstractBlock.Settings.create().instrument(Instrument.ZOMBIE).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block ZOMBIE_WALL_HEAD = register(
		"zombie_wall_head",
		new WallSkullBlock(SkullBlock.Type.ZOMBIE, AbstractBlock.Settings.create().strength(1.0F).dropsLike(ZOMBIE_HEAD).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block PLAYER_HEAD = register(
		"player_head", new PlayerSkullBlock(AbstractBlock.Settings.create().instrument(Instrument.CUSTOM_HEAD).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block PLAYER_WALL_HEAD = register(
		"player_wall_head", new WallPlayerSkullBlock(AbstractBlock.Settings.create().strength(1.0F).dropsLike(PLAYER_HEAD).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block CREEPER_HEAD = register(
		"creeper_head",
		new SkullBlock(SkullBlock.Type.CREEPER, AbstractBlock.Settings.create().instrument(Instrument.CREEPER).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block CREEPER_WALL_HEAD = register(
		"creeper_wall_head",
		new WallSkullBlock(SkullBlock.Type.CREEPER, AbstractBlock.Settings.create().strength(1.0F).dropsLike(CREEPER_HEAD).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block DRAGON_HEAD = register(
		"dragon_head",
		new SkullBlock(SkullBlock.Type.DRAGON, AbstractBlock.Settings.create().instrument(Instrument.DRAGON).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block DRAGON_WALL_HEAD = register(
		"dragon_wall_head",
		new WallSkullBlock(SkullBlock.Type.DRAGON, AbstractBlock.Settings.create().strength(1.0F).dropsLike(DRAGON_HEAD).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block PIGLIN_HEAD = register(
		"piglin_head",
		new SkullBlock(SkullBlock.Type.PIGLIN, AbstractBlock.Settings.create().instrument(Instrument.PIGLIN).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block PIGLIN_WALL_HEAD = register(
		"piglin_wall_head", new WallPiglinHeadBlock(AbstractBlock.Settings.create().strength(1.0F).dropsLike(PIGLIN_HEAD).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block ANVIL = register(
		"anvil",
		new AnvilBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.requiresTool()
				.strength(5.0F, 1200.0F)
				.sounds(BlockSoundGroup.ANVIL)
				.pistonBehavior(PistonBehavior.BLOCK)
		)
	);
	public static final Block CHIPPED_ANVIL = register(
		"chipped_anvil",
		new AnvilBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.requiresTool()
				.strength(5.0F, 1200.0F)
				.sounds(BlockSoundGroup.ANVIL)
				.pistonBehavior(PistonBehavior.BLOCK)
		)
	);
	public static final Block DAMAGED_ANVIL = register(
		"damaged_anvil",
		new AnvilBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.requiresTool()
				.strength(5.0F, 1200.0F)
				.sounds(BlockSoundGroup.ANVIL)
				.pistonBehavior(PistonBehavior.BLOCK)
		)
	);
	public static final Block TRAPPED_CHEST = register(
		"trapped_chest",
		new TrappedChestBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block LIGHT_WEIGHTED_PRESSURE_PLATE = register(
		"light_weighted_pressure_plate",
		new WeightedPressurePlateBlock(
			15,
			BlockSetType.GOLD,
			AbstractBlock.Settings.create().mapColor(MapColor.GOLD).solid().requiresTool().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block HEAVY_WEIGHTED_PRESSURE_PLATE = register(
		"heavy_weighted_pressure_plate",
		new WeightedPressurePlateBlock(
			150,
			BlockSetType.IRON,
			AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).solid().requiresTool().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block COMPARATOR = register(
		"comparator", new ComparatorBlock(AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.STONE).pistonBehavior(PistonBehavior.DESTROY))
	);
	public static final Block DAYLIGHT_DETECTOR = register(
		"daylight_detector",
		new DaylightDetectorBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block REDSTONE_BLOCK = register(
		"redstone_block",
		new RedstoneBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_RED).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).solidBlock(Blocks::never)
		)
	);
	public static final Block NETHER_QUARTZ_ORE = register(
		"nether_quartz_ore",
		new ExperienceDroppingBlock(
			UniformIntProvider.create(2, 5),
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(3.0F, 3.0F)
				.sounds(BlockSoundGroup.NETHER_ORE)
		)
	);
	public static final Block HOPPER = register(
		"hopper",
		new HopperBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(3.0F, 4.8F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block QUARTZ_BLOCK = register(
		"quartz_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(0.8F))
	);
	public static final Block CHISELED_QUARTZ_BLOCK = register(
		"chiseled_quartz_block",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(0.8F))
	);
	public static final Block QUARTZ_PILLAR = register(
		"quartz_pillar", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(0.8F))
	);
	public static final Block QUARTZ_STAIRS = register("quartz_stairs", createOldStairsBlock(QUARTZ_BLOCK));
	public static final Block ACTIVATOR_RAIL = register(
		"activator_rail", new PoweredRailBlock(AbstractBlock.Settings.create().noCollision().strength(0.7F).sounds(BlockSoundGroup.METAL))
	);
	public static final Block DROPPER = register(
		"dropper", new DropperBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.5F))
	);
	public static final Block WHITE_TERRACOTTA = register(
		"white_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block ORANGE_TERRACOTTA = register(
		"orange_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block MAGENTA_TERRACOTTA = register(
		"magenta_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_MAGENTA).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block LIGHT_BLUE_TERRACOTTA = register(
		"light_blue_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block YELLOW_TERRACOTTA = register(
		"yellow_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block LIME_TERRACOTTA = register(
		"lime_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_LIME).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block PINK_TERRACOTTA = register(
		"pink_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_PINK).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block GRAY_TERRACOTTA = register(
		"gray_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block LIGHT_GRAY_TERRACOTTA = register(
		"light_gray_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block CYAN_TERRACOTTA = register(
		"cyan_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_CYAN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block PURPLE_TERRACOTTA = register(
		"purple_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_PURPLE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block BLUE_TERRACOTTA = register(
		"blue_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_BLUE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block BROWN_TERRACOTTA = register(
		"brown_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_BROWN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block GREEN_TERRACOTTA = register(
		"green_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_GREEN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block RED_TERRACOTTA = register(
		"red_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_RED).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block BLACK_TERRACOTTA = register(
		"black_terracotta",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block WHITE_STAINED_GLASS_PANE = register(
		"white_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.WHITE, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block ORANGE_STAINED_GLASS_PANE = register(
		"orange_stained_glass_pane",
		new StainedGlassPaneBlock(
			DyeColor.ORANGE, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
		)
	);
	public static final Block MAGENTA_STAINED_GLASS_PANE = register(
		"magenta_stained_glass_pane",
		new StainedGlassPaneBlock(
			DyeColor.MAGENTA, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
		)
	);
	public static final Block LIGHT_BLUE_STAINED_GLASS_PANE = register(
		"light_blue_stained_glass_pane",
		new StainedGlassPaneBlock(
			DyeColor.LIGHT_BLUE, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
		)
	);
	public static final Block YELLOW_STAINED_GLASS_PANE = register(
		"yellow_stained_glass_pane",
		new StainedGlassPaneBlock(
			DyeColor.YELLOW, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
		)
	);
	public static final Block LIME_STAINED_GLASS_PANE = register(
		"lime_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.LIME, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block PINK_STAINED_GLASS_PANE = register(
		"pink_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.PINK, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block GRAY_STAINED_GLASS_PANE = register(
		"gray_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.GRAY, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block LIGHT_GRAY_STAINED_GLASS_PANE = register(
		"light_gray_stained_glass_pane",
		new StainedGlassPaneBlock(
			DyeColor.LIGHT_GRAY, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
		)
	);
	public static final Block CYAN_STAINED_GLASS_PANE = register(
		"cyan_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.CYAN, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block PURPLE_STAINED_GLASS_PANE = register(
		"purple_stained_glass_pane",
		new StainedGlassPaneBlock(
			DyeColor.PURPLE, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()
		)
	);
	public static final Block BLUE_STAINED_GLASS_PANE = register(
		"blue_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.BLUE, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block BROWN_STAINED_GLASS_PANE = register(
		"brown_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.BROWN, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block GREEN_STAINED_GLASS_PANE = register(
		"green_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.GREEN, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block RED_STAINED_GLASS_PANE = register(
		"red_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.RED, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block BLACK_STAINED_GLASS_PANE = register(
		"black_stained_glass_pane",
		new StainedGlassPaneBlock(DyeColor.BLACK, AbstractBlock.Settings.create().instrument(Instrument.HAT).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque())
	);
	public static final Block ACACIA_STAIRS = register("acacia_stairs", createOldStairsBlock(ACACIA_PLANKS));
	public static final Block CHERRY_STAIRS = register("cherry_stairs", createOldStairsBlock(CHERRY_PLANKS));
	public static final Block DARK_OAK_STAIRS = register("dark_oak_stairs", createOldStairsBlock(DARK_OAK_PLANKS));
	public static final Block MANGROVE_STAIRS = register("mangrove_stairs", createOldStairsBlock(MANGROVE_PLANKS));
	public static final Block BAMBOO_STAIRS = register("bamboo_stairs", createOldStairsBlock(BAMBOO_PLANKS));
	public static final Block BAMBOO_MOSAIC_STAIRS = register("bamboo_mosaic_stairs", createOldStairsBlock(BAMBOO_MOSAIC));
	public static final Block SLIME_BLOCK = register(
		"slime_block", new SlimeBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_GREEN).slipperiness(0.8F).sounds(BlockSoundGroup.SLIME).nonOpaque())
	);
	public static final Block BARRIER = register(
		"barrier",
		new BarrierBlock(
			AbstractBlock.Settings.create()
				.strength(-1.0F, 3600000.8F)
				.dropsNothing()
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.noBlockBreakParticles()
				.pistonBehavior(PistonBehavior.BLOCK)
		)
	);
	public static final Block LIGHT = register(
		"light",
		new LightBlock(AbstractBlock.Settings.create().replaceable().strength(-1.0F, 3600000.8F).dropsNothing().nonOpaque().luminance(LightBlock.STATE_TO_LUMINANCE))
	);
	public static final Block IRON_TRAPDOOR = register(
		"iron_trapdoor",
		new TrapdoorBlock(
			BlockSetType.IRON, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0F).nonOpaque().allowsSpawning(Blocks::never)
		)
	);
	public static final Block PRISMARINE = register(
		"prismarine", new Block(AbstractBlock.Settings.create().mapColor(MapColor.CYAN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PRISMARINE_BRICKS = register(
		"prismarine_bricks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DARK_PRISMARINE = register(
		"dark_prismarine",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PRISMARINE_STAIRS = register("prismarine_stairs", createOldStairsBlock(PRISMARINE));
	public static final Block PRISMARINE_BRICK_STAIRS = register("prismarine_brick_stairs", createOldStairsBlock(PRISMARINE_BRICKS));
	public static final Block DARK_PRISMARINE_STAIRS = register("dark_prismarine_stairs", createOldStairsBlock(DARK_PRISMARINE));
	public static final Block PRISMARINE_SLAB = register(
		"prismarine_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PRISMARINE_BRICK_SLAB = register(
		"prismarine_brick_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DARK_PRISMARINE_SLAB = register(
		"dark_prismarine_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block SEA_LANTERN = register(
		"sea_lantern",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OFF_WHITE)
				.instrument(Instrument.HAT)
				.strength(0.3F)
				.sounds(BlockSoundGroup.GLASS)
				.luminance(state -> 15)
				.solidBlock(Blocks::never)
		)
	);
	public static final Block HAY_BLOCK = register(
		"hay_block",
		new HayBlock(AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(Instrument.BANJO).strength(0.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block WHITE_CARPET = register(
		"white_carpet",
		new DyedCarpetBlock(DyeColor.WHITE, AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block ORANGE_CARPET = register(
		"orange_carpet",
		new DyedCarpetBlock(DyeColor.ORANGE, AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block MAGENTA_CARPET = register(
		"magenta_carpet",
		new DyedCarpetBlock(DyeColor.MAGENTA, AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block LIGHT_BLUE_CARPET = register(
		"light_blue_carpet",
		new DyedCarpetBlock(DyeColor.LIGHT_BLUE, AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block YELLOW_CARPET = register(
		"yellow_carpet",
		new DyedCarpetBlock(DyeColor.YELLOW, AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block LIME_CARPET = register(
		"lime_carpet",
		new DyedCarpetBlock(DyeColor.LIME, AbstractBlock.Settings.create().mapColor(MapColor.LIME).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block PINK_CARPET = register(
		"pink_carpet",
		new DyedCarpetBlock(DyeColor.PINK, AbstractBlock.Settings.create().mapColor(MapColor.PINK).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block GRAY_CARPET = register(
		"gray_carpet",
		new DyedCarpetBlock(DyeColor.GRAY, AbstractBlock.Settings.create().mapColor(MapColor.GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block LIGHT_GRAY_CARPET = register(
		"light_gray_carpet",
		new DyedCarpetBlock(DyeColor.LIGHT_GRAY, AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_GRAY).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block CYAN_CARPET = register(
		"cyan_carpet",
		new DyedCarpetBlock(DyeColor.CYAN, AbstractBlock.Settings.create().mapColor(MapColor.CYAN).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block PURPLE_CARPET = register(
		"purple_carpet",
		new DyedCarpetBlock(DyeColor.PURPLE, AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block BLUE_CARPET = register(
		"blue_carpet",
		new DyedCarpetBlock(DyeColor.BLUE, AbstractBlock.Settings.create().mapColor(MapColor.BLUE).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block BROWN_CARPET = register(
		"brown_carpet",
		new DyedCarpetBlock(DyeColor.BROWN, AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block GREEN_CARPET = register(
		"green_carpet",
		new DyedCarpetBlock(DyeColor.GREEN, AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block RED_CARPET = register(
		"red_carpet",
		new DyedCarpetBlock(DyeColor.RED, AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block BLACK_CARPET = register(
		"black_carpet",
		new DyedCarpetBlock(DyeColor.BLACK, AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(0.1F).sounds(BlockSoundGroup.WOOL).burnable())
	);
	public static final Block TERRACOTTA = register(
		"terracotta", new Block(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.25F, 4.2F))
	);
	public static final Block COAL_BLOCK = register(
		"coal_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F))
	);
	public static final Block PACKED_ICE = register(
		"packed_ice",
		new Block(
			AbstractBlock.Settings.create().mapColor(MapColor.PALE_PURPLE).instrument(Instrument.CHIME).slipperiness(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS)
		)
	);
	public static final Block SUNFLOWER = register(
		"sunflower",
		new TallFlowerBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block LILAC = register(
		"lilac",
		new TallFlowerBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block ROSE_BUSH = register(
		"rose_bush",
		new TallFlowerBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block PEONY = register(
		"peony",
		new TallFlowerBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block TALL_GRASS = register(
		"tall_grass",
		new TallPlantBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block LARGE_FERN = register(
		"large_fern",
		new TallPlantBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.GRASS)
				.offset(AbstractBlock.OffsetType.XZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block WHITE_BANNER = register(
		"white_banner",
		new BannerBlock(
			DyeColor.WHITE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block ORANGE_BANNER = register(
		"orange_banner",
		new BannerBlock(
			DyeColor.ORANGE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block MAGENTA_BANNER = register(
		"magenta_banner",
		new BannerBlock(
			DyeColor.MAGENTA,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block LIGHT_BLUE_BANNER = register(
		"light_blue_banner",
		new BannerBlock(
			DyeColor.LIGHT_BLUE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block YELLOW_BANNER = register(
		"yellow_banner",
		new BannerBlock(
			DyeColor.YELLOW,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block LIME_BANNER = register(
		"lime_banner",
		new BannerBlock(
			DyeColor.LIME,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block PINK_BANNER = register(
		"pink_banner",
		new BannerBlock(
			DyeColor.PINK,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block GRAY_BANNER = register(
		"gray_banner",
		new BannerBlock(
			DyeColor.GRAY,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block LIGHT_GRAY_BANNER = register(
		"light_gray_banner",
		new BannerBlock(
			DyeColor.LIGHT_GRAY,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block CYAN_BANNER = register(
		"cyan_banner",
		new BannerBlock(
			DyeColor.CYAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block PURPLE_BANNER = register(
		"purple_banner",
		new BannerBlock(
			DyeColor.PURPLE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block BLUE_BANNER = register(
		"blue_banner",
		new BannerBlock(
			DyeColor.BLUE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block BROWN_BANNER = register(
		"brown_banner",
		new BannerBlock(
			DyeColor.BROWN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block GREEN_BANNER = register(
		"green_banner",
		new BannerBlock(
			DyeColor.GREEN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block RED_BANNER = register(
		"red_banner",
		new BannerBlock(
			DyeColor.RED,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block BLACK_BANNER = register(
		"black_banner",
		new BannerBlock(
			DyeColor.BLACK,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		)
	);
	public static final Block WHITE_WALL_BANNER = register(
		"white_wall_banner",
		new WallBannerBlock(
			DyeColor.WHITE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(WHITE_BANNER)
				.burnable()
		)
	);
	public static final Block ORANGE_WALL_BANNER = register(
		"orange_wall_banner",
		new WallBannerBlock(
			DyeColor.ORANGE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(ORANGE_BANNER)
				.burnable()
		)
	);
	public static final Block MAGENTA_WALL_BANNER = register(
		"magenta_wall_banner",
		new WallBannerBlock(
			DyeColor.MAGENTA,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(MAGENTA_BANNER)
				.burnable()
		)
	);
	public static final Block LIGHT_BLUE_WALL_BANNER = register(
		"light_blue_wall_banner",
		new WallBannerBlock(
			DyeColor.LIGHT_BLUE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(LIGHT_BLUE_BANNER)
				.burnable()
		)
	);
	public static final Block YELLOW_WALL_BANNER = register(
		"yellow_wall_banner",
		new WallBannerBlock(
			DyeColor.YELLOW,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(YELLOW_BANNER)
				.burnable()
		)
	);
	public static final Block LIME_WALL_BANNER = register(
		"lime_wall_banner",
		new WallBannerBlock(
			DyeColor.LIME,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(LIME_BANNER)
				.burnable()
		)
	);
	public static final Block PINK_WALL_BANNER = register(
		"pink_wall_banner",
		new WallBannerBlock(
			DyeColor.PINK,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(PINK_BANNER)
				.burnable()
		)
	);
	public static final Block GRAY_WALL_BANNER = register(
		"gray_wall_banner",
		new WallBannerBlock(
			DyeColor.GRAY,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(GRAY_BANNER)
				.burnable()
		)
	);
	public static final Block LIGHT_GRAY_WALL_BANNER = register(
		"light_gray_wall_banner",
		new WallBannerBlock(
			DyeColor.LIGHT_GRAY,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(LIGHT_GRAY_BANNER)
				.burnable()
		)
	);
	public static final Block CYAN_WALL_BANNER = register(
		"cyan_wall_banner",
		new WallBannerBlock(
			DyeColor.CYAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(CYAN_BANNER)
				.burnable()
		)
	);
	public static final Block PURPLE_WALL_BANNER = register(
		"purple_wall_banner",
		new WallBannerBlock(
			DyeColor.PURPLE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(PURPLE_BANNER)
				.burnable()
		)
	);
	public static final Block BLUE_WALL_BANNER = register(
		"blue_wall_banner",
		new WallBannerBlock(
			DyeColor.BLUE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(BLUE_BANNER)
				.burnable()
		)
	);
	public static final Block BROWN_WALL_BANNER = register(
		"brown_wall_banner",
		new WallBannerBlock(
			DyeColor.BROWN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(BROWN_BANNER)
				.burnable()
		)
	);
	public static final Block GREEN_WALL_BANNER = register(
		"green_wall_banner",
		new WallBannerBlock(
			DyeColor.GREEN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(GREEN_BANNER)
				.burnable()
		)
	);
	public static final Block RED_WALL_BANNER = register(
		"red_wall_banner",
		new WallBannerBlock(
			DyeColor.RED,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(RED_BANNER)
				.burnable()
		)
	);
	public static final Block BLACK_WALL_BANNER = register(
		"black_wall_banner",
		new WallBannerBlock(
			DyeColor.BLACK,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.OAK_TAN)
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(1.0F)
				.sounds(BlockSoundGroup.WOOD)
				.dropsLike(BLACK_BANNER)
				.burnable()
		)
	);
	public static final Block RED_SANDSTONE = register(
		"red_sandstone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(0.8F))
	);
	public static final Block CHISELED_RED_SANDSTONE = register(
		"chiseled_red_sandstone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(0.8F))
	);
	public static final Block CUT_RED_SANDSTONE = register(
		"cut_red_sandstone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(0.8F))
	);
	public static final Block RED_SANDSTONE_STAIRS = register("red_sandstone_stairs", createOldStairsBlock(RED_SANDSTONE));
	public static final Block OAK_SLAB = register(
		"oak_slab",
		new SlabBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block SPRUCE_SLAB = register(
		"spruce_slab",
		new SlabBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block BIRCH_SLAB = register(
		"birch_slab",
		new SlabBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block JUNGLE_SLAB = register(
		"jungle_slab",
		new SlabBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block ACACIA_SLAB = register(
		"acacia_slab",
		new SlabBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block CHERRY_SLAB = register(
		"cherry_slab",
		new SlabBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_WHITE)
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.sounds(BlockSoundGroup.CHERRY_WOOD)
				.burnable()
		)
	);
	public static final Block DARK_OAK_SLAB = register(
		"dark_oak_slab",
		new SlabBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block MANGROVE_SLAB = register(
		"mangrove_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block BAMBOO_SLAB = register(
		"bamboo_slab",
		new SlabBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.BAMBOO_WOOD).burnable()
		)
	);
	public static final Block BAMBOO_MOSAIC_SLAB = register(
		"bamboo_mosaic_slab",
		new SlabBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.BAMBOO_WOOD).burnable()
		)
	);
	public static final Block STONE_SLAB = register(
		"stone_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_STONE_SLAB = register(
		"smooth_stone_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SANDSTONE_SLAB = register(
		"sandstone_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block CUT_SANDSTONE_SLAB = register(
		"cut_sandstone_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block PETRIFIED_OAK_SLAB = register(
		"petrified_oak_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block COBBLESTONE_SLAB = register(
		"cobblestone_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block BRICK_SLAB = register(
		"brick_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block STONE_BRICK_SLAB = register(
		"stone_brick_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block MUD_BRICK_SLAB = register(
		"mud_brick_slab",
		new SlabBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.5F, 3.0F)
				.sounds(BlockSoundGroup.MUD_BRICKS)
		)
	);
	public static final Block NETHER_BRICK_SLAB = register(
		"nether_brick_slab",
		new SlabBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(2.0F, 6.0F)
				.sounds(BlockSoundGroup.NETHER_BRICKS)
		)
	);
	public static final Block QUARTZ_SLAB = register(
		"quartz_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block RED_SANDSTONE_SLAB = register(
		"red_sandstone_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block CUT_RED_SANDSTONE_SLAB = register(
		"cut_red_sandstone_slab",
		new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block PURPUR_SLAB = register(
		"purpur_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_STONE = register(
		"smooth_stone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_SANDSTONE = register(
		"smooth_sandstone",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_QUARTZ = register(
		"smooth_quartz", new Block(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SMOOTH_RED_SANDSTONE = register(
		"smooth_red_sandstone",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(2.0F, 6.0F))
	);
	public static final Block SPRUCE_FENCE_GATE = register(
		"spruce_fence_gate",
		new FenceGateBlock(
			WoodType.SPRUCE,
			AbstractBlock.Settings.create().mapColor(SPRUCE_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable()
		)
	);
	public static final Block BIRCH_FENCE_GATE = register(
		"birch_fence_gate",
		new FenceGateBlock(
			WoodType.BIRCH,
			AbstractBlock.Settings.create().mapColor(BIRCH_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable()
		)
	);
	public static final Block JUNGLE_FENCE_GATE = register(
		"jungle_fence_gate",
		new FenceGateBlock(
			WoodType.JUNGLE,
			AbstractBlock.Settings.create().mapColor(JUNGLE_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable()
		)
	);
	public static final Block ACACIA_FENCE_GATE = register(
		"acacia_fence_gate",
		new FenceGateBlock(
			WoodType.ACACIA,
			AbstractBlock.Settings.create().mapColor(ACACIA_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable()
		)
	);
	public static final Block CHERRY_FENCE_GATE = register(
		"cherry_fence_gate",
		new FenceGateBlock(
			WoodType.CHERRY,
			AbstractBlock.Settings.create().mapColor(CHERRY_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable()
		)
	);
	public static final Block DARK_OAK_FENCE_GATE = register(
		"dark_oak_fence_gate",
		new FenceGateBlock(
			WoodType.DARK_OAK,
			AbstractBlock.Settings.create().mapColor(DARK_OAK_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable()
		)
	);
	public static final Block MANGROVE_FENCE_GATE = register(
		"mangrove_fence_gate",
		new FenceGateBlock(
			WoodType.MANGROVE,
			AbstractBlock.Settings.create().mapColor(MANGROVE_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable()
		)
	);
	public static final Block BAMBOO_FENCE_GATE = register(
		"bamboo_fence_gate",
		new FenceGateBlock(
			WoodType.BAMBOO,
			AbstractBlock.Settings.create().mapColor(BAMBOO_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable()
		)
	);
	public static final Block SPRUCE_FENCE = register(
		"spruce_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(SPRUCE_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.burnable()
				.sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block BIRCH_FENCE = register(
		"birch_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(BIRCH_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.burnable()
				.sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block JUNGLE_FENCE = register(
		"jungle_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(JUNGLE_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.burnable()
				.sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block ACACIA_FENCE = register(
		"acacia_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(ACACIA_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.burnable()
				.sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block CHERRY_FENCE = register(
		"cherry_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(CHERRY_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.burnable()
				.sounds(BlockSoundGroup.CHERRY_WOOD)
		)
	);
	public static final Block DARK_OAK_FENCE = register(
		"dark_oak_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(DARK_OAK_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.burnable()
				.sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block MANGROVE_FENCE = register(
		"mangrove_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(MANGROVE_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.burnable()
				.sounds(BlockSoundGroup.WOOD)
		)
	);
	public static final Block BAMBOO_FENCE = register(
		"bamboo_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(BAMBOO_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.sounds(BlockSoundGroup.BAMBOO_WOOD)
				.burnable()
		)
	);
	public static final Block SPRUCE_DOOR = register(
		"spruce_door",
		new DoorBlock(
			BlockSetType.SPRUCE,
			AbstractBlock.Settings.create()
				.mapColor(SPRUCE_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BIRCH_DOOR = register(
		"birch_door",
		new DoorBlock(
			BlockSetType.BIRCH,
			AbstractBlock.Settings.create()
				.mapColor(BIRCH_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block JUNGLE_DOOR = register(
		"jungle_door",
		new DoorBlock(
			BlockSetType.JUNGLE,
			AbstractBlock.Settings.create()
				.mapColor(JUNGLE_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block ACACIA_DOOR = register(
		"acacia_door",
		new DoorBlock(
			BlockSetType.ACACIA,
			AbstractBlock.Settings.create()
				.mapColor(ACACIA_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CHERRY_DOOR = register(
		"cherry_door",
		new DoorBlock(
			BlockSetType.CHERRY,
			AbstractBlock.Settings.create()
				.mapColor(CHERRY_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block DARK_OAK_DOOR = register(
		"dark_oak_door",
		new DoorBlock(
			BlockSetType.DARK_OAK,
			AbstractBlock.Settings.create()
				.mapColor(DARK_OAK_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block MANGROVE_DOOR = register(
		"mangrove_door",
		new DoorBlock(
			BlockSetType.MANGROVE,
			AbstractBlock.Settings.create()
				.mapColor(MANGROVE_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BAMBOO_DOOR = register(
		"bamboo_door",
		new DoorBlock(
			BlockSetType.BAMBOO,
			AbstractBlock.Settings.create()
				.mapColor(BAMBOO_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block END_ROD = register(
		"end_rod", new EndRodBlock(AbstractBlock.Settings.create().notSolid().breakInstantly().luminance(state -> 14).sounds(BlockSoundGroup.WOOD).nonOpaque())
	);
	public static final Block CHORUS_PLANT = register(
		"chorus_plant",
		new ChorusPlantBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PURPLE)
				.notSolid()
				.strength(0.4F)
				.sounds(BlockSoundGroup.WOOD)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CHORUS_FLOWER = register(
		"chorus_flower",
		new ChorusFlowerBlock(
			CHORUS_PLANT,
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
		)
	);
	public static final Block PURPUR_BLOCK = register(
		"purpur_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PURPUR_PILLAR = register(
		"purpur_pillar",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block PURPUR_STAIRS = register("purpur_stairs", createOldStairsBlock(PURPUR_BLOCK));
	public static final Block END_STONE_BRICKS = register(
		"end_stone_bricks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 9.0F))
	);
	public static final Block TORCHFLOWER_CROP = register(
		"torchflower_crop",
		new TorchflowerBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.CROP)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block PITCHER_CROP = register(
		"pitcher_crop",
		new PitcherCropBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.CROP)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block PITCHER_PLANT = register(
		"pitcher_plant",
		new TallPlantBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.CROP)
				.offset(AbstractBlock.OffsetType.XZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BEETROOTS = register(
		"beetroots",
		new BeetrootsBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.CROP)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block DIRT_PATH = register(
		"dirt_path",
		new DirtPathBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DIRT_BROWN)
				.strength(0.65F)
				.sounds(BlockSoundGroup.GRASS)
				.blockVision(Blocks::always)
				.suffocates(Blocks::always)
		)
	);
	public static final Block END_GATEWAY = register(
		"end_gateway",
		new EndGatewayBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BLACK)
				.noCollision()
				.luminance(state -> 15)
				.strength(-1.0F, 3600000.0F)
				.dropsNothing()
				.pistonBehavior(PistonBehavior.BLOCK)
		)
	);
	public static final Block REPEATING_COMMAND_BLOCK = register(
		"repeating_command_block",
		new CommandBlock(false, AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block CHAIN_COMMAND_BLOCK = register(
		"chain_command_block",
		new CommandBlock(true, AbstractBlock.Settings.create().mapColor(MapColor.GREEN).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block FROSTED_ICE = register(
		"frosted_ice",
		new FrostedIceBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PALE_PURPLE)
				.slipperiness(0.98F)
				.ticksRandomly()
				.strength(0.5F)
				.sounds(BlockSoundGroup.GLASS)
				.nonOpaque()
				.allowsSpawning((state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR)
				.solidBlock(Blocks::never)
		)
	);
	public static final Block MAGMA_BLOCK = register(
		"magma_block",
		new MagmaBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.luminance(state -> 3)
				.strength(0.5F)
				.allowsSpawning((state, world, pos, entityType) -> entityType.isFireImmune())
				.postProcess(Blocks::always)
				.emissiveLighting(Blocks::always)
		)
	);
	public static final Block NETHER_WART_BLOCK = register(
		"nether_wart_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(1.0F).sounds(BlockSoundGroup.WART_BLOCK))
	);
	public static final Block RED_NETHER_BRICKS = register(
		"red_nether_bricks",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(2.0F, 6.0F)
				.sounds(BlockSoundGroup.NETHER_BRICKS)
		)
	);
	public static final Block BONE_BLOCK = register(
		"bone_block",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.XYLOPHONE).requiresTool().strength(2.0F).sounds(BlockSoundGroup.BONE)
		)
	);
	public static final Block STRUCTURE_VOID = register(
		"structure_void",
		new StructureVoidBlock(
			AbstractBlock.Settings.create().replaceable().noCollision().dropsNothing().noBlockBreakParticles().pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block OBSERVER = register(
		"observer",
		new ObserverBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).strength(3.0F).requiresTool().solidBlock(Blocks::never)
		)
	);
	public static final Block SHULKER_BOX = register("shulker_box", createShulkerBoxBlock(null, MapColor.PURPLE));
	public static final Block WHITE_SHULKER_BOX = register("white_shulker_box", createShulkerBoxBlock(DyeColor.WHITE, MapColor.WHITE));
	public static final Block ORANGE_SHULKER_BOX = register("orange_shulker_box", createShulkerBoxBlock(DyeColor.ORANGE, MapColor.ORANGE));
	public static final Block MAGENTA_SHULKER_BOX = register("magenta_shulker_box", createShulkerBoxBlock(DyeColor.MAGENTA, MapColor.MAGENTA));
	public static final Block LIGHT_BLUE_SHULKER_BOX = register("light_blue_shulker_box", createShulkerBoxBlock(DyeColor.LIGHT_BLUE, MapColor.LIGHT_BLUE));
	public static final Block YELLOW_SHULKER_BOX = register("yellow_shulker_box", createShulkerBoxBlock(DyeColor.YELLOW, MapColor.YELLOW));
	public static final Block LIME_SHULKER_BOX = register("lime_shulker_box", createShulkerBoxBlock(DyeColor.LIME, MapColor.LIME));
	public static final Block PINK_SHULKER_BOX = register("pink_shulker_box", createShulkerBoxBlock(DyeColor.PINK, MapColor.PINK));
	public static final Block GRAY_SHULKER_BOX = register("gray_shulker_box", createShulkerBoxBlock(DyeColor.GRAY, MapColor.GRAY));
	public static final Block LIGHT_GRAY_SHULKER_BOX = register("light_gray_shulker_box", createShulkerBoxBlock(DyeColor.LIGHT_GRAY, MapColor.LIGHT_GRAY));
	public static final Block CYAN_SHULKER_BOX = register("cyan_shulker_box", createShulkerBoxBlock(DyeColor.CYAN, MapColor.CYAN));
	public static final Block PURPLE_SHULKER_BOX = register("purple_shulker_box", createShulkerBoxBlock(DyeColor.PURPLE, MapColor.TERRACOTTA_PURPLE));
	public static final Block BLUE_SHULKER_BOX = register("blue_shulker_box", createShulkerBoxBlock(DyeColor.BLUE, MapColor.BLUE));
	public static final Block BROWN_SHULKER_BOX = register("brown_shulker_box", createShulkerBoxBlock(DyeColor.BROWN, MapColor.BROWN));
	public static final Block GREEN_SHULKER_BOX = register("green_shulker_box", createShulkerBoxBlock(DyeColor.GREEN, MapColor.GREEN));
	public static final Block RED_SHULKER_BOX = register("red_shulker_box", createShulkerBoxBlock(DyeColor.RED, MapColor.RED));
	public static final Block BLACK_SHULKER_BOX = register("black_shulker_box", createShulkerBoxBlock(DyeColor.BLACK, MapColor.BLACK));
	public static final Block WHITE_GLAZED_TERRACOTTA = register(
		"white_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.WHITE)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block ORANGE_GLAZED_TERRACOTTA = register(
		"orange_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.ORANGE)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block MAGENTA_GLAZED_TERRACOTTA = register(
		"magenta_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.MAGENTA)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block LIGHT_BLUE_GLAZED_TERRACOTTA = register(
		"light_blue_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.LIGHT_BLUE)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block YELLOW_GLAZED_TERRACOTTA = register(
		"yellow_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.YELLOW)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block LIME_GLAZED_TERRACOTTA = register(
		"lime_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.LIME)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block PINK_GLAZED_TERRACOTTA = register(
		"pink_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.PINK)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block GRAY_GLAZED_TERRACOTTA = register(
		"gray_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block LIGHT_GRAY_GLAZED_TERRACOTTA = register(
		"light_gray_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.LIGHT_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block CYAN_GLAZED_TERRACOTTA = register(
		"cyan_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.CYAN)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block PURPLE_GLAZED_TERRACOTTA = register(
		"purple_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.PURPLE)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block BLUE_GLAZED_TERRACOTTA = register(
		"blue_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.BLUE)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block BROWN_GLAZED_TERRACOTTA = register(
		"brown_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.BROWN)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block GREEN_GLAZED_TERRACOTTA = register(
		"green_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.GREEN)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block RED_GLAZED_TERRACOTTA = register(
		"red_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block BLACK_GLAZED_TERRACOTTA = register(
		"black_glazed_terracotta",
		new GlazedTerracottaBlock(
			AbstractBlock.Settings.create()
				.mapColor(DyeColor.BLACK)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(1.4F)
				.pistonBehavior(PistonBehavior.PUSH_ONLY)
		)
	);
	public static final Block WHITE_CONCRETE = register(
		"white_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block ORANGE_CONCRETE = register(
		"orange_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block MAGENTA_CONCRETE = register(
		"magenta_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.MAGENTA).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block LIGHT_BLUE_CONCRETE = register(
		"light_blue_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_BLUE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block YELLOW_CONCRETE = register(
		"yellow_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block LIME_CONCRETE = register(
		"lime_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.LIME).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block PINK_CONCRETE = register(
		"pink_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.PINK).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block GRAY_CONCRETE = register(
		"gray_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block LIGHT_GRAY_CONCRETE = register(
		"light_gray_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block CYAN_CONCRETE = register(
		"cyan_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.CYAN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block PURPLE_CONCRETE = register(
		"purple_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.PURPLE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block BLUE_CONCRETE = register(
		"blue_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.BLUE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block BROWN_CONCRETE = register(
		"brown_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.BROWN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block GREEN_CONCRETE = register(
		"green_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.GREEN).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block RED_CONCRETE = register(
		"red_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.RED).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block BLACK_CONCRETE = register(
		"black_concrete", new Block(AbstractBlock.Settings.create().mapColor(DyeColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(1.8F))
	);
	public static final Block WHITE_CONCRETE_POWDER = register(
		"white_concrete_powder",
		new ConcretePowderBlock(
			WHITE_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.WHITE).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block ORANGE_CONCRETE_POWDER = register(
		"orange_concrete_powder",
		new ConcretePowderBlock(
			ORANGE_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.ORANGE).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block MAGENTA_CONCRETE_POWDER = register(
		"magenta_concrete_powder",
		new ConcretePowderBlock(
			MAGENTA_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.MAGENTA).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block LIGHT_BLUE_CONCRETE_POWDER = register(
		"light_blue_concrete_powder",
		new ConcretePowderBlock(
			LIGHT_BLUE_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_BLUE).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block YELLOW_CONCRETE_POWDER = register(
		"yellow_concrete_powder",
		new ConcretePowderBlock(
			YELLOW_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.YELLOW).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block LIME_CONCRETE_POWDER = register(
		"lime_concrete_powder",
		new ConcretePowderBlock(
			LIME_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.LIME).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block PINK_CONCRETE_POWDER = register(
		"pink_concrete_powder",
		new ConcretePowderBlock(
			PINK_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.PINK).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block GRAY_CONCRETE_POWDER = register(
		"gray_concrete_powder",
		new ConcretePowderBlock(
			GRAY_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.GRAY).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block LIGHT_GRAY_CONCRETE_POWDER = register(
		"light_gray_concrete_powder",
		new ConcretePowderBlock(
			LIGHT_GRAY_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_GRAY).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block CYAN_CONCRETE_POWDER = register(
		"cyan_concrete_powder",
		new ConcretePowderBlock(
			CYAN_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.CYAN).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block PURPLE_CONCRETE_POWDER = register(
		"purple_concrete_powder",
		new ConcretePowderBlock(
			PURPLE_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.PURPLE).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block BLUE_CONCRETE_POWDER = register(
		"blue_concrete_powder",
		new ConcretePowderBlock(
			BLUE_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.BLUE).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block BROWN_CONCRETE_POWDER = register(
		"brown_concrete_powder",
		new ConcretePowderBlock(
			BROWN_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.BROWN).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block GREEN_CONCRETE_POWDER = register(
		"green_concrete_powder",
		new ConcretePowderBlock(
			GREEN_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.GREEN).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block RED_CONCRETE_POWDER = register(
		"red_concrete_powder",
		new ConcretePowderBlock(
			RED_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.RED).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block BLACK_CONCRETE_POWDER = register(
		"black_concrete_powder",
		new ConcretePowderBlock(
			BLACK_CONCRETE, AbstractBlock.Settings.create().mapColor(DyeColor.BLACK).instrument(Instrument.SNARE).strength(0.5F).sounds(BlockSoundGroup.SAND)
		)
	);
	public static final Block KELP = register(
		"kelp",
		new KelpBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WATER_BLUE)
				.noCollision()
				.ticksRandomly()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block KELP_PLANT = register(
		"kelp_plant",
		new KelpPlantBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WATER_BLUE)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block DRIED_KELP_BLOCK = register(
		"dried_kelp_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.5F, 2.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block TURTLE_EGG = register(
		"turtle_egg",
		new TurtleEggBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PALE_YELLOW)
				.solid()
				.strength(0.5F)
				.sounds(BlockSoundGroup.METAL)
				.ticksRandomly()
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SNIFFER_EGG = register(
		"sniffer_egg", new SnifferEggBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(0.5F).sounds(BlockSoundGroup.METAL).nonOpaque())
	);
	public static final Block DEAD_TUBE_CORAL_BLOCK = register(
		"dead_tube_coral_block",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_BRAIN_CORAL_BLOCK = register(
		"dead_brain_coral_block",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_BUBBLE_CORAL_BLOCK = register(
		"dead_bubble_coral_block",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_FIRE_CORAL_BLOCK = register(
		"dead_fire_coral_block",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block DEAD_HORN_CORAL_BLOCK = register(
		"dead_horn_coral_block",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block TUBE_CORAL_BLOCK = register(
		"tube_coral_block",
		new CoralBlockBlock(
			DEAD_TUBE_CORAL_BLOCK,
			AbstractBlock.Settings.create().mapColor(MapColor.BLUE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block BRAIN_CORAL_BLOCK = register(
		"brain_coral_block",
		new CoralBlockBlock(
			DEAD_BRAIN_CORAL_BLOCK,
			AbstractBlock.Settings.create().mapColor(MapColor.PINK).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block BUBBLE_CORAL_BLOCK = register(
		"bubble_coral_block",
		new CoralBlockBlock(
			DEAD_BUBBLE_CORAL_BLOCK,
			AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block FIRE_CORAL_BLOCK = register(
		"fire_coral_block",
		new CoralBlockBlock(
			DEAD_FIRE_CORAL_BLOCK,
			AbstractBlock.Settings.create().mapColor(MapColor.RED).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block HORN_CORAL_BLOCK = register(
		"horn_coral_block",
		new CoralBlockBlock(
			DEAD_HORN_CORAL_BLOCK,
			AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.CORAL)
		)
	);
	public static final Block DEAD_TUBE_CORAL = register(
		"dead_tube_coral",
		new DeadCoralBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block DEAD_BRAIN_CORAL = register(
		"dead_brain_coral",
		new DeadCoralBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block DEAD_BUBBLE_CORAL = register(
		"dead_bubble_coral",
		new DeadCoralBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block DEAD_FIRE_CORAL = register(
		"dead_fire_coral",
		new DeadCoralBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block DEAD_HORN_CORAL = register(
		"dead_horn_coral",
		new DeadCoralBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block TUBE_CORAL = register(
		"tube_coral",
		new CoralBlock(
			DEAD_TUBE_CORAL,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BLUE)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BRAIN_CORAL = register(
		"brain_coral",
		new CoralBlock(
			DEAD_BRAIN_CORAL,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PINK)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BUBBLE_CORAL = register(
		"bubble_coral",
		new CoralBlock(
			DEAD_BUBBLE_CORAL,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PURPLE)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block FIRE_CORAL = register(
		"fire_coral",
		new CoralBlock(
			DEAD_FIRE_CORAL,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.RED)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block HORN_CORAL = register(
		"horn_coral",
		new CoralBlock(
			DEAD_HORN_CORAL,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.YELLOW)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block DEAD_TUBE_CORAL_FAN = register(
		"dead_tube_coral_fan",
		new DeadCoralFanBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block DEAD_BRAIN_CORAL_FAN = register(
		"dead_brain_coral_fan",
		new DeadCoralFanBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block DEAD_BUBBLE_CORAL_FAN = register(
		"dead_bubble_coral_fan",
		new DeadCoralFanBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block DEAD_FIRE_CORAL_FAN = register(
		"dead_fire_coral_fan",
		new DeadCoralFanBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block DEAD_HORN_CORAL_FAN = register(
		"dead_horn_coral_fan",
		new DeadCoralFanBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GRAY).solid().instrument(Instrument.BASEDRUM).requiresTool().noCollision().breakInstantly()
		)
	);
	public static final Block TUBE_CORAL_FAN = register(
		"tube_coral_fan",
		new CoralFanBlock(
			DEAD_TUBE_CORAL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BLUE)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BRAIN_CORAL_FAN = register(
		"brain_coral_fan",
		new CoralFanBlock(
			DEAD_BRAIN_CORAL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PINK)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BUBBLE_CORAL_FAN = register(
		"bubble_coral_fan",
		new CoralFanBlock(
			DEAD_BUBBLE_CORAL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PURPLE)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block FIRE_CORAL_FAN = register(
		"fire_coral_fan",
		new CoralFanBlock(
			DEAD_FIRE_CORAL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.RED)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block HORN_CORAL_FAN = register(
		"horn_coral_fan",
		new CoralFanBlock(
			DEAD_HORN_CORAL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.YELLOW)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block DEAD_TUBE_CORAL_WALL_FAN = register(
		"dead_tube_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.GRAY)
				.solid()
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.noCollision()
				.breakInstantly()
				.dropsLike(DEAD_TUBE_CORAL_FAN)
		)
	);
	public static final Block DEAD_BRAIN_CORAL_WALL_FAN = register(
		"dead_brain_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.GRAY)
				.solid()
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.noCollision()
				.breakInstantly()
				.dropsLike(DEAD_BRAIN_CORAL_FAN)
		)
	);
	public static final Block DEAD_BUBBLE_CORAL_WALL_FAN = register(
		"dead_bubble_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.GRAY)
				.solid()
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.noCollision()
				.breakInstantly()
				.dropsLike(DEAD_BUBBLE_CORAL_FAN)
		)
	);
	public static final Block DEAD_FIRE_CORAL_WALL_FAN = register(
		"dead_fire_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.GRAY)
				.solid()
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.noCollision()
				.breakInstantly()
				.dropsLike(DEAD_FIRE_CORAL_FAN)
		)
	);
	public static final Block DEAD_HORN_CORAL_WALL_FAN = register(
		"dead_horn_coral_wall_fan",
		new DeadCoralWallFanBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.GRAY)
				.solid()
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.noCollision()
				.breakInstantly()
				.dropsLike(DEAD_HORN_CORAL_FAN)
		)
	);
	public static final Block TUBE_CORAL_WALL_FAN = register(
		"tube_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_TUBE_CORAL_WALL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BLUE)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(TUBE_CORAL_FAN)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BRAIN_CORAL_WALL_FAN = register(
		"brain_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_BRAIN_CORAL_WALL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PINK)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(BRAIN_CORAL_FAN)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BUBBLE_CORAL_WALL_FAN = register(
		"bubble_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_BUBBLE_CORAL_WALL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PURPLE)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(BUBBLE_CORAL_FAN)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block FIRE_CORAL_WALL_FAN = register(
		"fire_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_FIRE_CORAL_WALL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.RED)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(FIRE_CORAL_FAN)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block HORN_CORAL_WALL_FAN = register(
		"horn_coral_wall_fan",
		new CoralWallFanBlock(
			DEAD_HORN_CORAL_WALL_FAN,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.YELLOW)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WET_GRASS)
				.dropsLike(HORN_CORAL_FAN)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SEA_PICKLE = register(
		"sea_pickle",
		new SeaPickleBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.GREEN)
				.luminance(state -> SeaPickleBlock.isDry(state) ? 0 : 3 + 3 * (Integer)state.get(SeaPickleBlock.PICKLES))
				.sounds(BlockSoundGroup.SLIME)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BLUE_ICE = register(
		"blue_ice",
		new TranslucentBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_PURPLE).strength(2.8F).slipperiness(0.989F).sounds(BlockSoundGroup.GLASS))
	);
	public static final Block CONDUIT = register(
		"conduit",
		new ConduitBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).solid().instrument(Instrument.HAT).strength(3.0F).luminance(state -> 15).nonOpaque()
		)
	);
	public static final Block BAMBOO_SAPLING = register(
		"bamboo_sapling",
		new BambooShootBlock(
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
		)
	);
	public static final Block BAMBOO = register(
		"bamboo",
		new BambooBlock(
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
		)
	);
	public static final Block POTTED_BAMBOO = register("potted_bamboo", createFlowerPotBlock(BAMBOO));
	public static final Block VOID_AIR = register("void_air", new AirBlock(AbstractBlock.Settings.create().replaceable().noCollision().dropsNothing().air()));
	public static final Block CAVE_AIR = register("cave_air", new AirBlock(AbstractBlock.Settings.create().replaceable().noCollision().dropsNothing().air()));
	public static final Block BUBBLE_COLUMN = register(
		"bubble_column",
		new BubbleColumnBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WATER_BLUE)
				.replaceable()
				.noCollision()
				.dropsNothing()
				.pistonBehavior(PistonBehavior.DESTROY)
				.liquid()
				.sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
		)
	);
	public static final Block POLISHED_GRANITE_STAIRS = register("polished_granite_stairs", createOldStairsBlock(POLISHED_GRANITE));
	public static final Block SMOOTH_RED_SANDSTONE_STAIRS = register("smooth_red_sandstone_stairs", createOldStairsBlock(SMOOTH_RED_SANDSTONE));
	public static final Block MOSSY_STONE_BRICK_STAIRS = register("mossy_stone_brick_stairs", createOldStairsBlock(MOSSY_STONE_BRICKS));
	public static final Block POLISHED_DIORITE_STAIRS = register("polished_diorite_stairs", createOldStairsBlock(POLISHED_DIORITE));
	public static final Block MOSSY_COBBLESTONE_STAIRS = register("mossy_cobblestone_stairs", createOldStairsBlock(MOSSY_COBBLESTONE));
	public static final Block END_STONE_BRICK_STAIRS = register("end_stone_brick_stairs", createOldStairsBlock(END_STONE_BRICKS));
	public static final Block STONE_STAIRS = register("stone_stairs", createOldStairsBlock(STONE));
	public static final Block SMOOTH_SANDSTONE_STAIRS = register("smooth_sandstone_stairs", createOldStairsBlock(SMOOTH_SANDSTONE));
	public static final Block SMOOTH_QUARTZ_STAIRS = register("smooth_quartz_stairs", createOldStairsBlock(SMOOTH_QUARTZ));
	public static final Block GRANITE_STAIRS = register("granite_stairs", createOldStairsBlock(GRANITE));
	public static final Block ANDESITE_STAIRS = register("andesite_stairs", createOldStairsBlock(ANDESITE));
	public static final Block RED_NETHER_BRICK_STAIRS = register("red_nether_brick_stairs", createOldStairsBlock(RED_NETHER_BRICKS));
	public static final Block POLISHED_ANDESITE_STAIRS = register("polished_andesite_stairs", createOldStairsBlock(POLISHED_ANDESITE));
	public static final Block DIORITE_STAIRS = register("diorite_stairs", createOldStairsBlock(DIORITE));
	public static final Block POLISHED_GRANITE_SLAB = register("polished_granite_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(POLISHED_GRANITE)));
	public static final Block SMOOTH_RED_SANDSTONE_SLAB = register(
		"smooth_red_sandstone_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(SMOOTH_RED_SANDSTONE))
	);
	public static final Block MOSSY_STONE_BRICK_SLAB = register("mossy_stone_brick_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(MOSSY_STONE_BRICKS)));
	public static final Block POLISHED_DIORITE_SLAB = register("polished_diorite_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(POLISHED_DIORITE)));
	public static final Block MOSSY_COBBLESTONE_SLAB = register("mossy_cobblestone_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(MOSSY_COBBLESTONE)));
	public static final Block END_STONE_BRICK_SLAB = register("end_stone_brick_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(END_STONE_BRICKS)));
	public static final Block SMOOTH_SANDSTONE_SLAB = register("smooth_sandstone_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(SMOOTH_SANDSTONE)));
	public static final Block SMOOTH_QUARTZ_SLAB = register("smooth_quartz_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(SMOOTH_QUARTZ)));
	public static final Block GRANITE_SLAB = register("granite_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(GRANITE)));
	public static final Block ANDESITE_SLAB = register("andesite_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(ANDESITE)));
	public static final Block RED_NETHER_BRICK_SLAB = register("red_nether_brick_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(RED_NETHER_BRICKS)));
	public static final Block POLISHED_ANDESITE_SLAB = register("polished_andesite_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(POLISHED_ANDESITE)));
	public static final Block DIORITE_SLAB = register("diorite_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(DIORITE)));
	public static final Block BRICK_WALL = register("brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(BRICKS).solid()));
	public static final Block PRISMARINE_WALL = register("prismarine_wall", new WallBlock(AbstractBlock.Settings.copyShallow(PRISMARINE).solid()));
	public static final Block RED_SANDSTONE_WALL = register("red_sandstone_wall", new WallBlock(AbstractBlock.Settings.copyShallow(RED_SANDSTONE).solid()));
	public static final Block MOSSY_STONE_BRICK_WALL = register(
		"mossy_stone_brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(MOSSY_STONE_BRICKS).solid())
	);
	public static final Block GRANITE_WALL = register("granite_wall", new WallBlock(AbstractBlock.Settings.copyShallow(GRANITE).solid()));
	public static final Block STONE_BRICK_WALL = register("stone_brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(STONE_BRICKS).solid()));
	public static final Block MUD_BRICK_WALL = register("mud_brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(MUD_BRICKS).solid()));
	public static final Block NETHER_BRICK_WALL = register("nether_brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(NETHER_BRICKS).solid()));
	public static final Block ANDESITE_WALL = register("andesite_wall", new WallBlock(AbstractBlock.Settings.copyShallow(ANDESITE).solid()));
	public static final Block RED_NETHER_BRICK_WALL = register(
		"red_nether_brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(RED_NETHER_BRICKS).solid())
	);
	public static final Block SANDSTONE_WALL = register("sandstone_wall", new WallBlock(AbstractBlock.Settings.copyShallow(SANDSTONE).solid()));
	public static final Block END_STONE_BRICK_WALL = register("end_stone_brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(END_STONE_BRICKS).solid()));
	public static final Block DIORITE_WALL = register("diorite_wall", new WallBlock(AbstractBlock.Settings.copyShallow(DIORITE).solid()));
	public static final Block SCAFFOLDING = register(
		"scaffolding",
		new ScaffoldingBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PALE_YELLOW)
				.noCollision()
				.sounds(BlockSoundGroup.SCAFFOLDING)
				.dynamicBounds()
				.allowsSpawning(Blocks::never)
				.pistonBehavior(PistonBehavior.DESTROY)
				.solidBlock(Blocks::never)
		)
	);
	public static final Block LOOM = register(
		"loom",
		new LoomBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block BARREL = register(
		"barrel",
		new BarrelBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block SMOKER = register(
		"smoker",
		new SmokerBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(3.5F)
				.luminance(createLightLevelFromLitBlockState(13))
		)
	);
	public static final Block BLAST_FURNACE = register(
		"blast_furnace",
		new BlastFurnaceBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(3.5F)
				.luminance(createLightLevelFromLitBlockState(13))
		)
	);
	public static final Block CARTOGRAPHY_TABLE = register(
		"cartography_table",
		new CartographyTableBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block FLETCHING_TABLE = register(
		"fletching_table",
		new FletchingTableBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block GRINDSTONE = register(
		"grindstone",
		new GrindstoneBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.requiresTool()
				.strength(2.0F, 6.0F)
				.sounds(BlockSoundGroup.STONE)
				.pistonBehavior(PistonBehavior.BLOCK)
		)
	);
	public static final Block LECTERN = register(
		"lectern",
		new LecternBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block SMITHING_TABLE = register(
		"smithing_table",
		new SmithingTableBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block STONECUTTER = register(
		"stonecutter",
		new StonecutterBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.5F))
	);
	public static final Block BELL = register(
		"bell",
		new BellBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.GOLD)
				.solid()
				.requiresTool()
				.strength(5.0F)
				.sounds(BlockSoundGroup.ANVIL)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block LANTERN = register(
		"lantern",
		new LanternBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.solid()
				.requiresTool()
				.strength(3.5F)
				.sounds(BlockSoundGroup.LANTERN)
				.luminance(state -> 15)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SOUL_LANTERN = register(
		"soul_lantern",
		new LanternBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.solid()
				.requiresTool()
				.strength(3.5F)
				.sounds(BlockSoundGroup.LANTERN)
				.luminance(state -> 10)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CAMPFIRE = register(
		"campfire",
		new CampfireBlock(
			true,
			1,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.SPRUCE_BROWN)
				.instrument(Instrument.BASS)
				.strength(2.0F)
				.sounds(BlockSoundGroup.WOOD)
				.luminance(createLightLevelFromLitBlockState(15))
				.nonOpaque()
				.burnable()
		)
	);
	public static final Block SOUL_CAMPFIRE = register(
		"soul_campfire",
		new CampfireBlock(
			false,
			2,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.SPRUCE_BROWN)
				.instrument(Instrument.BASS)
				.strength(2.0F)
				.sounds(BlockSoundGroup.WOOD)
				.luminance(createLightLevelFromLitBlockState(10))
				.nonOpaque()
				.burnable()
		)
	);
	public static final Block SWEET_BERRY_BUSH = register(
		"sweet_berry_bush",
		new SweetBerryBushBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.ticksRandomly()
				.noCollision()
				.sounds(BlockSoundGroup.SWEET_BERRY_BUSH)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block WARPED_STEM = register("warped_stem", createNetherStemBlock(MapColor.DARK_AQUA));
	public static final Block STRIPPED_WARPED_STEM = register("stripped_warped_stem", createNetherStemBlock(MapColor.DARK_AQUA));
	public static final Block WARPED_HYPHAE = register(
		"warped_hyphae",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DARK_DULL_PINK).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)
		)
	);
	public static final Block STRIPPED_WARPED_HYPHAE = register(
		"stripped_warped_hyphae",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DARK_DULL_PINK).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)
		)
	);
	public static final Block WARPED_NYLIUM = register(
		"warped_nylium",
		new NyliumBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TEAL)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(0.4F)
				.sounds(BlockSoundGroup.NYLIUM)
				.ticksRandomly()
		)
	);
	public static final Block WARPED_FUNGUS = register(
		"warped_fungus",
		new FungusBlock(
			TreeConfiguredFeatures.WARPED_FUNGUS_PLANTED,
			WARPED_NYLIUM,
			AbstractBlock.Settings.create().mapColor(MapColor.CYAN).breakInstantly().noCollision().sounds(BlockSoundGroup.FUNGUS).pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block WARPED_WART_BLOCK = register(
		"warped_wart_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_TEAL).strength(1.0F).sounds(BlockSoundGroup.WART_BLOCK))
	);
	public static final Block WARPED_ROOTS = register(
		"warped_roots",
		new RootsBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.CYAN)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.ROOTS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block NETHER_SPROUTS = register(
		"nether_sprouts",
		new SproutsBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.CYAN)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.NETHER_SPROUTS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CRIMSON_STEM = register("crimson_stem", createNetherStemBlock(MapColor.DULL_PINK));
	public static final Block STRIPPED_CRIMSON_STEM = register("stripped_crimson_stem", createNetherStemBlock(MapColor.DULL_PINK));
	public static final Block CRIMSON_HYPHAE = register(
		"crimson_hyphae",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DARK_CRIMSON).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)
		)
	);
	public static final Block STRIPPED_CRIMSON_HYPHAE = register(
		"stripped_crimson_hyphae",
		new PillarBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DARK_CRIMSON).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)
		)
	);
	public static final Block CRIMSON_NYLIUM = register(
		"crimson_nylium",
		new NyliumBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DULL_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(0.4F)
				.sounds(BlockSoundGroup.NYLIUM)
				.ticksRandomly()
		)
	);
	public static final Block CRIMSON_FUNGUS = register(
		"crimson_fungus",
		new FungusBlock(
			TreeConfiguredFeatures.CRIMSON_FUNGUS_PLANTED,
			CRIMSON_NYLIUM,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.breakInstantly()
				.noCollision()
				.sounds(BlockSoundGroup.FUNGUS)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SHROOMLIGHT = register(
		"shroomlight", new Block(AbstractBlock.Settings.create().mapColor(MapColor.RED).strength(1.0F).sounds(BlockSoundGroup.SHROOMLIGHT).luminance(state -> 15))
	);
	public static final Block WEEPING_VINES = register(
		"weeping_vines",
		new WeepingVinesBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.ticksRandomly()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WEEPING_VINES)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block WEEPING_VINES_PLANT = register(
		"weeping_vines_plant",
		new WeepingVinesPlantBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WEEPING_VINES)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block TWISTING_VINES = register(
		"twisting_vines",
		new TwistingVinesBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.CYAN)
				.ticksRandomly()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WEEPING_VINES)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block TWISTING_VINES_PLANT = register(
		"twisting_vines_plant",
		new TwistingVinesPlantBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.CYAN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.WEEPING_VINES)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CRIMSON_ROOTS = register(
		"crimson_roots",
		new RootsBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.ROOTS)
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CRIMSON_PLANKS = register(
		"crimson_planks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.DULL_PINK).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.NETHER_WOOD))
	);
	public static final Block WARPED_PLANKS = register(
		"warped_planks",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.DARK_AQUA).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.NETHER_WOOD))
	);
	public static final Block CRIMSON_SLAB = register(
		"crimson_slab",
		new SlabBlock(
			AbstractBlock.Settings.create()
				.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.sounds(BlockSoundGroup.NETHER_WOOD)
		)
	);
	public static final Block WARPED_SLAB = register(
		"warped_slab",
		new SlabBlock(
			AbstractBlock.Settings.create()
				.mapColor(WARPED_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.sounds(BlockSoundGroup.NETHER_WOOD)
		)
	);
	public static final Block CRIMSON_PRESSURE_PLATE = register(
		"crimson_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.CRIMSON,
			AbstractBlock.Settings.create()
				.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block WARPED_PRESSURE_PLATE = register(
		"warped_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.WARPED,
			AbstractBlock.Settings.create()
				.mapColor(WARPED_PLANKS.getDefaultMapColor())
				.solid()
				.instrument(Instrument.BASS)
				.noCollision()
				.strength(0.5F)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CRIMSON_FENCE = register(
		"crimson_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.sounds(BlockSoundGroup.NETHER_WOOD)
		)
	);
	public static final Block WARPED_FENCE = register(
		"warped_fence",
		new FenceBlock(
			AbstractBlock.Settings.create()
				.mapColor(WARPED_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(2.0F, 3.0F)
				.sounds(BlockSoundGroup.NETHER_WOOD)
		)
	);
	public static final Block CRIMSON_TRAPDOOR = register(
		"crimson_trapdoor",
		new TrapdoorBlock(
			BlockSetType.CRIMSON,
			AbstractBlock.Settings.create()
				.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
		)
	);
	public static final Block WARPED_TRAPDOOR = register(
		"warped_trapdoor",
		new TrapdoorBlock(
			BlockSetType.WARPED,
			AbstractBlock.Settings.create()
				.mapColor(WARPED_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
		)
	);
	public static final Block CRIMSON_FENCE_GATE = register(
		"crimson_fence_gate",
		new FenceGateBlock(
			WoodType.CRIMSON, AbstractBlock.Settings.create().mapColor(CRIMSON_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F)
		)
	);
	public static final Block WARPED_FENCE_GATE = register(
		"warped_fence_gate",
		new FenceGateBlock(
			WoodType.WARPED, AbstractBlock.Settings.create().mapColor(WARPED_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F)
		)
	);
	public static final Block CRIMSON_STAIRS = register("crimson_stairs", createOldStairsBlock(CRIMSON_PLANKS));
	public static final Block WARPED_STAIRS = register("warped_stairs", createOldStairsBlock(WARPED_PLANKS));
	public static final Block CRIMSON_BUTTON = register("crimson_button", createWoodenButtonBlock(BlockSetType.CRIMSON));
	public static final Block WARPED_BUTTON = register("warped_button", createWoodenButtonBlock(BlockSetType.WARPED));
	public static final Block CRIMSON_DOOR = register(
		"crimson_door",
		new DoorBlock(
			BlockSetType.CRIMSON,
			AbstractBlock.Settings.create()
				.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block WARPED_DOOR = register(
		"warped_door",
		new DoorBlock(
			BlockSetType.WARPED,
			AbstractBlock.Settings.create()
				.mapColor(WARPED_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.strength(3.0F)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CRIMSON_SIGN = register(
		"crimson_sign",
		new SignBlock(
			WoodType.CRIMSON,
			AbstractBlock.Settings.create().mapColor(CRIMSON_PLANKS.getDefaultMapColor()).instrument(Instrument.BASS).solid().noCollision().strength(1.0F)
		)
	);
	public static final Block WARPED_SIGN = register(
		"warped_sign",
		new SignBlock(
			WoodType.WARPED,
			AbstractBlock.Settings.create().mapColor(WARPED_PLANKS.getDefaultMapColor()).instrument(Instrument.BASS).solid().noCollision().strength(1.0F)
		)
	);
	public static final Block CRIMSON_WALL_SIGN = register(
		"crimson_wall_sign",
		new WallSignBlock(
			WoodType.CRIMSON,
			AbstractBlock.Settings.create()
				.mapColor(CRIMSON_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.solid()
				.noCollision()
				.strength(1.0F)
				.dropsLike(CRIMSON_SIGN)
		)
	);
	public static final Block WARPED_WALL_SIGN = register(
		"warped_wall_sign",
		new WallSignBlock(
			WoodType.WARPED,
			AbstractBlock.Settings.create()
				.mapColor(WARPED_PLANKS.getDefaultMapColor())
				.instrument(Instrument.BASS)
				.solid()
				.noCollision()
				.strength(1.0F)
				.dropsLike(WARPED_SIGN)
		)
	);
	public static final Block STRUCTURE_BLOCK = register(
		"structure_block",
		new StructureBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_GRAY).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block JIGSAW = register(
		"jigsaw", new JigsawBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_GRAY).requiresTool().strength(-1.0F, 3600000.0F).dropsNothing())
	);
	public static final Block COMPOSTER = register(
		"composter",
		new ComposterBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(0.6F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block TARGET = register(
		"target", new TargetBlock(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).strength(0.5F).sounds(BlockSoundGroup.GRASS))
	);
	public static final Block BEE_NEST = register(
		"bee_nest",
		new BeehiveBlock(AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).instrument(Instrument.BASS).strength(0.3F).sounds(BlockSoundGroup.WOOD).burnable())
	);
	public static final Block BEEHIVE = register(
		"beehive",
		new BeehiveBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(0.6F).sounds(BlockSoundGroup.WOOD).burnable()
		)
	);
	public static final Block HONEY_BLOCK = register(
		"honey_block",
		new HoneyBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).velocityMultiplier(0.4F).jumpVelocityMultiplier(0.5F).nonOpaque().sounds(BlockSoundGroup.HONEY)
		)
	);
	public static final Block HONEYCOMB_BLOCK = register(
		"honeycomb_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(0.6F).sounds(BlockSoundGroup.CORAL))
	);
	public static final Block NETHERITE_BLOCK = register(
		"netherite_block",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.NETHERITE))
	);
	public static final Block ANCIENT_DEBRIS = register(
		"ancient_debris",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).requiresTool().strength(30.0F, 1200.0F).sounds(BlockSoundGroup.ANCIENT_DEBRIS))
	);
	public static final Block CRYING_OBSIDIAN = register(
		"crying_obsidian",
		new CryingObsidianBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(50.0F, 1200.0F).luminance(state -> 10)
		)
	);
	public static final Block RESPAWN_ANCHOR = register(
		"respawn_anchor",
		new RespawnAnchorBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BLACK)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(50.0F, 1200.0F)
				.luminance(state -> RespawnAnchorBlock.getLightLevel(state, 15))
		)
	);
	public static final Block POTTED_CRIMSON_FUNGUS = register("potted_crimson_fungus", createFlowerPotBlock(CRIMSON_FUNGUS));
	public static final Block POTTED_WARPED_FUNGUS = register("potted_warped_fungus", createFlowerPotBlock(WARPED_FUNGUS));
	public static final Block POTTED_CRIMSON_ROOTS = register("potted_crimson_roots", createFlowerPotBlock(CRIMSON_ROOTS));
	public static final Block POTTED_WARPED_ROOTS = register("potted_warped_roots", createFlowerPotBlock(WARPED_ROOTS));
	public static final Block LODESTONE = register(
		"lodestone",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.requiresTool()
				.strength(3.5F)
				.sounds(BlockSoundGroup.LODESTONE)
				.pistonBehavior(PistonBehavior.BLOCK)
		)
	);
	public static final Block BLACKSTONE = register(
		"blackstone", new Block(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F))
	);
	public static final Block BLACKSTONE_STAIRS = register("blackstone_stairs", createOldStairsBlock(BLACKSTONE));
	public static final Block BLACKSTONE_WALL = register("blackstone_wall", new WallBlock(AbstractBlock.Settings.copyShallow(BLACKSTONE).solid()));
	public static final Block BLACKSTONE_SLAB = register("blackstone_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(BLACKSTONE).strength(2.0F, 6.0F)));
	public static final Block POLISHED_BLACKSTONE = register("polished_blackstone", new Block(AbstractBlock.Settings.copyShallow(BLACKSTONE).strength(2.0F, 6.0F)));
	public static final Block POLISHED_BLACKSTONE_BRICKS = register(
		"polished_blackstone_bricks", new Block(AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE).strength(1.5F, 6.0F))
	);
	public static final Block CRACKED_POLISHED_BLACKSTONE_BRICKS = register(
		"cracked_polished_blackstone_bricks", new Block(AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE_BRICKS))
	);
	public static final Block CHISELED_POLISHED_BLACKSTONE = register(
		"chiseled_polished_blackstone", new Block(AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE).strength(1.5F, 6.0F))
	);
	public static final Block POLISHED_BLACKSTONE_BRICK_SLAB = register(
		"polished_blackstone_brick_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE_BRICKS).strength(2.0F, 6.0F))
	);
	public static final Block POLISHED_BLACKSTONE_BRICK_STAIRS = register("polished_blackstone_brick_stairs", createOldStairsBlock(POLISHED_BLACKSTONE_BRICKS));
	public static final Block POLISHED_BLACKSTONE_BRICK_WALL = register(
		"polished_blackstone_brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE_BRICKS).solid())
	);
	public static final Block GILDED_BLACKSTONE = register(
		"gilded_blackstone", new Block(AbstractBlock.Settings.copyShallow(BLACKSTONE).sounds(BlockSoundGroup.GILDED_BLACKSTONE))
	);
	public static final Block POLISHED_BLACKSTONE_STAIRS = register("polished_blackstone_stairs", createOldStairsBlock(POLISHED_BLACKSTONE));
	public static final Block POLISHED_BLACKSTONE_SLAB = register(
		"polished_blackstone_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE))
	);
	public static final Block POLISHED_BLACKSTONE_PRESSURE_PLATE = register(
		"polished_blackstone_pressure_plate",
		new PressurePlateBlock(
			BlockSetType.POLISHED_BLACKSTONE,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BLACK)
				.solid()
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.noCollision()
				.strength(0.5F)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block POLISHED_BLACKSTONE_BUTTON = register("polished_blackstone_button", createStoneButtonBlock());
	public static final Block POLISHED_BLACKSTONE_WALL = register(
		"polished_blackstone_wall", new WallBlock(AbstractBlock.Settings.copyShallow(POLISHED_BLACKSTONE).solid())
	);
	public static final Block CHISELED_NETHER_BRICKS = register(
		"chiseled_nether_bricks",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(2.0F, 6.0F)
				.sounds(BlockSoundGroup.NETHER_BRICKS)
		)
	);
	public static final Block CRACKED_NETHER_BRICKS = register(
		"cracked_nether_bricks",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_RED)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(2.0F, 6.0F)
				.sounds(BlockSoundGroup.NETHER_BRICKS)
		)
	);
	public static final Block QUARTZ_BRICKS = register("quartz_bricks", new Block(AbstractBlock.Settings.copyShallow(QUARTZ_BLOCK)));
	public static final Block CANDLE = register("candle", createCandleBlock(MapColor.PALE_YELLOW));
	public static final Block WHITE_CANDLE = register("white_candle", createCandleBlock(MapColor.WHITE_GRAY));
	public static final Block ORANGE_CANDLE = register("orange_candle", createCandleBlock(MapColor.ORANGE));
	public static final Block MAGENTA_CANDLE = register("magenta_candle", createCandleBlock(MapColor.MAGENTA));
	public static final Block LIGHT_BLUE_CANDLE = register("light_blue_candle", createCandleBlock(MapColor.LIGHT_BLUE));
	public static final Block YELLOW_CANDLE = register("yellow_candle", createCandleBlock(MapColor.YELLOW));
	public static final Block LIME_CANDLE = register("lime_candle", createCandleBlock(MapColor.LIME));
	public static final Block PINK_CANDLE = register("pink_candle", createCandleBlock(MapColor.PINK));
	public static final Block GRAY_CANDLE = register("gray_candle", createCandleBlock(MapColor.GRAY));
	public static final Block LIGHT_GRAY_CANDLE = register("light_gray_candle", createCandleBlock(MapColor.LIGHT_GRAY));
	public static final Block CYAN_CANDLE = register("cyan_candle", createCandleBlock(MapColor.CYAN));
	public static final Block PURPLE_CANDLE = register("purple_candle", createCandleBlock(MapColor.PURPLE));
	public static final Block BLUE_CANDLE = register("blue_candle", createCandleBlock(MapColor.BLUE));
	public static final Block BROWN_CANDLE = register("brown_candle", createCandleBlock(MapColor.BROWN));
	public static final Block GREEN_CANDLE = register("green_candle", createCandleBlock(MapColor.GREEN));
	public static final Block RED_CANDLE = register("red_candle", createCandleBlock(MapColor.RED));
	public static final Block BLACK_CANDLE = register("black_candle", createCandleBlock(MapColor.BLACK));
	public static final Block CANDLE_CAKE = register(
		"candle_cake", new CandleCakeBlock(CANDLE, AbstractBlock.Settings.copyShallow(CAKE).luminance(createLightLevelFromLitBlockState(3)))
	);
	public static final Block WHITE_CANDLE_CAKE = register("white_candle_cake", new CandleCakeBlock(WHITE_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block ORANGE_CANDLE_CAKE = register(
		"orange_candle_cake", new CandleCakeBlock(ORANGE_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE))
	);
	public static final Block MAGENTA_CANDLE_CAKE = register(
		"magenta_candle_cake", new CandleCakeBlock(MAGENTA_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE))
	);
	public static final Block LIGHT_BLUE_CANDLE_CAKE = register(
		"light_blue_candle_cake", new CandleCakeBlock(LIGHT_BLUE_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE))
	);
	public static final Block YELLOW_CANDLE_CAKE = register(
		"yellow_candle_cake", new CandleCakeBlock(YELLOW_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE))
	);
	public static final Block LIME_CANDLE_CAKE = register("lime_candle_cake", new CandleCakeBlock(LIME_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block PINK_CANDLE_CAKE = register("pink_candle_cake", new CandleCakeBlock(PINK_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block GRAY_CANDLE_CAKE = register("gray_candle_cake", new CandleCakeBlock(GRAY_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block LIGHT_GRAY_CANDLE_CAKE = register(
		"light_gray_candle_cake", new CandleCakeBlock(LIGHT_GRAY_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE))
	);
	public static final Block CYAN_CANDLE_CAKE = register("cyan_candle_cake", new CandleCakeBlock(CYAN_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block PURPLE_CANDLE_CAKE = register(
		"purple_candle_cake", new CandleCakeBlock(PURPLE_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE))
	);
	public static final Block BLUE_CANDLE_CAKE = register("blue_candle_cake", new CandleCakeBlock(BLUE_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block BROWN_CANDLE_CAKE = register("brown_candle_cake", new CandleCakeBlock(BROWN_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block GREEN_CANDLE_CAKE = register("green_candle_cake", new CandleCakeBlock(GREEN_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block RED_CANDLE_CAKE = register("red_candle_cake", new CandleCakeBlock(RED_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block BLACK_CANDLE_CAKE = register("black_candle_cake", new CandleCakeBlock(BLACK_CANDLE, AbstractBlock.Settings.copyShallow(CANDLE_CAKE)));
	public static final Block AMETHYST_BLOCK = register(
		"amethyst_block",
		new AmethystBlock(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).strength(1.5F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool())
	);
	public static final Block BUDDING_AMETHYST = register(
		"budding_amethyst",
		new BuddingAmethystBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PURPLE)
				.ticksRandomly()
				.strength(1.5F)
				.sounds(BlockSoundGroup.AMETHYST_BLOCK)
				.requiresTool()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block AMETHYST_CLUSTER = register(
		"amethyst_cluster",
		new AmethystClusterBlock(
			7.0F,
			3.0F,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.PURPLE)
				.solid()
				.nonOpaque()
				.sounds(BlockSoundGroup.AMETHYST_CLUSTER)
				.strength(1.5F)
				.luminance(state -> 5)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block LARGE_AMETHYST_BUD = register(
		"large_amethyst_bud",
		new AmethystClusterBlock(5.0F, 3.0F, AbstractBlock.Settings.copyShallow(AMETHYST_CLUSTER).sounds(BlockSoundGroup.MEDIUM_AMETHYST_BUD).luminance(state -> 4))
	);
	public static final Block MEDIUM_AMETHYST_BUD = register(
		"medium_amethyst_bud",
		new AmethystClusterBlock(4.0F, 3.0F, AbstractBlock.Settings.copyShallow(AMETHYST_CLUSTER).sounds(BlockSoundGroup.LARGE_AMETHYST_BUD).luminance(state -> 2))
	);
	public static final Block SMALL_AMETHYST_BUD = register(
		"small_amethyst_bud",
		new AmethystClusterBlock(3.0F, 4.0F, AbstractBlock.Settings.copyShallow(AMETHYST_CLUSTER).sounds(BlockSoundGroup.SMALL_AMETHYST_BUD).luminance(state -> 1))
	);
	public static final Block TUFF = register(
		"tuff",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_GRAY)
				.instrument(Instrument.BASEDRUM)
				.sounds(BlockSoundGroup.TUFF)
				.requiresTool()
				.strength(1.5F, 6.0F)
		)
	);
	public static final Block TUFF_SLAB = register("tuff_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(TUFF).requires(FeatureFlags.UPDATE_1_21)));
	public static final Block TUFF_STAIRS = register(
		"tuff_stairs", new StairsBlock(TUFF.getDefaultState(), AbstractBlock.Settings.copyShallow(TUFF).requires(FeatureFlags.UPDATE_1_21))
	);
	public static final Block TUFF_WALL = register("tuff_wall", new WallBlock(AbstractBlock.Settings.copyShallow(TUFF).solid().requires(FeatureFlags.UPDATE_1_21)));
	public static final Block POLISHED_TUFF = register(
		"polished_tuff", new Block(AbstractBlock.Settings.copyShallow(TUFF).sounds(BlockSoundGroup.POLISHED_TUFF).requires(FeatureFlags.UPDATE_1_21))
	);
	public static final Block POLISHED_TUFF_SLAB = register("polished_tuff_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(POLISHED_TUFF)));
	public static final Block POLISHED_TUFF_STAIRS = register(
		"polished_tuff_stairs", new StairsBlock(POLISHED_TUFF.getDefaultState(), AbstractBlock.Settings.copyShallow(POLISHED_TUFF))
	);
	public static final Block POLISHED_TUFF_WALL = register("polished_tuff_wall", new WallBlock(AbstractBlock.Settings.copyShallow(POLISHED_TUFF).solid()));
	public static final Block CHISELED_TUFF = register("chiseled_tuff", new Block(AbstractBlock.Settings.copyShallow(TUFF).requires(FeatureFlags.UPDATE_1_21)));
	public static final Block TUFF_BRICKS = register(
		"tuff_bricks", new Block(AbstractBlock.Settings.copyShallow(TUFF).sounds(BlockSoundGroup.TUFF_BRICKS).requires(FeatureFlags.UPDATE_1_21))
	);
	public static final Block TUFF_BRICK_SLAB = register("tuff_brick_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(TUFF_BRICKS)));
	public static final Block TUFF_BRICK_STAIRS = register(
		"tuff_brick_stairs", new StairsBlock(TUFF_BRICKS.getDefaultState(), AbstractBlock.Settings.copyShallow(TUFF_BRICKS))
	);
	public static final Block TUFF_BRICK_WALL = register("tuff_brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(TUFF_BRICKS).solid()));
	public static final Block CHISELED_TUFF_BRICKS = register("chiseled_tuff_bricks", new Block(AbstractBlock.Settings.copyShallow(TUFF_BRICKS)));
	public static final Block CALCITE = register(
		"calcite",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_WHITE)
				.instrument(Instrument.BASEDRUM)
				.sounds(BlockSoundGroup.CALCITE)
				.requiresTool()
				.strength(0.75F)
		)
	);
	public static final Block TINTED_GLASS = register(
		"tinted_glass",
		new TintedGlassBlock(
			AbstractBlock.Settings.copyShallow(GLASS)
				.mapColor(MapColor.GRAY)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.solidBlock(Blocks::never)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		)
	);
	public static final Block POWDER_SNOW = register(
		"powder_snow",
		new PowderSnowBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(0.25F).sounds(BlockSoundGroup.POWDER_SNOW).dynamicBounds().solidBlock(Blocks::never)
		)
	);
	public static final Block SCULK_SENSOR = register(
		"sculk_sensor",
		new SculkSensorBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.CYAN)
				.strength(1.5F)
				.sounds(BlockSoundGroup.SCULK_SENSOR)
				.luminance(state -> 1)
				.emissiveLighting((state, world, pos) -> SculkSensorBlock.getPhase(state) == SculkSensorPhase.ACTIVE)
		)
	);
	public static final Block CALIBRATED_SCULK_SENSOR = register(
		"calibrated_sculk_sensor", new CalibratedSculkSensorBlock(AbstractBlock.Settings.copyShallow(SCULK_SENSOR))
	);
	public static final Block SCULK = register(
		"sculk", new SculkBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(0.2F).sounds(BlockSoundGroup.SCULK))
	);
	public static final Block SCULK_VEIN = register(
		"sculk_vein",
		new SculkVeinBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.BLACK)
				.solid()
				.noCollision()
				.strength(0.2F)
				.sounds(BlockSoundGroup.SCULK_VEIN)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SCULK_CATALYST = register(
		"sculk_catalyst",
		new SculkCatalystBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(3.0F, 3.0F).sounds(BlockSoundGroup.SCULK_CATALYST).luminance(state -> 6)
		)
	);
	public static final Block SCULK_SHRIEKER = register(
		"sculk_shrieker",
		new SculkShriekerBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(3.0F, 3.0F).sounds(BlockSoundGroup.SCULK_SHRIEKER))
	);
	public static final Block COPPER_BLOCK = register(
		"copper_block",
		new OxidizableBlock(
			Oxidizable.OxidationLevel.UNAFFECTED,
			AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER)
		)
	);
	public static final Block EXPOSED_COPPER = register(
		"exposed_copper", new OxidizableBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(COPPER_BLOCK).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY))
	);
	public static final Block WEATHERED_COPPER = register(
		"weathered_copper", new OxidizableBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(COPPER_BLOCK).mapColor(MapColor.DARK_AQUA))
	);
	public static final Block OXIDIZED_COPPER = register(
		"oxidized_copper", new OxidizableBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(COPPER_BLOCK).mapColor(MapColor.TEAL))
	);
	public static final Block COPPER_ORE = register(
		"copper_ore", new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.copyShallow(IRON_ORE))
	);
	public static final Block DEEPSLATE_COPPER_ORE = register(
		"deepslate_copper_ore",
		new ExperienceDroppingBlock(
			ConstantIntProvider.create(0),
			AbstractBlock.Settings.copyShallow(COPPER_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE)
		)
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
	public static final Block OXIDIZED_CHISELED_COPPER = register(
		"oxidized_chiseled_copper",
		new OxidizableBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(OXIDIZED_COPPER).requires(FeatureFlags.UPDATE_1_21))
	);
	public static final Block WEATHERED_CHISELED_COPPER = register(
		"weathered_chiseled_copper",
		new OxidizableBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(WEATHERED_COPPER).requires(FeatureFlags.UPDATE_1_21))
	);
	public static final Block EXPOSED_CHISELED_COPPER = register(
		"exposed_chiseled_copper",
		new OxidizableBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(EXPOSED_COPPER).requires(FeatureFlags.UPDATE_1_21))
	);
	public static final Block CHISELED_COPPER = register(
		"chiseled_copper", new OxidizableBlock(Oxidizable.OxidationLevel.UNAFFECTED, AbstractBlock.Settings.copy(COPPER_BLOCK).requires(FeatureFlags.UPDATE_1_21))
	);
	public static final Block WAXED_OXIDIZED_CHISELED_COPPER = register(
		"waxed_oxidized_chiseled_copper", new Block(AbstractBlock.Settings.copy(OXIDIZED_CHISELED_COPPER))
	);
	public static final Block WAXED_WEATHERED_CHISELED_COPPER = register(
		"waxed_weathered_chiseled_copper", new Block(AbstractBlock.Settings.copy(WEATHERED_CHISELED_COPPER))
	);
	public static final Block WAXED_EXPOSED_CHISELED_COPPER = register(
		"waxed_exposed_chiseled_copper", new Block(AbstractBlock.Settings.copy(EXPOSED_CHISELED_COPPER))
	);
	public static final Block WAXED_CHISELED_COPPER = register("waxed_chiseled_copper", new Block(AbstractBlock.Settings.copy(CHISELED_COPPER)));
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
		"oxidized_cut_copper_slab", new OxidizableSlabBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(OXIDIZED_CUT_COPPER))
	);
	public static final Block WEATHERED_CUT_COPPER_SLAB = register(
		"weathered_cut_copper_slab", new OxidizableSlabBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(WEATHERED_CUT_COPPER))
	);
	public static final Block EXPOSED_CUT_COPPER_SLAB = register(
		"exposed_cut_copper_slab", new OxidizableSlabBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(EXPOSED_CUT_COPPER))
	);
	public static final Block CUT_COPPER_SLAB = register(
		"cut_copper_slab", new OxidizableSlabBlock(Oxidizable.OxidationLevel.UNAFFECTED, AbstractBlock.Settings.copy(CUT_COPPER))
	);
	public static final Block WAXED_COPPER_BLOCK = register("waxed_copper_block", new Block(AbstractBlock.Settings.copy(COPPER_BLOCK)));
	public static final Block WAXED_WEATHERED_COPPER = register("waxed_weathered_copper", new Block(AbstractBlock.Settings.copy(WEATHERED_COPPER)));
	public static final Block WAXED_EXPOSED_COPPER = register("waxed_exposed_copper", new Block(AbstractBlock.Settings.copy(EXPOSED_COPPER)));
	public static final Block WAXED_OXIDIZED_COPPER = register("waxed_oxidized_copper", new Block(AbstractBlock.Settings.copy(OXIDIZED_COPPER)));
	public static final Block WAXED_OXIDIZED_CUT_COPPER = register("waxed_oxidized_cut_copper", new Block(AbstractBlock.Settings.copy(OXIDIZED_COPPER)));
	public static final Block WAXED_WEATHERED_CUT_COPPER = register("waxed_weathered_cut_copper", new Block(AbstractBlock.Settings.copy(WEATHERED_COPPER)));
	public static final Block WAXED_EXPOSED_CUT_COPPER = register("waxed_exposed_cut_copper", new Block(AbstractBlock.Settings.copy(EXPOSED_COPPER)));
	public static final Block WAXED_CUT_COPPER = register("waxed_cut_copper", new Block(AbstractBlock.Settings.copy(COPPER_BLOCK)));
	public static final Block WAXED_OXIDIZED_CUT_COPPER_STAIRS = register("waxed_oxidized_cut_copper_stairs", createStairsBlock(WAXED_OXIDIZED_CUT_COPPER));
	public static final Block WAXED_WEATHERED_CUT_COPPER_STAIRS = register("waxed_weathered_cut_copper_stairs", createStairsBlock(WAXED_WEATHERED_CUT_COPPER));
	public static final Block WAXED_EXPOSED_CUT_COPPER_STAIRS = register("waxed_exposed_cut_copper_stairs", createStairsBlock(WAXED_EXPOSED_CUT_COPPER));
	public static final Block WAXED_CUT_COPPER_STAIRS = register("waxed_cut_copper_stairs", createStairsBlock(WAXED_CUT_COPPER));
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
	public static final Block COPPER_DOOR = register(
		"copper_door",
		new OxidizableDoorBlock(
			BlockSetType.COPPER,
			Oxidizable.OxidationLevel.UNAFFECTED,
			AbstractBlock.Settings.create()
				.mapColor(COPPER_BLOCK.getDefaultMapColor())
				.strength(3.0F, 6.0F)
				.nonOpaque()
				.requiresTool()
				.pistonBehavior(PistonBehavior.DESTROY)
				.requires(FeatureFlags.UPDATE_1_21)
		)
	);
	public static final Block EXPOSED_COPPER_DOOR = register(
		"exposed_copper_door",
		new OxidizableDoorBlock(
			BlockSetType.COPPER, Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(COPPER_DOOR).mapColor(EXPOSED_COPPER.getDefaultMapColor())
		)
	);
	public static final Block OXIDIZED_COPPER_DOOR = register(
		"oxidized_copper_door",
		new OxidizableDoorBlock(
			BlockSetType.COPPER, Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(COPPER_DOOR).mapColor(OXIDIZED_COPPER.getDefaultMapColor())
		)
	);
	public static final Block WEATHERED_COPPER_DOOR = register(
		"weathered_copper_door",
		new OxidizableDoorBlock(
			BlockSetType.COPPER, Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(COPPER_DOOR).mapColor(WEATHERED_COPPER.getDefaultMapColor())
		)
	);
	public static final Block WAXED_COPPER_DOOR = register("waxed_copper_door", new DoorBlock(BlockSetType.COPPER, AbstractBlock.Settings.copy(COPPER_DOOR)));
	public static final Block WAXED_EXPOSED_COPPER_DOOR = register(
		"waxed_exposed_copper_door", new DoorBlock(BlockSetType.COPPER, AbstractBlock.Settings.copy(EXPOSED_COPPER_DOOR))
	);
	public static final Block WAXED_OXIDIZED_COPPER_DOOR = register(
		"waxed_oxidized_copper_door", new DoorBlock(BlockSetType.COPPER, AbstractBlock.Settings.copy(OXIDIZED_COPPER_DOOR))
	);
	public static final Block WAXED_WEATHERED_COPPER_DOOR = register(
		"waxed_weathered_copper_door", new DoorBlock(BlockSetType.COPPER, AbstractBlock.Settings.copy(WEATHERED_COPPER_DOOR))
	);
	public static final Block COPPER_TRAPDOOR = register(
		"copper_trapdoor",
		new OxidizableTrapdoorBlock(
			BlockSetType.COPPER,
			Oxidizable.OxidationLevel.UNAFFECTED,
			AbstractBlock.Settings.create()
				.mapColor(COPPER_BLOCK.getDefaultMapColor())
				.strength(3.0F, 6.0F)
				.requiresTool()
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.requires(FeatureFlags.UPDATE_1_21)
		)
	);
	public static final Block EXPOSED_COPPER_TRAPDOOR = register(
		"exposed_copper_trapdoor",
		new OxidizableTrapdoorBlock(
			BlockSetType.COPPER, Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(COPPER_TRAPDOOR).mapColor(EXPOSED_COPPER.getDefaultMapColor())
		)
	);
	public static final Block OXIDIZED_COPPER_TRAPDOOR = register(
		"oxidized_copper_trapdoor",
		new OxidizableTrapdoorBlock(
			BlockSetType.COPPER, Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(COPPER_TRAPDOOR).mapColor(OXIDIZED_COPPER.getDefaultMapColor())
		)
	);
	public static final Block WEATHERED_COPPER_TRAPDOOR = register(
		"weathered_copper_trapdoor",
		new OxidizableTrapdoorBlock(
			BlockSetType.COPPER, Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(COPPER_TRAPDOOR).mapColor(WEATHERED_COPPER.getDefaultMapColor())
		)
	);
	public static final Block WAXED_COPPER_TRAPDOOR = register(
		"waxed_copper_trapdoor", new TrapdoorBlock(BlockSetType.COPPER, AbstractBlock.Settings.copy(COPPER_TRAPDOOR))
	);
	public static final Block WAXED_EXPOSED_COPPER_TRAPDOOR = register(
		"waxed_exposed_copper_trapdoor", new TrapdoorBlock(BlockSetType.COPPER, AbstractBlock.Settings.copy(EXPOSED_COPPER_TRAPDOOR))
	);
	public static final Block WAXED_OXIDIZED_COPPER_TRAPDOOR = register(
		"waxed_oxidized_copper_trapdoor", new TrapdoorBlock(BlockSetType.COPPER, AbstractBlock.Settings.copy(OXIDIZED_COPPER_TRAPDOOR))
	);
	public static final Block WAXED_WEATHERED_COPPER_TRAPDOOR = register(
		"waxed_weathered_copper_trapdoor", new TrapdoorBlock(BlockSetType.COPPER, AbstractBlock.Settings.copy(WEATHERED_COPPER_TRAPDOOR))
	);
	public static final Block COPPER_GRATE = register(
		"copper_grate",
		new OxidizableGrateBlock(
			Oxidizable.OxidationLevel.UNAFFECTED,
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
				.requires(FeatureFlags.UPDATE_1_21)
		)
	);
	public static final Block EXPOSED_COPPER_GRATE = register(
		"exposed_copper_grate",
		new OxidizableGrateBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(COPPER_GRATE).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY))
	);
	public static final Block WEATHERED_COPPER_GRATE = register(
		"weathered_copper_grate",
		new OxidizableGrateBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(COPPER_GRATE).mapColor(MapColor.DARK_AQUA))
	);
	public static final Block OXIDIZED_COPPER_GRATE = register(
		"oxidized_copper_grate", new OxidizableGrateBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(COPPER_GRATE).mapColor(MapColor.TEAL))
	);
	public static final Block WAXED_COPPER_GRATE = register("waxed_copper_grate", new GrateBlock(AbstractBlock.Settings.copy(COPPER_GRATE)));
	public static final Block WAXED_EXPOSED_COPPER_GRATE = register(
		"waxed_exposed_copper_grate", new GrateBlock(AbstractBlock.Settings.copy(EXPOSED_COPPER_GRATE))
	);
	public static final Block WAXED_WEATHERED_COPPER_GRATE = register(
		"waxed_weathered_copper_grate", new GrateBlock(AbstractBlock.Settings.copy(WEATHERED_COPPER_GRATE))
	);
	public static final Block WAXED_OXIDIZED_COPPER_GRATE = register(
		"waxed_oxidized_copper_grate", new GrateBlock(AbstractBlock.Settings.copy(OXIDIZED_COPPER_GRATE))
	);
	public static final Block COPPER_BULB = register(
		"copper_bulb",
		new OxidizableBulbBlock(
			Oxidizable.OxidationLevel.UNAFFECTED,
			AbstractBlock.Settings.create()
				.mapColor(COPPER_BLOCK.getDefaultMapColor())
				.strength(3.0F, 6.0F)
				.sounds(BlockSoundGroup.COPPER_BULB)
				.requiresTool()
				.solidBlock(Blocks::never)
				.luminance(createLightLevelFromLitBlockState(15))
				.requires(FeatureFlags.UPDATE_1_21)
		)
	);
	public static final Block EXPOSED_COPPER_BULB = register(
		"exposed_copper_bulb",
		new OxidizableBulbBlock(
			Oxidizable.OxidationLevel.EXPOSED,
			AbstractBlock.Settings.copy(COPPER_BULB).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).luminance(createLightLevelFromLitBlockState(12))
		)
	);
	public static final Block WEATHERED_COPPER_BULB = register(
		"weathered_copper_bulb",
		new OxidizableBulbBlock(
			Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(COPPER_BULB).mapColor(MapColor.DARK_AQUA).luminance(createLightLevelFromLitBlockState(8))
		)
	);
	public static final Block OXIDIZED_COPPER_BULB = register(
		"oxidized_copper_bulb",
		new OxidizableBulbBlock(
			Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(COPPER_BULB).mapColor(MapColor.TEAL).luminance(createLightLevelFromLitBlockState(4))
		)
	);
	public static final Block WAXED_COPPER_BULB = register("waxed_copper_bulb", new BulbBlock(AbstractBlock.Settings.copy(COPPER_BULB)));
	public static final Block WAXED_EXPOSED_COPPER_BULB = register("waxed_exposed_copper_bulb", new BulbBlock(AbstractBlock.Settings.copy(EXPOSED_COPPER_BULB)));
	public static final Block WAXED_WEATHERED_COPPER_BULB = register(
		"waxed_weathered_copper_bulb", new BulbBlock(AbstractBlock.Settings.copy(WEATHERED_COPPER_BULB))
	);
	public static final Block WAXED_OXIDIZED_COPPER_BULB = register("waxed_oxidized_copper_bulb", new BulbBlock(AbstractBlock.Settings.copy(OXIDIZED_COPPER_BULB)));
	public static final Block LIGHTNING_ROD = register(
		"lightning_rod",
		new LightningRodBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).solid().requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER).nonOpaque()
		)
	);
	public static final Block POINTED_DRIPSTONE = register(
		"pointed_dripstone",
		new PointedDripstoneBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_BROWN)
				.solid()
				.instrument(Instrument.BASEDRUM)
				.nonOpaque()
				.sounds(BlockSoundGroup.POINTED_DRIPSTONE)
				.ticksRandomly()
				.strength(1.5F, 3.0F)
				.dynamicBounds()
				.offset(AbstractBlock.OffsetType.XZ)
				.pistonBehavior(PistonBehavior.DESTROY)
				.solidBlock(Blocks::never)
		)
	);
	public static final Block DRIPSTONE_BLOCK = register(
		"dripstone_block",
		new Block(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.TERRACOTTA_BROWN)
				.instrument(Instrument.BASEDRUM)
				.sounds(BlockSoundGroup.DRIPSTONE_BLOCK)
				.requiresTool()
				.strength(1.5F, 1.0F)
		)
	);
	public static final Block CAVE_VINES = register(
		"cave_vines",
		new CaveVinesHeadBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.ticksRandomly()
				.noCollision()
				.luminance(CaveVines.getLuminanceSupplier(14))
				.breakInstantly()
				.sounds(BlockSoundGroup.CAVE_VINES)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block CAVE_VINES_PLANT = register(
		"cave_vines_plant",
		new CaveVinesBodyBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.luminance(CaveVines.getLuminanceSupplier(14))
				.breakInstantly()
				.sounds(BlockSoundGroup.CAVE_VINES)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SPORE_BLOSSOM = register(
		"spore_blossom",
		new SporeBlossomBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.breakInstantly()
				.noCollision()
				.sounds(BlockSoundGroup.SPORE_BLOSSOM)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block AZALEA = register(
		"azalea",
		new AzaleaBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.notSolid()
				.breakInstantly()
				.sounds(BlockSoundGroup.AZALEA)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block FLOWERING_AZALEA = register(
		"flowering_azalea",
		new AzaleaBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.notSolid()
				.breakInstantly()
				.sounds(BlockSoundGroup.FLOWERING_AZALEA)
				.nonOpaque()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block MOSS_CARPET = register(
		"moss_carpet",
		new CarpetBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.MOSS_CARPET).pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block PINK_PETALS = register(
		"pink_petals",
		new FlowerbedBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.DARK_GREEN).noCollision().sounds(BlockSoundGroup.PINK_PETALS).pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block MOSS_BLOCK = register(
		"moss_block",
		new MossBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.1F).sounds(BlockSoundGroup.MOSS_BLOCK).pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BIG_DRIPLEAF = register(
		"big_dripleaf",
		new BigDripleafBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.notSolid()
				.strength(0.1F)
				.sounds(BlockSoundGroup.BIG_DRIPLEAF)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block BIG_DRIPLEAF_STEM = register(
		"big_dripleaf_stem",
		new BigDripleafStemBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.strength(0.1F)
				.sounds(BlockSoundGroup.BIG_DRIPLEAF)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block SMALL_DRIPLEAF = register(
		"small_dripleaf",
		new SmallDripleafBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.SMALL_DRIPLEAF)
				.offset(AbstractBlock.OffsetType.XYZ)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block HANGING_ROOTS = register(
		"hanging_roots",
		new HangingRootsBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DIRT_BROWN)
				.replaceable()
				.noCollision()
				.breakInstantly()
				.sounds(BlockSoundGroup.HANGING_ROOTS)
				.offset(AbstractBlock.OffsetType.XZ)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block ROOTED_DIRT = register(
		"rooted_dirt", new RootedDirtBlock(AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).strength(0.5F).sounds(BlockSoundGroup.ROOTED_DIRT))
	);
	public static final Block MUD = register(
		"mud",
		new MudBlock(
			AbstractBlock.Settings.copyShallow(DIRT)
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
		new PillarBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DEEPSLATE_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.strength(3.0F, 6.0F)
				.sounds(BlockSoundGroup.DEEPSLATE)
		)
	);
	public static final Block COBBLED_DEEPSLATE = register("cobbled_deepslate", new Block(AbstractBlock.Settings.copyShallow(DEEPSLATE).strength(3.5F, 6.0F)));
	public static final Block COBBLED_DEEPSLATE_STAIRS = register("cobbled_deepslate_stairs", createOldStairsBlock(COBBLED_DEEPSLATE));
	public static final Block COBBLED_DEEPSLATE_SLAB = register("cobbled_deepslate_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE)));
	public static final Block COBBLED_DEEPSLATE_WALL = register(
		"cobbled_deepslate_wall", new WallBlock(AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).solid())
	);
	public static final Block POLISHED_DEEPSLATE = register(
		"polished_deepslate", new Block(AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.POLISHED_DEEPSLATE))
	);
	public static final Block POLISHED_DEEPSLATE_STAIRS = register("polished_deepslate_stairs", createOldStairsBlock(POLISHED_DEEPSLATE));
	public static final Block POLISHED_DEEPSLATE_SLAB = register("polished_deepslate_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(POLISHED_DEEPSLATE)));
	public static final Block POLISHED_DEEPSLATE_WALL = register(
		"polished_deepslate_wall", new WallBlock(AbstractBlock.Settings.copyShallow(POLISHED_DEEPSLATE).solid())
	);
	public static final Block DEEPSLATE_TILES = register(
		"deepslate_tiles", new Block(AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE_TILES))
	);
	public static final Block DEEPSLATE_TILE_STAIRS = register("deepslate_tile_stairs", createOldStairsBlock(DEEPSLATE_TILES));
	public static final Block DEEPSLATE_TILE_SLAB = register("deepslate_tile_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(DEEPSLATE_TILES)));
	public static final Block DEEPSLATE_TILE_WALL = register("deepslate_tile_wall", new WallBlock(AbstractBlock.Settings.copyShallow(DEEPSLATE_TILES).solid()));
	public static final Block DEEPSLATE_BRICKS = register(
		"deepslate_bricks", new Block(AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE_BRICKS))
	);
	public static final Block DEEPSLATE_BRICK_STAIRS = register("deepslate_brick_stairs", createOldStairsBlock(DEEPSLATE_BRICKS));
	public static final Block DEEPSLATE_BRICK_SLAB = register("deepslate_brick_slab", new SlabBlock(AbstractBlock.Settings.copyShallow(DEEPSLATE_BRICKS)));
	public static final Block DEEPSLATE_BRICK_WALL = register("deepslate_brick_wall", new WallBlock(AbstractBlock.Settings.copyShallow(DEEPSLATE_BRICKS).solid()));
	public static final Block CHISELED_DEEPSLATE = register(
		"chiseled_deepslate", new Block(AbstractBlock.Settings.copyShallow(COBBLED_DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE_BRICKS))
	);
	public static final Block CRACKED_DEEPSLATE_BRICKS = register("cracked_deepslate_bricks", new Block(AbstractBlock.Settings.copyShallow(DEEPSLATE_BRICKS)));
	public static final Block CRACKED_DEEPSLATE_TILES = register("cracked_deepslate_tiles", new Block(AbstractBlock.Settings.copyShallow(DEEPSLATE_TILES)));
	public static final Block INFESTED_DEEPSLATE = register(
		"infested_deepslate",
		new RotatedInfestedBlock(DEEPSLATE, AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).sounds(BlockSoundGroup.DEEPSLATE))
	);
	public static final Block SMOOTH_BASALT = register("smooth_basalt", new Block(AbstractBlock.Settings.copyShallow(BASALT)));
	public static final Block RAW_IRON_BLOCK = register(
		"raw_iron_block",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.RAW_IRON_PINK).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F))
	);
	public static final Block RAW_COPPER_BLOCK = register(
		"raw_copper_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F))
	);
	public static final Block RAW_GOLD_BLOCK = register(
		"raw_gold_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.GOLD).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F))
	);
	public static final Block POTTED_AZALEA_BUSH = register("potted_azalea_bush", createFlowerPotBlock(AZALEA));
	public static final Block POTTED_FLOWERING_AZALEA_BUSH = register("potted_flowering_azalea_bush", createFlowerPotBlock(FLOWERING_AZALEA));
	public static final Block OCHRE_FROGLIGHT = register(
		"ochre_froglight",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.3F).luminance(state -> 15).sounds(BlockSoundGroup.FROGLIGHT))
	);
	public static final Block VERDANT_FROGLIGHT = register(
		"verdant_froglight",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.LICHEN_GREEN).strength(0.3F).luminance(state -> 15).sounds(BlockSoundGroup.FROGLIGHT))
	);
	public static final Block PEARLESCENT_FROGLIGHT = register(
		"pearlescent_froglight",
		new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.PINK).strength(0.3F).luminance(state -> 15).sounds(BlockSoundGroup.FROGLIGHT))
	);
	public static final Block FROGSPAWN = register(
		"frogspawn",
		new FrogspawnBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.WATER_BLUE)
				.breakInstantly()
				.nonOpaque()
				.noCollision()
				.sounds(BlockSoundGroup.FROGSPAWN)
				.pistonBehavior(PistonBehavior.DESTROY)
		)
	);
	public static final Block REINFORCED_DEEPSLATE = register(
		"reinforced_deepslate",
		new Block(
			AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).instrument(Instrument.BASEDRUM).sounds(BlockSoundGroup.DEEPSLATE).strength(55.0F, 1200.0F)
		)
	);
	public static final Block DECORATED_POT = register(
		"decorated_pot",
		new DecoratedPotBlock(
			AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_RED).strength(0.0F, 0.0F).pistonBehavior(PistonBehavior.DESTROY).nonOpaque()
		)
	);
	public static final Block CRAFTER = register(
		"crafter", new CrafterBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5F, 3.5F).requires(FeatureFlags.UPDATE_1_21))
	);
	public static final Block TRIAL_SPAWNER = register(
		"trial_spawner",
		new TrialSpawnerBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.instrument(Instrument.BASEDRUM)
				.requiresTool()
				.luminance(state -> ((TrialSpawnerState)state.get(TrialSpawnerBlock.TRIAL_SPAWNER_STATE)).getLuminance())
				.strength(50.0F)
				.sounds(BlockSoundGroup.TRIAL_SPAWNER)
				.blockVision(Blocks::never)
				.nonOpaque()
				.requires(FeatureFlags.UPDATE_1_21)
		)
	);
	public static final Block VAULT = register(
		"vault",
		new VaultBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.instrument(Instrument.BASEDRUM)
				.nonOpaque()
				.requiresTool()
				.sounds(BlockSoundGroup.VAULT)
				.luminance(state -> ((VaultState)state.get(VaultBlock.VAULT_STATE)).getLuminance())
				.strength(50.0F)
				.blockVision(Blocks::never)
				.requires(FeatureFlags.UPDATE_1_21)
		)
	);
	public static final Block HEAVY_CORE = register(
		"heavy_core",
		new HeavyCoreBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.IRON_GRAY)
				.instrument(Instrument.SNARE)
				.sounds(BlockSoundGroup.HEAVY_CORE)
				.strength(10.0F)
				.pistonBehavior(PistonBehavior.NORMAL)
				.resistance(1200.0F)
				.requires(FeatureFlags.UPDATE_1_21)
		)
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

	private static Block createBedBlock(DyeColor color) {
		return new BedBlock(
			color,
			AbstractBlock.Settings.create()
				.mapColor(state -> state.get(BedBlock.PART) == BedPart.FOOT ? color.getMapColor() : MapColor.WHITE_GRAY)
				.sounds(BlockSoundGroup.WOOD)
				.strength(0.2F)
				.nonOpaque()
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
		);
	}

	private static Block createLogBlock(MapColor topMapColor, MapColor sideMapColor) {
		return new PillarBlock(
			AbstractBlock.Settings.create()
				.mapColor(state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor)
				.instrument(Instrument.BASS)
				.strength(2.0F)
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
		);
	}

	private static Block createLogBlock(MapColor topMapColor, MapColor sideMapColor, BlockSoundGroup soundGroup) {
		return new PillarBlock(
			AbstractBlock.Settings.create()
				.mapColor(state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor)
				.instrument(Instrument.BASS)
				.strength(2.0F)
				.sounds(soundGroup)
				.burnable()
		);
	}

	private static Block createNetherStemBlock(MapColor mapColor) {
		return new PillarBlock(
			AbstractBlock.Settings.create().mapColor(state -> mapColor).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.NETHER_STEM)
		);
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

	private static Block createStainedGlassBlock(DyeColor color) {
		return new StainedGlassBlock(
			color,
			AbstractBlock.Settings.create()
				.mapColor(color)
				.instrument(Instrument.HAT)
				.strength(0.3F)
				.sounds(BlockSoundGroup.GLASS)
				.nonOpaque()
				.allowsSpawning(Blocks::never)
				.solidBlock(Blocks::never)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
		);
	}

	private static Block createLeavesBlock(BlockSoundGroup soundGroup) {
		return new LeavesBlock(
			AbstractBlock.Settings.create()
				.mapColor(MapColor.DARK_GREEN)
				.strength(0.2F)
				.ticksRandomly()
				.sounds(soundGroup)
				.nonOpaque()
				.allowsSpawning(Blocks::canSpawnOnLeaves)
				.suffocates(Blocks::never)
				.blockVision(Blocks::never)
				.burnable()
				.pistonBehavior(PistonBehavior.DESTROY)
				.solidBlock(Blocks::never)
		);
	}

	private static Block createShulkerBoxBlock(@Nullable DyeColor color, MapColor mapColor) {
		return new ShulkerBoxBlock(
			color,
			AbstractBlock.Settings.create()
				.mapColor(mapColor)
				.solid()
				.strength(2.0F)
				.dynamicBounds()
				.nonOpaque()
				.suffocates(SHULKER_BOX_SUFFOCATES_PREDICATE)
				.blockVision(SHULKER_BOX_SUFFOCATES_PREDICATE)
				.pistonBehavior(PistonBehavior.DESTROY)
		);
	}

	private static Block createPistonBlock(boolean sticky) {
		AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> !(Boolean)state.get(PistonBlock.EXTENDED);
		return new PistonBlock(
			sticky,
			AbstractBlock.Settings.create()
				.mapColor(MapColor.STONE_GRAY)
				.strength(1.5F)
				.solidBlock(Blocks::never)
				.suffocates(contextPredicate)
				.blockVision(contextPredicate)
				.pistonBehavior(PistonBehavior.BLOCK)
		);
	}

	private static Block createWoodenButtonBlock(BlockSetType blockSetType) {
		return new ButtonBlock(blockSetType, 30, AbstractBlock.Settings.create().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY));
	}

	private static Block createStoneButtonBlock() {
		return new ButtonBlock(BlockSetType.STONE, 20, AbstractBlock.Settings.create().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY));
	}

	private static Block createFlowerPotBlock(Block flower) {
		return new FlowerPotBlock(flower, AbstractBlock.Settings.create().breakInstantly().nonOpaque().pistonBehavior(PistonBehavior.DESTROY));
	}

	private static Block createCandleBlock(MapColor color) {
		return new CandleBlock(
			AbstractBlock.Settings.create()
				.mapColor(color)
				.nonOpaque()
				.strength(0.1F)
				.sounds(BlockSoundGroup.CANDLE)
				.luminance(CandleBlock.STATE_TO_LUMINANCE)
				.pistonBehavior(PistonBehavior.DESTROY)
		);
	}

	@Deprecated
	private static Block createOldStairsBlock(Block block) {
		return new StairsBlock(block.getDefaultState(), AbstractBlock.Settings.copyShallow(block));
	}

	private static Block createStairsBlock(Block base) {
		return new StairsBlock(base.getDefaultState(), AbstractBlock.Settings.copy(base));
	}

	public static Block register(String id, Block block) {
		return Registry.register(Registries.BLOCK, id, block);
	}

	public static Block register(RegistryKey<Block> key, Block block) {
		return Registry.register(Registries.BLOCK, key, block);
	}

	public static void refreshShapeCache() {
		Block.STATE_IDS.forEach(AbstractBlock.AbstractBlockState::initShapeCache);
	}

	static {
		for (Block block : Registries.BLOCK) {
			for (BlockState blockState : block.getStateManager().getStates()) {
				Block.STATE_IDS.add(blockState);
				blockState.initShapeCache();
			}

			block.getLootTableKey();
		}
	}
}
