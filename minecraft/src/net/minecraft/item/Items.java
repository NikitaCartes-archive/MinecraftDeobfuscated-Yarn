package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class Items {
	public static final Item AIR = register(Blocks.AIR, new AirBlockItem(Blocks.AIR, new Item.Settings()));
	public static final Item STONE = register(Blocks.STONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRANITE = register(Blocks.GRANITE, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_GRANITE = register(Blocks.POLISHED_GRANITE, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIORITE = register(Blocks.DIORITE, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_DIORITE = register(Blocks.POLISHED_DIORITE, ItemGroup.BUILDING_BLOCKS);
	public static final Item ANDESITE = register(Blocks.ANDESITE, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_ANDESITE = register(Blocks.POLISHED_ANDESITE, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRASS_BLOCK = register(Blocks.GRASS_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIRT = register(Blocks.DIRT, ItemGroup.BUILDING_BLOCKS);
	public static final Item COARSE_DIRT = register(Blocks.COARSE_DIRT, ItemGroup.BUILDING_BLOCKS);
	public static final Item PODZOL = register(Blocks.PODZOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRIMSON_NYLIUM = register(Blocks.CRIMSON_NYLIUM, ItemGroup.BUILDING_BLOCKS);
	public static final Item WARPED_NYLIUM = register(Blocks.WARPED_NYLIUM, ItemGroup.BUILDING_BLOCKS);
	public static final Item COBBLESTONE = register(Blocks.COBBLESTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_PLANKS = register(Blocks.OAK_PLANKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_PLANKS = register(Blocks.SPRUCE_PLANKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_PLANKS = register(Blocks.BIRCH_PLANKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_PLANKS = register(Blocks.JUNGLE_PLANKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_PLANKS = register(Blocks.ACACIA_PLANKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_PLANKS = register(Blocks.DARK_OAK_PLANKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRIMSON_PLANKS = register(Blocks.CRIMSON_PLANKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item WARPED_PLANKS = register(Blocks.WARPED_PLANKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_SAPLING = register(Blocks.OAK_SAPLING, ItemGroup.DECORATIONS);
	public static final Item SPRUCE_SAPLING = register(Blocks.SPRUCE_SAPLING, ItemGroup.DECORATIONS);
	public static final Item BIRCH_SAPLING = register(Blocks.BIRCH_SAPLING, ItemGroup.DECORATIONS);
	public static final Item JUNGLE_SAPLING = register(Blocks.JUNGLE_SAPLING, ItemGroup.DECORATIONS);
	public static final Item ACACIA_SAPLING = register(Blocks.ACACIA_SAPLING, ItemGroup.DECORATIONS);
	public static final Item DARK_OAK_SAPLING = register(Blocks.DARK_OAK_SAPLING, ItemGroup.DECORATIONS);
	public static final Item BEDROCK = register(Blocks.BEDROCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item SAND = register(Blocks.SAND, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_SAND = register(Blocks.RED_SAND, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAVEL = register(Blocks.GRAVEL, ItemGroup.BUILDING_BLOCKS);
	public static final Item GOLD_ORE = register(Blocks.GOLD_ORE, ItemGroup.BUILDING_BLOCKS);
	public static final Item IRON_ORE = register(Blocks.IRON_ORE, ItemGroup.BUILDING_BLOCKS);
	public static final Item COAL_ORE = register(Blocks.COAL_ORE, ItemGroup.BUILDING_BLOCKS);
	public static final Item NETHER_GOLD_ORE = register(Blocks.NETHER_GOLD_ORE, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_LOG = register(Blocks.OAK_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_LOG = register(Blocks.SPRUCE_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_LOG = register(Blocks.BIRCH_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_LOG = register(Blocks.JUNGLE_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_LOG = register(Blocks.ACACIA_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_LOG = register(Blocks.DARK_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRIMSON_STEM = register(Blocks.CRIMSON_STEM, ItemGroup.BUILDING_BLOCKS);
	public static final Item WARPED_STEM = register(Blocks.WARPED_STEM, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_OAK_LOG = register(Blocks.STRIPPED_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_SPRUCE_LOG = register(Blocks.STRIPPED_SPRUCE_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_BIRCH_LOG = register(Blocks.STRIPPED_BIRCH_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_JUNGLE_LOG = register(Blocks.STRIPPED_JUNGLE_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_ACACIA_LOG = register(Blocks.STRIPPED_ACACIA_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_DARK_OAK_LOG = register(Blocks.STRIPPED_DARK_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_CRIMSON_STEM = register(Blocks.STRIPPED_CRIMSON_STEM, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_WARPED_STEM = register(Blocks.STRIPPED_WARPED_STEM, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_OAK_WOOD = register(Blocks.STRIPPED_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_SPRUCE_WOOD = register(Blocks.STRIPPED_SPRUCE_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_BIRCH_WOOD = register(Blocks.STRIPPED_BIRCH_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_JUNGLE_WOOD = register(Blocks.STRIPPED_JUNGLE_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_ACACIA_WOOD = register(Blocks.STRIPPED_ACACIA_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_DARK_OAK_WOOD = register(Blocks.STRIPPED_DARK_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_CRIMSON_HYPHAE = register(Blocks.STRIPPED_CRIMSON_HYPHAE, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_WARPED_HYPHAE = register(Blocks.STRIPPED_WARPED_HYPHAE, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_WOOD = register(Blocks.OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_WOOD = register(Blocks.SPRUCE_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_WOOD = register(Blocks.BIRCH_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_WOOD = register(Blocks.JUNGLE_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_WOOD = register(Blocks.ACACIA_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_WOOD = register(Blocks.DARK_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRIMSON_HYPHAE = register(Blocks.CRIMSON_HYPHAE, ItemGroup.BUILDING_BLOCKS);
	public static final Item WARPED_HYPHAE = register(Blocks.WARPED_HYPHAE, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_LEAVES = register(Blocks.OAK_LEAVES, ItemGroup.DECORATIONS);
	public static final Item SPRUCE_LEAVES = register(Blocks.SPRUCE_LEAVES, ItemGroup.DECORATIONS);
	public static final Item BIRCH_LEAVES = register(Blocks.BIRCH_LEAVES, ItemGroup.DECORATIONS);
	public static final Item JUNGLE_LEAVES = register(Blocks.JUNGLE_LEAVES, ItemGroup.DECORATIONS);
	public static final Item ACACIA_LEAVES = register(Blocks.ACACIA_LEAVES, ItemGroup.DECORATIONS);
	public static final Item DARK_OAK_LEAVES = register(Blocks.DARK_OAK_LEAVES, ItemGroup.DECORATIONS);
	public static final Item SPONGE = register(Blocks.SPONGE, ItemGroup.BUILDING_BLOCKS);
	public static final Item WET_SPONGE = register(Blocks.WET_SPONGE, ItemGroup.BUILDING_BLOCKS);
	public static final Item GLASS = register(Blocks.GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item LAPIS_ORE = register(Blocks.LAPIS_ORE, ItemGroup.BUILDING_BLOCKS);
	public static final Item LAPIS_BLOCK = register(Blocks.LAPIS_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item DISPENSER = register(Blocks.DISPENSER, ItemGroup.REDSTONE);
	public static final Item SANDSTONE = register(Blocks.SANDSTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item CHISELED_SANDSTONE = register(Blocks.CHISELED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item CUT_SANDSTONE = register(Blocks.CUT_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item NOTE_BLOCK = register(Blocks.NOTE_BLOCK, ItemGroup.REDSTONE);
	public static final Item POWERED_RAIL = register(Blocks.POWERED_RAIL, ItemGroup.TRANSPORTATION);
	public static final Item DETECTOR_RAIL = register(Blocks.DETECTOR_RAIL, ItemGroup.TRANSPORTATION);
	public static final Item STICKY_PISTON = register(Blocks.STICKY_PISTON, ItemGroup.REDSTONE);
	public static final Item COBWEB = register(Blocks.COBWEB, ItemGroup.DECORATIONS);
	public static final Item GRASS = register(Blocks.GRASS, ItemGroup.DECORATIONS);
	public static final Item FERN = register(Blocks.FERN, ItemGroup.DECORATIONS);
	public static final Item DEAD_BUSH = register(Blocks.DEAD_BUSH, ItemGroup.DECORATIONS);
	public static final Item SEAGRASS = register(Blocks.SEAGRASS, ItemGroup.DECORATIONS);
	public static final Item SEA_PICKLE = register(Blocks.SEA_PICKLE, ItemGroup.DECORATIONS);
	public static final Item PISTON = register(Blocks.PISTON, ItemGroup.REDSTONE);
	public static final Item WHITE_WOOL = register(Blocks.WHITE_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_WOOL = register(Blocks.ORANGE_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_WOOL = register(Blocks.MAGENTA_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_WOOL = register(Blocks.LIGHT_BLUE_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_WOOL = register(Blocks.YELLOW_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_WOOL = register(Blocks.LIME_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_WOOL = register(Blocks.PINK_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_WOOL = register(Blocks.GRAY_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_WOOL = register(Blocks.LIGHT_GRAY_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_WOOL = register(Blocks.CYAN_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_WOOL = register(Blocks.PURPLE_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_WOOL = register(Blocks.BLUE_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_WOOL = register(Blocks.BROWN_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_WOOL = register(Blocks.GREEN_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_WOOL = register(Blocks.RED_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_WOOL = register(Blocks.BLACK_WOOL, ItemGroup.BUILDING_BLOCKS);
	public static final Item DANDELION = register(Blocks.DANDELION, ItemGroup.DECORATIONS);
	public static final Item POPPY = register(Blocks.POPPY, ItemGroup.DECORATIONS);
	public static final Item BLUE_ORCHID = register(Blocks.BLUE_ORCHID, ItemGroup.DECORATIONS);
	public static final Item ALLIUM = register(Blocks.ALLIUM, ItemGroup.DECORATIONS);
	public static final Item AZURE_BLUET = register(Blocks.AZURE_BLUET, ItemGroup.DECORATIONS);
	public static final Item RED_TULIP = register(Blocks.RED_TULIP, ItemGroup.DECORATIONS);
	public static final Item ORANGE_TULIP = register(Blocks.ORANGE_TULIP, ItemGroup.DECORATIONS);
	public static final Item WHITE_TULIP = register(Blocks.WHITE_TULIP, ItemGroup.DECORATIONS);
	public static final Item PINK_TULIP = register(Blocks.PINK_TULIP, ItemGroup.DECORATIONS);
	public static final Item OXEYE_DAISY = register(Blocks.OXEYE_DAISY, ItemGroup.DECORATIONS);
	public static final Item CORNFLOWER = register(Blocks.CORNFLOWER, ItemGroup.DECORATIONS);
	public static final Item LILY_OF_THE_VALLEY = register(Blocks.LILY_OF_THE_VALLEY, ItemGroup.DECORATIONS);
	public static final Item WITHER_ROSE = register(Blocks.WITHER_ROSE, ItemGroup.DECORATIONS);
	public static final Item BROWN_MUSHROOM = register(Blocks.BROWN_MUSHROOM, ItemGroup.DECORATIONS);
	public static final Item RED_MUSHROOM = register(Blocks.RED_MUSHROOM, ItemGroup.DECORATIONS);
	public static final Item CRIMSON_FUNGUS = register(Blocks.CRIMSON_FUNGUS, ItemGroup.DECORATIONS);
	public static final Item WARPED_FUNGUS = register(Blocks.WARPED_FUNGUS, ItemGroup.DECORATIONS);
	public static final Item CRIMSON_ROOTS = register(Blocks.CRIMSON_ROOTS, ItemGroup.DECORATIONS);
	public static final Item WARPED_ROOTS = register(Blocks.WARPED_ROOTS, ItemGroup.DECORATIONS);
	public static final Item NETHER_SPROUTS = register(Blocks.NETHER_SPROUTS, ItemGroup.DECORATIONS);
	public static final Item WEEPING_VINES = register(Blocks.WEEPING_VINES, ItemGroup.DECORATIONS);
	public static final Item TWISTING_VINES = register(Blocks.TWISTING_VINES, ItemGroup.DECORATIONS);
	public static final Item SUGAR_CANE = register(Blocks.SUGAR_CANE, ItemGroup.DECORATIONS);
	public static final Item KELP = register(Blocks.KELP, ItemGroup.DECORATIONS);
	public static final Item BAMBOO = register(Blocks.BAMBOO, ItemGroup.DECORATIONS);
	public static final Item GOLD_BLOCK = register(Blocks.GOLD_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item IRON_BLOCK = register(Blocks.IRON_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_SLAB = register(Blocks.OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_SLAB = register(Blocks.SPRUCE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_SLAB = register(Blocks.BIRCH_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_SLAB = register(Blocks.JUNGLE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_SLAB = register(Blocks.ACACIA_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_SLAB = register(Blocks.DARK_OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRIMSON_SLAB = register(Blocks.CRIMSON_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item WARPED_SLAB = register(Blocks.WARPED_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item STONE_SLAB = register(Blocks.STONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_STONE_SLAB = register(Blocks.SMOOTH_STONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item SANDSTONE_SLAB = register(Blocks.SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item CUT_SANDSTONE_SLAB = register(Blocks.CUT_SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item PETRIFIED_OAK_SLAB = register(Blocks.PETRIFIED_OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item COBBLESTONE_SLAB = register(Blocks.COBBLESTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item BRICK_SLAB = register(Blocks.BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item STONE_BRICK_SLAB = register(Blocks.STONE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item NETHER_BRICK_SLAB = register(Blocks.NETHER_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item QUARTZ_SLAB = register(Blocks.QUARTZ_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_SANDSTONE_SLAB = register(Blocks.RED_SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item CUT_RED_SANDSTONE_SLAB = register(Blocks.CUT_RED_SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPUR_SLAB = register(Blocks.PURPUR_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_SLAB = register(Blocks.PRISMARINE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_BRICK_SLAB = register(Blocks.PRISMARINE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_PRISMARINE_SLAB = register(Blocks.DARK_PRISMARINE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_QUARTZ = register(Blocks.SMOOTH_QUARTZ, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_RED_SANDSTONE = register(Blocks.SMOOTH_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_SANDSTONE = register(Blocks.SMOOTH_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_STONE = register(Blocks.SMOOTH_STONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item BRICKS = register(Blocks.BRICKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item TNT = register(Blocks.TNT, ItemGroup.REDSTONE);
	public static final Item BOOKSHELF = register(Blocks.BOOKSHELF, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_COBBLESTONE = register(Blocks.MOSSY_COBBLESTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item OBSIDIAN = register(Blocks.OBSIDIAN, ItemGroup.BUILDING_BLOCKS);
	public static final Item TORCH = register(new WallStandingBlockItem(Blocks.TORCH, Blocks.WALL_TORCH, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item END_ROD = register(Blocks.END_ROD, ItemGroup.DECORATIONS);
	public static final Item CHORUS_PLANT = register(Blocks.CHORUS_PLANT, ItemGroup.DECORATIONS);
	public static final Item CHORUS_FLOWER = register(Blocks.CHORUS_FLOWER, ItemGroup.DECORATIONS);
	public static final Item PURPUR_BLOCK = register(Blocks.PURPUR_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPUR_PILLAR = register(Blocks.PURPUR_PILLAR, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPUR_STAIRS = register(Blocks.PURPUR_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPAWNER = register(Blocks.SPAWNER);
	public static final Item OAK_STAIRS = register(Blocks.OAK_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item CHEST = register(Blocks.CHEST, ItemGroup.DECORATIONS);
	public static final Item DIAMOND_ORE = register(Blocks.DIAMOND_ORE, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIAMOND_BLOCK = register(Blocks.DIAMOND_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRAFTING_TABLE = register(Blocks.CRAFTING_TABLE, ItemGroup.DECORATIONS);
	public static final Item FARMLAND = register(Blocks.FARMLAND, ItemGroup.DECORATIONS);
	public static final Item FURNACE = register(Blocks.FURNACE, ItemGroup.DECORATIONS);
	public static final Item LADDER = register(Blocks.LADDER, ItemGroup.DECORATIONS);
	public static final Item RAIL = register(Blocks.RAIL, ItemGroup.TRANSPORTATION);
	public static final Item COBBLESTONE_STAIRS = register(Blocks.COBBLESTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item LEVER = register(Blocks.LEVER, ItemGroup.REDSTONE);
	public static final Item STONE_PRESSURE_PLATE = register(Blocks.STONE_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item OAK_PRESSURE_PLATE = register(Blocks.OAK_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item SPRUCE_PRESSURE_PLATE = register(Blocks.SPRUCE_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item BIRCH_PRESSURE_PLATE = register(Blocks.BIRCH_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item JUNGLE_PRESSURE_PLATE = register(Blocks.JUNGLE_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item ACACIA_PRESSURE_PLATE = register(Blocks.ACACIA_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item DARK_OAK_PRESSURE_PLATE = register(Blocks.DARK_OAK_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item CRIMSON_PRESSURE_PLATE = register(Blocks.CRIMSON_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item WARPED_PRESSURE_PLATE = register(Blocks.WARPED_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item REDSTONE_ORE = register(Blocks.REDSTONE_ORE, ItemGroup.BUILDING_BLOCKS);
	public static final Item REDSTONE_TORCH = register(
		new WallStandingBlockItem(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, new Item.Settings().group(ItemGroup.REDSTONE))
	);
	public static final Item STONE_BUTTON = register(Blocks.STONE_BUTTON, ItemGroup.REDSTONE);
	public static final Item SNOW = register(Blocks.SNOW, ItemGroup.DECORATIONS);
	public static final Item ICE = register(Blocks.ICE, ItemGroup.BUILDING_BLOCKS);
	public static final Item SNOW_BLOCK = register(Blocks.SNOW_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item CACTUS = register(Blocks.CACTUS, ItemGroup.DECORATIONS);
	public static final Item CLAY = register(Blocks.CLAY, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUKEBOX = register(Blocks.JUKEBOX, ItemGroup.DECORATIONS);
	public static final Item OAK_FENCE = register(Blocks.OAK_FENCE, ItemGroup.DECORATIONS);
	public static final Item SPRUCE_FENCE = register(Blocks.SPRUCE_FENCE, ItemGroup.DECORATIONS);
	public static final Item BIRCH_FENCE = register(Blocks.BIRCH_FENCE, ItemGroup.DECORATIONS);
	public static final Item JUNGLE_FENCE = register(Blocks.JUNGLE_FENCE, ItemGroup.DECORATIONS);
	public static final Item ACACIA_FENCE = register(Blocks.ACACIA_FENCE, ItemGroup.DECORATIONS);
	public static final Item DARK_OAK_FENCE = register(Blocks.DARK_OAK_FENCE, ItemGroup.DECORATIONS);
	public static final Item CRIMSON_FENCE = register(Blocks.CRIMSON_FENCE, ItemGroup.DECORATIONS);
	public static final Item WARPED_FENCE = register(Blocks.WARPED_FENCE, ItemGroup.DECORATIONS);
	public static final Item PUMPKIN = register(Blocks.PUMPKIN, ItemGroup.BUILDING_BLOCKS);
	public static final Item CARVED_PUMPKIN = register(Blocks.CARVED_PUMPKIN, ItemGroup.BUILDING_BLOCKS);
	public static final Item NETHERRACK = register(Blocks.NETHERRACK, ItemGroup.BUILDING_BLOCKS);
	public static final Item SOUL_SAND = register(Blocks.SOUL_SAND, ItemGroup.BUILDING_BLOCKS);
	public static final Item SOUL_SOIL = register(Blocks.SOUL_SOIL, ItemGroup.BUILDING_BLOCKS);
	public static final Item BASALT = register(Blocks.BASALT, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_BASALT = register(Blocks.POLISHED_BASALT, ItemGroup.BUILDING_BLOCKS);
	public static final Item SOUL_FIRE_TORCH = register(
		new WallStandingBlockItem(Blocks.SOUL_FIRE_TORCH, Blocks.SOUL_FIRE_WALL_TORCH, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item GLOWSTONE = register(Blocks.GLOWSTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item JACK_O_LANTERN = register(Blocks.JACK_O_LANTERN, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_TRAPDOOR = register(Blocks.OAK_TRAPDOOR, ItemGroup.REDSTONE);
	public static final Item SPRUCE_TRAPDOOR = register(Blocks.SPRUCE_TRAPDOOR, ItemGroup.REDSTONE);
	public static final Item BIRCH_TRAPDOOR = register(Blocks.BIRCH_TRAPDOOR, ItemGroup.REDSTONE);
	public static final Item JUNGLE_TRAPDOOR = register(Blocks.JUNGLE_TRAPDOOR, ItemGroup.REDSTONE);
	public static final Item ACACIA_TRAPDOOR = register(Blocks.ACACIA_TRAPDOOR, ItemGroup.REDSTONE);
	public static final Item DARK_OAK_TRAPDOOR = register(Blocks.DARK_OAK_TRAPDOOR, ItemGroup.REDSTONE);
	public static final Item CRIMSON_TRAPDOOR = register(Blocks.CRIMSON_TRAPDOOR, ItemGroup.REDSTONE);
	public static final Item WARPED_TRAPDOOR = register(Blocks.WARPED_TRAPDOOR, ItemGroup.REDSTONE);
	public static final Item INFESTED_STONE = register(Blocks.INFESTED_STONE, ItemGroup.DECORATIONS);
	public static final Item INFESTED_COBBLESTONE = register(Blocks.INFESTED_COBBLESTONE, ItemGroup.DECORATIONS);
	public static final Item INFESTED_STONE_BRICKS = register(Blocks.INFESTED_STONE_BRICKS, ItemGroup.DECORATIONS);
	public static final Item INFESTED_MOSSY_STONE_BRICKS = register(Blocks.INFESTED_MOSSY_STONE_BRICKS, ItemGroup.DECORATIONS);
	public static final Item INFESTED_CRACKED_STONE_BRICKS = register(Blocks.INFESTED_CRACKED_STONE_BRICKS, ItemGroup.DECORATIONS);
	public static final Item INFESTED_CHISELED_STONE_BRICKS = register(Blocks.INFESTED_CHISELED_STONE_BRICKS, ItemGroup.DECORATIONS);
	public static final Item STONE_BRICKS = register(Blocks.STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_STONE_BRICKS = register(Blocks.MOSSY_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRACKED_STONE_BRICKS = register(Blocks.CRACKED_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item CHISELED_STONE_BRICKS = register(Blocks.CHISELED_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_MUSHROOM_BLOCK = register(Blocks.BROWN_MUSHROOM_BLOCK, ItemGroup.DECORATIONS);
	public static final Item RED_MUSHROOM_BLOCK = register(Blocks.RED_MUSHROOM_BLOCK, ItemGroup.DECORATIONS);
	public static final Item MUSHROOM_STEM = register(Blocks.MUSHROOM_STEM, ItemGroup.DECORATIONS);
	public static final Item IRON_BARS = register(Blocks.IRON_BARS, ItemGroup.DECORATIONS);
	public static final Item GLASS_PANE = register(Blocks.GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item MELON = register(Blocks.MELON, ItemGroup.BUILDING_BLOCKS);
	public static final Item VINE = register(Blocks.VINE, ItemGroup.DECORATIONS);
	public static final Item OAK_FENCE_GATE = register(Blocks.OAK_FENCE_GATE, ItemGroup.REDSTONE);
	public static final Item SPRUCE_FENCE_GATE = register(Blocks.SPRUCE_FENCE_GATE, ItemGroup.REDSTONE);
	public static final Item BIRCH_FENCE_GATE = register(Blocks.BIRCH_FENCE_GATE, ItemGroup.REDSTONE);
	public static final Item JUNGLE_FENCE_GATE = register(Blocks.JUNGLE_FENCE_GATE, ItemGroup.REDSTONE);
	public static final Item ACACIA_FENCE_GATE = register(Blocks.ACACIA_FENCE_GATE, ItemGroup.REDSTONE);
	public static final Item DARK_OAK_FENCE_GATE = register(Blocks.DARK_OAK_FENCE_GATE, ItemGroup.REDSTONE);
	public static final Item CRIMSON_FENCE_GATE = register(Blocks.CRIMSON_FENCE_GATE, ItemGroup.REDSTONE);
	public static final Item WARPED_FENCE_GATE = register(Blocks.WARPED_FENCE_GATE, ItemGroup.REDSTONE);
	public static final Item BRICK_STAIRS = register(Blocks.BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item STONE_BRICK_STAIRS = register(Blocks.STONE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item MYCELIUM = register(Blocks.MYCELIUM, ItemGroup.BUILDING_BLOCKS);
	public static final Item LILY_PAD = register(new LilyPadItem(Blocks.LILY_PAD, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item NETHER_BRICKS = register(Blocks.NETHER_BRICKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item NETHER_BRICK_FENCE = register(Blocks.NETHER_BRICK_FENCE, ItemGroup.DECORATIONS);
	public static final Item NETHER_BRICK_STAIRS = register(Blocks.NETHER_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item ENCHANTING_TABLE = register(Blocks.ENCHANTING_TABLE, ItemGroup.DECORATIONS);
	public static final Item END_PORTAL_FRAME = register(Blocks.END_PORTAL_FRAME, ItemGroup.DECORATIONS);
	public static final Item END_STONE = register(Blocks.END_STONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item END_STONE_BRICKS = register(Blocks.END_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item DRAGON_EGG = register(new BlockItem(Blocks.DRAGON_EGG, new Item.Settings().rarity(Rarity.EPIC)));
	public static final Item REDSTONE_LAMP = register(Blocks.REDSTONE_LAMP, ItemGroup.REDSTONE);
	public static final Item SANDSTONE_STAIRS = register(Blocks.SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item EMERALD_ORE = register(Blocks.EMERALD_ORE, ItemGroup.BUILDING_BLOCKS);
	public static final Item ENDER_CHEST = register(Blocks.ENDER_CHEST, ItemGroup.DECORATIONS);
	public static final Item TRIPWIRE_HOOK = register(Blocks.TRIPWIRE_HOOK, ItemGroup.REDSTONE);
	public static final Item EMERALD_BLOCK = register(Blocks.EMERALD_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_STAIRS = register(Blocks.SPRUCE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_STAIRS = register(Blocks.BIRCH_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_STAIRS = register(Blocks.JUNGLE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRIMSON_STAIRS = register(Blocks.CRIMSON_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item WARPED_STAIRS = register(Blocks.WARPED_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item COMMAND_BLOCK = register(new CommandBlockItem(Blocks.COMMAND_BLOCK, new Item.Settings().rarity(Rarity.EPIC)));
	public static final Item BEACON = register(new BlockItem(Blocks.BEACON, new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.RARE)));
	public static final Item COBBLESTONE_WALL = register(Blocks.COBBLESTONE_WALL, ItemGroup.DECORATIONS);
	public static final Item MOSSY_COBBLESTONE_WALL = register(Blocks.MOSSY_COBBLESTONE_WALL, ItemGroup.DECORATIONS);
	public static final Item BRICK_WALL = register(Blocks.BRICK_WALL, ItemGroup.DECORATIONS);
	public static final Item PRISMARINE_WALL = register(Blocks.PRISMARINE_WALL, ItemGroup.DECORATIONS);
	public static final Item RED_SANDSTONE_WALL = register(Blocks.RED_SANDSTONE_WALL, ItemGroup.DECORATIONS);
	public static final Item MOSSY_STONE_BRICK_WALL = register(Blocks.MOSSY_STONE_BRICK_WALL, ItemGroup.DECORATIONS);
	public static final Item GRANITE_WALL = register(Blocks.GRANITE_WALL, ItemGroup.DECORATIONS);
	public static final Item STONE_BRICK_WALL = register(Blocks.STONE_BRICK_WALL, ItemGroup.DECORATIONS);
	public static final Item NETHER_BRICK_WALL = register(Blocks.NETHER_BRICK_WALL, ItemGroup.DECORATIONS);
	public static final Item ANDESITE_WALL = register(Blocks.ANDESITE_WALL, ItemGroup.DECORATIONS);
	public static final Item RED_NETHER_BRICK_WALL = register(Blocks.RED_NETHER_BRICK_WALL, ItemGroup.DECORATIONS);
	public static final Item SANDSTONE_WALL = register(Blocks.SANDSTONE_WALL, ItemGroup.DECORATIONS);
	public static final Item END_STONE_BRICK_WALL = register(Blocks.END_STONE_BRICK_WALL, ItemGroup.DECORATIONS);
	public static final Item DIORITE_WALL = register(Blocks.DIORITE_WALL, ItemGroup.DECORATIONS);
	public static final Item OAK_BUTTON = register(Blocks.OAK_BUTTON, ItemGroup.REDSTONE);
	public static final Item SPRUCE_BUTTON = register(Blocks.SPRUCE_BUTTON, ItemGroup.REDSTONE);
	public static final Item BIRCH_BUTTON = register(Blocks.BIRCH_BUTTON, ItemGroup.REDSTONE);
	public static final Item JUNGLE_BUTTON = register(Blocks.JUNGLE_BUTTON, ItemGroup.REDSTONE);
	public static final Item ACACIA_BUTTON = register(Blocks.ACACIA_BUTTON, ItemGroup.REDSTONE);
	public static final Item DARK_OAK_BUTTON = register(Blocks.DARK_OAK_BUTTON, ItemGroup.REDSTONE);
	public static final Item CRIMSON_BUTTON = register(Blocks.CRIMSON_BUTTON, ItemGroup.REDSTONE);
	public static final Item WARPED_BUTTON = register(Blocks.WARPED_BUTTON, ItemGroup.REDSTONE);
	public static final Item ANVIL = register(Blocks.ANVIL, ItemGroup.DECORATIONS);
	public static final Item CHIPPED_ANVIL = register(Blocks.CHIPPED_ANVIL, ItemGroup.DECORATIONS);
	public static final Item DAMAGED_ANVIL = register(Blocks.DAMAGED_ANVIL, ItemGroup.DECORATIONS);
	public static final Item TRAPPED_CHEST = register(Blocks.TRAPPED_CHEST, ItemGroup.REDSTONE);
	public static final Item LIGHT_WEIGHTED_PRESSURE_PLATE = register(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item HEAVY_WEIGHTED_PRESSURE_PLATE = register(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, ItemGroup.REDSTONE);
	public static final Item DAYLIGHT_DETECTOR = register(Blocks.DAYLIGHT_DETECTOR, ItemGroup.REDSTONE);
	public static final Item REDSTONE_BLOCK = register(Blocks.REDSTONE_BLOCK, ItemGroup.REDSTONE);
	public static final Item NETHER_QUARTZ_ORE = register(Blocks.NETHER_QUARTZ_ORE, ItemGroup.BUILDING_BLOCKS);
	public static final Item HOPPER = register(Blocks.HOPPER, ItemGroup.REDSTONE);
	public static final Item CHISELED_QUARTZ_BLOCK = register(Blocks.CHISELED_QUARTZ_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item QUARTZ_BLOCK = register(Blocks.QUARTZ_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item QUARTZ_PILLAR = register(Blocks.QUARTZ_PILLAR, ItemGroup.BUILDING_BLOCKS);
	public static final Item QUARTZ_STAIRS = register(Blocks.QUARTZ_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACTIVATOR_RAIL = register(Blocks.ACTIVATOR_RAIL, ItemGroup.TRANSPORTATION);
	public static final Item DROPPER = register(Blocks.DROPPER, ItemGroup.REDSTONE);
	public static final Item WHITE_TERRACOTTA = register(Blocks.WHITE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_TERRACOTTA = register(Blocks.ORANGE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_TERRACOTTA = register(Blocks.MAGENTA_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_TERRACOTTA = register(Blocks.LIGHT_BLUE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_TERRACOTTA = register(Blocks.YELLOW_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_TERRACOTTA = register(Blocks.LIME_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_TERRACOTTA = register(Blocks.PINK_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_TERRACOTTA = register(Blocks.GRAY_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_TERRACOTTA = register(Blocks.LIGHT_GRAY_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_TERRACOTTA = register(Blocks.CYAN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_TERRACOTTA = register(Blocks.PURPLE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_TERRACOTTA = register(Blocks.BLUE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_TERRACOTTA = register(Blocks.BROWN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_TERRACOTTA = register(Blocks.GREEN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_TERRACOTTA = register(Blocks.RED_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_TERRACOTTA = register(Blocks.BLACK_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item BARRIER = register(Blocks.BARRIER);
	public static final Item IRON_TRAPDOOR = register(Blocks.IRON_TRAPDOOR, ItemGroup.REDSTONE);
	public static final Item HAY_BLOCK = register(Blocks.HAY_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item WHITE_CARPET = register(Blocks.WHITE_CARPET, ItemGroup.DECORATIONS);
	public static final Item ORANGE_CARPET = register(Blocks.ORANGE_CARPET, ItemGroup.DECORATIONS);
	public static final Item MAGENTA_CARPET = register(Blocks.MAGENTA_CARPET, ItemGroup.DECORATIONS);
	public static final Item LIGHT_BLUE_CARPET = register(Blocks.LIGHT_BLUE_CARPET, ItemGroup.DECORATIONS);
	public static final Item YELLOW_CARPET = register(Blocks.YELLOW_CARPET, ItemGroup.DECORATIONS);
	public static final Item LIME_CARPET = register(Blocks.LIME_CARPET, ItemGroup.DECORATIONS);
	public static final Item PINK_CARPET = register(Blocks.PINK_CARPET, ItemGroup.DECORATIONS);
	public static final Item GRAY_CARPET = register(Blocks.GRAY_CARPET, ItemGroup.DECORATIONS);
	public static final Item LIGHT_GRAY_CARPET = register(Blocks.LIGHT_GRAY_CARPET, ItemGroup.DECORATIONS);
	public static final Item CYAN_CARPET = register(Blocks.CYAN_CARPET, ItemGroup.DECORATIONS);
	public static final Item PURPLE_CARPET = register(Blocks.PURPLE_CARPET, ItemGroup.DECORATIONS);
	public static final Item BLUE_CARPET = register(Blocks.BLUE_CARPET, ItemGroup.DECORATIONS);
	public static final Item BROWN_CARPET = register(Blocks.BROWN_CARPET, ItemGroup.DECORATIONS);
	public static final Item GREEN_CARPET = register(Blocks.GREEN_CARPET, ItemGroup.DECORATIONS);
	public static final Item RED_CARPET = register(Blocks.RED_CARPET, ItemGroup.DECORATIONS);
	public static final Item BLACK_CARPET = register(Blocks.BLACK_CARPET, ItemGroup.DECORATIONS);
	public static final Item TERRACOTTA = register(Blocks.TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
	public static final Item COAL_BLOCK = register(Blocks.COAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item PACKED_ICE = register(Blocks.PACKED_ICE, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_STAIRS = register(Blocks.ACACIA_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_STAIRS = register(Blocks.DARK_OAK_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item SLIME_BLOCK = register(Blocks.SLIME_BLOCK, ItemGroup.DECORATIONS);
	public static final Item GRASS_PATH = register(Blocks.GRASS_PATH, ItemGroup.DECORATIONS);
	public static final Item SUNFLOWER = register(new TallBlockItem(Blocks.SUNFLOWER, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item LILAC = register(new TallBlockItem(Blocks.LILAC, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item ROSE_BUSH = register(new TallBlockItem(Blocks.ROSE_BUSH, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item PEONY = register(new TallBlockItem(Blocks.PEONY, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item TALL_GRASS = register(new TallBlockItem(Blocks.TALL_GRASS, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item LARGE_FERN = register(new TallBlockItem(Blocks.LARGE_FERN, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item WHITE_STAINED_GLASS = register(Blocks.WHITE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_STAINED_GLASS = register(Blocks.ORANGE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_STAINED_GLASS = register(Blocks.MAGENTA_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_STAINED_GLASS = register(Blocks.LIGHT_BLUE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_STAINED_GLASS = register(Blocks.YELLOW_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_STAINED_GLASS = register(Blocks.LIME_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_STAINED_GLASS = register(Blocks.PINK_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_STAINED_GLASS = register(Blocks.GRAY_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_STAINED_GLASS = register(Blocks.LIGHT_GRAY_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_STAINED_GLASS = register(Blocks.CYAN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_STAINED_GLASS = register(Blocks.PURPLE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_STAINED_GLASS = register(Blocks.BLUE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_STAINED_GLASS = register(Blocks.BROWN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_STAINED_GLASS = register(Blocks.GREEN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_STAINED_GLASS = register(Blocks.RED_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_STAINED_GLASS = register(Blocks.BLACK_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
	public static final Item WHITE_STAINED_GLASS_PANE = register(Blocks.WHITE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item ORANGE_STAINED_GLASS_PANE = register(Blocks.ORANGE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item MAGENTA_STAINED_GLASS_PANE = register(Blocks.MAGENTA_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item LIGHT_BLUE_STAINED_GLASS_PANE = register(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item YELLOW_STAINED_GLASS_PANE = register(Blocks.YELLOW_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item LIME_STAINED_GLASS_PANE = register(Blocks.LIME_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item PINK_STAINED_GLASS_PANE = register(Blocks.PINK_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item GRAY_STAINED_GLASS_PANE = register(Blocks.GRAY_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item LIGHT_GRAY_STAINED_GLASS_PANE = register(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item CYAN_STAINED_GLASS_PANE = register(Blocks.CYAN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item PURPLE_STAINED_GLASS_PANE = register(Blocks.PURPLE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item BLUE_STAINED_GLASS_PANE = register(Blocks.BLUE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item BROWN_STAINED_GLASS_PANE = register(Blocks.BROWN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item GREEN_STAINED_GLASS_PANE = register(Blocks.GREEN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item RED_STAINED_GLASS_PANE = register(Blocks.RED_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item BLACK_STAINED_GLASS_PANE = register(Blocks.BLACK_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
	public static final Item PRISMARINE = register(Blocks.PRISMARINE, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_BRICKS = register(Blocks.PRISMARINE_BRICKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_PRISMARINE = register(Blocks.DARK_PRISMARINE, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_STAIRS = register(Blocks.PRISMARINE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_BRICK_STAIRS = register(Blocks.PRISMARINE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_PRISMARINE_STAIRS = register(Blocks.DARK_PRISMARINE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item SEA_LANTERN = register(Blocks.SEA_LANTERN, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_SANDSTONE = register(Blocks.RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item CHISELED_RED_SANDSTONE = register(Blocks.CHISELED_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item CUT_RED_SANDSTONE = register(Blocks.CUT_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_SANDSTONE_STAIRS = register(Blocks.RED_SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item REPEATING_COMMAND_BLOCK = register(new CommandBlockItem(Blocks.REPEATING_COMMAND_BLOCK, new Item.Settings().rarity(Rarity.EPIC)));
	public static final Item CHAIN_COMMAND_BLOCK = register(new CommandBlockItem(Blocks.CHAIN_COMMAND_BLOCK, new Item.Settings().rarity(Rarity.EPIC)));
	public static final Item MAGMA_BLOCK = register(Blocks.MAGMA_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item NETHER_WART_BLOCK = register(Blocks.NETHER_WART_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item WARPED_WART_BLOCK = register(Blocks.WARPED_WART_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_NETHER_BRICKS = register(Blocks.RED_NETHER_BRICKS, ItemGroup.BUILDING_BLOCKS);
	public static final Item BONE_BLOCK = register(Blocks.BONE_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRUCTURE_VOID = register(Blocks.STRUCTURE_VOID);
	public static final Item OBSERVER = register(Blocks.OBSERVER, ItemGroup.REDSTONE);
	public static final Item SHULKER_BOX = register(new BlockItem(Blocks.SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item WHITE_SHULKER_BOX = register(new BlockItem(Blocks.WHITE_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item ORANGE_SHULKER_BOX = register(new BlockItem(Blocks.ORANGE_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item MAGENTA_SHULKER_BOX = register(
		new BlockItem(Blocks.MAGENTA_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS))
	);
	public static final Item LIGHT_BLUE_SHULKER_BOX = register(
		new BlockItem(Blocks.LIGHT_BLUE_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS))
	);
	public static final Item YELLOW_SHULKER_BOX = register(new BlockItem(Blocks.YELLOW_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIME_SHULKER_BOX = register(new BlockItem(Blocks.LIME_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item PINK_SHULKER_BOX = register(new BlockItem(Blocks.PINK_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item GRAY_SHULKER_BOX = register(new BlockItem(Blocks.GRAY_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIGHT_GRAY_SHULKER_BOX = register(
		new BlockItem(Blocks.LIGHT_GRAY_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS))
	);
	public static final Item CYAN_SHULKER_BOX = register(new BlockItem(Blocks.CYAN_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item PURPLE_SHULKER_BOX = register(new BlockItem(Blocks.PURPLE_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BLUE_SHULKER_BOX = register(new BlockItem(Blocks.BLUE_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BROWN_SHULKER_BOX = register(new BlockItem(Blocks.BROWN_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item GREEN_SHULKER_BOX = register(new BlockItem(Blocks.GREEN_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item RED_SHULKER_BOX = register(new BlockItem(Blocks.RED_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BLACK_SHULKER_BOX = register(new BlockItem(Blocks.BLACK_SHULKER_BOX, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item WHITE_GLAZED_TERRACOTTA = register(Blocks.WHITE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item ORANGE_GLAZED_TERRACOTTA = register(Blocks.ORANGE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item MAGENTA_GLAZED_TERRACOTTA = register(Blocks.MAGENTA_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item LIGHT_BLUE_GLAZED_TERRACOTTA = register(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item YELLOW_GLAZED_TERRACOTTA = register(Blocks.YELLOW_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item LIME_GLAZED_TERRACOTTA = register(Blocks.LIME_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item PINK_GLAZED_TERRACOTTA = register(Blocks.PINK_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item GRAY_GLAZED_TERRACOTTA = register(Blocks.GRAY_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item LIGHT_GRAY_GLAZED_TERRACOTTA = register(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item CYAN_GLAZED_TERRACOTTA = register(Blocks.CYAN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item PURPLE_GLAZED_TERRACOTTA = register(Blocks.PURPLE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item BLUE_GLAZED_TERRACOTTA = register(Blocks.BLUE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item BROWN_GLAZED_TERRACOTTA = register(Blocks.BROWN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item GREEN_GLAZED_TERRACOTTA = register(Blocks.GREEN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item RED_GLAZED_TERRACOTTA = register(Blocks.RED_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item BLACK_GLAZED_TERRACOTTA = register(Blocks.BLACK_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
	public static final Item WHITE_CONCRETE = register(Blocks.WHITE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_CONCRETE = register(Blocks.ORANGE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_CONCRETE = register(Blocks.MAGENTA_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_CONCRETE = register(Blocks.LIGHT_BLUE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_CONCRETE = register(Blocks.YELLOW_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_CONCRETE = register(Blocks.LIME_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_CONCRETE = register(Blocks.PINK_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_CONCRETE = register(Blocks.GRAY_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_CONCRETE = register(Blocks.LIGHT_GRAY_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_CONCRETE = register(Blocks.CYAN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_CONCRETE = register(Blocks.PURPLE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_CONCRETE = register(Blocks.BLUE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_CONCRETE = register(Blocks.BROWN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_CONCRETE = register(Blocks.GREEN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_CONCRETE = register(Blocks.RED_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_CONCRETE = register(Blocks.BLACK_CONCRETE, ItemGroup.BUILDING_BLOCKS);
	public static final Item WHITE_CONCRETE_POWDER = register(Blocks.WHITE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_CONCRETE_POWDER = register(Blocks.ORANGE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_CONCRETE_POWDER = register(Blocks.MAGENTA_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_CONCRETE_POWDER = register(Blocks.LIGHT_BLUE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_CONCRETE_POWDER = register(Blocks.YELLOW_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_CONCRETE_POWDER = register(Blocks.LIME_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_CONCRETE_POWDER = register(Blocks.PINK_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_CONCRETE_POWDER = register(Blocks.GRAY_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_CONCRETE_POWDER = register(Blocks.LIGHT_GRAY_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_CONCRETE_POWDER = register(Blocks.CYAN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_CONCRETE_POWDER = register(Blocks.PURPLE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_CONCRETE_POWDER = register(Blocks.BLUE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_CONCRETE_POWDER = register(Blocks.BROWN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_CONCRETE_POWDER = register(Blocks.GREEN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_CONCRETE_POWDER = register(Blocks.RED_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_CONCRETE_POWDER = register(Blocks.BLACK_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
	public static final Item TURTLE_EGG = register(Blocks.TURTLE_EGG, ItemGroup.MISC);
	public static final Item DEAD_TUBE_CORAL_BLOCK = register(Blocks.DEAD_TUBE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item DEAD_BRAIN_CORAL_BLOCK = register(Blocks.DEAD_BRAIN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item DEAD_BUBBLE_CORAL_BLOCK = register(Blocks.DEAD_BUBBLE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item DEAD_FIRE_CORAL_BLOCK = register(Blocks.DEAD_FIRE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item DEAD_HORN_CORAL_BLOCK = register(Blocks.DEAD_HORN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item TUBE_CORAL_BLOCK = register(Blocks.TUBE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item BRAIN_CORAL_BLOCK = register(Blocks.BRAIN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item BUBBLE_CORAL_BLOCK = register(Blocks.BUBBLE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item FIRE_CORAL_BLOCK = register(Blocks.FIRE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item HORN_CORAL_BLOCK = register(Blocks.HORN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item TUBE_CORAL = register(Blocks.TUBE_CORAL, ItemGroup.DECORATIONS);
	public static final Item BRAIN_CORAL = register(Blocks.BRAIN_CORAL, ItemGroup.DECORATIONS);
	public static final Item BUBBLE_CORAL = register(Blocks.BUBBLE_CORAL, ItemGroup.DECORATIONS);
	public static final Item FIRE_CORAL = register(Blocks.FIRE_CORAL, ItemGroup.DECORATIONS);
	public static final Item HORN_CORAL = register(Blocks.HORN_CORAL, ItemGroup.DECORATIONS);
	public static final Item DEAD_BRAIN_CORAL = register(Blocks.DEAD_BRAIN_CORAL, ItemGroup.DECORATIONS);
	public static final Item DEAD_BUBBLE_CORAL = register(Blocks.DEAD_BUBBLE_CORAL, ItemGroup.DECORATIONS);
	public static final Item DEAD_FIRE_CORAL = register(Blocks.DEAD_FIRE_CORAL, ItemGroup.DECORATIONS);
	public static final Item DEAD_HORN_CORAL = register(Blocks.DEAD_HORN_CORAL, ItemGroup.DECORATIONS);
	public static final Item DEAD_TUBE_CORAL = register(Blocks.DEAD_TUBE_CORAL, ItemGroup.DECORATIONS);
	public static final Item TUBE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item BRAIN_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item BUBBLE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item FIRE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item HORN_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_TUBE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.DEAD_TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_BRAIN_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_BUBBLE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_FIRE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.DEAD_FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_HORN_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.DEAD_HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item BLUE_ICE = register(Blocks.BLUE_ICE, ItemGroup.BUILDING_BLOCKS);
	public static final Item CONDUIT = register(new BlockItem(Blocks.CONDUIT, new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.RARE)));
	public static final Item POLISHED_GRANITE_STAIRS = register(Blocks.POLISHED_GRANITE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_RED_SANDSTONE_STAIRS = register(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_STONE_BRICK_STAIRS = register(Blocks.MOSSY_STONE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_DIORITE_STAIRS = register(Blocks.POLISHED_DIORITE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_COBBLESTONE_STAIRS = register(Blocks.MOSSY_COBBLESTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item END_STONE_BRICK_STAIRS = register(Blocks.END_STONE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item STONE_STAIRS = register(Blocks.STONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_SANDSTONE_STAIRS = register(Blocks.SMOOTH_SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_QUARTZ_STAIRS = register(Blocks.SMOOTH_QUARTZ_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRANITE_STAIRS = register(Blocks.GRANITE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item ANDESITE_STAIRS = register(Blocks.ANDESITE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_NETHER_BRICK_STAIRS = register(Blocks.RED_NETHER_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_ANDESITE_STAIRS = register(Blocks.POLISHED_ANDESITE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIORITE_STAIRS = register(Blocks.DIORITE_STAIRS, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_GRANITE_SLAB = register(Blocks.POLISHED_GRANITE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_RED_SANDSTONE_SLAB = register(Blocks.SMOOTH_RED_SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_STONE_BRICK_SLAB = register(Blocks.MOSSY_STONE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_DIORITE_SLAB = register(Blocks.POLISHED_DIORITE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_COBBLESTONE_SLAB = register(Blocks.MOSSY_COBBLESTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item END_STONE_BRICK_SLAB = register(Blocks.END_STONE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_SANDSTONE_SLAB = register(Blocks.SMOOTH_SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_QUARTZ_SLAB = register(Blocks.SMOOTH_QUARTZ_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRANITE_SLAB = register(Blocks.GRANITE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item ANDESITE_SLAB = register(Blocks.ANDESITE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_NETHER_BRICK_SLAB = register(Blocks.RED_NETHER_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_ANDESITE_SLAB = register(Blocks.POLISHED_ANDESITE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIORITE_SLAB = register(Blocks.DIORITE_SLAB, ItemGroup.BUILDING_BLOCKS);
	public static final Item SCAFFOLDING = register(new ScaffoldingItem(Blocks.SCAFFOLDING, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item IRON_DOOR = register(new TallBlockItem(Blocks.IRON_DOOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item OAK_DOOR = register(new TallBlockItem(Blocks.OAK_DOOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item SPRUCE_DOOR = register(new TallBlockItem(Blocks.SPRUCE_DOOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item BIRCH_DOOR = register(new TallBlockItem(Blocks.BIRCH_DOOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item JUNGLE_DOOR = register(new TallBlockItem(Blocks.JUNGLE_DOOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item ACACIA_DOOR = register(new TallBlockItem(Blocks.ACACIA_DOOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item DARK_OAK_DOOR = register(new TallBlockItem(Blocks.DARK_OAK_DOOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item CRIMSON_DOOR = register(new TallBlockItem(Blocks.CRIMSON_DOOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item WARPED_DOOR = register(new TallBlockItem(Blocks.WARPED_DOOR, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item REPEATER = register(Blocks.REPEATER, ItemGroup.REDSTONE);
	public static final Item COMPARATOR = register(Blocks.COMPARATOR, ItemGroup.REDSTONE);
	public static final Item STRUCTURE_BLOCK = register(new CommandBlockItem(Blocks.STRUCTURE_BLOCK, new Item.Settings().rarity(Rarity.EPIC)));
	public static final Item JIGSAW = register(new CommandBlockItem(Blocks.JIGSAW, new Item.Settings().rarity(Rarity.EPIC)));
	public static final Item TURTLE_HELMET = register(
		"turtle_helmet", new ArmorItem(ArmorMaterials.TURTLE, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item SCUTE = register("scute", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item IRON_SHOVEL = register("iron_shovel", new ShovelItem(ToolMaterials.IRON, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item IRON_PICKAXE = register("iron_pickaxe", new PickaxeItem(ToolMaterials.IRON, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item IRON_AXE = register("iron_axe", new AxeItem(ToolMaterials.IRON, 6.0F, -3.1F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item FLINT_AND_STEEL = register("flint_and_steel", new FlintAndSteelItem(new Item.Settings().maxDamage(64).group(ItemGroup.TOOLS)));
	public static final Item APPLE = register("apple", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.APPLE)));
	public static final Item BOW = register("bow", new BowItem(new Item.Settings().maxDamage(384).group(ItemGroup.COMBAT)));
	public static final Item ARROW = register("arrow", new ArrowItem(new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item COAL = register("coal", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item CHARCOAL = register("charcoal", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item DIAMOND = register("diamond", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item IRON_INGOT = register("iron_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item GOLD_INGOT = register("gold_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item IRON_SWORD = register("iron_sword", new SwordItem(ToolMaterials.IRON, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item WOODEN_SWORD = register("wooden_sword", new SwordItem(ToolMaterials.WOOD, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item WOODEN_SHOVEL = register("wooden_shovel", new ShovelItem(ToolMaterials.WOOD, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item WOODEN_PICKAXE = register("wooden_pickaxe", new PickaxeItem(ToolMaterials.WOOD, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item WOODEN_AXE = register("wooden_axe", new AxeItem(ToolMaterials.WOOD, 6.0F, -3.2F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item STONE_SWORD = register("stone_sword", new SwordItem(ToolMaterials.STONE, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item STONE_SHOVEL = register("stone_shovel", new ShovelItem(ToolMaterials.STONE, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item STONE_PICKAXE = register("stone_pickaxe", new PickaxeItem(ToolMaterials.STONE, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item STONE_AXE = register("stone_axe", new AxeItem(ToolMaterials.STONE, 7.0F, -3.2F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item DIAMOND_SWORD = register("diamond_sword", new SwordItem(ToolMaterials.DIAMOND, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item DIAMOND_SHOVEL = register(
		"diamond_shovel", new ShovelItem(ToolMaterials.DIAMOND, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item DIAMOND_PICKAXE = register(
		"diamond_pickaxe", new PickaxeItem(ToolMaterials.DIAMOND, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item DIAMOND_AXE = register("diamond_axe", new AxeItem(ToolMaterials.DIAMOND, 5.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item STICK = register("stick", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BOWL = register("bowl", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item MUSHROOM_STEW = register(
		"mushroom_stew", new MushroomStewItem(new Item.Settings().maxCount(1).group(ItemGroup.FOOD).food(FoodComponents.MUSHROOM_STEW))
	);
	public static final Item GOLDEN_SWORD = register("golden_sword", new SwordItem(ToolMaterials.GOLD, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item GOLDEN_SHOVEL = register("golden_shovel", new ShovelItem(ToolMaterials.GOLD, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item GOLDEN_PICKAXE = register("golden_pickaxe", new PickaxeItem(ToolMaterials.GOLD, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item GOLDEN_AXE = register("golden_axe", new AxeItem(ToolMaterials.GOLD, 6.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item NETHERITE_SWORD = register(
		"netherite_sword", new SwordItem(ToolMaterials.NETHERITE, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT).fireproof())
	);
	public static final Item NETHERITE_SHOVEL = register(
		"netherite_shovel", new ShovelItem(ToolMaterials.NETHERITE, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS).fireproof())
	);
	public static final Item NETHERITE_PICKAXE = register(
		"netherite_pickaxe", new PickaxeItem(ToolMaterials.NETHERITE, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS).fireproof())
	);
	public static final Item NETHERITE_AXE = register(
		"netherite_axe", new AxeItem(ToolMaterials.NETHERITE, 5.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS).fireproof())
	);
	public static final Item STRING = register("string", new AliasedBlockItem(Blocks.TRIPWIRE, new Item.Settings().group(ItemGroup.MISC)));
	public static final Item FEATHER = register("feather", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item GUNPOWDER = register("gunpowder", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item WOODEN_HOE = register("wooden_hoe", new HoeItem(ToolMaterials.WOOD, 0, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item STONE_HOE = register("stone_hoe", new HoeItem(ToolMaterials.STONE, -1, -2.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item IRON_HOE = register("iron_hoe", new HoeItem(ToolMaterials.IRON, -2, -1.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item DIAMOND_HOE = register("diamond_hoe", new HoeItem(ToolMaterials.DIAMOND, -3, 0.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item GOLDEN_HOE = register("golden_hoe", new HoeItem(ToolMaterials.GOLD, 0, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item NETHERITE_HOE = register(
		"netherite_hoe", new HoeItem(ToolMaterials.NETHERITE, -4, 0.0F, new Item.Settings().group(ItemGroup.TOOLS).fireproof())
	);
	public static final Item WHEAT_SEEDS = register("wheat_seeds", new AliasedBlockItem(Blocks.WHEAT, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item WHEAT = register("wheat", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BREAD = register("bread", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.BREAD)));
	public static final Item LEATHER_HELMET = register(
		"leather_helmet", new DyeableArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item LEATHER_CHESTPLATE = register(
		"leather_chestplate", new DyeableArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item LEATHER_LEGGINGS = register(
		"leather_leggings", new DyeableArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item LEATHER_BOOTS = register(
		"leather_boots", new DyeableArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item CHAINMAIL_HELMET = register(
		"chainmail_helmet", new ArmorItem(ArmorMaterials.CHAIN, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item CHAINMAIL_CHESTPLATE = register(
		"chainmail_chestplate", new ArmorItem(ArmorMaterials.CHAIN, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item CHAINMAIL_LEGGINGS = register(
		"chainmail_leggings", new ArmorItem(ArmorMaterials.CHAIN, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item CHAINMAIL_BOOTS = register(
		"chainmail_boots", new ArmorItem(ArmorMaterials.CHAIN, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item IRON_HELMET = register(
		"iron_helmet", new ArmorItem(ArmorMaterials.IRON, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item IRON_CHESTPLATE = register(
		"iron_chestplate", new ArmorItem(ArmorMaterials.IRON, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item IRON_LEGGINGS = register(
		"iron_leggings", new ArmorItem(ArmorMaterials.IRON, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item IRON_BOOTS = register(
		"iron_boots", new ArmorItem(ArmorMaterials.IRON, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item DIAMOND_HELMET = register(
		"diamond_helmet", new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item DIAMOND_CHESTPLATE = register(
		"diamond_chestplate", new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item DIAMOND_LEGGINGS = register(
		"diamond_leggings", new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item DIAMOND_BOOTS = register(
		"diamond_boots", new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item GOLDEN_HELMET = register(
		"golden_helmet", new ArmorItem(ArmorMaterials.GOLD, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item GOLDEN_CHESTPLATE = register(
		"golden_chestplate", new ArmorItem(ArmorMaterials.GOLD, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item GOLDEN_LEGGINGS = register(
		"golden_leggings", new ArmorItem(ArmorMaterials.GOLD, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item GOLDEN_BOOTS = register(
		"golden_boots", new ArmorItem(ArmorMaterials.GOLD, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item NETHERITE_HELMET = register(
		"netherite_helmet", new ArmorItem(ArmorMaterials.NETHERITE, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT).fireproof())
	);
	public static final Item NETHERITE_CHESTPLATE = register(
		"netherite_chestplate", new ArmorItem(ArmorMaterials.NETHERITE, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT).fireproof())
	);
	public static final Item NETHERITE_LEGGINGS = register(
		"netherite_leggings", new ArmorItem(ArmorMaterials.NETHERITE, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT).fireproof())
	);
	public static final Item NETHERITE_BOOTS = register(
		"netherite_boots", new ArmorItem(ArmorMaterials.NETHERITE, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT).fireproof())
	);
	public static final Item FLINT = register("flint", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item PORKCHOP = register("porkchop", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.PORKCHOP)));
	public static final Item COOKED_PORKCHOP = register(
		"cooked_porkchop", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_PORKCHOP))
	);
	public static final Item PAINTING = register("painting", new DecorationItem(EntityType.PAINTING, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item GOLDEN_APPLE = register(
		"golden_apple", new Item(new Item.Settings().group(ItemGroup.FOOD).rarity(Rarity.RARE).food(FoodComponents.GOLDEN_APPLE))
	);
	public static final Item ENCHANTED_GOLDEN_APPLE = register(
		"enchanted_golden_apple",
		new EnchantedGoldenAppleItem(new Item.Settings().group(ItemGroup.FOOD).rarity(Rarity.EPIC).food(FoodComponents.ENCHANTED_GOLDEN_APPLE))
	);
	public static final Item OAK_SIGN = register(
		"oak_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN)
	);
	public static final Item SPRUCE_SIGN = register(
		"spruce_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN)
	);
	public static final Item BIRCH_SIGN = register(
		"birch_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN)
	);
	public static final Item JUNGLE_SIGN = register(
		"jungle_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN)
	);
	public static final Item ACACIA_SIGN = register(
		"acacia_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN)
	);
	public static final Item DARK_OAK_SIGN = register(
		"dark_oak_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN)
	);
	public static final Item CRIMSON_SIGN = register(
		"crimson_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN)
	);
	public static final Item WARPED_SIGN = register(
		"warped_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN)
	);
	public static final Item BUCKET = register("bucket", new BucketItem(Fluids.EMPTY, new Item.Settings().maxCount(16).group(ItemGroup.MISC)));
	public static final Item WATER_BUCKET = register(
		"water_bucket", new BucketItem(Fluids.WATER, new Item.Settings().recipeRemainder(BUCKET).maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item LAVA_BUCKET = register(
		"lava_bucket", new BucketItem(Fluids.LAVA, new Item.Settings().recipeRemainder(BUCKET).maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item MINECART = register(
		"minecart", new MinecartItem(AbstractMinecartEntity.Type.RIDEABLE, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item SADDLE = register("saddle", new SaddleItem(new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)));
	public static final Item REDSTONE = register("redstone", new AliasedBlockItem(Blocks.REDSTONE_WIRE, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item SNOWBALL = register("snowball", new SnowballItem(new Item.Settings().maxCount(16).group(ItemGroup.MISC)));
	public static final Item OAK_BOAT = register("oak_boat", new BoatItem(BoatEntity.Type.OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)));
	public static final Item LEATHER = register("leather", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item MILK_BUCKET = register(
		"milk_bucket", new MilkBucketItem(new Item.Settings().recipeRemainder(BUCKET).maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item PUFFERFISH_BUCKET = register(
		"pufferfish_bucket", new FishBucketItem(EntityType.PUFFERFISH, Fluids.WATER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item SALMON_BUCKET = register(
		"salmon_bucket", new FishBucketItem(EntityType.SALMON, Fluids.WATER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item COD_BUCKET = register(
		"cod_bucket", new FishBucketItem(EntityType.COD, Fluids.WATER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item TROPICAL_FISH_BUCKET = register(
		"tropical_fish_bucket", new FishBucketItem(EntityType.TROPICAL_FISH, Fluids.WATER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item BRICK = register("brick", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item CLAY_BALL = register("clay_ball", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item DRIED_KELP_BLOCK = register(Blocks.DRIED_KELP_BLOCK, ItemGroup.BUILDING_BLOCKS);
	public static final Item PAPER = register("paper", new Item(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item BOOK = register("book", new BookItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item SLIME_BALL = register("slime_ball", new Item(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item CHEST_MINECART = register(
		"chest_minecart", new MinecartItem(AbstractMinecartEntity.Type.CHEST, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item FURNACE_MINECART = register(
		"furnace_minecart", new MinecartItem(AbstractMinecartEntity.Type.FURNACE, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item EGG = register("egg", new EggItem(new Item.Settings().maxCount(16).group(ItemGroup.MATERIALS)));
	public static final Item COMPASS = register("compass", new CompassItem(new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item FISHING_ROD = register("fishing_rod", new FishingRodItem(new Item.Settings().maxDamage(64).group(ItemGroup.TOOLS)));
	public static final Item CLOCK = register("clock", new ClockItem(new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item GLOWSTONE_DUST = register("glowstone_dust", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item COD = register("cod", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COD)));
	public static final Item SALMON = register("salmon", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.SALMON)));
	public static final Item TROPICAL_FISH = register("tropical_fish", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.TROPICAL_FISH)));
	public static final Item PUFFERFISH = register("pufferfish", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.PUFFERFISH)));
	public static final Item COOKED_COD = register("cooked_cod", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_COD)));
	public static final Item COOKED_SALMON = register("cooked_salmon", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_SALMON)));
	public static final Item INK_SAC = register("ink_sac", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item RED_DYE = register("red_dye", new DyeItem(DyeColor.RED, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item GREEN_DYE = register("green_dye", new DyeItem(DyeColor.GREEN, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item COCOA_BEANS = register("cocoa_beans", new AliasedBlockItem(Blocks.COCOA, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item LAPIS_LAZULI = register("lapis_lazuli", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item PURPLE_DYE = register("purple_dye", new DyeItem(DyeColor.PURPLE, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item CYAN_DYE = register("cyan_dye", new DyeItem(DyeColor.CYAN, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item LIGHT_GRAY_DYE = register("light_gray_dye", new DyeItem(DyeColor.LIGHT_GRAY, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item GRAY_DYE = register("gray_dye", new DyeItem(DyeColor.GRAY, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item PINK_DYE = register("pink_dye", new DyeItem(DyeColor.PINK, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item LIME_DYE = register("lime_dye", new DyeItem(DyeColor.LIME, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item YELLOW_DYE = register("yellow_dye", new DyeItem(DyeColor.YELLOW, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item LIGHT_BLUE_DYE = register("light_blue_dye", new DyeItem(DyeColor.LIGHT_BLUE, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item MAGENTA_DYE = register("magenta_dye", new DyeItem(DyeColor.MAGENTA, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item ORANGE_DYE = register("orange_dye", new DyeItem(DyeColor.ORANGE, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BONE_MEAL = register("bone_meal", new BoneMealItem(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BLUE_DYE = register("blue_dye", new DyeItem(DyeColor.BLUE, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BROWN_DYE = register("brown_dye", new DyeItem(DyeColor.BROWN, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BLACK_DYE = register("black_dye", new DyeItem(DyeColor.BLACK, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item WHITE_DYE = register("white_dye", new DyeItem(DyeColor.WHITE, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BONE = register("bone", new Item(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item SUGAR = register("sugar", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item CAKE = register(new BlockItem(Blocks.CAKE, new Item.Settings().maxCount(1).group(ItemGroup.FOOD)));
	public static final Item WHITE_BED = register(new BedItem(Blocks.WHITE_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item ORANGE_BED = register(new BedItem(Blocks.ORANGE_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item MAGENTA_BED = register(new BedItem(Blocks.MAGENTA_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIGHT_BLUE_BED = register(new BedItem(Blocks.LIGHT_BLUE_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item YELLOW_BED = register(new BedItem(Blocks.YELLOW_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIME_BED = register(new BedItem(Blocks.LIME_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item PINK_BED = register(new BedItem(Blocks.PINK_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item GRAY_BED = register(new BedItem(Blocks.GRAY_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIGHT_GRAY_BED = register(new BedItem(Blocks.LIGHT_GRAY_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item CYAN_BED = register(new BedItem(Blocks.CYAN_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item PURPLE_BED = register(new BedItem(Blocks.PURPLE_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BLUE_BED = register(new BedItem(Blocks.BLUE_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BROWN_BED = register(new BedItem(Blocks.BROWN_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item GREEN_BED = register(new BedItem(Blocks.GREEN_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item RED_BED = register(new BedItem(Blocks.RED_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BLACK_BED = register(new BedItem(Blocks.BLACK_BED, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item COOKIE = register("cookie", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKIE)));
	public static final Item FILLED_MAP = register("filled_map", new FilledMapItem(new Item.Settings()));
	public static final Item SHEARS = register("shears", new ShearsItem(new Item.Settings().maxDamage(238).group(ItemGroup.TOOLS)));
	public static final Item MELON_SLICE = register("melon_slice", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.MELON_SLICE)));
	public static final Item DRIED_KELP = register("dried_kelp", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.DRIED_KELP)));
	public static final Item PUMPKIN_SEEDS = register("pumpkin_seeds", new AliasedBlockItem(Blocks.PUMPKIN_STEM, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item MELON_SEEDS = register("melon_seeds", new AliasedBlockItem(Blocks.MELON_STEM, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BEEF = register("beef", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.BEEF)));
	public static final Item COOKED_BEEF = register("cooked_beef", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_BEEF)));
	public static final Item CHICKEN = register("chicken", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.CHICKEN)));
	public static final Item COOKED_CHICKEN = register("cooked_chicken", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_CHICKEN)));
	public static final Item ROTTEN_FLESH = register("rotten_flesh", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.ROTTEN_FLESH)));
	public static final Item ENDER_PEARL = register("ender_pearl", new EnderPearlItem(new Item.Settings().maxCount(16).group(ItemGroup.MISC)));
	public static final Item BLAZE_ROD = register("blaze_rod", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item GHAST_TEAR = register("ghast_tear", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item GOLD_NUGGET = register("gold_nugget", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item NETHER_WART = register("nether_wart", new AliasedBlockItem(Blocks.NETHER_WART, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item POTION = register("potion", new PotionItem(new Item.Settings().maxCount(1).group(ItemGroup.BREWING)));
	public static final Item GLASS_BOTTLE = register("glass_bottle", new GlassBottleItem(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item SPIDER_EYE = register("spider_eye", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.SPIDER_EYE)));
	public static final Item FERMENTED_SPIDER_EYE = register("fermented_spider_eye", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item BLAZE_POWDER = register("blaze_powder", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item MAGMA_CREAM = register("magma_cream", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item BREWING_STAND = register(Blocks.BREWING_STAND, ItemGroup.BREWING);
	public static final Item CAULDRON = register(Blocks.CAULDRON, ItemGroup.BREWING);
	public static final Item ENDER_EYE = register("ender_eye", new EnderEyeItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item GLISTERING_MELON_SLICE = register("glistering_melon_slice", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item BAT_SPAWN_EGG = register(
		"bat_spawn_egg", new SpawnEggItem(EntityType.BAT, 4996656, 986895, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item BEE_SPAWN_EGG = register(
		"bee_spawn_egg", new SpawnEggItem(EntityType.BEE, 15582019, 4400155, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item BLAZE_SPAWN_EGG = register(
		"blaze_spawn_egg", new SpawnEggItem(EntityType.BLAZE, 16167425, 16775294, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item CAT_SPAWN_EGG = register(
		"cat_spawn_egg", new SpawnEggItem(EntityType.CAT, 15714446, 9794134, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item CAVE_SPIDER_SPAWN_EGG = register(
		"cave_spider_spawn_egg", new SpawnEggItem(EntityType.CAVE_SPIDER, 803406, 11013646, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item CHICKEN_SPAWN_EGG = register(
		"chicken_spawn_egg", new SpawnEggItem(EntityType.CHICKEN, 10592673, 16711680, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item COD_SPAWN_EGG = register(
		"cod_spawn_egg", new SpawnEggItem(EntityType.COD, 12691306, 15058059, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item COW_SPAWN_EGG = register(
		"cow_spawn_egg", new SpawnEggItem(EntityType.COW, 4470310, 10592673, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item CREEPER_SPAWN_EGG = register(
		"creeper_spawn_egg", new SpawnEggItem(EntityType.CREEPER, 894731, 0, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item DOLPHIN_SPAWN_EGG = register(
		"dolphin_spawn_egg", new SpawnEggItem(EntityType.DOLPHIN, 2243405, 16382457, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item DONKEY_SPAWN_EGG = register(
		"donkey_spawn_egg", new SpawnEggItem(EntityType.DONKEY, 5457209, 8811878, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item DROWNED_SPAWN_EGG = register(
		"drowned_spawn_egg", new SpawnEggItem(EntityType.DROWNED, 9433559, 7969893, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item ELDER_GUARDIAN_SPAWN_EGG = register(
		"elder_guardian_spawn_egg", new SpawnEggItem(EntityType.ELDER_GUARDIAN, 13552826, 7632531, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item ENDERMAN_SPAWN_EGG = register(
		"enderman_spawn_egg", new SpawnEggItem(EntityType.ENDERMAN, 1447446, 0, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item ENDERMITE_SPAWN_EGG = register(
		"endermite_spawn_egg", new SpawnEggItem(EntityType.ENDERMITE, 1447446, 7237230, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item EVOKER_SPAWN_EGG = register(
		"evoker_spawn_egg", new SpawnEggItem(EntityType.EVOKER, 9804699, 1973274, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item FOX_SPAWN_EGG = register(
		"fox_spawn_egg", new SpawnEggItem(EntityType.FOX, 14005919, 13396256, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item GHAST_SPAWN_EGG = register(
		"ghast_spawn_egg", new SpawnEggItem(EntityType.GHAST, 16382457, 12369084, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item GUARDIAN_SPAWN_EGG = register(
		"guardian_spawn_egg", new SpawnEggItem(EntityType.GUARDIAN, 5931634, 15826224, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item HOGLIN_SPAWN_EGG = register(
		"hoglin_spawn_egg", new SpawnEggItem(EntityType.HOGLIN, 13004373, 6251620, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item HORSE_SPAWN_EGG = register(
		"horse_spawn_egg", new SpawnEggItem(EntityType.HORSE, 12623485, 15656192, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item HUSK_SPAWN_EGG = register(
		"husk_spawn_egg", new SpawnEggItem(EntityType.HUSK, 7958625, 15125652, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item LLAMA_SPAWN_EGG = register(
		"llama_spawn_egg", new SpawnEggItem(EntityType.LLAMA, 12623485, 10051392, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item MAGMA_CUBE_SPAWN_EGG = register(
		"magma_cube_spawn_egg", new SpawnEggItem(EntityType.MAGMA_CUBE, 3407872, 16579584, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item MOOSHROOM_SPAWN_EGG = register(
		"mooshroom_spawn_egg", new SpawnEggItem(EntityType.MOOSHROOM, 10489616, 12040119, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item MULE_SPAWN_EGG = register(
		"mule_spawn_egg", new SpawnEggItem(EntityType.MULE, 1769984, 5321501, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item OCELOT_SPAWN_EGG = register(
		"ocelot_spawn_egg", new SpawnEggItem(EntityType.OCELOT, 15720061, 5653556, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item PANDA_SPAWN_EGG = register(
		"panda_spawn_egg", new SpawnEggItem(EntityType.PANDA, 15198183, 1776418, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item PARROT_SPAWN_EGG = register(
		"parrot_spawn_egg", new SpawnEggItem(EntityType.PARROT, 894731, 16711680, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item PHANTOM_SPAWN_EGG = register(
		"phantom_spawn_egg", new SpawnEggItem(EntityType.PHANTOM, 4411786, 8978176, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item PIG_SPAWN_EGG = register(
		"pig_spawn_egg", new SpawnEggItem(EntityType.PIG, 15771042, 14377823, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item PIGLIN_SPAWN_EGG = register(
		"piglin_spawn_egg", new SpawnEggItem(EntityType.PIGLIN, 10051392, 16380836, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item PILLAGER_SPAWN_EGG = register(
		"pillager_spawn_egg", new SpawnEggItem(EntityType.PILLAGER, 5451574, 9804699, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item POLAR_BEAR_SPAWN_EGG = register(
		"polar_bear_spawn_egg", new SpawnEggItem(EntityType.POLAR_BEAR, 15921906, 9803152, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item PUFFERFISH_SPAWN_EGG = register(
		"pufferfish_spawn_egg", new SpawnEggItem(EntityType.PUFFERFISH, 16167425, 3654642, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item RABBIT_SPAWN_EGG = register(
		"rabbit_spawn_egg", new SpawnEggItem(EntityType.RABBIT, 10051392, 7555121, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item RAVAGER_SPAWN_EGG = register(
		"ravager_spawn_egg", new SpawnEggItem(EntityType.RAVAGER, 7697520, 5984329, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item SALMON_SPAWN_EGG = register(
		"salmon_spawn_egg", new SpawnEggItem(EntityType.SALMON, 10489616, 951412, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item SHEEP_SPAWN_EGG = register(
		"sheep_spawn_egg", new SpawnEggItem(EntityType.SHEEP, 15198183, 16758197, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item SHULKER_SPAWN_EGG = register(
		"shulker_spawn_egg", new SpawnEggItem(EntityType.SHULKER, 9725844, 5060690, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item SILVERFISH_SPAWN_EGG = register(
		"silverfish_spawn_egg", new SpawnEggItem(EntityType.SILVERFISH, 7237230, 3158064, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item SKELETON_SPAWN_EGG = register(
		"skeleton_spawn_egg", new SpawnEggItem(EntityType.SKELETON, 12698049, 4802889, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item SKELETON_HORSE_SPAWN_EGG = register(
		"skeleton_horse_spawn_egg", new SpawnEggItem(EntityType.SKELETON_HORSE, 6842447, 15066584, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item SLIME_SPAWN_EGG = register(
		"slime_spawn_egg", new SpawnEggItem(EntityType.SLIME, 5349438, 8306542, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item SPIDER_SPAWN_EGG = register(
		"spider_spawn_egg", new SpawnEggItem(EntityType.SPIDER, 3419431, 11013646, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item SQUID_SPAWN_EGG = register(
		"squid_spawn_egg", new SpawnEggItem(EntityType.SQUID, 2243405, 7375001, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item STRAY_SPAWN_EGG = register(
		"stray_spawn_egg", new SpawnEggItem(EntityType.STRAY, 6387319, 14543594, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item STRIDER_SPAWN_EGG = register(
		"strider_spawn_egg", new SpawnEggItem(EntityType.STRIDER, 10236982, 5065037, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item TRADER_LLAMA_SPAWN_EGG = register(
		"trader_llama_spawn_egg", new SpawnEggItem(EntityType.TRADER_LLAMA, 15377456, 4547222, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item TROPICAL_FISH_SPAWN_EGG = register(
		"tropical_fish_spawn_egg", new SpawnEggItem(EntityType.TROPICAL_FISH, 15690005, 16775663, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item TURTLE_SPAWN_EGG = register(
		"turtle_spawn_egg", new SpawnEggItem(EntityType.TURTLE, 15198183, 44975, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item VEX_SPAWN_EGG = register(
		"vex_spawn_egg", new SpawnEggItem(EntityType.VEX, 8032420, 15265265, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item VILLAGER_SPAWN_EGG = register(
		"villager_spawn_egg", new SpawnEggItem(EntityType.VILLAGER, 5651507, 12422002, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item VINDICATOR_SPAWN_EGG = register(
		"vindicator_spawn_egg", new SpawnEggItem(EntityType.VINDICATOR, 9804699, 2580065, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item WANDERING_TRADER_SPAWN_EGG = register(
		"wandering_trader_spawn_egg", new SpawnEggItem(EntityType.WANDERING_TRADER, 4547222, 15377456, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item WITCH_SPAWN_EGG = register(
		"witch_spawn_egg", new SpawnEggItem(EntityType.WITCH, 3407872, 5349438, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item WITHER_SKELETON_SPAWN_EGG = register(
		"wither_skeleton_spawn_egg", new SpawnEggItem(EntityType.WITHER_SKELETON, 1315860, 4672845, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item WOLF_SPAWN_EGG = register(
		"wolf_spawn_egg", new SpawnEggItem(EntityType.WOLF, 14144467, 13545366, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item ZOGLIN_SPAWN_EGG = register(
		"zoglin_spawn_egg", new SpawnEggItem(EntityType.ZOGLIN, 13004373, 15132390, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item ZOMBIE_SPAWN_EGG = register(
		"zombie_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE, 44975, 7969893, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item ZOMBIE_HORSE_SPAWN_EGG = register(
		"zombie_horse_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE_HORSE, 3232308, 9945732, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item ZOMBIFIED_PIGLIN_SPAWN_EGG = register(
		"zombified_piglin_spawn_egg", new SpawnEggItem(EntityType.ZOMBIFIED_PIGLIN, 15373203, 5009705, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item ZOMBIE_VILLAGER_SPAWN_EGG = register(
		"zombie_villager_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE_VILLAGER, 5651507, 7969893, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item EXPERIENCE_BOTTLE = register(
		"experience_bottle", new ExperienceBottleItem(new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON))
	);
	public static final Item FIRE_CHARGE = register("fire_charge", new FireChargeItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item WRITABLE_BOOK = register("writable_book", new WritableBookItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC)));
	public static final Item WRITTEN_BOOK = register("written_book", new WrittenBookItem(new Item.Settings().maxCount(16)));
	public static final Item EMERALD = register("emerald", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item ITEM_FRAME = register("item_frame", new ItemFrameItem(new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item FLOWER_POT = register(Blocks.FLOWER_POT, ItemGroup.DECORATIONS);
	public static final Item CARROT = register(
		"carrot", new AliasedBlockItem(Blocks.CARROTS, new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.CARROT))
	);
	public static final Item POTATO = register(
		"potato", new AliasedBlockItem(Blocks.POTATOES, new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.POTATO))
	);
	public static final Item BAKED_POTATO = register("baked_potato", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.BAKED_POTATO)));
	public static final Item POISONOUS_POTATO = register(
		"poisonous_potato", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.POISONOUS_POTATO))
	);
	public static final Item MAP = register("map", new EmptyMapItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item GOLDEN_CARROT = register("golden_carrot", new Item(new Item.Settings().group(ItemGroup.BREWING).food(FoodComponents.GOLDEN_CARROT)));
	public static final Item SKELETON_SKULL = register(
		new WallStandingBlockItem(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.UNCOMMON))
	);
	public static final Item WITHER_SKELETON_SKULL = register(
		new WallStandingBlockItem(
			Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.UNCOMMON)
		)
	);
	public static final Item PLAYER_HEAD = register(
		new SkullItem(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.UNCOMMON))
	);
	public static final Item ZOMBIE_HEAD = register(
		new WallStandingBlockItem(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.UNCOMMON))
	);
	public static final Item CREEPER_HEAD = register(
		new WallStandingBlockItem(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.UNCOMMON))
	);
	public static final Item DRAGON_HEAD = register(
		new WallStandingBlockItem(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.UNCOMMON))
	);
	public static final Item CARROT_ON_A_STICK = register(
		"carrot_on_a_stick", new OnAStickItem<>(new Item.Settings().maxDamage(25).group(ItemGroup.TRANSPORTATION), EntityType.PIG, 7)
	);
	public static final Item WARPED_FUNGUS_ON_A_STICK = register(
		"warped_fungus_on_a_stick", new OnAStickItem<>(new Item.Settings().maxDamage(100).group(ItemGroup.TRANSPORTATION), EntityType.STRIDER, 1)
	);
	public static final Item NETHER_STAR = register("nether_star", new NetherStarItem(new Item.Settings().group(ItemGroup.MATERIALS).rarity(Rarity.UNCOMMON)));
	public static final Item PUMPKIN_PIE = register("pumpkin_pie", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.PUMPKIN_PIE)));
	public static final Item FIREWORK_ROCKET = register("firework_rocket", new FireworkItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item FIREWORK_STAR = register("firework_star", new FireworkChargeItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item ENCHANTED_BOOK = register("enchanted_book", new EnchantedBookItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
	public static final Item NETHER_BRICK = register("nether_brick", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item QUARTZ = register("quartz", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item TNT_MINECART = register(
		"tnt_minecart", new MinecartItem(AbstractMinecartEntity.Type.TNT, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item HOPPER_MINECART = register(
		"hopper_minecart", new MinecartItem(AbstractMinecartEntity.Type.HOPPER, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item PRISMARINE_SHARD = register("prismarine_shard", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item PRISMARINE_CRYSTALS = register("prismarine_crystals", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item RABBIT = register("rabbit", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.RABBIT)));
	public static final Item COOKED_RABBIT = register("cooked_rabbit", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_RABBIT)));
	public static final Item RABBIT_STEW = register(
		"rabbit_stew", new MushroomStewItem(new Item.Settings().maxCount(1).group(ItemGroup.FOOD).food(FoodComponents.RABBIT_STEW))
	);
	public static final Item RABBIT_FOOT = register("rabbit_foot", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item RABBIT_HIDE = register("rabbit_hide", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item ARMOR_STAND = register("armor_stand", new ArmorStandItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS)));
	public static final Item IRON_HORSE_ARMOR = register("iron_horse_armor", new HorseArmorItem(5, "iron", new Item.Settings().maxCount(1).group(ItemGroup.MISC)));
	public static final Item GOLDEN_HORSE_ARMOR = register(
		"golden_horse_armor", new HorseArmorItem(7, "gold", new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item DIAMOND_HORSE_ARMOR = register(
		"diamond_horse_armor", new HorseArmorItem(11, "diamond", new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item LEATHER_HORSE_ARMOR = register(
		"leather_horse_armor", new DyeableHorseArmorItem(3, "leather", new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item LEAD = register("lead", new LeadItem(new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item NAME_TAG = register("name_tag", new NameTagItem(new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item COMMAND_BLOCK_MINECART = register(
		"command_block_minecart", new MinecartItem(AbstractMinecartEntity.Type.COMMAND_BLOCK, new Item.Settings().maxCount(1))
	);
	public static final Item MUTTON = register("mutton", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.MUTTON)));
	public static final Item COOKED_MUTTON = register("cooked_mutton", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_MUTTON)));
	public static final Item WHITE_BANNER = register(
		"white_banner", new BannerItem(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item ORANGE_BANNER = register(
		"orange_banner", new BannerItem(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item MAGENTA_BANNER = register(
		"magenta_banner", new BannerItem(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item LIGHT_BLUE_BANNER = register(
		"light_blue_banner", new BannerItem(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item YELLOW_BANNER = register(
		"yellow_banner", new BannerItem(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item LIME_BANNER = register(
		"lime_banner", new BannerItem(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item PINK_BANNER = register(
		"pink_banner", new BannerItem(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item GRAY_BANNER = register(
		"gray_banner", new BannerItem(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item LIGHT_GRAY_BANNER = register(
		"light_gray_banner", new BannerItem(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item CYAN_BANNER = register(
		"cyan_banner", new BannerItem(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item PURPLE_BANNER = register(
		"purple_banner", new BannerItem(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item BLUE_BANNER = register(
		"blue_banner", new BannerItem(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item BROWN_BANNER = register(
		"brown_banner", new BannerItem(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item GREEN_BANNER = register(
		"green_banner", new BannerItem(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item RED_BANNER = register(
		"red_banner", new BannerItem(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item BLACK_BANNER = register(
		"black_banner", new BannerItem(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item END_CRYSTAL = register("end_crystal", new EndCrystalItem(new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.RARE)));
	public static final Item CHORUS_FRUIT = register(
		"chorus_fruit", new ChorusFruitItem(new Item.Settings().group(ItemGroup.MATERIALS).food(FoodComponents.CHORUS_FRUIT))
	);
	public static final Item POPPED_CHORUS_FRUIT = register("popped_chorus_fruit", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BEETROOT = register("beetroot", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.BEETROOT)));
	public static final Item BEETROOT_SEEDS = register("beetroot_seeds", new AliasedBlockItem(Blocks.BEETROOTS, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BEETROOT_SOUP = register(
		"beetroot_soup", new MushroomStewItem(new Item.Settings().maxCount(1).group(ItemGroup.FOOD).food(FoodComponents.BEETROOT_SOUP))
	);
	public static final Item DRAGON_BREATH = register(
		"dragon_breath", new Item(new Item.Settings().recipeRemainder(GLASS_BOTTLE).group(ItemGroup.BREWING).rarity(Rarity.UNCOMMON))
	);
	public static final Item SPLASH_POTION = register("splash_potion", new SplashPotionItem(new Item.Settings().maxCount(1).group(ItemGroup.BREWING)));
	public static final Item SPECTRAL_ARROW = register("spectral_arrow", new SpectralArrowItem(new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item TIPPED_ARROW = register("tipped_arrow", new TippedArrowItem(new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item LINGERING_POTION = register("lingering_potion", new LingeringPotionItem(new Item.Settings().maxCount(1).group(ItemGroup.BREWING)));
	public static final Item SHIELD = register("shield", new ShieldItem(new Item.Settings().maxDamage(336).group(ItemGroup.COMBAT)));
	public static final Item ELYTRA = register(
		"elytra", new ElytraItem(new Item.Settings().maxDamage(432).group(ItemGroup.TRANSPORTATION).rarity(Rarity.UNCOMMON))
	);
	public static final Item SPRUCE_BOAT = register(
		"spruce_boat", new BoatItem(BoatEntity.Type.SPRUCE, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item BIRCH_BOAT = register(
		"birch_boat", new BoatItem(BoatEntity.Type.BIRCH, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item JUNGLE_BOAT = register(
		"jungle_boat", new BoatItem(BoatEntity.Type.JUNGLE, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item ACACIA_BOAT = register(
		"acacia_boat", new BoatItem(BoatEntity.Type.ACACIA, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item DARK_OAK_BOAT = register(
		"dark_oak_boat", new BoatItem(BoatEntity.Type.DARK_OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item TOTEM_OF_UNDYING = register(
		"totem_of_undying", new Item(new Item.Settings().maxCount(1).group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON))
	);
	public static final Item SHULKER_SHELL = register("shulker_shell", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item IRON_NUGGET = register("iron_nugget", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item KNOWLEDGE_BOOK = register("knowledge_book", new KnowledgeBookItem(new Item.Settings().maxCount(1)));
	public static final Item DEBUG_STICK = register("debug_stick", new DebugStickItem(new Item.Settings().maxCount(1)));
	public static final Item MUSIC_DISC_13 = register(
		"music_disc_13", new MusicDiscItem(1, SoundEvents.MUSIC_DISC_13, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_CAT = register(
		"music_disc_cat", new MusicDiscItem(2, SoundEvents.MUSIC_DISC_CAT, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_BLOCKS = register(
		"music_disc_blocks", new MusicDiscItem(3, SoundEvents.MUSIC_DISC_BLOCKS, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_CHIRP = register(
		"music_disc_chirp", new MusicDiscItem(4, SoundEvents.MUSIC_DISC_CHIRP, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_FAR = register(
		"music_disc_far", new MusicDiscItem(5, SoundEvents.MUSIC_DISC_FAR, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_MALL = register(
		"music_disc_mall", new MusicDiscItem(6, SoundEvents.MUSIC_DISC_MALL, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_MELLOHI = register(
		"music_disc_mellohi", new MusicDiscItem(7, SoundEvents.MUSIC_DISC_MELLOHI, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_STAL = register(
		"music_disc_stal", new MusicDiscItem(8, SoundEvents.MUSIC_DISC_STAL, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_STRAD = register(
		"music_disc_strad", new MusicDiscItem(9, SoundEvents.MUSIC_DISC_STRAD, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_WARD = register(
		"music_disc_ward", new MusicDiscItem(10, SoundEvents.MUSIC_DISC_WARD, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_11 = register(
		"music_disc_11", new MusicDiscItem(11, SoundEvents.MUSIC_DISC_11, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item MUSIC_DISC_WAIT = register(
		"music_disc_wait", new MusicDiscItem(12, SoundEvents.MUSIC_DISC_WAIT, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE))
	);
	public static final Item TRIDENT = register("trident", new TridentItem(new Item.Settings().maxDamage(250).group(ItemGroup.COMBAT)));
	public static final Item PHANTOM_MEMBRANE = register("phantom_membrane", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item NAUTILUS_SHELL = register("nautilus_shell", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item HEART_OF_THE_SEA = register("heart_of_the_sea", new Item(new Item.Settings().group(ItemGroup.MATERIALS).rarity(Rarity.UNCOMMON)));
	public static final Item CROSSBOW = register("crossbow", new CrossbowItem(new Item.Settings().maxCount(1).group(ItemGroup.COMBAT).maxDamage(326)));
	public static final Item SUSPICIOUS_STEW = register(
		"suspicious_stew", new SuspiciousStewItem(new Item.Settings().maxCount(1).food(FoodComponents.SUSPICIOUS_STEW))
	);
	public static final Item LOOM = register(Blocks.LOOM, ItemGroup.DECORATIONS);
	public static final Item FLOWER_BANNER_PATTERN = register(
		"flower_banner_pattern", new BannerPatternItem(BannerPattern.FLOWER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item CREEPER_BANNER_PATTERN = register(
		"creeper_banner_pattern", new BannerPatternItem(BannerPattern.CREEPER, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.UNCOMMON))
	);
	public static final Item SKULL_BANNER_PATTERN = register(
		"skull_banner_pattern", new BannerPatternItem(BannerPattern.SKULL, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.UNCOMMON))
	);
	public static final Item MOJANG_BANNER_PATTERN = register(
		"mojang_banner_pattern", new BannerPatternItem(BannerPattern.MOJANG, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.EPIC))
	);
	public static final Item GLOBE_BANNER_PATTERN = register(
		"globe_banner_pattern", new BannerPatternItem(BannerPattern.GLOBE, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item COMPOSTER = register(Blocks.COMPOSTER, ItemGroup.DECORATIONS);
	public static final Item BARREL = register(Blocks.BARREL, ItemGroup.DECORATIONS);
	public static final Item SMOKER = register(Blocks.SMOKER, ItemGroup.DECORATIONS);
	public static final Item BLAST_FURNACE = register(Blocks.BLAST_FURNACE, ItemGroup.DECORATIONS);
	public static final Item CARTOGRAPHY_TABLE = register(Blocks.CARTOGRAPHY_TABLE, ItemGroup.DECORATIONS);
	public static final Item FLETCHING_TABLE = register(Blocks.FLETCHING_TABLE, ItemGroup.DECORATIONS);
	public static final Item GRINDSTONE = register(Blocks.GRINDSTONE, ItemGroup.DECORATIONS);
	public static final Item LECTERN = register(Blocks.LECTERN, ItemGroup.REDSTONE);
	public static final Item SMITHING_TABLE = register(Blocks.SMITHING_TABLE, ItemGroup.DECORATIONS);
	public static final Item STONECUTTER = register(Blocks.STONECUTTER, ItemGroup.DECORATIONS);
	public static final Item BELL = register(Blocks.BELL, ItemGroup.DECORATIONS);
	public static final Item LANTERN = register(Blocks.LANTERN, ItemGroup.DECORATIONS);
	public static final Item SOUL_FIRE_LANTERN = register(Blocks.SOUL_FIRE_LANTERN, ItemGroup.DECORATIONS);
	public static final Item SWEET_BERRIES = register(
		"sweet_berries", new AliasedBlockItem(Blocks.SWEET_BERRY_BUSH, new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.SWEET_BERRIES))
	);
	public static final Item CAMPFIRE = register(Blocks.CAMPFIRE, ItemGroup.DECORATIONS);
	public static final Item SHROOMLIGHT = register(Blocks.SHROOMLIGHT, ItemGroup.DECORATIONS);
	public static final Item HONEYCOMB = register("honeycomb", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BEE_NEST = register(Blocks.BEE_NEST, ItemGroup.DECORATIONS);
	public static final Item BEEHIVE = register(Blocks.BEEHIVE, ItemGroup.DECORATIONS);
	public static final Item HONEY_BOTTLE = register(
		"honey_bottle", new HoneyBottleItem(new Item.Settings().recipeRemainder(GLASS_BOTTLE).food(FoodComponents.HONEY_BOTTLE).group(ItemGroup.FOOD).maxCount(16))
	);
	public static final Item HONEY_BLOCK = register(Blocks.HONEY_BLOCK, ItemGroup.DECORATIONS);
	public static final Item HONEYCOMB_BLOCK = register(Blocks.HONEYCOMB_BLOCK, ItemGroup.DECORATIONS);
	public static final Item LODESTONE = register(Blocks.LODESTONE, ItemGroup.DECORATIONS);
	public static final Item NETHERITE_BLOCK = register(new BlockItem(Blocks.NETHERITE_BLOCK, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS).fireproof()));
	public static final Item ANCIENT_DEBRIS = register(new BlockItem(Blocks.ANCIENT_DEBRIS, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS).fireproof()));
	public static final Item NETHERITE_INGOT = register("netherite_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS).fireproof()));
	public static final Item NETHERITE_SCRAP = register("netherite_scrap", new Item(new Item.Settings().group(ItemGroup.MATERIALS).fireproof()));
	public static final Item TARGET = register(Blocks.TARGET, ItemGroup.REDSTONE);
	public static final Item CRYING_OBSIDIAN = register(Blocks.CRYING_OBSIDIAN, ItemGroup.BUILDING_BLOCKS);
	public static final Item RESPAWN_ANCHOR = register(Blocks.RESPAWN_ANCHOR, ItemGroup.DECORATIONS);

	private static Item register(Block block) {
		return register(new BlockItem(block, new Item.Settings()));
	}

	private static Item register(Block block, ItemGroup group) {
		return register(new BlockItem(block, new Item.Settings().group(group)));
	}

	private static Item register(BlockItem item) {
		return register(item.getBlock(), item);
	}

	protected static Item register(Block block, Item item) {
		return register(Registry.BLOCK.getId(block), item);
	}

	private static Item register(String id, Item item) {
		return register(new Identifier(id), item);
	}

	private static Item register(Identifier id, Item item) {
		if (item instanceof BlockItem) {
			((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
		}

		return Registry.register(Registry.ITEM, id, item);
	}
}
