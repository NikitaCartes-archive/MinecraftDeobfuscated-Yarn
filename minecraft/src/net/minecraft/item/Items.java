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
	public static final Item AIR = register(Blocks.field_10124, new AirBlockItem(Blocks.field_10124, new Item.Settings()));
	public static final Item STONE = register(Blocks.field_10340, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRANITE = register(Blocks.field_10474, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_GRANITE = register(Blocks.field_10289, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIORITE = register(Blocks.field_10508, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_DIORITE = register(Blocks.field_10346, ItemGroup.BUILDING_BLOCKS);
	public static final Item ANDESITE = register(Blocks.field_10115, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_ANDESITE = register(Blocks.field_10093, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRASS_BLOCK = register(Blocks.field_10219, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIRT = register(Blocks.field_10566, ItemGroup.BUILDING_BLOCKS);
	public static final Item COARSE_DIRT = register(Blocks.field_10253, ItemGroup.BUILDING_BLOCKS);
	public static final Item PODZOL = register(Blocks.field_10520, ItemGroup.BUILDING_BLOCKS);
	public static final Item COBBLESTONE = register(Blocks.field_10445, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_PLANKS = register(Blocks.field_10161, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_PLANKS = register(Blocks.field_9975, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_PLANKS = register(Blocks.field_10148, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_PLANKS = register(Blocks.field_10334, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_PLANKS = register(Blocks.field_10218, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_PLANKS = register(Blocks.field_10075, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_SAPLING = register(Blocks.field_10394, ItemGroup.DECORATIONS);
	public static final Item SPRUCE_SAPLING = register(Blocks.field_10217, ItemGroup.DECORATIONS);
	public static final Item BIRCH_SAPLING = register(Blocks.field_10575, ItemGroup.DECORATIONS);
	public static final Item JUNGLE_SAPLING = register(Blocks.field_10276, ItemGroup.DECORATIONS);
	public static final Item ACACIA_SAPLING = register(Blocks.field_10385, ItemGroup.DECORATIONS);
	public static final Item DARK_OAK_SAPLING = register(Blocks.field_10160, ItemGroup.DECORATIONS);
	public static final Item BEDROCK = register(Blocks.field_9987, ItemGroup.BUILDING_BLOCKS);
	public static final Item SAND = register(Blocks.field_10102, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_SAND = register(Blocks.field_10534, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAVEL = register(Blocks.field_10255, ItemGroup.BUILDING_BLOCKS);
	public static final Item GOLD_ORE = register(Blocks.field_10571, ItemGroup.BUILDING_BLOCKS);
	public static final Item IRON_ORE = register(Blocks.field_10212, ItemGroup.BUILDING_BLOCKS);
	public static final Item COAL_ORE = register(Blocks.field_10418, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_LOG = register(Blocks.field_10431, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_LOG = register(Blocks.field_10037, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_LOG = register(Blocks.field_10511, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_LOG = register(Blocks.field_10306, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_LOG = register(Blocks.field_10533, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_LOG = register(Blocks.field_10010, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_OAK_LOG = register(Blocks.field_10519, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_SPRUCE_LOG = register(Blocks.field_10436, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_BIRCH_LOG = register(Blocks.field_10366, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_JUNGLE_LOG = register(Blocks.field_10254, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_ACACIA_LOG = register(Blocks.field_10622, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_DARK_OAK_LOG = register(Blocks.field_10244, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_OAK_WOOD = register(Blocks.field_10250, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_SPRUCE_WOOD = register(Blocks.field_10558, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_BIRCH_WOOD = register(Blocks.field_10204, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_JUNGLE_WOOD = register(Blocks.field_10084, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_ACACIA_WOOD = register(Blocks.field_10103, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRIPPED_DARK_OAK_WOOD = register(Blocks.field_10374, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_WOOD = register(Blocks.field_10126, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_WOOD = register(Blocks.field_10155, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_WOOD = register(Blocks.field_10307, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_WOOD = register(Blocks.field_10303, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_WOOD = register(Blocks.field_9999, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_WOOD = register(Blocks.field_10178, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_LEAVES = register(Blocks.field_10503, ItemGroup.DECORATIONS);
	public static final Item SPRUCE_LEAVES = register(Blocks.field_9988, ItemGroup.DECORATIONS);
	public static final Item BIRCH_LEAVES = register(Blocks.field_10539, ItemGroup.DECORATIONS);
	public static final Item JUNGLE_LEAVES = register(Blocks.field_10335, ItemGroup.DECORATIONS);
	public static final Item ACACIA_LEAVES = register(Blocks.field_10098, ItemGroup.DECORATIONS);
	public static final Item DARK_OAK_LEAVES = register(Blocks.field_10035, ItemGroup.DECORATIONS);
	public static final Item SPONGE = register(Blocks.field_10258, ItemGroup.BUILDING_BLOCKS);
	public static final Item WET_SPONGE = register(Blocks.field_10562, ItemGroup.BUILDING_BLOCKS);
	public static final Item GLASS = register(Blocks.field_10033, ItemGroup.BUILDING_BLOCKS);
	public static final Item LAPIS_ORE = register(Blocks.field_10090, ItemGroup.BUILDING_BLOCKS);
	public static final Item LAPIS_BLOCK = register(Blocks.field_10441, ItemGroup.BUILDING_BLOCKS);
	public static final Item DISPENSER = register(Blocks.field_10200, ItemGroup.REDSTONE);
	public static final Item SANDSTONE = register(Blocks.field_9979, ItemGroup.BUILDING_BLOCKS);
	public static final Item CHISELED_SANDSTONE = register(Blocks.field_10292, ItemGroup.BUILDING_BLOCKS);
	public static final Item CUT_SANDSTONE = register(Blocks.field_10361, ItemGroup.BUILDING_BLOCKS);
	public static final Item NOTE_BLOCK = register(Blocks.field_10179, ItemGroup.REDSTONE);
	public static final Item POWERED_RAIL = register(Blocks.field_10425, ItemGroup.TRANSPORTATION);
	public static final Item DETECTOR_RAIL = register(Blocks.field_10025, ItemGroup.TRANSPORTATION);
	public static final Item STICKY_PISTON = register(Blocks.field_10615, ItemGroup.REDSTONE);
	public static final Item COBWEB = register(Blocks.field_10343, ItemGroup.DECORATIONS);
	public static final Item GRASS = register(Blocks.field_10479, ItemGroup.DECORATIONS);
	public static final Item FERN = register(Blocks.field_10112, ItemGroup.DECORATIONS);
	public static final Item DEAD_BUSH = register(Blocks.field_10428, ItemGroup.DECORATIONS);
	public static final Item SEAGRASS = register(Blocks.field_10376, ItemGroup.DECORATIONS);
	public static final Item SEA_PICKLE = register(Blocks.field_10476, ItemGroup.DECORATIONS);
	public static final Item PISTON = register(Blocks.field_10560, ItemGroup.REDSTONE);
	public static final Item WHITE_WOOL = register(Blocks.field_10446, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_WOOL = register(Blocks.field_10095, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_WOOL = register(Blocks.field_10215, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_WOOL = register(Blocks.field_10294, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_WOOL = register(Blocks.field_10490, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_WOOL = register(Blocks.field_10028, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_WOOL = register(Blocks.field_10459, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_WOOL = register(Blocks.field_10423, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_WOOL = register(Blocks.field_10222, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_WOOL = register(Blocks.field_10619, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_WOOL = register(Blocks.field_10259, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_WOOL = register(Blocks.field_10514, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_WOOL = register(Blocks.field_10113, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_WOOL = register(Blocks.field_10170, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_WOOL = register(Blocks.field_10314, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_WOOL = register(Blocks.field_10146, ItemGroup.BUILDING_BLOCKS);
	public static final Item DANDELION = register(Blocks.field_10182, ItemGroup.DECORATIONS);
	public static final Item POPPY = register(Blocks.field_10449, ItemGroup.DECORATIONS);
	public static final Item BLUE_ORCHID = register(Blocks.field_10086, ItemGroup.DECORATIONS);
	public static final Item ALLIUM = register(Blocks.field_10226, ItemGroup.DECORATIONS);
	public static final Item AZURE_BLUET = register(Blocks.field_10573, ItemGroup.DECORATIONS);
	public static final Item RED_TULIP = register(Blocks.field_10270, ItemGroup.DECORATIONS);
	public static final Item ORANGE_TULIP = register(Blocks.field_10048, ItemGroup.DECORATIONS);
	public static final Item WHITE_TULIP = register(Blocks.field_10156, ItemGroup.DECORATIONS);
	public static final Item PINK_TULIP = register(Blocks.field_10315, ItemGroup.DECORATIONS);
	public static final Item OXEYE_DAISY = register(Blocks.field_10554, ItemGroup.DECORATIONS);
	public static final Item CORNFLOWER = register(Blocks.field_9995, ItemGroup.DECORATIONS);
	public static final Item LILY_OF_THE_VALLEY = register(Blocks.field_10548, ItemGroup.DECORATIONS);
	public static final Item WITHER_ROSE = register(Blocks.field_10606, ItemGroup.DECORATIONS);
	public static final Item BROWN_MUSHROOM = register(Blocks.field_10251, ItemGroup.DECORATIONS);
	public static final Item RED_MUSHROOM = register(Blocks.field_10559, ItemGroup.DECORATIONS);
	public static final Item GOLD_BLOCK = register(Blocks.field_10205, ItemGroup.BUILDING_BLOCKS);
	public static final Item IRON_BLOCK = register(Blocks.field_10085, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_SLAB = register(Blocks.field_10119, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_SLAB = register(Blocks.field_10071, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_SLAB = register(Blocks.field_10257, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_SLAB = register(Blocks.field_10617, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_SLAB = register(Blocks.field_10031, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_SLAB = register(Blocks.field_10500, ItemGroup.BUILDING_BLOCKS);
	public static final Item STONE_SLAB = register(Blocks.field_10454, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_STONE_SLAB = register(Blocks.field_10136, ItemGroup.BUILDING_BLOCKS);
	public static final Item SANDSTONE_SLAB = register(Blocks.field_10007, ItemGroup.BUILDING_BLOCKS);
	public static final Item CUT_SANDSTONE_SLAB = register(Blocks.field_18890, ItemGroup.BUILDING_BLOCKS);
	public static final Item PETRIFIED_OAK_SLAB = register(Blocks.field_10298, ItemGroup.BUILDING_BLOCKS);
	public static final Item COBBLESTONE_SLAB = register(Blocks.field_10351, ItemGroup.BUILDING_BLOCKS);
	public static final Item BRICK_SLAB = register(Blocks.field_10191, ItemGroup.BUILDING_BLOCKS);
	public static final Item STONE_BRICK_SLAB = register(Blocks.field_10131, ItemGroup.BUILDING_BLOCKS);
	public static final Item NETHER_BRICK_SLAB = register(Blocks.field_10390, ItemGroup.BUILDING_BLOCKS);
	public static final Item QUARTZ_SLAB = register(Blocks.field_10237, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_SANDSTONE_SLAB = register(Blocks.field_10624, ItemGroup.BUILDING_BLOCKS);
	public static final Item CUT_RED_SANDSTONE_SLAB = register(Blocks.field_18891, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPUR_SLAB = register(Blocks.field_10175, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_SLAB = register(Blocks.field_10389, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_BRICK_SLAB = register(Blocks.field_10236, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_PRISMARINE_SLAB = register(Blocks.field_10623, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_QUARTZ = register(Blocks.field_9978, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_RED_SANDSTONE = register(Blocks.field_10483, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_SANDSTONE = register(Blocks.field_10467, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_STONE = register(Blocks.field_10360, ItemGroup.BUILDING_BLOCKS);
	public static final Item BRICKS = register(Blocks.field_10104, ItemGroup.BUILDING_BLOCKS);
	public static final Item TNT = register(Blocks.field_10375, ItemGroup.REDSTONE);
	public static final Item BOOKSHELF = register(Blocks.field_10504, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_COBBLESTONE = register(Blocks.field_9989, ItemGroup.BUILDING_BLOCKS);
	public static final Item OBSIDIAN = register(Blocks.field_10540, ItemGroup.BUILDING_BLOCKS);
	public static final Item TORCH = register(new WallStandingBlockItem(Blocks.field_10336, Blocks.field_10099, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item END_ROD = register(Blocks.field_10455, ItemGroup.DECORATIONS);
	public static final Item CHORUS_PLANT = register(Blocks.field_10021, ItemGroup.DECORATIONS);
	public static final Item CHORUS_FLOWER = register(Blocks.field_10528, ItemGroup.DECORATIONS);
	public static final Item PURPUR_BLOCK = register(Blocks.field_10286, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPUR_PILLAR = register(Blocks.field_10505, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPUR_STAIRS = register(Blocks.field_9992, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPAWNER = register(Blocks.field_10260);
	public static final Item OAK_STAIRS = register(Blocks.field_10563, ItemGroup.BUILDING_BLOCKS);
	public static final Item CHEST = register(Blocks.field_10034, ItemGroup.DECORATIONS);
	public static final Item DIAMOND_ORE = register(Blocks.field_10442, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIAMOND_BLOCK = register(Blocks.field_10201, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRAFTING_TABLE = register(Blocks.field_9980, ItemGroup.DECORATIONS);
	public static final Item FARMLAND = register(Blocks.field_10362, ItemGroup.DECORATIONS);
	public static final Item FURNACE = register(Blocks.field_10181, ItemGroup.DECORATIONS);
	public static final Item LADDER = register(Blocks.field_9983, ItemGroup.DECORATIONS);
	public static final Item RAIL = register(Blocks.field_10167, ItemGroup.TRANSPORTATION);
	public static final Item COBBLESTONE_STAIRS = register(Blocks.field_10596, ItemGroup.BUILDING_BLOCKS);
	public static final Item LEVER = register(Blocks.field_10363, ItemGroup.REDSTONE);
	public static final Item STONE_PRESSURE_PLATE = register(Blocks.field_10158, ItemGroup.REDSTONE);
	public static final Item OAK_PRESSURE_PLATE = register(Blocks.field_10484, ItemGroup.REDSTONE);
	public static final Item SPRUCE_PRESSURE_PLATE = register(Blocks.field_10332, ItemGroup.REDSTONE);
	public static final Item BIRCH_PRESSURE_PLATE = register(Blocks.field_10592, ItemGroup.REDSTONE);
	public static final Item JUNGLE_PRESSURE_PLATE = register(Blocks.field_10026, ItemGroup.REDSTONE);
	public static final Item ACACIA_PRESSURE_PLATE = register(Blocks.field_10397, ItemGroup.REDSTONE);
	public static final Item DARK_OAK_PRESSURE_PLATE = register(Blocks.field_10470, ItemGroup.REDSTONE);
	public static final Item REDSTONE_ORE = register(Blocks.field_10080, ItemGroup.BUILDING_BLOCKS);
	public static final Item REDSTONE_TORCH = register(
		new WallStandingBlockItem(Blocks.field_10523, Blocks.field_10301, new Item.Settings().group(ItemGroup.REDSTONE))
	);
	public static final Item STONE_BUTTON = register(Blocks.field_10494, ItemGroup.REDSTONE);
	public static final Item SNOW = register(Blocks.field_10477, ItemGroup.DECORATIONS);
	public static final Item ICE = register(Blocks.field_10295, ItemGroup.BUILDING_BLOCKS);
	public static final Item SNOW_BLOCK = register(Blocks.field_10491, ItemGroup.BUILDING_BLOCKS);
	public static final Item CACTUS = register(Blocks.field_10029, ItemGroup.DECORATIONS);
	public static final Item CLAY = register(Blocks.field_10460, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUKEBOX = register(Blocks.field_10223, ItemGroup.DECORATIONS);
	public static final Item OAK_FENCE = register(Blocks.field_10620, ItemGroup.DECORATIONS);
	public static final Item SPRUCE_FENCE = register(Blocks.field_10020, ItemGroup.DECORATIONS);
	public static final Item BIRCH_FENCE = register(Blocks.field_10299, ItemGroup.DECORATIONS);
	public static final Item JUNGLE_FENCE = register(Blocks.field_10319, ItemGroup.DECORATIONS);
	public static final Item ACACIA_FENCE = register(Blocks.field_10144, ItemGroup.DECORATIONS);
	public static final Item DARK_OAK_FENCE = register(Blocks.field_10132, ItemGroup.DECORATIONS);
	public static final Item PUMPKIN = register(Blocks.field_10261, ItemGroup.BUILDING_BLOCKS);
	public static final Item CARVED_PUMPKIN = register(Blocks.field_10147, ItemGroup.BUILDING_BLOCKS);
	public static final Item NETHERRACK = register(Blocks.field_10515, ItemGroup.BUILDING_BLOCKS);
	public static final Item SOUL_SAND = register(Blocks.field_10114, ItemGroup.BUILDING_BLOCKS);
	public static final Item GLOWSTONE = register(Blocks.field_10171, ItemGroup.BUILDING_BLOCKS);
	public static final Item JACK_O_LANTERN = register(Blocks.field_10009, ItemGroup.BUILDING_BLOCKS);
	public static final Item OAK_TRAPDOOR = register(Blocks.field_10137, ItemGroup.REDSTONE);
	public static final Item SPRUCE_TRAPDOOR = register(Blocks.field_10323, ItemGroup.REDSTONE);
	public static final Item BIRCH_TRAPDOOR = register(Blocks.field_10486, ItemGroup.REDSTONE);
	public static final Item JUNGLE_TRAPDOOR = register(Blocks.field_10017, ItemGroup.REDSTONE);
	public static final Item ACACIA_TRAPDOOR = register(Blocks.field_10608, ItemGroup.REDSTONE);
	public static final Item DARK_OAK_TRAPDOOR = register(Blocks.field_10246, ItemGroup.REDSTONE);
	public static final Item INFESTED_STONE = register(Blocks.field_10277, ItemGroup.DECORATIONS);
	public static final Item INFESTED_COBBLESTONE = register(Blocks.field_10492, ItemGroup.DECORATIONS);
	public static final Item INFESTED_STONE_BRICKS = register(Blocks.field_10387, ItemGroup.DECORATIONS);
	public static final Item INFESTED_MOSSY_STONE_BRICKS = register(Blocks.field_10480, ItemGroup.DECORATIONS);
	public static final Item INFESTED_CRACKED_STONE_BRICKS = register(Blocks.field_10100, ItemGroup.DECORATIONS);
	public static final Item INFESTED_CHISELED_STONE_BRICKS = register(Blocks.field_10176, ItemGroup.DECORATIONS);
	public static final Item STONE_BRICKS = register(Blocks.field_10056, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_STONE_BRICKS = register(Blocks.field_10065, ItemGroup.BUILDING_BLOCKS);
	public static final Item CRACKED_STONE_BRICKS = register(Blocks.field_10416, ItemGroup.BUILDING_BLOCKS);
	public static final Item CHISELED_STONE_BRICKS = register(Blocks.field_10552, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_MUSHROOM_BLOCK = register(Blocks.field_10580, ItemGroup.DECORATIONS);
	public static final Item RED_MUSHROOM_BLOCK = register(Blocks.field_10240, ItemGroup.DECORATIONS);
	public static final Item MUSHROOM_STEM = register(Blocks.field_10556, ItemGroup.DECORATIONS);
	public static final Item IRON_BARS = register(Blocks.field_10576, ItemGroup.DECORATIONS);
	public static final Item GLASS_PANE = register(Blocks.field_10285, ItemGroup.DECORATIONS);
	public static final Item MELON = register(Blocks.field_10545, ItemGroup.BUILDING_BLOCKS);
	public static final Item VINE = register(Blocks.field_10597, ItemGroup.DECORATIONS);
	public static final Item OAK_FENCE_GATE = register(Blocks.field_10188, ItemGroup.REDSTONE);
	public static final Item SPRUCE_FENCE_GATE = register(Blocks.field_10291, ItemGroup.REDSTONE);
	public static final Item BIRCH_FENCE_GATE = register(Blocks.field_10513, ItemGroup.REDSTONE);
	public static final Item JUNGLE_FENCE_GATE = register(Blocks.field_10041, ItemGroup.REDSTONE);
	public static final Item ACACIA_FENCE_GATE = register(Blocks.field_10457, ItemGroup.REDSTONE);
	public static final Item DARK_OAK_FENCE_GATE = register(Blocks.field_10196, ItemGroup.REDSTONE);
	public static final Item BRICK_STAIRS = register(Blocks.field_10089, ItemGroup.BUILDING_BLOCKS);
	public static final Item STONE_BRICK_STAIRS = register(Blocks.field_10392, ItemGroup.BUILDING_BLOCKS);
	public static final Item MYCELIUM = register(Blocks.field_10402, ItemGroup.BUILDING_BLOCKS);
	public static final Item LILY_PAD = register(new LilyPadItem(Blocks.field_10588, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item NETHER_BRICKS = register(Blocks.field_10266, ItemGroup.BUILDING_BLOCKS);
	public static final Item NETHER_BRICK_FENCE = register(Blocks.field_10364, ItemGroup.DECORATIONS);
	public static final Item NETHER_BRICK_STAIRS = register(Blocks.field_10159, ItemGroup.BUILDING_BLOCKS);
	public static final Item ENCHANTING_TABLE = register(Blocks.field_10485, ItemGroup.DECORATIONS);
	public static final Item END_PORTAL_FRAME = register(Blocks.field_10398, ItemGroup.DECORATIONS);
	public static final Item END_STONE = register(Blocks.field_10471, ItemGroup.BUILDING_BLOCKS);
	public static final Item END_STONE_BRICKS = register(Blocks.field_10462, ItemGroup.BUILDING_BLOCKS);
	public static final Item DRAGON_EGG = register(new BlockItem(Blocks.field_10081, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item REDSTONE_LAMP = register(Blocks.field_10524, ItemGroup.REDSTONE);
	public static final Item SANDSTONE_STAIRS = register(Blocks.field_10142, ItemGroup.BUILDING_BLOCKS);
	public static final Item EMERALD_ORE = register(Blocks.field_10013, ItemGroup.BUILDING_BLOCKS);
	public static final Item ENDER_CHEST = register(Blocks.field_10443, ItemGroup.DECORATIONS);
	public static final Item TRIPWIRE_HOOK = register(Blocks.field_10348, ItemGroup.REDSTONE);
	public static final Item EMERALD_BLOCK = register(Blocks.field_10234, ItemGroup.BUILDING_BLOCKS);
	public static final Item SPRUCE_STAIRS = register(Blocks.field_10569, ItemGroup.BUILDING_BLOCKS);
	public static final Item BIRCH_STAIRS = register(Blocks.field_10408, ItemGroup.BUILDING_BLOCKS);
	public static final Item JUNGLE_STAIRS = register(Blocks.field_10122, ItemGroup.BUILDING_BLOCKS);
	public static final Item COMMAND_BLOCK = register(new CommandBlockItem(Blocks.field_10525, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item BEACON = register(new BlockItem(Blocks.field_10327, new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.field_8903)));
	public static final Item COBBLESTONE_WALL = register(Blocks.field_10625, ItemGroup.DECORATIONS);
	public static final Item MOSSY_COBBLESTONE_WALL = register(Blocks.field_9990, ItemGroup.DECORATIONS);
	public static final Item BRICK_WALL = register(Blocks.field_10269, ItemGroup.DECORATIONS);
	public static final Item PRISMARINE_WALL = register(Blocks.field_10530, ItemGroup.DECORATIONS);
	public static final Item RED_SANDSTONE_WALL = register(Blocks.field_10413, ItemGroup.DECORATIONS);
	public static final Item MOSSY_STONE_BRICK_WALL = register(Blocks.field_10059, ItemGroup.DECORATIONS);
	public static final Item GRANITE_WALL = register(Blocks.field_10072, ItemGroup.DECORATIONS);
	public static final Item STONE_BRICK_WALL = register(Blocks.field_10252, ItemGroup.DECORATIONS);
	public static final Item NETHER_BRICK_WALL = register(Blocks.field_10127, ItemGroup.DECORATIONS);
	public static final Item ANDESITE_WALL = register(Blocks.field_10489, ItemGroup.DECORATIONS);
	public static final Item RED_NETHER_BRICK_WALL = register(Blocks.field_10311, ItemGroup.DECORATIONS);
	public static final Item SANDSTONE_WALL = register(Blocks.field_10630, ItemGroup.DECORATIONS);
	public static final Item END_STONE_BRICK_WALL = register(Blocks.field_10001, ItemGroup.DECORATIONS);
	public static final Item DIORITE_WALL = register(Blocks.field_10517, ItemGroup.DECORATIONS);
	public static final Item OAK_BUTTON = register(Blocks.field_10057, ItemGroup.REDSTONE);
	public static final Item SPRUCE_BUTTON = register(Blocks.field_10066, ItemGroup.REDSTONE);
	public static final Item BIRCH_BUTTON = register(Blocks.field_10417, ItemGroup.REDSTONE);
	public static final Item JUNGLE_BUTTON = register(Blocks.field_10553, ItemGroup.REDSTONE);
	public static final Item ACACIA_BUTTON = register(Blocks.field_10278, ItemGroup.REDSTONE);
	public static final Item DARK_OAK_BUTTON = register(Blocks.field_10493, ItemGroup.REDSTONE);
	public static final Item ANVIL = register(Blocks.field_10535, ItemGroup.DECORATIONS);
	public static final Item CHIPPED_ANVIL = register(Blocks.field_10105, ItemGroup.DECORATIONS);
	public static final Item DAMAGED_ANVIL = register(Blocks.field_10414, ItemGroup.DECORATIONS);
	public static final Item TRAPPED_CHEST = register(Blocks.field_10380, ItemGroup.REDSTONE);
	public static final Item LIGHT_WEIGHTED_PRESSURE_PLATE = register(Blocks.field_10224, ItemGroup.REDSTONE);
	public static final Item HEAVY_WEIGHTED_PRESSURE_PLATE = register(Blocks.field_10582, ItemGroup.REDSTONE);
	public static final Item DAYLIGHT_DETECTOR = register(Blocks.field_10429, ItemGroup.REDSTONE);
	public static final Item REDSTONE_BLOCK = register(Blocks.field_10002, ItemGroup.REDSTONE);
	public static final Item NETHER_QUARTZ_ORE = register(Blocks.field_10213, ItemGroup.BUILDING_BLOCKS);
	public static final Item HOPPER = register(Blocks.field_10312, ItemGroup.REDSTONE);
	public static final Item CHISELED_QUARTZ_BLOCK = register(Blocks.field_10044, ItemGroup.BUILDING_BLOCKS);
	public static final Item QUARTZ_BLOCK = register(Blocks.field_10153, ItemGroup.BUILDING_BLOCKS);
	public static final Item QUARTZ_PILLAR = register(Blocks.field_10437, ItemGroup.BUILDING_BLOCKS);
	public static final Item QUARTZ_STAIRS = register(Blocks.field_10451, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACTIVATOR_RAIL = register(Blocks.field_10546, ItemGroup.TRANSPORTATION);
	public static final Item DROPPER = register(Blocks.field_10228, ItemGroup.REDSTONE);
	public static final Item WHITE_TERRACOTTA = register(Blocks.field_10611, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_TERRACOTTA = register(Blocks.field_10184, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_TERRACOTTA = register(Blocks.field_10015, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_TERRACOTTA = register(Blocks.field_10325, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_TERRACOTTA = register(Blocks.field_10143, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_TERRACOTTA = register(Blocks.field_10014, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_TERRACOTTA = register(Blocks.field_10444, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_TERRACOTTA = register(Blocks.field_10349, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_TERRACOTTA = register(Blocks.field_10590, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_TERRACOTTA = register(Blocks.field_10235, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_TERRACOTTA = register(Blocks.field_10570, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_TERRACOTTA = register(Blocks.field_10409, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_TERRACOTTA = register(Blocks.field_10123, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_TERRACOTTA = register(Blocks.field_10526, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_TERRACOTTA = register(Blocks.field_10328, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_TERRACOTTA = register(Blocks.field_10626, ItemGroup.BUILDING_BLOCKS);
	public static final Item BARRIER = register(Blocks.field_10499);
	public static final Item IRON_TRAPDOOR = register(Blocks.field_10453, ItemGroup.REDSTONE);
	public static final Item HAY_BLOCK = register(Blocks.field_10359, ItemGroup.BUILDING_BLOCKS);
	public static final Item WHITE_CARPET = register(Blocks.field_10466, ItemGroup.DECORATIONS);
	public static final Item ORANGE_CARPET = register(Blocks.field_9977, ItemGroup.DECORATIONS);
	public static final Item MAGENTA_CARPET = register(Blocks.field_10482, ItemGroup.DECORATIONS);
	public static final Item LIGHT_BLUE_CARPET = register(Blocks.field_10290, ItemGroup.DECORATIONS);
	public static final Item YELLOW_CARPET = register(Blocks.field_10512, ItemGroup.DECORATIONS);
	public static final Item LIME_CARPET = register(Blocks.field_10040, ItemGroup.DECORATIONS);
	public static final Item PINK_CARPET = register(Blocks.field_10393, ItemGroup.DECORATIONS);
	public static final Item GRAY_CARPET = register(Blocks.field_10591, ItemGroup.DECORATIONS);
	public static final Item LIGHT_GRAY_CARPET = register(Blocks.field_10209, ItemGroup.DECORATIONS);
	public static final Item CYAN_CARPET = register(Blocks.field_10433, ItemGroup.DECORATIONS);
	public static final Item PURPLE_CARPET = register(Blocks.field_10510, ItemGroup.DECORATIONS);
	public static final Item BLUE_CARPET = register(Blocks.field_10043, ItemGroup.DECORATIONS);
	public static final Item BROWN_CARPET = register(Blocks.field_10473, ItemGroup.DECORATIONS);
	public static final Item GREEN_CARPET = register(Blocks.field_10338, ItemGroup.DECORATIONS);
	public static final Item RED_CARPET = register(Blocks.field_10536, ItemGroup.DECORATIONS);
	public static final Item BLACK_CARPET = register(Blocks.field_10106, ItemGroup.DECORATIONS);
	public static final Item TERRACOTTA = register(Blocks.field_10415, ItemGroup.BUILDING_BLOCKS);
	public static final Item COAL_BLOCK = register(Blocks.field_10381, ItemGroup.BUILDING_BLOCKS);
	public static final Item PACKED_ICE = register(Blocks.field_10225, ItemGroup.BUILDING_BLOCKS);
	public static final Item ACACIA_STAIRS = register(Blocks.field_10256, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_OAK_STAIRS = register(Blocks.field_10616, ItemGroup.BUILDING_BLOCKS);
	public static final Item SLIME_BLOCK = register(Blocks.field_10030, ItemGroup.DECORATIONS);
	public static final Item GRASS_PATH = register(Blocks.field_10194, ItemGroup.DECORATIONS);
	public static final Item SUNFLOWER = register(new TallBlockItem(Blocks.field_10583, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item LILAC = register(new TallBlockItem(Blocks.field_10378, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item ROSE_BUSH = register(new TallBlockItem(Blocks.field_10430, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item PEONY = register(new TallBlockItem(Blocks.field_10003, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item TALL_GRASS = register(new TallBlockItem(Blocks.field_10214, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item LARGE_FERN = register(new TallBlockItem(Blocks.field_10313, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item WHITE_STAINED_GLASS = register(Blocks.field_10087, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_STAINED_GLASS = register(Blocks.field_10227, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_STAINED_GLASS = register(Blocks.field_10574, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_STAINED_GLASS = register(Blocks.field_10271, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_STAINED_GLASS = register(Blocks.field_10049, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_STAINED_GLASS = register(Blocks.field_10157, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_STAINED_GLASS = register(Blocks.field_10317, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_STAINED_GLASS = register(Blocks.field_10555, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_STAINED_GLASS = register(Blocks.field_9996, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_STAINED_GLASS = register(Blocks.field_10248, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_STAINED_GLASS = register(Blocks.field_10399, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_STAINED_GLASS = register(Blocks.field_10060, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_STAINED_GLASS = register(Blocks.field_10073, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_STAINED_GLASS = register(Blocks.field_10357, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_STAINED_GLASS = register(Blocks.field_10272, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_STAINED_GLASS = register(Blocks.field_9997, ItemGroup.BUILDING_BLOCKS);
	public static final Item WHITE_STAINED_GLASS_PANE = register(Blocks.field_9991, ItemGroup.DECORATIONS);
	public static final Item ORANGE_STAINED_GLASS_PANE = register(Blocks.field_10496, ItemGroup.DECORATIONS);
	public static final Item MAGENTA_STAINED_GLASS_PANE = register(Blocks.field_10469, ItemGroup.DECORATIONS);
	public static final Item LIGHT_BLUE_STAINED_GLASS_PANE = register(Blocks.field_10193, ItemGroup.DECORATIONS);
	public static final Item YELLOW_STAINED_GLASS_PANE = register(Blocks.field_10578, ItemGroup.DECORATIONS);
	public static final Item LIME_STAINED_GLASS_PANE = register(Blocks.field_10305, ItemGroup.DECORATIONS);
	public static final Item PINK_STAINED_GLASS_PANE = register(Blocks.field_10565, ItemGroup.DECORATIONS);
	public static final Item GRAY_STAINED_GLASS_PANE = register(Blocks.field_10077, ItemGroup.DECORATIONS);
	public static final Item LIGHT_GRAY_STAINED_GLASS_PANE = register(Blocks.field_10129, ItemGroup.DECORATIONS);
	public static final Item CYAN_STAINED_GLASS_PANE = register(Blocks.field_10355, ItemGroup.DECORATIONS);
	public static final Item PURPLE_STAINED_GLASS_PANE = register(Blocks.field_10152, ItemGroup.DECORATIONS);
	public static final Item BLUE_STAINED_GLASS_PANE = register(Blocks.field_9982, ItemGroup.DECORATIONS);
	public static final Item BROWN_STAINED_GLASS_PANE = register(Blocks.field_10163, ItemGroup.DECORATIONS);
	public static final Item GREEN_STAINED_GLASS_PANE = register(Blocks.field_10419, ItemGroup.DECORATIONS);
	public static final Item RED_STAINED_GLASS_PANE = register(Blocks.field_10118, ItemGroup.DECORATIONS);
	public static final Item BLACK_STAINED_GLASS_PANE = register(Blocks.field_10070, ItemGroup.DECORATIONS);
	public static final Item PRISMARINE = register(Blocks.field_10135, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_BRICKS = register(Blocks.field_10006, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_PRISMARINE = register(Blocks.field_10297, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_STAIRS = register(Blocks.field_10350, ItemGroup.BUILDING_BLOCKS);
	public static final Item PRISMARINE_BRICK_STAIRS = register(Blocks.field_10190, ItemGroup.BUILDING_BLOCKS);
	public static final Item DARK_PRISMARINE_STAIRS = register(Blocks.field_10130, ItemGroup.BUILDING_BLOCKS);
	public static final Item SEA_LANTERN = register(Blocks.field_10174, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_SANDSTONE = register(Blocks.field_10344, ItemGroup.BUILDING_BLOCKS);
	public static final Item CHISELED_RED_SANDSTONE = register(Blocks.field_10117, ItemGroup.BUILDING_BLOCKS);
	public static final Item CUT_RED_SANDSTONE = register(Blocks.field_10518, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_SANDSTONE_STAIRS = register(Blocks.field_10420, ItemGroup.BUILDING_BLOCKS);
	public static final Item REPEATING_COMMAND_BLOCK = register(new CommandBlockItem(Blocks.field_10263, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item CHAIN_COMMAND_BLOCK = register(new CommandBlockItem(Blocks.field_10395, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item MAGMA_BLOCK = register(Blocks.field_10092, ItemGroup.BUILDING_BLOCKS);
	public static final Item NETHER_WART_BLOCK = register(Blocks.field_10541, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_NETHER_BRICKS = register(Blocks.field_9986, ItemGroup.BUILDING_BLOCKS);
	public static final Item BONE_BLOCK = register(Blocks.field_10166, ItemGroup.BUILDING_BLOCKS);
	public static final Item STRUCTURE_VOID = register(Blocks.field_10369);
	public static final Item OBSERVER = register(Blocks.field_10282, ItemGroup.REDSTONE);
	public static final Item SHULKER_BOX = register(new BlockItem(Blocks.field_10603, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item WHITE_SHULKER_BOX = register(new BlockItem(Blocks.field_10199, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item ORANGE_SHULKER_BOX = register(new BlockItem(Blocks.field_10407, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item MAGENTA_SHULKER_BOX = register(new BlockItem(Blocks.field_10063, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIGHT_BLUE_SHULKER_BOX = register(new BlockItem(Blocks.field_10203, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item YELLOW_SHULKER_BOX = register(new BlockItem(Blocks.field_10600, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIME_SHULKER_BOX = register(new BlockItem(Blocks.field_10275, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item PINK_SHULKER_BOX = register(new BlockItem(Blocks.field_10051, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item GRAY_SHULKER_BOX = register(new BlockItem(Blocks.field_10140, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIGHT_GRAY_SHULKER_BOX = register(new BlockItem(Blocks.field_10320, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item CYAN_SHULKER_BOX = register(new BlockItem(Blocks.field_10532, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item PURPLE_SHULKER_BOX = register(new BlockItem(Blocks.field_10268, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BLUE_SHULKER_BOX = register(new BlockItem(Blocks.field_10605, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BROWN_SHULKER_BOX = register(new BlockItem(Blocks.field_10373, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item GREEN_SHULKER_BOX = register(new BlockItem(Blocks.field_10055, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item RED_SHULKER_BOX = register(new BlockItem(Blocks.field_10068, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BLACK_SHULKER_BOX = register(new BlockItem(Blocks.field_10371, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item WHITE_GLAZED_TERRACOTTA = register(Blocks.field_10595, ItemGroup.DECORATIONS);
	public static final Item ORANGE_GLAZED_TERRACOTTA = register(Blocks.field_10280, ItemGroup.DECORATIONS);
	public static final Item MAGENTA_GLAZED_TERRACOTTA = register(Blocks.field_10538, ItemGroup.DECORATIONS);
	public static final Item LIGHT_BLUE_GLAZED_TERRACOTTA = register(Blocks.field_10345, ItemGroup.DECORATIONS);
	public static final Item YELLOW_GLAZED_TERRACOTTA = register(Blocks.field_10096, ItemGroup.DECORATIONS);
	public static final Item LIME_GLAZED_TERRACOTTA = register(Blocks.field_10046, ItemGroup.DECORATIONS);
	public static final Item PINK_GLAZED_TERRACOTTA = register(Blocks.field_10567, ItemGroup.DECORATIONS);
	public static final Item GRAY_GLAZED_TERRACOTTA = register(Blocks.field_10220, ItemGroup.DECORATIONS);
	public static final Item LIGHT_GRAY_GLAZED_TERRACOTTA = register(Blocks.field_10052, ItemGroup.DECORATIONS);
	public static final Item CYAN_GLAZED_TERRACOTTA = register(Blocks.field_10078, ItemGroup.DECORATIONS);
	public static final Item PURPLE_GLAZED_TERRACOTTA = register(Blocks.field_10426, ItemGroup.DECORATIONS);
	public static final Item BLUE_GLAZED_TERRACOTTA = register(Blocks.field_10550, ItemGroup.DECORATIONS);
	public static final Item BROWN_GLAZED_TERRACOTTA = register(Blocks.field_10004, ItemGroup.DECORATIONS);
	public static final Item GREEN_GLAZED_TERRACOTTA = register(Blocks.field_10475, ItemGroup.DECORATIONS);
	public static final Item RED_GLAZED_TERRACOTTA = register(Blocks.field_10383, ItemGroup.DECORATIONS);
	public static final Item BLACK_GLAZED_TERRACOTTA = register(Blocks.field_10501, ItemGroup.DECORATIONS);
	public static final Item WHITE_CONCRETE = register(Blocks.field_10107, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_CONCRETE = register(Blocks.field_10210, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_CONCRETE = register(Blocks.field_10585, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_CONCRETE = register(Blocks.field_10242, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_CONCRETE = register(Blocks.field_10542, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_CONCRETE = register(Blocks.field_10421, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_CONCRETE = register(Blocks.field_10434, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_CONCRETE = register(Blocks.field_10038, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_CONCRETE = register(Blocks.field_10172, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_CONCRETE = register(Blocks.field_10308, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_CONCRETE = register(Blocks.field_10206, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_CONCRETE = register(Blocks.field_10011, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_CONCRETE = register(Blocks.field_10439, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_CONCRETE = register(Blocks.field_10367, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_CONCRETE = register(Blocks.field_10058, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_CONCRETE = register(Blocks.field_10458, ItemGroup.BUILDING_BLOCKS);
	public static final Item WHITE_CONCRETE_POWDER = register(Blocks.field_10197, ItemGroup.BUILDING_BLOCKS);
	public static final Item ORANGE_CONCRETE_POWDER = register(Blocks.field_10022, ItemGroup.BUILDING_BLOCKS);
	public static final Item MAGENTA_CONCRETE_POWDER = register(Blocks.field_10300, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_BLUE_CONCRETE_POWDER = register(Blocks.field_10321, ItemGroup.BUILDING_BLOCKS);
	public static final Item YELLOW_CONCRETE_POWDER = register(Blocks.field_10145, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIME_CONCRETE_POWDER = register(Blocks.field_10133, ItemGroup.BUILDING_BLOCKS);
	public static final Item PINK_CONCRETE_POWDER = register(Blocks.field_10522, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRAY_CONCRETE_POWDER = register(Blocks.field_10353, ItemGroup.BUILDING_BLOCKS);
	public static final Item LIGHT_GRAY_CONCRETE_POWDER = register(Blocks.field_10628, ItemGroup.BUILDING_BLOCKS);
	public static final Item CYAN_CONCRETE_POWDER = register(Blocks.field_10233, ItemGroup.BUILDING_BLOCKS);
	public static final Item PURPLE_CONCRETE_POWDER = register(Blocks.field_10404, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLUE_CONCRETE_POWDER = register(Blocks.field_10456, ItemGroup.BUILDING_BLOCKS);
	public static final Item BROWN_CONCRETE_POWDER = register(Blocks.field_10023, ItemGroup.BUILDING_BLOCKS);
	public static final Item GREEN_CONCRETE_POWDER = register(Blocks.field_10529, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_CONCRETE_POWDER = register(Blocks.field_10287, ItemGroup.BUILDING_BLOCKS);
	public static final Item BLACK_CONCRETE_POWDER = register(Blocks.field_10506, ItemGroup.BUILDING_BLOCKS);
	public static final Item TURTLE_EGG = register(Blocks.field_10195, ItemGroup.MISC);
	public static final Item DEAD_TUBE_CORAL_BLOCK = register(Blocks.field_10614, ItemGroup.BUILDING_BLOCKS);
	public static final Item DEAD_BRAIN_CORAL_BLOCK = register(Blocks.field_10264, ItemGroup.BUILDING_BLOCKS);
	public static final Item DEAD_BUBBLE_CORAL_BLOCK = register(Blocks.field_10396, ItemGroup.BUILDING_BLOCKS);
	public static final Item DEAD_FIRE_CORAL_BLOCK = register(Blocks.field_10111, ItemGroup.BUILDING_BLOCKS);
	public static final Item DEAD_HORN_CORAL_BLOCK = register(Blocks.field_10488, ItemGroup.BUILDING_BLOCKS);
	public static final Item TUBE_CORAL_BLOCK = register(Blocks.field_10309, ItemGroup.BUILDING_BLOCKS);
	public static final Item BRAIN_CORAL_BLOCK = register(Blocks.field_10629, ItemGroup.BUILDING_BLOCKS);
	public static final Item BUBBLE_CORAL_BLOCK = register(Blocks.field_10000, ItemGroup.BUILDING_BLOCKS);
	public static final Item FIRE_CORAL_BLOCK = register(Blocks.field_10516, ItemGroup.BUILDING_BLOCKS);
	public static final Item HORN_CORAL_BLOCK = register(Blocks.field_10464, ItemGroup.BUILDING_BLOCKS);
	public static final Item TUBE_CORAL = register(Blocks.field_10125, ItemGroup.DECORATIONS);
	public static final Item BRAIN_CORAL = register(Blocks.field_10339, ItemGroup.DECORATIONS);
	public static final Item BUBBLE_CORAL = register(Blocks.field_10134, ItemGroup.DECORATIONS);
	public static final Item FIRE_CORAL = register(Blocks.field_10618, ItemGroup.DECORATIONS);
	public static final Item HORN_CORAL = register(Blocks.field_10169, ItemGroup.DECORATIONS);
	public static final Item DEAD_BRAIN_CORAL = register(Blocks.field_10572, ItemGroup.DECORATIONS);
	public static final Item DEAD_BUBBLE_CORAL = register(Blocks.field_10296, ItemGroup.DECORATIONS);
	public static final Item DEAD_FIRE_CORAL = register(Blocks.field_10579, ItemGroup.DECORATIONS);
	public static final Item DEAD_HORN_CORAL = register(Blocks.field_10032, ItemGroup.DECORATIONS);
	public static final Item DEAD_TUBE_CORAL = register(Blocks.field_10082, ItemGroup.DECORATIONS);
	public static final Item TUBE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10053, Blocks.field_10584, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item BRAIN_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10079, Blocks.field_10186, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item BUBBLE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10427, Blocks.field_10447, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item FIRE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10551, Blocks.field_10498, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item HORN_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10005, Blocks.field_9976, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_TUBE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10448, Blocks.field_10347, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_BRAIN_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10097, Blocks.field_10116, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_BUBBLE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10047, Blocks.field_10094, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_FIRE_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10568, Blocks.field_10557, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item DEAD_HORN_CORAL_FAN = register(
		new WallStandingBlockItem(Blocks.field_10221, Blocks.field_10239, new Item.Settings().group(ItemGroup.DECORATIONS))
	);
	public static final Item BLUE_ICE = register(Blocks.field_10384, ItemGroup.BUILDING_BLOCKS);
	public static final Item CONDUIT = register(new BlockItem(Blocks.field_10502, new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.field_8903)));
	public static final Item POLISHED_GRANITE_STAIRS = register(Blocks.field_10435, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_RED_SANDSTONE_STAIRS = register(Blocks.field_10039, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_STONE_BRICK_STAIRS = register(Blocks.field_10173, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_DIORITE_STAIRS = register(Blocks.field_10310, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_COBBLESTONE_STAIRS = register(Blocks.field_10207, ItemGroup.BUILDING_BLOCKS);
	public static final Item END_STONE_BRICK_STAIRS = register(Blocks.field_10012, ItemGroup.BUILDING_BLOCKS);
	public static final Item STONE_STAIRS = register(Blocks.field_10440, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_SANDSTONE_STAIRS = register(Blocks.field_10549, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_QUARTZ_STAIRS = register(Blocks.field_10245, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRANITE_STAIRS = register(Blocks.field_10607, ItemGroup.BUILDING_BLOCKS);
	public static final Item ANDESITE_STAIRS = register(Blocks.field_10386, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_NETHER_BRICK_STAIRS = register(Blocks.field_10497, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_ANDESITE_STAIRS = register(Blocks.field_9994, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIORITE_STAIRS = register(Blocks.field_10216, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_GRANITE_SLAB = register(Blocks.field_10329, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_RED_SANDSTONE_SLAB = register(Blocks.field_10283, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_STONE_BRICK_SLAB = register(Blocks.field_10024, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_DIORITE_SLAB = register(Blocks.field_10412, ItemGroup.BUILDING_BLOCKS);
	public static final Item MOSSY_COBBLESTONE_SLAB = register(Blocks.field_10405, ItemGroup.BUILDING_BLOCKS);
	public static final Item END_STONE_BRICK_SLAB = register(Blocks.field_10064, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_SANDSTONE_SLAB = register(Blocks.field_10262, ItemGroup.BUILDING_BLOCKS);
	public static final Item SMOOTH_QUARTZ_SLAB = register(Blocks.field_10601, ItemGroup.BUILDING_BLOCKS);
	public static final Item GRANITE_SLAB = register(Blocks.field_10189, ItemGroup.BUILDING_BLOCKS);
	public static final Item ANDESITE_SLAB = register(Blocks.field_10016, ItemGroup.BUILDING_BLOCKS);
	public static final Item RED_NETHER_BRICK_SLAB = register(Blocks.field_10478, ItemGroup.BUILDING_BLOCKS);
	public static final Item POLISHED_ANDESITE_SLAB = register(Blocks.field_10322, ItemGroup.BUILDING_BLOCKS);
	public static final Item DIORITE_SLAB = register(Blocks.field_10507, ItemGroup.BUILDING_BLOCKS);
	public static final Item SCAFFOLDING = register(new ScaffoldingItem(Blocks.field_16492, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item IRON_DOOR = register(new TallBlockItem(Blocks.field_9973, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item OAK_DOOR = register(new TallBlockItem(Blocks.field_10149, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item SPRUCE_DOOR = register(new TallBlockItem(Blocks.field_10521, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item BIRCH_DOOR = register(new TallBlockItem(Blocks.field_10352, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item JUNGLE_DOOR = register(new TallBlockItem(Blocks.field_10627, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item ACACIA_DOOR = register(new TallBlockItem(Blocks.field_10232, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item DARK_OAK_DOOR = register(new TallBlockItem(Blocks.field_10403, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item REPEATER = register(Blocks.field_10450, ItemGroup.REDSTONE);
	public static final Item COMPARATOR = register(Blocks.field_10377, ItemGroup.REDSTONE);
	public static final Item STRUCTURE_BLOCK = register(new CommandBlockItem(Blocks.field_10465, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item JIGSAW = register(new CommandBlockItem(Blocks.field_16540, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item COMPOSTER = register(Blocks.field_17563, ItemGroup.MISC);
	public static final Item field_8090 = register(
		"turtle_helmet", new ArmorItem(ArmorMaterials.field_7890, EquipmentSlot.field_6169, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8161 = register("scute", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8699 = register(
		"iron_shovel", new ShovelItem(ToolMaterials.field_8923, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item field_8403 = register("iron_pickaxe", new PickaxeItem(ToolMaterials.field_8923, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8475 = register("iron_axe", new AxeItem(ToolMaterials.field_8923, 6.0F, -3.1F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8884 = register("flint_and_steel", new FlintAndSteelItem(new Item.Settings().maxDamage(64).group(ItemGroup.TOOLS)));
	public static final Item field_8279 = register("apple", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.APPLE)));
	public static final Item field_8102 = register("bow", new BowItem(new Item.Settings().maxDamage(384).group(ItemGroup.COMBAT)));
	public static final Item field_8107 = register("arrow", new ArrowItem(new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item field_8713 = register("coal", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8665 = register("charcoal", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8477 = register("diamond", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8620 = register("iron_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8695 = register("gold_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8371 = register("iron_sword", new SwordItem(ToolMaterials.field_8923, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item field_8091 = register("wooden_sword", new SwordItem(ToolMaterials.field_8922, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item field_8876 = register(
		"wooden_shovel", new ShovelItem(ToolMaterials.field_8922, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item field_8647 = register(
		"wooden_pickaxe", new PickaxeItem(ToolMaterials.field_8922, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item field_8406 = register("wooden_axe", new AxeItem(ToolMaterials.field_8922, 6.0F, -3.2F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8528 = register("stone_sword", new SwordItem(ToolMaterials.field_8927, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item field_8776 = register(
		"stone_shovel", new ShovelItem(ToolMaterials.field_8927, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item field_8387 = register(
		"stone_pickaxe", new PickaxeItem(ToolMaterials.field_8927, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item field_8062 = register("stone_axe", new AxeItem(ToolMaterials.field_8927, 7.0F, -3.2F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8802 = register("diamond_sword", new SwordItem(ToolMaterials.field_8930, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item field_8250 = register(
		"diamond_shovel", new ShovelItem(ToolMaterials.field_8930, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item field_8377 = register(
		"diamond_pickaxe", new PickaxeItem(ToolMaterials.field_8930, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item field_8556 = register("diamond_axe", new AxeItem(ToolMaterials.field_8930, 5.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8600 = register("stick", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8428 = register("bowl", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8208 = register(
		"mushroom_stew", new MushroomStewItem(new Item.Settings().maxCount(1).group(ItemGroup.FOOD).food(FoodComponents.MUSHROOM_STEW))
	);
	public static final Item field_8845 = register("golden_sword", new SwordItem(ToolMaterials.field_8929, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item field_8322 = register(
		"golden_shovel", new ShovelItem(ToolMaterials.field_8929, 1.5F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item field_8335 = register(
		"golden_pickaxe", new PickaxeItem(ToolMaterials.field_8929, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS))
	);
	public static final Item field_8825 = register("golden_axe", new AxeItem(ToolMaterials.field_8929, 6.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8276 = register("string", new AliasedBlockItem(Blocks.field_10589, new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8153 = register("feather", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8054 = register("gunpowder", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8167 = register("wooden_hoe", new HoeItem(ToolMaterials.field_8922, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8431 = register("stone_hoe", new HoeItem(ToolMaterials.field_8927, -2.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8609 = register("iron_hoe", new HoeItem(ToolMaterials.field_8923, -1.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8527 = register("diamond_hoe", new HoeItem(ToolMaterials.field_8930, 0.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8303 = register("golden_hoe", new HoeItem(ToolMaterials.field_8929, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8317 = register("wheat_seeds", new AliasedBlockItem(Blocks.field_10293, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8861 = register("wheat", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8229 = register("bread", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.BREAD)));
	public static final Item field_8267 = register(
		"leather_helmet", new DyeableArmorItem(ArmorMaterials.field_7897, EquipmentSlot.field_6169, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8577 = register(
		"leather_chestplate", new DyeableArmorItem(ArmorMaterials.field_7897, EquipmentSlot.field_6174, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8570 = register(
		"leather_leggings", new DyeableArmorItem(ArmorMaterials.field_7897, EquipmentSlot.field_6172, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8370 = register(
		"leather_boots", new DyeableArmorItem(ArmorMaterials.field_7897, EquipmentSlot.field_6166, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8283 = register(
		"chainmail_helmet", new ArmorItem(ArmorMaterials.CHAIN, EquipmentSlot.field_6169, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8873 = register(
		"chainmail_chestplate", new ArmorItem(ArmorMaterials.CHAIN, EquipmentSlot.field_6174, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8218 = register(
		"chainmail_leggings", new ArmorItem(ArmorMaterials.CHAIN, EquipmentSlot.field_6172, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8313 = register(
		"chainmail_boots", new ArmorItem(ArmorMaterials.CHAIN, EquipmentSlot.field_6166, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8743 = register(
		"iron_helmet", new ArmorItem(ArmorMaterials.field_7892, EquipmentSlot.field_6169, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8523 = register(
		"iron_chestplate", new ArmorItem(ArmorMaterials.field_7892, EquipmentSlot.field_6174, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8396 = register(
		"iron_leggings", new ArmorItem(ArmorMaterials.field_7892, EquipmentSlot.field_6172, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8660 = register(
		"iron_boots", new ArmorItem(ArmorMaterials.field_7892, EquipmentSlot.field_6166, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8805 = register(
		"diamond_helmet", new ArmorItem(ArmorMaterials.field_7889, EquipmentSlot.field_6169, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8058 = register(
		"diamond_chestplate", new ArmorItem(ArmorMaterials.field_7889, EquipmentSlot.field_6174, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8348 = register(
		"diamond_leggings", new ArmorItem(ArmorMaterials.field_7889, EquipmentSlot.field_6172, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8285 = register(
		"diamond_boots", new ArmorItem(ArmorMaterials.field_7889, EquipmentSlot.field_6166, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8862 = register(
		"golden_helmet", new ArmorItem(ArmorMaterials.field_7895, EquipmentSlot.field_6169, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8678 = register(
		"golden_chestplate", new ArmorItem(ArmorMaterials.field_7895, EquipmentSlot.field_6174, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8416 = register(
		"golden_leggings", new ArmorItem(ArmorMaterials.field_7895, EquipmentSlot.field_6172, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8753 = register(
		"golden_boots", new ArmorItem(ArmorMaterials.field_7895, EquipmentSlot.field_6166, new Item.Settings().group(ItemGroup.COMBAT))
	);
	public static final Item field_8145 = register("flint", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8389 = register("porkchop", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.PORKCHOP)));
	public static final Item field_8261 = register("cooked_porkchop", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_PORKCHOP)));
	public static final Item field_8892 = register("painting", new DecorationItem(EntityType.field_6120, new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item field_8463 = register(
		"golden_apple", new Item(new Item.Settings().group(ItemGroup.FOOD).rarity(Rarity.field_8903).food(FoodComponents.GOLDEN_APPLE))
	);
	public static final Item field_8367 = register(
		"enchanted_golden_apple",
		new EnchantedGoldenAppleItem(new Item.Settings().group(ItemGroup.FOOD).rarity(Rarity.field_8904).food(FoodComponents.ENCHANTED_GOLDEN_APPLE))
	);
	public static final Item field_8788 = register(
		"oak_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.field_10121, Blocks.field_10187)
	);
	public static final Item field_8111 = register(
		"spruce_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.field_10411, Blocks.field_10088)
	);
	public static final Item field_8422 = register(
		"birch_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.field_10231, Blocks.field_10391)
	);
	public static final Item field_8867 = register(
		"jungle_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.field_10544, Blocks.field_10587)
	);
	public static final Item field_8203 = register(
		"acacia_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.field_10284, Blocks.field_10401)
	);
	public static final Item field_8496 = register(
		"dark_oak_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), Blocks.field_10330, Blocks.field_10265)
	);
	public static final Item field_8550 = register("bucket", new BucketItem(Fluids.field_15906, new Item.Settings().maxCount(16).group(ItemGroup.MISC)));
	public static final Item field_8705 = register(
		"water_bucket", new BucketItem(Fluids.WATER, new Item.Settings().recipeRemainder(field_8550).maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item field_8187 = register(
		"lava_bucket", new BucketItem(Fluids.LAVA, new Item.Settings().recipeRemainder(field_8550).maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item field_8045 = register(
		"minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7674, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8175 = register("saddle", new SaddleItem(new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)));
	public static final Item field_8725 = register("redstone", new AliasedBlockItem(Blocks.field_10091, new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item field_8543 = register("snowball", new SnowballItem(new Item.Settings().maxCount(16).group(ItemGroup.MISC)));
	public static final Item field_8533 = register(
		"oak_boat", new BoatItem(BoatEntity.Type.field_7727, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8745 = register("leather", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8103 = register(
		"milk_bucket", new MilkBucketItem(new Item.Settings().recipeRemainder(field_8550).maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item field_8108 = register(
		"pufferfish_bucket", new FishBucketItem(EntityType.field_6062, Fluids.WATER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item field_8714 = register(
		"salmon_bucket", new FishBucketItem(EntityType.field_6073, Fluids.WATER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item field_8666 = register(
		"cod_bucket", new FishBucketItem(EntityType.field_6070, Fluids.WATER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item field_8478 = register(
		"tropical_fish_bucket", new FishBucketItem(EntityType.field_6111, Fluids.WATER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item field_8621 = register("brick", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8696 = register("clay_ball", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item SUGAR_CANE = register(Blocks.field_10424, ItemGroup.MISC);
	public static final Item KELP = register(Blocks.field_9993, ItemGroup.MISC);
	public static final Item DRIED_KELP_BLOCK = register(Blocks.field_10342, ItemGroup.BUILDING_BLOCKS);
	public static final Item BAMBOO = register(Blocks.field_10211, ItemGroup.MATERIALS);
	public static final Item field_8407 = register("paper", new Item(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8529 = register("book", new BookItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8777 = register("slime_ball", new Item(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8388 = register(
		"chest_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7678, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8063 = register(
		"furnace_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7679, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8803 = register("egg", new EggItem(new Item.Settings().maxCount(16).group(ItemGroup.MATERIALS)));
	public static final Item field_8251 = register("compass", new CompassItem(new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8378 = register("fishing_rod", new FishingRodItem(new Item.Settings().maxDamage(64).group(ItemGroup.TOOLS)));
	public static final Item field_8557 = register("clock", new ClockItem(new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8601 = register("glowstone_dust", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8429 = register("cod", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COD)));
	public static final Item field_8209 = register("salmon", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.SALMON)));
	public static final Item field_8846 = register("tropical_fish", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.TROPICAL_FISH)));
	public static final Item field_8323 = register("pufferfish", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.PUFFERFISH)));
	public static final Item field_8373 = register("cooked_cod", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_COD)));
	public static final Item field_8509 = register("cooked_salmon", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_SALMON)));
	public static final Item field_8794 = register("ink_sac", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8264 = register("red_dye", new DyeItem(DyeColor.field_7964, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8408 = register("green_dye", new DyeItem(DyeColor.field_7942, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8116 = register("cocoa_beans", new AliasedBlockItem(Blocks.field_10302, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8759 = register("lapis_lazuli", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8296 = register("purple_dye", new DyeItem(DyeColor.field_7945, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8632 = register("cyan_dye", new DyeItem(DyeColor.field_7955, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8851 = register("light_gray_dye", new DyeItem(DyeColor.field_7967, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8298 = register("gray_dye", new DyeItem(DyeColor.field_7944, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8330 = register("pink_dye", new DyeItem(DyeColor.field_7954, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8131 = register("lime_dye", new DyeItem(DyeColor.field_7961, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8192 = register("yellow_dye", new DyeItem(DyeColor.field_7947, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8273 = register("light_blue_dye", new DyeItem(DyeColor.field_7951, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8669 = register("magenta_dye", new DyeItem(DyeColor.field_7958, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8492 = register("orange_dye", new DyeItem(DyeColor.field_7946, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8324 = register("bone_meal", new BoneMealItem(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8345 = register("blue_dye", new DyeItem(DyeColor.field_7966, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8099 = register("brown_dye", new DyeItem(DyeColor.field_7957, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8226 = register("black_dye", new DyeItem(DyeColor.field_7963, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8446 = register("white_dye", new DyeItem(DyeColor.field_7952, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8606 = register("bone", new Item(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8479 = register("sugar", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item CAKE = register(new BlockItem(Blocks.field_10183, new Item.Settings().maxCount(1).group(ItemGroup.FOOD)));
	public static final Item WHITE_BED = register(new BedItem(Blocks.field_10120, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item ORANGE_BED = register(new BedItem(Blocks.field_10410, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item MAGENTA_BED = register(new BedItem(Blocks.field_10230, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIGHT_BLUE_BED = register(new BedItem(Blocks.field_10621, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item YELLOW_BED = register(new BedItem(Blocks.field_10356, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIME_BED = register(new BedItem(Blocks.field_10180, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item PINK_BED = register(new BedItem(Blocks.field_10610, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item GRAY_BED = register(new BedItem(Blocks.field_10141, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item LIGHT_GRAY_BED = register(new BedItem(Blocks.field_10326, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item CYAN_BED = register(new BedItem(Blocks.field_10109, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item PURPLE_BED = register(new BedItem(Blocks.field_10019, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BLUE_BED = register(new BedItem(Blocks.field_10527, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BROWN_BED = register(new BedItem(Blocks.field_10288, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item GREEN_BED = register(new BedItem(Blocks.field_10561, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item RED_BED = register(new BedItem(Blocks.field_10069, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item BLACK_BED = register(new BedItem(Blocks.field_10461, new Item.Settings().maxCount(1).group(ItemGroup.DECORATIONS)));
	public static final Item field_8423 = register("cookie", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKIE)));
	public static final Item field_8204 = register("filled_map", new FilledMapItem(new Item.Settings()));
	public static final Item field_8868 = register("shears", new ShearsItem(new Item.Settings().maxDamage(238).group(ItemGroup.TOOLS)));
	public static final Item field_8497 = register("melon_slice", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.MELON_SLICE)));
	public static final Item field_8551 = register("dried_kelp", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.DRIED_KELP)));
	public static final Item field_8706 = register("pumpkin_seeds", new AliasedBlockItem(Blocks.field_9984, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8188 = register("melon_seeds", new AliasedBlockItem(Blocks.field_10168, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8046 = register("beef", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.BEEF)));
	public static final Item field_8176 = register("cooked_beef", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_BEEF)));
	public static final Item field_8726 = register("chicken", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.CHICKEN)));
	public static final Item field_8544 = register("cooked_chicken", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_CHICKEN)));
	public static final Item field_8511 = register("rotten_flesh", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.ROTTEN_FLESH)));
	public static final Item field_8634 = register("ender_pearl", new EnderPearlItem(new Item.Settings().maxCount(16).group(ItemGroup.MISC)));
	public static final Item field_8894 = register("blaze_rod", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8070 = register("ghast_tear", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item field_8397 = register("gold_nugget", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8790 = register("nether_wart", new AliasedBlockItem(Blocks.field_9974, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8574 = register("potion", new PotionItem(new Item.Settings().maxCount(1).group(ItemGroup.BREWING)));
	public static final Item field_8469 = register("glass_bottle", new GlassBottleItem(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item field_8680 = register("spider_eye", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.SPIDER_EYE)));
	public static final Item field_8711 = register("fermented_spider_eye", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item field_8183 = register("blaze_powder", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item field_8135 = register("magma_cream", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item BREWING_STAND = register(Blocks.field_10333, ItemGroup.BREWING);
	public static final Item CAULDRON = register(Blocks.field_10593, ItemGroup.BREWING);
	public static final Item field_8449 = register("ender_eye", new EnderEyeItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8597 = register("glistering_melon_slice", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item field_8727 = register(
		"bat_spawn_egg", new SpawnEggItem(EntityType.field_6108, 4996656, 986895, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8154 = register(
		"blaze_spawn_egg", new SpawnEggItem(EntityType.field_6099, 16167425, 16775294, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_16314 = register(
		"cat_spawn_egg", new SpawnEggItem(EntityType.field_16281, 15714446, 9794134, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8068 = register(
		"cave_spider_spawn_egg", new SpawnEggItem(EntityType.field_6084, 803406, 11013646, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8835 = register(
		"chicken_spawn_egg", new SpawnEggItem(EntityType.field_6132, 10592673, 16711680, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8661 = register(
		"cod_spawn_egg", new SpawnEggItem(EntityType.field_6070, 12691306, 15058059, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8433 = register(
		"cow_spawn_egg", new SpawnEggItem(EntityType.field_6085, 4470310, 10592673, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8503 = register(
		"creeper_spawn_egg", new SpawnEggItem(EntityType.field_6046, 894731, 0, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8751 = register(
		"dolphin_spawn_egg", new SpawnEggItem(EntityType.field_6087, 2243405, 16382457, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8306 = register(
		"donkey_spawn_egg", new SpawnEggItem(EntityType.field_6067, 5457209, 8811878, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8083 = register(
		"drowned_spawn_egg", new SpawnEggItem(EntityType.field_6123, 9433559, 7969893, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8769 = register(
		"elder_guardian_spawn_egg", new SpawnEggItem(EntityType.field_6086, 13552826, 7632531, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8374 = register(
		"enderman_spawn_egg", new SpawnEggItem(EntityType.field_6091, 1447446, 0, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8510 = register(
		"endermite_spawn_egg", new SpawnEggItem(EntityType.field_6128, 1447446, 7237230, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8795 = register(
		"evoker_spawn_egg", new SpawnEggItem(EntityType.field_6090, 9804699, 1973274, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_18005 = register(
		"fox_spawn_egg", new SpawnEggItem(EntityType.field_17943, 14005919, 13396256, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8265 = register(
		"ghast_spawn_egg", new SpawnEggItem(EntityType.field_6107, 16382457, 12369084, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8409 = register(
		"guardian_spawn_egg", new SpawnEggItem(EntityType.field_6118, 5931634, 15826224, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8117 = register(
		"horse_spawn_egg", new SpawnEggItem(EntityType.field_6139, 12623485, 15656192, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8760 = register(
		"husk_spawn_egg", new SpawnEggItem(EntityType.field_6071, 7958625, 15125652, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8633 = register(
		"llama_spawn_egg", new SpawnEggItem(EntityType.field_6074, 12623485, 10051392, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8852 = register(
		"magma_cube_spawn_egg", new SpawnEggItem(EntityType.field_6102, 3407872, 16579584, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8299 = register(
		"mooshroom_spawn_egg", new SpawnEggItem(EntityType.field_6143, 10489616, 12040119, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8331 = register(
		"mule_spawn_egg", new SpawnEggItem(EntityType.field_6057, 1769984, 5321501, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8132 = register(
		"ocelot_spawn_egg", new SpawnEggItem(EntityType.field_6081, 15720061, 5653556, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8193 = register(
		"panda_spawn_egg", new SpawnEggItem(EntityType.field_6146, 15198183, 1776418, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8274 = register(
		"parrot_spawn_egg", new SpawnEggItem(EntityType.field_6104, 894731, 16711680, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8670 = register(
		"phantom_spawn_egg", new SpawnEggItem(EntityType.field_6078, 4411786, 8978176, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8493 = register(
		"pig_spawn_egg", new SpawnEggItem(EntityType.field_6093, 15771042, 14377823, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8325 = register(
		"pillager_spawn_egg", new SpawnEggItem(EntityType.field_6105, 5451574, 9804699, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8346 = register(
		"polar_bear_spawn_egg", new SpawnEggItem(EntityType.field_6042, 15921906, 9803152, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8100 = register(
		"pufferfish_spawn_egg", new SpawnEggItem(EntityType.field_6062, 16167425, 3654642, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8227 = register(
		"rabbit_spawn_egg", new SpawnEggItem(EntityType.field_6140, 10051392, 7555121, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8297 = register(
		"ravager_spawn_egg", new SpawnEggItem(EntityType.field_6134, 7697520, 5984329, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8447 = register(
		"salmon_spawn_egg", new SpawnEggItem(EntityType.field_6073, 10489616, 951412, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8607 = register(
		"sheep_spawn_egg", new SpawnEggItem(EntityType.field_6115, 15198183, 16758197, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8480 = register(
		"shulker_spawn_egg", new SpawnEggItem(EntityType.field_6109, 9725844, 5060690, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8564 = register(
		"silverfish_spawn_egg", new SpawnEggItem(EntityType.field_6125, 7237230, 3158064, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8300 = register(
		"skeleton_spawn_egg", new SpawnEggItem(EntityType.field_6137, 12698049, 4802889, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8232 = register(
		"skeleton_horse_spawn_egg", new SpawnEggItem(EntityType.field_6075, 6842447, 15066584, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8881 = register(
		"slime_spawn_egg", new SpawnEggItem(EntityType.field_6069, 5349438, 8306542, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8185 = register(
		"spider_spawn_egg", new SpawnEggItem(EntityType.field_6079, 3419431, 11013646, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8307 = register(
		"squid_spawn_egg", new SpawnEggItem(EntityType.field_6114, 2243405, 7375001, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8514 = register(
		"stray_spawn_egg", new SpawnEggItem(EntityType.field_6098, 6387319, 14543594, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_17731 = register(
		"trader_llama_spawn_egg", new SpawnEggItem(EntityType.field_17714, 15377456, 4547222, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8612 = register(
		"tropical_fish_spawn_egg", new SpawnEggItem(EntityType.field_6111, 15690005, 16775663, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8435 = register(
		"turtle_spawn_egg", new SpawnEggItem(EntityType.field_6113, 15198183, 44975, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8235 = register(
		"vex_spawn_egg", new SpawnEggItem(EntityType.field_6059, 8032420, 15265265, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8086 = register(
		"villager_spawn_egg", new SpawnEggItem(EntityType.field_6077, 5651507, 12422002, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8149 = register(
		"vindicator_spawn_egg", new SpawnEggItem(EntityType.field_6117, 9804699, 2580065, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_17732 = register(
		"wandering_trader_spawn_egg", new SpawnEggItem(EntityType.field_17713, 4547222, 15377456, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8254 = register(
		"witch_spawn_egg", new SpawnEggItem(EntityType.field_6145, 3407872, 5349438, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8832 = register(
		"wither_skeleton_spawn_egg", new SpawnEggItem(EntityType.field_6076, 1315860, 4672845, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8485 = register(
		"wolf_spawn_egg", new SpawnEggItem(EntityType.field_6055, 14144467, 13545366, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8441 = register(
		"zombie_spawn_egg", new SpawnEggItem(EntityType.field_6051, 44975, 7969893, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8728 = register(
		"zombie_horse_spawn_egg", new SpawnEggItem(EntityType.field_6048, 3232308, 9945732, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8093 = register(
		"zombie_pigman_spawn_egg", new SpawnEggItem(EntityType.field_6050, 15373203, 5009705, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8136 = register(
		"zombie_villager_spawn_egg", new SpawnEggItem(EntityType.field_6054, 5651507, 7969893, new Item.Settings().group(ItemGroup.MISC))
	);
	public static final Item field_8287 = register(
		"experience_bottle", new ExperienceBottleItem(new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.field_8907))
	);
	public static final Item field_8814 = register("fire_charge", new FireChargeItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8674 = register("writable_book", new WritableBookItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC)));
	public static final Item field_8360 = register("written_book", new WrittenBookItem(new Item.Settings().maxCount(16)));
	public static final Item field_8687 = register("emerald", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8143 = register("item_frame", new ItemFrameItem(new Item.Settings().group(ItemGroup.DECORATIONS)));
	public static final Item FLOWER_POT = register(Blocks.field_10495, ItemGroup.DECORATIONS);
	public static final Item field_8179 = register(
		"carrot", new AliasedBlockItem(Blocks.field_10609, new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.CARROT))
	);
	public static final Item field_8567 = register(
		"potato", new AliasedBlockItem(Blocks.field_10247, new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.POTATO))
	);
	public static final Item field_8512 = register("baked_potato", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.BAKED_POTATO)));
	public static final Item field_8635 = register("poisonous_potato", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.POISONOUS_POTATO)));
	public static final Item field_8895 = register("map", new EmptyMapItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8071 = register("golden_carrot", new Item(new Item.Settings().group(ItemGroup.BREWING).food(FoodComponents.GOLDEN_CARROT)));
	public static final Item SKELETON_SKULL = register(
		new WallStandingBlockItem(Blocks.field_10481, Blocks.field_10388, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item WITHER_SKELETON_SKULL = register(
		new WallStandingBlockItem(Blocks.field_10177, Blocks.field_10101, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item PLAYER_HEAD = register(
		new SkullItem(Blocks.field_10432, Blocks.field_10208, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item ZOMBIE_HEAD = register(
		new WallStandingBlockItem(Blocks.field_10241, Blocks.field_10581, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item CREEPER_HEAD = register(
		new WallStandingBlockItem(Blocks.field_10042, Blocks.field_10509, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item DRAGON_HEAD = register(
		new WallStandingBlockItem(Blocks.field_10337, Blocks.field_10472, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item field_8184 = register("carrot_on_a_stick", new CarrotOnAStickItem(new Item.Settings().maxDamage(25).group(ItemGroup.TRANSPORTATION)));
	public static final Item field_8137 = register("nether_star", new NetherStarItem(new Item.Settings().group(ItemGroup.MATERIALS).rarity(Rarity.field_8907)));
	public static final Item field_8741 = register("pumpkin_pie", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.PUMPKIN_PIE)));
	public static final Item field_8639 = register("firework_rocket", new FireworkItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8450 = register("firework_star", new FireworkChargeItem(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item field_8598 = register("enchanted_book", new EnchantedBookItem(new Item.Settings().maxCount(1).rarity(Rarity.field_8907)));
	public static final Item field_8729 = register("nether_brick", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8155 = register("quartz", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8069 = register(
		"tnt_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7675, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8836 = register(
		"hopper_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7677, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8662 = register("prismarine_shard", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8434 = register("prismarine_crystals", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8504 = register("rabbit", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.RABBIT)));
	public static final Item field_8752 = register("cooked_rabbit", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_RABBIT)));
	public static final Item field_8308 = register(
		"rabbit_stew", new MushroomStewItem(new Item.Settings().maxCount(1).group(ItemGroup.FOOD).food(FoodComponents.RABBIT_STEW))
	);
	public static final Item field_8073 = register("rabbit_foot", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item field_8245 = register("rabbit_hide", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8694 = register("armor_stand", new ArmorStandItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS)));
	public static final Item field_8578 = register("iron_horse_armor", new HorseArmorItem(5, "iron", new Item.Settings().maxCount(1).group(ItemGroup.MISC)));
	public static final Item field_8560 = register("golden_horse_armor", new HorseArmorItem(7, "gold", new Item.Settings().maxCount(1).group(ItemGroup.MISC)));
	public static final Item field_8807 = register("diamond_horse_armor", new HorseArmorItem(11, "diamond", new Item.Settings().maxCount(1).group(ItemGroup.MISC)));
	public static final Item field_18138 = register(
		"leather_horse_armor", new DyeableHorseArmorItem(3, "leather", new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item field_8719 = register("lead", new LeadItem(new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8448 = register("name_tag", new NameTagItem(new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item field_8220 = register(
		"command_block_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7681, new Item.Settings().maxCount(1))
	);
	public static final Item field_8748 = register("mutton", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.MUTTON)));
	public static final Item field_8347 = register("cooked_mutton", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_MUTTON)));
	public static final Item field_8539 = register(
		"white_banner", new BannerItem(Blocks.field_10154, Blocks.field_10202, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8824 = register(
		"orange_banner", new BannerItem(Blocks.field_10045, Blocks.field_10599, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8671 = register(
		"magenta_banner", new BannerItem(Blocks.field_10438, Blocks.field_10274, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8379 = register(
		"light_blue_banner", new BannerItem(Blocks.field_10452, Blocks.field_10050, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8049 = register(
		"yellow_banner", new BannerItem(Blocks.field_10547, Blocks.field_10139, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8778 = register(
		"lime_banner", new BannerItem(Blocks.field_10229, Blocks.field_10318, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8329 = register(
		"pink_banner", new BannerItem(Blocks.field_10612, Blocks.field_10531, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8617 = register(
		"gray_banner", new BannerItem(Blocks.field_10185, Blocks.field_10267, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8855 = register(
		"light_gray_banner", new BannerItem(Blocks.field_9985, Blocks.field_10604, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8629 = register(
		"cyan_banner", new BannerItem(Blocks.field_10165, Blocks.field_10372, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8405 = register(
		"purple_banner", new BannerItem(Blocks.field_10368, Blocks.field_10054, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8128 = register(
		"blue_banner", new BannerItem(Blocks.field_10281, Blocks.field_10067, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8124 = register(
		"brown_banner", new BannerItem(Blocks.field_10602, Blocks.field_10370, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8295 = register(
		"green_banner", new BannerItem(Blocks.field_10198, Blocks.field_10594, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8586 = register(
		"red_banner", new BannerItem(Blocks.field_10406, Blocks.field_10279, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8572 = register(
		"black_banner", new BannerItem(Blocks.field_10062, Blocks.field_10537, new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS))
	);
	public static final Item field_8301 = register("end_crystal", new EndCrystalItem(new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.field_8903)));
	public static final Item field_8233 = register(
		"chorus_fruit", new ChorusFruitItem(new Item.Settings().group(ItemGroup.MATERIALS).food(FoodComponents.CHORUS_FRUIT))
	);
	public static final Item field_8882 = register("popped_chorus_fruit", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8186 = register("beetroot", new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.BEETROOT)));
	public static final Item field_8309 = register("beetroot_seeds", new AliasedBlockItem(Blocks.field_10341, new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8515 = register(
		"beetroot_soup", new MushroomStewItem(new Item.Settings().maxCount(1).group(ItemGroup.FOOD).food(FoodComponents.BEETROOT_SOUP))
	);
	public static final Item field_8613 = register(
		"dragon_breath", new Item(new Item.Settings().recipeRemainder(field_8469).group(ItemGroup.BREWING).rarity(Rarity.field_8907))
	);
	public static final Item field_8436 = register("splash_potion", new SplashPotionItem(new Item.Settings().maxCount(1).group(ItemGroup.BREWING)));
	public static final Item field_8236 = register("spectral_arrow", new SpectralArrowItem(new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item field_8087 = register("tipped_arrow", new TippedArrowItem(new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item field_8150 = register("lingering_potion", new LingeringPotionItem(new Item.Settings().maxCount(1).group(ItemGroup.BREWING)));
	public static final Item field_8255 = register("shield", new ShieldItem(new Item.Settings().maxDamage(336).group(ItemGroup.COMBAT)));
	public static final Item field_8833 = register(
		"elytra", new ElytraItem(new Item.Settings().maxDamage(432).group(ItemGroup.TRANSPORTATION).rarity(Rarity.field_8907))
	);
	public static final Item field_8486 = register(
		"spruce_boat", new BoatItem(BoatEntity.Type.field_7728, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8442 = register(
		"birch_boat", new BoatItem(BoatEntity.Type.field_7729, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8730 = register(
		"jungle_boat", new BoatItem(BoatEntity.Type.field_7730, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8094 = register(
		"acacia_boat", new BoatItem(BoatEntity.Type.field_7725, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8138 = register(
		"dark_oak_boat", new BoatItem(BoatEntity.Type.field_7723, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8288 = register("totem_of_undying", new Item(new Item.Settings().maxCount(1).group(ItemGroup.COMBAT).rarity(Rarity.field_8907)));
	public static final Item field_8815 = register("shulker_shell", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8675 = register("iron_nugget", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8361 = register("knowledge_book", new KnowledgeBookItem(new Item.Settings().maxCount(1)));
	public static final Item field_8688 = register("debug_stick", new DebugStickItem(new Item.Settings().maxCount(1)));
	public static final Item field_8144 = register(
		"music_disc_13", new MusicDiscItem(1, SoundEvents.field_14592, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8075 = register(
		"music_disc_cat", new MusicDiscItem(2, SoundEvents.field_14744, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8425 = register(
		"music_disc_blocks", new MusicDiscItem(3, SoundEvents.field_14829, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8623 = register(
		"music_disc_chirp", new MusicDiscItem(4, SoundEvents.field_15039, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8502 = register(
		"music_disc_far", new MusicDiscItem(5, SoundEvents.field_14944, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8534 = register(
		"music_disc_mall", new MusicDiscItem(6, SoundEvents.field_15059, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8344 = register(
		"music_disc_mellohi", new MusicDiscItem(7, SoundEvents.field_15169, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8834 = register(
		"music_disc_stal", new MusicDiscItem(8, SoundEvents.field_14578, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8065 = register(
		"music_disc_strad", new MusicDiscItem(9, SoundEvents.field_14656, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8355 = register(
		"music_disc_ward", new MusicDiscItem(10, SoundEvents.field_14838, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8731 = register(
		"music_disc_11", new MusicDiscItem(11, SoundEvents.field_14654, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8806 = register(
		"music_disc_wait", new MusicDiscItem(12, SoundEvents.field_14759, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8547 = register("trident", new TridentItem(new Item.Settings().maxDamage(250).group(ItemGroup.COMBAT)));
	public static final Item field_8614 = register("phantom_membrane", new Item(new Item.Settings().group(ItemGroup.BREWING)));
	public static final Item field_8864 = register("nautilus_shell", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item field_8207 = register("heart_of_the_sea", new Item(new Item.Settings().group(ItemGroup.MATERIALS).rarity(Rarity.field_8907)));
	public static final Item field_8399 = register("crossbow", new CrossbowItem(new Item.Settings().maxCount(1).group(ItemGroup.COMBAT).maxDamage(326)));
	public static final Item field_8766 = register("suspicious_stew", new SuspiciousStewItem(new Item.Settings().maxCount(1).food(FoodComponents.SUSPICIOUS_STEW)));
	public static final Item LOOM = register(Blocks.field_10083, ItemGroup.DECORATIONS);
	public static final Item field_8498 = register(
		"flower_banner_pattern", new BannerPatternItem(BannerPattern.FLOWER, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item field_8573 = register(
		"creeper_banner_pattern", new BannerPatternItem(BannerPattern.CREEPER, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8907))
	);
	public static final Item field_8891 = register(
		"skull_banner_pattern", new BannerPatternItem(BannerPattern.SKULL, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8907))
	);
	public static final Item field_8159 = register(
		"mojang_banner_pattern", new BannerPatternItem(BannerPattern.MOJANG, new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.field_8904))
	);
	public static final Item field_18674 = register(
		"globe_banner_pattern", new BannerPatternItem(BannerPattern.GLOBE, new Item.Settings().maxCount(1).group(ItemGroup.MISC))
	);
	public static final Item BARREL = register(Blocks.field_16328, ItemGroup.DECORATIONS);
	public static final Item SMOKER = register(Blocks.field_16334, ItemGroup.DECORATIONS);
	public static final Item BLAST_FURNACE = register(Blocks.field_16333, ItemGroup.DECORATIONS);
	public static final Item CARTOGRAPHY_TABLE = register(Blocks.field_16336, ItemGroup.DECORATIONS);
	public static final Item FLETCHING_TABLE = register(Blocks.field_16331, ItemGroup.DECORATIONS);
	public static final Item GRINDSTONE = register(Blocks.field_16337, ItemGroup.DECORATIONS);
	public static final Item LECTERN = register(Blocks.field_16330, ItemGroup.REDSTONE);
	public static final Item SMITHING_TABLE = register(Blocks.field_16329, ItemGroup.DECORATIONS);
	public static final Item STONECUTTER = register(Blocks.field_16335, ItemGroup.DECORATIONS);
	public static final Item BELL = register(Blocks.field_16332, ItemGroup.DECORATIONS);
	public static final Item LANTERN = register(Blocks.field_16541, ItemGroup.DECORATIONS);
	public static final Item field_16998 = register(
		"sweet_berries", new AliasedBlockItem(Blocks.field_16999, new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.SWEET_BERRIES))
	);
	public static final Item CAMPFIRE = register(Blocks.field_17350, ItemGroup.DECORATIONS);

	private static Item register(Block block) {
		return register(new BlockItem(block, new Item.Settings()));
	}

	private static Item register(Block block, ItemGroup itemGroup) {
		return register(new BlockItem(block, new Item.Settings().group(itemGroup)));
	}

	private static Item register(BlockItem blockItem) {
		return register(blockItem.getBlock(), blockItem);
	}

	protected static Item register(Block block, Item item) {
		return register(Registry.BLOCK.getId(block), item);
	}

	private static Item register(String string, Item item) {
		return register(new Identifier(string), item);
	}

	private static Item register(Identifier identifier, Item item) {
		if (item instanceof BlockItem) {
			((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
		}

		return Registry.register(Registry.ITEM, identifier, item);
	}
}
