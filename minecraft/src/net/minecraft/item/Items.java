package net.minecraft.item;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.Sherds;
import net.minecraft.block.jukebox.JukeboxSongs;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.DebugStickStateComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.component.type.MapDecorationsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.OminousBottleAmplifierComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentModels;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BannerPatternTags;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.Direction;

public class Items {
	public static final Item AIR = register(Blocks.AIR, AirBlockItem::new);
	public static final Item STONE = register(Blocks.STONE);
	public static final Item GRANITE = register(Blocks.GRANITE);
	public static final Item POLISHED_GRANITE = register(Blocks.POLISHED_GRANITE);
	public static final Item DIORITE = register(Blocks.DIORITE);
	public static final Item POLISHED_DIORITE = register(Blocks.POLISHED_DIORITE);
	public static final Item ANDESITE = register(Blocks.ANDESITE);
	public static final Item POLISHED_ANDESITE = register(Blocks.POLISHED_ANDESITE);
	public static final Item DEEPSLATE = register(Blocks.DEEPSLATE);
	public static final Item COBBLED_DEEPSLATE = register(Blocks.COBBLED_DEEPSLATE);
	public static final Item POLISHED_DEEPSLATE = register(Blocks.POLISHED_DEEPSLATE);
	public static final Item CALCITE = register(Blocks.CALCITE);
	public static final Item TUFF = register(Blocks.TUFF);
	public static final Item TUFF_SLAB = register(Blocks.TUFF_SLAB);
	public static final Item TUFF_STAIRS = register(Blocks.TUFF_STAIRS);
	public static final Item TUFF_WALL = register(Blocks.TUFF_WALL);
	public static final Item CHISELED_TUFF = register(Blocks.CHISELED_TUFF);
	public static final Item POLISHED_TUFF = register(Blocks.POLISHED_TUFF);
	public static final Item POLISHED_TUFF_SLAB = register(Blocks.POLISHED_TUFF_SLAB);
	public static final Item POLISHED_TUFF_STAIRS = register(Blocks.POLISHED_TUFF_STAIRS);
	public static final Item POLISHED_TUFF_WALL = register(Blocks.POLISHED_TUFF_WALL);
	public static final Item TUFF_BRICKS = register(Blocks.TUFF_BRICKS);
	public static final Item TUFF_BRICK_SLAB = register(Blocks.TUFF_BRICK_SLAB);
	public static final Item TUFF_BRICK_STAIRS = register(Blocks.TUFF_BRICK_STAIRS);
	public static final Item TUFF_BRICK_WALL = register(Blocks.TUFF_BRICK_WALL);
	public static final Item CHISELED_TUFF_BRICKS = register(Blocks.CHISELED_TUFF_BRICKS);
	public static final Item DRIPSTONE_BLOCK = register(Blocks.DRIPSTONE_BLOCK);
	public static final Item GRASS_BLOCK = register(Blocks.GRASS_BLOCK);
	public static final Item DIRT = register(Blocks.DIRT);
	public static final Item COARSE_DIRT = register(Blocks.COARSE_DIRT);
	public static final Item PODZOL = register(Blocks.PODZOL);
	public static final Item ROOTED_DIRT = register(Blocks.ROOTED_DIRT);
	public static final Item MUD = register(Blocks.MUD);
	public static final Item CRIMSON_NYLIUM = register(Blocks.CRIMSON_NYLIUM);
	public static final Item WARPED_NYLIUM = register(Blocks.WARPED_NYLIUM);
	public static final Item COBBLESTONE = register(Blocks.COBBLESTONE);
	public static final Item OAK_PLANKS = register(Blocks.OAK_PLANKS);
	public static final Item SPRUCE_PLANKS = register(Blocks.SPRUCE_PLANKS);
	public static final Item BIRCH_PLANKS = register(Blocks.BIRCH_PLANKS);
	public static final Item JUNGLE_PLANKS = register(Blocks.JUNGLE_PLANKS);
	public static final Item ACACIA_PLANKS = register(Blocks.ACACIA_PLANKS);
	public static final Item CHERRY_PLANKS = register(Blocks.CHERRY_PLANKS);
	public static final Item DARK_OAK_PLANKS = register(Blocks.DARK_OAK_PLANKS);
	public static final Item MANGROVE_PLANKS = register(Blocks.MANGROVE_PLANKS);
	public static final Item BAMBOO_PLANKS = register(Blocks.BAMBOO_PLANKS);
	public static final Item CRIMSON_PLANKS = register(Blocks.CRIMSON_PLANKS);
	public static final Item WARPED_PLANKS = register(Blocks.WARPED_PLANKS);
	public static final Item BAMBOO_MOSAIC = register(Blocks.BAMBOO_MOSAIC);
	public static final Item OAK_SAPLING = register(Blocks.OAK_SAPLING);
	public static final Item SPRUCE_SAPLING = register(Blocks.SPRUCE_SAPLING);
	public static final Item BIRCH_SAPLING = register(Blocks.BIRCH_SAPLING);
	public static final Item JUNGLE_SAPLING = register(Blocks.JUNGLE_SAPLING);
	public static final Item ACACIA_SAPLING = register(Blocks.ACACIA_SAPLING);
	public static final Item CHERRY_SAPLING = register(Blocks.CHERRY_SAPLING);
	public static final Item DARK_OAK_SAPLING = register(Blocks.DARK_OAK_SAPLING);
	public static final Item MANGROVE_PROPAGULE = register(Blocks.MANGROVE_PROPAGULE);
	public static final Item BEDROCK = register(Blocks.BEDROCK);
	public static final Item SAND = register(Blocks.SAND);
	public static final Item SUSPICIOUS_SAND = register(Blocks.SUSPICIOUS_SAND);
	public static final Item SUSPICIOUS_GRAVEL = register(Blocks.SUSPICIOUS_GRAVEL);
	public static final Item RED_SAND = register(Blocks.RED_SAND);
	public static final Item GRAVEL = register(Blocks.GRAVEL);
	public static final Item COAL_ORE = register(Blocks.COAL_ORE);
	public static final Item DEEPSLATE_COAL_ORE = register(Blocks.DEEPSLATE_COAL_ORE);
	public static final Item IRON_ORE = register(Blocks.IRON_ORE);
	public static final Item DEEPSLATE_IRON_ORE = register(Blocks.DEEPSLATE_IRON_ORE);
	public static final Item COPPER_ORE = register(Blocks.COPPER_ORE);
	public static final Item DEEPSLATE_COPPER_ORE = register(Blocks.DEEPSLATE_COPPER_ORE);
	public static final Item GOLD_ORE = register(Blocks.GOLD_ORE);
	public static final Item DEEPSLATE_GOLD_ORE = register(Blocks.DEEPSLATE_GOLD_ORE);
	public static final Item REDSTONE_ORE = register(Blocks.REDSTONE_ORE);
	public static final Item DEEPSLATE_REDSTONE_ORE = register(Blocks.DEEPSLATE_REDSTONE_ORE);
	public static final Item EMERALD_ORE = register(Blocks.EMERALD_ORE);
	public static final Item DEEPSLATE_EMERALD_ORE = register(Blocks.DEEPSLATE_EMERALD_ORE);
	public static final Item LAPIS_ORE = register(Blocks.LAPIS_ORE);
	public static final Item DEEPSLATE_LAPIS_ORE = register(Blocks.DEEPSLATE_LAPIS_ORE);
	public static final Item DIAMOND_ORE = register(Blocks.DIAMOND_ORE);
	public static final Item DEEPSLATE_DIAMOND_ORE = register(Blocks.DEEPSLATE_DIAMOND_ORE);
	public static final Item NETHER_GOLD_ORE = register(Blocks.NETHER_GOLD_ORE);
	public static final Item NETHER_QUARTZ_ORE = register(Blocks.NETHER_QUARTZ_ORE);
	public static final Item ANCIENT_DEBRIS = register(Blocks.ANCIENT_DEBRIS, new Item.Settings().fireproof());
	public static final Item COAL_BLOCK = register(Blocks.COAL_BLOCK);
	public static final Item RAW_IRON_BLOCK = register(Blocks.RAW_IRON_BLOCK);
	public static final Item RAW_COPPER_BLOCK = register(Blocks.RAW_COPPER_BLOCK);
	public static final Item RAW_GOLD_BLOCK = register(Blocks.RAW_GOLD_BLOCK);
	public static final Item HEAVY_CORE = register(Blocks.HEAVY_CORE, new Item.Settings().rarity(Rarity.EPIC));
	public static final Item AMETHYST_BLOCK = register(Blocks.AMETHYST_BLOCK);
	public static final Item BUDDING_AMETHYST = register(Blocks.BUDDING_AMETHYST);
	public static final Item IRON_BLOCK = register(Blocks.IRON_BLOCK);
	public static final Item COPPER_BLOCK = register(Blocks.COPPER_BLOCK);
	public static final Item GOLD_BLOCK = register(Blocks.GOLD_BLOCK);
	public static final Item DIAMOND_BLOCK = register(Blocks.DIAMOND_BLOCK);
	public static final Item NETHERITE_BLOCK = register(Blocks.NETHERITE_BLOCK, new Item.Settings().fireproof());
	public static final Item EXPOSED_COPPER = register(Blocks.EXPOSED_COPPER);
	public static final Item WEATHERED_COPPER = register(Blocks.WEATHERED_COPPER);
	public static final Item OXIDIZED_COPPER = register(Blocks.OXIDIZED_COPPER);
	public static final Item CHISELED_COPPER = register(Blocks.CHISELED_COPPER);
	public static final Item EXPOSED_CHISELED_COPPER = register(Blocks.EXPOSED_CHISELED_COPPER);
	public static final Item WEATHERED_CHISELED_COPPER = register(Blocks.WEATHERED_CHISELED_COPPER);
	public static final Item OXIDIZED_CHISELED_COPPER = register(Blocks.OXIDIZED_CHISELED_COPPER);
	public static final Item CUT_COPPER = register(Blocks.CUT_COPPER);
	public static final Item EXPOSED_CUT_COPPER = register(Blocks.EXPOSED_CUT_COPPER);
	public static final Item WEATHERED_CUT_COPPER = register(Blocks.WEATHERED_CUT_COPPER);
	public static final Item OXIDIZED_CUT_COPPER = register(Blocks.OXIDIZED_CUT_COPPER);
	public static final Item CUT_COPPER_STAIRS = register(Blocks.CUT_COPPER_STAIRS);
	public static final Item EXPOSED_CUT_COPPER_STAIRS = register(Blocks.EXPOSED_CUT_COPPER_STAIRS);
	public static final Item WEATHERED_CUT_COPPER_STAIRS = register(Blocks.WEATHERED_CUT_COPPER_STAIRS);
	public static final Item OXIDIZED_CUT_COPPER_STAIRS = register(Blocks.OXIDIZED_CUT_COPPER_STAIRS);
	public static final Item CUT_COPPER_SLAB = register(Blocks.CUT_COPPER_SLAB);
	public static final Item EXPOSED_CUT_COPPER_SLAB = register(Blocks.EXPOSED_CUT_COPPER_SLAB);
	public static final Item WEATHERED_CUT_COPPER_SLAB = register(Blocks.WEATHERED_CUT_COPPER_SLAB);
	public static final Item OXIDIZED_CUT_COPPER_SLAB = register(Blocks.OXIDIZED_CUT_COPPER_SLAB);
	public static final Item WAXED_COPPER_BLOCK = register(Blocks.WAXED_COPPER_BLOCK);
	public static final Item WAXED_EXPOSED_COPPER = register(Blocks.WAXED_EXPOSED_COPPER);
	public static final Item WAXED_WEATHERED_COPPER = register(Blocks.WAXED_WEATHERED_COPPER);
	public static final Item WAXED_OXIDIZED_COPPER = register(Blocks.WAXED_OXIDIZED_COPPER);
	public static final Item WAXED_CHISELED_COPPER = register(Blocks.WAXED_CHISELED_COPPER);
	public static final Item WAXED_EXPOSED_CHISELED_COPPER = register(Blocks.WAXED_EXPOSED_CHISELED_COPPER);
	public static final Item WAXED_WEATHERED_CHISELED_COPPER = register(Blocks.WAXED_WEATHERED_CHISELED_COPPER);
	public static final Item WAXED_OXIDIZED_CHISELED_COPPER = register(Blocks.WAXED_OXIDIZED_CHISELED_COPPER);
	public static final Item WAXED_CUT_COPPER = register(Blocks.WAXED_CUT_COPPER);
	public static final Item WAXED_EXPOSED_CUT_COPPER = register(Blocks.WAXED_EXPOSED_CUT_COPPER);
	public static final Item WAXED_WEATHERED_CUT_COPPER = register(Blocks.WAXED_WEATHERED_CUT_COPPER);
	public static final Item WAXED_OXIDIZED_CUT_COPPER = register(Blocks.WAXED_OXIDIZED_CUT_COPPER);
	public static final Item WAXED_CUT_COPPER_STAIRS = register(Blocks.WAXED_CUT_COPPER_STAIRS);
	public static final Item WAXED_EXPOSED_CUT_COPPER_STAIRS = register(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS);
	public static final Item WAXED_WEATHERED_CUT_COPPER_STAIRS = register(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS);
	public static final Item WAXED_OXIDIZED_CUT_COPPER_STAIRS = register(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
	public static final Item WAXED_CUT_COPPER_SLAB = register(Blocks.WAXED_CUT_COPPER_SLAB);
	public static final Item WAXED_EXPOSED_CUT_COPPER_SLAB = register(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB);
	public static final Item WAXED_WEATHERED_CUT_COPPER_SLAB = register(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB);
	public static final Item WAXED_OXIDIZED_CUT_COPPER_SLAB = register(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB);
	public static final Item OAK_LOG = register(Blocks.OAK_LOG);
	public static final Item SPRUCE_LOG = register(Blocks.SPRUCE_LOG);
	public static final Item BIRCH_LOG = register(Blocks.BIRCH_LOG);
	public static final Item JUNGLE_LOG = register(Blocks.JUNGLE_LOG);
	public static final Item ACACIA_LOG = register(Blocks.ACACIA_LOG);
	public static final Item CHERRY_LOG = register(Blocks.CHERRY_LOG);
	public static final Item DARK_OAK_LOG = register(Blocks.DARK_OAK_LOG);
	public static final Item MANGROVE_LOG = register(Blocks.MANGROVE_LOG);
	public static final Item MANGROVE_ROOTS = register(Blocks.MANGROVE_ROOTS);
	public static final Item MUDDY_MANGROVE_ROOTS = register(Blocks.MUDDY_MANGROVE_ROOTS);
	public static final Item CRIMSON_STEM = register(Blocks.CRIMSON_STEM);
	public static final Item WARPED_STEM = register(Blocks.WARPED_STEM);
	public static final Item BAMBOO_BLOCK = register(Blocks.BAMBOO_BLOCK);
	public static final Item STRIPPED_OAK_LOG = register(Blocks.STRIPPED_OAK_LOG);
	public static final Item STRIPPED_SPRUCE_LOG = register(Blocks.STRIPPED_SPRUCE_LOG);
	public static final Item STRIPPED_BIRCH_LOG = register(Blocks.STRIPPED_BIRCH_LOG);
	public static final Item STRIPPED_JUNGLE_LOG = register(Blocks.STRIPPED_JUNGLE_LOG);
	public static final Item STRIPPED_ACACIA_LOG = register(Blocks.STRIPPED_ACACIA_LOG);
	public static final Item STRIPPED_CHERRY_LOG = register(Blocks.STRIPPED_CHERRY_LOG);
	public static final Item STRIPPED_DARK_OAK_LOG = register(Blocks.STRIPPED_DARK_OAK_LOG);
	public static final Item STRIPPED_MANGROVE_LOG = register(Blocks.STRIPPED_MANGROVE_LOG);
	public static final Item STRIPPED_CRIMSON_STEM = register(Blocks.STRIPPED_CRIMSON_STEM);
	public static final Item STRIPPED_WARPED_STEM = register(Blocks.STRIPPED_WARPED_STEM);
	public static final Item STRIPPED_OAK_WOOD = register(Blocks.STRIPPED_OAK_WOOD);
	public static final Item STRIPPED_SPRUCE_WOOD = register(Blocks.STRIPPED_SPRUCE_WOOD);
	public static final Item STRIPPED_BIRCH_WOOD = register(Blocks.STRIPPED_BIRCH_WOOD);
	public static final Item STRIPPED_JUNGLE_WOOD = register(Blocks.STRIPPED_JUNGLE_WOOD);
	public static final Item STRIPPED_ACACIA_WOOD = register(Blocks.STRIPPED_ACACIA_WOOD);
	public static final Item STRIPPED_CHERRY_WOOD = register(Blocks.STRIPPED_CHERRY_WOOD);
	public static final Item STRIPPED_DARK_OAK_WOOD = register(Blocks.STRIPPED_DARK_OAK_WOOD);
	public static final Item STRIPPED_MANGROVE_WOOD = register(Blocks.STRIPPED_MANGROVE_WOOD);
	public static final Item STRIPPED_CRIMSON_HYPHAE = register(Blocks.STRIPPED_CRIMSON_HYPHAE);
	public static final Item STRIPPED_WARPED_HYPHAE = register(Blocks.STRIPPED_WARPED_HYPHAE);
	public static final Item STRIPPED_BAMBOO_BLOCK = register(Blocks.STRIPPED_BAMBOO_BLOCK);
	public static final Item OAK_WOOD = register(Blocks.OAK_WOOD);
	public static final Item SPRUCE_WOOD = register(Blocks.SPRUCE_WOOD);
	public static final Item BIRCH_WOOD = register(Blocks.BIRCH_WOOD);
	public static final Item JUNGLE_WOOD = register(Blocks.JUNGLE_WOOD);
	public static final Item ACACIA_WOOD = register(Blocks.ACACIA_WOOD);
	public static final Item CHERRY_WOOD = register(Blocks.CHERRY_WOOD);
	public static final Item DARK_OAK_WOOD = register(Blocks.DARK_OAK_WOOD);
	public static final Item MANGROVE_WOOD = register(Blocks.MANGROVE_WOOD);
	public static final Item CRIMSON_HYPHAE = register(Blocks.CRIMSON_HYPHAE);
	public static final Item WARPED_HYPHAE = register(Blocks.WARPED_HYPHAE);
	public static final Item OAK_LEAVES = register(Blocks.OAK_LEAVES);
	public static final Item SPRUCE_LEAVES = register(Blocks.SPRUCE_LEAVES);
	public static final Item BIRCH_LEAVES = register(Blocks.BIRCH_LEAVES);
	public static final Item JUNGLE_LEAVES = register(Blocks.JUNGLE_LEAVES);
	public static final Item ACACIA_LEAVES = register(Blocks.ACACIA_LEAVES);
	public static final Item CHERRY_LEAVES = register(Blocks.CHERRY_LEAVES);
	public static final Item DARK_OAK_LEAVES = register(Blocks.DARK_OAK_LEAVES);
	public static final Item MANGROVE_LEAVES = register(Blocks.MANGROVE_LEAVES);
	public static final Item AZALEA_LEAVES = register(Blocks.AZALEA_LEAVES);
	public static final Item FLOWERING_AZALEA_LEAVES = register(Blocks.FLOWERING_AZALEA_LEAVES);
	public static final Item SPONGE = register(Blocks.SPONGE);
	public static final Item WET_SPONGE = register(Blocks.WET_SPONGE);
	public static final Item GLASS = register(Blocks.GLASS);
	public static final Item TINTED_GLASS = register(Blocks.TINTED_GLASS);
	public static final Item LAPIS_BLOCK = register(Blocks.LAPIS_BLOCK);
	public static final Item SANDSTONE = register(Blocks.SANDSTONE);
	public static final Item CHISELED_SANDSTONE = register(Blocks.CHISELED_SANDSTONE);
	public static final Item CUT_SANDSTONE = register(Blocks.CUT_SANDSTONE);
	public static final Item COBWEB = register(Blocks.COBWEB);
	public static final Item SHORT_GRASS = register(Blocks.SHORT_GRASS);
	public static final Item FERN = register(Blocks.FERN);
	public static final Item AZALEA = register(Blocks.AZALEA);
	public static final Item FLOWERING_AZALEA = register(Blocks.FLOWERING_AZALEA);
	public static final Item DEAD_BUSH = register(Blocks.DEAD_BUSH);
	public static final Item SEAGRASS = register(Blocks.SEAGRASS);
	public static final Item SEA_PICKLE = register(Blocks.SEA_PICKLE);
	public static final Item WHITE_WOOL = register(Blocks.WHITE_WOOL);
	public static final Item ORANGE_WOOL = register(Blocks.ORANGE_WOOL);
	public static final Item MAGENTA_WOOL = register(Blocks.MAGENTA_WOOL);
	public static final Item LIGHT_BLUE_WOOL = register(Blocks.LIGHT_BLUE_WOOL);
	public static final Item YELLOW_WOOL = register(Blocks.YELLOW_WOOL);
	public static final Item LIME_WOOL = register(Blocks.LIME_WOOL);
	public static final Item PINK_WOOL = register(Blocks.PINK_WOOL);
	public static final Item GRAY_WOOL = register(Blocks.GRAY_WOOL);
	public static final Item LIGHT_GRAY_WOOL = register(Blocks.LIGHT_GRAY_WOOL);
	public static final Item CYAN_WOOL = register(Blocks.CYAN_WOOL);
	public static final Item PURPLE_WOOL = register(Blocks.PURPLE_WOOL);
	public static final Item BLUE_WOOL = register(Blocks.BLUE_WOOL);
	public static final Item BROWN_WOOL = register(Blocks.BROWN_WOOL);
	public static final Item GREEN_WOOL = register(Blocks.GREEN_WOOL);
	public static final Item RED_WOOL = register(Blocks.RED_WOOL);
	public static final Item BLACK_WOOL = register(Blocks.BLACK_WOOL);
	public static final Item DANDELION = register(Blocks.DANDELION);
	public static final Item POPPY = register(Blocks.POPPY);
	public static final Item BLUE_ORCHID = register(Blocks.BLUE_ORCHID);
	public static final Item ALLIUM = register(Blocks.ALLIUM);
	public static final Item AZURE_BLUET = register(Blocks.AZURE_BLUET);
	public static final Item RED_TULIP = register(Blocks.RED_TULIP);
	public static final Item ORANGE_TULIP = register(Blocks.ORANGE_TULIP);
	public static final Item WHITE_TULIP = register(Blocks.WHITE_TULIP);
	public static final Item PINK_TULIP = register(Blocks.PINK_TULIP);
	public static final Item OXEYE_DAISY = register(Blocks.OXEYE_DAISY);
	public static final Item CORNFLOWER = register(Blocks.CORNFLOWER);
	public static final Item LILY_OF_THE_VALLEY = register(Blocks.LILY_OF_THE_VALLEY);
	public static final Item WITHER_ROSE = register(Blocks.WITHER_ROSE);
	public static final Item TORCHFLOWER = register(Blocks.TORCHFLOWER);
	public static final Item PITCHER_PLANT = register(Blocks.PITCHER_PLANT);
	public static final Item SPORE_BLOSSOM = register(Blocks.SPORE_BLOSSOM);
	public static final Item BROWN_MUSHROOM = register(Blocks.BROWN_MUSHROOM);
	public static final Item RED_MUSHROOM = register(Blocks.RED_MUSHROOM);
	public static final Item CRIMSON_FUNGUS = register(Blocks.CRIMSON_FUNGUS);
	public static final Item WARPED_FUNGUS = register(Blocks.WARPED_FUNGUS);
	public static final Item CRIMSON_ROOTS = register(Blocks.CRIMSON_ROOTS);
	public static final Item WARPED_ROOTS = register(Blocks.WARPED_ROOTS);
	public static final Item NETHER_SPROUTS = register(Blocks.NETHER_SPROUTS);
	public static final Item WEEPING_VINES = register(Blocks.WEEPING_VINES);
	public static final Item TWISTING_VINES = register(Blocks.TWISTING_VINES);
	public static final Item SUGAR_CANE = register(Blocks.SUGAR_CANE);
	public static final Item KELP = register(Blocks.KELP);
	public static final Item MOSS_CARPET = register(Blocks.MOSS_CARPET);
	public static final Item PINK_PETALS = register(Blocks.PINK_PETALS);
	public static final Item MOSS_BLOCK = register(Blocks.MOSS_BLOCK);
	public static final Item HANGING_ROOTS = register(Blocks.HANGING_ROOTS);
	public static final Item BIG_DRIPLEAF = register(Blocks.BIG_DRIPLEAF, Blocks.BIG_DRIPLEAF_STEM);
	public static final Item SMALL_DRIPLEAF = register(Blocks.SMALL_DRIPLEAF, TallBlockItem::new);
	public static final Item BAMBOO = register(Blocks.BAMBOO);
	public static final Item OAK_SLAB = register(Blocks.OAK_SLAB);
	public static final Item SPRUCE_SLAB = register(Blocks.SPRUCE_SLAB);
	public static final Item BIRCH_SLAB = register(Blocks.BIRCH_SLAB);
	public static final Item JUNGLE_SLAB = register(Blocks.JUNGLE_SLAB);
	public static final Item ACACIA_SLAB = register(Blocks.ACACIA_SLAB);
	public static final Item CHERRY_SLAB = register(Blocks.CHERRY_SLAB);
	public static final Item DARK_OAK_SLAB = register(Blocks.DARK_OAK_SLAB);
	public static final Item MANGROVE_SLAB = register(Blocks.MANGROVE_SLAB);
	public static final Item BAMBOO_SLAB = register(Blocks.BAMBOO_SLAB);
	public static final Item BAMBOO_MOSAIC_SLAB = register(Blocks.BAMBOO_MOSAIC_SLAB);
	public static final Item CRIMSON_SLAB = register(Blocks.CRIMSON_SLAB);
	public static final Item WARPED_SLAB = register(Blocks.WARPED_SLAB);
	public static final Item STONE_SLAB = register(Blocks.STONE_SLAB);
	public static final Item SMOOTH_STONE_SLAB = register(Blocks.SMOOTH_STONE_SLAB);
	public static final Item SANDSTONE_SLAB = register(Blocks.SANDSTONE_SLAB);
	public static final Item CUT_SANDSTONE_SLAB = register(Blocks.CUT_SANDSTONE_SLAB);
	public static final Item PETRIFIED_OAK_SLAB = register(Blocks.PETRIFIED_OAK_SLAB);
	public static final Item COBBLESTONE_SLAB = register(Blocks.COBBLESTONE_SLAB);
	public static final Item BRICK_SLAB = register(Blocks.BRICK_SLAB);
	public static final Item STONE_BRICK_SLAB = register(Blocks.STONE_BRICK_SLAB);
	public static final Item MUD_BRICK_SLAB = register(Blocks.MUD_BRICK_SLAB);
	public static final Item NETHER_BRICK_SLAB = register(Blocks.NETHER_BRICK_SLAB);
	public static final Item QUARTZ_SLAB = register(Blocks.QUARTZ_SLAB);
	public static final Item RED_SANDSTONE_SLAB = register(Blocks.RED_SANDSTONE_SLAB);
	public static final Item CUT_RED_SANDSTONE_SLAB = register(Blocks.CUT_RED_SANDSTONE_SLAB);
	public static final Item PURPUR_SLAB = register(Blocks.PURPUR_SLAB);
	public static final Item PRISMARINE_SLAB = register(Blocks.PRISMARINE_SLAB);
	public static final Item PRISMARINE_BRICK_SLAB = register(Blocks.PRISMARINE_BRICK_SLAB);
	public static final Item DARK_PRISMARINE_SLAB = register(Blocks.DARK_PRISMARINE_SLAB);
	public static final Item SMOOTH_QUARTZ = register(Blocks.SMOOTH_QUARTZ);
	public static final Item SMOOTH_RED_SANDSTONE = register(Blocks.SMOOTH_RED_SANDSTONE);
	public static final Item SMOOTH_SANDSTONE = register(Blocks.SMOOTH_SANDSTONE);
	public static final Item SMOOTH_STONE = register(Blocks.SMOOTH_STONE);
	public static final Item BRICKS = register(Blocks.BRICKS);
	public static final Item BOOKSHELF = register(Blocks.BOOKSHELF);
	public static final Item CHISELED_BOOKSHELF = register(
		Blocks.CHISELED_BOOKSHELF, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item DECORATED_POT = register(Blocks.DECORATED_POT, new Item.Settings().component(DataComponentTypes.POT_DECORATIONS, Sherds.DEFAULT));
	public static final Item MOSSY_COBBLESTONE = register(Blocks.MOSSY_COBBLESTONE);
	public static final Item OBSIDIAN = register(Blocks.OBSIDIAN);
	public static final Item TORCH = register(
		Blocks.TORCH,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.WALL_TORCH, Direction.DOWN, settings))
	);
	public static final Item END_ROD = register(Blocks.END_ROD);
	public static final Item CHORUS_PLANT = register(Blocks.CHORUS_PLANT);
	public static final Item CHORUS_FLOWER = register(Blocks.CHORUS_FLOWER);
	public static final Item PURPUR_BLOCK = register(Blocks.PURPUR_BLOCK);
	public static final Item PURPUR_PILLAR = register(Blocks.PURPUR_PILLAR);
	public static final Item PURPUR_STAIRS = register(Blocks.PURPUR_STAIRS);
	public static final Item SPAWNER = register(Blocks.SPAWNER);
	public static final Item CHEST = register(
		Blocks.CHEST, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item CRAFTING_TABLE = register(Blocks.CRAFTING_TABLE);
	public static final Item FARMLAND = register(Blocks.FARMLAND);
	public static final Item FURNACE = register(
		Blocks.FURNACE, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item LADDER = register(Blocks.LADDER);
	public static final Item COBBLESTONE_STAIRS = register(Blocks.COBBLESTONE_STAIRS);
	public static final Item SNOW = register(Blocks.SNOW);
	public static final Item ICE = register(Blocks.ICE);
	public static final Item SNOW_BLOCK = register(Blocks.SNOW_BLOCK);
	public static final Item CACTUS = register(Blocks.CACTUS);
	public static final Item CLAY = register(Blocks.CLAY);
	public static final Item JUKEBOX = register(Blocks.JUKEBOX);
	public static final Item OAK_FENCE = register(Blocks.OAK_FENCE);
	public static final Item SPRUCE_FENCE = register(Blocks.SPRUCE_FENCE);
	public static final Item BIRCH_FENCE = register(Blocks.BIRCH_FENCE);
	public static final Item JUNGLE_FENCE = register(Blocks.JUNGLE_FENCE);
	public static final Item ACACIA_FENCE = register(Blocks.ACACIA_FENCE);
	public static final Item CHERRY_FENCE = register(Blocks.CHERRY_FENCE);
	public static final Item DARK_OAK_FENCE = register(Blocks.DARK_OAK_FENCE);
	public static final Item MANGROVE_FENCE = register(Blocks.MANGROVE_FENCE);
	public static final Item BAMBOO_FENCE = register(Blocks.BAMBOO_FENCE);
	public static final Item CRIMSON_FENCE = register(Blocks.CRIMSON_FENCE);
	public static final Item WARPED_FENCE = register(Blocks.WARPED_FENCE);
	public static final Item PUMPKIN = register(Blocks.PUMPKIN);
	public static final Item CARVED_PUMPKIN = register(Blocks.CARVED_PUMPKIN, (UnaryOperator<Item.Settings>)(settings -> settings.equippable(EquipmentSlot.HEAD)));
	public static final Item JACK_O_LANTERN = register(Blocks.JACK_O_LANTERN);
	public static final Item NETHERRACK = register(Blocks.NETHERRACK);
	public static final Item SOUL_SAND = register(Blocks.SOUL_SAND);
	public static final Item SOUL_SOIL = register(Blocks.SOUL_SOIL);
	public static final Item BASALT = register(Blocks.BASALT);
	public static final Item POLISHED_BASALT = register(Blocks.POLISHED_BASALT);
	public static final Item SMOOTH_BASALT = register(Blocks.SMOOTH_BASALT);
	public static final Item SOUL_TORCH = register(
		Blocks.SOUL_TORCH,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.SOUL_WALL_TORCH, Direction.DOWN, settings))
	);
	public static final Item GLOWSTONE = register(Blocks.GLOWSTONE);
	public static final Item INFESTED_STONE = register(Blocks.INFESTED_STONE);
	public static final Item INFESTED_COBBLESTONE = register(Blocks.INFESTED_COBBLESTONE);
	public static final Item INFESTED_STONE_BRICKS = register(Blocks.INFESTED_STONE_BRICKS);
	public static final Item INFESTED_MOSSY_STONE_BRICKS = register(Blocks.INFESTED_MOSSY_STONE_BRICKS);
	public static final Item INFESTED_CRACKED_STONE_BRICKS = register(Blocks.INFESTED_CRACKED_STONE_BRICKS);
	public static final Item INFESTED_CHISELED_STONE_BRICKS = register(Blocks.INFESTED_CHISELED_STONE_BRICKS);
	public static final Item INFESTED_DEEPSLATE = register(Blocks.INFESTED_DEEPSLATE);
	public static final Item STONE_BRICKS = register(Blocks.STONE_BRICKS);
	public static final Item MOSSY_STONE_BRICKS = register(Blocks.MOSSY_STONE_BRICKS);
	public static final Item CRACKED_STONE_BRICKS = register(Blocks.CRACKED_STONE_BRICKS);
	public static final Item CHISELED_STONE_BRICKS = register(Blocks.CHISELED_STONE_BRICKS);
	public static final Item PACKED_MUD = register(Blocks.PACKED_MUD);
	public static final Item MUD_BRICKS = register(Blocks.MUD_BRICKS);
	public static final Item DEEPSLATE_BRICKS = register(Blocks.DEEPSLATE_BRICKS);
	public static final Item CRACKED_DEEPSLATE_BRICKS = register(Blocks.CRACKED_DEEPSLATE_BRICKS);
	public static final Item DEEPSLATE_TILES = register(Blocks.DEEPSLATE_TILES);
	public static final Item CRACKED_DEEPSLATE_TILES = register(Blocks.CRACKED_DEEPSLATE_TILES);
	public static final Item CHISELED_DEEPSLATE = register(Blocks.CHISELED_DEEPSLATE);
	public static final Item REINFORCED_DEEPSLATE = register(Blocks.REINFORCED_DEEPSLATE);
	public static final Item BROWN_MUSHROOM_BLOCK = register(Blocks.BROWN_MUSHROOM_BLOCK);
	public static final Item RED_MUSHROOM_BLOCK = register(Blocks.RED_MUSHROOM_BLOCK);
	public static final Item MUSHROOM_STEM = register(Blocks.MUSHROOM_STEM);
	public static final Item IRON_BARS = register(Blocks.IRON_BARS);
	public static final Item CHAIN = register(Blocks.CHAIN);
	public static final Item GLASS_PANE = register(Blocks.GLASS_PANE);
	public static final Item MELON = register(Blocks.MELON);
	public static final Item VINE = register(Blocks.VINE);
	public static final Item GLOW_LICHEN = register(Blocks.GLOW_LICHEN);
	public static final Item BRICK_STAIRS = register(Blocks.BRICK_STAIRS);
	public static final Item STONE_BRICK_STAIRS = register(Blocks.STONE_BRICK_STAIRS);
	public static final Item MUD_BRICK_STAIRS = register(Blocks.MUD_BRICK_STAIRS);
	public static final Item MYCELIUM = register(Blocks.MYCELIUM);
	public static final Item LILY_PAD = register(Blocks.LILY_PAD, PlaceableOnWaterItem::new);
	public static final Item NETHER_BRICKS = register(Blocks.NETHER_BRICKS);
	public static final Item CRACKED_NETHER_BRICKS = register(Blocks.CRACKED_NETHER_BRICKS);
	public static final Item CHISELED_NETHER_BRICKS = register(Blocks.CHISELED_NETHER_BRICKS);
	public static final Item NETHER_BRICK_FENCE = register(Blocks.NETHER_BRICK_FENCE);
	public static final Item NETHER_BRICK_STAIRS = register(Blocks.NETHER_BRICK_STAIRS);
	public static final Item SCULK = register(Blocks.SCULK);
	public static final Item SCULK_VEIN = register(Blocks.SCULK_VEIN);
	public static final Item SCULK_CATALYST = register(Blocks.SCULK_CATALYST);
	public static final Item SCULK_SHRIEKER = register(Blocks.SCULK_SHRIEKER);
	public static final Item ENCHANTING_TABLE = register(Blocks.ENCHANTING_TABLE);
	public static final Item END_PORTAL_FRAME = register(Blocks.END_PORTAL_FRAME);
	public static final Item END_STONE = register(Blocks.END_STONE);
	public static final Item END_STONE_BRICKS = register(Blocks.END_STONE_BRICKS);
	public static final Item DRAGON_EGG = register(Blocks.DRAGON_EGG, new Item.Settings().rarity(Rarity.EPIC));
	public static final Item SANDSTONE_STAIRS = register(Blocks.SANDSTONE_STAIRS);
	public static final Item ENDER_CHEST = register(Blocks.ENDER_CHEST);
	public static final Item EMERALD_BLOCK = register(Blocks.EMERALD_BLOCK);
	public static final Item OAK_STAIRS = register(Blocks.OAK_STAIRS);
	public static final Item SPRUCE_STAIRS = register(Blocks.SPRUCE_STAIRS);
	public static final Item BIRCH_STAIRS = register(Blocks.BIRCH_STAIRS);
	public static final Item JUNGLE_STAIRS = register(Blocks.JUNGLE_STAIRS);
	public static final Item ACACIA_STAIRS = register(Blocks.ACACIA_STAIRS);
	public static final Item CHERRY_STAIRS = register(Blocks.CHERRY_STAIRS);
	public static final Item DARK_OAK_STAIRS = register(Blocks.DARK_OAK_STAIRS);
	public static final Item MANGROVE_STAIRS = register(Blocks.MANGROVE_STAIRS);
	public static final Item BAMBOO_STAIRS = register(Blocks.BAMBOO_STAIRS);
	public static final Item BAMBOO_MOSAIC_STAIRS = register(Blocks.BAMBOO_MOSAIC_STAIRS);
	public static final Item CRIMSON_STAIRS = register(Blocks.CRIMSON_STAIRS);
	public static final Item WARPED_STAIRS = register(Blocks.WARPED_STAIRS);
	public static final Item COMMAND_BLOCK = register(Blocks.COMMAND_BLOCK, OperatorOnlyBlockItem::new, new Item.Settings().rarity(Rarity.EPIC));
	public static final Item BEACON = register(Blocks.BEACON, new Item.Settings().rarity(Rarity.RARE));
	public static final Item COBBLESTONE_WALL = register(Blocks.COBBLESTONE_WALL);
	public static final Item MOSSY_COBBLESTONE_WALL = register(Blocks.MOSSY_COBBLESTONE_WALL);
	public static final Item BRICK_WALL = register(Blocks.BRICK_WALL);
	public static final Item PRISMARINE_WALL = register(Blocks.PRISMARINE_WALL);
	public static final Item RED_SANDSTONE_WALL = register(Blocks.RED_SANDSTONE_WALL);
	public static final Item MOSSY_STONE_BRICK_WALL = register(Blocks.MOSSY_STONE_BRICK_WALL);
	public static final Item GRANITE_WALL = register(Blocks.GRANITE_WALL);
	public static final Item STONE_BRICK_WALL = register(Blocks.STONE_BRICK_WALL);
	public static final Item MUD_BRICK_WALL = register(Blocks.MUD_BRICK_WALL);
	public static final Item NETHER_BRICK_WALL = register(Blocks.NETHER_BRICK_WALL);
	public static final Item ANDESITE_WALL = register(Blocks.ANDESITE_WALL);
	public static final Item RED_NETHER_BRICK_WALL = register(Blocks.RED_NETHER_BRICK_WALL);
	public static final Item SANDSTONE_WALL = register(Blocks.SANDSTONE_WALL);
	public static final Item END_STONE_BRICK_WALL = register(Blocks.END_STONE_BRICK_WALL);
	public static final Item DIORITE_WALL = register(Blocks.DIORITE_WALL);
	public static final Item BLACKSTONE_WALL = register(Blocks.BLACKSTONE_WALL);
	public static final Item POLISHED_BLACKSTONE_WALL = register(Blocks.POLISHED_BLACKSTONE_WALL);
	public static final Item POLISHED_BLACKSTONE_BRICK_WALL = register(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
	public static final Item COBBLED_DEEPSLATE_WALL = register(Blocks.COBBLED_DEEPSLATE_WALL);
	public static final Item POLISHED_DEEPSLATE_WALL = register(Blocks.POLISHED_DEEPSLATE_WALL);
	public static final Item DEEPSLATE_BRICK_WALL = register(Blocks.DEEPSLATE_BRICK_WALL);
	public static final Item DEEPSLATE_TILE_WALL = register(Blocks.DEEPSLATE_TILE_WALL);
	public static final Item ANVIL = register(Blocks.ANVIL);
	public static final Item CHIPPED_ANVIL = register(Blocks.CHIPPED_ANVIL);
	public static final Item DAMAGED_ANVIL = register(Blocks.DAMAGED_ANVIL);
	public static final Item CHISELED_QUARTZ_BLOCK = register(Blocks.CHISELED_QUARTZ_BLOCK);
	public static final Item QUARTZ_BLOCK = register(Blocks.QUARTZ_BLOCK);
	public static final Item QUARTZ_BRICKS = register(Blocks.QUARTZ_BRICKS);
	public static final Item QUARTZ_PILLAR = register(Blocks.QUARTZ_PILLAR);
	public static final Item QUARTZ_STAIRS = register(Blocks.QUARTZ_STAIRS);
	public static final Item WHITE_TERRACOTTA = register(Blocks.WHITE_TERRACOTTA);
	public static final Item ORANGE_TERRACOTTA = register(Blocks.ORANGE_TERRACOTTA);
	public static final Item MAGENTA_TERRACOTTA = register(Blocks.MAGENTA_TERRACOTTA);
	public static final Item LIGHT_BLUE_TERRACOTTA = register(Blocks.LIGHT_BLUE_TERRACOTTA);
	public static final Item YELLOW_TERRACOTTA = register(Blocks.YELLOW_TERRACOTTA);
	public static final Item LIME_TERRACOTTA = register(Blocks.LIME_TERRACOTTA);
	public static final Item PINK_TERRACOTTA = register(Blocks.PINK_TERRACOTTA);
	public static final Item GRAY_TERRACOTTA = register(Blocks.GRAY_TERRACOTTA);
	public static final Item LIGHT_GRAY_TERRACOTTA = register(Blocks.LIGHT_GRAY_TERRACOTTA);
	public static final Item CYAN_TERRACOTTA = register(Blocks.CYAN_TERRACOTTA);
	public static final Item PURPLE_TERRACOTTA = register(Blocks.PURPLE_TERRACOTTA);
	public static final Item BLUE_TERRACOTTA = register(Blocks.BLUE_TERRACOTTA);
	public static final Item BROWN_TERRACOTTA = register(Blocks.BROWN_TERRACOTTA);
	public static final Item GREEN_TERRACOTTA = register(Blocks.GREEN_TERRACOTTA);
	public static final Item RED_TERRACOTTA = register(Blocks.RED_TERRACOTTA);
	public static final Item BLACK_TERRACOTTA = register(Blocks.BLACK_TERRACOTTA);
	public static final Item BARRIER = register(Blocks.BARRIER, new Item.Settings().rarity(Rarity.EPIC));
	public static final Item LIGHT = register(Blocks.LIGHT, new Item.Settings().rarity(Rarity.EPIC));
	public static final Item HAY_BLOCK = register(Blocks.HAY_BLOCK);
	public static final Item WHITE_CARPET = register(
		Blocks.WHITE_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.WHITE)))
	);
	public static final Item ORANGE_CARPET = register(
		Blocks.ORANGE_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.ORANGE)))
	);
	public static final Item MAGENTA_CARPET = register(
		Blocks.MAGENTA_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.MAGENTA)))
	);
	public static final Item LIGHT_BLUE_CARPET = register(
		Blocks.LIGHT_BLUE_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.LIGHT_BLUE)))
	);
	public static final Item YELLOW_CARPET = register(
		Blocks.YELLOW_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.YELLOW)))
	);
	public static final Item LIME_CARPET = register(
		Blocks.LIME_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.LIME)))
	);
	public static final Item PINK_CARPET = register(
		Blocks.PINK_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.PINK)))
	);
	public static final Item GRAY_CARPET = register(
		Blocks.GRAY_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.GRAY)))
	);
	public static final Item LIGHT_GRAY_CARPET = register(
		Blocks.LIGHT_GRAY_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.LIGHT_GRAY)))
	);
	public static final Item CYAN_CARPET = register(
		Blocks.CYAN_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.CYAN)))
	);
	public static final Item PURPLE_CARPET = register(
		Blocks.PURPLE_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.PURPLE)))
	);
	public static final Item BLUE_CARPET = register(
		Blocks.BLUE_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.BLUE)))
	);
	public static final Item BROWN_CARPET = register(
		Blocks.BROWN_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.BROWN)))
	);
	public static final Item GREEN_CARPET = register(
		Blocks.GREEN_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.GREEN)))
	);
	public static final Item RED_CARPET = register(
		Blocks.RED_CARPET, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.RED)))
	);
	public static final Item BLACK_CARPET = register(
		Blocks.BLACK_CARPET,
		(UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.ofCarpet(DyeColor.BLACK)))
	);
	public static final Item TERRACOTTA = register(Blocks.TERRACOTTA);
	public static final Item PACKED_ICE = register(Blocks.PACKED_ICE);
	public static final Item DIRT_PATH = register(Blocks.DIRT_PATH);
	public static final Item SUNFLOWER = register(Blocks.SUNFLOWER, TallBlockItem::new);
	public static final Item LILAC = register(Blocks.LILAC, TallBlockItem::new);
	public static final Item ROSE_BUSH = register(Blocks.ROSE_BUSH, TallBlockItem::new);
	public static final Item PEONY = register(Blocks.PEONY, TallBlockItem::new);
	public static final Item TALL_GRASS = register(Blocks.TALL_GRASS, TallBlockItem::new);
	public static final Item LARGE_FERN = register(Blocks.LARGE_FERN, TallBlockItem::new);
	public static final Item WHITE_STAINED_GLASS = register(Blocks.WHITE_STAINED_GLASS);
	public static final Item ORANGE_STAINED_GLASS = register(Blocks.ORANGE_STAINED_GLASS);
	public static final Item MAGENTA_STAINED_GLASS = register(Blocks.MAGENTA_STAINED_GLASS);
	public static final Item LIGHT_BLUE_STAINED_GLASS = register(Blocks.LIGHT_BLUE_STAINED_GLASS);
	public static final Item YELLOW_STAINED_GLASS = register(Blocks.YELLOW_STAINED_GLASS);
	public static final Item LIME_STAINED_GLASS = register(Blocks.LIME_STAINED_GLASS);
	public static final Item PINK_STAINED_GLASS = register(Blocks.PINK_STAINED_GLASS);
	public static final Item GRAY_STAINED_GLASS = register(Blocks.GRAY_STAINED_GLASS);
	public static final Item LIGHT_GRAY_STAINED_GLASS = register(Blocks.LIGHT_GRAY_STAINED_GLASS);
	public static final Item CYAN_STAINED_GLASS = register(Blocks.CYAN_STAINED_GLASS);
	public static final Item PURPLE_STAINED_GLASS = register(Blocks.PURPLE_STAINED_GLASS);
	public static final Item BLUE_STAINED_GLASS = register(Blocks.BLUE_STAINED_GLASS);
	public static final Item BROWN_STAINED_GLASS = register(Blocks.BROWN_STAINED_GLASS);
	public static final Item GREEN_STAINED_GLASS = register(Blocks.GREEN_STAINED_GLASS);
	public static final Item RED_STAINED_GLASS = register(Blocks.RED_STAINED_GLASS);
	public static final Item BLACK_STAINED_GLASS = register(Blocks.BLACK_STAINED_GLASS);
	public static final Item WHITE_STAINED_GLASS_PANE = register(Blocks.WHITE_STAINED_GLASS_PANE);
	public static final Item ORANGE_STAINED_GLASS_PANE = register(Blocks.ORANGE_STAINED_GLASS_PANE);
	public static final Item MAGENTA_STAINED_GLASS_PANE = register(Blocks.MAGENTA_STAINED_GLASS_PANE);
	public static final Item LIGHT_BLUE_STAINED_GLASS_PANE = register(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
	public static final Item YELLOW_STAINED_GLASS_PANE = register(Blocks.YELLOW_STAINED_GLASS_PANE);
	public static final Item LIME_STAINED_GLASS_PANE = register(Blocks.LIME_STAINED_GLASS_PANE);
	public static final Item PINK_STAINED_GLASS_PANE = register(Blocks.PINK_STAINED_GLASS_PANE);
	public static final Item GRAY_STAINED_GLASS_PANE = register(Blocks.GRAY_STAINED_GLASS_PANE);
	public static final Item LIGHT_GRAY_STAINED_GLASS_PANE = register(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
	public static final Item CYAN_STAINED_GLASS_PANE = register(Blocks.CYAN_STAINED_GLASS_PANE);
	public static final Item PURPLE_STAINED_GLASS_PANE = register(Blocks.PURPLE_STAINED_GLASS_PANE);
	public static final Item BLUE_STAINED_GLASS_PANE = register(Blocks.BLUE_STAINED_GLASS_PANE);
	public static final Item BROWN_STAINED_GLASS_PANE = register(Blocks.BROWN_STAINED_GLASS_PANE);
	public static final Item GREEN_STAINED_GLASS_PANE = register(Blocks.GREEN_STAINED_GLASS_PANE);
	public static final Item RED_STAINED_GLASS_PANE = register(Blocks.RED_STAINED_GLASS_PANE);
	public static final Item BLACK_STAINED_GLASS_PANE = register(Blocks.BLACK_STAINED_GLASS_PANE);
	public static final Item PRISMARINE = register(Blocks.PRISMARINE);
	public static final Item PRISMARINE_BRICKS = register(Blocks.PRISMARINE_BRICKS);
	public static final Item DARK_PRISMARINE = register(Blocks.DARK_PRISMARINE);
	public static final Item PRISMARINE_STAIRS = register(Blocks.PRISMARINE_STAIRS);
	public static final Item PRISMARINE_BRICK_STAIRS = register(Blocks.PRISMARINE_BRICK_STAIRS);
	public static final Item DARK_PRISMARINE_STAIRS = register(Blocks.DARK_PRISMARINE_STAIRS);
	public static final Item SEA_LANTERN = register(Blocks.SEA_LANTERN);
	public static final Item RED_SANDSTONE = register(Blocks.RED_SANDSTONE);
	public static final Item CHISELED_RED_SANDSTONE = register(Blocks.CHISELED_RED_SANDSTONE);
	public static final Item CUT_RED_SANDSTONE = register(Blocks.CUT_RED_SANDSTONE);
	public static final Item RED_SANDSTONE_STAIRS = register(Blocks.RED_SANDSTONE_STAIRS);
	public static final Item REPEATING_COMMAND_BLOCK = register(
		Blocks.REPEATING_COMMAND_BLOCK, OperatorOnlyBlockItem::new, new Item.Settings().rarity(Rarity.EPIC)
	);
	public static final Item CHAIN_COMMAND_BLOCK = register(Blocks.CHAIN_COMMAND_BLOCK, OperatorOnlyBlockItem::new, new Item.Settings().rarity(Rarity.EPIC));
	public static final Item MAGMA_BLOCK = register(Blocks.MAGMA_BLOCK);
	public static final Item NETHER_WART_BLOCK = register(Blocks.NETHER_WART_BLOCK);
	public static final Item WARPED_WART_BLOCK = register(Blocks.WARPED_WART_BLOCK);
	public static final Item RED_NETHER_BRICKS = register(Blocks.RED_NETHER_BRICKS);
	public static final Item BONE_BLOCK = register(Blocks.BONE_BLOCK);
	public static final Item STRUCTURE_VOID = register(Blocks.STRUCTURE_VOID, new Item.Settings().rarity(Rarity.EPIC));
	public static final Item SHULKER_BOX = register(
		Blocks.SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item WHITE_SHULKER_BOX = register(
		Blocks.WHITE_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item ORANGE_SHULKER_BOX = register(
		Blocks.ORANGE_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item MAGENTA_SHULKER_BOX = register(
		Blocks.MAGENTA_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item LIGHT_BLUE_SHULKER_BOX = register(
		Blocks.LIGHT_BLUE_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item YELLOW_SHULKER_BOX = register(
		Blocks.YELLOW_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item LIME_SHULKER_BOX = register(
		Blocks.LIME_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item PINK_SHULKER_BOX = register(
		Blocks.PINK_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item GRAY_SHULKER_BOX = register(
		Blocks.GRAY_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item LIGHT_GRAY_SHULKER_BOX = register(
		Blocks.LIGHT_GRAY_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item CYAN_SHULKER_BOX = register(
		Blocks.CYAN_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item PURPLE_SHULKER_BOX = register(
		Blocks.PURPLE_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item BLUE_SHULKER_BOX = register(
		Blocks.BLUE_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item BROWN_SHULKER_BOX = register(
		Blocks.BROWN_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item GREEN_SHULKER_BOX = register(
		Blocks.GREEN_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item RED_SHULKER_BOX = register(
		Blocks.RED_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item BLACK_SHULKER_BOX = register(
		Blocks.BLACK_SHULKER_BOX, new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)
	);
	public static final Item WHITE_GLAZED_TERRACOTTA = register(Blocks.WHITE_GLAZED_TERRACOTTA);
	public static final Item ORANGE_GLAZED_TERRACOTTA = register(Blocks.ORANGE_GLAZED_TERRACOTTA);
	public static final Item MAGENTA_GLAZED_TERRACOTTA = register(Blocks.MAGENTA_GLAZED_TERRACOTTA);
	public static final Item LIGHT_BLUE_GLAZED_TERRACOTTA = register(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
	public static final Item YELLOW_GLAZED_TERRACOTTA = register(Blocks.YELLOW_GLAZED_TERRACOTTA);
	public static final Item LIME_GLAZED_TERRACOTTA = register(Blocks.LIME_GLAZED_TERRACOTTA);
	public static final Item PINK_GLAZED_TERRACOTTA = register(Blocks.PINK_GLAZED_TERRACOTTA);
	public static final Item GRAY_GLAZED_TERRACOTTA = register(Blocks.GRAY_GLAZED_TERRACOTTA);
	public static final Item LIGHT_GRAY_GLAZED_TERRACOTTA = register(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
	public static final Item CYAN_GLAZED_TERRACOTTA = register(Blocks.CYAN_GLAZED_TERRACOTTA);
	public static final Item PURPLE_GLAZED_TERRACOTTA = register(Blocks.PURPLE_GLAZED_TERRACOTTA);
	public static final Item BLUE_GLAZED_TERRACOTTA = register(Blocks.BLUE_GLAZED_TERRACOTTA);
	public static final Item BROWN_GLAZED_TERRACOTTA = register(Blocks.BROWN_GLAZED_TERRACOTTA);
	public static final Item GREEN_GLAZED_TERRACOTTA = register(Blocks.GREEN_GLAZED_TERRACOTTA);
	public static final Item RED_GLAZED_TERRACOTTA = register(Blocks.RED_GLAZED_TERRACOTTA);
	public static final Item BLACK_GLAZED_TERRACOTTA = register(Blocks.BLACK_GLAZED_TERRACOTTA);
	public static final Item WHITE_CONCRETE = register(Blocks.WHITE_CONCRETE);
	public static final Item ORANGE_CONCRETE = register(Blocks.ORANGE_CONCRETE);
	public static final Item MAGENTA_CONCRETE = register(Blocks.MAGENTA_CONCRETE);
	public static final Item LIGHT_BLUE_CONCRETE = register(Blocks.LIGHT_BLUE_CONCRETE);
	public static final Item YELLOW_CONCRETE = register(Blocks.YELLOW_CONCRETE);
	public static final Item LIME_CONCRETE = register(Blocks.LIME_CONCRETE);
	public static final Item PINK_CONCRETE = register(Blocks.PINK_CONCRETE);
	public static final Item GRAY_CONCRETE = register(Blocks.GRAY_CONCRETE);
	public static final Item LIGHT_GRAY_CONCRETE = register(Blocks.LIGHT_GRAY_CONCRETE);
	public static final Item CYAN_CONCRETE = register(Blocks.CYAN_CONCRETE);
	public static final Item PURPLE_CONCRETE = register(Blocks.PURPLE_CONCRETE);
	public static final Item BLUE_CONCRETE = register(Blocks.BLUE_CONCRETE);
	public static final Item BROWN_CONCRETE = register(Blocks.BROWN_CONCRETE);
	public static final Item GREEN_CONCRETE = register(Blocks.GREEN_CONCRETE);
	public static final Item RED_CONCRETE = register(Blocks.RED_CONCRETE);
	public static final Item BLACK_CONCRETE = register(Blocks.BLACK_CONCRETE);
	public static final Item WHITE_CONCRETE_POWDER = register(Blocks.WHITE_CONCRETE_POWDER);
	public static final Item ORANGE_CONCRETE_POWDER = register(Blocks.ORANGE_CONCRETE_POWDER);
	public static final Item MAGENTA_CONCRETE_POWDER = register(Blocks.MAGENTA_CONCRETE_POWDER);
	public static final Item LIGHT_BLUE_CONCRETE_POWDER = register(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
	public static final Item YELLOW_CONCRETE_POWDER = register(Blocks.YELLOW_CONCRETE_POWDER);
	public static final Item LIME_CONCRETE_POWDER = register(Blocks.LIME_CONCRETE_POWDER);
	public static final Item PINK_CONCRETE_POWDER = register(Blocks.PINK_CONCRETE_POWDER);
	public static final Item GRAY_CONCRETE_POWDER = register(Blocks.GRAY_CONCRETE_POWDER);
	public static final Item LIGHT_GRAY_CONCRETE_POWDER = register(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
	public static final Item CYAN_CONCRETE_POWDER = register(Blocks.CYAN_CONCRETE_POWDER);
	public static final Item PURPLE_CONCRETE_POWDER = register(Blocks.PURPLE_CONCRETE_POWDER);
	public static final Item BLUE_CONCRETE_POWDER = register(Blocks.BLUE_CONCRETE_POWDER);
	public static final Item BROWN_CONCRETE_POWDER = register(Blocks.BROWN_CONCRETE_POWDER);
	public static final Item GREEN_CONCRETE_POWDER = register(Blocks.GREEN_CONCRETE_POWDER);
	public static final Item RED_CONCRETE_POWDER = register(Blocks.RED_CONCRETE_POWDER);
	public static final Item BLACK_CONCRETE_POWDER = register(Blocks.BLACK_CONCRETE_POWDER);
	public static final Item TURTLE_EGG = register(Blocks.TURTLE_EGG);
	public static final Item SNIFFER_EGG = register(Blocks.SNIFFER_EGG, (UnaryOperator<Item.Settings>)(settings -> settings.rarity(Rarity.UNCOMMON)));
	public static final Item DEAD_TUBE_CORAL_BLOCK = register(Blocks.DEAD_TUBE_CORAL_BLOCK);
	public static final Item DEAD_BRAIN_CORAL_BLOCK = register(Blocks.DEAD_BRAIN_CORAL_BLOCK);
	public static final Item DEAD_BUBBLE_CORAL_BLOCK = register(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
	public static final Item DEAD_FIRE_CORAL_BLOCK = register(Blocks.DEAD_FIRE_CORAL_BLOCK);
	public static final Item DEAD_HORN_CORAL_BLOCK = register(Blocks.DEAD_HORN_CORAL_BLOCK);
	public static final Item TUBE_CORAL_BLOCK = register(Blocks.TUBE_CORAL_BLOCK);
	public static final Item BRAIN_CORAL_BLOCK = register(Blocks.BRAIN_CORAL_BLOCK);
	public static final Item BUBBLE_CORAL_BLOCK = register(Blocks.BUBBLE_CORAL_BLOCK);
	public static final Item FIRE_CORAL_BLOCK = register(Blocks.FIRE_CORAL_BLOCK);
	public static final Item HORN_CORAL_BLOCK = register(Blocks.HORN_CORAL_BLOCK);
	public static final Item TUBE_CORAL = register(Blocks.TUBE_CORAL);
	public static final Item BRAIN_CORAL = register(Blocks.BRAIN_CORAL);
	public static final Item BUBBLE_CORAL = register(Blocks.BUBBLE_CORAL);
	public static final Item FIRE_CORAL = register(Blocks.FIRE_CORAL);
	public static final Item HORN_CORAL = register(Blocks.HORN_CORAL);
	public static final Item DEAD_BRAIN_CORAL = register(Blocks.DEAD_BRAIN_CORAL);
	public static final Item DEAD_BUBBLE_CORAL = register(Blocks.DEAD_BUBBLE_CORAL);
	public static final Item DEAD_FIRE_CORAL = register(Blocks.DEAD_FIRE_CORAL);
	public static final Item DEAD_HORN_CORAL = register(Blocks.DEAD_HORN_CORAL);
	public static final Item DEAD_TUBE_CORAL = register(Blocks.DEAD_TUBE_CORAL);
	public static final Item TUBE_CORAL_FAN = register(
		Blocks.TUBE_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.TUBE_CORAL_WALL_FAN, Direction.DOWN, settings))
	);
	public static final Item BRAIN_CORAL_FAN = register(
		Blocks.BRAIN_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.BRAIN_CORAL_WALL_FAN, Direction.DOWN, settings))
	);
	public static final Item BUBBLE_CORAL_FAN = register(
		Blocks.BUBBLE_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.BUBBLE_CORAL_WALL_FAN, Direction.DOWN, settings))
	);
	public static final Item FIRE_CORAL_FAN = register(
		Blocks.FIRE_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.FIRE_CORAL_WALL_FAN, Direction.DOWN, settings))
	);
	public static final Item HORN_CORAL_FAN = register(
		Blocks.HORN_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.HORN_CORAL_WALL_FAN, Direction.DOWN, settings))
	);
	public static final Item DEAD_TUBE_CORAL_FAN = register(
		Blocks.DEAD_TUBE_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(
				block, Blocks.DEAD_TUBE_CORAL_WALL_FAN, Direction.DOWN, settings
			))
	);
	public static final Item DEAD_BRAIN_CORAL_FAN = register(
		Blocks.DEAD_BRAIN_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(
				block, Blocks.DEAD_BRAIN_CORAL_WALL_FAN, Direction.DOWN, settings
			))
	);
	public static final Item DEAD_BUBBLE_CORAL_FAN = register(
		Blocks.DEAD_BUBBLE_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(
				block, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, Direction.DOWN, settings
			))
	);
	public static final Item DEAD_FIRE_CORAL_FAN = register(
		Blocks.DEAD_FIRE_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(
				block, Blocks.DEAD_FIRE_CORAL_WALL_FAN, Direction.DOWN, settings
			))
	);
	public static final Item DEAD_HORN_CORAL_FAN = register(
		Blocks.DEAD_HORN_CORAL_FAN,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(
				block, Blocks.DEAD_HORN_CORAL_WALL_FAN, Direction.DOWN, settings
			))
	);
	public static final Item BLUE_ICE = register(Blocks.BLUE_ICE);
	public static final Item CONDUIT = register(Blocks.CONDUIT, new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item POLISHED_GRANITE_STAIRS = register(Blocks.POLISHED_GRANITE_STAIRS);
	public static final Item SMOOTH_RED_SANDSTONE_STAIRS = register(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
	public static final Item MOSSY_STONE_BRICK_STAIRS = register(Blocks.MOSSY_STONE_BRICK_STAIRS);
	public static final Item POLISHED_DIORITE_STAIRS = register(Blocks.POLISHED_DIORITE_STAIRS);
	public static final Item MOSSY_COBBLESTONE_STAIRS = register(Blocks.MOSSY_COBBLESTONE_STAIRS);
	public static final Item END_STONE_BRICK_STAIRS = register(Blocks.END_STONE_BRICK_STAIRS);
	public static final Item STONE_STAIRS = register(Blocks.STONE_STAIRS);
	public static final Item SMOOTH_SANDSTONE_STAIRS = register(Blocks.SMOOTH_SANDSTONE_STAIRS);
	public static final Item SMOOTH_QUARTZ_STAIRS = register(Blocks.SMOOTH_QUARTZ_STAIRS);
	public static final Item GRANITE_STAIRS = register(Blocks.GRANITE_STAIRS);
	public static final Item ANDESITE_STAIRS = register(Blocks.ANDESITE_STAIRS);
	public static final Item RED_NETHER_BRICK_STAIRS = register(Blocks.RED_NETHER_BRICK_STAIRS);
	public static final Item POLISHED_ANDESITE_STAIRS = register(Blocks.POLISHED_ANDESITE_STAIRS);
	public static final Item DIORITE_STAIRS = register(Blocks.DIORITE_STAIRS);
	public static final Item COBBLED_DEEPSLATE_STAIRS = register(Blocks.COBBLED_DEEPSLATE_STAIRS);
	public static final Item POLISHED_DEEPSLATE_STAIRS = register(Blocks.POLISHED_DEEPSLATE_STAIRS);
	public static final Item DEEPSLATE_BRICK_STAIRS = register(Blocks.DEEPSLATE_BRICK_STAIRS);
	public static final Item DEEPSLATE_TILE_STAIRS = register(Blocks.DEEPSLATE_TILE_STAIRS);
	public static final Item POLISHED_GRANITE_SLAB = register(Blocks.POLISHED_GRANITE_SLAB);
	public static final Item SMOOTH_RED_SANDSTONE_SLAB = register(Blocks.SMOOTH_RED_SANDSTONE_SLAB);
	public static final Item MOSSY_STONE_BRICK_SLAB = register(Blocks.MOSSY_STONE_BRICK_SLAB);
	public static final Item POLISHED_DIORITE_SLAB = register(Blocks.POLISHED_DIORITE_SLAB);
	public static final Item MOSSY_COBBLESTONE_SLAB = register(Blocks.MOSSY_COBBLESTONE_SLAB);
	public static final Item END_STONE_BRICK_SLAB = register(Blocks.END_STONE_BRICK_SLAB);
	public static final Item SMOOTH_SANDSTONE_SLAB = register(Blocks.SMOOTH_SANDSTONE_SLAB);
	public static final Item SMOOTH_QUARTZ_SLAB = register(Blocks.SMOOTH_QUARTZ_SLAB);
	public static final Item GRANITE_SLAB = register(Blocks.GRANITE_SLAB);
	public static final Item ANDESITE_SLAB = register(Blocks.ANDESITE_SLAB);
	public static final Item RED_NETHER_BRICK_SLAB = register(Blocks.RED_NETHER_BRICK_SLAB);
	public static final Item POLISHED_ANDESITE_SLAB = register(Blocks.POLISHED_ANDESITE_SLAB);
	public static final Item DIORITE_SLAB = register(Blocks.DIORITE_SLAB);
	public static final Item COBBLED_DEEPSLATE_SLAB = register(Blocks.COBBLED_DEEPSLATE_SLAB);
	public static final Item POLISHED_DEEPSLATE_SLAB = register(Blocks.POLISHED_DEEPSLATE_SLAB);
	public static final Item DEEPSLATE_BRICK_SLAB = register(Blocks.DEEPSLATE_BRICK_SLAB);
	public static final Item DEEPSLATE_TILE_SLAB = register(Blocks.DEEPSLATE_TILE_SLAB);
	public static final Item SCAFFOLDING = register(Blocks.SCAFFOLDING, ScaffoldingItem::new);
	public static final Item REDSTONE = register("redstone", createBlockItemWithUniqueName(Blocks.REDSTONE_WIRE));
	public static final Item REDSTONE_TORCH = register(
		Blocks.REDSTONE_TORCH,
		(BiFunction<Block, Item.Settings, Item>)((block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.REDSTONE_WALL_TORCH, Direction.DOWN, settings))
	);
	public static final Item REDSTONE_BLOCK = register(Blocks.REDSTONE_BLOCK);
	public static final Item REPEATER = register(Blocks.REPEATER);
	public static final Item COMPARATOR = register(Blocks.COMPARATOR);
	public static final Item PISTON = register(Blocks.PISTON);
	public static final Item STICKY_PISTON = register(Blocks.STICKY_PISTON);
	public static final Item SLIME_BLOCK = register(Blocks.SLIME_BLOCK);
	public static final Item HONEY_BLOCK = register(Blocks.HONEY_BLOCK);
	public static final Item OBSERVER = register(Blocks.OBSERVER);
	public static final Item HOPPER = register(
		Blocks.HOPPER, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item DISPENSER = register(
		Blocks.DISPENSER, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item DROPPER = register(
		Blocks.DROPPER, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item LECTERN = register(Blocks.LECTERN);
	public static final Item TARGET = register(Blocks.TARGET);
	public static final Item LEVER = register(Blocks.LEVER);
	public static final Item LIGHTNING_ROD = register(Blocks.LIGHTNING_ROD);
	public static final Item DAYLIGHT_DETECTOR = register(Blocks.DAYLIGHT_DETECTOR);
	public static final Item SCULK_SENSOR = register(Blocks.SCULK_SENSOR);
	public static final Item CALIBRATED_SCULK_SENSOR = register(Blocks.CALIBRATED_SCULK_SENSOR);
	public static final Item TRIPWIRE_HOOK = register(Blocks.TRIPWIRE_HOOK);
	public static final Item TRAPPED_CHEST = register(
		Blocks.TRAPPED_CHEST, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item TNT = register(Blocks.TNT);
	public static final Item REDSTONE_LAMP = register(Blocks.REDSTONE_LAMP);
	public static final Item NOTE_BLOCK = register(Blocks.NOTE_BLOCK);
	public static final Item STONE_BUTTON = register(Blocks.STONE_BUTTON);
	public static final Item POLISHED_BLACKSTONE_BUTTON = register(Blocks.POLISHED_BLACKSTONE_BUTTON);
	public static final Item OAK_BUTTON = register(Blocks.OAK_BUTTON);
	public static final Item SPRUCE_BUTTON = register(Blocks.SPRUCE_BUTTON);
	public static final Item BIRCH_BUTTON = register(Blocks.BIRCH_BUTTON);
	public static final Item JUNGLE_BUTTON = register(Blocks.JUNGLE_BUTTON);
	public static final Item ACACIA_BUTTON = register(Blocks.ACACIA_BUTTON);
	public static final Item CHERRY_BUTTON = register(Blocks.CHERRY_BUTTON);
	public static final Item DARK_OAK_BUTTON = register(Blocks.DARK_OAK_BUTTON);
	public static final Item MANGROVE_BUTTON = register(Blocks.MANGROVE_BUTTON);
	public static final Item BAMBOO_BUTTON = register(Blocks.BAMBOO_BUTTON);
	public static final Item CRIMSON_BUTTON = register(Blocks.CRIMSON_BUTTON);
	public static final Item WARPED_BUTTON = register(Blocks.WARPED_BUTTON);
	public static final Item STONE_PRESSURE_PLATE = register(Blocks.STONE_PRESSURE_PLATE);
	public static final Item POLISHED_BLACKSTONE_PRESSURE_PLATE = register(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
	public static final Item LIGHT_WEIGHTED_PRESSURE_PLATE = register(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
	public static final Item HEAVY_WEIGHTED_PRESSURE_PLATE = register(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
	public static final Item OAK_PRESSURE_PLATE = register(Blocks.OAK_PRESSURE_PLATE);
	public static final Item SPRUCE_PRESSURE_PLATE = register(Blocks.SPRUCE_PRESSURE_PLATE);
	public static final Item BIRCH_PRESSURE_PLATE = register(Blocks.BIRCH_PRESSURE_PLATE);
	public static final Item JUNGLE_PRESSURE_PLATE = register(Blocks.JUNGLE_PRESSURE_PLATE);
	public static final Item ACACIA_PRESSURE_PLATE = register(Blocks.ACACIA_PRESSURE_PLATE);
	public static final Item CHERRY_PRESSURE_PLATE = register(Blocks.CHERRY_PRESSURE_PLATE);
	public static final Item DARK_OAK_PRESSURE_PLATE = register(Blocks.DARK_OAK_PRESSURE_PLATE);
	public static final Item MANGROVE_PRESSURE_PLATE = register(Blocks.MANGROVE_PRESSURE_PLATE);
	public static final Item BAMBOO_PRESSURE_PLATE = register(Blocks.BAMBOO_PRESSURE_PLATE);
	public static final Item CRIMSON_PRESSURE_PLATE = register(Blocks.CRIMSON_PRESSURE_PLATE);
	public static final Item WARPED_PRESSURE_PLATE = register(Blocks.WARPED_PRESSURE_PLATE);
	public static final Item IRON_DOOR = register(Blocks.IRON_DOOR, TallBlockItem::new);
	public static final Item OAK_DOOR = register(Blocks.OAK_DOOR, TallBlockItem::new);
	public static final Item SPRUCE_DOOR = register(Blocks.SPRUCE_DOOR, TallBlockItem::new);
	public static final Item BIRCH_DOOR = register(Blocks.BIRCH_DOOR, TallBlockItem::new);
	public static final Item JUNGLE_DOOR = register(Blocks.JUNGLE_DOOR, TallBlockItem::new);
	public static final Item ACACIA_DOOR = register(Blocks.ACACIA_DOOR, TallBlockItem::new);
	public static final Item CHERRY_DOOR = register(Blocks.CHERRY_DOOR, TallBlockItem::new);
	public static final Item DARK_OAK_DOOR = register(Blocks.DARK_OAK_DOOR, TallBlockItem::new);
	public static final Item MANGROVE_DOOR = register(Blocks.MANGROVE_DOOR, TallBlockItem::new);
	public static final Item BAMBOO_DOOR = register(Blocks.BAMBOO_DOOR, TallBlockItem::new);
	public static final Item CRIMSON_DOOR = register(Blocks.CRIMSON_DOOR, TallBlockItem::new);
	public static final Item WARPED_DOOR = register(Blocks.WARPED_DOOR, TallBlockItem::new);
	public static final Item COPPER_DOOR = register(Blocks.COPPER_DOOR, TallBlockItem::new);
	public static final Item EXPOSED_COPPER_DOOR = register(Blocks.EXPOSED_COPPER_DOOR, TallBlockItem::new);
	public static final Item WEATHERED_COPPER_DOOR = register(Blocks.WEATHERED_COPPER_DOOR, TallBlockItem::new);
	public static final Item OXIDIZED_COPPER_DOOR = register(Blocks.OXIDIZED_COPPER_DOOR, TallBlockItem::new);
	public static final Item WAXED_COPPER_DOOR = register(Blocks.WAXED_COPPER_DOOR, TallBlockItem::new);
	public static final Item WAXED_EXPOSED_COPPER_DOOR = register(Blocks.WAXED_EXPOSED_COPPER_DOOR, TallBlockItem::new);
	public static final Item WAXED_WEATHERED_COPPER_DOOR = register(Blocks.WAXED_WEATHERED_COPPER_DOOR, TallBlockItem::new);
	public static final Item WAXED_OXIDIZED_COPPER_DOOR = register(Blocks.WAXED_OXIDIZED_COPPER_DOOR, TallBlockItem::new);
	public static final Item IRON_TRAPDOOR = register(Blocks.IRON_TRAPDOOR);
	public static final Item OAK_TRAPDOOR = register(Blocks.OAK_TRAPDOOR);
	public static final Item SPRUCE_TRAPDOOR = register(Blocks.SPRUCE_TRAPDOOR);
	public static final Item BIRCH_TRAPDOOR = register(Blocks.BIRCH_TRAPDOOR);
	public static final Item JUNGLE_TRAPDOOR = register(Blocks.JUNGLE_TRAPDOOR);
	public static final Item ACACIA_TRAPDOOR = register(Blocks.ACACIA_TRAPDOOR);
	public static final Item CHERRY_TRAPDOOR = register(Blocks.CHERRY_TRAPDOOR);
	public static final Item DARK_OAK_TRAPDOOR = register(Blocks.DARK_OAK_TRAPDOOR);
	public static final Item MANGROVE_TRAPDOOR = register(Blocks.MANGROVE_TRAPDOOR);
	public static final Item BAMBOO_TRAPDOOR = register(Blocks.BAMBOO_TRAPDOOR);
	public static final Item CRIMSON_TRAPDOOR = register(Blocks.CRIMSON_TRAPDOOR);
	public static final Item WARPED_TRAPDOOR = register(Blocks.WARPED_TRAPDOOR);
	public static final Item COPPER_TRAPDOOR = register(Blocks.COPPER_TRAPDOOR);
	public static final Item EXPOSED_COPPER_TRAPDOOR = register(Blocks.EXPOSED_COPPER_TRAPDOOR);
	public static final Item WEATHERED_COPPER_TRAPDOOR = register(Blocks.WEATHERED_COPPER_TRAPDOOR);
	public static final Item OXIDIZED_COPPER_TRAPDOOR = register(Blocks.OXIDIZED_COPPER_TRAPDOOR);
	public static final Item WAXED_COPPER_TRAPDOOR = register(Blocks.WAXED_COPPER_TRAPDOOR);
	public static final Item WAXED_EXPOSED_COPPER_TRAPDOOR = register(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
	public static final Item WAXED_WEATHERED_COPPER_TRAPDOOR = register(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
	public static final Item WAXED_OXIDIZED_COPPER_TRAPDOOR = register(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
	public static final Item OAK_FENCE_GATE = register(Blocks.OAK_FENCE_GATE);
	public static final Item SPRUCE_FENCE_GATE = register(Blocks.SPRUCE_FENCE_GATE);
	public static final Item BIRCH_FENCE_GATE = register(Blocks.BIRCH_FENCE_GATE);
	public static final Item JUNGLE_FENCE_GATE = register(Blocks.JUNGLE_FENCE_GATE);
	public static final Item ACACIA_FENCE_GATE = register(Blocks.ACACIA_FENCE_GATE);
	public static final Item CHERRY_FENCE_GATE = register(Blocks.CHERRY_FENCE_GATE);
	public static final Item DARK_OAK_FENCE_GATE = register(Blocks.DARK_OAK_FENCE_GATE);
	public static final Item MANGROVE_FENCE_GATE = register(Blocks.MANGROVE_FENCE_GATE);
	public static final Item BAMBOO_FENCE_GATE = register(Blocks.BAMBOO_FENCE_GATE);
	public static final Item CRIMSON_FENCE_GATE = register(Blocks.CRIMSON_FENCE_GATE);
	public static final Item WARPED_FENCE_GATE = register(Blocks.WARPED_FENCE_GATE);
	public static final Item POWERED_RAIL = register(Blocks.POWERED_RAIL);
	public static final Item DETECTOR_RAIL = register(Blocks.DETECTOR_RAIL);
	public static final Item RAIL = register(Blocks.RAIL);
	public static final Item ACTIVATOR_RAIL = register(Blocks.ACTIVATOR_RAIL);
	public static final Item SADDLE = register("saddle", SaddleItem::new, new Item.Settings().maxCount(1));
	public static final Item MINECART = register(
		"minecart", settings -> new MinecartItem(AbstractMinecartEntity.Type.RIDEABLE, settings), new Item.Settings().maxCount(1)
	);
	public static final Item CHEST_MINECART = register(
		"chest_minecart", settings -> new MinecartItem(AbstractMinecartEntity.Type.CHEST, settings), new Item.Settings().maxCount(1)
	);
	public static final Item FURNACE_MINECART = register(
		"furnace_minecart", settings -> new MinecartItem(AbstractMinecartEntity.Type.FURNACE, settings), new Item.Settings().maxCount(1)
	);
	public static final Item TNT_MINECART = register(
		"tnt_minecart", settings -> new MinecartItem(AbstractMinecartEntity.Type.TNT, settings), new Item.Settings().maxCount(1)
	);
	public static final Item HOPPER_MINECART = register(
		"hopper_minecart", settings -> new MinecartItem(AbstractMinecartEntity.Type.HOPPER, settings), new Item.Settings().maxCount(1)
	);
	public static final Item CARROT_ON_A_STICK = register(
		"carrot_on_a_stick", settings -> new OnAStickItem<>(EntityType.PIG, 7, settings), new Item.Settings().maxDamage(25)
	);
	public static final Item WARPED_FUNGUS_ON_A_STICK = register(
		"warped_fungus_on_a_stick", settings -> new OnAStickItem<>(EntityType.STRIDER, 1, settings), new Item.Settings().maxDamage(100)
	);
	public static final Item PHANTOM_MEMBRANE = register("phantom_membrane");
	public static final Item ELYTRA = register(
		"elytra",
		new Item.Settings()
			.maxDamage(432)
			.rarity(Rarity.EPIC)
			.component(DataComponentTypes.GLIDER, Unit.INSTANCE)
			.equippable(EquipmentSlot.CHEST, SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, EquipmentModels.ELYTRA)
			.repairable(PHANTOM_MEMBRANE)
	);
	public static final Item OAK_BOAT = register("oak_boat", settings -> new BoatItem(false, BoatEntity.Type.OAK, settings), new Item.Settings().maxCount(1));
	public static final Item OAK_CHEST_BOAT = register(
		"oak_chest_boat", settings -> new BoatItem(true, BoatEntity.Type.OAK, settings), new Item.Settings().maxCount(1)
	);
	public static final Item SPRUCE_BOAT = register(
		"spruce_boat", settings -> new BoatItem(false, BoatEntity.Type.SPRUCE, settings), new Item.Settings().maxCount(1)
	);
	public static final Item SPRUCE_CHEST_BOAT = register(
		"spruce_chest_boat", settings -> new BoatItem(true, BoatEntity.Type.SPRUCE, settings), new Item.Settings().maxCount(1)
	);
	public static final Item BIRCH_BOAT = register("birch_boat", settings -> new BoatItem(false, BoatEntity.Type.BIRCH, settings), new Item.Settings().maxCount(1));
	public static final Item BIRCH_CHEST_BOAT = register(
		"birch_chest_boat", settings -> new BoatItem(true, BoatEntity.Type.BIRCH, settings), new Item.Settings().maxCount(1)
	);
	public static final Item JUNGLE_BOAT = register(
		"jungle_boat", settings -> new BoatItem(false, BoatEntity.Type.JUNGLE, settings), new Item.Settings().maxCount(1)
	);
	public static final Item JUNGLE_CHEST_BOAT = register(
		"jungle_chest_boat", settings -> new BoatItem(true, BoatEntity.Type.JUNGLE, settings), new Item.Settings().maxCount(1)
	);
	public static final Item ACACIA_BOAT = register(
		"acacia_boat", settings -> new BoatItem(false, BoatEntity.Type.ACACIA, settings), new Item.Settings().maxCount(1)
	);
	public static final Item ACACIA_CHEST_BOAT = register(
		"acacia_chest_boat", settings -> new BoatItem(true, BoatEntity.Type.ACACIA, settings), new Item.Settings().maxCount(1)
	);
	public static final Item CHERRY_BOAT = register(
		"cherry_boat", settings -> new BoatItem(false, BoatEntity.Type.CHERRY, settings), new Item.Settings().maxCount(1)
	);
	public static final Item CHERRY_CHEST_BOAT = register(
		"cherry_chest_boat", settings -> new BoatItem(true, BoatEntity.Type.CHERRY, settings), new Item.Settings().maxCount(1)
	);
	public static final Item DARK_OAK_BOAT = register(
		"dark_oak_boat", settings -> new BoatItem(false, BoatEntity.Type.DARK_OAK, settings), new Item.Settings().maxCount(1)
	);
	public static final Item DARK_OAK_CHEST_BOAT = register(
		"dark_oak_chest_boat", settings -> new BoatItem(true, BoatEntity.Type.DARK_OAK, settings), new Item.Settings().maxCount(1)
	);
	public static final Item MANGROVE_BOAT = register(
		"mangrove_boat", settings -> new BoatItem(false, BoatEntity.Type.MANGROVE, settings), new Item.Settings().maxCount(1)
	);
	public static final Item MANGROVE_CHEST_BOAT = register(
		"mangrove_chest_boat", settings -> new BoatItem(true, BoatEntity.Type.MANGROVE, settings), new Item.Settings().maxCount(1)
	);
	public static final Item BAMBOO_RAFT = register(
		"bamboo_raft", settings -> new BoatItem(false, BoatEntity.Type.BAMBOO, settings), new Item.Settings().maxCount(1)
	);
	public static final Item BAMBOO_CHEST_RAFT = register(
		"bamboo_chest_raft", settings -> new BoatItem(true, BoatEntity.Type.BAMBOO, settings), new Item.Settings().maxCount(1)
	);
	public static final Item STRUCTURE_BLOCK = register(Blocks.STRUCTURE_BLOCK, OperatorOnlyBlockItem::new, new Item.Settings().rarity(Rarity.EPIC));
	public static final Item JIGSAW = register(Blocks.JIGSAW, OperatorOnlyBlockItem::new, new Item.Settings().rarity(Rarity.EPIC));
	public static final Item TURTLE_HELMET = register("turtle_helmet", settings -> new ArmorItem(ArmorMaterials.TURTLE_SCUTE, EquipmentType.HELMET, settings));
	public static final Item TURTLE_SCUTE = register("turtle_scute");
	public static final Item ARMADILLO_SCUTE = register("armadillo_scute");
	public static final Item WOLF_ARMOR = register(
		"wolf_armor", settings -> new AnimalArmorItem(ArmorMaterials.ARMADILLO_SCUTE, AnimalArmorItem.Type.CANINE, settings)
	);
	public static final Item FLINT_AND_STEEL = register("flint_and_steel", FlintAndSteelItem::new, new Item.Settings().maxDamage(64));
	public static final Item BOWL = register("bowl");
	public static final Item APPLE = register("apple", new Item.Settings().food(FoodComponents.APPLE));
	public static final Item BOW = register("bow", BowItem::new, new Item.Settings().maxDamage(384).enchantable(1));
	public static final Item ARROW = register("arrow", ArrowItem::new);
	public static final Item COAL = register("coal");
	public static final Item CHARCOAL = register("charcoal");
	public static final Item DIAMOND = register("diamond");
	public static final Item EMERALD = register("emerald");
	public static final Item LAPIS_LAZULI = register("lapis_lazuli");
	public static final Item QUARTZ = register("quartz");
	public static final Item AMETHYST_SHARD = register("amethyst_shard");
	public static final Item RAW_IRON = register("raw_iron");
	public static final Item IRON_INGOT = register("iron_ingot");
	public static final Item RAW_COPPER = register("raw_copper");
	public static final Item COPPER_INGOT = register("copper_ingot");
	public static final Item RAW_GOLD = register("raw_gold");
	public static final Item GOLD_INGOT = register("gold_ingot");
	public static final Item NETHERITE_INGOT = register("netherite_ingot", new Item.Settings().fireproof());
	public static final Item NETHERITE_SCRAP = register("netherite_scrap", new Item.Settings().fireproof());
	public static final Item WOODEN_SWORD = register("wooden_sword", settings -> new SwordItem(ToolMaterial.WOOD, 3.0F, -2.4F, settings));
	public static final Item WOODEN_SHOVEL = register("wooden_shovel", settings -> new ShovelItem(ToolMaterial.WOOD, 1.5F, -3.0F, settings));
	public static final Item WOODEN_PICKAXE = register("wooden_pickaxe", settings -> new PickaxeItem(ToolMaterial.WOOD, 1.0F, -2.8F, settings));
	public static final Item WOODEN_AXE = register("wooden_axe", settings -> new AxeItem(ToolMaterial.WOOD, 6.0F, -3.2F, settings));
	public static final Item WOODEN_HOE = register("wooden_hoe", settings -> new HoeItem(ToolMaterial.WOOD, 0.0F, -3.0F, settings));
	public static final Item STONE_SWORD = register("stone_sword", settings -> new SwordItem(ToolMaterial.STONE, 3.0F, -2.4F, settings));
	public static final Item STONE_SHOVEL = register("stone_shovel", settings -> new ShovelItem(ToolMaterial.STONE, 1.5F, -3.0F, settings));
	public static final Item STONE_PICKAXE = register("stone_pickaxe", settings -> new PickaxeItem(ToolMaterial.STONE, 1.0F, -2.8F, settings));
	public static final Item STONE_AXE = register("stone_axe", settings -> new AxeItem(ToolMaterial.STONE, 7.0F, -3.2F, settings));
	public static final Item STONE_HOE = register("stone_hoe", settings -> new HoeItem(ToolMaterial.STONE, -1.0F, -2.0F, settings));
	public static final Item GOLDEN_SWORD = register("golden_sword", settings -> new SwordItem(ToolMaterial.GOLD, 3.0F, -2.4F, settings));
	public static final Item GOLDEN_SHOVEL = register("golden_shovel", settings -> new ShovelItem(ToolMaterial.GOLD, 1.5F, -3.0F, settings));
	public static final Item GOLDEN_PICKAXE = register("golden_pickaxe", settings -> new PickaxeItem(ToolMaterial.GOLD, 1.0F, -2.8F, settings));
	public static final Item GOLDEN_AXE = register("golden_axe", settings -> new AxeItem(ToolMaterial.GOLD, 6.0F, -3.0F, settings));
	public static final Item GOLDEN_HOE = register("golden_hoe", settings -> new HoeItem(ToolMaterial.GOLD, 0.0F, -3.0F, settings));
	public static final Item IRON_SWORD = register("iron_sword", settings -> new SwordItem(ToolMaterial.IRON, 3.0F, -2.4F, settings));
	public static final Item IRON_SHOVEL = register("iron_shovel", settings -> new ShovelItem(ToolMaterial.IRON, 1.5F, -3.0F, settings));
	public static final Item IRON_PICKAXE = register("iron_pickaxe", settings -> new PickaxeItem(ToolMaterial.IRON, 1.0F, -2.8F, settings));
	public static final Item IRON_AXE = register("iron_axe", settings -> new AxeItem(ToolMaterial.IRON, 6.0F, -3.1F, settings));
	public static final Item IRON_HOE = register("iron_hoe", settings -> new HoeItem(ToolMaterial.IRON, -2.0F, -1.0F, settings));
	public static final Item DIAMOND_SWORD = register("diamond_sword", settings -> new SwordItem(ToolMaterial.DIAMOND, 3.0F, -2.4F, settings));
	public static final Item DIAMOND_SHOVEL = register("diamond_shovel", settings -> new ShovelItem(ToolMaterial.DIAMOND, 1.5F, -3.0F, settings));
	public static final Item DIAMOND_PICKAXE = register("diamond_pickaxe", settings -> new PickaxeItem(ToolMaterial.DIAMOND, 1.0F, -2.8F, settings));
	public static final Item DIAMOND_AXE = register("diamond_axe", settings -> new AxeItem(ToolMaterial.DIAMOND, 5.0F, -3.0F, settings));
	public static final Item DIAMOND_HOE = register("diamond_hoe", settings -> new HoeItem(ToolMaterial.DIAMOND, -3.0F, 0.0F, settings));
	public static final Item NETHERITE_SWORD = register(
		"netherite_sword", settings -> new SwordItem(ToolMaterial.NETHERITE, 3.0F, -2.4F, settings), new Item.Settings().fireproof()
	);
	public static final Item NETHERITE_SHOVEL = register(
		"netherite_shovel", settings -> new ShovelItem(ToolMaterial.NETHERITE, 1.5F, -3.0F, settings), new Item.Settings().fireproof()
	);
	public static final Item NETHERITE_PICKAXE = register(
		"netherite_pickaxe", settings -> new PickaxeItem(ToolMaterial.NETHERITE, 1.0F, -2.8F, settings), new Item.Settings().fireproof()
	);
	public static final Item NETHERITE_AXE = register(
		"netherite_axe", settings -> new AxeItem(ToolMaterial.NETHERITE, 5.0F, -3.0F, settings), new Item.Settings().fireproof()
	);
	public static final Item NETHERITE_HOE = register(
		"netherite_hoe", settings -> new HoeItem(ToolMaterial.NETHERITE, -4.0F, 0.0F, settings), new Item.Settings().fireproof()
	);
	public static final Item STICK = register("stick");
	public static final Item MUSHROOM_STEW = register("mushroom_stew", new Item.Settings().maxCount(1).food(FoodComponents.MUSHROOM_STEW).useRemainder(BOWL));
	public static final Item STRING = register("string", createBlockItemWithUniqueName(Blocks.TRIPWIRE));
	public static final Item FEATHER = register("feather");
	public static final Item GUNPOWDER = register("gunpowder");
	public static final Item WHEAT_SEEDS = register("wheat_seeds", createBlockItemWithUniqueName(Blocks.WHEAT));
	public static final Item WHEAT = register("wheat");
	public static final Item BREAD = register("bread", new Item.Settings().food(FoodComponents.BREAD));
	public static final Item LEATHER_HELMET = register("leather_helmet", settings -> new ArmorItem(ArmorMaterials.LEATHER, EquipmentType.HELMET, settings));
	public static final Item LEATHER_CHESTPLATE = register(
		"leather_chestplate", settings -> new ArmorItem(ArmorMaterials.LEATHER, EquipmentType.CHESTPLATE, settings)
	);
	public static final Item LEATHER_LEGGINGS = register("leather_leggings", settings -> new ArmorItem(ArmorMaterials.LEATHER, EquipmentType.LEGGINGS, settings));
	public static final Item LEATHER_BOOTS = register("leather_boots", settings -> new ArmorItem(ArmorMaterials.LEATHER, EquipmentType.BOOTS, settings));
	public static final Item CHAINMAIL_HELMET = register(
		"chainmail_helmet", settings -> new ArmorItem(ArmorMaterials.CHAIN, EquipmentType.HELMET, settings), new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item CHAINMAIL_CHESTPLATE = register(
		"chainmail_chestplate", settings -> new ArmorItem(ArmorMaterials.CHAIN, EquipmentType.CHESTPLATE, settings), new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item CHAINMAIL_LEGGINGS = register(
		"chainmail_leggings", settings -> new ArmorItem(ArmorMaterials.CHAIN, EquipmentType.LEGGINGS, settings), new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item CHAINMAIL_BOOTS = register(
		"chainmail_boots", settings -> new ArmorItem(ArmorMaterials.CHAIN, EquipmentType.BOOTS, settings), new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item IRON_HELMET = register("iron_helmet", settings -> new ArmorItem(ArmorMaterials.IRON, EquipmentType.HELMET, settings));
	public static final Item IRON_CHESTPLATE = register("iron_chestplate", settings -> new ArmorItem(ArmorMaterials.IRON, EquipmentType.CHESTPLATE, settings));
	public static final Item IRON_LEGGINGS = register("iron_leggings", settings -> new ArmorItem(ArmorMaterials.IRON, EquipmentType.LEGGINGS, settings));
	public static final Item IRON_BOOTS = register("iron_boots", settings -> new ArmorItem(ArmorMaterials.IRON, EquipmentType.BOOTS, settings));
	public static final Item DIAMOND_HELMET = register("diamond_helmet", settings -> new ArmorItem(ArmorMaterials.DIAMOND, EquipmentType.HELMET, settings));
	public static final Item DIAMOND_CHESTPLATE = register(
		"diamond_chestplate", settings -> new ArmorItem(ArmorMaterials.DIAMOND, EquipmentType.CHESTPLATE, settings)
	);
	public static final Item DIAMOND_LEGGINGS = register("diamond_leggings", settings -> new ArmorItem(ArmorMaterials.DIAMOND, EquipmentType.LEGGINGS, settings));
	public static final Item DIAMOND_BOOTS = register("diamond_boots", settings -> new ArmorItem(ArmorMaterials.DIAMOND, EquipmentType.BOOTS, settings));
	public static final Item GOLDEN_HELMET = register("golden_helmet", settings -> new ArmorItem(ArmorMaterials.GOLD, EquipmentType.HELMET, settings));
	public static final Item GOLDEN_CHESTPLATE = register("golden_chestplate", settings -> new ArmorItem(ArmorMaterials.GOLD, EquipmentType.CHESTPLATE, settings));
	public static final Item GOLDEN_LEGGINGS = register("golden_leggings", settings -> new ArmorItem(ArmorMaterials.GOLD, EquipmentType.LEGGINGS, settings));
	public static final Item GOLDEN_BOOTS = register("golden_boots", settings -> new ArmorItem(ArmorMaterials.GOLD, EquipmentType.BOOTS, settings));
	public static final Item NETHERITE_HELMET = register(
		"netherite_helmet", settings -> new ArmorItem(ArmorMaterials.NETHERITE, EquipmentType.HELMET, settings), new Item.Settings().fireproof()
	);
	public static final Item NETHERITE_CHESTPLATE = register(
		"netherite_chestplate", settings -> new ArmorItem(ArmorMaterials.NETHERITE, EquipmentType.CHESTPLATE, settings), new Item.Settings().fireproof()
	);
	public static final Item NETHERITE_LEGGINGS = register(
		"netherite_leggings", settings -> new ArmorItem(ArmorMaterials.NETHERITE, EquipmentType.LEGGINGS, settings), new Item.Settings().fireproof()
	);
	public static final Item NETHERITE_BOOTS = register(
		"netherite_boots", settings -> new ArmorItem(ArmorMaterials.NETHERITE, EquipmentType.BOOTS, settings), new Item.Settings().fireproof()
	);
	public static final Item FLINT = register("flint");
	public static final Item PORKCHOP = register("porkchop", new Item.Settings().food(FoodComponents.PORKCHOP));
	public static final Item COOKED_PORKCHOP = register("cooked_porkchop", new Item.Settings().food(FoodComponents.COOKED_PORKCHOP));
	public static final Item PAINTING = register("painting", settings -> new DecorationItem(EntityType.PAINTING, settings));
	public static final Item GOLDEN_APPLE = register("golden_apple", new Item.Settings().food(FoodComponents.GOLDEN_APPLE, ConsumableComponents.GOLDEN_APPLE));
	public static final Item ENCHANTED_GOLDEN_APPLE = register(
		"enchanted_golden_apple",
		new Item.Settings()
			.rarity(Rarity.RARE)
			.food(FoodComponents.ENCHANTED_GOLDEN_APPLE, ConsumableComponents.ENCHANTED_GOLDEN_APPLE)
			.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
	);
	public static final Item OAK_SIGN = register(
		Blocks.OAK_SIGN, (block, settings) -> new SignItem(block, Blocks.OAK_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item SPRUCE_SIGN = register(
		Blocks.SPRUCE_SIGN, (block, settings) -> new SignItem(block, Blocks.SPRUCE_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item BIRCH_SIGN = register(
		Blocks.BIRCH_SIGN, (block, settings) -> new SignItem(block, Blocks.BIRCH_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item JUNGLE_SIGN = register(
		Blocks.JUNGLE_SIGN, (block, settings) -> new SignItem(block, Blocks.JUNGLE_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item ACACIA_SIGN = register(
		Blocks.ACACIA_SIGN, (block, settings) -> new SignItem(block, Blocks.ACACIA_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item CHERRY_SIGN = register(
		Blocks.CHERRY_SIGN, (block, settings) -> new SignItem(block, Blocks.CHERRY_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item DARK_OAK_SIGN = register(
		Blocks.DARK_OAK_SIGN, (block, settings) -> new SignItem(block, Blocks.DARK_OAK_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item MANGROVE_SIGN = register(
		Blocks.MANGROVE_SIGN, (block, settings) -> new SignItem(block, Blocks.MANGROVE_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item BAMBOO_SIGN = register(
		Blocks.BAMBOO_SIGN, (block, settings) -> new SignItem(block, Blocks.BAMBOO_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item CRIMSON_SIGN = register(
		Blocks.CRIMSON_SIGN, (block, settings) -> new SignItem(block, Blocks.CRIMSON_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item WARPED_SIGN = register(
		Blocks.WARPED_SIGN, (block, settings) -> new SignItem(block, Blocks.WARPED_WALL_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item OAK_HANGING_SIGN = register(
		Blocks.OAK_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.OAK_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item SPRUCE_HANGING_SIGN = register(
		Blocks.SPRUCE_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.SPRUCE_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item BIRCH_HANGING_SIGN = register(
		Blocks.BIRCH_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.BIRCH_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item JUNGLE_HANGING_SIGN = register(
		Blocks.JUNGLE_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.JUNGLE_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item ACACIA_HANGING_SIGN = register(
		Blocks.ACACIA_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.ACACIA_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item CHERRY_HANGING_SIGN = register(
		Blocks.CHERRY_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.CHERRY_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item DARK_OAK_HANGING_SIGN = register(
		Blocks.DARK_OAK_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.DARK_OAK_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item MANGROVE_HANGING_SIGN = register(
		Blocks.MANGROVE_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.MANGROVE_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item BAMBOO_HANGING_SIGN = register(
		Blocks.BAMBOO_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.BAMBOO_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item CRIMSON_HANGING_SIGN = register(
		Blocks.CRIMSON_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.CRIMSON_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item WARPED_HANGING_SIGN = register(
		Blocks.WARPED_HANGING_SIGN, (block, settings) -> new HangingSignItem(block, Blocks.WARPED_WALL_HANGING_SIGN, settings), new Item.Settings().maxCount(16)
	);
	public static final Item BUCKET = register("bucket", settings -> new BucketItem(Fluids.EMPTY, settings), new Item.Settings().maxCount(16));
	public static final Item WATER_BUCKET = register(
		"water_bucket", settings -> new BucketItem(Fluids.WATER, settings), new Item.Settings().recipeRemainder(BUCKET).maxCount(1)
	);
	public static final Item LAVA_BUCKET = register(
		"lava_bucket", settings -> new BucketItem(Fluids.LAVA, settings), new Item.Settings().recipeRemainder(BUCKET).maxCount(1)
	);
	public static final Item POWDER_SNOW_BUCKET = register(
		"powder_snow_bucket",
		settings -> new PowderSnowBucketItem(Blocks.POWDER_SNOW, SoundEvents.ITEM_BUCKET_EMPTY_POWDER_SNOW, settings),
		new Item.Settings().maxCount(1).useItemPrefixedTranslationKey()
	);
	public static final Item SNOWBALL = register("snowball", SnowballItem::new, new Item.Settings().maxCount(16));
	public static final Item LEATHER = register("leather");
	public static final Item MILK_BUCKET = register(
		"milk_bucket",
		new Item.Settings().recipeRemainder(BUCKET).component(DataComponentTypes.CONSUMABLE, ConsumableComponents.MILK_BUCKET).useRemainder(BUCKET).maxCount(1)
	);
	public static final Item PUFFERFISH_BUCKET = register(
		"pufferfish_bucket",
		settings -> new EntityBucketItem(EntityType.PUFFERFISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, settings),
		new Item.Settings().maxCount(1).component(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT)
	);
	public static final Item SALMON_BUCKET = register(
		"salmon_bucket",
		settings -> new EntityBucketItem(EntityType.SALMON, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, settings),
		new Item.Settings().maxCount(1).component(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT)
	);
	public static final Item COD_BUCKET = register(
		"cod_bucket",
		settings -> new EntityBucketItem(EntityType.COD, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, settings),
		new Item.Settings().maxCount(1).component(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT)
	);
	public static final Item TROPICAL_FISH_BUCKET = register(
		"tropical_fish_bucket",
		settings -> new EntityBucketItem(EntityType.TROPICAL_FISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, settings),
		new Item.Settings().maxCount(1).component(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT)
	);
	public static final Item AXOLOTL_BUCKET = register(
		"axolotl_bucket",
		settings -> new EntityBucketItem(EntityType.AXOLOTL, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_AXOLOTL, settings),
		new Item.Settings().maxCount(1).component(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT)
	);
	public static final Item TADPOLE_BUCKET = register(
		"tadpole_bucket",
		settings -> new EntityBucketItem(EntityType.TADPOLE, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_TADPOLE, settings),
		new Item.Settings().maxCount(1).component(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT)
	);
	public static final Item BRICK = register("brick");
	public static final Item CLAY_BALL = register("clay_ball");
	public static final Item DRIED_KELP_BLOCK = register(Blocks.DRIED_KELP_BLOCK);
	public static final Item PAPER = register("paper");
	public static final Item BOOK = register("book", new Item.Settings().enchantable(1));
	public static final Item SLIME_BALL = register("slime_ball");
	public static final Item EGG = register("egg", EggItem::new, new Item.Settings().maxCount(16));
	public static final Item COMPASS = register("compass", CompassItem::new);
	public static final Item RECOVERY_COMPASS = register("recovery_compass", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item BUNDLE = register(
		"bundle",
		settings -> new BundleItem(Identifier.ofVanilla("bundle_open_front"), Identifier.ofVanilla("bundle_open_back"), settings),
		new Item.Settings().maxCount(1).requires(FeatureFlags.BUNDLE).component(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT)
	);
	public static final Item FISHING_ROD = register("fishing_rod", FishingRodItem::new, new Item.Settings().maxDamage(64).enchantable(1));
	public static final Item CLOCK = register("clock");
	public static final Item SPYGLASS = register("spyglass", SpyglassItem::new, new Item.Settings().maxCount(1).modelId(Identifier.ofVanilla("spyglass_in_hand")));
	public static final Item GLOWSTONE_DUST = register("glowstone_dust");
	public static final Item COD = register("cod", new Item.Settings().food(FoodComponents.COD));
	public static final Item SALMON = register("salmon", new Item.Settings().food(FoodComponents.SALMON));
	public static final Item TROPICAL_FISH = register("tropical_fish", new Item.Settings().food(FoodComponents.TROPICAL_FISH));
	public static final Item PUFFERFISH = register("pufferfish", new Item.Settings().food(FoodComponents.PUFFERFISH, ConsumableComponents.PUFFERFISH));
	public static final Item COOKED_COD = register("cooked_cod", new Item.Settings().food(FoodComponents.COOKED_COD));
	public static final Item COOKED_SALMON = register("cooked_salmon", new Item.Settings().food(FoodComponents.COOKED_SALMON));
	public static final Item INK_SAC = register("ink_sac", InkSacItem::new);
	public static final Item GLOW_INK_SAC = register("glow_ink_sac", GlowInkSacItem::new);
	public static final Item COCOA_BEANS = register("cocoa_beans", createBlockItemWithUniqueName(Blocks.COCOA));
	public static final Item WHITE_DYE = register("white_dye", settings -> new DyeItem(DyeColor.WHITE, settings));
	public static final Item ORANGE_DYE = register("orange_dye", settings -> new DyeItem(DyeColor.ORANGE, settings));
	public static final Item MAGENTA_DYE = register("magenta_dye", settings -> new DyeItem(DyeColor.MAGENTA, settings));
	public static final Item LIGHT_BLUE_DYE = register("light_blue_dye", settings -> new DyeItem(DyeColor.LIGHT_BLUE, settings));
	public static final Item YELLOW_DYE = register("yellow_dye", settings -> new DyeItem(DyeColor.YELLOW, settings));
	public static final Item LIME_DYE = register("lime_dye", settings -> new DyeItem(DyeColor.LIME, settings));
	public static final Item PINK_DYE = register("pink_dye", settings -> new DyeItem(DyeColor.PINK, settings));
	public static final Item GRAY_DYE = register("gray_dye", settings -> new DyeItem(DyeColor.GRAY, settings));
	public static final Item LIGHT_GRAY_DYE = register("light_gray_dye", settings -> new DyeItem(DyeColor.LIGHT_GRAY, settings));
	public static final Item CYAN_DYE = register("cyan_dye", settings -> new DyeItem(DyeColor.CYAN, settings));
	public static final Item PURPLE_DYE = register("purple_dye", settings -> new DyeItem(DyeColor.PURPLE, settings));
	public static final Item BLUE_DYE = register("blue_dye", settings -> new DyeItem(DyeColor.BLUE, settings));
	public static final Item BROWN_DYE = register("brown_dye", settings -> new DyeItem(DyeColor.BROWN, settings));
	public static final Item GREEN_DYE = register("green_dye", settings -> new DyeItem(DyeColor.GREEN, settings));
	public static final Item RED_DYE = register("red_dye", settings -> new DyeItem(DyeColor.RED, settings));
	public static final Item BLACK_DYE = register("black_dye", settings -> new DyeItem(DyeColor.BLACK, settings));
	public static final Item BONE_MEAL = register("bone_meal", BoneMealItem::new);
	public static final Item BONE = register("bone");
	public static final Item SUGAR = register("sugar");
	public static final Item CAKE = register(Blocks.CAKE, new Item.Settings().maxCount(1));
	public static final Item WHITE_BED = register(Blocks.WHITE_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item ORANGE_BED = register(Blocks.ORANGE_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item MAGENTA_BED = register(Blocks.MAGENTA_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item LIGHT_BLUE_BED = register(Blocks.LIGHT_BLUE_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item YELLOW_BED = register(Blocks.YELLOW_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item LIME_BED = register(Blocks.LIME_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item PINK_BED = register(Blocks.PINK_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item GRAY_BED = register(Blocks.GRAY_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item LIGHT_GRAY_BED = register(Blocks.LIGHT_GRAY_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item CYAN_BED = register(Blocks.CYAN_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item PURPLE_BED = register(Blocks.PURPLE_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item BLUE_BED = register(Blocks.BLUE_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item BROWN_BED = register(Blocks.BROWN_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item GREEN_BED = register(Blocks.GREEN_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item RED_BED = register(Blocks.RED_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item BLACK_BED = register(Blocks.BLACK_BED, BedItem::new, new Item.Settings().maxCount(1));
	public static final Item COOKIE = register("cookie", new Item.Settings().food(FoodComponents.COOKIE));
	public static final Item CRAFTER = register(
		Blocks.CRAFTER, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item FILLED_MAP = register(
		"filled_map",
		FilledMapItem::new,
		new Item.Settings()
			.component(DataComponentTypes.MAP_COLOR, MapColorComponent.DEFAULT)
			.component(DataComponentTypes.MAP_DECORATIONS, MapDecorationsComponent.DEFAULT)
	);
	public static final Item SHEARS = register(
		"shears", ShearsItem::new, new Item.Settings().maxDamage(238).component(DataComponentTypes.TOOL, ShearsItem.createToolComponent())
	);
	public static final Item MELON_SLICE = register("melon_slice", new Item.Settings().food(FoodComponents.MELON_SLICE));
	public static final Item DRIED_KELP = register("dried_kelp", new Item.Settings().food(FoodComponents.DRIED_KELP, ConsumableComponents.DRIED_KELP));
	public static final Item PUMPKIN_SEEDS = register(ItemKeys.PUMPKIN_SEEDS, createBlockItemWithUniqueName(Blocks.PUMPKIN_STEM));
	public static final Item MELON_SEEDS = register(ItemKeys.MELON_SEEDS, createBlockItemWithUniqueName(Blocks.MELON_STEM));
	public static final Item BEEF = register("beef", new Item.Settings().food(FoodComponents.BEEF));
	public static final Item COOKED_BEEF = register("cooked_beef", new Item.Settings().food(FoodComponents.COOKED_BEEF));
	public static final Item CHICKEN = register("chicken", new Item.Settings().food(FoodComponents.CHICKEN, ConsumableComponents.RAW_CHICKEN));
	public static final Item COOKED_CHICKEN = register("cooked_chicken", new Item.Settings().food(FoodComponents.COOKED_CHICKEN));
	public static final Item ROTTEN_FLESH = register("rotten_flesh", new Item.Settings().food(FoodComponents.ROTTEN_FLESH, ConsumableComponents.ROTTEN_FLESH));
	public static final Item ENDER_PEARL = register("ender_pearl", EnderPearlItem::new, new Item.Settings().maxCount(16).useCooldown(1.0F));
	public static final Item BLAZE_ROD = register("blaze_rod");
	public static final Item GHAST_TEAR = register("ghast_tear");
	public static final Item GOLD_NUGGET = register("gold_nugget");
	public static final Item NETHER_WART = register("nether_wart", createBlockItemWithUniqueName(Blocks.NETHER_WART));
	public static final Item GLASS_BOTTLE = register("glass_bottle", GlassBottleItem::new);
	public static final Item POTION = register(
		"potion",
		PotionItem::new,
		new Item.Settings()
			.maxCount(1)
			.component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
			.component(DataComponentTypes.CONSUMABLE, ConsumableComponents.DRINK)
			.useRemainder(GLASS_BOTTLE)
	);
	public static final Item SPIDER_EYE = register("spider_eye", new Item.Settings().food(FoodComponents.SPIDER_EYE, ConsumableComponents.SPIDER_EYE));
	public static final Item FERMENTED_SPIDER_EYE = register("fermented_spider_eye");
	public static final Item BLAZE_POWDER = register("blaze_powder");
	public static final Item MAGMA_CREAM = register("magma_cream");
	public static final Item BREWING_STAND = register(
		Blocks.BREWING_STAND, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item CAULDRON = register(Blocks.CAULDRON, Blocks.WATER_CAULDRON, Blocks.LAVA_CAULDRON, Blocks.POWDER_SNOW_CAULDRON);
	public static final Item ENDER_EYE = register("ender_eye", EnderEyeItem::new);
	public static final Item GLISTERING_MELON_SLICE = register("glistering_melon_slice");
	public static final Item ARMADILLO_SPAWN_EGG = register("armadillo_spawn_egg", settings -> new SpawnEggItem(EntityType.ARMADILLO, 11366765, 8538184, settings));
	public static final Item ALLAY_SPAWN_EGG = register("allay_spawn_egg", settings -> new SpawnEggItem(EntityType.ALLAY, 56063, 44543, settings));
	public static final Item AXOLOTL_SPAWN_EGG = register("axolotl_spawn_egg", settings -> new SpawnEggItem(EntityType.AXOLOTL, 16499171, 10890612, settings));
	public static final Item BAT_SPAWN_EGG = register("bat_spawn_egg", settings -> new SpawnEggItem(EntityType.BAT, 4996656, 986895, settings));
	public static final Item BEE_SPAWN_EGG = register("bee_spawn_egg", settings -> new SpawnEggItem(EntityType.BEE, 15582019, 4400155, settings));
	public static final Item BLAZE_SPAWN_EGG = register("blaze_spawn_egg", settings -> new SpawnEggItem(EntityType.BLAZE, 16167425, 16775294, settings));
	public static final Item BOGGED_SPAWN_EGG = register("bogged_spawn_egg", settings -> new SpawnEggItem(EntityType.BOGGED, 9084018, 3231003, settings));
	public static final Item BREEZE_SPAWN_EGG = register("breeze_spawn_egg", settings -> new SpawnEggItem(EntityType.BREEZE, 11506911, 9529055, settings));
	public static final Item CAT_SPAWN_EGG = register("cat_spawn_egg", settings -> new SpawnEggItem(EntityType.CAT, 15714446, 9794134, settings));
	public static final Item CAMEL_SPAWN_EGG = register("camel_spawn_egg", settings -> new SpawnEggItem(EntityType.CAMEL, 16565097, 13341495, settings));
	public static final Item CAVE_SPIDER_SPAWN_EGG = register(
		"cave_spider_spawn_egg", settings -> new SpawnEggItem(EntityType.CAVE_SPIDER, 803406, 11013646, settings)
	);
	public static final Item CHICKEN_SPAWN_EGG = register("chicken_spawn_egg", settings -> new SpawnEggItem(EntityType.CHICKEN, 10592673, 16711680, settings));
	public static final Item COD_SPAWN_EGG = register("cod_spawn_egg", settings -> new SpawnEggItem(EntityType.COD, 12691306, 15058059, settings));
	public static final Item COW_SPAWN_EGG = register("cow_spawn_egg", settings -> new SpawnEggItem(EntityType.COW, 4470310, 10592673, settings));
	public static final Item CREEPER_SPAWN_EGG = register("creeper_spawn_egg", settings -> new SpawnEggItem(EntityType.CREEPER, 894731, 0, settings));
	public static final Item DOLPHIN_SPAWN_EGG = register("dolphin_spawn_egg", settings -> new SpawnEggItem(EntityType.DOLPHIN, 2243405, 16382457, settings));
	public static final Item DONKEY_SPAWN_EGG = register("donkey_spawn_egg", settings -> new SpawnEggItem(EntityType.DONKEY, 5457209, 8811878, settings));
	public static final Item DROWNED_SPAWN_EGG = register("drowned_spawn_egg", settings -> new SpawnEggItem(EntityType.DROWNED, 9433559, 7969893, settings));
	public static final Item ELDER_GUARDIAN_SPAWN_EGG = register(
		"elder_guardian_spawn_egg", settings -> new SpawnEggItem(EntityType.ELDER_GUARDIAN, 13552826, 7632531, settings)
	);
	public static final Item ENDER_DRAGON_SPAWN_EGG = register(
		"ender_dragon_spawn_egg", settings -> new SpawnEggItem(EntityType.ENDER_DRAGON, 1842204, 14711290, settings)
	);
	public static final Item ENDERMAN_SPAWN_EGG = register("enderman_spawn_egg", settings -> new SpawnEggItem(EntityType.ENDERMAN, 1447446, 0, settings));
	public static final Item ENDERMITE_SPAWN_EGG = register("endermite_spawn_egg", settings -> new SpawnEggItem(EntityType.ENDERMITE, 1447446, 7237230, settings));
	public static final Item EVOKER_SPAWN_EGG = register("evoker_spawn_egg", settings -> new SpawnEggItem(EntityType.EVOKER, 9804699, 1973274, settings));
	public static final Item FOX_SPAWN_EGG = register("fox_spawn_egg", settings -> new SpawnEggItem(EntityType.FOX, 14005919, 13396256, settings));
	public static final Item FROG_SPAWN_EGG = register("frog_spawn_egg", settings -> new SpawnEggItem(EntityType.FROG, 13661252, 16762748, settings));
	public static final Item GHAST_SPAWN_EGG = register("ghast_spawn_egg", settings -> new SpawnEggItem(EntityType.GHAST, 16382457, 12369084, settings));
	public static final Item GLOW_SQUID_SPAWN_EGG = register(
		"glow_squid_spawn_egg", settings -> new SpawnEggItem(EntityType.GLOW_SQUID, 611926, 8778172, settings)
	);
	public static final Item GOAT_SPAWN_EGG = register("goat_spawn_egg", settings -> new SpawnEggItem(EntityType.GOAT, 10851452, 5589310, settings));
	public static final Item GUARDIAN_SPAWN_EGG = register("guardian_spawn_egg", settings -> new SpawnEggItem(EntityType.GUARDIAN, 5931634, 15826224, settings));
	public static final Item HOGLIN_SPAWN_EGG = register("hoglin_spawn_egg", settings -> new SpawnEggItem(EntityType.HOGLIN, 13004373, 6251620, settings));
	public static final Item HORSE_SPAWN_EGG = register("horse_spawn_egg", settings -> new SpawnEggItem(EntityType.HORSE, 12623485, 15656192, settings));
	public static final Item HUSK_SPAWN_EGG = register("husk_spawn_egg", settings -> new SpawnEggItem(EntityType.HUSK, 7958625, 15125652, settings));
	public static final Item IRON_GOLEM_SPAWN_EGG = register(
		"iron_golem_spawn_egg", settings -> new SpawnEggItem(EntityType.IRON_GOLEM, 14405058, 7643954, settings)
	);
	public static final Item LLAMA_SPAWN_EGG = register("llama_spawn_egg", settings -> new SpawnEggItem(EntityType.LLAMA, 12623485, 10051392, settings));
	public static final Item MAGMA_CUBE_SPAWN_EGG = register(
		"magma_cube_spawn_egg", settings -> new SpawnEggItem(EntityType.MAGMA_CUBE, 3407872, 16579584, settings)
	);
	public static final Item MOOSHROOM_SPAWN_EGG = register(
		"mooshroom_spawn_egg", settings -> new SpawnEggItem(EntityType.MOOSHROOM, 10489616, 12040119, settings)
	);
	public static final Item MULE_SPAWN_EGG = register("mule_spawn_egg", settings -> new SpawnEggItem(EntityType.MULE, 1769984, 5321501, settings));
	public static final Item OCELOT_SPAWN_EGG = register("ocelot_spawn_egg", settings -> new SpawnEggItem(EntityType.OCELOT, 15720061, 5653556, settings));
	public static final Item PANDA_SPAWN_EGG = register("panda_spawn_egg", settings -> new SpawnEggItem(EntityType.PANDA, 15198183, 1776418, settings));
	public static final Item PARROT_SPAWN_EGG = register("parrot_spawn_egg", settings -> new SpawnEggItem(EntityType.PARROT, 894731, 16711680, settings));
	public static final Item PHANTOM_SPAWN_EGG = register("phantom_spawn_egg", settings -> new SpawnEggItem(EntityType.PHANTOM, 4411786, 8978176, settings));
	public static final Item PIG_SPAWN_EGG = register("pig_spawn_egg", settings -> new SpawnEggItem(EntityType.PIG, 15771042, 14377823, settings));
	public static final Item PIGLIN_SPAWN_EGG = register("piglin_spawn_egg", settings -> new SpawnEggItem(EntityType.PIGLIN, 10051392, 16380836, settings));
	public static final Item PIGLIN_BRUTE_SPAWN_EGG = register(
		"piglin_brute_spawn_egg", settings -> new SpawnEggItem(EntityType.PIGLIN_BRUTE, 5843472, 16380836, settings)
	);
	public static final Item PILLAGER_SPAWN_EGG = register("pillager_spawn_egg", settings -> new SpawnEggItem(EntityType.PILLAGER, 5451574, 9804699, settings));
	public static final Item POLAR_BEAR_SPAWN_EGG = register(
		"polar_bear_spawn_egg", settings -> new SpawnEggItem(EntityType.POLAR_BEAR, 15658718, 14014157, settings)
	);
	public static final Item PUFFERFISH_SPAWN_EGG = register(
		"pufferfish_spawn_egg", settings -> new SpawnEggItem(EntityType.PUFFERFISH, 16167425, 3654642, settings)
	);
	public static final Item RABBIT_SPAWN_EGG = register("rabbit_spawn_egg", settings -> new SpawnEggItem(EntityType.RABBIT, 10051392, 7555121, settings));
	public static final Item RAVAGER_SPAWN_EGG = register("ravager_spawn_egg", settings -> new SpawnEggItem(EntityType.RAVAGER, 7697520, 5984329, settings));
	public static final Item SALMON_SPAWN_EGG = register("salmon_spawn_egg", settings -> new SpawnEggItem(EntityType.SALMON, 10489616, 951412, settings));
	public static final Item SHEEP_SPAWN_EGG = register("sheep_spawn_egg", settings -> new SpawnEggItem(EntityType.SHEEP, 15198183, 16758197, settings));
	public static final Item SHULKER_SPAWN_EGG = register("shulker_spawn_egg", settings -> new SpawnEggItem(EntityType.SHULKER, 9725844, 5060690, settings));
	public static final Item SILVERFISH_SPAWN_EGG = register(
		"silverfish_spawn_egg", settings -> new SpawnEggItem(EntityType.SILVERFISH, 7237230, 3158064, settings)
	);
	public static final Item SKELETON_SPAWN_EGG = register("skeleton_spawn_egg", settings -> new SpawnEggItem(EntityType.SKELETON, 12698049, 4802889, settings));
	public static final Item SKELETON_HORSE_SPAWN_EGG = register(
		"skeleton_horse_spawn_egg", settings -> new SpawnEggItem(EntityType.SKELETON_HORSE, 6842447, 15066584, settings)
	);
	public static final Item SLIME_SPAWN_EGG = register("slime_spawn_egg", settings -> new SpawnEggItem(EntityType.SLIME, 5349438, 8306542, settings));
	public static final Item SNIFFER_SPAWN_EGG = register("sniffer_spawn_egg", settings -> new SpawnEggItem(EntityType.SNIFFER, 8855049, 2468720, settings));
	public static final Item SNOW_GOLEM_SPAWN_EGG = register(
		"snow_golem_spawn_egg", settings -> new SpawnEggItem(EntityType.SNOW_GOLEM, 14283506, 8496292, settings)
	);
	public static final Item SPIDER_SPAWN_EGG = register("spider_spawn_egg", settings -> new SpawnEggItem(EntityType.SPIDER, 3419431, 11013646, settings));
	public static final Item SQUID_SPAWN_EGG = register("squid_spawn_egg", settings -> new SpawnEggItem(EntityType.SQUID, 2243405, 7375001, settings));
	public static final Item STRAY_SPAWN_EGG = register("stray_spawn_egg", settings -> new SpawnEggItem(EntityType.STRAY, 6387319, 14543594, settings));
	public static final Item STRIDER_SPAWN_EGG = register("strider_spawn_egg", settings -> new SpawnEggItem(EntityType.STRIDER, 10236982, 5065037, settings));
	public static final Item TADPOLE_SPAWN_EGG = register("tadpole_spawn_egg", settings -> new SpawnEggItem(EntityType.TADPOLE, 7164733, 1444352, settings));
	public static final Item TRADER_LLAMA_SPAWN_EGG = register(
		"trader_llama_spawn_egg", settings -> new SpawnEggItem(EntityType.TRADER_LLAMA, 15377456, 4547222, settings)
	);
	public static final Item TROPICAL_FISH_SPAWN_EGG = register(
		"tropical_fish_spawn_egg", settings -> new SpawnEggItem(EntityType.TROPICAL_FISH, 15690005, 16775663, settings)
	);
	public static final Item TURTLE_SPAWN_EGG = register("turtle_spawn_egg", settings -> new SpawnEggItem(EntityType.TURTLE, 15198183, 44975, settings));
	public static final Item VEX_SPAWN_EGG = register("vex_spawn_egg", settings -> new SpawnEggItem(EntityType.VEX, 8032420, 15265265, settings));
	public static final Item VILLAGER_SPAWN_EGG = register("villager_spawn_egg", settings -> new SpawnEggItem(EntityType.VILLAGER, 5651507, 12422002, settings));
	public static final Item VINDICATOR_SPAWN_EGG = register(
		"vindicator_spawn_egg", settings -> new SpawnEggItem(EntityType.VINDICATOR, 9804699, 2580065, settings)
	);
	public static final Item WANDERING_TRADER_SPAWN_EGG = register(
		"wandering_trader_spawn_egg", settings -> new SpawnEggItem(EntityType.WANDERING_TRADER, 4547222, 15377456, settings)
	);
	public static final Item WARDEN_SPAWN_EGG = register("warden_spawn_egg", settings -> new SpawnEggItem(EntityType.WARDEN, 1001033, 3790560, settings));
	public static final Item WITCH_SPAWN_EGG = register("witch_spawn_egg", settings -> new SpawnEggItem(EntityType.WITCH, 3407872, 5349438, settings));
	public static final Item WITHER_SPAWN_EGG = register("wither_spawn_egg", settings -> new SpawnEggItem(EntityType.WITHER, 1315860, 5075616, settings));
	public static final Item WITHER_SKELETON_SPAWN_EGG = register(
		"wither_skeleton_spawn_egg", settings -> new SpawnEggItem(EntityType.WITHER_SKELETON, 1315860, 4672845, settings)
	);
	public static final Item WOLF_SPAWN_EGG = register("wolf_spawn_egg", settings -> new SpawnEggItem(EntityType.WOLF, 14144467, 13545366, settings));
	public static final Item ZOGLIN_SPAWN_EGG = register("zoglin_spawn_egg", settings -> new SpawnEggItem(EntityType.ZOGLIN, 13004373, 15132390, settings));
	public static final Item ZOMBIE_SPAWN_EGG = register("zombie_spawn_egg", settings -> new SpawnEggItem(EntityType.ZOMBIE, 44975, 7969893, settings));
	public static final Item ZOMBIE_HORSE_SPAWN_EGG = register(
		"zombie_horse_spawn_egg", settings -> new SpawnEggItem(EntityType.ZOMBIE_HORSE, 3232308, 9945732, settings)
	);
	public static final Item ZOMBIE_VILLAGER_SPAWN_EGG = register(
		"zombie_villager_spawn_egg", settings -> new SpawnEggItem(EntityType.ZOMBIE_VILLAGER, 5651507, 7969893, settings)
	);
	public static final Item ZOMBIFIED_PIGLIN_SPAWN_EGG = register(
		"zombified_piglin_spawn_egg", settings -> new SpawnEggItem(EntityType.ZOMBIFIED_PIGLIN, 15373203, 5009705, settings)
	);
	public static final Item EXPERIENCE_BOTTLE = register(
		"experience_bottle", ExperienceBottleItem::new, new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
	);
	public static final Item FIRE_CHARGE = register("fire_charge", FireChargeItem::new);
	public static final Item WIND_CHARGE = register("wind_charge", WindChargeItem::new, new Item.Settings().useCooldown(0.5F));
	public static final Item WRITABLE_BOOK = register(
		"writable_book",
		WritableBookItem::new,
		new Item.Settings().maxCount(1).component(DataComponentTypes.WRITABLE_BOOK_CONTENT, WritableBookContentComponent.DEFAULT)
	);
	public static final Item WRITTEN_BOOK = register(
		"written_book", WrittenBookItem::new, new Item.Settings().maxCount(16).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
	);
	public static final Item BREEZE_ROD = register("breeze_rod");
	public static final Item MACE = register(
		"mace",
		MaceItem::new,
		new Item.Settings()
			.rarity(Rarity.EPIC)
			.maxDamage(500)
			.component(DataComponentTypes.TOOL, MaceItem.createToolComponent())
			.repairable(BREEZE_ROD)
			.attributeModifiers(MaceItem.createAttributeModifiers())
			.enchantable(15)
	);
	public static final Item ITEM_FRAME = register("item_frame", settings -> new ItemFrameItem(EntityType.ITEM_FRAME, settings));
	public static final Item GLOW_ITEM_FRAME = register("glow_item_frame", settings -> new ItemFrameItem(EntityType.GLOW_ITEM_FRAME, settings));
	public static final Item FLOWER_POT = register(Blocks.FLOWER_POT);
	public static final Item CARROT = register("carrot", createBlockItemWithUniqueName(Blocks.CARROTS), new Item.Settings().food(FoodComponents.CARROT));
	public static final Item POTATO = register("potato", createBlockItemWithUniqueName(Blocks.POTATOES), new Item.Settings().food(FoodComponents.POTATO));
	public static final Item BAKED_POTATO = register("baked_potato", new Item.Settings().food(FoodComponents.BAKED_POTATO));
	public static final Item POISONOUS_POTATO = register(
		"poisonous_potato", new Item.Settings().food(FoodComponents.POISONOUS_POTATO, ConsumableComponents.POISONOUS_POTATO)
	);
	public static final Item MAP = register("map", EmptyMapItem::new);
	public static final Item GOLDEN_CARROT = register("golden_carrot", new Item.Settings().food(FoodComponents.GOLDEN_CARROT));
	public static final Item SKELETON_SKULL = register(
		Blocks.SKELETON_SKULL,
		(block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.SKELETON_WALL_SKULL, Direction.DOWN, settings),
		new Item.Settings().rarity(Rarity.UNCOMMON).equippable(EquipmentSlot.HEAD)
	);
	public static final Item WITHER_SKELETON_SKULL = register(
		Blocks.WITHER_SKELETON_SKULL,
		(block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.WITHER_SKELETON_WALL_SKULL, Direction.DOWN, settings),
		new Item.Settings().rarity(Rarity.RARE).equippable(EquipmentSlot.HEAD)
	);
	public static final Item PLAYER_HEAD = register(
		Blocks.PLAYER_HEAD,
		(block, settings) -> new PlayerHeadItem(block, Blocks.PLAYER_WALL_HEAD, settings),
		new Item.Settings().rarity(Rarity.UNCOMMON).equippable(EquipmentSlot.HEAD)
	);
	public static final Item ZOMBIE_HEAD = register(
		Blocks.ZOMBIE_HEAD,
		(block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.ZOMBIE_WALL_HEAD, Direction.DOWN, settings),
		new Item.Settings().rarity(Rarity.UNCOMMON).equippable(EquipmentSlot.HEAD)
	);
	public static final Item CREEPER_HEAD = register(
		Blocks.CREEPER_HEAD,
		(block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.CREEPER_WALL_HEAD, Direction.DOWN, settings),
		new Item.Settings().rarity(Rarity.UNCOMMON).equippable(EquipmentSlot.HEAD)
	);
	public static final Item DRAGON_HEAD = register(
		Blocks.DRAGON_HEAD,
		(block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.DRAGON_WALL_HEAD, Direction.DOWN, settings),
		new Item.Settings().rarity(Rarity.EPIC).equippable(EquipmentSlot.HEAD)
	);
	public static final Item PIGLIN_HEAD = register(
		Blocks.PIGLIN_HEAD,
		(block, settings) -> new VerticallyAttachableBlockItem(block, Blocks.PIGLIN_WALL_HEAD, Direction.DOWN, settings),
		new Item.Settings().rarity(Rarity.UNCOMMON).equippable(EquipmentSlot.HEAD)
	);
	public static final Item NETHER_STAR = register(
		"nether_star", new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
	);
	public static final Item PUMPKIN_PIE = register("pumpkin_pie", new Item.Settings().food(FoodComponents.PUMPKIN_PIE));
	public static final Item FIREWORK_ROCKET = register(
		"firework_rocket", FireworkRocketItem::new, new Item.Settings().component(DataComponentTypes.FIREWORKS, new FireworksComponent(1, List.of()))
	);
	public static final Item FIREWORK_STAR = register("firework_star", FireworkStarItem::new);
	public static final Item ENCHANTED_BOOK = register(
		"enchanted_book",
		new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.UNCOMMON)
			.component(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
			.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
	);
	public static final Item NETHER_BRICK = register("nether_brick");
	public static final Item PRISMARINE_SHARD = register("prismarine_shard");
	public static final Item PRISMARINE_CRYSTALS = register("prismarine_crystals");
	public static final Item RABBIT = register("rabbit", new Item.Settings().food(FoodComponents.RABBIT));
	public static final Item COOKED_RABBIT = register("cooked_rabbit", new Item.Settings().food(FoodComponents.COOKED_RABBIT));
	public static final Item RABBIT_STEW = register("rabbit_stew", new Item.Settings().maxCount(1).food(FoodComponents.RABBIT_STEW).useRemainder(BOWL));
	public static final Item RABBIT_FOOT = register("rabbit_foot");
	public static final Item RABBIT_HIDE = register("rabbit_hide");
	public static final Item ARMOR_STAND = register("armor_stand", ArmorStandItem::new, new Item.Settings().maxCount(16));
	public static final Item IRON_HORSE_ARMOR = register(
		"iron_horse_armor", settings -> new AnimalArmorItem(ArmorMaterials.IRON, AnimalArmorItem.Type.EQUESTRIAN, settings), new Item.Settings().maxCount(1)
	);
	public static final Item GOLDEN_HORSE_ARMOR = register(
		"golden_horse_armor", settings -> new AnimalArmorItem(ArmorMaterials.GOLD, AnimalArmorItem.Type.EQUESTRIAN, settings), new Item.Settings().maxCount(1)
	);
	public static final Item DIAMOND_HORSE_ARMOR = register(
		"diamond_horse_armor", settings -> new AnimalArmorItem(ArmorMaterials.DIAMOND, AnimalArmorItem.Type.EQUESTRIAN, settings), new Item.Settings().maxCount(1)
	);
	public static final Item LEATHER_HORSE_ARMOR = register(
		"leather_horse_armor", settings -> new AnimalArmorItem(ArmorMaterials.LEATHER, AnimalArmorItem.Type.EQUESTRIAN, settings), new Item.Settings().maxCount(1)
	);
	public static final Item LEAD = register("lead", LeadItem::new);
	public static final Item NAME_TAG = register("name_tag", NameTagItem::new);
	public static final Item COMMAND_BLOCK_MINECART = register(
		"command_block_minecart",
		settings -> new MinecartItem(AbstractMinecartEntity.Type.COMMAND_BLOCK, settings),
		new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
	);
	public static final Item MUTTON = register("mutton", new Item.Settings().food(FoodComponents.MUTTON));
	public static final Item COOKED_MUTTON = register("cooked_mutton", new Item.Settings().food(FoodComponents.COOKED_MUTTON));
	public static final Item WHITE_BANNER = register(
		Blocks.WHITE_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.WHITE_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item ORANGE_BANNER = register(
		Blocks.ORANGE_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.ORANGE_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item MAGENTA_BANNER = register(
		Blocks.MAGENTA_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.MAGENTA_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item LIGHT_BLUE_BANNER = register(
		Blocks.LIGHT_BLUE_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.LIGHT_BLUE_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item YELLOW_BANNER = register(
		Blocks.YELLOW_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.YELLOW_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item LIME_BANNER = register(
		Blocks.LIME_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.LIME_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item PINK_BANNER = register(
		Blocks.PINK_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.PINK_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item GRAY_BANNER = register(
		Blocks.GRAY_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.GRAY_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item LIGHT_GRAY_BANNER = register(
		Blocks.LIGHT_GRAY_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.LIGHT_GRAY_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item CYAN_BANNER = register(
		Blocks.CYAN_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.CYAN_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item PURPLE_BANNER = register(
		Blocks.PURPLE_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.PURPLE_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item BLUE_BANNER = register(
		Blocks.BLUE_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.BLUE_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item BROWN_BANNER = register(
		Blocks.BROWN_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.BROWN_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item GREEN_BANNER = register(
		Blocks.GREEN_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.GREEN_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item RED_BANNER = register(
		Blocks.RED_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.RED_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item BLACK_BANNER = register(
		Blocks.BLACK_BANNER,
		(block, settings) -> new BannerItem(block, Blocks.BLACK_WALL_BANNER, settings),
		new Item.Settings().maxCount(16).component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
	);
	public static final Item END_CRYSTAL = register(
		"end_crystal", EndCrystalItem::new, new Item.Settings().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
	);
	public static final Item CHORUS_FRUIT = register(
		"chorus_fruit", new Item.Settings().food(FoodComponents.CHORUS_FRUIT, ConsumableComponents.CHORUS_FRUIT).useCooldown(1.0F)
	);
	public static final Item POPPED_CHORUS_FRUIT = register("popped_chorus_fruit");
	public static final Item TORCHFLOWER_SEEDS = register("torchflower_seeds", createBlockItemWithUniqueName(Blocks.TORCHFLOWER_CROP));
	public static final Item PITCHER_POD = register("pitcher_pod", createBlockItemWithUniqueName(Blocks.PITCHER_CROP));
	public static final Item BEETROOT = register("beetroot", new Item.Settings().food(FoodComponents.BEETROOT));
	public static final Item BEETROOT_SEEDS = register("beetroot_seeds", createBlockItemWithUniqueName(Blocks.BEETROOTS));
	public static final Item BEETROOT_SOUP = register("beetroot_soup", new Item.Settings().maxCount(1).food(FoodComponents.BEETROOT_SOUP).useRemainder(BOWL));
	public static final Item DRAGON_BREATH = register("dragon_breath", new Item.Settings().recipeRemainder(GLASS_BOTTLE).rarity(Rarity.UNCOMMON));
	public static final Item SPLASH_POTION = register(
		"splash_potion", SplashPotionItem::new, new Item.Settings().maxCount(1).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
	);
	public static final Item SPECTRAL_ARROW = register("spectral_arrow", SpectralArrowItem::new);
	public static final Item TIPPED_ARROW = register(
		"tipped_arrow", TippedArrowItem::new, new Item.Settings().component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
	);
	public static final Item LINGERING_POTION = register(
		"lingering_potion", LingeringPotionItem::new, new Item.Settings().maxCount(1).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
	);
	public static final Item SHIELD = register(
		"shield",
		ShieldItem::new,
		new Item.Settings()
			.maxDamage(336)
			.component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
			.repairable(ItemTags.WOODEN_TOOL_MATERIALS)
			.equippable(EquipmentSlot.OFFHAND)
	);
	public static final Item TOTEM_OF_UNDYING = register("totem_of_undying", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
	public static final Item SHULKER_SHELL = register("shulker_shell");
	public static final Item IRON_NUGGET = register("iron_nugget");
	public static final Item KNOWLEDGE_BOOK = register(
		"knowledge_book", KnowledgeBookItem::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).component(DataComponentTypes.RECIPES, List.of())
	);
	public static final Item DEBUG_STICK = register(
		"debug_stick",
		DebugStickItem::new,
		new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.EPIC)
			.component(DataComponentTypes.DEBUG_STICK_STATE, DebugStickStateComponent.DEFAULT)
			.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
	);
	public static final Item MUSIC_DISC_13 = register(
		"music_disc_13", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.THIRTEEN)
	);
	public static final Item MUSIC_DISC_CAT = register("music_disc_cat", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.CAT));
	public static final Item MUSIC_DISC_BLOCKS = register(
		"music_disc_blocks", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.BLOCKS)
	);
	public static final Item MUSIC_DISC_CHIRP = register(
		"music_disc_chirp", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.CHIRP)
	);
	public static final Item MUSIC_DISC_CREATOR = register(
		"music_disc_creator", new Item.Settings().maxCount(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongs.CREATOR)
	);
	public static final Item MUSIC_DISC_CREATOR_MUSIC_BOX = register(
		"music_disc_creator_music_box", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.CREATOR_MUSIC_BOX)
	);
	public static final Item MUSIC_DISC_FAR = register("music_disc_far", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.FAR));
	public static final Item MUSIC_DISC_MALL = register(
		"music_disc_mall", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.MALL)
	);
	public static final Item MUSIC_DISC_MELLOHI = register(
		"music_disc_mellohi", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.MELLOHI)
	);
	public static final Item MUSIC_DISC_STAL = register(
		"music_disc_stal", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.STAL)
	);
	public static final Item MUSIC_DISC_STRAD = register(
		"music_disc_strad", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.STRAD)
	);
	public static final Item MUSIC_DISC_WARD = register(
		"music_disc_ward", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.WARD)
	);
	public static final Item MUSIC_DISC_11 = register(
		"music_disc_11", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.ELEVEN)
	);
	public static final Item MUSIC_DISC_WAIT = register(
		"music_disc_wait", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.WAIT)
	);
	public static final Item MUSIC_DISC_OTHERSIDE = register(
		"music_disc_otherside", new Item.Settings().maxCount(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongs.OTHERSIDE)
	);
	public static final Item MUSIC_DISC_RELIC = register(
		"music_disc_relic", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.RELIC)
	);
	public static final Item MUSIC_DISC_5 = register("music_disc_5", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.FIVE));
	public static final Item MUSIC_DISC_PIGSTEP = register(
		"music_disc_pigstep", new Item.Settings().maxCount(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongs.PIGSTEP)
	);
	public static final Item MUSIC_DISC_PRECIPICE = register(
		"music_disc_precipice", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.PRECIPICE)
	);
	public static final Item DISC_FRAGMENT_5 = register("disc_fragment_5", DiscFragmentItem::new, new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item TRIDENT = register(
		"trident",
		TridentItem::new,
		new Item.Settings()
			.rarity(Rarity.RARE)
			.maxDamage(250)
			.attributeModifiers(TridentItem.createAttributeModifiers())
			.component(DataComponentTypes.TOOL, TridentItem.createToolComponent())
			.enchantable(1)
			.modelId(Identifier.ofVanilla("trident_in_hand"))
	);
	public static final Item NAUTILUS_SHELL = register("nautilus_shell", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item HEART_OF_THE_SEA = register("heart_of_the_sea", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item CROSSBOW = register(
		"crossbow",
		CrossbowItem::new,
		new Item.Settings().maxCount(1).maxDamage(465).component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT).enchantable(1)
	);
	public static final Item SUSPICIOUS_STEW = register(
		"suspicious_stew",
		new Item.Settings()
			.maxCount(1)
			.food(FoodComponents.SUSPICIOUS_STEW)
			.component(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffectsComponent.DEFAULT)
			.useRemainder(BOWL)
	);
	public static final Item LOOM = register(Blocks.LOOM);
	public static final Item FLOWER_BANNER_PATTERN = register(
		"flower_banner_pattern", settings -> new BannerPatternItem(BannerPatternTags.FLOWER_PATTERN_ITEM, settings), new Item.Settings().maxCount(1)
	);
	public static final Item CREEPER_BANNER_PATTERN = register(
		"creeper_banner_pattern",
		settings -> new BannerPatternItem(BannerPatternTags.CREEPER_PATTERN_ITEM, settings),
		new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
	);
	public static final Item SKULL_BANNER_PATTERN = register(
		"skull_banner_pattern",
		settings -> new BannerPatternItem(BannerPatternTags.SKULL_PATTERN_ITEM, settings),
		new Item.Settings().maxCount(1).rarity(Rarity.RARE)
	);
	public static final Item MOJANG_BANNER_PATTERN = register(
		"mojang_banner_pattern",
		settings -> new BannerPatternItem(BannerPatternTags.MOJANG_PATTERN_ITEM, settings),
		new Item.Settings().maxCount(1).rarity(Rarity.RARE)
	);
	public static final Item GLOBE_BANNER_PATTERN = register(
		"globe_banner_pattern", settings -> new BannerPatternItem(BannerPatternTags.GLOBE_PATTERN_ITEM, settings), new Item.Settings().maxCount(1)
	);
	public static final Item PIGLIN_BANNER_PATTERN = register(
		"piglin_banner_pattern",
		settings -> new BannerPatternItem(BannerPatternTags.PIGLIN_PATTERN_ITEM, settings),
		new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
	);
	public static final Item FLOW_BANNER_PATTERN = register(
		"flow_banner_pattern", settings -> new BannerPatternItem(BannerPatternTags.FLOW_PATTERN_ITEM, settings), new Item.Settings().maxCount(1).rarity(Rarity.RARE)
	);
	public static final Item GUSTER_BANNER_PATTERN = register(
		"guster_banner_pattern",
		settings -> new BannerPatternItem(BannerPatternTags.GUSTER_PATTERN_ITEM, settings),
		new Item.Settings().maxCount(1).rarity(Rarity.RARE)
	);
	public static final Item FIELD_MASONED_BANNER_PATTERN = register(
		"field_masoned_banner_pattern", settings -> new BannerPatternItem(BannerPatternTags.FIELD_MASONED_PATTERN_ITEM, settings), new Item.Settings().maxCount(1)
	);
	public static final Item BORDURE_INDENTED_BANNER_PATTERN = register(
		"bordure_indented_banner_pattern",
		settings -> new BannerPatternItem(BannerPatternTags.BORDURE_INDENTED_PATTERN_ITEM, settings),
		new Item.Settings().maxCount(1)
	);
	public static final Item GOAT_HORN = register(
		"goat_horn", settings -> new GoatHornItem(InstrumentTags.GOAT_HORNS, settings), new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(1)
	);
	public static final Item COMPOSTER = register(Blocks.COMPOSTER);
	public static final Item BARREL = register(
		Blocks.BARREL, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item SMOKER = register(
		Blocks.SMOKER, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item BLAST_FURNACE = register(
		Blocks.BLAST_FURNACE, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item CARTOGRAPHY_TABLE = register(Blocks.CARTOGRAPHY_TABLE);
	public static final Item FLETCHING_TABLE = register(Blocks.FLETCHING_TABLE);
	public static final Item GRINDSTONE = register(Blocks.GRINDSTONE);
	public static final Item SMITHING_TABLE = register(Blocks.SMITHING_TABLE);
	public static final Item STONECUTTER = register(Blocks.STONECUTTER);
	public static final Item BELL = register(Blocks.BELL);
	public static final Item LANTERN = register(Blocks.LANTERN);
	public static final Item SOUL_LANTERN = register(Blocks.SOUL_LANTERN);
	public static final Item SWEET_BERRIES = register(
		"sweet_berries", createBlockItemWithUniqueName(Blocks.SWEET_BERRY_BUSH), new Item.Settings().food(FoodComponents.SWEET_BERRIES)
	);
	public static final Item GLOW_BERRIES = register(
		"glow_berries", createBlockItemWithUniqueName(Blocks.CAVE_VINES), new Item.Settings().food(FoodComponents.GLOW_BERRIES)
	);
	public static final Item CAMPFIRE = register(
		Blocks.CAMPFIRE, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item SOUL_CAMPFIRE = register(
		Blocks.SOUL_CAMPFIRE, (UnaryOperator<Item.Settings>)(settings -> settings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT))
	);
	public static final Item SHROOMLIGHT = register(Blocks.SHROOMLIGHT);
	public static final Item HONEYCOMB = register("honeycomb", HoneycombItem::new);
	public static final Item BEE_NEST = register(Blocks.BEE_NEST, new Item.Settings().component(DataComponentTypes.BEES, List.of()));
	public static final Item BEEHIVE = register(Blocks.BEEHIVE, new Item.Settings().component(DataComponentTypes.BEES, List.of()));
	public static final Item HONEY_BOTTLE = register(
		"honey_bottle",
		new Item.Settings()
			.recipeRemainder(GLASS_BOTTLE)
			.food(FoodComponents.HONEY_BOTTLE, ConsumableComponents.HONEY_BOTTLE)
			.useRemainder(GLASS_BOTTLE)
			.maxCount(16)
	);
	public static final Item HONEYCOMB_BLOCK = register(Blocks.HONEYCOMB_BLOCK);
	public static final Item LODESTONE = register(Blocks.LODESTONE);
	public static final Item CRYING_OBSIDIAN = register(Blocks.CRYING_OBSIDIAN);
	public static final Item BLACKSTONE = register(Blocks.BLACKSTONE);
	public static final Item BLACKSTONE_SLAB = register(Blocks.BLACKSTONE_SLAB);
	public static final Item BLACKSTONE_STAIRS = register(Blocks.BLACKSTONE_STAIRS);
	public static final Item GILDED_BLACKSTONE = register(Blocks.GILDED_BLACKSTONE);
	public static final Item POLISHED_BLACKSTONE = register(Blocks.POLISHED_BLACKSTONE);
	public static final Item POLISHED_BLACKSTONE_SLAB = register(Blocks.POLISHED_BLACKSTONE_SLAB);
	public static final Item POLISHED_BLACKSTONE_STAIRS = register(Blocks.POLISHED_BLACKSTONE_STAIRS);
	public static final Item CHISELED_POLISHED_BLACKSTONE = register(Blocks.CHISELED_POLISHED_BLACKSTONE);
	public static final Item POLISHED_BLACKSTONE_BRICKS = register(Blocks.POLISHED_BLACKSTONE_BRICKS);
	public static final Item POLISHED_BLACKSTONE_BRICK_SLAB = register(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
	public static final Item POLISHED_BLACKSTONE_BRICK_STAIRS = register(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
	public static final Item CRACKED_POLISHED_BLACKSTONE_BRICKS = register(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
	public static final Item RESPAWN_ANCHOR = register(Blocks.RESPAWN_ANCHOR);
	public static final Item CANDLE = register(Blocks.CANDLE);
	public static final Item WHITE_CANDLE = register(Blocks.WHITE_CANDLE);
	public static final Item ORANGE_CANDLE = register(Blocks.ORANGE_CANDLE);
	public static final Item MAGENTA_CANDLE = register(Blocks.MAGENTA_CANDLE);
	public static final Item LIGHT_BLUE_CANDLE = register(Blocks.LIGHT_BLUE_CANDLE);
	public static final Item YELLOW_CANDLE = register(Blocks.YELLOW_CANDLE);
	public static final Item LIME_CANDLE = register(Blocks.LIME_CANDLE);
	public static final Item PINK_CANDLE = register(Blocks.PINK_CANDLE);
	public static final Item GRAY_CANDLE = register(Blocks.GRAY_CANDLE);
	public static final Item LIGHT_GRAY_CANDLE = register(Blocks.LIGHT_GRAY_CANDLE);
	public static final Item CYAN_CANDLE = register(Blocks.CYAN_CANDLE);
	public static final Item PURPLE_CANDLE = register(Blocks.PURPLE_CANDLE);
	public static final Item BLUE_CANDLE = register(Blocks.BLUE_CANDLE);
	public static final Item BROWN_CANDLE = register(Blocks.BROWN_CANDLE);
	public static final Item GREEN_CANDLE = register(Blocks.GREEN_CANDLE);
	public static final Item RED_CANDLE = register(Blocks.RED_CANDLE);
	public static final Item BLACK_CANDLE = register(Blocks.BLACK_CANDLE);
	public static final Item SMALL_AMETHYST_BUD = register(Blocks.SMALL_AMETHYST_BUD);
	public static final Item MEDIUM_AMETHYST_BUD = register(Blocks.MEDIUM_AMETHYST_BUD);
	public static final Item LARGE_AMETHYST_BUD = register(Blocks.LARGE_AMETHYST_BUD);
	public static final Item AMETHYST_CLUSTER = register(Blocks.AMETHYST_CLUSTER);
	public static final Item POINTED_DRIPSTONE = register(Blocks.POINTED_DRIPSTONE);
	public static final Item OCHRE_FROGLIGHT = register(Blocks.OCHRE_FROGLIGHT);
	public static final Item VERDANT_FROGLIGHT = register(Blocks.VERDANT_FROGLIGHT);
	public static final Item PEARLESCENT_FROGLIGHT = register(Blocks.PEARLESCENT_FROGLIGHT);
	public static final Item FROGSPAWN = register(Blocks.FROGSPAWN, PlaceableOnWaterItem::new);
	public static final Item ECHO_SHARD = register("echo_shard", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item BRUSH = register("brush", BrushItem::new, new Item.Settings().maxDamage(64));
	public static final Item NETHERITE_UPGRADE_SMITHING_TEMPLATE = register(
		"netherite_upgrade_smithing_template", SmithingTemplateItem::createNetheriteUpgrade, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"sentry_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item DUNE_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"dune_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item COAST_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"coast_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item WILD_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"wild_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item WARD_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"ward_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.RARE)
	);
	public static final Item EYE_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"eye_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.RARE)
	);
	public static final Item VEX_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"vex_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.RARE)
	);
	public static final Item TIDE_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"tide_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"snout_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item RIB_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"rib_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"spire_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.RARE)
	);
	public static final Item WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"wayfinder_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"shaper_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"silence_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.EPIC)
	);
	public static final Item RAISER_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"raiser_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item HOST_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"host_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item FLOW_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"flow_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item BOLT_ARMOR_TRIM_SMITHING_TEMPLATE = register(
		"bolt_armor_trim_smithing_template", SmithingTemplateItem::of, new Item.Settings().rarity(Rarity.UNCOMMON)
	);
	public static final Item ANGLER_POTTERY_SHERD = register("angler_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item ARCHER_POTTERY_SHERD = register("archer_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item ARMS_UP_POTTERY_SHERD = register("arms_up_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item BLADE_POTTERY_SHERD = register("blade_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item BREWER_POTTERY_SHERD = register("brewer_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item BURN_POTTERY_SHERD = register("burn_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item DANGER_POTTERY_SHERD = register("danger_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item EXPLORER_POTTERY_SHERD = register("explorer_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item FLOW_POTTERY_SHERD = register("flow_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item FRIEND_POTTERY_SHERD = register("friend_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item GUSTER_POTTERY_SHERD = register("guster_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item HEART_POTTERY_SHERD = register("heart_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item HEARTBREAK_POTTERY_SHERD = register("heartbreak_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item HOWL_POTTERY_SHERD = register("howl_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item MINER_POTTERY_SHERD = register("miner_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item MOURNER_POTTERY_SHERD = register("mourner_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item PLENTY_POTTERY_SHERD = register("plenty_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item PRIZE_POTTERY_SHERD = register("prize_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item SCRAPE_POTTERY_SHERD = register("scrape_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item SHEAF_POTTERY_SHERD = register("sheaf_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item SHELTER_POTTERY_SHERD = register("shelter_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item SKULL_POTTERY_SHERD = register("skull_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item SNORT_POTTERY_SHERD = register("snort_pottery_sherd", new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final Item COPPER_GRATE = register(Blocks.COPPER_GRATE);
	public static final Item EXPOSED_COPPER_GRATE = register(Blocks.EXPOSED_COPPER_GRATE);
	public static final Item WEATHERED_COPPER_GRATE = register(Blocks.WEATHERED_COPPER_GRATE);
	public static final Item OXIDIZED_COPPER_GRATE = register(Blocks.OXIDIZED_COPPER_GRATE);
	public static final Item WAXED_COPPER_GRATE = register(Blocks.WAXED_COPPER_GRATE);
	public static final Item WAXED_EXPOSED_COPPER_GRATE = register(Blocks.WAXED_EXPOSED_COPPER_GRATE);
	public static final Item WAXED_WEATHERED_COPPER_GRATE = register(Blocks.WAXED_WEATHERED_COPPER_GRATE);
	public static final Item WAXED_OXIDIZED_COPPER_GRATE = register(Blocks.WAXED_OXIDIZED_COPPER_GRATE);
	public static final Item COPPER_BULB = register(Blocks.COPPER_BULB);
	public static final Item EXPOSED_COPPER_BULB = register(Blocks.EXPOSED_COPPER_BULB);
	public static final Item WEATHERED_COPPER_BULB = register(Blocks.WEATHERED_COPPER_BULB);
	public static final Item OXIDIZED_COPPER_BULB = register(Blocks.OXIDIZED_COPPER_BULB);
	public static final Item WAXED_COPPER_BULB = register(Blocks.WAXED_COPPER_BULB);
	public static final Item WAXED_EXPOSED_COPPER_BULB = register(Blocks.WAXED_EXPOSED_COPPER_BULB);
	public static final Item WAXED_WEATHERED_COPPER_BULB = register(Blocks.WAXED_WEATHERED_COPPER_BULB);
	public static final Item WAXED_OXIDIZED_COPPER_BULB = register(Blocks.WAXED_OXIDIZED_COPPER_BULB);
	public static final Item TRIAL_SPAWNER = register(Blocks.TRIAL_SPAWNER);
	public static final Item TRIAL_KEY = register("trial_key");
	public static final Item OMINOUS_TRIAL_KEY = register("ominous_trial_key");
	public static final Item VAULT = register(Blocks.VAULT);
	public static final Item OMINOUS_BOTTLE = register(
		"ominous_bottle",
		new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.component(DataComponentTypes.CONSUMABLE, ConsumableComponents.OMINOUS_BOTTLE)
			.component(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifierComponent(0))
	);

	private static Function<Item.Settings, Item> createBlockItemWithUniqueName(Block block) {
		return settings -> new BlockItem(block, settings.useItemPrefixedTranslationKey());
	}

	private static RegistryKey<Item> keyOf(String id) {
		return RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla(id));
	}

	private static RegistryKey<Item> keyOf(RegistryKey<Block> blockKey) {
		return RegistryKey.of(RegistryKeys.ITEM, blockKey.getValue());
	}

	public static Item register(Block block) {
		return register(block, BlockItem::new);
	}

	public static Item register(Block block, Item.Settings settings) {
		return register(block, BlockItem::new, settings);
	}

	public static Item register(Block block, UnaryOperator<Item.Settings> settingsOperator) {
		return register(block, (BiFunction<Block, Item.Settings, Item>)((blockx, settings) -> new BlockItem(blockx, (Item.Settings)settingsOperator.apply(settings))));
	}

	public static Item register(Block block, Block... blocks) {
		Item item = register(block);

		for (Block block2 : blocks) {
			Item.BLOCK_ITEMS.put(block2, item);
		}

		return item;
	}

	public static Item register(Block block, BiFunction<Block, Item.Settings, Item> factory) {
		return register(block, factory, new Item.Settings());
	}

	public static Item register(Block block, BiFunction<Block, Item.Settings, Item> factory, Item.Settings settings) {
		return register(
			keyOf(block.getRegistryEntry().registryKey()), itemSettings -> (Item)factory.apply(block, itemSettings), settings.useBlockPrefixedTranslationKey()
		);
	}

	public static Item register(String id, Function<Item.Settings, Item> factory) {
		return register(keyOf(id), factory, new Item.Settings());
	}

	public static Item register(String id, Function<Item.Settings, Item> factory, Item.Settings settings) {
		return register(keyOf(id), factory, settings);
	}

	public static Item register(String id, Item.Settings settings) {
		return register(keyOf(id), Item::new, settings);
	}

	public static Item register(String id) {
		return register(keyOf(id), Item::new, new Item.Settings());
	}

	public static Item register(RegistryKey<Item> key, Function<Item.Settings, Item> factory) {
		return register(key, factory, new Item.Settings());
	}

	public static Item register(RegistryKey<Item> key, Function<Item.Settings, Item> factory, Item.Settings settings) {
		Item item = (Item)factory.apply(settings.registryKey(key));
		if (item instanceof BlockItem blockItem) {
			blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
		}

		return Registry.register(Registries.ITEM, key, item);
	}
}
